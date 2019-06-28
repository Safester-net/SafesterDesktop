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
package net.safester.noobs.clientserver;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import net.safester.noobs.clientserver.specs.Local;



/**
 * @author Nicolas de Pomereu
 *
 * Contains local instance of the remote folder for a unique user :
 * <br> - The Folder Id.
 * <br> - The Folder Name.
 * <br> - The list of children folders.
 */
public class FolderLocal implements Local, Serializable
{
    /** The FolderLocal must be Serializable. It's cleaner to have a unique serialVersionUID */
    private static final long serialVersionUID = 7444646960150476436L;

    /** The folder Id for the user */
    private int folderId;

    /** the folder name */
    private String name;

    /** If true the folder is a root folder */
    private boolean rootFolder = false;

    /** The ordered list of children for this folder */
    private List<Integer> children = null;


    /**
     * Constructor
     */
    public FolderLocal()
    {

    }

    /**
     * @return the rootFolder
     */
    public boolean isRootFolder()
    {
        return rootFolder;
    }


    /**
     * @param rootFolder the rootFolder to set
     */
    public void setRootFolder(boolean rootFolder)
    {
        this.rootFolder = rootFolder;
    }


    /**
     * @return the folder_id
     */
    public int getFolderId()
    {
        return folderId;
    }

    /**
     * @param folderId the folder_id to set
     */
    public void setFolderId(int folderId)
    {
        this.folderId = folderId;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the children
     */
    public List<Integer> getChildren()
    {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<Integer> children)
    {
        this.children = children;
    }

    public void addChild(int theFolderId) {
        if(children == null)
        {
            children = new Vector<Integer>();
        }
        this.children.add(theFolderId);
    }


    /**
     * Use instead of toString() to display the instance content
     * @return  the instance content
     */
    public String toDisplayString()
    {
        return "FolderLocal [folderId=" + this.folderId + ", name=" + this.name
                + ", rootFolder=" + this.rootFolder + ", children="
                + this.children + "]";
    }
    
    /**
     * Used by Swing to display the name of the folder in JTree
     * Do no modify. Use toInfoString() if you need instance detail
     * @return  the string representation for Swing in JTree
     */
    
    @Override
    public String toString()
    {
        return name;
    }
    
    
    
    

}
