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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.safester.application.parms.Parms;
import net.safester.application.util.TableUtil;
import net.safester.noobs.clientserver.FolderLocal;

public class FolderTreeCellRendererNew extends DefaultTreeCellRenderer {

    private TreePath oldSelectedPath = null;
    private static Color BACKGROUND_COLOR_SELECTED = null;
    private static Color FOREGROUND_COLOR_SELECTED = null;
    private boolean exited = false;
    
    Icon homeIcon;
    Icon inboxIcon;
    Icon outBoxIcon;
    Icon draftIcon;
    Icon expandedIcon;
    Icon starredIcon;

    public FolderTreeCellRendererNew(final JTree tree) {
        //Init icons
        this.homeIcon = Parms.createImageIcon("images/files_2/16x16/home.png");
        this.inboxIcon = Parms.createImageIcon("images/files_2/16x16/inbox_into.png");
        this.outBoxIcon = Parms.createImageIcon("images/files_2/16x16/inbox_out.png");
        this.draftIcon = Parms.createImageIcon("images/files_2/16x16/inbox.png");
        this.closedIcon = Parms.createImageIcon("images/files_2/16x16/folder.png");
        this.expandedIcon = Parms.createImageIcon("images/files_2/16x16/folder_open.png");
        this.starredIcon = Parms.createImageIcon(Parms.STARRED_ICON);

      // BEGIN FOR HIGHLIGHT HOVER LINE
        Color selectionColor = UIManager.getDefaults().getColor("Tree.selectionBackground");
        BACKGROUND_COLOR_SELECTED = new Color(selectionColor.getRed(), selectionColor.getGreen(), selectionColor.getBlue());

        selectionColor = UIManager.getDefaults().getColor("Tree.selectionForeground");
        FOREGROUND_COLOR_SELECTED = new Color(selectionColor.getRed(), selectionColor.getGreen(), selectionColor.getBlue());
        
        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exited = false;
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exited = true;
            }
        });

        tree.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                // Nothing to do
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
                DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
                int selRow = tree.getRowForLocation(mouseEvent.getX(), mouseEvent.getY());
                if (selRow < 0) {
                    TreePath currentSelected = oldSelectedPath;
                    oldSelectedPath = null;
                    if (currentSelected != null) {
                        treeModel.nodeChanged((TreeNode) currentSelected.getLastPathComponent());
                    }
                } else {
                    TreePath selectedPath = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
                    if ((oldSelectedPath == null) || !selectedPath.equals(oldSelectedPath)) {
                        oldSelectedPath = selectedPath;
                        treeModel.nodeChanged((TreeNode) oldSelectedPath.getLastPathComponent());
                    }
                }

                //tree.repaint();
                tree.updateUI();
            }
        });
        
        // END FOR HIGHLIGHT HOVER LINE        
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);

        // BEGIN FOR HIGHLIGHT HOVER LINE
        this.setOpaque(true);
        boolean highlight = (oldSelectedPath != null) && (value == oldSelectedPath.getLastPathComponent());

        // BEGIN HACK FOR CUT OFF LEAF 
        if (leaf) {
            final Dimension size = this.getPreferredSize(); 
            int theWidth = Math.min(200, size.width); // because on Search otw leaf takes very long size
            int theHeight = size.height;
            this.setMinimumSize(new Dimension(theWidth, theHeight)); 
            this.setPreferredSize(new Dimension(theWidth, theHeight)); 
        // END HACK FOR CUT OFF LEAF          
        }

        this.setBackground(highlight ? TableUtil.HOVER_COLOR : tree.getBackground());
        this.setForeground(highlight ? Color.BLACK : tree.getForeground());

        if (exited) {
            this.setBackground(tree.getBackground());
            this.setForeground(tree.getForeground());
        }

        this.setBorder(new EmptyBorder(2,2,2,2));
        
        if (sel) {
            this.setBackground(BACKGROUND_COLOR_SELECTED);
            this.setForeground(FOREGROUND_COLOR_SELECTED);
        }
        
       // END FOR HIGHLIGHT HOVER LINE
       
        int id = -1;

        //GEt current node
        DefaultMutableTreeNode currentNode = null;
        if (value instanceof DefaultMutableTreeNode) {
            currentNode = (DefaultMutableTreeNode) value;
        }
        
        if (currentNode != null && currentNode.getUserObject() instanceof FolderLocal) {
            //Get id of current folder
            id = ((FolderLocal) currentNode.getUserObject()).getFolderId();
        } else if (currentNode != null && currentNode.getUserObject() instanceof String) {
            //Set root icon
            setIcon(homeIcon);
            return this;
        }
        if(id == Parms.STARRED_ID) {
        	setIcon(starredIcon);
        } else if (id == Parms.INBOX_ID) {
            //Set inbox icon
            setIcon(inboxIcon);
        } else if (id == Parms.OUTBOX_ID) {
            //Set outbox icon
            setIcon(outBoxIcon);
        } else if (id == Parms.DRAFT_ID) {
            //Set draft
            setIcon(draftIcon);
        } else {
            //Default icon
            //setIcon(this.getClosedIcon());
            
            if (expanded) {
              setIcon(this.expandedIcon);              
            }
            else {
               setIcon(this.closedIcon);                 
            }
        }
       
        return this;
    }
}
