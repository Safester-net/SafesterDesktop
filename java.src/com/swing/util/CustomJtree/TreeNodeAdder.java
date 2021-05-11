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
package com.swing.util.CustomJtree;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.swing.util.SwingUtil;
import com.swing.util.CustomJtree.dragdrop.TreeDataExtractor;

import net.safester.application.messages.MessagesManager;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.FolderListTransfer;
import net.safester.noobs.clientserver.FolderLocal;


public class TreeNodeAdder extends javax.swing.JDialog {

    JFrame parent;
    Connection connection;
    private int userNumber;
    CustomJTree parentTree;

    MessagesManager messages = new MessagesManager();

    ClipboardManager clipboard;

    /** Creates new form TreeNodeAdder */
    protected TreeNodeAdder(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initCompany();

    }

    public TreeNodeAdder(JFrame parent, Connection connection, int theUserNumber, CustomJTree parentTree, boolean modal) {
        this(parent, modal);
        this.connection = connection;
        this.parentTree = parentTree;
        this.parent = parent;
        this.userNumber = theUserNumber;
    }

    public boolean doIt() {
        String dirName = jTextFieldFolderName.getText();
        
        if (dirName.isEmpty()) {
            return true;
        }

        FolderListTransfer folderListTransfer;
        int folderId = -1;

        try {
            folderListTransfer = new FolderListTransfer(connection, this.userNumber);
            folderId = folderListTransfer.getMaxFolderId() + 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPaneNewCustom.showException(parent, ex, messages.getMessage("cannot_add_folder"));
            return true;
        }

        TreePath currentSelection = parentTree.getTree().getSelectionPath();
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());

        //TreeFolderInfo TreeFolderInfoNew = new TreeFolderInfo(folderId, dirName);        
        //parentTree.addObject(TreeFolderInfoNew);

        FolderLocal folderLocal = new FolderLocal();   
        folderLocal.setFolderId(folderId);
        folderLocal.setName(dirName);
        parentTree.addObject(folderLocal);
        
        try {

            DefaultTreeModel treeModel = (DefaultTreeModel)parentTree.getTree().getModel();
            treeModel.reload(currentNode); // refresh the graphic part

            treeModel.nodeChanged(currentNode);
            parentTree.getTree().updateUI();
            
            DefaultMutableTreeNode rootTreeNode =(DefaultMutableTreeNode) treeModel.getRoot();
            
            //Save changes
            TreeDataExtractor treeDataExtractor = new TreeDataExtractor(rootTreeNode);
            List<FolderLocal>  folderLocals = treeDataExtractor.getFolderLocalList();

            folderListTransfer = new FolderListTransfer(connection, userNumber);
            folderListTransfer.putList(folderLocals);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPaneNewCustom.showException(parentTree, e, messages.getMessage("cannot_add_folder"));
            return true;
        }

            //mainWindow.reloadJTree();
        
        this.dispose();

        return false;
    }

    public void initCompany() {
        clipboard = new ClipboardManager(rootPane);
        jLabelFolderName.setText(messages.getMessage("folder_name"));
        jTextFieldFolderName.setText(messages.getMessage("new_folder"));
        jButtonOk.setText("Ok");
        jButtonCancel.setText("Cancel");

        ButtonResizer br = new ButtonResizer(jPanel5);
        br.setWidthToMax();

        this.setTitle(messages.getMessage("add_folder"));
        this.setLocationRelativeTo(parent);
        keyListenerAdder();

        SwingUtil.resizeJComponentsForAll(rootPane);
    }

    /**
     * Universal key listener
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
                jButtonCancelActionPerformed(null);
            }
            if (keyCode == KeyEvent.VK_ENTER) {
                jButtonOkActionPerformed(null);
            }
        }
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
        jPanelEast = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jLabelFolderName = new javax.swing.JLabel();
        jTextFieldFolderName = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelNorth.setMaximumSize(new java.awt.Dimension(10, 10));
        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelEast.setMaximumSize(new java.awt.Dimension(32767, 10));
        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);

        jPanelWest.setMaximumSize(new java.awt.Dimension(32767, 10));
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelCenter.setPreferredSize(new java.awt.Dimension(55, 30));
        jPanelCenter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelFolderName.setText("Folder Name: ");
        jPanelCenter.add(jLabelFolderName);

        jTextFieldFolderName.setText("jTextField1");
        jTextFieldFolderName.setMinimumSize(new java.awt.Dimension(200, 20));
        jTextFieldFolderName.setPreferredSize(new java.awt.Dimension(200, 20));
        jTextFieldFolderName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldFolderNameActionPerformed(evt);
            }
        });
        jPanelCenter.add(jTextFieldFolderName);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanel5.setMaximumSize(new java.awt.Dimension(32767, 49));
        jPanel5.setPreferredSize(new java.awt.Dimension(453, 49));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 12));

        jButtonOk.setText("Ok");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        jPanel5.add(jButtonOk);

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanel5.add(jButtonCancel);

        jPanel1.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel1.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel1.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanel5.add(jPanel1);

        getContentPane().add(jPanel5, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed

        try {
            doIt();
        } catch (Exception e) {
            JOptionPaneNewCustom.showException(parent, e);
        }
                
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jTextFieldFolderNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldFolderNameActionPerformed
    }//GEN-LAST:event_jTextFieldFolderNameActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                TreeNodeAdder dialog = new TreeNodeAdder(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabelFolderName;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JTextField jTextFieldFolderName;
    // End of variables declaration//GEN-END:variables
}
