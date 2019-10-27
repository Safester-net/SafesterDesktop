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

import static net.safester.application.photo.PhotoAddresBookTableCellRendererNew.GENERIC_PHOTO_ICON;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import net.safester.application.parms.Parms;
import net.safester.application.util.GroupTreeTableElement;
import net.safester.application.util.TableUtil;


/**
 *
 * @author Alexandre Becquereau
 */
public class GroupTreeCellRendererNew extends DefaultTreeCellRenderer {

    Icon groupIcon;
    Icon contactIcon;

    private static Color BACKGROUND_COLOR_SELECTED = null;
    private static Color FOREGROUND_COLOR_SELECTED = null;

    private TreePath oldSelectedPath = null;
    private boolean exited = false;
    
    public GroupTreeCellRendererNew(final JXTreeTable jXTreeTable) {
        //Init icons
        this.groupIcon = Parms.createImageIcon("images/files_2/16x16/users3.png");
        this.contactIcon = Parms.createImageIcon("images/files_2/16x16/businessman2.png");

         // BEGIN FOR HIGHLIGHT HOVER LINE
        Color selectionColor = UIManager.getDefaults().getColor("Tree.selectionBackground");
        BACKGROUND_COLOR_SELECTED = new Color(selectionColor.getRed(), selectionColor.getGreen(), selectionColor.getBlue());

        selectionColor = UIManager.getDefaults().getColor("Tree.selectionForeground");
        FOREGROUND_COLOR_SELECTED = new Color(selectionColor.getRed(), selectionColor.getGreen(), selectionColor.getBlue());
        
        jXTreeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exited = false;
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exited = true;
            }
        });

        jXTreeTable.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                // Nothing to do
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
                
                DefaultTreeTableModel treeModel = (DefaultTreeTableModel) jXTreeTable.getTreeTableModel();
                
                //int selRow = jXTreeTable.g getRowForLocation(mouseEvent.getX(), mouseEvent.getY());
                
                int selRow = jXTreeTable.rowAtPoint(mouseEvent.getPoint());
                if (selRow < 0) {
                    TreePath currentSelected = oldSelectedPath;
                    oldSelectedPath = null;
                    if (currentSelected != null) {
                        //treeModel.nodeChanged((TreeNode) currentSelected.getLastPathComponent());
                    }
                } else {
                    TreePath selectedPath = jXTreeTable.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
                    if ((oldSelectedPath == null) || !selectedPath.equals(oldSelectedPath)) {
                        oldSelectedPath = selectedPath;
                        //treeModel.nodeChanged((TreeNode) oldSelectedPath.getLastPathComponent());
                    }
                }
                jXTreeTable.repaint();
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

        this.setBackground(highlight ? TableUtil.HOVER_COLOR : tree.getBackground());
        this.setForeground(highlight ? Color.BLACK : tree.getForeground());

        if (exited) {
            this.setBackground(tree.getBackground());
            this.setForeground(tree.getForeground());
        }

        if (selected) {
            this.setBackground(BACKGROUND_COLOR_SELECTED);
            this.setForeground(FOREGROUND_COLOR_SELECTED);
        }
        // END FOR HIGHLIGHT HOVER LINE
        
        if (value instanceof DefaultMutableTreeTableNode) {
            DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) value;
            if (node.getUserObject() instanceof GroupTreeTableElement) {
                GroupTreeTableElement elmt = (GroupTreeTableElement) node.getUserObject();

                if (elmt.getId() != -1) {
                    setIcon(groupIcon);
                } else {

                    String photoBase64 = elmt.getThumbnail();
                    if (photoBase64 != null && photoBase64.length() > 10) {
                        ImageIcon imageIcon = ImageResizer.createIconFromBase64(photoBase64);
                        this.setIcon(imageIcon);
                    } else {
                        this.setIcon(Parms.createImageIcon(GENERIC_PHOTO_ICON));
                    }
                }
            }
        }

        return this;
    }
}
