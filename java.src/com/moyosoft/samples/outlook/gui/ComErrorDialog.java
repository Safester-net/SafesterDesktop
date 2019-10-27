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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import com.moyosoft.connector.com.ComponentObjectModelException;

public class ComErrorDialog
{
   public static void open(Frame pParent, ComponentObjectModelException pEx)
   {
      MessageDialog.openOk(pParent, createMessage(pEx));
   }

   public static void open(Dialog pParent, ComponentObjectModelException pEx)
   {
      MessageDialog.openOk(pParent, createMessage(pEx));
   }

   public static void openDialog(Window pParent, ComponentObjectModelException pEx)
   {
      if(pParent instanceof Dialog)
      {
         open((Dialog) pParent, pEx);
      }
      else if(pParent instanceof Frame)
      {
         open((Frame) pParent, pEx);
      }
      else
      {
         MessageDialog.openOk(createMessage(pEx));
      }
   }
   
   private static String createMessage(ComponentObjectModelException pEx)
   {
   		String message = pEx.getMessage();
   		if(message == null || message.length() <= 0)
   		{
   			message = "Unknown COM error occured.";
   		}
   		
   		return message;
   }
}
