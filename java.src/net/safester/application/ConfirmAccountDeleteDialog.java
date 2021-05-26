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

import com.swing.util.LookAndFeelHelper;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.util.HtmlTextUtil;
import net.safester.application.util.JOptionPaneNewCustom;

/**
 * This dialog is displayed when user wants to delete his account
 * @author Alexandre Becquereau
 */
public class ConfirmAccountDeleteDialog extends javax.swing.JDialog {

    private MessagesManager messages = new MessagesManager();

    /** The user number of the account */
    private int userNumber;

    /** the email of the account */
    private String keyId = null;

        /** The JDBC Connection */
    private Connection connection;
    
    private Frame parent;

    /**
     * Constructor
     * @param usernumber    The user number of the account
     * @param keyId         the keyId of the account
     * @param theConnection    the JDBC Connection
     */
    public ConfirmAccountDeleteDialog(java.awt.Frame parent, int usernumber, String keyId, Connection theConnection) {

        this.parent = parent;
        this.userNumber = usernumber;
        this.keyId = keyId;

        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();
        
        initComponents();
        initCompany();
        this.setLocationRelativeTo(parent);
    }

    private void initCompany() {
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        this.setTitle(messages.getMessage("confirm_delete_account"));
        this.jLabelTitle.setText(messages.getMessage("confirm_delete_account"));
        
        this.jLabelConfirmDelete.setText(messages.getMessage("account_delete_yes_no"));
        this.jTextFieldConfirmDelete.setText(messages.getMessage("no"));
        
        if (LookAndFeelHelper.isDarkMode()) {
            jPanelEditorPane.setBackground(Color.BLACK);
            jEditorPane.setBackground(Color.BLACK);
            jPanelNorth1.setBackground(Color.BLACK);
            jPanelSouth1.setBackground(Color.BLACK);
            jPanelWest1.setBackground(Color.BLACK);
            jPanelEast1.setBackground(Color.BLACK);
        } else {
            jEditorPane.setBackground(Color.WHITE);
        }
        
        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);

        
        jLabelPassphrase.setText(messages.getMessage("passphrase"));
        jEditorPane.setText(HtmlTextUtil.getHtmlHelpContent("confirm_delete_account"));

        this.jButtonOk.setText(messages.getMessage("ok"));
        this.jButtonCancel.setText(messages.getMessage("cancel"));

        keyListenerAdder();
        
        ButtonResizer br = new ButtonResizer(jPanelButton);
        br.setWidthToMax();

        SwingUtil.applySwingUpdates(rootPane);
        
        this.setSize(460, 330);
        this.setLocationRelativeTo(this.parent);

        jPassword.requestFocus(); 
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
        }
    }

    /**
     * Check of the old passphrase is valid
     * @return  true if the old passphrase is valid
     */
    private boolean checkPresentPassphrase()
        throws Exception
    {        
        if (parent instanceof Main) {
            
            Main main = (Main)parent;
            char [] passphrase = main.getUserPassphrase();
            
            return Arrays.equals(passphrase, jPassword.getPassword());            
        }
        else {
            throw new IllegalArgumentException("parent is not instanceof Main");
        }
    }
    


    private void doIt() {

        String errorMsg = messages.getMessage("error");

        String theYes = messages.getMessage("yes");
        String theNo = messages.getMessage("no");
                
        String confirmation = this.jTextFieldConfirmDelete.getText();
        if(confirmation == null ||(!confirmation.equalsIgnoreCase(theYes) && !confirmation.equalsIgnoreCase(theNo))){
            JOptionPane.showMessageDialog(rootPane, messages.getMessage("enter_yes_or_no"),
                                          errorMsg, JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (! confirmation.equalsIgnoreCase(theYes))
        {
            JOptionPane.showMessageDialog(rootPane, messages.getMessage("account_will_not_be_deleted"),
                                          errorMsg, JOptionPane.ERROR_MESSAGE);
            this.dispose();
            return;
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            if (!checkPresentPassphrase()) {
                this.setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(rootPane, messages.getMessage("invalid_passphrase"),
                                              errorMsg, JOptionPane.ERROR_MESSAGE);

                return;
            }
        } catch (Exception e) {
            // Show the Exception
            JOptionPaneNewCustom.showException(this, e);
            return;
        }

        // Do the delete on the server, because of security concerns
        
        try
        {
            AwakeConnection awakeConnection = (AwakeConnection)connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

            awakeFileSession.call("net.safester.server.AccountDeletor.delete", 
                                  userNumber,
                                  keyId,
                                  connection);

//            if (! awakeFileSession.isOperationOk())
//            {
//                throw new SQLException(awakeFileSession.getErrorCode(), awakeFileSession.getException());
//            }
        }
        catch (Exception e)
        {
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPaneNewCustom.showException(rootPane, e);
            return;
        }
        
        this.setCursor(Cursor.getDefaultCursor());
        
        JOptionPane.showMessageDialog(rootPane,
                messages.getMessage("account_deleted"));
        System.exit(1);
       
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelNorth = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelEast = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelUp = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelSep2 = new javax.swing.JPanel();
        jPanelEditorPane = new javax.swing.JPanel();
        jEditorPane = new javax.swing.JEditorPane();
        jPanelNorth1 = new javax.swing.JPanel();
        jPanelSouth1 = new javax.swing.JPanel();
        jPanelEast1 = new javax.swing.JPanel();
        jPanelWest1 = new javax.swing.JPanel();
        jPanelSep1 = new javax.swing.JPanel();
        jPanelPassphrase = new javax.swing.JPanel();
        jPanelSepFromGrid1 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabelPassphrase = new javax.swing.JLabel();
        jPanelSepFromGrid = new javax.swing.JPanel();
        jPanelPassword = new javax.swing.JPanel();
        jPassword = new javax.swing.JPasswordField();
        jPanelConfirmDelete = new javax.swing.JPanel();
        jLabelConfirmDelete = new javax.swing.JLabel();
        jTextFieldConfirmDelete = new javax.swing.JTextField();
        jPanelSep = new javax.swing.JPanel();
        jPanelSepLine = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelSouth = new javax.swing.JPanel();
        jPanelButton = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);

        jPanelNorth.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);
        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);

        jPanelCenter.setRequestFocusEnabled(false);
        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelUp.setMaximumSize(new java.awt.Dimension(32767, 42));
        jPanelUp.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/key_delete.png"))); // NOI18N
        jLabelTitle.setText("jLabelTitle");
        jPanelUp.add(jLabelTitle);

        jPanelCenter.add(jPanelUp);

        jPanelSep2.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelCenter.add(jPanelSep2);

        jPanelEditorPane.setBackground(new java.awt.Color(255, 255, 255));
        jPanelEditorPane.setLayout(new java.awt.BorderLayout());

        jEditorPane.setBorder(null);
        jPanelEditorPane.add(jEditorPane, java.awt.BorderLayout.CENTER);

        jPanelNorth1.setBackground(new java.awt.Color(255, 255, 255));
        jPanelNorth1.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelNorth1.setMinimumSize(new java.awt.Dimension(10, 5));
        jPanelNorth1.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelEditorPane.add(jPanelNorth1, java.awt.BorderLayout.PAGE_START);

        jPanelSouth1.setBackground(new java.awt.Color(255, 255, 255));
        jPanelSouth1.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSouth1.setMinimumSize(new java.awt.Dimension(10, 5));
        jPanelSouth1.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelEditorPane.add(jPanelSouth1, java.awt.BorderLayout.SOUTH);

        jPanelEast1.setBackground(new java.awt.Color(255, 255, 255));
        jPanelEast1.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelEast1.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelEast1.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelEditorPane.add(jPanelEast1, java.awt.BorderLayout.EAST);

        jPanelWest1.setBackground(new java.awt.Color(255, 255, 255));
        jPanelWest1.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelWest1.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelWest1.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelEditorPane.add(jPanelWest1, java.awt.BorderLayout.WEST);

        jPanelCenter.add(jPanelEditorPane);

        jPanelSep1.setMaximumSize(new java.awt.Dimension(32767, 14));
        jPanelSep1.setMinimumSize(new java.awt.Dimension(10, 14));
        jPanelSep1.setPreferredSize(new java.awt.Dimension(10, 14));
        jPanelCenter.add(jPanelSep1);

        jPanelPassphrase.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPanelPassphrase.setMinimumSize(new java.awt.Dimension(83, 32));
        jPanelPassphrase.setPreferredSize(new java.awt.Dimension(388, 32));
        jPanelPassphrase.setLayout(new javax.swing.BoxLayout(jPanelPassphrase, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSepFromGrid1.setMaximumSize(new java.awt.Dimension(5, 10));
        jPanelSepFromGrid1.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelSepFromGrid1.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelPassphrase.add(jPanelSepFromGrid1);

        jPanel1.setRequestFocusEnabled(false);
        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        jLabelPassphrase.setText("jLabelPassphrase");
        jPanel1.add(jLabelPassphrase);

        jPanelPassphrase.add(jPanel1);

        jPanelSepFromGrid.setMaximumSize(new java.awt.Dimension(5, 10));
        jPanelSepFromGrid.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelSepFromGrid.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelPassphrase.add(jPanelSepFromGrid);

        jPanelPassword.setMaximumSize(new java.awt.Dimension(2147483647, 31));
        jPanelPassword.setMinimumSize(new java.awt.Dimension(295, 31));
        jPanelPassword.setPreferredSize(new java.awt.Dimension(295, 31));
        jPanelPassword.setLayout(new javax.swing.BoxLayout(jPanelPassword, javax.swing.BoxLayout.LINE_AXIS));

        jPassword.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jPassword.setMinimumSize(new java.awt.Dimension(295, 22));
        jPassword.setPreferredSize(new java.awt.Dimension(295, 22));
        jPanelPassword.add(jPassword);

        jPanelPassphrase.add(jPanelPassword);

        jPanelCenter.add(jPanelPassphrase);

        jPanelConfirmDelete.setMaximumSize(new java.awt.Dimension(98301, 31));
        jPanelConfirmDelete.setMinimumSize(new java.awt.Dimension(312, 31));
        jPanelConfirmDelete.setPreferredSize(new java.awt.Dimension(100, 31));
        jPanelConfirmDelete.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelConfirmDelete.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelConfirmDelete.setText("jLabelConfirmDelete");
        jPanelConfirmDelete.add(jLabelConfirmDelete);

        jTextFieldConfirmDelete.setText("non");
        jTextFieldConfirmDelete.setMaximumSize(new java.awt.Dimension(50, 22));
        jTextFieldConfirmDelete.setMinimumSize(new java.awt.Dimension(50, 22));
        jTextFieldConfirmDelete.setPreferredSize(new java.awt.Dimension(50, 22));
        jPanelConfirmDelete.add(jTextFieldConfirmDelete);

        jPanelCenter.add(jPanelConfirmDelete);

        jPanelSep.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSep.setMinimumSize(new java.awt.Dimension(10, 5));
        jPanelSep.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelCenter.add(jPanelSep);

        jPanelSepLine.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepLine.setLayout(new javax.swing.BoxLayout(jPanelSepLine, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSepLine.add(jSeparator2);

        jPanelCenter.add(jPanelSepLine);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setLayout(new javax.swing.BoxLayout(jPanelSouth, javax.swing.BoxLayout.LINE_AXIS));

        jPanelButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonOk.setText("jButtonOk");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        jPanelButton.add(jButtonOk);

        jButtonCancel.setText("jButtonCancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelButton.add(jButtonCancel);

        jPanel5.setMaximumSize(new java.awt.Dimension(1, 32767));
        jPanel5.setMinimumSize(new java.awt.Dimension(1, 10));
        jPanel5.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelButton.add(jPanel5);

        jPanelSouth.add(jPanelButton);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        doIt();
    }//GEN-LAST:event_jButtonOkActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            System.out.println("Failed loading L&F: ");
            System.out.println(ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                ConfirmAccountDeleteDialog dialog
                        = new ConfirmAccountDeleteDialog(new javax.swing.JFrame(), 1, "email@email.com", null);
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
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JLabel jLabelConfirmDelete;
    private javax.swing.JLabel jLabelPassphrase;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelConfirmDelete;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelEast1;
    private javax.swing.JPanel jPanelEditorPane;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelNorth1;
    private javax.swing.JPanel jPanelPassphrase;
    private javax.swing.JPanel jPanelPassword;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSepFromGrid;
    private javax.swing.JPanel jPanelSepFromGrid1;
    private javax.swing.JPanel jPanelSepLine;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelSouth1;
    private javax.swing.JPanel jPanelUp;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JPanel jPanelWest1;
    private javax.swing.JPasswordField jPassword;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextFieldConfirmDelete;
    // End of variables declaration//GEN-END:variables
}
