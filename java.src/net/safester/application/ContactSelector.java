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
package net.safester.application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.safelogic.utilx.Base64;
import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.photo.GroupListNew;
import net.safester.application.photo.ImageResizer;
import net.safester.application.photo.PhotoAddressBookListTransfer;
import net.safester.application.photo.PhotoAddressBookLocal;
import net.safester.application.photo.PhotoAddressBookTableCreatorNew;
import net.safester.application.photo.PhotoAddressBookUpdaterNew;
import net.safester.application.photo.PhotoDisplayer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.TableUtil;
import net.safester.clientserver.UserPhotoLocal;
import net.safester.noobs.clientserver.GsonUtil;

/**
 * This Window is displayed when composing an email and press To or Cc Button
 * @author Alexandre Becquereau
 */
public class ContactSelector extends javax.swing.JFrame {

    //MessageManager
    private MessagesManager messages = new MessagesManager();
    //Contact List
    private List<PhotoAddressBookLocal> addresses;
    //Clipboard manager
    private ClipboardManager clipboard1 = null;
    //Calling JFrame
    private JFrame parent;
    private JFrame thisOne;
    //Connection to db
    private Connection connection;
    //Popup menu
    private JPopupMenu popupMenu;
    //Index to tell if was called from To button or Cc button
    private int origin;

    /**
     * Constructor
     * @param caller        Calling JFrame
     * @param origin        Index of button that called the AddressBook can be
     *                      MessageComposer.BUTTON_TO or MessageComposer.BUTTON_CC
     */
    public ContactSelector(JFrame caller, Connection theConnection, int origin) {
        //Check param
        if (theConnection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        if (origin != MessageComposer.BUTTON_TO && origin != MessageComposer.BUTTON_CC 
                && origin != MessageComposer.BUTTON_BCC && origin != GroupEditor.BUTTON_ADD) {
            throw new IllegalArgumentException("Invalid origin: " + origin);
        }
        //Calling JFrame
        this.parent = caller;
        this.connection = theConnection;

        //Index of JButton that called AddressBook
        this.origin = origin;
        thisOne = this;
        initComponents();
        initCompany();
    }

    /**
     * Our graphic init method
     */
    private void initCompany() {
        
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        
        jComboBoxSearch.removeAllItems();
        jComboBoxSearch.addItem(messages.getMessage("name"));
        jComboBoxSearch.addItem(messages.getMessage("email"));
        jComboBoxSearch.setSelectedIndex(0);
        jLabelSearch.setText(messages.getMessage("search"));
        jTextFieldSearch.setText(null);
        jLabelIn.setText(messages.getMessage("in"));
        jButtonClose.setText(messages.getMessage("close_button"));
        jTextAreaHelp.setFont(jButtonClose.getFont());
        jTextAreaHelp.setText(messages.getMessage("select_and_use_right_click_to_add"));
        //Build popup menu
        popupMenu = new JPopupMenu();
        if( origin != GroupEditor.BUTTON_ADD){
            initPopupForComposer();
        }else{
            initPopupForGroupCreator();
        }

        this.jTextFieldSearch.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                textFieldSearchKeyPressed(e);
            }
        });

        this.jLabelTitle.setText(messages.getMessage("address_book"));
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                initializeAddressBook();
            }
        });
        
        // No choice of item to choose now
        jLabelIn.setVisible(false);
        jComboBoxSearch.setVisible(false);
        
        jTextAreaHelp.setBackground(Color.WHITE);
        this.keyListenerAdder();
        
        clipboard1 = new ClipboardManager(jTextFieldSearch);
        this.setTitle(messages.getMessage("address_book"));

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                WindowSettingManager.save(thisOne);
            }
        });

        SwingUtil.resizeJComponentsForAll(rootPane);
        SwingUtil.setBackgroundColor(this, Color.WHITE);
        
        this.setLocationRelativeTo(parent);
                
        WindowSettingManager.load(this);
    }

    private void initPopupForGroupCreator(){
        JMenuItem itemAddGroup = new JMenuItem(messages.getMessage("add_to_group"));
        itemAddGroup.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doIt(GroupEditor.BUTTON_ADD);
            }
        });
        popupMenu.add(itemAddGroup);
    }

    private void initPopupForComposer() {
        JMenuItem itemTo = new JMenuItem(messages.getMessage("add_to_recipients"));
        itemTo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doIt(MessageComposer.BUTTON_TO);
            }
        });
        popupMenu.add(itemTo);
        JMenuItem itemCc = new JMenuItem(messages.getMessage("add_to_cc_recipients"));
        itemCc.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doIt(MessageComposer.BUTTON_CC);
            }
        });
        popupMenu.add(itemCc);
        JMenuItem itemBcc = new JMenuItem(messages.getMessage("add_to_bcc_recipients"));
        itemBcc.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doIt(MessageComposer.BUTTON_BCC);
            }
        });
        popupMenu.add(itemBcc);
    }

    /**
     * Init JTable with address book
     */
    public void initializeAddressBook() {
        
        //Load addresses
        try {
            addresses = loadAddresses();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPaneNewCustom.showException(parent, e, messages.getMessage("unable_to_load_address_book"));
            this.dispose();
            return;
        }
                
        PhotoAddressBookTableCreatorNew tableCreator = new PhotoAddressBookTableCreatorNew(addresses, this, false);
        this.jTable1 = tableCreator.create();
        
        jTable1.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                addressList_mousePressedOrReleased(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                addressList_mousePressedOrReleased(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 1) {
                    displayPhoto(null);
                }
            }
                        
        });
        
        jTable1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                this_keyPressed(e);
            }
        });      
        
        TableUtil.selectRowWhenMouverOverAddressLine(jTable1);
        
        jScrollPane1.setViewportView(jTable1);
        jScrollPane1.getViewport().setBackground(Color.WHITE);
    }

    // Display the key detail in a new Window
    private void displayPhoto(ActionEvent e) {
        int[] selRows = jTable1.getSelectedRows();

        if (selRows.length > 0) {

            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            try {

                // get Table data
                TableModel tm = jTable1.getModel();

                String email = null;
                Object obj = tm.getValueAt(selRows[0], 2);

                if (obj != null && obj instanceof String) {
                    email = (String) obj;
                }

                if (email == null) {
                    return;
                }

                
//                UserPhoto userPhoto = new UserPhoto();
//                userPhoto.setUserEmail(email.toLowerCase().trim());
//
//                boolean exists = userPhoto.read(connection);
//
//                if (!exists) {
//                    return;
//                }
//                
                AwakeConnection awakeConnection = (AwakeConnection) connection;
                AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

                String methodRemote = "net.safester.server.hosts.newapi.UserPhotoNewApi.get";
                String jsonString = awakeFileSession.call(methodRemote,
                				      email.toLowerCase().trim(),
            	    				      connection
                                                        );
                    
                UserPhotoLocal userPhotoLocal = GsonUtil.userPhotoFromGson(jsonString);

                if (userPhotoLocal.getUserEmail() == null) {
                    return;
                }
                
                String photoBase64 = userPhotoLocal.getPhoto();

                if (photoBase64 == null || photoBase64.length() < 10) {
                    return;
                }

                ImageResizer imageResizer = new ImageResizer(Base64.base64ToByteArray(photoBase64));
                new PhotoDisplayer(this, imageResizer.getImage(), 200).setVisible(true);

            } catch (Exception ex) {
                getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                JOptionPaneNewCustom.showException(this, ex, messages.getMessage("cannot_display_photo"));
            } finally {
                this.setCursor(Cursor.getDefaultCursor());
            }

            //JOptionPane.showMessageDialog(parent, "Ok! " + email);
        } else {
            JOptionPane.showMessageDialog(this, this.messages.getMessage("SELECT_KEY_MSG"));
        }
    }
    
		    /**
     * Method called when user press a key in JTextField
     *
     * @param e
     */
    private void textFieldSearchKeyPressed(KeyEvent e) {
        
        //System.out.println("textFieldSearchKeyPressed");
        int id = e.getID();
        if (id == KeyEvent.KEY_PRESSED) {

            if (jTextFieldSearch.getText().trim().isEmpty()) {
                return;
            }

            PhotoAddressBookUpdaterNew.searchInTable(jTable1, jTextFieldSearch.getText().trim());
            
            //TableUtil.searchInTable(jTable1, jTextFieldSearch.getText().trim());
        }
        
    }


    /**
     * Send selected email to MailComposer
     * @param destination   Index of JTextArea where to print selected email
     */
    private void doIt(int destination) {
        int[] selectedRows = jTable1.getSelectedRows();
        java.util.Set<String> selectedEmails = new HashSet<String>();

        try {
            //get all selected rows
            for (int i = 0; i < selectedRows.length; i++) {
                int index = selectedRows[i];
                String name = (String) jTable1.getValueAt(index, 1);
                String keyId = (String) jTable1.getValueAt(index, 2);

                String nameAndEmailAddress = buildNameAndEmailAddress(name, keyId);

                selectedEmails.add(nameAndEmailAddress);
            }
            switch (destination){
                case MessageComposer.BUTTON_TO: ((MessageComposer) parent).addRecipientsTo(selectedEmails);
                break;
                case MessageComposer.BUTTON_CC: ((MessageComposer) parent).addCopyRecipients(selectedEmails);
                break;
                case GroupEditor.BUTTON_ADD: ((GroupEditor) parent).addGroupMembers(selectedEmails);
                break;
                default: ((MessageComposer) parent).addBlindCopyRecipients(selectedEmails);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPaneNewCustom.showException(this, e);
        }
    }

    /**
     * Build a "name <email@domain.net>" string from name and email@domain.net
     * @param name      the name
     * @param keyId     the email
     * @return
     */
    private String buildNameAndEmailAddress(String name, String email) {

        // Security check 2
        if (name == null || name.isEmpty())
        {
            return email;
        }

        // Security check 2
        if (email == null)
        {
            return name;
        }

        // Ok, all is filled
        String nameAndEmailAddress = name.trim() + " <" + email.trim() + ">";
        return nameAndEmailAddress;

    }
    
    /**
     * When the Mouse is pressed or released...
     * @param e     The Mouse Eevent
     */
    public void addressList_mousePressedOrReleased(MouseEvent e) {
        int[] selRows = jTable1.getSelectedRows();
        jTable1.requestFocusInWindow();
        //System.out.println("there, " + e.isPopupTrigger());
        //If selection is empty, select the row near the mouse location
        if (selRows.length == 0) {
            ListSelectionModel selectionModel = jTable1.getSelectionModel();
            int index = jTable1.rowAtPoint(e.getPoint());
            selectionModel.setSelectionInterval(index, index);
        }
        if (e.isPopupTrigger()) {

            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * Load all address of Address Book in a List
     * @retur
     */
    private List<PhotoAddressBookLocal> loadAddresses()
            throws SQLException {
        int userId = -1;
        if (parent instanceof MessageComposer) {
            Main mainWindow = ((MessageComposer) parent).getCaller();
            if (mainWindow != null) {
                userId = mainWindow.getUserNumber();
            }
        }else if(parent instanceof GroupEditor){
            userId = ((GroupEditor)parent).getUserNumber();
        }else{
            userId = ((GroupListNew)parent).getUserNumber();
        }

        PhotoAddressBookListTransfer addressBookListTransfer = new PhotoAddressBookListTransfer(connection, userId);
        return addressBookListTransfer.getList();
    }

    /**
     * Universal key listener
     *
     */
    private void keyListenerAdder() {
        java.util.List<Component> components = SwingUtil.getAllComponants(this);

        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);

            comp.addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    this_keyPressed(e);
                }
            });
        }
    }

    private void this_keyPressed(KeyEvent e) {
        
        int id = e.getID();
        if (id == KeyEvent.KEY_PRESSED) {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_ESCAPE) {
                close();
            }
        }
    }

    /**
     * Close window
     */
    public void close() {
        this.dispose();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelNorth = new javax.swing.JPanel();
        jLabelIcon = new javax.swing.JLabel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelSep1 = new javax.swing.JPanel();
        jPanelSearch1 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanelHelp = new javax.swing.JPanel();
        jPanelSepLeft = new javax.swing.JPanel();
        jTextAreaHelp = new javax.swing.JTextArea();
        jPanelSepLeft1 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanelSep = new javax.swing.JPanel();
        jPanelTable = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jPanelSep3 = new javax.swing.JPanel();
        jPanelSearch = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabelSearch = new javax.swing.JLabel();
        jTextFieldSearch = new javax.swing.JTextField();
        jLabelIn = new javax.swing.JLabel();
        jComboBoxSearch = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelNorth.setMaximumSize(new java.awt.Dimension(32767, 56));
        jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 12));

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/businesspeople.png"))); // NOI18N
        jPanelNorth.add(jLabelIcon);

        jLabelTitle.setText("Address Book");
        jPanelNorth.add(jLabelTitle);

        getContentPane().add(jPanelNorth);

        jPanelSep1.setMaximumSize(new java.awt.Dimension(32767, 10));
        getContentPane().add(jPanelSep1);

        jPanelSearch1.setMaximumSize(new java.awt.Dimension(32803, 23));
        jPanelSearch1.setMinimumSize(new java.awt.Dimension(10, 23));
        jPanelSearch1.setPreferredSize(new java.awt.Dimension(10, 23));
        jPanelSearch1.setLayout(new javax.swing.BoxLayout(jPanelSearch1, javax.swing.BoxLayout.X_AXIS));

        jPanel10.setMaximumSize(new java.awt.Dimension(12, 10));
        jPanel10.setPreferredSize(new java.awt.Dimension(12, 10));
        jPanelSearch1.add(jPanel10);

        jPanelHelp.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(240, 240, 240), 1, true));
        jPanelHelp.setLayout(new javax.swing.BoxLayout(jPanelHelp, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSepLeft.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSepLeft.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelSepLeft.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanelSepLeftLayout = new javax.swing.GroupLayout(jPanelSepLeft);
        jPanelSepLeft.setLayout(jPanelSepLeftLayout);
        jPanelSepLeftLayout.setHorizontalGroup(
            jPanelSepLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanelSepLeftLayout.setVerticalGroup(
            jPanelSepLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelHelp.add(jPanelSepLeft);

        jTextAreaHelp.setEditable(false);
        jTextAreaHelp.setColumns(20);
        jTextAreaHelp.setRows(5);
        jTextAreaHelp.setBorder(null);
        jTextAreaHelp.setMargin(new java.awt.Insets(3, 3, 3, 3));
        jTextAreaHelp.setName(""); // NOI18N
        jPanelHelp.add(jTextAreaHelp);

        jPanelSepLeft1.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSepLeft1.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanelSepLeft1Layout = new javax.swing.GroupLayout(jPanelSepLeft1);
        jPanelSepLeft1.setLayout(jPanelSepLeft1Layout);
        jPanelSepLeft1Layout.setHorizontalGroup(
            jPanelSepLeft1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanelSepLeft1Layout.setVerticalGroup(
            jPanelSepLeft1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelHelp.add(jPanelSepLeft1);

        jPanelSearch1.add(jPanelHelp);

        jPanel12.setMaximumSize(new java.awt.Dimension(12, 10));
        jPanel12.setPreferredSize(new java.awt.Dimension(12, 10));
        jPanelSearch1.add(jPanel12);

        getContentPane().add(jPanelSearch1);

        jPanelSep.setMaximumSize(new java.awt.Dimension(32767, 10));
        getContentPane().add(jPanelSep);

        jPanelTable.setLayout(new javax.swing.BoxLayout(jPanelTable, javax.swing.BoxLayout.X_AXIS));

        jPanel7.setMaximumSize(new java.awt.Dimension(10, 32767));
        jPanel7.setMinimumSize(new java.awt.Dimension(10, 0));
        jPanel7.setPreferredSize(new java.awt.Dimension(10, 306));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 263, Short.MAX_VALUE)
        );

        jPanelTable.add(jPanel7);

        jTable1.setAutoCreateRowSorter(true);
        jScrollPane1.setViewportView(jTable1);

        jPanelTable.add(jScrollPane1);

        jPanel8.setMaximumSize(new java.awt.Dimension(10, 32767));
        jPanel8.setMinimumSize(new java.awt.Dimension(10, 0));
        jPanel8.setPreferredSize(new java.awt.Dimension(10, 306));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 263, Short.MAX_VALUE)
        );

        jPanelTable.add(jPanel8);

        getContentPane().add(jPanelTable);

        jPanelSep3.setMaximumSize(new java.awt.Dimension(10, 10));
        getContentPane().add(jPanelSep3);

        jPanelSearch.setMaximumSize(new java.awt.Dimension(32791, 30));
        jPanelSearch.setLayout(new javax.swing.BoxLayout(jPanelSearch, javax.swing.BoxLayout.X_AXIS));

        jPanel1.setMaximumSize(new java.awt.Dimension(12, 10));
        jPanel1.setPreferredSize(new java.awt.Dimension(12, 10));
        jPanelSearch.add(jPanel1);

        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 30));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelSearch.setText("Search: ");
        jPanel2.add(jLabelSearch);

        jTextFieldSearch.setText("jTextField1");
        jTextFieldSearch.setPreferredSize(new java.awt.Dimension(200, 22));
        jPanel2.add(jTextFieldSearch);

        jLabelIn.setText("jLabel1");
        jPanel2.add(jLabelIn);

        jComboBoxSearch.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel2.add(jComboBoxSearch);

        jPanelSearch.add(jPanel2);

        jPanel6.setMaximumSize(new java.awt.Dimension(12, 10));
        jPanel6.setPreferredSize(new java.awt.Dimension(12, 10));
        jPanelSearch.add(jPanel6);

        getContentPane().add(jPanelSearch);

        jPanel5.setMaximumSize(new java.awt.Dimension(32767, 43));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        jButtonClose.setText("Close");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanel5.add(jButtonClose);

        getContentPane().add(jPanel5);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        close();
    }//GEN-LAST:event_jButtonCloseActionPerformed
    /**
     * @param args the command line arguments
     */
//    public static void main(String args[])
//            throws Exception {
//
//        try {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
////                    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
////                        if ("Nimbus".equals(info.getName())) {
////                             UIManager.setLookAndFeel(info.getClassName());
////                        break;
////                        }
////                    }
//        } catch (Exception ex) {
//            System.out.println("Failed loading L&F: ");
//            System.out.println(ex);
//        }
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    new ContactSelector(null, Parms.getConnection(), 0).setVisible(true);
//                } catch (Exception ex) {
//                    Logger.getLogger(ContactSelector.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JComboBox jComboBoxSearch;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelIn;
    private javax.swing.JLabel jLabelSearch;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelHelp;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSearch;
    private javax.swing.JPanel jPanelSearch1;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep3;
    private javax.swing.JPanel jPanelSepLeft;
    private javax.swing.JPanel jPanelSepLeft1;
    private javax.swing.JPanel jPanelTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextAreaHelp;
    private javax.swing.JTextField jTextFieldSearch;
    // End of variables declaration//GEN-END:variables

}
