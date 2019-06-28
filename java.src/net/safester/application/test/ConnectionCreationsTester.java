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
package net.safester.application.test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.awakefw.commons.api.client.RemoteException;

import net.safester.application.parms.ConnectionParms;
import net.safester.clientserver.LimitClause;
import net.safester.clientserver.MessageLocalStore;
import net.safester.clientserver.MessageSelectCaller;

/**
 *
 * @author Nicolas de Pomereu
 */
public class ConnectionCreationsTester {
    
    public static void main (String args[]) throws Exception {

        ConnectionParms connectionParms = new ConnectionParms(
               "ndepomereu@kawansoft.com",
                "*2loveme$123".toCharArray(),
                null, null);
        
        Connection connection = connectionParms.getConnection();
        
        System.err.println();
        if (connection == null) {
            System.err.println("connection is null!");
        }
        else {
            System.out.println("connection created!");
        }
        
        while (true) {
            System.out.println(new Date() + " getting messagges...");
            getMessages(connection);
            Thread.sleep(1000);
        }

    }

    private static void getMessages(Connection connection)
	    throws SQLException, UnknownHostException, ConnectException, RemoteException, IOException {
	LimitClause limitClause = new LimitClause(0, 20);
        int inbox = 1;
        int userNumber = 1182;
	MessageSelectCaller messageSelectCaller = new MessageSelectCaller(userNumber, inbox, limitClause, connection);
	MessageLocalStore messageLocalStore = messageSelectCaller.selectMessages();
    }
}
