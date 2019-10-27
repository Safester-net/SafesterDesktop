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

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.awakefw.sql.api.client.AwakeConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.safester.clientserver.holder.GroupHolder;
import net.safester.clientserver.specs.ListTransfer;
import net.safester.noobs.clientserver.GroupMemberLocal;
import net.safester.noobs.clientserver.GsonUtil;

/**
 *
 * @author Alexandre Becquereau
 */
public class GroupMemberListTransfert implements ListTransfer<GroupMemberLocal>{

    /** debug infos */
    public static boolean DEBUG = false;  
    
    private Connection connection;
    private int userNumber;
    private int groupId;

    public GroupMemberListTransfert(Connection theConnection, int theUserNumber, int theGroupId){
        if(theConnection == null){
            throw new IllegalArgumentException("Connection can't be null");
        }
        if(theUserNumber < 0){
            throw new IllegalArgumentException("Invalid userNumber: " + theUserNumber);
        }
        if(theGroupId < 0){
            throw new IllegalArgumentException("Invalid groupId: " + theGroupId);
        }

        this.connection = theConnection;
        this.userNumber = theUserNumber;
        this.groupId = theGroupId;
    }

    public List<GroupMemberLocal> getList() throws SQLException {
              
	return getListSecure(userNumber, groupId, connection);
    }

    /**
     * @return
     * @throws SQLException
     */
    private static synchronized List<GroupMemberLocal> getListSecure(int userNumber, int groupId, Connection connection) throws SQLException {
	if (GroupMemberCache.containsKey(groupId)) {
	    return GroupMemberCache.get(groupId);
	}
	        
        List<GroupMemberLocal> groupMemberLocals = new ArrayList<>();
        
        //Put the list on the server, because of intricated SQL statements        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
        
        try
        {
            String returnString = awakeFileSession.call( "net.safester.server.hosts.newapi.GroupMemberNewApi.getListSecure",
                                    userNumber,
                                    groupId,
                                    connection );
            
            Gson gsonOut = new Gson();
            Type type = new TypeToken<List<GroupMemberLocal>>() {
            }.getType();
            groupMemberLocals  = gsonOut.fromJson(returnString, type);
            
            for (GroupMemberLocal groupMemberLocal : groupMemberLocals) {
                String name = groupMemberLocal.getName();
                if (name != null) {
                    name = HtmlConverter.fromHtml(name);
                }
                groupMemberLocal.setName(name);
            }
            
            return groupMemberLocals;
            
        }
        catch (Exception e)
        {
            throw new SQLException(e);
        }
        
        
    }


    @Override
    public void putList(List<GroupMemberLocal> groupMembersLocal) throws SQLException {
        toHtml(groupMembersLocal);

        String jsonString = GsonUtil.listGroupMemberLocalToGson(groupMembersLocal);

        //Put the list on the server, because of intricated SQL statements
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        try
        {
            awakeFileSession.call( "net.safester.server.GroupMemberLocalList.put",
                                    jsonString,
                                    userNumber,
                                    groupId,
                                    connection );
        }
        catch (Exception e)
        {
            throw new SQLException(e);
        }

        GroupHolder groupHolder = new GroupHolder(connection, userNumber);
        groupHolder.resetMap();
        groupHolder.reset();       
        GroupMemberCache.clearAll();        
    }

    private void toHtml(List<GroupMemberLocal> groupMembersLocal)
    {
        // Set HTML in names (we don't want any accents on host SQL UTF-8)
        for (GroupMemberLocal groupMemberLocal : groupMembersLocal) {
            String name = groupMemberLocal.getName();
            name = HtmlConverter.toHtml(name);
            groupMemberLocal.setName(name);
        }
    }
        
    /**
     * debug tool
     */
    private static void debug(String s) {
	if (DEBUG)
	    System.out.println(s);
    }          

}
