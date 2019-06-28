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
package net.safester.application.addrbooknew.gmail;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Organization;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.PhoneNumber;
import net.safester.application.addrbooknew.RecipientEntry;
import net.safester.application.addrbooknew.tools.MobileUtil;


/**
 * Build the list of PdfRecipient from Google People API
 * 
 * @author Nicolas de Pomereu 
 */
public class GoogleRecipientsBuilder {

    private List<Person> persons = null;
    private boolean doDisplayFirstBeforeLast = false;

    /**
     * Constructor
     * 
     * @param persons
     *            the persons retrieved from Gmail People APIs
     * @param doDisplayFirstBeforeLast the value of doDisplayFirstBeforeLast
     */
    public GoogleRecipientsBuilder(List<Person> persons, boolean doDisplayFirstBeforeLast) {
	super();
	this.persons = persons;
        this.doDisplayFirstBeforeLast = doDisplayFirstBeforeLast;
    }

    /**
     * Builds the list of PDF recipients
     * 
     * @return the list of PDF recipients
     */
    public List<RecipientEntry> build() {

	List<RecipientEntry> pdfRecipients = new ArrayList<RecipientEntry>();
	    
	if (persons == null || persons.size() == 0) {
	    return pdfRecipients;
	}

	for (Person person : persons) {

	    // Get email addresses
	    List<EmailAddress> emails = person.getEmailAddresses();

	    if (emails == null || emails.size() == 0) {
		continue;
	    }

	    // We include all found emails & addresses per person
	    for (EmailAddress personEmail : emails) {

		String emailAddress = personEmail.getValue();

		String lastName = "";
                String firstName = "";
		// Get names
		List<Name> names = person.getNames();
		if (names != null && names.size() > 0) {
		    lastName = names.get(0).getFamilyName();
                    firstName = names.get(0).getGivenName();
		}
               
		String mobile = "";
		
		// Get he first mobile phone number
		List<PhoneNumber> phones = person.getPhoneNumbers();
		if (phones != null) {
		    for (PhoneNumber phoneNumber : phones) {
			if (phoneNumber.getType() != null && phoneNumber.getType().toLowerCase().contains("mobile")) {
			    mobile = phoneNumber.getValue();
			}
		    }
		}
		
		String company = null;
		List<Organization> organizations = person.getOrganizations();
		if (organizations != null && organizations.size() > 0) {
		    company = organizations.get(0).getName();
		}
		
                if (mobile == null || mobile.isEmpty()) {
                    mobile = "";
                }
                
               // Format mobile by removing special chars
                mobile = MobileUtil.removeSpecialCharacters(mobile);
                
        
                String name = null;
 	        if (lastName == null) {
 	            lastName = "";
 	        }
 	        if (firstName == null) {
 	            firstName = "";
 	        }
 	        
 	        if (doDisplayFirstBeforeLast) {
 	            name = firstName.trim() + " " + lastName.trim();
 	        }
 	        else {
 	            name = lastName.trim() + " " + firstName.trim();
 	        }
 
		RecipientEntry recipientEntry = new RecipientEntry(emailAddress, name, company, mobile, null);
		pdfRecipients.add(recipientEntry);
	    }
	}

	return pdfRecipients;
    }

    @Override
    public String toString() {
	return "GooglePdfRecipientsBuilder [pdfRecipients=" + build() + "]";
    }
    
}
