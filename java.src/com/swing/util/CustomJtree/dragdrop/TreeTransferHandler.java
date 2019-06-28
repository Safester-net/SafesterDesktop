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
package com.swing.util.CustomJtree.dragdrop;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.safester.application.Main;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.FolderListTransfer;
import net.safester.noobs.clientserver.FolderLocal;

/**
 * Class to handle JTree drag & Drop fro SafeShare Project.
 * Original code is here (use is explicitely authorized by post author)
 * http://www.coderanch.com/t/346509/GUI/java/JTree-drag-drop-inside-one
 * 
 * @author Stealer: Nicolas de Pomereu
 *
 */
public class TreeTransferHandler extends TransferHandler {

    public static boolean DEBUG = false;
    
    private static final long serialVersionUID = 1L;

    /** Current user number */
    private int userNumber;
    
    /** The remove JDBC connection (for updating folders & message */
    private Connection connection = null;
    
    /** instance needed for callback when moving Messages */
    private Main safeShareitMain = null;
    
    /** The dedicated Flavor for Nodes */
    private DataFlavor nodesFlavor;
    private DataFlavor[] flavors = new DataFlavor[1];
    private DefaultMutableTreeNode[] nodesToRemove;

    /**
     *  Constructor 
     * @param safeShareitMain   instance needed for callback when moving Messages
     * @param userNumber        the user number
     * @param connection        the JDBC Connection
     */
    public TreeTransferHandler(Main safeShareitMain, int userNumber, Connection connection) {

        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType
                    + ";class=\""
                    + javax.swing.tree.DefaultMutableTreeNode[].class.getName()
                    + "\"";
            nodesFlavor = new DataFlavor(mimeType);
            flavors[0] = nodesFlavor;

        } catch (ClassNotFoundException e) {
            JOptionPaneNewCustom.showException(null, e);
            System.out.println("ClassNotFound: " + e.getMessage());
        }
        
        this.safeShareitMain = safeShareitMain;
        this.userNumber = userNumber;
        this.connection = connection;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {

        if (!support.isDrop()) {
            return false;
        }
        support.setShowDropLocation(true);

        Transferable tr = support.getTransferable();
        DataFlavor[] allFlavors = tr.getTransferDataFlavors();

        for (DataFlavor dataFlavor : allFlavors) {
            //debug("flavor: " + dataFlavor.toString());

            if (!support.isDataFlavorSupported(dataFlavor)) {
                debug("canImport(): flavor not supported: " + dataFlavor);
                return false;
            }

            Object object = null;
            try {
                object = tr.getTransferData(dataFlavor);
                //debug ("Object to be transfered: " + o.getClass().getName()

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPaneNewCustom.showException(null, e, "Impossible to get transfer data");
                return false;
            }

            if (object instanceof DefaultMutableTreeNode) {

                // Do not allow a drop on the drag source selections.
                JTree.DropLocation dl =
                        (JTree.DropLocation) support.getDropLocation();
                JTree tree = (JTree) support.getComponent();

                //HACK NDP BEGIN
                TreePath selectedPath = tree.getSelectionPath();
                DefaultMutableTreeNode selectedNode =
                        (DefaultMutableTreeNode) selectedPath.getLastPathComponent();

                //debug("selectedNode:" + selectedNode);

                FolderLocal treeFolderInfo = (FolderLocal) selectedNode.getUserObject();
                 
                if (! Parms.folderRemovable(treeFolderInfo.getFolderId()))
                {
                    //debug("false: " + treeFolderInfo.toDisplayString());
                    return false;
                }
                //HACK NDP END

                int dropRow = tree.getRowForPath(dl.getPath());
                int[] selRows = tree.getSelectionRows();
                for (int i = 0; i < selRows.length; i++) {
                    if (selRows[i] == dropRow) {
                        return false;
                    }
                }
                // Do not allow MOVE-action drops if a non-leaf node is
                // selected unless all of its children are also selected.
                int action = support.getDropAction();
                if (action == MOVE) {
                    //return haveCompleteNode(tree);

                    // Do not allow a non-leaf node to be copied to a level
                    // which is less than its source level.
                    TreePath dest = dl.getPath();
                    DefaultMutableTreeNode target =
                        (DefaultMutableTreeNode)dest.getLastPathComponent();

                    TreePath path = tree.getPathForRow(selRows[0]);

                    DefaultMutableTreeNode firstNode =
                        (DefaultMutableTreeNode)path.getLastPathComponent();

                    // Refuse drop if target is a descendant of selected first node.
                    boolean dropForbiden = target.isNodeAncestor(firstNode);
                    return (!dropForbiden);           
                }

                // We should never go here, because code is desinged only for move

                // Do not allow a non-leaf node to be copied to a level
                // which is less than its source level.
                TreePath dest = dl.getPath();
                DefaultMutableTreeNode target =
                        (DefaultMutableTreeNode) dest.getLastPathComponent();
                TreePath path = tree.getPathForRow(selRows[0]);
                DefaultMutableTreeNode firstNode =
                        (DefaultMutableTreeNode) path.getLastPathComponent();
                if (firstNode.getChildCount() > 0
                        && target.getLevel() < firstNode.getLevel()) {
                    return false;
                }

                return true;

            } else if ( object instanceof String ) {
                //debug("canImport(): flavor is String!");
                // Flavor is string. It's OK
                return true;
            }
            else
            {
                debug("Warning: object not a DefaultMutableTreeNode and not a String: " + object.getClass().getName());
                return false;
            }
        }

        return false;
    }

//    private boolean haveCompleteNode(JTree tree) {
//        int[] selRows = tree.getSelectionRows();
//        TreePath path = tree.getPathForRow(selRows[0]);
//        DefaultMutableTreeNode first =
//                (DefaultMutableTreeNode) path.getLastPathComponent();
//        int childCount = first.getChildCount();
//        // first has children and no children are selected.
//        if (childCount > 0 && selRows.length == 1) {
//            return false;
//        }
//        // first may have children.
//        for (int i = 1; i < selRows.length; i++) {
//            path = tree.getPathForRow(selRows[i]);
//            DefaultMutableTreeNode next =
//                    (DefaultMutableTreeNode) path.getLastPathComponent();
//            if (first.isNodeChild(next)) {
//                // Found a child of first.
//                if (childCount > selRows.length - 1) {
//                    // Not all children of first are selected.
//                    return false;
//                }
//            }
//        }
//        return true;
//    }


    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        Transferable tr = support.getTransferable();
        DataFlavor[] allFlavors = tr.getTransferDataFlavors();

        for (DataFlavor dataFlavor : allFlavors) {
            //debug("flavor: " + dataFlavor.toString());

            if (!support.isDataFlavorSupported(dataFlavor)) {
                debug("importData(): flavor not supported: " + dataFlavor);
                return false;
            }

            Object object = null;
            try {
                object = tr.getTransferData(dataFlavor);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPaneNewCustom.showException(null, e, "Impossible to get transfer data");
            }

            Transferable t = support.getTransferable();

            if (object instanceof DefaultMutableTreeNode) {

                // Extract transfer data.
                DefaultMutableTreeNode node = null;
                try {

                    node = (DefaultMutableTreeNode) t.getTransferData(nodesFlavor);
                } catch (Exception e ) {
                    System.out.println("UnsupportedFlavor: " + e.getMessage());
                    JOptionPaneNewCustom.showException(null, e, "Impossible to move folder");
                }
                
                // Get drop location info.
                JTree.DropLocation dl =
                        (JTree.DropLocation) support.getDropLocation();
                int childIndex = dl.getChildIndex();
                TreePath dest = dl.getPath();

                DefaultMutableTreeNode parent =
                        (DefaultMutableTreeNode) dest.getLastPathComponent();

                JTree tree = (JTree) support.getComponent();
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

                // Configure for drop mode.
                int index = childIndex;    // DropMode.INSERT
                if (childIndex == -1) {     // DropMode.ON
                    index = parent.getChildCount();
                }

                // There is only one one (that contain all his children) to add:
                model.insertNodeInto(node, parent, index++);

                return true;
            }
            else if ( object instanceof String ) 
            {
                // We import a String

                // Get drop location info.
                JTree.DropLocation dl =
                        (JTree.DropLocation) support.getDropLocation();
                TreePath dest = dl.getPath();
                DefaultMutableTreeNode parent =
                        (DefaultMutableTreeNode) dest.getLastPathComponent();

                FolderLocal folder = null;
               
                if (parent.getUserObject() instanceof FolderLocal)
                {
                    folder = (FolderLocal) parent.getUserObject();
                    debug("folder: " + folder.toDisplayString());                    
                }
                else
                {
                    JOptionPane.showConfirmDialog(null,
                            "parent.getUserObject() is not a TreeFolderInfo: " + parent.getUserObject().getClass().getName());
                    return false;
                }

                String imported = null;                
                try {
                    imported = (String) t.getTransferData(dataFlavor);
                }
                catch (Exception e)
                {
                   e.printStackTrace();
                   JOptionPaneNewCustom.showException(null, e, "Impossible to move Message(s). Imported String is null.");
                   return false;
                }
                
                debug("importData(): flavor is String: " + imported);

                // Ok, change the message from folder to new folder
                MessageMoveDroper messageMoveDroper = new MessageMoveDroper(safeShareitMain, userNumber, connection);
                
                try
                {
                    messageMoveDroper.moveMessages(imported, folder.getFolderId());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    JOptionPaneNewCustom.showException(null, e, "Impossible to move Message(s).");    
                    return false;
                }
                
                return true;
            }
            else
            {
                // Do nothing.
                System.err.println("Import is not a NodeTree nor a String. Failed!");
                return false;
            }
        }

        return true;

    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        
        //TreePath[] paths = tree.getSelectionPaths();

        TreePathBuilder treePathBuilder = new TreePathBuilder();
        TreePath[] paths = treePathBuilder.getPaths(tree, tree.getSelectionPath());
        
        if (paths != null) {
            // Make up a node array nodes that will be removed in
            // exportDone after a successful drop.
            //List<DefaultMutableTreeNode> copies =
            //        new ArrayList<DefaultMutableTreeNode>();

            List<DefaultMutableTreeNode> toRemove =
                    new ArrayList<DefaultMutableTreeNode>();

            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) paths[0].getLastPathComponent();

            if (!(node.getUserObject() instanceof FolderLocal)) {
                debug("node " + node.getUserObject() + " is not TreeFolderInfo!");
            }

            //DefaultMutableTreeNode copy = copy(node);
            //copies.add(copy);
            toRemove.add(node);

            //if (!(copy.getUserObject() instanceof FolderLocal)) {
            //    debug("copy " + copy.getUserObject() + " is not TreeFolderInfo!");
            //}

            for (int i = 1; i < paths.length; i++) {
                DefaultMutableTreeNode next =
                        (DefaultMutableTreeNode) paths[i].getLastPathComponent();

                // Do not allow higher level nodes to be added to list.
                if (next.getLevel() < node.getLevel()) {
                    break;
                } else if (next.getLevel() > node.getLevel()) {  // child node
                    //copy.add(copy(next));
                    // node already contains child
                } else {                                        // sibling
                    //copies.add(copy(next));
                    toRemove.add(next);
                }
            }
            
            //DefaultMutableTreeNode[] nodes =
            //        copies.toArray(new DefaultMutableTreeNode[copies.size()]);

            nodesToRemove =
                    toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);
            
            debug("");
            if (DEBUG)
            {
                for (int i = 0; i < paths.length; i++) {
                    DefaultMutableTreeNode next =
                            (DefaultMutableTreeNode) paths[i].getLastPathComponent();
                    TreePathBuilder.displayNodeInfo(i, next);
                }
            }

//            DefaultMutableTreeNode newNode0
//            = new DefaultMutableTreeNode ((DefaultMutableTreeNode)paths[0].getLastPathComponent());
//
//            Enumeration<?> children = ((DefaultMutableTreeNode)paths[0].getLastPathComponent()).children();
//            if (children != null) {
//                while (children.hasMoreElements()) {
//                    DefaultMutableTreeNode child
//                    = new DefaultMutableTreeNode((DefaultMutableTreeNode)children.nextElement());
//                    newNode0.add(child);
//                }
//            }
            
            DefaultMutableTreeNode newNode0 = buildHierarchy((DefaultMutableTreeNode)paths[0].getLastPathComponent());
            return new NodesTransferable(newNode0);
        }
        return null;
    }

    
    /**
     * Duplicate a hierarchy
     * @param origin        Origin of hierarchy to duplicate
     * @return              a clone of hierarchy
     */
    private DefaultMutableTreeNode buildHierarchy(DefaultMutableTreeNode origin){
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(origin);
        
        if (origin.getUserObject() instanceof FolderLocal) {
            FolderLocal treeFolderInfo = (FolderLocal) origin.getUserObject();
            newNode.setUserObject(treeFolderInfo);
        }      
        
        Enumeration<?> children = origin.children();

        if(children !=null){
            while (children.hasMoreElements()) {
                DefaultMutableTreeNode child
                = buildHierarchy((DefaultMutableTreeNode)children.nextElement());
                newNode.add(child);
            }
        }
        return newNode;
    }
    
//    /** Defensive copy used in createTransferable. */
//    private DefaultMutableTreeNode copy(DefaultMutableTreeNode node) {
//
//        if (node.getUserObject() instanceof FolderLocal) {
//            FolderLocal treeFolderInfo = (FolderLocal) node.getUserObject();
//            DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(treeFolderInfo);
//            return defaultMutableTreeNode;
//        } else {
//            return new DefaultMutableTreeNode(node);
//        }
//    }

    
    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {

        if ((action & MOVE) == MOVE) {
            JTree tree = (JTree) source;
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            // Remove nodes saved in nodesToRemove in createTransferable.
            for (int i = 0; i < nodesToRemove.length; i++) {
                model.removeNodeFromParent(nodesToRemove[i]);
            }

            debug("");
            debug("exportDone!");
            debug("");

            TreeModel treeModel = tree.getModel();
            DefaultMutableTreeNode rootTreeNode = (DefaultMutableTreeNode) treeModel.getRoot();

            MessagesManager messages = new MessagesManager();

            try {

                //Save changes
                TreeDataExtractor treeDataExtractor = new TreeDataExtractor(rootTreeNode);
                List<FolderLocal> folderLocals = treeDataExtractor.getFolderLocalList();

                FolderListTransfer folderListTransfer = new FolderListTransfer(connection, userNumber);
                folderListTransfer.putList(folderLocals);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPaneNewCustom.showException(null, e, messages.getMessage("cannot_add_folder"));

            }

            // Ok, we may now update back the host.
        }
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }


    public String toString() {
        return getClass().getName();
    }

    
    public class NodesTransferable implements Transferable {

        DefaultMutableTreeNode node;

        public NodesTransferable(DefaultMutableTreeNode node) {
            this.node = node;
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return node;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return (nodesFlavor.equals(flavor));
        }
    }

    private static void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }
}
