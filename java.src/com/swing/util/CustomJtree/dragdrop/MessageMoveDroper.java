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
package com.swing.util.CustomJtree.dragdrop;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import net.safester.application.Main;
import net.safester.clientserver.MessageLocalStoreCache;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;


/**
 * Utility class when droping a Message (from the JTable in main window) on a folder for moving
 * the message folder.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class MessageMoveDroper
{
    public static boolean DEBUG = false;
    
    /** Current user number */
    private int userNumber;
    
    /** The remove JDBC connection (for updating folders & message */
    private Connection connection = null;
    
    /** instance needed for callback when moving Messages */
    private Main theMain = null;

    /**
     *  Constructor 
     * @param safeShareitMain   instance needed for callback when moving Messages
     * @param userNumber        the user number
     * @param connection        the JDBC Connection
     */
    public MessageMoveDroper(Main safeShareitMain, int userNumber,  Connection connection)
    {
        debug("MessageMoveDroper");
        
        if (connection == null)
        {
            throw new IllegalArgumentException("connection can not be null");
        }

        debug("MessageMoveDroper 2");
        
        if (safeShareitMain == null)
        {
            throw new IllegalArgumentException("safeShareitMain can not be null");
        }

        debug("MessageMoveDroper 3");

        this.userNumber = userNumber;
        this.connection = connection;
        this.theMain = safeShareitMain;

        debug("MessageMoveDroper(): end constructor!");
    }
 
    /**
     * Move a list of Messages to a New folder.
     * Because we are in Drag & Drop mode, Old Folder info is contained inside the Messagers
     * 
     * @param String messageLines  the messages lines in their Drag & Drop format
     * @param newFolderId          the new Folder Id
     * 
     * @throws Exception
     */
    public void moveMessages(String messageLines, int newFolderId)
        throws Exception
    {        
        String[] messages = messageLines.split("\n");

        if (messages == null || messages.length == 0)
        {
            debug("messages is null!");
            return;
        }
        
        List<String> messagesList = Arrays.asList(messages);

        debug(messagesList.toString());

        if (messagesList == null || messagesList.isEmpty())
        {
            debug("messagesList is null!");
            return;
        }

        int oldFolderId = 0;

        List <Integer> messagesId = new Vector<Integer>();

        for (String msgInfos : messagesList) {

            String msg_Id = msgInfos.substring(0, msgInfos.indexOf(","));
            msgInfos = msgInfos.substring(msgInfos.indexOf(",") + 1);
            String oldFolder_Id = msgInfos.substring(0, msgInfos.indexOf(","));

            int msgId = Integer.parseInt(msg_Id);
            messagesId.add(msgId);

            oldFolderId = Integer.parseInt(oldFolder_Id);
        }

        String messagesIdFromIntegerList = messagesId.toString();

        debug("moveMessages()           :");
        debug("userNumber               :" + userNumber);
        debug("getKeyId                 :" + theMain.getKeyId());
        debug("oldFolderId              :" + oldFolderId);
        debug("newFolderId              :" + newFolderId);
        debug("messagesIdFromIntegerList:" + messagesIdFromIntegerList);

        // Do the move on the server, because of security concerns
        AwakeConnection awakeConnection = (AwakeConnection)connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        awakeFileSession.call("net.safester.server.MessageMover.moveMessages",
                userNumber,
                theMain.getKeyId(),
                oldFolderId,
                newFolderId,
                messagesIdFromIntegerList,
                connection);
        
        MessageLocalStoreCache.remove(oldFolderId);
        MessageLocalStoreCache.remove(newFolderId);
        
        theMain.createTable();

    }        

    private static void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }
    
}

