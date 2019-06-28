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

import net.safester.clientserver.specs.UniqueExtractor;
import net.safester.noobs.clientserver.GsonUtil;

/**
 * @author RunningLiberty
 *
 */
public class AutoresponderExtractor implements UniqueExtractor<AutoresponderLocal>
{
    /** The Jdbc connection */
    private Connection connection = null;
    /** The user number */
    private int userNumber = 0;

    /**
     * Constructor
     * @param userNumber        the user number to get the message from     
     * @param connection        the JDBC connection
     */
    public AutoresponderExtractor(Connection connection, int userNumber) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection can\'t be null");
        }

        this.connection = connection;
        this.userNumber = userNumber;
    }

    /**
     * @return a unique Autoresponder Local from the SQL Server
     * @throws SQLException     if any SQL Exception is raised
     */
    @Override
    public AutoresponderLocal get()
            throws SQLException {
        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        String methodRemote = "net.safester.server.hosts.newapi.AutoresponderNewApi.get";
        //debug("methodRemote: " + methodRemote);

        String jsonString = null;
        try
        {
            jsonString = awakeFileSession.call(methodRemote,
                                                userNumber,
                                                connection
                                                );
        }
        catch (Exception e)
        {
            throw new SQLException(e);
        }
        
        AutoresponderLocal autoresponderLocal = GsonUtil.autoresponderFromGson(jsonString);
        return autoresponderLocal;

    }

    /**
     * Update the server Autoresponder table with a local instance
     * @param autoresponderLocal    the local autoresponder instance
     * @throws SQLException
     */
    public void update(AutoresponderLocal autoresponderLocal)
            throws SQLException {

        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        String methodRemote = "net.safester.server.hosts.newapi.AutoresponderNewApi.put";
        //debug("methodRemote: " + methodRemote);

        String jsonString = GsonUtil.autoresponderToGson(autoresponderLocal);
        try
        {
             awakeFileSession.call(methodRemote,
                                                userNumber,
                                                jsonString,
                                                connection
                                                );
        }
        catch (Exception e)
        {
            throw new SQLException(e);
        }
          
    }
}

