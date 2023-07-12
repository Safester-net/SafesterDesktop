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
package com.swing.util.CustomJtree;

/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


import com.swing.util.LookAndFeelHelper;
import com.swing.util.SwingUtil;
import com.swing.util.CustomJtree.dragdrop.TreeDataExtractor;
import com.swing.util.CustomJtree.dragdrop.TreeTransferHandler;

import net.safester.application.Main;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.FoldersHandler;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.FolderListTransfer;
import net.safester.noobs.clientserver.FolderLocal;

public class CustomJTree extends JPanel
        implements
        ActionListener {

    public static boolean DEBUG = false;

    //Action Commands
    private static String ADD_COMMAND = "add";
    private static String REMOVE_COMMAND = "remove";
    private static String RENAME_COMMAND = "rename";

    protected DefaultMutableTreeNode rootNode;
    private MessagesManager messages = new MessagesManager();
    protected DefaultTreeModel treeModel;
    protected JTree tree;

    private Toolkit toolkit = Toolkit.getDefaultToolkit();

    //Popup menu
    JPopupMenu popup;
    JMenuItem addItem;
    JMenuItem removeItem;
    JMenuItem renameItem;

    //Calling Frame
    private JFrame parent;
    //Current user number
    private int userNumber;
    Connection connection;

    /** if true the tree is accessible. If false: the tree can not be modifed */
    private boolean treeEnabled = true;

    /**
     * Default Constructor
     */
    public CustomJTree(JFrame parent, Connection theConnection, int theUserNumber) {
        super(new GridLayout(1, 0, 10, 10));

        this.parent = parent;
        this.userNumber = theUserNumber;
        this.connection = theConnection;

        initComponent();
    }


    /**
     * @return the isTreeEnabled
     */
    public boolean isTreeEnabled() {
        return treeEnabled;
    }

    /**
     * method to allow tree enabled from other composants
     * @param enabled   if true, composant is enabled
     */
    public void setTreeEnabled(boolean enabled) {
        //this.tree.setEnabled(enabled);
        this.treeEnabled = enabled;
        this.tree.setFocusable(enabled);
    }

    /**
     * Graphic Tree initialisation
     * @throws HeadlessException
     */
    private void initComponent() throws HeadlessException {

        MessagesManager messages = new MessagesManager();
        String rootNodeText = messages.getMessage("messages");
        
//        if (SystemUtils
//                .IS_OS_LINUX || UI_Util.isNimbus()) {
//            rootNodeText = "";
//        }
        
        rootNode = new DefaultMutableTreeNode(rootNodeText);
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree();
        tree.setLargeModel(true); // VERY IMPORTANT FOR MAC OS X 
        
        tree.setModel(treeModel);

        tree.setBackground(LookAndFeelHelper.getDefaultBackgroundColor());
        
        FolderTreeCellRendererNew myRenderer = new FolderTreeCellRendererNew(tree);
        tree.setCellRenderer(myRenderer);
        tree.setEditable(true);

        //HACK NDP BEGIN
        tree.setDragEnabled(true);
        tree.setDropMode(DropMode.ON_OR_INSERT);
        tree.setTransferHandler(new TreeTransferHandler((Main)parent, userNumber, connection));
        //HACK NDP END
        
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                
        tree.setShowsRootHandles(true);
        tree.setEditable(false);
        tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                //if (!tree.isEnabled()) {
                if (! treeEnabled) {
                    e.consume();
                    return;
                }
                showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                //if (!tree.isEnabled()) {
                if (!treeEnabled) {
                    e.consume();
                    return;
                }

                showPopup(e);
            }
        });

        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(null);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane);

        Icon folderOpen = Parms.createImageIcon("images/files_2/16x16/folder_open_plus.png");
        Icon delete = Parms.createImageIcon("images/files_2/16x16/delete.png");
        
        popup = new JPopupMenu();
        addItem = new JMenuItem(messages.getMessage("add_folder"));
        addItem.setActionCommand(ADD_COMMAND);
        addItem.addActionListener(this);
        addItem.setIcon(folderOpen);
        popup.add(addItem);
        renameItem = new JMenuItem(messages.getMessage("rename"));
        renameItem.setActionCommand(RENAME_COMMAND);
        renameItem.addActionListener(this);
        popup.add(renameItem);
        removeItem = new JMenuItem(messages.getMessage("delete"));
        removeItem.setActionCommand(REMOVE_COMMAND);
        removeItem.addActionListener(this);
        removeItem.setIcon(delete);
        popup.add(removeItem);

        keyListenerAdder();
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

            if (keyCode == KeyEvent.VK_F2) {
                renameAction();
            } else if (keyCode == KeyEvent.VK_DELETE) {
                removeAction();
            } else if (keyCode == KeyEvent.VK_N && e.isControlDown() && e.isShiftDown()) {
                addAction();
            }

        }
    }


    /**
     * Display pop up menu
     * @param e
     */
    private void showPopup(MouseEvent e) {
        JTree theTree = (JTree) e.getSource();
        if (SwingUtilities.isRightMouseButton(e)) {
            int row = theTree.getClosestRowForLocation(e.getX(), e.getY());
            theTree.setSelectionRow(row);
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) theTree.getSelectionPath().getLastPathComponent();
            Object userObject = selectedNode.getUserObject();
            
            if (userObject instanceof FolderLocal) {
                int folderId = ((FolderLocal) userObject).getFolderId();

                if (! Parms.folderRemovable(folderId)) {
                    
                    if (Parms.folderAddable(folderId)) {
                        addItem.setEnabled(true);
                    }
                    else {
                        addItem.setEnabled(false);
                    }
                    //Cannot remove the system folders
                    removeItem.setEnabled(false);
                    renameItem.setEnabled(false);
                } else {
                    addItem.setEnabled(true);
                    removeItem.setEnabled(true);
                    renameItem.setEnabled(true);
                }
            } else {
                //We are on the root folder cannot change it!
                addItem.setEnabled(true);
                removeItem.setEnabled(false);
                renameItem.setEnabled(false);
            }
        }
        if (e.isPopupTrigger()) {
            popup.show(theTree, e.getX(), e.getY());
        }
    }

    /**
     * Constructor allowing to show / hide root node
     * @param showRoot
     */
    public CustomJTree(JFrame parent, Connection theConnection, int theUserNumber, boolean showRoot) {
        this(parent, theConnection, theUserNumber);
        this.tree.setRootVisible(showRoot);
    }

    /** Add child to the currently selected node.
     * @param child
     * @return  */
    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
        //Get current selection path
        TreePath parentPath = tree.getSelectionPath();

        if (parentPath == null) {
            // Current selection path is null consider using root
            parentNode = rootNode;
        } else {
            //get current selected node
            parentNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }

    /**
     * Add a child to a folder
     * @param parent
     * @param child
     * @param shouldBeVisible
     * @return
     */
    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
            Object child,
            boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode =
                new DefaultMutableTreeNode(child, true);

        if (parent == null) {
            //if no parent ==> Use root as parent
            parent = rootNode;
        }


        //It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
        treeModel.insertNodeInto(childNode, parent,
                parent.getChildCount());


        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    /**
     * Get root node
     * @return
     */
    public DefaultMutableTreeNode getRootNode() {
        return rootNode;
    }

    /**
     * Get JTree object
     * @return
     */
    public JTree getTree() {
        return tree;
    }

    /**
     * Set Jtree Object
     * @param tree
     */
    public void setTree(JTree tree) {
        this.tree = tree;
    }

    /**
     * Display / hide root node
     * @param show
     */
    public void showRoot(boolean show) {
        tree.setRootVisible(show);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (ADD_COMMAND.equals(command)) {
            //Add button clicked
            addAction();
        } else if (REMOVE_COMMAND.equals(command)) {
            //Remove button clicked
            removeAction();
        } else if (RENAME_COMMAND.equals(command)) {
            //Rename button clicked
            renameAction();
        }
    }

    /**
     * Creates a new folder (using a JDialog)
     */
    private void addAction() {
        new TreeNodeAdder(parent, connection, this.userNumber, this, true).setVisible(true);
    }

    /**
     * Remove a folder
     */
    private void removeAction() {

        String text = messages.getMessage("confirm_folder_delete");
        String title = messages.getMessage("warning");

        int result = JOptionPane.showConfirmDialog(this, text, title, JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.NO_OPTION) {
            return;
        }
        TreePath currentSelection = tree.getSelectionPath();

        if (currentSelection != null) {
            //Get current selected node & parent
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)(currentSelection.getLastPathComponent());
            MutableTreeNode parentNode = (MutableTreeNode)(currentNode.getParent());
            FolderLocal folder = null;

            if (currentNode.getUserObject() instanceof FolderLocal) {
                folder = (FolderLocal) currentNode.getUserObject();
            } else {
                //Not a folder do nothing
                return;
            }

            int idFolder = folder.getFolderId();

            //Current folder must be removable & not root folder
            if (parentNode != null && Parms.folderRemovable(idFolder)) {
                try {

                    debug("removeAction 1");

                    //Remove graphic representation must be done before updating data!
                    treeModel.removeNodeFromParent(currentNode);
                    treeModel.reload(parentNode); // refresh the graphic part
                    
                    //Rebuild list and save it
                    TreeDataExtractor treeDataExtractor = new TreeDataExtractor(rootNode);
                    List<FolderLocal>  folderLocals = treeDataExtractor.getFolderLocalList();

                    Main safeShareItMain = (Main) parent;

                    debug("removeAction 2");

                    FolderListTransfer folderListTransfer = new FolderListTransfer(connection, userNumber);

                    // Key id is passed for server security check, because messages will be deleted
                    folderListTransfer.putListAndDelete(folderLocals, safeShareItMain.getKeyId());

                    debug("removeAction 3");

                    safeShareItMain.clearCache();

                    debug("removeAction 4");

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPaneNewCustom.showException(parent, ex);
                }
                return;
            }

            //Tell user that trying removing a system folder is a evil!
            JOptionPane.showMessageDialog(tree,
            messages.getMessage("cannot_remove_system_folder"));
        }

        // Either there was no selection, or the root was selected.
        toolkit.beep();
    }

    /**
     * Rename a folder using a JDialog
     */
    private void renameAction() {
        //Get current folder name
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) this.tree.getSelectionPath().getLastPathComponent();
        if (currentNode.getUserObject() instanceof FolderLocal) {
            FolderLocal currentFolder = (FolderLocal) currentNode.getUserObject();

            int folderId = currentFolder.getFolderId();

            // NO! USE CLEAN CODE to TEST IF A FOLDER IS OK TO DELETE!
            //if (currentFolder.getFolderId() != Parms.INBOX_ID && currentFolder.getFolderId() != Parms.OUTBOX_ID && currentFolder.getFolderId() != Parms.DRAFT_ID) {
            if (Parms.folderRemovable(folderId)) {
                 String folderName = currentFolder.getName();
                // Open JDialog
                new TreeNodeRenamer(parent, connection, userNumber, this, folderName, true).setVisible(true);
            }
        }
    }

    public DefaultMutableTreeNode searchFolder(int folderId) {
        DefaultMutableTreeNode node = null;
        Enumeration<?> e = getRootNode().breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            node = (DefaultMutableTreeNode) e.nextElement();

            if (node.getUserObject() instanceof FolderLocal) {
                FolderLocal  folder = (FolderLocal) node.getUserObject();
                if (folder.getFolderId() == folderId) {
                    return node;
                }
            }
        }

        return null;
    }

    public void addTreeSelectionListener(CustomJTreeSelectionListener customJTreeSelectionListener) {
        this.tree.addTreeSelectionListener(customJTreeSelectionListener);
    }

    /**
     * Init the Jtree representing folders
     */
    public static CustomJTree initJTree(JFrame frame, Connection theConnection, FoldersHandler foldersHandler, int userNumber) {
        //Get the list of root folder
        List<FolderLocal> rootFolders = foldersHandler.getRootFolders();
        CustomJTree customJtree = new CustomJTree(frame, theConnection, userNumber, true);
        
        for (FolderLocal folder : rootFolders) {

            //add each root folder
            customJtree.getTree().setSelectionPath(new TreePath(customJtree.getRootNode()));
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(folder, true);

            treeNode = customJtree.addObject(treeNode);
            treeNode.setUserObject(folder);

            //Add children of folder
            if (folder.getChildren() != null) {
                addChildren(customJtree, treeNode, folder.getChildren(), foldersHandler);
            }
        }

        //Select inbox
        DefaultMutableTreeNode inBoxFolder = customJtree.searchFolder(Parms.INBOX_ID);
        customJtree.getTree().setSelectionPath(new TreePath(inBoxFolder.getPath()));

        customJtree.addTreeSelectionListener(new CustomJTreeSelectionListener((Main) frame, customJtree.getTree()));
        
        customJtree.getTree().updateUI();
        return customJtree;

    }

    /**
     * Add children of folder
     * @param parent        The parent folder
     * @param childs        List of ids of children folders
     */
    private static void addChildren(CustomJTree jTree, DefaultMutableTreeNode parent, List<Integer> childs, FoldersHandler foldersHandler) {

        for (Integer folderId : childs) {
            //Get folder
            FolderLocal folder = foldersHandler.getFolder(folderId);

            if (folder == null)
            {
                continue;
            }
            
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(folder, true);
            jTree.getTree().setSelectionPath(new TreePath(parent.getPath()));
            treeNode = jTree.addObject(parent, folder, true);
            treeNode.setUserObject(folder);
            
            jTree.getTree().updateUI();
            
            //Add children of folders
            if (folder.getChildren() != null) {
                addChildren(jTree, treeNode, folder.getChildren(), foldersHandler);
            }
        }
    }

    /**
     * debug tool
     */
    private static void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
    }

}
