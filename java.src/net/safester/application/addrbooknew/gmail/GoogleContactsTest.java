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

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.services.people.v1.model.Person;
import java.io.File;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import net.safester.application.addrbooknew.RecipientEntry;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Nicolas de Pomereu
 */
public class GoogleContactsTest {
        
     /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        
	GoogleContacts googleContacts = new GoogleContacts();

	GoogleContacts.openBrowserToGetCode();

	String code = JOptionPane.showInputDialog("Entrer le code SVP:");

	System.out.println(new Date() + " BEGIN");
        //String clientSecret = FileUtils.readFileToString(new File(ParmsUtil.getDebugDir() + File.separator + "client_secret.txt"));
        String clientSecret = null;
	boolean isCodeValid = googleContacts.validateCode(code, clientSecret);
	System.out.println(new Date() + " END");

	if (!isCodeValid) {
	    System.out.println("Invalid code!");
	    return;
	}

	System.out.println(new Date() + " BEGIN");
	List<Person> persons = null;
	try {
	    persons = googleContacts.getPersons();
	} catch (Exception ex) {

	    if (ex instanceof TokenResponseException
		    && ex.getMessage().trim().contains("400 Bad Request")) {
		System.out.println("Bad Token");
		ex.printStackTrace();
		return;
	    } else {

		throw ex;
	    }
	}

	System.out.println(new Date() + " END");

	GoogleRecipientsBuilder googlePdfRecipientsBuilder = new GoogleRecipientsBuilder(
		persons, false);
	List<RecipientEntry> pdfRecipients = googlePdfRecipientsBuilder.build();
	for (RecipientEntry pdfRecipient : pdfRecipients) {
	    System.out.println(pdfRecipient);
	}

    }


}
