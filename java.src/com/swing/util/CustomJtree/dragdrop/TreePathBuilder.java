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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Class that allows to "develop" or "expand" all the sub-paths of a select path
 *
 * @author Nicolas
 */
public class TreePathBuilder
{
    
    /**
     * Default Constructor
     */
    public TreePathBuilder()
    {
        
    }
    
    /**
     * Return the array of sub-paths of the selected path
     * 
     * @param tree          the JTree
     * @param selectedPath  the selected unique path
     * @return              the array of sub-paths of the selected path
     */
    public TreePath[] getPaths(JTree tree, TreePath selectedPath) {
        
        //TreeNode root = (TreeNode) tree.getModel().getRoot();
        
        List<TreePath> list = new ArrayList<TreePath>();
        
        getPaths(tree, selectedPath, list);

        return (TreePath[]) list.toArray(new TreePath[list.size()]);
      }

     /**
      * Update the list of TreePath with recursion
      * @param tree     the JTree
      * @param parent   the parent tree path
      * @param list     the list of TreePaths to update
      */
      private void getPaths(JTree tree, TreePath parent, List<TreePath> list) {
        
       // We don't care about the expansion status  
       //if (expanded && !tree.isVisible(parent)) {
       //   return;
       // }
        
        list.add(parent);
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastPathComponent();
        
        if (node.getChildCount() >= 0) {
          for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
              DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
            TreePath path = parent.pathByAddingChild(n);
            getPaths(tree, path, list);
          }
        }
      }
        

      public static void displayNodeInfo(int indice, DefaultMutableTreeNode node)
      {
          System.out.println(indice + " - " + node + " - p: " + node.getParent()+  " - c: " + node.getChildCount());                                
      }
      
    
}

