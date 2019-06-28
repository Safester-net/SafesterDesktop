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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.safester.clientserver.UserSettingsExtractor;
import net.safester.noobs.clientserver.UserSettingsLocal;

import org.awakefw.sql.api.client.AwakeConnection;


/**
 *
 * @author Nicolas
 *
 * Store in memory the user settings. Much better for user experience.
 * 
 */
public class TheUserSettingsHolder {

    /** The static user settings in memory per user */
    private static Map<Integer, UserSettingsLocal> userSettingsLocalMap = new HashMap<Integer, UserSettingsLocal>();

    /** The Jdbc connection */
    private Connection connection = null;
    
    /** The user number */
    private int userNumber = 0;

    /**
     * @param theConnection
     * @param userNumber
     */
    public TheUserSettingsHolder(Connection theConnection, int userNumber)
        throws SQLException
    {

        if (theConnection == null)
        {
            throw new IllegalArgumentException("Connection can\'t be null");
        }

        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();
        this.userNumber = userNumber;        
    }

    /**
     * Load the User Settings
     * @throws SQLException
     */
    public void load() throws SQLException
    {
        loadSynchronized(connection, userNumber);
    }

    /**
     * Load with synchronisation
     *
     * @param connection       the JDBC connection
     * @param userNumber       the user number
     * @throws SQLException
     */
    private static synchronized void loadSynchronized(Connection connection, int userNumber) throws SQLException
    {
        if (userSettingsLocalMap.get(userNumber) == null)
        {
            UserSettingsExtractor userSettingsExtractor = new UserSettingsExtractor(connection, userNumber);
            UserSettingsLocal userSettingsLocal = userSettingsExtractor.get();
            userSettingsLocalMap.put(userNumber, userSettingsLocal);
        }
    }

    /**
     * 
     * @return  the user settings (from memory if available)
     * @throws SQLException
     */
    public UserSettingsLocal get() 
        throws SQLException
    {   
        load();
        return userSettingsLocalMap.get(userNumber);
    }
    
    /**
     * Reset (when the user modify his settings)
     */
    public void reset()
        throws SQLException
    {
        userSettingsLocalMap = new HashMap<Integer, UserSettingsLocal>();
        load();
    }
    
}
