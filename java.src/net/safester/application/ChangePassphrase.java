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
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.commons.lang3.SystemUtils;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.safelogic.pgp.api.KeyHandlerOne;
import com.swing.util.ButtonUrlOver;
import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.register.Register;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.HtmlTextUtil;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.crypto.PassphraseUtil;
import net.safester.clientserver.PgpKeyPairLocal;

public class ChangePassphrase extends javax.swing.JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 6783083435019423049L;
    MessagesManager messages;
    private char defaultEchocar;
    private Frame caller;
    private JDialog thisOne;
    private int userNumber;
    private Connection connection;

    /** Creates new form SafeShareItChangePassphrase */
    public ChangePassphrase(java.awt.Frame parent, Connection theConnection, int theUserNumber, boolean modal) {
        super(parent, modal);
        caller = parent;

        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();
        
        userNumber = theUserNumber;
        thisOne = this;
        initComponents();
        initCompany();
    }

    private void initCompany() {
        
        messages = new MessagesManager();
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        this.setTitle(messages.getMessage("change_passphrase"));
        defaultEchocar = this.jPassword.getEchoChar();

        jPasswordOld.setText(null);
        jPassword.setText(null);
        jPassword1.setText(null);

        jLabelTitle.setText(messages.getMessage("change_passphrase"));

        jLabelPassphraseOld.setText(messages.getMessage("present_passphrase"));
        jLabelPassphrase.setText(messages.getMessage("new_passphrase"));
        jLabelRetypePassphrase.setText(messages.getMessage("confirm_new_passphrase"));
        jLabelKeyboardWarning.setText(null);

        jLabelQualityText.setText(" ");
        jButtonPassphraseQuality.setForeground(new Color(0, 0, 255));

        jButtonPassphraseQuality.setText(messages.getMessage("passphrase_quality"));
        jButtonCancel.setText(messages.getMessage("cancel"));

        jButtonOk.setText(messages.getMessage("ok"));

        jCheckBoxHideTyping.setSelected(true);
        jLabelHideTyping.setText(messages.getMessage("hide_typing"));
        jCheckBoxHideTyping.setText(null);

        ButtonResizer br = new ButtonResizer(jPanelSouth);
        br.setWidthToMax();
        jButtonOk.setEnabled(false);
        
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

        this.setSize(new Dimension(616, 342));
        
        this.setLocationRelativeTo(caller);
        WindowSettingManager.load(this);

        SwingUtil.applySwingUpdates(rootPane);

        testCapsOn();

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

    /**
     * Test0 if Caps is on
     *
     */
    private void testCapsOn() {

        if (SystemUtils.IS_OS_MAC) return; // Nothing on Mac (has already a hint)

        // NDP - Toolkit.getDefaultToolkit().getLockingKeyState is wrapped in a try/catch
        //       because Linux Ubuntu does not supports it!
        boolean capsOn = false;

        try {
            capsOn = java.awt.Toolkit.getDefaultToolkit().getLockingKeyState(
                    java.awt.event.KeyEvent.VK_CAPS_LOCK);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (capsOn) {
            jLabelKeyboardWarning.setEnabled(true);
            jLabelKeyboardWarning.setOpaque(true);
            jLabelKeyboardWarning.setBackground(new Color(255, 255, 180));
            jLabelKeyboardWarning.setText(" " + messages.getMessage("caps_on") + " ");
        } else {
            jLabelKeyboardWarning.setEnabled(false);
            jLabelKeyboardWarning.setBorder(null);
            jLabelKeyboardWarning.setOpaque(false);
            jLabelKeyboardWarning.setText(" ");
        }
    }

    private void this_keyPressed(KeyEvent e) {
        testCapsOn();

        int id = e.getID();
        if (id == KeyEvent.KEY_PRESSED) {
            //System.out.println("Key Realeased");
            //System.out.println("TextFieldUserEmail.getText():" + jTextFieldUserEmail.getText() + ":");

            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_ESCAPE) {
                this.dispose();
                return;
            }

            if (Register.getPassphraseQuality(jPassword.getPassword()) >= Register.QUALITY_WEAK) {
                jButtonOk.setEnabled(true);
            } else {
                jButtonOk.setEnabled(false);
            }

            if (keyCode == KeyEvent.VK_ENTER && jButtonOk.isEnabled()) {
                jButtonOkActionPerformed(null);
                return;
            }

            if (keyCode == KeyEvent.VK_F1) {
                // helpKeys();
            }
        }
    }



    private boolean arePassphraseEqual() {
        char[] passphrase1 = jPassword.getPassword();
        char[] passphrase2 = jPassword1.getPassword();

        if (passphrase1.length != passphrase2.length) {
            return false;
        }

        for (int i = 0; i < passphrase1.length; i++) {
            if (passphrase1[i] != passphrase2[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check of the old passphrase is valid
     * @return  true if the old passphrase is valid
     */
    private boolean checkPresentPassphrase()
        throws Exception
    {        
        if (caller instanceof Main) {
            
            Main main = (Main)caller;
            char [] passphrase = main.getUserPassphrase();
            
            return Arrays.equals(passphrase, jPasswordOld.getPassword());            
        }
        else {
            throw new IllegalArgumentException("parent is not instanceof Main");
        }
    }

    /**
     * Do the passphrase change.
     * @return true if the change has been done.
     */
    private boolean doIt() {

        if (caller instanceof Main) {
            try {

                String errorMsg = messages.getMessage("error");

                if (!checkPresentPassphrase()) {
                    JOptionPane.showMessageDialog(this,
                            messages.getMessage("present_passphrase_is_invalid"), errorMsg, JOptionPane.ERROR_MESSAGE);
                    this.setCursor(Cursor.getDefaultCursor());
                    return false;
                }

                char[] newPassphrase = jPassword.getPassword();

                Main safeShareMain = (Main) caller;
                char[] passphrase = safeShareMain.getUserPassphrase();

                MessageDecryptor messageDecryptor = null;
                try {
                    messageDecryptor = new MessageDecryptor(userNumber, passphrase, connection);
                } catch (SQLException ex) {
                    JOptionPaneNewCustom.showException(this, ex);
                    return false;
                }
                
//                PassphraseRecoveryTransfert passphraseRecoveryTransfert = new PassphraseRecoveryTransfert(connection, userNumber, passphrase);
//                PassphraseRecoveryLocal passphraseRecoveryLocal = passphraseRecoveryTransfert.get();
//
//                boolean passphraseRecoveryIsOn = false;
//
//                if (passphraseRecoveryLocal != null)
//                {
//                    if(passphraseRecoveryLocal.isUse_hint() || passphraseRecoveryLocal.isUse_passphrase_recovery()){
//                        passphraseRecoveryIsOn = true;
//                    }
//                }

                PgpKeyPairLocal pgpKeyPairLocal = messageDecryptor.getKeyPair();
                String privateKeyPgpBlock = pgpKeyPairLocal.getPrivateKeyPgpBlock();

                InputStream privKeyRing = new ByteArrayInputStream(privateKeyPgpBlock.getBytes());
                OutputStream newPrivKeyRing = new ByteArrayOutputStream();

                KeyHandlerOne kh = new KeyHandlerOne();
                kh.changePassphrase(privKeyRing, newPrivKeyRing, passphrase, newPassphrase);
                byte[] privKey = ((ByteArrayOutputStream) newPrivKeyRing).toByteArray();
                newPrivKeyRing.close();
                ByteArrayInputStream inKeyRing = new ByteArrayInputStream(privKey);
                privateKeyPgpBlock = kh.getAscPgpPrivKey(safeShareMain.getKeyId(), inKeyRing);
                                
                String hashPass 
                    = PassphraseUtil.computeHashAndSaltedPassphrase(safeShareMain.getKeyId(), 
                                                                    newPassphrase);
                                
//                if(passphraseRecoveryIsOn){
//                    if(passphraseRecoveryLocal.isUse_passphrase_recovery()){
//                        passphraseRecoveryLocal.setPassphrase_encrypted(new String(newPassphrase));
//                    }
//                }

                AwakeConnection awakeConnection = (AwakeConnection) connection;
                AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

                awakeFileSession.call("net.safester.server.PassphraseUpdater.update",
                        userNumber,
                        safeShareMain.getKeyId(),
                        privateKeyPgpBlock,
                        hashPass);

                safeShareMain.setUserPassphrase(newPassphrase);

//                if(passphraseRecoveryIsOn){
//                    passphraseRecoveryTransfert.put(passphraseRecoveryLocal);
//                }
                
                messageDecryptor.reload();

                return true;
                
            } catch (Exception e) {

                e.printStackTrace();
                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(ChangePassphrase.class.getName()).log(Level.SEVERE, null, ex);
                }

                this.setCursor(Cursor.getDefaultCursor());

                // Show the Exception
                JOptionPaneNewCustom.showException(this, e);
            }

        }

        return false;
    }

    private void passwordKeyPressed(java.awt.event.KeyEvent evt) {
        char[] cPassphrase = jPassword.getPassword();

        if (cPassphrase.length == 0) {
            jLabelQualityText.setText(" ");
            jLabelQualityText.setBackground(Color.GRAY);
            jLabelQualityText.setForeground(Color.BLACK);
        } else {
            jLabelQualityText.setOpaque(true);
            jLabelQualityText.setForeground(Color.BLACK);

            String qualityMsg = "";

            int passphrase_quality = Register.getPassphraseQuality(jPassword.getPassword());

            if (passphrase_quality == Register.QUALITY_TOO_SHORT) {
                qualityMsg = messages.getMessage("reg_pass_quality_too_short");
                qualityMsg += Register.BLANKS_TOO_SHORT;
                jLabelQualityText.setText(qualityMsg);
                jLabelQualityText.setBackground(Color.BLACK);
                jLabelQualityText.setForeground(Color.WHITE);

            } else if (passphrase_quality == Register.QUALITY_WEAK) {
                qualityMsg = messages.getMessage("reg_pass_quality_weak");
                qualityMsg += Register.BLANKS_WEAK;
                jLabelQualityText.setText(qualityMsg);
                jLabelQualityText.setBackground(Color.RED);
            } else if (passphrase_quality == Register.QUALITY_MEDIUM) {
                qualityMsg = messages.getMessage("reg_pass_quality_medium");
                qualityMsg += Register.BLANKS_MEDIUM;
                jLabelQualityText.setText(qualityMsg);
                jLabelQualityText.setBackground(Color.YELLOW);
            } else if (passphrase_quality == Register.QUALITY_STRONG) {
                qualityMsg = messages.getMessage("reg_pass_quality_strong");
                qualityMsg += Register.BLANKS_STRONG;
                jLabelQualityText.setText(qualityMsg);
                jLabelQualityText.setBackground(Color.GREEN);
            } else {
                throw new IllegalArgumentException("Unknown quality: " + passphrase_quality);
            }
        }

        this_keyPressed(evt);
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
        jPanelSepBlank = new javax.swing.JPanel();
        jPaneOldPassphrase = new javax.swing.JPanel();
        jPanelLeft1 = new javax.swing.JPanel();
        jLabelPassphraseOld = new javax.swing.JLabel();
        jPanelSepFromGrid = new javax.swing.JPanel();
        jPaneRight1 = new javax.swing.JPanel();
        jPasswordOld = new javax.swing.JPasswordField();
        jPanelSepBlank1 = new javax.swing.JPanel();
        jPanelPassphrase = new javax.swing.JPanel();
        jPanelLeft2 = new javax.swing.JPanel();
        jLabelPassphrase = new javax.swing.JLabel();
        jPanelSepFromGrid1 = new javax.swing.JPanel();
        jPaneRight2 = new javax.swing.JPanel();
        jPassword = new javax.swing.JPasswordField();
        jPanelPassphraseRetype = new javax.swing.JPanel();
        jPanelLeft3 = new javax.swing.JPanel();
        jLabelRetypePassphrase = new javax.swing.JLabel();
        jPanelSepFromGrid2 = new javax.swing.JPanel();
        jPaneRight3 = new javax.swing.JPanel();
        jPassword1 = new javax.swing.JPasswordField();
        jPanelHideTypingNew = new javax.swing.JPanel();
        jLabelHideTyping = new javax.swing.JLabel();
        jCheckBoxHideTyping = new javax.swing.JCheckBox();
        jLabelKeyboardWarning = new javax.swing.JLabel();
        jPanelPassphraseQualityNew = new javax.swing.JPanel();
        jButtonPassphraseQuality = new javax.swing.JButton();
        jLabelQualityText = new javax.swing.JLabel();
        jPanelSepLine1 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanelSouth = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

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

        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/key_edit.png"))); // NOI18N
        jLabelTitle.setText("jLabel1");
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

        jPanelSepBlank.setMaximumSize(new java.awt.Dimension(10, 14));
        jPanelSepBlank.setMinimumSize(new java.awt.Dimension(10, 14));
        jPanelSepBlank.setPreferredSize(new java.awt.Dimension(10, 14));
        jPanelSepBlank.setLayout(new javax.swing.BoxLayout(jPanelSepBlank, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenter.add(jPanelSepBlank);

        jPaneOldPassphrase.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPaneOldPassphrase.setMinimumSize(new java.awt.Dimension(10, 32));
        jPaneOldPassphrase.setPreferredSize(new java.awt.Dimension(10, 32));
        jPaneOldPassphrase.setLayout(new javax.swing.BoxLayout(jPaneOldPassphrase, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft1.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelLeft1.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelLeft1.setPreferredSize(new java.awt.Dimension(170, 31));
        jPanelLeft1.setLayout(new java.awt.GridLayout(1, 0));

        jLabelPassphraseOld.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelPassphraseOld.setText("jLabelPassphraseOld");
        jPanelLeft1.add(jLabelPassphraseOld);

        jPaneOldPassphrase.add(jPanelLeft1);

        jPanelSepFromGrid.setMaximumSize(new java.awt.Dimension(5, 31));
        jPanelSepFromGrid.setMinimumSize(new java.awt.Dimension(5, 31));
        jPanelSepFromGrid.setPreferredSize(new java.awt.Dimension(5, 31));
        jPaneOldPassphrase.add(jPanelSepFromGrid);

        jPaneRight1.setMaximumSize(new java.awt.Dimension(2147483647, 31));
        jPaneRight1.setMinimumSize(new java.awt.Dimension(6, 31));
        jPaneRight1.setPreferredSize(new java.awt.Dimension(295, 31));
        jPaneRight1.setLayout(new javax.swing.BoxLayout(jPaneRight1, javax.swing.BoxLayout.LINE_AXIS));

        jPasswordOld.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jPasswordOld.setMinimumSize(new java.awt.Dimension(8, 22));
        jPasswordOld.setPreferredSize(new java.awt.Dimension(300, 22));
        jPaneRight1.add(jPasswordOld);

        jPaneOldPassphrase.add(jPaneRight1);

        jPanelCenter.add(jPaneOldPassphrase);

        jPanelSepBlank1.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepBlank1.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank1.setPreferredSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank1.setLayout(new javax.swing.BoxLayout(jPanelSepBlank1, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenter.add(jPanelSepBlank1);

        jPanelPassphrase.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPanelPassphrase.setMinimumSize(new java.awt.Dimension(10, 32));
        jPanelPassphrase.setPreferredSize(new java.awt.Dimension(470, 32));
        jPanelPassphrase.setRequestFocusEnabled(false);
        jPanelPassphrase.setLayout(new javax.swing.BoxLayout(jPanelPassphrase, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft2.setMaximumSize(new java.awt.Dimension(32767, 31));
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

        jPassword.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jPassword.setMinimumSize(new java.awt.Dimension(8, 22));
        jPassword.setPreferredSize(new java.awt.Dimension(300, 22));
        jPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPasswordKeyPressed(evt);
            }
        });
        jPaneRight2.add(jPassword);

        jPanelPassphrase.add(jPaneRight2);

        jPanelCenter.add(jPanelPassphrase);

        jPanelPassphraseRetype.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPanelPassphraseRetype.setMinimumSize(new java.awt.Dimension(10, 32));
        jPanelPassphraseRetype.setPreferredSize(new java.awt.Dimension(470, 32));
        jPanelPassphraseRetype.setLayout(new javax.swing.BoxLayout(jPanelPassphraseRetype, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft3.setMaximumSize(new java.awt.Dimension(170, 31));
        jPanelLeft3.setMinimumSize(new java.awt.Dimension(170, 31));
        jPanelLeft3.setPreferredSize(new java.awt.Dimension(170, 31));
        jPanelLeft3.setLayout(new java.awt.GridLayout(1, 0));

        jLabelRetypePassphrase.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelRetypePassphrase.setText("jLabelRetypePassphrase");
        jPanelLeft3.add(jLabelRetypePassphrase);

        jPanelPassphraseRetype.add(jPanelLeft3);

        jPanelSepFromGrid2.setMaximumSize(new java.awt.Dimension(5, 31));
        jPanelSepFromGrid2.setMinimumSize(new java.awt.Dimension(5, 31));
        jPanelSepFromGrid2.setPreferredSize(new java.awt.Dimension(5, 31));
        jPanelPassphraseRetype.add(jPanelSepFromGrid2);

        jPaneRight3.setMaximumSize(new java.awt.Dimension(2147483647, 31));
        jPaneRight3.setMinimumSize(new java.awt.Dimension(6, 31));
        jPaneRight3.setPreferredSize(new java.awt.Dimension(295, 31));
        jPaneRight3.setLayout(new javax.swing.BoxLayout(jPaneRight3, javax.swing.BoxLayout.LINE_AXIS));

        jPassword1.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jPassword1.setMinimumSize(new java.awt.Dimension(8, 22));
        jPassword1.setPreferredSize(new java.awt.Dimension(300, 22));
        jPaneRight3.add(jPassword1);

        jPanelPassphraseRetype.add(jPaneRight3);

        jPanelCenter.add(jPanelPassphraseRetype);

        jPanelHideTypingNew.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelHideTypingNew.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelHideTypingNew.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelHideTypingNew.setRequestFocusEnabled(false);
        jPanelHideTypingNew.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelHideTyping.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelHideTyping.setText("jLabelHideTyping");
        jLabelHideTyping.setPreferredSize(new java.awt.Dimension(165, 16));
        jPanelHideTypingNew.add(jLabelHideTyping);

        jCheckBoxHideTyping.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jCheckBoxHideTyping.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jCheckBoxHideTyping.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxHideTyping.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBoxHideTypingStateChanged(evt);
            }
        });
        jCheckBoxHideTyping.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxHideTypingItemStateChanged(evt);
            }
        });
        jPanelHideTypingNew.add(jCheckBoxHideTyping);

        jLabelKeyboardWarning.setText("jLabelKeyboardWarning");
        jPanelHideTypingNew.add(jLabelKeyboardWarning);

        jPanelCenter.add(jPanelHideTypingNew);

        jPanelPassphraseQualityNew.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelPassphraseQualityNew.setMinimumSize(new java.awt.Dimension(400, 31));
        jPanelPassphraseQualityNew.setPreferredSize(new java.awt.Dimension(400, 31));
        jPanelPassphraseQualityNew.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jButtonPassphraseQuality.setForeground(new java.awt.Color(0, 0, 255));
        jButtonPassphraseQuality.setText("jButtonPassphraseQuality");
        jButtonPassphraseQuality.setBorderPainted(false);
        jButtonPassphraseQuality.setContentAreaFilled(false);
        jButtonPassphraseQuality.setFocusPainted(false);
        jButtonPassphraseQuality.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jButtonPassphraseQuality.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPassphraseQuality.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonPassphraseQuality.setPreferredSize(new java.awt.Dimension(167, 22));
        jButtonPassphraseQuality.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonPassphraseQualityMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonPassphraseQualityMouseExited(evt);
            }
        });
        jButtonPassphraseQuality.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPassphraseQualityActionPerformed(evt);
            }
        });
        jPanelPassphraseQualityNew.add(jButtonPassphraseQuality);

        jLabelQualityText.setText("jLabelQualityText");
        jLabelQualityText.setMaximumSize(new java.awt.Dimension(200, 16));
        jLabelQualityText.setMinimumSize(new java.awt.Dimension(200, 16));
        jLabelQualityText.setPreferredSize(new java.awt.Dimension(200, 16));
        jPanelPassphraseQualityNew.add(jLabelQualityText);

        jPanelCenter.add(jPanelPassphraseQualityNew);

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

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed

        String errorMsg = messages.getMessage("error");
        
        if (!arePassphraseEqual()) {
            JOptionPane.showMessageDialog(this,
                    messages.getMessage("passphrase_different"), errorMsg, JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        boolean done = doIt();
        this.setCursor(Cursor.getDefaultCursor());

        if (done)
        {
            JOptionPane.showMessageDialog(this,
                    messages.getMessage("passphrase_successfully_changed"));
            this.dispose();
        }
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordKeyPressed
        passwordKeyPressed(evt);
    }//GEN-LAST:event_jPasswordKeyPressed

    private void jCheckBoxHideTypingStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBoxHideTypingStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxHideTypingStateChanged

    private void jCheckBoxHideTypingItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxHideTypingItemStateChanged
        if ((evt.getStateChange() == ItemEvent.SELECTED)) {
            this.jPasswordOld.setEchoChar(this.defaultEchocar);
            this.jPassword.setEchoChar(this.defaultEchocar);
            this.jPassword1.setEchoChar(this.defaultEchocar);
        } else {
            // Display  chars
            this.jPasswordOld.setEchoChar((char) 0);
            this.jPassword.setEchoChar((char) 0);
            this.jPassword1.setEchoChar((char) 0);
        }
    }//GEN-LAST:event_jCheckBoxHideTypingItemStateChanged

    private void jButtonPassphraseQualityMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonPassphraseQualityMouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ButtonUrlOver.enter(evt);
    }//GEN-LAST:event_jButtonPassphraseQualityMouseEntered

    private void jButtonPassphraseQualityMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonPassphraseQualityMouseExited
        this.setCursor(Cursor.getDefaultCursor());
        ButtonUrlOver.exit(evt);
    }//GEN-LAST:event_jButtonPassphraseQualityMouseExited

    private void jButtonPassphraseQualityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPassphraseQualityActionPerformed
        
        String content = HtmlTextUtil.getHtmlHelpContent("help_register_4");
        new NewsFrame((JFrame) this.caller, content, messages.getMessage("change_passphrase"));
    }//GEN-LAST:event_jButtonPassphraseQualityActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

            try {
                //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                
                //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

            } catch (Exception ex) {
                System.out.println("Failed loading L&F: ");
                System.out.println(ex);
            }

                ChangePassphrase dialog = new ChangePassphrase(new javax.swing.JFrame(), null, -1, false);
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
    private javax.swing.JButton jButtonPassphraseQuality;
    private javax.swing.JCheckBox jCheckBoxHideTyping;
    private javax.swing.JLabel jLabelHideTyping;
    private javax.swing.JLabel jLabelKeyboardWarning;
    private javax.swing.JLabel jLabelPassphrase;
    private javax.swing.JLabel jLabelPassphraseOld;
    private javax.swing.JLabel jLabelQualityText;
    private javax.swing.JLabel jLabelRetypePassphrase;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPaneOldPassphrase;
    private javax.swing.JPanel jPaneRight1;
    private javax.swing.JPanel jPaneRight2;
    private javax.swing.JPanel jPaneRight3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelHideTypingNew;
    private javax.swing.JPanel jPanelLeft1;
    private javax.swing.JPanel jPanelLeft2;
    private javax.swing.JPanel jPanelLeft3;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelPassphrase;
    private javax.swing.JPanel jPanelPassphraseQualityNew;
    private javax.swing.JPanel jPanelPassphraseRetype;
    private javax.swing.JPanel jPanelSepBlank;
    private javax.swing.JPanel jPanelSepBlank1;
    private javax.swing.JPanel jPanelSepFromGrid;
    private javax.swing.JPanel jPanelSepFromGrid1;
    private javax.swing.JPanel jPanelSepFromGrid2;
    private javax.swing.JPanel jPanelSepLine;
    private javax.swing.JPanel jPanelSepLine1;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelTitle;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JPasswordField jPassword;
    private javax.swing.JPasswordField jPassword1;
    private javax.swing.JPasswordField jPasswordOld;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    // End of variables declaration//GEN-END:variables
}
