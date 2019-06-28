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
package net.safester.clientserver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import net.safester.clientserver.specs.ListExtractor;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;


/**
 * @author Nicolas de Pomereu
 *
 */
public class EmailRecipientExtractor implements ListExtractor<EmailRecipientLocal> {

    /** The Jdbc connection */
    private Connection connection = null;
    
    /** The user number */
    private int userNumber = 0;

    /**
     * Constructor
     * @param userNumber        the user number to get the message from     
     * @param connection        the JDBC connection
     */
    public EmailRecipientExtractor(Connection connection, int userNumber) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection can\'t be null");
        }

        this.connection = connection;
        this.userNumber = userNumber;
    }

    /**
     * Get the emails adresses of all those with whom the user has been
     * sending emails
     *
     * return a list of EmailRecipients for this user number
     *
     * @throws java.sql.SQLException
     */
    @Override
    public List<EmailRecipientLocal> getList() throws SQLException {
        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        try {
            String returnString = awakeFileSession.call("net.safester.server.hosts.newapi.RecipientsNewApi.getList",
                    userNumber,
                    connection);
            Gson gsonOut = new Gson();
            Type type = new TypeToken<List<EmailRecipientLocal>>() {
            }.getType();
            
            // It's OK for now if name is HTML encoded, will be decoded in calling methods
            List<EmailRecipientLocal> emailsRecipientsList = gsonOut.fromJson(returnString, type);
            return emailsRecipientsList;

        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }
}

