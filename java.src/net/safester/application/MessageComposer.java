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
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.Caret;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.awakefw.commons.api.client.AwakeProgressManager;
import org.awakefw.commons.api.client.DefaultAwakeProgressManager;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.awakefw.sql.api.client.AwakeConnection;
import org.bouncycastle.openpgp.PGPPublicKey;

import com.kawansoft.httpclient.KawanHttpClient;
import com.keyoti.rapidSpell.LanguageType;
import com.keyoti.rapidSpell.desktop.RapidSpellAsYouType;
import com.safelogic.utilx.StringMgr;
import com.swing.util.SwingUtil;
import javax.swing.Icon;

import net.atlanticbb.tantlinger.shef.HTMLEditorPane;
import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.atlanticbb.tantlinger.ui.text.HTMLUtils;
import net.safester.application.compose.RecipientsEmailBuilder;
import net.safester.application.compose.api.ApiMessageSender;
import net.safester.application.compose.api.IncomingAttachementDTOUtil;
import net.safester.application.compose.api.PGPPublicKeysBuilder;
import net.safester.application.compose.api.PgpTextEncryptor;
import net.safester.application.compose.api.drafts.MessageDraftManager;
import net.safester.application.compose.api.engines.ApiEncryptAttachmentsUsingThread;
import net.safester.application.engines.ThreadLocker;
import net.safester.application.http.ApiMessages;
import net.safester.application.http.KawanHttpClientBuilder;
import net.safester.application.http.dto.IncomingAttachementDTO;
import net.safester.application.http.dto.IncomingMessageDTO;
import net.safester.application.http.dto.IncomingRecipientDTO;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.parms.StoreParms;
import net.safester.application.parms.SubscriptionLocalStore;
import net.safester.application.photo.GroupListNew;
import net.safester.application.tool.AttachmentListHandler;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.ClipboardModifier;
import net.safester.application.tool.FileListManager;
import net.safester.application.tool.FileListRenderer;
import net.safester.application.tool.JFileChooserFactory;
import net.safester.application.tool.MessageTableCellRenderer;
import net.safester.application.tool.SortedDefaultListModel;
import net.safester.application.tool.TextReplaceFrame;
import net.safester.application.tool.TextSearchFrame;
import net.safester.application.tool.UI_Util;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.AppDateFormat;
import net.safester.application.util.EmailUser;
import net.safester.application.util.HtmlTextUtil;
import net.safester.application.util.JDialogDiscardableMessage;
import net.safester.application.util.JListUtil;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.PowerEditor;
import net.safester.application.util.TextCleanUtil;
import net.safester.application.util.UserPrefManager;
import net.safester.clientserver.MessageLocalStoreCache;
import net.safester.clientserver.MessageTransfer;
import net.safester.clientserver.PgpKeyPairLocal;
import net.safester.clientserver.ServerParms;
import net.safester.clientserver.UserNumberGetterClient;
import net.safester.clientserver.holder.GroupHolder;
import net.safester.clientserver.holder.PgpKeyPairHolder;
import net.safester.clientserver.holder.ServerTimeHolder;
import net.safester.clientserver.holder.TheUserSettingsHolder;
import net.safester.clientserver.holder.UserCompletionHolder;
import net.safester.noobs.clientserver.AttachmentLocal;
import net.safester.noobs.clientserver.MessageLocal;
import net.safester.noobs.clientserver.RecipientLocal;
import net.safester.noobs.clientserver.UserSettingsLocal;

/**
 * Main window for composing a message.
 *
 * @author Alexandre Becquereau
 */
public class MessageComposer extends javax.swing.JFrame {

    /**
     * The debug flag
     */
    public static boolean DEBUG = true;

    /**
     * The visual debug flag, if we want to see tthe html content
     */
    public static boolean VISUAL_DEBUG = false;
    
    //Index of buttons (for AddressBook)
    public static final int BUTTON_TO = 0;
    public static final int BUTTON_CC = 1;
    public static final int BUTTON_BCC = 2;
    public static final int RECIPIENT_NOT_SET = -1;
    public static final int RECIPIENT_INVALID = 0;
    public static final int RECIPIENT_OK = 1;
    public static final int MESSAGE_SIZE_EXCEEDED = -2;

    /**
     * RapidSpell instance must be static for optimisation
     */
    private static RapidSpellAsYouType rapidAYT = null;
    private ContactSelector addressBook = null;
    private JFrame thisOne;
    private MessagesManager messages = new MessagesManager();
    private HTMLEditorPane htmlEditor = new HTMLEditorPane();
    private ClipboardManager clipboard;
    //private UndoManager undo = new UndoManager();

    private Connection connection;
    /**
     * The File List Manager
     */
    private FileListManager fileListManager;
    JFrame caller;
    private int userNumber;
    private char[] passphrase;
    
    // BEGIN OLD FIELDS 
    //private MessageLocal message;
    //private List<RecipientLocal> recipients;
    // END OLD FIELDS 
    
    // BEGIN NEW FIELDS 
    private IncomingMessageDTO incomingMessageDTO = null;
    private List<IncomingRecipientDTO> incomingRecipientsDTO = null;
    // END NEW FIELDS     
    
    private Set<String> recipientKeyList;

           
    //private List<String> emailsToInvite;
    public static final String CR_LF = System.getProperty("line.separator");
    private String signature;
    //private int messageId = -1;
    private boolean isChanged = false;
    private String keyId = null;
    /**
     * If false, impossible to close window (when sendinf, because setEnable()
     * is not usable because of cursor busy not working
     */
    private boolean windowClosingEnabled = true;

    private TextSearchFrame textSearchFrame = null;
    private TextReplaceFrame textReplaceFrame = null;

    private int folderId = -1;
    private int messageId = -1;
    private static boolean TEST_CLASSIC_HTML = false;
    
    /**
     * Creates new form MailComposer
     * @param caller
     * @param keyId
     * @param theConnection
     * @param userNumber
     * @param thePassphrase
     */
    public MessageComposer(JFrame caller, String keyId, int userNumber, char[] thePassphrase, Connection theConnection) {
                    
        if (connection != null) {
            if (!(caller instanceof Main) && !(caller instanceof MessageReader) && !(caller instanceof GroupListNew)) {
                throw new IllegalArgumentException("MailComposer can only be caller from Main or MessageReader JFrame");
            }
        }

        this.caller = caller;

        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection) theConnection).clone();

        this.keyId = keyId;
        this.userNumber = userNumber;
        this.passphrase = thePassphrase;

        initComponents();
        initCompany();
        
       
    }

    public MessageComposer(JFrame caller, String keyId, int userNumber, char[] thePassphrase, Connection connection, MessageLocal message, int action) {
        this(caller, keyId, userNumber, thePassphrase, connection);
        
        //Remove attachments in case of drafts
        if (message.getFolderId() == Parms.DRAFT_ID) {
            // Put files to attach in jListAttach
            List<AttachmentLocal> attachmentLocalList = message.getAttachmentLocal();
            if (attachmentLocalList != null) {
                for (AttachmentLocal attachmentLocal : attachmentLocalList) {
                    fileListManager.add(new File(attachmentLocal.getFileName()));
                }
                // Set to null the instance to avoid problems
                message.setAttachmentLocal(null);
            }
            
            message.setSenderUserNumber(userNumber);
        }
        
        initMessageComponent(message, action);
        
        if (message.getFolderId() == Parms.DRAFT_ID) {
            
            jToggleButtonNoFoward.setSelected(! message.isFowardable());
            jToggleButtonNoPrint.setSelected(! message.isPrintable());
            jToggleButtonSendAnonymousNotification.setSelected(message.isAnonymousNotification());
            jToggleButtonSendAnonymousNotificationActionPerformed(null); // Force action
            //If we just opened a Draft no need to indicate that message had been changed
            isChanged = false;
            String title = thisOne.getTitle();
            title = StringUtils.removeEnd(title, "*");
            thisOne.setTitle(title);
            folderId = Parms.DRAFT_ID;
            messageId = message.getMessageId(); // required for drafts save


        }
    }

    public MessageComposer(JFrame caller, String keyId, int userNumber, Connection connection, String recipient) {
        this(caller, keyId, userNumber, null, connection);
        this.jTextAreaRecipientsTo.setText(recipient);
    }
    /**
     * Completion tool
     */
    private PowerEditor powerEditorTo = null;
    private PowerEditor powerEditorCc = null;
    private PowerEditor powerEditorBcc = null;

    /**
     * Our graphic init method
     */
    public void initCompany() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        thisOne = this;
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        this.setTitle(messages.getMessage("untitled") + " - " + messages.getMessage("message"));

        jMenuBar1.remove(jMenuEdit);
        jMenuBar1.remove(jMenuTools);
        jMenuBar1.remove(jMenuSpellCheck);

        // Keep Menu because if ctrl-y & z:
        jMenuBar1.add(htmlEditor.getEditMenu());

        jMenuBar1.add(jMenuTools);
        jMenuBar1.add(jMenuSpellCheck);
        jMenuBar1.add(jMenuHelp);

        //Header part
        //jPanelRecipients.setBorder(new TitledBorder(messages.getMessage("semicolon_separated_recipients")));
        jLabelRecipients.setText(" " + messages.getMessage("semicolon_separated_recipients") + " ");

        jButtonTo.setText(messages.getMessage("to"));
        jButtonCc.setText(messages.getMessage("cc"));
        jButtonBcc2.setText(messages.getMessage("bcc"));

        jToggleButtonBcc2.setText(messages.getMessage("display_bcc"));

        jPanelRecipientsBcc.setVisible(false);
        jPanelSepRecipientsBcc.setVisible(false);

        List<Component> buttonGroupRecipient = new Vector<Component>();
        buttonGroupRecipient.add(jButtonTo);
        buttonGroupRecipient.add(jButtonCc);
        buttonGroupRecipient.add(jButtonBcc2);

        //jPanelSubject.setBorder(new TitledBorder(messages.getMessage("subject")));
        jLabelSubject.setText(messages.getMessage("subject"));
        jTextFieldSubject.setText("");

        //jPanelFiles.setBorder(new TitledBorder(messages.getMessage("attachments")));
        jLabelAttached.setText(messages.getMessage("attached"));

        jPanelScrollPane.remove(jListAttach);

        SortedDefaultListModel model_attachs = new SortedDefaultListModel();
        jListAttach = new JList(model_attachs);
        jListAttach.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        jListAttach.setVisibleRowCount(-1);

        JListUtil.selectItemWhenMouverOver(jListAttach);

        //HACK NDP 17/03/18
        JListUtil.formatSpacing(jListAttach);

        jScrollPane2.setViewportView(jListAttach);

        fileListManager = new FileListManager(this, jListAttach);
        FileListRenderer render = new FileListRenderer();
        render.setListManager(fileListManager);
        jListAttach.setCellRenderer(render);

        jPanelScrollPane.add(jScrollPane2);

        jMenuItemReplace.setVisible(false);

        //Menu part
        jMenuFile.setText(messages.getMessage("file"));
        jMenuEdit.setText(messages.getMessage("edit"));
        jMenuTools.setText(messages.getMessage("tools"));

        jMenuItemSend.setText(messages.getMessage("send"));
        jMenuItemPrint.setText(messages.getMessage("print"));
        jMenuItemClose.setText(messages.getMessage("close"));

        cancelMenuItem.setText(messages.getMessage("cancel"));
        copyMenuItem.setText(messages.getMessage("copy"));
        pasteMenuItem.setText(messages.getMessage("paste"));
        cutMenuItem.setText(messages.getMessage("cut"));
        selectAlljMenuItem.setText(messages.getMessage("select_all"));
        jMenuItemReplace.setText(messages.getMessage("replace"));
        jMenuItemFind.setText(messages.getMessage("find"));

        jToolbarButtonSend.setText(messages.getMessage("send"));
        jToolbarButtonAddRecipient.setText(messages.getMessage("add_recipient"));
        jToolbarButtonAttach.setText(messages.getMessage("attach_file"));

        jToggleButtonNoFoward.setText(messages.getMessage("no_foward"));
        jToggleButtonNoPrint.setText(messages.getMessage("no_print"));

        jToolbarButtonSend.setToolTipText(messages.getMessage("send"));
        jToolbarButtonAddRecipient.setToolTipText(messages.getMessage("add_recipient"));
        jToolbarButtonAttach.setToolTipText(messages.getMessage("attach_file"));

        jToggleButtonBcc2.setToolTipText(messages.getMessage("display_bcc"));
        jToggleButtonNoFoward.setToolTipText(messages.getMessage("no_foward"));
        jToggleButtonNoPrint.setToolTipText(messages.getMessage("no_print"));

        jMenuSpellCheck.setText(messages.getMessage("spell_check"));
        jRadioButtonMenuItemEnglish.setText(messages.getMessage("english"));
        jRadioButtonMenuItemFrench.setText(messages.getMessage("french"));

        jMenuHelp.setText(messages.getMessage("help"));
        jMenuItemHelp.setText(messages.getMessage("help"));
        jToolbarButtonHelp.setText(messages.getMessage("help"));

        String emailBccNotarization = MessageComposerNotarization.getEMAIL_BCC_NOTARIZATION();
        if (emailBccNotarization != null && !emailBccNotarization.equals("NONE")) {
            String message = messages.getMessage("a_bbc_of_every_messages_is_sent_to");
            message = message.replace("{0}", emailBccNotarization);
            jLabelBccNotification.setText(message + " ");
        } else {
            jPanelBccNotarization.setVisible(false);
        }

        if (SystemUtils.IS_OS_MAC_OSX) {
            jMenuItemClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        } else {
            jMenuItemClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        }

        int language = LanguageType.ENGLISH;
        if (Locale.getDefault().getLanguage().equals("fr")) {
            language = LanguageType.FRENCH;
        }

        String sLanguage = UserPrefManager.getPreference(UserPrefManager.SPELL_CHECK_LANGUAGE);
        if (sLanguage != null) {
            try {
                language = Integer.parseInt(sLanguage);
            } catch (NumberFormatException e) {
                //Do nothing leave english as default language
            }
        }
        if (language == LanguageType.FRENCH) {
            jRadioButtonMenuItemFrench.setSelected(true);
        } else {
            jRadioButtonMenuItemEnglish.setSelected(true);
        }

        jToggleButtonSendAnonymousNotification.setToolTipText(messages.getMessage("anonymous_notification"));
        jToggleButtonSendAnonymousNotification.setText(messages.getMessage("anonymous_notification"));

        jMenuItemSave.setText(messages.getMessage("save_draft"));
        jToolbarButtonSave.setText(messages.getMessage("save_draft"));
        jToolbarButtonSave.setToolTipText(messages.getMessage("save_draft"));

        // jToggleButtonSendAnonymousNotification visible now!
        jSeparatorSendAnonymous.setVisible(true);
        jToggleButtonSendAnonymousNotification.setVisible(true);

        //jPanelEmailBody.setBorder(new TitledBorder(messages.getMessage("message")));
        DocumentListener documentListener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                setMessageChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setMessageChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setMessageChanged();
            }
        };

        htmlEditor.getEditor().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                htmlEditor.getEditor().getCaret().setVisible(true);
                Caret caret = htmlEditor.getEditor().getCaret();
            }
        });

        try {

            if (connection != null) {

                //TODO: fix this. Dirty. Sometimes, keyId can be null (call from Address Book)
                if (keyId == null) {
                    System.out.println("WARNING! keyId is null in MessageComposer!");
                    //UserLoginTransfert userLoginTransfert = new UserLoginTransfert(connection, this.userNumber);
                    //UserLoginLocal userLoginLocal = userLoginTransfert.get();
                    //keyId = userLoginLocal.getKey_id();
                    keyId = new UserNumberGetterClient(connection).getLoginFromUserNumber(this.userNumber);
                }
                
                debug(new Date() + " instal completion...");
                try {
                    installCompletion();
                    debug(new Date() + " completion installed.");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, this.messages.getMessage("can_not_install_completion") + " " + e.getMessage());
                }

                TheUserSettingsHolder theUserSettingsHolder = new TheUserSettingsHolder(connection, userNumber);
                UserSettingsLocal userSettingsLocal = theUserSettingsHolder.get();

                if (UserPrefManager.getBooleanPreference(UserPrefManager.INSERT_SIGNATURE)) {
                    signature = userSettingsLocal.getSignature();
                }
               
                jToggleButtonSendAnonymousNotification.setSelected(userSettingsLocal.getSend_anonymous_notification_on());
                jToggleButtonSendAnonymousNotificationActionPerformed(null); // Force action
            }

            htmlEditor =  new HTMLEditorPane();

            setSpellCheck(language);

            if (signature == null) {
                signature = "";
            }
            if (signature.equalsIgnoreCase("null")) {
                signature = "";
            }

            boolean isSimpleText = UserPrefManager.getBooleanPreference(UserPrefManager.IS_SIMPLE_TEXT);

            // Remove trailing &nbsp; if there is a signature
            if (isSimpleText) {
                if (!signature.isEmpty()) {
                    htmlEditor.setText("<pre>" + signature + "</pre><br>");
                } else {
                    htmlEditor.setText("&nbsp;");
                }
                htmlEditor.setTextSimple();
            } else {

                if (!signature.isEmpty()) {
                    formatSignatureWithBr();
                    htmlEditor.setText(signature + "<br>");
                } else {
                    htmlEditor.setText("&nbsp;");
                }
            }

            htmlEditor.getEditor().moveCaretPosition(0);
            htmlEditor.getEditor().setCaretPosition(0);
            htmlEditor.getEditor().setSelectionEnd(1);

//            debug("BEGIN editor.getText() :"
//                    + CR_LF + htmlEditor.getText() + ":"
//                    + CR_LF
//                    + "END editor.getText()");
        } catch (Exception e) {
            e.printStackTrace();
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPaneNewCustom.showException(this, e);
        }

        //Editor part
        clipboard = new ClipboardManager(this);

        //this.jPanelEmailBody.add(stylePad);
        this.jPanelEmailBody.add(htmlEditor);

        jTextFieldSubject.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                jTextFieldSubjectFocusLost(e);
            }
        });

        //getCaret().setVisible( true ). 
        this.setSize(891, 736);

        this.keyListenerAdder();

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                if (!windowClosingEnabled) {
                    return;
                }

                WindowSettingManager.save(thisOne);
                UserPrefManager.setPreference(UserPrefManager.IS_SIMPLE_TEXT,
                        !(htmlEditor.isRichText()));
                close();
            }
        });

        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                WindowSettingManager.save(thisOne);
            }
        });

        ListDataListener listDataListener = new ListDataListener() {

            @Override
            public void intervalAdded(ListDataEvent e) {
                setMessageChanged();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                setMessageChanged();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                setMessageChanged();
            }
        };

        jListAttach.getModel().addListDataListener(listDataListener);
        jTextAreaRecipientsTo.getDocument().addDocumentListener(documentListener);
        jTextAreaRecipientsCc.getDocument().addDocumentListener(documentListener);
        jTextAreaRecipientsBcc.getDocument().addDocumentListener(documentListener);

        jTextFieldSubject.getDocument().addDocumentListener(documentListener);

        htmlEditor.getEditor().getDocument().addDocumentListener(documentListener);

        //Harmonize button sizes
        ButtonResizer buttonResizer = new ButtonResizer();
        buttonResizer.setWidthToMax(buttonGroupRecipient);

        jTextAreaRecipientsTo.setFont(jTextFieldSubject.getFont());
        jTextAreaRecipientsCc.setFont(jTextFieldSubject.getFont());
        jTextAreaRecipientsBcc.setFont(jTextFieldSubject.getFont());

        resizePanelHeigthForWindows(jPanelRecipientsTo, 40);
        resizePanelHeigthForWindows(jPanelRecipientsCc, 40);
        resizePanelHeigthForWindows(jPanelRecipientsBcc, 40);
        resizePanelHeigthForWindows(jPanelFiles, 56);
        
        // Mac settings
        resizePanelHeigthForMac(jPanelRecipientsTo, 50);
        resizePanelHeigthForMac(jPanelRecipientsCc, 50);
        resizePanelHeigthForMac(jPanelRecipientsBcc, 50);
        resizePanelHeigthForMac(jPanelFiles, 56);

        // To be done after resizeJComponents
        resizePanelHeigthForNimbus(jPanelRecipientsTo, 45);
        resizePanelHeigthForNimbus(jPanelRecipientsCc, 45);
        resizePanelHeigthForNimbus(jPanelRecipientsBcc, 45);
        resizePanelHeigthForNimbus(jPanelFiles, 45);

        // To align on right size all visualTextDebug & textarea field lengths
        alignFieldsOnRightForAllOs();

        // Nimbus settings
        SwingUtil.applySwingUpdates(rootPane);

        this.jTextAreaRecipientsTo.requestFocusInWindow();
        //this.htmlEditor.getEditor().requestFocusInWindow();
        
        this.setLocationRelativeTo(caller);
        WindowSettingManager.load(this);
        this.setCursor(Cursor.getDefaultCursor());

        
        long timeBegin = new Date().getTime();

        // Load in thread to put in memory cache the server time & the Groups & The Master Key
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    debug(new Date() + " Thread start...");
                    ServerTimeHolder.load(connection);

                    PgpKeyPairHolder pgpKeyPairHolder = new PgpKeyPairHolder(connection, ServerParms.getMasterKeyUserNumber());
                    pgpKeyPairHolder.load();

                    GroupHolder groupHolder = new GroupHolder(connection, userNumber);
                    groupHolder.load();

                    debug(new Date() + " Thread end...");
                } catch (Exception ex) {
                    JOptionPaneNewCustom.showException(rootPane, ex);
                }
            }
        };
        t.start();

        long timeEnd = new Date().getTime();
        long timeElapsed = timeEnd - timeBegin;
        debug("timeElapsed: " + timeElapsed);
    }

    /**
     * Complicated ans olly method to align the to & cc fields with subject and
     * attached On Windows : alignnement is done on subject/attached, which is
     * larger. On Mac : alignement is done on to/cc button, which is larger
     */
    private void alignFieldsOnRightForAllOs() {
        if (SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_LINUX) {
            int widthReference = jPanelLabelSubject.getPreferredSize().width;

            int maxHeight = (int) jButtonTo.getMaximumSize().getHeight();
            int minHeight = (int) jButtonTo.getMinimumSize().getHeight();
            int prefHeight = (int) jButtonTo.getPreferredSize().getHeight();

            jButtonTo.setMaximumSize(new Dimension(widthReference, maxHeight));
            jButtonTo.setMinimumSize(new Dimension(widthReference, minHeight));
            jButtonTo.setPreferredSize(new Dimension(widthReference, prefHeight));

            jButtonCc.setMaximumSize(new Dimension(widthReference, maxHeight));
            jButtonCc.setMinimumSize(new Dimension(widthReference, minHeight));
            jButtonCc.setPreferredSize(new Dimension(widthReference, prefHeight));

            jButtonBcc2.setMaximumSize(new Dimension(widthReference, maxHeight));
            jButtonBcc2.setMinimumSize(new Dimension(widthReference, minHeight));
            jButtonBcc2.setPreferredSize(new Dimension(widthReference, prefHeight));

        }

        if (SystemUtils.IS_OS_MAC_OSX) {
            // Border of subject textfield must be same as textarea
            jTextFieldSubject.setBorder(jTextAreaRecipientsTo.getBorder());

            int widthReference = jButtonTo.getPreferredSize().width + 5; // 5 is the sep panel width

            int maxHeight = (int) jPanelLabelSubject.getMaximumSize().getHeight();
            int minHeight = (int) jPanelLabelSubject.getMinimumSize().getHeight();
            int prefHeight = (int) jPanelLabelSubject.getPreferredSize().getHeight();

            jPanelLabelSubject.setMaximumSize(new Dimension(widthReference, maxHeight));
            jPanelLabelSubject.setMinimumSize(new Dimension(widthReference, minHeight));
            jPanelLabelSubject.setPreferredSize(new Dimension(widthReference, prefHeight));

            jPanelLabelAttached.setMaximumSize(new Dimension(widthReference, maxHeight));
            jPanelLabelAttached.setMinimumSize(new Dimension(widthReference, minHeight));
            jPanelLabelAttached.setPreferredSize(new Dimension(widthReference, prefHeight));
        }

    }

    private void resizePanelHeigthForWindows(JComponent component, int preferedHeight) {
        if (SystemUtils.IS_OS_WINDOWS) {
            int maxWidth = (int) component.getMaximumSize().getWidth();
            int minWidth = (int) component.getMinimumSize().getWidth();
            int prefWidth = (int) component.getPreferredSize().getWidth();

            component.setMaximumSize(new Dimension(maxWidth, preferedHeight));
            component.setMinimumSize(new Dimension(minWidth, preferedHeight));
            component.setPreferredSize(new Dimension(prefWidth, preferedHeight));
        }
    }
        
    private void resizePanelHeigthForMac(JComponent component, int preferedHeight) {
        if (SystemUtils.IS_OS_MAC_OSX) {
            int maxWidth = (int) component.getMaximumSize().getWidth();
            int minWidth = (int) component.getMinimumSize().getWidth();
            int prefWidth = (int) component.getPreferredSize().getWidth();

            component.setMaximumSize(new Dimension(maxWidth, preferedHeight));
            component.setMinimumSize(new Dimension(minWidth, preferedHeight));
            component.setPreferredSize(new Dimension(prefWidth, preferedHeight));
        }
    }

    private void resizePanelHeigthForNimbus(JComponent component, int preferedHeight) {
        if (UI_Util.isNimbus()) {
            int maxWidth = (int) component.getMaximumSize().getWidth();
            int minWidth = (int) component.getMinimumSize().getWidth();
            int prefWidth = (int) component.getPreferredSize().getWidth();

            component.setMaximumSize(new Dimension(maxWidth, preferedHeight));
            component.setMinimumSize(new Dimension(minWidth, preferedHeight));
            component.setPreferredSize(new Dimension(prefWidth, preferedHeight));
        }
    }

    /**
     * Universal key listener
     */
    private void keyListenerAdder() {
        List<Component> components = SwingUtil.getAllComponants(this);

        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);

            if (!(comp instanceof JButton)) {
                comp.addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyPressed(KeyEvent e) {
                        this_keyPressedOrReleased(e);
                    }
                });

                comp.addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyReleased(KeyEvent e) {
                        this_keyPressedOrReleased(e);
                    }
                });
            }

        }
    }

    private String removeUselessStyle(String messageBody) {
        int start = messageBody.indexOf("<!--");
        //Remove useless style definition
        if (start != -1) {
            int stop = messageBody.indexOf("-->");
            if (stop == -1) {
                return messageBody; // Sould never happen
            }
            stop = stop + "-->".length();
            String buffer = messageBody.substring(0, start);
            String buffer2 = messageBody.substring(stop);
            messageBody = buffer + buffer2;
        }

        return messageBody;
    }

    ///////////////////////////////////////////////////////////////////////////
    // KEYS PART
    ///////////////////////////////////////////////////////////////////////////
    private void this_keyPressedOrReleased(KeyEvent e) {
        //System.out.println("this_keyReleased(KeyEvent e) " + e.getComponent().getName());
        int id = e.getID();
        if (id == KeyEvent.KEY_RELEASED) {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_ESCAPE) {
                this.close();
            }
        }

        if (id == KeyEvent.KEY_PRESSED) {

            // Paste visualTextDebug with a special routine
            if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) {
                if (e.getComponent() == htmlEditor.getEditor()) {

                    //Only for HTML Editor!
                    if (ClipboardModifier.isClipboadContentMultiLines()) {
                        pasteTextWithBr();
                        e.consume();
                    }
                }
            }
        }
    }

    /**
     * Method to use when a visualTextDebug with CR_LF is to copied to external window
 (Word, Gmal, etc).
     */
    private void pasteTextWithBr() {

        HTMLDocument document = (HTMLDocument) htmlEditor.getEditor().getDocument();

        int pos = htmlEditor.getEditor().getCaretPosition();
        if (ClipboardModifier.isClipboardContentIsString()) {

            CompoundUndoManager.beginCompoundEdit(document);

            //Empty current selection
            if (htmlEditor.getEditor().getSelectionStart() != htmlEditor.getEditor().getSelectionEnd()) {
                htmlEditor.getEditor().replaceSelection("");
                debug("pasteTextWithBr(): replace!!!");
            }

            HTMLUtils.insertHTML("<img src=safester_tag>", HTML.Tag.IMG, htmlEditor.getEditor());

            String newtext = htmlEditor.getText();
            String clipbardText = ClipboardModifier.modifyClipboardString();

            newtext = StringMgr.ReplaceAll(newtext, "<img src=\"safester_tag\">", clipbardText);

            htmlEditor.setText(newtext);
            //Set correct carret position (=>Orignal position + inserted content length)
            int newTextLength = ClipboardModifier.getOriginalContentLength();
            int newCarretPosition = pos + newTextLength;

            //Restore clipboard content
            ClipboardModifier.restoreOrignalClipboardString();

            CompoundUndoManager.endCompoundEdit(document);

            int documentLength = htmlEditor.getEditor().getDocument().getLength();
            if (newCarretPosition > documentLength) {
                newCarretPosition = documentLength;
            }

            // We safely put the carret at newCarretPosition -2, but check that it's not < 0
            //htmlEditor.setCaretPosition(newCarretPosition);
            newCarretPosition = newCarretPosition - 2;
            if (newCarretPosition < 0) {
                newCarretPosition = 0;
            }
            htmlEditor.setCaretPosition(newCarretPosition);

        }
    }

    /**
     * Set the spell checker static instance instance
     *
     * @param language the language to use
     * @param dictionaryPath the path to the dictionnary
     */
    private void setSpellCheck(int language) {

        try {
            DictUtil.copyDictFilesToUserHome();
        } catch (IOException ex) {
            JOptionPaneNewCustom.showException(this, ex, "Impossible to create English and French dictionnaries.");
            return;
        }

        String dictFilePath = DictUtil.getDictFilesPath() + File.separator;
        debug("dictFilePath: " + dictFilePath);

        if (DEBUG) {
            //JOptionPane.showMessageDialog(this, "dictFilePath: " + dictFilePath);
        }

        if (rapidAYT == null) {
            //debug("new RapidSpellAsYouType() for English");
            rapidAYT = new RapidSpellAsYouType();
        }

        if (language == LanguageType.ENGLISH) {
            rapidAYT.setLanguageParser(LanguageType.ENGLISH);
            rapidAYT.setGUILanguage(LanguageType.ENGLISH);
            rapidAYT.setDictFilePath(dictFilePath + Parms.DICTIONARY_ENGLISH);
        } else {
            rapidAYT.setLanguageParser(LanguageType.FRENCH);
            rapidAYT.setGUILanguage(LanguageType.FRENCH);
            rapidAYT.setDictFilePath(dictFilePath + Parms.DICTIONARY_FRENCH);
        }

        JEditorPane jEditorPane = htmlEditor.getEditor();
        rapidAYT.setTextComponent(jEditorPane);

        boolean ignoreCapitalizedWord = UserPrefManager.getBooleanPreference(UserPrefManager.IGNORE_CAPITALIZED_WORDS);
        boolean ignoreWordsWithDigits = UserPrefManager.getBooleanPreference(UserPrefManager.IGNORE_WORDS_WITH_DIGITS);
        boolean separateHyphenWords = UserPrefManager.getBooleanPreference(UserPrefManager.SEPARATE_HYPHEN_WORDS);

        rapidAYT.setIgnoreCapitalizedWords(ignoreCapitalizedWord);
        rapidAYT.setIgnoreWordsWithDigits(ignoreWordsWithDigits);
        rapidAYT.setSeparateHyphenWords(separateHyphenWords);

        rapidAYT.forceCheckAll();
        htmlEditor.setEditor(jEditorPane);
    }

    /**
     * Dispose all resources
     */
    public void close() {
        debug("close() called!");
        this.dispose();
    }

    private void setMessageChanged() {
        isChanged = true;
        String title = thisOne.getTitle();
        title = StringUtils.removeEnd(title, "*");
        title += "*";
        thisOne.setTitle(title);
    }

    public void installCompletion() {
        // Add completion to to and cc visualTextDebug area
        UserCompletionHolder userCompletionHolder = new UserCompletionHolder(connection, userNumber);

        // HACK: lexicon must now be global
              
        LexiconStore lexiconStore = new LexiconStore(keyId);
        if (! lexiconStore.existsLexicon()) {
            Set<String> lexiconForKeyId = userCompletionHolder.getLexicon();
            lexiconStore.addLexicon(lexiconForKeyId);
        }

        Set<String> lexicon = LexiconStore.getAllLexicons();
        
        powerEditorTo = new PowerEditor(lexicon, this, jTextAreaRecipientsTo);
        jPanelScrollPaneTo.remove(jScrollPaneTo);
        jScrollPaneTo = new JScrollPane(powerEditorTo);
        jPanelScrollPaneTo.add(jScrollPaneTo);
        jScrollPaneTo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        //KawanSoft NDP 13/05/21
        jScrollPaneTo.setViewportView(jTextAreaRecipientsTo);
        
        powerEditorCc = new PowerEditor(lexicon, this, jTextAreaRecipientsCc);
        jPanelScrollPaneCc.remove(jScrollPaneCc);
        jScrollPaneCc = new JScrollPane(powerEditorCc);
        jPanelScrollPaneCc.add(jScrollPaneCc);
        jScrollPaneCc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //KawanSoft NDP 13/05/21
        jScrollPaneCc.setViewportView(jTextAreaRecipientsCc);
        
        powerEditorBcc = new PowerEditor(lexicon, this, jTextAreaRecipientsBcc);
        jPanelScrollPaneBcc.remove(jScrollPanelBcc);
        jScrollPanelBcc = new JScrollPane(powerEditorBcc);
        jPanelScrollPaneBcc.add(jScrollPanelBcc);
        jScrollPanelBcc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //KawanSoft NDP 13/05/21
        jScrollPanelBcc.setViewportView(jTextAreaRecipientsBcc);
        
        releaseCompletion();
    }

    public void releaseCompletion() {
        Component[] componentArray = {jTextFieldSubject, //editor, // NDP: Test that editor is not concernet by focus loss
    //jButtonTo,
    //jButtonCc,
    //jButtonAttach
    };

        List<Component> components = new Vector<Component>();
        components = Arrays.asList(componentArray);

        for (Component component : components) {
            component.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent e) {
                    powerEditorTo.close();
                    powerEditorCc.close();
                }
            });
        }
    }

    public void formatSignatureWithBr() throws IOException {
        StringReader sr = new StringReader(signature);
        LineNumberReader in = new LineNumberReader(sr);
        String line = null;
        String rebuildSignature = "";
        while ((line = in.readLine()) != null) {
            rebuildSignature += line.trim() + "<br>";
        }
        signature = rebuildSignature;
    }

    private void editMessage(MessageLocal message) {

        //this.messageId = message.getMessageId();
        //this.message = message;

        this.jTextFieldSubject.setText(message.getSubject());
        this.setTitle(this.jTextFieldSubject.getText());
        message.setRecipientLocal(cleanRecipientList(message.getRecipientLocal()));
        buildRecipientList(message, false);
        buildAttachmentList(message);

        String messageBody = message.getBody();
        messageBody = removeUselessStyle(messageBody);

        htmlEditor.setText(messageBody);

        if (VISUAL_DEBUG) {
            JOptionPane.showMessageDialog(rootPane, htmlEditor.getText());
        }

        htmlEditor.setCaretPosition(0);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void initMessageComponent(MessageLocal message, int action) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        String header = " <br><br><hr>";
        if (signature != null && !signature.equalsIgnoreCase("null")) {

            header = "<br>" + signature + header;
        }

        header += getMessageHeader(message);

        String subject = "";

        if (action == Parms.ACTION_EDIT) {
            editMessage(message);
            return;
        }
        if (action == Parms.ACTION_REPLY_ALL) {

            message.setRecipientLocal(cleanRecipientList(message.getRecipientLocal()));
            buildRecipientList(message, true);
            subject = "Re: ";
        }

        if (action == Parms.ACTION_REPLY) {
            //String sender = message.getSenderUserEmail();

            EmailUser emailUser = new EmailUser(message.getSenderUserName(), message.getSenderUserEmail());
            String sender = emailUser.getNameAndEmailAddress();
            
            sender = HtmlConverter.fromHtml(sender);

            jTextAreaRecipientsTo.setText(sender);
            if (!message.getSubject().startsWith("Re: ")) {
                subject = "Re: ";
            }
        }
        if (action == Parms.ACTION_FOWARD) {
            buildAttachmentList(message);
            if (!message.getSubject().startsWith("Fw: ")) {
                subject = "Fw: ";
            }
        }

        subject += message.getSubject();
        subject = HtmlConverter.fromHtml(subject);

        this.jTextFieldSubject.setText(subject);
        this.setTitle(subject);

        if (message.isFowardable() && message.isPrintable()) {

            String messageBody = message.getBody();
            messageBody = removeUselessStyle(messageBody);
            
            // test if message is created by Mobile, if yes remove HTML tags
            if (message.getIsSigned()) {
                messageBody = HtmlConverter.fromHtml(messageBody);
            } 
            
            htmlEditor.setText(header + messageBody);
        }
        htmlEditor.getEditor().moveCaretPosition(0);
        htmlEditor.getEditor().setCaretPosition(0);
        htmlEditor.getEditor().setSelectionEnd(0);

        this.setCursor(Cursor.getDefaultCursor());

    }

    private String getMessageHeader(MessageLocal message) {
        String to = getRecipients(message, Parms.RECIPIENT_TO);

        to = StringUtils.replace(to, "<", "&lt;");
        to = StringUtils.replace(to, ">", "&gt;");

        String cc = getRecipients(message, Parms.RECIPIENT_CC);
        cc = StringUtils.replace(cc, "<", "&lt;");
        cc = StringUtils.replace(cc, ">", "&gt;");
        String header = "";

        header += messages.getMessage("from") + " " + message.getSenderUserName() + " &lt;" + message.getSenderUserEmail() + "&gt;<br>";
        header += jButtonTo.getText() + " " + to + "<br>";
        header += jButtonCc.getText() + " " + cc + "<br>";

        //DateFormat df = new SimpleDateFormat(messages.getMessage("date_format"));
        AppDateFormat df = new AppDateFormat();

        header += messages.getMessage("sent_on") + " " + df.format(message.getDateMessage()) + "<br>";
        header += messages.getMessage("subject") + " " + message.getSubject() + "<br><br>";

        return header;
    }

    public List<RecipientLocal> cleanRecipientList(List<RecipientLocal> recipients) {
        List<RecipientLocal> newRecipients = new Vector<RecipientLocal>();

        for (RecipientLocal recipient : recipients) {

            String email = recipient.getEmail();
            
            if (!newRecipients.contains(recipient) && !this.keyId.equalsIgnoreCase(email)) {
                newRecipients.add(recipient);
            }
        }
        return newRecipients;
    }

    private String getRecipients(MessageLocal message, int typeRecipient) {
        String recipientsAdresses = "";
        List<RecipientLocal> recipientsList = message.getRecipientLocal();
        for (RecipientLocal recipient : recipientsList) {

            if (recipient.getTypeRecipient() == typeRecipient) {
                try {
                    EmailUser emailUser = new EmailUser(recipient.getNameRecipient(), recipient.getEmail());
                    
                    //recipientsAdresses += emailUser.getNameAndEmailAddress() + "; ";
                    String sender = emailUser.getNameAndEmailAddress();
                    sender = HtmlConverter.fromHtml(sender);
                    recipientsAdresses += sender  + "; ";

                } catch (Exception e) {
                    e.printStackTrace();
                    recipientsAdresses += recipient.getNameRecipient() + "; ";
                }
            }
        }

//        try {
//            List<PendingMessageUserLocal> pendingMessageUserLocals = message.getPendingMessageUserLocal();
//
//            for (PendingMessageUserLocal pendingMessageUserLocal : pendingMessageUserLocals) {
//                if (typeRecipient == pendingMessageUserLocal.getType_recipient()) {
//                    recipientsAdresses += pendingMessageUserLocal.getEmail() + "; ";
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPaneNewCustom.showException(null, e);
//        }

        return recipientsAdresses;
    }

    private void buildRecipientList(MessageLocal message, boolean addSenderToRecipients) {
        String to = "";
        String cc = "";
        to += getRecipients(message, Parms.RECIPIENT_TO);
        //if(this.)
        if (addSenderToRecipients) {
            //String sender = message.getSenderUserEmail();

            EmailUser emailUser = new EmailUser(message.getSenderUserName(), message.getSenderUserEmail());
            String sender = emailUser.getNameAndEmailAddress();
            sender = HtmlConverter.fromHtml(sender);
            
            if (!to.contains(sender) && !cc.contains(sender)) {
                to = sender + "; " + to;
            }
        }

        cc += getRecipients(message, Parms.RECIPIENT_CC);
        jTextAreaRecipientsTo.setText(to);
        jTextAreaRecipientsCc.setText(cc);

    }

    private void buildAttachmentList(MessageLocal message) {

        if (message.getIsWithAttachment()) {
            List<String> files = new Vector<String>();

            //String destinationDir = System.getProperty("java.io.tmpdir");
            String destinationDir = Parms.getSafesterTempDir();

            if (!destinationDir.endsWith(File.separator)) {
                destinationDir += File.separator;
            }

            destinationDir += "edit";

            File destinationDirectory = new File(destinationDir);
            if (!destinationDirectory.exists()) {
                destinationDirectory.mkdirs();
            }

            // Test size for decryption
            boolean okDecrypt = isSpaceEnoughOnDestinationDirForAllFiles(destinationDirectory,
                    message.getAttachmentLocal());
            if (!okDecrypt) {
                return;
            }

            for (AttachmentLocal attachmentLocal : message.getAttachmentLocal()) {
                String fileName = attachmentLocal.getFileName();
                fileName = HtmlConverter.fromHtml(fileName);

                files.add(fileName);
            }

            try {

                MessageDecryptor messageDecryptor = null;
                try {
                    messageDecryptor = new MessageDecryptor(userNumber, passphrase, connection);
                } catch (SQLException ex) {
                    JOptionPaneNewCustom.showException(this, ex);
                    return;
                }

                PgpKeyPairLocal pgpKeyPairLocal = messageDecryptor.getKeyPair();
                String privateKeyPgpBlock = pgpKeyPairLocal.getPrivateKeyPgpBlock();

                int senderUserNumber = message.getSenderUserNumber();
                AttachmentListHandler attachmentListHandler = new AttachmentListHandler(thisOne, senderUserNumber, connection, privateKeyPgpBlock, passphrase);
                attachmentListHandler.downloadAll(files, destinationDir);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPaneNewCustom.showException(rootPane, e);
            }

        }
    }

    private boolean isSpaceEnoughOnDestinationDirForAllFiles(File destinationDir,
            List<AttachmentLocal> attachmentsLocal) {

        long totalSize = 0;

        for (AttachmentLocal attachmentLocal : attachmentsLocal) {
            totalSize += attachmentLocal.getFileSize();
        }

        long freeSpace = destinationDir.getFreeSpace();

        if (freeSpace < (totalSize * 2)) {
            String deviceDirectory = destinationDir.toString();
            String errorMsg = messages.getMessage("free_space_required_for_decryption");
            String explain = SwingUtil.getTextContent("the_files_list_can_not_be_decrypted");

            explain = MessageFormat.format(explain, deviceDirectory, (totalSize * 2), freeSpace);

            JOptionPane.showMessageDialog(this, explain, errorMsg, JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;

    }

    public void setAttachmentList(List<File> files) {
        this.fileListManager.add(files);
    }

    /**
     * Change title of window using Subject of email
     */
    private void jTextFieldSubjectFocusLost(FocusEvent ev) {
        if (!jTextFieldSubject.getText().equals("")) {
            String newTitle = jTextFieldSubject.getText() + " - " + messages.getMessage("message");
            thisOne.setTitle(newTitle);
        }

        //not very proud of this method but only way for editor to get focus
        //when userNumber press tab
        if (ev.getOppositeComponent() instanceof JList) {
//            stylePad.getEditor().requestFocusInWindow();
            //jButtonAttach.requestFocusInWindow();
        } else {
            //focus is going reverse order => normal behaviour
            if (ev.getOppositeComponent() != null) {
                ev.getOppositeComponent().requestFocusInWindow();
            }
        }

    }

    public void setConnection(Connection theConnection) {
        this.connection = theConnection;
    }

    /**
     * Apply font on menu bar
     *
     * @param menuBar Menu bar
     * @param f The font to apply
     */
    private void applyFontToMenuBar(JMenuBar menuBar, Font f) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            applyFontToMenuItem(menu, f);
            menu.setFont(f);
        }
    }

    /**
     * Apply Font to a menu item
     *
     * @param menu Menu
     * @param f The font to apply
     */
    private void applyFontToMenuItem(JMenu menu, Font f) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem menuItem = menu.getItem(i);
            if (menuItem != null) {
                menuItem.setFont(f);
            }
        }
    }

    /**
     * Add a list of recipients to JTextAreaRecipientsTo
     *
     * @param recipients The set containing recipients to add
     */
    public void addRecipientsTo(Set<String> recipients) {
        if (recipients != null) {
            addRecipients(recipients, jTextAreaRecipientsTo);
        }
    }

    /**
     * Add a list of recipients to jTextAreaRecipientsCc
     *
     * @param recipients The set containing recipients to add
     */
    public void addCopyRecipients(Set<String> recipients) {
        if (recipients != null) {
            addRecipients(recipients, jTextAreaRecipientsCc);
        }
    }

    /**
     * Add a list of recipients to jTextAreaRecipientsCc
     *
     * @param recipients The set containing recipients to add
     */
    public void addBlindCopyRecipients(Set<String> recipients) {
        if (recipients != null) {
            addRecipients(recipients, jTextAreaRecipientsBcc);
        }
    }

    /**
     * Add a list of recipients to a JTextArea
     *
     * @param recipients The set containing recipients to add
     * @param textArea The destination visualTextDebug area
     */
    private void addRecipients(Set<String> recipients, JTextArea textArea) {
        String newRecipientsStr = "";
        for (String recipient : recipients) {
            newRecipientsStr += recipient + "; ";
        }
        //Add recipients to those current list
        String finalRecipients = textArea.getText();
        if (finalRecipients.length() > 1) {
            if (!finalRecipients.trim().endsWith(";")) {
                finalRecipients += "; ";
            }
        }
        finalRecipients += newRecipientsStr;
        textArea.setText(finalRecipients);
    }

    private void print() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        JEditorPane printEditorPane = new JEditorPane();

        JFrame jframePrint = new JFrame();
        jframePrint.setIconImage(this.getIconImage());
        jframePrint.add(printEditorPane);

        String to = StringMgr.ReplaceAll(jTextAreaRecipientsTo.getText(), "<", "&lt;");
        to = StringMgr.ReplaceAll(to, ">", "&gt;");
        String cc = StringMgr.ReplaceAll(jTextAreaRecipientsCc.getText(), "<", "&lt;");
        cc = StringMgr.ReplaceAll(cc, ">", "&gt;");

        String sep = ": ";
        //sep = Util.fillWithHtmlBlanks(sep, 20);

        String printText = "";
        printText += jButtonTo.getText() + sep + to + "<br>";
        printText += jButtonCc.getText() + sep + cc + "<br>";
        //printText += jLabelDate.getText() + " " + jTextFieldDate.getText() + "<br>";
        printText += messages.getMessage("subject") + sep + jTextFieldSubject.getText() + "<br><br>";

        printText += htmlEditor.getText();
        printEditorPane.setContentType("text/html");
        printEditorPane.setText(printText);

        try {
            setCursor(Cursor.getPredefinedCursor(0));
            setAlwaysOnTop(false);

            printEditorPane.print();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPaneNewCustom.showException(this, e);
        }
    }

    private void saveAsDraft() {
        
        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            this.htmlEditor.getEditor().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            // Everything done here:
            int statusBuild = buildMessage();
            
            if (statusBuild == RECIPIENT_OK ) {
 
                PGPPublicKeysBuilder pGPPublicKeysBuilder = new PGPPublicKeysBuilder(recipientKeyList, connection);
                List<PGPPublicKey> pGPPublicKeyList = pGPPublicKeysBuilder.buildPGPPublicKeys();

                PgpTextEncryptor pgpTextEncryptor = new PgpTextEncryptor(pGPPublicKeyList);
                String encrypted = pgpTextEncryptor.encrypt(incomingMessageDTO.getBody());
                incomingMessageDTO.setBody(encrypted);
                encrypted = pgpTextEncryptor.encrypt(incomingMessageDTO.getSubject());
                incomingMessageDTO.setSubject(encrypted);
                
                debug("incomingMessageDTO saved: " + incomingMessageDTO);
   
                // Afd file paths to dratfs
                // Empty Attachment info but real files info
                List<IncomingAttachementDTO> incomingAttachementDTOListEmpty = new ArrayList<>();
                incomingMessageDTO.setAttachments(incomingAttachementDTOListEmpty);
                List<String> filesToAttach = new ArrayList<>();
                for (File file : fileListManager.getFiles()) {
                    filesToAttach.add(file.toString());
                }
                incomingMessageDTO.setFileToAttachList(filesToAttach);
                
                MessageDraftManager messageDraftSaver = new MessageDraftManager(userNumber);
                messageDraftSaver.save(incomingMessageDTO);
                
                if (getCaller() instanceof Main) {
                    Main theMain = getCaller();
                    theMain.createTable();
                }
  
                this.setCursor(Cursor.getDefaultCursor());
                this.htmlEditor.getEditor().setCursor(Cursor.getDefaultCursor());
                this.isChanged = false;
                
            } else {
                this.setCursor(Cursor.getDefaultCursor());
                this.htmlEditor.getEditor().setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(this, messages.getMessage("unable_to_save_message"));
                this.isChanged = true;
            }
        } catch (Exception exception) {
            this.setCursor(Cursor.getDefaultCursor());
            this.htmlEditor.getEditor().setCursor(Cursor.getDefaultCursor());
            exception.printStackTrace();
            JOptionPaneNewCustom.showException(this, exception);
        }
        
    }

    private void sendMessage() {

        debug("");
        debug("BEGIN send " + new Date());

        int statusSend = RECIPIENT_NOT_SET;

        if (!checkSubjectLength()) {
            return;
        }

        try {
            statusSend = buildMessage();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPaneNewCustom.showException(this.caller, e);
            return;
        }

        try {

            // Test if there is enough space per file
            if (!checkFreeSpaceForEncryption()) {
                return;
            }

            if (!checkForDateCoherence()) {
                return;
            }
            
            boolean askForConfirm = UserPrefManager.getBooleanPreference(UserPrefManager.ASK_FOR_CONFIRM);
            if (askForConfirm) {
                boolean sendConfirmed = doSendAfterAskForConfirm();
                if (!sendConfirmed) {
                    return;
                }
            }

            //Does user authorized to send a message because of total folders size
            long actualStorage = MessageTransfer.getTotalMailboxSize(connection, userNumber);
            long maximumStorage = StoreParms.getStorageForSubscription(SubscriptionLocalStore.getSubscription());

            if (actualStorage > maximumStorage) {
                this.setCursor(Cursor.getDefaultCursor());

                if (SubscriptionLocalStore.getSubscription() != StoreParms.PRODUCT_PLATINUM) {
                    String productName = StoreParms.getProductNameForSubscription(SubscriptionLocalStore.getSubscription());
                    String htmlMessage = HtmlTextUtil.getHtmlHelpContent("upgrade_storage_capacity_exceeded");
                    String displayMaximumStorage = MessageTableCellRenderer.getDisplaySize(maximumStorage);

                    htmlMessage = MessageFormat.format(htmlMessage, productName, displayMaximumStorage);
                    new UnavailableFeatureDialog(null, this.userNumber, connection, htmlMessage, true).setVisible(true);
                    return;
                } else {
                    String msg = messages.getMessage("storage_capacity_exceeded");
                    JOptionPane.showMessageDialog(this, msg, "Safester", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } catch (Exception e) {
            JOptionPaneNewCustom.showException(rootPane, e);
            return;
        }

        if (statusSend == MessageComposer.RECIPIENT_OK) {
            ThreadLocker threadLocker = new ThreadLocker();

            // Try to lock. If this return false ==> Thread is already locked
            if (!threadLocker.TryTolock()) {
                return; // Do nothing
            }
            
            debug("BEGIN BODY");
            debug(incomingMessageDTO.getBody());
            debug("END BODY");

            // We are now locked ==> Send message.
            try {
                /*
                MessageLocalStoreCache.remove(Parms.OUTBOX_ID);
                MessageSender messageSender = new MessageSender(this, connection, message, recipientKeyList);
                messageSender.send();
                */
                List<File> enuncryptedFiles = fileListManager.getFiles();
                                
                AwakeConnection awakeConnection = (AwakeConnection) connection;
                KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.buildFromAwakeConnection(connection);
                AwakeProgressManager awakeProgressManager = new DefaultAwakeProgressManager();  
  
                PGPPublicKeysBuilder pGPPublicKeysBuilder = new PGPPublicKeysBuilder(recipientKeyList, connection);
                List<PGPPublicKey> pGPPublicKeyList = pGPPublicKeysBuilder.buildPGPPublicKeys();

                PgpTextEncryptor pgpTextEncryptor = new PgpTextEncryptor(pGPPublicKeyList);
                String bodyEncrypted = pgpTextEncryptor.encrypt(incomingMessageDTO.getBody());
                incomingMessageDTO.setBody(bodyEncrypted);
                
                ApiMessageSender apiMessageSender = new ApiMessageSender(kawanHttpClient,
		incomingMessageDTO.getSenderEmailAddr(), awakeConnection.getAuthenticationToken(), incomingMessageDTO, enuncryptedFiles,
		awakeProgressManager);
	        
                ApiEncryptAttachmentsUsingThread apiEncryptAttachmentsUsingThread = new ApiEncryptAttachmentsUsingThread(
                        apiMessageSender, pGPPublicKeyList, this);
                apiEncryptAttachmentsUsingThread.encryptAndsendMessage();
        
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPaneNewCustom.showException(this.caller, e);
                return;
            } finally {
                this.setCursor(Cursor.getDefaultCursor());
                threadLocker.unlock();
            }
        }
    }

    private boolean checkSubjectLength() {
        int length = this.jTextFieldSubject.getText().trim().length();

        if (length > 80) {
            String msg = messages.getMessage("subject_maxlength_exceeded");
            JOptionPane.showMessageDialog(this, msg, "Safester", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Check user date/time settings coherence. Done only once in session. If
     * the user local date/time is incoherent, display an explanation message
     * and return false.
     *
     * @return true if use date/time setting is coherent, else false.
     */
    private boolean checkForDateCoherence() throws Exception {
        long serverTime = new ServerTimeHolder(connection).getTime();
        long userTime = new Date().getTime();

        long secondsUserAheadOfServer = (userTime - serverTime) / 1000;
        int day = 3600 * 24; // Days in seconds

        if (secondsUserAheadOfServer > 2 * day) {
            String explain = messages.getMessage("please_set_correct_time");
            String errorMsg = messages.getMessage("error");
            JOptionPane.showMessageDialog(rootPane, explain, errorMsg, JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check for each file if there is enough space for encryption on the
     * concerned device. if not, display an explanation message and return false
     *
     * @return true if space is sufficient, else false.
     */
    private boolean checkFreeSpaceForEncryption() {
        List<File> files = fileListManager.getFiles();

        for (File file : files) {

            Long freeSpace = file.getFreeSpace();
            long fileLength = file.length();

            if (freeSpace < fileLength) {
                String deviceDirectory = file.getParent();
                String fileName = file.toString();
                String errorMsg = messages.getMessage("free_space_required_for_encryption");
                String explain = SwingUtil.getTextContent("the_file_can_not_be_encrypted");

                explain = MessageFormat.format(explain, fileName, deviceDirectory, fileLength, freeSpace);

                JOptionPane.showMessageDialog(rootPane, explain, errorMsg, JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    /**
     * Create the MessageLocal object from Window element
     *
     * @return
     */
    private int buildMessage() {

        //String body = htmlEditor.getText();

        if (incomingMessageDTO == null) {
            incomingMessageDTO = new IncomingMessageDTO();
        }

        // Required for DRAFT identification
        if (this.messageId > 0) {
            incomingMessageDTO.setMessageId(messageId);
        }
        else {
            incomingMessageDTO.setMessageId(-1);
        }
        
        //message.setSenderUserNumber(this.userNumber);
        //message.setBody("<br>" + htmlEditor.getText());
        incomingMessageDTO.setDesktopCreation(true);
        incomingMessageDTO.setSenderEmailAddr(keyId);
        
        String rawText = htmlEditor.getText();
        String text = TextCleanUtil.cleanSpecialChars(rawText);
        
        //debug("rawText:");
        //debug(rawText);
        //bug("text:");
        //bug(text);
        
        // Test that any HTML encoding pass
        // Ceci est une cha&#238;ne accentu&#233;e.
        // Ceci est une cha&icirc;ne accentu&eacute;e
        if (TEST_CLASSIC_HTML) {
            text = text.replace("&#238;", "&icirc;");
            text = text.replace("&#233;", "&eacute;");
            debug("text classic HTML");
        } else {
            debug("text HTML");
        }

        debug(text);
        
        
        // 23/10/19 HACK NDP: for drafts, do not repeat operation
        if (folderId != Parms.DRAFT_ID) {
            incomingMessageDTO.setBody("<br>" + text);
        }
        else {
            incomingMessageDTO.setBody(text);
        }
        
        if (doViualTextDebug()) return MessageComposer.RECIPIENT_NOT_SET;

        if (!recipientSet()) {
            JOptionPane.showMessageDialog(this, messages.getMessage("no_recipient_set"));
            return MessageComposer.RECIPIENT_NOT_SET;
        }

        if (DEBUG) {
            debug("-BEGIN message.setBody(editor.getText(): ");
            debug(htmlEditor.getText());
            debug("-END   message.setBody(editor.getText(): ");
        }

        incomingMessageDTO.setPrintable(!jToggleButtonNoPrint.isSelected());
        incomingMessageDTO.setFowardable(!jToggleButtonNoFoward.isSelected());
        incomingMessageDTO.setAnonymousNotification(jToggleButtonSendAnonymousNotification.isSelected());

        long sizeMessage = incomingMessageDTO.getBody().length();
        short userSubscription = SubscriptionLocalStore.getSubscription();

        if (sizeMessage > StoreParms.getBodyLimitForSubscription(userSubscription)) {
            if (userSubscription == StoreParms.PRODUCT_FREE) {
                String htmlMessage = HtmlTextUtil.getHtmlHelpContent("upgrade_for_body_size");
                String displayBodyLength = MessageTableCellRenderer.getDisplaySize(sizeMessage);
                long maximumLength = StoreParms.getBodyLimitForSubscription(userSubscription);
                String productName = StoreParms.getProductNameForSubscription(userSubscription);
                String displayMaximumLength = MessageTableCellRenderer.getDisplaySize(maximumLength);

                htmlMessage = MessageFormat.format(htmlMessage, displayBodyLength, productName, displayMaximumLength);
                new UnavailableFeatureDialog(null, this.userNumber, this.connection, htmlMessage, true).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, messages.getMessage("body_size_limit_exceeded"));
            }

            return MessageComposer.MESSAGE_SIZE_EXCEEDED;
        }

        //List<AttachmentLocal> attachments = buildAttachList();
        
        List<IncomingAttachementDTO> attachments = null;
        try {
            attachments = IncomingAttachementDTOUtil.getAttachmentsAddingPgpExt(fileListManager.getFiles());
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(MessageComposer.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, messages.getMessage("can_not_attach_file") + " " + ex.getMessage());
        }
        
        if (attachments != null && !attachments.isEmpty()) {

            long attachmentLength = computeAttachmentLength(attachments);
            sizeMessage += attachmentLength;

            if (attachmentLength > StoreParms.getAttachLimitForSubscription(userSubscription)) {
                if (userSubscription < StoreParms.PRODUCT_PLATINUM) {
                    String htmlMessage = HtmlTextUtil.getHtmlHelpContent("upgrade_for_attach_size");
                    String displayAttachLength = MessageTableCellRenderer.getDisplaySize(attachmentLength);
                    long maximumLength = StoreParms.getAttachLimitForSubscription(userSubscription);
                    String productName = StoreParms.getProductNameForSubscription(userSubscription);
                    String displayMaximumLength = MessageTableCellRenderer.getDisplaySize(maximumLength);

                    htmlMessage = MessageFormat.format(htmlMessage, displayAttachLength, productName, displayMaximumLength);

                    new UnavailableFeatureDialog(null, this.userNumber, this.connection, htmlMessage, true).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, messages.getMessage("attachment_size_limit_exceeded"));
                }
                return MessageComposer.MESSAGE_SIZE_EXCEEDED;
            }
            //message.setIsWithAttachment(true);
            //message.setAttachmentLocal(attachments);
            incomingMessageDTO.setAttachments(attachments);
        }
        
        //message.setDateMessage(new Timestamp(System.currentTimeMillis()));

        // message.setSubject(jTextFieldSubject.getText());
        // Convert subject to Html before encryption
        incomingMessageDTO.setSubject(HtmlConverter.toHtml(jTextFieldSubject.getText()));

        //message.setSizeMessage(sizeMessage);
        incomingMessageDTO.setSize(sizeMessage);
        
        //recipients = new Vector<RecipientLocal>();
        
        incomingRecipientsDTO = new ArrayList<>();
        recipientKeyList = new HashSet<String>();
        recipientKeyList.add(keyId);

        int recipientStatus = MessageComposer.RECIPIENT_OK;
        
        int rc = buildRecipientList(jTextAreaRecipientsTo, Parms.RECIPIENT_TO);
        if (rc != MessageComposer.RECIPIENT_OK) {
            recipientStatus = rc;
        }

        // HACK NDP
        //debug("jTextAreaRecipientsCc: " + jTextAreaRecipientsCc.getText() + ":");

        rc = buildRecipientList(jTextAreaRecipientsCc, Parms.RECIPIENT_CC);

        if (rc != MessageComposer.RECIPIENT_OK) {
            recipientStatus = rc;
        }

        rc = buildRecipientList(jTextAreaRecipientsBcc, Parms.RECIPIENT_BCC);

        if (rc != MessageComposer.RECIPIENT_OK) {
            recipientStatus = rc;
        }

        if (recipientStatus == MessageComposer.RECIPIENT_OK) {

            int recipientsLimit = StoreParms.getRecipientsLimitForSubscription(SubscriptionLocalStore.getSubscription());

            if (incomingRecipientsDTO.size() > recipientsLimit) {

                String htmlMessage = HtmlTextUtil.getHtmlHelpContent("upgrade_for_recipients_number");
                String productName = StoreParms.getProductNameForSubscription(userSubscription);

                htmlMessage = MessageFormat.format(htmlMessage, recipientKeyList.size(), productName, recipientsLimit);
                new UnavailableFeatureDialog(null, this.userNumber, this.connection, htmlMessage, true).setVisible(true);

                return MessageComposer.RECIPIENT_INVALID;
            }

            // Encode in HTML the names in incomingRecipientsDTO
            incomingRecipientsDTO = htmlEncodeNames(incomingRecipientsDTO);
            
            incomingMessageDTO.setRecipients(incomingRecipientsDTO);
        }
        
        return recipientStatus;

    }

        
    private boolean doViualTextDebug() throws HeadlessException {
        String visualTextDebug = incomingMessageDTO.getBody();
        if (VISUAL_DEBUG) {
            JOptionPane.showMessageDialog(this, visualTextDebug);
        }
        if (VISUAL_DEBUG) {
            new NewsFrame(this, visualTextDebug, "debug").setVisible(true);
        }
        if (VISUAL_DEBUG) {
            return true;
        }
        return false;
    }

    private static List<IncomingRecipientDTO> htmlEncodeNames(final List<IncomingRecipientDTO> incomingRecipientsDTO) {
        
        List<IncomingRecipientDTO> incomingRecipientDTOListNew = new ArrayList<>();
        
        for (IncomingRecipientDTO incomingRecipientDTO : incomingRecipientsDTO) {
            String name = incomingRecipientDTO.getRecipientName();
            name = HtmlConverter.toHtml(name);
            incomingRecipientDTO.setRecipientName(name);
            incomingRecipientDTOListNew.add(incomingRecipientDTO);
        }
        
        return incomingRecipientDTOListNew;
    }
        
    private long computeAttachmentLength(List<IncomingAttachementDTO> attachments) {
        long totalLength = 0;
        for (IncomingAttachementDTO attachment : attachments) {
            totalLength += attachment.getSize();
        }
        return totalLength;
    }

    private boolean recipientSet() {

        boolean recipientSet = false;
        if (jTextAreaRecipientsTo.getText() != null && jTextAreaRecipientsTo.getText().length() > 1) {
            recipientSet = true;
        }

        if (!recipientSet) {
            if (jTextAreaRecipientsCc.getText() != null && jTextAreaRecipientsCc.getText().length() > 1) {
                recipientSet = true;
            }
        }

        return recipientSet;
    }

    public void putMessage() {

        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            this.jTextFieldSubject.setText(incomingMessageDTO.getSubject());
            this.htmlEditor.setText(incomingMessageDTO.getBody().replaceAll(System.getProperty("line.separator"), "<br>"));
            
            rapidAYT.setTextComponent(null);
            this.htmlEditor.setCaretPosition(0);

            JDialogDiscardableMessage pgeepShowDiscardableMessage = new JDialogDiscardableMessage(thisOne, messages.getMessage("message_encrypted"));
            
            //uploadMessage();
            this.requestFocus();
            this.isChanged = false;
            //String title = thisOne.getTitle();
            //title = StringUtils.removeEnd(title, "*");

            // Delete the Draft
           if (folderId == Parms.DRAFT_ID && messageId > 0) {
                MessageDraftManager messageDraftSaver = new MessageDraftManager(userNumber);
                List<Integer> messageIdList = new ArrayList<>();
                messageIdList.add(messageId);
                try {
                    messageDraftSaver.delete(messageIdList);
                } catch (IOException ex) {
                    Logger.getLogger(MessageComposer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (caller instanceof Main) {
                MessageLocalStoreCache.remove(Parms.OUTBOX_ID);
                Main main = (Main) caller;
                main.createTable();
            }
                           
            //Close windows-
            this.dispose();
            
            new ThreadLocker().unlock();
        } finally {
            this.setCursor(Cursor.getDefaultCursor());
            this.htmlEditor.setText(incomingMessageDTO.getBody().replaceAll("<br>", System.getProperty("line.separator")));
            //enableSendAction(true);
        }

    }

    private void enableSendAction(boolean enable) {
        windowClosingEnabled = enable;
        jToolBar1.setEnabled(enable);
        jMenuBar1.setEnabled(enable);
        this.jTextAreaRecipientsTo.setEnabled(enable);
        this.jTextAreaRecipientsCc.setEnabled(enable);
        this.jTextFieldSubject.setEnabled(enable);
        this.htmlEditor.setEnabled(enable);
    }

    /*
    public void uploadDraft() throws SQLException {

        if (caller instanceof Main) {
            Main safeShareItMain = (Main) caller;
            safeShareItMain.deleteMessage(message.getMessageId(), Parms.DRAFT_ID);
        }

        MessageTransfer messageTransfert = new MessageTransfer(connection, keyId, userNumber);
        this.messageId = messageTransfert.putDraft(message);
        this.setCursor(Cursor.getDefaultCursor());
        this.htmlEditor.getEditor().setCursor(Cursor.getDefaultCursor());
        JOptionPane.showMessageDialog(rootPane, messages.getMessage("message_saved_to_draft"));

        this.isChanged = false;

        if (caller instanceof Main) {
            Main theMain = (Main) caller;
            theMain.createTable();
        }

        String title = thisOne.getTitle();
        title = StringUtils.removeEnd(title, "*");

        thisOne.setTitle(title);

        this.requestFocus();
    }
    */
    
    /*
    private void uploadMessage() throws SQLException {
        
        if (this.messageId != -1) {
            message.setMessageId(messageId);
            //Delete draft
            if (caller instanceof Main) {
                Main theMain = (Main) caller;
                theMain.deleteMessage(message.getMessageId(), Parms.DRAFT_ID);
                this.requestFocus();
            }
        }

        MessageTransfer messageTransfert = new MessageTransfer(connection, keyId, userNumber);
        messageTransfert.put(message);

        this.requestFocus();

        this.isChanged = false;
        String title = thisOne.getTitle();
        title = StringUtils.removeEnd(title, "*");

        //Close windows-
        this.dispose();

    }
    */

    private int buildRecipientList(JTextArea textArea, int recipientType) {

        // If notarization BCC email is set for domain in table bcc_notarization, send a bcc email to address in table
        if (recipientType == Parms.RECIPIENT_BCC) {
            textArea = MessageComposerNotarization.addEmailBccNotarization(rootPane, textArea, this.keyId, this.connection);
        }

        //List<RecipientLocal> bufferRecipients = new Vector<RecipientLocal>();
        
        List<IncomingRecipientDTO> bufferRecipients = new ArrayList<>();
        
        if (textArea.getText() == null) {
            return MessageComposer.RECIPIENT_NOT_SET;
        }
        String recipientTo = textArea.getText().trim();
        int position = 1;

        try {
            RecipientsEmailBuilder recipientsEmailBuilder = new RecipientsEmailBuilder(connection, userNumber, recipientTo, recipientType);
            List<String> emailsList = recipientsEmailBuilder.build();

            if (recipientsEmailBuilder.getFirstInvalidEmail() != null) {
                String msg = messages.getMessage("recipient_not_contain_email");
                msg = msg.replace("{0}", recipientsEmailBuilder.getFirstInvalidEmail());
                JOptionPane.showMessageDialog(this, msg);
                textArea.requestFocus();
                return MessageComposer.RECIPIENT_INVALID;
            }

            AwakeConnection awakeConnection = (AwakeConnection) connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

            KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.buildFromAwakeConnection(awakeConnection);
            ApiMessages apiMessages = new ApiMessages(kawanHttpClient, awakeFileSession.getUsername(),
                    awakeFileSession.getAuthenticationToken());
            for (String emailAddres : emailsList) {
                if (!apiMessages.verifyEmailAddrMx(emailAddres)) {
                    String msg = messages.getMessage("recipient_not_contain_email_no_mx");
                    msg = msg.replace("{0}", emailAddres);
                    JOptionPane.showMessageDialog(this, msg);
                    textArea.requestFocus();
                    return MessageComposer.RECIPIENT_INVALID;
                }
            }
            
            for (String emailAddress : emailsList) {
                //int recipientUserNumber = - 1; // We don't care ==> not used any more

//                RecipientLocal recipient = new RecipientLocal();
//                recipient.setUserNumber(recipientUserNumber);
//                recipient.setTypeRecipient(recipientType);
//                recipient.setNameRecipient(emailAddress);
//                recipient.setRecipientPosition(position++);

                IncomingRecipientDTO recipient = new IncomingRecipientDTO();
                recipient.setRecipientType(recipientType);
                recipient.setRecipientName(recipientsEmailBuilder.getName(emailAddress));
                recipient.setRecipientEmailAddr(emailAddress);
                              
                bufferRecipients.add(recipient);

                this.recipientKeyList.add(emailAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPaneNewCustom.showException(rootPane, e);
        }

        //recipients.addAll(bufferRecipients);
        incomingRecipientsDTO.addAll(bufferRecipients);
        
        return MessageComposer.RECIPIENT_OK;
    }

    public Main getCaller() {
        if (this.caller instanceof Main) {
            return (Main) this.caller;
        } else if (this.caller instanceof GroupListNew) {
            return (Main) ((GroupListNew) this.caller).getMain();
        } else {
            return null;
        }
    }

    private List<AttachmentLocal> buildAttachList() {
        List<AttachmentLocal> attachments = null;

        List<File> files = fileListManager.getFiles();
        if (files != null && !files.isEmpty()) {
            attachments = new Vector<AttachmentLocal>();
            int attachPosition = 1;
            for (File file : files) {
                AttachmentLocal attachmentLocal = new AttachmentLocal();
                attachmentLocal.setFileName(file.toString());
                attachmentLocal.setAttachPosition(attachPosition++);
                attachmentLocal.setFileSize(file.length());

                attachments.add(attachmentLocal);
            }
        }

        return attachments;
    }

    @Override
    public void dispose() {
        if (isChanged) {
            int result = JOptionPane.showConfirmDialog(rootPane, messages.getMessage("message_not_save"), messages.getMessage("warning"), JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                this.saveAsDraft();
            } else {
                isChanged = false;
            }
        }
        if (!isChanged) {
            super.dispose();
        }
    }

    private boolean doSendAfterAskForConfirm() {
        Object[] options = {this.messages.getMessage("yes"),
            this.messages.getMessage("no")};

        int result = JOptionPane.showOptionDialog(rootPane,
                this.messages.getMessage("are_you_sure_you_want_to_send"), 
                this.messages.getMessage("warning"), 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.WARNING_MESSAGE,
                null, 
                options, 
                options[0]
        );
				
        return (result == JOptionPane.YES_OPTION);
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

        buttonGroupLanguage = new javax.swing.ButtonGroup();
        jPanelCenterMain = new javax.swing.JPanel();
        jPanelSepButtons = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanelSepRecipients = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabelRecipients = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelSep4 = new javax.swing.JPanel();
        jPanelRecipients = new javax.swing.JPanel();
        jPanelRecipientsTo = new javax.swing.JPanel();
        jButtonTo = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanelScrollPaneTo = new javax.swing.JPanel();
        jScrollPaneTo = new javax.swing.JScrollPane();
        jTextAreaRecipientsTo = new javax.swing.JTextArea();
        jPanel9 = new javax.swing.JPanel();
        jPanelSepRecip = new javax.swing.JPanel();
        jPanelRecipientsCc = new javax.swing.JPanel();
        jButtonCc = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jPanelScrollPaneCc = new javax.swing.JPanel();
        jScrollPaneCc = new javax.swing.JScrollPane();
        jTextAreaRecipientsCc = new javax.swing.JTextArea();
        jPanel13 = new javax.swing.JPanel();
        jPanelSepRecipientsBcc = new javax.swing.JPanel();
        jPanelRecipientsBcc = new javax.swing.JPanel();
        jButtonBcc2 = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jPanelScrollPaneBcc = new javax.swing.JPanel();
        jScrollPanelBcc = new javax.swing.JScrollPane();
        jTextAreaRecipientsBcc = new javax.swing.JTextArea();
        jPanel16 = new javax.swing.JPanel();
        jPanelBccNotarization = new javax.swing.JPanel();
        jLabelBccNotification = new javax.swing.JLabel();
        jPanelSepButtons1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanelSepRecipients1 = new javax.swing.JPanel();
        jSeparator8 = new javax.swing.JSeparator();
        jPanelEmailAndFiles = new javax.swing.JPanel();
        jPanelSep1 = new javax.swing.JPanel();
        jPanelSubjectContainer = new javax.swing.JPanel();
        jPanelLabelSubject = new javax.swing.JPanel();
        jLabelSubject = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanelSubject = new javax.swing.JPanel();
        jTextFieldSubject = new javax.swing.JTextField();
        jPanel5_2 = new javax.swing.JPanel();
        jPanelSep2 = new javax.swing.JPanel();
        jPanelFiles = new javax.swing.JPanel();
        jPanelLabelAttached = new javax.swing.JPanel();
        jLabelAttached = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanelScrollPane = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListAttach = new javax.swing.JList();
        jPanel5_3 = new javax.swing.JPanel();
        jPanelSep3 = new javax.swing.JPanel();
        jPanelEmailBody = new javax.swing.JPanel();
        jPanelNorth = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jToolbarButtonSend = new javax.swing.JButton();
        jToolbarButtonSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jToolbarButtonAddRecipient = new javax.swing.JButton();
        jToolbarButtonAttach = new javax.swing.JButton();
        jToggleButtonBcc2 = new javax.swing.JToggleButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        jToggleButtonNoFoward = new javax.swing.JToggleButton();
        jToggleButtonNoPrint = new javax.swing.JToggleButton();
        jSeparatorSendAnonymous = new javax.swing.JToolBar.Separator();
        jToggleButtonSendAnonymousNotification = new javax.swing.JToggleButton();
        jSeparatorSendAnonymous1 = new javax.swing.JToolBar.Separator();
        jToolbarButtonHelp = new javax.swing.JButton();
        jPanel10x10 = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelEast = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemSend = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemPrint = new javax.swing.JMenuItem();
        jMenuItemClose = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        cancelMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        selectAlljMenuItem = new javax.swing.JMenuItem();
        jMenuTools = new javax.swing.JMenu();
        jMenuItemFind = new javax.swing.JMenuItem();
        jMenuItemReplace = new javax.swing.JMenuItem();
        jMenuSpellCheck = new javax.swing.JMenu();
        jRadioButtonMenuItemEnglish = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemFrench = new javax.swing.JRadioButtonMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemHelp = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(500, 330));

        jPanelCenterMain.setLayout(new javax.swing.BoxLayout(jPanelCenterMain, javax.swing.BoxLayout.Y_AXIS));

        jPanelSepButtons.setMaximumSize(new java.awt.Dimension(32767, 8));
        jPanelSepButtons.setMinimumSize(new java.awt.Dimension(438, 8));
        jPanelSepButtons.setPreferredSize(new java.awt.Dimension(443, 8));
        jPanelSepButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 12));

        jPanel1.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepButtons.add(jPanel1);

        jPanelCenterMain.add(jPanelSepButtons);

        jPanelSepRecipients.setLayout(new javax.swing.BoxLayout(jPanelSepRecipients, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator3.setMaximumSize(new java.awt.Dimension(20, 6));
        jSeparator3.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator3.setPreferredSize(new java.awt.Dimension(20, 6));
        jPanelSepRecipients.add(jSeparator3);

        jLabelRecipients.setText("jLabelRecipients");
        jLabelRecipients.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanelSepRecipients.add(jLabelRecipients);

        jSeparator2.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator2.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator2.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepRecipients.add(jSeparator2);

        jPanelCenterMain.add(jPanelSepRecipients);

        jPanelSep4.setMaximumSize(new java.awt.Dimension(32787, 8));
        jPanelSep4.setMinimumSize(new java.awt.Dimension(390, 8));
        jPanelSep4.setPreferredSize(new java.awt.Dimension(20, 8));
        jPanelSep4.setLayout(new javax.swing.BoxLayout(jPanelSep4, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenterMain.add(jPanelSep4);

        jPanelRecipients.setLayout(new javax.swing.BoxLayout(jPanelRecipients, javax.swing.BoxLayout.Y_AXIS));

        jPanelRecipientsTo.setMaximumSize(new java.awt.Dimension(2147483647, 31));
        jPanelRecipientsTo.setMinimumSize(new java.awt.Dimension(104, 31));
        jPanelRecipientsTo.setPreferredSize(new java.awt.Dimension(171, 31));
        jPanelRecipientsTo.setLayout(new javax.swing.BoxLayout(jPanelRecipientsTo, javax.swing.BoxLayout.LINE_AXIS));

        jButtonTo.setText("To...");
        jButtonTo.setMaximumSize(new java.awt.Dimension(130, 24));
        jButtonTo.setMinimumSize(new java.awt.Dimension(130, 24));
        jButtonTo.setPreferredSize(new java.awt.Dimension(130, 24));
        jButtonTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonToActionPerformed(evt);
            }
        });
        jPanelRecipientsTo.add(jButtonTo);

        jPanel2.setMaximumSize(new java.awt.Dimension(5, 10));
        jPanel2.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanel2.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelRecipientsTo.add(jPanel2);

        jPanelScrollPaneTo.setLayout(new javax.swing.BoxLayout(jPanelScrollPaneTo, javax.swing.BoxLayout.X_AXIS));

        jScrollPaneTo.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextAreaRecipientsTo.setLineWrap(true);
        jTextAreaRecipientsTo.setWrapStyleWord(true);
        jTextAreaRecipientsTo.setNextFocusableComponent(jTextAreaRecipientsCc);
        jTextAreaRecipientsTo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextAreaRecipientsToKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextAreaRecipientsToKeyReleased(evt);
            }
        });
        jScrollPaneTo.setViewportView(jTextAreaRecipientsTo);

        jPanelScrollPaneTo.add(jScrollPaneTo);

        jPanelRecipientsTo.add(jPanelScrollPaneTo);

        jPanel9.setMaximumSize(new java.awt.Dimension(1, 32767));
        jPanel9.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelRecipientsTo.add(jPanel9);

        jPanelRecipients.add(jPanelRecipientsTo);

        jPanelSepRecip.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSepRecip.setMinimumSize(new java.awt.Dimension(10, 5));
        jPanelSepRecip.setPreferredSize(new java.awt.Dimension(10, 5));

        javax.swing.GroupLayout jPanelSepRecipLayout = new javax.swing.GroupLayout(jPanelSepRecip);
        jPanelSepRecip.setLayout(jPanelSepRecipLayout);
        jPanelSepRecipLayout.setHorizontalGroup(
            jPanelSepRecipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1412, Short.MAX_VALUE)
        );
        jPanelSepRecipLayout.setVerticalGroup(
            jPanelSepRecipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelRecipients.add(jPanelSepRecip);

        jPanelRecipientsCc.setMaximumSize(new java.awt.Dimension(32830, 31));
        jPanelRecipientsCc.setMinimumSize(new java.awt.Dimension(100, 31));
        jPanelRecipientsCc.setPreferredSize(new java.awt.Dimension(171, 31));
        jPanelRecipientsCc.setLayout(new javax.swing.BoxLayout(jPanelRecipientsCc, javax.swing.BoxLayout.LINE_AXIS));

        jButtonCc.setText("Cc...");
        jButtonCc.setMaximumSize(new java.awt.Dimension(130, 24));
        jButtonCc.setMinimumSize(new java.awt.Dimension(130, 24));
        jButtonCc.setPreferredSize(new java.awt.Dimension(130, 24));
        jButtonCc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCcActionPerformed(evt);
            }
        });
        jPanelRecipientsCc.add(jButtonCc);

        jPanel11.setMaximumSize(new java.awt.Dimension(5, 10));
        jPanel11.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanel11.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelRecipientsCc.add(jPanel11);

        jPanelScrollPaneCc.setLayout(new javax.swing.BoxLayout(jPanelScrollPaneCc, javax.swing.BoxLayout.X_AXIS));

        jScrollPaneCc.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextAreaRecipientsCc.setLineWrap(true);
        jTextAreaRecipientsCc.setWrapStyleWord(true);
        jTextAreaRecipientsCc.setNextFocusableComponent(jTextFieldSubject);
        jTextAreaRecipientsCc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextAreaRecipientsCcKeyPressed(evt);
            }
        });
        jScrollPaneCc.setViewportView(jTextAreaRecipientsCc);

        jPanelScrollPaneCc.add(jScrollPaneCc);

        jPanelRecipientsCc.add(jPanelScrollPaneCc);

        jPanel13.setMaximumSize(new java.awt.Dimension(1, 32767));
        jPanel13.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelRecipientsCc.add(jPanel13);

        jPanelRecipients.add(jPanelRecipientsCc);

        jPanelSepRecipientsBcc.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSepRecipientsBcc.setMinimumSize(new java.awt.Dimension(10, 5));

        javax.swing.GroupLayout jPanelSepRecipientsBccLayout = new javax.swing.GroupLayout(jPanelSepRecipientsBcc);
        jPanelSepRecipientsBcc.setLayout(jPanelSepRecipientsBccLayout);
        jPanelSepRecipientsBccLayout.setHorizontalGroup(
            jPanelSepRecipientsBccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1412, Short.MAX_VALUE)
        );
        jPanelSepRecipientsBccLayout.setVerticalGroup(
            jPanelSepRecipientsBccLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelRecipients.add(jPanelSepRecipientsBcc);

        jPanelRecipientsBcc.setMaximumSize(new java.awt.Dimension(32830, 31));
        jPanelRecipientsBcc.setMinimumSize(new java.awt.Dimension(100, 31));
        jPanelRecipientsBcc.setPreferredSize(new java.awt.Dimension(171, 31));
        jPanelRecipientsBcc.setLayout(new javax.swing.BoxLayout(jPanelRecipientsBcc, javax.swing.BoxLayout.LINE_AXIS));

        jButtonBcc2.setText("Bcc...");
        jButtonBcc2.setMaximumSize(new java.awt.Dimension(130, 24));
        jButtonBcc2.setMinimumSize(new java.awt.Dimension(130, 24));
        jButtonBcc2.setPreferredSize(new java.awt.Dimension(130, 24));
        jButtonBcc2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBcc2ActionPerformed(evt);
            }
        });
        jPanelRecipientsBcc.add(jButtonBcc2);

        jPanel15.setMaximumSize(new java.awt.Dimension(5, 10));
        jPanel15.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanel15.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelRecipientsBcc.add(jPanel15);

        jPanelScrollPaneBcc.setLayout(new javax.swing.BoxLayout(jPanelScrollPaneBcc, javax.swing.BoxLayout.X_AXIS));

        jScrollPanelBcc.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextAreaRecipientsBcc.setLineWrap(true);
        jTextAreaRecipientsBcc.setWrapStyleWord(true);
        jTextAreaRecipientsBcc.setNextFocusableComponent(jTextFieldSubject);
        jScrollPanelBcc.setViewportView(jTextAreaRecipientsBcc);

        jPanelScrollPaneBcc.add(jScrollPanelBcc);

        jPanelRecipientsBcc.add(jPanelScrollPaneBcc);

        jPanel16.setMaximumSize(new java.awt.Dimension(1, 32767));
        jPanel16.setPreferredSize(new java.awt.Dimension(1, 10));
        jPanelRecipientsBcc.add(jPanel16);

        jPanelRecipients.add(jPanelRecipientsBcc);

        jPanelBccNotarization.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 130, 5));

        jLabelBccNotification.setFont(new java.awt.Font("Tahoma", 2, 13)); // NOI18N
        jLabelBccNotification.setText("jLabelNotification");
        jLabelBccNotification.setRequestFocusEnabled(false);
        jPanelBccNotarization.add(jLabelBccNotification);

        jPanelRecipients.add(jPanelBccNotarization);

        jPanelSepButtons1.setMaximumSize(new java.awt.Dimension(32767, 8));
        jPanelSepButtons1.setMinimumSize(new java.awt.Dimension(438, 8));
        jPanelSepButtons1.setPreferredSize(new java.awt.Dimension(443, 8));
        jPanelSepButtons1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 12));

        jPanel3.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepButtons1.add(jPanel3);

        jPanelRecipients.add(jPanelSepButtons1);

        jPanelCenterMain.add(jPanelRecipients);

        jPanelSepRecipients1.setLayout(new javax.swing.BoxLayout(jPanelSepRecipients1, javax.swing.BoxLayout.LINE_AXIS));

        jSeparator8.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator8.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator8.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepRecipients1.add(jSeparator8);

        jPanelCenterMain.add(jPanelSepRecipients1);

        jPanelEmailAndFiles.setLayout(new javax.swing.BoxLayout(jPanelEmailAndFiles, javax.swing.BoxLayout.Y_AXIS));

        jPanelSep1.setMaximumSize(new java.awt.Dimension(32767, 8));
        jPanelSep1.setMinimumSize(new java.awt.Dimension(10, 8));
        jPanelSep1.setPreferredSize(new java.awt.Dimension(10, 8));
        jPanelSep1.setRequestFocusEnabled(false);
        jPanelEmailAndFiles.add(jPanelSep1);

        jPanelSubjectContainer.setMaximumSize(new java.awt.Dimension(2147483647, 32));
        jPanelSubjectContainer.setMinimumSize(new java.awt.Dimension(73, 32));
        jPanelSubjectContainer.setPreferredSize(new java.awt.Dimension(126, 32));
        jPanelSubjectContainer.setLayout(new javax.swing.BoxLayout(jPanelSubjectContainer, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLabelSubject.setMaximumSize(new java.awt.Dimension(130, 24));
        jPanelLabelSubject.setMinimumSize(new java.awt.Dimension(130, 24));
        jPanelLabelSubject.setPreferredSize(new java.awt.Dimension(130, 24));
        jPanelLabelSubject.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING));

        jLabelSubject.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelSubject.setText("Subject");
        jPanelLabelSubject.add(jLabelSubject);

        jPanelSubjectContainer.add(jPanelLabelSubject);

        jPanel6.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel6.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSubjectContainer.add(jPanel6);

        jPanelSubject.setMaximumSize(new java.awt.Dimension(2147483647, 31));
        jPanelSubject.setMinimumSize(new java.awt.Dimension(6, 31));
        jPanelSubject.setPreferredSize(new java.awt.Dimension(59, 31));
        jPanelSubject.setLayout(new javax.swing.BoxLayout(jPanelSubject, javax.swing.BoxLayout.LINE_AXIS));

        jTextFieldSubject.setText("jTextFieldSubject");
        jTextFieldSubject.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jTextFieldSubject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSubjectActionPerformed(evt);
            }
        });
        jPanelSubject.add(jTextFieldSubject);

        jPanelSubjectContainer.add(jPanelSubject);

        jPanel5_2.setMaximumSize(new java.awt.Dimension(0, 5));
        jPanel5_2.setMinimumSize(new java.awt.Dimension(0, 5));
        jPanel5_2.setPreferredSize(new java.awt.Dimension(0, 5));

        javax.swing.GroupLayout jPanel5_2Layout = new javax.swing.GroupLayout(jPanel5_2);
        jPanel5_2.setLayout(jPanel5_2Layout);
        jPanel5_2Layout.setHorizontalGroup(
            jPanel5_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel5_2Layout.setVerticalGroup(
            jPanel5_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSubjectContainer.add(jPanel5_2);

        jPanelEmailAndFiles.add(jPanelSubjectContainer);

        jPanelSep2.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSep2.setMinimumSize(new java.awt.Dimension(10, 5));
        jPanelSep2.setPreferredSize(new java.awt.Dimension(10, 5));

        javax.swing.GroupLayout jPanelSep2Layout = new javax.swing.GroupLayout(jPanelSep2);
        jPanelSep2.setLayout(jPanelSep2Layout);
        jPanelSep2Layout.setHorizontalGroup(
            jPanelSep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1412, Short.MAX_VALUE)
        );
        jPanelSep2Layout.setVerticalGroup(
            jPanelSep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelEmailAndFiles.add(jPanelSep2);

        jPanelFiles.setMaximumSize(new java.awt.Dimension(32767, 50));
        jPanelFiles.setMinimumSize(new java.awt.Dimension(10, 50));
        jPanelFiles.setPreferredSize(new java.awt.Dimension(10, 50));
        jPanelFiles.setLayout(new javax.swing.BoxLayout(jPanelFiles, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLabelAttached.setMaximumSize(new java.awt.Dimension(130, 24));
        jPanelLabelAttached.setMinimumSize(new java.awt.Dimension(130, 24));
        jPanelLabelAttached.setPreferredSize(new java.awt.Dimension(130, 24));
        jPanelLabelAttached.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING));

        jLabelAttached.setText("Pièces Jointes");
        jPanelLabelAttached.add(jLabelAttached);

        jPanelFiles.add(jPanelLabelAttached);

        jPanel5.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel5.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel5.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelFiles.add(jPanel5);

        jPanelScrollPane.setLayout(new javax.swing.BoxLayout(jPanelScrollPane, javax.swing.BoxLayout.LINE_AXIS));

        jListAttach.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListAttach.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        jListAttach.setVisibleRowCount(-1);
        jScrollPane2.setViewportView(jListAttach);

        jPanelScrollPane.add(jScrollPane2);

        jPanelFiles.add(jPanelScrollPane);

        jPanel5_3.setMaximumSize(new java.awt.Dimension(0, 5));
        jPanel5_3.setMinimumSize(new java.awt.Dimension(0, 5));
        jPanel5_3.setPreferredSize(new java.awt.Dimension(0, 5));

        javax.swing.GroupLayout jPanel5_3Layout = new javax.swing.GroupLayout(jPanel5_3);
        jPanel5_3.setLayout(jPanel5_3Layout);
        jPanel5_3Layout.setHorizontalGroup(
            jPanel5_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel5_3Layout.setVerticalGroup(
            jPanel5_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelFiles.add(jPanel5_3);

        jPanelEmailAndFiles.add(jPanelFiles);

        jPanelSep3.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelEmailAndFiles.add(jPanelSep3);

        jPanelCenterMain.add(jPanelEmailAndFiles);

        jPanelEmailBody.setPreferredSize(new java.awt.Dimension(40, 400));
        jPanelEmailBody.setLayout(new javax.swing.BoxLayout(jPanelEmailBody, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenterMain.add(jPanelEmailBody);

        getContentPane().add(jPanelCenterMain, java.awt.BorderLayout.CENTER);

        jPanelNorth.setLayout(new javax.swing.BoxLayout(jPanelNorth, javax.swing.BoxLayout.LINE_AXIS));

        jPanel4.setLayout(new java.awt.GridLayout(1, 0));

        jToolBar1.setRollover(true);

        jToolbarButtonSend.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/mail_out.png"))); // NOI18N
        jToolbarButtonSend.setActionCommand("Send");
        jToolbarButtonSend.setFocusable(false);
        jToolbarButtonSend.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToolbarButtonSend.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolbarButtonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToolbarButtonSendActionPerformed(evt);
            }
        });
        jToolBar1.add(jToolbarButtonSend);

        jToolbarButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/floppy_disk.png"))); // NOI18N
        jToolbarButtonSave.setActionCommand("Send");
        jToolbarButtonSave.setFocusable(false);
        jToolbarButtonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToolbarButtonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolbarButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToolbarButtonSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(jToolbarButtonSave);
        jToolBar1.add(jSeparator1);

        jToolbarButtonAddRecipient.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/businessman2_plus.png"))); // NOI18N
        jToolbarButtonAddRecipient.setFocusable(false);
        jToolbarButtonAddRecipient.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToolbarButtonAddRecipient.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolbarButtonAddRecipient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToolbarButtonAddRecipientActionPerformed(evt);
            }
        });
        jToolBar1.add(jToolbarButtonAddRecipient);

        jToolbarButtonAttach.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/paperclip2.png"))); // NOI18N
        jToolbarButtonAttach.setFocusable(false);
        jToolbarButtonAttach.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToolbarButtonAttach.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolbarButtonAttach.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToolbarButtonAttachActionPerformed(evt);
            }
        });
        jToolBar1.add(jToolbarButtonAttach);

        jToggleButtonBcc2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/window_bcc.png"))); // NOI18N
        jToggleButtonBcc2.setText("jToggleButtonBcc");
        jToggleButtonBcc2.setFocusable(false);
        jToggleButtonBcc2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonBcc2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonBcc2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonBcc2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jToggleButtonBcc2);
        jToolBar1.add(jSeparator7);

        jToggleButtonNoFoward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/mail_forward.png"))); // NOI18N
        jToggleButtonNoFoward.setText("jToggleButtonNoFoward");
        jToggleButtonNoFoward.setFocusable(false);
        jToggleButtonNoFoward.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonNoFoward.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonNoFoward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonNoFowardActionPerformed(evt);
            }
        });
        jToolBar1.add(jToggleButtonNoFoward);

        jToggleButtonNoPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/printer.png"))); // NOI18N
        jToggleButtonNoPrint.setText("jToggleButtonNoPrint");
        jToggleButtonNoPrint.setFocusable(false);
        jToggleButtonNoPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonNoPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonNoPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonNoPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(jToggleButtonNoPrint);
        jToolBar1.add(jSeparatorSendAnonymous);

        jToggleButtonSendAnonymousNotification.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/id_card.png"))); // NOI18N
        jToggleButtonSendAnonymousNotification.setText("jToggleButtonSendAnonymousNotification");
        jToggleButtonSendAnonymousNotification.setFocusable(false);
        jToggleButtonSendAnonymousNotification.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonSendAnonymousNotification.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonSendAnonymousNotification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSendAnonymousNotificationActionPerformed(evt);
            }
        });
        jToolBar1.add(jToggleButtonSendAnonymousNotification);
        jToolBar1.add(jSeparatorSendAnonymous1);

        jToolbarButtonHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/question.png"))); // NOI18N
        jToolbarButtonHelp.setActionCommand("Send");
        jToolbarButtonHelp.setFocusable(false);
        jToolbarButtonHelp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToolbarButtonHelp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolbarButtonHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToolbarButtonHelpActionPerformed(evt);
            }
        });
        jToolBar1.add(jToolbarButtonHelp);

        jPanel4.add(jToolBar1);

        jPanelNorth.add(jPanel4);

        jPanel10x10.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel10x10.setLayout(new javax.swing.BoxLayout(jPanel10x10, javax.swing.BoxLayout.LINE_AXIS));
        jPanelNorth.add(jPanel10x10);

        getContentPane().add(jPanelNorth, java.awt.BorderLayout.PAGE_START);

        jPanelWest.setMinimumSize(new java.awt.Dimension(12, 12));
        jPanelWest.setPreferredSize(new java.awt.Dimension(12, 12));
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelEast.setMinimumSize(new java.awt.Dimension(12, 12));
        jPanelEast.setPreferredSize(new java.awt.Dimension(12, 12));
        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);

        jPanelSouth.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSouth.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSouth.setPreferredSize(new java.awt.Dimension(656, 10));

        javax.swing.GroupLayout jPanelSouthLayout = new javax.swing.GroupLayout(jPanelSouth);
        jPanelSouth.setLayout(jPanelSouthLayout);
        jPanelSouthLayout.setHorizontalGroup(
            jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1436, Short.MAX_VALUE)
        );
        jPanelSouthLayout.setVerticalGroup(
            jPanelSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.PAGE_END);

        jMenuFile.setText("File");

        jMenuItemSend.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemSend.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/mail_out.png"))); // NOI18N
        jMenuItemSend.setText("jMenuItemSend");
        jMenuItemSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSendActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSend);

        jMenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/floppy_disk.png"))); // NOI18N
        jMenuItemSave.setText("jMenuItemSave");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSave);

        jMenuItemPrint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/printer.png"))); // NOI18N
        jMenuItemPrint.setText("jMenuItemPrint");
        jMenuItemPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPrintActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemPrint);

        jMenuItemClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItemClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/close.png"))); // NOI18N
        jMenuItemClose.setText("jMenuItemClose");
        jMenuItemClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCloseActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemClose);

        jMenuBar1.add(jMenuFile);

        jMenuEdit.setText("Edit");

        cancelMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        cancelMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/undo.png"))); // NOI18N
        cancelMenuItem.setText("Cancel");
        cancelMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelMenuItemActionPerformed(evt);
            }
        });
        jMenuEdit.add(cancelMenuItem);
        jMenuEdit.add(jSeparator4);

        cutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        cutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/cut.png"))); // NOI18N
        cutMenuItem.setText("Cut");
        cutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutMenuItemActionPerformed(evt);
            }
        });
        jMenuEdit.add(cutMenuItem);

        copyMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        copyMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/copy.png"))); // NOI18N
        copyMenuItem.setText("Copy");
        copyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyMenuItemActionPerformed(evt);
            }
        });
        jMenuEdit.add(copyMenuItem);

        pasteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        pasteMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/clipboard_paste_no_format.png"))); // NOI18N
        pasteMenuItem.setText("Paste");
        pasteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteMenuItemActionPerformed(evt);
            }
        });
        jMenuEdit.add(pasteMenuItem);
        jMenuEdit.add(jSeparator6);

        selectAlljMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        selectAlljMenuItem.setText("Select All");
        selectAlljMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAlljMenuItemActionPerformed(evt);
            }
        });
        jMenuEdit.add(selectAlljMenuItem);

        jMenuBar1.add(jMenuEdit);

        jMenuTools.setText("Tools");

        jMenuItemFind.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemFind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/binocular.png"))); // NOI18N
        jMenuItemFind.setText("Find");
        jMenuItemFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFindActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemFind);

        jMenuItemReplace.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemReplace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/find_replace.png"))); // NOI18N
        jMenuItemReplace.setText("Replace");
        jMenuItemReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemReplaceActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemReplace);

        jMenuBar1.add(jMenuTools);

        jMenuSpellCheck.setText("Spell Check");

        jRadioButtonMenuItemEnglish.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        buttonGroupLanguage.add(jRadioButtonMenuItemEnglish);
        jRadioButtonMenuItemEnglish.setSelected(true);
        jRadioButtonMenuItemEnglish.setText("English");
        jRadioButtonMenuItemEnglish.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/flags/gb.png"))); // NOI18N
        jRadioButtonMenuItemEnglish.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonMenuItemEnglishItemStateChanged(evt);
            }
        });
        jRadioButtonMenuItemEnglish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemEnglishActionPerformed(evt);
            }
        });
        jMenuSpellCheck.add(jRadioButtonMenuItemEnglish);

        jRadioButtonMenuItemFrench.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        buttonGroupLanguage.add(jRadioButtonMenuItemFrench);
        jRadioButtonMenuItemFrench.setText("French");
        jRadioButtonMenuItemFrench.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/flags/fr.png"))); // NOI18N
        jMenuSpellCheck.add(jRadioButtonMenuItemFrench);

        jMenuBar1.add(jMenuSpellCheck);

        jMenuHelp.setText("Help");

        jMenuItemHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItemHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/question.png"))); // NOI18N
        jMenuItemHelp.setText("jMenuItemHelp");
        jMenuItemHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemHelpActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemHelp);

        jMenuBar1.add(jMenuHelp);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jToolbarButtonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToolbarButtonSendActionPerformed

        if (connection == null) {
            JOptionPane.showMessageDialog(this, htmlEditor.getText());
            return;
        }
        
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        enableSendAction(false);

        try {
            sendMessage();
        } finally {
            enableSendAction(true);
            this.setCursor(Cursor.getDefaultCursor());
        }

    }//GEN-LAST:event_jToolbarButtonSendActionPerformed

    private void jButtonToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonToActionPerformed

        if (addressBook != null) {
            addressBook.dispose();
        }

        addressBook = new ContactSelector(this, connection, MessageComposer.BUTTON_TO);
        addressBook.setVisible(true);
    }//GEN-LAST:event_jButtonToActionPerformed

    private void jButtonCcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCcActionPerformed
        if (addressBook != null) {
            addressBook.dispose();
        }

        addressBook = new ContactSelector(this, connection, MessageComposer.BUTTON_CC);
        addressBook.setVisible(true);
    }//GEN-LAST:event_jButtonCcActionPerformed

    private void jToolbarButtonAddRecipientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToolbarButtonAddRecipientActionPerformed
        jButtonToActionPerformed(evt);
    }//GEN-LAST:event_jToolbarButtonAddRecipientActionPerformed

    private void jMenuItemPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPrintActionPerformed
        print();
    }//GEN-LAST:event_jMenuItemPrintActionPerformed

    private void jToolbarButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToolbarButtonPrintActionPerformed
        print();
    }//GEN-LAST:event_jToolbarButtonPrintActionPerformed

    private void jMenuItemCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCloseActionPerformed
        close();
    }//GEN-LAST:event_jMenuItemCloseActionPerformed

    private void jMenuItemSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSendActionPerformed
        sendMessage();
    }//GEN-LAST:event_jMenuItemSendActionPerformed

    private void jToolbarButtonAttachActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToolbarButtonAttachActionPerformed
        attach();
    }//GEN-LAST:event_jToolbarButtonAttachActionPerformed

    private void cancelMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelMenuItemActionPerformed
//        if (undo.canUndo()) {
//            undo.undo();
//        }
//        Action a = stylePad.getAction("Undo");
//        a.actionPerformed(evt);
    }//GEN-LAST:event_cancelMenuItemActionPerformed

    private void cutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutMenuItemActionPerformed
        //Action a = stylePad.getAction("cut-to-clipboard");
        //Action a = editor.getAction("cut-to-clipboard");
        //a.actionPerformed(evt);
    }//GEN-LAST:event_cutMenuItemActionPerformed

    public void attach() {
        //Attachment are not allowed in basic version
        //if (ConnectionParms.getSubscription() == StoreParms.PRODUCT_BASIC) {
        //    String htmlMessage = HtmlTextUtil.getHtmlHelpContent("no_attach_basic");
        //    new UnavailableFeatureDialog(null, htmlMessage, true).setVisible(true);
        //    return;
        //}

        JFileChooser saveTo = JFileChooserFactory.getInstance();

        saveTo.setMultiSelectionEnabled(true);
        saveTo.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int returnVal = saveTo.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] files = saveTo.getSelectedFiles();

            fileListManager.add(files);
        }
    }


    private void jMenuItemFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFindActionPerformed
        if (textReplaceFrame != null) {
            textReplaceFrame.dispose();
        }

        if (textSearchFrame != null) {
            textSearchFrame.dispose();
        }

        textSearchFrame = new TextSearchFrame(this, this.htmlEditor.getEditor());

    }//GEN-LAST:event_jMenuItemFindActionPerformed

    private void jMenuItemReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemReplaceActionPerformed
        if (textSearchFrame != null) {
            textSearchFrame.dispose();
        }

        if (textReplaceFrame != null) {
            textReplaceFrame.dispose();
        }

        textReplaceFrame = new TextReplaceFrame(this, this.htmlEditor.getEditor());
    }//GEN-LAST:event_jMenuItemReplaceActionPerformed

    private void copyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyMenuItemActionPerformed
//        Action a = stylePad.getAction("copy-to-clipboard");
//        a.actionPerformed(evt);
    }//GEN-LAST:event_copyMenuItemActionPerformed

    private void pasteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteMenuItemActionPerformed
//        Action a = stylePad.getAction("paste-from-clipboard");
//        a.actionPerformed(evt);
    }//GEN-LAST:event_pasteMenuItemActionPerformed

    private void selectAlljMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAlljMenuItemActionPerformed
//        stylePad.getEditor().selectAll();
    }//GEN-LAST:event_selectAlljMenuItemActionPerformed

    private void jTextAreaRecipientsToKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextAreaRecipientsToKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
            //Do not print the TAB in jTextArea!
            evt.consume();
            //Go to next component

            jTextAreaRecipientsCc.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextAreaRecipientsToKeyPressed

    private void jTextAreaRecipientsToKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextAreaRecipientsToKeyReleased
    }//GEN-LAST:event_jTextAreaRecipientsToKeyReleased

    private void jTextAreaRecipientsCcKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextAreaRecipientsCcKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
            //Do not print the TAB in jTextArea!
            evt.consume();

            if (evt.isShiftDown()) {
                jTextAreaRecipientsTo.requestFocusInWindow();
            } else {
                //Go to next component
                jTextFieldSubject.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jTextAreaRecipientsCcKeyPressed

    private void jToolbarButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToolbarButtonSaveActionPerformed
        saveAsDraft();
    }//GEN-LAST:event_jToolbarButtonSaveActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
        saveAsDraft();
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jRadioButtonMenuItemEnglishItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemEnglishItemStateChanged
        int language;
        //String dictionaryPath;
        if (jRadioButtonMenuItemEnglish.isSelected()) {
            language = LanguageType.ENGLISH;
            //dictionaryPath = SafeShareItMain.class.getResource(Parms.DICTIONARY_ENGLISH).getFile();
        } else {
            language = LanguageType.FRENCH;
            //dictionaryPath = SafeShareItMain.class.getResource(Parms.DICTIONARY_FRENCH).getFile();
        }

        setSpellCheck(language);

    }//GEN-LAST:event_jRadioButtonMenuItemEnglishItemStateChanged

    private void jRadioButtonMenuItemEnglishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemEnglishActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonMenuItemEnglishActionPerformed

    private void jMenuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHelpActionPerformed
        String content = HtmlTextUtil.getHtmlHelpContent("message_compose_help");
        new NewsFrame(this, content, messages.getMessage("help"));
    }//GEN-LAST:event_jMenuItemHelpActionPerformed

    private void jToolbarButtonHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToolbarButtonHelpActionPerformed
        String content = HtmlTextUtil.getHtmlHelpContent("message_compose_help");
        new NewsFrame(this, content, messages.getMessage("help"));
    }//GEN-LAST:event_jToolbarButtonHelpActionPerformed

    private void jTextFieldSubjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSubjectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSubjectActionPerformed

    private void jButtonBcc2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBcc2ActionPerformed
        if (addressBook != null) {
            addressBook.dispose();
        }

        addressBook = new ContactSelector(this, connection, MessageComposer.BUTTON_BCC);
        addressBook.setVisible(true);
    }//GEN-LAST:event_jButtonBcc2ActionPerformed

    private void jButtonBccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCc1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonCc1ActionPerformed

    private void jTextAreaRecipientsBccKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextAreaRecipientsCc1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextAreaRecipientsCc1KeyPressed

    private void jToggleButtonBccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToolbarButtonAttach1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jToolbarButtonAttach1ActionPerformed

    private void jToggleButtonBcc2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonBcc2ActionPerformed
        if (jToggleButtonBcc2.isSelected()) {
            jPanelRecipientsBcc.setVisible(true);
            jPanelSepRecipientsBcc.setVisible(true);
        } else {
            jPanelRecipientsBcc.setVisible(false);
            jPanelSepRecipientsBcc.setVisible(false);
        }
    }//GEN-LAST:event_jToggleButtonBcc2ActionPerformed

    private void jToggleButtonNoPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonNoPrintActionPerformed
        
        Icon icon = Parms.createImageIcon("images/files_2/32x32/printer.png");
        Icon forbiddenIcon = Parms.createImageIcon("images/files_2/32x32/printer_forbidden.png");
                                
        if (jToggleButtonNoPrint.isSelected()) {
            jToggleButtonNoPrint.setIcon(forbiddenIcon);
        }
        else {
            jToggleButtonNoPrint.setIcon(icon); 
        }
    }//GEN-LAST:event_jToggleButtonNoPrintActionPerformed

    private void jToggleButtonNoFowardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonNoFowardActionPerformed
        Icon icon = Parms.createImageIcon("images/files_2/32x32/mail_forward.png");
        Icon forbiddenIcon = Parms.createImageIcon("images/files_2/32x32/mail_forward_forbidden.png");
                                
        if (jToggleButtonNoFoward.isSelected()) {
            jToggleButtonNoFoward .setIcon(forbiddenIcon);
        }
        else {
            jToggleButtonNoFoward.setIcon(icon); 
        }
    }//GEN-LAST:event_jToggleButtonNoFowardActionPerformed

    private void jToggleButtonSendAnonymousNotificationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonSendAnonymousNotificationActionPerformed
        Icon icon = Parms.createImageIcon("images/files_2/32x32/id_card.png");
        Icon forbiddenIcon = Parms.createImageIcon("images/files_2/32x32/id_card_forbidden.png");
                                
        if (jToggleButtonSendAnonymousNotification.isSelected()) {
            jToggleButtonSendAnonymousNotification .setIcon(forbiddenIcon);
        }
        else {
            jToggleButtonSendAnonymousNotification.setIcon(icon); 
        }
    }//GEN-LAST:event_jToggleButtonSendAnonymousNotificationActionPerformed
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
                try {
                    MessageComposer messageComposer = new MessageComposer(null, null, 0, "null".toCharArray(), null);
                    messageComposer.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupLanguage;
    private javax.swing.JMenuItem cancelMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JButton jButtonBcc2;
    private javax.swing.JButton jButtonCc;
    private javax.swing.JButton jButtonTo;
    private javax.swing.JLabel jLabelAttached;
    private javax.swing.JLabel jLabelBccNotification;
    private javax.swing.JLabel jLabelRecipients;
    private javax.swing.JLabel jLabelSubject;
    private javax.swing.JList jListAttach;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemClose;
    private javax.swing.JMenuItem jMenuItemFind;
    private javax.swing.JMenuItem jMenuItemHelp;
    private javax.swing.JMenuItem jMenuItemPrint;
    private javax.swing.JMenuItem jMenuItemReplace;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSend;
    private javax.swing.JMenu jMenuSpellCheck;
    private javax.swing.JMenu jMenuTools;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10x10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel5_2;
    private javax.swing.JPanel jPanel5_3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelBccNotarization;
    private javax.swing.JPanel jPanelCenterMain;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelEmailAndFiles;
    private javax.swing.JPanel jPanelEmailBody;
    private javax.swing.JPanel jPanelFiles;
    private javax.swing.JPanel jPanelLabelAttached;
    private javax.swing.JPanel jPanelLabelSubject;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelRecipients;
    private javax.swing.JPanel jPanelRecipientsBcc;
    private javax.swing.JPanel jPanelRecipientsCc;
    private javax.swing.JPanel jPanelRecipientsTo;
    private javax.swing.JPanel jPanelScrollPane;
    private javax.swing.JPanel jPanelScrollPaneBcc;
    private javax.swing.JPanel jPanelScrollPaneCc;
    private javax.swing.JPanel jPanelScrollPaneTo;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSep3;
    private javax.swing.JPanel jPanelSep4;
    private javax.swing.JPanel jPanelSepButtons;
    private javax.swing.JPanel jPanelSepButtons1;
    private javax.swing.JPanel jPanelSepRecip;
    private javax.swing.JPanel jPanelSepRecipients;
    private javax.swing.JPanel jPanelSepRecipients1;
    private javax.swing.JPanel jPanelSepRecipientsBcc;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelSubject;
    private javax.swing.JPanel jPanelSubjectContainer;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemEnglish;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemFrench;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneCc;
    private javax.swing.JScrollPane jScrollPaneTo;
    private javax.swing.JScrollPane jScrollPanelBcc;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparatorSendAnonymous;
    private javax.swing.JToolBar.Separator jSeparatorSendAnonymous1;
    private javax.swing.JTextArea jTextAreaRecipientsBcc;
    private javax.swing.JTextArea jTextAreaRecipientsCc;
    private javax.swing.JTextArea jTextAreaRecipientsTo;
    private javax.swing.JTextField jTextFieldSubject;
    private javax.swing.JToggleButton jToggleButtonBcc2;
    private javax.swing.JToggleButton jToggleButtonNoFoward;
    private javax.swing.JToggleButton jToggleButtonNoPrint;
    private javax.swing.JToggleButton jToggleButtonSendAnonymousNotification;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jToolbarButtonAddRecipient;
    private javax.swing.JButton jToolbarButtonAttach;
    private javax.swing.JButton jToolbarButtonHelp;
    private javax.swing.JButton jToolbarButtonSave;
    private javax.swing.JButton jToolbarButtonSend;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JMenuItem selectAlljMenuItem;
    // End of variables declaration//GEN-END:variables

}
