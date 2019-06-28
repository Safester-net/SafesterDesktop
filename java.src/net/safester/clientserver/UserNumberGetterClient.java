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

import java.sql.Connection;
import java.sql.SQLException;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

public class UserNumberGetterClient {

    /** The Jdbc connection */
    private Connection connection = null;
    
    /**
     * Constructor to be used to retrieve the user number using the login
     * @param connection the SQL/JDBC Connection
     */
    public UserNumberGetterClient(Connection connection)
    {
        if (connection == null)
        {
            throw new IllegalArgumentException("Connection can\'t be null");
        }
        
        this.connection = connection;
    }
    
    /**
     * Return  the user number for the passed login,
     *  
     * @param login     the user login
     *
     * @return  the user number for this login, -1 if the user does not exists for 
     * the provided login.
     */
    public int getUserNumberFromLogin(String login) throws SQLException
    {
        if (login == null)
        {
            throw new IllegalArgumentException("login can\'t be null");
        }
                
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        try {
            String userNumberStr = awakeFileSession.call("net.safester.server.hosts.newapi.UserNumberGetterNewApi.getUserNumberFromLogin", login, connection);
            int userNumber = Integer.parseInt(userNumberStr);
            return userNumber;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
    
    /**
     * Return the user login from the passed userNumber
     * @param userNumber
     * @return
     * @throws SQLException
     */
    public String getLoginFromUserNumber(int userNumber) throws SQLException
    {
                
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        try {
            String login = awakeFileSession.call("net.safester.server.hosts.newapi.UserNumberGetterNewApi.getLoginFromUserNumber", userNumber, connection);
            return login;
        } catch (Exception e) {
            throw new SQLException(e);
        }
   
    }
}
