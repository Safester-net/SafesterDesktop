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
package net.safester.application;

import java.sql.Connection;
import java.sql.SQLException;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;


/**
 * 
 * Manager for all notifications actions
 * @author Nicolas de Pomereu
 *
 */
public class MainNotifierServerInfo {

    private Connection connection = null;
    private int userNumber = -1;
    
    public MainNotifierServerInfo(int userNumber, Connection connection) {

	this.connection = connection;
	this.userNumber = userNumber;
    }

    /**
     * Gets from server the id of he last message ever dowloaded on client side
     * @return
     * @throws SQLException
     */
    public int getLastMessageId() throws SQLException {
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        try {
            String lastMessageIdStr = awakeFileSession.call("net.safester.server.NotifyInfo.getLastMessageNonDownloaded", userNumber, connection);
            int lastMessageId =  0;
            try {
                lastMessageId = Integer.parseInt(lastMessageIdStr);
            } catch (NumberFormatException numberFormatException) {
                // Nothing. Sometimes we have "false" in String, don't know why
            }
            return lastMessageId;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
    
    /**
     * Says if there is a new message on server that's waiting to be delivered.
     * @return
     * @throws SQLException
     */
    public boolean newInboxMessageExists() throws SQLException {
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        try {
            String newMessageStr = awakeFileSession.call("net.safester.server.NotifyInfo.newInboxMessageExists", userNumber, connection);
            boolean newMessage = Boolean.parseBoolean(newMessageStr.trim());
            
            //System.out.println("newMessageStr: " + newMessageStr + ":");
            //System.out.println("newMessage   : " + newMessage + ":");
            
            return newMessage;
          
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

}
