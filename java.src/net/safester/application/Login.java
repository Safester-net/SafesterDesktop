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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.awakefw.commons.api.client.HttpProxy;
import org.awakefw.commons.api.client.Invalid2faCodeException;
import org.awakefw.commons.api.client.InvalidLoginException;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.client.AwakeUrl;
import org.awakefw.sql.api.client.AwakeConnection;

import com.safelogic.utilx.syntax.EmailChecker;
import com.swing.util.ButtonUrlOver;
import com.swing.util.SwingUtil;

import net.safester.application.install.AskForDownloadJframe;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.ConnectionParms;
import net.safester.application.parms.Parms;
import net.safester.application.parms.StoreParms;
import net.safester.application.parms.SubscriptionLocalManager;
import net.safester.application.parms.SubscriptionLocalStore;
import net.safester.application.register.Register;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.FrameShaker;
import net.safester.application.tool.UI_Util;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.HtmlTextUtil;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.PolicyInstaller;
import net.safester.application.util.UserPrefManager;
import net.safester.application.util.proxy.ProxySessionCheckerNew;
import net.safester.application.wait.tools.CmWaitDialog;
import net.safester.clientserver.ServerParms;
import net.safester.clientserver.SubscriptionLocal;

/**
 * Login window
 *
 * @author Alexandre Becquereau
 */
public class Login extends javax.swing.JFrame {

    public static boolean DEBUG  = false;
    public static int MAX_SIZE = 425;
    /**
     * Called windows instances
     */
    private SystemPropDisplayer systemPropDisplayer = null;
    private About about = null;
    /**
     * The default passphrase echo char
     */
    private char defaultEchocar;
    /**
     * The wait dialog
     */
    CmWaitDialog cmWaitDialog = null;
    MessagesManager messages = new MessagesManager();
    private ClipboardManager clipboardManager;
    private Connection connection = null;

    //Optional pendingEmail for merging pending account with current
    //private String pendingEmail = null;
    private JFrame thisOne;
    
    Main main = null;
    /* The Set that contains all accounts when multi accounts usage */
    private Set<UserAccount> userAccounts = new TreeSet<>();

    /**
     * Creates new form at first login time
     */
    public Login() {
        this.thisOne = this;
        initComponents();
        initCompany(null, null);

    }

    /**
     * To be used when multi login
     * @param main 
     * @param defaultAddAccount the account chosen by user in Main Window
     */
    public Login(Main main, String defaultAddAccount) {
	
	if (main == null) {
	    throw new NullPointerException("main cannot be null!");
	}
	
        this.main = main;
        this.userAccounts = main.getUserAccounts();
        this.thisOne = this;
        initComponents();
        initCompany(null, defaultAddAccount);
    }

    private void initCompany(String email, String defaultAddAccount) {

        //org.awakefm.file.http.HttpTransferOne.DEBUG = false;
        clipboardManager = new ClipboardManager(rootPane);
        
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());

        this.jButtonVersion.setText(net.safester.application.version.Version.VERSION + " ");
        this.jButtonVersion.setForeground(Parms.COLOR_URL);

        if (email == null) {
            email = UserPrefManager.getPreference(UserPrefManager.USER_LOGIN);
        }

        this.jMenuSettings.setText(messages.getMessage("settings"));
        this.jMenuItemProxySettings.setText(messages.getMessage("proxy_settings"));

        this.jMenuHelp.setText(messages.getMessage("help"));
        this.jMenuItemHelp.setText(messages.getMessage("new_user_help"));
        this.jMenuItemSystemInfo.setText(messages.getMessage("system_info"));
        this.jMenuItemAbout.setText(messages.getMessage("about"));
        this.jMenuItemWhatsNew.setText(messages.getMessage("whats_new"));

        // First time, display the defaults last login
        if (this.main == null) {
            this.jTextFieldLogin.setText(email);
        }
        else {
            this.jTextFieldLogin.setText(null);
            if (defaultAddAccount != null) {
                this.jTextFieldLogin.setText(defaultAddAccount);
            }
        }

        this.jLabelErrorMessage.setText(" ");
        this.jLabelLogin.setText(messages.getMessage("email"));
        this.jLabelPwd.setText(messages.getMessage("passphrase"));
        this.jPasswordField.setText("");

        //this.jButtonCreateAccount.setText(messages.getMessage("new_user_click_here_to_register"));
        this.jButtonCreateAccount.setText(messages.getMessage("new_user_create_account"));
        this.jButtonLogin.setText(messages.getMessage("login"));
        this.jButtonCancel.setText(messages.getMessage("cancel"));

        this.jButtonLostPassphrase.setText(messages.getMessage("forgotten_passphrase"));

        // For Mac OS X behavior (rounded default buttons)
        jButtonLostPassphrase.putClientProperty("JButton.buttonType", "square");
        jButtonVersion.putClientProperty("JButton.buttonType", "square");

        defaultEchocar = this.jPasswordField.getEchoChar();

        jCheckBoxHideTyping.setSelected(true);
        jCheckBoxHideTyping.setText(messages.getMessage("hide_typing"));

        jLabelKeyboardWarning.setText(null);
        keyListenerAdder();

        this.setTitle(messages.getMessage("login_title"));
        this.setLocationRelativeTo(null);
        this.jTextFieldLogin.requestFocus();

        if (jTextFieldLogin.getText() != null && jTextFieldLogin.getText().length() > 2) {
            this.jTextFieldLogin.setCaretPosition(jTextFieldLogin.getText().length());
            this.jPasswordField.requestFocus();
        }

        testCapsOn();

        ButtonResizer buttonResizer = new ButtonResizer(jPanelButtons);
        buttonResizer.setWidthToMax();

        SwingUtil.applySwingUpdates(rootPane);

        if (UI_Util.isNimbus()) {
            //Linux version specific
            //jPanelMain.setPreferredSize(new Dimension(395, 263));
            this.jButtonLostPassphrase.setMargin(new Insets(3, 0, 2, 0));
            Dimension dim = this.jButtonLostPassphrase.getPreferredSize();
            dim.setSize(dim.getWidth(), dim.getHeight() + 1);
            this.jButtonLostPassphrase.setPreferredSize(dim);
        }

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                close(); // To force exit
            }
        });

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

        jPanelRememberPassphrase.remove(jButtonLostPassphrase);

        if (SystemUtils.IS_OS_WINDOWS) {
            MAX_SIZE += 10;
        }

        this.setSize(MAX_SIZE, MAX_SIZE);

        WindowSettingManager.load(this);

        if (!Desktop.isDesktopSupported()) {
            JOptionPane.showMessageDialog(this, messages.getMessage("java_desktop_not_supported"), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        pack();

    }

    private void resizeToMax() {
        if (this.getSize().width > MAX_SIZE || this.getSize().height > MAX_SIZE) {
            this.setSize(new Dimension(MAX_SIZE, MAX_SIZE));

            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            Point middlePoint = new Point((dim.width / 2) - (this.getWidth() / 2),
                    (dim.height / 2) - (this.getHeight() / 2));

            this.setLocation(middlePoint);
        }
    }

    private void save() {
        WindowSettingManager.save(this);
    }

    private void close() {
        save();
        this.dispose();
        if (main == null) {
            System.exit(0);
        }
    }

    /**
     * Universal key listener
     *
     */
    private void keyListenerAdder() {

        java.util.List<Component> components = SwingUtil.getAllComponants(rootPane);

        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);

            if (!(comp instanceof JTextField)) {
                continue;
            }

            //System.out.println(comp);
            comp.addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    testCapsOn();
                    this_keyPressed(e);
                }
            });
        }

        //System.out.println("addKeyListener");
    }

    private void this_keyPressed(KeyEvent e) {

        int id = e.getID();

        if (id == KeyEvent.KEY_PRESSED) {
            int keyCode = e.getKeyCode();

            //System.out.println("key pressed: " + e.getID());
            if (keyCode == KeyEvent.VK_ESCAPE) {
                close();
            }
            if (keyCode == KeyEvent.VK_ENTER) {
                doIt();
            }
        }
    }

    /**
     * Test0 if Caps is on
     *
     */
    private void testCapsOn() {

        if (SystemUtils.IS_OS_MAC) {
            return; // Nothing on Mac (has already a hint)
        }
        // Toolkit.getDefaultToolkit().getLockingKeyState is wrapped in a try/catch
        // because Linux Ubuntu does not supports it!
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
            jLabelKeyboardWarning.setText(null);
        }
    }

    /**
     * Test if an account is pending. We do not trap communications errors.
     *
     * @param userEmail the user email
     * @param httpNetworkParameters the http network parameters for this http
     * session
     * @return true if the account is pending state
     */
    private boolean isAccountPending(String userEmail, HttpProxy httpProxy)
            throws Exception {
        String isPendingStr = "false";

        AwakeFileSession awakeFileSession = new AwakeFileSession(ServerParms.getAwakeSqlServerUrl(), null, null, httpProxy);
        isPendingStr = awakeFileSession.call("net.safester.server.hosts.KeyPairCreatorV3.tempKeyPairExist", userEmail);

        debug("isPendingStr: " + isPendingStr);

        boolean isPending = Boolean.parseBoolean(isPendingStr);
        return isPending;
    }

    /**
     * Check login/password
     */
    private void doIt() {

        debug("");
        debug(new Date() + " Do it begin...");

        PolicyInstaller.setCryptographyRestrictions();
                
        String email = jTextFieldLogin.getText();
        email = email.trim();

        EmailChecker emailChecker = new EmailChecker(email);
        if (!emailChecker.isSyntaxValid()) {
            FrameShaker shaker = new FrameShaker(this);
            shaker.startShake();
            jLabelErrorMessage.setText(messages.getMessage("check_login"));
            return;
        }
        char[] passphrase = jPasswordField.getPassword();
        if (passphrase == null || passphrase.length <= 1) {
            passphrase = getPassphraseFromFile(passphrase);
        }

        jButtonCreateAccount.setVisible(false);
        update(getGraphics());

        debug(new Date() + " Before in Progress...");
        
        // Start wait dialog with "server contacted" message
        cmWaitDialog = new CmWaitDialog(this,
                this.messages.getMessage("in_progress"),
                this.messages.getMessage("contacting_server"),
                null);
        cmWaitDialog.startWaiting();

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        debug("");
        debug(new Date() + " Login... Detecting Proxy...");

        ProxySessionCheckerNew proxySessionCheckerNew = new ProxySessionCheckerNew(this, cmWaitDialog, email, passphrase);
        try {
            if (!proxySessionCheckerNew.check()) {
                this.setCursor(Cursor.getDefaultCursor());
                cmWaitDialog.stopWaiting();
                jButtonCreateAccount.setVisible(true);
                return;
            }

            if (proxySessionCheckerNew.doDownloadNewVersion()) {
                this.setCursor(Cursor.getDefaultCursor());
                cmWaitDialog.stopWaiting();
                jButtonCreateAccount.setVisible(true);
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            this.setCursor(Cursor.getDefaultCursor());
            cmWaitDialog.stopWaiting();
            jButtonCreateAccount.setVisible(true);
            JOptionPaneNewCustom.showException(this, ex, messages.getMessage("unable_to_connect"));
            return;
        }
        

        HttpProxy httpProxy = proxySessionCheckerNew.getHttpProxy();
        debug(new Date() + " httpProxy: " + httpProxy);
        
        cmWaitDialog.changeText(this.messages.getMessage("authenticating"));

        connection = null;         //Reset Connection

        //Load connection
        //String lowerCaseLogin = this.jTextFieldLogin.getText();
        String lowerCaseLogin = email.toLowerCase();
        this.jTextFieldLogin.setText(lowerCaseLogin);

        ConnectionParms connectionParms = new ConnectionParms(
                this.jTextFieldLogin.getText(),
                passphrase,
                httpProxy, 
                null);

        debug(new Date() + " Login... Getting Connection begin...");
        boolean askForValidationCode = false;

        try {
            connection = connectionParms.getConnection();
        } catch (ConnectException e) {
            this.setCursor(Cursor.getDefaultCursor());
            cmWaitDialog.stopWaiting();
            if (e.toString().contains("status: 407")) {
                JOptionPane.showMessageDialog(this, messages.getMessage("your_proxy_requires_authentication"));
            } else {
                JOptionPane.showMessageDialog(this, messages.getMessage("safester_server_is_under_maintenance"));
            }
            jButtonCreateAccount.setVisible(true);
            return;
        } catch (UnknownHostException e) {
            this.setCursor(Cursor.getDefaultCursor());
            cmWaitDialog.stopWaiting();
            JOptionPane.showMessageDialog(this, messages.getMessage("safester_server_is_under_maintenance"));
            jButtonCreateAccount.setVisible(true);
            return;
        } catch (InvalidLoginException e) {
            boolean accountPending = false;
            try {
                accountPending = isAccountPending(lowerCaseLogin, httpProxy);
            } catch (Exception e1) {
                cmWaitDialog.stopWaiting();
                jButtonCreateAccount.setVisible(true);
                JOptionPaneNewCustom.showException(rootPane, e1);
                return;
            }
            if (accountPending) {
                this.setCursor(Cursor.getDefaultCursor());
                cmWaitDialog.stopWaiting();
                String text = SwingUtil.getTextContent("account_waiting_for_confirm");
                text = text.replace("{0}", lowerCaseLogin);
                jButtonCreateAccount.setVisible(true);
                JOptionPane.showMessageDialog(this, text);
                return;
            } else {
                // BEGIN CODE TO UNCOMMENT //
                this.setCursor(Cursor.getDefaultCursor());
                cmWaitDialog.stopWaiting();
                jButtonCreateAccount.setVisible(true);
                //Error is not really a system error
                FrameShaker shaker = new FrameShaker(this);
                shaker.startShake();
                jLabelErrorMessage.setText(messages.getMessage("bad_login_password"));
                return;
                // END CODE TO UNCOMMENT //
            }
        } catch (Invalid2faCodeException e) // A 2FA code asked
        {
            cmWaitDialog.stopWaiting();
            jButtonCreateAccount.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
            askForValidationCode = true;
        } catch (Exception e) // Others *unexpected* Exception
        {
            cmWaitDialog.stopWaiting();
            jButtonCreateAccount.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPaneNewCustom.showException(this, e, messages.getMessage("unable_to_connect"));
            return;
        }

        // Retry with 2FA code if required by server
        if (askForValidationCode) {
            jLabelErrorMessage.setText(null);
            
            while (true) {
                this.setCursor(Cursor.getDefaultCursor());
                cmWaitDialog.stopWaiting();
                
                askForValidationCode = false;
                Double2FaCodeDialog dialog = new Double2FaCodeDialog(this);
                dialog.setVisible(true);

                if (dialog.isCancelAsked()) {
                    return;
                }

                String code = dialog.getValidationCode();
                debug("code: " + code);
                
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                jButtonCreateAccount.setVisible(false);
                update(getGraphics());
        
                cmWaitDialog = new CmWaitDialog(this,
                this.messages.getMessage("in_progress"),
                this.messages.getMessage("contacting_server"),
                null);
                        
                cmWaitDialog.startWaiting();
                cmWaitDialog.changeText(this.messages.getMessage("authenticating"));

                connectionParms = new ConnectionParms(
                        this.jTextFieldLogin.getText(),
                        passphrase,
                        httpProxy, code);

                try {
                    connection = connectionParms.getConnection();
                } catch (UnknownHostException e) {
                    this.setCursor(Cursor.getDefaultCursor());
                    cmWaitDialog.stopWaiting();
                    JOptionPane.showMessageDialog(this, messages.getMessage("safester_server_is_under_maintenance"));
                    jButtonCreateAccount.setVisible(true);
                    return;
                } catch (Invalid2faCodeException e) // A 2FA code asked
                {
                    debug("Invalid2faCodeException: "  + e.toString());
                    cmWaitDialog.stopWaiting();
                    jButtonCreateAccount.setVisible(true);
                    this.setCursor(Cursor.getDefaultCursor());
                    askForValidationCode = true;
                } catch (Exception e) // Others *unexpected* Exception
                {
                    cmWaitDialog.stopWaiting();
                    jButtonCreateAccount.setVisible(true);
                    this.setCursor(Cursor.getDefaultCursor());
                    JOptionPaneNewCustom.showException(this, e, messages.getMessage("unable_to_connect"));
                    return;
                }
                
                if (! askForValidationCode) {
                    break;
                }
            }
        }
                   
        //DONE LOGIN & 2FA OK!
        debug(new Date() + " Login... Getting Connection end...");
        //int userNumber = connectionParms.getUserNumber();

        UserPrefManager.setPreference(UserPrefManager.USER_LOGIN, jTextFieldLogin.getText());
        
        String accountLists = UserPrefManager.getPreference(UserPrefManager.ACCOUNTS_LIST);
        if (accountLists == null) {
            accountLists = jTextFieldLogin.getText();
        } else {
            if (!accountLists.contains(jTextFieldLogin.getText())) {
                accountLists += "," + jTextFieldLogin.getText();
            }
        }
        UserPrefManager.setPreference(UserPrefManager.ACCOUNTS_LIST, accountLists);
        
        // Notify the user if expired & allow him to buy
        SubscriptionLocal subscriptionLocal =  connectionParms.getSubscriptionLocal();
        
        /*
        try {
	    subscriptionLocal  = SubscriptionLocalGetterClient.get(jTextFieldLogin.getText(), connection);
	} catch (SQLException e) {
            cmWaitDialog.stopWaiting();
            jButtonCreateAccount.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPaneNewCustom.showException(this, e, messages.getMessage("unable_to_connect"));
            return;
	}
        */
        
        int userNumber = subscriptionLocal.getUserNumber();
        allowUserToBuyIfExpired(subscriptionLocal);

        SubscriptionLocalStore.put(subscriptionLocal);
        
        //debug("Final ConnectionParms.getSubscription(): " + ConnectionParms.getSubscription());

        cmWaitDialog.changeText(this.messages.getMessage("login_success"));

        System.out.println(new Date() + " Login... Launching Safester Main..");
        System.out.println(new Date() + " subscriptionLocal: " + subscriptionLocal);

        // Create a new account to add to existing accounts
        UserAccount userAccount = new UserAccount(connection, lowerCaseLogin, userNumber, passphrase, subscriptionLocal.getTypeSubscription());
        userAccounts.add(userAccount);
        
        Main mainNew = new Main(connection, lowerCaseLogin, userNumber, passphrase, subscriptionLocal.getTypeSubscription(), userAccounts);
        mainNew.setVisible(true);
            
        if (main != null) {
            main.dispose();
        }
        
        this.setCursor(Cursor.getDefaultCursor());
        cmWaitDialog.stopWaiting();

        this.dispose();

    }



    public static char[] getPassphraseFromFile(char[] passphrase) {
        File file = new File(System.getProperty("user.home") + File.separator + "password.txt");
        if (file.exists()) {
            try {
                String passphraseTrs = FileUtils.readFileToString(file);
                passphrase = new char[passphraseTrs.length()];
                for (int i = 0; i < passphraseTrs.length(); i++) {
                    passphrase[i] = passphraseTrs.charAt(i);
                }

            } catch (IOException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return passphrase;
    }

    /**
     *
     * @return the content of host/newstart/service_message_flags
     */
    private String getServiceMessageFlags() throws Exception {
        String content = null;
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        String theHost = awakeFileSession.getUrl();
        theHost = StringUtils.substringBeforeLast(theHost, "/");

        URL url = new URL(theHost + "/newstart/service_message_flags.txt");

        AwakeUrl awakeUrl = new AwakeUrl(awakeFileSession.getHttpProxy(), awakeFileSession.getHttpProtocolParameters());
        content = awakeUrl.download(url);

        return content;
    }

//    /**
//     * Allow the use
//     *
//     * @param userNumber
//     */
//    @SuppressWarnings("unused")
//    private void allowUserToBuyIfExpired(int userNumber) {
//
//        //Get current user subscription & the expired one
//        short userSubscription = ConnectionParms.getSubscription();
//        short userExpiredSubscription = ConnectionParms.getExpiredSubscription();
//
//        System.out.println("isExpired              : " + ConnectionParms.isExpired());
//        System.out.println("userSubscription       : " + userSubscription);
//        System.out.println("userExpiredSubscription: " + userExpiredSubscription);
//
//        // Accept free 
//        if (userSubscription == StoreParms.PRODUCT_FREE && userExpiredSubscription == StoreParms.PRODUCT_FREE) {
//            return; // FREE! Always OK
//        }
//
//        // If expired 
//        if (!ConnectionParms.isExpired()) {
//            return; // User is OK and subscription up to date
//        }
//
//        // Case paid subscription is expired
//        boolean doDiscard = UserPrefManager.getBooleanPreference(UserPrefManager.EXPIRED_SUBSCRIPTION_DIALOG_DISCARD);
//
//        if (!doDiscard) {
//            ExpiredSubscriptionDialog expiredSubscriptionDialog = new ExpiredSubscriptionDialog(this, connection,
//                    userNumber, userExpiredSubscription, true);
//            expiredSubscriptionDialog.setVisible(true);
//
//            if (expiredSubscriptionDialog.getNewSubscription() != StoreParms.PRODUCT_FREE) {
//                userSubscription = (short) expiredSubscriptionDialog.getNewSubscription();
//                ConnectionParms.setSubscription(userSubscription);
//            }
//        }
//
//    }

    private void allowUserToBuyIfExpired(SubscriptionLocal subscriptionLocal) {
	
        int userSubscription = subscriptionLocal.getTypeSubscription();
        boolean isExpired = SubscriptionLocalManager.isEsxpired(subscriptionLocal);
        
        debug("isExpired              : " + isExpired);
        debug("userSubscription       : " + userSubscription);
        
        // Always accept FREE
        if (userSubscription == StoreParms.PRODUCT_FREE) {
            return;
        }
        
        // If not expired , no buy
        if (!isExpired) {
            return; // User is OK and subscription up to date
        }

        int userNumber = subscriptionLocal.getUserNumber();
        
        // Case paid subscription is expired
        boolean doDiscard = UserPrefManager.getBooleanPreference(UserPrefManager.EXPIRED_SUBSCRIPTION_DIALOG_DISCARD);

        if (!doDiscard) {
            ExpiredSubscriptionDialog expiredSubscriptionDialog = new ExpiredSubscriptionDialog(this, connection,
                    userNumber, StoreParms.PRODUCT_FREE, true);
            expiredSubscriptionDialog.setVisible(true);

            userSubscription = expiredSubscriptionDialog.getNewSubscription();
            subscriptionLocal.setTypeSubscription(userSubscription);
           
        }
        
    }
    
    /**
     * Merge account
     *
     * @param httpProxy
     * @param connection
     * @param userNumber
     * @throws HeadlessException
     */
    /*
    @SuppressWarnings("unused")
    private void mergeAccounts(HttpProxy httpProxy, Connection connection, int userNumber) {
        if (pendingEmail == null) {
            return;
        }

        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
        String result = null;

        try {
            result = awakeFileSession.call("net.safester.server.hosts.pending.AccountMergerHost.mergeAccounts", connection, userNumber, pendingEmail);
        } catch (Exception e) {
            // No do not throw error ==> Display message instead.
            // Will be logged inse user.home/SafeShareIt.out.log
            System.err.println("mergeAccounts AwakeSession error: ");
            e.printStackTrace();
        }

        if (StringUtils.isNumeric(result)) {
            int importedMessages = Integer.parseInt(result);
            String message = messages.getMessage("imported_messages");
            message = MessageFormat.format(message, importedMessages);
            JOptionPane.showMessageDialog(rootPane, message);
        } else {
            System.err.println("mergeAccounts - result is not numeric: " + result);
        }

    }
     */
    /**
     * for callback by Register *
     */
    public void setLogin(String login) {
        this.jTextFieldLogin.setText(login);
    }

    public Connection getConnection() {
        return this.connection;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel8 = new javax.swing.JPanel();
        jLabelLogo = new javax.swing.JLabel();
        jPanelSep1 = new javax.swing.JPanel();
        jPanelNewAccount = new javax.swing.JPanel();
        jButtonCreateAccount = new javax.swing.JButton();
        jPanelSep2 = new javax.swing.JPanel();
        jPanelSepLeft1 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanelSepRight1 = new javax.swing.JPanel();
        jPanelMain = new javax.swing.JPanel();
        jPanelErrorMessage = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanelErrorMessage2 = new javax.swing.JPanel();
        jLabelErrorMessage = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanelVersion = new javax.swing.JPanel();
        jButtonVersion = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jPanelLabelLogin = new javax.swing.JPanel();
        jLabelLogin = new javax.swing.JLabel();
        jTextFieldLogin = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jPanelPassword = new javax.swing.JPanel();
        jLabelPwd = new javax.swing.JLabel();
        jPasswordField = new javax.swing.JPasswordField();
        jPanel2 = new javax.swing.JPanel();
        jPanelRememberPassphrase = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jCheckBoxHideTyping = new javax.swing.JCheckBox();
        jButtonLostPassphrase = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanelSepWithSeparator = new javax.swing.JPanel();
        jPanelSep = new javax.swing.JPanel();
        jPanelSepLeft = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelSepRight = new javax.swing.JPanel();
        jPanelButtonsBottom = new javax.swing.JPanel();
        jPanelLeft = new javax.swing.JPanel();
        jPanelLeftSpace = new javax.swing.JPanel();
        jLabelKeyboardWarning = new javax.swing.JLabel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonLogin = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuSettings = new javax.swing.JMenu();
        jMenuItemProxySettings = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemHelp = new javax.swing.JMenuItem();
        jMenuItemSystemInfo = new javax.swing.JMenuItem();
        jMenuItemWhatsNew = new javax.swing.JMenuItem();
        jMenuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanel8.setMaximumSize(new java.awt.Dimension(32767, 122));
        jPanel8.setMinimumSize(new java.awt.Dimension(289, 122));
        jPanel8.setPreferredSize(new java.awt.Dimension(289, 122));
        jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 8, 8));

        jLabelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/logo-blue-on-white-300x99.png"))); // NOI18N
        jPanel8.add(jLabelLogo);

        getContentPane().add(jPanel8);

        jPanelSep1.setMaximumSize(new java.awt.Dimension(32767, 10));
        getContentPane().add(jPanelSep1);

        jPanelNewAccount.setMaximumSize(new java.awt.Dimension(32767, 43));
        jPanelNewAccount.setMinimumSize(new java.awt.Dimension(165, 43));
        jPanelNewAccount.setName(""); // NOI18N
        jPanelNewAccount.setPreferredSize(new java.awt.Dimension(165, 43));

        jButtonCreateAccount.setText("jButtonCreateAccount");
        jButtonCreateAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateAccountActionPerformed(evt);
            }
        });
        jPanelNewAccount.add(jButtonCreateAccount);

        getContentPane().add(jPanelNewAccount);

        jPanelSep2.setMinimumSize(new java.awt.Dimension(20, 6));
        jPanelSep2.setPreferredSize(new java.awt.Dimension(10, 6));
        jPanelSep2.setLayout(new javax.swing.BoxLayout(jPanelSep2, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSepLeft1.setMaximumSize(new java.awt.Dimension(10, 2));
        jPanelSepLeft1.setMinimumSize(new java.awt.Dimension(10, 2));
        jPanelSepLeft1.setPreferredSize(new java.awt.Dimension(10, 2));
        jPanelSep2.add(jPanelSepLeft1);

        jSeparator3.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator3.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSep2.add(jSeparator3);

        jPanelSepRight1.setMaximumSize(new java.awt.Dimension(10, 2));
        jPanelSepRight1.setMinimumSize(new java.awt.Dimension(10, 2));
        jPanelSepRight1.setPreferredSize(new java.awt.Dimension(10, 2));
        jPanelSep2.add(jPanelSepRight1);

        getContentPane().add(jPanelSep2);

        jPanelMain.setLayout(new javax.swing.BoxLayout(jPanelMain, javax.swing.BoxLayout.Y_AXIS));

        jPanelErrorMessage.setMaximumSize(new java.awt.Dimension(65534, 32));
        jPanelErrorMessage.setMinimumSize(new java.awt.Dimension(219, 32));
        jPanelErrorMessage.setPreferredSize(new java.awt.Dimension(406, 32));
        jPanelErrorMessage.setLayout(new javax.swing.BoxLayout(jPanelErrorMessage, javax.swing.BoxLayout.LINE_AXIS));

        jPanel7.setMaximumSize(new java.awt.Dimension(10, 11));
        jPanel7.setMinimumSize(new java.awt.Dimension(10, 11));
        jPanel7.setPreferredSize(new java.awt.Dimension(10, 11));
        jPanelErrorMessage.add(jPanel7);

        jPanelErrorMessage2.setLayout(new javax.swing.BoxLayout(jPanelErrorMessage2, javax.swing.BoxLayout.LINE_AXIS));

        jLabelErrorMessage.setForeground(new java.awt.Color(255, 0, 0));
        jLabelErrorMessage.setText("jLabelErrorMessage");
        jLabelErrorMessage.setMaximumSize(new java.awt.Dimension(300, 16));
        jLabelErrorMessage.setMinimumSize(new java.awt.Dimension(300, 16));
        jLabelErrorMessage.setPreferredSize(new java.awt.Dimension(300, 16));
        jPanelErrorMessage2.add(jLabelErrorMessage);

        jPanelErrorMessage.add(jPanelErrorMessage2);

        jPanel6.setMinimumSize(new java.awt.Dimension(100, 10));
        jPanelErrorMessage.add(jPanel6);

        jPanelVersion.setLayout(new javax.swing.BoxLayout(jPanelVersion, javax.swing.BoxLayout.LINE_AXIS));

        jButtonVersion.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        jButtonVersion.setForeground(new java.awt.Color(0, 0, 255));
        jButtonVersion.setText("jButtonVersion");
        jButtonVersion.setBorder(null);
        jButtonVersion.setBorderPainted(false);
        jButtonVersion.setContentAreaFilled(false);
        jButtonVersion.setFocusPainted(false);
        jButtonVersion.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButtonVersion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonVersionMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonVersionMouseExited(evt);
            }
        });
        jButtonVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVersionActionPerformed(evt);
            }
        });
        jPanelVersion.add(jButtonVersion);

        jPanelErrorMessage.add(jPanelVersion);

        jPanel9.setMaximumSize(new java.awt.Dimension(10, 11));
        jPanel9.setMinimumSize(new java.awt.Dimension(10, 11));
        jPanel9.setPreferredSize(new java.awt.Dimension(10, 11));
        jPanelErrorMessage.add(jPanel9);

        jPanelMain.add(jPanelErrorMessage);

        jPanelLabelLogin.setMaximumSize(new java.awt.Dimension(2147483647, 33));
        jPanelLabelLogin.setMinimumSize(new java.awt.Dimension(121, 33));
        jPanelLabelLogin.setName(""); // NOI18N
        jPanelLabelLogin.setPreferredSize(new java.awt.Dimension(395, 33));
        jPanelLabelLogin.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelLogin.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelLogin.setText("jLabelLogin");
        jLabelLogin.setMaximumSize(new java.awt.Dimension(100, 22));
        jLabelLogin.setMinimumSize(new java.awt.Dimension(100, 22));
        jLabelLogin.setName(""); // NOI18N
        jLabelLogin.setPreferredSize(new java.awt.Dimension(100, 22));
        jPanelLabelLogin.add(jLabelLogin);

        jTextFieldLogin.setText("jTextFieldLogin");
        jTextFieldLogin.setPreferredSize(new java.awt.Dimension(280, 22));
        jPanelLabelLogin.add(jTextFieldLogin);

        jPanel1.setPreferredSize(new java.awt.Dimension(30, 10));
        jPanelLabelLogin.add(jPanel1);

        jPanelMain.add(jPanelLabelLogin);

        jPanelPassword.setMaximumSize(new java.awt.Dimension(2147483647, 33));
        jPanelPassword.setMinimumSize(new java.awt.Dimension(121, 33));
        jPanelPassword.setPreferredSize(new java.awt.Dimension(415, 33));
        jPanelPassword.setRequestFocusEnabled(false);
        jPanelPassword.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelPwd.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelPwd.setText("jLabelPwd");
        jLabelPwd.setMaximumSize(new java.awt.Dimension(100, 16));
        jLabelPwd.setMinimumSize(new java.awt.Dimension(100, 16));
        jLabelPwd.setPreferredSize(new java.awt.Dimension(100, 16));
        jPanelPassword.add(jLabelPwd);

        jPasswordField.setText("jPasswordField1");
        jPasswordField.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jPasswordField.setPreferredSize(new java.awt.Dimension(280, 22));
        jPanelPassword.add(jPasswordField);

        jPanel2.setPreferredSize(new java.awt.Dimension(30, 10));
        jPanelPassword.add(jPanel2);

        jPanelMain.add(jPanelPassword);

        jPanelRememberPassphrase.setMaximumSize(new java.awt.Dimension(33094, 31));
        jPanelRememberPassphrase.setMinimumSize(new java.awt.Dimension(337, 31));
        jPanelRememberPassphrase.setPreferredSize(new java.awt.Dimension(337, 31));
        jPanelRememberPassphrase.setLayout(new javax.swing.BoxLayout(jPanelRememberPassphrase, javax.swing.BoxLayout.LINE_AXIS));

        jPanel5.setMaximumSize(new java.awt.Dimension(107, 10));
        jPanel5.setMinimumSize(new java.awt.Dimension(107, 10));
        jPanel5.setName(""); // NOI18N
        jPanel5.setPreferredSize(new java.awt.Dimension(107, 10));
        jPanel5.setRequestFocusEnabled(false);
        jPanelRememberPassphrase.add(jPanel5);

        jCheckBoxHideTyping.setText("Hide Typing");
        jCheckBoxHideTyping.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxHideTypingItemStateChanged(evt);
            }
        });
        jPanelRememberPassphrase.add(jCheckBoxHideTyping);

        jButtonLostPassphrase.setForeground(new java.awt.Color(0, 0, 255));
        jButtonLostPassphrase.setText("jButtonLostPassphrase");
        jButtonLostPassphrase.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButtonLostPassphrase.setBorderPainted(false);
        jButtonLostPassphrase.setContentAreaFilled(false);
        jButtonLostPassphrase.setFocusPainted(false);
        jButtonLostPassphrase.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButtonLostPassphrase.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonLostPassphraseMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonLostPassphraseMouseExited(evt);
            }
        });
        jButtonLostPassphrase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLostPassphraseActionPerformed(evt);
            }
        });
        jPanelRememberPassphrase.add(jButtonLostPassphrase);

        jPanel4.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelRememberPassphrase.add(jPanel4);

        jPanelMain.add(jPanelRememberPassphrase);
        jPanelMain.add(jPanelSepWithSeparator);

        getContentPane().add(jPanelMain);

        jPanelSep.setMaximumSize(new java.awt.Dimension(32787, 6));
        jPanelSep.setMinimumSize(new java.awt.Dimension(10, 6));
        jPanelSep.setPreferredSize(new java.awt.Dimension(10, 6));
        jPanelSep.setLayout(new javax.swing.BoxLayout(jPanelSep, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSepLeft.setMaximumSize(new java.awt.Dimension(10, 2));
        jPanelSepLeft.setMinimumSize(new java.awt.Dimension(10, 2));
        jPanelSepLeft.setPreferredSize(new java.awt.Dimension(10, 2));
        jPanelSep.add(jPanelSepLeft);

        jSeparator2.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator2.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator2.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSep.add(jSeparator2);

        jPanelSepRight.setMaximumSize(new java.awt.Dimension(10, 2));
        jPanelSepRight.setMinimumSize(new java.awt.Dimension(10, 2));
        jPanelSepRight.setPreferredSize(new java.awt.Dimension(10, 2));
        jPanelSep.add(jPanelSepRight);

        getContentPane().add(jPanelSep);

        jPanelButtonsBottom.setMaximumSize(new java.awt.Dimension(32912, 45));
        jPanelButtonsBottom.setLayout(new javax.swing.BoxLayout(jPanelButtonsBottom, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft.setLayout(new javax.swing.BoxLayout(jPanelLeft, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeftSpace.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelLeft.add(jPanelLeftSpace);

        jLabelKeyboardWarning.setText("jLabelKeyboardWarning");
        jLabelKeyboardWarning.setMinimumSize(new java.awt.Dimension(150, 14));
        jLabelKeyboardWarning.setPreferredSize(new java.awt.Dimension(150, 14));
        jPanelLeft.add(jLabelKeyboardWarning);

        jPanelButtonsBottom.add(jPanelLeft);

        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 4, 10));

        jButtonLogin.setText("jButtonLogin");
        jButtonLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLoginActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonLogin);

        jButtonCancel.setText("jButtonCancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonCancel);

        jPanel3.setMaximumSize(new java.awt.Dimension(1, 32767));
        jPanel3.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelButtons.add(jPanel3);

        jPanelButtonsBottom.add(jPanelButtons);

        getContentPane().add(jPanelButtonsBottom);

        jMenuSettings.setText("jMenuSettings");

        jMenuItemProxySettings.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuItemProxySettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/server_network.png"))); // NOI18N
        jMenuItemProxySettings.setText("jMenuItemProxySettings");
        jMenuItemProxySettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProxySettingsActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemProxySettings);

        jMenuBar1.add(jMenuSettings);

        jMenuHelp.setText("jMenuHelp");

        jMenuItemHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItemHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/question.png"))); // NOI18N
        jMenuItemHelp.setText("jMenuItemHelp");
        jMenuItemHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemHelpActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemHelp);

        jMenuItemSystemInfo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        jMenuItemSystemInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/speech_balloon_answer.png"))); // NOI18N
        jMenuItemSystemInfo.setText("jMenuItemSystemInfo");
        jMenuItemSystemInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSystemInfoActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemSystemInfo);

        jMenuItemWhatsNew.setText("jMenuItemWhatsNew");
        jMenuItemWhatsNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemWhatsNewActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemWhatsNew);

        jMenuItemAbout.setText("jMenuItemAbout");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemAbout);

        jMenuBar1.add(jMenuHelp);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        close();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLoginActionPerformed
        doIt();
    }//GEN-LAST:event_jButtonLoginActionPerformed

    private void jCheckBoxHideTypingItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxHideTypingItemStateChanged
        if ((evt.getStateChange() == ItemEvent.SELECTED)) {
            this.jPasswordField.setEchoChar(this.defaultEchocar);
            this.jPasswordField.setEchoChar(this.defaultEchocar);
        } else {
            // Display  chars
            this.jPasswordField.setEchoChar((char) 0);
            this.jPasswordField.setEchoChar((char) 0);
        }
    }//GEN-LAST:event_jCheckBoxHideTypingItemStateChanged

    private void jButtonVersionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVersionActionPerformed

        Desktop desktop = Desktop.getDesktop();
        try {
            String whatsNew = AskForDownloadJframe.getWhatsNewUrl();
            desktop.browse(new URI(whatsNew));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.toString());
        }
    }//GEN-LAST:event_jButtonVersionActionPerformed

    private void jButtonVersionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonVersionMouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
         ButtonUrlOver.enter(evt);
    }//GEN-LAST:event_jButtonVersionMouseEntered

    private void jButtonVersionMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonVersionMouseExited
        this.setCursor(Cursor.getDefaultCursor());
        ButtonUrlOver.exit(evt);
    }//GEN-LAST:event_jButtonVersionMouseExited

    private void jMenuItemSystemInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSystemInfoActionPerformed

        if (systemPropDisplayer != null) {
            systemPropDisplayer.dispose();
        }

        systemPropDisplayer = new SystemPropDisplayer(this);
    }//GEN-LAST:event_jMenuItemSystemInfoActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed

        if (about != null) {
            about.dispose();
        }

        about = new About(this, false);
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuItemProxySettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemProxySettingsActionPerformed

        if (frameProxyParms != null) {
            frameProxyParms.dispose();
        }

        frameProxyParms = new FrameProxyParms(this);
        frameProxyParms.setVisible(true);
}//GEN-LAST:event_jMenuItemProxySettingsActionPerformed

    private void jButtonCreateAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateAccountActionPerformed

        if (register != null) {
            register.dispose();
        }

        register = new Register(this, null);

    }//GEN-LAST:event_jButtonCreateAccountActionPerformed

    private void jMenuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHelpActionPerformed
        String content = HtmlTextUtil.getHtmlHelpContent("new_user_help");

        if (newsFrame != null) {
            newsFrame.dispose();
        }

        newsFrame = new NewsFrame(this, content, messages.getMessage("help"));

    }//GEN-LAST:event_jMenuItemHelpActionPerformed

    private void jButtonLostPassphraseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLostPassphraseActionPerformed

    }//GEN-LAST:event_jButtonLostPassphraseActionPerformed

    private void jButtonLostPassphraseMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLostPassphraseMouseExited
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_jButtonLostPassphraseMouseExited

    private void jButtonLostPassphraseMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLostPassphraseMouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jButtonLostPassphraseMouseEntered

    private void jMenuItemWhatsNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWhatsNewActionPerformed
        Desktop desktop = Desktop.getDesktop();
        try {
            String whatsNew = AskForDownloadJframe.getWhatsNewUrl();
            desktop.browse(new URI(whatsNew));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.toString());
        }
    }//GEN-LAST:event_jMenuItemWhatsNewActionPerformed

    /**
     * debug tool
     */
    private void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

        } catch (Exception ex) {
            System.out.println("Failed loading L&F: ");
            System.out.println(ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                new Login().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonCreateAccount;
    private javax.swing.JButton jButtonLogin;
    private javax.swing.JButton jButtonLostPassphrase;
    private javax.swing.JButton jButtonVersion;
    private javax.swing.JCheckBox jCheckBoxHideTyping;
    private javax.swing.JLabel jLabelErrorMessage;
    private javax.swing.JLabel jLabelKeyboardWarning;
    private javax.swing.JLabel jLabelLogin;
    private javax.swing.JLabel jLabelLogo;
    private javax.swing.JLabel jLabelPwd;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemHelp;
    private javax.swing.JMenuItem jMenuItemProxySettings;
    private javax.swing.JMenuItem jMenuItemSystemInfo;
    private javax.swing.JMenuItem jMenuItemWhatsNew;
    private javax.swing.JMenu jMenuSettings;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelButtonsBottom;
    private javax.swing.JPanel jPanelErrorMessage;
    private javax.swing.JPanel jPanelErrorMessage2;
    private javax.swing.JPanel jPanelLabelLogin;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelLeftSpace;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelNewAccount;
    private javax.swing.JPanel jPanelPassword;
    private javax.swing.JPanel jPanelRememberPassphrase;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSepLeft;
    private javax.swing.JPanel jPanelSepLeft1;
    private javax.swing.JPanel jPanelSepRight;
    private javax.swing.JPanel jPanelSepRight1;
    private javax.swing.JPanel jPanelSepWithSeparator;
    private javax.swing.JPanel jPanelVersion;
    private javax.swing.JPasswordField jPasswordField;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTextFieldLogin;
    // End of variables declaration//GEN-END:variables
    private FrameProxyParms frameProxyParms;
    private Register register;
    private NewsFrame newsFrame;

}
