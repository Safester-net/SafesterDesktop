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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.lang3.SystemUtils;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.awakefw.sql.api.client.AwakeConnection;

import com.safelogic.utilx.StringMgr;
import com.swing.util.SwingColorUtil;
import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.AttachmentListHandler;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.DesktopWrapper;
import net.safester.application.tool.JTextComponetPopupMenu;
import net.safester.application.tool.ReceivedAttachmentListRenderer;
import net.safester.application.tool.SortedDefaultListModel;
import net.safester.application.tool.UI_Util;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.AppDateFormat;
import net.safester.application.util.EmailUser;
import net.safester.application.util.HtmlTextUtil;
import net.safester.application.util.JEditorPaneLinkDetector;
import net.safester.application.util.JListUtil;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.UserPrefManager;
import net.safester.application.util.Util;
import net.safester.clientserver.PgpKeyPairLocal;
import net.safester.clientserver.util.FileNameConverter;
import net.safester.noobs.clientserver.AttachmentLocal;
import net.safester.noobs.clientserver.GsonUtil;
import net.safester.noobs.clientserver.MessageBodyLocal;
import net.safester.noobs.clientserver.MessageLocal;
import net.safester.noobs.clientserver.RecipientLocal;

public class MessageReader extends javax.swing.JFrame {

    public static boolean DEBUG = false;

    private ClipboardManager clipboardManager;
    private MessagesManager messages = new MessagesManager();
    private MessageLocal message;
    private JFrame parent;
    private Connection connection;
    private int folderId;
    private String keyId;
    private int userNumber;
    private char[] passphrase;
    private JFrame thisOne;
    private AttachmentListHandler attachmentJListHandler;
    private JPopupMenu jListPopupMenu;
    private boolean displayDecrypted = true;
    private String decryptedBody = null;
    private String encryptedBody = null;
    private Color COLOR_DISABLED = new Color(153, 153, 153);

    /**
     * Creates new form MessageReader
     */
    public MessageReader(JFrame theParent,
            Connection theConnection,
            MessageLocal theMessage,
            String keyId,
            int userNumber,
            char[] thePassphrase,
            int theFolderId) {
        this.message = theMessage;
        this.parent = theParent;

        debug("MessageLocal: " + message.toString());

        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection) theConnection).clone();

        this.keyId = keyId;
        this.userNumber = userNumber;
        this.passphrase = thePassphrase;
        this.folderId = theFolderId;
        thisOne = this;
        initComponents();
        initCompany();

    }

    private void initCompany() {
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());

        this.jSeparatorColored1.setForeground(SwingColorUtil.getSeparatorColor());
        this.jSeparatorColored2.setForeground(SwingColorUtil.getSeparatorColor());
                
        //clipboardManager = new ClipboardManager(rootPane);
        this.jMenuFile.setText(messages.getMessage("file"));
        this.jMenuMessage.setText(messages.getMessage("message"));
        this.jMenuItemDelete.setText(messages.getMessage("delete"));
        this.jMenuItemFoward.setText(messages.getMessage("forward"));
        this.jMenuItemNew.setText(messages.getMessage("new_message"));
        this.jMenuItemPrint.setText(messages.getMessage("print"));
        this.jMenuItemReply.setText(messages.getMessage("reply"));
        this.jMenuItemReplyAll.setText(messages.getMessage("reply_all"));
        this.jMenuItemClose.setText(messages.getMessage("close_button"));
        this.jLabelFrom.setText(messages.getMessage("from"));
        this.jLabelTo.setText(messages.getMessage("to"));
        this.jLabelCc.setText(messages.getMessage("cc"));
        this.jLabelSubject.setText(messages.getMessage("subject"));
        this.jLabelDate.setText(messages.getMessage("sent"));

        if (SystemUtils.IS_OS_MAC_OSX) {
            jMenuItemClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        } else {
            jMenuItemClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        }

        this.jButtonDelete.setToolTipText(messages.getMessage("delete"));
        this.jButtonFoward.setToolTipText(messages.getMessage("forward"));
        this.jButtonPrint.setToolTipText(messages.getMessage("print"));
        this.jButtonReply.setToolTipText(messages.getMessage("reply"));
        this.jButtonReplyAll.setToolTipText(messages.getMessage("reply_all"));

        this.jButtonDelete.setText(messages.getMessage("delete"));
        this.jButtonFoward.setText(messages.getMessage("forward"));
        this.jButtonPrint.setText(messages.getMessage("print"));
        this.jButtonReply.setText(" " + messages.getMessage("reply") + " ");
        this.jButtonReplyAll.setText(messages.getMessage("reply_all"));

        this.jButtonDisplayEncrypted.setText(messages.getMessage("display_encrypted_as_stored_on_server"));

        jButtonPrint.setEnabled(message.isPrintable());
        jMenuItemPrint.setEnabled(message.isPrintable());
        jButtonFoward.setEnabled(message.isFowardable());
        jMenuItemFoward.setEnabled(message.isFowardable());

        this.jMenuItemNew.setVisible(false);

        boolean jEditorPaneBodyFocusable = true;
        if (!message.isFowardable() || !message.isPrintable()) {
            jEditorPaneBodyFocusable = false;
        }

        jLabelFrom.setForeground(Main.COLOR_MSG_INFO);
        jLabelDate.setForeground(Main.COLOR_MSG_INFO);
        jLabelTo.setForeground(Main.COLOR_MSG_INFO);
        jLabelCc.setForeground(Main.COLOR_MSG_INFO);
        jLabelSubject.setForeground(Main.COLOR_MSG_INFO);

        jTextAreaRecipientsTo.setLineWrap(true);
        jTextAreaRecipientsTo.setFont(jLabelTo.getFont());
        jTextAreaRecipientsCc.setLineWrap(true);
        jTextAreaRecipientsCc.setFont(jLabelTo.getFont());

        jEditorPaneBody.setFocusable(jEditorPaneBodyFocusable);

        String header = ""; // Keep this for mise au point / display tuning

        String theSubject = message.getSubject();
        theSubject = HtmlConverter.fromHtml(theSubject);
        this.jTextFieldObject.setText(header + theSubject);

        this.setTitle(theSubject);

        String sender = HtmlConverter.fromHtml(message.getSenderUserName());
        sender = header + sender + " <" + message.getSenderUserEmail() + ">";
        sender = Util.removeTrailingSemiColumns(sender);

        this.jTextFieldUserFrom.setText(sender);

        //DateFormat sdf = new SimpleDateFormat(messages.getMessage("date_format"));
        AppDateFormat df = new AppDateFormat();

        String messageDate = df.format(message.getDateMessage());

        this.jTextFieldDate.setText(header + messageDate);

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

        /*
        if (DEBUG) {
            System.out.println("message.getIsSigned(): " + message.getIsSigned());
            JOptionPane.showMessageDialog(parent, text);
        }
         */
        jPanelBody.remove(jScrollPaneBody);

        // create a JEditorPane that renders HTML and defaults to the system font.
        jEditorPaneBody
                = new JEditorPane(new HTMLEditorKit().getContentType(), text);

        this.jEditorPaneBody.setContentType("text/html");
        this.jEditorPaneBody.setEditable(false);

        this.jEditorPaneBody.setBackground(Color.WHITE);

        this.jEditorPaneBody.setText(text);

        setSystemFontToHtmlPane(jEditorPaneBody);
        addHyperLinkDetector();

        //jEditorPaneBody.setBackground(Color.RED);
        jEditorPaneBody.setBorder(new EmptyBorder(5, 8, 5, 8));

        jScrollPaneBody.setViewportView(jEditorPaneBody);
        jPanelBody.add(jScrollPaneBody);

        decryptedBody = text;

        List<RecipientLocal> recipients = message.getRecipientLocal();
        String recipientTo = header;
        String recipientCc = header;

        for (RecipientLocal recipient : recipients) {

            EmailUser emailUser = new EmailUser(recipient.getNameRecipient(), recipient.getEmail());

            // NDP : MessageReader: do not display BCC recipients in CC panel
            if (recipient.getTypeRecipient() == Parms.RECIPIENT_TO) {
                recipientTo += emailUser.getNameAndEmailAddress() + "; ";
            } else if (recipient.getTypeRecipient() == Parms.RECIPIENT_CC) {
                recipientCc += emailUser.getNameAndEmailAddress() + "; ";
            } else {
                // Nothing for BCC: do not display them back.
            }
        }

//        try {
//
//            List<PendingMessageUserLocal> pendingMessageUserLocals = message.getPendingMessageUserLocal();
//
//            for (PendingMessageUserLocal pendingMessageUserLocal : pendingMessageUserLocals) {
//                //int pendingUserId = pendingMessageUserLocal.getPending_user_id();
//                int typeRecipient = pendingMessageUserLocal.getType_recipient();
//
//                if (typeRecipient == Parms.RECIPIENT_TO) {
//                    //recipientTo += "<i>" + pendingUserLocal.getEmail() + "</i>; ";
//                    recipientTo += pendingMessageUserLocal.getEmail() + "; ";
//                } else if (typeRecipient == Parms.RECIPIENT_CC) {
//                    //recipientCc += "<i>" + pendingUserLocal.getEmail() + "</i>; ";
//                    recipientCc += pendingMessageUserLocal.getEmail() + "; ";
//                } else {
//                    // Nothing for BCC: do not display them back.
//                }
//            }
//
//            if (recipientTo.equals(header) && recipientCc.equals(header)) {
//                recipientTo = Parms.UNKNOWN_RECIPIENT;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPaneNewCustom.showException(rootPane, e);
//        }

        recipientTo = Util.removeTrailingSemiColumns(recipientTo);
        recipientCc = Util.removeTrailingSemiColumns(recipientCc);

        // Remove HTML coding
        recipientTo = HtmlConverter.fromHtml(recipientTo);
        recipientCc = HtmlConverter.fromHtml(recipientCc);

        this.jTextAreaRecipientsTo.setText(recipientTo);
        this.jTextAreaRecipientsTo.setCaretPosition(0);
        this.jTextAreaRecipientsCc.setText(recipientCc);
        this.jTextAreaRecipientsCc.setCaretPosition(0);
        
        if (jTextAreaRecipientsCc.getText().length() <= 1) {
            jPanelCc.setVisible(false);
        }

        jScrollPaneAttach.remove(jListAttach);

        //  jPanelAttachSepRecipients.setBackground(Color.white);
        jPanelAttach.setBackground(Color.white);

        buildJListAttachment();

        // This must be done AFTER to, cc, and attach values settings
        setAllPanelsWithtextAreaHeightForMac();

        clipboardManager = new ClipboardManager(rootPane);

        this.requestFocus();

        setTextFieldsPopup();

        SwingUtil.applySwingUpdates(rootPane);

        setLabelBackgroundToFields();

        this.setSize(732, 732);

        //this.setLocationByPlatform(true);
        this.keyListenerAdder();

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

        this.setLocationRelativeTo(parent);
        WindowSettingManager.load(this);

        this.jEditorPaneBody.setCaretPosition(0);
        if (!message.isIntegrityCheck()) {
            JOptionPane.showMessageDialog(rootPane, messages.getMessage("integrity_check_failed"));
        }

        jButtonDisplayEncrypted.putClientProperty("JButton.buttonType", "square");
    }

    private void setLabelBackgroundToFields() {

        jTextFieldUserFrom.setEditable(false);
        jTextAreaRecipientsTo.setEditable(false);
        jTextAreaRecipientsCc.setEditable(false);

        /*
         UIDefaults defaults = UIManager.getDefaults();
         Color color = null;

         if ( UIManager.getLookAndFeel().getName().contains("Nimbus"))
         {
         color = (Color) defaults.get("background");
         }
         else
         {
         color = (Color) defaults.get("Label.background");
         }
        
         Color theBackground = new Color(color.getRed(), color.getGreen(), color.getBlue());

         jTextFieldUserFrom.setBackground(theBackground);
         jTextFieldDate.setBackground(theBackground);
         jTextAreaRecipientsTo.setBackground(theBackground);
         jTextAreaRecipientsCc.setBackground(theBackground);
         * 
         */
        Color theBackground = jPanelMessage.getBackground();

        jTextFieldUserFrom.setBackground(theBackground);
        jTextFieldObject.setBackground(theBackground);
        jTextFieldDate.setBackground(theBackground);
                
        jTextAreaRecipientsTo.setBackground(theBackground);
        jTextAreaRecipientsCc.setBackground(theBackground);
        
        //NDP 13/05/21
        jScrollPaneTo.getViewport().setBackground(theBackground);
        jScrollPaneCc.getViewport().setBackground(theBackground);
        
    }

    private void setAllPanelsWithtextAreaHeightForMac() {
        //debug("jTextAreaRecipientsTo: " + jTextAreaRecipientsTo.getLineCount());

        if (jTextAreaRecipientsTo.getText().length() > MAX_LINE_LENGTH) {
            setPanelWithTextAreaHeightForMac(jPanelTo, INCREASE_FACTOR);
            setPanelWithTextAreaHeightForMac(jPanelToLeft, INCREASE_FACTOR);
            setPanelWithTextAreaHeightForMac(jPanelToRight, INCREASE_FACTOR);
        }

        if (jTextAreaRecipientsCc.getText().length() > MAX_LINE_LENGTH) {
            setPanelWithTextAreaHeightForMac(jPanelCc, INCREASE_FACTOR);
            setPanelWithTextAreaHeightForMac(jPanelCcLeft, INCREASE_FACTOR);
            setPanelWithTextAreaHeightForMac(jPanelCcRight, INCREASE_FACTOR);
        }

        if (jListAttach.getModel().getSize() > 1) {
            setPanelWithTextAreaHeightForMac(jPanelAttach, INCREASE_FACTOR);
        }
    }
    /**
     * Resize a panel with teaxeare for Mac OS, because of long elevators...
     *
     * @param jPanel
     */
    public static int MAX_LINE_LENGTH = 120;
    public static double HEIGHT_REFERENCE = 30;
    public static double INCREASE_FACTOR = 2.40;

    public static void setPanelWithTextAreaHeightForMac(JPanel jPanel, double increase) {
        if (!SystemUtils.IS_OS_MAC) {
            return;
        }

        Dimension dim = null;

        dim = jPanel.getMaximumSize();
        dim.setSize(dim.width, (double) (HEIGHT_REFERENCE * increase));
        jPanel.setMaximumSize(dim);

        dim = jPanel.getMinimumSize();
        dim.setSize(dim.width, (double) (HEIGHT_REFERENCE * increase));
        jPanel.setMinimumSize(dim);

        dim = jPanel.getPreferredSize();
        dim.setSize(dim.width, (double) (HEIGHT_REFERENCE * increase));
        jPanel.setPreferredSize(dim);
    }

    /**
     * Sets a clean font to the html editor pane
     *
     * @param jEditorPane the editor pane with html content
     */
    public static void setSystemFontToHtmlPane(JEditorPane jEditorPane) {

        /*
        if (!SystemUtils.IS_OS_WINDOWS) {
            return;
        }
         */
        MessagesManager messages = new MessagesManager();
        String small = messages.getMessage(messages.getMessage("small"));
        String medium = messages.getMessage(messages.getMessage("medium"));
        String big = messages.getMessage(messages.getMessage("big"));

        String bodyFontSize = UserPrefManager.getPreference(UserPrefManager.FONT_SIZE_BODY);
        if (bodyFontSize == null) {
            bodyFontSize = medium;
        }

        int sizeIncrease = 2; // init to default if any problem
        if (bodyFontSize.equals(small)) {
            sizeIncrease = 0;
        }
        if (bodyFontSize.equals(medium)) {
            sizeIncrease = 2;
        }
        if (bodyFontSize.equals(big)) {
            sizeIncrease = 4;
        }

        // add a CSS rule to force body tags to use the default label font
        // instead of the value in javax.swing.text.html.default.csss
        Font font = UIManager.getFont("Label.font");
        String bodyRule = "body { font-family: " + font.getFamily() + "; " + "font-size: " + (font.getSize() + sizeIncrease) + "pt; }";
        ((HTMLDocument) jEditorPane.getDocument()).getStyleSheet().addRule(bodyRule);
    }

    /**
     * Universal key listener
     */
    private void keyListenerAdder() {
        List<Component> components = SwingUtil.getAllComponants(this);

        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);

            comp.addKeyListener(new KeyAdapter() {
                @Override
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
                this.dispose();
            }
            if (keyCode == KeyEvent.VK_ESCAPE) {
                this.dispose();
            }
        }
    }

    /**
     * Replace the editor pane with a JEditorPaneLinkDetector
     */
    private void addHyperLinkDetector() {
        // Hyperlink listener that will open a new Broser with the given URL
        jEditorPaneBody.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent r) {
                if (r.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    DesktopWrapper.browse(r.getURL());
                }
            }
        });
    }

    private void buildJListAttachment() {

        SortedDefaultListModel model_attachs = new SortedDefaultListModel();
        jListAttach = new JList(model_attachs);
        jListAttach.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        jListAttach.setVisibleRowCount(-1);

        List<AttachmentLocal> attachments = message.getAttachmentLocal();

        jListAttach.setCellRenderer(new ReceivedAttachmentListRenderer(attachments));

        JListUtil.selectItemWhenMouverOver(jListAttach);

        //HACK NDP 17/03/18
        JListUtil.formatSpacing(jListAttach);

        // Add space for macOS and Nimbus
        setListFixedCellWidthForMacOsAndNimbus(jListAttach, attachments);

        if (attachments.isEmpty()) {
            //jScrollPaneAttach.setViewportView(null);
            //jPanelMessage.remove(jScrollPaneAttach);
            jPanelMessage.remove(jPanelAttachSepRecipients);
            jPanelMessage.remove(jPanelAttach);
            jPanelMessage.remove(jPanelSepAttach);
            return;
        }

        for (AttachmentLocal attachment : attachments) {
            //model_attachs.addElement(attachment.getFileName());
            String fileName = attachment.getFileName();
            fileName = HtmlConverter.fromHtml(fileName);
            model_attachs.addElement(fileName);
        }

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

        MessageDecryptor messageDecryptor = null;
        try {
            messageDecryptor = new MessageDecryptor(userNumber, passphrase, connection);
        } catch (SQLException ex) {
            JOptionPaneNewCustom.showException(this, ex);
            return;
        }

        PgpKeyPairLocal pgpKeyPairLocal = messageDecryptor.getKeyPair();
        String privateKeyPgpBlock = pgpKeyPairLocal.getPrivateKeyPgpBlock();

        Map<Integer, Long> attachmentsSize = Main.getAttachmentsSize(attachments);
        int senderUserNumber = message.getSenderUserNumber();
        attachmentJListHandler = new AttachmentListHandler(thisOne, senderUserNumber, jListAttach, attachmentsSize, connection, privateKeyPgpBlock, passphrase);

        jScrollPaneAttach.setViewportView(jListAttach);
    }

    /**
     * Special method for Maxc OS & Nimbus: we need to define the cell width
     * from string max length and add icon size (24) plus size of size display
     * info. Otherwise text is cut with trailing "...".
     *
     * @param jListAttach
     * @param attachments
     */
    public void setListFixedCellWidthForMacOsAndNimbus(JList jListAttach, List<AttachmentLocal> attachments) {

        if (!SystemUtils.IS_OS_MAC && !UI_Util.isNimbus()) {
            return;
        }

        String maxString = "";
        for (AttachmentLocal attachment : attachments) {
            String fileName = attachment.getFileName();

            FileNameConverter fileNameConverter = new FileNameConverter(fileName);
            fileName = fileNameConverter.fromServerName(); // remove before "-"
            fileName = fileName.substring(0, fileName.lastIndexOf(".")); // remove before ".pgp"

            fileName = HtmlConverter.fromHtml(fileName);
            if (fileName.length() > maxString.length()) {
                maxString = fileName;
            }
        }
        FontMetrics fontMetric = new JLabel().getFontMetrics(new Font("Tahoma", Font.PLAIN, 13));

        if (DEBUG) {
            System.out.println();
            System.out.println("fontMetric      : " + fontMetric);
            System.out.println("stringWidth     : " + fontMetric.stringWidth(maxString));
            System.out.println("maxString.length: " + maxString.length());
            System.out.println("maxString       : " + maxString);
        }

        int logoWidth = 24;
        int sizeDisplayWidth = fontMetric.stringWidth(" (1111 Kb)");
        int cellWidth = logoWidth + fontMetric.stringWidth(maxString) + sizeDisplayWidth;

        if (SystemUtils.IS_OS_MAC) {
            cellWidth += 55;
        }

        jListAttach.setFixedCellWidth(cellWidth);
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
        JTextComponetPopupMenu jTextComponentPopupMenu = new JTextComponetPopupMenu(this.getConnection(), this.getParent(), jTextComponent, userNumber, type);
        jTextComponentPopupMenu.showPopupMenu(e);
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

    @Override
    public JFrame getParent() {
        return this.parent;
    }

    /**
     * Display popup menu of the JTable
     *
     * @param e
     */
    public void showJListPopupMenu(MouseEvent e) {
        if (jListAttach.getSelectedIndices().length > 0) {
            jListPopupMenu.show(e.getComponent(),
                    e.getX(), e.getY());
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

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection theConnection) {
        this.connection = theConnection;
    }

    public AttachmentListHandler getAttachmentJListHandler() {
        return attachmentJListHandler;
    }

    public int getUserNumber() {
        return userNumber;
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
        //sep = Util.fillWithHtmlBlanks(sep, 20);

        String printText = "";
        printText += jLabelFrom.getText() + sep + from + "<br>";
        printText += jLabelTo.getText() + sep + to + "<br>";
        printText += jLabelCc.getText() + sep + cc + "<br>";
        printText += jLabelDate.getText() + sep + jTextFieldDate.getText() + "<br>";
        printText += messages.getMessage("subject") + sep + jTextFieldObject.getText() + "<br><br>";
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

    public JFrame getCaller() {
        return this.parent;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        defaultUserNameStore1 = new org.jdesktop.swingx.auth.DefaultUserNameStore();
        jPanelBorderLeft = new javax.swing.JPanel();
        jPanelMain = new javax.swing.JPanel();
        jPanelTopButtons = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButtonReply = new javax.swing.JButton();
        jButtonReplyAll = new javax.swing.JButton();
        jButtonFoward = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButtonPrint = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButtonDelete = new javax.swing.JButton();
        jPanelMessage = new javax.swing.JPanel();
        jPanelTop = new javax.swing.JPanel();
        jPanelSepToolbar = new javax.swing.JPanel();
        jSeparatorColored1 = new javax.swing.JSeparator();
        jPanelInfoMessage = new javax.swing.JPanel();
        jPanelFromNew = new javax.swing.JPanel();
        jPanelLeft5 = new javax.swing.JPanel();
        jLabelFrom = new javax.swing.JLabel();
        jTextFieldUserFrom = new javax.swing.JTextField();
        jPanelDate = new javax.swing.JPanel();
        jPanelSep1 = new javax.swing.JPanel();
        jLabelDate = new javax.swing.JLabel();
        jTextFieldDate = new javax.swing.JTextField();
        jPanelSep2 = new javax.swing.JPanel();
        jPanelTo = new javax.swing.JPanel();
        jPanelToLeft = new javax.swing.JPanel();
        jLabelTo = new javax.swing.JLabel();
        jPanelToRight = new javax.swing.JPanel();
        jScrollPaneTo = new javax.swing.JScrollPane();
        jTextAreaRecipientsTo = new javax.swing.JTextArea();
        jPanelSep = new javax.swing.JPanel();
        jPanelCc = new javax.swing.JPanel();
        jPanelCcLeft = new javax.swing.JPanel();
        jLabelCc = new javax.swing.JLabel();
        jPanelCcRight = new javax.swing.JPanel();
        jScrollPaneCc = new javax.swing.JScrollPane();
        jTextAreaRecipientsCc = new javax.swing.JTextArea();
        jPanelSep3 = new javax.swing.JPanel();
        jPanelSubjectLabel = new javax.swing.JPanel();
        jPanelLeft6 = new javax.swing.JPanel();
        jLabelSubject = new javax.swing.JLabel();
        jTextFieldObject = new javax.swing.JTextField();
        jPanelSep4 = new javax.swing.JPanel();
        jPanelAttachSepRecipients = new javax.swing.JPanel();
        jSeparatorColored2 = new javax.swing.JSeparator();
        jPanelSepAttach1 = new javax.swing.JPanel();
        jPanelAttach = new javax.swing.JPanel();
        jScrollPaneAttach = new javax.swing.JScrollPane();
        jListAttach = new javax.swing.JList();
        jPanelSepAttach = new javax.swing.JPanel();
        jPanelDisplayEncrypted = new javax.swing.JPanel();
        jButtonDisplayEncrypted = new javax.swing.JButton();
        jPanelBody = new javax.swing.JPanel();
        jScrollPaneBody = new javax.swing.JScrollPane();
        jEditorPaneBody = new javax.swing.JEditorPane();
        jPanelBottom = new javax.swing.JPanel();
        jPanelBorderRight = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemNew = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        jMenuItemPrint = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItemClose = new javax.swing.JMenuItem();
        jMenuMessage = new javax.swing.JMenu();
        jMenuItemReply = new javax.swing.JMenuItem();
        jMenuItemReplyAll = new javax.swing.JMenuItem();
        jMenuItemFoward = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuItemDelete = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanelBorderLeft.setMaximumSize(new java.awt.Dimension(10, 10));
        getContentPane().add(jPanelBorderLeft);

        jPanelMain.setLayout(new javax.swing.BoxLayout(jPanelMain, javax.swing.BoxLayout.Y_AXIS));

        jPanelTopButtons.setMaximumSize(new java.awt.Dimension(32767, 51));
        jPanelTopButtons.setLayout(new java.awt.GridLayout(1, 0));

        jToolBar1.setRollover(true);

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

        jButtonFoward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/mail_forward.png"))); // NOI18N
        jButtonFoward.setFocusable(false);
        jButtonFoward.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonFoward.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonFoward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFowardActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonFoward);
        jToolBar1.add(jSeparator1);

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
        jToolBar1.add(jSeparator2);

        jButtonDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/delete.png"))); // NOI18N
        jButtonDelete.setFocusable(false);
        jButtonDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonDelete);

        jPanelTopButtons.add(jToolBar1);

        jPanelMain.add(jPanelTopButtons);

        jPanelMessage.setLayout(new javax.swing.BoxLayout(jPanelMessage, javax.swing.BoxLayout.Y_AXIS));

        jPanelTop.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelTop.setMinimumSize(new java.awt.Dimension(0, 5));
        jPanelTop.setPreferredSize(new java.awt.Dimension(528, 5));

        javax.swing.GroupLayout jPanelTopLayout = new javax.swing.GroupLayout(jPanelTop);
        jPanelTop.setLayout(jPanelTopLayout);
        jPanelTopLayout.setHorizontalGroup(
            jPanelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 528, Short.MAX_VALUE)
        );
        jPanelTopLayout.setVerticalGroup(
            jPanelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelMessage.add(jPanelTop);

        jPanelSepToolbar.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepToolbar.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepToolbar.setPreferredSize(new java.awt.Dimension(0, 10));
        jPanelSepToolbar.setLayout(new javax.swing.BoxLayout(jPanelSepToolbar, javax.swing.BoxLayout.LINE_AXIS));

        jSeparatorColored1.setForeground(new java.awt.Color(102, 102, 255));
        jSeparatorColored1.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparatorColored1.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparatorColored1.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelSepToolbar.add(jSeparatorColored1);

        jPanelMessage.add(jPanelSepToolbar);

        jPanelInfoMessage.setLayout(new javax.swing.BoxLayout(jPanelInfoMessage, javax.swing.BoxLayout.Y_AXIS));

        jPanelFromNew.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelFromNew.setMinimumSize(new java.awt.Dimension(39, 31));
        jPanelFromNew.setPreferredSize(new java.awt.Dimension(465, 31));
        jPanelFromNew.setLayout(new javax.swing.BoxLayout(jPanelFromNew, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft5.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelLeft5.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelLeft5.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelFromNew.add(jPanelLeft5);

        jLabelFrom.setText("From");
        jLabelFrom.setMaximumSize(new java.awt.Dimension(55, 14));
        jLabelFrom.setMinimumSize(new java.awt.Dimension(55, 14));
        jLabelFrom.setPreferredSize(new java.awt.Dimension(55, 14));
        jPanelFromNew.add(jLabelFrom);

        jTextFieldUserFrom.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextFieldUserFrom.setText("jTextFieldUserFrom");
        jTextFieldUserFrom.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jTextFieldUserFrom.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jTextFieldUserFrom.setMinimumSize(new java.awt.Dimension(0, 22));
        jTextFieldUserFrom.setPreferredSize(new java.awt.Dimension(350, 22));
        jPanelFromNew.add(jTextFieldUserFrom);

        jPanelInfoMessage.add(jPanelFromNew);

        jPanelDate.setMaximumSize(new java.awt.Dimension(32767, 31));
        jPanelDate.setMinimumSize(new java.awt.Dimension(38, 31));
        jPanelDate.setPreferredSize(new java.awt.Dimension(144, 31));
        jPanelDate.setLayout(new javax.swing.BoxLayout(jPanelDate, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSep1.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSep1.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelSep1.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelSep1.setLayout(new javax.swing.BoxLayout(jPanelSep1, javax.swing.BoxLayout.LINE_AXIS));
        jPanelDate.add(jPanelSep1);

        jLabelDate.setText("Date");
        jLabelDate.setPreferredSize(new java.awt.Dimension(55, 14));
        jPanelDate.add(jLabelDate);

        jTextFieldDate.setEditable(false);
        jTextFieldDate.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldDate.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextFieldDate.setText("jTextFieldDate");
        jTextFieldDate.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jPanelDate.add(jTextFieldDate);

        jPanelInfoMessage.add(jPanelDate);

        jPanelSep2.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSep2.setMinimumSize(new java.awt.Dimension(0, 5));
        jPanelSep2.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelSep2.setLayout(new javax.swing.BoxLayout(jPanelSep2, javax.swing.BoxLayout.LINE_AXIS));
        jPanelInfoMessage.add(jPanelSep2);

        jPanelTo.setLayout(new javax.swing.BoxLayout(jPanelTo, javax.swing.BoxLayout.LINE_AXIS));

        jPanelToLeft.setMaximumSize(new java.awt.Dimension(50, 30));
        jPanelToLeft.setMinimumSize(new java.awt.Dimension(50, 30));
        jPanelToLeft.setPreferredSize(new java.awt.Dimension(60, 30));
        jPanelToLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        jLabelTo.setText("To");
        jPanelToLeft.add(jLabelTo);

        jPanelTo.add(jPanelToLeft);

        jPanelToRight.setAutoscrolls(true);
        jPanelToRight.setMaximumSize(new java.awt.Dimension(32767, 30));
        jPanelToRight.setMinimumSize(new java.awt.Dimension(21, 30));
        jPanelToRight.setPreferredSize(new java.awt.Dimension(100, 30));
        jPanelToRight.setLayout(new javax.swing.BoxLayout(jPanelToRight, javax.swing.BoxLayout.PAGE_AXIS));

        jScrollPaneTo.setBorder(null);
        jScrollPaneTo.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneTo.setOpaque(false);

        jTextAreaRecipientsTo.setColumns(20);
        jTextAreaRecipientsTo.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jTextAreaRecipientsTo.setRows(1);
        jTextAreaRecipientsTo.setText("jTextAreaRecipientsTo");
        jTextAreaRecipientsTo.setWrapStyleWord(true);
        jTextAreaRecipientsTo.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        jScrollPaneTo.setViewportView(jTextAreaRecipientsTo);

        jPanelToRight.add(jScrollPaneTo);

        jPanelTo.add(jPanelToRight);

        jPanelInfoMessage.add(jPanelTo);

        jPanelSep.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSep.setMinimumSize(new java.awt.Dimension(0, 5));
        jPanelSep.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelSep.setLayout(new javax.swing.BoxLayout(jPanelSep, javax.swing.BoxLayout.LINE_AXIS));
        jPanelInfoMessage.add(jPanelSep);

        jPanelCc.setLayout(new javax.swing.BoxLayout(jPanelCc, javax.swing.BoxLayout.LINE_AXIS));

        jPanelCcLeft.setMaximumSize(new java.awt.Dimension(50, 30));
        jPanelCcLeft.setMinimumSize(new java.awt.Dimension(50, 30));
        jPanelCcLeft.setPreferredSize(new java.awt.Dimension(60, 30));
        jPanelCcLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        jLabelCc.setText("Cc");
        jPanelCcLeft.add(jLabelCc);

        jPanelCc.add(jPanelCcLeft);

        jPanelCcRight.setAutoscrolls(true);
        jPanelCcRight.setMaximumSize(new java.awt.Dimension(32767, 30));
        jPanelCcRight.setMinimumSize(new java.awt.Dimension(21, 30));
        jPanelCcRight.setOpaque(false);
        jPanelCcRight.setPreferredSize(new java.awt.Dimension(100, 30));
        jPanelCcRight.setLayout(new javax.swing.BoxLayout(jPanelCcRight, javax.swing.BoxLayout.PAGE_AXIS));

        jScrollPaneCc.setBorder(null);
        jScrollPaneCc.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneCc.setOpaque(false);

        jTextAreaRecipientsCc.setColumns(20);
        jTextAreaRecipientsCc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jTextAreaRecipientsCc.setRows(1);
        jTextAreaRecipientsCc.setText("jTextAreaRecipientsCc");
        jTextAreaRecipientsCc.setWrapStyleWord(true);
        jTextAreaRecipientsCc.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        jScrollPaneCc.setViewportView(jTextAreaRecipientsCc);

        jPanelCcRight.add(jScrollPaneCc);

        jPanelCc.add(jPanelCcRight);

        jPanelInfoMessage.add(jPanelCc);

        jPanelSep3.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSep3.setMinimumSize(new java.awt.Dimension(0, 5));
        jPanelSep3.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelSep3.setLayout(new javax.swing.BoxLayout(jPanelSep3, javax.swing.BoxLayout.LINE_AXIS));
        jPanelInfoMessage.add(jPanelSep3);

        jPanelSubjectLabel.setMaximumSize(new java.awt.Dimension(32767, 24));
        jPanelSubjectLabel.setMinimumSize(new java.awt.Dimension(111, 24));
        jPanelSubjectLabel.setPreferredSize(new java.awt.Dimension(125, 24));
        jPanelSubjectLabel.setLayout(new javax.swing.BoxLayout(jPanelSubjectLabel, javax.swing.BoxLayout.LINE_AXIS));

        jPanelLeft6.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelLeft6.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelLeft6.setPreferredSize(new java.awt.Dimension(5, 5));
        jPanelSubjectLabel.add(jPanelLeft6);

        jLabelSubject.setText("Subject");
        jLabelSubject.setMaximumSize(new java.awt.Dimension(55, 14));
        jLabelSubject.setMinimumSize(new java.awt.Dimension(55, 14));
        jLabelSubject.setPreferredSize(new java.awt.Dimension(55, 14));
        jPanelSubjectLabel.add(jLabelSubject);

        jTextFieldObject.setEditable(false);
        jTextFieldObject.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldObject.setText("jTextFieldObject");
        jTextFieldObject.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jPanelSubjectLabel.add(jTextFieldObject);

        jPanelInfoMessage.add(jPanelSubjectLabel);

        jPanelSep4.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSep4.setMinimumSize(new java.awt.Dimension(0, 5));
        jPanelSep4.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelSep4.setLayout(new javax.swing.BoxLayout(jPanelSep4, javax.swing.BoxLayout.LINE_AXIS));
        jPanelInfoMessage.add(jPanelSep4);

        jPanelMessage.add(jPanelInfoMessage);

        jPanelAttachSepRecipients.setMaximumSize(new java.awt.Dimension(32767, 6));
        jPanelAttachSepRecipients.setMinimumSize(new java.awt.Dimension(10, 6));
        jPanelAttachSepRecipients.setPreferredSize(new java.awt.Dimension(10, 6));
        jPanelAttachSepRecipients.setLayout(new javax.swing.BoxLayout(jPanelAttachSepRecipients, javax.swing.BoxLayout.LINE_AXIS));

        jSeparatorColored2.setBackground(new java.awt.Color(240, 240, 240));
        jSeparatorColored2.setForeground(new java.awt.Color(102, 102, 255));
        jSeparatorColored2.setMaximumSize(new java.awt.Dimension(32767, 6));
        jSeparatorColored2.setMinimumSize(new java.awt.Dimension(0, 6));
        jSeparatorColored2.setPreferredSize(new java.awt.Dimension(0, 6));
        jPanelAttachSepRecipients.add(jSeparatorColored2);

        jPanelMessage.add(jPanelAttachSepRecipients);

        jPanelSepAttach1.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSepAttach1.setMinimumSize(new java.awt.Dimension(0, 5));
        jPanelSepAttach1.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelSepAttach1.setLayout(new javax.swing.BoxLayout(jPanelSepAttach1, javax.swing.BoxLayout.LINE_AXIS));
        jPanelMessage.add(jPanelSepAttach1);

        jPanelAttach.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanelAttach.setMaximumSize(new java.awt.Dimension(32770, 50));
        jPanelAttach.setMinimumSize(new java.awt.Dimension(33, 50));
        jPanelAttach.setPreferredSize(new java.awt.Dimension(177, 50));
        jPanelAttach.setLayout(new javax.swing.BoxLayout(jPanelAttach, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPaneAttach.setBorder(null);
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

        jPanelMessage.add(jPanelAttach);

        jPanelSepAttach.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelSepAttach.setMinimumSize(new java.awt.Dimension(0, 5));
        jPanelSepAttach.setPreferredSize(new java.awt.Dimension(10, 5));
        jPanelSepAttach.setLayout(new javax.swing.BoxLayout(jPanelSepAttach, javax.swing.BoxLayout.LINE_AXIS));
        jPanelMessage.add(jPanelSepAttach);

        jPanelDisplayEncrypted.setMaximumSize(new java.awt.Dimension(32767, 30));
        jPanelDisplayEncrypted.setMinimumSize(new java.awt.Dimension(126, 30));
        jPanelDisplayEncrypted.setPreferredSize(new java.awt.Dimension(126, 30));
        jPanelDisplayEncrypted.setLayout(new java.awt.BorderLayout());

        jButtonDisplayEncrypted.setForeground(new java.awt.Color(0, 0, 255));
        jButtonDisplayEncrypted.setText("jButtonDisplayEncrypted");
        jButtonDisplayEncrypted.setBorder(null);
        jButtonDisplayEncrypted.setBorderPainted(false);
        jButtonDisplayEncrypted.setContentAreaFilled(false);
        jButtonDisplayEncrypted.setFocusPainted(false);
        jButtonDisplayEncrypted.setMargin(new java.awt.Insets(2, 2, 2, 10));
        jButtonDisplayEncrypted.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonDisplayEncryptedMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonDisplayEncryptedMouseExited(evt);
            }
        });
        jButtonDisplayEncrypted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDisplayEncryptedActionPerformed(evt);
            }
        });
        jPanelDisplayEncrypted.add(jButtonDisplayEncrypted, java.awt.BorderLayout.EAST);

        jPanelMessage.add(jPanelDisplayEncrypted);

        jPanelBody.setLayout(new javax.swing.BoxLayout(jPanelBody, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPaneBody.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneBody.setViewportView(jEditorPaneBody);

        jPanelBody.add(jScrollPaneBody);

        jPanelMessage.add(jPanelBody);

        jPanelMain.add(jPanelMessage);

        jPanelBottom.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelBottom.setMinimumSize(new java.awt.Dimension(0, 10));

        javax.swing.GroupLayout jPanelBottomLayout = new javax.swing.GroupLayout(jPanelBottom);
        jPanelBottom.setLayout(jPanelBottomLayout);
        jPanelBottomLayout.setHorizontalGroup(
            jPanelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelBottomLayout.setVerticalGroup(
            jPanelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelMain.add(jPanelBottom);

        getContentPane().add(jPanelMain);

        jPanelBorderRight.setMaximumSize(new java.awt.Dimension(10, 10));
        getContentPane().add(jPanelBorderRight);

        jMenuFile.setText("File");

        jMenuItemNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/mail_write.png"))); // NOI18N
        jMenuItemNew.setText("jMenuItem1");
        jMenuItemNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemNew);
        jMenuFile.add(jSeparator11);

        jMenuItemPrint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/printer.png"))); // NOI18N
        jMenuItemPrint.setText("jMenuItem2");
        jMenuItemPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPrintActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemPrint);
        jMenuFile.add(jSeparator7);

        jMenuItemClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuItemClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/close.png"))); // NOI18N
        jMenuItemClose.setText("jMenuItem1");
        jMenuItemClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCloseActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemClose);

        jMenuBar1.add(jMenuFile);

        jMenuMessage.setText("Message");

        jMenuItemReply.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemReply.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/mail_reply.png"))); // NOI18N
        jMenuItemReply.setText("jMenuItem2");
        jMenuItemReply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemReplyActionPerformed(evt);
            }
        });
        jMenuMessage.add(jMenuItemReply);

        jMenuItemReplyAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/mail_reply_all.png"))); // NOI18N
        jMenuItemReplyAll.setText("jMenuItem2");
        jMenuItemReplyAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemReplyAllActionPerformed(evt);
            }
        });
        jMenuMessage.add(jMenuItemReplyAll);

        jMenuItemFoward.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemFoward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/mail_forward.png"))); // NOI18N
        jMenuItemFoward.setText("jMenuItem2");
        jMenuItemFoward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFowardActionPerformed(evt);
            }
        });
        jMenuMessage.add(jMenuItemFoward);
        jMenuMessage.add(jSeparator9);

        jMenuItemDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/delete.png"))); // NOI18N
        jMenuItemDelete.setText("jMenuItem2");
        jMenuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteActionPerformed(evt);
            }
        });
        jMenuMessage.add(jMenuItemDelete);

        jMenuBar1.add(jMenuMessage);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonReplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReplyActionPerformed

        new MessageComposer(parent, keyId, userNumber, this.passphrase, connection, message, Parms.ACTION_REPLY).setVisible(true);
    }//GEN-LAST:event_jButtonReplyActionPerformed

    private void jButtonReplyAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReplyAllActionPerformed

        new MessageComposer(parent, keyId, userNumber, this.passphrase, connection, message, Parms.ACTION_REPLY_ALL).setVisible(true);
    }//GEN-LAST:event_jButtonReplyAllActionPerformed

    private void jButtonFowardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFowardActionPerformed

        new MessageComposer(parent, keyId, userNumber, this.passphrase, connection, message, Parms.ACTION_FOWARD).setVisible(true);
    }//GEN-LAST:event_jButtonFowardActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        String text;
        String title = messages.getMessage("warning");

//        if(message.getFolderId() == Parms.OUTBOX_ID){
//
//            text = messages.getMessage("confirm_delete_permanent");
//        }
//        else{
//            text = messages.getMessage("confirm_delete");
//        }
        text = messages.getMessage("confirm_delete");
        int result = JOptionPane.showConfirmDialog(this, text, title, JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.NO_OPTION) {
            return;
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (parent instanceof Main) {
            ((Main) parent).deleteMessage(message.getMessageId(), folderId);
            this.setCursor(Cursor.getDefaultCursor());
            this.dispose();

        }
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintActionPerformed
        print();
    }//GEN-LAST:event_jButtonPrintActionPerformed

    private void jMenuItemPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPrintActionPerformed
        jButtonPrintActionPerformed(evt);
}//GEN-LAST:event_jMenuItemPrintActionPerformed

    private void jMenuItemCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCloseActionPerformed
        this.dispose();
}//GEN-LAST:event_jMenuItemCloseActionPerformed

    private void jMenuItemReplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemReplyActionPerformed
        jButtonReplyActionPerformed(evt);
}//GEN-LAST:event_jMenuItemReplyActionPerformed

    private void jMenuItemReplyAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemReplyAllActionPerformed
        jButtonReplyAllActionPerformed(evt);
}//GEN-LAST:event_jMenuItemReplyAllActionPerformed

    private void jMenuItemFowardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFowardActionPerformed
}//GEN-LAST:event_jMenuItemFowardActionPerformed

    private void jMenuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteActionPerformed
        jButtonDeleteActionPerformed(evt);
}//GEN-LAST:event_jMenuItemDeleteActionPerformed

    private void jMenuItemNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewActionPerformed
        new MessageComposer(parent, keyId, userNumber, passphrase, connection);
    }//GEN-LAST:event_jMenuItemNewActionPerformed

    private void jButtonDisplayEncryptedMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonDisplayEncryptedMouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jButtonDisplayEncryptedMouseEntered

    private void jButtonDisplayEncryptedMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonDisplayEncryptedMouseExited
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_jButtonDisplayEncryptedMouseExited

    private void jButtonDisplayEncryptedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDisplayEncryptedActionPerformed

        if (displayDecrypted) {
            this.jButtonDisplayEncrypted.setText(messages.getMessage("display_in_clear"));

            if (encryptedBody == null) {
                try {

                    MessageBodyLocal messageBodyLocal = new MessageBodyLocal();
                    messageBodyLocal.setMessageId(message.getMessageId());
                    messageBodyLocal.setDateMessage(message.getDateMessage());
                    messageBodyLocal.setSenderUserNumber(message.getSenderUserNumber());
                    String jsonString = GsonUtil.messageBodyLocalToGson(messageBodyLocal);
                    AwakeFileSession awakeFileSession = ((AwakeConnection) connection).getAwakeFileSession();

                    jsonString = awakeFileSession.call("net.safester.server.hosts.MessageReaderHost.getMessageBodyOnly", message.getSenderUserNumber(), jsonString);
                    messageBodyLocal = GsonUtil.gsonToMessageBodyLocal(jsonString);
                    encryptedBody = messageBodyLocal.getBody();

                } catch (Exception ex) {
                    JOptionPaneNewCustom.showException(this, ex);
                    return;
                }
            }

            //Font courrier = new java.awt.Font("Courrier", 1, 11);
            //jEditorPaneBody.setFont(courrier);
            encryptedBody = HtmlTextUtil.formatWithBr(encryptedBody);
            jEditorPaneBody.setText(encryptedBody);
        } else {
            this.jButtonDisplayEncrypted.setText(messages.getMessage("display_encrypted_as_stored_on_server"));
            jEditorPaneBody.setText(decryptedBody);
        }

        displayDecrypted = !displayDecrypted;

        // These 2 stupid lines : only to Force to diplay top of file first
        jEditorPaneBody.moveCaretPosition(0);
        jEditorPaneBody.setSelectionEnd(0);

    }//GEN-LAST:event_jButtonDisplayEncryptedActionPerformed

    private void debug(String string) {
        System.out.println(new Date() + " " + MessageReader.class.getSimpleName() + " " + string);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                //new MessageReader().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.auth.DefaultUserNameStore defaultUserNameStore1;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonDisplayEncrypted;
    private javax.swing.JButton jButtonFoward;
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JButton jButtonReply;
    private javax.swing.JButton jButtonReplyAll;
    private javax.swing.JEditorPane jEditorPaneBody;
    private javax.swing.JLabel jLabelCc;
    private javax.swing.JLabel jLabelDate;
    private javax.swing.JLabel jLabelFrom;
    private javax.swing.JLabel jLabelSubject;
    private javax.swing.JLabel jLabelTo;
    private javax.swing.JList jListAttach;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItemClose;
    private javax.swing.JMenuItem jMenuItemDelete;
    private javax.swing.JMenuItem jMenuItemFoward;
    private javax.swing.JMenuItem jMenuItemNew;
    private javax.swing.JMenuItem jMenuItemPrint;
    private javax.swing.JMenuItem jMenuItemReply;
    private javax.swing.JMenuItem jMenuItemReplyAll;
    private javax.swing.JMenu jMenuMessage;
    private javax.swing.JPanel jPanelAttach;
    private javax.swing.JPanel jPanelAttachSepRecipients;
    private javax.swing.JPanel jPanelBody;
    private javax.swing.JPanel jPanelBorderLeft;
    private javax.swing.JPanel jPanelBorderRight;
    private javax.swing.JPanel jPanelBottom;
    private javax.swing.JPanel jPanelCc;
    private javax.swing.JPanel jPanelCcLeft;
    private javax.swing.JPanel jPanelCcRight;
    private javax.swing.JPanel jPanelDate;
    private javax.swing.JPanel jPanelDisplayEncrypted;
    private javax.swing.JPanel jPanelFromNew;
    private javax.swing.JPanel jPanelInfoMessage;
    private javax.swing.JPanel jPanelLeft5;
    private javax.swing.JPanel jPanelLeft6;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelMessage;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSep3;
    private javax.swing.JPanel jPanelSep4;
    private javax.swing.JPanel jPanelSepAttach;
    private javax.swing.JPanel jPanelSepAttach1;
    private javax.swing.JPanel jPanelSepToolbar;
    private javax.swing.JPanel jPanelSubjectLabel;
    private javax.swing.JPanel jPanelTo;
    private javax.swing.JPanel jPanelToLeft;
    private javax.swing.JPanel jPanelToRight;
    private javax.swing.JPanel jPanelTop;
    private javax.swing.JPanel jPanelTopButtons;
    private javax.swing.JScrollPane jScrollPaneAttach;
    private javax.swing.JScrollPane jScrollPaneBody;
    private javax.swing.JScrollPane jScrollPaneCc;
    private javax.swing.JScrollPane jScrollPaneTo;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JSeparator jSeparatorColored1;
    private javax.swing.JSeparator jSeparatorColored2;
    private javax.swing.JTextArea jTextAreaRecipientsCc;
    private javax.swing.JTextArea jTextAreaRecipientsTo;
    private javax.swing.JTextField jTextFieldDate;
    private javax.swing.JTextField jTextFieldObject;
    private javax.swing.JTextField jTextFieldUserFrom;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

}
