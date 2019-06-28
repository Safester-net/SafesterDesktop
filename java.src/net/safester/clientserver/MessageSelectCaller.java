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

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import org.awakefw.commons.api.client.RemoteException;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;


import net.safester.noobs.clientserver.MessageLocalStoreTransfer;
import org.apache.commons.lang3.StringUtils;

public class MessageSelectCaller {

    public static boolean DEBUG = true;
    
    /** The user number */
    private int userNumber = 0;

    /** The folder id to use */
    private int folderId;

    /** The Limit ... offset ... clause holder */
    private LimitClause limitClause = null;
    
    /** The Jdbc connection */
    private Connection connection = null;

    
    public MessageSelectCaller(int userNumber, int folderId,
	    LimitClause limitClause, Connection connection) {
	this.userNumber = userNumber;
	this.folderId = folderId;
	this.limitClause = limitClause;
	this.connection = connection;
    }

    MessageSelectCaller(int userNumber, int folderId, Connection connection) {
	this.userNumber = userNumber;
	this.folderId = folderId;
	this.connection = connection;
    }
    
    public MessageLocalStore selectMessages() throws SQLException, IllegalArgumentException, UnknownHostException, ConnectException, RemoteException, IOException {
        AwakeConnection awakeConnection = (AwakeConnection)connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        if (this.limitClause == null) {
            throw new NullPointerException("limitClause is null!");
        }
        
	String jsonString = awakeFileSession.call(
		"net.safester.server.MessageSelectNew.selectMessages", userNumber,
		folderId, limitClause.toString(), connection);

        MessageLocalStore messageLocalStore = MessageLocalStoreTransfer.fromJson(jsonString);
	return messageLocalStore;
    }
             
    int count() throws SQLException, IllegalArgumentException, UnknownHostException, ConnectException, RemoteException, IOException {
        AwakeConnection awakeConnection = (AwakeConnection)connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

	String countString = awakeFileSession.call(
		"net.safester.server.MessageSelectNew.count", userNumber,
		folderId,  connection);
        
        int count = 0;
        if (StringUtils.isNumeric(countString)) {
            count = Integer.parseInt(countString);
        }
        
        return count;

    }

    private void debug(String string) {
        if (DEBUG) {
            System.out.println(string);
        }

    }
        
    

}
