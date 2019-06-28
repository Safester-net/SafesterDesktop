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
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import net.safester.application.messages.MessagesManager;
import net.safester.application.tool.ButtonResizer;

import com.swing.util.SwingUtil;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.text.MaskFormatter;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Alexandre Becquereau
 */
public class Double2FaCodeDialog extends javax.swing.JDialog {

    private Frame caller;
    private ClipboardManager clipboardManager;
    private MessagesManager messagesManager = new MessagesManager();

    /**
     * Says if user has canceled dialog
     */
    private boolean cancelAsked = true;
    private final Double2FaCodeDialog thisOne;

    /**
     * Creates new form OtpDialog
     *
     * @param parent
     */
    public Double2FaCodeDialog(java.awt.Frame parent) {
        super(parent, true);
        caller = parent;
        this.thisOne = this;
        initComponents();
        initCompany();
    }

    protected MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }

    private void initCompany() {

        clipboardManager = new ClipboardManager(rootPane);

        this.setTitle(messagesManager.getMessage("two_factor_authentication"));
        jLabelTitle.setText(this.getTitle());

        this.jLabelEnterCode.setText(messagesManager.getMessage("validation_code"));
        this.jTextFieldValidationCode.setText(null);

        jEditorPaneCode.setContentType("text/html");
        jEditorPaneCode.setEditable(false);
        jEditorPaneCode.setText(Help.getHtmlHelpContent("strong_authentication"));

        int minHeight = jTextFieldValidationCode.getMinimumSize().height;
        int height = jTextFieldValidationCode.getPreferredSize().height;

        /*
        jPanelEditorValidationCode.remove(jTextFieldValidationCode);

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(999999);
        formatter.setAllowsInvalid(true);
       
        // If you want the value to be committed on each keystroke instead of focus lost
        formatter.setCommitsOnValidEdit(true);
        
        jTextFieldValidationCode = new JFormattedTextField(createFormatter("### ###"));
        
        jTextFieldValidationCode.setMinimumSize(new Dimension(80, minHeight));
        jTextFieldValidationCode.setPreferredSize(new Dimension(80, height));
        
        jPanelEditorValidationCode.add(jTextFieldValidationCode);
        //jTextFieldValidationCode.setText("000 000");
         */
        jTextFieldValidationCode.setMinimumSize(new Dimension(80, minHeight));
        jTextFieldValidationCode.setPreferredSize(new Dimension(80, height));

        jTextFieldValidationCode.requestFocusInWindow();

        this.okButton.setText(messagesManager.getMessage("ok"));
        this.cancelButton.setText(messagesManager.getMessage("cancel"));

        ButtonResizer buttonResizer = new ButtonResizer(jPanelSouth);
        buttonResizer.setWidthToMax();

        this.setLocationRelativeTo(caller);
        //pack();
        this.setSize(390, 390);

        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentMoved(ComponentEvent e) {
                WindowSettingManager.save(thisOne);
            }

            @Override
            public void componentResized(ComponentEvent e) {
                WindowSettingManager.save(thisOne);
            }

        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });

        WindowSettingManager.load(this);

        jTextFieldValidationCode.requestFocus();
        keyListenerAdder();
    }

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
                close();
            }
            if (keyCode == KeyEvent.VK_ENTER) {
                okButtonActionPerformed(null);
            }
        }
    }

    public boolean isCancelAsked() {
        return cancelAsked;
    }

    public String getValidationCode() {
        String code = jTextFieldValidationCode.getText();
        if (code != null) {
            code = code.trim();
            code = code.replace(" ", "");
        }
        return code;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelNorth = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelWest = new javax.swing.JPanel();
        jPanelEast = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelSep1 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanelHelpEmail = new javax.swing.JPanel();
        jPanelLeftHelp1 = new javax.swing.JPanel();
        jPanelEditorHelp1 = new javax.swing.JPanel();
        jEditorPaneCode = new javax.swing.JEditorPane();
        jPanelRightHelp1 = new javax.swing.JPanel();
        jPanelBlank3 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanelLeftHelp2 = new javax.swing.JPanel();
        jPanelEditorValidationCode = new javax.swing.JPanel();
        jLabelEnterCode = new javax.swing.JLabel();
        jTextFieldValidationCode = new javax.swing.JTextField();
        jPanelRightHelp2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanelSep2 = new javax.swing.JPanel();
        jPanel28 = new javax.swing.JPanel();
        jSeparator4 = new javax.swing.JSeparator();
        jPanelSouth = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();

        jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));

        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/log_in.png"))); // NOI18N
        jLabelTitle.setText("jLabelTitle");
        jPanelNorth.add(jLabelTitle);

        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);
        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelSep1.setMaximumSize(new java.awt.Dimension(32767, 20));
        jPanelSep1.setMinimumSize(new java.awt.Dimension(390, 20));
        jPanelSep1.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanelSep1.setLayout(new javax.swing.BoxLayout(jPanelSep1, javax.swing.BoxLayout.LINE_AXIS));

        jPanel26.setMaximumSize(new java.awt.Dimension(32787, 10));
        jPanel26.setMinimumSize(new java.awt.Dimension(390, 10));
        jPanel26.setLayout(new javax.swing.BoxLayout(jPanel26, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator3.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator3.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator3.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanel26.add(jSeparator3);

        jPanelSep1.add(jPanel26);

        jPanelCenter.add(jPanelSep1);

        jPanelHelpEmail.setLayout(new javax.swing.BoxLayout(jPanelHelpEmail, javax.swing.BoxLayout.X_AXIS));

        jPanelLeftHelp1.setMaximumSize(new java.awt.Dimension(40, 10));
        jPanelLeftHelp1.setMinimumSize(new java.awt.Dimension(40, 10));
        jPanelLeftHelp1.setPreferredSize(new java.awt.Dimension(40, 10));
        jPanelHelpEmail.add(jPanelLeftHelp1);

        jPanelEditorHelp1.setLayout(new javax.swing.BoxLayout(jPanelEditorHelp1, javax.swing.BoxLayout.LINE_AXIS));

        jEditorPaneCode.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jPanelEditorHelp1.add(jEditorPaneCode);

        jPanelHelpEmail.add(jPanelEditorHelp1);

        jPanelRightHelp1.setMaximumSize(new java.awt.Dimension(40, 10));
        jPanelRightHelp1.setMinimumSize(new java.awt.Dimension(40, 10));
        jPanelRightHelp1.setPreferredSize(new java.awt.Dimension(40, 10));
        jPanelHelpEmail.add(jPanelRightHelp1);

        jPanelCenter.add(jPanelHelpEmail);

        jPanelBlank3.setMaximumSize(new java.awt.Dimension(32767, 12));
        jPanelBlank3.setMinimumSize(new java.awt.Dimension(8, 12));
        jPanelBlank3.setPreferredSize(new java.awt.Dimension(547, 12));

        javax.swing.GroupLayout jPanelBlank3Layout = new javax.swing.GroupLayout(jPanelBlank3);
        jPanelBlank3.setLayout(jPanelBlank3Layout);
        jPanelBlank3Layout.setHorizontalGroup(
            jPanelBlank3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 438, Short.MAX_VALUE)
        );
        jPanelBlank3Layout.setVerticalGroup(
            jPanelBlank3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 12, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelBlank3);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.X_AXIS));

        jPanelLeftHelp2.setMaximumSize(new java.awt.Dimension(40, 10));
        jPanelLeftHelp2.setMinimumSize(new java.awt.Dimension(40, 10));
        jPanelLeftHelp2.setPreferredSize(new java.awt.Dimension(40, 10));
        jPanel3.add(jPanelLeftHelp2);

        jLabelEnterCode.setText("jLabelEnterCode");
        jPanelEditorValidationCode.add(jLabelEnterCode);

        jTextFieldValidationCode.setText("jTextFieldValidationCode");
        jPanelEditorValidationCode.add(jTextFieldValidationCode);

        jPanel3.add(jPanelEditorValidationCode);

        jPanelRightHelp2.setMaximumSize(new java.awt.Dimension(40, 10));
        jPanelRightHelp2.setMinimumSize(new java.awt.Dimension(40, 10));
        jPanelRightHelp2.setPreferredSize(new java.awt.Dimension(40, 10));
        jPanel3.add(jPanelRightHelp2);

        jPanelCenter.add(jPanel3);
        jPanelCenter.add(jPanel1);

        jPanelSep2.setMaximumSize(new java.awt.Dimension(32767, 20));
        jPanelSep2.setMinimumSize(new java.awt.Dimension(390, 20));
        jPanelSep2.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanelSep2.setLayout(new javax.swing.BoxLayout(jPanelSep2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel28.setMaximumSize(new java.awt.Dimension(32787, 10));
        jPanel28.setMinimumSize(new java.awt.Dimension(390, 10));
        jPanel28.setLayout(new javax.swing.BoxLayout(jPanel28, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator4.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator4.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator4.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanel28.add(jSeparator4);

        jPanelSep2.add(jPanel28);

        jPanelCenter.add(jPanelSep2);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanelSouth.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanelSouth.add(cancelButton);

        jPanel2.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanel2.setPreferredSize(new java.awt.Dimension(1, 10));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSouth.add(jPanel2);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doIt();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed

        close();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void doIt() {

        String code = jTextFieldValidationCode.getText();

        if (code.length() != 6 || !StringUtils.isNumeric(code)) {
            String errorMessage = messagesManager.getMessage("please_enter_6_digits_code");
            JOptionPane.showMessageDialog(rootPane, errorMessage,
                    messagesManager.getMessage("error"), JOptionPane.ERROR_MESSAGE);
            jTextFieldValidationCode.setText(null);
            jTextFieldValidationCode.requestFocusInWindow();
            return;
        }

        this.cancelAsked = false;
        close();
    }

    private void close() {
        setVisible(false);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            System.out.println("Failed loading L&F: ");
            System.out.println(ex);
        }

        Double2FaCodeDialog dialog = new Double2FaCodeDialog(new javax.swing.JFrame());
        dialog.setVisible(true);

        if (dialog.cancelAsked) {
            System.out.println("cancelAsked!");
        } else {
            System.out.println("validationCode: " + dialog.getValidationCode());
        }

        dialog.dispose();
        System.exit(0);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JEditorPane jEditorPaneCode;
    private javax.swing.JLabel jLabelEnterCode;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelBlank3;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelEditorHelp1;
    private javax.swing.JPanel jPanelEditorValidationCode;
    private javax.swing.JPanel jPanelHelpEmail;
    private javax.swing.JPanel jPanelLeftHelp1;
    private javax.swing.JPanel jPanelLeftHelp2;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelRightHelp1;
    private javax.swing.JPanel jPanelRightHelp2;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTextField jTextFieldValidationCode;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

}
