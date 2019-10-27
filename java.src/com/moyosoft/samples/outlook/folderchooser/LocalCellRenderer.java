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
package com.moyosoft.samples.outlook.folderchooser;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Nicolas de Pomereu
 */
public class LocalCellRenderer extends DefaultTreeCellRenderer {

    private TreePath oldSelectedPath = null;

    public static Color HOVER_COLOR = null;

    private boolean exited = false;

    public LocalCellRenderer(final JTree tree) {

        Color selectionColor = UIManager.getDefaults().getColor("Tree.selectionBackground");
        HOVER_COLOR = new Color(selectionColor.getRed(), selectionColor.getGreen(), selectionColor.getBlue());

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
                tree.repaint();
            }
        });
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {
        JComponent comp = (JComponent) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        comp.setOpaque(true);
        boolean highlight = (oldSelectedPath != null) && (value == oldSelectedPath.getLastPathComponent());

        comp.setBackground(highlight ? HOVER_COLOR : tree.getBackground());
        comp.setForeground(highlight ? Color.WHITE : tree.getForeground());

        if (exited) {
            comp.setBackground(tree.getBackground());
            comp.setForeground(tree.getForeground());
        }

        if (selected) {
            comp.setBackground(HOVER_COLOR);
            comp.setForeground(Color.WHITE);
        }

        return comp;
    }
}
