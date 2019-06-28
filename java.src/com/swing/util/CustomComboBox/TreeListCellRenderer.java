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
package com.swing.util.CustomComboBox;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

/**
 *
 * @author Alexandre Becquereau
 */
public class TreeListCellRenderer extends JPanel implements ListCellRenderer{
    private static final JTree tree = new JTree();
    TreeModel treeModel;
    TreeCellRenderer treeRenderer;
    IndentBorder indentBorder = new IndentBorder();

    public TreeListCellRenderer(TreeModel treeModel, TreeCellRenderer treeRenderer){
        this.treeModel = treeModel;
        this.treeRenderer = treeRenderer;
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(indentBorder);
        setOpaque(false);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
        if(value==null){ //if selected value is null
            removeAll();
            return this;
        }

        boolean leaf = treeModel.isLeaf(value);
        Component comp = treeRenderer.getTreeCellRendererComponent(tree, value, isSelected, true, leaf, index, cellHasFocus);
        removeAll();
        add(comp);

                PreorderEnumeration enumer = new PreorderEnumeration(treeModel);
        for(int i = 0; i<=index; i++)
            enumer.nextElement();
        indentBorder.setDepth(enumer.getDepth());
        

        return this;
    }
}
