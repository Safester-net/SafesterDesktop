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
package net.safester.clientserver.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.awakefw.file.api.util.HtmlConverter;

import net.safester.application.util.EmailUser;
import net.safester.clientserver.EmailRecipientExtractor;
import net.safester.clientserver.EmailRecipientLocal;
import net.safester.clientserver.holder.GroupHolder;

/**
 * Buils for a suer number the list of emails addresses for completion.
 * <br>
 * The list is build using:
 * <ul>
 * <li> The content of the user address book.</li>
 * <li> *Plus* the list of emails that have been his recipients.</li>
 * </ul>
 * 
 * @author Nicolas de Pomereu
 */
public class EmailCompletionBuilder {

    /** The Jdbc connection */
    private Connection connection = null;

    /** The user number */
    private int userNumber = 0;

    /**
     * Constructor
     * @param userNumber        the user number to get the completion list from
     * @param connection        the JDBC connection
     */
    public EmailCompletionBuilder(Connection connection, int userNumber) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection can\'t be null");
        }

        this.connection = connection;
        this.userNumber = userNumber;
    }

    /**
     * @return the list of concerced emails for completion
     */
    public Set<String> getLexiconSet() throws SQLException
    {
        Set<String> emails = new TreeSet<String>();
        
        EmailRecipientExtractor emailRecipientExtractor = new EmailRecipientExtractor(connection, userNumber);
        List<EmailRecipientLocal> emailRecipientLocalList = emailRecipientExtractor.getList();

        // Just "email" & "name <email@domain.net>"
        for (EmailRecipientLocal emailRecipientLocal : emailRecipientLocalList) {
            emails.add(emailRecipientLocal.getEmail());

            String name = emailRecipientLocal.getName();
            name = HtmlConverter.fromHtml(name);
            
            EmailUser emailUser = new EmailUser(name, emailRecipientLocal.getEmail());
            String nameAndEmailAddress = emailUser.getNameAndEmailAddress();
            
            emails.add(nameAndEmailAddress);

        }

        GroupHolder groupHolder = new GroupHolder(connection, userNumber);
        Set<String> allGroupsSet = groupHolder.get();

        for (String group : allGroupsSet) {
            emails.add(group);
        }
                
        /*
        AddressBookListTransfer addressBookListTransfer = new AddressBookListTransfer(connection, userNumber);
        List<AddressBookLocal> addressBookLocalList = addressBookListTransfer.getList();

        for (AddressBookLocal addressBookLocal : addressBookLocalList)
        {
            emails.add(addressBookLocal.getEmail());

            EmailUser emailUser = new EmailUser(addressBookLocal.getName(), addressBookLocal.getEmail());
            emails.add(emailUser.getNameAndEmailAddress());
        }
        */
        
        return emails;
    }
    
}
