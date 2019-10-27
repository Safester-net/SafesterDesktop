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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.UserPrefManager;

public class SpellCheckSettings extends javax.swing.JDialog {

    private ClipboardManager clipboardManager;
    private MessagesManager messages = new MessagesManager();
    private JFrame parent;
    private JDialog thisOne;

    /** Creates new form SafeShareItSettings */
    public SpellCheckSettings(JFrame jFrame) {
        initComponents();
        parent = jFrame;
        thisOne = this;
        initCompany();
    }

    private void initCompany() {
        this.setModal(true);
        clipboardManager = new ClipboardManager(rootPane);
        
        this.setTitle(messages.getMessage("spell_check_options"));
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());

        this.jLabelTitle.setText(messages.getMessage("spell_check_options"));
        
        this.jCheckBoxIgnoreCapitalizedWords.setText(messages.getMessage("ignore_capitalized_words"));
        this.jCheckBoxIgnoreWordsWithDigits.setText(messages.getMessage("ignore_words_with_digits"));
        this.jCheckBoxSeparateHyphenWords.setText(messages.getMessage("treat_hyphenated_words_as_separated"));

        jButtonOk.setText(messages.getMessage("ok"));
        jButtonCancel.setText(messages.getMessage("cancel"));
        
        boolean ignoreCapitalizedWord = UserPrefManager.getBooleanPreference(UserPrefManager.IGNORE_CAPITALIZED_WORDS);
        jCheckBoxIgnoreCapitalizedWords.setSelected(ignoreCapitalizedWord);

        boolean ignoreWordsWithDigits = UserPrefManager.getBooleanPreference(UserPrefManager.IGNORE_WORDS_WITH_DIGITS);
        jCheckBoxIgnoreWordsWithDigits.setSelected(ignoreWordsWithDigits);

        boolean separateHyphenWords = UserPrefManager.getBooleanPreference(UserPrefManager.SEPARATE_HYPHEN_WORDS);
        jCheckBoxSeparateHyphenWords.setSelected(separateHyphenWords);
        
        ButtonResizer br = new ButtonResizer(jPanelSouth);
        br.setWidthToMax();
        this.setLocationByPlatform(true);

        this.keyListenerAdder();

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
        
        this.setLocationRelativeTo(parent);

        this.setSize(new Dimension(489, 271));

        WindowSettingManager.load(this);
    }

    /**
     * Update user settings on remote sql with data entered by user
     */
    private void updateUserSettings() {

        UserPrefManager.setPreference(UserPrefManager.IGNORE_CAPITALIZED_WORDS, jCheckBoxIgnoreCapitalizedWords.isSelected());
        UserPrefManager.setPreference(UserPrefManager.IGNORE_WORDS_WITH_DIGITS, jCheckBoxIgnoreWordsWithDigits.isSelected());
        UserPrefManager.setPreference(UserPrefManager.SEPARATE_HYPHEN_WORDS,    jCheckBoxSeparateHyphenWords.isSelected());

        this.dispose();
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
                this.dispose();
            }
            if (keyCode == KeyEvent.VK_ENTER) {
                updateUserSettings();
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
        jPanelWest = new javax.swing.JPanel();
        jPanelEast = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jPanelCheckBox = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jCheckBoxIgnoreCapitalizedWords = new javax.swing.JCheckBox();
        jPanel17 = new javax.swing.JPanel();
        jCheckBoxIgnoreWordsWithDigits = new javax.swing.JCheckBox();
        jPanel18 = new javax.swing.JPanel();
        jCheckBoxSeparateHyphenWords = new javax.swing.JCheckBox();
        jPanelSepBlank = new javax.swing.JPanel();
        jPanelSepLine = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelSouth = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);
        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 42));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/spellcheck2.png"))); // NOI18N
        jLabelTitle.setText("jLabelTitle");
        jPanel1.add(jLabelTitle);

        jPanelCenter.add(jPanel1);

        jPanel12.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelCenter.add(jPanel12);

        jPanelCheckBox.setLayout(new javax.swing.BoxLayout(jPanelCheckBox, javax.swing.BoxLayout.Y_AXIS));

        jPanel16.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jCheckBoxIgnoreCapitalizedWords.setText("jCheckBoixIgnoreCapitalizedWords");
        jPanel16.add(jCheckBoxIgnoreCapitalizedWords);

        jPanelCheckBox.add(jPanel16);

        jPanel17.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jCheckBoxIgnoreWordsWithDigits.setText("jCheckBoixIgnoreWordsWithDigits");
        jPanel17.add(jCheckBoxIgnoreWordsWithDigits);

        jPanelCheckBox.add(jPanel17);

        jPanel18.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jCheckBoxSeparateHyphenWords.setText("jCheckBoixIgnoreXmlAndHtml");
        jPanel18.add(jCheckBoxSeparateHyphenWords);

        jPanelCheckBox.add(jPanel18);

        jPanelCenter.add(jPanelCheckBox);

        jPanelSepBlank.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepBlank.setPreferredSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank.setLayout(new javax.swing.BoxLayout(jPanelSepBlank, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenter.add(jPanelSepBlank);

        jPanelSepLine.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepLine.setLayout(new javax.swing.BoxLayout(jPanelSepLine, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSepLine.add(jSeparator2);

        jPanelCenter.add(jPanelSepLine);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

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

        jPanel9.setMaximumSize(new java.awt.Dimension(1, 32767));
        jPanel9.setMinimumSize(new java.awt.Dimension(1, 10));
        jPanel9.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelSouth.add(jPanel9);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        updateUserSettings();
    }//GEN-LAST:event_jButtonOkActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Failed loading L&F: ");
            System.out.println(ex);
        }
            
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new SpellCheckSettings(null).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JCheckBox jCheckBoxIgnoreCapitalizedWords;
    private javax.swing.JCheckBox jCheckBoxIgnoreWordsWithDigits;
    private javax.swing.JCheckBox jCheckBoxSeparateHyphenWords;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelCheckBox;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSepBlank;
    private javax.swing.JPanel jPanelSepLine;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JSeparator jSeparator2;
    // End of variables declaration//GEN-END:variables
}
