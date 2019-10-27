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

import java.util.ArrayList;
import java.util.List;

import com.moyosoft.connector.ms.outlook.contact.OutlookContact;
import com.moyosoft.connector.ms.outlook.folder.OutlookFolder;
import com.moyosoft.connector.ms.outlook.item.ItemsCollection;
import com.moyosoft.connector.ms.outlook.item.ItemsIterator;
import com.moyosoft.connector.ms.outlook.item.OutlookItem;

import net.safester.application.addrbooknew.RecipientEntry;
import net.safester.application.addrbooknew.tools.MobileUtil;

public class OutlookRecipientEntriesBuilder {

    private OutlookFolder contactFolder = null;
    private boolean doDisplayFirstBeforeLast = false;
    
    
    /**
     *  Constructor
     * @param contactFolder
     * @param doDisplayFirstBeforeLast
     */
    public OutlookRecipientEntriesBuilder(OutlookFolder contactFolder,
	    boolean doDisplayFirstBeforeLast) {
	super();
	this.contactFolder = contactFolder;
	this.doDisplayFirstBeforeLast = doDisplayFirstBeforeLast;
    }


    /**
     * Builds the list of PDF recipients
     * 
     * @return the list of PDF recipients
     */
    public List<RecipientEntry> build() {
 	// Get the folder's items collection
 	ItemsCollection items = contactFolder.getItems();

	List<RecipientEntry> pdfRecipients = new ArrayList<RecipientEntry>();
 	
 	// Display info for all contacts in the folder
 	for (ItemsIterator it = items.iterator(); it.hasNext();) {
 	    OutlookItem item = it.nextItem();

 	    // Check the item is a contact
 	    if (item != null && item.getType().isContact()) {
 	        OutlookContact contact = (OutlookContact) item;
 	        
 	        //System.out.println("First name: " + contact.getFirstName());
 	        //System.out.println("Last name: " + contact.getLastName());
 	        //System.out.println("Company: " + contact.getCompanyName());
 	        
 	        String email1Address = contact.getEmail1Address();
 	        String email2Address = contact.getEmail2Address();
 	        String email3Address = contact.getEmail3Address();
 	                
 	        String lastname = contact.getLastName();
 	        String firstName = contact.getFirstName();
                
                String company = contact.getCompanyName();
                
 	        String mobile = contact.getMobileTelephoneNumber();
 	        
 	        mobile = MobileUtil.removeSpecialCharacters(mobile);
 	        
 	        if (lastname == null) {
 	            lastname = "";
 	        }
 	        if (firstName == null) {
 	            firstName = "";
 	        }
 	        
 	        String name = null;
 	        if (doDisplayFirstBeforeLast) {
 	            name = firstName.trim() + " " + lastname.trim();
 	        }
 	        else {
 	            name = lastname.trim() + " " + firstName.trim();
 	        }
 	        
 	        if (mobile == null || mobile.isEmpty()) {
 	            mobile = "";
 	        }
 	                                
 	        if (email1Address != null && ! email1Address.isEmpty()) {
 	            RecipientEntry recipientEntry = new RecipientEntry(email1Address, name, company, mobile, null);
 	            pdfRecipients.add(recipientEntry);
 	        }
 	        
 	        if (email2Address != null && ! email2Address.isEmpty()) {
 	            RecipientEntry recipientEntry = new RecipientEntry(email2Address, name, company, mobile, null);
 	            pdfRecipients.add(recipientEntry);
 	        }

 	        if (email3Address != null && !email3Address.isEmpty()) {
 	            RecipientEntry recipientEntry = new RecipientEntry(email3Address, name, company, mobile, null);
 	            pdfRecipients.add(recipientEntry);
 	        }
 	    }
 	}
 	
 	return pdfRecipients;
 	
     }

}
