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
package net.safester.application.mac;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.util.JOptionPaneNewCustom;

import com.swing.util.SwingUtil;

/**
 * Dialog to ask a Mac Os X account.
 * 
 * @author Nicolas de Pomereu
 */
public class MacOsXPasswordAsk extends javax.swing.JDialog {

    public static boolean DEBUG = false;
    
    ClipboardManager clipboardManager;

    MessagesManager messages = new MessagesManager();

    /** The username to display */
    private final String username;

    /** Says if we cancel */
    private boolean cancelled = false;
        
    /** Creates new form JDialogProxyAuth */
    public MacOsXPasswordAsk(Window parent, String username) {
        super(parent);
        initComponents();
        this.setLocationRelativeTo(parent);

        this.username = username;

        initCompany();
    }

    private void initCompany(){
        clipboardManager = new ClipboardManager(rootPane);
        
        this.setTitle(messages.getMessage("authentificate_with_mac_account_password"));
        this.jLabelTitle.setText(this.getTitle());

        this.jLabelPassword.setText(messages.getMessage("password") + ":");

        this.jButtonCancel.setText(messages.getMessage("cancel"));
        this.jButtonOk.setText(messages.getMessage("ok"));

        this.jTextField.setText(username);
        this.jPasswordField.setText(null);
        
        try
        {
            this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        } 

        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);

        String helpText = "<font face=Arial size=\"3\">"
        + "We need to upgrade Java Security configuration files to unable strong encryption."
        + "<br>" 
        + "This operation must be done by a Mac Admin account and requires password authentication."

        + "<br>If your account has no Admin rights, please log in with an Admin account and retry."
        + "<br><br>"
        + "The operation is done only once, unless you upgrade Java. It has no impact on other Java applications."
        + "<br>";        

        //jEditorPane.setText(Help.getHtmlHelpContent("mac_os_account_password"));
        jEditorPane.setText(helpText);        
        keyListenerAdder();
        
        ButtonResizer buttonResizer = new ButtonResizer();
        buttonResizer.setWidthToMax(SwingUtil.getAllComponants(jPanelSouth));
        SwingUtil.resizeJComponentsForNimbusAndMacOsX(rootPane);

        this.jPasswordField.requestFocus();

        this.setVisible(true);
    }

    boolean isCancelled() {
        return cancelled;
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

    
    private void doIt()
    {
        try {

            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            String password = new String(jPasswordField.getPassword());

           MacOsXFullPolicyFiles macOsXFullPolicyFiles = new MacOsXFullPolicyFiles();
           String tempDir      = macOsXFullPolicyFiles.getTempDir();
           String libSecurityDir  = macOsXFullPolicyFiles.getLibSecurityDir();

           if (DEBUG) JOptionPane.showMessageDialog(null, "tempDir       : " + tempDir);           
           if (DEBUG) JOptionPane.showMessageDialog(null, "libSecurityDir: " + libSecurityDir);
           
            // 3) Launch shell to update files and continue back to loop to test install
            MacOsXCommands macOsXCommands = new MacOsXCommands();

            macOsXCommands.cpWithSudo(password,
                    tempDir + MacOsXFullPolicyFiles.LOCAL_POLICY_JAR,
                    libSecurityDir + MacOsXFullPolicyFiles.LOCAL_POLICY_JAR);

            macOsXCommands.cpWithSudo(password,
                    tempDir + MacOsXFullPolicyFiles.US_EXPORT_POLICY_JAR,
                    libSecurityDir + MacOsXFullPolicyFiles.US_EXPORT_POLICY_JAR);

            // If policy files are now correctly installed ==> Say yes
            this.setCursor(Cursor.getDefaultCursor());
            
            if (macOsXFullPolicyFiles.isFullPolicyFileInstalled(MacOsXFullPolicyFiles.US_EXPORT_POLICY_JAR)
                    && macOsXFullPolicyFiles.isFullPolicyFileInstalled(MacOsXFullPolicyFiles.LOCAL_POLICY_JAR)) {
                this.setCursor(Cursor.getDefaultCursor());
                this.dispose();
            } else {
                // Display message and loop back to password ask
                JOptionPane.showMessageDialog(this, messages.getMessage("invalid_password_or_not_admin_account"));
            }
        } catch (Exception e)
        {
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPaneNewCustom.showException(rootPane, e);
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
        jPanelTitle = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanelhelp = new javax.swing.JPanel();
        jPanelLeftHelp = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jEditorPane = new javax.swing.JEditorPane();
        jPanel24 = new javax.swing.JPanel();
        jPanelSep = new javax.swing.JPanel();
        jPanelName = new javax.swing.JPanel();
        jLabeName = new javax.swing.JLabel();
        jTextField = new javax.swing.JTextField();
        jPanelLeftHelp3 = new javax.swing.JPanel();
        jPanelPassword = new javax.swing.JPanel();
        jLabelPassword = new javax.swing.JLabel();
        jPasswordField = new javax.swing.JPasswordField();
        jPanelLeftHelp2 = new javax.swing.JPanel();
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

        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/lock.png"))); // NOI18N
        jLabelTitle.setText("Please authenticate with your Mac account password");
        jPanelTitle.add(jLabelTitle);

        jPanelCenter.add(jPanelTitle);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jPanelhelp.setLayout(new javax.swing.BoxLayout(jPanelhelp, javax.swing.BoxLayout.X_AXIS));

        jPanelLeftHelp.setMaximumSize(new java.awt.Dimension(50, 10));
        jPanelLeftHelp.setMinimumSize(new java.awt.Dimension(50, 10));
        jPanelLeftHelp.setPreferredSize(new java.awt.Dimension(50, 10));
        jPanelhelp.add(jPanelLeftHelp);

        jPanel23.setLayout(new javax.swing.BoxLayout(jPanel23, javax.swing.BoxLayout.LINE_AXIS));

        jEditorPane.setEditable(false);
        jEditorPane.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jEditorPane.setPreferredSize(new java.awt.Dimension(170, 166));
        jPanel23.add(jEditorPane);

        jPanelhelp.add(jPanel23);

        jPanel24.setMaximumSize(new java.awt.Dimension(50, 10));
        jPanel24.setMinimumSize(new java.awt.Dimension(50, 10));
        jPanel24.setPreferredSize(new java.awt.Dimension(50, 10));
        jPanelhelp.add(jPanel24);

        jPanel2.add(jPanelhelp);

        jPanelSep.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanel2.add(jPanelSep);

        jPanelName.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPanelName.setMinimumSize(new java.awt.Dimension(310, 32));
        jPanelName.setPreferredSize(new java.awt.Dimension(310, 32));
        jPanelName.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING));

        jLabeName.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabeName.setText("Name:");
        jPanelName.add(jLabeName);

        jTextField.setEditable(false);
        jTextField.setText("jTextField");
        jTextField.setMaximumSize(new java.awt.Dimension(200, 20));
        jTextField.setMinimumSize(new java.awt.Dimension(200, 20));
        jTextField.setPreferredSize(new java.awt.Dimension(200, 20));
        jPanelName.add(jTextField);

        jPanelLeftHelp3.setMaximumSize(new java.awt.Dimension(40, 10));
        jPanelLeftHelp3.setMinimumSize(new java.awt.Dimension(40, 10));
        jPanelLeftHelp3.setPreferredSize(new java.awt.Dimension(40, 10));
        jPanelName.add(jPanelLeftHelp3);

        jPanel2.add(jPanelName);

        jPanelPassword.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPanelPassword.setMinimumSize(new java.awt.Dimension(310, 32));
        jPanelPassword.setPreferredSize(new java.awt.Dimension(310, 32));
        jPanelPassword.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING));

        jLabelPassword.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelPassword.setText("Password:");
        jLabelPassword.setMaximumSize(new java.awt.Dimension(70, 14));
        jPanelPassword.add(jLabelPassword);

        jPasswordField.setText("jPasswordField1");
        jPasswordField.setMaximumSize(new java.awt.Dimension(200, 20));
        jPasswordField.setMinimumSize(new java.awt.Dimension(200, 20));
        jPasswordField.setPreferredSize(new java.awt.Dimension(200, 20));
        jPanelPassword.add(jPasswordField);

        jPanelLeftHelp2.setMaximumSize(new java.awt.Dimension(40, 10));
        jPanelLeftHelp2.setMinimumSize(new java.awt.Dimension(40, 10));
        jPanelLeftHelp2.setPreferredSize(new java.awt.Dimension(40, 10));
        jPanelPassword.add(jPanelLeftHelp2);

        jPanel2.add(jPanelPassword);

        jPanelCenter.add(jPanel2);

        jPanelSepBlank.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepBlank.setMinimumSize(new java.awt.Dimension(0, 10));
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
        this.cancelled = true;
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
                MacOsXPasswordAsk dialog = new MacOsXPasswordAsk(new javax.swing.JFrame(), null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JLabel jLabeName;
    private javax.swing.JLabel jLabelPassword;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelLeftHelp;
    private javax.swing.JPanel jPanelLeftHelp2;
    private javax.swing.JPanel jPanelLeftHelp3;
    private javax.swing.JPanel jPanelName;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelPassword;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSepBlank;
    private javax.swing.JPanel jPanelSepLine;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelTitle;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JPanel jPanelhelp;
    private javax.swing.JPasswordField jPasswordField;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextField;
    // End of variables declaration//GEN-END:variables


}
