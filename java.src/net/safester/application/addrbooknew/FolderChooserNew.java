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
package net.safester.application.addrbooknew;

import static net.safester.application.addrbooknew.AddressBookImportCsv1.ADDR_HEIGHT;
import static net.safester.application.addrbooknew.AddressBookImportCsv1.ADDR_WIDTH;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.moyosoft.connector.com.ComponentObjectModelException;
import com.moyosoft.connector.exception.LibraryNotFoundException;
import com.moyosoft.connector.ms.outlook.Outlook;
import com.moyosoft.connector.ms.outlook.folder.FolderType;
import com.moyosoft.connector.ms.outlook.folder.FoldersCollection;
import com.moyosoft.connector.ms.outlook.folder.OutlookFolder;
import com.moyosoft.connector.ms.outlook.folder.OutlookFolderID;
import com.moyosoft.samples.outlook.folderchooser.FolderTreeNode;
import com.moyosoft.samples.outlook.folderchooser.LocalCellRenderer;
import com.moyosoft.samples.outlook.folderchooser.OutlookFolderRootNode;
import com.moyosoft.samples.outlook.gui.Icons;
import com.swing.util.SwingUtil;

import net.safester.application.addrbooknew.outlook.OutlookRecipientEntriesBuilder;
import net.safester.application.addrbooknew.outlook.OutlookUtilMoyosoft;
import net.safester.application.addrbooknew.tools.CryptAppUtil;
import net.safester.application.addrbooknew.tools.ProcessUtil;
import net.safester.application.addrbooknew.tools.SessionUtil;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.ImageParmsUtil;
import net.safester.application.parms.Parms;
import net.safester.application.tool.ButtonResizer;
import net.safester.application.tool.WindowSettingManager;

/**
 * 
 * @author Nicolas de Pomereu
 */
public class FolderChooserNew extends javax.swing.JDialog implements TreeSelectionListener,
        TreeWillExpandListener {

    public static final String CR_LF = System.getProperty("line.separator");

    private Outlook outlook = null;
    private OutlookFolder mSelectedFolder = null;
    private FoldersCollection mRootFolders = null;

    private Connection connection = null;
    private int userNumber = -1;
    
    /**
     * Add a clipboard manager for content management
     */
    private java.awt.Window parent = null;


//    public FolderChooserNew(FoldersCollection pFolders) {
//        super();
//        mRootFolders = pFolders;
//        initialize();
//    }

    public FolderChooserNew(Window parent, Connection connection, int userNumber) throws ComponentObjectModelException, LibraryNotFoundException {
        super(parent);
        this.parent = parent;
        this.connection = connection;
        this.userNumber = userNumber;
        outlook = new Outlook();
        mRootFolders = outlook.getFolders();
        initialize();
    }

    /**
     * This is the method to include in *our* constructor
     */
    public void initialize() {

        initComponents();

        this.setSize(AddressBookImportCsv1.ADDR_WIDTH, AddressBookImportCsv1.ADDR_HEIGHT);
        this.setPreferredSize(new Dimension(ADDR_WIDTH, ADDR_HEIGHT));

        try {
            this.setIconImage(ImageParmsUtil.getAppIcon());
        } catch (RuntimeException e1) {
            e1.printStackTrace();
        }

        this.setModal(true);
        jCheckBoxDisplayFirstNameBefore.setSelected(true);
        
        jLabelTitle.setText(MessagesManager.get("importing_contacts_from_outlook"));
        jLabelHelp.setText(MessagesManager.get("select_outlook_contact_folder_to_import"));
        jLabelSelectedFolder.setText(MessagesManager.get("selected_folder"));
        jCheckBoxDisplayFirstNameBefore.setText(MessagesManager.get("insert_first_name_before_last"));
        
        jButtonNext.setText(MessagesManager.get("next") + " >");
        jButtonCancel.setText(MessagesManager.get("cancel"));
                
        //DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        LocalCellRenderer renderer = new LocalCellRenderer(mMainTree);
        
        //DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(Icons.FOLDER_CLOSED_ICON);
        renderer.setClosedIcon(Icons.FOLDER_CLOSED_ICON);
        renderer.setOpenIcon(Icons.FOLDER_OPEN_ICON);

        mMainTree.setShowsRootHandles(true);
        mMainTree.setRootVisible(false);
        mMainTree.setCellRenderer(renderer);
        mMainTree.addMouseListener(new FolderPopupMenuNew(this));

        
   
        try {
            FolderTreeNode rootNode = new OutlookFolderRootNode(mRootFolders);
            rootNode.createChildrens();

            mMainTree.setModel(new DefaultTreeModel(rootNode));
            mMainTree.expandPath(new TreePath(new Object[]{rootNode}));
        } catch (ComponentObjectModelException e) {
            e.printStackTrace();
        }

        mMainTree.addTreeWillExpandListener(this);
        mMainTree.addTreeSelectionListener(this);

        mMainTree.expandRow(0);
        
        setSelectionPathToDefaultContact();
        
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = mMainTree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = mMainTree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    if (e.getClickCount() == 1 || e.getClickCount() == 2) {
                        jTextFieldSelectedFolder.setText(selPath.getLastPathComponent().toString());
                    } 
                }
            }
        };
        mMainTree.addMouseListener(ml);
        
        jTextFieldNameFirstname.setText(null);
        jCheckBoxDisplayFirstNameBeforeItemStateChanged(null);
        CryptAppUtil.setTextFieldAsLabel(jTextFieldNameFirstname);
        
        // Our window listener for all events
        // If window is closed ==> call close()
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeOnExit();
            }
        });

//        this.addComponentListener(new ComponentAdapter() {
//            public void componentMoved(ComponentEvent e) {
//                saveSettings();
//            }
//
//            public void componentResized(ComponentEvent e) {
//                saveSettings();
//            }
//        });
        
        this.keyListenerAdder();

        ButtonResizer buttonResizer = new ButtonResizer(jPanelButtons);
        buttonResizer.setWidthToMax();

        SwingUtil.applySwingUpdates(rootPane);

        WindowSettingManager.load(this);
        
        this.setTitle(jLabelTitle.getText());

        pack();

    }

    /**
     * Sets the selection path to the default Contact folder
     */
    private void setSelectionPathToDefaultContact() {
        
        OutlookFolder defaultContactFolder = outlook.getDefaultFolder(FolderType.CONTACTS);
        jTextFieldSelectedFolder.setText(defaultContactFolder.getName());
        
        TreeModel model = mMainTree.getModel();
        
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
        // Just changing enumeration kind here
        Enumeration<DefaultMutableTreeNode> en = rootNode.preorderEnumeration();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = en.nextElement();
            
            FolderTreeNode folderTreeNode = (FolderTreeNode) node;
            //System.out.println("folderTreeNode: " + folderTreeNode + ":");
            //System.out.println("getFolder: " + folderTreeNode.getFolder() + ":");
            
            TreeNode[] path = node.getPath();
            
            if (folderTreeNode.getFolder() != null && defaultContactFolder != null) {
                OutlookFolderID id =  folderTreeNode.getFolder().getFolderId();
                //System.out.println("ID: " + id);
                
                TreePath treePath = new TreePath(path);
                
                if (id.equals(defaultContactFolder.getFolderId())) {
                    //System.out.println("equal:" );
                    mMainTree.setSelectionPath(treePath);
                }
            }
            
            // System.out.println((node.isLeaf() ? "  - " : "+ ") + path[path.length - 1]);
        }
    }

    private void closeOnExit() {
        cancelPressed();
    }

    /**
     * Universal key listener
     */
    private void keyListenerAdder() {
        List<Component> components = SwingUtil.getAllComponants(this);

        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);

            comp.addKeyListener(new KeyAdapter() {
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
                doNext();
            }
                        
            if (keyCode == KeyEvent.VK_ESCAPE) {
                cancelPressed();
            }
        }
    }

//    public void saveSettings() {
//        WindowSettingMgr.save(this);
//    }

    protected JTree getTree() {
        return mMainTree;
    }

    public OutlookFolder getChoosedFolder() {
        return mSelectedFolder;
    }
   
    protected OutlookFolder getSelectedFolder() {
        return getFolderForPath(mMainTree.getSelectionPath());
    }

    protected OutlookFolder getFolderForPath(TreePath pPath) {
        if (pPath == null) {
            return null;
        }

        return ((FolderTreeNode) (pPath.getLastPathComponent())).getFolder();
    }

    protected void expandSelectedItem() {
        TreePath path = mMainTree.getSelectionPath();

        if (path != null) {
            mMainTree.expandPath(path);
        }
    }

    protected void collapseSelectedItem() {
        TreePath path = mMainTree.getSelectionPath();

        if (path != null) {
            mMainTree.collapsePath(path);
        }
    }

    protected void refreshNode(FolderTreeNode pNode) {
        if (pNode != null) {
            try {
                pNode.refresh();
                ((DefaultTreeModel) mMainTree.getModel()).reload(pNode);
            } catch (ComponentObjectModelException e) {
                e.printStackTrace();
            }
        }
    }

    protected void refreshSelectedItem() {
        TreePath path = mMainTree.getSelectionPath();

        if (path != null) {
            refreshNode((FolderTreeNode) path.getLastPathComponent());
        }
    }

    protected void refreshAll() {
        FolderTreeNode rootNode = (FolderTreeNode) mMainTree.getModel()
                .getRoot();

        if (rootNode != null) {
            for (int i = 0; i < rootNode.getChildCount(); i++) {
                refreshNode((FolderTreeNode) rootNode.getChildAt(i));
            }
        }
    }

    protected TreePath getSelectedPath() {
        return mMainTree.getSelectionPath();
    }

    protected void removeSelectionPath(TreePath pPath) {
        if (pPath != null) {
            FolderTreeNode node = (FolderTreeNode) pPath.getLastPathComponent();

            ((DefaultTreeModel) mMainTree.getModel())
                    .removeNodeFromParent(node);

            refreshAll();
        }
    }

    public void cancelPressed() {
        mSelectedFolder = null;
        
        OutlookUtilMoyosoft.outlookDispose(outlook);
        //saveSettings(); NO: import windows to stay same place & size
        this.dispose();
    }

    public void doNext() {
        
        try {
            if (!ProcessUtil.isWindowsProgramRunning("outlook")) {
                JOptionPane.showMessageDialog(null, MessagesManager.get("outlook_office_closed_restart_import"), Parms.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
                OutlookUtilMoyosoft.outlookDispose(outlook);
                this.dispose();
                return;
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Impossible to load Contacts from Outlook Office " + CR_LF
                    + SessionUtil.getCleanErrorMessage(ex), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            OutlookUtilMoyosoft.outlookDispose(outlook);
            this.dispose();
            return;
        }
        
        mSelectedFolder = getSelectedFolder();
        
        if (mSelectedFolder == null) {
            JOptionPane.showMessageDialog(this, MessagesManager.get("please_select_a_folder"), Parms.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
              
        List<RecipientEntry> recipientEntries = new ArrayList<>();
        
        try {
            this.setCursor(Cursor
                    .getPredefinedCursor(Cursor.WAIT_CURSOR));

            OutlookRecipientEntriesBuilder outlookRecipientEntriesBuilder = new OutlookRecipientEntriesBuilder(mSelectedFolder, jCheckBoxDisplayFirstNameBefore.isSelected());
            recipientEntries = outlookRecipientEntriesBuilder.build();
        
            this.setCursor(Cursor.getDefaultCursor());
        } catch (Exception ex) {
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, "Impossible to acess Outlook Office data. " + CR_LF
                    + SessionUtil.getCleanErrorMessage(ex), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
        }
        finally {
            OutlookUtilMoyosoft.outlookDispose(outlook);
        }
                
         // Ok, now retrieve all contacts in folders
         this.dispose();
         AddressBookImportFinal addressBookImportFinal 
                 = new AddressBookImportFinal(parent, recipientEntries, this.connection, this.userNumber);
        
    }

    public void treeWillCollapse(TreeExpansionEvent event)
            throws ExpandVetoException {
    }

    public void treeWillExpand(TreeExpansionEvent event)
            throws ExpandVetoException {
        if (event.getSource() == mMainTree) {
            Object o = event.getPath().getLastPathComponent();

            if (o instanceof FolderTreeNode) {
                ((FolderTreeNode) o).createChildrens();
            }
        }
    }

    public void valueChanged(TreeSelectionEvent e) {
    }

    public static OutlookFolder open(Dialog pParentDialog) throws ComponentObjectModelException, LibraryNotFoundException {
        final FolderChooserNew dlg = new FolderChooserNew(pParentDialog, null, -1);

        //dlg.setModal(true);
        dlg.setVisible(true);

        return dlg.getChoosedFolder();
    }

//    public static OutlookFolder open(FoldersCollection pFolders) {
//        final FolderChooserNew dlg = new FolderChooserNew(pFolders);
//
//        dlg.setModal(true);
//        dlg.setVisible(true);
//
//        return dlg.getChoosedFolder();
//    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelSep1 = new javax.swing.JPanel();
        jPanelNorth = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanelSep2 = new javax.swing.JPanel();
        jPanelSepLine2New = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel25 = new javax.swing.JPanel();
        jPanelHRight = new javax.swing.JPanel();
        jPanelHelpLeft1 = new javax.swing.JPanel();
        jLabelHelp = new javax.swing.JLabel();
        jPanelTree = new javax.swing.JPanel();
        jPanelTreeLeft = new javax.swing.JPanel();
        jPanelTreeMain = new javax.swing.JPanel();
        scrollPanel = new javax.swing.JScrollPane();
        mMainTree = new javax.swing.JTree();
        jPanelHelpRight = new javax.swing.JPanel();
        jPanelSepBlanc4 = new javax.swing.JPanel();
        jPanelSelectedFolder = new javax.swing.JPanel();
        jPanelSepBlank12 = new javax.swing.JPanel();
        jLabelSelectedFolder = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jTextFieldSelectedFolder = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jPanelSepBlanc2 = new javax.swing.JPanel();
        jPanelCheckBoxInsertName = new javax.swing.JPanel();
        jPanelSepBlank11 = new javax.swing.JPanel();
        jCheckBoxDisplayFirstNameBefore = new javax.swing.JCheckBox();
        jPanelSepBlank13 = new javax.swing.JPanel();
        jTextFieldNameFirstname = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jPanelSepBlanc3 = new javax.swing.JPanel();
        jPanelSepLine2New1 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel27 = new javax.swing.JPanel();
        jPanelBottom = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonNext = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Aide");
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelSep1.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanelSep1Layout = new javax.swing.GroupLayout(jPanelSep1);
        jPanelSep1.setLayout(jPanelSep1Layout);
        jPanelSep1Layout.setHorizontalGroup(
            jPanelSep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSep1Layout.setVerticalGroup(
            jPanelSep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelSep1);

        jPanelNorth.setMaximumSize(new java.awt.Dimension(32767, 72));
        jPanelNorth.setLayout(new javax.swing.BoxLayout(jPanelNorth, javax.swing.BoxLayout.LINE_AXIS));

        jPanel5.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel5.setPreferredSize(new java.awt.Dimension(10, 11));

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

        jPanelNorth.add(jPanel5);

        jLabelTitle.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/safester/application/images/files/icons8-ms-outlook-32.png"))); // NOI18N
        jLabelTitle.setText("Importer des Contacts depuis Outlook Office");
        jPanelNorth.add(jLabelTitle);

        getContentPane().add(jPanelNorth);

        jPanelSep2.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanelSep2Layout = new javax.swing.GroupLayout(jPanelSep2);
        jPanelSep2.setLayout(jPanelSep2Layout);
        jPanelSep2Layout.setHorizontalGroup(
            jPanelSep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSep2Layout.setVerticalGroup(
            jPanelSep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelSep2);

        jPanelSepLine2New.setMaximumSize(new java.awt.Dimension(32787, 10));
        jPanelSepLine2New.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepLine2New.setLayout(new javax.swing.BoxLayout(jPanelSepLine2New, javax.swing.BoxLayout.LINE_AXIS));

        jPanel24.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine2New.add(jPanel24);
        jPanelSepLine2New.add(jSeparator2);

        jPanel25.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine2New.add(jPanel25);

        getContentPane().add(jPanelSepLine2New);

        jPanelHRight.setMaximumSize(new java.awt.Dimension(32767, 36));
        jPanelHRight.setPreferredSize(new java.awt.Dimension(329, 36));
        jPanelHRight.setLayout(new javax.swing.BoxLayout(jPanelHRight, javax.swing.BoxLayout.LINE_AXIS));

        jPanelHelpLeft1.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelHRight.add(jPanelHelpLeft1);

        jLabelHelp.setText("Sélectionnez le dossier des Contacts Outlook à importer");
        jPanelHRight.add(jLabelHelp);

        getContentPane().add(jPanelHRight);

        jPanelTree.setLayout(new javax.swing.BoxLayout(jPanelTree, javax.swing.BoxLayout.LINE_AXIS));

        jPanelTreeLeft.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelTree.add(jPanelTreeLeft);

        jPanelTreeMain.setPreferredSize(new java.awt.Dimension(319, 180));
        jPanelTreeMain.setLayout(new javax.swing.BoxLayout(jPanelTreeMain, javax.swing.BoxLayout.LINE_AXIS));

        scrollPanel.setViewportView(mMainTree);

        jPanelTreeMain.add(scrollPanel);

        jPanelTree.add(jPanelTreeMain);

        jPanelHelpRight.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelTree.add(jPanelHelpRight);

        getContentPane().add(jPanelTree);

        jPanelSepBlanc4.setMaximumSize(new java.awt.Dimension(32767, 8));
        jPanelSepBlanc4.setMinimumSize(new java.awt.Dimension(10, 8));
        jPanelSepBlanc4.setPreferredSize(new java.awt.Dimension(1000, 8));
        getContentPane().add(jPanelSepBlanc4);

        jPanelSelectedFolder.setMaximumSize(new java.awt.Dimension(32767, 35));
        jPanelSelectedFolder.setLayout(new javax.swing.BoxLayout(jPanelSelectedFolder, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSepBlank12.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepBlank12.setMinimumSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanelSepBlank12Layout = new javax.swing.GroupLayout(jPanelSepBlank12);
        jPanelSepBlank12.setLayout(jPanelSepBlank12Layout);
        jPanelSepBlank12Layout.setHorizontalGroup(
            jPanelSepBlank12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSepBlank12Layout.setVerticalGroup(
            jPanelSepBlank12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSelectedFolder.add(jPanelSepBlank12);

        jLabelSelectedFolder.setText("Dossier Sélectionné");
        jPanelSelectedFolder.add(jLabelSelectedFolder);

        jPanel1.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanel1.setMinimumSize(new java.awt.Dimension(5, 5));
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

        jPanelSelectedFolder.add(jPanel1);

        jTextFieldSelectedFolder.setEditable(false);
        jTextFieldSelectedFolder.setText("jTextFieldSelectedFolder");
        jTextFieldSelectedFolder.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextFieldSelectedFolder.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jTextFieldSelectedFolder.setPreferredSize(new java.awt.Dimension(300, 22));
        jPanelSelectedFolder.add(jTextFieldSelectedFolder);

        jPanel2.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel2.setPreferredSize(new java.awt.Dimension(10, 11));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelSelectedFolder.add(jPanel2);

        getContentPane().add(jPanelSelectedFolder);

        jPanelSepBlanc2.setMaximumSize(new java.awt.Dimension(32767, 8));
        jPanelSepBlanc2.setMinimumSize(new java.awt.Dimension(10, 8));
        jPanelSepBlanc2.setPreferredSize(new java.awt.Dimension(1000, 8));
        getContentPane().add(jPanelSepBlanc2);

        jPanelCheckBoxInsertName.setMaximumSize(new java.awt.Dimension(32767, 35));
        jPanelCheckBoxInsertName.setLayout(new javax.swing.BoxLayout(jPanelCheckBoxInsertName, javax.swing.BoxLayout.LINE_AXIS));

        jPanelSepBlank11.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanelSepBlank11.setMinimumSize(new java.awt.Dimension(10, 0));

        javax.swing.GroupLayout jPanelSepBlank11Layout = new javax.swing.GroupLayout(jPanelSepBlank11);
        jPanelSepBlank11.setLayout(jPanelSepBlank11Layout);
        jPanelSepBlank11Layout.setHorizontalGroup(
            jPanelSepBlank11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanelSepBlank11Layout.setVerticalGroup(
            jPanelSepBlank11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelCheckBoxInsertName.add(jPanelSepBlank11);

        jCheckBoxDisplayFirstNameBefore.setText("Insérer le prénom avant le nom");
        jCheckBoxDisplayFirstNameBefore.setToolTipText("");
        jCheckBoxDisplayFirstNameBefore.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxDisplayFirstNameBeforeItemStateChanged(evt);
            }
        });
        jPanelCheckBoxInsertName.add(jCheckBoxDisplayFirstNameBefore);

        jPanelSepBlank13.setMaximumSize(new java.awt.Dimension(5, 5));
        jPanelSepBlank13.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanelSepBlank13.setPreferredSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout jPanelSepBlank13Layout = new javax.swing.GroupLayout(jPanelSepBlank13);
        jPanelSepBlank13.setLayout(jPanelSepBlank13Layout);
        jPanelSepBlank13Layout.setHorizontalGroup(
            jPanelSepBlank13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        jPanelSepBlank13Layout.setVerticalGroup(
            jPanelSepBlank13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelCheckBoxInsertName.add(jPanelSepBlank13);

        jTextFieldNameFirstname.setText("jTextFieldNameFirstname");
        jTextFieldNameFirstname.setMaximumSize(new java.awt.Dimension(2147483647, 22));
        jTextFieldNameFirstname.setPreferredSize(new java.awt.Dimension(200, 22));
        jPanelCheckBoxInsertName.add(jTextFieldNameFirstname);

        jPanel3.setMaximumSize(new java.awt.Dimension(10, 10));
        jPanel3.setPreferredSize(new java.awt.Dimension(10, 11));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanelCheckBoxInsertName.add(jPanel3);

        getContentPane().add(jPanelCheckBoxInsertName);

        jPanelSepBlanc3.setMaximumSize(new java.awt.Dimension(32767, 8));
        jPanelSepBlanc3.setMinimumSize(new java.awt.Dimension(10, 8));
        jPanelSepBlanc3.setPreferredSize(new java.awt.Dimension(1000, 8));
        getContentPane().add(jPanelSepBlanc3);

        jPanelSepLine2New1.setMaximumSize(new java.awt.Dimension(32787, 10));
        jPanelSepLine2New1.setMinimumSize(new java.awt.Dimension(0, 10));
        jPanelSepLine2New1.setLayout(new javax.swing.BoxLayout(jPanelSepLine2New1, javax.swing.BoxLayout.LINE_AXIS));

        jPanel26.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine2New1.add(jPanel26);
        jPanelSepLine2New1.add(jSeparator3);

        jPanel27.setMaximumSize(new java.awt.Dimension(10, 10));

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        jPanelSepLine2New1.add(jPanel27);

        getContentPane().add(jPanelSepLine2New1);

        jPanelBottom.setMaximumSize(new java.awt.Dimension(32767, 5));
        jPanelBottom.setPreferredSize(new java.awt.Dimension(100, 5));

        javax.swing.GroupLayout jPanelBottomLayout = new javax.swing.GroupLayout(jPanelBottom);
        jPanelBottom.setLayout(jPanelBottomLayout);
        jPanelBottomLayout.setHorizontalGroup(
            jPanelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 411, Short.MAX_VALUE)
        );
        jPanelBottomLayout.setVerticalGroup(
            jPanelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 4, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelBottom);

        jPanelButtons.setMaximumSize(new java.awt.Dimension(32767, 33));
        jPanelButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 10));

        jButtonNext.setText("Suivant >");
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonNext);

        jButtonCancel.setText("Annuler");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonCancel);

        jPanel4.setMaximumSize(new java.awt.Dimension(0, 0));
        jPanel4.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel4.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanelButtons.add(jPanel4);

        getContentPane().add(jPanelButtons);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
        doNext();
    }//GEN-LAST:event_jButtonNextActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        cancelPressed();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jCheckBoxDisplayFirstNameBeforeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxDisplayFirstNameBeforeItemStateChanged
        
        if (jCheckBoxDisplayFirstNameBefore.isSelected()) {
            jTextFieldNameFirstname.setText("(Ex: John Smith)");
        } else {
            jTextFieldNameFirstname.setText("(Ex: Smith John)");
        }
    }//GEN-LAST:event_jCheckBoxDisplayFirstNameBeforeItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {
        try {
            
            //LoginFrame.setLookAndFeel();
            
            OutlookFolder folder = FolderChooserNew.open(null);

            if (folder != null) {
                System.out.println("Folder: " + folder.getName());
            }

        } catch (ComponentObjectModelException ex) {
            ex.printStackTrace();
        } catch (LibraryNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JCheckBox jCheckBoxDisplayFirstNameBefore;
    private javax.swing.JLabel jLabelHelp;
    private javax.swing.JLabel jLabelSelectedFolder;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelBottom;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelCheckBoxInsertName;
    private javax.swing.JPanel jPanelHRight;
    private javax.swing.JPanel jPanelHelpLeft1;
    private javax.swing.JPanel jPanelHelpRight;
    private javax.swing.JPanel jPanelNorth;
    private javax.swing.JPanel jPanelSelectedFolder;
    private javax.swing.JPanel jPanelSep1;
    private javax.swing.JPanel jPanelSep2;
    private javax.swing.JPanel jPanelSepBlanc2;
    private javax.swing.JPanel jPanelSepBlanc3;
    private javax.swing.JPanel jPanelSepBlanc4;
    private javax.swing.JPanel jPanelSepBlank11;
    private javax.swing.JPanel jPanelSepBlank12;
    private javax.swing.JPanel jPanelSepBlank13;
    private javax.swing.JPanel jPanelSepLine2New;
    private javax.swing.JPanel jPanelSepLine2New1;
    private javax.swing.JPanel jPanelTree;
    private javax.swing.JPanel jPanelTreeLeft;
    private javax.swing.JPanel jPanelTreeMain;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTextFieldNameFirstname;
    private javax.swing.JTextField jTextFieldSelectedFolder;
    private javax.swing.JTree mMainTree;
    private javax.swing.JScrollPane scrollPanel;
    // End of variables declaration//GEN-END:variables
}
