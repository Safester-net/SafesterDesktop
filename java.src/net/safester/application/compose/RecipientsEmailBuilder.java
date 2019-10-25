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
package net.safester.application.compose;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import net.safester.application.util.EmailUser;
import net.safester.clientserver.holder.GroupHolder;

import org.awakefw.file.api.util.HtmlConverter;

/**
 * Allow to build the recipients lists and to expand from groups
 */
public class RecipientsEmailBuilder {

    public static boolean DEBUG = true;
    
    /** The Jdbc Connection */
    private Connection connection = null;

    /** the user number of the email sender */
    private int userNumber = -1;
    
    /** The list of address separated by ; */
    String adressList = null;

    private String firstInvalidEmail = null;
    private int recipientType;
    
    private Map<String, String> names = new HashMap<>();
    
    /**
     * @param connection        the jdbc connection
     * @param userNumber        the user number for the sender
     * @param adressList        The list of address separated by ;
     * @param recipientType     the recipient type (to, cc, bcc)
     */
    public RecipientsEmailBuilder(Connection connection, int userNumber, String adressList, int recipientType)
    {
        if (connection == null)
        {
            throw new IllegalArgumentException("connection can not be null!");
        }
        
        if (adressList == null)
        {
            throw new IllegalArgumentException("adressList can not be null!");
        }
        
        this.connection = connection;
        this.userNumber = userNumber;
        this.adressList = adressList;    
        
        this.recipientType = recipientType;
                
    }

    /**
     * @return the first invalid email
     */
    public String getFirstInvalidEmail()
    {
        return firstInvalidEmail;
    }
    
    public String getName(String emailAddress) {
        String name = "";
        if (names.get(emailAddress) != null)
        { 
            name = names.get(emailAddress);
        }
        return name;
    }
        
    /**
     * Builds the list of emails addresses that will receive the message
     * @return the list of emails addresses tthat will receive the message
     * @throws Exception
     */
    public List<String>  build() throws Exception {
        
        // Build the group Set for the userNumber
        debug("groupHolder.get()");

        GroupHolder groupHolder = new GroupHolder(connection, userNumber);
        Set<String> allGroupsSet = groupHolder.get();

        debug("allGroupsSet: " + allGroupsSet);
        
        List<String> groupsToExpand = new Vector<String>();
        
        List<String> emailsList = new Vector<String>();
        
        String recipientTo = this.adressList.trim();

        StringTokenizer st = new StringTokenizer(recipientTo, ";");

        debug("");

        while (st.hasMoreTokens()) {
            String recipientValue = st.nextToken();
            debug("recipientValue: " + recipientValue);

            if (allGroupsSet.contains(recipientValue.toLowerCase()))
            {
                groupsToExpand.add(HtmlConverter.toHtml(recipientValue.toLowerCase()));
            }
            else
            {
                recipientValue = recipientValue.trim();

                // Nice class that does all the dirt work of name/email split!
                EmailUser emailUser = new EmailUser(recipientValue);
                
                if (!emailUser.isEmailSyntaxValid()) {
                    firstInvalidEmail = recipientValue;
                    return new Vector<String>();
                }

                String emailAddress = emailUser.getEmailAddress();

                if (emailAddress != null) {
                    emailAddress = emailAddress.toLowerCase();
                }
                
                emailsList.add(emailAddress);   
                names.put(emailAddress, emailUser.getName());
            }          
        }

        debug("groupsToExpand: " + groupsToExpand);
                
        // Ok, now expand all groups emails        
        List<String> expandedEmails = groupHolder.getExpandedEmailsFromGroups(groupsToExpand);
        emailsList.addAll(expandedEmails);

        debug("expandedEmails: " + expandedEmails);
        
        return emailsList;
    }

    
    /**
     * debug tool
     */
    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(new Date() + " " + s);
        }
    }


    
}
