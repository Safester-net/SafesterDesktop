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

import com.moyosoft.connector.ms.outlook.folder.*;
import javax.swing.JOptionPane;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;

public class OutlookFolderTreeNode extends AbstractFolderTreeNode
{

    private static boolean DISPLAY_DONE;
    private OutlookFolder mFolder;

    public OutlookFolderTreeNode(OutlookFolder folder)
    {
        super(folder.getName());
        mFolder = folder;
    }

    public void createChildrens()
    {
        if(mFolder != null && mFolder.hasChildren())
        {
            createChildrens(mFolder.getFolders());
        }
    }

    public OutlookFolder getFolder()
    {
        return mFolder;
    }

    public static void resetDisplayDone() {
        DISPLAY_DONE = false;
    }
    
    public static boolean isDisplayDone() {
        return DISPLAY_DONE;
    }
    
    public static void setDisplayDone() {
        DISPLAY_DONE = true;
    }
    
    public boolean isLeaf() {
        try {
            if (mFolder != null) {
                return !mFolder.hasChildren();
            }
            return false;
        } catch (Exception e) {
            if (! isDisplayDone() ) {
                JOptionPane.showMessageDialog(null, MessagesManager.get("outlook_office_was_closed"), Parms.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
                setDisplayDone();
            }
            return false;
        }
    }
}
