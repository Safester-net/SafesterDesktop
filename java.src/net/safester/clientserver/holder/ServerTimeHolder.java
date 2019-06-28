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
package net.safester.clientserver.holder;

import java.sql.Connection;
import java.util.Date;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;


/**
 * Store in memory cache the server time in Date.getTime() format
 * 
 * @author Nicolas de Pomereu
 */
public class ServerTimeHolder {

    /** use to store the last time it was loaded */
    private static long lastLoad = 0;

    /** the number of milliseconds since January 1, 1970, 00:00:00 GMT*/
    private static long fastTime = 0;
        
    /** The Jdbc connection */
    private Connection connection = null;

    public ServerTimeHolder(Connection theConnection) {

        if (theConnection == null)
        {
            throw new IllegalArgumentException("theConnection can not be null!");
        }
        
        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();
        
    }

    /**
     * Loads the lexicon once in session
     */
    
    /** Load the server time once in session */
    public long getTime() throws Exception
    {
        load(connection);
        return fastTime;
    }

   /**
    * Static server time loader
    * @param TheConnection  the Awake Connection
    */
    public static synchronized void load(Connection theConnection)
        throws Exception
    {
        // May be already loaded:
        if (fastTime != 0)
        {
            Long now = new Date().getTime();

            long oneHour = 3600 * 1000;
            // Say it' ok if it's less than one hour ago
            if (now - lastLoad <= oneHour)
            {
                return;
            }
        }

        // Use a dedicated Connection to avoid overlap of result files
        Connection connection = ((AwakeConnection)theConnection).clone();
        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
        
        String fastTimeStr = awakeFileSession.call("net.safester.server.hosts.MessageCreatorMain.getTime");
        fastTime = Long.parseLong(fastTimeStr);

        lastLoad = new Date().getTime();
    }

}
