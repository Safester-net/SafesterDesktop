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

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.awakefw.commons.api.client.RemoteException;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.kawansoft.httpclient.KawanHttpClient;

import net.safester.application.http.ApiMessages;
import net.safester.application.http.KawanHttpClientBuilder;
import net.safester.application.http.dto.MessageHeaderDTO;
import net.safester.application.http.dto.MessageListDTO;
import net.safester.application.http.dto.RecipientDTO;
import net.safester.application.parms.Parms;
import net.safester.noobs.clientserver.MessageLocal;
import net.safester.noobs.clientserver.RecipientLocal;

public class MessageSelectCaller {

    public static boolean DEBUG = false;

    /**
     * The user number
     */
    private int userNumber = 0;

    /**
     * The folder id to use
     */
    private int folderId;

    /**
     * The Limit ... offset ... clause holder
     */
    private LimitClause limitClause = null;

    /**
     * The Jdbc connection
     */
    private Connection connection = null;

    public MessageSelectCaller(int userNumber, int folderId, LimitClause limitClause, Connection connection) {
        this.userNumber = userNumber;
        this.folderId = folderId;
        this.limitClause = limitClause;
        this.connection = connection;
    }

    MessageSelectCaller(int userNumber, int folderId, Connection connection) {
        this.userNumber = userNumber;
        this.folderId = folderId;
        this.connection = connection;
    }

    public MessageLocalStore selectMessages() throws SQLException, IllegalArgumentException, UnknownHostException,
            ConnectException, RemoteException, IOException {

        if (this.limitClause == null) {
            throw new NullPointerException("limitClause is null!");
        }
        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

//	String jsonString = awakeFileSession.call("net.safester.server.MessageSelectNew.selectMessages", userNumber,
//		folderId, limitClause.toString(), connection);
        KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.buildFromAwakeConnection(awakeConnection);
        ApiMessages apiMessages = new ApiMessages(kawanHttpClient, awakeFileSession.getUsername(),
                awakeFileSession.getAuthenticationToken());
        MessageListDTO messageListDTO = null;
        if( this.folderId == Parms.STARRED_ID ) {
        	try {
        		messageListDTO = apiMessages.listMessagesStarred(limitClause.getLimit(), limitClause.getOffset());
        	} catch (Exception e) {
	            throw new SQLException(e);
	        }
        } else {
	        try {
	            messageListDTO = apiMessages.listMessages(this.folderId, limitClause.getLimit(), limitClause.getOffset());
	        } catch (Exception e) {
	            throw new SQLException(e);
	        }
        }
        List<MessageHeaderDTO> messages = messageListDTO.getMessages();

        MessageLocalStore messageLocalStore = new MessageLocalStore();

        for (MessageHeaderDTO message : messages) {

            int message_id = (int) message.getMessageId();
            String priority = message.getPriority();
            boolean is_with_attachment = message.isHasAttachs();
            boolean is_encrypted = message.isEncrypted();
            boolean is_signed = message.isSigned();
            Timestamp date_message = new Timestamp(message.getDate());
			
            boolean printable = message.isPrintable();
            boolean fowardable = message.isFowardable();

            //TODO ABE: use only message.getFolderId() when API is updated 
            int folder_id = this.folderId == Parms.STARRED_ID ? message.getFolderId() : this.folderId;
            boolean is_read = message.isRead();
            int sender_user_number = message.getSenderUserNumber();
            boolean isStarred = message.isStarred();
            
            String senderEmail = message.getSenderEmailAddr();
            String senderUserName = message.getSenderName();

            MessageLocal messageLocal = new MessageLocal();
            messageLocal.setMessageId(message_id);
            messageLocal.setPriority(priority);
            messageLocal.setIsWithAttachment(is_with_attachment);
            messageLocal.setIsEncrypted(is_encrypted);
            messageLocal.setIsSigned(is_signed);
            messageLocal.setIsStarred(isStarred);
            messageLocal.setDateMessage(date_message);

            messageLocal.setSenderUserEmail(senderEmail);
            messageLocal.setSenderUserName(senderUserName);

            messageLocal.setSubject(message.getSubject());

            long size_message = message.getSize();
            messageLocal.setSizeMessage(size_message);

            messageLocal.setPrintable(printable);
            messageLocal.setFowardable(fowardable);
            messageLocal.setSenderUserNumber(sender_user_number);

            messageLocal.setFolderId(folder_id);
            if (folder_id != Parms.OUTBOX_ID && folder_id != Parms.DRAFT_ID) {
                messageLocal.setIsRead(is_read);
            } else {
                messageLocal.setIsRead(true);
            }

            messageLocal.setRecipientLocal(getRecipientLocalList(message.getRecipients()));

            messageLocalStore.put(message_id, messageLocal);

        }

        return messageLocalStore;
    }

    private List<RecipientLocal> getRecipientLocalList(List<RecipientDTO> recipients) {

        List<RecipientLocal> recpipientLocalList = new ArrayList<>();
        for (final RecipientDTO recipient : recipients) {
            RecipientLocal recipientLocal = new RecipientLocal();
            recipientLocal.setEmail(recipient.getRecipientEmailAddr());
            recipientLocal.setNameRecipient(recipient.getRecipientName());
            recipientLocal.setTypeRecipient(recipient.getRecipientType());
            recipientLocal.setRecipientPosition(recipient.getRecipientPosition());
            recpipientLocalList.add(recipientLocal);
        }
        return recpipientLocalList;
    }

    /**
     * Gets the number of mssages per folder id
     * @return
     * @throws SQLException
     * @throws IllegalArgumentException
     * @throws UnknownHostException
     * @throws ConnectException
     * @throws RemoteException
     * @throws IOException 
     */
    int count() throws SQLException, IllegalArgumentException, UnknownHostException, ConnectException, RemoteException,
            IOException {

        if (folderId == Parms.DRAFT_ID) {
            MessageSelectCallerDraft messageSelectCallerDraft = new MessageSelectCallerDraft(userNumber);
            return messageSelectCallerDraft.count();
        }
                
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
        KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.buildFromAwakeConnection(awakeConnection);
        ApiMessages apiMessages = new ApiMessages(kawanHttpClient, awakeFileSession.getUsername(),
                awakeFileSession.getAuthenticationToken());

        int count = 0;
        try {
            count = apiMessages.countMessages(folderId);
        } catch (Exception e) {
            throw new SQLException(e);
        }

        return count;
    }

    private void debug(String string) {
        if (DEBUG) {
            System.out.println(string);
        }

    }

    private MessageLocalStore getDraftMessageLocalStore() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
