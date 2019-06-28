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

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

/**
 * @author Nicolas de Pomereu
 *
 */


public class IconManager
{

    protected IconManager()
    {
        
    }

    /**
     * Get the file type 
     * @param file  to get the type from
     * @return
     */
    
    public static String getSystemTypeDescription(File file)
    {
        String type = null;
        
        try
        {
            FileSystemView view = FileSystemView.getFileSystemView();                 
            type = view.getSystemTypeDescription(file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }       
        
        return type;
        
    }
    
    /**
     * Get the system icon for a file
     * 
     * @param file  the file to get the icon
     * @return      the icon of the file, null if *any* error occurs
     */
    public static Icon getSystemIcon(File file)
    {
        try
        {
            FileSystemView view = FileSystemView.getFileSystemView();      
            Icon icon = view.getSystemIcon(file);   
            return icon;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
