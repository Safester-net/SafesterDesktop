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

import static net.safester.application.MessageReader.INCREASE_FACTOR;
import static net.safester.application.MessageReader.setPanelWithTextAreaHeightForMac;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.RowSorter.SortKey;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.awakefw.commons.api.client.HttpProxy;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.awakefw.sql.api.client.AwakeConnection;

import com.kawansoft.httpclient.KawanHttpClient;
import com.safelogic.utilx.StringMgr;
import com.swing.util.SwingUtil;
import com.swing.util.CustomJtree.CustomJTree;
import com.swing.util.CustomJtree.TreeNodeAdder;

import net.safester.application.addrbooknew.AddressBookImportStart;
import net.safester.application.compose.api.drafts.MessageDraftManager;
import net.safester.application.engines.BackgroundDownloaderEngine;
import net.safester.application.http.ApiMessages;
import net.safester.application.http.KawanHttpClientBuilder;
import net.safester.application.http.dto.SystemInfoDTO;
import net.safester.application.install.AskForDownloadJframe;
import net.safester.application.install.NewVersionInstaller;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.parms.StoreParms;
import net.safester.application.parms.SubscriptionLocalStore;
import net.safester.application.photo.PhotoAdder;
import net.safester.application.photo.PhotoAddressBookUpdaterNew;
import net.safester.application.photo.PhotoUtil;
import net.safester.application.socket.client.SocketClient;
import net.safester.application.tool.AttachmentListHandler;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.DesktopWrapper;
import net.safester.application.tool.FoldersHandler;
import net.safester.application.tool.JTextComponetPopupMenu;
import net.safester.application.tool.MessageTableCellRenderer;
import net.safester.application.tool.MessagesTableCreator;
import net.safester.application.tool.ReceivedAttachmentListRenderer;
import net.safester.application.tool.SortedDefaultListModel;
import net.safester.application.tool.UI_Util;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.AppDateFormat;
import net.safester.application.util.CacheFileHandler;
import net.safester.application.util.EmailUser;
import net.safester.application.util.HtmlTextUtil;
import net.safester.application.util.JDialogDiscardableMessage;
import net.safester.application.util.JEditorPaneLinkDetector;
import net.safester.application.util.JListUtil;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.UserPrefManager;
import net.safester.application.util.Util;
import net.safester.application.version.Version;
import net.safester.clientserver.LimitClause;
import net.safester.clientserver.MessageLocalStore;
import net.safester.clientserver.MessageLocalStoreCache;
import net.safester.clientserver.MessageStoreExtractor;
import net.safester.clientserver.PgpKeyPairLocal;
import net.safester.noobs.clientserver.AttachmentLocal;
import net.safester.noobs.clientserver.FolderLocal;
import net.safester.noobs.clientserver.GsonUtil;
import net.safester.noobs.clientserver.MessageLocal;
import net.safester.noobs.clientserver.RecipientLocal;
import net.safester.noobs.clientserver.SubjectDecryptionClient;

/**
 *
 * @author Alexandre Becquereau
 */
public class Main extends javax.swing.JFrame {

    public static boolean DEBUG = false;

    public static final int STANDARD_DIVIDER_SIZE = 5;

    public static final String CR_LF = System.getProperty("line.separator");

    public static final int DEFAULT_SPLIT_PANE_FOLDERS_LOC = 140;
    public static final int MIN_LOCATION_MESSAGE = 120;

    public static final Color COLOR_MSG_INFO = new Color(132, 192, 252);

    public static final boolean NOTIFY_ON = true;
    
    private Connection connection;
    private CustomJTree customJtree;
    private MessagesManager messages = new MessagesManager();
    private String keyId; // The User Login
    private int userNumber;
    private char[] passphrase;

    private FoldersHandler foldersHandler;
    private MessageLocalStore messageLocalStore = null;
    private JPopupMenu jTablePopupMenu;
    private JPopupMenu jListPopupMenu;
    private List<Integer> selectedMessages;
    private AttachmentListHandler attachmentJListHandler;
    private JFrame thisOne;
    JPanel jPanelEmpty;
    JMenuItem itemFoward;
    /**
     * Called windows instances
     */
    // private SystemPropDisplayer systemPropDisplayer = null;
    /**
     * The limit to display per page
     */
    private int limit = 10;
    /**
     * The current offset
     */
    private int offset = 0;
    /**
     * The total number of messages for a folder
     */
    int totalMessages = 0;
    ClipboardManager clipboardManager;

    private static CryptTray cryptTray = null;

    /**
     * The symmetric to use to encrypt address book
     */
    private static String addressBookEncryptionKey = null;
    private boolean initDone = false;

    // Interface classes instances
    private SystemPropDisplayer systemPropDisplayer;
    private About about;
    private AddressBookImportStart addressBookImportStart;
    private PhotoAddressBookUpdaterNew photoAddressBookUpdaterNew;
    private ConfirmAccountDeleteDialog confirmAccountDeleteDialog;
    private LastLogin lastLogin;
    private UserSettingsUpdater userSettingsUpdater;
    private SslCertificateDisplayer sslCertificateDisplayer;
    private Search search;
    private AutoResponder autoResponder;
    private ChangePassphrase changePassphrase;

    private final boolean DO_DELETE_MESSAGE_FILES = false;
    private NewsFrame newsFrame;

    /* The Set that contains all accounts when multi-accounts usage */
    private Set<UserAccount> userAccounts = new TreeSet<>();

    private int typeSubscription;

    /**
     * Creates new form SafeShareMain
     *
     * @param connection
     * @param keyId
     * @param userNumber
     * @param passphrase
     * @param userAccounts
     */
    public Main(Connection connection, String keyId, int userNumber, char[] passphrase, int typeSubscription, Set<UserAccount> userAccounts) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection can\'t be null");
        }
        if (userNumber == -1) {
            throw new IllegalArgumentException("Invalid user number: " + userNumber);
        }

        if (userAccounts == null) {
            throw new NullPointerException("userAccounts cannot be null!");
        }

        this.connection = connection;
        this.keyId = keyId;
        this.userNumber = userNumber;
        this.passphrase = passphrase;
        this.typeSubscription = typeSubscription;
        this.userAccounts = userAccounts;
        initComponents();
        initCompany();
        thisOne = this;
    }

    /* Init a secondary connection for list messages */
    /**
     * Out initialisation method
     */
    public void initCompany() {

        // Set subscription static valud because of mono account Legacy code...
        SubscriptionLocalStore.setSubscription((short) typeSubscription, userNumber);

        MessageLocalStoreCache.clear();

        System.out.println(new Date() + "Safester... initCompany begin...");

        // clipboardManager = new ClipboardManager(rootPane);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        this.setTitle(Version.NAME + " " + this.getKeyId());

        // Be sure messagePane is clear
        resetMessagePane();

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

//        if(!Parms.FEATURE_SEARCH_IS_ON){
//            this.jMenuMessage.remove(jMenuItemSearch);
//            this.jMenuMessage.remove(jSeparator15);
//            this.jToolBar1.remove(jButtonSearch);
//        }
        this.jLabelFrom.setText(messages.getMessage("from"));
        this.jLabelTo.setText(messages.getMessage("to"));
        this.jLabelCc.setText(messages.getMessage("cc"));
        this.jLabelDate.setText(messages.getMessage("sent"));
        this.jLabelNbElements.setText("");

        this.jTextAreaRecipientsTo.setBackground(Color.white);
        this.jTextAreaRecipientsCc.setBackground(Color.white);
        this.jTextFieldDate.setBackground(Color.white);
        this.jTextAreaRecipientsTo.setBorder(null);
        this.jTextAreaRecipientsCc.setBorder(null);
        this.jTextFieldUserFrom.setBorder(null);

        jLabelFrom.setForeground(Main.COLOR_MSG_INFO);
        jLabelDate.setForeground(Main.COLOR_MSG_INFO);
        jLabelTo.setForeground(Main.COLOR_MSG_INFO);
        jLabelCc.setForeground(Main.COLOR_MSG_INFO);

        jMenuItemQuit.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        if (SystemUtils.IS_OS_MAC_OSX) {
            jMenuItemQuit.setVisible(false); // Quit is already in default left menu
            jMenuItemClose.setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        } else {
            jMenuItemClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4,
                    java.awt.event.InputEvent.ALT_MASK));
        }

        this.jMenuFile.setText(messages.getMessage("file"));
        this.jMenuMessage.setText(messages.getMessage("message"));
        this.jMenuItemAddressBook.setText(messages.getMessage("address_book"));
        this.jMenuItemDelete.setText(messages.getMessage("delete"));
        this.jMenuItemFoward.setText(messages.getMessage("forward"));
        this.jMenuItemMarkRead.setText(messages.getMessage("mark_messages_as_read"));
        this.jMenuItemMarkUnread.setText(messages.getMessage("mark_messages_as_unread"));
        this.jMenuItemGetNewMessage.setText(messages.getMessage("get_new_message"));
        this.jMenuItemMove.setText(messages.getMessage("move"));
        this.jMenuItemNew.setText(messages.getMessage("new_message"));
        this.jMenuItemNewFolder.setText(messages.getMessage("new_folder"));
        this.jMenuItemPrint.setText(messages.getMessage("print"));
        this.jMenuItemReply.setText(messages.getMessage("reply"));
        this.jMenuItemReplyAll.setText(messages.getMessage("reply_all"));
        this.jMenuItemClose.setText(messages.getMessage("close"));
        this.jMenuItemQuit.setText(messages.getMessage("logout"));
        this.jMenuSettings.setText(messages.getMessage("settings"));
        this.jMenuItemChangePassphrase.setText(messages.getMessage("change_passphrase"));
        this.jMenuItemProxySettings.setText(messages.getMessage("proxy_settings"));
        this.jMenuItemAutoresponder.setText(messages.getMessage("vacation_responder"));
        this.jMenuItemUserSettings.setText(messages.getMessage("user_settings"));
        this.jMenuItemImportAddrBook.setText(messages.getMessage("importing_contacts"));
        this.jMenuItemSearch.setText(messages.getMessage("search_message"));
        this.jMenuItemDeleteAccount.setText(messages.getMessage("menu_delete_account"));

        this.jMenuItemPassphraseRecoverySettings.setText(messages.getMessage("passphrase_recovery_settings"));
        this.jMenuItemForgetPassphrase.setText(messages.getMessage("remove_passphrase_from_memory"));

        jMenuItemAddPhoto.setText(messages.getMessage("add_photo"));
        jMenuItemDeletePhoto.setText(messages.getMessage("delete_photo"));

        this.jMenuItemActivateSubscription.setText(messages.getMessage("activate_subscription"));

        if (SubscriptionLocalStore.getSubscription() == StoreParms.PRODUCT_FREE) {
            this.jMenuItemUpgrade.setText(messages.getMessage("buy_subscription"));
            jSeparatorButtonBuy.setVisible(true);
            jButtonBuy.setVisible(true);
        } else {
            this.jMenuItemUpgrade.setText(messages.getMessage("upgrade_subscription"));
            jSeparatorButtonBuy.setVisible(false);
            jButtonBuy.setVisible(false);
        }

        if (SubscriptionLocalStore.getSubscription() == StoreParms.PRODUCT_PLATINUM) {
            this.jMenuSettings.remove(this.jMenuItemUpgrade);
        }

        this.jMenuAbout.setText(messages.getMessage("about"));
        this.jMenuItemSystemInfo.setText(messages.getMessage("system_info"));
        this.jMenuItemWhatsNew.setText(messages.getMessage("whats_new"));
        this.jMenuItemAbout.setText(messages.getMessage("about"));
        this.jMenuItemCheckNewVersion.setText(messages.getMessage("check_for_updates_new_version"));

        this.jMenuItemClose.setToolTipText(messages.getMessage("close_window"));
        this.jMenuItemQuit.setToolTipText(messages.getMessage("logout_tooltip"));

        this.jMenuContacts.setText(messages.getMessage("contacts"));

        this.jMenuItemDouble2FaAccountQRcode.setText(messages.getMessage("double_2fa_account_qr_code"));
        this.jMenuItemDouble2FaActivation.setText(messages.getMessage("double_2fa_activation"));

        this.jMenuItemDouble2faHelp.setText(messages.getMessage("double_2fa_help"));

        jMenuWindow.setText(messages.getMessage("window"));
        jMenuOrientation.setText(messages.getMessage("reading_pane"));
        jRadioButtonMenuItemBottom.setText(messages.getMessage("bottom"));
        jRadioButtonMenuItemRight.setText(messages.getMessage("right"));
        jRadioButtonMenuItemPaneInactive.setText(messages.getMessage("inactive"));

        buttonGroupReadingPane.add(jRadioButtonMenuItemBottom);
        buttonGroupReadingPane.add(jRadioButtonMenuItemRight);
        buttonGroupReadingPane.add(jRadioButtonMenuItemPaneInactive);

        buttonGroupFolderSection.add(jRadioButtonMenuItemNormal);
        buttonGroupFolderSection.add(jRadioButtonMenuItemFolderInactive);

        jMenuFolderSection.setText(messages.getMessage("folder_section"));
        jRadioButtonMenuItemNormal.setText(messages.getMessage("normal"));
        jRadioButtonMenuItemFolderInactive.setText(messages.getMessage("inactive"));
        jMenuItemReset.setText(messages.getMessage("reset_windows"));

        this.jMenuAccounts.setText(messages.getMessage("accounts"));
        this.jMenuConnectToAccount.setText(messages.getMessage("connect_to_account"));
        buildAccountsMenu();

        this.jButtonAddressBook.setToolTipText(messages.getMessage("address_book"));
        this.jButtonDeleteSelectedMessage.setToolTipText(messages.getMessage("delete"));
        this.jButtonTransfert.setToolTipText(messages.getMessage("forward"));
        this.jButtonRefresh.setToolTipText(messages.getMessage("get_new_message"));
        this.jButtonMoveMessage.setToolTipText(messages.getMessage("move"));
        this.jButtonNewMessage.setToolTipText(messages.getMessage("new_message"));
        this.jButtonPrint.setToolTipText(messages.getMessage("print"));
        this.jButtonReply.setToolTipText(messages.getMessage("reply"));
        this.jButtonReplyAll.setToolTipText(messages.getMessage("reply_all"));
        this.jButtonNewFolder.setToolTipText(messages.getMessage("new_folder"));
        this.jButtonSearch.setToolTipText(messages.getMessage("search_message"));
        this.jButtonBuy.setToolTipText(messages.getMessage("buy_subscription"));

        this.jButtonAddressBook.setText(messages.getMessage("address_book"));
        this.jButtonDeleteSelectedMessage.setText(messages.getMessage("delete"));
        this.jButtonTransfert.setText(messages.getMessage("forward"));
        this.jButtonRefresh.setText(messages.getMessage("get_new_message"));
        this.jButtonMoveMessage.setText(messages.getMessage("move"));
        this.jButtonNewMessage.setText(messages.getMessage("new_message"));
        this.jButtonPrint.setText(messages.getMessage("print"));
        this.jButtonReply.setText(messages.getMessage("reply"));
        this.jButtonReplyAll.setText(messages.getMessage("reply_all"));
        this.jButtonNewFolder.setText(messages.getMessage("new_folder"));
        this.jButtonBuy.setText(messages.getMessage("buy_subscription"));

        // No Buttons on top of message for now
        jPanelTopButtons.setVisible(false);
        this.jButtonTransfert1.setText(messages.getMessage("forward"));
        this.jButtonReply1.setText(messages.getMessage("reply"));
        this.jButtonReplyAll1.setText(messages.getMessage("reply_all"));
        this.jButtonTransfert1.setToolTipText(messages.getMessage("forward"));
        this.jButtonReply1.setToolTipText(messages.getMessage("reply"));
        this.jButtonReplyAll1.setToolTipText(messages.getMessage("reply_all"));

        this.jButtonHostLock.setToolTipText(messages.getMessage("ssl_certificate_info"));

        // this.jButtonLogout.setText(messages.getMessage("logout"));
        // this.jButtonLogout.setToolTipText(messages.getMessage("logout_tooltip"));
        // this.jButtonLogout.setVisible(false);
        this.jButtonSearch.setText(messages.getMessage("search_message"));
        this.jButtonPrev.setForeground(Parms.COLOR_URL);
        this.jButtonNext.setForeground(Parms.COLOR_URL);

        this.jButtonPrev.setText("<" + messages.getMessage("prev"));
        this.jButtonNext.setText(messages.getMessage("next") + ">");

        this.jButtonPrev.setVisible(false);
        this.jButtonNext.setVisible(false);

        jButtonDetail.setText(messages.getMessage("details_button"));

        // For Mac OS, to avoid rounded border space
        jButtonPrev.putClientProperty("JButton.buttonType", "square");
        jButtonNext.putClientProperty("JButton.buttonType", "square");
        jButtonHostLock.putClientProperty("JButton.buttonType", "square");

        // For Mac OS X behavior (rounded default buttons)
        jButtonDetail.putClientProperty("JButton.buttonType", "square");

        this.jLabelPlan.setText(null);
        this.jLabelStorage.setText(null);
        this.jProgressBar1.setVisible(false);

        Font f = jTextFieldSubject.getFont();
        f = f.deriveFont(Font.BOLD);
        f = f.deriveFont(f.getSize() * 2);
        jTextFieldSubject.setFont(f);

        jTextAreaRecipientsTo.setLineWrap(true);
        jTextAreaRecipientsTo.setFont(jLabelTo.getFont());
        jTextAreaRecipientsCc.setLineWrap(true);
        jTextAreaRecipientsCc.setFont(jLabelTo.getFont());

        jProgressBar1.setString(messages.getMessage("downloading_and_decrypting"));

        // Load Folders
        foldersHandler = new FoldersHandler(this.getConnection(), userNumber);

        // Init tree containing folders
        try {
            customJtree = CustomJTree.initJTree(this, connection, foldersHandler, userNumber);
        } catch (Exception e) {
            JOptionPaneNewCustom.showException(rootPane, e);
            System.exit(-1);
        }

        this.customJtree.setBorder(new EmptyBorder(2, 5, 2, 2));
        this.customJtree.setBackground(Color.WHITE);

        // this.jPanelFolders.add(customJtree, BorderLayout.CENTER);
        // this.jPanelFoldersTree.add(customJtree);
        this.jScrollPane2.setViewportView(customJtree);
        jSplitPaneFolders.setOneTouchExpandable(false);
        jSplitPaneMessage.setOneTouchExpandable(false);

        updateStatusBar();

        // Build table
        Color tableBackground = null;
        tableBackground = jTable1.getBackground();

        // This because on nimbus the color is wrapped...
        Color colorToSet = new Color(tableBackground.getRed(), tableBackground.getGreen(), tableBackground.getBlue());
        jTable1.getParent().setBackground(colorToSet);
        jTable1.getParent().remove(jTable1);

        setHostLabelAndIcon();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createTable();
                // setAddressBookEncryptionKey();
            }
        });

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
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

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int width = dim.width * 70 / 100;
        int height = dim.height * 70 / 100;
        this.setSize(width, height);

        // To be done before a call to Radio button that re-set positions
        setSplitPanePositions();

        int readingPosition = UserPrefManager.getIntegerPreference(UserPrefManager.READING_PANE_POSITION);
        if (readingPosition == UserPrefManager.READING_PANE_BOTTOM) {
            jRadioButtonMenuItemBottom.setSelected(true);
        } else if (readingPosition == UserPrefManager.READING_PANE_RIGHT) {
            jRadioButtonMenuItemRight.setSelected(true);
        } else {
            jSplitPaneMessage.setDividerSize(0);
            jPanelMessageMain.setVisible(false);
            jRadioButtonMenuItemPaneInactive.setSelected(true);
        }

        boolean folderSectionInactive = UserPrefManager
                .getBooleanPreference(UserPrefManager.FOLDER_SECTION_IS_INACTIVE);
        if (folderSectionInactive) {
            jRadioButtonMenuItemFolderInactive.setSelected(true);
            jSplitPaneFolders.setDividerSize(0);
            jPanelFolders.setVisible(false);
        } else {
            jRadioButtonMenuItemNormal.setSelected(true);
        }

        WindowSettingManager.load(this);

        jSplitPaneMessage.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                // System.out.println("propertyChange: " + pce.toString());
                if (jSplitPaneMessage.getOrientation() == JSplitPane.VERTICAL_SPLIT) {

                    // System.out.println("jSplitPaneMessage.getUI().getDividerLocation(jSplitPaneMessage):
                    // " + jSplitPaneMessage.getUI().getDividerLocation(jSplitPaneMessage));
                    // Do not remember if we have expanded for ,no more message display
                    if (!jRadioButtonMenuItemPaneInactive.isSelected()) {
                        UserPrefManager.setPreference(UserPrefManager.SPLIT_PANE_MESSAGE_LOC_VERTICAL_SPLIT,
                                (Integer) pce.getNewValue());
                    }

                } else {

                    UserPrefManager.setPreference(UserPrefManager.SPLIT_PANE_MESSAGE_LOC_HORIZONTAL_SPLIT,
                            (Integer) pce.getNewValue());
                }
            }
        });

        jSplitPaneFolders.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {

                if (!jRadioButtonMenuItemPaneInactive.isSelected()) {
                    UserPrefManager.setPreference(UserPrefManager.SPLIT_PANE_FOLDERS_LOC, (Integer) pce.getNewValue());
                }
            }
        });

        // pack();
        this.setCursor(Cursor.getDefaultCursor());

        if (!Parms.FEATURE_CACHE_PASSPHRASE) {
            jMenuSettings.remove(jMenuItemForgetPassphrase);
        }

        System.out.println(new Date() + " SafeShareItMain... initCompany end...");

        SwingUtil.resizeJComponentsForNimbusAndMacOsX(rootPane);

        // if(SystemUtils.IS_OS_LINUX)
        if (UI_Util.isNimbus()) {
            this.jButtonReply.setText(" " + this.jButtonReply.getText() + " ");
            jProgressBar1.setString(" " + messages.getMessage("downloading_and_decrypting") + " ");
            this.jProgressBar1.setPreferredSize(new Dimension(this.jProgressBar1.getPreferredSize().width + 10, 22));
        }

        // 11/09/06 15:01 ABE : No more passphrase recovery
        jMenuSettings.remove(jMenuItemPassphraseRecoverySettings);

        thisOne = this;
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    NewVersionInstaller.checkIfNewVersion(thisOne, net.safester.application.version.Version.VERSION,
                            true);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(thisOne,
                            messages.getMessage("impossible_to_access_new_version_info") + CR_LF + ex.getMessage(),
                            Parms.PRODUCT_NAME, JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        t.start();

        if (CryptTray.isSupported()) {
            
            if (cryptTray != null) {
                cryptTray.remove();
            }
            
            cryptTray = new CryptTray();
            cryptTray.startAsTray(this);
        }

        notifyLoopInThread();

        Thread t2 = new Thread() {
            @Override
            public void run() {
                MessageComposerNotarization.setEmailBccNotarization(keyId, connection);
            }
        };
        t2.start();

        // Subject decryption migration
        SubjectDecryptionClient subjectDecryptionClient = new SubjectDecryptionClient(userNumber, passphrase,
                connection);
        subjectDecryptionClient.updateSubjectsInThread();

        initDone = true;
    }

    private void updateStatusBar() {
        try {
            MainStatusBarUpdater mainStatusBarUpdater = new MainStatusBarUpdater(connection, keyId, userNumber);
            this.jLabelPlan.setText(MainStatusBarUpdater.getAccount());
            
            String storageInfo = mainStatusBarUpdater.getStorageInfo();
            this.jLabelStorage.setText(storageInfo);
            if (storageInfo.isEmpty()) {
                jLabelSep.setText(null);
            }
            
            this.jLabelLastLogin.setText(mainStatusBarUpdater.getLastLoginAgo());
            
            if (mainStatusBarUpdater.getLastLoginAgo().isEmpty()) {
                this.jButtonDetail.setVisible(false);
                jPanelSepVerticalLastLogin.setVisible(false);
            }
            
        } catch (Exception ex) {
            JOptionPaneNewCustom.showException(rootPane, ex);
        }
    }

    private void buildAccountsMenu() {

        JMenu jMenuSwitchTo = new JMenu(messages.getMessage("switch_to_account"));
        jMenuAccounts.add(jMenuSwitchTo);
        this.jMenuItemConnectToAccount.setText(messages.getMessage("new"));

        if (userAccounts.size() < 2) {
            JMenuItem item = new JMenuItem(messages.getMessage("please_add_account_to_session"));
            jMenuSwitchTo.add(item);
        }

        String accountLists = UserPrefManager.getPreference(UserPrefManager.ACCOUNTS_LIST);
        if (accountLists != null) {
            createAddItems(accountLists);
        }
        if (accountLists == null || !accountLists.contains(",")) {
            this.jMenuItemConnectToAccount.setText(messages.getMessage("go"));
        }

        if (userAccounts.size() < 2) {
            return;
        }

        for (UserAccount userAccount : userAccounts) {
            if (userAccount.getKeyId().equals(keyId)) {
                continue;
            }

            JMenuItem itemKeyId = new JMenuItem(userAccount.getKeyId());
            jMenuSwitchTo.add(itemKeyId);

            itemKeyId.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {

                    String newKeyId = itemKeyId.getText();

                    UserAccountSwitcher userAccountSwitcher
                            = new UserAccountSwitcher(newKeyId, userAccounts);
                    userAccountSwitcher.switchTo();
                    thisOne.dispose();
                }
            });

        }
    }

    private void createAddItems(String accountLists) {

        String[] keyIds = accountLists.split(",");

        if (keyIds == null) {
            return;
        }

        for (String theKeyId : keyIds) {

            if (theKeyId.equalsIgnoreCase(this.keyId)) {
                continue;
            }

            if (UserAccountManager.containsAccountForKey(theKeyId, userAccounts)) {
                continue;
            }

            JMenuItem item = new JMenuItem(theKeyId);
            jMenuConnectToAccount.add(item);

            item.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Login login = new Login((Main) thisOne, theKeyId);
                    login.setVisible(true);
                }
            });
        }
    }

    public static String getAddressBookEncryptionKey() {
        return addressBookEncryptionKey;
    }

    /**
     * Get from server the address book symetric encryption key. Create it and
     * uplodd it if oes not exist.
     */
//    private void setAddressBookEncryptionKey() {
//        AddressBookKeyManager addressBookKeyManager = new AddressBookKeyManager(userNumber, passphrase, connection);
//        try {
//            addressBookEncryptionKey = addressBookKeyManager.getKey();
//        } catch (Exception ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public void radioButtonChange() {
        // Save current Loc if init has been done
        if (initDone) {

//            System.out.println("jRadioButtonMenuItemBottom.isSelected()  : " + jRadioButtonMenuItemBottom.isSelected());
//            System.out.println("jRadioButtonMenuItemRight.isSelected()   : " + jRadioButtonMenuItemRight.isSelected());
//            System.out.println("jRadioButtonMenuItemInactive.isSelected(): " + jRadioButtonMenuItemInactive.isSelected());
            if (jRadioButtonMenuItemPaneInactive.isSelected()) {
                UserPrefManager.setPreference(UserPrefManager.READING_PANE_POSITION,
                        UserPrefManager.READING_PANE_INACTIVE);
                jSplitPaneMessage.setDividerSize(0);
                jPanelMessageMain.setVisible(false);
                this.repaint();
            } else if (jRadioButtonMenuItemBottom.isSelected()) {
                UserPrefManager.setPreference(UserPrefManager.READING_PANE_POSITION,
                        UserPrefManager.READING_PANE_BOTTOM);
                jPanelMessageMain.setVisible(true);
                jSplitPaneMessage.setDividerSize(STANDARD_DIVIDER_SIZE);
                this.repaint();

            } else {
                UserPrefManager.setPreference(UserPrefManager.READING_PANE_POSITION,
                        UserPrefManager.READING_PANE_RIGHT);
                jPanelMessageMain.setVisible(true);
                jSplitPaneMessage.setDividerSize(STANDARD_DIVIDER_SIZE);
                this.repaint();
            }

            if (jRadioButtonMenuItemFolderInactive.isSelected()) {
                UserPrefManager.setPreference(UserPrefManager.FOLDER_SECTION_IS_INACTIVE, true);
                jPanelFolders.setVisible(false);
                jSplitPaneFolders.setDividerSize(0);
            } else {
                UserPrefManager.setPreference(UserPrefManager.FOLDER_SECTION_IS_INACTIVE, false);
                jPanelFolders.setVisible(true);
                jSplitPaneFolders.setDividerSize(STANDARD_DIVIDER_SIZE);
            }

        }

        setSplitPanePositions();
    }

    /**
     * Centralized method for split panes location
     */
    public void setSplitPanePositions() {
        int loc = UserPrefManager.getIntegerPreference(UserPrefManager.SPLIT_PANE_FOLDERS_LOC);

        debug("");
        if (loc == 0) {
            debug("loc is 0! Force to " + DEFAULT_SPLIT_PANE_FOLDERS_LOC);
            loc = DEFAULT_SPLIT_PANE_FOLDERS_LOC;
        }

        debug("loc: " + loc);

        jSplitPaneFolders.setDividerLocation(loc);

        int readingPosition = UserPrefManager.getIntegerPreference(UserPrefManager.READING_PANE_POSITION);
        if (readingPosition == UserPrefManager.READING_PANE_BOTTOM
                || readingPosition == UserPrefManager.READING_PANE_INACTIVE) {
            jSplitPaneMessage.setOrientation(JSplitPane.VERTICAL_SPLIT);
        } else {
            jSplitPaneMessage.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        }

        int orientation = jSplitPaneMessage.getOrientation();
        if (orientation == JSplitPane.VERTICAL_SPLIT) {
            loc = UserPrefManager.getIntegerPreference(UserPrefManager.SPLIT_PANE_MESSAGE_LOC_VERTICAL_SPLIT);
            loc = Math.max(loc, MIN_LOCATION_MESSAGE);
        } else {
            loc = UserPrefManager.getIntegerPreference(UserPrefManager.SPLIT_PANE_MESSAGE_LOC_HORIZONTAL_SPLIT);
            loc = Math.max(loc, MIN_LOCATION_MESSAGE);
        }

        // System.out.println("orientation (0=Vertical): " + orientation);
        // System.out.println("loc : " + loc);
        jSplitPaneMessage.setDividerLocation(loc);
        jSplitPaneMessage.setLastDividerLocation(loc);

        this.repaint();
    }

    private void saveSplitPanesLocation() {

        if (!jRadioButtonMenuItemFolderInactive.isSelected()) {
            UserPrefManager.setPreference(UserPrefManager.SPLIT_PANE_FOLDERS_LOC,
                    jSplitPaneFolders.getUI().getDividerLocation(jSplitPaneFolders));
        }

        UserPrefManager.setPreference(UserPrefManager.FOLDER_SECTION_IS_INACTIVE,
                jRadioButtonMenuItemFolderInactive.isSelected());

        if (jRadioButtonMenuItemBottom.isSelected()) {
            UserPrefManager.setPreference(UserPrefManager.READING_PANE_POSITION, UserPrefManager.READING_PANE_BOTTOM);
        } else if (jRadioButtonMenuItemRight.isSelected()) {
            UserPrefManager.setPreference(UserPrefManager.READING_PANE_POSITION, UserPrefManager.READING_PANE_RIGHT);
        } else {
            UserPrefManager.setPreference(UserPrefManager.READING_PANE_POSITION, UserPrefManager.READING_PANE_INACTIVE);
        }

        if (jSplitPaneMessage.getOrientation() == JSplitPane.VERTICAL_SPLIT) {

            if (!jRadioButtonMenuItemPaneInactive.isSelected()) {
                UserPrefManager.setPreference(UserPrefManager.SPLIT_PANE_MESSAGE_LOC_VERTICAL_SPLIT,
                        jSplitPaneMessage.getUI().getDividerLocation(jSplitPaneMessage));
            }
        } else {
            UserPrefManager.setPreference(UserPrefManager.SPLIT_PANE_MESSAGE_LOC_HORIZONTAL_SPLIT,
                    jSplitPaneMessage.getUI().getDividerLocation(jSplitPaneMessage));
        }

    }

    private void setAllPanelWithtextAreaHeightForMac() {
        int maxLineLength = MessageReader.MAX_LINE_LENGTH;

        if (jTextAreaRecipientsTo.getText().length() > maxLineLength) {
            MessageReader.setPanelWithTextAreaHeightForMac(jPanelTo, MessageReader.INCREASE_FACTOR);
            MessageReader.setPanelWithTextAreaHeightForMac(jPanelToLeft, MessageReader.INCREASE_FACTOR);
            MessageReader.setPanelWithTextAreaHeightForMac(jPanelToRight, MessageReader.INCREASE_FACTOR);
        } else {
            MessageReader.setPanelWithTextAreaHeightForMac(jPanelTo, 1);
            MessageReader.setPanelWithTextAreaHeightForMac(jPanelToLeft, 1);
            MessageReader.setPanelWithTextAreaHeightForMac(jPanelToRight, 1);
        }

        if (jTextAreaRecipientsCc.getText().length() > maxLineLength) {
            MessageReader.setPanelWithTextAreaHeightForMac(jPanelCc, MessageReader.INCREASE_FACTOR);
            MessageReader.setPanelWithTextAreaHeightForMac(jPanelCcLeft, MessageReader.INCREASE_FACTOR);
            MessageReader.setPanelWithTextAreaHeightForMac(jPanelCcRight, MessageReader.INCREASE_FACTOR);
        } else {
            MessageReader.setPanelWithTextAreaHeightForMac(jPanelCc, 1);
            MessageReader.setPanelWithTextAreaHeightForMac(jPanelCcLeft, 1);
            MessageReader.setPanelWithTextAreaHeightForMac(jPanelCcRight, 1);
        }

//        if (jListAttachments.getModel().getSize() > 1)
//        {
//           MessageReader.setPanelWithTextAreaHeightForMac(jPanelAttach, MessageReader.INCREASE_FACTOR);
//        }
//        else
//        {
//           MessageReader.setPanelWithTextAreaHeightForMac(jPanelAttach, 1);
//        }
        // resizePanelHeigthForMac(jPanelAttach, 56 + 10);
        // resizePanelHeigthForNimbus(jPanelAttach, 45 + 10);
        if (jListAttach.getModel().getSize() > 1) {
            setPanelWithTextAreaHeightForMac(jPanelAttach, INCREASE_FACTOR);
        }
    }

    /*
     * private void resizePanelHeigthForMac(JComponent component, int
     * preferedHeight) { if (SystemUtils.IS_OS_MAC_OSX) { int maxWidth = (int)
     * component.getMaximumSize().getWidth(); int minWidth = (int)
     * component.getMinimumSize().getWidth(); int prefWidth = (int)
     * component.getPreferredSize().getWidth();
     * 
     * component.setMaximumSize(new Dimension(maxWidth, preferedHeight));
     * component.setMinimumSize(new Dimension(minWidth, preferedHeight));
     * component.setPreferredSize(new Dimension(prefWidth, preferedHeight)); } }
     */
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

    private void setHostLabelAndIcon() {
        if (connection instanceof AwakeConnection) {
            AwakeConnection awakeConnection = (AwakeConnection) connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
            String url = awakeFileSession.getUrl();
            url = StringUtils.substringBeforeLast(url, "/");
            jLabelHost.setText(" " + url + " "); // one blank for clean display on green background

            Color greenSecurity = new Color(164, 220, 144);
            jLabelHost.setBackground(greenSecurity);

        }
    }

    /**
     * Close window
     */
    public void close() {
        WindowSettingManager.save(this);
        saveSplitPanesLocation();

        if (!CryptTray.isSupported()) {
            logout();
        }
        this.dispose();
    }

    public void logout() {

        WindowSettingManager.save(this);
        saveSplitPanesLocation();

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Will try to delete the files for 4 o5 seconds maximum
        LogoutManager.logoutAndExit();
        setCursor(Cursor.getDefaultCursor());
        JDialogDiscardableMessage jDialogDiscardableMessage = new JDialogDiscardableMessage(thisOne,
                messages.getMessage("logout_success"));
        // close(); NO! logout() can be called by close();

        System.exit(0);

    }

    /**
     * Formet the elements (number ofr messages) strin g
     *
     * @return the formated elements
     */
    public String getNbElementsMessage() {
        String nbElementsTemplate = "{0} - {1} of {2}";
        MessageFormat messageFormat = new MessageFormat(nbElementsTemplate);
        Integer[] params = new Integer[3];
        params[0] = offset + 1;
        params[1] = Math.min(offset + limit, totalMessages);
        params[2] = totalMessages;

        if (totalMessages == 0) {
            return "";
        } else {
            String nbElements = messageFormat.format(params);
            return nbElements;
        }
    }

    /**
     * Replace the editor pane with a JEditorPaneLinkDetector
     */
    private void addHyperLinkDetector() {

        // Hyperlink listener that will open a new Browser with the given URL
        jEditorPaneBody.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent r) {
                if (r.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    DesktopWrapper.browse(r.getURL());
                }
            }
        });
        // END USE JEditorPaneLinkDetector
    }

    private void setTextFieldsPopup() {
        if (jTextFieldUserFrom.getText() != null) {
            jTextFieldUserFrom.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    jTextComponentPopupMenuAdder(jTextFieldUserFrom, e, JTextComponetPopupMenu.SINGLE);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    jTextComponentPopupMenuAdder(jTextFieldUserFrom, e, JTextComponetPopupMenu.SINGLE);
                }
            });
        }
        if (jTextAreaRecipientsTo.getText() != null) {
            jTextAreaRecipientsTo.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    jTextComponentPopupMenuAdder(jTextAreaRecipientsTo, e, JTextComponetPopupMenu.LIST);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    jTextComponentPopupMenuAdder(jTextAreaRecipientsTo, e, JTextComponetPopupMenu.LIST);
                }
            });
        }

        if (jTextAreaRecipientsCc.getText() != null) {
            jTextAreaRecipientsCc.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    jTextComponentPopupMenuAdder(jTextAreaRecipientsCc, e, JTextComponetPopupMenu.LIST);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    jTextComponentPopupMenuAdder(jTextAreaRecipientsCc, e, JTextComponetPopupMenu.LIST);
                }
            });
        }
    }

    private void jTextComponentPopupMenuAdder(JTextComponent jTextComponent, MouseEvent e, int type) {
        JTextComponetPopupMenu jTextFieldPopupMenu = new JTextComponetPopupMenu(this.getConnection(), this,
                jTextComponent, userNumber, type);
        jTextFieldPopupMenu.showPopupMenu(e);
    }

    /**
     * Build popup menu linked to the jTable
     */
    private void buildJTablePopupMenu() {

        jTablePopupMenu = new JPopupMenu();
        JMenuItem itemOpen = new JMenuItem(messages.getMessage("open"));
        JMenuItem itemMove = new JMenuItem(messages.getMessage("move"));
        JMenuItem itemDelete = new JMenuItem(messages.getMessage("delete"));
        JMenuItem itemAddSender = new JMenuItem(messages.getMessage("add_sender_to_address_book"));
        itemOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                itemOpen_actionPerformed(e);
            }
        });

        itemMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                itemMove_actionPerformed(e);
            }
        });

        itemMove.setIcon(jMenuItemMove.getIcon());
        itemDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                itemDelete_actionPerformed(e);
            }
        });
        itemDelete.setIcon(jMenuItemDelete.getIcon());

        itemAddSender.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToContact();
            }
        });

        jTablePopupMenu.add(itemOpen);
        jTablePopupMenu.add(itemMove);
        jTablePopupMenu.addSeparator();

        JMenuItem itemReply = new JMenuItem(jMenuItemReply.getText());
        itemReply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemReplyActionPerformed(e);
            }
        });
        itemReply.setIcon(jMenuItemReply.getIcon());
        jTablePopupMenu.add(itemReply);

        JMenuItem itemReplyAll = new JMenuItem(jMenuItemReplyAll.getText());
        itemReplyAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemReplyAllActionPerformed(e);
            }
        });
        itemReplyAll.setIcon(jMenuItemReplyAll.getIcon());
        jTablePopupMenu.add(itemReplyAll);

        itemFoward = new JMenuItem(jMenuItemFoward.getText());
        itemFoward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemFowardActionPerformed(e);
            }
        });
        itemFoward.setIcon(jMenuItemFoward.getIcon());
        jTablePopupMenu.add(itemFoward);
        
        JMenuItem itemMarkRead = new JMenuItem(jMenuItemMarkRead.getText());
        itemMarkRead.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemMarkReadActionPerformed(e);
            }
        });
        itemMarkRead.setIcon(jMenuItemMarkRead.getIcon());
        jTablePopupMenu.add(itemMarkRead);
        
        JMenuItem itemMarkUnread = new JMenuItem(jMenuItemMarkUnread.getText());
        itemMarkUnread.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemMarkUnreadActionPerformed(e);
            }
        });
        itemMarkUnread.setIcon(jMenuItemMarkUnread.getIcon());
        jTablePopupMenu.add(itemMarkUnread);
        
//        jTablePopupMenu.add(new JMenuItem(jMenuItemFoward));
        jTablePopupMenu.addSeparator();
        // jTablePopupMenu.add(itemAddSender);
        // jTablePopupMenu.addSeparator();
        jTablePopupMenu.add(itemDelete);
    }

    private void addToContact() {
        // jTableMessages.setS
        int selected_index = jTable1.getSelectedRow();
        String str = (String) jTable1.getValueAt(selected_index, 4);
        debug("str: " + str);
    }

    /*
     * Pop up menu actions
     */
    private void itemOpen_actionPerformed(ActionEvent e) {
        openSelectedMessage();
    }

    private void itemMove_actionPerformed(ActionEvent e) {
        jButtonMoveMessageActionPerformed(e);
    }

    private void itemDelete_actionPerformed(ActionEvent e) {
        deleteSelectedMessage();
    }

    public void getIncomingMessage() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        // Select inbox
        DefaultMutableTreeNode inBoxFolder = customJtree.searchFolder(Parms.INBOX_ID);
        customJtree.getTree().setSelectionPath(new TreePath(inBoxFolder.getPath()));

        MessageLocalStoreCache.remove(Parms.INBOX_ID);
        MessageLocalStoreCache.remove(Parms.OUTBOX_ID);
        createTable();

        this.setCursor(Cursor.getDefaultCursor());
    }

    /**
     * Get the current selected folder id
     *
     * @return
     */
    private int getSelectedFolderId() {
        int idFolder = -1;

        if (customJtree.getTree().getSelectionPath() != null) {
            DefaultMutableTreeNode selectedFolder = (DefaultMutableTreeNode) customJtree.getTree().getSelectionPath()
                    .getLastPathComponent();
            if (selectedFolder.getUserObject() instanceof FolderLocal) {
                FolderLocal folder = (FolderLocal) selectedFolder.getUserObject();
                idFolder = folder.getFolderId();
            }
        }
        return idFolder;
    }

    /**
     * Init the list of selected messages
     */
    private void setSelectedMessages() {
        // Reset list
        selectedMessages = new ArrayList<Integer>();
        // Get selected rows index
        int[] selRows = jTable1.getSelectedRows();
        
        debug("setSelectedMessages() selRows: " + selRows.length);
        
        if (selRows.length > 0) {
            for (Integer rowIndex : selRows) {
                // For each row get message
                // MessageLocal messageLocal = getMessageForRowIndex(rowIndex.intValue());
                
                if (jTable1.getValueAt(rowIndex, 0) instanceof Integer) {
                    int messageId = (Integer) jTable1.getValueAt(rowIndex, 0);
                    debug("setSelectedMessages() messageId: " + messageId);
                    if (messageId > 0) {
                        selectedMessages.add(messageId);
                    }
                }
            }
        }
    }

    /**
     * Open selected messages in new windows
     */
    public void displaySelectedMessageNumber() {
        // Init selected message list

        setSelectedMessages();

        int folderId = getSelectedFolderId();

        if (folderId != -1) {
            for (Integer messageId : selectedMessages) {

                updateMessageIsReadInThread(folderId, messageId, false);
                MessageLocal message = getCompletedMessage(messageId);

                JOptionPane.showMessageDialog(this, "Message ID: " + message.getMessageId());
                break;
            }
        }
    }

    /**
     * Open selected messages in new windows
     */
    public void openSelectedMessage() {
        // Init selected message list

        setSelectedMessages();

        int folderId = getSelectedFolderId();
        debug("folderId: " + folderId);
                
        if (folderId != -1) {
            for (Integer messageId : selectedMessages) {

                debug("openSelectedMessage messageId: " + messageId);
                
                updateMessageIsReadInThread(folderId, messageId, false);

                MessageLocal message = null;
                
                if (folderId == Parms.DRAFT_ID) {
                    message = messageLocalStore.get(messageId);
                }
                else {
                    message = getCompletedMessage(messageId);
                }

                debug("openSelectedMessage() message: " + message);
                
                // Open a new window for each message
                if (folderId != Parms.DRAFT_ID) {
                    new MessageReader(this, this.getConnection(), message, this.getKeyId(), this.userNumber,
                            this.passphrase, folderId).setVisible(true);
                } else {
                    new MessageComposer(thisOne, getKeyId(), this.userNumber, passphrase, connection, message,
                            Parms.ACTION_EDIT).setVisible(true);
                }
            }
        }
    }

    
    private void markRead(boolean messageUnread) {

        int folderId = getSelectedFolderId();
        setSelectedMessages();
        
        List<Integer> selectedMessageIds = new ArrayList<>();
        for (int messageId : selectedMessages) {
            selectedMessageIds.add(messageId);
        }
        
        jTable1.getSelectionModel().clearSelection();
        
        for (int messageId : selectedMessageIds) {
            updateMessageIsReadInThread(folderId, messageId, messageUnread);
            MessageLocal message = getCompletedMessage(messageId);
        }
        
        // Refresh the table
        createTable();

    }


    /**
     * Delete all selected message
     */
    public void deleteSelectedMessage() {
        // Init selected message list
        int folderId = getSelectedFolderId();

        String text;
        String title = messages.getMessage("warning");

        text = messages.getMessage("confirm_delete");
        int result = JOptionPane.showConfirmDialog(this, text, title, JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.NO_OPTION) {
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        setSelectedMessages();

        // for (Integer messageId : selectedMessages) {
        // deleteMessage(messageId, idFolder);
        // }
        //
        // Refresh the table
        // createTable();
        deleteMessages(selectedMessages, folderId);
        
        this.setCursor(Cursor.getDefaultCursor());
    }

    /**
     * Delete a list of messages. All is done with *ONE* Awake call() to be
     * hyper-fast
     *
     * @param messagesId The list of messages to delete
     * @param folderId the folder id of the messages to delete
     */
    public void deleteMessages(List<Integer> messagesId, int folderId) {
        try {

            if (folderId == Parms.DRAFT_ID) {
                MessageDraftManager messageDraftSaver = new MessageDraftManager(userNumber);
                messageDraftSaver.delete(messagesId);
                createTable();
                return;
            }
            
            String messagesList = messagesId.toString();

            // Do the delete on the server, because of security concerns
            AwakeConnection awakeConnection = (AwakeConnection) connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

            if (DO_DELETE_MESSAGE_FILES) {
                Set<Integer> messageIdset = new HashSet<>(messagesId);
                String attachmentMapJson = GsonUtil.MessageIdSetToJson(messageIdset);
                // 1) Extract my messages, the ones where I am the sender
                String myMessageIdsJson = awakeFileSession.call(
                        "net.safester.server.delete.FileDeletor.getMyMessageIds", userNumber, attachmentMapJson,
                        connection);

                Set<Integer> myMessageIdSet = GsonUtil.jsonToMessageIdSet(myMessageIdsJson);

                if (myMessageIdSet != null && !myMessageIdSet.isEmpty()) {

                    // 2) Get the file names to delete
                    attachmentMapJson = awakeFileSession.call("net.safester.server.AttachmentSelect.selectAttachments",
                            myMessageIdsJson, connection);
                    Map<Integer, List<AttachmentLocal>> attachmentLocalMap = GsonUtil
                            .attachmentLocalFromGson(attachmentMapJson);

                    // 3) FIRST Delete the attachment files. First get the names from main server.
                    // Delete but be done prior to SQL because of SQL commit
                    List<String> attachFilenames = getAttachFilenames(attachmentLocalMap);
                    String attachFilesnamesJson = GsonUtil.attachFilenamesToGson(attachFilenames);

                    awakeFileSession.call("net.safester.server.delete.FileDeletor.deleteFilesOnHttpFileServer",
                            userNumber, myMessageIdsJson, attachFilesnamesJson);
                }
            }

            // 4) Delete the SQL values. TO BE DONE IN LAST.
            awakeFileSession.call("net.safester.server.MessageDeletor.deleteMessages", userNumber, keyId, folderId,
                    messagesList, connection);

            MessageLocalStoreCache.remove(folderId);

        } catch (Exception e) {
            this.setCursor(Cursor.getDefaultCursor());
            e.printStackTrace();
            JOptionPaneNewCustom.showException(this, e);
        }

        // Refresh the table
        createTable();
    }

    private static List<String> getAttachFilenames(Map<Integer, List<AttachmentLocal>> attachmentLocalMap) {

        List<String> fileNames = new ArrayList<>();

        for (Integer key : attachmentLocalMap.keySet()) {
            List<AttachmentLocal> attachmentsLocals = attachmentLocalMap.get(key);
            for (AttachmentLocal attachmentsLocal : attachmentsLocals) {
                fileNames.add(attachmentsLocal.getFileName());
            }
        }

        return fileNames;
    }

    /**
     * New deleteMessage that calls deleteMessages for one value List<Integer>
     *
     * @param messageId the Id of the message to delete
     * @param folderId the folder id of the message to delete
     */
    public void deleteMessage(int messageId, int folderId) {
        List<Integer> messageIdList = new Vector<Integer>();
        messageIdList.add(messageId);
        deleteMessages(messageIdList, folderId);
    }

    /**
     * Creates the table containing the messages. To be used when first display
     * of a folder, or when new messages are arriving
     */
    public void createTable() {
        this.jButtonPrev.setVisible(false);
        this.jButtonNext.setVisible(false);
        this.jLabelNbElements.setText(null);

        createTable(true);
    }

    /**
     * If true, a thread is running, so do nothing
     */
    private static boolean createTableThreadRunning = false;

//    public static boolean isCreateTableThreadRunning() {
//        return createTableThreadRunning;
//    }
    /**
     * Says securely if the list messages (to create tje JTable) is already busy
     *
     * @return true if the thread is busy
     */
    public static synchronized boolean lockCreateTableThreadRunning() {
        if (createTableThreadRunning) {
            debug("Thread is running!");
            return true;
        } else {
            createTableThreadRunning = true;
            return false;
        }
    }

//    public static void releaseCreateTableThreadRunning() {
//        createTableThreadRunning = false;
//    }
    /**
     * Define if access must be restricted while database is accessed through
     * building list of messages
     *
     * @param access if true access is NOT restricted.
     */
    private void setEnabledRestrictedAcces(boolean access) {
        customJtree.setTreeEnabled(access);
        jButtonRefresh.setEnabled(access);
    }

    /**
     * Start as a thread
     *
     * @param reset
     */
    private void createTable(boolean reset) {
        if (lockCreateTableThreadRunning()) {
            return;
        }

        this.setEnabledRestrictedAcces(false);

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        jProgressBar1.setVisible(true);

        final boolean finalReset = reset;

        Thread t = new Thread() {
            @Override
            public void run() {
                createTableAsThread(finalReset);
            }
        };
        t.start();
    }

    /**
     * Creates the table containing the messages
     *
     * @param reset if true, all datas are asked back. Should be use for init,
     * all other cases are for <prev or next> button
     */
    private void createTableAsThread(boolean reset) {

        try {

            if (reset) {
                limit = UserPrefManager.getIntegerPreference(UserPrefManager.NB_MESSAGES_PER_PAGE);

                if (limit == 0) {
                    limit = Parms.DEFAULT_NB_MESSAGES_PER_PAGE;
                }

                offset = 0;
            }

            LimitClause limitClause = new LimitClause(limit, offset);

            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int idFolder = -1;
            idFolder = getSelectedFolderId();

            // this.setConnection(Parms.getConnection());
            messageLocalStore = new MessageLocalStore();

            if (idFolder != -1) {
                // Get messages for the selected folder
                debug("");
                debug(new Date() + " Safester... messageStoreExtractor.getStore() begin...");

                MessageStoreExtractor messageStoreExtractor = new MessageStoreExtractor(connection, userNumber,
                        passphrase, idFolder, limitClause);
                messageLocalStore = messageStoreExtractor.getStore();

                debug(new Date() + " Safester... messageStoreExtractor.getStore() end...");

                // NOTIFY
                if (idFolder == Parms.INBOX_ID) {
                    MainNotifier mainNotifier = new MainNotifier(this, cryptTray, userNumber, connection);
                    mainNotifier.notifyNewInbox();
                }
                
                debug(new Date() + " Safester... mainNotifier.notifyNewInbox() end...");
                
                if (idFolder != Parms.DRAFT_ID) {
                    // Start the thread that will fetch the messages Body content in memory
                    // and user settings
                    BackgroundDownloaderEngine backgroundDownloaderEngine = new BackgroundDownloaderEngine(userNumber,
                            passphrase, messageLocalStore, connection);
                    BackgroundDownloaderEngine.setIsRequestInterrupt(true);
                    backgroundDownloaderEngine.start();
                }
                
                debug(new Date() + " Safester... backgroundDownloaderEngine.start() end...");
            }

            final boolean finalReset = reset;

            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {

                    if (finalReset) {
                        // If we are in reset mode, ask for the total number of messages
                        // for the user id and the folder id
                        LimitClause limitClause = new LimitClause(limit, offset);
                        int idFolder = getSelectedFolderId();
                        MessageStoreExtractor messageStoreExtractor = new MessageStoreExtractor(connection, userNumber,
                                passphrase, idFolder, limitClause);
                        try {
                            totalMessages = messageStoreExtractor.getTotalMessages();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }

                    String nbElementsMessage = getNbElementsMessage();
                    jLabelNbElements.setText(nbElementsMessage);
                    setButtonsPrevNextEnableMode();
                }
            });

            boolean isOutBox = false;
            if (idFolder == Parms.OUTBOX_ID) {
                isOutBox = true;
            }

            // Build the table
            MessagesTableCreator messagesTableCreator = new MessagesTableCreator(this, messageLocalStore, isOutBox,
                    idFolder);
            int sortedColumnIndex = -1;
            SortOrder sortOrder = null;
            
            //Backup of sorted column and sort order
            final RowSorter<?> rowSorter = this.jTable1.getRowSorter();
            if(rowSorter != null) {
            	sortedColumnIndex = rowSorter.getSortKeys().get(0).getColumn();
            	sortOrder = rowSorter.getSortKeys().get(0).getSortOrder();
            }
            this.jTable1 = messagesTableCreator.create();
            buildJTablePopupMenu();

            DefaultMutableTreeNode folderNode = customJtree.searchFolder(idFolder);
            if (folderNode != null) {
                customJtree.getTree().setSelectionPath(new TreePath(folderNode.getPath()));
            }
            //Restore previous sort of messages
            if(sortedColumnIndex != -1) {
            	RowSorter<?> sorter =  jTable1.getRowSorter();
            	List<RowSorter.SortKey> sortKeys = new ArrayList<>();
            	sortKeys.add(new SortKey(sortedColumnIndex, sortOrder));
            	sorter.setSortKeys(sortKeys);
            }
            // Reset message pane
            resetMessagePane();
            jScrollPane1.setViewportView(jTable1);
            
            // HACK TRY TO SELCT FIRST MESSAGE
            debug("jTable1.getRowCount(): " + jTable1.getRowCount());
            if (jTable1.getRowCount() > 0 && ! SystemUtils.IS_OS_LINUX) {
                try {
                    // Build message pane with first message of list
                    if (jTable1.getValueAt(0, 0) instanceof Integer) {
                        int messageId = (Integer) jTable1.getValueAt(0, 0);
                        buildMessagePane(messageId);
                    }
                } catch (Exception e) {
                    System.err.println(e);
                }
            }

            debug(new Date() + " Safester... jScrollPane1.setViewportView(jTable) end...");

        } catch (Exception e) {
            e.printStackTrace();
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPaneNewCustom.showException(rootPane, e);
        } finally {
            this.setCursor(Cursor.getDefaultCursor());
            jProgressBar1.setVisible(false);
            createTableThreadRunning = false;
            this.setEnabledRestrictedAcces(true);
            updateStatusBar();
            
        }

    }

    /**
     * Loop that tests if it necessary to notify a new message
     */
    private void notifyLoopInThread() {
        Thread t = new Thread() {
            @Override
            public void run() {

        	if (NOTIFY_ON ) {
                    while (true) {
                        try {
                            sleep(Parms.NOTIFY_PERIOD);
                            MainNotifierServerInfo mainNotifierServerInfo = new MainNotifierServerInfo(userNumber,
                                    connection);
                            boolean doExist = mainNotifierServerInfo.newInboxMessageExists();
                            //System.out.println(new Date() + " Testing if new message exists on server: " + doExist);
                            
                            if (doExist) {
                                // Select inbox
                                DefaultMutableTreeNode inBoxFolder = customJtree.searchFolder(Parms.INBOX_ID);
                                customJtree.getTree().setSelectionPath(new TreePath(inBoxFolder.getPath()));

                                MessageLocalStoreCache.remove(Parms.INBOX_ID); // Cear cache
                                createTable();
                                // Resleep to avoid overlaps in threads
                                sleep(Parms.NOTIFY_PERIOD);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } 
        	}
        	

            }

        };
        t.start();
    }

    public void resetJTable() {
        jScrollPane1.remove(jTable1);
        jScrollPane1.setViewportView(null);
    }

    /**
     * Enable or not navigation buttons depending on number of messages
     */
    private void setButtonsPrevNextEnableMode() {
        // Previous
        if (offset != 0) {
            jButtonPrev.setVisible(true);
        } else {
            jButtonPrev.setVisible(false);
        }

        // Next
        if (offset + limit < totalMessages) {
            jButtonNext.setVisible(true);
        } else {
            jButtonNext.setVisible(false);
        }

    }

    /**
     * Display popup menu of the JTable
     *
     * @param e
     */
    public void showJTablePopupMenu(MouseEvent e) {
        setSelectedMessages();
        if (!selectedMessages.isEmpty()) {

            jTablePopupMenu.show(e.getComponent(), e.getX(), e.getY());

        }
    }

    /**
     * Display popup menu of the JList
     *
     * @param e
     */
    public void showJListPopupMenu(MouseEvent e) {
        if (jListAttach.getSelectedIndices().length > 0) {
            jListPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * Display the first selected message in preview message pane
     */
    public void displaySelectedMessage() {

        // init selected message list
        setSelectedMessages();

        if (selectedMessages.isEmpty()) {
            // Selection is empty => nothing to do
            return;
        }

        int folderId = getSelectedFolderId();
        int messageId = selectedMessages.get(0);

        updateMessageIsReadInThread(folderId, messageId, false);

        // Build message pane with first message of list
        buildMessagePane(messageId);
    }

    /**
     * Update the message status to read and do it in background
     *
     * @param folderId the folder id
     * @param messageId the Message Id
     * @param messageUnread ir true, message will be marked as unread
     */
    public void updateMessageIsReadInThread(int folderId, int messageId, boolean messageUnread) {

        // Nothing if folder is unknown
        if (folderId == -1 || folderId == Parms.DRAFT_ID) {
            return;
        }

        MessageLocal messageLocal = messageLocalStore.get(messageId);
        if (messageLocal == null) {
            return;
        }

//        // If message is already read and it sead in MessageTableCellRenderer, do
//        // nothing
//        if (!messageUnread && MessageTableCellRenderer.readMessages.contains(messageId)) {
//            return;
//        }

        // Ok, update the diplay status *and* the valus isRead on host
        if (messageUnread) {
             MessageTableCellRenderer.readMessages.remove(messageId);
             messageLocal.setIsRead(false);
        }
        else {
            MessageTableCellRenderer.readMessages.add(messageId);
            messageLocal.setIsRead(true);
        }

        final String messageSenderEmailAddress = messageLocal.getSenderUserEmail();
        final int messageIdFinal = messageId;
        final boolean messageUnreadFinal = messageUnread;
                
        Thread t = new Thread() {
            @Override
            public void run() {
                try {

                    updateMessageIsRead(connection, messageSenderEmailAddress, messageIdFinal, messageUnreadFinal);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPaneNewCustom.showException(null, e);
                }
            }
        };
        t.start();
    }

    /**
     * synchronized update of message Status.
     *
     * @param connection
     * @param userNumber
     * @param folderId
     * @param messageId
     * @throws Exception
     */
    private static synchronized void updateMessageIsRead(Connection connection, String messageSenderEmailAddress, int messageId, boolean messageUnread) throws Exception {
        // Update the server saying the message has been read
        //MessageTransfer messageTransfer = new MessageTransfer(connection, userNumber, messageId, folderId);
        //messageTransfer.setMessageIsRead(true);
        
        AwakeConnection awakeConnection = (AwakeConnection)connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
        
        KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.buildFromAwakeConnection(awakeConnection);
        ApiMessages apiMessages = new ApiMessages(kawanHttpClient, awakeFileSession.getUsername(),
                awakeFileSession.getAuthenticationToken());
        apiMessages.setMessageRead(messageId, messageSenderEmailAddress, messageUnread);
        
        debug("");
        debug("Message " + messageId + " marked as unread: " + messageUnread + " (messageSenderEmailAddress: " + messageSenderEmailAddress + ")");
        
    }

    /**
     * Reset message preview panel
     */
    private void resetMessagePane() {

        jPanelMessage.setVisible(false);
        this.jTextFieldSubject.setText(null);
        this.jTextAreaRecipientsTo.setText(null);
        this.jTextAreaRecipientsCc.setText(null);
        this.jTextFieldDate.setText(null);

    }

    /**
     * Display message in preview pane (in thread for progress bar)
     *
     * @param message
     */
    private void buildMessagePane(int messageId) {

        // Remove thread with Swing actions! Must be thread-safe using EDT!
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Reset pane
        if (messageId > 0) {
            displayMessage(messageId);
            jPanelMessage.setVisible(true);
        } else {
            jButtonPrint.setEnabled(true);
            jButtonTransfert.setEnabled(true);
            jMenuItemPrint.setEnabled(true);
            jMenuItemFoward.setEnabled(true);
        }
    }

    /**
     * Set the messages elements in preview pane
     *
     * @param message
     */
    private void displayMessage(int messageId) {
        int location = jSplitPaneMessage.getDividerLocation();

        // Subject is printed in bold and bigger font
        Font f = jTextFieldSubject.getFont();
        f = f.deriveFont(Font.BOLD);
        float newSize = 22;
        f = f.deriveFont(newSize);
        jTextFieldSubject.setFont(f);

        MessageLocal message = getCompletedMessage(messageId);
        if (!message.isIntegrityCheck()) {
            JOptionPane.showMessageDialog(rootPane, messages.getMessage("integrity_check_failed"));
        }

        String header = ""; // Keep this for mise au point / display tuning

        // this.jTextFieldSubject.setText(message.getSubject());
        // Convert subject from Html after encryption
        this.jTextFieldSubject.setText(HtmlConverter.fromHtml(message.getSubject()));

        String sender = HtmlConverter.fromHtml(message.getSenderUserName());
        sender = header + sender + " <" + message.getSenderUserEmail() + ">";
        sender = Util.removeTrailingSemiColumns(sender);

        this.jTextFieldUserFrom.setText(sender);

        // this.jEditorPaneBody.setText(message.getBody());
        String text = message.getBody();

        // test if message is created by Mobile, if yes remove HTML tags
        if (message.getIsSigned()) {
            text = HtmlConverter.fromHtml(text);
        }

        try {
            text = JEditorPaneLinkDetector.detectLinkAndCodeHtml(text);
        } catch (IOException ex) {
            JOptionPaneNewCustom.showException(this, ex);
        }

        jPanelBody.remove(jScrollPaneBody);

        // create a JEditorPane that renders HTML and defaults to the system font.
        jEditorPaneBody = new JEditorPane(new HTMLEditorKit().getContentType(), text);

        this.jEditorPaneBody.setContentType("text/html");
        this.jEditorPaneBody.setEditable(false);
        this.jEditorPaneBody.setBackground(Color.WHITE);

        this.jEditorPaneBody.setText(text);

        MessageReader.setSystemFontToHtmlPane(jEditorPaneBody);

        addHyperLinkDetector();

        jScrollPaneBody.setViewportView(jEditorPaneBody);
        jPanelBody.add(jScrollPaneBody);

        clipboardManager = new ClipboardManager(rootPane);

        jButtonPrint.setEnabled(message.isPrintable());
        jButtonTransfert.setEnabled(message.isFowardable());
        if (itemFoward != null) {
            itemFoward.setEnabled(message.isFowardable());
        }
        jMenuItemPrint.setEnabled(message.isPrintable());
        jMenuItemFoward.setEnabled(message.isFowardable());

        boolean jEditorPaneBodyFocusable = true;
        if (!message.isFowardable() || !message.isPrintable()) {
            jEditorPaneBodyFocusable = false;
        }
        jEditorPaneBody.setFocusable(jEditorPaneBodyFocusable);

        // Display recipients (To and Cc
        List<RecipientLocal> recipients = message.getRecipientLocal();
        String recipientTo = "";
        String recipientCc = "";

        for (RecipientLocal recipient : recipients) {
            EmailUser emailUser = new EmailUser(recipient.getNameRecipient(), recipient.getEmail());

            // Main: do not display BCC recipients in CC panel
            if (recipient.getTypeRecipient() == Parms.RECIPIENT_TO) {
                recipientTo += emailUser.getNameAndEmailAddress() + "; ";
            } else if (recipient.getTypeRecipient() == Parms.RECIPIENT_CC) {
                recipientCc += emailUser.getNameAndEmailAddress() + "; ";
            } else {
                // Nothing for BCC: do not display them back.
            }

        }

//        try {
//            List<PendingMessageUserLocal> pendingMessageUserLocals = message.getPendingMessageUserLocal();
//
//            // NDP : Main: do not display BCC recipients in CC panel
//            for (PendingMessageUserLocal pendingMessageUserLocal : pendingMessageUserLocals) {
//                int typeRecipient = pendingMessageUserLocal.getType_recipient();
//
//                if (typeRecipient == Parms.RECIPIENT_TO) {
//                    recipientTo += pendingMessageUserLocal.getEmail() + "; ";
//                } else if (typeRecipient == Parms.RECIPIENT_CC) {
//                    recipientCc += pendingMessageUserLocal.getEmail() + "; ";
//                } else {
//                    // Nothing for BCC: do not display them back.
//                }
//
//            }
//        } catch (Exception e) {
//            this.setCursor(Cursor.getDefaultCursor());
//            e.printStackTrace();
//            JOptionPaneNewCustom.showException(rootPane, e);
//        }

        // Remove HTML coding
        recipientTo = HtmlConverter.fromHtml(recipientTo);
        recipientCc = HtmlConverter.fromHtml(recipientCc);

        recipientTo = Util.removeTrailingSemiColumns(recipientTo);
        recipientCc = Util.removeTrailingSemiColumns(recipientCc);

        this.jTextAreaRecipientsTo.setText(recipientTo);
        this.jTextAreaRecipientsCc.setText(recipientCc);

        this.jTextAreaRecipientsTo.setCaretPosition(0);
        this.jTextAreaRecipientsCc.setCaretPosition(0);

        if (jTextAreaRecipientsCc.getText().length() <= 1) {
            jPanelCc.setVisible(false);
        } else {
            jPanelCc.setVisible(true);
        }

        // DateFormat sdf = new SimpleDateFormat(messages.getMessage("date_format"));
        AppDateFormat df = new AppDateFormat();

        String messageDate = df.format(message.getDateMessage());

        jTextFieldDate.setText(messageDate);
        setTextFieldsPopup();
        // Build attachment list

        List<AttachmentLocal> attachments = message.getAttachmentLocal();
        buildAttachmentList(attachments, message.getSenderUserNumber());

        // This must be done AFTER to, cc, and attach values settings
        setAllPanelWithtextAreaHeightForMac();

        // jPanelMessageAndButtons.remove(jPanelButtons);
        // jPanelMessageAndButtons.add(jPanelButtons);
        jPanelMessageMain.add(jPanelMessage);

        jSplitPaneMessage.setDividerLocation(location);

        this.setCursor(Cursor.getDefaultCursor());

        // These 2 stupid lines : only to Force to diplay top of file first
        jEditorPaneBody.moveCaretPosition(0);
        jEditorPaneBody.setSelectionEnd(0);

        // this.jButtonPrev.setVisible(false);
        // this.jButtonNext.setVisible(false);
        // this.jLabelNbElements.setText(null);
    }

    /**
     * @param messageId
     * @return
     */
    public MessageLocal getCompletedMessage(int messageId) {
        MessageLocal message = null;

        // Callback the message updated by the background thread
        while (true) {
            message = messageLocalStore.get(messageId);

            if (message == null) {
                throw new IllegalArgumentException("Message is null for Message Id: " + messageId);
            }

            //23/10/19 NDP: No wait for DRAFT
            int folderId = getSelectedFolderId();
            if (folderId == Parms.DRAFT_ID) {
                return message;
            }
            
            if (message.isUpdateComplete()) {
                break;
            }
            // Sleep wating for
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    /**
     * Build the map of attachment sizes
     *
     * @param attachments the attachments
     * @return
     */
    public static Map<Integer, Long> getAttachmentsSize(List<AttachmentLocal> attachments) {
        Map<Integer, Long> AttachmentsSize = new HashMap<Integer, Long>();

        for (int i = 0; i < attachments.size(); i++) {
            AttachmentLocal attachmentLocal = attachments.get(i);
            int attachPosition = attachmentLocal.getAttachPosition();
            long size = attachmentLocal.getFileSize();

            // System.out.println("attachPosition: " + attachPosition + " size: " + size);
            AttachmentsSize.put(attachPosition - 1, size);
        }

        return AttachmentsSize;
    }

    /**
     * Build the JList of attachments
     *
     * @param attachments The List of AttachmentLocal to display
     * @param senderUserNumber the uset that sends the message
     */
    private void buildAttachmentList(List<AttachmentLocal> attachments, int senderUserNumber) {

        jScrollPaneAttach.remove(jListAttach);
        SortedDefaultListModel model_attachs = new SortedDefaultListModel();
        jListAttach = new JList(model_attachs);
        jListAttach.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        jListAttach.setVisibleRowCount(-1);
        // jListAttachments.setPrototypeCellValue("a_a_aaaaaaaaaaaaaaaa.aaa");
        jListAttach.setVisibleRowCount(-1);

        JListUtil.selectItemWhenMouverOver(jListAttach);

        jListAttach.setCellRenderer(new ReceivedAttachmentListRenderer(attachments));

        // HACK NDP 17/03/18
        JListUtil.formatSpacing(jListAttach);

        jPanelNorth.remove(jPanelAttach);
        jPanelNorth.remove(jPanelAttachSepMessage);

        if (attachments.isEmpty()) {
            // jPanelAttachSepMessage.setVisible(false);
            // jScrollPaneAttach.setVisible(false);
            return;
        } else {
            jPanelNorth.add(jPanelAttach);
            jPanelNorth.add(jPanelAttachSepMessage);
            jPanelAttachSepMessage.setVisible(true);
            jScrollPaneAttach.setVisible(true);
        }
        for (AttachmentLocal attachment : attachments) {

            // model_attachs.addElement(attachment.getFileName());
            String fileName = attachment.getFileName();
            fileName = HtmlConverter.fromHtml(fileName);
            model_attachs.addElement(fileName);
        }

        MessageDecryptor messageDecryptor = null;
        try {
            messageDecryptor = new MessageDecryptor(userNumber, passphrase, connection);
        } catch (SQLException ex) {
            JOptionPaneNewCustom.showException(this, ex);
            return;
        }

        PgpKeyPairLocal pgpKeyPairLocal = messageDecryptor.getKeyPair();
        String privateKeyPgpBlock = pgpKeyPairLocal.getPrivateKeyPgpBlock();

        Map<Integer, Long> attachmentsSize = getAttachmentsSize(attachments);
        attachmentJListHandler = new AttachmentListHandler(thisOne, senderUserNumber, jListAttach, attachmentsSize,
                this.getConnection(), privateKeyPgpBlock, passphrase);

        jListAttach.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                jList_mousePressedOrReleased(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                jList_mousePressedOrReleased(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                jList_mouseClicked(e);
            }
        });
        buildJListPopupMenu();
        jScrollPaneAttach.setViewportView(jListAttach);

    }

    public void jList_mousePressedOrReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showJListPopupMenu(e);
        }
    }

    public void jList_mouseClicked(MouseEvent e) {
        if (e.getClickCount() >= 2) {
            attachmentJListHandler.openAttach();
        }
    }

    /**
     * Build popup menu for the JList containing attachment
     */
    private void buildJListPopupMenu() {
        jListPopupMenu = new JPopupMenu();
        JMenuItem itemOpenAttach = new JMenuItem(messages.getMessage("open"));
        JMenuItem itemSaveAttach = new JMenuItem(messages.getMessage("save"));
        JMenuItem itemSaveAll = new JMenuItem(messages.getMessage("save_all"));

        itemOpenAttach.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attachmentJListHandler.openAttach();
            }
        });

        itemSaveAttach.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attachmentJListHandler.saveAttach();
            }
        });

        itemSaveAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attachmentJListHandler.saveAllAttachments();
            }
        });

        jListPopupMenu.add(itemOpenAttach);
        jListPopupMenu.add(itemSaveAttach);
        jListPopupMenu.addSeparator();
        jListPopupMenu.add(itemSaveAll);
    }

    /**
     * Reload the tree of folders
     */
    public void reloadJTree() {
        // Init tree containing folders
        try {

            // System.out.println("Begin reloadJTree");
            foldersHandler = new FoldersHandler(this.getConnection(), userNumber);
            foldersHandler.initFolders();
            // jPanelFoldersTree.remove(customJtree);
            customJtree = CustomJTree.initJTree(this, connection, foldersHandler, userNumber);

            // System.out.println("End reloadJTree");
        } catch (Exception e) {
            JOptionPaneNewCustom.showException(rootPane, e);
            System.exit(-1);
        }

        this.jScrollPane2.setViewportView(customJtree);
        this.update(this.getGraphics());
        // pack();
    }

    /**
     * Get folder handler
     *
     * @return
     */
    public FoldersHandler getFoldersHandler() {
        return foldersHandler;
    }

    /**
     * Set folder handler
     *
     * @param theHandler
     */
    public void setFoldersHandler(FoldersHandler theHandler) {
        this.foldersHandler = theHandler;
    }

    /**
     * Get the Jtree
     *
     * @return
     */
    public JTree getJtree() {
        return this.customJtree.getTree();
    }

    public CustomJTree getCustomJTree() {
        return this.customJtree;
    }

    /**
     * Get logged user number
     *
     * @return
     */
    public int getUserNumber() {
        return this.userNumber;
    }

    /**
     * @return the theKeyId
     */
    public String getKeyId() {
        return keyId;
    }

    /**
     * Get passphrase of logged user
     *
     * @return the passphrase
     */
    public char[] getUserPassphrase() {
        return passphrase;
    }

    public void setUserPassphrase(char[] passphrase) {
        this.passphrase = passphrase;
    }

    /**
     * Get connection
     *
     * @return
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Delete all temp/cache files
     */
    public void clearCache() {
        CacheFileHandler cacheFileHandler = new CacheFileHandler();
        cacheFileHandler.deletedAllCachedFiles();
    }

    public AttachmentListHandler getAttachmentJListHandler() {
        return attachmentJListHandler;
    }

    Set<UserAccount> getUserAccounts() {
        return userAccounts;
    }

    private void print() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        JEditorPane printEditorPane = new JEditorPane();

        JFrame jframePrint = new JFrame();
        jframePrint.setIconImage(this.getIconImage());
        jframePrint.add(this.jEditorPaneBody);

        String from = jTextFieldUserFrom.getText();
        String to = StringMgr.ReplaceAll(jTextAreaRecipientsTo.getText(), "<", "&lt;");
        to = StringMgr.ReplaceAll(to, ">", "&gt;");
        String cc = StringMgr.ReplaceAll(jTextAreaRecipientsCc.getText(), "<", "&lt;");
        cc = StringMgr.ReplaceAll(cc, ">", "&gt;");

        String sep = ": ";
        // sep = Util.fillWithHtmlBlanks(sep, 20);

        String printText = "";
        printText += jLabelFrom.getText() + sep + from + "<br>";
        printText += messages.getMessage("to_col") + sep + to + "<br>";
        printText += messages.getMessage("cc_col") + sep + cc + "<br>";
        printText += jLabelDate.getText() + sep + jTextFieldDate.getText() + "<br>";
        printText += messages.getMessage("subject") + sep + jTextFieldSubject.getText() + "<br><br>";
        printText += jEditorPaneBody.getText();
        printEditorPane.setContentType(jEditorPaneBody.getContentType());
        printEditorPane.setFont(jEditorPaneBody.getFont());
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

    private void search() {

        if (search != null) {
            search.dispose();
        }

        search = new Search(this, connection, this.getJtree(), userNumber, passphrase, getKeyId());
        search.setVisible(true);
    }

    
    void updateBottomPlan(String account) {
        this.jLabelPlan.setText(account);
    }
    
    /**
     * debug tool
     */
    private static void debug(String s) {
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
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupReadingPane = new javax.swing.ButtonGroup();
        buttonGroupFolderSection = new javax.swing.ButtonGroup();
        jPanelCenter = new javax.swing.JPanel();
        jPanelToolbar = new javax.swing.JPanel();
        jPanelToolbarMain = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButtonNewMessage = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButtonReply = new javax.swing.JButton();
        jButtonReplyAll = new javax.swing.JButton();
        jButtonTransfert = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButtonDeleteSelectedMessage = new javax.swing.JButton();
        jButtonMoveMessage = new javax.swing.JButton();
        jButtonPrint = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jButtonAddressBook = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jButtonRefresh = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jButtonNewFolder = new javax.swing.JButton();
        jSeparator14 = new javax.swing.JToolBar.Separator();
        jButtonSearch = new javax.swing.JButton();
        jSeparatorButtonBuy = new javax.swing.JToolBar.Separator();
        jButtonBuy = new javax.swing.JButton();
        jPanelProgressBarBox = new javax.swing.JPanel();
        jPanelPush = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanelEndProgress = new javax.swing.JPanel();
        jPanelLogout = new javax.swing.JPanel();
        jPanelSeparator = new javax.swing.JPanel();
        jSplitPaneFolders = new javax.swing.JSplitPane();
        jPanelFolders = new javax.swing.JPanel();
        jPanelFoldersTree = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jSplitPaneMessage = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanelMessageMain = new javax.swing.JPanel();
        jPanelMessageContainer = new javax.swing.JPanel();
        jPanelMessage = new javax.swing.JPanel();
        jPanelNorth = new javax.swing.JPanel();
        jPanelTop = new javax.swing.JPanel();
        jPanelTopButtons = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jButtonReply1 = new javax.swing.JButton();
        jButtonReplyAll1 = new javax.swing.JButton();
        jButtonTransfert1 = new javax.swing.JButton();
        jPanelSubject = new javax.swing.JPanel();
        jPanelLeft8 = new javax.swing.JPanel();
        jTextFieldSubject = new javax.swing.JTextField();
        jPanelFromNew = new javax.swing.JPanel();
        jPanelLeft5 = new javax.swing.JPanel();
        jLabelFrom = new javax.swing.JLabel();
        jTextFieldUserFrom = new javax.swing.JTextField();
        jPanelFromAndRecip = new javax.swing.JPanel();
        jPanelDate = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jPanelLeft7 = new javax.swing.JPanel();
        jLabelDate = new javax.swing.JLabel();
        jTextFieldDate = new javax.swing.JTextField();
        jPanelSepRecip2 = new javax.swing.JPanel();
        jPanelTo = new javax.swing.JPanel();
        jPanelToLeft = new javax.swing.JPanel();
        jLabelTo = new javax.swing.JLabel();
        jPanelToRight = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaRecipientsTo = new javax.swing.JTextArea();
        jPanelSepRecip1 = new javax.swing.JPanel();
        jPanelCc = new javax.swing.JPanel();
        jPanelCcLeft = new javax.swing.JPanel();
        jLabelCc = new javax.swing.JLabel();
        jPanelCcRight = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextAreaRecipientsCc = new javax.swing.JTextArea();
        jPanelSepAttach1 = new javax.swing.JPanel();
        jPanelSepRecipients = new javax.swing.JPanel();
        jPanelSepBorder = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelSepBorder1 = new javax.swing.JPanel();
        jPanelAttach = new javax.swing.JPanel();
        jScrollPaneAttach = new javax.swing.JScrollPane();
        jListAttach = new javax.swing.JList();
        jPanelAttachSepMessage = new javax.swing.JPanel();
        jPanelSepBorder2 = new javax.swing.JPanel();
        jSeparator10 = new javax.swing.JSeparator();
        jPanelSepBorder3 = new javax.swing.JPanel();
        jPanelScrollPane = new javax.swing.JPanel();
        jPanelLeftSpace1 = new javax.swing.JPanel();
        jPanelBody = new javax.swing.JPanel();
        jScrollPaneBody = new javax.swing.JScrollPane();
        jEditorPaneBody = new javax.swing.JEditorPane();
        jPanelRigthSpace2 = new javax.swing.JPanel();
        jSeparator13 = new javax.swing.JSeparator();
        jPanelSep = new javax.swing.JPanel();
        jPaneStatusBar = new javax.swing.JPanel();
        jPanelSslCert = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabelHost = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButtonHostLock = new javax.swing.JButton();
        jPanelSepVertical2 = new javax.swing.JPanel();
        jPanelLeftSep2 = new javax.swing.JPanel();
        jSeparator19 = new javax.swing.JSeparator();
        jPanelRightSep2 = new javax.swing.JPanel();
        jPanelInfo = new javax.swing.JPanel();
        jPanelStorage = new javax.swing.JPanel();
        jLabelPlan = new javax.swing.JLabel();
        jLabelSep = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabelStorage = new javax.swing.JLabel();
        jPanelSepVerticalLastLogin = new javax.swing.JPanel();
        jPanelLeftSep1 = new javax.swing.JPanel();
        jSeparator18 = new javax.swing.JSeparator();
        jPanelRightSep1 = new javax.swing.JPanel();
        jPanelLastLogin = new javax.swing.JPanel();
        jLabelLastLogin = new javax.swing.JLabel();
        jPanelSepLabels = new javax.swing.JPanel();
        jButtonDetail = new javax.swing.JButton();
        jPanelButtonsNav = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabelNbElements = new javax.swing.JLabel();
        jButtonPrev = new javax.swing.JButton();
        jButtonNext = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemNew = new javax.swing.JMenuItem();
        jMenuItemNewFolder = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        jMenuItemPrint = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItemClose = new javax.swing.JMenuItem();
        jMenuItemQuit = new javax.swing.JMenuItem();
        jMenuAccounts = new javax.swing.JMenu();
        jMenuConnectToAccount = new javax.swing.JMenu();
        jMenuItemConnectToAccount = new javax.swing.JMenuItem();
        jSeparator21 = new javax.swing.JPopupMenu.Separator();
        jMenuMessage = new javax.swing.JMenu();
        jMenuItemGetNewMessage = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItemReply = new javax.swing.JMenuItem();
        jMenuItemReplyAll = new javax.swing.JMenuItem();
        jMenuItemFoward = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuItemMarkRead = new javax.swing.JMenuItem();
        jMenuItemMarkUnread = new javax.swing.JMenuItem();
        jMenuItemMove = new javax.swing.JMenuItem();
        jMenuItemDelete = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        jMenuItemSearch = new javax.swing.JMenuItem();
        jMenuSettings = new javax.swing.JMenu();
        jMenuItemUserSettings = new javax.swing.JMenuItem();
        jMenuItemChangePassphrase = new javax.swing.JMenuItem();
        jMenuItemPassphraseRecoverySettings = new javax.swing.JMenuItem();
        jMenuItemForgetPassphrase = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        jMenuItemProxySettings = new javax.swing.JMenuItem();
        jMenuItemAutoresponder = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JPopupMenu.Separator();
        jMenuItemAddPhoto = new javax.swing.JMenuItem();
        jMenuItemDeletePhoto = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JPopupMenu.Separator();
        jMenuItemUpgrade = new javax.swing.JMenuItem();
        jMenuItemActivateSubscription = new javax.swing.JMenuItem();
        jSeparator22 = new javax.swing.JPopupMenu.Separator();
        jMenuItemDeleteAccount = new javax.swing.JMenuItem();
        jMenu2FA = new javax.swing.JMenu();
        jMenuItemDouble2FaAccountQRcode = new javax.swing.JMenuItem();
        jMenuItemDouble2FaActivation = new javax.swing.JMenuItem();
        jMenuItemDouble2faHelp = new javax.swing.JMenuItem();
        jMenuContacts = new javax.swing.JMenu();
        jMenuItemImportAddrBook = new javax.swing.JMenuItem();
        jMenuItemAddressBook = new javax.swing.JMenuItem();
        jMenuWindow = new javax.swing.JMenu();
        jMenuOrientation = new javax.swing.JMenu();
        jRadioButtonMenuItemBottom = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemRight = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemPaneInactive = new javax.swing.JRadioButtonMenuItem();
        jMenuFolderSection = new javax.swing.JMenu();
        jRadioButtonMenuItemNormal = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemFolderInactive = new javax.swing.JRadioButtonMenuItem();
        jSeparator20 = new javax.swing.JPopupMenu.Separator();
        jMenuItemReset = new javax.swing.JMenuItem();
        jMenuAbout = new javax.swing.JMenu();
        jMenuItemSystemInfo = new javax.swing.JMenuItem();
        jMenuItemWhatsNew = new javax.swing.JMenuItem();
        jMenuItemCheckNewVersion = new javax.swing.JMenuItem();
        jMenuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelToolbar.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelToolbar.setLayout(new javax.swing.BoxLayout(jPanelToolbar, javax.swing.BoxLayout.LINE_AXIS));

        jPanelToolbarMain.setLayout(new java.awt.GridLayout(1, 0));

        jToolBar1.setRollover(true);

        jButtonNewMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/mail_write.png"))); // NOI18N
        jButtonNewMessage.setFocusable(false);
        jButtonNewMessage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonNewMessage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonNewMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewMessageActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonNewMessage);
        jToolBar1.add(jSeparator3);

        jButtonReply.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/mail_reply.png"))); // NOI18N
        jButtonReply.setFocusable(false);
        jButtonReply.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonReply.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonReply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReplyActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonReply);

        jButtonReplyAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/mail_reply_all.png"))); // NOI18N
        jButtonReplyAll.setFocusable(false);
        jButtonReplyAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonReplyAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonReplyAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReplyAllActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonReplyAll);

        jButtonTransfert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/mail_forward.png"))); // NOI18N
        jButtonTransfert.setFocusable(false);
        jButtonTransfert.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTransfert.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTransfert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTransfertActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonTransfert);
        jToolBar1.add(jSeparator1);

        jButtonDeleteSelectedMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/delete.png"))); // NOI18N
        jButtonDeleteSelectedMessage.setFocusable(false);
        jButtonDeleteSelectedMessage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDeleteSelectedMessage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDeleteSelectedMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteSelectedMessageActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonDeleteSelectedMessage);

        jButtonMoveMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/mail_exchange.png"))); // NOI18N
        jButtonMoveMessage.setFocusable(false);
        jButtonMoveMessage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonMoveMessage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonMoveMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMoveMessageActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonMoveMessage);

        jButtonPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/printer.png"))); // NOI18N
        jButtonPrint.setFocusable(false);
        jButtonPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonPrint);
        jToolBar1.add(jSeparator6);

        jButtonAddressBook.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/businesspeople.png"))); // NOI18N
        jButtonAddressBook.setFocusable(false);
        jButtonAddressBook.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAddressBook.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAddressBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddressBookActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonAddressBook);
        jToolBar1.add(jSeparator5);

        jButtonRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/refresh.png"))); // NOI18N
        jButtonRefresh.setFocusable(false);
        jButtonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonRefresh);
        jToolBar1.add(jSeparator4);

        jButtonNewFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/folder_open_plus.png"))); // NOI18N
        jButtonNewFolder.setFocusable(false);
        jButtonNewFolder.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonNewFolder.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonNewFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewFolderActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonNewFolder);
        jToolBar1.add(jSeparator14);

        jButtonSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/binocular.png"))); // NOI18N
        jButtonSearch.setFocusable(false);
        jButtonSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonSearch);
        jToolBar1.add(jSeparatorButtonBuy);

        jButtonBuy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/shopping_cart.png"))); // NOI18N
        jButtonBuy.setFocusable(false);
        jButtonBuy.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonBuy.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonBuy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBuyActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonBuy);

        jPanelProgressBarBox.setMaximumSize(new java.awt.Dimension(32767, 33));
        jPanelProgressBarBox.setOpaque(false);
        jPanelProgressBarBox.setLayout(new javax.swing.BoxLayout(jPanelProgressBarBox, javax.swing.BoxLayout.LINE_AXIS));

        jPanelPush.setMaximumSize(new java.awt.Dimension(32767, 14));
        jPanelPush.setMinimumSize(new java.awt.Dimension(10, 14));
        jPanelPush.setOpaque(false);
        jPanelPush.setPreferredSize(new java.awt.Dimension(10, 14));
        jPanelProgressBarBox.add(jPanelPush);

        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setMaximumSize(new java.awt.Dimension(190, 17));
        jProgressBar1.setMinimumSize(new java.awt.Dimension(190, 17));
        jProgressBar1.setPreferredSize(new java.awt.Dimension(190, 17));
        jProgressBar1.setString(" ");
        jProgressBar1.setStringPainted(true);
        jPanelProgressBarBox.add(jProgressBar1);

        jPanelEndProgress.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelProgressBarBox.add(jPanelEndProgress);

        jToolBar1.add(jPanelProgressBarBox);

        jPanelToolbarMain.add(jToolBar1);

        jPanelToolbar.add(jPanelToolbarMain);

        jPanelLogout.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelLogout.setLayout(new javax.swing.BoxLayout(jPanelLogout, javax.swing.BoxLayout.LINE_AXIS));
        jPanelToolbar.add(jPanelLogout);

        jPanelCenter.add(jPanelToolbar);

        jPanelSeparator.setBackground(new java.awt.Color(255, 255, 255));
        jPanelSeparator.setLayout(new javax.swing.BoxLayout(jPanelSeparator, javax.swing.BoxLayout.LINE_AXIS));

        jSplitPaneFolders.setBorder(null);

        jPanelFolders.setBackground(new java.awt.Color(255, 255, 255));
        jPanelFolders.setLayout(new javax.swing.BoxLayout(jPanelFolders, javax.swing.BoxLayout.Y_AXIS));

        jPanelFoldersTree.setBackground(new java.awt.Color(255, 255, 255));
        jPanelFoldersTree.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setBorder(null);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jPanelFoldersTree.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanelFolders.add(jPanelFoldersTree);

        jSplitPaneFolders.setLeftComponent(jPanelFolders);

        jSplitPaneMessage.setBackground(new java.awt.Color(255, 255, 255));
        jSplitPaneMessage.setBorder(null);
        jSplitPaneMessage.setDividerLocation(120);
        jSplitPaneMessage.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPaneMessage.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSplitPaneMessage.setLastDividerLocation(120);

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(23, 60));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setShowHorizontalLines(false);
        jTable1.setShowVerticalLines(false);
        jScrollPane1.setViewportView(jTable1);

        jSplitPaneMessage.setLeftComponent(jScrollPane1);

        jPanelMessageMain.setBackground(new java.awt.Color(255, 255, 255));
        jPanelMessageMain.setLayout(new javax.swing.BoxLayout(jPanelMessageMain, javax.swing.BoxLayout.Y_AXIS));

        jPanelMessageContainer.setBackground(new java.awt.Color(255, 255, 255));
        jPanelMessageContainer.setLayout(new java.awt.BorderLayout());

        jPanelMessage.setBackground(new java.awt.Color(255, 255, 255));
        jPanelMessage.setLayout(new java.awt.BorderLayout());

        jPanelNorth.setOpaque(false);
        jPanelNorth.setLayout(new javax.swing.BoxLayout(jPanelNorth, javax.swing.BoxLayout.Y_AXIS));

        jPanelTop.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelTop.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelTop.setOpaque(false);
        jPanelTop.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelNorth.add(jPanelTop);

        jPanelTopButtons.setOpaque(false);
        jPanelTopButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jPanel5.setMaximumSize(new java.awt.Dimension(0, 0));
        jPanel5.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel5.setOpaque(false);
        jPanel5.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanelTopButtons.add(jPanel5);

        jButtonReply1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/mail_reply.png"))); // NOI18N
        jButtonReply1.setText("Reply");
        jButtonReply1.setBorderPainted(false);
        jButtonReply1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonReply1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReply1ActionPerformed(evt);
            }
        });
        jPanelTopButtons.add(jButtonReply1);

        jButtonReplyAll1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/mail_reply_all.png"))); // NOI18N
        jButtonReplyAll1.setText("Reply All");
        jButtonReplyAll1.setBorderPainted(false);
        jButtonReplyAll1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonReplyAll1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReplyAll1ActionPerformed(evt);
            }
        });
        jPanelTopButtons.add(jButtonReplyAll1);

        jButtonTransfert1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/mail_forward.png"))); // NOI18N
        jButtonTransfert1.setText("Forward");
        jButtonTransfert1.setBorderPainted(false);
        jButtonTransfert1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTransfert1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTransfert1ActionPerformed(evt);
            }
        });
        jPanelTopButtons.add(jButtonTransfert1);

        jPanelNorth.add(jPanelTopButtons);

        jPanelSubject.setBackground(new java.awt.Color(255, 255, 255));
        jPanelSubject.setLayout(new javax.swing.BoxLayout(jPanelSubject, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft8.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelLeft8.setOpaque(false);
        jPanelSubject.add(jPanelLeft8);

        jTextFieldSubject.setEditable(false);
        jTextFieldSubject.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldSubject.setText("jTextField1");
        jTextFieldSubject.setBorder(null);
        jPanelSubject.add(jTextFieldSubject);

        jPanelNorth.add(jPanelSubject);

        jPanelFromNew.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelFromNew.setMinimumSize(new java.awt.Dimension(39, 0));
        jPanelFromNew.setOpaque(false);
        jPanelFromNew.setPreferredSize(new java.awt.Dimension(465, 31));
        jPanelFromNew.setLayout(new javax.swing.BoxLayout(jPanelFromNew, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft5.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelLeft5.setOpaque(false);
        jPanelFromNew.add(jPanelLeft5);

        jLabelFrom.setText("From");
        jLabelFrom.setPreferredSize(new java.awt.Dimension(55, 14));
        jPanelFromNew.add(jLabelFrom);

        jTextFieldUserFrom.setEditable(false);
        jTextFieldUserFrom.setText("jTextFieldUserFrom");
        jTextFieldUserFrom.setBorder(null);
        jTextFieldUserFrom.setOpaque(false);
        jPanelFromNew.add(jTextFieldUserFrom);

        jPanelNorth.add(jPanelFromNew);

        jPanelFromAndRecip.setBackground(new java.awt.Color(255, 255, 255));
        jPanelFromAndRecip.setLayout(new javax.swing.BoxLayout(jPanelFromAndRecip, javax.swing.BoxLayout.Y_AXIS));

        jPanelDate.setMaximumSize(new java.awt.Dimension(32767, 24));
        jPanelDate.setMinimumSize(new java.awt.Dimension(38, 0));
        jPanelDate.setOpaque(false);
        jPanelDate.setLayout(new javax.swing.BoxLayout(jPanelDate, javax.swing.BoxLayout.LINE_AXIS));

        jPanel20.setMinimumSize(new java.awt.Dimension(38, 0));
        jPanel20.setOpaque(false);
        jPanel20.setPreferredSize(new java.awt.Dimension(185, 31));
        jPanel20.setLayout(new javax.swing.BoxLayout(jPanel20, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft7.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelLeft7.setOpaque(false);
        jPanel20.add(jPanelLeft7);

        jLabelDate.setText("Date");
        jLabelDate.setMaximumSize(new java.awt.Dimension(30, 16));
        jLabelDate.setMinimumSize(new java.awt.Dimension(30, 16));
        jLabelDate.setPreferredSize(new java.awt.Dimension(55, 14));
        jPanel20.add(jLabelDate);

        jTextFieldDate.setEditable(false);
        jTextFieldDate.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextFieldDate.setText("jTextFieldDate");
        jTextFieldDate.setBorder(null);
        jTextFieldDate.setOpaque(false);
        jTextFieldDate.setPreferredSize(new java.awt.Dimension(120, 14));
        jPanel20.add(jTextFieldDate);

        jPanelDate.add(jPanel20);

        jPanelFromAndRecip.add(jPanelDate);

        jPanelSepRecip2.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSepRecip2.setOpaque(false);
        jPanelSepRecip2.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelSepRecip2.setLayout(new javax.swing.BoxLayout(jPanelSepRecip2, javax.swing.BoxLayout.LINE_AXIS));
        jPanelFromAndRecip.add(jPanelSepRecip2);

        jPanelTo.setMinimumSize(new java.awt.Dimension(71, 0));
        jPanelTo.setOpaque(false);
        jPanelTo.setLayout(new javax.swing.BoxLayout(jPanelTo, javax.swing.BoxLayout.LINE_AXIS));

        jPanelToLeft.setMaximumSize(new java.awt.Dimension(50, 30));
        jPanelToLeft.setMinimumSize(new java.awt.Dimension(50, 30));
        jPanelToLeft.setOpaque(false);
        jPanelToLeft.setPreferredSize(new java.awt.Dimension(65, 30));
        jPanelToLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));

        jLabelTo.setText("To");
        jPanelToLeft.add(jLabelTo);

        jPanelTo.add(jPanelToLeft);

        jPanelToRight.setAutoscrolls(true);
        jPanelToRight.setMaximumSize(new java.awt.Dimension(32767, 30));
        jPanelToRight.setMinimumSize(new java.awt.Dimension(21, 30));
        jPanelToRight.setOpaque(false);
        jPanelToRight.setPreferredSize(new java.awt.Dimension(100, 30));
        jPanelToRight.setLayout(new javax.swing.BoxLayout(jPanelToRight, javax.swing.BoxLayout.PAGE_AXIS));

        jScrollPane3.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane3.setBorder(null);
        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setOpaque(false);

        jTextAreaRecipientsTo.setEditable(false);
        jTextAreaRecipientsTo.setColumns(20);
        jTextAreaRecipientsTo.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jTextAreaRecipientsTo.setRows(1);
        jTextAreaRecipientsTo.setText("jTextAreaRecipientsTo");
        jTextAreaRecipientsTo.setWrapStyleWord(true);
        jTextAreaRecipientsTo.setBorder(null);
        jScrollPane3.setViewportView(jTextAreaRecipientsTo);

        jPanelToRight.add(jScrollPane3);

        jPanelTo.add(jPanelToRight);

        jPanelFromAndRecip.add(jPanelTo);

        jPanelSepRecip1.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSepRecip1.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanelSepRecip1.setOpaque(false);
        jPanelSepRecip1.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelSepRecip1.setLayout(new javax.swing.BoxLayout(jPanelSepRecip1, javax.swing.BoxLayout.LINE_AXIS));
        jPanelFromAndRecip.add(jPanelSepRecip1);

        jPanelCc.setMinimumSize(new java.awt.Dimension(71, 0));
        jPanelCc.setOpaque(false);
        jPanelCc.setLayout(new javax.swing.BoxLayout(jPanelCc, javax.swing.BoxLayout.LINE_AXIS));

        jPanelCcLeft.setMaximumSize(new java.awt.Dimension(50, 30));
        jPanelCcLeft.setMinimumSize(new java.awt.Dimension(50, 30));
        jPanelCcLeft.setOpaque(false);
        jPanelCcLeft.setPreferredSize(new java.awt.Dimension(65, 30));
        jPanelCcLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));

        jLabelCc.setText("Cc");
        jPanelCcLeft.add(jLabelCc);

        jPanelCc.add(jPanelCcLeft);

        jPanelCcRight.setAutoscrolls(true);
        jPanelCcRight.setMaximumSize(new java.awt.Dimension(32767, 30));
        jPanelCcRight.setMinimumSize(new java.awt.Dimension(21, 30));
        jPanelCcRight.setOpaque(false);
        jPanelCcRight.setPreferredSize(new java.awt.Dimension(100, 30));
        jPanelCcRight.setLayout(new javax.swing.BoxLayout(jPanelCcRight, javax.swing.BoxLayout.PAGE_AXIS));

        jScrollPane5.setBorder(null);
        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane5.setOpaque(false);

        jTextAreaRecipientsCc.setEditable(false);
        jTextAreaRecipientsCc.setColumns(20);
        jTextAreaRecipientsCc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jTextAreaRecipientsCc.setRows(1);
        jTextAreaRecipientsCc.setText("jTextAreaRecipientsCc");
        jTextAreaRecipientsCc.setWrapStyleWord(true);
        jTextAreaRecipientsCc.setBorder(null);
        jScrollPane5.setViewportView(jTextAreaRecipientsCc);

        jPanelCcRight.add(jScrollPane5);

        jPanelCc.add(jPanelCcRight);

        jPanelFromAndRecip.add(jPanelCc);

        jPanelSepAttach1.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSepAttach1.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanelSepAttach1.setOpaque(false);
        jPanelSepAttach1.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelSepAttach1.setLayout(new javax.swing.BoxLayout(jPanelSepAttach1, javax.swing.BoxLayout.LINE_AXIS));
        jPanelFromAndRecip.add(jPanelSepAttach1);

        jPanelNorth.add(jPanelFromAndRecip);

        jPanelSepRecipients.setBackground(new java.awt.Color(255, 255, 255));
        jPanelSepRecipients.setMaximumSize(new java.awt.Dimension(32777, 10));
        jPanelSepRecipients.setMinimumSize(new java.awt.Dimension(10, 0));
        jPanelSepRecipients.setLayout(new javax.swing.BoxLayout(jPanelSepRecipients, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSepBorder.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSepBorder.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelSepBorder.setOpaque(false);
        jPanelSepBorder.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelSepRecipients.add(jPanelSepBorder);

        jSeparator2.setForeground(new java.awt.Color(102, 102, 255));
        jSeparator2.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator2.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator2.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepRecipients.add(jSeparator2);

        jPanelSepBorder1.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSepBorder1.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelSepBorder1.setOpaque(false);
        jPanelSepBorder1.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelSepRecipients.add(jPanelSepBorder1);

        jPanelNorth.add(jPanelSepRecipients);

        jPanelAttach.setBackground(new java.awt.Color(255, 255, 255));
        jPanelAttach.setMaximumSize(new java.awt.Dimension(32777, 50));
        jPanelAttach.setMinimumSize(new java.awt.Dimension(33, 0));
        jPanelAttach.setPreferredSize(new java.awt.Dimension(177, 50));
        jPanelAttach.setLayout(new javax.swing.BoxLayout(jPanelAttach, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPaneAttach.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPaneAttach.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 5, 3, 5));
        jScrollPaneAttach.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneAttach.setOpaque(false);

        jListAttach.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListAttach.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        jListAttach.setVisibleRowCount(1);
        jScrollPaneAttach.setViewportView(jListAttach);

        jPanelAttach.add(jScrollPaneAttach);

        jPanelNorth.add(jPanelAttach);

        jPanelAttachSepMessage.setBackground(new java.awt.Color(255, 255, 255));
        jPanelAttachSepMessage.setMaximumSize(new java.awt.Dimension(32777, 6));
        jPanelAttachSepMessage.setMinimumSize(new java.awt.Dimension(10, 0));
        jPanelAttachSepMessage.setPreferredSize(new java.awt.Dimension(10, 6));
        jPanelAttachSepMessage.setLayout(new javax.swing.BoxLayout(jPanelAttachSepMessage, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSepBorder2.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSepBorder2.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelSepBorder2.setOpaque(false);
        jPanelSepBorder2.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelAttachSepMessage.add(jPanelSepBorder2);

        jSeparator10.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparator10.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparator10.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelAttachSepMessage.add(jSeparator10);

        jPanelSepBorder3.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSepBorder3.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelSepBorder3.setOpaque(false);
        jPanelSepBorder3.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelAttachSepMessage.add(jPanelSepBorder3);

        jPanelNorth.add(jPanelAttachSepMessage);

        jPanelMessage.add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        jPanelScrollPane.setMinimumSize(new java.awt.Dimension(31, 0));
        jPanelScrollPane.setLayout(new javax.swing.BoxLayout(jPanelScrollPane, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeftSpace1.setBackground(new java.awt.Color(255, 255, 255));
        jPanelLeftSpace1.setMaximumSize(new java.awt.Dimension(5, 10));
        jPanelLeftSpace1.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelLeftSpace1.setOpaque(false);
        jPanelLeftSpace1.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelScrollPane.add(jPanelLeftSpace1);

        jPanelBody.setLayout(new javax.swing.BoxLayout(jPanelBody, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPaneBody.setBorder(null);
        jScrollPaneBody.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneBody.setMinimumSize(new java.awt.Dimension(21, 100));
        jScrollPaneBody.setPreferredSize(new java.awt.Dimension(106, 600));

        jEditorPaneBody.setEditable(false);
        jEditorPaneBody.setBorder(null);
        jEditorPaneBody.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jScrollPaneBody.setViewportView(jEditorPaneBody);

        jPanelBody.add(jScrollPaneBody);

        jPanelScrollPane.add(jPanelBody);

        jPanelRigthSpace2.setBackground(new java.awt.Color(255, 255, 255));
        jPanelRigthSpace2.setMaximumSize(new java.awt.Dimension(5, 10));
        jPanelRigthSpace2.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelRigthSpace2.setOpaque(false);
        jPanelRigthSpace2.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelScrollPane.add(jPanelRigthSpace2);

        jPanelMessage.add(jPanelScrollPane, java.awt.BorderLayout.CENTER);

        jPanelMessageContainer.add(jPanelMessage, java.awt.BorderLayout.CENTER);

        jPanelMessageMain.add(jPanelMessageContainer);

        jSplitPaneMessage.setRightComponent(jPanelMessageMain);

        jSplitPaneFolders.setRightComponent(jSplitPaneMessage);

        jPanelSeparator.add(jSplitPaneFolders);
        jPanelSeparator.add(jSeparator13);

        jPanelCenter.add(jPanelSeparator);

        getContentPane().add(jPanelCenter);

        jPanelSep.setMaximumSize(new java.awt.Dimension(32767, 2));
        jPanelSep.setMinimumSize(new java.awt.Dimension(10, 2));
        jPanelSep.setPreferredSize(new java.awt.Dimension(10, 2));
        getContentPane().add(jPanelSep);

        jPaneStatusBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPaneStatusBar.setMaximumSize(new java.awt.Dimension(32767, 40));
        jPaneStatusBar.setMinimumSize(new java.awt.Dimension(152, 40));
        jPaneStatusBar.setPreferredSize(new java.awt.Dimension(344, 40));
        jPaneStatusBar.setLayout(new javax.swing.BoxLayout(jPaneStatusBar, javax.swing.BoxLayout.X_AXIS));

        jPanelSslCert.setLayout(new javax.swing.BoxLayout(jPanelSslCert, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSslCert.add(jPanel1);

        jLabelHost.setBackground(new java.awt.Color(164, 220, 144));
        jLabelHost.setText("jLabelHost");
        jLabelHost.setOpaque(true);
        jPanelSslCert.add(jLabelHost);

        jPanel2.setMaximumSize(new java.awt.Dimension(5, 10));
        jPanel2.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanel2.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelSslCert.add(jPanel2);

        jButtonHostLock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/24x24/lock_ok.png"))); // NOI18N
        jButtonHostLock.setBorder(null);
        jButtonHostLock.setBorderPainted(false);
        jButtonHostLock.setContentAreaFilled(false);
        jButtonHostLock.setFocusPainted(false);
        jButtonHostLock.setMargin(new java.awt.Insets(0, 2, 0, 2));
        jButtonHostLock.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/24x24/lock_ok_roll_over.png"))); // NOI18N
        jButtonHostLock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonHostLockMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonHostLockMouseExited(evt);
            }
        });
        jButtonHostLock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHostLockActionPerformed(evt);
            }
        });
        jPanelSslCert.add(jButtonHostLock);

        jPaneStatusBar.add(jPanelSslCert);

        jPanelSepVertical2.setMaximumSize(new java.awt.Dimension(22, 26));
        jPanelSepVertical2.setMinimumSize(new java.awt.Dimension(22, 10));
        jPanelSepVertical2.setPreferredSize(new java.awt.Dimension(22, 10));
        jPanelSepVertical2.setLayout(new javax.swing.BoxLayout(jPanelSepVertical2, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeftSep2.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepVertical2.add(jPanelLeftSep2);

        jSeparator19.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator19.setMaximumSize(new java.awt.Dimension(6, 34));
        jSeparator19.setMinimumSize(new java.awt.Dimension(6, 0));
        jSeparator19.setPreferredSize(new java.awt.Dimension(6, 0));
        jPanelSepVertical2.add(jSeparator19);

        jPanelRightSep2.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelRightSep2.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelSepVertical2.add(jPanelRightSep2);

        jPaneStatusBar.add(jPanelSepVertical2);

        jPanelInfo.setMaximumSize(new java.awt.Dimension(98323, 26));
        jPanelInfo.setLayout(new javax.swing.BoxLayout(jPanelInfo, javax.swing.BoxLayout.LINE_AXIS));

        jPanelStorage.setLayout(new javax.swing.BoxLayout(jPanelStorage, javax.swing.BoxLayout.LINE_AXIS));

        jLabelPlan.setText("jLabelPlan");
        jPanelStorage.add(jLabelPlan);

        jLabelSep.setText(" -");
        jPanelStorage.add(jLabelSep);

        jPanel3.setMaximumSize(new java.awt.Dimension(5, 10));
        jPanel3.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanel3.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelStorage.add(jPanel3);

        jLabelStorage.setText("jLabelStorage");
        jPanelStorage.add(jLabelStorage);

        jPanelInfo.add(jPanelStorage);

        jPanelSepVerticalLastLogin.setMaximumSize(new java.awt.Dimension(22, 32767));
        jPanelSepVerticalLastLogin.setMinimumSize(new java.awt.Dimension(22, 10));
        jPanelSepVerticalLastLogin.setPreferredSize(new java.awt.Dimension(22, 10));
        jPanelSepVerticalLastLogin.setLayout(new javax.swing.BoxLayout(jPanelSepVerticalLastLogin, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeftSep1.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepVerticalLastLogin.add(jPanelLeftSep1);

        jSeparator18.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator18.setMaximumSize(new java.awt.Dimension(6, 34));
        jSeparator18.setMinimumSize(new java.awt.Dimension(6, 0));
        jSeparator18.setPreferredSize(new java.awt.Dimension(6, 0));
        jPanelSepVerticalLastLogin.add(jSeparator18);

        jPanelRightSep1.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelRightSep1.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelSepVerticalLastLogin.add(jPanelRightSep1);

        jPanelInfo.add(jPanelSepVerticalLastLogin);

        jPanelLastLogin.setLayout(new javax.swing.BoxLayout(jPanelLastLogin, javax.swing.BoxLayout.LINE_AXIS));

        jLabelLastLogin.setText("jLabelLastLogin");
        jPanelLastLogin.add(jLabelLastLogin);

        jPanelSepLabels.setMaximumSize(new java.awt.Dimension(5, 10));
        jPanelSepLabels.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanelSepLabels.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanelLastLogin.add(jPanelSepLabels);

        jButtonDetail.setForeground(new java.awt.Color(0, 0, 255));
        jButtonDetail.setText("jButtonDetail");
        jButtonDetail.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButtonDetail.setBorderPainted(false);
        jButtonDetail.setContentAreaFilled(false);
        jButtonDetail.setFocusPainted(false);
        jButtonDetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonDetailMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonDetailMouseExited(evt);
            }
        });
        jButtonDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetailActionPerformed(evt);
            }
        });
        jPanelLastLogin.add(jButtonDetail);

        jPanelInfo.add(jPanelLastLogin);

        jPaneStatusBar.add(jPanelInfo);

        jPanelButtonsNav.setMaximumSize(new java.awt.Dimension(32767, 25));
        jPanelButtonsNav.setMinimumSize(new java.awt.Dimension(10, 25));
        jPanelButtonsNav.setPreferredSize(new java.awt.Dimension(32767, 25));
        jPanelButtonsNav.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 5));

        jPanel4.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel4.setOpaque(false);
        jPanelButtonsNav.add(jPanel4);

        jLabelNbElements.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelNbElements.setText("jLabelNbElements");
        jLabelNbElements.setMaximumSize(new java.awt.Dimension(300, 16));
        jLabelNbElements.setMinimumSize(new java.awt.Dimension(300, 16));
        jLabelNbElements.setPreferredSize(new java.awt.Dimension(120, 16));
        jPanelButtonsNav.add(jLabelNbElements);

        jButtonPrev.setBorder(null);
        jButtonPrev.setBorderPainted(false);
        jButtonPrev.setContentAreaFilled(false);
        jButtonPrev.setFocusPainted(false);
        jButtonPrev.setLabel("<Prev");
        jButtonPrev.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonPrev.setPreferredSize(new java.awt.Dimension(55, 14));
        jButtonPrev.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonPrevMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonPrevMouseExited(evt);
            }
        });
        jButtonPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrevActionPerformed(evt);
            }
        });
        jPanelButtonsNav.add(jButtonPrev);

        jButtonNext.setBorder(null);
        jButtonNext.setBorderPainted(false);
        jButtonNext.setContentAreaFilled(false);
        jButtonNext.setFocusPainted(false);
        jButtonNext.setLabel("Next>");
        jButtonNext.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonNext.setPreferredSize(new java.awt.Dimension(55, 14));
        jButtonNext.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonNextMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonNextMouseExited(evt);
            }
        });
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });
        jPanelButtonsNav.add(jButtonNext);

        jPanel6.setMaximumSize(new java.awt.Dimension(5, 10));
        jPanel6.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanel6.setOpaque(false);
        jPanel6.setPreferredSize(new java.awt.Dimension(5, 10));
        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 5));
        jPanelButtonsNav.add(jPanel6);

        jPaneStatusBar.add(jPanelButtonsNav);

        getContentPane().add(jPaneStatusBar);

        jMenuFile.setText("File");

        jMenuItemNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/mail_write.png"))); // NOI18N
        jMenuItemNew.setText("jMenuItemNew");
        jMenuItemNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemNew);

        jMenuItemNewFolder.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemNewFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/folder_open_plus.png"))); // NOI18N
        jMenuItemNewFolder.setText("jMenuItemNewFolder");
        jMenuItemNewFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewFolderActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemNewFolder);
        jMenuFile.add(jSeparator11);

        jMenuItemPrint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/printer.png"))); // NOI18N
        jMenuItemPrint.setText("jMenuItemPrint");
        jMenuItemPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPrintActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemPrint);
        jMenuFile.add(jSeparator7);

        jMenuItemClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/close.png"))); // NOI18N
        jMenuItemClose.setText("jMenuItemClose");
        jMenuItemClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCloseActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemClose);

        jMenuItemQuit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/log_out.png"))); // NOI18N
        jMenuItemQuit.setText("jMenuItemQuit");
        jMenuItemQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemQuitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemQuit);

        jMenuBar1.add(jMenuFile);

        jMenuAccounts.setText("Accounts");

        jMenuConnectToAccount.setText("Connect to Account");

        jMenuItemConnectToAccount.setText("Go");
        jMenuItemConnectToAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemConnectToAccountActionPerformed(evt);
            }
        });
        jMenuConnectToAccount.add(jMenuItemConnectToAccount);

        jMenuAccounts.add(jMenuConnectToAccount);
        jMenuAccounts.add(jSeparator21);

        jMenuBar1.add(jMenuAccounts);

        jMenuMessage.setText("Message");

        jMenuItemGetNewMessage.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jMenuItemGetNewMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/refresh.png"))); // NOI18N
        jMenuItemGetNewMessage.setText("jMenuItemGetNewMessage");
        jMenuItemGetNewMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGetNewMessageActionPerformed(evt);
            }
        });
        jMenuMessage.add(jMenuItemGetNewMessage);
        jMenuMessage.add(jSeparator8);

        jMenuItemReply.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemReply.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/mail_reply.png"))); // NOI18N
        jMenuItemReply.setText("jMenuItemReply");
        jMenuItemReply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemReplyActionPerformed(evt);
            }
        });
        jMenuMessage.add(jMenuItemReply);

        jMenuItemReplyAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/mail_reply_all.png"))); // NOI18N
        jMenuItemReplyAll.setText("jMenuItemReplyAll");
        jMenuItemReplyAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemReplyAllActionPerformed(evt);
            }
        });
        jMenuMessage.add(jMenuItemReplyAll);

        jMenuItemFoward.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemFoward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/mail_forward.png"))); // NOI18N
        jMenuItemFoward.setText("jMenuItemFoward");
        jMenuItemFoward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFowardActionPerformed(evt);
            }
        });
        jMenuMessage.add(jMenuItemFoward);
        jMenuMessage.add(jSeparator9);

        jMenuItemMarkRead.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/mail_open.png"))); // NOI18N
        jMenuItemMarkRead.setText("jMenuItemMarkRead");
        jMenuItemMarkRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMarkReadActionPerformed(evt);
            }
        });
        jMenuMessage.add(jMenuItemMarkRead);

        jMenuItemMarkUnread.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/mail.png"))); // NOI18N
        jMenuItemMarkUnread.setText("jMenuItemMarkUnread");
        jMenuItemMarkUnread.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMarkUnreadActionPerformed(evt);
            }
        });
        jMenuMessage.add(jMenuItemMarkUnread);

        jMenuItemMove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/mail_exchange.png"))); // NOI18N
        jMenuItemMove.setText("jMenuItemMove");
        jMenuItemMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMoveActionPerformed(evt);
            }
        });
        jMenuMessage.add(jMenuItemMove);

        jMenuItemDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/delete.png"))); // NOI18N
        jMenuItemDelete.setText("jMenuItemDelete");
        jMenuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteActionPerformed(evt);
            }
        });
        jMenuMessage.add(jMenuItemDelete);
        jMenuMessage.add(jSeparator15);

        jMenuItemSearch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/binocular.png"))); // NOI18N
        jMenuItemSearch.setText("jMenuItemSearch");
        jMenuItemSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSearchActionPerformed(evt);
            }
        });
        jMenuMessage.add(jMenuItemSearch);

        jMenuBar1.add(jMenuMessage);

        jMenuSettings.setText("jMenuSettings");

        jMenuItemUserSettings.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        jMenuItemUserSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/window_gear.png"))); // NOI18N
        jMenuItemUserSettings.setText("jMenuItemUserSettings");
        jMenuItemUserSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUserSettingsActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemUserSettings);

        jMenuItemChangePassphrase.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/key.png"))); // NOI18N
        jMenuItemChangePassphrase.setText("jMenuItemChangePassphrase");
        jMenuItemChangePassphrase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChangePassphraseActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemChangePassphrase);

        jMenuItemPassphraseRecoverySettings.setText("jMenuItemPassphraseRecoverySettings");
        jMenuItemPassphraseRecoverySettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPassphraseRecoverySettingsActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemPassphraseRecoverySettings);

        jMenuItemForgetPassphrase.setText("jMenuItemForgetPassphrase");
        jMenuItemForgetPassphrase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemForgetPassphraseActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemForgetPassphrase);
        jMenuSettings.add(jSeparator12);

        jMenuItemProxySettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/server_network.png"))); // NOI18N
        jMenuItemProxySettings.setText("jMenuItemProxySettings");
        jMenuItemProxySettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProxySettingsActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemProxySettings);

        jMenuItemAutoresponder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/telephone.png"))); // NOI18N
        jMenuItemAutoresponder.setText("jMenuItemAutoresponder");
        jMenuItemAutoresponder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAutoresponderActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemAutoresponder);
        jMenuSettings.add(jSeparator17);

        jMenuItemAddPhoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/photo_portrait.png"))); // NOI18N
        jMenuItemAddPhoto.setText("Add Photo");
        jMenuItemAddPhoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddPhotoActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemAddPhoto);

        jMenuItemDeletePhoto.setText("Delete Photo");
        jMenuItemDeletePhoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeletePhotoActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemDeletePhoto);
        jMenuSettings.add(jSeparator16);

        jMenuItemUpgrade.setText("jMenuItemUpgrade");
        jMenuItemUpgrade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUpgradeActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemUpgrade);

        jMenuItemActivateSubscription.setText("jMenuItemActivateSubscription");
        jMenuItemActivateSubscription.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemActivateSubscriptionActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemActivateSubscription);
        jMenuSettings.add(jSeparator22);

        jMenuItemDeleteAccount.setText("jMenuItemDeleteAccount");
        jMenuItemDeleteAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteAccountActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemDeleteAccount);

        jMenuBar1.add(jMenuSettings);

        jMenu2FA.setText("2FA");

        jMenuItemDouble2FaAccountQRcode.setText("jMenuItemDouble2FaAccountQRcode");
        jMenuItemDouble2FaAccountQRcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDouble2FaAccountQRcodeActionPerformed(evt);
            }
        });
        jMenu2FA.add(jMenuItemDouble2FaAccountQRcode);

        jMenuItemDouble2FaActivation.setText("jMenuItemDouble2FaActivation");
        jMenuItemDouble2FaActivation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDouble2FaActivationActionPerformed(evt);
            }
        });
        jMenu2FA.add(jMenuItemDouble2FaActivation);

        jMenuItemDouble2faHelp.setText("jMenuItemDouble2faHelp");
        jMenuItemDouble2faHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDouble2faHelpActionPerformed(evt);
            }
        });
        jMenu2FA.add(jMenuItemDouble2faHelp);

        jMenuBar1.add(jMenu2FA);

        jMenuContacts.setText("jMenuContacts");

        jMenuItemImportAddrBook.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemImportAddrBook.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/book_telephone.png"))); // NOI18N
        jMenuItemImportAddrBook.setText("jMenuItemImportAddrBook");
        jMenuItemImportAddrBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemImportAddrBookActionPerformed(evt);
            }
        });
        jMenuContacts.add(jMenuItemImportAddrBook);

        jMenuItemAddressBook.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
        jMenuItemAddressBook.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/businesspeople.png"))); // NOI18N
        jMenuItemAddressBook.setText("jMenuItemAddressBook");
        jMenuItemAddressBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddressBookActionPerformed(evt);
            }
        });
        jMenuContacts.add(jMenuItemAddressBook);

        jMenuBar1.add(jMenuContacts);

        jMenuWindow.setText("jMenuWindow");

        jMenuOrientation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/window_sidebar.png"))); // NOI18N
        jMenuOrientation.setText("jMenuOrientation");

        jRadioButtonMenuItemBottom.setText("Bottom");
        jRadioButtonMenuItemBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/window_split_ver.png"))); // NOI18N
        jRadioButtonMenuItemBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemBottomActionPerformed(evt);
            }
        });
        jMenuOrientation.add(jRadioButtonMenuItemBottom);

        jRadioButtonMenuItemRight.setText("Right");
        jRadioButtonMenuItemRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/window_split_hor.png"))); // NOI18N
        jRadioButtonMenuItemRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemRightActionPerformed(evt);
            }
        });
        jMenuOrientation.add(jRadioButtonMenuItemRight);

        jRadioButtonMenuItemPaneInactive.setText("Inactive");
        jRadioButtonMenuItemPaneInactive.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/window.png"))); // NOI18N
        jRadioButtonMenuItemPaneInactive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemPaneInactiveActionPerformed(evt);
            }
        });
        jMenuOrientation.add(jRadioButtonMenuItemPaneInactive);

        jMenuWindow.add(jMenuOrientation);

        jMenuFolderSection.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/folders.png"))); // NOI18N
        jMenuFolderSection.setText("jMenuFolderSection");

        jRadioButtonMenuItemNormal.setSelected(true);
        jRadioButtonMenuItemNormal.setText("jRadioButtonMenuItemNormal");
        jRadioButtonMenuItemNormal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemNormalActionPerformed(evt);
            }
        });
        jMenuFolderSection.add(jRadioButtonMenuItemNormal);

        jRadioButtonMenuItemFolderInactive.setSelected(true);
        jRadioButtonMenuItemFolderInactive.setText("jRadioButtonMenuItemFolderInactive");
        jRadioButtonMenuItemFolderInactive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemFolderInactiveActionPerformed(evt);
            }
        });
        jMenuFolderSection.add(jRadioButtonMenuItemFolderInactive);

        jMenuWindow.add(jMenuFolderSection);
        jMenuWindow.add(jSeparator20);

        jMenuItemReset.setText("jMenuItemReset");
        jMenuItemReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemResetActionPerformed(evt);
            }
        });
        jMenuWindow.add(jMenuItemReset);

        jMenuBar1.add(jMenuWindow);

        jMenuAbout.setText("jMenuAbout");

        jMenuItemSystemInfo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0));
        jMenuItemSystemInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/speech_balloon_answer.png"))); // NOI18N
        jMenuItemSystemInfo.setText("jMenuItemSystemInfo");
        jMenuItemSystemInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSystemInfoActionPerformed(evt);
            }
        });
        jMenuAbout.add(jMenuItemSystemInfo);

        jMenuItemWhatsNew.setText("jMenuItemWhatsNew");
        jMenuItemWhatsNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemWhatsNewActionPerformed(evt);
            }
        });
        jMenuAbout.add(jMenuItemWhatsNew);

        jMenuItemCheckNewVersion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/recycle.png"))); // NOI18N
        jMenuItemCheckNewVersion.setText("jMenuItemCheckNewVersion");
        jMenuItemCheckNewVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCheckNewVersionActionPerformed(evt);
            }
        });
        jMenuAbout.add(jMenuItemCheckNewVersion);

        jMenuItemAbout.setText("jMenuItemAbout");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuAbout.add(jMenuItemAbout);

        jMenuBar1.add(jMenuAbout);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemMarkReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMarkReadActionPerformed
        markRead(false);
    }//GEN-LAST:event_jMenuItemMarkReadActionPerformed

    private void jMenuItemMarkUnreadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMarkUnreadActionPerformed
        markRead(true);
    }//GEN-LAST:event_jMenuItemMarkUnreadActionPerformed

    private void jButtonNewMessageActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonNewMessageActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new MessageComposer(this, getKeyId(), userNumber, this.passphrase, this.getConnection()).setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }// GEN-LAST:event_jButtonNewMessageActionPerformed

    private void jButtonMoveMessageActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonMoveMessageActionPerformed
        // Get selected message
        setSelectedMessages();
        if (selectedMessages.isEmpty()) {
            // No selected messages => nothing to do
            return;
        }

        List<MessageLocal> listMessages = new ArrayList<MessageLocal>();

        // Build the list of completed messages
        for (int i = 0; i < selectedMessages.size(); i++) {
            int messageId = selectedMessages.get(i);
            MessageLocal messageLocal = getCompletedMessage(messageId);
            listMessages.add(messageLocal);
        }

        // Display dialog to select new folder
        new MessageMover(this, listMessages, true).setVisible(true);
    }// GEN-LAST:event_jButtonMoveMessageActionPerformed

    private void jButtonAddressBookActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonAddressBookActionPerformed
        jMenuItemAddressBookActionPerformed(null);
    }// GEN-LAST:event_jButtonAddressBookActionPerformed

    private void jButtonReplyActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonReplyActionPerformed

        setSelectedMessages();

        for (int i = 0; i < selectedMessages.size(); i++) {
            int messageId = selectedMessages.get(i);
            MessageLocal message = getCompletedMessage(messageId);
            new MessageComposer(thisOne, getKeyId(), userNumber, this.passphrase, this.getConnection(), message,
                    Parms.ACTION_REPLY).setVisible(true);
        }

    }// GEN-LAST:event_jButtonReplyActionPerformed

    private void jButtonReplyAllActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonReplyAllActionPerformed

        setSelectedMessages();

        for (int i = 0; i < selectedMessages.size(); i++) {
            int messageId = selectedMessages.get(i);
            MessageLocal message = getCompletedMessage(messageId);
            new MessageComposer(thisOne, getKeyId(), userNumber, this.passphrase, this.getConnection(), message,
                    Parms.ACTION_REPLY_ALL).setVisible(true);
        }
    }// GEN-LAST:event_jButtonReplyAllActionPerformed

    private void jButtonTransfertActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonTransfertActionPerformed

        setSelectedMessages();

        for (int i = 0; i < selectedMessages.size(); i++) {
            int messageId = selectedMessages.get(i);
            MessageLocal message = getCompletedMessage(messageId);
            new MessageComposer(thisOne, getKeyId(), userNumber, this.passphrase, this.getConnection(), message,
                    Parms.ACTION_FOWARD).setVisible(true);
        }
    }// GEN-LAST:event_jButtonTransfertActionPerformed

    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonRefreshActionPerformed
        getIncomingMessage();
    }// GEN-LAST:event_jButtonRefreshActionPerformed

    private void jButtonDeleteSelectedMessageActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonDeleteSelectedMessageActionPerformed
        deleteSelectedMessage();
    }// GEN-LAST:event_jButtonDeleteSelectedMessageActionPerformed

    private void jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonPrintActionPerformed
        if (selectedMessages == null || selectedMessages.isEmpty()) {
            return;
        }
        print();

    }// GEN-LAST:event_jButtonPrintActionPerformed

    private void jMenuItemQuitActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemQuitActionPerformed
        logout();
    }// GEN-LAST:event_jMenuItemQuitActionPerformed

    private void jMenuItemPrintActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemPrintActionPerformed
        jButtonPrintActionPerformed(evt);
    }// GEN-LAST:event_jMenuItemPrintActionPerformed

    private void jMenuItemGetNewMessageActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemGetNewMessageActionPerformed
        jButtonRefreshActionPerformed(evt);
    }// GEN-LAST:event_jMenuItemGetNewMessageActionPerformed

    private void jMenuItemReplyActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemReplyActionPerformed
        jButtonReplyActionPerformed(evt);
    }// GEN-LAST:event_jMenuItemReplyActionPerformed

    private void jMenuItemReplyAllActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemReplyAllActionPerformed
        jButtonReplyAllActionPerformed(evt);
    }// GEN-LAST:event_jMenuItemReplyAllActionPerformed

    private void jMenuItemFowardActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemFowardActionPerformed
        jButtonTransfertActionPerformed(evt);
    }// GEN-LAST:event_jMenuItemFowardActionPerformed

    private void jMenuItemMoveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemMoveActionPerformed
        jButtonMoveMessageActionPerformed(evt);
    }// GEN-LAST:event_jMenuItemMoveActionPerformed

    private void jMenuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemDeleteActionPerformed
        jButtonDeleteSelectedMessageActionPerformed(evt);
    }// GEN-LAST:event_jMenuItemDeleteActionPerformed

    private void jMenuItemNewFolderActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemNewFolderActionPerformed

        // Do now allow to add a Folder if tree is not enabled
        if (!this.customJtree.isTreeEnabled()) {
            return;
        }
        new TreeNodeAdder(this, connection, this.userNumber, this.customJtree, true).setVisible(true);
    }// GEN-LAST:event_jMenuItemNewFolderActionPerformed

    private void jButtonNewFolderActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonNewFolderActionPerformed
        // Do now allow to add a Folder if tree is not enabled
        if (!this.customJtree.isTreeEnabled()) {
            return;
        }
        new TreeNodeAdder(this, connection, this.userNumber, this.customJtree, true).setVisible(true);
    }// GEN-LAST:event_jButtonNewFolderActionPerformed

    private void jMenuItemNewActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemNewActionPerformed
        jButtonNewMessageActionPerformed(evt);
    }// GEN-LAST:event_jMenuItemNewActionPerformed

    private void jMenuItemUserSettingsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemUserSettingsActionPerformed

        if (userSettingsUpdater != null) {
            userSettingsUpdater.dispose();
        }

        userSettingsUpdater = new UserSettingsUpdater(thisOne, connection, userNumber, getKeyId());
        userSettingsUpdater.setVisible(true);

    }// GEN-LAST:event_jMenuItemUserSettingsActionPerformed

    private void jMenuItemProxySettingsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemProxySettingsActionPerformed
        new FrameProxyParms(thisOne).setVisible(true);
    }// GEN-LAST:event_jMenuItemProxySettingsActionPerformed

    private void jMenuItemChangePassphraseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemChangePassphraseActionPerformed

        if (changePassphrase != null) {
            changePassphrase.dispose();
        }

        changePassphrase = new ChangePassphrase(this, connection, userNumber, false);
        changePassphrase.setVisible(true);

    }// GEN-LAST:event_jMenuItemChangePassphraseActionPerformed

    private void jButtonPrevMouseEntered(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jButtonPrevMouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }// GEN-LAST:event_jButtonPrevMouseEntered

    private void jButtonNextMouseEntered(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jButtonNextMouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }// GEN-LAST:event_jButtonNextMouseEntered

    private void jButtonPrevMouseExited(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jButtonPrevMouseExited
        this.setCursor(Cursor.getDefaultCursor());
    }// GEN-LAST:event_jButtonPrevMouseExited

    private void jButtonNextMouseExited(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jButtonNextMouseExited
        this.setCursor(Cursor.getDefaultCursor());
    }// GEN-LAST:event_jButtonNextMouseExited

    private void jButtonPrevActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonPrevActionPerformed
        offset = offset - limit;
        createTable(false);
    }// GEN-LAST:event_jButtonPrevActionPerformed

    private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonNextActionPerformed
        offset = offset + limit;
        createTable(false);
    }// GEN-LAST:event_jButtonNextActionPerformed

    private void jMenuItemSystemInfoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemSystemInfoActionPerformed

        if (systemPropDisplayer != null) {
            systemPropDisplayer.dispose();
        }

        systemPropDisplayer = new SystemPropDisplayer(this);

    }// GEN-LAST:event_jMenuItemSystemInfoActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemAboutActionPerformed

        if (about != null) {
            about.dispose();
        }

        about = new About(this);

    }// GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuItemWhatsNewActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemWhatsNewActionPerformed
        Desktop desktop = Desktop.getDesktop();
        try {
            String whatsNew = AskForDownloadJframe.getWhatsNewUrl();
            desktop.browse(new URI(whatsNew));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.toString());
        }
    }// GEN-LAST:event_jMenuItemWhatsNewActionPerformed

    private void jMenuItemImportAddrBookActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemImportAddrBookActionPerformed

        if (addressBookImportStart != null) {
            addressBookImportStart.dispose();
        }

        addressBookImportStart = new AddressBookImportStart(this, connection, userNumber);

    }// GEN-LAST:event_jMenuItemImportAddrBookActionPerformed

    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonSearchActionPerformed
        search();
    }// GEN-LAST:event_jButtonSearchActionPerformed

    private void jMenuItemSearchActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemSearchActionPerformed
        search();
    }// GEN-LAST:event_jMenuItemSearchActionPerformed

    private void jMenuItemImportAccountActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemImportAccountActionPerformed
    }// GEN-LAST:event_jMenuItemImportAccountActionPerformed

    private void jMenuItemAddressBookActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemAddressBookActionPerformed
        try {

            if (photoAddressBookUpdaterNew != null) {
                photoAddressBookUpdaterNew.dispose();
            }

            photoAddressBookUpdaterNew = new PhotoAddressBookUpdaterNew(this, this.getConnection(), userNumber);
            photoAddressBookUpdaterNew.setVisible(true);
        } catch (Exception e) {
            JOptionPaneNewCustom.showException(rootPane, e);
        }
    }// GEN-LAST:event_jMenuItemAddressBookActionPerformed

    private void jMenuItemDeleteAccountActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemDeleteAccountActionPerformed

        if (confirmAccountDeleteDialog != null) {
            confirmAccountDeleteDialog.dispose();
        }

        confirmAccountDeleteDialog = new ConfirmAccountDeleteDialog(this, this.userNumber, this.getKeyId(), connection);
        confirmAccountDeleteDialog.setVisible(true);
    }// GEN-LAST:event_jMenuItemDeleteAccountActionPerformed

    private void jMenuItemActivateSubscriptionActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemActivateSubscriptionActionPerformed
        new ActivateSubscriptionDialog(this, userNumber, true, connection).setVisible(true);

    }// GEN-LAST:event_jMenuItemActivateSubscriptionActionPerformed

    private void jButtonHostLockActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonHostLockActionPerformed

        if (connection instanceof AwakeConnection) {
            AwakeConnection awakeConnection = (AwakeConnection) connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
            String host = awakeFileSession.getUrl();
            host = StringUtils.substringBeforeLast(host, "/");
            HttpProxy httpProxy = awakeFileSession.getHttpProxy();

            if (sslCertificateDisplayer != null) {
                sslCertificateDisplayer.dispose();
            }

            KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.buildFromAwakeConnection(awakeConnection);
            ApiMessages apiMessages = new ApiMessages(kawanHttpClient, awakeFileSession.getUsername(),
                    awakeFileSession.getAuthenticationToken());
                   
            try {
                SystemInfoDTO systemInfoDTO = apiMessages.getSystemInfo();
                sslCertificateDisplayer = new SslCertificateDisplayer(this, host, httpProxy, systemInfoDTO);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPaneNewCustom.showException(rootPane, e);
            }
     
        }

    }// GEN-LAST:event_jButtonHostLockActionPerformed

    private void jButtonHostLockMouseEntered(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jButtonHostLockMouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }// GEN-LAST:event_jButtonHostLockMouseEntered

    private void jButtonHostLockMouseExited(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jButtonHostLockMouseExited
        this.setCursor(Cursor.getDefaultCursor());
    }// GEN-LAST:event_jButtonHostLockMouseExited

    private void jMenuItemPassphraseRecoverySettingsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemPassphraseRecoverySettingsActionPerformed
        // 11/09/06 15:01 ABE : No more passphrase recovery
//        if (true) {
//            return;
//        }
//        new PassphraseRecoverySettings(connection, userNumber, userPassphrase).setVisible(true);
    }// GEN-LAST:event_jMenuItemPassphraseRecoverySettingsActionPerformed

    private void jMenuItemForgetPassphraseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemForgetPassphraseActionPerformed

        if (Parms.FEATURE_CACHE_PASSPHRASE) {
            boolean rememberPassphrase = UserPrefManager.getBooleanPreference(UserPrefManager.DO_CACHE_PASSPHRASE);

            if (rememberPassphrase) {
                UserPrefManager.setPreference(UserPrefManager.DO_CACHE_PASSPHRASE, false);
                SocketClient socketClient = new SocketClient();
                socketClient.closeServerSilent();
            }
        }

    }// GEN-LAST:event_jMenuItemForgetPassphraseActionPerformed

    private void jMenuItemAutoresponderActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemAutoresponderActionPerformed

        if (autoResponder != null) {
            autoResponder.dispose();
        }

        autoResponder = new AutoResponder(this, connection, userNumber, keyId, passphrase);
    }// GEN-LAST:event_jMenuItemAutoresponderActionPerformed

    private void jMenuItemUpgradeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemUpgradeActionPerformed
        jButtonBuyActionPerformed(null);
    }// GEN-LAST:event_jMenuItemUpgradeActionPerformed

    private void jButtonDetailMouseEntered(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jButtonDetailMouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }// GEN-LAST:event_jButtonDetailMouseEntered

    private void jButtonDetailMouseExited(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jButtonDetailMouseExited
        this.setCursor(Cursor.getDefaultCursor());
    }// GEN-LAST:event_jButtonDetailMouseExited

    private void jButtonDetailActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonDetailActionPerformed

        if (lastLogin != null) {
            lastLogin.dispose();
        }

        lastLogin = new LastLogin(this, connection, userNumber, keyId);
        lastLogin.setVisible(true);

    }// GEN-LAST:event_jButtonDetailActionPerformed

    private void jButtonBuyActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonBuyActionPerformed

        BuyDialog buyDialog = new BuyDialog(this, connection, userNumber, true);
        buyDialog.setVisible(true);

        if (buyDialog.getNewSubscription() != StoreParms.PRODUCT_FREE) {
            short userSubscription = buyDialog.getNewSubscription();
            SubscriptionLocalStore.setSubscription(userSubscription, userNumber);
        }

    }// GEN-LAST:event_jButtonBuyActionPerformed

    private void jMenuItemAddPhotoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemAddPhotoActionPerformed
        PhotoAdder photoAdder = new PhotoAdder(new javax.swing.JFrame(), connection, keyId, true);
        photoAdder.setVisible(true);
    }// GEN-LAST:event_jMenuItemAddPhotoActionPerformed

    private void jMenuItemDeletePhotoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemDeletePhotoActionPerformed
        PhotoUtil.photoDelete(this, connection, keyId);
    }// GEN-LAST:event_jMenuItemDeletePhotoActionPerformed

    private void jMenuItemCloseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemCloseActionPerformed
        this.close();
    }// GEN-LAST:event_jMenuItemCloseActionPerformed

    private void jMenuItemCheckNewVersionActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemCheckNewVersionActionPerformed

        Thread t = new Thread() {
            @Override
            public void run() {
                String version = net.safester.application.version.Version.VERSION;
                try {
                    NewVersionInstaller.checkIfNewVersion(thisOne, version, false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPaneNewCustom.showException(rootPane, ex);
                }
            }
        };
        t.start();

    }// GEN-LAST:event_jMenuItemCheckNewVersionActionPerformed

    private void jRadioButtonMenuItemBottomActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButtonMenuItemBottomActionPerformed
        radioButtonChange();
    }// GEN-LAST:event_jRadioButtonMenuItemBottomActionPerformed

    private void jRadioButtonMenuItemRightActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButtonMenuItemRightActionPerformed
        radioButtonChange();
    }// GEN-LAST:event_jRadioButtonMenuItemRightActionPerformed

    private void jRadioButtonMenuItemPaneInactiveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButtonMenuItemPaneInactiveActionPerformed
        radioButtonChange();
    }// GEN-LAST:event_jRadioButtonMenuItemPaneInactiveActionPerformed

    private void jMenuItemResetActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemResetActionPerformed
        WindowsReseter.actionResetWindows(this);
    }// GEN-LAST:event_jMenuItemResetActionPerformed

    private void jRadioButtonMenuItemNormalActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButtonMenuItemNormalActionPerformed
        radioButtonChange();
    }// GEN-LAST:event_jRadioButtonMenuItemNormalActionPerformed

    private void jRadioButtonMenuItemFolderInactiveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButtonMenuItemFolderInactiveActionPerformed
        radioButtonChange();
    }// GEN-LAST:event_jRadioButtonMenuItemFolderInactiveActionPerformed

    private void jButtonReply1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonReply1ActionPerformed

        int messageId = selectedMessages.get(0);
        MessageLocal message = getCompletedMessage(messageId);
        new MessageComposer(thisOne, keyId, userNumber, this.passphrase, connection, message, Parms.ACTION_REPLY)
                .setVisible(true);
    }// GEN-LAST:event_jButtonReply1ActionPerformed

    private void jButtonReplyAll1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonReplyAll1ActionPerformed

        int messageId = selectedMessages.get(0);
        MessageLocal message = getCompletedMessage(messageId);
        new MessageComposer(thisOne, keyId, userNumber, this.passphrase, connection, message, Parms.ACTION_REPLY_ALL)
                .setVisible(true);
    }// GEN-LAST:event_jButtonReplyAll1ActionPerformed

    private void jButtonTransfert1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonTransfert1ActionPerformed

        int messageId = selectedMessages.get(0);
        MessageLocal message = getCompletedMessage(messageId);
        new MessageComposer(thisOne, keyId, userNumber, this.passphrase, connection, message, Parms.ACTION_FOWARD)
                .setVisible(true);
    }// GEN-LAST:event_jButtonTransfert1ActionPerformed

    private void jMenuItemDouble2FaAccountQRcodeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemDouble2FaAccountQRcodeActionPerformed

        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            // net.safester.server.auth2fa.awake.Auth2faManagerAwake
            String hexImage = awakeFileSession.call(
                    "net.safester.server.auth2fa.awake.Auth2faManagerAwake.get2faQrCodeImage", userNumber, keyId,
                    awakeFileSession.getAuthenticationToken(), connection);

            // Store the image to a file and pass the file to Double2FaDisplayQrCode
            File file = new File(SystemUtils.USER_HOME + File.separator + "qrcode.png");
            byte[] image = new Hex().decode(hexImage.getBytes());
            FileUtils.writeByteArrayToFile(file, image);

            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            // Double2FaAccountCreator
            // Like PhotoDisplayer with a Decorator
            // Your 2FA Account has been created!
            // Please go to "Activity Status" to enable 2FA for next login.
            Double2FaDisplayQrCode dialog = new Double2FaDisplayQrCode(new javax.swing.JFrame(), this.userNumber,
                    this.keyId, passphrase, file, true, connection);
            dialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, messages.getMessage("double_2fa_can_not_create_account"));
            return;
        }

    }// GEN-LAST:event_jMenuItemDouble2FaAccountQRcodeActionPerformed

    private void jMenuItemDouble2FaActivationActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemDouble2FaActivationActionPerformed

        // Double2FaActivityStatus
        // 2FA status: Enabled / Disabled.
        // lightbulb_on.png
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        String double2FaAccountExistsStr = "true";
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {

            double2FaAccountExistsStr = awakeFileSession.call(
                    "net.safester.server.auth2fa.awake.Auth2faManagerAwake.exists2faAccount", userNumber, keyId,
                    awakeFileSession.getAuthenticationToken(), connection);
            this.setCursor(Cursor.getDefaultCursor());
        } catch (Exception e) {
            e.printStackTrace();
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, messages.getMessage("double_2fa_can_not_check_if_account_exists"));
            return;
        }

        if (!Boolean.parseBoolean(double2FaAccountExistsStr)) {
            String text = messages.getMessage("double_2fa_account_not_exists_1") + CR_LF
                    + messages.getMessage("double_2fa_account_not_exists_2");

            text = text.replace("${0}", keyId);
            String title = messages.getMessage("warning");

            JOptionPane.showMessageDialog(this, text, title, JOptionPane.INFORMATION_MESSAGE);
            return;

        }

        Double2FaActivationStatus dialog = new Double2FaActivationStatus(new javax.swing.JFrame(), userNumber, keyId,
                connection);
        dialog.setVisible(true);

    }// GEN-LAST:event_jMenuItemDouble2FaActivationActionPerformed

    private void jMenuItemDouble2faHelpActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemDouble2faHelpActionPerformed
        String content = HtmlTextUtil.getHtmlHelpContent("2fa_settings_help");

        if (newsFrame != null) {
            newsFrame.dispose();
        }

        newsFrame = new NewsFrame(this, content, messages.getMessage("help"));
    }// GEN-LAST:event_jMenuItemDouble2faHelpActionPerformed

    private void jMenuItemConnectToAccountActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jMenuItemAddAccountActionPerformed
        Login login = new Login(this, null);
        login.setVisible(true);
    }// GEN-LAST:event_jMenuItemAddAccountActionPerformed

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

            Set<UserAccount> userAccounts = new HashSet<>();

            @Override
            public void run() {
                new Main(null, "login@email.net", 0, "passphrase".toCharArray(), StoreParms.PRODUCT_FREE, userAccounts).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupFolderSection;
    private javax.swing.ButtonGroup buttonGroupReadingPane;
    private javax.swing.JButton jButtonAddressBook;
    private javax.swing.JButton jButtonBuy;
    private javax.swing.JButton jButtonDeleteSelectedMessage;
    private javax.swing.JButton jButtonDetail;
    private javax.swing.JButton jButtonHostLock;
    private javax.swing.JButton jButtonMoveMessage;
    private javax.swing.JButton jButtonNewFolder;
    private javax.swing.JButton jButtonNewMessage;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JButton jButtonPrev;
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JButton jButtonReply;
    private javax.swing.JButton jButtonReply1;
    private javax.swing.JButton jButtonReplyAll;
    private javax.swing.JButton jButtonReplyAll1;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JButton jButtonTransfert;
    private javax.swing.JButton jButtonTransfert1;
    private javax.swing.JEditorPane jEditorPaneBody;
    private javax.swing.JLabel jLabelCc;
    private javax.swing.JLabel jLabelDate;
    private javax.swing.JLabel jLabelFrom;
    private javax.swing.JLabel jLabelHost;
    private javax.swing.JLabel jLabelLastLogin;
    private javax.swing.JLabel jLabelNbElements;
    private javax.swing.JLabel jLabelPlan;
    private javax.swing.JLabel jLabelSep;
    private javax.swing.JLabel jLabelStorage;
    private javax.swing.JLabel jLabelTo;
    private javax.swing.JList jListAttach;
    private javax.swing.JMenu jMenu2FA;
    private javax.swing.JMenu jMenuAbout;
    private javax.swing.JMenu jMenuAccounts;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuConnectToAccount;
    private javax.swing.JMenu jMenuContacts;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuFolderSection;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemActivateSubscription;
    private javax.swing.JMenuItem jMenuItemAddPhoto;
    private javax.swing.JMenuItem jMenuItemAddressBook;
    private javax.swing.JMenuItem jMenuItemAutoresponder;
    private javax.swing.JMenuItem jMenuItemChangePassphrase;
    private javax.swing.JMenuItem jMenuItemCheckNewVersion;
    private javax.swing.JMenuItem jMenuItemClose;
    private javax.swing.JMenuItem jMenuItemConnectToAccount;
    private javax.swing.JMenuItem jMenuItemDelete;
    private javax.swing.JMenuItem jMenuItemDeleteAccount;
    private javax.swing.JMenuItem jMenuItemDeletePhoto;
    private javax.swing.JMenuItem jMenuItemDouble2FaAccountQRcode;
    private javax.swing.JMenuItem jMenuItemDouble2FaActivation;
    private javax.swing.JMenuItem jMenuItemDouble2faHelp;
    private javax.swing.JMenuItem jMenuItemForgetPassphrase;
    private javax.swing.JMenuItem jMenuItemFoward;
    private javax.swing.JMenuItem jMenuItemGetNewMessage;
    private javax.swing.JMenuItem jMenuItemImportAddrBook;
    private javax.swing.JMenuItem jMenuItemMarkRead;
    private javax.swing.JMenuItem jMenuItemMarkUnread;
    private javax.swing.JMenuItem jMenuItemMove;
    private javax.swing.JMenuItem jMenuItemNew;
    private javax.swing.JMenuItem jMenuItemNewFolder;
    private javax.swing.JMenuItem jMenuItemPassphraseRecoverySettings;
    private javax.swing.JMenuItem jMenuItemPrint;
    private javax.swing.JMenuItem jMenuItemProxySettings;
    private javax.swing.JMenuItem jMenuItemQuit;
    private javax.swing.JMenuItem jMenuItemReply;
    private javax.swing.JMenuItem jMenuItemReplyAll;
    private javax.swing.JMenuItem jMenuItemReset;
    private javax.swing.JMenuItem jMenuItemSearch;
    private javax.swing.JMenuItem jMenuItemSystemInfo;
    private javax.swing.JMenuItem jMenuItemUpgrade;
    private javax.swing.JMenuItem jMenuItemUserSettings;
    private javax.swing.JMenuItem jMenuItemWhatsNew;
    private javax.swing.JMenu jMenuMessage;
    private javax.swing.JMenu jMenuOrientation;
    private javax.swing.JMenu jMenuSettings;
    private javax.swing.JMenu jMenuWindow;
    private javax.swing.JPanel jPaneStatusBar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanelAttach;
    private javax.swing.JPanel jPanelAttachSepMessage;
    private javax.swing.JPanel jPanelBody;
    private javax.swing.JPanel jPanelButtonsNav;
    private javax.swing.JPanel jPanelCc;
    private javax.swing.JPanel jPanelCcLeft;
    private javax.swing.JPanel jPanelCcRight;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelDate;
    private javax.swing.JPanel jPanelEndProgress;
    private javax.swing.JPanel jPanelFolders;
    private javax.swing.JPanel jPanelFoldersTree;
    private javax.swing.JPanel jPanelFromAndRecip;
    private javax.swing.JPanel jPanelFromNew;
    private javax.swing.JPanel jPanelInfo;
    private javax.swing.JPanel jPanelLastLogin;
    private javax.swing.JPanel jPanelLeft5;
    private javax.swing.JPanel jPanelLeft7;
    private javax.swing.JPanel jPanelLeft8;
    private javax.swing.JPanel jPanelLeftSep1;
    private javax.swing.JPanel jPanelLeftSep2;
    private javax.swing.JPanel jPanelLeftSpace1;
    private javax.swing.JPanel jPanelLogout;
    private javax.swing.JPanel jPanelMessage;
    private javax.swing.JPanel jPanelMessageContainer;
    private javax.swing.JPanel jPanelMessageMain;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelProgressBarBox;
    private javax.swing.JPanel jPanelPush;
    private javax.swing.JPanel jPanelRightSep1;
    private javax.swing.JPanel jPanelRightSep2;
    private javax.swing.JPanel jPanelRigthSpace2;
    private javax.swing.JPanel jPanelScrollPane;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSepAttach1;
    private javax.swing.JPanel jPanelSepBorder;
    private javax.swing.JPanel jPanelSepBorder1;
    private javax.swing.JPanel jPanelSepBorder2;
    private javax.swing.JPanel jPanelSepBorder3;
    private javax.swing.JPanel jPanelSepLabels;
    private javax.swing.JPanel jPanelSepRecip1;
    private javax.swing.JPanel jPanelSepRecip2;
    private javax.swing.JPanel jPanelSepRecipients;
    private javax.swing.JPanel jPanelSepVertical2;
    private javax.swing.JPanel jPanelSepVerticalLastLogin;
    private javax.swing.JPanel jPanelSeparator;
    private javax.swing.JPanel jPanelSslCert;
    private javax.swing.JPanel jPanelStorage;
    private javax.swing.JPanel jPanelSubject;
    private javax.swing.JPanel jPanelTo;
    private javax.swing.JPanel jPanelToLeft;
    private javax.swing.JPanel jPanelToRight;
    private javax.swing.JPanel jPanelToolbar;
    private javax.swing.JPanel jPanelToolbarMain;
    private javax.swing.JPanel jPanelTop;
    private javax.swing.JPanel jPanelTopButtons;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemBottom;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemFolderInactive;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemNormal;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemPaneInactive;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemRight;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPaneAttach;
    private javax.swing.JScrollPane jScrollPaneBody;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JToolBar.Separator jSeparator14;
    private javax.swing.JPopupMenu.Separator jSeparator15;
    private javax.swing.JPopupMenu.Separator jSeparator16;
    private javax.swing.JPopupMenu.Separator jSeparator17;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JSeparator jSeparator19;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator20;
    private javax.swing.JPopupMenu.Separator jSeparator21;
    private javax.swing.JPopupMenu.Separator jSeparator22;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JToolBar.Separator jSeparatorButtonBuy;
    private javax.swing.JSplitPane jSplitPaneFolders;
    private javax.swing.JSplitPane jSplitPaneMessage;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextAreaRecipientsCc;
    private javax.swing.JTextArea jTextAreaRecipientsTo;
    private javax.swing.JTextField jTextFieldDate;
    private javax.swing.JTextField jTextFieldSubject;
    private javax.swing.JTextField jTextFieldUserFrom;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables




}
