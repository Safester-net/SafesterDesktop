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

import java.util.Comparator;

import com.moyosoft.connector.ms.outlook.folder.OutlookFolder;

class OutlookFolderComparator implements Comparator<OutlookFolder>
{
    private static OutlookFolderComparator mInstance = null;

    private OutlookFolderComparator()
    {
    }

    public static OutlookFolderComparator getInstance()
    {
        if(mInstance == null)
        {
            mInstance = new OutlookFolderComparator();
        }
        return mInstance;
    }

    public int compare(OutlookFolder o1, OutlookFolder o2)
    {
        OutlookFolder child1 = o1;
        OutlookFolder child2 = o2;

        if(child1.getName() == null)
        {
            return -1;
        }
        if(child2.getName() == null)
        {
            return 1;
        }
        return child1.getName().compareToIgnoreCase(child2.getName());
    }
}
