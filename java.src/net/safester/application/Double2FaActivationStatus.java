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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.photo.ImageResizer;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;

/**
 * Displays the QR Code for the 2FA account.
 *
 * @author Nicolas de Pomereu
 */
public class Double2FaActivationStatus extends javax.swing.JDialog {

    public static boolean DEBUG = false;

    /**
     * Messages in national language
     */
    private MessagesManager messages = new MessagesManager();

    ClipboardManager clipboardManager;
    private final Double2FaActivationStatus thisOne;

    /**
     * The ImageResizer
     */
    private ImageResizer imageResizer = null;

    //Email of owner
    private String keyId;
    private boolean activationStatusOn = false;
    private final int userNumber;
    private Connection connection = null;
    private Color greenEclipse = new Color(0, 153, 0);

    /**
     * Creates new form ImagePreview
     */
    public Double2FaActivationStatus(Window parent, int userNumber, String keyId, Connection connection) {

        this.setModal(true);
        thisOne = this;
        this.userNumber = userNumber;
        this.keyId = keyId;
        this.connection = connection;
        initComponents();
        initialize();

    }

    /**
     * This is the method to include in *our* constructor
     */
    public void initialize() {
        clipboardManager = new ClipboardManager(rootPane);

        this.setSize(new Dimension(390, 390));

        try {
            this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        this.setTitle(messages.getMessage("double_2fa_activation"));
        this.jLabelTitle.setText(messages.getMessage("double_2fa_activation_howto"));

        //this.jLabelActivationStatus.setText(messages.getMessage("double_2fa_activation"));
        jButtonClose.setText(messages.getMessage("close"));

        boolean activated = getActivityStatus();
        if (activated) {
            setActivationOnLabels();
            jToggleButton.setSelected(true);
        } else {
            setActivationOffLabels();
            jToggleButton.setSelected(false);
        }

        ButtonResizer buttonResizer = new ButtonResizer(jPanelButtons);
        buttonResizer.setWidthToMax();

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                saveSettings();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                saveSettings();
            }
        });

                // Our window listener for all events
        // If window is closed ==> call close()
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                WindowSettingManager.save(thisOne);
                close();
            }
        });
        
        this.keyListenerAdder();

        SwingUtil.applySwingUpdates(rootPane);

        //getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.setLocationByPlatform(true);
        WindowSettingManager.load(this);

    }

    public boolean getActivityStatus() throws HeadlessException {
        String activityStatus = "false";
        try {
            AwakeConnection awakeConnection = (AwakeConnection) connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
            activityStatus = awakeFileSession.call("net.safester.server.auth2fa.awake.Auth2faManagerAwake.getActivityStatus",
                    userNumber,
                    keyId,
                    awakeFileSession.getAuthenticationToken(),
                    connection);
            setCursor(Cursor.getDefaultCursor());
        } catch (Exception e) {
            e.printStackTrace();
            setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(thisOne, messages.getMessage("double_2fa_can_not_check_if_auth_is_on"));
            return false;
        }
        return Boolean.parseBoolean(activityStatus);
    }

    public boolean setActivityStatus(boolean activityStatus) throws HeadlessException {
        
        setCursor(Cursor.getPredefinedCursor((Cursor.WAIT_CURSOR)));
        
        try {
            AwakeConnection awakeConnection = (AwakeConnection) connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
            awakeFileSession.call("net.safester.server.auth2fa.awake.Auth2faManagerAwake.setActivityStatus",
                    userNumber,
                    keyId,
                    activityStatus,
                    awakeFileSession.getAuthenticationToken(),
                    connection);
            setCursor(Cursor.getDefaultCursor());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(thisOne, messages.getMessage("double_2fa_can_not_set_auth"));
            return false;
        }
    }

    private void close() {
        WindowSettingManager.save(this);

        this.dispose();
        thisOne.dispose();

    }

    public void saveSettings() {
        WindowSettingManager.save(this);
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
                close();
            }

            if (keyCode == KeyEvent.VK_ESCAPE) {
                close();
            }

            if (keyCode == KeyEvent.VK_F1) {
                //jButtonHelpActionPerformed(null);
            }
        }
    }

    /**
     * debug tool
     */
    private void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
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

        jPanelUp = new javax.swing.JPanel();
        jPanelTtile = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelPhotoMain = new javax.swing.JPanel();
        jPanelLeft = new javax.swing.JPanel();
        jPanelPhoto = new javax.swing.JPanel();
        jPanelDefaultPhoto3 = new javax.swing.JPanel();
        jPanelDefaultPhoto1 = new javax.swing.JPanel();
        jToggleButton = new javax.swing.JToggleButton();
        jPanelDefaultPhoto2 = new javax.swing.JPanel();
        jLabelActivationStatusEnabled = new javax.swing.JLabel();
        jPanelDefaultPhoto4 = new javax.swing.JPanel();
        jPanelRight = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanelSepBottom = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelUp.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelUp.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        getContentPane().add(jPanelUp);

        jPanelTtile.setMaximumSize(new java.awt.Dimension(32767, 50));
        jPanelTtile.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 9));

        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/log_in.png"))); // NOI18N
        jLabelTitle.setText("jLabelActivationStatus");
        jPanelTtile.add(jLabelTitle);

        getContentPane().add(jPanelTtile);

        jPanelPhotoMain.setLayout(new javax.swing.BoxLayout(jPanelPhotoMain, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft.setMaximumSize(new java.awt.Dimension(12, 12));
        jPanelLeft.setMinimumSize(new java.awt.Dimension(12, 12));
        jPanelLeft.setPreferredSize(new java.awt.Dimension(12, 12));
        jPanelLeft.setLayout(new javax.swing.BoxLayout(jPanelLeft, javax.swing.BoxLayout.LINE_AXIS));
        jPanelPhotoMain.add(jPanelLeft);

        jPanelPhoto.setBackground(new java.awt.Color(255, 255, 255));
        jPanelPhoto.setLayout(new javax.swing.BoxLayout(jPanelPhoto, javax.swing.BoxLayout.PAGE_AXIS));

        jPanelDefaultPhoto3.setBackground(new java.awt.Color(255, 255, 255));
        jPanelPhoto.add(jPanelDefaultPhoto3);

        jPanelDefaultPhoto1.setBackground(new java.awt.Color(255, 255, 255));
        jPanelDefaultPhoto1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));

        jToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/48x48/lock_open.png"))); // NOI18N
        jToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButtonMouseClicked(evt);
            }
        });
        jToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonActionPerformed(evt);
            }
        });
        jPanelDefaultPhoto1.add(jToggleButton);

        jPanelPhoto.add(jPanelDefaultPhoto1);

        jPanelDefaultPhoto2.setBackground(new java.awt.Color(255, 255, 255));
        jPanelDefaultPhoto2.setLayout(new javax.swing.BoxLayout(jPanelDefaultPhoto2, javax.swing.BoxLayout.LINE_AXIS));

        jLabelActivationStatusEnabled.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabelActivationStatusEnabled.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelActivationStatusEnabled.setText("2FA Disabled");
        jLabelActivationStatusEnabled.setMaximumSize(new java.awt.Dimension(200, 29));
        jLabelActivationStatusEnabled.setMinimumSize(new java.awt.Dimension(200, 29));
        jLabelActivationStatusEnabled.setPreferredSize(new java.awt.Dimension(200, 29));
        jPanelDefaultPhoto2.add(jLabelActivationStatusEnabled);

        jPanelPhoto.add(jPanelDefaultPhoto2);

        jPanelDefaultPhoto4.setBackground(new java.awt.Color(255, 255, 255));
        jPanelPhoto.add(jPanelDefaultPhoto4);

        jPanelPhotoMain.add(jPanelPhoto);

        jPanelRight.setMaximumSize(new java.awt.Dimension(12, 12));
        jPanelRight.setMinimumSize(new java.awt.Dimension(12, 12));
        jPanelRight.setPreferredSize(new java.awt.Dimension(12, 12));
        jPanelRight.setRequestFocusEnabled(false);
        jPanelRight.setLayout(new javax.swing.BoxLayout(jPanelRight, javax.swing.BoxLayout.LINE_AXIS));
        jPanelPhotoMain.add(jPanelRight);

        getContentPane().add(jPanelPhotoMain);

        jPanel14.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel14.setLayout(new javax.swing.BoxLayout(jPanel14, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(jPanel14);

        jPanelSepBottom.setMaximumSize(new java.awt.Dimension(32767, 8));
        jPanelSepBottom.setMinimumSize(new java.awt.Dimension(10, 8));
        jPanelSepBottom.setPreferredSize(new java.awt.Dimension(10, 8));
        getContentPane().add(jPanelSepBottom);

        jPanelButtons.setMaximumSize(new java.awt.Dimension(32767, 43));
        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonClose.setText("jButtonClose");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonClose);

        jPanel4.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelButtons.add(jPanel4);

        getContentPane().add(jPanelButtons);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        close();
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonActionPerformed
        // TODO add your handling code here:
        //System.out.println("State changed:");

        if (jToggleButton.isSelected()) {
            if (setActivityStatus(true)) {
                setActivationOnLabels();
            }

        } else {
            if (setActivityStatus(false)) {
                setActivationOffLabels();
            }
        }
    }//GEN-LAST:event_jToggleButtonActionPerformed

    public void setActivationOffLabels() {
        jToggleButton.setIcon(Parms.createImageIcon("images/files_2/48x48/lock_open.png"));
        jLabelActivationStatusEnabled.setText(messages.getMessage("double_2fa_activation_off"));
        jLabelActivationStatusEnabled.setForeground(Color.red);
    }

    public void setActivationOnLabels() {
        //popupItemEdit.setIcon(Parms.createImageIcon("images/files_2/16x16/businessman2_edit.png"));
        jToggleButton.setIcon(Parms.createImageIcon("images/files_2/48x48/lock.png"));
        jLabelActivationStatusEnabled.setText(messages.getMessage("double_2fa_activation_on"));
        jLabelActivationStatusEnabled.setForeground(greenEclipse);
    }

    private void jToggleButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButtonMouseClicked
        // TODO add your handling code here:
        //System.out.println("jToggleButton2MouseClicked");
    }//GEN-LAST:event_jToggleButtonMouseClicked

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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Double2FaActivationStatus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Double2FaActivationStatus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Double2FaActivationStatus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Double2FaActivationStatus.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            System.out.println("Failed loading L&F: ");
            System.out.println(ex);
        }

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {

                    Double2FaActivationStatus dialog = new Double2FaActivationStatus(new javax.swing.JFrame(), 1, "ndepomereu@kawansoft.com", null);

                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            System.exit(0);
                        }
                    });
                    dialog.setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(Double2FaActivationStatus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JLabel jLabelActivationStatusEnabled;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelDefaultPhoto1;
    private javax.swing.JPanel jPanelDefaultPhoto2;
    private javax.swing.JPanel jPanelDefaultPhoto3;
    private javax.swing.JPanel jPanelDefaultPhoto4;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelPhoto;
    private javax.swing.JPanel jPanelPhotoMain;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelSepBottom;
    private javax.swing.JPanel jPanelTtile;
    private javax.swing.JPanel jPanelUp;
    private javax.swing.JToggleButton jToggleButton;
    // End of variables declaration//GEN-END:variables
}
