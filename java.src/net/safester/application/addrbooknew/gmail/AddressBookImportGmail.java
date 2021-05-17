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
package net.safester.application.addrbooknew.gmail;

import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.kawansoft.httpclient.KawanHttpClient;
import com.swing.util.ButtonUrlOver;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.swing.util.SwingUtil;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import net.safester.application.Help;
import net.safester.application.addrbooknew.AddressBookImportFinal;
import net.safester.application.addrbooknew.tools.SessionUtil;
import net.safester.application.http.ApiMessages;
import net.safester.application.http.KawanHttpClientBuilder;
import net.safester.application.http.dto.AddressBookEntryDTO;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.ImageParmsUtil;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;

/**
 * Main class & frame to import external address book.
 *
 * @author Nicolas de Pomereu
 */
public class AddressBookImportGmail extends javax.swing.JDialog {

    private static final String CR_LF = System.getProperty("line.separator");

    public static final int ADDR_HEIGHT = 566;
    public static final int ADDR_WIDTH = 566;

    /**
     * The parent JFrame
     */
    private Window parent = null;

    private Window thisOne = this;
    private Help help;

    private ClipboardManager clipboard;
    private final Connection connection;
    private final int userNumber;

    /**
     * Creates new form NewsFrame
     */
    public AddressBookImportGmail(Window parent, Connection connection, int userNumber) {
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

        // Add a Clipboard Manager
        clipboard = new ClipboardManager(jPanelCenter);

        this.setModal(true);
        this.setTitle(this.jLabelTitle.getText());

        jCheckBoxDisplayFirstNameBefore.setSelected(true);
        
        jCheckBoxDisplayFirstNameBeforeItemStateChanged(null);
        
        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);

        jLabelTitle.setText(MessagesManager.get("importing_contacts_from_gmail"));
        jCheckBoxDisplayFirstNameBefore.setText(MessagesManager.get("display_firstname_before_lastname") + " (Ex: Smith John)");
        jCheckBoxDisplayFirstNameBeforeItemStateChanged(null);
                
        jLabelCode.setText(MessagesManager.get("code"));
                
        this.jButtonGetCode.setText(MessagesManager.get("get_a_code"));
        this.jButtonNext.setText(MessagesManager.get("next") + " >");
        this.jButtonClose.setText(MessagesManager.get("cancel"));
        
        jEditorPane.setText(Help.getHtmlHelpContent("snip/address_book_import_gmail"));

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

        this.setLocationRelativeTo(parent);
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

    /**
     * Select the CSV file and launch the import selector
     */
    private void doNext() {

        if (jTextFieldCode.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, MessagesManager.get("this_code_is_invalid"), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            jTextFieldCode.requestFocusInWindow();
            return;
        }
        
        this.setCursor(Cursor
                .getPredefinedCursor(Cursor.WAIT_CURSOR));

        List<AddressBookEntryDTO> persons = null;
        
        try {
            AwakeConnection awakeConnection = (AwakeConnection)connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

            KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.buildFromAwakeConnection(awakeConnection);
            ApiMessages apiMessages = new ApiMessages(kawanHttpClient, awakeFileSession.getUsername(),
                awakeFileSession.getAuthenticationToken());
                       
            persons = apiMessages.googleGetPersons(jTextFieldCode.getText(), jCheckBoxDisplayFirstNameBefore.isSelected());
            
            if (persons == null) {
                this.setCursor(Cursor
                        .getDefaultCursor());
                JOptionPane.showMessageDialog(this, MessagesManager.get("this_code_is_invalid"), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
                jTextFieldCode.requestFocusInWindow();
                return;
            }
                               
        } catch (Exception ex) {
            this.setCursor(Cursor
                    .getDefaultCursor());
            JOptionPane.showMessageDialog(this, MessagesManager.get("failed_to_retrieve_a_google_id") +  " " + Parms.APP_NAME + ". " + CR_LF
                    + SessionUtil.getCleanErrorMessage(ex), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            jTextFieldCode.requestFocusInWindow();
            return;
        }
                
        this.dispose();
        
        // Pass Contacts instance to last window for final import
        AddressBookImportFinal addressBookImportFinal = new AddressBookImportFinal(parent, persons, jCheckBoxDisplayFirstNameBefore.isSelected(), connection, userNumber);

    }
    
    private void getGoogleCode() {
        try {
            // Go to the Google Developers Console, open your application's
            // credentials page, and copy the client ID and client secret.
            // Then paste them into the following code.
            String clientId = GooglePeopleParms.CLIENT_ID;

            // Or your redirect URL for web based applications.
            String redirectUrl = GooglePeopleParms.REDIRECT_URL;
            String scope = GooglePeopleParms.SCOPE;

            // Step 1: Authorize -->
            String authorizationUrl = new GoogleBrowserClientRequestUrl(clientId,
                    redirectUrl, Arrays.asList(scope)).setResponseTypes(
                    Arrays.asList(GooglePeopleParms.RESPONSE_TYPE_CODE)).build();

            // Point or redirect your user to the authorizationUrl.
            Desktop desktop = Desktop.getDesktop();

            URI uri = null;

            try {
                uri = new URL(authorizationUrl).toURI();
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }

            desktop.browse(uri);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, MessagesManager.get("unable_to_get_an_authorization_code") + CR_LF
                    + SessionUtil.getCleanErrorMessage(ex), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
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
        jLabelIcon = new javax.swing.JLabel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelSepLine2New = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel23 = new javax.swing.JPanel();
        jPanelEditorPane = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jEditorPane = new javax.swing.JEditorPane();
        jPanel5 = new javax.swing.JPanel();
        jPanelSep = new javax.swing.JPanel();
        jPaneCodeButton = new javax.swing.JPanel();
        jButtonGetCode = new javax.swing.JButton();
        jPanelSep2 = new javax.swing.JPanel();
        jPaneCode = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabelCode = new javax.swing.JLabel();
        jPanel5on5 = new javax.swing.JPanel();
        jTextFieldCode = new javax.swing.JTextField();
        jPanelCheckBoxInsertName = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jCheckBoxDisplayFirstNameBefore = new javax.swing.JCheckBox();
        jPanelSepBlank13 = new javax.swing.JPanel();
        jPanelSep1 = new javax.swing.JPanel();
        jPanelSepLine2New1 = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel25 = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jButtonNext = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 12));

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/icons8-gmail-32.png"))); // NOI18N
        jPanelNorth.add(jLabelIcon);

        jLabelTitle.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabelTitle.setText("Importer des Contacts depuis un compte Gmail");
        jPanelNorth.add(jLabelTitle);

        getContentPane().add(jPanelNorth);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelSepLine2New.setMaximumSize(new java.awt.Dimension(32787, 10));
        jPanelSepLine2New.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepLine2New.setLayout(new javax.swing.BoxLayout(jPanelSepLine2New, javax.swing.BoxLayout.LINE_AXIS));

        jPanel22.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine2New.add(jPanel22);
        jPanelSepLine2New.add(jSeparator2);

        jPanel23.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine2New.add(jPanel23);

        jPanelCenter.add(jPanelSepLine2New);

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
        jEditorPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
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

        jPanelSep.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSep.setMinimumSize(new java.awt.Dimension(0, 5));
        jPanelSep.setPreferredSize(new java.awt.Dimension(376, 5));

        javax.swing.GroupLayout jPanelSepLayout = new javax.swing.GroupLayout(jPanelSep);
        jPanelSep.setLayout(jPanelSepLayout);
        jPanelSepLayout.setHorizontalGroup(
            jPanelSepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 434, Short.MAX_VALUE)
        );
        jPanelSepLayout.setVerticalGroup(
            jPanelSepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSep);

        jPaneCodeButton.setMaximumSize(new java.awt.Dimension(32767, 26));
        jPaneCodeButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 5));

        jButtonGetCode.setForeground(new java.awt.Color(0, 0, 255));
        jButtonGetCode.setText("Obtenir un Code ");
        jButtonGetCode.setBorder(null);
        jButtonGetCode.setBorderPainted(false);
        jButtonGetCode.setContentAreaFilled(false);
        jButtonGetCode.setFocusPainted(false);
        jButtonGetCode.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButtonGetCode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonGetCodeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonGetCodeMouseExited(evt);
            }
        });
        jButtonGetCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGetCodeActionPerformed(evt);
            }
        });
        jPaneCodeButton.add(jButtonGetCode);

        jPanelCenter.add(jPaneCodeButton);

        jPanelSep2.setMaximumSize(new java.awt.Dimension(32767, 20));
        jPanelSep2.setMinimumSize(new java.awt.Dimension(0, 20));
        jPanelSep2.setPreferredSize(new java.awt.Dimension(376, 20));
        jPanelSep2.setRequestFocusEnabled(false);

        javax.swing.GroupLayout jPanelSep2Layout = new javax.swing.GroupLayout(jPanelSep2);
        jPanelSep2.setLayout(jPanelSep2Layout);
        jPanelSep2Layout.setHorizontalGroup(
            jPanelSep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 434, Short.MAX_VALUE)
        );
        jPanelSep2Layout.setVerticalGroup(
            jPanelSep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSep2);

        jPaneCode.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPaneCode.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jPanel2.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPaneCode.add(jPanel2);

        jLabelCode.setText("Code");
        jPaneCode.add(jLabelCode);

        jPanel5on5.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel5on5.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel5on5Layout = new javax.swing.GroupLayout(jPanel5on5);
        jPanel5on5.setLayout(jPanel5on5Layout);
        jPanel5on5Layout.setHorizontalGroup(
            jPanel5on5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel5on5Layout.setVerticalGroup(
            jPanel5on5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPaneCode.add(jPanel5on5);

        jTextFieldCode.setPreferredSize(new java.awt.Dimension(300, 22));
        jPaneCode.add(jTextFieldCode);

        jPanelCenter.add(jPaneCode);

        jPanelCheckBoxInsertName.setMaximumSize(new java.awt.Dimension(32767, 35));
        jPanelCheckBoxInsertName.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jPanel3.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelCheckBoxInsertName.add(jPanel3);

        jCheckBoxDisplayFirstNameBefore.setText("Insérer le prénom avant le nom");
        jCheckBoxDisplayFirstNameBefore.setToolTipText("");
        jCheckBoxDisplayFirstNameBefore.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxDisplayFirstNameBeforeItemStateChanged(evt);
            }
        });
        jPanelCheckBoxInsertName.add(jCheckBoxDisplayFirstNameBefore);

        jPanelSepBlank13.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSepBlank13.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanelSepBlank13Layout = new javax.swing.GroupLayout(jPanelSepBlank13);
        jPanelSepBlank13.setLayout(jPanelSepBlank13Layout);
        jPanelSepBlank13Layout.setHorizontalGroup(
            jPanelSepBlank13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanelSepBlank13Layout.setVerticalGroup(
            jPanelSepBlank13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelCheckBoxInsertName.add(jPanelSepBlank13);

        jPanelCenter.add(jPanelCheckBoxInsertName);

        jPanelSep1.setMinimumSize(new java.awt.Dimension(0, 20));
        jPanelSep1.setPreferredSize(new java.awt.Dimension(376, 20));
        jPanelSep1.setRequestFocusEnabled(false);

        javax.swing.GroupLayout jPanelSep1Layout = new javax.swing.GroupLayout(jPanelSep1);
        jPanelSep1.setLayout(jPanelSep1Layout);
        jPanelSep1Layout.setHorizontalGroup(
            jPanelSep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 434, Short.MAX_VALUE)
        );
        jPanelSep1Layout.setVerticalGroup(
            jPanelSep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSep1);

        jPanelSepLine2New1.setMaximumSize(new java.awt.Dimension(32787, 10));
        jPanelSepLine2New1.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepLine2New1.setLayout(new javax.swing.BoxLayout(jPanelSepLine2New1, javax.swing.BoxLayout.LINE_AXIS));

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

        jPanelSepLine2New1.add(jPanel24);
        jPanelSepLine2New1.add(jSeparator3);

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

        jPanelSepLine2New1.add(jPanel25);

        jPanelCenter.add(jPanelSepLine2New1);

        getContentPane().add(jPanelCenter);

        jPanelSouth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonNext.setText("Suivant > ");
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

    private void jButtonGetCodeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonGetCodeMouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ButtonUrlOver.enter(evt);
    }//GEN-LAST:event_jButtonGetCodeMouseEntered

    private void jButtonGetCodeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonGetCodeMouseExited
        this.setCursor(Cursor.getDefaultCursor());
        ButtonUrlOver.exit(evt);
    }//GEN-LAST:event_jButtonGetCodeMouseExited

    private void jButtonGetCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGetCodeActionPerformed
        getGoogleCode();
    }//GEN-LAST:event_jButtonGetCodeActionPerformed

    private void jCheckBoxDisplayFirstNameBeforeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxDisplayFirstNameBeforeItemStateChanged
        if (jCheckBoxDisplayFirstNameBefore.isSelected()) {
            jCheckBoxDisplayFirstNameBefore.setText(MessagesManager.get("display_firstname_before_lastname") + " (Ex: John Smith)");
        } else {
            jCheckBoxDisplayFirstNameBefore.setText(MessagesManager.get("display_firstname_before_lastname") + " (Ex: Smith John)");
        }
    }//GEN-LAST:event_jCheckBoxDisplayFirstNameBeforeItemStateChanged

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

                throw new IllegalArgumentException("Not Implemented.");
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonGetCode;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JCheckBox jCheckBoxDisplayFirstNameBefore;
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JLabel jLabelCode;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPaneCode;
    private javax.swing.JPanel jPaneCodeButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel5on5;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelCheckBoxInsertName;
    private javax.swing.JPanel jPanelEditorPane;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSepBlank13;
    private javax.swing.JPanel jPanelSepLine2New;
    private javax.swing.JPanel jPanelSepLine2New1;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTextFieldCode;
    // End of variables declaration//GEN-END:variables

}
