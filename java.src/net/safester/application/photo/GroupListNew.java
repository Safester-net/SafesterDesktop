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
package net.safester.application.photo;

import net.safester.application.*;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.util.GroupTreeTableElement;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.GroupMemberListTransfert;
import net.safester.clientserver.holder.GroupHolder;
import net.safester.noobs.clientserver.GroupMemberLocal;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.awakefw.sql.api.client.AwakeConnection;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import com.swing.util.SwingUtil;
import java.awt.Color;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import net.safester.clientserver.GroupMemberCache;
import net.safester.clientserver.UserNumberGetterClient;
import org.apache.commons.lang3.SystemUtils;

/**
 *
 * @author Alexandre Becquereau
 */
public class GroupListNew extends javax.swing.JFrame {

    private Connection connection;
    private int userNumber;
    private JXTreeTable jXTreeTable;
    private MessagesManager messagesManager = new MessagesManager();
    private JPopupMenu jTablePopupMenu;

    private JFrame caller;
    
    /** button will not be displayed for Vault */
    private boolean displayJButtonSendMessageToGroup = true;
    
    
    /** Creates new form GroupList */
    public GroupListNew() {
        initComponents();

    }

    public GroupListNew(JFrame theCaller, Connection theConnection, int theUserNumber, boolean displayJButtonSendMessageToGroup) {
        this();

        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();
        
        userNumber = theUserNumber;
        caller = theCaller;
        this.displayJButtonSendMessageToGroup = displayJButtonSendMessageToGroup;
        initCompany();
    }    
    
    public GroupListNew(JFrame theCaller, Connection theConnection, int theUserNumber) {
        this();

        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();
        
        userNumber = theUserNumber;
        caller = theCaller;
        initCompany();
    }

    /**
     * Create Treenode representing a group
     * @param group     Group object
     * @param members   List of memebers
     * @return          The representation of group as tree element
     */
    private DefaultMutableTreeTableNode buildGroup(GroupTreeTableElement group, List<GroupMemberLocal> members) {
        DefaultMutableTreeTableNode groupNode = new DefaultMutableTreeTableNode(group);
        for (GroupMemberLocal member : members) {
            GroupTreeTableElement groupMember = new GroupTreeTableElement();
            groupMember.setId(-1);
            groupMember.setLabel(member.getName() + " <" + member.getEmail() + ">");
            groupMember.setGroupId(group.getId());
            groupMember.setThumbnail(member.getThumbnail());
            DefaultMutableTreeTableNode groupMemberNode = new DefaultMutableTreeTableNode(groupMember);
            groupNode.add(groupMemberNode);
        }
        return groupNode;
    }
    
    private void close() {
        dispose();
    }

    /**
     * Create th JXtreeTable containing all groups of user
     */
    public void createJXTreeTable() {
        try {            
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            DefaultMutableTreeTableNode root = loadData();
            jXTreeTable = new JXTreeTable(new DefaultTreeTableModel(root));

            jXTreeTable.setRootVisible(false);
            jXTreeTable.setTreeCellRenderer(new GroupTreeCellRendererNew(jXTreeTable));
            this.jXTreeTable.setEditable(false);
            this.jXTreeTable.setAutoCreateRowSorter(true);
            
            this.jXTreeTable.setRowHeight(ImageResizer.getStickerHeight() + 2);
            
            this.jXTreeTable.getColumnModel().getColumn(0).setHeaderValue(messagesManager.getMessage("name"));
            buildJXTablePopupMenu();

            jXTreeTable.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e)
                    {
                        jXTableMessages_mousePressedOrReleased(e);
                    }

                    @Override
                    public void mouseReleased(MouseEvent e)
                    {
                        jXTableMessages_mousePressedOrReleased(e);
                    }

                    @Override
                    public void mouseClicked(MouseEvent e)
                    {
                        jXTableMessages_mouseClicked(e);
                    }
                });

            jXTreeTable.setBackground(Color.WHITE);      
            jXTreeTable.expandAll();
                    
            this.jScrollPane1.setViewportView(jXTreeTable);
            jScrollPane1.getViewport().setBackground(Color.WHITE);

            this.setCursor(Cursor.getDefaultCursor());
        } catch (SQLException ex) {
            ex.printStackTrace();
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPaneNewCustom.showException(rootPane, ex);
        }
    }

    /**
     * Build popup menu linked to the jxTreeTable
     */
    private void buildJXTablePopupMenu() {

        jTablePopupMenu = new JPopupMenu();
        JMenuItem itemEdit = new JMenuItem(messagesManager.getMessage("edit"));
        itemEdit.setIcon(Parms.createImageIcon("images/files_2/16x16/users3_plus.png"));
        JMenuItem itemDelete = new JMenuItem(messagesManager.getMessage("delete"));
        itemDelete.setIcon(Parms.createImageIcon("images/files_2/16x16/users3_delete.png"));
        JMenuItem itemMailTo = new JMenuItem(messagesManager.getMessage("mail_to"));
        itemMailTo.setIcon(Parms.createImageIcon("images/files_2/16x16/mail_write.png"));
        
        itemEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                jButtonEditGroupActionPerformed(e);
            }
        });

        itemDelete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                jButtonDeleteActionPerformed(e);
            }
        });

        
        itemMailTo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                jButtonSendMessageToGroupActionPerformed(e);
            }
        });
        
        jTablePopupMenu.add(itemEdit);
        jTablePopupMenu.add(itemDelete);

        if (displayJButtonSendMessageToGroup) {
            jTablePopupMenu.addSeparator();
            jTablePopupMenu.add(itemMailTo);            
        }
    }

   private void jXTableMessages_mousePressedOrReleased(MouseEvent e) {

            if (e.isPopupTrigger()) {
                showJXTablePopupMenu(e);
            }
    }

   private void jXTableMessages_mouseClicked(MouseEvent e) {

            if (e.getClickCount() >= 2) {
                jButtonEditGroupActionPerformed(null);
            }
     
   }
    /**
     * Display popup menu of the JXTreeTable
     * @param e
     */
    public void showJXTablePopupMenu(MouseEvent e) {
        if (jXTreeTable.getSelectedRowCount() != 0) {
            jTablePopupMenu.show(e.getComponent(),
                    e.getX(), e.getY());
        }
    }

    /**
     * Delete selected elements of jxtreetable
     * @throws Exception
     */
    private void deleteAction() throws Exception {
        int[] selectedRows = jXTreeTable.getSelectedRows();
        if (jXTreeTable.getSelectedRows().length == 0) {
            return;
        }

        List<Integer> groupsToDelete = new ArrayList<Integer>();
        List<String> membersToRemove = new ArrayList<String>();
        //List elements to be deleted (Reverse order)
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            int rowIndex = selectedRows[i];
            GroupTreeTableElement selectedElement = (GroupTreeTableElement) jXTreeTable.getValueAt(rowIndex, 0);
            int id = selectedElement.getId();

            if (id > 0) {
                //All group must be deleted
                groupsToDelete.add(id);
            } else {
                //Only a menber of group must be deleted
                String email = selectedElement.getLabel();
                int groupId = selectedElement.getGroupId();
                membersToRemove.add("{" + groupId + "; " + email + "}");
            }
        }
        
        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        //Do the delete
        if (!groupsToDelete.isEmpty()) {
            String strGroup = groupsToDelete.toString();
            awakeFileSession.call("net.safester.server.GroupDeletor.deleteGroups", connection, userNumber, strGroup);
        }
        if (!membersToRemove.isEmpty()) {
            String strGroup = membersToRemove.toString();
            awakeFileSession.call("net.safester.server.GroupDeletor.removeGroupMembers", connection, userNumber, strGroup);
        }

        GroupHolder groupHolder = new GroupHolder(connection, userNumber);
        groupHolder.resetMap();
        groupHolder.reset();       
        GroupMemberCache.clearAll();
        
        createJXTreeTable();
                
        return;
    }


    /**
     * Edit selected groups
     */
    private void editGroup() {
        int[] selectedRows = jXTreeTable.getSelectedRows();
        List<Integer> groupsToEdit = new ArrayList<Integer>();
        //Get the list of selected group
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            int rowIndex = selectedRows[i];
            GroupTreeTableElement selectedElement = (GroupTreeTableElement) jXTreeTable.getValueAt(rowIndex, 0);
            int id = selectedElement.getId();

            if (id > 0) {
                //Group is selected
                groupsToEdit.add(id);
            } else {
                //member of a group is selected => add group member to selected groups
                int groupId = selectedElement.getGroupId();
                groupsToEdit.add(groupId);
            }
        }

        //Edit all selected groups
        for (Integer idGroup : groupsToEdit) {
            new GroupEditor(this, connection, userNumber, idGroup).setVisible(true);

        }
    }

    private void initCompany() {
        this.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
        this.setTitle(messagesManager.getMessage("group_list"));

        this.jButtonAddGroup.setText(messagesManager.getMessage("group_creation"));
        this.jButtonAddGroup.setToolTipText(messagesManager.getMessage("group_creation"));
        this.jButtonDelete.setText(messagesManager.getMessage("delete"));
        this.jButtonDelete.setToolTipText(messagesManager.getMessage("delete_selection"));
        this.jButtonEditGroup.setText(messagesManager.getMessage("edit_group"));
        this.jButtonEditGroup.setToolTipText(messagesManager.getMessage("edit_group"));
        this.jButtonSendMessageToGroup.setText(messagesManager.getMessage("mail_to"));
        this.jButtonSendMessageToGroup.setToolTipText(messagesManager.getMessage("mail_to"));
        this.jLabelSearch.setText(messagesManager.getMessage("search"));
        this.jMenu1.setText(messagesManager.getMessage("File"));
        this.jMenuItemClose.setText(messagesManager.getMessage("close"));
        this.jMenuItemNewGroup.setText(messagesManager.getMessage("group_creation"));
        this.jLabel1.setText(null);

        if (SystemUtils.IS_OS_MAC_OSX) {
            jMenuItemClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        } else {
            jMenuItemClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        }
                
        this.jTextFieldSearch.setText("");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {   
                jScrollPane1.remove(jTable1);
                createJXTreeTable();
            }
        });        
        
        /*
        try {
            this.jScrollPane1.remove(jTable1);
            createJXTreeTable();
        } catch (Exception e) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPaneNewCustom.showException(rootPane, e);
        }
        */
        
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        jTextFieldSearch.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                textFieldSearchKeyPressed(e);
            }
        });
        keyListenerAdder();

        if (! displayJButtonSendMessageToGroup) {
            jToolBar1.remove(jSeparatorSendMessageToGroup1);
            jToolBar1.remove(jButtonSendMessageToGroup);
            jToolBar1.remove(jSeparatorSendMessageToGroup2);
        }
        
        this.setLocationRelativeTo(this.getParent());
        
        SwingUtil.resizeJComponentsForNimbusAndMacOsX(rootPane);        
        SwingUtil.setBackgroundColor(this, Color.WHITE);
        
        jPanelToolbar.setBackground(new Color(240, 240, 240));        
    }

    /**
     * Method called when user press a key in JTextField
     * @param e
     */
    private void textFieldSearchKeyPressed(KeyEvent e) {
        searchInTable(jTextFieldSearch.getText() + KeyEvent.getKeyText(e.getKeyCode()));
    }

    /**
     * Search the first item in table while user is typing in JTextField
     * @param pattern String currently in JTextField
     */
    private void searchInTable(String pattern) {
        int rows = jXTreeTable.getRowCount();
        for (int i = 0; i < rows; i++) {
            String value = jXTreeTable.getModel().getValueAt(i, 0).toString();
            
            if (value == null) {
                continue;
            }

            if (value.toLowerCase().startsWith(pattern.toLowerCase())) {
                //When found a row matching select it and exit
                ListSelectionModel selectionModel = jXTreeTable.getSelectionModel();
                selectionModel.setSelectionInterval(i, i+1);
                Rectangle rect = jXTreeTable.getCellRect(i, 0, true);
                jXTreeTable.scrollRectToVisible(rect);
                return;
            }
        }
        //Nothing found unselect all
        jXTreeTable.clearSelection();
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

        int id = e.getID();
        if (id == KeyEvent.KEY_RELEASED) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_DELETE) {
                jButtonDeleteActionPerformed(null);
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
        }
    }

    /**
     * Load the group list
     * @return
     * @throws SQLException
     */
    public DefaultMutableTreeTableNode loadData() throws SQLException {
        DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode();

        //Get list of groups
        GroupHolder groupHolder = new GroupHolder(connection, userNumber);
        Map<Integer, String> groups = groupHolder.getMap();
        for (int groupId : groups.keySet()) {

            GroupTreeTableElement group = new GroupTreeTableElement();
            group.setId(groupId);
            String groupName = groups.get(groupId);
            group.setLabel(groupName);
            
            List<String> groupNameList = new ArrayList<String>();
            groupNameList.add(HtmlConverter.toHtml(groupName));
            List<GroupMemberLocal> members = new ArrayList<GroupMemberLocal>();
            GroupMemberListTransfert groupMemberListTransfert = new GroupMemberListTransfert(connection, userNumber, groupId);
            members = groupMemberListTransfert.getList();
            
            //List<String> members = groupHolder.getExpandedEmailsFromGroups(groupNameList);
            
            //Create tree element representing group            
            DefaultMutableTreeTableNode groupRoot = buildGroup(group, members);

            //Add group to tree
            root.add(groupRoot);
        }
        return root;
    }

    /**
     * Open a message composer JFrame with selected groups has recipients
     */
    private void buildMessageForGroup() {
        try {

            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Set<String> selectedGroups = new HashSet<String>();
            GroupHolder groupHolder = new GroupHolder(connection, userNumber);
            Map<Integer, String> groups = groupHolder.getMap();
            //Get selected groups
            int[] selectedRows = jXTreeTable.getSelectedRows();
            if (jXTreeTable.getSelectedRows().length == 0) {
                return;
            }

            for (int rowIndex : selectedRows) {
                GroupTreeTableElement selectedElement = (GroupTreeTableElement) jXTreeTable.getValueAt(rowIndex, 0);
                int id = selectedElement.getId();

                if (id > 0) {
                    selectedGroups.add(groups.get(id));
                } else {
                    int groupId = selectedElement.getGroupId();
                    selectedGroups.add(groups.get(groupId));
                }
            }

            //Build recipient list in a String
            String recipients = "";
            for (String groupName : selectedGroups) {
                recipients += groupName + "; ";
            }
            //Open Composer JFrame
            
            try {
                String userEmailAddr = new UserNumberGetterClient(connection).getLoginFromUserNumber(userNumber);
                new MessageComposer(this, userEmailAddr, userNumber, connection, recipients).setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(PhotoAddressBookUpdaterNew.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            this.setCursor(Cursor.getDefaultCursor());
        } catch (Exception e) {
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPaneNewCustom.showException(rootPane, e);
        }
    }

    public int getUserNumber() {
        return this.userNumber;
    }

    protected Connection getConnection(){
        return connection;
    }

    public JFrame getMain() {
        if(this.caller instanceof PhotoAddressBookUpdaterNew){
            return ((PhotoAddressBookUpdaterNew)caller).getCaller();
        }
        return null;
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
        jButtonAddGroup = new javax.swing.JButton();
        jButtonEditGroup = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jSeparatorSendMessageToGroup1 = new javax.swing.JToolBar.Separator();
        jButtonSendMessageToGroup = new javax.swing.JButton();
        jSeparatorSendMessageToGroup2 = new javax.swing.JToolBar.Separator();
        jPanel2 = new javax.swing.JPanel();
        jPanelWest = new javax.swing.JPanel();
        jPanelEasst = new javax.swing.JPanel();
        jPanelCenter = new javax.swing.JPanel();
        jPanelSep1 = new javax.swing.JPanel();
        jPanelSearch = new javax.swing.JPanel();
        jLabelSearch = new javax.swing.JLabel();
        jTextFieldSearch = new javax.swing.JTextField();
        jPanelSep2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanelSouth = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemNewGroup = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemClose = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jPanelToolbar.setLayout(new javax.swing.BoxLayout(jPanelToolbar, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        jToolBar1.setRollover(true);

        jButtonAddGroup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/users3_plus.png"))); // NOI18N
        jButtonAddGroup.setFocusable(false);
        jButtonAddGroup.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAddGroup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAddGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddGroupActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonAddGroup);

        jButtonEditGroup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/users3_edit.png"))); // NOI18N
        jButtonEditGroup.setFocusable(false);
        jButtonEditGroup.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonEditGroup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonEditGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditGroupActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonEditGroup);

        jButtonDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/users3_delete.png"))); // NOI18N
        jButtonDelete.setFocusable(false);
        jButtonDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonDelete);
        jToolBar1.add(jSeparatorSendMessageToGroup1);

        jButtonSendMessageToGroup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/32x32/mail_write.png"))); // NOI18N
        jButtonSendMessageToGroup.setFocusable(false);
        jButtonSendMessageToGroup.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSendMessageToGroup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSendMessageToGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendMessageToGroupActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonSendMessageToGroup);
        jToolBar1.add(jSeparatorSendMessageToGroup2);

        jPanel1.add(jToolBar1);

        jPanelToolbar.add(jPanel1);

        jPanel2.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel2.setOpaque(false);
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));
        jPanelToolbar.add(jPanel2);

        getContentPane().add(jPanelToolbar, java.awt.BorderLayout.NORTH);

        jPanelWest.setMaximumSize(new java.awt.Dimension(10, 32767));
        getContentPane().add(jPanelWest, java.awt.BorderLayout.WEST);

        jPanelEasst.setMaximumSize(new java.awt.Dimension(10, 32767));
        getContentPane().add(jPanelEasst, java.awt.BorderLayout.EAST);

        jPanelCenter.setLayout(new javax.swing.BoxLayout(jPanelCenter, javax.swing.BoxLayout.Y_AXIS));

        jPanelSep1.setMaximumSize(new java.awt.Dimension(10, 10));

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

        jPanelSearch.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelSearch.setText("jLabelSearch");
        jPanelSearch.add(jLabelSearch);

        jTextFieldSearch.setText("jTextFieldSearch");
        jTextFieldSearch.setPreferredSize(new java.awt.Dimension(200, 22));
        jPanelSearch.add(jTextFieldSearch);

        jPanelCenter.add(jPanelSearch);

        jPanelSep2.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSep2.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelSep2.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanelSep2Layout = new javax.swing.GroupLayout(jPanelSep2);
        jPanelSep2.setLayout(jPanelSep2Layout);
        jPanelSep2Layout.setHorizontalGroup(
            jPanelSep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanelSep2Layout.setVerticalGroup(
            jPanelSep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelCenter.add(jPanelSep2);

        jScrollPane1.setBorder(null);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jPanelCenter.add(jScrollPane1);

        getContentPane().add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanel4.setPreferredSize(new java.awt.Dimension(419, 48));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jPanel6.setMaximumSize(new java.awt.Dimension(9, 9));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel6);

        jPanelSouth.setPreferredSize(new java.awt.Dimension(400, 48));
        jPanelSouth.setLayout(new javax.swing.BoxLayout(jPanelSouth, javax.swing.BoxLayout.Y_AXIS));

        jPanel5.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanel5.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanel5.setPreferredSize(new java.awt.Dimension(2, 10));
        jPanelSouth.add(jPanel5);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setText("jLabel1");
        jPanel3.add(jLabel1);

        jPanelSouth.add(jPanel3);

        jPanel8.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanel8.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanel8.setPreferredSize(new java.awt.Dimension(2, 10));
        jPanelSouth.add(jPanel8);

        jPanel4.add(jPanelSouth);

        jPanel7.setMaximumSize(new java.awt.Dimension(9, 9));
        jPanel7.setPreferredSize(new java.awt.Dimension(9, 10));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel7);

        getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

        jMenu1.setText("File");

        jMenuItemNewGroup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files_2/16x16/users3_plus.png"))); // NOI18N
        jMenuItemNewGroup.setText("jMenuItemNewGroup");
        jMenuItemNewGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewGroupActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemNewGroup);
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

    private void jMenuItemNewGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewGroupActionPerformed
        new GroupEditor(this, connection, userNumber).setVisible(true);
}//GEN-LAST:event_jMenuItemNewGroupActionPerformed

    private void jMenuItemCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCloseActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        close();
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_jMenuItemCloseActionPerformed

    private void jButtonAddGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddGroupActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new GroupEditor(this, connection, userNumber).setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_jButtonAddGroupActionPerformed

    private void jButtonEditGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditGroupActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        editGroup();
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_jButtonEditGroupActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        try {
            
            String message = messagesManager.getMessage("are_you_sure_to_delete_group");
            String deleteFile = messagesManager.getMessage("delete_group");

            int response = JOptionPane.showConfirmDialog(this, message, deleteFile, JOptionPane.YES_NO_OPTION);
            if (response != JOptionPane.YES_OPTION) {
                return;
            }            
            
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            deleteAction();
            this.setCursor(Cursor.getDefaultCursor());
        } catch (Exception e) {
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPaneNewCustom.showException(rootPane, e);
        }
}//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonSendMessageToGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendMessageToGroupActionPerformed
        buildMessageForGroup();
}//GEN-LAST:event_jButtonSendMessageToGroupActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new GroupListNew().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddGroup;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonEditGroup;
    private javax.swing.JButton jButtonSendMessageToGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelSearch;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemClose;
    private javax.swing.JMenuItem jMenuItemNewGroup;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelEasst;
    private javax.swing.JPanel jPanelSearch;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSouth;
    private javax.swing.JPanel jPanelToolbar;
    private javax.swing.JPanel jPanelWest;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparatorSendMessageToGroup1;
    private javax.swing.JToolBar.Separator jSeparatorSendMessageToGroup2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextFieldSearch;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables



}
