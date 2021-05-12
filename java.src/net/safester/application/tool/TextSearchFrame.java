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
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

import org.apache.commons.lang3.StringUtils;

import com.safelogic.utilx.Debug;
import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.ImageParmsUtil;
import net.safester.application.parms.Parms;

/**
 *
 * @author Nicolas de Pomereu
 */
public class TextSearchFrame extends javax.swing.JFrame {

    public static boolean DEBUG = Debug.isSet(TextSearchFrame.class);
        
    private ClipboardManager clipboard = null;
        
    private Window parent = null;
    private JEditorPane jTextPane = null;
    
    private String theTitle = MessagesManager.get("search");
    
    /**
     * Creates new form SearchFrame
     */
    public TextSearchFrame(Window parent, JEditorPane jTextPane) {
        initComponents();
        
        this.parent = parent;
        this.jTextPane = jTextPane;
        
        initializeIt();
        this.setVisible(true);
    }

        /**
     * This is the method to include in *our* constructor(s)
     */
    public void initializeIt() {

        //Dimension dim = new Dimension(372, 149);
        //this.setPreferredSize(dim);
        
        try {
            this.setIconImage(ImageParmsUtil.getAppIcon());
        } catch (RuntimeException e1) {
            e1.printStackTrace();
        }
        
        this.setTitle(theTitle);
        
        // Add a Clipboard Manager
        clipboard = new ClipboardManager(jPanelMain);
        
        buttonGroup1.add(jRadioButtonDown);
        buttonGroup1.add(jRadioButtonUp);
        
        jRadioButtonDown.setSelected(true);
                        
        // Until we finish navigation algorithm
        jPanelDirection.setVisible(false);
    
        SwingUtil.applySwingUpdates(rootPane);

        this.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                saveSettings();
            }

            public void componentResized(ComponentEvent e) {
                saveSettings();
            }
        });

        // Our window listener for all events
        // If window is closed ==> call close()
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeOnExit();
            }
        });

        this.keyListenerAdder();
                
        jLabelSearch.setText(MessagesManager.get("search"));
        jButtonCancel.setText(MessagesManager.get("cancel"));
        jButtonNext.setText(MessagesManager.get("next"));
        jCheckBoxWordOnly.setText(MessagesManager.get("whole_word_only"));
        jCheckBoxRespectCase.setText(MessagesManager.get("case_sensitive"));
        
        this.jTextFieldSearch.requestFocusInWindow(); 
        
        // Load and activate previous windows settings
        WindowSettingManager.load(this);

        // Because there are not in the same panel, resize search button witdh to search reset button
        jButtonNext.setPreferredSize(jButtonCancel.getPreferredSize());
        jButtonNext.setSize(jButtonCancel.getSize());
        jButtonNext.setMaximumSize(jButtonCancel.getMaximumSize());
        jButtonNext.setMinimumSize(jButtonCancel.getMinimumSize());
        
        pack();
        
    }
    
       /**
     * Universal key listener
     */
    private void keyListenerAdder() {
        List<Component> components = SwingUtil.getAllComponants(this);

        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);

            comp.addKeyListener(new KeyAdapter() {
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
                actionNext();
            }
                        
            if (keyCode == KeyEvent.VK_ESCAPE) {
                closeOnExit();
            }

        }
    }
    
    private int lastSearchIndexForward = 0;
    
    private void actionNext() {

        boolean forward = jRadioButtonDown.isSelected();
        boolean ignoreCase = !jCheckBoxRespectCase.isSelected();
        boolean wordOnly = jCheckBoxWordOnly.isSelected();
 
        String searchText = jTextFieldSearch.getText();
        if (searchText.length() == 0) {
            return;
        }

        while (true) {
            int length = jTextPane.getDocument().getLength();
            
            String text = null;
            try {
                text = jTextPane.getDocument().getText(0, length);
            } catch (BadLocationException ex) {
                throw new IllegalArgumentException(ex);
            }

            if (ignoreCase) {
                text = text.toLowerCase();
                searchText = searchText.toLowerCase();
            }

            String subText = null;
            subText = text.substring(lastSearchIndexForward);

            debug("subText: " + subText);
            
            int index1 = 0;
            index1 = subText.indexOf(searchText);        
   
            if (index1 == -1) {
                java.awt.Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, MessagesManager.get("sorry_text_not_found"), Parms.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
                jTextPane.select(0,0);
                lastSearchIndexForward = 0;
                return;
            }
            
            char charBefore = ' ';
            char charAfter = ' ';
            
            if (index1 != 0) {
                charBefore = subText.charAt(index1 - 1);
            }
            
            if (index1 +  searchText.length() < subText.length()) {
                charAfter = subText.charAt(index1 + searchText.length());
            }

            boolean loopForNewSearch = false;
            // Before/after char can not be letter or number 
            if (wordOnly && ( StringUtils.isAlphanumeric("" + charBefore ) || StringUtils.isAlphanumeric("" + charAfter ))) {
                loopForNewSearch = true;
            }
            
            debug("");
            debug("subtext               : " + subText);
            debug("lastSearchIndexForward: " + index1);
            debug("index1                : " + index1);
            
            debug("charBefore: " + charBefore);
            debug("charAfter : " + charAfter);

            int indexInFullText = index1 + lastSearchIndexForward;
            final int inputLength = searchText.length();

            if (loopForNewSearch) {
                lastSearchIndexForward = indexInFullText + inputLength;
                continue;
            }
            
            if (index1 == -1) {
                java.awt.Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, MessagesManager.get("sorry_text_not_found"), Parms.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
                lastSearchIndexForward = 0;
                jTextPane.select(0,0);
                return;
            } else {
                jTextPane.select(indexInFullText, indexInFullText + inputLength);
                debug("selection: " + jTextPane.getSelectionStart() + " / " + jTextPane.getSelectionEnd());
            }

            lastSearchIndexForward = indexInFullText + inputLength;
            return;
        }
        
    }
        
   private void actionCancel() {
        closeOnExit();
    }
        
    public void saveSettings() {
        WindowSettingManager.save(this);
    }

    private void closeOnExit() {
        saveSettings();
        this.dispose();
    }

    /**
     * debug tool
     */
    private static void debug(String s) {
        if (DEBUG) {
            System.out.println(new Date()  + " " + s);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanelMain = new javax.swing.JPanel();
        jPanelLineSep = new javax.swing.JPanel();
        jPanelSearchLine = new javax.swing.JPanel();
        jPanelSearchLineBegin = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jLabelSearch = new javax.swing.JLabel();
        jPanel5x5_2 = new javax.swing.JPanel();
        jTextFieldSearch = new javax.swing.JTextField();
        jPanel5x5_1 = new javax.swing.JPanel();
        jPanelSearchLineButton = new javax.swing.JPanel();
        jPanelButton = new javax.swing.JPanel();
        jButtonNext = new javax.swing.JButton();
        jPanelOptions = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        jPanelChoices = new javax.swing.JPanel();
        jCheckBoxWordOnly = new javax.swing.JCheckBox();
        jCheckBoxRespectCase = new javax.swing.JCheckBox();
        jPanel23 = new javax.swing.JPanel();
        jPanelDirection = new javax.swing.JPanel();
        jRadioButtonUp = new javax.swing.JRadioButton();
        jRadioButtonDown = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButtonCancel = new javax.swing.JButton();
        jPanelLineSep1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelMain.setLayout(new javax.swing.BoxLayout(jPanelMain, javax.swing.BoxLayout.Y_AXIS));

        jPanelLineSep.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelLineSep.setMinimumSize(new java.awt.Dimension(0, 10));

        javax.swing.GroupLayout jPanelLineSepLayout = new javax.swing.GroupLayout(jPanelLineSep);
        jPanelLineSep.setLayout(jPanelLineSepLayout);
        jPanelLineSepLayout.setHorizontalGroup(
            jPanelLineSepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 472, Short.MAX_VALUE)
        );
        jPanelLineSepLayout.setVerticalGroup(
            jPanelLineSepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelMain.add(jPanelLineSep);

        jPanelSearchLine.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPanelSearchLine.setMinimumSize(new java.awt.Dimension(201, 32));
        jPanelSearchLine.setPreferredSize(new java.awt.Dimension(92, 32));
        jPanelSearchLine.setLayout(new javax.swing.BoxLayout(jPanelSearchLine, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSearchLineBegin.setLayout(new javax.swing.BoxLayout(jPanelSearchLineBegin, javax.swing.BoxLayout.LINE_AXIS));

        jPanel20.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel20.setPreferredSize(new java.awt.Dimension(10, 11));

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSearchLineBegin.add(jPanel20);

        jLabelSearch.setText("Rechercher");
        jPanelSearchLineBegin.add(jLabelSearch);

        jPanel5x5_2.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel5x5_2.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel5x5_2Layout = new javax.swing.GroupLayout(jPanel5x5_2);
        jPanel5x5_2.setLayout(jPanel5x5_2Layout);
        jPanel5x5_2Layout.setHorizontalGroup(
            jPanel5x5_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel5x5_2Layout.setVerticalGroup(
            jPanel5x5_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSearchLineBegin.add(jPanel5x5_2);

        jTextFieldSearch.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jTextFieldSearch.setPreferredSize(new java.awt.Dimension(200, 22));
        jPanelSearchLineBegin.add(jTextFieldSearch);

        jPanel5x5_1.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel5x5_1.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel5x5_1.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel5x5_1Layout = new javax.swing.GroupLayout(jPanel5x5_1);
        jPanel5x5_1.setLayout(jPanel5x5_1Layout);
        jPanel5x5_1Layout.setHorizontalGroup(
            jPanel5x5_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel5x5_1Layout.setVerticalGroup(
            jPanel5x5_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSearchLineBegin.add(jPanel5x5_1);

        jPanelSearchLine.add(jPanelSearchLineBegin);

        jPanelSearchLineButton.setLayout(new javax.swing.BoxLayout(jPanelSearchLineButton, javax.swing.BoxLayout.LINE_AXIS));

        jPanelButton.setMaximumSize(new java.awt.Dimension(120, 25));
        jPanelButton.setMinimumSize(new java.awt.Dimension(120, 25));
        jPanelButton.setPreferredSize(new java.awt.Dimension(120, 25));
        jPanelButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 10, 0));

        jButtonNext.setText("Suivant");
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });
        jPanelButton.add(jButtonNext);

        jPanelSearchLineButton.add(jPanelButton);

        jPanelSearchLine.add(jPanelSearchLineButton);

        jPanelMain.add(jPanelSearchLine);

        jPanelOptions.setMaximumSize(new java.awt.Dimension(33194, 50));
        jPanelOptions.setLayout(new javax.swing.BoxLayout(jPanelOptions, javax.swing.BoxLayout.LINE_AXIS));

        jPanel25.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel25.setMinimumSize(new java.awt.Dimension(10, 10));
        jPanel25.setName(""); // NOI18N
        jPanel25.setPreferredSize(new java.awt.Dimension(10, 11));

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelOptions.add(jPanel25);

        jPanelChoices.setLayout(new javax.swing.BoxLayout(jPanelChoices, javax.swing.BoxLayout.Y_AXIS));

        jCheckBoxWordOnly.setText("Mot entier uniquement");
        jPanelChoices.add(jCheckBoxWordOnly);

        jCheckBoxRespectCase.setText("Respecter la casse");
        jPanelChoices.add(jCheckBoxRespectCase);

        jPanelOptions.add(jPanelChoices);

        jPanel23.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelOptions.add(jPanel23);

        jPanelDirection.setBorder(javax.swing.BorderFactory.createTitledBorder("Direction"));
        jPanelDirection.setMaximumSize(new java.awt.Dimension(130, 50));
        jPanelDirection.setMinimumSize(new java.awt.Dimension(130, 35));
        jPanelDirection.setPreferredSize(new java.awt.Dimension(130, 35));
        jPanelDirection.setLayout(new javax.swing.BoxLayout(jPanelDirection, javax.swing.BoxLayout.LINE_AXIS));

        jRadioButtonUp.setText("Haut");
        jPanelDirection.add(jRadioButtonUp);

        jRadioButtonDown.setText("Bas");
        jPanelDirection.add(jRadioButtonDown);

        jPanelOptions.add(jPanelDirection);

        jPanel4.setMaximumSize(new java.awt.Dimension(32767, 10));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 48, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelOptions.add(jPanel4);

        jPanel2.setMaximumSize(new java.awt.Dimension(120, 32));
        jPanel2.setMinimumSize(new java.awt.Dimension(120, 32));
        jPanel2.setPreferredSize(new java.awt.Dimension(120, 32));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 10, 0));

        jButtonCancel.setText("Annuler");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonCancel);

        jPanelOptions.add(jPanel2);

        jPanelMain.add(jPanelOptions);

        jPanelLineSep1.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelLineSep1.setMinimumSize(new java.awt.Dimension(0, 10));

        javax.swing.GroupLayout jPanelLineSep1Layout = new javax.swing.GroupLayout(jPanelLineSep1);
        jPanelLineSep1.setLayout(jPanelLineSep1Layout);
        jPanelLineSep1Layout.setHorizontalGroup(
            jPanelLineSep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 472, Short.MAX_VALUE)
        );
        jPanelLineSep1Layout.setVerticalGroup(
            jPanelLineSep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelMain.add(jPanelLineSep1);

        getContentPane().add(jPanelMain);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        actionCancel();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
        actionNext();
    }//GEN-LAST:event_jButtonNextActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TextSearchFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TextSearchFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TextSearchFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TextSearchFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TextSearchFrame(null, null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JCheckBox jCheckBoxRespectCase;
    private javax.swing.JCheckBox jCheckBoxWordOnly;
    private javax.swing.JLabel jLabelSearch;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5x5_1;
    private javax.swing.JPanel jPanel5x5_2;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelChoices;
    private javax.swing.JPanel jPanelDirection;
    private javax.swing.JPanel jPanelLineSep;
    private javax.swing.JPanel jPanelLineSep1;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelOptions;
    private javax.swing.JPanel jPanelSearchLine;
    private javax.swing.JPanel jPanelSearchLineBegin;
    private javax.swing.JPanel jPanelSearchLineButton;
    private javax.swing.JRadioButton jRadioButtonDown;
    private javax.swing.JRadioButton jRadioButtonUp;
    private javax.swing.JTextField jTextFieldSearch;
    // End of variables declaration//GEN-END:variables


}
