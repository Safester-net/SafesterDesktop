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
package net.safester.application.photo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.SystemUtils;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.kawansoft.crypt.sms.MobilePhoneValidator;
import com.kawansoft.crypt.sms.MobileUtil;
import com.safelogic.utilx.Base64;
import com.swing.util.SwingUtil;

import net.safester.application.Main;
import net.safester.application.MessageComposer;
import net.safester.application.addrbooknew.AddressBookImportStart;
import net.safester.application.addrbooknew.RecipientEntry;
import net.safester.application.addrbooknew.tools.CryptAppUtil;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.TableUtil;
import net.safester.clientserver.UserNumberGetterClient;
import net.safester.clientserver.UserPhotoLocal;
import net.safester.noobs.clientserver.AddressBookListTransfer;
import net.safester.noobs.clientserver.AddressBookNewLocal;
import net.safester.noobs.clientserver.GsonUtil;

/*
 * AddressBookUpdater.java
 *
 * Main address book window
 */
public class PhotoAddressBookUpdaterNew extends javax.swing.JFrame {

    public static boolean DEBUG = false;
        
    public static final String CR_LF = System.getProperty("line.separator");
        
    public static int TEST_USERID = 1;
    //MessageManager
    private MessagesManager messages = new MessagesManager();
    JPopupMenu jPopupMenu;
    //Contact list
    private List<PhotoAddressBookLocal> addresses;
    //Connection to db
    private Connection connection;
    //Calling jFrame
    private JFrame parent;
    JFrame thisOne;
    //User Id of owner
    int userId;
    private boolean updated = false;
    ClipboardManager clipboardManager;

    private AddressBookImportStart addressBookImportStart;
    private GroupListNew groupListNew;
    
    /**
     * Creates new form AddressBookUpdater
     */
    public PhotoAddressBookUpdaterNew(JFrame theParent, Connection theConnection, int theUserNumber) {
        this.parent = theParent;
        this.connection = theConnection;
//        if (parent instanceof SafeShareMain) {
//            SafeShareMain caller = (SafeShareMain) parent;
//            userId = caller.getUserNumber();
//        }
        userId = theUserNumber;
        thisOne = this;
        initComponents();
        initCompany();
    }

    public JFrame getCaller() {
        return this.parent;
    }

    /**
     * Delete all selected contact in db
     */
    public void deleteSelectedLines() {
        //if selection is empty
        if (jTable1.getSelectedRow() == -1) {
            return;
        }

        String text = messages.getMessage("confirm_contact_delete");
        String title = messages.getMessage("warning");

        int result = JOptionPane.showConfirmDialog(this, text, title, JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.NO_OPTION) {
            return;
        }

        //Delete rows from TableModel
        int[] selectedRows = jTable1.getSelectedRows();
        DefaultTableModel tm = (DefaultTableModel) jTable1.getModel();
        for (int index = selectedRows.length - 1; index >= 0; index--) {
            tm.removeRow(selectedRows[index]);
        }
        this.updated = true;
        //Update db using jtable elements
        //updateListFromTable();
    }

    private boolean save() {
        boolean saveDone = updateListFromTable();
        if (saveDone) {
            jButtonSave.setEnabled(false);
        }
        return saveDone;
    }


    /**
     * Set selected line in edition mode
     */
    public void editSelectedLine() {
        
        if (jTable1.getSelectedRow() == -1) {
            //Selection is empty do nothing
            return;
        }
        
        //System.out.println("jTable1.getSelectedRow(): " + jTable1.getSelectedRow());
        
        //jTable1.requestFocus();
        jTable1.requestFocusInWindow();
        jTable1.editCellAt(jTable1.getSelectedRow(), 1);
        
        //jTable1.validate();
        //update(getGraphics());
    }

    /**
     * Our Swing init method
     */
    private void initCompany() {
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        this.setTitle(messages.getMessage("address_book"));

        jPopupMenu = new JPopupMenu();

        jComboBoxSearch.removeAllItems();
        jComboBoxSearch.addItem(messages.getMessage("name"));
        jComboBoxSearch.addItem(messages.getMessage("email"));
        jComboBoxSearch.setSelectedIndex(0);
        jLabelSearch.setText(messages.getMessage("search"));
        jTextFieldSearch.setText(null);
        jLabelIn.setText(messages.getMessage("in"));
        //jTextFieldCount.setText("");

        jMenuItemSave.setText(messages.getMessage("save_changes"));
        jMenuItemImport.setText(messages.getMessage("importing_contacts"));
        jMenuItemImport.setEnabled(true);
        jMenuItemImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importAddressBook();
            }
        });
        jMenuItemExport.setText(messages.getMessage("export_contact"));
        jMenuItemExport.setEnabled(false);
        jMenuItemClose.setText(messages.getMessage("close"));
        jButtonAddRecipients.setText(messages.getMessage("add"));
        jButtonDeleteContact.setText(messages.getMessage("delete_contact"));
        jButtonEditContact.setText(messages.getMessage("edit_contact"));
        jButtonSendMessageToContact.setText(messages.getMessage("mail_to"));
        jButtonSave.setText(messages.getMessage("save_changes"));
        jButtonGroups.setText(messages.getMessage("group_button"));

        jButtonDeleteContact.setToolTipText(messages.getMessage("delete_contact"));
        jButtonEditContact.setToolTipText(messages.getMessage("edit_contact"));
        jButtonSendMessageToContact.setToolTipText(messages.getMessage("mail_to"));
        jButtonSave.setToolTipText(messages.getMessage("save_changes"));
        jButtonGroups.setToolTipText(messages.getMessage("group_button"));

        jMenuFile.setText(messages.getMessage("file"));
                
        if (SystemUtils.IS_OS_MAC_OSX) {
            jMenuItemClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        } else {
            jMenuItemClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        }
                
        //jButtonSendMessageToContact.setEnabled(false);

        JMenuItem popupItemEdit = new JMenuItem(messages.getMessage("edit_contact"));
        popupItemEdit.setIcon(Parms.createImageIcon("images/files_2/16x16/businessman2_edit.png"));
        popupItemEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jButtonEditContactActionPerformed(e);
            }
        });

        jPopupMenu.add(popupItemEdit);

        JMenuItem popupItemDelete = new JMenuItem(messages.getMessage("delete_contact"));
        popupItemDelete.setIcon(Parms.createImageIcon("images/files_2/16x16/delete.png"));
        popupItemDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jButtonDeleteContactActionPerformed(e);
            }
        });

        jPopupMenu.add(popupItemDelete);

        jPopupMenu.addSeparator();

        JMenuItem popupItemMailTo = new JMenuItem(messages.getMessage("mail_to"));
        popupItemMailTo.setIcon(Parms.createImageIcon("images/files_2/16x16/mail_write.png"));
        popupItemMailTo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jButtonSendMessageToContactActionPerformed(e);
            }
        });

        if ((parent instanceof Main)) {
            jPopupMenu.add(popupItemMailTo);
        }
                        
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                initializeAddressBook();
//            }
//        });

        Thread t = new Thread() {
            @Override
            public void run() {
                initializeAddressBook();
            }
        };
        t.start();

        this.jTextFieldSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                textFieldSearchKeyPressed(e);
            }
        });

        clipboardManager = new ClipboardManager(this);

        jPanelRecipientEmail.remove(jTextFieldRecipientEmail1);
        jTextFieldRecipientEmail1 = new JPromptTextField();
        ((JPromptTextField) jTextFieldRecipientEmail1).setPromptText(MessagesManager.get("email"));
        jPanelRecipientEmail.add(jTextFieldRecipientEmail1);

        jPanelRecipientName.remove(jTextFieldRecipientName);
        jTextFieldRecipientName = new JPromptTextField();
        ((JPromptTextField) jTextFieldRecipientName).setPromptText(MessagesManager.get("last_and_first_name"));
        jPanelRecipientName.add(jTextFieldRecipientName);

        jPanelRecipientCompany.remove(jTextFieldCompany);
        jTextFieldCompany = new JPromptTextField();
        ((JPromptTextField) jTextFieldCompany).setPromptText(MessagesManager.get("company"));
        jPanelRecipientCompany.add(jTextFieldCompany);
        
        jPanelRecipientMobile.remove(jTextFieldMobile);
        jTextFieldMobile = new JPromptTextField();
        ((JPromptTextField) jTextFieldMobile).setPromptText(MessagesManager.get("mobile_number"));
        jPanelRecipientMobile.add(jTextFieldMobile);
                
        this.keyListenerAdder();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                WindowSettingManager.save(thisOne);
                close();
            }
        });

        SwingUtil.resizeJComponentsForNimbusAndMacOsX(rootPane);
        SwingUtil.setBackgroundColor(this, Color.white);
        
        // Exception
        jPanelToobar.setBackground(new Color(240, 240, 240));

        jTextFieldCompany.setBackground(Color.WHITE);
        jTextFieldMobile.setBackground(Color.WHITE);
        jTextFieldRecipientEmail1.setBackground(Color.WHITE);
        jTextFieldRecipientName.setBackground(Color.WHITE);
        jTextFieldSearch.setBackground(Color.WHITE);
        
        if (!(parent instanceof Main)) {
            jToolBar1.remove(jButtonSendMessageToContact);
            jToolBar1.remove(jSeparatorSendMessagesToContact);
        }

        // No choice of item to choose now
        jLabelIn.setVisible(false);
        jComboBoxSearch.setVisible(false);
        
        jButtonSave.setEnabled(false);
        jMenuItemSave.setEnabled(false);
                
        this.setLocationRelativeTo(parent);
        WindowSettingManager.load(this);
    }

    private void importAddressBook() {
        if (addressBookImportStart != null) {
            addressBookImportStart.dispose();
        }

        addressBookImportStart = new AddressBookImportStart(this.parent, connection, userId);
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

            if (keyCode == KeyEvent.VK_ESCAPE) {
                close();
            }
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

            searchInTable(jTable1, jTextFieldSearch.getText().trim());
            
            //TableUtil.searchInTable(jTable1, jTextFieldSearch.getText().trim());
        }
        
    }

     /**
     * Search the first item in table while user is typing in JTextField. Seach
     * is done on col 0 or 1 or 2
     *
     * @param jTable the table to search in
     * @param pattern String currently in JTextField
     */
    public static void searchInTable(JTable jTable, String pattern) {
        int rows = jTable.getRowCount();
        List<Integer> listRrows = new ArrayList<Integer>();
        for (int i = 0; i < rows; i++) {
            listRrows.add(jTable.convertRowIndexToModel(i));
        }
        for (int i = 0; i < rows; i++) {
            String valueCol0 = (String) jTable.getModel().getValueAt(listRrows.get(i), 0);
            String valueCol1 = (String) jTable.getModel().getValueAt(listRrows.get(i), 1);
            String valueCol2 = (String) jTable.getModel().getValueAt(listRrows.get(i), 2);


            if ((valueCol0 != null && valueCol0.toLowerCase().contains(pattern.toLowerCase())) 
                    || (valueCol1 != null && valueCol1.toLowerCase().contains(pattern.toLowerCase())) 
                    || (valueCol2 != null && valueCol2.toLowerCase().contains(pattern.toLowerCase())) 
                    ) {
                ListSelectionModel selectionModel = jTable.getSelectionModel();
                
                if (false) {
                    System.out.println("valueCol0 = " + valueCol0);
                    System.out.println("i         = " + i);
                    System.out.println("converted = " + listRrows.get(i));
                }
                
                selectionModel.setSelectionInterval(i, i);
                Rectangle rect = jTable.getCellRect(i, 0, true);
                jTable.scrollRectToVisible(rect);
                return;
            }
        }
        jTable.clearSelection();
    }
   
    /**
     * Init JTable with address book
     */
    public void initializeAddressBook() {

        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            addresses = loadAddresses();
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } catch (SQLException e) {
            e.printStackTrace();
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPaneNewCustom.showException(parent, e, messages.getMessage("unable_to_load_address_book"));
            this.dispose();
            return;
        }
        
        PhotoAddressBookTableCreatorNew tableCreator = new PhotoAddressBookTableCreatorNew(addresses, this, true);
        this.jTable1 = tableCreator.create();
        
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String count = myFormatter.format(jTable1.getModel().getRowCount());
                
        // Don't know why there is a ":"
        count = count.replace(":", "");
        count = count.replace(" ", "");
        
        //setTextFieldAsLabel(jLabelCount);
        jLabelCount.setText(count + " Contacts");
         
        jTable1.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                TableModel tm1 = (TableModel) e.getSource();
                DecimalFormat myFormatter = new DecimalFormat("###,###");
                String count = myFormatter.format(tm1.getRowCount());
                
                // Don't know why there is a ":"
                count = count.replace(":", "");
                count = count.replace(" ", "");
                
                jLabelCount.setText(count + " Contacts");
                
                jButtonSave.setEnabled(true);
                jMenuItemSave.setEnabled(true);
            }
        });
        
        TableUtil.selectRowWhenMouverOverAddressLine(jTable1);

        TableColumn col = jTable1.getColumnModel().getColumn(2);
        col.setCellEditor(new RecipientCellEditor(2));

        col = jTable1.getColumnModel().getColumn(4);
        col.setCellEditor(new RecipientCellEditor(4));
        
        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                jTable1_mousePressedOrReleased(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                jTable1_mousePressedOrReleased(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 1) {
                    displayPhoto(null);
                }
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jScrollPane1.getViewport().setBackground(Color.WHITE);
        
    }
    
    /**
     * Sets the background same a JLabel
     *
     * @param jTextField the text field to "transform" to a label like display
     */
    public static void setTextFieldAsLabel(JTextField jTextField) {
        jTextField.setEditable(false);
        jTextField.setBorder(null);
        jTextField.setBackground(Color.WHITE);
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

                AwakeConnection awakeConnection = (AwakeConnection) connection;
                AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

                String methodRemote = "net.safester.server.hosts.newapi.UserPhotoNewApi.get";
                debug("methodRemote: " + methodRemote);

                String jsonString = awakeFileSession.call(methodRemote,
                				      email,
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

    private void jTable1_mousePressedOrReleased(MouseEvent e) {
        int[] selRows = jTable1.getSelectedRows();

        jTable1.requestFocusInWindow();
        //If selection is empty, select the row near the mouse location
        if (selRows.length == 0) {
            ListSelectionModel selectionModel = jTable1.getSelectionModel();
            int index = jTable1.rowAtPoint(e.getPoint());
            selectionModel.setSelectionInterval(index, index);
        }
        if (e.isPopupTrigger()) {

            jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * Load address book from SQL
     *
     * @return The list of AddressBookLocal found in DB
     * @throws SQLException
     */
    private List<PhotoAddressBookLocal> loadAddresses()
            throws SQLException {
        PhotoAddressBookListTransfer addressBookListTransfer = new PhotoAddressBookListTransfer(connection, userId);
        return addressBookListTransfer.getList();
    }

    /**
     * Add a new empty line in JTable
     */
    public void addNewRow() {
        DefaultTableModel tm = (DefaultTableModel) (jTable1.getModel());
        tm.insertRow(0, new Object[5]);
        updated = true;
    }

    /**
     * Get all elements in JTable and update db with these elements
     */
    public boolean updateListFromTable() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        List<AddressBookNewLocal> newAddressBook = new Vector<AddressBookNewLocal>();

        for (int i = 0; i < jTable1.getRowCount(); i++) {
            AddressBookNewLocal addressBookLocal = new AddressBookNewLocal();
            addressBookLocal.setUserNumber(this.userId);
            addressBookLocal.setAddressBookId(i);

            if (jTable1.getValueAt(i, 1) == null && jTable1.getValueAt(i, 2) == null) {
                //Table is in editing mode, do nothing
                continue;
            }

            String name = (String) jTable1.getValueAt(i, 1);

            /*
            if (name == null || name.isEmpty()) {
                //group_name_invalid_save_not_done=The group name is blank at line {0}. Please correct in order to save.
                String message = messages.getMessage("group_name_invalid_save_not_done");
                message = MessageFormat.format(message, i + 1);
                this.setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(this, message);
                return false;
            }*/

            if (name == null) {
                name ="";
            }
            
            addressBookLocal.setName(name);

            String email = (String) jTable1.getValueAt(i, 2);

            /*
            EmailChecker emailChecker = new EmailChecker(email);
            if (!emailChecker.isSyntaxValid()) {
                String message = messages.getMessage("email_invalid_save_not_done");
                message = MessageFormat.format(message, email);
                this.setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(this, message);
                return false;
            }
            */
            
            addressBookLocal.setEmail(email);
            
            String company = (String) jTable1.getValueAt(i, 3);  
            addressBookLocal.setCompany(company);
 
            String cellPhone = (String) jTable1.getValueAt(i, 4);
            addressBookLocal.setCellPhone(cellPhone);
                        
            newAddressBook.add(addressBookLocal);
        }

        try {
            AddressBookListTransfer addressBookListTransfer = new AddressBookListTransfer(connection, userId);
            addressBookListTransfer.putList(newAddressBook);
        } catch (SQLException e) {
            e.printStackTrace();
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPaneNewCustom.showException(parent, e, messages.getMessage("unable_to_apply_changes"));
            return false;
        }

        UsersAddressBookCache.clear();
        addresses.clear();

        //TODO: addresses.addAll(newAddressBook);

        updated = false;
        this.setCursor(Cursor.getDefaultCursor());
        return true;
    }

    private void buildMessageForRecipient() {
        String recipients = "";
        int[] selectedRows = jTable1.getSelectedRows();
        if (selectedRows.length > 0) {
            for (int index : selectedRows) {
                String name = (String) jTable1.getValueAt(index, 1);
                String email = (String) jTable1.getValueAt(index, 2);
                
                String fullAddress = email;
                if ( name != null && ! name.isEmpty()) {
                    fullAddress = name + " <" + email + ">";
                }
                
                recipients += fullAddress + "; ";
            }
            
            try {
                String userEmailAddr = new UserNumberGetterClient(connection).getLoginFromUserNumber(userId);
                new MessageComposer(this.parent, userEmailAddr, userId, connection, recipients).setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(PhotoAddressBookUpdaterNew.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }

    public void setUpdated(boolean isUpdated) {
        this.updated = isUpdated;
    }

    private void close() {
        if (this.updated) {
            int result = JOptionPane.showConfirmDialog(this, messages.getMessage("save_modification_before_close"), messages.getMessage("address_book"), JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
            if (result == JOptionPane.YES_OPTION) {
                boolean isSaved = save();
                if (!isSaved) {
                    //Do not dispose window if save failed
                    return;
                }
            }
        }

        dispose();
    }
    public void addRecipient() throws HeadlessException {

        if (jTextFieldRecipientEmail1.getText().isEmpty()) {
            String errorMessage = MessagesManager.get("email_address_is_required");
            JOptionPane.showMessageDialog(this, errorMessage, Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            jTextFieldRecipientEmail1.requestFocusInWindow();
            return;
        }
        
        String emailAddress = jTextFieldRecipientEmail1.getText();
        String name = jTextFieldRecipientName.getText();
        String company = jTextFieldCompany.getText();
        String mobile = jTextFieldMobile.getText();

        emailAddress = emailAddress.trim();
        name = name.trim();
        company = company.trim();
        mobile = mobile.trim();

        if (!CryptAppUtil.isValidEmailAddress(emailAddress)) {
            String errorMessage = MessagesManager.get("this_email_address_is_invalid") +  " " + emailAddress;
            JOptionPane.showMessageDialog(this, errorMessage, Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            jTextFieldRecipientEmail1.requestFocusInWindow();
            return;
        }

        if (!mobile.isEmpty()) {
            mobile = MobileUtil.removeSpecialCharacters(mobile);

            MobilePhoneValidator mobilePhoneValidator = new MobilePhoneValidator(mobile);
            boolean isValid = mobilePhoneValidator.isValid();

            if (!isValid) {
                JOptionPane.showMessageDialog(this, MessagesManager.get("mobile_number_contraints") + CR_LF + mobilePhoneValidator.getErrorMessage(), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
                jTextFieldMobile.requestFocusInWindow();
                return;
            }
        }

        jTextFieldRecipientEmail1.setText(null);
        jTextFieldRecipientName.setText(null);
        jTextFieldCompany.setText(null);
        jTextFieldMobile.setText(null);
        //jTextFieldRecipientEmail2.setText(null);

        RecipientEntry pdfRecipient = new RecipientEntry(emailAddress, name, company, mobile, null);
        DefaultTableModel tm = (DefaultTableModel) jTable1.getModel();

        Vector recipientVector = new Vector();
        recipientVector.add(null);
        recipientVector.add(pdfRecipient.getName());
        recipientVector.add(pdfRecipient.getEmailAddress());
        recipientVector.add(pdfRecipient.getCompany());
        recipientVector.add(pdfRecipient.getMobile());

        tm.addRow(recipientVector);

    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelToobar = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButtonSave = new javax.swing.JButton();
        jButtonEditContact = new javax.swing.JButton();
        jButtonDeleteContact = new javax.swing.JButton();
        jSeparatorSendMessagesToContact = new javax.swing.JToolBar.Separator();
        jButtonSendMessageToContact = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jButtonGroups = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelEasst = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelSep1 = new javax.swing.JPanel();
        jPanelSearch = new javax.swing.JPanel();
        jLabelSearch = new javax.swing.JLabel();
        jTextFieldSearch = new javax.swing.JTextField();
        jLabelIn = new javax.swing.JLabel();
        jComboBoxSearch = new javax.swing.JComboBox();
        jPanelSep4 = new javax.swing.JPanel();
        jPanelAddRecipient = new javax.swing.JPanel();
        jPanelTextFields1 = new javax.swing.JPanel();
        jPanelGrid3 = new javax.swing.JPanel();
        jPanelRecipientEmail = new javax.swing.JPanel();
        jTextFieldRecipientEmail1 = new javax.swing.JTextField();
        jPanelRecipientName = new javax.swing.JPanel();
        jTextFieldRecipientName = new javax.swing.JTextField();
        jPanelRecipientCompany = new javax.swing.JPanel();
        jTextFieldCompany = new javax.swing.JTextField();
        jPanelRecipientMobile = new javax.swing.JPanel();
        jTextFieldMobile = new javax.swing.JTextField();
        jPanelAddButton = new javax.swing.JPanel();
        jPanelSep3 = new javax.swing.JPanel();
        jButtonAddRecipients = new javax.swing.JButton();
        jPanelSep2 = new javax.swing.JPanel();
        jPanelSep = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanelTextFieldCount = new javax.swing.JPanel();
        jLabelCount = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemSave = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItemExport = new javax.swing.JMenuItem();
        jMenuItemImport = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemClose = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelToobar.setLayout(new javax.swing.BoxLayout(jPanelToobar, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        jToolBar1.setRollover(true);
        jToolBar1.add(jSeparator3);

        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/floppy_disk.png"))); // NOI18N
        jButtonSave.setFocusable(false);
        jButtonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonSave);

        jButtonEditContact.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/businessman2_edit.png"))); // NOI18N
        jButtonEditContact.setFocusable(false);
        jButtonEditContact.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonEditContact.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonEditContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditContactActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonEditContact);

        jButtonDeleteContact.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/delete.png"))); // NOI18N
        jButtonDeleteContact.setFocusable(false);
        jButtonDeleteContact.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDeleteContact.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDeleteContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteContactActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonDeleteContact);
        jToolBar1.add(jSeparatorSendMessagesToContact);

        jButtonSendMessageToContact.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/mail_write.png"))); // NOI18N
        jButtonSendMessageToContact.setFocusable(false);
        jButtonSendMessageToContact.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSendMessageToContact.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSendMessageToContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendMessageToContactActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonSendMessageToContact);
        jToolBar1.add(jSeparator5);

        jButtonGroups.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/users3.png"))); // NOI18N
        jButtonGroups.setText("jButtonGroups");
        jButtonGroups.setFocusable(false);
        jButtonGroups.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonGroups.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGroupsActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonGroups);

        jPanel1.add(jToolBar1);

        jPanelToobar.add(jPanel1);

        jPanel2.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel2.setOpaque(false);
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));
        jPanelToobar.add(jPanel2);

        getContentPane().add(jPanelToobar, java.awt.BorderLayout.NORTH);

        jPanelWest.setMaximumSize(new java.awt.Dimension(10, 32767));
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelEasst.setMaximumSize(new java.awt.Dimension(10, 32767));
        getContentPane().add(jPanelEasst, java.awt.BorderLayout.EAST);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelSep1.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanelSep1Layout = new javax.swing.GroupLayout(jPanelSep1);
        jPanelSep1.setLayout(jPanelSep1Layout);
        jPanelSep1Layout.setHorizontalGroup(
            jPanelSep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSep1Layout.setVerticalGroup(
            jPanelSep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSep1);

        jPanelSearch.setMaximumSize(new java.awt.Dimension(32767, 30));
        jPanelSearch.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelSearch.setText("Search: ");
        jPanelSearch.add(jLabelSearch);

        jTextFieldSearch.setText("jTextFieldSearch");
        jTextFieldSearch.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jTextFieldSearch.setPreferredSize(new java.awt.Dimension(200, 22));
        jTextFieldSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSearchActionPerformed(evt);
            }
        });
        jPanelSearch.add(jTextFieldSearch);

        jLabelIn.setText("jLabelIn");
        jPanelSearch.add(jLabelIn);

        jComboBoxSearch.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanelSearch.add(jComboBoxSearch);

        jPanelCenter.add(jPanelSearch);

        jPanelSep4.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSep4.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelSep4.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanelSep4Layout = new javax.swing.GroupLayout(jPanelSep4);
        jPanelSep4.setLayout(jPanelSep4Layout);
        jPanelSep4Layout.setHorizontalGroup(
            jPanelSep4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanelSep4Layout.setVerticalGroup(
            jPanelSep4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSep4);

        jPanelAddRecipient.setMinimumSize(new java.awt.Dimension(144, 26));
        jPanelAddRecipient.setLayout(new javax.swing.BoxLayout(jPanelAddRecipient, javax.swing.BoxLayout.Y_AXIS));

        jPanelTextFields1.setLayout(new javax.swing.BoxLayout(jPanelTextFields1, javax.swing.BoxLayout.LINE_AXIS));

        jPanelGrid3.setMaximumSize(new java.awt.Dimension(32767, 22));
        jPanelGrid3.setPreferredSize(new java.awt.Dimension(820, 22));
        jPanelGrid3.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        jPanelRecipientEmail.setLayout(new javax.swing.BoxLayout(jPanelRecipientEmail, javax.swing.BoxLayout.LINE_AXIS));

        jTextFieldRecipientEmail1.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jPanelRecipientEmail.add(jTextFieldRecipientEmail1);

        jPanelGrid3.add(jPanelRecipientEmail);

        jPanelRecipientName.setLayout(new javax.swing.BoxLayout(jPanelRecipientName, javax.swing.BoxLayout.LINE_AXIS));

        jTextFieldRecipientName.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jPanelRecipientName.add(jTextFieldRecipientName);

        jPanelGrid3.add(jPanelRecipientName);

        jPanelRecipientCompany.setLayout(new javax.swing.BoxLayout(jPanelRecipientCompany, javax.swing.BoxLayout.LINE_AXIS));

        jTextFieldCompany.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jPanelRecipientCompany.add(jTextFieldCompany);

        jPanelGrid3.add(jPanelRecipientCompany);

        jPanelRecipientMobile.setLayout(new javax.swing.BoxLayout(jPanelRecipientMobile, javax.swing.BoxLayout.LINE_AXIS));

        jTextFieldMobile.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jPanelRecipientMobile.add(jTextFieldMobile);

        jPanelGrid3.add(jPanelRecipientMobile);

        jPanelTextFields1.add(jPanelGrid3);

        jPanelAddButton.setLayout(new javax.swing.BoxLayout(jPanelAddButton, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSep3.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelAddButton.add(jPanelSep3);

        jButtonAddRecipients.setText("Add");
        jButtonAddRecipients.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddRecipientsActionPerformed(evt);
            }
        });
        jPanelAddButton.add(jButtonAddRecipients);

        jPanelSep2.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelAddButton.add(jPanelSep2);

        jPanelTextFields1.add(jPanelAddButton);

        jPanelAddRecipient.add(jPanelTextFields1);

        jPanelCenter.add(jPanelAddRecipient);

        jPanelSep.setMaximumSize(new java.awt.Dimension(100, 8));
        jPanelSep.setMinimumSize(new java.awt.Dimension(100, 8));
        jPanelSep.setPreferredSize(new java.awt.Dimension(100, 8));
        jPanelSep.setLayout(new javax.swing.BoxLayout(jPanelSep, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenter.add(jPanelSep);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jPanelCenter.add(jScrollPane1);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanel4.setPreferredSize(new java.awt.Dimension(419, 48));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jPanel6.setMaximumSize(new java.awt.Dimension(9, 9));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel6);

        jPanelSouth.setMaximumSize(new java.awt.Dimension(32767, 48));
        jPanelSouth.setMinimumSize(new java.awt.Dimension(48, 48));
        jPanelSouth.setPreferredSize(new java.awt.Dimension(400, 48));
        jPanelSouth.setLayout(new javax.swing.BoxLayout(jPanelSouth, javax.swing.BoxLayout.Y_AXIS));

        jPanel5.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanel5.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel5.setPreferredSize(new java.awt.Dimension(2, 5));
        jPanelSouth.add(jPanel5);

        jPanelTextFieldCount.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanelTextFieldCount.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 5));

        jLabelCount.setPreferredSize(new java.awt.Dimension(200, 16));
        jPanelTextFieldCount.add(jLabelCount);

        jPanelSouth.add(jPanelTextFieldCount);

        jPanel8.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanel8.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanel8.setPreferredSize(new java.awt.Dimension(2, 10));
        jPanelSouth.add(jPanel8);

        jPanel4.add(jPanelSouth);

        jPanel7.setMaximumSize(new java.awt.Dimension(9, 9));
        jPanel7.setPreferredSize(new java.awt.Dimension(9, 10));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel7);

        getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

        jMenuFile.setText("File");

        jMenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/floppy_disk.png"))); // NOI18N
        jMenuItemSave.setText("jMenuItemSave");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSave);
        jMenuFile.add(jSeparator4);

        jMenuItemExport.setText("jMenuItemExport");
        jMenuFile.add(jMenuItemExport);

        jMenuItemImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/book_telephone.png"))); // NOI18N
        jMenuItemImport.setText("jMenuItemImport");
        jMenuFile.add(jMenuItemImport);
        jMenuFile.add(jSeparator2);

        jMenuItemClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/close.png"))); // NOI18N
        jMenuItemClose.setText("jMenuItemClose");
        jMenuItemClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCloseActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemClose);

        jMenuBar1.add(jMenuFile);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDeleteContactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteContactActionPerformed
        deleteSelectedLines();
    }//GEN-LAST:event_jButtonDeleteContactActionPerformed

    private void jButtonEditContactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditContactActionPerformed
        editSelectedLine();
    }//GEN-LAST:event_jButtonEditContactActionPerformed

    private void jMenuItemCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCloseActionPerformed
        close();
        //this.dispose();
    }//GEN-LAST:event_jMenuItemCloseActionPerformed

    private void jButtonSendMessageToContactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendMessageToContactActionPerformed
        buildMessageForRecipient();
    }//GEN-LAST:event_jButtonSendMessageToContactActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
        save();
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        save();
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGroupsActionPerformed

        boolean displayJButtonSendMessageToGroup = false;

        // Display button for Safester Mail
        if (parent instanceof Main) {
            displayJButtonSendMessageToGroup = true;
        }

        if (groupListNew != null) {
            groupListNew.dispose();
        }
        
        groupListNew = new GroupListNew(this, connection, userId, displayJButtonSendMessageToGroup);
        groupListNew.setVisible(true);
    }//GEN-LAST:event_jButtonGroupsActionPerformed

    private void jButtonAddRecipientsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddRecipientsActionPerformed
        addRecipient();
    }//GEN-LAST:event_jButtonAddRecipientsActionPerformed

    private void jTextFieldSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSearchActionPerformed

    private void debug(String string) {
        if (DEBUG) {
            System.out.println ( new Date() + " " + string);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                try {
                    Connection connection = null; //LocalConnection.get();

                    if (connection == null) {
                        throw new SQLException("Connection is null!");
                    }

                    new PhotoAddressBookUpdaterNew(null, connection, TEST_USERID).setVisible(true);

                } catch (Exception ex) {
                    Logger.getLogger(PhotoAdder.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddRecipients;
    private javax.swing.JButton jButtonDeleteContact;
    private javax.swing.JButton jButtonEditContact;
    private javax.swing.JButton jButtonGroups;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonSendMessageToContact;
    private javax.swing.JComboBox jComboBoxSearch;
    private javax.swing.JLabel jLabelCount;
    private javax.swing.JLabel jLabelIn;
    private javax.swing.JLabel jLabelSearch;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItemClose;
    private javax.swing.JMenuItem jMenuItemExport;
    private javax.swing.JMenuItem jMenuItemImport;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelAddButton;
    private javax.swing.JPanel jPanelAddRecipient;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEasst;
    private javax.swing.JPanel jPanelGrid3;
    private javax.swing.JPanel jPanelRecipientCompany;
    private javax.swing.JPanel jPanelRecipientEmail;
    private javax.swing.JPanel jPanelRecipientMobile;
    private javax.swing.JPanel jPanelRecipientName;
    private javax.swing.JPanel jPanelSearch;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSep3;
    private javax.swing.JPanel jPanelSep4;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelTextFieldCount;
    private javax.swing.JPanel jPanelTextFields1;
    private javax.swing.JPanel jPanelToobar;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparatorSendMessagesToContact;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextFieldCompany;
    private javax.swing.JTextField jTextFieldMobile;
    private javax.swing.JTextField jTextFieldRecipientEmail1;
    private javax.swing.JTextField jTextFieldRecipientName;
    private javax.swing.JTextField jTextFieldSearch;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

}
