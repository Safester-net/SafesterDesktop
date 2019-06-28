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
package net.safester.application.util;


public class RecipientLocalUtil {

    /*
	public static String buildRecipientDisplayName(Connection connection, RecipientLocal recipient) {
		String recipientDisplayName = "";
		
		String recipientEmail = "";
		String recipientName = "";
		try{
			
			UserSettingsExtractor userSettingsExtractor = new UserSettingsExtractor(connection, recipient.getUserNumber());
			UserSettingsLocal userSettingsLocal = userSettingsExtractor.get();
			if(userSettingsLocal != null){
				//recipientEmail = userSettingsLocal.getNotificationEmail();
				recipientName = userSettingsLocal.getUserName();
			}
                        UserLoginTransfert userLoginTransfert = new UserLoginTransfert(connection, recipient.getUserNumber());
                        UserLoginLocal userLoginLocal = userLoginTransfert.get();
                        if(userLoginLocal != null){
                            recipientEmail = userLoginLocal.getKey_id();
                        }

		}
		catch(SQLException e){
			
		}
		recipientDisplayName = recipient.getNameRecipient();
		if(recipientEmail.length() > 0){
			if(recipientEmail.equals(recipientDisplayName)){
				recipientDisplayName = recipientName;
			}
			recipientDisplayName += " <" + recipientEmail + ">";
		}
		recipientDisplayName += "; ";
		
		return recipientDisplayName;
	}
     * 
     */
}
