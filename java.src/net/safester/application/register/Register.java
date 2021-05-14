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
package net.safester.application.register;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.awakefw.commons.api.client.HttpProtocolParameters;
import org.awakefw.commons.api.client.HttpProxy;
import org.awakefw.commons.api.client.RemoteException;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.bouncycastle.openpgp.PGPException;

import com.kawansoft.httpclient.KawanHttpClient;
import com.safelogic.pgp.api.KeyHandlerOne;
import com.safelogic.pgp.api.toolkit.CgeepApiTools;
import com.safelogic.utilx.StringMgr;
import com.safelogic.utilx.syntax.EmailChecker;
import com.swing.util.ButtonUrlOver;
import com.swing.util.SwingUtil;
import java.awt.FocusTraversalPolicy;
import java.util.Vector;

import net.safester.application.Help;
import net.safester.application.Login;
import net.safester.application.NewsFrame;
import net.safester.application.http.ApiRegister;
import net.safester.application.http.KawanHttpClientBuilder;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.CryptoParms;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.UI_Util;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.HtmlTextUtil;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.PolicyInstaller;
import net.safester.application.util.proxy.ProxySessionCheckerNew;
import net.safester.application.wait.tools.CmWaitDialog;
import net.safester.clientserver.ServerParms;
import static com.swing.util.ButtonUrlOver.exit;

public class Register extends javax.swing.JFrame {

   public static final String ERROR_ACCOUNT_ALREADY_EXISTS = "error_account_already_exists";
        
    public static final String BLANKS_TOO_SHORT = getBlanks(0);
    public static final String BLANKS_WEAK = getBlanks(15);
    public static final String BLANKS_MEDIUM = getBlanks(25);
    public static final String BLANKS_STRONG = getBlanks(40);

    // Passphrase quality values
    public static final int QUALITY_TOO_SHORT = 1;
    public static final int QUALITY_WEAK = 2;
    public static final int QUALITY_MEDIUM = 3;
    public static final int QUALITY_STRONG = 4;
    private Date expirationDate = null;

    private String algoAsym = CryptoParms.KEY_ALGO_ASYM_DSA_ELGAMAL;
    private int asymKeyLength = Integer.parseInt(CryptoParms.KEY_LENGTHS_ASYM_2048);
    private String algoSymmetric = CryptoParms.KEY_ALGOS_SYM_AES_256;

    //The private key bloc
    private String ascPrivKey = null;
    //The public key bloc
    private String ascPubKey = null;
    private MessagesManager messages = new MessagesManager();
    /**
     * The default passphrase echo char
     */
    private char defaultEchocar;

    /**
     * The wait dialog
     */
    CmWaitDialog cmWaitDialog = null;

    //private Connection connection;
    private AwakeFileSession awakeFileSession;

    private String originalEmail;

    /**
     * The parent frame
     */
    private JFrame parent = null;

    private JFrame thisOne = this;

    private HttpProxy httpProxy = null;

    private Vector<Component> focusList =  new Vector<>();
    

    /**
     *
     * @param parent
     * @param email the email parameter (optional)
     */
    public Register(JFrame parent, String email) {
        
        this.parent = parent;
        this.originalEmail = email;

        initComponents();
        initCompany();

        // To be done BEFORE proxy tests (because we crypt the line)
//        if (!Safester.testPolicyFile()) {
//            System.exit(0);
//        }
        PolicyInstaller.setCryptographyRestrictions();

        if (!proxyInit()) {
            return;
        }

        parent.setVisible(false);
        this.setVisible(true);

    }

    private void initCompany() {

        //clipboardManager = new ClipboardManager(rootPane);
        messages = new MessagesManager();

        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        this.setTitle(messages.getMessage("create_new_account"));
        defaultEchocar = this.jPassword.getEchoChar();

        jTextFieldUserEmail.setText(originalEmail);
        jTextFieldUserFirstName.setText(null);
        jTextFieldUserName.setText(null);
        jPassword.setText(null);
        jPassword1.setText(null);

        jEditorPaneEmail.setContentType("text/html");
        jEditorPaneEmail.setEditable(false);
        jEditorPaneEmail.setText(Help.getHtmlHelpContent("register_email"));

        // Hyperlink listener that will open a new Broser with the given URL
        jEditorPaneEmail.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent r) {
                if (r.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        java.awt.Desktop dekstop = java.awt.Desktop.getDesktop();
                        dekstop.browse(r.getURL().toURI());
                    } catch (Exception e) {
                        // We don't care
                        e.printStackTrace();
                    }
                }
            }
        });

        // Add + 2 to JEditorPane fonts
        int increaseFont = 0;
        
        if ( !SystemUtils.IS_OS_LINUX) {
            increaseFont = 2;
        }
     
        Font fontNewJEditorPane = new Font(jEditorPane.getFont().getName(), jEditorPane.getFont().getStyle(), jEditorPane.getFont().getSize() + increaseFont);
        
        jEditorPaneEmail.setFont(fontNewJEditorPane);
        
        jEditorPaneEmail.setBackground(Color.WHITE);
        jEditorPane.setBackground(Color.WHITE);

        jLabelQualityText.setText(null);

        // For Mac OS X behavior (rounded default buttons)
        jButtonCryptoSettings.putClientProperty("JButton.buttonType", "square");

        jLabelTitle.setText(this.getTitle());

        jLabelUserName.setText(messages.getMessage("user_first_last_name"));
        jLabelUserEmail.setText(messages.getMessage("email_address"));
        jLabelEmailHelp.setText(messages.getMessage("existing_email_address") + " ");
        jLabelPassphrase.setText(messages.getMessage("passphrase"));
        jLabelRetypePassphrase.setText(messages.getMessage("confirm_passphrase"));
        jLabelKeyboardWarning.setText(null);

        jButtonCryptoSettings.putClientProperty("JButton.buttonType", "square");

        //jLabelQualityText.setText("null");
        setCryptographicSettingsLabel();

        jButtonCryptoSettings.setText("<html>" + messages.getMessage("more_info") + "</html>");

        //jButtonAdvencedSettings.setText("<html><u>" + messages.getMessage("advanced_settings") + "</u></html>");
        jLabelPassphraseQuality.setText(messages.getMessage("passphrase_quality"));
        jButtonCancel.setText(messages.getMessage("cancel"));

        jButtonCreate.setText(messages.getMessage("create"));
        //Font f = jButtonCreate.getFont();
        //Font fontNew = new Font(f.getName(), f.getStyle(), f.getSize());

        jEditorPane.setFont(fontNewJEditorPane);
        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);
        jEditorPane.setText(Help.getHtmlHelpContent("register_passphrase"));

        // Hyperlink listener that will open the Passprase Quality window
        jEditorPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent r) {
                if (r.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    String content = HtmlTextUtil.getHtmlHelpContent((messages.getMessage("HELP_REGISTER_4")));
                    new NewsFrame(thisOne, content, messages.getMessage("help"));

                    //new Help(thisOne, messages.getMessage("HELP_REGISTER_4"));
                }
            }
        });

        jCheckBoxHideTyping.setSelected(true);
        jLabelHideTyping.setText(messages.getMessage("hide_typing"));

        ButtonResizer br = new ButtonResizer(jPanelButtons);
        br.setWidthToMax();
        jButtonCreate.setEnabled(false);

        keyListenerAdder();

        SwingUtil.applySwingUpdates(rootPane);

        //if (SystemUtils.IS_OS_LINUX)
        if (UI_Util.isNimbus()) {
            jPanelUserInfo.setPreferredSize(new Dimension(552, 124));
        }

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        Point middlePoint = new Point((dim.width / 2) - (this.getWidth() / 2),
                (dim.height / 2) - (this.getHeight() / 2));

        this.setLocation(middlePoint);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                WindowSettingManager.displayWindowSettings(thisOne);
            }

        });

        focusList.add(jTextFieldUserFirstName);
        focusList.add(jTextFieldUserName);
        focusList.add(jTextFieldUserEmail);
        focusList.add(jPassword);
        focusList.add(jPassword1);
        focusList.add(jCheckBoxHideTyping);
                     
        FocusTraversalPolicy focusTraversalPolicy =  new MyOwnFocusTraversalPolicy(focusList);
        this.setFocusTraversalPolicy(focusTraversalPolicy);
        
        this.setSize(602, 602);

        testCapsOn();

        jTextFieldUserFirstName.requestFocusInWindow();

    }

    /**
     * Return a HttpProtocolParameters instance, with an encryption password
     * @return 
     */
    public static HttpProtocolParameters getHttpProtocolParameters() {
        HttpProtocolParameters httpProtocolParameters = new HttpProtocolParameters();
        httpProtocolParameters.setEncryptionPassword("mypassword123$".toCharArray());
        return httpProtocolParameters;
    }

    private boolean proxyInit() {
        // Start wait dialog with "server contacted" message
        cmWaitDialog = new CmWaitDialog(this,
                this.messages.getMessage("in_progress"),
                this.messages.getMessage("contacting_server"),
                null);
        cmWaitDialog.startWaiting();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        System.out.println();
        System.out.println(new Date() + " Login... Detecting Proxy...");

        ProxySessionCheckerNew proxySessionCheckerNew = new ProxySessionCheckerNew(this, cmWaitDialog, null, null);
        try {
            if (!proxySessionCheckerNew.check()) {
                this.setCursor(Cursor.getDefaultCursor());
                cmWaitDialog.stopWaiting();
                System.exit(0); // Exit if no proxy setting
            }

            if (proxySessionCheckerNew.doDownloadNewVersion()) {
                this.setCursor(Cursor.getDefaultCursor());
                cmWaitDialog.stopWaiting();
                return false;
            }
        } catch (UnknownHostException e) {
            this.setCursor(Cursor.getDefaultCursor());
            cmWaitDialog.stopWaiting();
            JOptionPane.showMessageDialog(this, messages.getMessage("check_internet_connection"));
            return false;
        } catch (Exception ex) {
            this.setCursor(Cursor.getDefaultCursor());
            cmWaitDialog.stopWaiting();
            JOptionPaneNewCustom.showException(this, ex, messages.getMessage("unable_to_connect"));
            return false;
        }

        httpProxy = proxySessionCheckerNew.getHttpProxy();

        this.setCursor(Cursor.getDefaultCursor());
        cmWaitDialog.stopWaiting();

        return true;
    }

    public void setCryptographicSettingsLabel() {
        jLabelCryptoSettings.setText(messages.getMessage("cryptography_settings"));
        String cryptoSettings = algoAsym + " - " + asymKeyLength + " bits / " + algoSymmetric;
        jTextFieldCryptoSettings.setText(cryptoSettings);
    }

    /**
     * Generate keys.
     *
     * @param userEmail
     * @param passphrase
     * @throws IOException
     * @throws KeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws NumberFormatException
     * @throws PGPException
     */
    private void generateKeys(String lifeName, String userEmail, char[] passphrase)
            throws IOException, KeyException, InvalidAlgorithmParameterException,
            NoSuchProviderException, NoSuchAlgorithmException, NumberFormatException, PGPException {

        cmWaitDialog.changeText(this.messages.getMessage("key_generation_in_progress"));

        KeyHandlerOne kh = new KeyHandlerOne();
        byte[] seed = getSeed();

        String keySym = this.algoSymmetric.substring(0, algoSymmetric.indexOf("-")).trim();

        String length = algoSymmetric.substring(algoSymmetric.indexOf("-") + 1).trim();
        length = StringMgr.ReplaceAll(length, "bits", "").trim();
        int keyLength = Integer.parseInt(length);

        //Generate keyring in a byte array output stream
        OutputStream privKeyRing = new ByteArrayOutputStream();
        OutputStream pubKeyRing = new ByteArrayOutputStream();

        String pgpUserId = lifeName + " <" + userEmail.trim() + ">";

        kh.generateKeyPair(pgpUserId, passphrase, algoAsym, asymKeyLength, keySym, keyLength, seed, expirationDate, privKeyRing, pubKeyRing);

        byte[] privKey = ((ByteArrayOutputStream) privKeyRing).toByteArray();
        privKeyRing.close();

        byte[] pubKey = ((ByteArrayOutputStream) pubKeyRing).toByteArray();
        pubKeyRing.close();

        //Export keys in asc format
        ByteArrayInputStream inKeyRing = new ByteArrayInputStream(privKey);
        ascPrivKey = kh.getAscPgpPrivKey(userEmail, inKeyRing);
        inKeyRing.close();

        inKeyRing = new ByteArrayInputStream(pubKey);
        ascPubKey = kh.getAscPgpPubKey(userEmail, inKeyRing);

        //Re-open input stream on public key ring
        inKeyRing = new ByteArrayInputStream(pubKey);

        cmWaitDialog.changeText(this.messages.getMessage("uploading_keys_to_server"));

    }
    public String getAlgoAsym() {
        return algoAsym;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public int getKeyAsymLength() {
        return asymKeyLength;
    }

    public String getKeySymAlgo() {
        return algoSymmetric;
    }

    public void setAlgoAsym(String algoAsym) {
        this.algoAsym = algoAsym;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setKeyAsymLength(int keyAsymLength) {
        this.asymKeyLength = keyAsymLength;
    }

    public void setKeySymAlgo(String keySymAlgo) {
        this.algoSymmetric = keySymAlgo;
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
     * @return the quality of the passphrase, aka one of the following values:
     *
     * <br> - QUALITY_TOO_SHORT
     * <br> - QUALITY_WEAK
     * <br> - QUALITY_MEDIUM
     * <br> - QUALITY_STRONG
     *
     * @param cPassphrase the passphrase to check
     *
     * Algorithm may be enhanced in the futur
     */
    public static int getPassphraseQuality(char[] cPassphrase) {
        int quality = QUALITY_TOO_SHORT;

        if (cPassphrase.length < 6) {
            return QUALITY_TOO_SHORT;
        } else if (cPassphrase.length < 8) {
            return QUALITY_WEAK;
        } else if (cPassphrase.length < 10) {
            quality = QUALITY_MEDIUM;
        } else {
            quality = QUALITY_STRONG;
        }

        //
        // See if quality may be degraded
        //
        String s = new String(cPassphrase);

        // Weak if empty, blank.
        if (StringUtils.isEmpty(s)
                || StringUtils.isBlank(s)) {
            return QUALITY_WEAK;
        }

        // Is weak if always same char
        int countMatches = StringUtils.countMatches(s, s.substring(0, 1));

        if (countMatches == s.length()) {
            return QUALITY_WEAK;
        }

        //System.out.println("countMatches: " + countMatches);
        // Downgrade quality if only alpha or only digits
        if (StringUtils.isAlphaSpace(s)
                || StringUtils.isNumericSpace(s)
                || StringUtils.isEmpty(s)
                || StringUtils.isBlank(s)) {
            quality--;
        }

        return quality;

    }

    public static String getBlanks(int num) {
        String blanks = "";

        for (int i = 0; i < num; i++) {
            blanks += " ";
        }

        return blanks;
    }

    /**
     * Test0 if Caps is on
     *
     */
    private void testCapsOn() {

        // Nothing on Mac (has already a hint).
        if (SystemUtils.IS_OS_MAC_OSX) {
            return;
        }

        boolean capsOn = false;

        try {
            capsOn = java.awt.Toolkit.getDefaultToolkit().getLockingKeyState(
                    java.awt.event.KeyEvent.VK_CAPS_LOCK);
        } catch (Exception e) {
            return; // No trace necessary
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

    private void this_keyPressed(KeyEvent e) {
        testCapsOn();

        int id = e.getID();
        if (id == KeyEvent.KEY_PRESSED) {
            //System.out.println("Key Realeased");
            //System.out.println("TextFieldUserEmail.getText():" + jTextFieldUserEmail.getText() + ":");

            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_ESCAPE) {
                this.close();
                return;
            }

            if (getPassphraseQuality(jPassword.getPassword()) >= QUALITY_WEAK) {
                jButtonCreate.setEnabled(true);
            } else {
                jButtonCreate.setEnabled(false);
            }

            if (keyCode == KeyEvent.VK_ENTER && jButtonCreate.isEnabled()) {
                jButtonCreateActionPerformed(null);
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

    private boolean areEmailsEqual() {
        String email1 = jTextFieldUserEmail.getText().toLowerCase();
        String email2 = null;
        if (email2 == null) {
            return false;
        }
        email2 = email2.toLowerCase();
        if (email1.trim().equals(email2.trim())) {
            return true;
        }
        return false;
    }

    private boolean doIt() {

        String errorMsg = messages.getMessage("error");

        //Check emails identical
//        if (!areEmailsEqual()) {
//            JOptionPane.showMessageDialog(this, messages.getMessage("email_different"), errorMsg, JOptionPane.ERROR_MESSAGE);
//            return false;
//        }
        //Check email validity
        String userEmail = jTextFieldUserEmail.getText().toLowerCase().trim();
        EmailChecker emailChecker = new EmailChecker(userEmail);
        if (!emailChecker.isSyntaxValid()) {
            String msg = MessageFormat.format(messages.getMessage("email_not_vaild"), userEmail);
            JOptionPane.showMessageDialog(this, msg, errorMsg, JOptionPane.ERROR_MESSAGE);
            return false;
        }

        //Check passphrases identical
        if (!arePassphraseEqual()) {
            JOptionPane.showMessageDialog(this,
                    messages.getMessage("passphrase_different"), errorMsg, JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // start wait dialog with "server contacted" message
        cmWaitDialog = new CmWaitDialog(this,
                this.messages.getMessage("in_progress"),
                this.messages.getMessage("contacting_server"),
                null);

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        cmWaitDialog.startWaiting();

        userEmail = userEmail.toLowerCase();
        jTextFieldUserEmail.setText(userEmail);
        //String result = null;

        // Build the awake file session
        try {
            HttpProtocolParameters httpProtocolParameters = getHttpProtocolParameters();
            awakeFileSession = new AwakeFileSession(
                    ServerParms.getAwakeSqlServerUrl(),
                    null,
                    (char[]) null, // Constructor with password
                    httpProxy,
                    httpProtocolParameters);

            //result = awakeFileSession.call("net.safester.server.hosts.KeyPairCreatorV3.keyPairExist", userEmail);
        } catch (ConnectException e) {
            this.setCursor(Cursor.getDefaultCursor());
            cmWaitDialog.stopWaiting();
            if (e.toString().contains("status: 407")) {
                JOptionPane.showMessageDialog(this, messages.getMessage("your_proxy_requires_authentication"));
            } else {
                JOptionPane.showMessageDialog(this, messages.getMessage("safester_server_is_under_maintenance"));
            }
            return false;
        } catch (UnknownHostException e) {
            this.setCursor(Cursor.getDefaultCursor());
            cmWaitDialog.stopWaiting();
            JOptionPane.showMessageDialog(this, messages.getMessage("safester_server_is_under_maintenance"));
            return false;
        } catch (Exception e) {
            this.setCursor(Cursor.getDefaultCursor());
            cmWaitDialog.stopWaiting();
            e.printStackTrace();
            JOptionPaneNewCustom.showException(this, e);
            return false;
        }

        String firstname = jTextFieldUserFirstName.getText();
        String lastname = jTextFieldUserName.getText();
        String lifeName = firstname + " " + lastname;

        char[] passphrase = this.jPassword.getPassword();

        //  String userId = firstname + " " + lastname + " <" + email + ">";
        try {

            //Generate the key pair
            generateKeys(lifeName, userEmail, passphrase);

           //Store keys in db. If not OK, accounts already exists. All other problem trigger an Exception
            boolean ok = callRegisterApi(lifeName, userEmail, passphrase);
            
            if (! ok) {
                this.setCursor(Cursor.getDefaultCursor());
                cmWaitDialog.stopWaiting();
                JOptionPane.showMessageDialog(this, messages.getMessage("email_already_used"), errorMsg, JOptionPane.ERROR_MESSAGE);
                return false;
            }
                    
            cmWaitDialog.stopWaiting();
            this.setCursor(Cursor.getDefaultCursor());
            String registrationDone = SwingUtil.getTextContent("registration_done");
            registrationDone = MessageFormat.format(registrationDone, userEmail);

            JOptionPane.showMessageDialog(this, registrationDone);

            //SafeShareItLogin login = new SafeShareItLogin(ServerParms.getHOST(), email);
            //safeShareItLogin.setVisible(true);
            if (parent != null && parent instanceof Login) {
                Login login = (Login) parent;
                login.setLogin(userEmail);
            }

            this.dispose();
            return true;

        } catch (Exception e) {
            cmWaitDialog.stopWaiting();
            this.setCursor(Cursor.getDefaultCursor());
            e.printStackTrace();
            JOptionPaneNewCustom.showException(rootPane, e);
            return false;
        }
    }

    /**
     * Store on the server a key pari by creating a PgpKey instance
     *
     * @param lifeName the user name
     * @param userEmail the user id
     * @param passphrase the user very secret passphrase
     * login
     * @throws SQLException
     */
    private boolean callRegisterApi(String lifeName, String userEmail, char[] passphrase)
            throws Exception {

        lifeName = HtmlConverter.toHtml(lifeName);
        
        KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.build(httpProxy);
        ApiRegister apiRegister = new ApiRegister(kawanHttpClient);
        
        boolean ok = apiRegister.register(userEmail, lifeName, passphrase, ascPrivKey, ascPubKey);
        
        if (ok) {
            return true;
        }
        
        String errorMessage = apiRegister.getErrorMessage();
        if (errorMessage != null && errorMessage.equals(ERROR_ACCOUNT_ALREADY_EXISTS)) {
            return false;
        }
        else {
            throw new RemoteException(apiRegister.getErrorMessage(), new Exception(apiRegister.getExceptionName()), apiRegister.getExceptionStackTrace());
        }
    }
    
    /**
     * Get a seed from a file (if file does not exist creates it)
     *
     * @return byte[] containing seed
     */
    private byte[] getSeed() {

        try {

            //File seedFile = new File(System.getProperty("java.io.tmpdir") + "seed.bin");
            File seedFile = new File(Parms.getSafesterTempDir() + "seed.bin");

            if (!seedFile.exists()) {
                CgeepApiTools.generateSeed(seedFile);
            }
            InputStream in = new FileInputStream(seedFile);
            int seedLength = in.available();
            byte[] seed = new byte[seedLength];
            in.read(seed);
            in.close();
            return seed;
        } catch (Exception e) {
            return null;
        }

    }

    private void close() {
        // We return to Login
        this.dispose();
        parent.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelCenter = new javax.swing.JPanel();
        jPanelLogo = new javax.swing.JPanel();
        jPanelLogoLeft = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabelMiniIcon = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelLogoRight = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanelSep1 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jPanel27 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel31 = new javax.swing.JPanel();
        jPanelHelpEmail = new javax.swing.JPanel();
        jPanelLeftHelp1 = new javax.swing.JPanel();
        jPanelEditorHelp1 = new javax.swing.JPanel();
        jEditorPaneEmail = new javax.swing.JEditorPane();
        jPanelRightHelp1 = new javax.swing.JPanel();
        jPanelBlank3 = new javax.swing.JPanel();
        jPanelUserIdentification = new javax.swing.JPanel();
        jPanelUserInfo = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanelName = new javax.swing.JPanel();
        jLabelUserName = new javax.swing.JLabel();
        jTextFieldUserFirstName = new javax.swing.JTextField();
        jTextFieldUserName = new javax.swing.JTextField();
        jPanelEmail1 = new javax.swing.JPanel();
        jLabelUserEmail = new javax.swing.JLabel();
        jTextFieldUserEmail = new javax.swing.JTextField();
        jLabelEmailHelp = new javax.swing.JLabel();
        jPanelPassphrase = new javax.swing.JPanel();
        jPanelSep2 = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel29 = new javax.swing.JPanel();
        jPanelCryptographySettings = new javax.swing.JPanel();
        jLabelCryptoSettings = new javax.swing.JLabel();
        jTextFieldCryptoSettings = new javax.swing.JTextField();
        jButtonCryptoSettings = new javax.swing.JButton();
        jPanelBlank = new javax.swing.JPanel();
        jPanelHelpPassphrase = new javax.swing.JPanel();
        jPanelLeftHelp = new javax.swing.JPanel();
        jPanelEditorHelp = new javax.swing.JPanel();
        jEditorPane = new javax.swing.JEditorPane();
        jPanelRightHelp = new javax.swing.JPanel();
        jPanelBlank2 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jPanelPassphrase1 = new javax.swing.JPanel();
        jPanelLeft1 = new javax.swing.JPanel();
        jLabelPassphrase = new javax.swing.JLabel();
        jPanelRight1 = new javax.swing.JPanel();
        jPassword = new javax.swing.JPasswordField();
        jPanel11 = new javax.swing.JPanel();
        jPanelPassphrase3 = new javax.swing.JPanel();
        jPanelLeft2 = new javax.swing.JPanel();
        jLabelRetypePassphrase = new javax.swing.JLabel();
        jPanelRight2 = new javax.swing.JPanel();
        jPassword1 = new javax.swing.JPasswordField();
        jPanel14 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jPanel34 = new javax.swing.JPanel();
        jLabelHideTyping = new javax.swing.JLabel();
        jCheckBoxHideTyping = new javax.swing.JCheckBox();
        jLabelKeyboardWarning = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        jLabelPassphraseQuality = new javax.swing.JLabel();
        jLabelQualityText = new javax.swing.JLabel();
        jPanelSep = new javax.swing.JPanel();
        jPanelSepLeft = new javax.swing.JPanel();
        jSeparator5 = new javax.swing.JSeparator();
        jPanelSepRight = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonCreate = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel30 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelLogo.setMaximumSize(new java.awt.Dimension(32767, 55));
        jPanelLogo.setMinimumSize(new java.awt.Dimension(520, 55));
        jPanelLogo.setPreferredSize(new java.awt.Dimension(10, 55));
        jPanelLogo.setLayout(new java.awt.GridLayout(1, 0));

        jPanelLogoLeft.setLayout(new javax.swing.BoxLayout(jPanelLogoLeft, javax.swing.BoxLayout.LINE_AXIS));

        jPanel3.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelLogoLeft.add(jPanel3);

        jLabelMiniIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/key_plus.png"))); // NOI18N
        jPanelLogoLeft.add(jLabelMiniIcon);

        jPanel5.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelLogoLeft.add(jPanel5);

        jLabelTitle.setText("jLabelTitle");
        jPanelLogoLeft.add(jLabelTitle);

        jPanelLogo.add(jPanelLogoLeft);

        jPanelLogoRight.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 5, 10));

        jPanel8.setMaximumSize(new java.awt.Dimension(1, 10));
        jPanel8.setMinimumSize(new java.awt.Dimension(1, 10));
        jPanel8.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelLogoRight.add(jPanel8);

        jPanelLogo.add(jPanelLogoRight);

        jPanelCenter.add(jPanelLogo);

        jPanelSep1.setMaximumSize(new java.awt.Dimension(32767, 20));
        jPanelSep1.setMinimumSize(new java.awt.Dimension(390, 20));
        jPanelSep1.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanelSep1.setLayout(new javax.swing.BoxLayout(jPanelSep1, javax.swing.BoxLayout.LINE_AXIS));

        jPanel26.setMaximumSize(new java.awt.Dimension(32787, 10));
        jPanel26.setMinimumSize(new java.awt.Dimension(390, 10));
        jPanel26.setLayout(new javax.swing.BoxLayout(jPanel26, javax.swing.BoxLayout.LINE_AXIS));

        jPanel27.setMaximumSize(new java.awt.Dimension(10, 2));
        jPanel26.add(jPanel27);

        jSeparator3.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator3.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator3.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanel26.add(jSeparator3);

        jPanel31.setMaximumSize(new java.awt.Dimension(10, 2));
        jPanel26.add(jPanel31);

        jPanelSep1.add(jPanel26);

        jPanelCenter.add(jPanelSep1);

        jPanelHelpEmail.setLayout(new javax.swing.BoxLayout(jPanelHelpEmail, javax.swing.BoxLayout.X_AXIS));

        jPanelLeftHelp1.setMaximumSize(new java.awt.Dimension(40, 10));
        jPanelLeftHelp1.setMinimumSize(new java.awt.Dimension(40, 10));
        jPanelLeftHelp1.setPreferredSize(new java.awt.Dimension(40, 10));
        jPanelHelpEmail.add(jPanelLeftHelp1);

        jPanelEditorHelp1.setLayout(new javax.swing.BoxLayout(jPanelEditorHelp1, javax.swing.BoxLayout.LINE_AXIS));

        jEditorPaneEmail.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jEditorPaneEmail.setMaximumSize(new java.awt.Dimension(2147483647, 200));
        jEditorPaneEmail.setPreferredSize(new java.awt.Dimension(106, 126));
        jPanelEditorHelp1.add(jEditorPaneEmail);

        jPanelHelpEmail.add(jPanelEditorHelp1);

        jPanelRightHelp1.setMaximumSize(new java.awt.Dimension(40, 10));
        jPanelRightHelp1.setMinimumSize(new java.awt.Dimension(40, 10));
        jPanelRightHelp1.setPreferredSize(new java.awt.Dimension(40, 10));
        jPanelHelpEmail.add(jPanelRightHelp1);

        jPanelCenter.add(jPanelHelpEmail);

        jPanelBlank3.setMaximumSize(new java.awt.Dimension(32767, 8));
        jPanelBlank3.setMinimumSize(new java.awt.Dimension(0, 8));
        jPanelBlank3.setPreferredSize(new java.awt.Dimension(547, 8));

        javax.swing.GroupLayout jPanelBlank3Layout = new javax.swing.GroupLayout(jPanelBlank3);
        jPanelBlank3.setLayout(jPanelBlank3Layout);
        jPanelBlank3Layout.setHorizontalGroup(
            jPanelBlank3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 632, Short.MAX_VALUE)
        );
        jPanelBlank3Layout.setVerticalGroup(
            jPanelBlank3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelBlank3);

        jPanelUserIdentification.setMinimumSize(new java.awt.Dimension(10, 90));
        jPanelUserIdentification.setRequestFocusEnabled(false);
        jPanelUserIdentification.setLayout(new javax.swing.BoxLayout(jPanelUserIdentification, javax.swing.BoxLayout.Y_AXIS));

        jPanelUserInfo.setMinimumSize(new java.awt.Dimension(10, 93));
        jPanelUserInfo.setLayout(new java.awt.GridLayout(1, 0));

        jPanel9.setMaximumSize(new java.awt.Dimension(32767, 105));
        jPanel9.setPreferredSize(new java.awt.Dimension(445, 93));
        jPanel9.setLayout(new java.awt.GridLayout(3, 0));

        jPanelName.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelName.setMinimumSize(new java.awt.Dimension(83, 31));
        jPanelName.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));

        jLabelUserName.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelUserName.setText("jLabelUserName");
        jLabelUserName.setPreferredSize(new java.awt.Dimension(170, 16));
        jPanelName.add(jLabelUserName);

        jTextFieldUserFirstName.setText("jTextFieldUserEmail");
        jTextFieldUserFirstName.setMinimumSize(new java.awt.Dimension(100, 22));
        jTextFieldUserFirstName.setPreferredSize(new java.awt.Dimension(100, 22));
        jPanelName.add(jTextFieldUserFirstName);

        jTextFieldUserName.setText("jTextFieldUserName");
        jTextFieldUserName.setMinimumSize(new java.awt.Dimension(160, 22));
        jTextFieldUserName.setPreferredSize(new java.awt.Dimension(160, 22));
        jPanelName.add(jTextFieldUserName);

        jPanel9.add(jPanelName);

        jPanelEmail1.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelEmail1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));

        jLabelUserEmail.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelUserEmail.setText("jLabelUserEmail");
        jLabelUserEmail.setPreferredSize(new java.awt.Dimension(170, 16));
        jPanelEmail1.add(jLabelUserEmail);

        jTextFieldUserEmail.setText("jTextFieldUserEmail");
        jTextFieldUserEmail.setMinimumSize(new java.awt.Dimension(265, 22));
        jTextFieldUserEmail.setPreferredSize(new java.awt.Dimension(265, 22));
        jPanelEmail1.add(jTextFieldUserEmail);

        jLabelEmailHelp.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        jLabelEmailHelp.setText("jLabelEmailHelp ");
        jPanelEmail1.add(jLabelEmailHelp);

        jPanel9.add(jPanelEmail1);

        jPanelUserInfo.add(jPanel9);

        jPanelUserIdentification.add(jPanelUserInfo);

        jPanelCenter.add(jPanelUserIdentification);

        jPanelPassphrase.setLayout(new javax.swing.BoxLayout(jPanelPassphrase, javax.swing.BoxLayout.Y_AXIS));

        jPanelSep2.setMaximumSize(new java.awt.Dimension(32767, 16));
        jPanelSep2.setMinimumSize(new java.awt.Dimension(390, 16));
        jPanelSep2.setPreferredSize(new java.awt.Dimension(20, 16));
        jPanelSep2.setLayout(new javax.swing.BoxLayout(jPanelSep2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel24.setMaximumSize(new java.awt.Dimension(32787, 6));
        jPanel24.setMinimumSize(new java.awt.Dimension(390, 6));
        jPanel24.setPreferredSize(new java.awt.Dimension(20, 6));
        jPanel24.setLayout(new javax.swing.BoxLayout(jPanel24, javax.swing.BoxLayout.LINE_AXIS));

        jPanel25.setMaximumSize(new java.awt.Dimension(10, 2));
        jPanel24.add(jPanel25);

        jSeparator2.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator2.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator2.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanel24.add(jSeparator2);

        jPanel29.setMaximumSize(new java.awt.Dimension(10, 2));
        jPanel24.add(jPanel29);

        jPanelSep2.add(jPanel24);

        jPanelPassphrase.add(jPanelSep2);

        jPanelCryptographySettings.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelCryptographySettings.setMinimumSize(new java.awt.Dimension(427, 31));
        jPanelCryptographySettings.setPreferredSize(new java.awt.Dimension(497, 31));
        jPanelCryptographySettings.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelCryptoSettings.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelCryptoSettings.setText("jLabelCryptoSettings");
        jLabelCryptoSettings.setPreferredSize(new java.awt.Dimension(170, 16));
        jPanelCryptographySettings.add(jLabelCryptoSettings);

        jTextFieldCryptoSettings.setEditable(false);
        jTextFieldCryptoSettings.setText("jTextFieldCryptoSettings");
        jTextFieldCryptoSettings.setMaximumSize(new java.awt.Dimension(285, 22));
        jTextFieldCryptoSettings.setMinimumSize(new java.awt.Dimension(285, 22));
        jTextFieldCryptoSettings.setPreferredSize(new java.awt.Dimension(285, 22));
        jPanelCryptographySettings.add(jTextFieldCryptoSettings);

        jButtonCryptoSettings.setForeground(new java.awt.Color(0, 0, 255));
        jButtonCryptoSettings.setText("More Info");
        jButtonCryptoSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButtonCryptoSettings.setBorderPainted(false);
        jButtonCryptoSettings.setContentAreaFilled(false);
        jButtonCryptoSettings.setFocusPainted(false);
        jButtonCryptoSettings.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButtonCryptoSettings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonCryptoSettingsMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonCryptoSettingsMouseExited(evt);
            }
        });
        jButtonCryptoSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCryptoSettingsActionPerformed(evt);
            }
        });
        jPanelCryptographySettings.add(jButtonCryptoSettings);

        jPanelPassphrase.add(jPanelCryptographySettings);

        jPanelBlank.setMaximumSize(new java.awt.Dimension(32767, 8));
        jPanelBlank.setMinimumSize(new java.awt.Dimension(10, 8));
        jPanelBlank.setPreferredSize(new java.awt.Dimension(10, 8));
        jPanelPassphrase.add(jPanelBlank);

        jPanelHelpPassphrase.setLayout(new javax.swing.BoxLayout(jPanelHelpPassphrase, javax.swing.BoxLayout.X_AXIS));

        jPanelLeftHelp.setMaximumSize(new java.awt.Dimension(40, 10));
        jPanelLeftHelp.setMinimumSize(new java.awt.Dimension(40, 10));
        jPanelLeftHelp.setPreferredSize(new java.awt.Dimension(40, 10));
        jPanelHelpPassphrase.add(jPanelLeftHelp);

        jPanelEditorHelp.setLayout(new javax.swing.BoxLayout(jPanelEditorHelp, javax.swing.BoxLayout.LINE_AXIS));

        jEditorPane.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jEditorPane.setMaximumSize(new java.awt.Dimension(2147483647, 100));
        jEditorPane.setPreferredSize(new java.awt.Dimension(170, 166));
        jPanelEditorHelp.add(jEditorPane);

        jPanelHelpPassphrase.add(jPanelEditorHelp);

        jPanelRightHelp.setMaximumSize(new java.awt.Dimension(40, 10));
        jPanelRightHelp.setMinimumSize(new java.awt.Dimension(40, 10));
        jPanelRightHelp.setPreferredSize(new java.awt.Dimension(40, 10));
        jPanelHelpPassphrase.add(jPanelRightHelp);

        jPanelPassphrase.add(jPanelHelpPassphrase);

        jPanelBlank2.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelBlank2.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelBlank2.setPreferredSize(new java.awt.Dimension(547, 10));

        javax.swing.GroupLayout jPanelBlank2Layout = new javax.swing.GroupLayout(jPanelBlank2);
        jPanelBlank2.setLayout(jPanelBlank2Layout);
        jPanelBlank2Layout.setHorizontalGroup(
            jPanelBlank2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 632, Short.MAX_VALUE)
        );
        jPanelBlank2Layout.setVerticalGroup(
            jPanelBlank2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelPassphrase.add(jPanelBlank2);

        jPanel19.setLayout(new java.awt.GridLayout(2, 0, 0, 5));

        jPanelPassphrase1.setMaximumSize(new java.awt.Dimension(2147483647, 32));
        jPanelPassphrase1.setMinimumSize(new java.awt.Dimension(389, 32));
        jPanelPassphrase1.setPreferredSize(new java.awt.Dimension(525, 32));
        jPanelPassphrase1.setLayout(new javax.swing.BoxLayout(jPanelPassphrase1, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft1.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelLeft1.setMinimumSize(new java.awt.Dimension(44, 31));
        jPanelLeft1.setPreferredSize(new java.awt.Dimension(180, 31));
        jPanelLeft1.setLayout(new javax.swing.BoxLayout(jPanelLeft1, javax.swing.BoxLayout.LINE_AXIS));

        jLabelPassphrase.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelPassphrase.setText("jLabelPassphrase");
        jLabelPassphrase.setMaximumSize(new java.awt.Dimension(175, 16));
        jLabelPassphrase.setMinimumSize(new java.awt.Dimension(175, 16));
        jLabelPassphrase.setPreferredSize(new java.awt.Dimension(175, 16));
        jPanelLeft1.add(jLabelPassphrase);

        jPanelPassphrase1.add(jPanelLeft1);

        jPanelRight1.setMaximumSize(new java.awt.Dimension(2147483647, 31));
        jPanelRight1.setMinimumSize(new java.awt.Dimension(295, 31));
        jPanelRight1.setPreferredSize(new java.awt.Dimension(295, 31));
        jPanelRight1.setLayout(new javax.swing.BoxLayout(jPanelRight1, javax.swing.BoxLayout.LINE_AXIS));

        jPassword.setText("jPasswordField1");
        jPassword.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jPassword.setMinimumSize(new java.awt.Dimension(295, 22));
        jPassword.setPreferredSize(new java.awt.Dimension(295, 22));
        jPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPasswordKeyPressed(evt);
            }
        });
        jPanelRight1.add(jPassword);

        jPanelPassphrase1.add(jPanelRight1);

        jPanel11.setMaximumSize(new java.awt.Dimension(40, 20));
        jPanel11.setMinimumSize(new java.awt.Dimension(40, 20));
        jPanel11.setPreferredSize(new java.awt.Dimension(40, 20));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jPanelPassphrase1.add(jPanel11);

        jPanel19.add(jPanelPassphrase1);

        jPanelPassphrase3.setMaximumSize(new java.awt.Dimension(2147483647, 32));
        jPanelPassphrase3.setMinimumSize(new java.awt.Dimension(389, 32));
        jPanelPassphrase3.setPreferredSize(new java.awt.Dimension(525, 32));
        jPanelPassphrase3.setLayout(new javax.swing.BoxLayout(jPanelPassphrase3, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft2.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelLeft2.setMinimumSize(new java.awt.Dimension(44, 31));
        jPanelLeft2.setPreferredSize(new java.awt.Dimension(180, 31));
        jPanelLeft2.setLayout(new javax.swing.BoxLayout(jPanelLeft2, javax.swing.BoxLayout.LINE_AXIS));

        jLabelRetypePassphrase.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelRetypePassphrase.setText("jLabelRetypePassphrase");
        jLabelRetypePassphrase.setMaximumSize(new java.awt.Dimension(175, 16));
        jLabelRetypePassphrase.setMinimumSize(new java.awt.Dimension(175, 16));
        jLabelRetypePassphrase.setPreferredSize(new java.awt.Dimension(175, 16));
        jPanelLeft2.add(jLabelRetypePassphrase);

        jPanelPassphrase3.add(jPanelLeft2);

        jPanelRight2.setMaximumSize(new java.awt.Dimension(2147483647, 31));
        jPanelRight2.setMinimumSize(new java.awt.Dimension(295, 31));
        jPanelRight2.setPreferredSize(new java.awt.Dimension(295, 31));
        jPanelRight2.setLayout(new javax.swing.BoxLayout(jPanelRight2, javax.swing.BoxLayout.LINE_AXIS));

        jPassword1.setText("jPasswordField1");
        jPassword1.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jPassword1.setMinimumSize(new java.awt.Dimension(295, 22));
        jPassword1.setPreferredSize(new java.awt.Dimension(295, 22));
        jPanelRight2.add(jPassword1);

        jPanelPassphrase3.add(jPanelRight2);

        jPanel14.setMaximumSize(new java.awt.Dimension(40, 20));
        jPanel14.setMinimumSize(new java.awt.Dimension(40, 20));
        jPanel14.setPreferredSize(new java.awt.Dimension(40, 20));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jPanelPassphrase3.add(jPanel14);

        jPanel19.add(jPanelPassphrase3);

        jPanelPassphrase.add(jPanel19);

        jPanel22.setLayout(new javax.swing.BoxLayout(jPanel22, javax.swing.BoxLayout.Y_AXIS));

        jPanel34.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanel34.setMinimumSize(new java.awt.Dimension(145, 31));
        jPanel34.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));

        jLabelHideTyping.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelHideTyping.setText("jLabelHideTyping");
        jLabelHideTyping.setMinimumSize(new java.awt.Dimension(135, 14));
        jLabelHideTyping.setPreferredSize(new java.awt.Dimension(167, 14));
        jPanel34.add(jLabelHideTyping);

        jCheckBoxHideTyping.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jCheckBoxHideTyping.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
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
        jPanel34.add(jCheckBoxHideTyping);

        jLabelKeyboardWarning.setText("jLabelKeyboardWarning");
        jPanel34.add(jLabelKeyboardWarning);

        jPanel22.add(jPanel34);

        jPanel23.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanel23.setMinimumSize(new java.awt.Dimension(234, 31));
        jPanel23.setPreferredSize(new java.awt.Dimension(227, 31));
        jPanel23.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelPassphraseQuality.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelPassphraseQuality.setText("jLabelPassphraseQuality");
        jLabelPassphraseQuality.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabelPassphraseQuality.setPreferredSize(new java.awt.Dimension(170, 16));
        jPanel23.add(jLabelPassphraseQuality);

        jLabelQualityText.setText("jLabelQualityText");
        jLabelQualityText.setMaximumSize(new java.awt.Dimension(295, 16));
        jLabelQualityText.setMinimumSize(new java.awt.Dimension(295, 16));
        jLabelQualityText.setPreferredSize(new java.awt.Dimension(295, 16));
        jPanel23.add(jLabelQualityText);

        jPanel22.add(jPanel23);

        jPanelPassphrase.add(jPanel22);

        jPanelSep.setMinimumSize(new java.awt.Dimension(10, 6));
        jPanelSep.setPreferredSize(new java.awt.Dimension(10, 6));
        jPanelSep.setLayout(new javax.swing.BoxLayout(jPanelSep, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSepLeft.setMaximumSize(new java.awt.Dimension(10, 2));
        jPanelSepLeft.setMinimumSize(new java.awt.Dimension(10, 2));
        jPanelSepLeft.setPreferredSize(new java.awt.Dimension(10, 2));
        jPanelSep.add(jPanelSepLeft);

        jSeparator5.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator5.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator5.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSep.add(jSeparator5);

        jPanelSepRight.setMaximumSize(new java.awt.Dimension(10, 2));
        jPanelSepRight.setMinimumSize(new java.awt.Dimension(10, 2));
        jPanelSepRight.setPreferredSize(new java.awt.Dimension(10, 2));
        jPanelSep.add(jPanelSepRight);

        jPanelPassphrase.add(jPanelSep);

        jPanelCenter.add(jPanelPassphrase);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setMaximumSize(new java.awt.Dimension(32767, 43));
        jPanelSouth.setMinimumSize(new java.awt.Dimension(319, 43));
        jPanelSouth.setPreferredSize(new java.awt.Dimension(319, 43));
        jPanelSouth.setLayout(new javax.swing.BoxLayout(jPanelSouth, javax.swing.BoxLayout.LINE_AXIS));

        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 5, 10));

        jButtonCreate.setText("Create");
        jButtonCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonCreate);

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonCancel);

        jPanel30.setMaximumSize(new java.awt.Dimension(0, 32767));
        jPanel30.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanel30.setPreferredSize(new java.awt.Dimension(0, 10));

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelButtons.add(jPanel30);

        jPanelSouth.add(jPanelButtons);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed

        this.close();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateActionPerformed
        boolean okDone = doIt();
        if (okDone) {
            parent.setVisible(true);
        }

    }//GEN-LAST:event_jButtonCreateActionPerformed

    private void jPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordKeyPressed
        char[] cPassphrase = jPassword.getPassword();

        if (cPassphrase.length == 0) {
            jLabelQualityText.setText(null);
            jLabelQualityText.setBackground(jLabelPassphraseQuality.getBackground());
            jLabelQualityText.setForeground(jLabelPassphraseQuality.getForeground());
        } else {
            jLabelQualityText.setOpaque(true);
            jLabelQualityText.setForeground(Color.BLACK);

            String qualityMsg = "";

            int passphrase_quality = getPassphraseQuality(jPassword.getPassword());

            if (passphrase_quality == QUALITY_TOO_SHORT) {
                qualityMsg = messages.getMessage("reg_pass_quality_too_short");
                qualityMsg += BLANKS_TOO_SHORT;
                jLabelQualityText.setText(qualityMsg);
                jLabelQualityText.setBackground(Color.BLACK);
                jLabelQualityText.setForeground(Color.WHITE);

            } else if (passphrase_quality == QUALITY_WEAK) {
                qualityMsg = messages.getMessage("reg_pass_quality_weak");
                qualityMsg += BLANKS_WEAK;
                jLabelQualityText.setText(qualityMsg);
                jLabelQualityText.setBackground(Color.RED);
            } else if (passphrase_quality == QUALITY_MEDIUM) {
                qualityMsg = messages.getMessage("reg_pass_quality_medium");
                qualityMsg += BLANKS_MEDIUM;
                jLabelQualityText.setText(qualityMsg);
                jLabelQualityText.setBackground(Color.YELLOW);
            } else if (passphrase_quality == QUALITY_STRONG) {
                qualityMsg = messages.getMessage("reg_pass_quality_strong");
                qualityMsg += BLANKS_STRONG;
                jLabelQualityText.setText(qualityMsg);
                jLabelQualityText.setBackground(Color.GREEN);
            } else {
                throw new IllegalArgumentException("Unknown quality: " + passphrase_quality);
            }

            //JOptionPane.showMessageDialog(this, jLabelQualityText);
        }

        //update(getGraphics());
        this_keyPressed(evt);
    }//GEN-LAST:event_jPasswordKeyPressed

    private void jCheckBoxHideTypingStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBoxHideTypingStateChanged
    }//GEN-LAST:event_jCheckBoxHideTypingStateChanged

    private void jCheckBoxHideTypingItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxHideTypingItemStateChanged
        if ((evt.getStateChange() == ItemEvent.SELECTED)) {
            this.jPassword.setEchoChar(this.defaultEchocar);
            this.jPassword1.setEchoChar(this.defaultEchocar);
        } else {
            // Display  chars
            this.jPassword.setEchoChar((char) 0);
            this.jPassword1.setEchoChar((char) 0);
        }
    }//GEN-LAST:event_jCheckBoxHideTypingItemStateChanged
    
    private void jButtonCryptoSettingsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonCryptoSettingsMouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ButtonUrlOver.enter(evt);
    }//GEN-LAST:event_jButtonCryptoSettingsMouseEntered

    private void jButtonCryptoSettingsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonCryptoSettingsMouseExited
        this.setCursor(Cursor.getDefaultCursor());
        ButtonUrlOver.exit(evt);
    }//GEN-LAST:event_jButtonCryptoSettingsMouseExited


    private void jButtonCryptoSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCryptoSettingsActionPerformed

        RegisterCryptoOptions dialog
                = new RegisterCryptoOptions(this,
                        algoAsym, asymKeyLength, algoSymmetric);
        dialog.setVisible(true);

        this.algoAsym = dialog.getAlgoAsym();
        this.asymKeyLength = dialog.getAsymKeyLength();
        this.algoSymmetric = dialog.getAlgoSymmetric();

        this.setCryptographicSettingsLabel();

    }//GEN-LAST:event_jButtonCryptoSettingsActionPerformed

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
                new Register(null, null);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonCreate;
    private javax.swing.JButton jButtonCryptoSettings;
    private javax.swing.JCheckBox jCheckBoxHideTyping;
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JEditorPane jEditorPaneEmail;
    private javax.swing.JLabel jLabelCryptoSettings;
    private javax.swing.JLabel jLabelEmailHelp;
    private javax.swing.JLabel jLabelHideTyping;
    private javax.swing.JLabel jLabelKeyboardWarning;
    private javax.swing.JLabel jLabelMiniIcon;
    private javax.swing.JLabel jLabelPassphrase;
    private javax.swing.JLabel jLabelPassphraseQuality;
    private javax.swing.JLabel jLabelQualityText;
    private javax.swing.JLabel jLabelRetypePassphrase;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelUserEmail;
    private javax.swing.JLabel jLabelUserName;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelBlank;
    private javax.swing.JPanel jPanelBlank2;
    private javax.swing.JPanel jPanelBlank3;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelCryptographySettings;
    private javax.swing.JPanel jPanelEditorHelp;
    private javax.swing.JPanel jPanelEditorHelp1;
    private javax.swing.JPanel jPanelEmail1;
    private javax.swing.JPanel jPanelHelpEmail;
    private javax.swing.JPanel jPanelHelpPassphrase;
    private javax.swing.JPanel jPanelLeft1;
    private javax.swing.JPanel jPanelLeft2;
    private javax.swing.JPanel jPanelLeftHelp;
    private javax.swing.JPanel jPanelLeftHelp1;
    private javax.swing.JPanel jPanelLogo;
    private javax.swing.JPanel jPanelLogoLeft;
    private javax.swing.JPanel jPanelLogoRight;
    private javax.swing.JPanel jPanelName;
    private javax.swing.JPanel jPanelPassphrase;
    private javax.swing.JPanel jPanelPassphrase1;
    private javax.swing.JPanel jPanelPassphrase3;
    private javax.swing.JPanel jPanelRight1;
    private javax.swing.JPanel jPanelRight2;
    private javax.swing.JPanel jPanelRightHelp;
    private javax.swing.JPanel jPanelRightHelp1;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSepLeft;
    private javax.swing.JPanel jPanelSepRight;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelUserIdentification;
    private javax.swing.JPanel jPanelUserInfo;
    private javax.swing.JPasswordField jPassword;
    private javax.swing.JPasswordField jPassword1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JTextField jTextFieldCryptoSettings;
    private javax.swing.JTextField jTextFieldUserEmail;
    private javax.swing.JTextField jTextFieldUserFirstName;
    private javax.swing.JTextField jTextFieldUserName;
    // End of variables declaration//GEN-END:variables
}
