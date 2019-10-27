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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.lang3.SystemUtils;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.awakefw.sql.api.client.AwakeConnection;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPException;

import com.kawansoft.httpclient.KawanHttpClient;
import com.keyoti.rapidSpell.LanguageType;
import com.safelogic.pgp.api.KeyHandlerOne;
import com.safelogic.pgp.api.PgeepPrivateKey;
import com.safelogic.pgp.api.util.crypto.Sha1;
import com.safelogic.utilx.syntax.EmailChecker;
import com.swing.util.SwingUtil;

import net.safester.application.http.ApiCoupon;
import net.safester.application.http.KawanHttpClientBuilder;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.parms.StoreParms;
import net.safester.application.parms.SubscriptionLocalStore;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.UserPrefManager;
import net.safester.clientserver.MessageTransfer;
import net.safester.clientserver.PgpKeyPairLocal;
import net.safester.clientserver.UserSettingsExtractor;
import net.safester.clientserver.holder.PgpKeyPairHolder;
import net.safester.clientserver.holder.TheUserSettingsHolder;
import net.safester.noobs.clientserver.GsonUtil;
import net.safester.noobs.clientserver.UserSettingsLocal;

public class UserSettingsUpdater extends javax.swing.JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 263554947931665545L;
    	
    private ClipboardManager clipboardManager;
    private MessagesManager messages = new MessagesManager();
    private Connection connection;
    private JFrame parent;
    private JFrame thisOne;
    private int userNumber;
    private String keyId;

    private String accountTypeName = null;
    private SignatureFrame signatureFrame = null;
    private SoundChooser soundChooser = null;

    /** Creates new form SafeShareItSettings */
    public UserSettingsUpdater(JFrame jFrame, Connection theConnection, int theUserNumber, String keyId) {
        initComponents();
        parent = jFrame;
        
        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();
        
        userNumber = theUserNumber;
        this.keyId = keyId;
        thisOne = this;
        initCompany();
    }

    private void initCompany() {
        
        clipboardManager = new ClipboardManager(rootPane);
        this.setTitle(messages.getMessage("user_settings"));
        
        this.jTextFieldAccount.setText(null);
        this.jTextFieldCryptoSettings.setText(null);
        this.jTextFieldEmail.setText(null);
        this.jTextFieldStorage.setText(null);
        this.jTextFieldUserName.setText(null);
                
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());

        this.jLabelTitle.setText(this.getTitle());
        this.jLabelAccount.setText(messages.getMessage("account"));
        this.jLabelName.setText(messages.getMessage("user_name"));
        this.jLabelCryptoSettings.setText(messages.getMessage("cryptography_settings"));

        this.jLabelStorage.setText(messages.getMessage("storage_info"));            
        this.jLabelFontSizeBody.setText(messages.getMessage("font_size_for_reading_message_body"));
        
        jLabelCoupon.setText(messages.getMessage("coupon"));
        jLabelCouponHelp .setText(messages.getMessage("optional") + " ");

        /*
        if (UI_Util.isNimbus())
        {
            this.jLabelFontSizeBody.setMinimumSize(new Dimension(224, 14));
            this.jLabelFontSizeBody.setPreferredSize(new Dimension(224, 14));
            this.jPanelNameAndEmail.setMaximumSize(new Dimension(2147483647, 455));
        }
        */
        this.jLabelSpellCheckDefaultLanguage.setText(messages.getMessage("spell_check_default_language"));
        this.jCheckBoxHideDecrypDialog.setText(messages.getMessage("hide_decrypting_progess_bar"));
        this.jCheckBoxHideEncryptionDiscardableWindow.setText(messages.getMessage("hide_encrypted_warning_on_send"));
        this.jLabelNbMessagesPerPage.setText(messages.getMessage("nb_message_per_page"));
        this.jCheckBoxSendNotifyEmail.setText(messages.getMessage("activate_notification"));
        this.jCheckBoxSendAnonymousNotifications.setText(messages.getMessage("send_anonymous_notifications"));
        this.jButtonSpellCheckOptions.setText(messages.getMessage("spell_check_options"));

        this.jComboSpellCheckDefaulltLanguage.addItem(messages.getMessage(messages.getMessage("english")));
        this.jComboSpellCheckDefaulltLanguage.addItem(messages.getMessage(messages.getMessage("french")));     
        
        jLabelNotify.setText(MessagesManager.get("new_messages_notification"));
        jCheckBoxSendNotifyEmail.setText(MessagesManager.get("send_notification_email_to"));
        jCheckBoxPopUpOnTaskbar.setText(MessagesManager.get("pop_up_message_on_taskbar"));
        jCheckBoxPlaySound.setText(MessagesManager.get("play_a_sound"));
        jButtonSelectSound.setText(MessagesManager.get("sound_picker"));
        
        jLabelAccountInfo.setText(messages.getMessage("acount_info"));
        jCheckBoxInsertSignature.setText(messages.getMessage("add_a_signature"));
        
        jLabelEmailPref.setText(messages.getMessage("mail_preferences"));
        jLabelSpellCheck.setText(messages.getMessage("spell_check"));
        
        this.jButtonSignature.setText(messages.getMessage("signature"));
        this.jButtonSignature.setToolTipText(messages.getMessage("add_a_signature_tooltip"));
                
        int defaultLanguage = LanguageType.ENGLISH;
        if (Locale.getDefault().getLanguage().equals("fr"))
        {
            defaultLanguage = LanguageType.FRENCH;
        }
        
        String sLanguage = UserPrefManager.getPreference(UserPrefManager.SPELL_CHECK_LANGUAGE);
        if(sLanguage != null){
            try{
                defaultLanguage = Integer.parseInt(sLanguage);
            }
            catch(NumberFormatException e){
                //Do nothing leave english as default language
            }
        }

        if(defaultLanguage == LanguageType.FRENCH){
            this.jComboSpellCheckDefaulltLanguage.setSelectedItem(messages.getMessage("french"));
        }else{
            this.jComboSpellCheckDefaulltLanguage.setSelectedItem(messages.getMessage("english"));
        }

        jCheckBoxInsertSignatureItemStateChanged(null);
        jCheckBoxPlaySoundStateChanged(null);
        
        String small = messages.getMessage(messages.getMessage("small"));
        String medium = messages.getMessage(messages.getMessage("medium"));
        String big = messages.getMessage(messages.getMessage("big"));

        String [] bodyFontSizeArray = {small, medium, big};
        jComboFontSizeBody.setModel(new DefaultComboBoxModel(bodyFontSizeArray));
        String bodyFontSize = UserPrefManager.getPreference(UserPrefManager.FONT_SIZE_BODY);
        if (bodyFontSize == null) bodyFontSize = medium;
        jComboFontSizeBody.setSelectedItem(bodyFontSize);

        Integer [] values = { 10, 25, 50, 100 };
        jComboNbMessagesPerPage.setModel(new DefaultComboBoxModel(values));
        int nbMessagesPerPage = UserPrefManager.getIntegerPreference(UserPrefManager.NB_MESSAGES_PER_PAGE);
        if (nbMessagesPerPage == 0)
        {
            nbMessagesPerPage = Parms.DEFAULT_NB_MESSAGES_PER_PAGE;
        }
        jComboNbMessagesPerPage.setSelectedItem(nbMessagesPerPage);

        jButtonSpellCheckOptions.putClientProperty( "JButton.buttonType", "square" );
                
        jButtonOk.setText(messages.getMessage("ok"));
        jButtonCancel.setText(messages.getMessage("cancel"));

        if (SystemUtils.IS_OS_MAC_OSX)
        {
            jLabelFontSizeBody.setVisible(false);
            jComboFontSizeBody.setVisible(false);
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               loadData();
            }
        });
        
        ButtonResizer br = new ButtonResizer(jPanelSouth);
        br.setWidthToMax();
        this.setLocationByPlatform(true);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                WindowSettingManager.save(thisOne);
            }
        });
        
        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentMoved(ComponentEvent e)
            {
               WindowSettingManager.save(thisOne);
            }
            
            @Override
            public void componentResized(ComponentEvent e)
            {
                WindowSettingManager.save(thisOne);
            }

        });
        
        this.jTextFieldUserName.requestFocus();
               
        this.setLocationRelativeTo(parent);
        this.setSize(new Dimension(575, 717));
       
        SwingUtil.resizeJComponentsForNimbusAndMacOsX(rootPane);
        
        WindowSettingManager.load(this);
        
    }

   /**
     * Setsthe formatted storage info fo the user
     * @throws SQL
     */
    private void setStorageInfo() throws SQLException
    {
        long actualStore = MessageTransfer.getTotalMailboxSize(connection, userNumber);        
        long maxStore = StoreParms.getStorageForSubscription(SubscriptionLocalStore.getSubscription());
        
//        if (maxStore == 0)
//        {
//            jTextFieldStorage.setText(messages.getMessage("unlimited_reception_only"));
//            return;
//        }
        
        actualStore = actualStore / Parms.MO;
        maxStore = maxStore / Parms.MO;

        Long percent = actualStore * 100 / maxStore;

        if (percent > 90)
        {
            jTextFieldStorage.setForeground(Color.red);
        }
        else if (percent > 60)
        {
            jTextFieldStorage.setForeground(Color.orange);
        }
        else
        {
            // Nothing : use default color
        }

        String message = messages.getMessage("storage");
        message = MessageFormat.format(message, actualStore, maxStore, percent);

        jTextFieldStorage.setText(message);

    }
    
    public void setCryptographicSettingsLabel() {
        jLabelCryptoSettings.setText( messages.getMessage("cryptography_settings"));

        String algoAsym = null;
        int asymKeyLength = 0;
        String algoSymmetric = null;
        
        String cryptoSettings = algoAsym + " - " + asymKeyLength + " bits / " + algoSymmetric;
        jTextFieldCryptoSettings.setText(cryptoSettings);
    }
    
    /**
     * Load user settings from remote SQL and display it in window
     */
    private void loadData() {
        try {
            UserSettingsExtractor userSettingsExtractor = new UserSettingsExtractor(connection, userNumber);
            UserSettingsLocal userSettingsLocal = userSettingsExtractor.get();

            accountTypeName = StoreParms.getProductNameForSubscription(SubscriptionLocalStore.getSubscription());
            accountTypeName = " (" + accountTypeName + ")";
        
            jTextFieldAccount.setText(keyId + accountTypeName);
            jTextFieldUserName.setText(userSettingsLocal.getUserName());
            jTextFieldEmail.setText(userSettingsLocal.getNotificationEmail());

            setStorageInfo();

            displayCryptographySettings();
            jTextFieldCoupon.setText(getCoupon());

            jCheckBoxSendNotifyEmail.setSelected(userSettingsLocal.isNotificationOn());
            jCheckBoxSendNotifyEmailStateChanged(null);

            jCheckBoxSendAnonymousNotifications.setSelected(userSettingsLocal.getSend_anonymous_notification_on());

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPaneNewCustom.showException(this, ex);
        }

        boolean popUpOnTaskbar = false;
        
        if (CryptTray.isSupported()) {
            popUpOnTaskbar = !UserPrefManager.getBooleanPreference(UserPrefManager.NOTIFY_NO_POPUP_ON_TASKBAR);
            jCheckBoxPopUpOnTaskbar.setSelected(popUpOnTaskbar);        
        }
        else {
            jCheckBoxPopUpOnTaskbar.setSelected(false);
            jCheckBoxPopUpOnTaskbar.setEnabled(false);
        }
                
        boolean playSound = ! UserPrefManager.getBooleanPreference(UserPrefManager.NOTIFY_NO_PLAY_SOUND);
        jCheckBoxPlaySound.setSelected(playSound);
        
        boolean hideDecryptingDialog = UserPrefManager.getBooleanPreference(UserPrefManager.HIDE_DECRYPTING_DIALOG);
        jCheckBoxHideDecrypDialog.setSelected(hideDecryptingDialog);

        boolean hideEncryptionDiscardableWindow = UserPrefManager.getBooleanPreference(UserPrefManager.HIDE_ENCRYPTION_DISCARDABLE_WINDOW);
        jCheckBoxHideEncryptionDiscardableWindow.setSelected(hideEncryptionDiscardableWindow);
        
        boolean insertSignature = UserPrefManager.getBooleanPreference(UserPrefManager.INSERT_SIGNATURE);
        jCheckBoxInsertSignature.setSelected(insertSignature);
        
        UserPrefManager userPreferencesManager = new UserPrefManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());

        String message = messages.getMessage("message_encrypted");
        Sha1 hashcode = new Sha1();
        String hashMessage = null;

        try {
            hashMessage = hashcode.getHexHash(message.getBytes());
        } catch (Exception e1) {
            e1.printStackTrace();
            hashMessage = "";
        }

        this.keyListenerAdder();

        String strDiscardMessage = prefs.get(hashMessage, Boolean.toString(false));
        
        if (strDiscardMessage.equalsIgnoreCase("true")) {
            jCheckBoxHideEncryptionDiscardableWindow.setSelected(true);
        } else {
            jCheckBoxHideEncryptionDiscardableWindow.setSelected(false);
        }
    }

    /**
     * Displays the key settings (next version will be better with the symmetric key)
     * @throws SQLException
     */
    private void displayCryptographySettings() throws SQLException, IOException, Exception {
                        
        PgpKeyPairHolder pgpKeyPairHolder = new PgpKeyPairHolder(connection, userNumber);
        PgpKeyPairLocal pgpKeyPairLocal = pgpKeyPairHolder.get();

        String symmetricAlgorithmName = getSymmetricAlgorithm(pgpKeyPairLocal);

        String cryptoSettings =
                pgpKeyPairLocal.getKeyType() + " - " + pgpKeyPairLocal.getKeyLength() + " bits "
                + " / " + 
                symmetricAlgorithmName + " bits ";

        this.jTextFieldCryptoSettings.setText(cryptoSettings);
    }

    
    private String getCoupon() throws Exception {
        AwakeConnection awakeConnection = (AwakeConnection)connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
        
        KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.buildFromAwakeConnection(awakeConnection);
        ApiCoupon apiCoupon = new ApiCoupon(kawanHttpClient, awakeFileSession.getUsername(),
                awakeFileSession.getAuthenticationToken());
       String coupon = apiCoupon.getCoupon();
       return coupon; 
    }
    
    private boolean storeCoupon(String coupon) throws Exception {
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.buildFromAwakeConnection(awakeConnection);
        ApiCoupon apiCoupon = new ApiCoupon(kawanHttpClient, awakeFileSession.getUsername(),
                awakeFileSession.getAuthenticationToken());
        
        boolean ok = apiCoupon.storeCoupon(coupon);
        return ok;
    }
   
   private String getSymmetricAlgorithm(PgpKeyPairLocal pgpKeyPairLocal) throws IOException, PGPException {
        // Extract the Symmetric Algorithm
        String privateKeyPgpBlock = pgpKeyPairLocal.getPrivateKeyPgpBlock();
        InputStream privKeyRing = new ByteArrayInputStream(privateKeyPgpBlock.getBytes());
        KeyHandlerOne kh = new KeyHandlerOne();
        PgeepPrivateKey pgeepPrivateKey = (PgeepPrivateKey) kh.getPgpPrivateKey(
                                            privKeyRing, this.keyId, null);
        int keyEncryptionAlgorithm = pgeepPrivateKey.getPGPSecretKey().getKeyEncryptionAlgorithm();
        String symmetricAlgorithmName = getSymmetricCipherName(keyEncryptionAlgorithm);
        return symmetricAlgorithmName;
    }

    private static String getSymmetricCipherName(
            int    algorithm)
        {
            switch (algorithm)
            {
            case SymmetricKeyAlgorithmTags.NULL:
                return null;
            case SymmetricKeyAlgorithmTags.TRIPLE_DES:
                return "DESEDE";
            case SymmetricKeyAlgorithmTags.IDEA:
                return "IDEA";
            case SymmetricKeyAlgorithmTags.CAST5:
                return "CAST5";
            case SymmetricKeyAlgorithmTags.BLOWFISH:
                return "Blowfish";
            case SymmetricKeyAlgorithmTags.SAFER:
                return "SAFER";
            case SymmetricKeyAlgorithmTags.DES:
                return "DES";
            case SymmetricKeyAlgorithmTags.AES_128:
                return "AES - 128";
            case SymmetricKeyAlgorithmTags.AES_192:
                return "AES - 192";
            case SymmetricKeyAlgorithmTags.AES_256:
                return "AES - 256";
            case SymmetricKeyAlgorithmTags.TWOFISH:
                return "Twofish";
            default:
                return "N/A";
            }
        }
    /**
     * Update user settings on remote sql with data entered by user
     */
    
    private void updateUserSettings() throws IllegalArgumentException {
        
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); 
        try {

            String email = jTextFieldEmail.getText().toLowerCase().trim();
            EmailChecker emailChecker = new EmailChecker(email);
            if(!emailChecker.isSyntaxValid()){
                String msg = MessageFormat.format(messages.getMessage("email_not_vaild"), email);
                JOptionPane.showMessageDialog(this, msg, msg, JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            TheUserSettingsHolder theUserSettingsHolder = new TheUserSettingsHolder(connection, userNumber);
            
            UserSettingsLocal userSettingsLocal = theUserSettingsHolder.get();
            userSettingsLocal.setUserNumber(userNumber);
            userSettingsLocal.setUserName(HtmlConverter.toHtml(this.jTextFieldUserName.getText()));
            userSettingsLocal.setNotificationEmail(this.jTextFieldEmail.getText());
            userSettingsLocal.setNotificationOn(this.jCheckBoxSendNotifyEmail.isSelected());
            userSettingsLocal.setSend_anonymous_notification_on(this.jCheckBoxSendAnonymousNotifications.isSelected());
            String signature = userSettingsLocal.getSignature();
            if (signature != null) {
                userSettingsLocal.setSignature(HtmlConverter.toHtml(signature));
            }

            AwakeConnection awakeConnection = (AwakeConnection) connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

            String jsonUserSetttings = GsonUtil.toGson(userSettingsLocal);
            
            awakeFileSession.call("net.safester.server.UserSettingsUpdater.update",
                    userNumber,
                    jsonUserSetttings,
                    connection);

            theUserSettingsHolder = new TheUserSettingsHolder(connection, userNumber);
            theUserSettingsHolder.reset();

            boolean isStored = storeCoupon(jTextFieldCoupon.getText());
            if (! isStored) {
                
                String msg = messages.getMessage("invalid_coupon_please_retry");
                JOptionPane.showMessageDialog(this, msg, msg, JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            this.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            this.setCursor(Cursor.getDefaultCursor()); 
            JOptionPaneNewCustom.showException(this, ex);
            return;
        }
        finally {
            this.setCursor(Cursor.getDefaultCursor());    
        }

        UserPrefManager.setPreference(UserPrefManager.FONT_SIZE_BODY,
                                      "" + jComboFontSizeBody.getSelectedItem());
        
        boolean popUpOnTaskbar = jCheckBoxPopUpOnTaskbar.isSelected();
        boolean playSound = jCheckBoxPlaySound.isSelected();
        UserPrefManager.setPreference(UserPrefManager.NOTIFY_NO_POPUP_ON_TASKBAR, ! popUpOnTaskbar);
        UserPrefManager.setPreference(UserPrefManager.NOTIFY_NO_PLAY_SOUND, !playSound);
        
        
        boolean hideDecryptingDialog = jCheckBoxHideDecrypDialog.isSelected();
        boolean hideEncryptionDiscardableWindow = jCheckBoxHideEncryptionDiscardableWindow.isSelected();

        UserPrefManager.setPreference(UserPrefManager.HIDE_DECRYPTING_DIALOG, hideDecryptingDialog);
        UserPrefManager.setPreference(UserPrefManager.INSERT_SIGNATURE, jCheckBoxInsertSignature.isSelected());
        
        int language = LanguageType.ENGLISH;
        if(jComboSpellCheckDefaulltLanguage.getSelectedItem().equals(messages.getMessage("french"))){
            language = LanguageType.FRENCH;
        }
        UserPrefManager.setPreference(UserPrefManager.SPELL_CHECK_LANGUAGE, "" + language);
        String hashMessage = null;
        Sha1 hashcode = new Sha1();
        MessagesManager messagesManager = new MessagesManager();
        String message = messagesManager.getMessage("message_encrypted");
        try
        {
            hashMessage = hashcode.getHexHash(message.getBytes());
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
            hashMessage = "";
        }

        UserPrefManager.setPreference(hashMessage, hideEncryptionDiscardableWindow);
        //UserPrefManager.setPreference(UserPrefManager.HIDE_ENCRYPTION_DISCARDABLE_WINDOW, hideEncryptionDiscardableWindow);
                        
        UserPrefManager userPreferencesManager = new UserPrefManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());

        UserPrefManager.setPreference(UserPrefManager.NB_MESSAGES_PER_PAGE,
                                            (Integer)jComboNbMessagesPerPage.getSelectedItem());
       
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

            if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F9) {
               JOptionPane.showMessageDialog(this, "User Number: " + userNumber);
            }
            
            // NO! Otw user can not hit CR/LF on signature pane!
            
//            if (keyCode == KeyEvent.VK_ENTER) {
//                updateUserSettings();
//            }
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
        jPanelTop = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelSepLine2 = new javax.swing.JPanel();
        jSeparator4 = new javax.swing.JSeparator();
        jPaneSepAccountInfo = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabelAccountInfo = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanelSep13 = new javax.swing.JPanel();
        jPanelNameAndEmail = new javax.swing.JPanel();
        jPanelAccountTop = new javax.swing.JPanel();
        jLabelAccount = new javax.swing.JLabel();
        jTextFieldAccount = new javax.swing.JTextField();
        jPanelCryptoSettings = new javax.swing.JPanel();
        jLabelCryptoSettings = new javax.swing.JLabel();
        jTextFieldCryptoSettings = new javax.swing.JTextField();
        jPanelCryptoSettings1 = new javax.swing.JPanel();
        jLabelStorage = new javax.swing.JLabel();
        jTextFieldStorage = new javax.swing.JTextField();
        jPanelNameNew = new javax.swing.JPanel();
        jLabelName = new javax.swing.JLabel();
        jTextFieldUserName = new javax.swing.JTextField();
        jPanelCoupon = new javax.swing.JPanel();
        jLabelCoupon = new javax.swing.JLabel();
        jTextFieldCoupon = new javax.swing.JTextField();
        jLabelCouponHelp = new javax.swing.JLabel();
        jPanelSep14 = new javax.swing.JPanel();
        jPaneSepNotify = new javax.swing.JPanel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabelNotify = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        jPanelEmailNew = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jCheckBoxSendNotifyEmail = new javax.swing.JCheckBox();
        jTextFieldEmail = new javax.swing.JTextField();
        jPanelNotificationOn = new javax.swing.JPanel();
        jPanelRight = new javax.swing.JPanel();
        jCheckBoxPopUpOnTaskbar = new javax.swing.JCheckBox();
        jCheckBoxPlaySound = new javax.swing.JCheckBox();
        jButtonSelectSound = new javax.swing.JButton();
        jPanelSepBlanc5 = new javax.swing.JPanel();
        jPaneSepEmailPref = new javax.swing.JPanel();
        jSeparator7 = new javax.swing.JSeparator();
        jLabelEmailPref = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jPanelCheckBox = new javax.swing.JPanel();
        jPanelHideAndAnon = new javax.swing.JPanel();
        jCheckBoxHideDecrypDialog = new javax.swing.JCheckBox();
        jCheckBoxSendAnonymousNotifications = new javax.swing.JCheckBox();
        jPanelHideEncryptDiscard = new javax.swing.JPanel();
        jCheckBoxHideEncryptionDiscardableWindow = new javax.swing.JCheckBox();
        jPanelHidensertSignature = new javax.swing.JPanel();
        jCheckBoxInsertSignature = new javax.swing.JCheckBox();
        jButtonSignature = new javax.swing.JButton();
        jPanelNbMessagesPerPage = new javax.swing.JPanel();
        jLabelNbMessagesPerPage = new javax.swing.JLabel();
        jComboNbMessagesPerPage = new javax.swing.JComboBox();
        jPanelSep1 = new javax.swing.JPanel();
        jLabelFontSizeBody = new javax.swing.JLabel();
        jComboFontSizeBody = new javax.swing.JComboBox();
        jPanelSepBlanc6 = new javax.swing.JPanel();
        jPaneSepSpellCheck = new javax.swing.JPanel();
        jSeparator9 = new javax.swing.JSeparator();
        jLabelSpellCheck = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JSeparator();
        jPanelSpellCheck = new javax.swing.JPanel();
        jLabelSpellCheckDefaultLanguage = new javax.swing.JLabel();
        jComboSpellCheckDefaulltLanguage = new javax.swing.JComboBox();
        jPanelSep2 = new javax.swing.JPanel();
        jButtonSpellCheckOptions = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jPanelSepBlank = new javax.swing.JPanel();
        jPanelSepLine = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelSepBlanc7 = new javax.swing.JPanel();
        jPanelSep15 = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanelButtonsLeft = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);
        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelTop.setMaximumSize(new java.awt.Dimension(32767, 42));
        jPanelTop.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/window_gear.png"))); // NOI18N
        jLabelTitle.setText("jLabelTitle");
        jPanelTop.add(jLabelTitle);

        jPanelCenter.add(jPanelTop);

        jPanelSepLine2.setLayout(new javax.swing.BoxLayout(jPanelSepLine2, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator4.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator4.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator4.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepLine2.add(jSeparator4);

        jPanelCenter.add(jPanelSepLine2);

        jPaneSepAccountInfo.setLayout(new javax.swing.BoxLayout(jPaneSepAccountInfo, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator1.setMaximumSize(new java.awt.Dimension(24, 6));
        jSeparator1.setMinimumSize(new java.awt.Dimension(24, 6));
        jSeparator1.setPreferredSize(new java.awt.Dimension(24, 6));
        jPaneSepAccountInfo.add(jSeparator1);

        jLabelAccountInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/24x24/id_card.png"))); // NOI18N
        jLabelAccountInfo.setText("Account Info");
        jLabelAccountInfo.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        jPaneSepAccountInfo.add(jLabelAccountInfo);

        jSeparator3.setMaximumSize(new java.awt.Dimension(27000, 6));
        jSeparator3.setPreferredSize(new java.awt.Dimension(27000, 6));
        jPaneSepAccountInfo.add(jSeparator3);

        jPanelCenter.add(jPaneSepAccountInfo);

        jPanelSep13.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSep13.setMinimumSize(new java.awt.Dimension(10, 5));
        jPanelSep13.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelCenter.add(jPanelSep13);

        jPanelNameAndEmail.setPreferredSize(new java.awt.Dimension(422, 208));
        jPanelNameAndEmail.setLayout(new javax.swing.BoxLayout(jPanelNameAndEmail, javax.swing.BoxLayout.Y_AXIS));

        jPanelAccountTop.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelAccountTop.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelAccountTop.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelAccountTop.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelAccount.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelAccount.setText("jLabelAccount");
        jLabelAccount.setPreferredSize(new java.awt.Dimension(150, 16));
        jPanelAccountTop.add(jLabelAccount);

        jTextFieldAccount.setEditable(false);
        jTextFieldAccount.setText("jTextFieldAccount");
        jTextFieldAccount.setMinimumSize(new java.awt.Dimension(20, 22));
        jTextFieldAccount.setPreferredSize(new java.awt.Dimension(300, 22));
        jPanelAccountTop.add(jTextFieldAccount);

        jPanelNameAndEmail.add(jPanelAccountTop);

        jPanelCryptoSettings.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelCryptoSettings.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelCryptoSettings.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelCryptoSettings.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelCryptoSettings.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelCryptoSettings.setText("jLabelCryptoSettings");
        jLabelCryptoSettings.setPreferredSize(new java.awt.Dimension(150, 16));
        jPanelCryptoSettings.add(jLabelCryptoSettings);

        jTextFieldCryptoSettings.setEditable(false);
        jTextFieldCryptoSettings.setText("jTextFieldCryptoSettings");
        jTextFieldCryptoSettings.setPreferredSize(new java.awt.Dimension(300, 22));
        jPanelCryptoSettings.add(jTextFieldCryptoSettings);

        jPanelNameAndEmail.add(jPanelCryptoSettings);

        jPanelCryptoSettings1.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelCryptoSettings1.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelCryptoSettings1.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelCryptoSettings1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelStorage.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelStorage.setText("jLabelStorage");
        jLabelStorage.setPreferredSize(new java.awt.Dimension(150, 16));
        jPanelCryptoSettings1.add(jLabelStorage);

        jTextFieldStorage.setEditable(false);
        jTextFieldStorage.setText("jTextFieldStorage");
        jTextFieldStorage.setPreferredSize(new java.awt.Dimension(300, 22));
        jPanelCryptoSettings1.add(jTextFieldStorage);

        jPanelNameAndEmail.add(jPanelCryptoSettings1);

        jPanelNameNew.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelNameNew.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelNameNew.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelNameNew.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelName.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelName.setText("jLabelName");
        jLabelName.setPreferredSize(new java.awt.Dimension(150, 16));
        jPanelNameNew.add(jLabelName);

        jTextFieldUserName.setText("jTextFieldUserName");
        jTextFieldUserName.setMinimumSize(new java.awt.Dimension(20, 22));
        jTextFieldUserName.setPreferredSize(new java.awt.Dimension(300, 22));
        jPanelNameNew.add(jTextFieldUserName);

        jPanelNameAndEmail.add(jPanelNameNew);

        jPanelCoupon.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelCoupon.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelCoupon.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelCoupon.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelCoupon.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelCoupon.setText("jLabelCoupon");
        jLabelCoupon.setPreferredSize(new java.awt.Dimension(150, 16));
        jPanelCoupon.add(jLabelCoupon);

        jTextFieldCoupon.setMinimumSize(new java.awt.Dimension(20, 22));
        jTextFieldCoupon.setPreferredSize(new java.awt.Dimension(170, 22));
        jPanelCoupon.add(jTextFieldCoupon);

        jLabelCouponHelp.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabelCouponHelp.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelCouponHelp.setText("jLabelOptionnal");
        jLabelCouponHelp.setPreferredSize(new java.awt.Dimension(200, 16));
        jPanelCoupon.add(jLabelCouponHelp);

        jPanelNameAndEmail.add(jPanelCoupon);

        jPanelCenter.add(jPanelNameAndEmail);

        jPanelSep14.setMaximumSize(new java.awt.Dimension(32767, 8));
        jPanelSep14.setMinimumSize(new java.awt.Dimension(10, 8));
        jPanelSep14.setPreferredSize(new java.awt.Dimension(10, 8));
        jPanelCenter.add(jPanelSep14);

        jPaneSepNotify.setLayout(new javax.swing.BoxLayout(jPaneSepNotify, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator5.setMaximumSize(new java.awt.Dimension(24, 6));
        jSeparator5.setMinimumSize(new java.awt.Dimension(24, 6));
        jSeparator5.setPreferredSize(new java.awt.Dimension(24, 6));
        jPaneSepNotify.add(jSeparator5);

        jLabelNotify.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/24x24/mail_into.png"))); // NOI18N
        jLabelNotify.setText("New Messages Notification");
        jLabelNotify.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        jPaneSepNotify.add(jLabelNotify);

        jSeparator6.setMaximumSize(new java.awt.Dimension(27000, 6));
        jSeparator6.setPreferredSize(new java.awt.Dimension(27000, 6));
        jPaneSepNotify.add(jSeparator6);

        jPanelCenter.add(jPaneSepNotify);

        jPanelEmailNew.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelEmailNew.setMinimumSize(new java.awt.Dimension(10, 31));
        jPanelEmailNew.setPreferredSize(new java.awt.Dimension(10, 31));
        jPanelEmailNew.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));

        jPanel4.setMaximumSize(new java.awt.Dimension(5, 10));
        jPanel4.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanel4.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));
        jPanelEmailNew.add(jPanel4);

        jCheckBoxSendNotifyEmail.setText("jCheckBoxSendNotifyEmail");
        jCheckBoxSendNotifyEmail.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBoxSendNotifyEmailStateChanged(evt);
            }
        });
        jPanelEmailNew.add(jCheckBoxSendNotifyEmail);

        jTextFieldEmail.setText("jTextFieldEmail");
        jTextFieldEmail.setMinimumSize(new java.awt.Dimension(20, 22));
        jTextFieldEmail.setPreferredSize(new java.awt.Dimension(280, 22));
        jPanelEmailNew.add(jTextFieldEmail);

        jPanelCenter.add(jPanelEmailNew);

        jPanelNotificationOn.setMaximumSize(new java.awt.Dimension(2147483647, 33));
        jPanelNotificationOn.setLayout(new javax.swing.BoxLayout(jPanelNotificationOn, javax.swing.BoxLayout.LINE_AXIS));

        jPanelRight.setMaximumSize(new java.awt.Dimension(2147483647, 31));
        jPanelRight.setPreferredSize(new java.awt.Dimension(264, 31));
        jPanelRight.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));

        jCheckBoxPopUpOnTaskbar.setText("jCheckBoxNotifyOnTaskbar");
        jPanelRight.add(jCheckBoxPopUpOnTaskbar);

        jCheckBoxPlaySound.setText("jCheckBoxPlaySound");
        jCheckBoxPlaySound.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBoxPlaySoundStateChanged(evt);
            }
        });
        jPanelRight.add(jCheckBoxPlaySound);

        jButtonSelectSound.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/window_equalizer.png"))); // NOI18N
        jButtonSelectSound.setText("jButtonSelectSound");
        jButtonSelectSound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectSoundActionPerformed(evt);
            }
        });
        jPanelRight.add(jButtonSelectSound);

        jPanelNotificationOn.add(jPanelRight);

        jPanelCenter.add(jPanelNotificationOn);

        jPanelSepBlanc5.setMaximumSize(new java.awt.Dimension(32767, 8));
        jPanelSepBlanc5.setMinimumSize(new java.awt.Dimension(10, 8));
        jPanelSepBlanc5.setPreferredSize(new java.awt.Dimension(10, 8));
        jPanelCenter.add(jPanelSepBlanc5);

        jPaneSepEmailPref.setLayout(new javax.swing.BoxLayout(jPaneSepEmailPref, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator7.setMaximumSize(new java.awt.Dimension(24, 6));
        jSeparator7.setMinimumSize(new java.awt.Dimension(24, 6));
        jSeparator7.setPreferredSize(new java.awt.Dimension(24, 6));
        jPaneSepEmailPref.add(jSeparator7);

        jLabelEmailPref.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/24x24/mails.png"))); // NOI18N
        jLabelEmailPref.setText("Mail Preferences");
        jLabelEmailPref.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        jPaneSepEmailPref.add(jLabelEmailPref);

        jSeparator8.setMaximumSize(new java.awt.Dimension(27000, 6));
        jSeparator8.setPreferredSize(new java.awt.Dimension(27000, 6));
        jPaneSepEmailPref.add(jSeparator8);

        jPanelCenter.add(jPaneSepEmailPref);

        jPanelCheckBox.setLayout(new javax.swing.BoxLayout(jPanelCheckBox, javax.swing.BoxLayout.Y_AXIS));

        jPanelHideAndAnon.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jCheckBoxHideDecrypDialog.setText("jCheckBoxHideDecryptDialog");
        jPanelHideAndAnon.add(jCheckBoxHideDecrypDialog);

        jCheckBoxSendAnonymousNotifications.setText("jCheckBoxSendAnonymousNotifications");
        jPanelHideAndAnon.add(jCheckBoxSendAnonymousNotifications);

        jPanelCheckBox.add(jPanelHideAndAnon);

        jPanelHideEncryptDiscard.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jCheckBoxHideEncryptionDiscardableWindow.setText("jCheckBoxHideEncryptionDiscardableWindow");
        jPanelHideEncryptDiscard.add(jCheckBoxHideEncryptionDiscardableWindow);

        jPanelCheckBox.add(jPanelHideEncryptDiscard);

        jPanelHidensertSignature.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jCheckBoxInsertSignature.setText("jCheckBoxInsertSignature");
        jCheckBoxInsertSignature.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxInsertSignatureItemStateChanged(evt);
            }
        });
        jPanelHidensertSignature.add(jCheckBoxInsertSignature);

        jButtonSignature.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/contract.png"))); // NOI18N
        jButtonSignature.setText("jButtonSignature");
        jButtonSignature.setToolTipText("");
        jButtonSignature.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSignatureActionPerformed(evt);
            }
        });
        jPanelHidensertSignature.add(jButtonSignature);

        jPanelCheckBox.add(jPanelHidensertSignature);

        jPanelNbMessagesPerPage.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelNbMessagesPerPage.setText("jLabelNbMessagesPerPage");
        jPanelNbMessagesPerPage.add(jLabelNbMessagesPerPage);
        jPanelNbMessagesPerPage.add(jComboNbMessagesPerPage);

        jPanelSep1.setMaximumSize(new java.awt.Dimension(5, 10));
        jPanelSep1.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelSep1.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelNbMessagesPerPage.add(jPanelSep1);

        jLabelFontSizeBody.setText("jLabelFontSizeBody");
        jPanelNbMessagesPerPage.add(jLabelFontSizeBody);
        jPanelNbMessagesPerPage.add(jComboFontSizeBody);

        jPanelCheckBox.add(jPanelNbMessagesPerPage);

        jPanelCenter.add(jPanelCheckBox);

        jPanelSepBlanc6.setMaximumSize(new java.awt.Dimension(32767, 8));
        jPanelSepBlanc6.setMinimumSize(new java.awt.Dimension(10, 8));
        jPanelSepBlanc6.setName(""); // NOI18N
        jPanelSepBlanc6.setPreferredSize(new java.awt.Dimension(10, 8));
        jPanelCenter.add(jPanelSepBlanc6);

        jPaneSepSpellCheck.setLayout(new javax.swing.BoxLayout(jPaneSepSpellCheck, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator9.setMaximumSize(new java.awt.Dimension(24, 6));
        jSeparator9.setMinimumSize(new java.awt.Dimension(24, 6));
        jSeparator9.setPreferredSize(new java.awt.Dimension(24, 6));
        jPaneSepSpellCheck.add(jSeparator9);

        jLabelSpellCheck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/24x24/spellcheck2.png"))); // NOI18N
        jLabelSpellCheck.setText("Spell Check");
        jLabelSpellCheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        jPaneSepSpellCheck.add(jLabelSpellCheck);

        jSeparator10.setMaximumSize(new java.awt.Dimension(27000, 6));
        jSeparator10.setPreferredSize(new java.awt.Dimension(27000, 6));
        jPaneSepSpellCheck.add(jSeparator10);

        jPanelCenter.add(jPaneSepSpellCheck);

        jPanelSpellCheck.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 5));

        jLabelSpellCheckDefaultLanguage.setText("jLabelSpellCheckDefaultLanguage");
        jPanelSpellCheck.add(jLabelSpellCheckDefaultLanguage);

        jComboSpellCheckDefaulltLanguage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboSpellCheckDefaulltLanguageActionPerformed(evt);
            }
        });
        jPanelSpellCheck.add(jComboSpellCheckDefaulltLanguage);

        jPanelSep2.setMaximumSize(new java.awt.Dimension(1, 10));
        jPanelSep2.setMinimumSize(new java.awt.Dimension(1, 10));
        jPanelSep2.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelSpellCheck.add(jPanelSep2);

        jButtonSpellCheckOptions.setForeground(new java.awt.Color(0, 0, 255));
        jButtonSpellCheckOptions.setText("jButtonSpellCheckOptions");
        jButtonSpellCheckOptions.setBorder(null);
        jButtonSpellCheckOptions.setBorderPainted(false);
        jButtonSpellCheckOptions.setContentAreaFilled(false);
        jButtonSpellCheckOptions.setFocusPainted(false);
        jButtonSpellCheckOptions.setMargin(new java.awt.Insets(2, 10, 2, 10));
        jButtonSpellCheckOptions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonSpellCheckOptionsMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonSpellCheckOptionsMouseExited(evt);
            }
        });
        jButtonSpellCheckOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSpellCheckOptionsActionPerformed(evt);
            }
        });
        jPanelSpellCheck.add(jButtonSpellCheckOptions);

        jPanelCenter.add(jPanelSpellCheck);

        jPanel13.setMaximumSize(new java.awt.Dimension(32767, 8));
        jPanel13.setMinimumSize(new java.awt.Dimension(10, 8));
        jPanel13.setPreferredSize(new java.awt.Dimension(10, 8));
        jPanelCenter.add(jPanel13);

        jPanelSepBlank.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepBlank.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank.setPreferredSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank.setLayout(new javax.swing.BoxLayout(jPanelSepBlank, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenter.add(jPanelSepBlank);

        jPanelSepLine.setMaximumSize(new java.awt.Dimension(32767, 6));
        jPanelSepLine.setMinimumSize(new java.awt.Dimension(0, 6));
        jPanelSepLine.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepLine.setLayout(new javax.swing.BoxLayout(jPanelSepLine, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator2.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator2.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator2.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepLine.add(jSeparator2);

        jPanelCenter.add(jPanelSepLine);

        jPanelSepBlanc7.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSepBlanc7.setMinimumSize(new java.awt.Dimension(10, 5));
        jPanelSepBlanc7.setPreferredSize(new java.awt.Dimension(1000, 5));
        jPanelCenter.add(jPanelSepBlanc7);

        jPanelSep15.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSep15.setMinimumSize(new java.awt.Dimension(10, 5));
        jPanelSep15.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelCenter.add(jPanelSep15);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setLayout(new javax.swing.BoxLayout(jPanelSouth, javax.swing.BoxLayout.LINE_AXIS));

        jPanel3.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel3.setMinimumSize(new java.awt.Dimension(10, 11));
        jPanel3.setPreferredSize(new java.awt.Dimension(10, 11));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSouth.add(jPanel3);

        jPanelButtonsLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 14));
        jPanelSouth.add(jPanelButtonsLeft);
        jPanelSouth.add(jPanel2);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonOk.setText("jButtonOk");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonOk);

        jButtonCancel.setText("jButtonCancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonCancel);

        jPanelSouth.add(jPanel1);

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

    private void jCheckBoxSendNotifyEmailStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBoxSendNotifyEmailStateChanged
        if(jCheckBoxSendNotifyEmail.isSelected())
        {
            jTextFieldEmail.setEnabled(true);
        }
        else
        {
            jTextFieldEmail.setEnabled(false);
        }
        this.repaint();
        
    }//GEN-LAST:event_jCheckBoxSendNotifyEmailStateChanged

    private void jButtonSpellCheckOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSpellCheckOptionsActionPerformed
       new SpellCheckSettings(this).setVisible(true);
    }//GEN-LAST:event_jButtonSpellCheckOptionsActionPerformed

    private void jButtonSpellCheckOptionsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSpellCheckOptionsMouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jButtonSpellCheckOptionsMouseEntered

    private void jButtonSpellCheckOptionsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSpellCheckOptionsMouseExited
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_jButtonSpellCheckOptionsMouseExited

    private void jButtonSignatureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSignatureActionPerformed
        if (signatureFrame != null) {
            signatureFrame.dispose();
        } 
        signatureFrame = new SignatureFrame(this, connection, userNumber, keyId);
        
    }//GEN-LAST:event_jButtonSignatureActionPerformed

    private void jCheckBoxInsertSignatureItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxInsertSignatureItemStateChanged
        if (jCheckBoxInsertSignature.isSelected()) {
            jButtonSignature.setEnabled(true);
        }
        else {
            jButtonSignature.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxInsertSignatureItemStateChanged

    private void jCheckBoxPlaySoundStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBoxPlaySoundStateChanged
        if(jCheckBoxPlaySound.isSelected())
        {
            jButtonSelectSound.setEnabled(true);
        }
        else
        {
            jButtonSelectSound.setEnabled(false);
        }
        this.repaint();
    }//GEN-LAST:event_jCheckBoxPlaySoundStateChanged

    private void jButtonSelectSoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectSoundActionPerformed
        
       if (soundChooser != null) {
           soundChooser.dispose();
       }

       soundChooser = new SoundChooser(this);
       soundChooser.setVisible(true);

    }//GEN-LAST:event_jButtonSelectSoundActionPerformed

    private void jComboSpellCheckDefaulltLanguageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboSpellCheckDefaulltLanguageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboSpellCheckDefaulltLanguageActionPerformed

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

            @Override
            public void run() {
                new UserSettingsUpdater(null, null, -1, "keyId").setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JButton jButtonSelectSound;
    private javax.swing.JButton jButtonSignature;
    private javax.swing.JButton jButtonSpellCheckOptions;
    private javax.swing.JCheckBox jCheckBoxHideDecrypDialog;
    private javax.swing.JCheckBox jCheckBoxHideEncryptionDiscardableWindow;
    private javax.swing.JCheckBox jCheckBoxInsertSignature;
    private javax.swing.JCheckBox jCheckBoxPlaySound;
    private javax.swing.JCheckBox jCheckBoxPopUpOnTaskbar;
    private javax.swing.JCheckBox jCheckBoxSendAnonymousNotifications;
    private javax.swing.JCheckBox jCheckBoxSendNotifyEmail;
    private javax.swing.JComboBox jComboFontSizeBody;
    private javax.swing.JComboBox jComboNbMessagesPerPage;
    private javax.swing.JComboBox jComboSpellCheckDefaulltLanguage;
    private javax.swing.JLabel jLabelAccount;
    private javax.swing.JLabel jLabelAccountInfo;
    private javax.swing.JLabel jLabelCoupon;
    private javax.swing.JLabel jLabelCouponHelp;
    private javax.swing.JLabel jLabelCryptoSettings;
    private javax.swing.JLabel jLabelEmailPref;
    private javax.swing.JLabel jLabelFontSizeBody;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JLabel jLabelNbMessagesPerPage;
    private javax.swing.JLabel jLabelNotify;
    private javax.swing.JLabel jLabelSpellCheck;
    private javax.swing.JLabel jLabelSpellCheckDefaultLanguage;
    private javax.swing.JLabel jLabelStorage;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPaneSepAccountInfo;
    private javax.swing.JPanel jPaneSepEmailPref;
    private javax.swing.JPanel jPaneSepNotify;
    private javax.swing.JPanel jPaneSepSpellCheck;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelAccountTop;
    private javax.swing.JPanel jPanelButtonsLeft;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelCheckBox;
    private javax.swing.JPanel jPanelCoupon;
    private javax.swing.JPanel jPanelCryptoSettings;
    private javax.swing.JPanel jPanelCryptoSettings1;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelEmailNew;
    private javax.swing.JPanel jPanelHideAndAnon;
    private javax.swing.JPanel jPanelHideEncryptDiscard;
    private javax.swing.JPanel jPanelHidensertSignature;
    private javax.swing.JPanel jPanelNameAndEmail;
    private javax.swing.JPanel jPanelNameNew;
    private javax.swing.JPanel jPanelNbMessagesPerPage;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelNotificationOn;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep13;
    private javax.swing.JPanel jPanelSep14;
    private javax.swing.JPanel jPanelSep15;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSepBlanc5;
    private javax.swing.JPanel jPanelSepBlanc6;
    private javax.swing.JPanel jPanelSepBlanc7;
    private javax.swing.JPanel jPanelSepBlank;
    private javax.swing.JPanel jPanelSepLine;
    private javax.swing.JPanel jPanelSepLine2;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelSpellCheck;
    private javax.swing.JPanel jPanelTop;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTextField jTextFieldAccount;
    private javax.swing.JTextField jTextFieldCoupon;
    private javax.swing.JTextField jTextFieldCryptoSettings;
    private javax.swing.JTextField jTextFieldEmail;
    private javax.swing.JTextField jTextFieldStorage;
    private javax.swing.JTextField jTextFieldUserName;
    // End of variables declaration//GEN-END:variables





}
