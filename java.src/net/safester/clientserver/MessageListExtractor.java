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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import static java.lang.reflect.Array.set;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import java.util.Set;
import net.safester.noobs.clientserver.GsonUtil;
import net.safester.noobs.clientserver.MessageLocal;
import org.awakefw.file.api.client.AwakeFileSession;

import org.awakefw.sql.api.client.AwakeConnection;

/**
 * @author Nicolas de Pomereu
 *
 * Transfer a List of MessageLocal between PC <==> SQL Server
 */
public class MessageListExtractor {

    public static boolean DEBUG = true;

    /**
     * The Jdbc connection
     */
    private Connection connection = null;

    /**
     * The user number
     */
    private int userNumber = 0;


    /**
     * Stores all the dowload messages count
     */
    public static int downloadedMessages;

    /**
     * Constructor to use when using MessageListExtractor from engine used by
     * search
     *
     * @param connection
     * @param user_number
     */
    public MessageListExtractor(Connection connection, int user_number) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
        this.userNumber = user_number;
    }

    /**
     * Get a list of MessageLocal corresponding to the List of messageIds.
     *
     * @param messageIds
     * @param folderIds
     * @param offset
     * @param limit
     * @return
     * @throws Exception
     */
    public List<MessageLocal> getMessagesFromIdListWithLimit(Set<Integer> messageIds, List<Integer> folderIds, int offset, int limit)
            throws Exception {

        debug("userNumber: " + userNumber);
        debug("BEFORE getMessagesFromIdListWithLimit");

        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        List<Integer> messageIdsList = new ArrayList<>(messageIds);
        
        String jsonMessageIds = GsonUtil.listIntegerToGson(messageIdsList);
        String jsonFolderIds = GsonUtil.listIntegerToGson(folderIds);

        String jsonString = awakeFileSession.call("net.safester.server.MessageSelectSearch.getMessagesFromIdListWithLimit",
                userNumber,
                jsonMessageIds,
                jsonFolderIds,
                offset,
                limit,
                connection);

        debug("AFTER getMessagesFromIdListWithLimit jsonString: " + jsonString);

        Gson gsonOut = new Gson();
        Type messageLocalStoreType = new TypeToken<List<MessageLocal>>() {
        }.getType();

        List<MessageLocal> messages = gsonOut.fromJson(jsonString, messageLocalStoreType);
        return messages;
    }

    /**
     * debug tool
     */
    private void debug(String s) {
        if (DEBUG) {
            System.out.println(this.getClass().getName()
                    + " "
                    + new java.util.Date()
                    + " "
                    + s);
        }
    }
}
