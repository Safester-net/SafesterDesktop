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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.commons.lang3.SystemUtils;

import com.swing.util.SwingUtil;

import net.iharder.dnd.FileDrop;
import net.safester.application.Help;
import net.safester.application.NewsFrame;
import net.safester.application.addrbooknew.tools.FileDialogMemory;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.ImageParmsUtil;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.CsvFileFilter;
import net.safester.application.tool.JFileChooserMemory;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.HtmlTextUtil;

/**
 * Main class & frame to import external address book.
 *
 * @author Nicolas de Pomereu
 */ 
public class AddressBookImportCsv1 extends javax.swing.JDialog {

    private static final String CR_LF = System.getProperty("line.separator");

    public static final int ADDR_HEIGHT = 566;
    public static final int ADDR_WIDTH = 566;

    /**
     * The parent JFrame
     */
    private Window parent = null;

    private Window thisOne = this;
    private NewsFrame help;

    private Connection connection = null;
    private int userNumber = -1;
    
    /**
     * Creates new form NewsFrame
     * @param parent the value of parent
     * @param connection the value of connection
     * @param userNumber the value of userNumber
     */
    public AddressBookImportCsv1(Window parent, Connection connection, int userNumber) {
        this.parent = parent;
        this.connection = connection;
        this.userNumber = userNumber;
        
        initComponents();
        initializeCompany();
    }

    /**
     * This is the method to include in the constructor
     */
    public void initializeCompany() {

        try {
            this.setIconImage(ImageParmsUtil.getAppIcon());
        } catch (RuntimeException e1) {
            e1.printStackTrace();
        }

        this.setSize(ADDR_WIDTH, ADDR_HEIGHT);
        this.setPreferredSize(new Dimension(ADDR_WIDTH, ADDR_HEIGHT));
        
        if (parent != null) {
            this.setLocationRelativeTo(parent);
        }
        
        this.setModal(true);

        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);

        jEditorPane.setText(Help.getHtmlHelpContent("snip/address_book_import_csv_1"));
        jLabelTitle.setText(MessagesManager.get("importing_contacts_from_a_csv_file"));
        
        this.jButtonNext.setText(MessagesManager.get("next") +  " >");
        this.jButtonClose.setText(MessagesManager.get("cancel"));
        this.jButtonHelp.setText(MessagesManager.get("help"));
       

        // These 2 stupid lines : only to force to display top of file first
        jEditorPane.moveCaretPosition(0);
        jEditorPane.setSelectionEnd(0);

        this.keyListenerAdder();
        this.setLocationByPlatform(true);

//        this.addWindowListener(new WindowAdapter() {
//
//            @Override
//            public void windowClosing(WindowEvent e) {
//                WindowSettingManager.save(thisOne);
//            }
//        });
        
        ButtonResizer br = new ButtonResizer(jPanelSouth);
        br.setWidthToMax();
        
        SwingUtil.applySwingUpdates(rootPane);
                
        new FileDrop(this, new FileDrop.Listener() {
            public void filesDropped(java.io.File[] files) {
                // handle file drop  
                if (files.length > 1) {

                    JOptionPane.showMessageDialog(parent,
                            MessagesManager.get("please_drag_and_drop_one_file"));
                    return;

                    //thisOne.jTextFieldFilename.setText(files[0].toString());         
                }
                
                if (files[0].length() > CsvAddressBook.MAX_FILE_LENGTH) {
                    //CsvAddressBook.MAX_FILE_LENGTH 
                    String message = MessagesManager.get("file_is_too_big_size_must_be_less");
                    message = message.replace("{0}", "" + CsvAddressBook.MAX_FILE_LENGTH);
                    JOptionPane.showMessageDialog(thisOne, message);
                }

                thisOne.dispose();
                new AddressBookImportCsv2(parent, files[0], connection, userNumber);

            } // end filesDropped
            
        }); // end FileDrop.Listener 

        //this.setLocationRelativeTo(parent);
        WindowSettingManager.load(this);
        
        pack();
        
        this.setVisible(true);
    }

    /**
     * Universal key listener
     */
    private void keyListenerAdder() {
        List<Component> components = SwingUtil.getAllComponants(this);

        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);

            comp.addKeyListener(new KeyAdapter() {

                @Override
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

            if (keyCode == KeyEvent.VK_ENTER) {
                this.doNext();
            }

            if (keyCode == KeyEvent.VK_ESCAPE) {
                this.dispose();
            }
            
            if (keyCode == KeyEvent.VK_N && e.isControlDown()) {
                doNext();
            }
        }
    }

    private File getFileWithSwing() {

        JFileChooserMemory jFileChooser = new JFileChooserMemory();

        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        jFileChooser.setFileFilter(new CsvFileFilter());
        jFileChooser.setAcceptAllFileFilterUsed(true);

        int returnVal = jFileChooser.showOpenDialog(this);

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File file  = jFileChooser.getSelectedFile();
        return file;
    }
        
    private File getFileWithAwt() {

        FileDialog fileDialog = new FileDialogMemory(this, MessagesManager.get("system_open"), FileDialog.LOAD);
        fileDialog.setIconImage(ImageParmsUtil.getAppIcon());
        fileDialog.setType(FileDialog.Type.NORMAL);
        fileDialog.setMultipleMode(false);

        fileDialog.setFile("*.csv");
        fileDialog.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".csv");
            }
        });

        fileDialog.setVisible(true);
        String fileStr = fileDialog.getFile();

        if (fileStr == null) {
            return null;
        }

        String dir = fileDialog.getDirectory();
        if (!dir.endsWith(File.separator)) {
            dir += File.separator;
        }

        File file = new File(dir + fileStr);
        return file;
    }

        
    /**
     * Select the CSV file and launch the import selector
     */
    private void doNext() {
        
        File file = null;
        
        // AWT does not work (freeze) on Windows
        if (SystemUtils.IS_OS_WINDOWS) {
            file = getFileWithSwing();
        } else {
            file = getFileWithAwt();
        }

        if (file == null) {
            return;
        }

        
        // Security check 
        if (! file.exists()) {
            JOptionPane.showMessageDialog(this, MessagesManager.get("file_does_not_anymore_exist") + file + ".", Parms.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (file.length() > CsvAddressBook.MAX_FILE_LENGTH) {
                    String message = MessagesManager.get("file_is_too_big_size_must_be_less");
                    message = message.replace("{0}", "" + CsvAddressBook.MAX_FILE_LENGTH);

            JOptionPane.showMessageDialog(this, message, Parms.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
        } else {
            this.dispose();
            new AddressBookImportCsv2(parent, file, connection, userNumber);

        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelNorth = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelSepLine2New2 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel23 = new javax.swing.JPanel();
        jPanelEditorPane = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jEditorPane = new javax.swing.JEditorPane();
        jPanel5 = new javax.swing.JPanel();
        jPanelSepLine2New3 = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jSeparator5 = new javax.swing.JSeparator();
        jPanel25 = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jButtonNext = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jButtonHelp = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 12));

        jLabelTitle.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/icons8-csv-32.png"))); // NOI18N
        jLabelTitle.setText("Importer des Contacts depuis un fichier CSV");
        jPanelNorth.add(jLabelTitle);

        getContentPane().add(jPanelNorth);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelSepLine2New2.setMaximumSize(new java.awt.Dimension(32787, 10));
        jPanelSepLine2New2.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepLine2New2.setLayout(new javax.swing.BoxLayout(jPanelSepLine2New2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel22.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSepLine2New2.add(jPanel22);
        jPanelSepLine2New2.add(jSeparator2);

        jPanel23.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSepLine2New2.add(jPanel23);

        jPanelCenter.add(jPanelSepLine2New2);

        jPanelEditorPane.setLayout(new javax.swing.BoxLayout(jPanelEditorPane, javax.swing.BoxLayout.LINE_AXIS));

        jPanel4.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelEditorPane.add(jPanel4);

        jEditorPane.setEditable(false);
        jEditorPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jEditorPane.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jEditorPane.setOpaque(false);
        jPanelEditorPane.add(jEditorPane);

        jPanel5.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelEditorPane.add(jPanel5);

        jPanelCenter.add(jPanelEditorPane);

        jPanelSepLine2New3.setMaximumSize(new java.awt.Dimension(32787, 10));
        jPanelSepLine2New3.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepLine2New3.setLayout(new javax.swing.BoxLayout(jPanelSepLine2New3, javax.swing.BoxLayout.LINE_AXIS));

        jPanel24.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSepLine2New3.add(jPanel24);
        jPanelSepLine2New3.add(jSeparator5);

        jPanel25.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSepLine2New3.add(jPanel25);

        jPanelCenter.add(jPanelSepLine2New3);

        getContentPane().add(jPanelCenter);

        jPanelSouth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonNext.setText("Suivant >");
        jButtonNext.setActionCommand("Suivant ");
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonNext);

        jButtonClose.setText("Annuler");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonClose);

        jButtonHelp.setText("Aide");
        jButtonHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHelpActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonHelp);

        jPanel1.setMaximumSize(new java.awt.Dimension(0, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanelSouth.add(jPanel1);

        getContentPane().add(jPanelSouth);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
    this.dispose();
}//GEN-LAST:event_jButtonCloseActionPerformed

private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
    doNext();
}//GEN-LAST:event_jButtonNextActionPerformed

    private void jButtonHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHelpActionPerformed
        if (help != null) {
            help.dispose();
        }

        help = new NewsFrame(this, HtmlTextUtil.getHtmlHelpContent("help_address_book_import_csv"), "Help");
    }//GEN-LAST:event_jButtonHelpActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                try {
                    //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    System.out.println("Failed loading L&F: ");
                    System.out.println(ex);
                }

                new AddressBookImportCsv1(null, null, -1).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonHelp;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEditorPane;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSepLine2New2;
    private javax.swing.JPanel jPanelSepLine2New3;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator5;
    // End of variables declaration//GEN-END:variables



}
