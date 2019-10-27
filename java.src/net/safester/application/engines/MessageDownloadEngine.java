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
package net.safester.application.engines;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.safelogic.pgp.api.util.parms.CmPgpCodes;

import net.safester.application.MessageDecryptor;
import net.safester.application.messages.MessagesManager;
import net.safester.clientserver.MessageListExtractor;
import net.safester.clientserver.MessageStoreExtractor;
import net.safester.noobs.clientserver.GsonUtil;
import net.safester.noobs.clientserver.MessageLocal;

/**
 * Message download engine, used by search
 *
 * @author Alexandre Becquereau
 */
public class MessageDownloadEngine extends Thread {

    public static boolean DEBUG = true;
        
    public static final int MAXIMUM_PROGRESS = 100;

    /* National language messages */
    private MessagesManager messages = new MessagesManager();

    /* Progress index*/
    private int m_current = 0;
    /**
     * Note to pass to the progression bar
     */
    private String m_note = null;

    /**
     * The return code
     */
    private int m_returnCode = CmPgpCodes.RC_ERROR;

    private Connection connection;
    private int userNumber;
    private Set<Integer> messageIds;
    private List<Integer> folderIds;
    private int nbMessagesToDownload;

    WaiterEngine m_waiterEngine;
    List<MessageLocal> decryptedMessages;
    Exception m_exception;
    private char[] passphrase;
    
    private static boolean DEBUG_BODY_SUBJECT = false;

    private static Map<Integer, Set<String>> pendingEmailsMap = new HashMap<>();

    public MessageDownloadEngine(Connection theConnection, WaiterEngine waiterEngine, int theUserNumber, char[] passphrase, Set<Integer> theMessageIds, List<Integer> theFolderIds) {

        if (theConnection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        if (theUserNumber < 0) {
            throw new IllegalArgumentException("Bad user number: " + theUserNumber);
        }
        if (theMessageIds == null) {
            throw new IllegalArgumentException("theMessagesId cannot be null");
        }
        if (theFolderIds == null) {
            throw new IllegalArgumentException("theFolderIds cannot be null");
        }

        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection) theConnection).clone();

        this.userNumber = theUserNumber;
        this.passphrase = passphrase;
        this.messageIds = theMessageIds;
        this.folderIds = theFolderIds;
        this.m_waiterEngine = waiterEngine;
    }

    @Override
    public void run() {
        //Download 100  messages in One query
        int limit = 100;
        m_current = 1;
        this.nbMessagesToDownload = messageIds.size();
        this.m_returnCode = CmPgpCodes.RC_OK;
        m_waiterEngine.setWaiterStop(true);
        m_note = messages.getMessage("downloading_messages");
       
        try {
            MessageDecryptor messageDecryptor = new MessageDecryptor(this.userNumber, this.passphrase, this.connection);
                    
            MessageListExtractor messageListExtractor = new MessageListExtractor(connection, userNumber);

            int downloadedMessages = 0;
            decryptedMessages = new ArrayList<>();

            debug("messagesId.size(): " + messageIds.size());
                  
            pendingEmailsMap = buildPendingUsersMap(messageIds, connection);
            
            //Download all messages            
            while (downloadedMessages < messageIds.size()) {

                List<MessageLocal> messageLocalList = messageListExtractor.getMessagesFromIdListWithLimit(messageIds, folderIds, downloadedMessages, downloadedMessages + limit);
                
                debug("messageLocalList.size(): " + messageLocalList.size());
                
                decryptMessages(messageDecryptor, messageLocalList);
                debug("AFTER decryptMessages... ");               
                
                decryptedMessages.addAll(messageLocalList);
                downloadedMessages += limit;

                //(100 X Valeur Partielle) / Valeur Totale
                int partialValue = messageLocalList.size();
                int totalValue = messageIds.size();
                int percent = (100 * partialValue) / totalValue;
               
                debug("downloadedMessages: " + downloadedMessages);
                debug("partialValue      : " + partialValue);
                
                addPercentToCurrentPercent(percent);
            }
            m_current = MAXIMUM_PROGRESS;
        } catch (Exception e) {
             m_current = MAXIMUM_PROGRESS;
            this.m_returnCode = CmPgpCodes.RC_ERROR;
            this.m_exception = e;
        }
    }

    /**
     * Build the Map of the pending user email adresses per messages
     * @param messageIds
     * @param connection
     * @return
     */
    public static Map<Integer, Set<String>> buildPendingUsersMap(Set<Integer> messageIds, Connection connection) throws Exception   {
        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        List<Integer> messageIdsList = new ArrayList<>(messageIds);
        
        String jsonMessageIds = GsonUtil.listIntegerToGson(messageIdsList);

        String jsonString = awakeFileSession.call("net.safester.server.hosts.newapi.MessagesNewApi.buildPendingUsersMap",
                jsonMessageIds,
                connection);
        
        Gson gsonOut = new Gson();
        Type type = new TypeToken<Map<Integer, Set<String>>>() {
        }.getType();
        Map<Integer, Set<String>> pendingUsersMap = gsonOut.fromJson(jsonString, type);
        return pendingUsersMap;
        
    }
    
    /**
     * @return the pendingEmailsMap
     */
    public static Map<Integer, Set<String>> getPendingEmailsMap() {
        return pendingEmailsMap;
    }
    
    public List<MessageLocal> getDecryptedMessages() {
        return decryptedMessages;
    }

    public Exception getException() {
        return m_exception;
    }

    public void addOneToCurrent() {
        m_current++;
    }

    public int getCurrent() {
        return m_current;
    }

    public String getNote() {
        return m_note;
    }

    public int getNbMessagesToDownload() {
        return nbMessagesToDownload;
    }

    private void addPercentToCurrentPercent(int percent) {
        int newCurrent = m_current + percent;

        if (newCurrent < 99) { // Stays always < 100 on indicator display
            m_current = newCurrent;
        } else {
            m_current = 99;
        }
    }

    private void decryptMessages(MessageDecryptor messageDecryptor , List<MessageLocal> messageLocalList) throws SQLException, Exception {

        for (MessageLocal messageLocal : messageLocalList) {

            debug("BEFORE decrypt of message: " + messageLocal.getMessageId());
            String body = messageLocal.getBody();
            String subject = messageLocal.getSubject();

            if (MessageStoreExtractor.isSubjectEncrypted(subject)) {
                try {
                    subject = messageDecryptor.decrypt(subject);

                    if (!messageDecryptor.isIntegrityCheckValid()) {
                        messageLocal.setIntegrityCheck(false);
                    }
                } catch (Exception exception) {
                    System.err.println(subject + ": bodys id. FAIL TO DECRYPT SUBJECT!");
                    subject = "*** Can not decrypt Subject ***";
                }
            }

            
            try {
                body = messageDecryptor.decrypt(body);

                if (!messageDecryptor.isIntegrityCheckValid()) {
                    messageLocal.setIntegrityCheck(false);
                }

            } catch (Exception e) {
                System.err.println(body + ": bodys id. FAIL TO DECRYPT BODY: " + e);
                body = "*** Can not decrypt Body ***";
            }

            if (DEBUG_BODY_SUBJECT) {
                debug("subject-1: " + subject);
                debug("body-1   : " + body);
            }

            messageLocal.setSubject(subject);
            messageLocal.setBody(body);

        }
    }
    
    
    	/**
	 * debug tool
	 */
    private void debug(String s)
    {
        if (DEBUG)
            System.out.println(this.getClass().getName() 
                    + " " 
                    + new java.util.Date() 
                    + " "
                    + s);
    }

    


}
