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
import java.util.List;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.awakefw.sql.api.client.AwakeConnection;

import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.specs.UniqueTransfer;
import net.safester.noobs.clientserver.EmailGroupLocal;
import net.safester.noobs.clientserver.GroupMemberLocal;
import net.safester.noobs.clientserver.GsonUtil;

/**
 *
 * @author Alexandre Becquereau
 */
public class EmailGroupTransfert implements UniqueTransfer<EmailGroupLocal>{
    /** The Jdbc connection */
    private Connection connection = null;

    int user_number;
    String name;

    int groupId = -1;
    public EmailGroupTransfert(Connection theConnection, int theUserNumber, String theName){

        this(theConnection);

        if(theUserNumber < 0){
            throw new IllegalArgumentException("Invalid user_number : " + theUserNumber);
        }
        if(theName == null || theName.isEmpty()){
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = theName;
        this.user_number = theUserNumber;
    }

    public EmailGroupTransfert(Connection theConnection){
        if(theConnection == null){
            throw new IllegalArgumentException("Connection cannot be null");
        }
        this.connection = theConnection;
    }

    public EmailGroupTransfert(Connection theConnection, int theUserNumber, int theGroupId){
        this(theConnection);
        if(theUserNumber < 0){
            throw new IllegalArgumentException("Invalid user_number : " + theUserNumber);
        }
        if(theGroupId < 0){
            throw new IllegalArgumentException("Invalid group_id : " + theGroupId);
        }
        this.groupId = theGroupId;
        this.user_number = theUserNumber;
    }

    public void put(EmailGroupLocal emailGroupLocal) throws SQLException {
        String groupName = emailGroupLocal.getName();
        groupName = HtmlConverter.toHtml(groupName);
        emailGroupLocal.setName(groupName);
        String gSonString = GsonUtil.emailGroupLocalToGson(emailGroupLocal);


        AwakeConnection awakeConnection = (AwakeConnection)connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        int newGroupId = -1;
        String newGroupIdStr = "-1";
        
        if(groupId == -1){            
            try{
                newGroupIdStr = awakeFileSession.call("net.safester.server.GroupCreator.createGroup", connection, gSonString);
            }catch(Exception e){
                throw new SQLException(e);
            }
        }else{
        try{
                newGroupIdStr = awakeFileSession.call("net.safester.server.GroupCreator.updateGroup", connection, gSonString);
            }catch(Exception e){
                throw new SQLException(e);
            }
        }

        try{
            newGroupId = Integer.parseInt(newGroupIdStr);
        }catch(NumberFormatException e){
            JOptionPaneNewCustom.showException(null, e);
        }

        emailGroupLocal.setId(newGroupId);
        List<GroupMemberLocal> members = emailGroupLocal.getMembers();
        for ( int i = 0; i<members.size(); i++ ) {
            GroupMemberLocal member = members.get(i);
            member.setId_email_group(newGroupId);
            members.set(i, member);
        }
        try{
            GroupMemberListTransfert groupMemberListTransfert = new GroupMemberListTransfert(connection, emailGroupLocal.getUserNumber(), newGroupId);
            groupMemberListTransfert.putList(members);
        }catch(Exception e){
                throw new SQLException(e);
            }
    }

    public void remove() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EmailGroupLocal get() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
