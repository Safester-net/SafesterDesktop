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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import com.moyosoft.connector.com.ComponentObjectModelException;
import com.moyosoft.connector.ms.outlook.folder.*;

public abstract class AbstractFolderTreeNode extends DefaultMutableTreeNode implements FolderTreeNode {

    private boolean childrensCreated = false;

    public AbstractFolderTreeNode(String name) {
        super(name);
    }

    protected void createChildrens(FoldersCollection folders) {
        if (childrensCreated) {
            return;
        }

        try {
            if (folders != null && folders.size() > 0) {
                List foldersList = new ArrayList();

                for (Iterator it = folders.iterator(); it.hasNext();) {
                    OutlookFolder folder = (OutlookFolder) it.next();
                    foldersList.add(folder);
                }

                Collections.sort(
                        foldersList,
                        OutlookFolderComparator.getInstance());

                for (Iterator it = foldersList.iterator(); it.hasNext();) {
                    OutlookFolder folder = (OutlookFolder) it.next();
                    FolderTreeNode node = new OutlookFolderTreeNode(folder);
                    add(node);
                }
            }

            childrensCreated = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refresh() throws ComponentObjectModelException {
        if (childrensCreated) {
            removeAllChildren();
            childrensCreated = false;
            createChildrens();
        }
    }

    public abstract OutlookFolder getFolder();

    public abstract void createChildrens();
}
