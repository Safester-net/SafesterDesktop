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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.swing.util.SwingUtil;
import com.swing.util.CustomComboBox.TreeListCellRenderer;
import com.swing.util.CustomComboBox.TreeListModel;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ClipboardManager;
import net.safester.application.tool.SearchResultMessagesTableCreator;
import net.safester.application.tool.WindowSettingManager;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.search.MessageSearch;
import net.safester.noobs.clientserver.FolderLocal;
import net.safester.noobs.clientserver.MessageLocal;

/**
 * SafeShareIt Search engine JFrame
 *
 * @author Alexandre Becquereau
 */
public class Search extends javax.swing.JFrame {

    private SearchContactSelector searchContactSelector;

    /**
     * Creates new form SafeShareItSearch
     */
    public Search() {
        initComponents();
        initCompany();
    }

    //Search parameters limitations
    private final static int MAX_SEARCHED_MONTHS = 6;

    private MessagesManager messages = new MessagesManager();

    private ClipboardManager clipboardManager;
    private JFrame parent;

    //user infos
    private int userNumber;
    private String keyId;
    private char[] passphrase;

    //The tree reprensenting folders
    private JTree folderTree;

    private List<Integer> selectedMessages;

    //Connection to db
    private Connection connection;

    /**
     * Our constructor that init the tree of folders
     *
     * @param parent Calling JFrame
     * @param folderTree jtree of folders
     */
    public Search(JFrame parent, Connection theConnection, JTree folderTree, int theUserNumber, char[] thePassphrase, String theKeyId) {
        this.parent = parent;
        this.folderTree = folderTree;
        this.userNumber = theUserNumber;
        this.passphrase = thePassphrase;

        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection) theConnection).clone();

        this.keyId = theKeyId;
        initComponents();
        initCompany();

    }

    private void initCompany() {

        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        this.setTitle(messages.getMessage("search_message"));
        this.jLabelTitle.setText(messages.getMessage("search_message"));
        this.jLabelFolder.setText(messages.getMessage("folder"));
        this.jLabelSearchContent.setText(messages.getMessage("find"));
        this.jLabelSearchDate.setText(messages.getMessage("message_date_between"));
        this.jLabelAnd.setText(messages.getMessage("and"));
        this.jButtonSearch.setText("search");
        this.jTextFieldSearchContent.setText("");
        this.jCheckBoxRecurse.setText(messages.getMessage("recurse_folders"));
        
        this.jButtonSearch.setText(messages.getMessage("search"));
        this.jButtonNewSearch.setText(messages.getMessage("new_search"));
        
        this.jButtonSearchSender.setText(messages.getMessage("from"));
        this.jButtonSearchRecipient.setText(messages.getMessage("to"));

        //Init date interval to last month
        Calendar now = Calendar.getInstance();
        jXDatePickerEnd.setDate(now.getTime());
        now.add(Calendar.MONTH, -1);
        jXDatePickerStart.setDate(now.getTime());

        this.jComboBoxContentType.addItem(messages.getMessage("subject"));
        this.jComboBoxContentType.addItem(messages.getMessage("subject_and_body"));
        jComboBoxContentType.setSelectedIndex(0);

       // ButtonResizer buttonResizer = new ButtonResizer(jPanelCenter);
        //buttonResizer.setWidthToMax();
   
        // Resize "Search" to "New Search" size (width)
        Dimension dim = jButtonNewSearch.getPreferredSize();
        int width = (int)dim.getWidth();
        int height = (int) dim.getHeight();
        jButtonSearch.setPreferredSize(new Dimension(width, height));
        jButtonSearch.setMinimumSize(new Dimension(width, height));
        jButtonSearch.setMaximumSize(new Dimension(width, height));
        jButtonSearch.setSize(new Dimension(width, height));
                
        //Replace default combo box by custom one with jTree
        initFolderComboBox();

        this.setSize(new Dimension(892, 495));
        
        SwingUtil.resizeJComponentsForNimbusAndMacOsX(rootPane);
                
        WindowSettingManager.load(this);

        //Init jtable (empty at the beginning)
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                createTable(null);
            }
        });

        clipboardManager = new ClipboardManager(rootPane);
        //pack();

        this.keyListenerAdder();
        this.setLocationByPlatform(true);
        this.setLocationRelativeTo(parent);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                save();
            }

        });



    }

    private void save() {
        WindowSettingManager.save(this);
    }

    private void close() {
        WindowSettingManager.save(this);
        this.dispose();
    }

    /**
     * Universal key listener
     */
    private void keyListenerAdder() {
        List<Component> components = SwingUtil.getAllComponants(this);

        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);
            if (comp instanceof JTable) {
                continue;
            }
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
                this.doIt();
            }
            if (keyCode == KeyEvent.VK_ESCAPE) {
                this.dispose();
            }
        }
    }

    /**
     * Create a combo box containing a jTree
     */
    private void initFolderComboBox() {
        //Replace default combo box by combo nox containing folders tree
        if (folderTree != null) {
            jPanelSearchFolderAndDatesNew.remove(jComboBoxFolder);
            jPanelSearchFolderAndDatesNew.remove(jCheckBoxRecurse);
            TreeModel treeModel = folderTree.getModel();
            this.jComboBoxFolder = new JComboBox(new TreeListModel(treeModel));
            TreeCellRenderer treeCellRenderer = folderTree.getCellRenderer();
            jComboBoxFolder.setRenderer(new TreeListCellRenderer(treeModel, treeCellRenderer));
            jPanelSearchFolderAndDatesNew.add(jComboBoxFolder);
            jPanelSearchFolderAndDatesNew.add(jCheckBoxRecurse);
            jComboBoxFolder.setSelectedIndex(1);
        }
    }

    /**
     * Create JTable
     *
     * @param messages the list of messages (can be null)
     */
    public void createTable(List<MessageLocal> messages) {

        if (messages == null) {
            messages = new ArrayList<MessageLocal>();
        }

        System.out.println("jComboBoxFolder.getSelectedIndex(): " + jComboBoxFolder.getSelectedIndex());
        System.out.println("jComboBoxFolder.getSelectedItem() : " + jComboBoxFolder.getSelectedItem());
                
        int folder = 0;
        if (jComboBoxFolder.getSelectedIndex()== 1) {
            folder = Parms.INBOX_ID;
        }
        else if (jComboBoxFolder.getSelectedIndex()== 2) {
              folder = Parms.OUTBOX_ID;
        }
        else if (jComboBoxFolder.getSelectedIndex()== 3) {
              folder = Parms.DRAFT_ID;
        }
        else {
            folder = Parms.INBOX_ID;
        }
        
        SearchResultMessagesTableCreator messagesTableCreator
                = new SearchResultMessagesTableCreator(this, connection, messages, false, folder);
        this.jTableResult = messagesTableCreator.create();

        jScrollPane.setViewportView(jTableResult);
        this.setCursor(Cursor.getDefaultCursor());

    }

    private void setSelectedMessages() {
        //Reset list
        selectedMessages = new ArrayList<Integer>();
        //Get selected rows index
        int[] selRows = jTableResult.getSelectedRows();
        if (selRows.length > 0) {
            for (Integer rowIndex : selRows) {
                //For each row get message
                //MessageLocal messageLocal = getMessageForRowIndex(rowIndex.intValue());
                int messageId = (Integer) jTableResult.getValueAt(rowIndex.intValue(), 0);
                if (messageId > 0) {
                    selectedMessages.add(messageId);
                }
            }
        }
    }

    /**
     * Open the selected message.
     *
     * @param messageLocalMap the map that contain in memory the Message Local
     * per Message Id
     * @throws Exception
     */
    public void openSelectedMessage(Map<Integer, MessageLocal> messageLocalMap) throws Exception {

        setSelectedMessages();

        Main ssiMain = (Main) parent;

        for (Integer id : selectedMessages) {

            //MessageTransfer messageTransfer = new MessageTransfer(connection, userNumber, id.intValue());
            //MessageLocal messageLocal = messageTransfer.get();
            //MessageDecryptor messageDecryptor = new MessageDecryptor(userNumber, passphrase, connection);
            MessageLocal messageLocal = messageLocalMap.get(id);

            int folderId = messageLocal.getFolderId();
            //Open a new window for each message
            if (folderId != Parms.DRAFT_ID) {
                new MessageReader(ssiMain, connection, messageLocal, keyId, this.getUserNumber(), passphrase, folderId).setVisible(true);
            } else {
                new MessageComposer(this, keyId, this.getUserNumber(), passphrase, connection, messageLocal, Parms.ACTION_EDIT).setVisible(true);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    /**
     * Do the search
     */
    private void doIt() {
        //Check if search parameters are ok
        if (!checkSearchParameters()) {
            return;
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        //Get folder id & if search is done on subfolders
        int folderId = getSelectedFolderId();
        boolean recurse = jCheckBoxRecurse.isSelected();

        //Get date interval
        Date start = jXDatePickerStart.getDate();
        Date end = jXDatePickerEnd.getDate();

        //Add one day to end date jXDatePicker set date to beginning of day!!
        Calendar cEnd = new GregorianCalendar();
        cEnd.setTime(end);
        cEnd.add(Calendar.DAY_OF_YEAR, 1);

        end = new Date(cEnd.getTimeInMillis());

        boolean searchOnBody = false;
        if (jComboBoxContentType.getSelectedIndex() == 1) {
            searchOnBody = true;
        }

        //System.out.println("jComboBoxContentType.getSelectedItem(): " + jComboBoxContentType.getSelectedItem());
        //System.out.println("searchOnBody                          : " + searchOnBody);

        try {
            //Init search engine
//            String searchContent = this.jTextFieldSearchContent.getText();
//            MessageSearch messageSearch = new MessageSearch(this, connection, getUserNumber(), passphrase, searchContent, searchOnBody, folderId, recurse);
//
//            //Call search depending on type
//            //Always base content on dates, will return all for any subject and body
//            Set<Integer> messageIds = messageSearch.searchOnDates(start, end);
//
//            if (jTextFieldSearchSender.getText().length() > 0) {
//                Set<Integer> set = messageSearch.searchOnSender(jTextFieldSearchSender.getText(), start, end);
//                // Build intersection
//                messageIds.retainAll(set);
//            }
//
//            if (jTextFieldSearchRecipient.getText().length() > 0) {
//                Set<Integer> set = messageSearch.searchOnRecipient(jTextFieldSearchRecipient.getText(), start, end);
//                // Build intersection
//                messageIds.retainAll(set);
//            }

            String searchSender = jTextFieldSearchSender.getText();
            String searchRecipient = jTextFieldSearchRecipient.getText();

            AwakeConnection awakeConnection = (AwakeConnection) connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

            String jsonString = awakeFileSession.call("net.safester.server.hosts.newapi.SearchNewApi.searchMessages",
                    userNumber, start.getTime(), end.getTime(), searchSender, searchRecipient,
                    folderId, recurse, connection);
        
            Gson gsonOut = new Gson();
            java.lang.reflect.Type type = new TypeToken<Set<Integer> >() {
            }.getType();
            Set<Integer> messageIds  = gsonOut.fromJson(jsonString, type);
            
            if (messageIds.isEmpty()) {
                MessagesManager messagesManager = new MessagesManager();
                JOptionPane.showMessageDialog(this, messagesManager.getMessage("no_matching_result_found"));
                this.createTable(null);
                return;
            }

            String searchContent = this.jTextFieldSearchContent.getText();
            MessageSearch messageSearch = new MessageSearch(this, connection, getUserNumber(), passphrase, searchContent, searchOnBody, folderId, recurse);
            // Ok, now decrypt and filter messages
            messageSearch.downloadAndDecryptMessages(messageIds);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPaneNewCustom.showException(parent, e);
        }

    }

    private int getSelectedFolderId() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jComboBoxFolder.getSelectedItem();
        if (!(selectedNode.getUserObject() instanceof FolderLocal)) {
            //should never happens if called after checkSearchParameters
            return -1;
        }
        FolderLocal selectedFolder = (FolderLocal) selectedNode.getUserObject();
        return selectedFolder.getFolderId();

    }

    /**
     * Check if search parameters are valid i.e. : a "real" folder is selected
     * (root of tree) searched string length > 4 interval period is 1 month max
     *
     * @return
     */
    private boolean checkSearchParameters() {

        //Search must be performed on a folder not on home
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jComboBoxFolder.getSelectedItem();
        if (selectedNode == null || !(selectedNode.getUserObject() instanceof FolderLocal)) {
            JOptionPane.showMessageDialog(this, messages.getMessage("select_folder"));
            return false;
        }

//        //Search string must be at least 5 caracters
//        String searchedString = jTextFieldSearchContent.getText().trim();
//        if(searchedString.length() < MIN_SEARCHED_STRING_LENGTH){
//            String msg = messages.getMessage("searched_string_must_be_more_n");
//            msg = MessageFormat.format(msg, "" + MIN_SEARCHED_STRING_LENGTH);
//            JOptionPane.showMessageDialog(this, messages.getMessage(msg));
//            return false;
//        }
        Date start = jXDatePickerStart.getDate();
        Date end = jXDatePickerEnd.getDate();

        Calendar cStart = Calendar.getInstance();
        cStart.setTime(start);
        Calendar cEnd = Calendar.getInstance();
        cEnd.setTime(end);

        //If start date after end date invert 2 dates!
        if (cStart.after(cEnd)) {
            jXDatePickerEnd.setDate(start);
            jXDatePickerStart.setDate(end);
            cStart.setTime(end);
            cEnd.setTime(start);
        }

        //Interval must be MAX_SEARCHED_MONTHS month max
        cStart.add(Calendar.MONTH, MAX_SEARCHED_MONTHS);

        if (cStart.before(cEnd)) {
            String msg = messages.getMessage("period_must_be_n_month");
            msg = MessageFormat.format(msg, "" + MAX_SEARCHED_MONTHS);
            JOptionPane.showMessageDialog(parent, msg);
            return false;
        }

        return true;
    }

    public void addToSender(String email) {
        jTextFieldSearchSender.setText(email);
    }

    public void addToRecipient(String email) {
        jTextFieldSearchRecipient.setText(email);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelNorth = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelEast = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelTitle = new javax.swing.JPanel();
        jLabelIcon = new javax.swing.JLabel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelSep1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanelSepEmpty = new javax.swing.JPanel();
        jPanelSearchContentNew = new javax.swing.JPanel();
        jPanelSearchContentLeft = new javax.swing.JPanel();
        jLabelSearchContent = new javax.swing.JLabel();
        jTextFieldSearchContent = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabelSearchIn = new javax.swing.JLabel();
        jComboBoxContentType = new javax.swing.JComboBox<>();
        jPanelSearchContentRight = new javax.swing.JPanel();
        jButtonSearch = new javax.swing.JButton();
        jPanelSearchSender = new javax.swing.JPanel();
        jPanelSearchSenderLeft = new javax.swing.JPanel();
        jButtonSearchSender = new javax.swing.JButton();
        jTextFieldSearchSender = new javax.swing.JTextField();
        jPanelSearchSenderRight = new javax.swing.JPanel();
        jButtonNewSearch = new javax.swing.JButton();
        jPanelSearchRecipient = new javax.swing.JPanel();
        jButtonSearchRecipient = new javax.swing.JButton();
        jTextFieldSearchRecipient = new javax.swing.JTextField();
        jPanelSearchFolderAndDatesNew = new javax.swing.JPanel();
        jLabelFolder = new javax.swing.JLabel();
        jComboBoxFolder = new javax.swing.JComboBox<>();
        jCheckBoxRecurse = new javax.swing.JCheckBox();
        jPanelSearchFolderAndDates = new javax.swing.JPanel();
        jLabelSearchDate = new javax.swing.JLabel();
        jXDatePickerStart = new org.jdesktop.swingx.JXDatePicker();
        jLabelAnd = new javax.swing.JLabel();
        jXDatePickerEnd = new org.jdesktop.swingx.JXDatePicker();
        jPanelSepEmpty3 = new javax.swing.JPanel();
        jPanelSep2 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanelTable = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        jTableResult = new javax.swing.JTable();
        jPanelSepEmpty2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jPanelNorth.setMaximumSize(new java.awt.Dimension(32767, 10));
        getContentPane().add(jPanelNorth, java.awt.BorderLayout.NORTH);

        jPanelWest.setMaximumSize(new java.awt.Dimension(10, 10));
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelEast.setMaximumSize(new java.awt.Dimension(10, 10));
        getContentPane().add(jPanelEast, java.awt.BorderLayout.EAST);

        jPanelCenter.setMinimumSize(new java.awt.Dimension(600, 164));
        jPanelCenter.setPreferredSize(new java.awt.Dimension(600, 417));
        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelTitle.setMaximumSize(new java.awt.Dimension(32767, 42));
        jPanelTitle.setPreferredSize(new java.awt.Dimension(32767, 42));
        jPanelTitle.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/binocular.png"))); // NOI18N
        jPanelTitle.add(jLabelIcon);

        jLabelTitle.setText("jLabelTitle");
        jPanelTitle.add(jLabelTitle);

        jPanelCenter.add(jPanelTitle);

        jPanelSep1.setMaximumSize(new java.awt.Dimension(32767, 2));
        jPanelSep1.setLayout(new javax.swing.BoxLayout(jPanelSep1, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSep1.add(jSeparator1);

        jPanelCenter.add(jPanelSep1);

        jPanelSepEmpty.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelCenter.add(jPanelSepEmpty);

        jPanelSearchContentNew.setLayout(new javax.swing.BoxLayout(jPanelSearchContentNew, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSearchContentLeft.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPanelSearchContentLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelSearchContent.setText("Search");
        jLabelSearchContent.setMaximumSize(new java.awt.Dimension(70, 16));
        jLabelSearchContent.setMinimumSize(new java.awt.Dimension(70, 16));
        jLabelSearchContent.setPreferredSize(new java.awt.Dimension(70, 16));
        jPanelSearchContentLeft.add(jLabelSearchContent);

        jTextFieldSearchContent.setPreferredSize(new java.awt.Dimension(440, 22));
        jPanelSearchContentLeft.add(jTextFieldSearchContent);
        jPanelSearchContentLeft.add(jPanel2);

        jLabelSearchIn.setText("In");
        jPanelSearchContentLeft.add(jLabelSearchIn);
        jPanelSearchContentLeft.add(jComboBoxContentType);

        jPanelSearchContentNew.add(jPanelSearchContentLeft);

        jPanelSearchContentRight.setLayout(new javax.swing.BoxLayout(jPanelSearchContentRight, javax.swing.BoxLayout.LINE_AXIS));

        jButtonSearch.setText("Search");
        jButtonSearch.setMaximumSize(new java.awt.Dimension(101, 25));
        jButtonSearch.setMinimumSize(new java.awt.Dimension(101, 25));
        jButtonSearch.setPreferredSize(new java.awt.Dimension(101, 25));
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });
        jPanelSearchContentRight.add(jButtonSearch);

        jPanelSearchContentNew.add(jPanelSearchContentRight);

        jPanelCenter.add(jPanelSearchContentNew);

        jPanelSearchSender.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPanelSearchSender.setLayout(new javax.swing.BoxLayout(jPanelSearchSender, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSearchSenderLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jButtonSearchSender.setForeground(new java.awt.Color(0, 0, 255));
        jButtonSearchSender.setText("From");
        jButtonSearchSender.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButtonSearchSender.setBorderPainted(false);
        jButtonSearchSender.setContentAreaFilled(false);
        jButtonSearchSender.setFocusPainted(false);
        jButtonSearchSender.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonSearchSender.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButtonSearchSender.setMaximumSize(new java.awt.Dimension(70, 16));
        jButtonSearchSender.setMinimumSize(new java.awt.Dimension(70, 16));
        jButtonSearchSender.setPreferredSize(new java.awt.Dimension(70, 16));
        jButtonSearchSender.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonSearchSenderMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonSearchSenderMouseExited(evt);
            }
        });
        jButtonSearchSender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchSenderActionPerformed(evt);
            }
        });
        jPanelSearchSenderLeft.add(jButtonSearchSender);

        jTextFieldSearchSender.setToolTipText("");
        jTextFieldSearchSender.setPreferredSize(new java.awt.Dimension(350, 22));
        jPanelSearchSenderLeft.add(jTextFieldSearchSender);

        jPanelSearchSender.add(jPanelSearchSenderLeft);

        jPanelSearchSenderRight.setLayout(new javax.swing.BoxLayout(jPanelSearchSenderRight, javax.swing.BoxLayout.LINE_AXIS));

        jButtonNewSearch.setText("New Search");
        jButtonNewSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewSearchActionPerformed(evt);
            }
        });
        jPanelSearchSenderRight.add(jButtonNewSearch);

        jPanelSearchSender.add(jPanelSearchSenderRight);

        jPanelCenter.add(jPanelSearchSender);

        jPanelSearchRecipient.setMaximumSize(new java.awt.Dimension(32767, 32));
        jPanelSearchRecipient.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jButtonSearchRecipient.setForeground(new java.awt.Color(0, 0, 255));
        jButtonSearchRecipient.setText("To");
        jButtonSearchRecipient.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButtonSearchRecipient.setBorderPainted(false);
        jButtonSearchRecipient.setContentAreaFilled(false);
        jButtonSearchRecipient.setFocusPainted(false);
        jButtonSearchRecipient.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonSearchRecipient.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButtonSearchRecipient.setMaximumSize(new java.awt.Dimension(70, 16));
        jButtonSearchRecipient.setMinimumSize(new java.awt.Dimension(70, 16));
        jButtonSearchRecipient.setPreferredSize(new java.awt.Dimension(70, 16));
        jButtonSearchRecipient.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonSearchRecipientMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonSearchRecipientMouseExited(evt);
            }
        });
        jButtonSearchRecipient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchRecipientActionPerformed(evt);
            }
        });
        jPanelSearchRecipient.add(jButtonSearchRecipient);

        jTextFieldSearchRecipient.setToolTipText("");
        jTextFieldSearchRecipient.setPreferredSize(new java.awt.Dimension(350, 22));
        jPanelSearchRecipient.add(jTextFieldSearchRecipient);

        jPanelCenter.add(jPanelSearchRecipient);

        jPanelSearchFolderAndDatesNew.setMaximumSize(new java.awt.Dimension(32767, 35));
        jPanelSearchFolderAndDatesNew.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelFolder.setText("Folder");
        jLabelFolder.setMaximumSize(new java.awt.Dimension(70, 16));
        jLabelFolder.setMinimumSize(new java.awt.Dimension(70, 16));
        jLabelFolder.setPreferredSize(new java.awt.Dimension(70, 16));
        jPanelSearchFolderAndDatesNew.add(jLabelFolder);

        jComboBoxFolder.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanelSearchFolderAndDatesNew.add(jComboBoxFolder);

        jCheckBoxRecurse.setText("jCheckBoxRecurse");
        jPanelSearchFolderAndDatesNew.add(jCheckBoxRecurse);

        jPanelCenter.add(jPanelSearchFolderAndDatesNew);

        jPanelSearchFolderAndDates.setMaximumSize(new java.awt.Dimension(32767, 35));
        jPanelSearchFolderAndDates.setMinimumSize(new java.awt.Dimension(426, 35));
        jPanelSearchFolderAndDates.setPreferredSize(new java.awt.Dimension(10, 35));
        jPanelSearchFolderAndDates.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelSearchDate.setText("jLabelSearchDate");
        jPanelSearchFolderAndDates.add(jLabelSearchDate);
        jPanelSearchFolderAndDates.add(jXDatePickerStart);

        jLabelAnd.setText("jLabel1");
        jPanelSearchFolderAndDates.add(jLabelAnd);
        jPanelSearchFolderAndDates.add(jXDatePickerEnd);

        jPanelCenter.add(jPanelSearchFolderAndDates);

        jPanelSepEmpty3.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanelCenter.add(jPanelSepEmpty3);

        jPanelSep2.setMaximumSize(new java.awt.Dimension(32767, 2));
        jPanelSep2.setLayout(new javax.swing.BoxLayout(jPanelSep2, javax.swing.BoxLayout.LINE_AXIS));
        jPanelSep2.add(jSeparator2);

        jPanelCenter.add(jPanelSep2);

        jPanelTable.setLayout(new java.awt.GridLayout(1, 0));

        jScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane.setPreferredSize(new java.awt.Dimension(200, 402));

        jTableResult.setModel(new javax.swing.table.DefaultTableModel(
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
        jTableResult.setPreferredSize(new java.awt.Dimension(20000, 64));
        jScrollPane.setViewportView(jTableResult);

        jPanelTable.add(jScrollPane);

        jPanelCenter.add(jPanelTable);

        jPanelSepEmpty2.setMaximumSize(new java.awt.Dimension(32767, 4));
        jPanelSepEmpty2.setMinimumSize(new java.awt.Dimension(10, 4));
        jPanelSepEmpty2.setPreferredSize(new java.awt.Dimension(10, 4));
        jPanelCenter.add(jPanelSepEmpty2);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchActionPerformed
        doIt();
    }//GEN-LAST:event_jButtonSearchActionPerformed

    private void jButtonSearchSenderMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSearchSenderMouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jButtonSearchSenderMouseEntered

    private void jButtonSearchSenderMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSearchSenderMouseExited
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_jButtonSearchSenderMouseExited

    private void jButtonSearchSenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchSenderActionPerformed
        if (searchContactSelector != null) {
            searchContactSelector.dispose();
        }

        searchContactSelector = new SearchContactSelector(this, connection, SearchContactSelector.ORIGIN_FROM);
        searchContactSelector.setVisible(true);

    }//GEN-LAST:event_jButtonSearchSenderActionPerformed

    private void jButtonSearchRecipientMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSearchRecipientMouseEntered
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_jButtonSearchRecipientMouseEntered

    private void jButtonSearchRecipientMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSearchRecipientMouseExited
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_jButtonSearchRecipientMouseExited

    private void jButtonSearchRecipientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchRecipientActionPerformed
        if (searchContactSelector != null) {
            searchContactSelector.dispose();
        }

        searchContactSelector = new SearchContactSelector(this, connection, SearchContactSelector.ORIGIN_TO);
        searchContactSelector.setVisible(true);
    }//GEN-LAST:event_jButtonSearchRecipientActionPerformed

    private void jButtonNewSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewSearchActionPerformed
           
        if (this.jTextFieldSearchContent.getText().isEmpty() && this.jTextFieldSearchRecipient.getText().isEmpty() && this.jTextFieldSearchSender.getText().isEmpty()
                && this.jTableResult.getRowCount() == 0) {
            return;
        }
        
        String text = messages.getMessage("this_will_cancel_the_current_request");
        String title = messages.getMessage("warning");

        int result = JOptionPane.showConfirmDialog(this, text, title, JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.NO_OPTION) {
            return;
        }
        
        jTextFieldSearchContent.setText(null);
        jTextFieldSearchRecipient.setText(null);
        jTextFieldSearchSender.setText(null);
        
        jComboBoxContentType.setSelectedIndex(0); 
        
        jComboBoxFolder.setSelectedIndex(1);
        jCheckBoxRecurse.setSelected(false);
        
        Calendar now = Calendar.getInstance();
        jXDatePickerEnd.setDate(now.getTime());
        now.add(Calendar.MONTH, -1);
        jXDatePickerStart.setDate(now.getTime());
        
        createTable(null);          
    }//GEN-LAST:event_jButtonNewSearchActionPerformed

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

            public void run() {
                new Search().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonNewSearch;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JButton jButtonSearchRecipient;
    private javax.swing.JButton jButtonSearchSender;
    private javax.swing.JCheckBox jCheckBoxRecurse;
    private javax.swing.JComboBox<String> jComboBoxContentType;
    private javax.swing.JComboBox<String> jComboBoxFolder;
    private javax.swing.JLabel jLabelAnd;
    private javax.swing.JLabel jLabelFolder;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelSearchContent;
    private javax.swing.JLabel jLabelSearchDate;
    private javax.swing.JLabel jLabelSearchIn;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEast;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSearchContentLeft;
    private javax.swing.JPanel jPanelSearchContentNew;
    private javax.swing.JPanel jPanelSearchContentRight;
    private javax.swing.JPanel jPanelSearchFolderAndDates;
    private javax.swing.JPanel jPanelSearchFolderAndDatesNew;
    private javax.swing.JPanel jPanelSearchRecipient;
    private javax.swing.JPanel jPanelSearchSender;
    private javax.swing.JPanel jPanelSearchSenderLeft;
    private javax.swing.JPanel jPanelSearchSenderRight;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSepEmpty;
    private javax.swing.JPanel jPanelSepEmpty2;
    private javax.swing.JPanel jPanelSepEmpty3;
    private javax.swing.JPanel jPanelTable;
    private javax.swing.JPanel jPanelTitle;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTableResult;
    private javax.swing.JTextField jTextFieldSearchContent;
    private javax.swing.JTextField jTextFieldSearchRecipient;
    private javax.swing.JTextField jTextFieldSearchSender;
    private org.jdesktop.swingx.JXDatePicker jXDatePickerEnd;
    private org.jdesktop.swingx.JXDatePicker jXDatePickerStart;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the userNumber
     */
    public int getUserNumber() {
        return userNumber;
    }

}
