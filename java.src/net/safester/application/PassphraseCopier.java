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
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import com.swing.util.SwingUtil;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.swing.JOptionPane;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.WindowSettingManager;

public class PassphraseCopier extends javax.swing.JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 6783083435019423049L;
    MessagesManager messages;
    private char defaultEchocar;
    private JDialog thisOne;
    private boolean doCreate;
    private final Frame theParent;
    private final char[] passphrase;

    /** Creates new form SafeShareItChangePassphrase
     * @param parent */
    public PassphraseCopier(java.awt.Frame parent, char [] passphrase) {
        this.theParent = parent;
        thisOne = this;
        this.passphrase = passphrase;
        initComponents();
        initCompany();
    }

    private void initCompany() {
        
        this.jPassword.setText(new String(passphrase));
        
        messages = new MessagesManager();
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        this.setTitle(messages.getMessage("save_your_passphrase"));
        defaultEchocar = this.jPassword.getEchoChar();

        jButtonCopyPassphrase.setText(messages.getMessage("copy_the_passphrase"));
        jLabelTitle.setText(messages.getMessage("save_your_passphrase"));

        jLabelPassphrase.setText(messages.getMessage("passphrase"));
        
        displayThePassphrase.setText(messages.getMessage("display_the_passphrase"));
        jCheckBoxDisplayPassphrase.setText(null);
        jCheckBoxDisplayPassphrase.setSelected(false);

        jEditorPaneCode.setContentType("text/html");
        jEditorPaneCode.setEditable(false);
        jEditorPaneCode.setText(Help.getHtmlHelpContent("save_your_passphrase"));
        
        jButtonCancel.setText(messages.getMessage("cancel"));
        jButtonCreate.setText(messages.getMessage("create"));
        
        ButtonResizer br = new ButtonResizer(jPanelSouth);
        br.setWidthToMax();
        
        keyListenerAdder();

        this.setLocationByPlatform(true);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                WindowSettingManager.save(thisOne);
            }
        });

       this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
              WindowSettingManager.save(thisOne);
            }

        });

        this.setSize(new Dimension(479, 398));
        
        this.setLocationRelativeTo(theParent);
        WindowSettingManager.load(this);

        SwingUtil.applySwingUpdates(rootPane);
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
            //System.out.println("Key Realeased");
            //System.out.println("TextFieldUserEmail.getText():" + jTextFieldUserEmail.getText() + ":");

            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_ESCAPE) {
                this.dispose();
                return;
            }

            if (keyCode == KeyEvent.VK_ENTER && jButtonCreate.isEnabled()) {
                jButtonCreateActionPerformed(null);
            }
        }
    }

    /**
     * If returns true: create rhe Safester Account
     * @return 
     */
    public boolean doCreate() {
        return doCreate;
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
        jPanelWest = new javax.swing.JPanel();
        jPanelEast = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelTitle = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelSepLine = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelSepBlank2 = new javax.swing.JPanel();
        jPanelHelpEmail = new javax.swing.JPanel();
        jPanelLeftHelp1 = new javax.swing.JPanel();
        jPanelEditorHelp1 = new javax.swing.JPanel();
        jEditorPaneCode = new javax.swing.JEditorPane();
        jPanelRightHelp1 = new javax.swing.JPanel();
        jPanelPassphraseCopy = new javax.swing.JPanel();
        jButtonCopyPassphrase = new javax.swing.JButton();
        jPanelSepBlank = new javax.swing.JPanel();
        jPanelPassphrase = new javax.swing.JPanel();
        jPanelLeft2 = new javax.swing.JPanel();
        jLabelPassphrase = new javax.swing.JLabel();
        jPanelSepFromGrid1 = new javax.swing.JPanel();
        jPaneRight2 = new javax.swing.JPanel();
        jPassword = new javax.swing.JPasswordField();
        jPanelRightHelp2 = new javax.swing.JPanel();
        jPanelHideTypingNew = new javax.swing.JPanel();
        displayThePassphrase = new javax.swing.JLabel();
        jPanelSep3 = new javax.swing.JPanel();
        jCheckBoxDisplayPassphrase = new javax.swing.JCheckBox();
        jPanelSepBlank3 = new javax.swing.JPanel();
        jPanelSepLine1 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanelSouth = new javax.swing.JPanel();
        jButtonCreate = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);

        jPanelNorth.setMaximumSize(new java.awt.Dimension(32767, 10));
        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);
        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelTitle.setMaximumSize(new java.awt.Dimension(32767, 43));
        jPanelTitle.setMinimumSize(new java.awt.Dimension(80, 43));
        jPanelTitle.setPreferredSize(new java.awt.Dimension(80, 43));
        jPanelTitle.setRequestFocusEnabled(false);
        jPanelTitle.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/key.png"))); // NOI18N
        jLabelTitle.setText("jLabelTitle");
        jPanelTitle.add(jLabelTitle);

        jPanelCenter.add(jPanelTitle);

        jPanelSepLine.setMaximumSize(new java.awt.Dimension(32767, 6));
        jPanelSepLine.setMinimumSize(new java.awt.Dimension(0, 6));
        jPanelSepLine.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepLine.setLayout(new javax.swing.BoxLayout(jPanelSepLine, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator2.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator2.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator2.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepLine.add(jSeparator2);

        jPanelCenter.add(jPanelSepLine);

        jPanelSepBlank2.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepBlank2.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank2.setPreferredSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank2.setLayout(new javax.swing.BoxLayout(jPanelSepBlank2, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenter.add(jPanelSepBlank2);

        jPanelHelpEmail.setLayout(new javax.swing.BoxLayout(jPanelHelpEmail, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeftHelp1.setMaximumSize(new java.awt.Dimension(40, 32767));
        jPanelLeftHelp1.setMinimumSize(new java.awt.Dimension(40, 10));
        jPanelLeftHelp1.setPreferredSize(new java.awt.Dimension(40, 10));
        jPanelHelpEmail.add(jPanelLeftHelp1);

        jPanelEditorHelp1.setLayout(new javax.swing.BoxLayout(jPanelEditorHelp1, javax.swing.BoxLayout.LINE_AXIS));

        jEditorPaneCode.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jEditorPaneCode.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jPanelEditorHelp1.add(jEditorPaneCode);

        jPanelHelpEmail.add(jPanelEditorHelp1);

        jPanelRightHelp1.setMaximumSize(new java.awt.Dimension(40, 32767));
        jPanelRightHelp1.setMinimumSize(new java.awt.Dimension(40, 10));
        jPanelRightHelp1.setPreferredSize(new java.awt.Dimension(40, 10));
        jPanelHelpEmail.add(jPanelRightHelp1);

        jPanelCenter.add(jPanelHelpEmail);

        jPanelPassphraseCopy.setMaximumSize(new java.awt.Dimension(32767, 40));
        jPanelPassphraseCopy.setMinimumSize(new java.awt.Dimension(10, 40));
        jPanelPassphraseCopy.setPreferredSize(new java.awt.Dimension(470, 40));
        jPanelPassphraseCopy.setRequestFocusEnabled(false);

        jButtonCopyPassphrase.setText("jButtonCopyPassphrase");
        jButtonCopyPassphrase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCopyPassphraseActionPerformed(evt);
            }
        });
        jPanelPassphraseCopy.add(jButtonCopyPassphrase);

        jPanelCenter.add(jPanelPassphraseCopy);

        jPanelSepBlank.setMaximumSize(new java.awt.Dimension(10, 14));
        jPanelSepBlank.setMinimumSize(new java.awt.Dimension(10, 14));
        jPanelSepBlank.setPreferredSize(new java.awt.Dimension(10, 14));
        jPanelSepBlank.setLayout(new javax.swing.BoxLayout(jPanelSepBlank, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenter.add(jPanelSepBlank);

        jPanelPassphrase.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPanelPassphrase.setMinimumSize(new java.awt.Dimension(10, 32));
        jPanelPassphrase.setPreferredSize(new java.awt.Dimension(470, 32));
        jPanelPassphrase.setRequestFocusEnabled(false);
        jPanelPassphrase.setLayout(new javax.swing.BoxLayout(jPanelPassphrase, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft2.setMaximumSize(new java.awt.Dimension(170, 31));
        jPanelLeft2.setMinimumSize(new java.awt.Dimension(170, 31));
        jPanelLeft2.setPreferredSize(new java.awt.Dimension(170, 31));
        jPanelLeft2.setLayout(new java.awt.GridLayout(1, 0));

        jLabelPassphrase.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelPassphrase.setText("jLabelPassphrase");
        jPanelLeft2.add(jLabelPassphrase);

        jPanelPassphrase.add(jPanelLeft2);

        jPanelSepFromGrid1.setMaximumSize(new java.awt.Dimension(5, 31));
        jPanelSepFromGrid1.setMinimumSize(new java.awt.Dimension(5, 31));
        jPanelSepFromGrid1.setPreferredSize(new java.awt.Dimension(5, 31));
        jPanelPassphrase.add(jPanelSepFromGrid1);

        jPaneRight2.setMaximumSize(new java.awt.Dimension(2147483647, 31));
        jPaneRight2.setMinimumSize(new java.awt.Dimension(6, 31));
        jPaneRight2.setPreferredSize(new java.awt.Dimension(295, 31));
        jPaneRight2.setLayout(new javax.swing.BoxLayout(jPaneRight2, javax.swing.BoxLayout.LINE_AXIS));

        jPassword.setEditable(false);
        jPassword.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jPassword.setMinimumSize(new java.awt.Dimension(8, 22));
        jPassword.setPreferredSize(new java.awt.Dimension(300, 22));
        jPaneRight2.add(jPassword);

        jPanelPassphrase.add(jPaneRight2);

        jPanelRightHelp2.setMaximumSize(new java.awt.Dimension(40, 10));
        jPanelRightHelp2.setMinimumSize(new java.awt.Dimension(40, 10));
        jPanelRightHelp2.setPreferredSize(new java.awt.Dimension(40, 10));
        jPanelPassphrase.add(jPanelRightHelp2);

        jPanelCenter.add(jPanelPassphrase);

        jPanelHideTypingNew.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelHideTypingNew.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelHideTypingNew.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelHideTypingNew.setRequestFocusEnabled(false);
        jPanelHideTypingNew.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        displayThePassphrase.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        displayThePassphrase.setText("displayThePassphrase");
        displayThePassphrase.setMaximumSize(new java.awt.Dimension(170, 16));
        displayThePassphrase.setMinimumSize(new java.awt.Dimension(170, 16));
        displayThePassphrase.setPreferredSize(new java.awt.Dimension(170, 16));
        jPanelHideTypingNew.add(displayThePassphrase);

        jPanelSep3.setMaximumSize(new java.awt.Dimension(5, 31));
        jPanelSep3.setMinimumSize(new java.awt.Dimension(5, 31));
        jPanelSep3.setPreferredSize(new java.awt.Dimension(5, 31));
        jPanelHideTypingNew.add(jPanelSep3);

        jCheckBoxDisplayPassphrase.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jCheckBoxDisplayPassphrase.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxDisplayPassphraseItemStateChanged(evt);
            }
        });
        jPanelHideTypingNew.add(jCheckBoxDisplayPassphrase);

        jPanelCenter.add(jPanelHideTypingNew);

        jPanelSepBlank3.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepBlank3.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank3.setPreferredSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank3.setLayout(new javax.swing.BoxLayout(jPanelSepBlank3, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenter.add(jPanelSepBlank3);

        jPanelSepLine1.setMaximumSize(new java.awt.Dimension(32767, 6));
        jPanelSepLine1.setMinimumSize(new java.awt.Dimension(0, 6));
        jPanelSepLine1.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepLine1.setLayout(new javax.swing.BoxLayout(jPanelSepLine1, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator3.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator3.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator3.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepLine1.add(jSeparator3);

        jPanelCenter.add(jPanelSepLine1);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonCreate.setText("jButtonCreate");
        jButtonCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonCreate);

        jButtonCancel.setText("jButtonCancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelSouth.add(jButtonCancel);

        jPanel2.setMaximumSize(new java.awt.Dimension(1, 10));
        jPanel2.setMinimumSize(new java.awt.Dimension(1, 0));
        jPanel2.setPreferredSize(new java.awt.Dimension(1, 10));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 1, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 10, Short.MAX_VALUE)
        );

        jPanelSouth.add(jPanel2);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateActionPerformed
        dispose();
        this.doCreate = true;
    }//GEN-LAST:event_jButtonCreateActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        dispose();
        this.doCreate = false;
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jCheckBoxDisplayPassphraseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxDisplayPassphraseItemStateChanged
        if ((evt.getStateChange() == ItemEvent.SELECTED)) {
            // Display  chars
            this.jPassword.setEchoChar((char) 0);
        } else {
            this.jPassword.setEchoChar(this.defaultEchocar);
        }
    }//GEN-LAST:event_jCheckBoxDisplayPassphraseItemStateChanged

    private void jButtonCopyPassphraseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCopyPassphraseActionPerformed

        StringSelection stringSelection = new StringSelection(new String(passphrase));
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        String message = messages.getMessage("the_passphrase_has_been_copied");
        JOptionPane.showMessageDialog(this, message, Parms.PRODUCT_NAME, JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButtonCopyPassphraseActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

            try {
                SafesterLookAndFeelManager.setLookAndFeel();
            } catch (Exception ex) {
                System.out.println("Failed loading L&F: ");
                System.out.println(ex);
            }

                PassphraseCopier dialog = new PassphraseCopier(new javax.swing.JFrame(), "the passphrase".toCharArray());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
                System.out.println("dialog.doCreate(): " + dialog.doCreate());
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel displayThePassphrase;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonCopyPassphrase;
    private javax.swing.JButton jButtonCreate;
    private javax.swing.JCheckBox jCheckBoxDisplayPassphrase;
    private javax.swing.JEditorPane jEditorPaneCode;
    private javax.swing.JLabel jLabelPassphrase;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPaneRight2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelEditorHelp1;
    private javax.swing.JPanel jPanelHelpEmail;
    private javax.swing.JPanel jPanelHideTypingNew;
    private javax.swing.JPanel jPanelLeft2;
    private javax.swing.JPanel jPanelLeftHelp1;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelPassphrase;
    private javax.swing.JPanel jPanelPassphraseCopy;
    private javax.swing.JPanel jPanelRightHelp1;
    private javax.swing.JPanel jPanelRightHelp2;
    private javax.swing.JPanel jPanelSep3;
    private javax.swing.JPanel jPanelSepBlank;
    private javax.swing.JPanel jPanelSepBlank2;
    private javax.swing.JPanel jPanelSepBlank3;
    private javax.swing.JPanel jPanelSepFromGrid1;
    private javax.swing.JPanel jPanelSepLine;
    private javax.swing.JPanel jPanelSepLine1;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelTitle;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JPasswordField jPassword;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    // End of variables declaration//GEN-END:variables
}
