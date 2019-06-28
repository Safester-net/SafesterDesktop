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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import net.safester.noobs.clientserver.FolderLocal;

/**
 * 
 * Class to use to rebuild completely a final List<FolderLocal> to put back on the server.
 * List<FolderLocal> is build using only the info from the JTree itself.
 * 
 * @author Nicolas
 *
 */
public class TreeDataExtractor
{

    /** The Map of (folder id, folder Name) */
    private Map<Integer, String> idAndName = new HashMap<Integer, String>();

    /**
     * A List of map of folders with children, one row per child.
     * So, Each map contains (folder id, child id)
     */
    private List<Map<Integer, Integer>> idAndChild = new Vector<Map<Integer, Integer>>();


    /** The re-builder Foldert local list */
    private List<FolderLocal> folderLocalList = null;
    
    /**
     * @param root
     */
    public TreeDataExtractor(TreeNode root)
    {
        this.createDatasFromTree(root);
        folderLocalList = this.buildFolderLocalList();
    }
    
    
    /**
     * @return the folderLocalList
     */
    public List<FolderLocal> getFolderLocalList()
    {
        return this.folderLocalList;
    }

    /**
     * Utility to displays all the infos for this tree
     * @param rootTreeNode
     */
    public void printDatasFromJTtree()
    {
        // Print it
        System.out.println();
        System.out.println("Map (folder id, folder name): ");
        System.out.println(idAndName);

        System.out.println();
        System.out.println("List of Map of (folder id, child id): ");
        System.out.println(idAndChild);

        System.out.println();
        List<FolderLocal> folderLocalTheList = getFolderLocalList();
        
        for (FolderLocal folderLocal : folderLocalTheList)
        {
            System.out.println(folderLocal.toDisplayString());
        }
    }
    
    /**
     * Function to create the tree architecture data from it's representation
     * 
     * @param root    the tree node
     */
    private void createDatasFromTree(TreeNode root) {
        //System.out.println(root);

        if (root instanceof DefaultMutableTreeNode)
        {
            DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) root;

            if ( theNode.getUserObject() instanceof FolderLocal)
            {
                FolderLocal local = (FolderLocal)theNode.getUserObject();
                idAndName.put(local.getFolderId(), local.toString());
            }
        }

        if (root.getParent()  instanceof DefaultMutableTreeNode)
        {
            DefaultMutableTreeNode theDefaultMutableTreeNode = (DefaultMutableTreeNode) root.getParent();
            DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) root;

            if (theDefaultMutableTreeNode.getUserObject() instanceof FolderLocal &&
                    theNode.getUserObject() instanceof FolderLocal)
            {
                FolderLocal local = (FolderLocal)theDefaultMutableTreeNode.getUserObject();
                FolderLocal child = (FolderLocal)theNode.getUserObject();

                Map<Integer, Integer> map= new HashMap<Integer, Integer>();
                map.put(local.getFolderId(), child.getFolderId());
                idAndChild.add(map);

                //System.out.println(local.toString() + " - " +  local.getFolderId()  + " - " + child.getFolderId());
            }
            else
            {
                //System.out.println(theDefaultMutableTreeNode.getUserObject()  + " ! " + theNode.getUserObject());
            }
        }

        Enumeration<?> children = root.children();
        if (children != null) {
            while (children.hasMoreElements()) {
                createDatasFromTree((TreeNode) children.nextElement());
            }
        }
    }


    /** 
     * Build the list of FolderLocal from idName and idAndChild objects 
     * @return  The list of folderLocal representing the tree 
     */ 
    private List<FolderLocal>  buildFolderLocalList() { 
        List<FolderLocal>  folders = new ArrayList<FolderLocal> (); 

        //Get all folder ids 
        Set<Integer>  folderIds = idAndName.keySet(); 
        
        for (Integer id : folderIds) { 
            //Build FolderLocal object 
            FolderLocal folderLocal = new FolderLocal(); 
            folderLocal.setFolderId(id); 
            folderLocal.setName(idAndName.get(id)); 
 
            //Get list of children 
            List<Integer>  children = getChildren(id); 
            //Add folder's children 
            folderLocal.setChildren(children); 

            //Set if folder is root folder or not 
            folderLocal.setRootFolder(isRoot(id)); 

            //Add new object to list 
            folders.add(folderLocal); 
        } 
 
        return folders; 
    } 
 
    /** 
     * Get children of a folder 
     * @param parentId  the id of the folder for which we want the children 
     * @return          a list containing the id of the folder's children 
     */ 
    private List<Integer>  getChildren(int parentId) { 
 
        List<Integer>  children = new ArrayList<Integer> (); 
 
        //For each map of the list of map 
        for (Map<Integer, Integer>  map : idAndChild) { 
            //Get all ids of map 
            Set<Integer>  ids = map.keySet(); 
            for (Integer id : ids) { 
                if (id == parentId) { 
                    //Id is folder id, get Child id 
                    //Add it to list 
                    children.add(map.get(id)); 
                } 
            } 
        } 

        return children; 
    } 
 
    /** 
     * Determine if a folder is a root folder 
     * @param folderId      folder id 
     * @return 
     */ 
    private boolean isRoot(int folderId) { 
 
        //Parse all maps of list 
        for (Map<Integer, Integer>  map : idAndChild) { 
            Set<Integer>  ids = map.keySet(); 
            for (Integer id : ids) { 
                int childId = map.get(id); 
                if (childId == folderId) { 
                    //Folder is child of one ==> It's not a root folder 
                    return false; 
                } 
            } 
        } 
        //Folder is child of no-one ==> It's a root folder 
        return true; 
    } 
    

}
