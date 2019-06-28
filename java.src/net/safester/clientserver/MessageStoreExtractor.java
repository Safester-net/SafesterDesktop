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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.safester.application.MessageDecryptor;
import net.safester.application.parms.Parms;
import net.safester.clientserver.specs.StoreExtractor;
import net.safester.noobs.clientserver.MessageLocal;
import net.safester.noobs.clientserver.PendingMessageUserLocal;
import net.safester.noobs.clientserver.RecipientLocal;

/**
 * @author Nicolas de Pomereu
 *
 * Return a LocalStore of MessageLocal between PC <==> SQL Server
 */
public class MessageStoreExtractor implements StoreExtractor<MessageLocalStore>
{

    /** The debug flag */
    public static boolean DEBUG = false;
    
    /** The Jdbc connection */
    private Connection connection = null;

    /** The user number */
    private int userNumber = 0;

    /** The folder id to use */
    private int folderId;

    /** The Limit ... offset ... clause holder */
    private LimitClause limitClause = null;

    /** The passphrase to use */
    private char [] passphrase;

    /**
     * Constructor  which uses a LIMIT/OFSET clause
     * @param theConnection    the JDBC theConnection
     * @param passphrase        the passphrase
     * @param limitClause      the LIMIT/OFFSET clause
     * @param user_number       the user number
     * @param folder_id         the folder Id to which belongs the message list
     */
    public MessageStoreExtractor(Connection theConnection,
                                int user_number,
                                char[] passphrase,
                                int folder_id, 
                                LimitClause limitClause)
    {
        if (theConnection == null)
        {
            throw new IllegalArgumentException("Connection can\'t be null");
        }

        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();
        
        this.userNumber = user_number;
        this.passphrase = passphrase;
        this.folderId = folder_id;
        this.limitClause = limitClause;
    }



    /**
     * 
     * Get the total number of messages for this user Id and folder Id.
     * <br>
     * Value may be get/set from/to cache if:
     * <ul> 
     * <li>offset value of LimitClause is 0. (Aka we ask for the first page of the folder).</li>
     * </ul>
     * 
     * @return the total number of messages for this user Id and folder Id
     */
    public int getTotalMessages() throws SQLException
    {        
        MessageSelectCaller messageSelectCaller = new MessageSelectCaller(userNumber, folderId, connection);
        
        int totalMessages = -1;

        // No cache for Draft folders
        if (folderId == Parms.DRAFT_ID)
        {
            try {
                totalMessages = messageSelectCaller.count();
            } catch (Exception exception) {
                throw new SQLException(exception);
            }
            return totalMessages;
        }
        
        if (MessageLocalStoreCache.containsKeyTotalMessages(folderId))
        {
            totalMessages =  MessageLocalStoreCache.getTotalMessages(folderId);
        }
        else
        {
            try {
                totalMessages = messageSelectCaller.count();
            } catch (Exception exception) {
                throw new SQLException(exception);
            }
            MessageLocalStoreCache.putTotalMessages(folderId, totalMessages);
        }
        
        return totalMessages;
    }


    @Override
    public MessageLocalStore getStore() throws SQLException
    {
        MessageLocalStore messageLocalStore = null;

        // No cache for Draft folders
        if (folderId == Parms.DRAFT_ID)
        {
            messageLocalStore =  selectAndBuildStore();
            return messageLocalStore;
        }

        int offset = limitClause.getOffset();
        if (MessageLocalStoreCache.containsKey(folderId, offset))
        {
            messageLocalStore =  MessageLocalStoreCache.get(folderId, offset);
        }
        else
        {
            messageLocalStore =  selectAndBuildStore();

            long begin = new Date().getTime();
            int sizeStorage = MessageLocalStoreCache.sizeStorage();
            long end = new Date().getTime();

            debug("");
            debug("sizeStorage() time: " + (end-begin));
            debug("sizeStorage() byte: " +  sizeStorage);

            // Clear the cache if it's size limit is reached
            if (sizeStorage >= Parms.MAX_MESSAGE_CACHE_SIZE)
            {
                MessageLocalStoreCache.clear();
            }
            
            MessageLocalStoreCache.put(folderId, offset, messageLocalStore);

        }
                
        return messageLocalStore;
    }


    /**
     * @return  the MessageLocalStore for this request
     * @throws SQLException
     */
    private MessageLocalStore selectAndBuildStore() throws SQLException
    {
        MessageLocalStore messageLocalStore = null;
	MessageSelectCaller messageSelectCaller = new MessageSelectCaller(userNumber, folderId, limitClause, connection);
        
        try {
            messageLocalStore = messageSelectCaller.selectMessages();
        } catch (Exception e) {
	    throw new SQLException(e);
	}
        
        Set<Integer> messageIdSet = messageLocalStore.keySet();
        
        MessageDecryptor messageDecryptor = new MessageDecryptor(userNumber,
                passphrase, connection);
                
        for (Integer messageId : messageIdSet) {
            
            MessageLocal messageLocal = messageLocalStore.get(messageId);
            String decrypedSubject = null;
            
            String subject = messageLocal.getSubject();
            
            if (isSubjectEncrypted(subject)) {
                try {
                    //decrypedSubject = messageDecryptor.decrypt(messageLocal.getSubject());
                    decrypedSubject = messageDecryptor.decrypt(subject);
                } catch (Exception exception) {
                    throw new SQLException(exception);
                }

                messageLocal.setSubject(decrypedSubject);
            }
            else {
                messageLocal.setSubject(subject);
            }

            messageLocalStore.put(messageId, messageLocal);
        }
        
        if (! messageLocalStore.isEmpty())
        {
            // The map of the List<RecipientLocal> of the Messages. Key is message_id
            Map<Integer, List<RecipientLocal>> recipientLocalMap = getRecipients(userNumber, messageIdSet, connection);      
            
            //folderId == Parms.DRAFT_ID
            Map<Integer, List<PendingMessageUserLocal>> pendingMessageUserLocalMap = new HashMap<>();
            
            if (folderId != Parms.INBOX_ID) {
                pendingMessageUserLocalMap = getPendingMessageUser(messageIdSet);
            }

            debug("");
            //debug("senderNameMap: " + senderNameMap);
            debug("");
            debug("recipientLocalMap: " + recipientLocalMap);
            //debug("message id 2: " + messageLocalStore.keySet());
            
            List<Integer> theList =  new ArrayList<Integer>(messageLocalStore.keySet());
            
            for (int i = 0; i < theList.size(); i++) {
                
                int messageId = theList.get(i);
                
                MessageLocal messageLocal = messageLocalStore.get(messageId);
                int senderUserNumber = messageLocal.getSenderUserNumber();

                debug("");
                debug("messageId       : " + messageId);
                debug("senderUserNumber: " + senderUserNumber);
                
                List<RecipientLocal> recipientLocalList = recipientLocalMap.get(messageId);
                if (recipientLocalList == null)
                {
                    recipientLocalList = new Vector<RecipientLocal>();
                }
                
                messageLocal.setRecipientLocal(recipientLocalList);
                
                List<PendingMessageUserLocal> pendingMessageUserLocalList = pendingMessageUserLocalMap.get(messageId);
                if (pendingMessageUserLocalList == null)
                {
                    pendingMessageUserLocalList = new Vector<PendingMessageUserLocal>();
                }
                                
                messageLocal.setPendingMessageUserLocal(pendingMessageUserLocalList);
                
                // Put back into messageLocalList
                messageLocalStore.put(messageId, messageLocal);
            }
        }
        
        return messageLocalStore;
    }


/*
    private void decryptSubjects(MessageSubjectLocalStore messageStore) throws Exception {
	Set<Integer> messageIdSetTemp = messageStore.keySet();

	MessageDecryptor messageDecryptor = new MessageDecryptor(userNumber,
		passphrase, connection);

	// OK get and decrypt subject & body and put them back in local store
	for (int messageId : messageIdSetTemp) {
	    MessageSubjectLocal messageLocal = messageStore
		    .get(messageId);
	    String subject = messageDecryptor
		    .decrypt(messageLocal.getSubject());
	    messageLocal.setSubject(subject);
	    messageLocal.setIntegrityCheck(
		    messageDecryptor.isIntegrityCheckValid());
        }
    }
*/

    /**
     * build the list of Recipient Local per message Id
     * @param userNumber
     * @param messageIdSet      the message Id to scan
     * @param connection        the JDBC Connection
     * 
     * @return the list of Recipient Local per message Id
     * 
     * @throws SQLException
     */
    public static Map<Integer, List<RecipientLocal>> getRecipients(int userNumber, Set<Integer> messageIdSet, Connection connection)
            throws SQLException
    {
        Gson gsonOut = new Gson();
        Type type = new TypeToken<Set<Integer>>() {
        }.getType();
        String jsonString = gsonOut.toJson(messageIdSet, type);

        //Put the list on the server, because of intricated SQL statements        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
        
        try
        {
            jsonString = awakeFileSession.call( "net.safester.server.MessageSelectSearch.getRecipients",
                                    userNumber,
                                    jsonString,
                                    connection );
            
            gsonOut = new Gson();
            type = new TypeToken<Map<Integer, List<RecipientLocal>>>() {
            }.getType();
            Map<Integer, List<RecipientLocal>> recipientLocalMap = gsonOut.fromJson(jsonString, type);
            return recipientLocalMap;
            
        }
        catch (Exception e)
        {
            throw new SQLException(e);
        }
    }
    
    /**
     * build the list of Recipient Local per message Id
     * @param messageIdSet      the message Id to scan
     * @return the list of Recipient Local per message Id
     * @throws SQLException
     */
    private Map<Integer, List<PendingMessageUserLocal>> getPendingMessageUser(Set<Integer> messageIdSet)
            throws SQLException
    {
           
        Gson gsonOut = new Gson();
        Type type = new TypeToken<Set<Integer>>() {
        }.getType();
        String jsonString = gsonOut.toJson(messageIdSet, type);

        //Put the list on the server, because of intricated SQL statements        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
        
        try
        {
            jsonString = awakeFileSession.call( "net.safester.server.MessageSelectSearch.getPendingMessageUser",
                                    userNumber,
                                    jsonString,
                                    connection );
            
            gsonOut = new Gson();
            type = new TypeToken<Map<Integer, List<PendingMessageUserLocal>>>() {
            }.getType();
            Map<Integer, List<PendingMessageUserLocal>> pendingMessageUserLocalList = gsonOut.fromJson(jsonString, type);
            return pendingMessageUserLocalList;
            
        }
        catch (Exception e)
        {
            throw new SQLException(e);
        }
    }

    
    /**
     * debug tool
     */
    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
    }

    public static boolean isSubjectEncrypted(String subject) {
        
        // To avoid Exception
        if (subject == null) {
            return false;
        }
        
        if (subject.trim().contains("-BEGIN PGP MESSAGE-") && subject.trim().contains("-END PGP MESSAGE-")) {
            return true;
        }
        else {
         return false;   
        }
    }
}
