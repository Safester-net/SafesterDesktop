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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import net.safester.application.Main;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.TableUtil;
import net.safester.application.util.Util;
import net.safester.clientserver.MessageLocalStore;
import net.safester.noobs.clientserver.MessageLocal;
import net.safester.noobs.clientserver.PendingMessageUserLocal;
import net.safester.noobs.clientserver.RecipientLocal;

import org.awakefw.file.api.util.HtmlConverter;

/**
 * Create a JTable with all public keys from the Keyring
 * @author Nicolas de Pomereu
 */
public class MessagesTableCreator {

    private JFrame parent;
    
    /** The National language  messages */
    private MessagesManager messages = new MessagesManager();

    private boolean isOutBox = false;
    
    /** The store of loacal Message */
    private MessageLocalStore messageLocalStore = null;

    /** The folder Id */
    int folderId;

    
    /**
     * Constructor
     */
    public MessagesTableCreator(JFrame parent, 
                                MessageLocalStore messageLocalStore,
                                boolean outBox,
                                int theFolderId) {
        this.parent = parent;
        this.messageLocalStore = messageLocalStore;
        this.isOutBox = outBox;
        this.folderId = theFolderId;
    }

    /**
     * Create a JTable with all public keys from the Keyring
     *
     * @return a JTable with all public keys from the Keyring
     */
    public JTable create() {


        String[] colName;
        if (isOutBox) {
            colName = new String[]{
                        messages.getMessage("id"),
                        messages.getMessage("folder"),
                        messages.getMessage(" "),
                        messages.getMessage(" "),
                        messages.getMessage("to_col"),
                        messages.getMessage("subject"),                        
                        messages.getMessage("sent"),
                        messages.getMessage("size")
            };
        } else if(folderId == Parms.DRAFT_ID){
            colName = new String[]{
                        messages.getMessage("id"),
                        messages.getMessage("folder"),
                        messages.getMessage(" "),
                        messages.getMessage(" "),
                        messages.getMessage("to_col"),
                        messages.getMessage("subject"),
                        messages.getMessage("saved"),                        
                        messages.getMessage("size")
            };
        } else {
            colName = new String[]{
                        messages.getMessage("id"),
                        messages.getMessage("folder"),
                        "read", // String Not displayed
                        "att",  // String Not displayed
                        messages.getMessage("from"),
                        messages.getMessage("subject"),                        
                        messages.getMessage("sent"),
                        messages.getMessage("size")
            };
        }

        int nbCols = colName.length;
        Object[][] data;

        data = initData(nbCols);

        try {

            //JTable jTable1 = new JTable(new TableModelNonEditable(data, colName));
            JTable jTable1 = new JTable(new TableModelMessages(data, colName));
            
            DesktopWrapper.setAutoCreateRowSorterTrue(jTable1);

            TableUtil.selectRowWhenMouverOverBoxLine(jTable1);
            
            TableColumnModel columnModel = jTable1.getColumnModel();
            columnModel.getColumn(0).setCellRenderer(new MessageTableCellRenderer());

            for (int i = 1; i < colName.length; i++) {
                columnModel.getColumn(i).setCellRenderer(new MessageTableCellRenderer());
            }

            JTableHeader jTableHeader = jTable1.getTableHeader();
            jTableHeader.setReorderingAllowed(false);

            ImageIcon headersIcon = Parms.createImageIcon("images/files_2/16x16/document_empty.png");
            ImageIcon headersIcon2= Parms.createImageIcon(Parms.PAPERCLIP_ICON);
            JLabel renderer;

            renderer = (JLabel)jTable1.getColumn(colName[2]).getHeaderRenderer();
            if(renderer == null){
                renderer = new JLabel();
            }
            
            renderer.setIcon(headersIcon);
            renderer.setText("");
            headersIcon.setImageObserver(new HeaderImageObserver(jTableHeader, 2));
            TableCellRenderer tr = new TableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    return (JLabel)value;
                }
            };
            jTable1.getColumn(colName[2]).setHeaderRenderer(tr);
            jTable1.getColumn(colName[2]).setHeaderValue(renderer);

            JLabel renderer2;
            renderer2 = (JLabel)jTable1.getColumn(colName[3]).getHeaderRenderer();
            if(renderer2 == null){
                renderer2 = new JLabel();
            }
            
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            renderer2.setHorizontalAlignment(SwingConstants.CENTER);
                        
            renderer2.setIcon(headersIcon2);
            renderer2.setText("");
            headersIcon2.setImageObserver(new HeaderImageObserver(jTableHeader, 3));
            jTable1.getColumn(colName[3]).setHeaderRenderer(tr);
            jTable1.getColumn(colName[3]).setHeaderValue(renderer2);

            jTable1.setTableHeader(jTableHeader);

            //jTable1.getTableHeader().setPreferredSize(new Dimension(25,35));
            
            jTable1.setColumnSelectionAllowed(false);
            jTable1.setRowSelectionAllowed(true);
            jTable1.setAutoscrolls(true);

            jTable1.setShowHorizontalLines(false);
            jTable1.setShowVerticalLines(false);


            jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            
            jTable1.setRowHeight(jTable1.getRowHeight() + 5);
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

            ListSelectionModel rowSM = jTable1.getSelectionModel();
            rowSM.addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    //Value changed is called twice when calleb by a mouse click!
                    boolean adjust = e.getValueIsAdjusting();
                    if (!adjust) {
                        jTable_selectionChange();
                    } 
                }
            });

            jTable1.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e)
                {
                    jTableMessages_mousePressedOrReleased(e);
                }

                @Override
                public void mouseReleased(MouseEvent e)
                {
                    jTableMessages_mousePressedOrReleased(e);
                }

                @Override
                public void mouseClicked(MouseEvent e)
                {
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
        
        Object[][] data = new Object[messageLocalStore.size()][nbCols];

        if (messageLocalStore.isEmpty()) {
            data = new Object[1][nbCols];
            for (int i = 0; i < nbCols; i++) {
                data[0][i++] = " ";
            }
        }
                
        List<Integer> theList =  new ArrayList<Integer>(messageLocalStore.keySet());

        for (int i = 0; i < theList.size(); i++) 
        {
            int messageId = theList.get(i);
            MessageLocal message = messageLocalStore.get(messageId);

            data[i][0] = message.getMessageId();

            int j = 1;

            data[i][j++] = folderId;
            if (message.getIsRead()) {
                data[i][j++] = "true";
            } else {
                data[i][j++] = "false";
            }
            if (message.getIsWithAttachment()) {
                data[i][j++] = "true";
            } else {
                data[i][j++] = "false";
            }

            if (!isOutBox  && folderId != Parms.DRAFT_ID) {

                String sender = null;

                if (message.getSenderUserName() != null) {
                    sender = HtmlConverter.fromHtml(message.getSenderUserName());
                    sender = Util.removeTrailingSemiColumns(sender);

                    data[i][j++] = sender;
                } else {
                    sender = HtmlConverter.fromHtml(message.getSenderUserEmail());
                    sender = Util.removeTrailingSemiColumns(sender);
                    
                    data[i][j++] = Util.removeTrailingSemiColumns(sender);
                }

            } else {
                String recipients = recipientsAsString(message);
                recipients = HtmlConverter.fromHtml(recipients);
                recipients = Util.removeTrailingSemiColumns(recipients);

                data[i][j++] = recipients;
            }

            //AppDateFormat df = new AppDateFormat();
            //data[i][j++] = df.format(message.getDateMessage());

            String subject = message.getSubject();
            subject = HtmlConverter.fromHtml(subject);
            data[i][j++] = subject;
            
            data[i][j++] = message.getDateMessage();
            
            long size = message.getSizeMessage();
            data[i][j++] = new Long(size);

        }
        return data;
    }

    private void jTable_selectionChange() {
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (parent instanceof Main) {
            Main caller = (Main) parent;
            caller.displaySelectedMessage();
            parent.setCursor(Cursor.getDefaultCursor());
        }
    }

    private void jTableMessages_keyPressed(KeyEvent e) {
        if (parent instanceof Main) {
            Main caller = (Main) parent;
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                caller.openSelectedMessage();
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                caller.deleteSelectedMessage();
            }
            
             if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F9) {
                caller.displaySelectedMessageNumber();
            }
        }
    }

    
   private void jTableMessages_mouseClicked(MouseEvent e) {

        if (parent instanceof Main) {
            Main caller = (Main) parent;

            if (e.getClickCount() >= 2) {
                caller.openSelectedMessage();
            }
     }
   }

   private void jTableMessages_mousePressedOrReleased(MouseEvent e) {

        if (parent instanceof Main) {
            Main caller = (Main) parent;
            if (e.isPopupTrigger()) {
                caller.showJTablePopupMenu(e);
            }

        }


    }

    private String recipientsAsString(MessageLocal message) {
        String recipients = "";
        List<RecipientLocal> recipientList = message.getRecipientLocal();
        for (RecipientLocal recipientLocal : recipientList) {
            if (recipientLocal.getTypeRecipient() == Parms.RECIPIENT_TO) {
                String recipientName = recipientLocal.getNameRecipient();

                if (recipientName == null || recipientName.isEmpty())
                {
                    recipientName = Parms.UNKNOWN_RECIPIENT;
                }

                if (recipientName != null) {
                    recipients += recipientName + "; ";
                }
            }
        }
                            
//        List<PendingMessageUserLocal> pendingMessageUserLocals = message.getPendingMessageUserLocal();
//        
//        for (PendingMessageUserLocal pendingMessageUserLocal : pendingMessageUserLocals) {
//            if (pendingMessageUserLocal.getType_recipient() == Parms.RECIPIENT_TO) {
//                recipients += pendingMessageUserLocal.getEmail() + "; ";
//            }
//        }

        if (recipients.isEmpty())
        {
            recipients = Parms.UNKNOWN_RECIPIENT;
        }

        return recipients;
    }
}
