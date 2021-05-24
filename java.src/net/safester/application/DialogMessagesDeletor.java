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
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

import com.swing.util.SwingUtil;
import java.awt.Dimension;
import javax.swing.DefaultComboBoxModel;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;

/**
 * 
 * @author ndepo
 */
public class DialogMessagesDeletor extends javax.swing.JDialog {

    MessagesManager messages = new MessagesManager();

    /** if true, uuse has canceled the form */
    private boolean delete;

    ClipboardManager clipboardManager;
    private boolean deleteForAll;
    
    /** Creates new form JDialogProxyAuth */
    public DialogMessagesDeletor(Window parent) {
        super(parent);
        initComponents();
        initCompany();
        this.setLocationRelativeTo(parent);
    }

    private void initCompany(){
        
        Dimension dim = new Dimension(550, this.getHeight() + 10);
	this.setSize(dim);
	this.setPreferredSize(dim);
        
        clipboardManager = new ClipboardManager(rootPane);
        
        this.setTitle(messages.getMessage("warning"));
        this.jLabelTitle.setText(messages.getMessage("confirm_delete"));
        
        this.jLabelMessageForWhich.setText(messages.getMessage("messages_for_which_i_am_the_sender"));
        
        String [] deletePolicy = {messages.getMessage("delete_for_everyone"), messages.getMessage("delete_for_me_only")};
        jComboBoxDeletePolicy.setModel(new DefaultComboBoxModel(deletePolicy));
	  
	this.jComboBoxDeletePolicy.setSelectedItem(messages.getMessage("delete_for_everyone"));
        
        this.jButtonYes.setText(messages.getMessage("yes"));
        this.jButtonNo.setText(messages.getMessage("no"));
        
        try
        {
            this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        } 

        keyListenerAdder();
        ButtonResizer buttonResizer = new ButtonResizer();
        buttonResizer.setWidthToMax(SwingUtil.getAllComponants(jPanelSouth));
        SwingUtil.applySwingUpdates(rootPane);
        
    }

    /**
     * Universal key listener
     */
    private void keyListenerAdder()
    {
        List<Component> components = SwingUtil.getAllComponants(this);

        for (int i = 0; i < components.size(); i++)
        {
            Component comp = components.get(i);

            comp.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e)
                {
                    this_keyPressed(e);
                }
            });
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // KEYS PART
    ///////////////////////////////////////////////////////////////////////////

    private void this_keyPressed(KeyEvent e)
    {
        int id = e.getID();
        if (id == KeyEvent.KEY_PRESSED)
        {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_ENTER)
            {
                doIt();
            }

            if (keyCode == KeyEvent.VK_ESCAPE)
            {
                delete=false;
                this.dispose();
            }

        }
    }

    public boolean doDeleteForAll() {
        System.out.println("deleteForAll: " + deleteForAll);
        return deleteForAll;
    }
    
    private void doIt(){
        delete = true;
        
        if (jComboBoxDeletePolicy.getSelectedItem().equals(messages.getMessage("delete_for_everyone"))) {
            deleteForAll = true;
        }
        else {
            deleteForAll = false;
        }
        
        dispose();
    }

    public boolean doTheDelete() {
        return delete;
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
        jPanelTitle = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelSepBlank = new javax.swing.JPanel();
        jPanelSepLine = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel8 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabelMessageForWhich = new javax.swing.JLabel();
        jComboBoxDeletePolicy = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jPanelSepBlank1 = new javax.swing.JPanel();
        jPanelSepLine1 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanelSouth = new javax.swing.JPanel();
        jPanelLeft = new javax.swing.JPanel();
        jPanelRight = new javax.swing.JPanel();
        jButtonYes = new javax.swing.JButton();
        jButtonNo = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);
        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelTitle.setMaximumSize(new java.awt.Dimension(32767, 45));
        jPanelTitle.setMinimumSize(new java.awt.Dimension(153, 45));
        jPanelTitle.setPreferredSize(new java.awt.Dimension(384, 45));
        jPanelTitle.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelTitle.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/sign_warning.png"))); // NOI18N
        jLabelTitle.setText("Are you sure you want to delete selected message(s)?");
        jPanelTitle.add(jLabelTitle);

        jPanelCenter.add(jPanelTitle);

        jPanelSepBlank.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepBlank.setPreferredSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank.setLayout(new javax.swing.BoxLayout(jPanelSepBlank, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenter.add(jPanelSepBlank);

        jPanelSepLine.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepLine.setLayout(new javax.swing.BoxLayout(jPanelSepLine, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSepLine.add(jSeparator2);

        jPanelCenter.add(jPanelSepLine);

        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelMessageForWhich.setText("Messages for which I am the sender:");
        jPanel1.add(jLabelMessageForWhich);

        jComboBoxDeletePolicy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2" }));
        jPanel1.add(jComboBoxDeletePolicy);

        jPanel8.add(jPanel1);

        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jPanel8.add(jPanel2);

        jPanelCenter.add(jPanel8);

        jPanelSepBlank1.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepBlank1.setPreferredSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank1.setLayout(new javax.swing.BoxLayout(jPanelSepBlank1, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenter.add(jPanelSepBlank1);

        jPanelSepLine1.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepLine1.setLayout(new javax.swing.BoxLayout(jPanelSepLine1, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSepLine1.add(jSeparator3);

        jPanelCenter.add(jPanelSepLine1);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setPreferredSize(new java.awt.Dimension(101, 43));
        jPanelSouth.setLayout(new java.awt.GridLayout(1, 2));

        jPanelLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 10));
        jPanelSouth.add(jPanelLeft);

        jPanelRight.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonYes.setText("Yes");
        jButtonYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonYesActionPerformed(evt);
            }
        });
        jPanelRight.add(jButtonYes);

        jButtonNo.setText("No");
        jButtonNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNoActionPerformed(evt);
            }
        });
        jPanelRight.add(jButtonNo);

        jPanel12.setMaximumSize(new java.awt.Dimension(0, 10));
        jPanel12.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanel12.setPreferredSize(new java.awt.Dimension(0, 10));
        jPanel12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 5));
        jPanelRight.add(jPanel12);

        jPanelSouth.add(jPanelRight);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonYesActionPerformed
        doIt();
    }//GEN-LAST:event_jButtonYesActionPerformed

    private void jButtonNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNoActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonNoActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {

        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        try
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
        catch (Exception ex)
        {
            System.out.println("Failed loading L&F: ");
            System.out.println(ex);
        }
            
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogMessagesDeletor dialog = new DialogMessagesDeletor(new javax.swing.JFrame());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonNo;
    private javax.swing.JButton jButtonYes;
    private javax.swing.JComboBox<String> jComboBoxDeletePolicy;
    private javax.swing.JLabel jLabelMessageForWhich;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelSepBlank;
    private javax.swing.JPanel jPanelSepBlank1;
    private javax.swing.JPanel jPanelSepLine;
    private javax.swing.JPanel jPanelSepLine1;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelTitle;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    // End of variables declaration//GEN-END:variables

}
