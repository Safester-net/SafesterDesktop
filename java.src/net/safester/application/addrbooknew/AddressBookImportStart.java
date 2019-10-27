/*
 * This file is part of Safester.                                    
 * Copyright (C) 2019, KawanSoft SAS
 * (https://www.Safester.net). All rights reserved.                                
 *                                                                               
 * Safester is free software; you can redistribute it and/or                 
 * modify it under the terms of the GNU Lesser General Public                    
 * License as published by the Free Software Foundation; either                  
 * version 2.1 of the License, or (at your option) any later version.            
 *                                                                               
 * Safester is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU             
 * Lesser General Public License for more details.                               
 *                                                                               
 * You should have received a copy of the GNU Lesser General Public              
 * License along with this library; if not, write to the Free Software           
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301  USA
 * 
 * Any modifications to this file must keep this entire header
 * intact.
 */
package net.safester.application.addrbooknew;
 
import static net.safester.application.addrbooknew.AddressBookImportCsv2.CR_LF;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.commons.lang3.SystemUtils;

import com.moyosoft.connector.ms.outlook.Outlook;
import com.safelogic.pgp.util.UserPreferencesManager;
import com.swing.util.SwingUtil;

import net.safester.application.NewsFrame;
import net.safester.application.addrbooknew.gmail.AddressBookImportGmail;
import net.safester.application.addrbooknew.outlook.OutlookUtil;
import net.safester.application.addrbooknew.outlook.OutlookUtilMoyosoft;
import net.safester.application.addrbooknew.tools.ProcessUtil;
import net.safester.application.addrbooknew.tools.SessionUtil;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.ImageParmsUtil;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.HtmlTextUtil;

/**
 *
 * @author Nicolas de Pomereu 
 */
public class AddressBookImportStart extends javax.swing.JDialog {

    private Window parent = null;

    private UserPreferencesManager userPreferencesManager = new UserPreferencesManager();

    /**
     * Add a clipboard manager for help text
     */
    private ClipboardManager clipboard = null;
    private NewsFrame help;
    private Connection connection = null;
    private int userNumber = -1;
    
    /**
     * Creates new form Preferences
     * @param parent the value of parent
     * @param connection the value of connection
     * @param userNumber the value of userNumber
     */
    public AddressBookImportStart(Window parent, Connection connection, int userNumber) {
        initComponents();
        initializeIt();
        this.parent = parent;
        this.connection = connection;
        this.userNumber = userNumber;
        this.setVisible(true);
    }

    /**
     * This is the method to include in *our* constructor(s)
     */
    public void initializeIt() {

        Dimension dim = new Dimension(447, 336);
                
        this.setPreferredSize(dim);
        this.setSize(dim);

        try {
            this.setIconImage(ImageParmsUtil.getAppIcon());
        } catch (RuntimeException e1) {
            e1.printStackTrace();
        }

        /*
        importing_contacts=Importing Contacts
        from_outlook_office=From Microsoft Oulook Office
        from_a_gmail_account=From a Gmail acount
        from_a_csv_file=From a CSV file
        */
        jLabelLogo.setText(MessagesManager.get("importing_contacts"));
        jXTitledSeparator2.setTitle(MessagesManager.get("choose_how_to_import"));
        jRadioButtonUseOutlookOffice.setText(MessagesManager.get("from_outlook_office"));
        jRadioButtonUseGmail.setText(MessagesManager.get("from_a_gmail_account"));
        jRadioButtonUseCsv.setText(MessagesManager.get("from_a_csv_file"));
        
        jButtonOk.setText(MessagesManager.get("submit"));
        jButtonClose.setText(MessagesManager.get("close"));
        jButtonHelp.setText(MessagesManager.get("help"));
        
        buttonGroup1.add(jRadioButtonUseCsv);
        buttonGroup1.add(jRadioButtonUseGmail);
        buttonGroup1.add(jRadioButtonUseOutlookOffice);
        
        jRadioButtonUseCsv.setSelected(true);
        
        // New version just query the Registry and does throw Exception
        boolean isOutlookInstalled = OutlookUtil.isOutlookInstalled();
        
        if (isOutlookInstalled) {
            jRadioButtonUseCsv.setSelected(false);
            jRadioButtonUseGmail.setSelected(false);
            jRadioButtonUseOutlookOffice.setSelected(true);
            jRadioButtonUseOutlookOffice.requestFocusInWindow();
        }
        else {
            if (! SystemUtils.IS_OS_WINDOWS) {
                String text = jRadioButtonUseOutlookOffice.getText().trim() + " (Windows)";
                jRadioButtonUseOutlookOffice.setText(text);
                jRadioButtonUseOutlookOffice.setSelected(false);
                jRadioButtonUseOutlookOffice.setEnabled(false);
            }
            else {
                jRadioButtonUseOutlookOffice.setEnabled(true);
                jRadioButtonUseOutlookOffice.setSelected(false);
            }

            jRadioButtonUseCsv.setSelected(true);
            jRadioButtonUseGmail.setSelected(false);
            jRadioButtonUseCsv.requestFocusInWindow();
        }
            
        // make invisible GMail panel for now
        ///jPanelGMail.setVisible(false);
        
        // Add a Clipboard Manager
        clipboard = new ClipboardManager(jPanelMain);

        //this.setModal(true);
        
        ButtonResizer buttonResizer = new ButtonResizer(jPanelButtons);
        buttonResizer.setWidthToMax();

        SwingUtil.resizeJComponentsForNimbusAndMacOsX(rootPane);

        this.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                saveSettings();
            }

            public void componentResized(ComponentEvent e) {
                saveSettings();
            }
        });

        // Our window listener for all events
        // If window is closed ==> call close()
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeOnExit();
            }
        });

        this.keyListenerAdder();

        // Load and activate previous windows settings
        WindowSettingManager.load(this);
        this.setTitle(jLabelLogo.getText());
        
        pack();
    }

    private void importUsingCsv() {
        this.closeOnExit();
        AddressBookImportCsv1 addressBookImport = new AddressBookImportCsv1(this, connection, userNumber);
    }

    private void importUsingOutlookOffice() {

        boolean opened = false;

        while (!opened) {
            try {
                opened = ProcessUtil.isWindowsProgramRunning("outlook");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Impossible to say if Outlook Office is already open. " + CR_LF
                        + SessionUtil.getCleanErrorMessage(ex), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
                this.closeOnExit();
                return;
            }

            if (!opened) {
                java.awt.Toolkit.getDefaultToolkit().beep();
                int result = JOptionPane.showConfirmDialog(this, MessagesManager.get("please_open_outlook_office")
                        + CR_LF + MessagesManager.get("outlook_must_remain_open"),
                        Parms.APP_NAME, JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    continue;
                } else {
                    return;
                }
            }
        }        
        
        this.closeOnExit();
        
        // Getting chose Contact folders in JTreee
        // In order to choose a folder.
        Outlook outlook = null;

        try {
            outlook = new Outlook();
            FolderChooserNew folderChooserNew = new FolderChooserNew(parent, connection, userNumber);
            folderChooserNew.setVisible(true);
            
            this.closeOnExit();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Impossible to choose an Contact folder in Outlook Office. " + CR_LF
                    + SessionUtil.getCleanErrorMessage(ex), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            OutlookUtilMoyosoft.outlookDispose(outlook);
            return;
        }

    }

    // Futur versions
    private void importUsingGmail() {

        AddressBookImportGmail addressBookImportGmail = new AddressBookImportGmail(parent, connection, userNumber);
        
    }
        
    /**
     * Universal key listener
     */
    private void keyListenerAdder() {
        List<Component> components = SwingUtil.getAllComponants(this);

        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);

            comp.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    this_keyReleased(e);
                }
            });
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // KEYS PART
    ///////////////////////////////////////////////////////////////////////////    
    private void this_keyReleased(KeyEvent e) {
        //System.out.println("this_keyReleased(KeyEvent e) " + e.getComponent().getName()); 
        int id = e.getID();
        if (id == KeyEvent.KEY_RELEASED) {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_F1) {
                jButtonHelpActionPerformed(null);
            }

            if (keyCode == KeyEvent.VK_ENTER) {
                actionOk();
            }

            if (keyCode == KeyEvent.VK_ESCAPE) {
                closeOnExit();
            }
        }
    }

    public void saveSettings() {
        WindowSettingManager.save(this);
    }

    private void closeOnExit() {
        this.dispose();
    }

    private void actionOk() {
        
        
        if (jRadioButtonUseOutlookOffice.isSelected()) {
            importUsingOutlookOffice(); 
        }
        else if (jRadioButtonUseGmail.isSelected()) {
            this.dispose();
            importUsingGmail();
        }
        else if (jRadioButtonUseCsv.isSelected()) {
            this.dispose();
            importUsingCsv();
        }
    }

    private void actionCancel() {
        closeOnExit();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanelMain = new javax.swing.JPanel();
        jPanelLogo = new javax.swing.JPanel();
        jLabelLogo = new javax.swing.JLabel();
        jPanelSepLine2New = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel25 = new javax.swing.JPanel();
        jPanelSepBlanc9 = new javax.swing.JPanel();
        jPanelTitledSeparator2 = new javax.swing.JPanel();
        jPaneBlanklLeft = new javax.swing.JPanel();
        jXTitledSeparator20pixels = new org.jdesktop.swingx.JXTitledSeparator();
        jPanelSep3x5 = new javax.swing.JPanel();
        jXTitledSeparator2 = new org.jdesktop.swingx.JXTitledSeparator();
        jPanelBlankRight = new javax.swing.JPanel();
        jPanelSepBlanc3 = new javax.swing.JPanel();
        jPanelImporMain = new javax.swing.JPanel();
        jPanelCSC = new javax.swing.JPanel();
        jPanelBorderLeft2 = new javax.swing.JPanel();
        jLabelCsv = new javax.swing.JLabel();
        jRadioButtonUseCsv = new javax.swing.JRadioButton();
        jPanelGMail = new javax.swing.JPanel();
        jPanelBorderLeft1 = new javax.swing.JPanel();
        jLabelGmail = new javax.swing.JLabel();
        jRadioButtonUseGmail = new javax.swing.JRadioButton();
        jPanelOL = new javax.swing.JPanel();
        jPanelBorderLeft = new javax.swing.JPanel();
        jLabelOutlook = new javax.swing.JLabel();
        jRadioButtonUseOutlookOffice = new javax.swing.JRadioButton();
        jPanelSepBlanc2 = new javax.swing.JPanel();
        jPanelSepLine2New1 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel27 = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jButtonHelp = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelMain.setLayout(new javax.swing.BoxLayout(jPanelMain, javax.swing.BoxLayout.Y_AXIS));

        jPanelLogo.setMaximumSize(new java.awt.Dimension(32767, 72));
        jPanelLogo.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 12));

        jLabelLogo.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/book_telephone.png"))); // NOI18N
        jLabelLogo.setText("Importer des Contacts");
        jLabelLogo.setToolTipText("");
        jPanelLogo.add(jLabelLogo);

        jPanelMain.add(jPanelLogo);

        jPanelSepLine2New.setMaximumSize(new java.awt.Dimension(32787, 10));
        jPanelSepLine2New.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepLine2New.setLayout(new javax.swing.BoxLayout(jPanelSepLine2New, javax.swing.BoxLayout.LINE_AXIS));

        jPanel24.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine2New.add(jPanel24);
        jPanelSepLine2New.add(jSeparator2);

        jPanel25.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine2New.add(jPanel25);

        jPanelMain.add(jPanelSepLine2New);

        jPanelSepBlanc9.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepBlanc9.setName(""); // NOI18N
        jPanelSepBlanc9.setPreferredSize(new java.awt.Dimension(1000, 10));
        jPanelMain.add(jPanelSepBlanc9);

        jPanelTitledSeparator2.setPreferredSize(new java.awt.Dimension(518, 16));
        jPanelTitledSeparator2.setLayout(new javax.swing.BoxLayout(jPanelTitledSeparator2, javax.swing.BoxLayout.LINE_AXIS));

        jPaneBlanklLeft.setMaximumSize(new java.awt.Dimension(10, 10));
        jPaneBlanklLeft.setPreferredSize(new java.awt.Dimension(10, 11));

        javax.swing.GroupLayout jPaneBlanklLeftLayout = new javax.swing.GroupLayout(jPaneBlanklLeft);
        jPaneBlanklLeft.setLayout(jPaneBlanklLeftLayout);
        jPaneBlanklLeftLayout.setHorizontalGroup(
            jPaneBlanklLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPaneBlanklLeftLayout.setVerticalGroup(
            jPaneBlanklLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelTitledSeparator2.add(jPaneBlanklLeft);

        jXTitledSeparator20pixels.setMaximumSize(new java.awt.Dimension(20, 16));
        jXTitledSeparator20pixels.setMinimumSize(new java.awt.Dimension(20, 16));
        jXTitledSeparator20pixels.setPreferredSize(new java.awt.Dimension(20, 16));
        jXTitledSeparator20pixels.setTitle("");
        jPanelTitledSeparator2.add(jXTitledSeparator20pixels);

        jPanelSep3x5.setMaximumSize(new java.awt.Dimension(3, 5));
        jPanelSep3x5.setMinimumSize(new java.awt.Dimension(3, 5));

        javax.swing.GroupLayout jPanelSep3x5Layout = new javax.swing.GroupLayout(jPanelSep3x5);
        jPanelSep3x5.setLayout(jPanelSep3x5Layout);
        jPanelSep3x5Layout.setHorizontalGroup(
            jPanelSep3x5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 3, Short.MAX_VALUE)
        );
        jPanelSep3x5Layout.setVerticalGroup(
            jPanelSep3x5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelTitledSeparator2.add(jPanelSep3x5);

        jXTitledSeparator2.setMaximumSize(new java.awt.Dimension(2147483647, 16));
        jXTitledSeparator2.setTitle("Choisissez comment importer vos contacts");
        jPanelTitledSeparator2.add(jXTitledSeparator2);

        jPanelBlankRight.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanelBlankRightLayout = new javax.swing.GroupLayout(jPanelBlankRight);
        jPanelBlankRight.setLayout(jPanelBlankRightLayout);
        jPanelBlankRightLayout.setHorizontalGroup(
            jPanelBlankRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelBlankRightLayout.setVerticalGroup(
            jPanelBlankRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelTitledSeparator2.add(jPanelBlankRight);

        jPanelMain.add(jPanelTitledSeparator2);

        jPanelSepBlanc3.setMaximumSize(new java.awt.Dimension(32767, 8));
        jPanelSepBlanc3.setMinimumSize(new java.awt.Dimension(10, 8));
        jPanelSepBlanc3.setPreferredSize(new java.awt.Dimension(1000, 8));
        jPanelMain.add(jPanelSepBlanc3);

        jPanelImporMain.setLayout(new javax.swing.BoxLayout(jPanelImporMain, javax.swing.BoxLayout.Y_AXIS));

        jPanelCSC.setMaximumSize(new java.awt.Dimension(32767, 38));
        jPanelCSC.setMinimumSize(new java.awt.Dimension(10, 38));
        jPanelCSC.setPreferredSize(new java.awt.Dimension(10, 38));
        jPanelCSC.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        jPanelBorderLeft2.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelCSC.add(jPanelBorderLeft2);

        jLabelCsv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/icons8-csv-32.png"))); // NOI18N
        jPanelCSC.add(jLabelCsv);

        jRadioButtonUseCsv.setText("A partir d'un fichier au format CSV");
        jPanelCSC.add(jRadioButtonUseCsv);

        jPanelImporMain.add(jPanelCSC);

        jPanelGMail.setMaximumSize(new java.awt.Dimension(32767, 38));
        jPanelGMail.setMinimumSize(new java.awt.Dimension(10, 38));
        jPanelGMail.setPreferredSize(new java.awt.Dimension(10, 38));
        jPanelGMail.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        jPanelBorderLeft1.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelGMail.add(jPanelBorderLeft1);

        jLabelGmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/icons8-gmail-32.png"))); // NOI18N
        jPanelGMail.add(jLabelGmail);

        jRadioButtonUseGmail.setText("A partir d'un compte Gmail");
        jPanelGMail.add(jRadioButtonUseGmail);

        jPanelImporMain.add(jPanelGMail);

        jPanelOL.setMaximumSize(new java.awt.Dimension(32767, 38));
        jPanelOL.setMinimumSize(new java.awt.Dimension(10, 38));
        jPanelOL.setPreferredSize(new java.awt.Dimension(10, 35));
        jPanelOL.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        jPanelBorderLeft.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelOL.add(jPanelBorderLeft);

        jLabelOutlook.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/icons8-ms-outlook-32.png"))); // NOI18N
        jPanelOL.add(jLabelOutlook);

        jRadioButtonUseOutlookOffice.setText("A partir de Microsoft Outlook Office");
        jPanelOL.add(jRadioButtonUseOutlookOffice);

        jPanelImporMain.add(jPanelOL);

        jPanelMain.add(jPanelImporMain);

        jPanelSepBlanc2.setMinimumSize(new java.awt.Dimension(10, 8));
        jPanelSepBlanc2.setPreferredSize(new java.awt.Dimension(1000, 8));
        jPanelMain.add(jPanelSepBlanc2);

        jPanelSepLine2New1.setMaximumSize(new java.awt.Dimension(32787, 10));
        jPanelSepLine2New1.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepLine2New1.setLayout(new javax.swing.BoxLayout(jPanelSepLine2New1, javax.swing.BoxLayout.LINE_AXIS));

        jPanel26.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine2New1.add(jPanel26);
        jPanelSepLine2New1.add(jSeparator3);

        jPanel27.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine2New1.add(jPanel27);

        jPanelMain.add(jPanelSepLine2New1);

        jPanelButtons.setMaximumSize(new java.awt.Dimension(32767, 45));
        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonOk.setText("Valider");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonOk);

        jButtonClose.setText("Fermer");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonClose);

        jButtonHelp.setText("Aide");
        jButtonHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHelpActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonHelp);

        jPanel1.setMaximumSize(new java.awt.Dimension(1, 1));
        jPanel1.setMinimumSize(new java.awt.Dimension(1, 1));
        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanelButtons.add(jPanel1);

        jPanelMain.add(jPanelButtons);

        getContentPane().add(jPanelMain);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        actionCancel();
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        actionOk();
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jButtonHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHelpActionPerformed
        if (help != null) {
            help.dispose();
        }

        String content = HtmlTextUtil.getHtmlHelpContent("help_address_book_import_main");
        help = new NewsFrame(this, content, MessagesManager.get("help"));
        
    }//GEN-LAST:event_jButtonHelpActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            /*
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            */
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AddressBookImportStart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddressBookImportStart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddressBookImportStart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddressBookImportStart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AddressBookImportStart(null, null, -1).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonHelp;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelCsv;
    private javax.swing.JLabel jLabelGmail;
    private javax.swing.JLabel jLabelLogo;
    private javax.swing.JLabel jLabelOutlook;
    private javax.swing.JPanel jPaneBlanklLeft;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanelBlankRight;
    private javax.swing.JPanel jPanelBorderLeft;
    private javax.swing.JPanel jPanelBorderLeft1;
    private javax.swing.JPanel jPanelBorderLeft2;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelCSC;
    private javax.swing.JPanel jPanelGMail;
    private javax.swing.JPanel jPanelImporMain;
    private javax.swing.JPanel jPanelLogo;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelOL;
    private javax.swing.JPanel jPanelSep3x5;
    private javax.swing.JPanel jPanelSepBlanc2;
    private javax.swing.JPanel jPanelSepBlanc3;
    private javax.swing.JPanel jPanelSepBlanc9;
    private javax.swing.JPanel jPanelSepLine2New;
    private javax.swing.JPanel jPanelSepLine2New1;
    private javax.swing.JPanel jPanelTitledSeparator2;
    private javax.swing.JRadioButton jRadioButtonUseCsv;
    private javax.swing.JRadioButton jRadioButtonUseGmail;
    private javax.swing.JRadioButton jRadioButtonUseOutlookOffice;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator2;
    private org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator20pixels;
    // End of variables declaration//GEN-END:variables



}
