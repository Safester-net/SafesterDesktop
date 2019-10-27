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
package com.moyosoft.samples.outlook.gui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icons
{
   public static final Icon SAVE_ICON = loadIcon("com/moyosoft/samples/outlook/gui/imgs/save.gif");
   public static final Icon SAVEAS_ICON = loadIcon("com/moyosoft/samples/outlook/gui/imgs//saveas.gif");
   public static final Icon DELETE_ICON = loadIcon("com/moyosoft/samples/outlook/gui/imgs//delete.gif");
   public static final Icon DELETE_DISABLED_ICON  = loadIcon("com/moyosoft/samples/outlook/gui/imgs//delete_disabled.gif");
   public static final Icon ADD_ATTACHMENT_ICON = loadIcon("com/moyosoft/samples/outlook/gui/imgs//add_attachement.gif");
   public static final Icon ADD_FOLDER_ICON = loadIcon("com/moyosoft/samples/outlook/gui/imgs//add_folder.gif");
   public static final Icon REFRESH_ICON = loadIcon("com/moyosoft/samples/outlook/gui/imgs//refresh.gif");
   public static final Icon CUT_ICON = loadIcon("com/moyosoft/samples/outlook/gui/imgs//cut.gif");
   public static final Icon COPY_ICON = loadIcon("com/moyosoft/samples/outlook/gui/imgs//copy.gif");
   public static final Icon PASTE_ICON = loadIcon("com/moyosoft/samples/outlook/gui/imgs//paste.gif");

   public static final Icon FOLDER_CLOSED_ICON = loadIcon("com/moyosoft/samples/outlook/gui/imgs//folder_closed_icon.gif");
   public static final Icon FOLDER_OPEN_ICON = loadIcon("com/moyosoft/samples/outlook/gui/imgs//folder_open_icon.gif");

   private static Icon loadIcon(String pIconPath)
   {
     try
     {
       return new ImageIcon(
         Icons.class.getClassLoader().getResource(
            pIconPath));
     }
     catch(Exception ex)
     {
       return null;
     }
   }
}
