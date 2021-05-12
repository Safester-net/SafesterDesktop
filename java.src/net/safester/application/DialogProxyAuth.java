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

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.util.UserPrefManager;

/**
 *
 * @author Alexandre Becquereau
 */
public class DialogProxyAuth extends javax.swing.JDialog {

    MessagesManager messages = new MessagesManager();

    /** if true, uuse has canceled the form */
    private boolean isCancelled = true;

    /** The proxy login */
    private String proxyUsername = null;

    /** The proxy password */
    private String proxyPassword = null;

    ClipboardManager clipboardManager;
    
    /** Creates new form JDialogProxyAuth */
    public DialogProxyAuth(Window parent) {
        super(parent);
        initComponents();
        initCompany();
        this.setLocationRelativeTo(parent);
    }

    private void initCompany(){
        clipboardManager = new ClipboardManager(rootPane);
        
        this.setTitle(messages.getMessage("proxy_authentification"));
        this.jLabelTitle.setText(messages.getMessage("proxy_authentification"));
        this.jLabelMessage.setText(messages.getMessage("proxy_requires_authentification"));
        this.jLabelUsername.setText(messages.getMessage("username"));
        this.jLabelPassword.setText(messages.getMessage("password_2"));
        this.jCheckBoxRememberInfo.setText(messages.getMessage("remember_information"));
        this.jButtonCancel.setText(messages.getMessage("cancel"));
        this.jButtonOk.setText(messages.getMessage("ok"));

        this.jCheckBoxRememberInfo.setSelected(UserPrefManager.getBooleanPreference(UserPrefManager.PROXY_AUTH_REMEMBER_INFO));

        this.jTextFieldUsername.setText(null);
        this.jPasswordField.setText(null);
        
        if (jCheckBoxRememberInfo.isSelected())
        {
            this.jTextFieldUsername.setText(UserPrefManager.getPreference(UserPrefManager.PROXY_AUTH_USERNAME));
            this.jPasswordField.setText(UserPrefManager.getPreference(UserPrefManager.PROXY_AUTH_PASSWORD));
        }
        
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
                jButtonCancelActionPerformed(null);
            }

        }
    }

    
    private void doIt(){

        UserPrefManager.setPreference(UserPrefManager.PROXY_AUTH_REMEMBER_INFO, jCheckBoxRememberInfo.isSelected());

        this.proxyUsername = jTextFieldUsername.getText();
        this.proxyPassword = new String(jPasswordField.getPassword());
        this.isCancelled = false;

        if (jCheckBoxRememberInfo.isSelected())
        {
            UserPrefManager.setPreference(UserPrefManager.PROXY_AUTH_USERNAME, proxyUsername);
            UserPrefManager.setPreference(UserPrefManager.PROXY_AUTH_PASSWORD, proxyPassword);
        }
        else
        {
            UserPrefManager.setPreference(UserPrefManager.PROXY_AUTH_USERNAME, "");
            UserPrefManager.setPreference(UserPrefManager.PROXY_AUTH_PASSWORD, "");
        }

        dispose();
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * @return the proxyLogin
     */
    public String getProxyUsername() {
        return proxyUsername;
    }

    /**
     * @return the proxyPassword
     */
    public String getProxyPassword() {
        return proxyPassword;
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
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabelMessage = new javax.swing.JLabel();
        jPanelSep = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabelUsername = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabelPassword = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jTextFieldUsername = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jPasswordField = new javax.swing.JPasswordField();
        jPanel13 = new javax.swing.JPanel();
        jCheckBoxRememberInfo = new javax.swing.JCheckBox();
        jPanelSepBlank = new javax.swing.JPanel();
        jPanelSepLine = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelSouth = new javax.swing.JPanel();
        jPanelLeft = new javax.swing.JPanel();
        jPanelRight = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
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

        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/server_network.png"))); // NOI18N
        jLabelTitle.setText("Proxy Authentification");
        jPanelTitle.add(jLabelTitle);

        jPanelCenter.add(jPanelTitle);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 30));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelMessage.setText("Proxy Authentication Required. Please enter a Username and a Password:");
        jPanel1.add(jLabelMessage);

        jPanel2.add(jPanel1);

        jPanelSep.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSep.setMinimumSize(new java.awt.Dimension(10, 5));
        jPanelSep.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanel2.add(jPanelSep);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jPanel9.setLayout(new javax.swing.BoxLayout(jPanel9, javax.swing.BoxLayout.Y_AXIS));

        jPanel4.setMaximumSize(new java.awt.Dimension(65, 30));
        jPanel4.setMinimumSize(new java.awt.Dimension(65, 30));
        jPanel4.setPreferredSize(new java.awt.Dimension(65, 30));
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 5, 8));

        jLabelUsername.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelUsername.setText("Username:");
        jPanel4.add(jLabelUsername);

        jPanel9.add(jPanel4);

        jPanel5.setMaximumSize(new java.awt.Dimension(65, 30));
        jPanel5.setMinimumSize(new java.awt.Dimension(65, 30));
        jPanel5.setPreferredSize(new java.awt.Dimension(65, 30));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 5, 8));

        jLabelPassword.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelPassword.setText("Password:");
        jPanel5.add(jLabelPassword);

        jPanel9.add(jPanel5);

        jPanel14.setMaximumSize(new java.awt.Dimension(65, 30));
        jPanel14.setMinimumSize(new java.awt.Dimension(65, 30));
        jPanel14.setPreferredSize(new java.awt.Dimension(65, 30));
        jPanel14.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 8));
        jPanel9.add(jPanel14);

        jPanel3.add(jPanel9);

        jPanel10.setPreferredSize(new java.awt.Dimension(100, 39));
        jPanel10.setLayout(new javax.swing.BoxLayout(jPanel10, javax.swing.BoxLayout.Y_AXIS));

        jPanel6.setMaximumSize(new java.awt.Dimension(32767, 30));
        jPanel6.setPreferredSize(new java.awt.Dimension(192, 30));
        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jTextFieldUsername.setText("jTextFieldLogin");
        jTextFieldUsername.setPreferredSize(new java.awt.Dimension(200, 22));
        jPanel6.add(jTextFieldUsername);

        jPanel10.add(jPanel6);

        jPanel7.setMaximumSize(new java.awt.Dimension(32767, 30));
        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jPasswordField.setText("jPasswordField1");
        jPasswordField.setMinimumSize(new java.awt.Dimension(200, 22));
        jPasswordField.setPreferredSize(new java.awt.Dimension(200, 22));
        jPanel7.add(jPasswordField);

        jPanel10.add(jPanel7);

        jPanel13.setMaximumSize(new java.awt.Dimension(32767, 30));
        jPanel13.setPreferredSize(new java.awt.Dimension(192, 30));
        jPanel13.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 5));

        jCheckBoxRememberInfo.setText("Remember these informations");
        jPanel13.add(jCheckBoxRememberInfo);

        jPanel10.add(jPanel13);

        jPanel3.add(jPanel10);

        jPanel2.add(jPanel3);

        jPanelCenter.add(jPanel2);

        jPanelSepBlank.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepBlank.setPreferredSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank.setLayout(new javax.swing.BoxLayout(jPanelSepBlank, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenter.add(jPanelSepBlank);

        jPanelSepLine.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepLine.setLayout(new javax.swing.BoxLayout(jPanelSepLine, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSepLine.add(jSeparator2);

        jPanelCenter.add(jPanelSepLine);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setPreferredSize(new java.awt.Dimension(101, 43));
        jPanelSouth.setLayout(new java.awt.GridLayout(1, 2));

        jPanelLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 10));
        jPanelSouth.add(jPanelLeft);

        jPanelRight.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonOk.setText("Ok");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        jPanelRight.add(jButtonOk);

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelRight.add(jButtonCancel);

        jPanel12.setMaximumSize(new java.awt.Dimension(0, 10));
        jPanel12.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanel12.setPreferredSize(new java.awt.Dimension(0, 10));
        jPanel12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 5));
        jPanelRight.add(jPanel12);

        jPanelSouth.add(jPanelRight);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        doIt();
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        System.out.println("this.dispose()");
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

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
                DialogProxyAuth dialog = new DialogProxyAuth(new javax.swing.JFrame());
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
    private javax.swing.JCheckBox jCheckBoxRememberInfo;
    private javax.swing.JLabel jLabelMessage;
    private javax.swing.JLabel jLabelPassword;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelUsername;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSepBlank;
    private javax.swing.JPanel jPanelSepLine;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelTitle;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JPasswordField jPasswordField;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextFieldUsername;
    // End of variables declaration//GEN-END:variables

}
