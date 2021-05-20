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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.swing.util.SwingUtil;
import com.swing.util.CustomJtree.FolderTreeCellRendererNew;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.MessageLocalStoreCache;
import net.safester.noobs.clientserver.FolderLocal;
import net.safester.noobs.clientserver.MessageLocal;

public class MessageMover extends javax.swing.JDialog {

    private MessagesManager messages = new MessagesManager();
    private Frame caller;
    private JDialog thisOne;
    private List<MessageLocal> messagesList;
    private JTree tree;
    private int currentSelectedFolder = -1;

    /** Creates new form MessageMover */
    public MessageMover(java.awt.Frame parent, List<MessageLocal> theMessages, boolean modal) {
        super(parent, modal);

        this.thisOne = this;
        this.caller = parent;
        this.messagesList = theMessages;
        initComponents();
        initSafelogic();
    }

    private void initSafelogic() {
        jLabelTitle.setText(messages.getMessage("select_folder"));

        if (caller instanceof Main) {
            Main safeShareMain = (Main) caller;

            // Does not wotk because Connection used
            //JTree jTreeOriginal = safeShareMain.getJtree();
            //tree = (JTree) copy(jTreeOriginal);
            
            tree = new JTree(safeShareMain.getJtree().getModel());
            
            FolderTreeCellRendererNew myRenderer = new FolderTreeCellRendererNew(tree);
            tree.setCellRenderer(myRenderer);
            jScrollPane1.setViewportView(this.tree);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) safeShareMain.getJtree().getSelectionPath().getLastPathComponent();
            if (node.getUserObject() instanceof FolderLocal) {
                FolderLocal folder = (FolderLocal) node.getUserObject();
                currentSelectedFolder = folder.getFolderId();
            }
        }
        jButtonOk.setText(messages.getMessage("ok"));
        jButtonCancel.setText(messages.getMessage("cancel"));

        ButtonResizer br = new ButtonResizer(jPanelSouth);
        br.setWidthToMax();

        keyListenerAdder();
        
        this.addWindowListener(new WindowAdapter() {
                      
            @Override
            public void windowClosing(WindowEvent e) {
                WindowSettingManager.save(thisOne);
                thisOne.dispose();
            }
        });
        this.setLocationRelativeTo(caller);
        WindowSettingManager.load(this);
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
                moveMessages();
            }
            if (keyCode == KeyEvent.VK_ESCAPE) {
                this.dispose();
            }
        }
    }
    
//    public static JTree copy(JTree orig) {
//        JTree obj = null;
//        try {
//            // Write the object out to a byte array
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            ObjectOutputStream out = new ObjectOutputStream(bos);
//            out.writeObject(orig);
//            out.flush();
//            out.close();
//
//            // Make an input stream from the byte array and read
//            // a copy of the object back in.
//            ObjectInputStream in = new ObjectInputStream(
//                    new ByteArrayInputStream(bos.toByteArray()));
//            obj = (JTree) in.readObject();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException cnfe) {
//            cnfe.printStackTrace();
//        }
//        return obj;
//    }

    private void moveMessages() {

        Main safeShareMain = null;
        
        if (caller instanceof Main) {
            safeShareMain = (Main) caller;
        } else {
            return;
        }
        
        try {            
            safeShareMain.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            if (tree.getSelectionPath() != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();

                if (node.getUserObject() instanceof FolderLocal) {
                    FolderLocal folder = (FolderLocal) node.getUserObject();
                    
                    if (folder.getFolderId() == Parms.STARRED_ID) {
                        safeShareMain.setCursor(Cursor.getDefaultCursor());
                        return;
                    }
                    
                    loopMoveMessages(safeShareMain, folder);
                }
            }

            safeShareMain.createTable();
            this.dispose();
            
        } catch (Exception e) {
            safeShareMain.setCursor(Cursor.getDefaultCursor());
            e.printStackTrace();
            JOptionPaneNewCustom.showException(this, e);
        }

    }

    /**
     * The final loop that moves all the messages
     * @param main     the parent instance
     * @param folder            the folder local
     */
    private void loopMoveMessages(Main main, FolderLocal folder)
            throws Exception
    {
        
        List <Integer> messagesId = new Vector<Integer>();
        for (MessageLocal message : messagesList)
        {
            messagesId.add(message.getMessageId());
        }

        String keyId   = main.getKeyId();
        int userNumber = main.getUserNumber();
        int oldFolderId = currentSelectedFolder;
        int newFolderId = folder.getFolderId();

        String messagesIdFromIntegerList = messagesId.toString();

        Connection mainConnection = main.getConnection();
        // Use a dedicated Connection to avoid overlap of result files
        Connection connection = ((AwakeConnection)mainConnection).clone();
        
        // Do the move on the server, because of security concerns
        AwakeConnection awakeConnection = (AwakeConnection)connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        awakeFileSession.call("net.safester.server.MessageMover.moveMessages",
                              userNumber,
                              keyId,
                              oldFolderId,
                              newFolderId,
                              messagesIdFromIntegerList,
                              connection);

        MessageLocalStoreCache.remove(currentSelectedFolder);        
        MessageLocalStoreCache.remove(folder.getFolderId());
        
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
        jPanelCenter = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabelTitle = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelSouth = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanelEast = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 42));
        jPanel1.setPreferredSize(new java.awt.Dimension(284, 42));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/mail_exchange.png"))); // NOI18N
        jPanel1.add(jLabel1);

        jLabelTitle.setText("jLabel2");
        jPanel1.add(jLabelTitle);

        jPanelCenter.add(jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jPanel2.add(jScrollPane1);

        jPanelCenter.add(jPanel2);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jButtonOk.setText("jButtonOk");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonOk);

        jButtonCancel.setText("jButtonCancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonCancel);

        jPanel3.setMaximumSize(new java.awt.Dimension(1, 10));
        jPanel3.setMinimumSize(new java.awt.Dimension(1, 10));
        jPanel3.setPreferredSize(new java.awt.Dimension(1, 10));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSouth.add(jPanel3);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        jPanelEast.setMaximumSize(new java.awt.Dimension(10, 32767));
        jPanelEast.setMinimumSize(new java.awt.Dimension(10, 0));
        jPanelEast.setPreferredSize(new java.awt.Dimension(10, 311));

        javax.swing.GroupLayout jPanelEastLayout = new javax.swing.GroupLayout(jPanelEast);
        jPanelEast.setLayout(jPanelEastLayout);
        jPanelEastLayout.setHorizontalGroup(
            jPanelEastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelEastLayout.setVerticalGroup(
            jPanelEastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 389, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);

        jPanelWest.setMaximumSize(new java.awt.Dimension(10, 32767));
        jPanelWest.setMinimumSize(new java.awt.Dimension(10, 0));
        jPanelWest.setPreferredSize(new java.awt.Dimension(10, 311));

        javax.swing.GroupLayout jPanelWestLayout = new javax.swing.GroupLayout(jPanelWest);
        jPanelWest.setLayout(jPanelWestLayout);
        jPanelWestLayout.setHorizontalGroup(
            jPanelWestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelWestLayout.setVerticalGroup(
            jPanelWestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 389, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        moveMessages();
    }//GEN-LAST:event_jButtonOkActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MessageMover dialog = new MessageMover(new javax.swing.JFrame(), null, true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables


}
