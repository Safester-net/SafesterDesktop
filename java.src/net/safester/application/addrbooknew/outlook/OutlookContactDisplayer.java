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
package net.safester.application.addrbooknew.outlook;

import java.util.Date;

import com.moyosoft.connector.ms.outlook.Outlook;
import com.moyosoft.connector.ms.outlook.contact.OutlookContact;
import com.moyosoft.connector.ms.outlook.folder.FolderType;
import com.moyosoft.connector.ms.outlook.folder.OutlookFolder;
import com.moyosoft.connector.ms.outlook.item.ItemsCollection;
import com.moyosoft.connector.ms.outlook.item.ItemsIterator;
import com.moyosoft.connector.ms.outlook.item.OutlookItem;

/**
 * @author Nicolas de Pomereu
 *
 */
public class OutlookContactDisplayer {

    private Outlook outlook = null;
    
    /**
     * 
     */
    public OutlookContactDisplayer() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {

	System.out.println(new Date() + " Begin");
	OutlookContactDisplayer outlookContactDisplayer = new OutlookContactDisplayer();
	
	String email = "ndepomereu@kawansoft.com";
	outlookContactDisplayer.displayIfexists(email);
	System.out.println(new Date() + " End 1");
	
	email = "abecquereau@kawansoft.com";
	outlookContactDisplayer.displayIfexists(email);

	System.out.println(new Date() + " End 2");
	outlookContactDisplayer.close();
	System.out.println(new Date() + " End 3");

    }

    /**
     * Closes Outlook. Must be done at end of each session.s
     */
    public void close() {
	OutlookUtilMoyosoft.outlookDispose(outlook);
    }

    /**
     * Displays the Outlook Contact for the passed email
     * @param email
     * @return true if email exists in Contactqs
     * @throws Exception
     */
    public boolean displayIfexists(String email) throws Exception{
	
	if (email == null) {
	    throw new NullPointerException("email is null!");
	}
	
	if (outlook == null) {
	    outlook = new Outlook(); 
	}

	OutlookFolder contactFolder = outlook.getDefaultFolder(FolderType.CONTACTS);
 	ItemsCollection items = contactFolder.getItems();

	//List<RecipientEntry> pdfRecipients = new ArrayList<RecipientEntry>();
 	
 	// Display info for all contacts in the folder
 	for (ItemsIterator it = items.iterator(); it.hasNext();) {
 	    OutlookItem item = it.nextItem();

 	    // Check the item is a contact
 	    if (item != null && item.getType().isContact()) {
 	        OutlookContact contact = (OutlookContact) item;
 	        
 	        if (emailIsFetched(email, contact)) {
 	           contact.display();
 	           return true;
 	        }
 	        
 	    }
 	}
 	
 	return false;
	
    }

    private boolean emailIsFetched(String email, OutlookContact contact) {

	String email1Address = contact.getEmail1Address();
	String email2Address = contact.getEmail2Address();
	String email3Address = contact.getEmail3Address();
	        
	if (email1Address != null && email1Address.trim().equalsIgnoreCase(email)) {
	    return true;
	}
	if (email2Address != null && email2Address.trim().equalsIgnoreCase(email)) {
	    return true;
	}
	if (email3Address != null && email3Address.trim().equalsIgnoreCase(email)) {
	    return true;
	}
	return false;
    }

}
