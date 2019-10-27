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
package net.safester.application.tool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.StringUtils;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.photo.UsersAddressBookCache;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.noobs.clientserver.AddressBookListTransfer;
import net.safester.noobs.clientserver.AddressBookNewLocal;


public class JTextComponetPopupMenu {

    public final static int SINGLE = 0;
    public final static int LIST = 1;
    private JTextComponent jTextField;
    private JPopupMenu jPopupMenu;
    private MessagesManager messages = new MessagesManager();
    private int type;
    private int userNumber;
    private Connection connection;
    JFrame parent;

    public JTextComponetPopupMenu(Connection theConnection, JFrame caller, JTextComponent theTextField, int theUserNumber,
            int theType) {
        if (theConnection == null) {
            throw new IllegalArgumentException("Connection can't be null");
        }
        jTextField = theTextField;
        this.type = theType;
        this.parent = caller;
        this.userNumber = theUserNumber;
        this.connection = theConnection;
        initPopupMenu();
    }

    private void actionForList() {

        String selectedText = "";
        if (jTextField.getSelectedText() != null) {
            selectedText = jTextField.getSelectedText();
            handleUserSelection(selectedText);
        } else {
            try {
                selectedText = jTextField.getText();
                addSelectionToAddressBook(selectedText);
            } catch (Exception ex) {
                ex.getStackTrace();
                JOptionPaneNewCustom.showException(parent, ex);
            }
        }
    }

    private void initPopupMenu() {
        jPopupMenu = new JPopupMenu();

        JMenuItem itemAdd = new JMenuItem(messages.getMessage("add_to_address_book"));
        itemAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                itemAdd_actionPerformed(e);
            }
        });
        jPopupMenu.add(itemAdd);
    }

    public void showPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger()) {

            jPopupMenu.show(e.getComponent(), e.getX(), e.getY());

        }
    }

    private void itemAdd_actionPerformed(ActionEvent e) {
        if (this.type == JTextComponetPopupMenu.LIST) {
            actionForList();
        } else {
            try {
                actionForSingle();
            } catch (Exception ex) {
                ex.getStackTrace();
                JOptionPaneNewCustom.showException(parent, ex);
            }
        }
    }

    private void actionForSingle() throws Exception {

        String contact = jTextField.getText();
        contact = StringUtils.remove(contact, "<");
        contact = StringUtils.remove(contact, ">");
        contact = contact.trim();

        String contactName = "";
        String contactEmail = "";
        if (contact.indexOf(" ") != -1) {
            contactName = contact.substring(0, contact.lastIndexOf(" ")).trim();
            contactEmail = contact.substring(contact.lastIndexOf(" ")).trim();
        }

        AddressBookListTransfer addressBookListTransfer = new AddressBookListTransfer(
                connection, userNumber);
        
        //List<AddressBookNewLocal> addressBookLocals = addressBookListTransfer.getList();
        //int maxId = addressBookLocals.size();
        
        List<AddressBookNewLocal> addressBookLocals  = new ArrayList<>();
        int maxId = addressBookListTransfer.getMaxId();
        
        maxId++;
        AddressBookNewLocal addressBookLocal = new AddressBookNewLocal();
        addressBookLocal.setAddressBookId(maxId);
        addressBookLocal.setUserNumber(userNumber);
        addressBookLocal.setName(contactName);
        addressBookLocal.setEmail(contactEmail);
        addressBookLocals.add(addressBookLocal);
        
        addressBookListTransfer.putListForAdd(addressBookLocals);
        
         UsersAddressBookCache.clear();
        //new PhotoAddressBookUpdaterNew(parent, connection, userNumber).setVisible(true);
        importOk();
        
    }

    private void addSelectionToAddressBook(String selectedString)
            throws Exception {

        selectedString = selectedString.trim();

        AddressBookListTransfer addressBookListTransfer = new AddressBookListTransfer(
                connection, userNumber);
        
        //List<AddressBookNewLocal> addressBookLocals = addressBookListTransfer.getList();
        //int maxId = addressBookLocals.size();
        
        List<AddressBookNewLocal> addressBookLocals  = new ArrayList<>();
        int maxId = addressBookListTransfer.getMaxId();
        
        maxId++;
        List<String> contacts = getContactListFromString(selectedString);
        for (String contact : contacts) {
            String contactName = " ";
            String contactEmail = "";
            if (contact.indexOf(" ") != -1) {
                contactName = contact.substring(0, contact.lastIndexOf(" ")).trim();
                contactEmail = contact.substring(contact.lastIndexOf(" ")).trim();
            } else {
                contactEmail = contact;
            }
            contactEmail = StringUtils.remove(contactEmail, "<");
            contactEmail = StringUtils.remove(contactEmail, ">");


            AddressBookNewLocal addressBookLocal = new AddressBookNewLocal();
            addressBookLocal.setEmail(contactEmail);
            addressBookLocal.setName(contactName);
            addressBookLocal.setUserNumber(userNumber);
            addressBookLocal.setAddressBookId(maxId++);
            addressBookLocals.add(addressBookLocal);
        }

        addressBookListTransfer.putListForAdd(addressBookLocals);
        UsersAddressBookCache.clear();
        
        //new PhotoAddressBookUpdaterNew(parent, connection, userNumber).setVisible(true);
        importOk();
    }

    private List<String> getContactListFromString(String selectedString) {
        List<String> emails = new Vector<String>();
        if (!selectedString.contains(";")) {
            emails.add(selectedString);
        } else {
            StringTokenizer st = new StringTokenizer(selectedString, ";", false);
            while (st.hasMoreTokens()) {
                String token = st.nextToken().trim();
                emails.add(token);
            }
        }
        return emails;
    }

    private void handleUserSelection(String selectedString) {
        try {
            selectedString = selectedString.trim();
            if (selectedString.startsWith(";")) {
                selectedString = selectedString.substring(1);
            }
            if (selectedString.endsWith(";")) {
                selectedString = selectedString.substring(0,
                        selectedString.length() - 1).trim();
            }
            selectedString = correctSelection(selectedString);
            addSelectionToAddressBook(selectedString);
        } catch (Exception ex) {
            ex.getStackTrace();
            JOptionPaneNewCustom.showException(parent, ex);
        }
    }

    private String correctSelection(String selectedText) {
        String original = jTextField.getText();
        if (original.indexOf(";") != -1) {
            StringTokenizer st = new StringTokenizer(original, ";", false);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (token.indexOf(selectedText) != -1) {
                    int carretPosition = jTextField.getCaretPosition();
                    int endTokenIndex = original.indexOf(token)
                            + token.length();
                    if (carretPosition > endTokenIndex && carretPosition < original.length()) {
                        continue;
                    } else {
                        return token;
                    }
                }
            }
        }
        return original.trim();
    }

    private void importOk() {
        JOptionPane.showMessageDialog(null, MessagesManager.get("emails_adresses_successfully_imported"), Parms.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
    }
}
