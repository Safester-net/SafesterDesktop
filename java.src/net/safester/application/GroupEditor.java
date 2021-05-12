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
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.awakefw.sql.api.client.AwakeConnection;

import com.swing.util.SwingUtil;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.photo.GroupListNew;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.util.EmailUser;
import net.safester.application.util.HtmlTextUtil;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.EmailGroupTransfert;
import net.safester.clientserver.GroupMemberCache;
import net.safester.clientserver.GroupMemberListTransfert;
import net.safester.clientserver.holder.GroupHolder;
import net.safester.noobs.clientserver.EmailGroupLocal;
import net.safester.noobs.clientserver.GroupMemberLocal;

/**
 *
 * @author Alexandre Becquereau
 */
public class GroupEditor extends javax.swing.JFrame {

    public static boolean DEBUG = false;
    
    private static final String CR_LF = System.getProperty("line.separator");
    public static final int BUTTON_ADD = 3;
    private ClipboardManager clipboard;
    private MessagesManager messagesManager = new MessagesManager();
    private boolean isSaved = true;
    private Connection connection;
    private int userNumber;
    private int groupId = -1;
    private JFrame caller;

    /** Creates new form GroupCreator */
    public GroupEditor() {
        initComponents();
    }

    /**
     * Constructor for group creation
     * @param caller
     * @param theConnection
     * @param theUserNumber
     */
    public GroupEditor(JFrame caller, Connection theConnection, int theUserNumber) {
        if (theConnection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        if (theUserNumber < 0) {
            throw new IllegalArgumentException("Invalid userNumber: " + theUserNumber);
        }

        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();

        this.userNumber = theUserNumber;
        this.caller = caller;
        initComponents();
        initCompany();
    }

    /**
     * Constructor for group edition
     * @param caller
     * @param theConnection
     * @param theUserNumber
     * @param groupId
     */
    public GroupEditor(JFrame caller, Connection theConnection, int theUserNumber, int groupId) {
        this(caller, theConnection, theUserNumber);
        this.groupId = groupId;
        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            loadData();
            this.setCursor(Cursor.getDefaultCursor());
        } catch (Exception e) {
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPaneNewCustom.showException(rootPane, e);
        }
    }

    /**
     * Build group from user input
     * @return
     * @throws IOException
     */
    private EmailGroupLocal buildGroup() throws IOException {
       
        String groupName = jTextFieldGroupName.getText();
        String members = jTextAreaMembers.getText();
        List<GroupMemberLocal> membersList = buildEmailList(members);
        EmailGroupLocal emailGroupLocal = new EmailGroupLocal();
        emailGroupLocal.setUserNumber(userNumber);
        emailGroupLocal.setName(groupName);
        emailGroupLocal.setMembers(membersList);
        return emailGroupLocal;
    }

    /**
     * Load data of group to be edited
     * @throws Exception
     */
    private void loadData() throws Exception {
        
        //EmailGroupTransfert emailGroupTransfert = new EmailGroupTransfert(connection, userNumber, groupId);
        //EmailGroupLocal emailGroupLocal = emailGroupTransfert.get();
        //this.jTextFieldGroupName.setText(emailGroupLocal.getName()); 
        
        GroupHolder groupHolder = new GroupHolder(connection, userNumber);
	Map<Integer, String> groups = groupHolder.getMap();
        String groupName = groups.get(groupId);        
        this.jTextFieldGroupName.setText(groupName);

        GroupMemberListTransfert groupMemberListTransfert = new GroupMemberListTransfert(connection, userNumber, groupId);
        List<GroupMemberLocal> members = groupMemberListTransfert.getList();

        String memberList = "";
        for (GroupMemberLocal member : members) {
            String memberStr = member.getName() + " <" + member.getEmail() + ">";
            memberList += memberStr + CR_LF;
        }
        
        if (SystemUtils.IS_OS_MAC_OSX) {
            jMenuItemClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        } else {
            jMenuItemClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        }
        
        this.jTextAreaMembers.setBackground(Color.WHITE);
        
        this.jTextAreaMembers.setText(memberList);
        this.setTitle(groupName);
        this.jButtonOk.setEnabled(true);

        this.isSaved = true;
    }

    /**
     * Build the list of members
     * @param members
     * @param membersEmail
     * @throws IOException
     */
    private List<GroupMemberLocal> buildEmailList(String members) throws IOException {
        List<GroupMemberLocal> membersList = new ArrayList<GroupMemberLocal>();
        StringReader stringReader = new StringReader(members);
        BufferedReader bufferedReader = new BufferedReader(stringReader);
        String line = bufferedReader.readLine();
        while (line != null) {
            if(line.length() <1){
                line = bufferedReader.readLine();
                continue;
            }
            EmailUser emailUser = new EmailUser(line);
            String email = emailUser.getEmailAddress();
            String name = emailUser.getName();
            GroupMemberLocal groupMemberLocal = new GroupMemberLocal();
            groupMemberLocal.setEmail(email);
            groupMemberLocal.setName(name);
            membersList.add(groupMemberLocal);
            line = bufferedReader.readLine();
        }
        return membersList;
    }

    private void initCompany() {

        clipboard = new ClipboardManager(rootPane);
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());

        this.setTitle(messagesManager.getMessage("new_group"));

        this.jButtonOk.setEnabled(false);
        this.jMenuItemSave.setText(messagesManager.getMessage("save_changes"));
        this.jMenuItemClose.setText(messagesManager.getMessage("close"));
        this.jMenuItemAddContact.setText(messagesManager.getMessage("add_contact"));
        this.jLabelGroupName.setText(messagesManager.getMessage("group_name"));
        this.jLabelGroupMembers.setText(messagesManager.getMessage("group_members"));
        this.jTextFieldGroupName.setText(null);
        this.jTextAreaMembers.setText(null);
        this.jButtonOk.setText(messagesManager.getMessage("ok"));
        this.jButtonCancel.setText(messagesManager.getMessage("cancel"));
        this.jButtonSave.setText(messagesManager.getMessage("save_changes"));
        this.jButtonAddContact.setText(messagesManager.getMessage("add_contact"));

        this.jButtonSave.setToolTipText(messagesManager.getMessage("save_changes"));
        this.jButtonAddContact.setToolTipText(messagesManager.getMessage("add_contact"));

        jEditorPaneHelp.setContentType("text/html");
        jEditorPaneHelp.setEditable(false);

        jEditorPaneHelp.setText(HtmlTextUtil.getHtmlHelpContent("create_group"));
        
        keyListenerAdder();
        this.jTextAreaMembers.removeKeyListener(this.jTextAreaMembers.getKeyListeners()[0]);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    close();
                } catch (Exception ex) {
                    JOptionPaneNewCustom.showException(rootPane, ex);
                }
            }
        });

        jTextAreaMembers.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                isSaved = false;
            }

            public void removeUpdate(DocumentEvent e) {
                isSaved = false;
            }

            public void changedUpdate(DocumentEvent e) {
                isSaved = false;
            }
        });

        SwingUtil.applySwingUpdates(rootPane);        
        SwingUtil.setBackgroundColor(this, Color.WHITE);
        
        ButtonResizer buttonResizer = new ButtonResizer(jPanelButtons);
        buttonResizer.setWidthToMax();
        
        jPanelToolbar.setBackground(new Color(240, 240, 240));                  
        jEditorPaneHelp.setBackground(Color.white);
                
        jEditorPaneHelp.setMargin(new Insets(5, 5, 5, 5));
        jTextAreaMembers.setMargin(new Insets(5, 5, 5, 5));
        
        this.setSize(450, 450);
        this.setLocationRelativeTo(this.getParent());
    }

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

    private void this_keyReleased(KeyEvent e) {

        //System.out.println("this_keyReleased(KeyEvent e) " + e.getComponent().getName());
        int id = e.getID();
        if (id == KeyEvent.KEY_RELEASED) {
            int keyCode = e.getKeyCode();

            if (jTextFieldGroupName.getText() != null && !jTextFieldGroupName.getText().isEmpty()) {
                if (e.getComponent() == jTextFieldGroupName) {
                    this.setTitle(jTextFieldGroupName.getText());
                    isSaved = false;
                }
                jButtonOk.setEnabled(true);
            } else {
                jButtonOk.setEnabled(false);
            }

            if (keyCode == KeyEvent.VK_ENTER) {
                jButtonOkActionPerformed(null);
            }
            if (keyCode == KeyEvent.VK_ESCAPE) {
                try {
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    close();
                    this.setCursor(Cursor.getDefaultCursor());
                } catch (Exception ex) {
                    this.setCursor(Cursor.getDefaultCursor());
                    JOptionPaneNewCustom.showException(rootPane, ex);
                }
            }
            if (keyCode == KeyEvent.VK_S && e.isControlDown()) {
                try {
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    save();
                    isSaved = true;
                    this.setCursor(Cursor.getDefaultCursor());
                } catch (Exception ex) {
                    this.setCursor(Cursor.getDefaultCursor());
                    JOptionPaneNewCustom.showException(rootPane, ex);
                }
            }
        }
    }

    public void close() throws Exception {
        if (jTextFieldGroupName.getText() != null && !isSaved) {
            int result = JOptionPane.showConfirmDialog(this, messagesManager.getMessage("save_modification_before_close"), messagesManager.getMessage("group_creation"), JOptionPane.YES_NO_CANCEL_OPTION);

            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
            if (result == JOptionPane.NO_OPTION) {
                dispose();
            }
            if (result == JOptionPane.YES_OPTION) {
                if (!save()) {
                    return;
                }
            }
        }
        dispose();
    }

    /**
     * Add a list of recipients to a JTextArea
     * @param recipients    The set containing recipients to add
     * @param textArea      The destination text area
     */
    public void addGroupMembers(Set<String> recipients) {
        String newRecipientsStr = "";
        for (String recipient : recipients) {
            recipient = recipient.trim();
            if (!recipient.endsWith(CR_LF)) {
                recipient += CR_LF;
            }
            newRecipientsStr += recipient;
        }

        //Add recipients to those current list
        String finalRecipients = jTextAreaMembers.getText();
        if (finalRecipients.length() > 1) {
            if (!finalRecipients.trim().endsWith(CR_LF)) {
                finalRecipients += CR_LF;
            }
        }
        finalRecipients += newRecipientsStr;
        if (finalRecipients.endsWith(CR_LF)) {
            finalRecipients = StringUtils.removeEnd(finalRecipients, CR_LF);
        }
        jTextAreaMembers.setText(finalRecipients);
    }

    /**
     * Save group in db (insert or update)
     * @return
     * @throws Exception
     */
    private boolean save() throws Exception {
        if (groupId != -1) {
            saveGroup();
        } else {
            groupId = createGroup();
        }
        
        if (groupId != -1) {
            GroupHolder groupHolder = new GroupHolder(connection, userNumber);
            groupHolder.resetMap();
            groupHolder.reset();            
            GroupMemberCache.clearAll();
                        
            if (this.caller instanceof GroupListNew) {
                ((GroupListNew) caller).createJXTreeTable();
            }
            return true;
        }
        return false;
    }

    /**
     * Save modifications of group
     * @throws Exception
     */
    private void saveGroup()
            throws Exception {
        EmailGroupLocal emailGroupLocal = buildGroup();
        
        debug("EmailGroupLocal: " + emailGroupLocal);
        
        emailGroupLocal.setId(groupId);
        EmailGroupTransfert emailGroupTransfert = new EmailGroupTransfert(connection, userNumber, groupId);
        emailGroupTransfert.put(emailGroupLocal);
    }

    /**
     * Create a new group in db
     * @return
     * @throws Exception
     */
    private int createGroup() throws Exception {
        EmailGroupLocal emailGroupLocal = buildGroup();

        EmailGroupTransfert emailGroupTransfert = new EmailGroupTransfert(connection);
        emailGroupTransfert.put(emailGroupLocal);

        return emailGroupLocal.getId();
    }

    protected int getUserNumber() {
        return userNumber;
    }

    /**
     * debug tool
     */
    private void debug(String s) {
        if (DEBUG) {
            System.out.println(this.getClass().getName() + " "
                    + new java.util.Date() + " " + s);
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

        jPanelToolbar = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButtonSave = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButtonAddContact = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelEasst = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelSep1 = new javax.swing.JPanel();
        jPanelSearch = new javax.swing.JPanel();
        jLabelGroupName = new javax.swing.JLabel();
        jTextFieldGroupName = new javax.swing.JTextField();
        jPanelSepBlank2 = new javax.swing.JPanel();
        jPanelHelp = new javax.swing.JPanel();
        jEditorPaneHelp = new javax.swing.JEditorPane();
        jPanelSepBlank3 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabelGroupMembers = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanelSepBlank1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaMembers = new javax.swing.JTextArea();
        jPanelSepBlank = new javax.swing.JPanel();
        jPanelSepLine = new javax.swing.JPanel();
        jSeparator5 = new javax.swing.JSeparator();
        jPanelSouth = new javax.swing.JPanel();
        jPanelLeft = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemSave = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItemAddContact = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemClose = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jPanelToolbar.setLayout(new javax.swing.BoxLayout(jPanelToolbar, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setLayout(new java.awt.GridLayout());

        jToolBar1.setRollover(true);

        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/floppy_disk.png"))); // NOI18N
        jButtonSave.setFocusable(false);
        jButtonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSave.setLabel("jButtonSave");
        jButtonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonSave);
        jToolBar1.add(jSeparator3);

        jButtonAddContact.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/businessman2_plus.png"))); // NOI18N
        jButtonAddContact.setFocusable(false);
        jButtonAddContact.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAddContact.setLabel("jButtonAddContact");
        jButtonAddContact.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAddContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddContactActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonAddContact);

        jPanel1.add(jToolBar1);

        jPanelToolbar.add(jPanel1);

        jPanel2.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel2.setMinimumSize(new java.awt.Dimension(10, 10));
        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(10, 10));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));
        jPanelToolbar.add(jPanel2);

        getContentPane().add(jPanelToolbar, java.awt.BorderLayout.NORTH);

        jPanelWest.setMaximumSize(new java.awt.Dimension(10, 32767));
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelEasst.setMaximumSize(new java.awt.Dimension(10, 32767));
        getContentPane().add(jPanelEasst, java.awt.BorderLayout.EAST);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelSep1.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSep1.setMinimumSize(new java.awt.Dimension(10, 10));
        jPanelSep1.setPreferredSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanelSep1Layout = new javax.swing.GroupLayout(jPanelSep1);
        jPanelSep1.setLayout(jPanelSep1Layout);
        jPanelSep1Layout.setHorizontalGroup(
            jPanelSep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSep1Layout.setVerticalGroup(
            jPanelSep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSep1);

        jPanelSearch.setMaximumSize(new java.awt.Dimension(32767, 30));
        jPanelSearch.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelGroupName.setText("jLabelGroupName");
        jPanelSearch.add(jLabelGroupName);

        jTextFieldGroupName.setText("jTextFieldGroupName");
        jTextFieldGroupName.setPreferredSize(new java.awt.Dimension(200, 22));
        jPanelSearch.add(jTextFieldGroupName);

        jPanelCenter.add(jPanelSearch);

        jPanelSepBlank2.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepBlank2.setPreferredSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank2.setLayout(new javax.swing.BoxLayout(jPanelSepBlank2, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenter.add(jPanelSepBlank2);

        jPanelHelp.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(240, 240, 240), 1, true));
        jPanelHelp.setMaximumSize(new java.awt.Dimension(2147483647, 110));
        jPanelHelp.setMinimumSize(new java.awt.Dimension(28, 110));
        jPanelHelp.setPreferredSize(new java.awt.Dimension(340, 110));
        jPanelHelp.setLayout(new javax.swing.BoxLayout(jPanelHelp, javax.swing.BoxLayout.X_AXIS));
        jPanelHelp.add(jEditorPaneHelp);

        jPanelCenter.add(jPanelHelp);

        jPanelSepBlank3.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepBlank3.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank3.setPreferredSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank3.setLayout(new javax.swing.BoxLayout(jPanelSepBlank3, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenter.add(jPanelSepBlank3);

        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 30));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jLabelGroupMembers.setText("jLabelGroupMembers");
        jPanel3.add(jLabelGroupMembers);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));
        jPanel4.add(jSeparator1);

        jPanel3.add(jPanel4);

        jPanelCenter.add(jPanel3);

        jPanelSepBlank1.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepBlank1.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank1.setPreferredSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank1.setLayout(new javax.swing.BoxLayout(jPanelSepBlank1, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenter.add(jPanelSepBlank1);

        jTextAreaMembers.setColumns(20);
        jTextAreaMembers.setRows(5);
        jScrollPane1.setViewportView(jTextAreaMembers);

        jPanelCenter.add(jScrollPane1);

        jPanelSepBlank.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepBlank.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank.setPreferredSize(new java.awt.Dimension(0, 10));
        jPanelSepBlank.setLayout(new javax.swing.BoxLayout(jPanelSepBlank, javax.swing.BoxLayout.LINE_AXIS));
        jPanelCenter.add(jPanelSepBlank);

        jPanelSepLine.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelSepLine.setLayout(new javax.swing.BoxLayout(jPanelSepLine, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSepLine.add(jSeparator5);

        jPanelCenter.add(jPanelSepLine);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelSouth.setMaximumSize(new java.awt.Dimension(32767, 43));
        jPanelSouth.setPreferredSize(new java.awt.Dimension(101, 43));
        jPanelSouth.setLayout(new java.awt.GridLayout(1, 2));

        jPanelLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 10));
        jPanelSouth.add(jPanelLeft);

        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonOk.setText("jButtonOk");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonOk);

        jButtonCancel.setText("jButtonCancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonCancel);

        jPanel12.setMaximumSize(new java.awt.Dimension(0, 10));
        jPanel12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 5));
        jPanelButtons.add(jPanel12);

        jPanelSouth.add(jPanelButtons);

        getContentPane().add(jPanelSouth, java.awt.BorderLayout.SOUTH);

        jMenu1.setText("File");

        jMenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/floppy_disk.png"))); // NOI18N
        jMenuItemSave.setText("jMenuItemSave");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemSave);
        jMenu1.add(jSeparator4);

        jMenuItemAddContact.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/businessman2_plus.png"))); // NOI18N
        jMenuItemAddContact.setText("jMenuItemNewContact");
        jMenuItemAddContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddContactActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemAddContact);
        jMenu1.add(jSeparator2);

        jMenuItemClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/close.png"))); // NOI18N
        jMenuItemClose.setText("jMenuItemClose");
        jMenuItemClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCloseActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemClose);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            save();
            isSaved = true;
            this.setCursor(Cursor.getDefaultCursor());
        } catch (Exception e) {
            JOptionPaneNewCustom.showException(rootPane, e);
        }
}//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jMenuItemAddContactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddContactActionPerformed
        jButtonAddContactActionPerformed(evt);
}//GEN-LAST:event_jMenuItemAddContactActionPerformed

    private void jMenuItemCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCloseActionPerformed
        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            close();
            this.setCursor(Cursor.getDefaultCursor());
        } catch (Exception e) {
            JOptionPaneNewCustom.showException(rootPane, e);
        }

}//GEN-LAST:event_jMenuItemCloseActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            save();
            isSaved = true;
            this.setCursor(Cursor.getDefaultCursor());
        } catch (Exception e) {
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPaneNewCustom.showException(rootPane, e);
        }
}//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonAddContactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddContactActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ContactSelector contactSelector = new ContactSelector(this, connection, GroupEditor.BUTTON_ADD);
        contactSelector.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_jButtonAddContactActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            close();

        } catch (Exception e) {
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPaneNewCustom.showException(rootPane, e);
        }
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_jButtonOkActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.dispose();
}//GEN-LAST:event_jButtonCancelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new GroupEditor().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddContact;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JEditorPane jEditorPaneHelp;
    private javax.swing.JLabel jLabelGroupMembers;
    private javax.swing.JLabel jLabelGroupName;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemAddContact;
    private javax.swing.JMenuItem jMenuItemClose;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEasst;
    private javax.swing.JPanel jPanelHelp;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelSearch;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSepBlank;
    private javax.swing.JPanel jPanelSepBlank1;
    private javax.swing.JPanel jPanelSepBlank2;
    private javax.swing.JPanel jPanelSepBlank3;
    private javax.swing.JPanel jPanelSepLine;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelToolbar;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JTextArea jTextAreaMembers;
    private javax.swing.JTextField jTextFieldGroupName;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
