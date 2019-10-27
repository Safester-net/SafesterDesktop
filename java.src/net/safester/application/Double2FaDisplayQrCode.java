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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;
import org.bouncycastle.openpgp.PGPPrivateKey;

import com.safelogic.pgp.api.KeyHandlerOne;
import com.safelogic.pgp.api.PgpActionsOne;
import com.safelogic.pgp.apispecs.KeyHandler;
import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.photo.ImageResizer;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.PgpKeyPairLocal;
import net.safester.clientserver.holder.PgpKeyPairHolder;

/**
 * Displays the QR Code for the 2FA account.
 *
 * @author Nicolas de Pomereu
 */
public class Double2FaDisplayQrCode extends javax.swing.JDialog {

    public static final String CR_LF = System.getProperty("line.separator");
        
    public static boolean DEBUG = true;

    /**
     * Messages in national language
     */
    private MessagesManager messages = new MessagesManager();

    ClipboardManager clipboardManager;
    private final Double2FaDisplayQrCode thisOne;

    /**
     * The ImageResizer
     */
    private ImageResizer imageResizer = null;

    //Email of owner
    private String keyId = null;
    private File qrCodeFile = null;
    private Connection connection = null;
    private final int userNumber;
    private final char[] passphrase;

    /**
     * Creates new form ImagePreview
     */
    public Double2FaDisplayQrCode(java.awt.Frame parent, int userNumber, String keyId, char[] passphrase, File qrCodeFile, boolean modal, Connection connection) {
        super(parent, modal);

        thisOne = this;
        this.userNumber = userNumber;
        this.keyId = keyId;
        this.passphrase = passphrase;
        this.qrCodeFile = qrCodeFile;
        this.connection = connection;

        initComponents();
        initialize();

    }

    /**
     * This is the method to include in *our* constructor
     */
    public void initialize() {
        clipboardManager = new ClipboardManager(rootPane);

        this.setSize(new Dimension(510, 510));

        try {
            this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        this.setTitle(messages.getMessage("double_2fa_account_qr_code"));
        this.jLabelHelp.setText(this.getTitle());
        
        jEditorPaneCode.setContentType("text/html");
        jEditorPaneCode.setEditable(false);
        jEditorPaneCode.setText(Help.getHtmlHelpContent("2fa_account_created"));
        

        jButtonDisplaySecretKey.setText(messages.getMessage("double_2fa_display_secret_key"));
        jButtonCreate .setText(messages.getMessage("double_2fa_create_new_account")); 
        jButtonClose.setText(messages.getMessage("close"));
        
        ButtonResizer buttonResizer = new ButtonResizer(jPanelButtonsLeft);
        buttonResizer.setWidthToMax();

        this.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                saveSettings();
            }

            public void componentResized(ComponentEvent e) {
                saveSettings();
            }
        });

        // If window is closed ==> call close()
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                WindowSettingManager.save(thisOne);
                close();
            }
        });
        
        this.keyListenerAdder();

        SwingUtil.resizeJComponentsForNimbusAndMacOsX(rootPane);

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                createQrCodeImage();
            }

        });

        //getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.setLocationByPlatform(true);
        WindowSettingManager.load(this);

    }

    public boolean createQrCodeImage() {
        Image image = null;
        try {
            image = ImageIO.read(qrCodeFile);
            ImageIcon icon = new ImageIcon(image);
            jLabelImage.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
            getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPaneNewCustom.showException(null, e);
            return true;
        }
        return false;
    }
                
    private void close() {
        WindowSettingManager.save(this);        

        JOptionPane.showMessageDialog(null, messages.getMessage("double_2fa_next_screen_will_allow_to_activate"));
        this.setVisible(false);
        
        Double2FaActivationStatus dialog = new Double2FaActivationStatus(null, userNumber, keyId, connection);
        dialog.setVisible(true);

        this.dispose();
               
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


    private void displayBase32SecretKey(char[] passphrase) {
        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        
            PgpKeyPairHolder pgpKeyPairHolder = new PgpKeyPairHolder(connection, userNumber);
            PgpKeyPairLocal keyPair = pgpKeyPairHolder.get();

            String privateKeyPgpBlock = keyPair.getPrivateKeyPgpBlock();
            String encrypted = null;

            AwakeConnection awakeConnection = (AwakeConnection) connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

            //net.safester.server.auth2fa.awake.Auth2faManagerAwake
            encrypted = awakeFileSession.call("net.safester.server.auth2fa.awake.Auth2faManagerAwake.get2faBase32SecretEncryptedForUser",
                    userNumber,
                    keyId,
                    awakeFileSession.getAuthenticationToken(),
                    connection);
             
            this.setCursor(Cursor.getDefaultCursor());
             
            KeyHandler keyHandler = new KeyHandlerOne();
            PGPPrivateKey privateKey = keyHandler.getPgpSecretKeyFromAsc(privateKeyPgpBlock, encrypted, passphrase);

            PgpActionsOne pgpActions = new PgpActionsOne();

            String base32SecretKey = pgpActions.decryptStringPgp(encrypted, privateKey, null);

            //System.out.println("base32SecretKey: " + base32SecretKey);
            
            String text = messages.getMessage("double_2fa_backup_secret_key") + CR_LF + CR_LF  + base32SecretKey;
            JTextArea ta = new JTextArea(5, 50);
            ta.setText(text);
            ta.setWrapStyleWord(true);
            ta.setLineWrap(true);
            ta.setCaretPosition(0);
            ta.setEditable(false);

            JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Safester", JOptionPane.INFORMATION_MESSAGE);

           
        } catch (Exception e) {
            e.printStackTrace();
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, messages.getMessage("double_2fa_can_not_display_secret_key"));
            return;
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
        jPanelLabelTop = new javax.swing.JPanel();
        jLabelHelp = new javax.swing.JLabel();
        jPanelUp2 = new javax.swing.JPanel();
        jPanelHelpText = new javax.swing.JPanel();
        jPanelLeftHelp1 = new javax.swing.JPanel();
        jPanelEditorHelp1 = new javax.swing.JPanel();
        jEditorPaneCode = new javax.swing.JEditorPane();
        jPanelRightHelp1 = new javax.swing.JPanel();
        jPanelUp1 = new javax.swing.JPanel();
        jPanelPhotoMain = new javax.swing.JPanel();
        jPanelLeft = new javax.swing.JPanel();
        jPanelPhoto = new javax.swing.JPanel();
        jPanelDefaultPhoto = new javax.swing.JPanel();
        jLabelImage = new javax.swing.JLabel();
        jPanelRight = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanelSepBottom = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jPanelButtonsLeft = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jButtonCreate = new javax.swing.JButton();
        jButtonDisplaySecretKey = new javax.swing.JButton();
        jPanelButtonsRight = new javax.swing.JPanel();
        jButtonClose = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelUp.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        getContentPane().add(jPanelUp);

        jPanelLabelTop.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 9));

        jLabelHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/qr_code.png"))); // NOI18N
        jLabelHelp.setText("jLabelHelp");
        jPanelLabelTop.add(jLabelHelp);

        getContentPane().add(jPanelLabelTop);

        jPanelUp2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        getContentPane().add(jPanelUp2);

        jPanelHelpText.setMaximumSize(new java.awt.Dimension(2147483647, 48));
        jPanelHelpText.setLayout(new javax.swing.BoxLayout(jPanelHelpText, javax.swing.BoxLayout.X_AXIS));

        jPanelLeftHelp1.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelHelpText.add(jPanelLeftHelp1);

        jPanelEditorHelp1.setLayout(new javax.swing.BoxLayout(jPanelEditorHelp1, javax.swing.BoxLayout.LINE_AXIS));

        jEditorPaneCode.setEditable(false);
        jEditorPaneCode.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jPanelEditorHelp1.add(jEditorPaneCode);

        jPanelHelpText.add(jPanelEditorHelp1);

        jPanelRightHelp1.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelHelpText.add(jPanelRightHelp1);

        getContentPane().add(jPanelHelpText);

        jPanelUp1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        getContentPane().add(jPanelUp1);

        jPanelPhotoMain.setLayout(new javax.swing.BoxLayout(jPanelPhotoMain, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelLeft.setLayout(new javax.swing.BoxLayout(jPanelLeft, javax.swing.BoxLayout.LINE_AXIS));
        jPanelPhotoMain.add(jPanelLeft);

        jPanelPhoto.setLayout(new java.awt.BorderLayout());

        jPanelDefaultPhoto.setBackground(new java.awt.Color(255, 255, 255));
        jPanelDefaultPhoto.setLayout(new java.awt.BorderLayout());

        jLabelImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanelDefaultPhoto.add(jLabelImage, java.awt.BorderLayout.CENTER);

        jPanelPhoto.add(jPanelDefaultPhoto, java.awt.BorderLayout.CENTER);

        jPanelPhotoMain.add(jPanelPhoto);

        jPanelRight.setMaximumSize(new java.awt.Dimension(10, 10));
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

        jPanelButtons.setLayout(new javax.swing.BoxLayout(jPanelButtons, javax.swing.BoxLayout.LINE_AXIS));

        jPanelButtonsLeft.setMaximumSize(new java.awt.Dimension(32767, 43));
        jPanelButtonsLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 10));

        jPanel5.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelButtonsLeft.add(jPanel5);

        jButtonCreate.setText("jButtonCreate");
        jButtonCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateActionPerformed(evt);
            }
        });
        jPanelButtonsLeft.add(jButtonCreate);

        jButtonDisplaySecretKey.setText("jButtonDisplaySecretKey");
        jButtonDisplaySecretKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDisplaySecretKeyActionPerformed(evt);
            }
        });
        jPanelButtonsLeft.add(jButtonDisplaySecretKey);

        jPanelButtons.add(jPanelButtonsLeft);

        jPanelButtonsRight.setMaximumSize(new java.awt.Dimension(32767, 43));
        jPanelButtonsRight.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonClose.setText("jButtonClose");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jPanelButtonsRight.add(jButtonClose);

        jPanel4.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelButtonsRight.add(jPanel4);

        jPanelButtons.add(jPanelButtonsRight);

        getContentPane().add(jPanelButtons);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        
        close();
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jButtonCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateActionPerformed
        
        String text = messages.getMessage("are_you_sure_you_want_to_create_new_qr_code_1") + CR_LF + messages.getMessage("are_you_sure_you_want_to_create_new_qr_code_2");
        text = text.replace("${0}", keyId);
        
        String title = messages.getMessage("warning");

        int result = JOptionPane.showConfirmDialog(this, text, title, JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.NO_OPTION) {
            return;
        }
        
        AwakeConnection awakeConnection = (AwakeConnection)connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
       
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
             awakeFileSession.call("net.safester.server.auth2fa.awake.Auth2faManagerAwake.delete2faAccount",
                    userNumber,
                    keyId,
                    awakeFileSession.getAuthenticationToken(),
                    connection);
                        
            String hexImage = awakeFileSession.call("net.safester.server.auth2fa.awake.Auth2faManagerAwake.get2faQrCodeImage",
                    userNumber,
                    keyId,
                    awakeFileSession.getAuthenticationToken(),
                    connection);
                        
            // Store the image to a file and pass the file to Double2FaDisplayQrCode
            qrCodeFile = new File(SystemUtils.USER_HOME + File.separator + "qrcode.png");
            byte[] image = new Hex().decode(hexImage.getBytes());
            FileUtils.writeByteArrayToFile(qrCodeFile, image);
        
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            
            createQrCodeImage();
            
            text = messages.getMessage("new_qr_code_successfully_created_1") + CR_LF + messages.getMessage("new_qr_code_successfully_created_2");
            text = text.replace("${0}", keyId);

            JOptionPane.showMessageDialog(this, text, title, JOptionPane.INFORMATION_MESSAGE);

        }
        catch (Exception e) {
            e.printStackTrace();
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, messages.getMessage("double_2fa_can_not_create_account"));
            return;
        }
            
    }//GEN-LAST:event_jButtonCreateActionPerformed

    private void jButtonDisplaySecretKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDisplaySecretKeyActionPerformed
        displayBase32SecretKey(passphrase);
    }//GEN-LAST:event_jButtonDisplaySecretKeyActionPerformed

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
            java.util.logging.Logger.getLogger(Double2FaDisplayQrCode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Double2FaDisplayQrCode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Double2FaDisplayQrCode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Double2FaDisplayQrCode.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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

                    File file = new File(SystemUtils.USER_HOME + File.separator + "qrcode.png");
                    Double2FaDisplayQrCode dialog = new Double2FaDisplayQrCode(new javax.swing.JFrame(), 1, "ndepomereu@kawansoft.com", "passphrase".toCharArray(), file, true, null);

                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            System.exit(0);
                        }
                    });
                    dialog.setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(Double2FaDisplayQrCode.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonCreate;
    private javax.swing.JButton jButtonDisplaySecretKey;
    private javax.swing.JEditorPane jEditorPaneCode;
    private javax.swing.JLabel jLabelHelp;
    private javax.swing.JLabel jLabelImage;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelButtonsLeft;
    private javax.swing.JPanel jPanelButtonsRight;
    private javax.swing.JPanel jPanelDefaultPhoto;
    private javax.swing.JPanel jPanelEditorHelp1;
    private javax.swing.JPanel jPanelHelpText;
    private javax.swing.JPanel jPanelLabelTop;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelLeftHelp1;
    private javax.swing.JPanel jPanelPhoto;
    private javax.swing.JPanel jPanelPhotoMain;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelRightHelp1;
    private javax.swing.JPanel jPanelSepBottom;
    private javax.swing.JPanel jPanelUp;
    private javax.swing.JPanel jPanelUp1;
    private javax.swing.JPanel jPanelUp2;
    // End of variables declaration//GEN-END:variables

}
