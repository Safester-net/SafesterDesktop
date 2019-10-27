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

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang3.StringUtils;
import org.awakefw.file.api.util.HtmlConverter;

import net.safester.application.Search;
import net.safester.application.engines.MessageDownloadEngine;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.util.AppDateFormat;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.TableUtil;
import net.safester.noobs.clientserver.MessageLocal;
import net.safester.noobs.clientserver.RecipientLocal;

/**
 * Create a JTable with all public keys from the Keyring
 *
 * @author Nicolas de Pomereu
 */
public class SearchResultMessagesTableCreator {

    private JFrame parent;

    /**
     * The JDBC Connection
     */
    private Connection connection = null;

    /**
     * The National language messages
     */
    private MessagesManager messages = new MessagesManager();

    //private boolean isOutBox = false;
    /**
     * The folder Id
     */
    int folderId;

    /**
     * List of all Message Local
     */
    private List<MessageLocal> messageLocalList = null;

    /**
     * The map of (message Id, MessageLocal)
     */
    private Map<Integer, MessageLocal> messageLocalMap = new HashMap<Integer, MessageLocal>();

    /**
     * Constructor
     */
    public SearchResultMessagesTableCreator(JFrame parent,
            Connection connection,
            List<MessageLocal> messagesList,
            boolean outBox,
            int theFolderId) {
        this.parent = parent;
        this.connection = connection;
        this.messageLocalList = messagesList;
        //   this.isOutBox = outBox;
        this.folderId = theFolderId;

        for (MessageLocal messageLocal : messagesList) {
            int messageId = messageLocal.getMessageId();
            messageLocalMap.put(messageId, messageLocal);
        }
    }

    /**
     * Create a JTable with all public keys from the Keyring
     *
     * @return a JTable with all public keys from the Keyring
     */
    public JTable create() {

        String[] colName;
        if (folderId == Parms.INBOX_ID) {
            colName = new String[]{
                messages.getMessage("id"),
                messages.getMessage("folder"),
                //messages.getMessage(" "),
                //messages.getMessage(" "),
                new String("read"),
                new String("att"),
                messages.getMessage("from"),
                messages.getMessage("subject"),
                messages.getMessage("sent")

            };
        } 
         else if (folderId == Parms.OUTBOX_ID) {
            colName = new String[]{
                messages.getMessage("id"),
                messages.getMessage("folder"),
                messages.getMessage(" "),
                messages.getMessage(" "),
                messages.getMessage("to_col"),
                messages.getMessage("subject"),
                messages.getMessage("saved")

            };
        } 
        else if (folderId == Parms.DRAFT_ID) {
            colName = new String[]{
                messages.getMessage("id"),
                messages.getMessage("folder"),
                messages.getMessage(" "),
                messages.getMessage(" "),
                messages.getMessage("to_col"),
                messages.getMessage("subject"),
                messages.getMessage("saved")

            };
        } 
        else { // Defaults to IN in this version
            colName = new String[]{
                messages.getMessage("id"),
                messages.getMessage("folder"),
                //messages.getMessage(" "),
                //messages.getMessage(" "),
                new String("read"),
                new String("att"),
                messages.getMessage("from"),
                messages.getMessage("subject"),
                messages.getMessage("sent")

            };
        }

        int nbCols = colName.length;
        Object[][] data;

        data = initData(nbCols);

        try {

            JTable jTable1 = new JTable(new TableModelNonEditable(data, colName));
            DesktopWrapper.setAutoCreateRowSorterTrue(jTable1);

            TableColumnModel columnModel = jTable1.getColumnModel();
            boolean isSearchTable = true;
            columnModel.getColumn(0).setCellRenderer(new MessageTableCellRenderer(isSearchTable));

            TableUtil.selectRowWhenMouverOverSearchLine(jTable1);

            for (int i = 1; i < colName.length; i++) {
                columnModel.getColumn(i).setCellRenderer(new MessageTableCellRenderer(isSearchTable));
            }

            JTableHeader jTableHeader = jTable1.getTableHeader();

            ImageIcon headersIcon = Parms.createImageIcon("images/files_2/16x16/document_empty.png");
            ImageIcon headersIcon2 = Parms.createImageIcon(Parms.PAPERCLIP_ICON);
            JLabel renderer;

            renderer = (JLabel) jTable1.getColumn(colName[2]).getHeaderRenderer();
            if (renderer == null) {
                renderer = new JLabel();
            }

            renderer.setIcon(headersIcon);
            renderer.setText("");
            headersIcon.setImageObserver(new HeaderImageObserver(jTableHeader, 2));
            TableCellRenderer tr = new TableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    return (JLabel) value;
                }
            };
            jTable1.getColumn(colName[2]).setHeaderRenderer(tr);
            jTable1.getColumn(colName[2]).setHeaderValue(renderer);

            JLabel renderer2;
            renderer2 = (JLabel) jTable1.getColumn(colName[3]).getHeaderRenderer();
            if (renderer2 == null) {
                renderer2 = new JLabel();
            }
            renderer2.setIcon(headersIcon2);
            renderer2.setText("");
            headersIcon2.setImageObserver(new HeaderImageObserver(jTableHeader, 3));

            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            renderer2.setHorizontalAlignment(SwingConstants.CENTER);

            jTable1.getColumn(colName[3]).setHeaderRenderer(tr);
            jTable1.getColumn(colName[3]).setHeaderValue(renderer2);

            jTable1.setTableHeader(jTableHeader);

            jTable1.setColumnSelectionAllowed(false);
            jTable1.setRowSelectionAllowed(true);
            jTable1.setAutoscrolls(true);

            jTable1.setShowHorizontalLines(false);
            jTable1.setShowVerticalLines(false);

            jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            jTable1.setRowHeight(jTable1.getRowHeight() + 6);
            //jTable1.setIntercellSpacing(new Dimension(3, 1));

            //Hide id column & read/unread column
            jTable1.getColumnModel().getColumn(0).setMinWidth(0);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(1).setMinWidth(0);
            jTable1.getColumnModel().getColumn(1).setMaxWidth(0);

            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setMinWidth(25);
            jTable1.getColumnModel().getColumn(2).setMaxWidth(25);

            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setMinWidth(25);
            jTable1.getColumnModel().getColumn(3).setMaxWidth(25);

            jTable1.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    jTableMessages_mouseClicked(e);
                }

            });

            jTable1.addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    jTableMessages_keyPressed(e);
                }
            });
            TableTransferHandler tableTransferHandler = new TableTransferHandler(false);

            // NO Drag capabilities to Jobs in v1.0
            jTable1.setDragEnabled(true);

            jTable1.setTransferHandler(tableTransferHandler); // refuse to drop inside
            return jTable1;
        } catch (Exception e) {
            //Should never happens
            e.printStackTrace();
            JOptionPaneNewCustom.showException(null, e);
        }
        return null;
    }

    private Object[][] initData(int nbCols) {
        Object[][] data = new Object[messageLocalList.size()][nbCols];

        for (int i = 0; i < this.messageLocalList.size(); i++) {
            MessageLocal message = messageLocalList.get(i);
            data[i][0] = message.getMessageId();

            //SimpleDateFormat df = new SimpleDateFormat(this.messages.getMessage("date_format"));
            AppDateFormat df = new AppDateFormat();

            int j = 1;

            data[i][j++] = folderId;

            if (message.getIsRead() || message.getFolderId() == Parms.OUTBOX_ID) {
                data[i][j++] = "true";
            } else {
                data[i][j++] = "false";
            }

            if (message.getIsWithAttachment()) {
                data[i][j++] = "true";
            } else {
                data[i][j++] = "false";
            }

            if (folderId == Parms.INBOX_ID) {
                if (message.getSenderUserName() != null) {
                    data[i][j++] = HtmlConverter.fromHtml(message.getSenderUserName());
                } else {
                    data[i][j++] = message.getSenderUserEmail();
                }
            } else if (folderId == Parms.DRAFT_ID || folderId == Parms.OUTBOX_ID) {

                String recipients = recipientsAsString(message);
                recipients = HtmlConverter.fromHtml(recipients);
                data[i][j++] = recipients;
            } else {
                // Defaults to IN in this version
                if (message.getSenderUserName() != null) {
                    data[i][j++] = HtmlConverter.fromHtml(message.getSenderUserName());
                } else {
                    data[i][j++] = message.getSenderUserEmail();
                }
            }

            //data[i][j++] = df.format(message.getDateMessage());
            String subject = message.getSubject();
            subject = HtmlConverter.fromHtml(subject);
            data[i][j++] = subject;

            data[i][j++] = message.getDateMessage();
        }
        return data;
    }

    private void jTableMessages_keyPressed(KeyEvent e) {

        if (parent instanceof Search) {
            Search caller = (Search) parent;
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                try {
                    caller.openSelectedMessage(messageLocalMap);

                } catch (Exception ex) {
                    Logger.getLogger(SearchResultMessagesTableCreator.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPaneNewCustom.showException(parent, ex);
                }
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                parent.dispose();
            }
        }

    }

    private void jTableMessages_mouseClicked(MouseEvent e) {

        if (parent instanceof Search) {
            Search caller = (Search) parent;

            if (e.getClickCount() >= 2) {
                try {
                    // System.out.println("call openSelectMessage");
                    caller.openSelectedMessage(messageLocalMap);
                } catch (Exception ex) {
                    Logger.getLogger(SearchResultMessagesTableCreator.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPaneNewCustom.showException(parent, ex);
                }
            }
        }
    }

    private String recipientsAsString(MessageLocal message) {
        String recipients = "";
        List<RecipientLocal> recipientList = message.getRecipientLocal();
        
        
        for (RecipientLocal recipientLocal : recipientList) {
            if (recipientLocal.getTypeRecipient() == Parms.RECIPIENT_TO) {
                String recipientName = recipientLocal.getNameRecipient();
                if (recipientName != null) {
                    recipients += recipientName + "; ";
                }
            }
        } 
                
        // Get all the pending emails from the Engine, in static (very dirty)
        Map<Integer, Set<String>> pendingEmailsMap = MessageDownloadEngine.getPendingEmailsMap();
        
        if (pendingEmailsMap.containsKey(message.getMessageId())) {
            Set<String> emails = pendingEmailsMap.get(message.getMessageId());
            if (emails != null) {
                //int i = 1;
                
                for (String email : emails) {
                    recipients += email + "; ";
                
//                    //Futur usage
//                    RecipientLocal recipientLocal = new RecipientLocal();
//                    recipientLocal.setEmail(email);
//                    recipientLocal.setUserNumber(i);
//                    recipientLocal.setRecipientPosition(i);
//                    recipientList.add(recipientLocal);
//                    message.setRecipientLocal(recipientList);
//                    
//                    messageLocalMap.put(message.getMessageId(), message);
               }
            }
        }

        recipients = recipients.trim();
        if (recipients.endsWith(";")) {
            recipients = StringUtils.substringBeforeLast(recipients,";");
        }
        System.out.println("recipientsAsString  : " + recipients + ": "  );
        System.out.println("List<RecipientLocal>: " + recipientList + ": "  );
        
        return recipients;
    }
}
