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
package net.safester.application.tool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.FolderListTransfer;
import net.safester.noobs.clientserver.FolderLocal;


public class FoldersHandler {

    private int userId;
    private Connection connection;

    private List<FolderLocal> rootFolders;
    private Map<Integer, FolderLocal> folders;

    public FoldersHandler(Connection connection, int userId)
    {
        this.userId = userId;
        this.connection = connection;
    }

//    public void setRootFolders(List<FolderLocal> rootFolders)
//    {
//        this.rootFolders = rootFolders;
//    }

    public List<FolderLocal> getRootFolders()       
    {
        if(rootFolders == null)
        {
            try {
                initFolders();
            } catch (SQLException ex) {
                JOptionPaneNewCustom.showException(null, ex);
                return null;
            }
        }

        return rootFolders;
    }

    public void initFolders()
            throws SQLException
    {
            List<FolderLocal> foldersList = new Vector<FolderLocal>();
            FolderListTransfer folderListTransfer = new FolderListTransfer(connection, userId);
            
            //System.out.println( new Date() + " before folderListTransfer.getList()");
            
            foldersList = folderListTransfer.getList();
            
            //System.out.println( new Date() + " end    folderListTransfer.getList()");
            
            rootFolders = new Vector<FolderLocal>();
            folders = new HashMap<Integer, FolderLocal>();
            
            for(FolderLocal folderLocal : foldersList)
            {
                if(folderLocal.isRootFolder())
                {
                    this.rootFolders.add(folderLocal);
                }
            
                folders.put(folderLocal.getFolderId(), folderLocal);
            }
    }


    public Map<Integer, FolderLocal> getFolders() {
        return folders;
    }

    public FolderLocal getFolder(int folderId)
    {
        return folders.get(folderId);
    }
    
//    public void setFolders(Map<Integer, FolderLocal> folders) {
//        this.folders = folders;
//    }

//	/**
//	 * Save folders on db
//	 **/
//    public void saveFolders()
//            throws SQLException
//    {
//        FolderListTransfer folderListTransfer = new FolderListTransfer(connection, userId);
//
//        List<FolderLocal> foldersList = new Vector<FolderLocal>();
//        for(int id : folders.keySet())
//        {
//            foldersList.add(folders.get(id));
//        }
//
//        folderListTransfer.putList(foldersList);
//    }

//	/**
//	 * Remove a folder
//	 * @param idFolder   Id of folder to remove
//	 **/
//    public void removeFolder(int idFolder) throws SQLException {
//       
//        FolderLocal folder = folders.get(idFolder);
//        List<Integer>childsId = folder.getChildren();
//        
//        List<Integer> childs = new ArrayList<Integer>();
//        childs.addAll(childsId);
//        if(childsId != null)
//        {
//            for(int idChild : childs)
//            {
//                 removeFolder(idChild);
//            }
//        }
//        if(folder.isRootFolder())
//        {
//            rootFolders.remove(folder);
//        }
//        folders.remove(idFolder);
//        for(FolderLocal f : folders.values())
//        {
//            List<Integer> children = f.getChildren();
//            if(children !=null)
//            {
//                if(children.contains(idFolder))
//                {
//                    children.remove(new Integer(idFolder));
//                    f.setChildren(children);
//                    folders.put(f.getFolderId(), f);
//                    break;
//                }
//            }
//        }
//        saveFolders();
//    }

//	/**
//	 * Remove a subfolder
//	 * @param oldParent  Parent folder
//	 * @param idFolder   Id of sub folder to remove
//	 **/
//    public void removeChild(FolderLocal oldParent, int idFolder) {
//       // System.out.println("oldParent: " + oldParent);
//        oldParent.getChildren().remove(new Integer(idFolder));
//        folders.put(oldParent.getFolderId(), oldParent);
//    }
//
//	/**
//	 *  Add a sub folder to a folder
//	 * @param parent  Parent folder
//	 * @param idFolder   Id of sub folder to add
//	 **/
//    public void addChild(FolderLocal parent, FolderLocal folder) {
//        parent.addChild(folder.getFolderId());
//        folders.put(parent.getFolderId(), parent);
//    }
//
//    public void addRootFolder(FolderLocal folder) {
//        this.rootFolders.add(folder);
//    }
}
