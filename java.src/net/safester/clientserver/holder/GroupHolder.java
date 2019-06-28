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

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.awakefw.sql.api.client.AwakeConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 *
 * @author Nicolas de Pomereu
 *
 * Store in memory the groups for a user id
 * 
 */
public class GroupHolder {

    /** The static user settings in memory */
    private static Set<String> groupsSet = null; // Collections.synchronizedSet(new HashSet<String>());

    /** The Jdbc connection */
    private Connection connection = null;

    /** The user number */
    private int userNumber = 0;

    private static Map<Integer, String> groupsMap = null; // new HashMap<Integer, String>();
    
    /**
     * Constructor
     * @param theConnection    the JDBC connection
     * @param userNumber       the user numberr
     */
    public GroupHolder(Connection theConnection, int userNumber)
    {        
        if (theConnection == null)
        {
            throw new IllegalArgumentException("Connection can\'t be null");
        }
        
        // Use a dedicated Connection to avoid overlap of result files
        if (connection instanceof AwakeConnection) {
           this.connection = ((AwakeConnection)theConnection).clone();            
        }
        else {
          this.connection = theConnection;  
        }

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
     * @param userNumber       the user numberr
     * @throws SQLException
     */
    private static synchronized void loadSynchronized(Connection connection, int userNumber) throws SQLException
    {
        if (groupsSet == null)
        {
            groupsSet = new HashSet<>();
            
            AwakeConnection awakeConnection = (AwakeConnection) connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

            try {
                String jsonString = awakeFileSession.call("net.safester.server.hosts.newapi.GroupHolderNewApi.getNames",
                        userNumber,
                        connection);
                
                Gson gsonOut = new Gson();
                Type type = new TypeToken<Set<String>>() {
                }.getType();
                Set<String> groupsSetTemp = gsonOut.fromJson(jsonString, type);

                // From HTML
                for (String name : groupsSetTemp) {
                    name = HtmlConverter.fromHtml(name);
                    groupsSet.add(name);
                }
                
            } catch (Exception ex) {
                throw new SQLException(ex);
            }   
            
            
        }
    }
    
    /**
     * 
     * @return  the user settings (from memory if available)
     * @throws SQLException
     */
    public Set<String> get() 
        throws SQLException
    {   
        load();
        return groupsSet;
    }
    
    /**
     * Reset (when the user modify his settings)
     */
    public void reset()
        throws SQLException
    {
        groupsSet = null;
        load();
    }

     /**
     * Expand the groups to their email addresses
     * @param groupsToExpand        the groups to expand
     * @return  the list of expanded email addresses
     * @throws java.sql.SQLException
     */
    public List<String> getExpandedEmailsFromGroups(List<String> groupsToExpand)
        throws SQLException
    {
        List<String> emailsList = new Vector<String>();

        if (groupsToExpand.isEmpty())
        {
            return emailsList;
        }
        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        Gson gsonOut = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        String jsonStringIn  = gsonOut.toJson(groupsToExpand, type);
        
        try {
            String jsonString = awakeFileSession.call("net.safester.server.hosts.newapi.GroupHolderNewApi.getExpandedEmailsFromGroups",
                    userNumber,
                    jsonStringIn,
                    connection);

            gsonOut = new Gson();
            type = new TypeToken<List<String>>() {
            }.getType();
            emailsList = gsonOut.fromJson(jsonString, type);

            return emailsList;

        } catch (Exception ex) {
            throw new SQLException(ex);
        }
                
    }


    public List<String> getExpandedEmailsFromGroupId(int idGroup)
    throws Exception {
    List<String> emailsList = new Vector<String>();

        if (idGroup == -1)
        {
            return emailsList;
        }
        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        try {
            String jsonString = awakeFileSession.call("net.safester.server.hosts.newapi.GroupHolderNewApi.getExpandedEmailsFromGroupId",
                    userNumber,
                    idGroup,
                    connection);

            Gson gsonOut = new Gson();
            Type type = new TypeToken<List<String>>() {
            }.getType();
            emailsList = gsonOut.fromJson(jsonString, type);

            return emailsList;

        } catch (Exception ex) {
            throw new SQLException(ex);
        }

    }

    public Map<Integer, String>getMap()
    throws SQLException{
        loadMap();
        return groupsMap;
    }

    public void resetMap()
    throws SQLException{
        groupsMap = null;
        loadMap();
    }

    private void loadMap()throws SQLException{
        
        if (groupsMap == null){
            
            groupsMap = new HashMap<Integer, String>();
            
            AwakeConnection awakeConnection = (AwakeConnection) connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

            try {
                String jsonString = awakeFileSession.call("net.safester.server.hosts.newapi.GroupHolderNewApi.getGroupMap",
                        userNumber,
                        connection);

                Gson gsonOut = new Gson();
                Type type = new TypeToken<HashMap<Integer, String>>() {
                }.getType();
                Map<Integer, String> groupsMapTemp = gsonOut.fromJson(jsonString, type);

                for (Map.Entry<Integer, String> entry : groupsMapTemp.entrySet()) {
//                    System.out.println("Key = " + entry.getKey()
//                            + ", Value = " + entry.getValue());
                    int key = entry.getKey();
                    String name = entry.getValue();
                    
                    name = HtmlConverter.fromHtml(name);
                    groupsMap.put(key, name);
                }
                
            } catch (Exception ex) {
                throw new SQLException(ex);
            }

        }
    }
}
