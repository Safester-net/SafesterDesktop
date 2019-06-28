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
import java.util.Vector;

import javax.swing.JOptionPane;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.awakefw.sql.api.client.AwakeConnection;

import net.safester.application.messages.MessagesManager;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.specs.UniqueTransfer;
import net.safester.noobs.clientserver.AttachmentLocal;
import net.safester.noobs.clientserver.GsonUtil;
import net.safester.noobs.clientserver.MessageLocal;

/**
 * @author Nicolas de Pomereu
 *
 * Transfer a MessageLocal between PC <==> SQL Server
 * <br><br>
 * 1) Howto to send an email:
 * <br>- Create the list of  List AttachmentLocal (List<AttachmentLocal>)
 * <br>- Create the list of  List RecipientLocal (List<RecipientLocal>)
 * <br>- Create the MessageLocal and set the two previous lists in MessageLocal
 * <br>- Create the MessageTransfer(Connection connection, int userNumber) instance
 * <br>- Encrypt the body & the attachments.
 * <br>- Upload in thread (create a listener) the attachments with AttachmentListTransfer.put()
 * <br>- When the thread is terminated ==> MessageTransfer.put()
 * <br>- Call the remote method that sends the notification email
 * <br>
 * 1) Howto to receive an email:
 * <br> - To be done.
 */
public class MessageTransfer implements UniqueTransfer<MessageLocal>
{
   public static boolean DEBUG = false;

    /** The number that idenfies the Inbox Folderin the database  */
    public static int INBOX_FOLDER_NUMBER = 1;

    /** The number that idenfies the Outbox  Folder in the database */
    public static int OUTBOX_FOLDER_NUMBER = 2;

    /** The Jdbc connection */
    private Connection connection = null;

    /** The user number */
    private int userNumber = 0;

    /** The key id */
    private String keyId;
    
    /** The message Id */
    private int messageId = 0;

    /** The folder Id */
    private int folderId = 0;
    
    /**
     * Default Constructor. For a Insert into the database.
     * <br>
     * The Message Id will be generated, no need for it
     * @param userNumber        the user number to get the message from
     * @param connection        the JDBC connection
     */
    public MessageTransfer(Connection connection, int userNumber)
    {
        if (connection == null)
        {
            throw new IllegalArgumentException("Connection can\'t be null");
        }

        this.connection = connection;
        this.userNumber = userNumber;
    }
    
    /**
     * 
     *  Use when sending z Message (or Saving a Draft) 
     * 
     * @param connection
     * @param keyId
     * @param userNumber
     */
    public MessageTransfer(Connection connection, String keyId, int userNumber)
    {
        this(connection, userNumber);
        this.keyId = keyId;
    }
    
    /**
     * Constructor. For a Select on the database
     * @param connection        the JDBC connection
     * @param userNumber        the user number to get the message from
     * @param messageId         the message Id to get the message from
     */
    public MessageTransfer(Connection connection, int userNumber, int messageId)
    {
        this(connection, userNumber);

        this.userNumber = userNumber;
        this.messageId = messageId;
    }

    /**
     * Constructor. For a Delete on the database
     * @param connection        the JDBC connection
     * @param userNumber        the user number to get the message from
     * @param messageId         the message Id to get the message from
     */
    public MessageTransfer(Connection connection, int userNumber, int messageId, int folderId)
    {
        this(connection, userNumber, messageId);

        this.folderId = folderId;
    }

    /**
     * Get a message from the server using the criteria: Message Id & User Number
     */
    @Override
    public MessageLocal get() throws SQLException
    {       
        throw new UnsupportedOperationException("Method is not anymore used in SafeShareit");
    }
    
    
    @Override
    public void put(MessageLocal messageLocal) throws SQLException
    {
        // Does the effective send of the message
        int newMessageId = putMessage(messageLocal, false);

        AwakeConnection awakeConnection = (AwakeConnection)connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        try
        {
            if(! messageLocal.isAnonymousNotification())
            {
                awakeFileSession.call("net.safester.server.NotificationEmailSender.sendNotificationEmail", connection, newMessageId);
            }
            else
            {
                awakeFileSession.call("net.safester.server.NotificationEmailSender.sendAnonymousNotificationEmail", connection, newMessageId);
            }
        }
        catch (Exception e)
        {
            MessagesManager messages = new MessagesManager();
            JOptionPane.showMessageDialog(null, messages.getMessage("warning_notification_was_not_sent"));
        }
    }

    public int putDraft(MessageLocal messageLocal) throws SQLException
    {
        int newMessageId = putMessage(messageLocal, true);
        return newMessageId;
    }


    private int putMessage(MessageLocal messageLocal, boolean isDraft) throws SQLException 
    {
        //Gson gsonOut = new Gson();
        //Type typeOfMessageLocal = new TypeToken<MessageLocal>(){}.getType();
        //String jsonString = gsonOut.toJson(messageLocal, typeOfMessageLocal);

        // Intercept attachment names
        List<AttachmentLocal> AttachmentLocalList = messageLocal.getAttachmentLocal();

        List<AttachmentLocal> htmlAttachmentLocalList = new Vector<AttachmentLocal>();

        for (AttachmentLocal attachmentLocal : AttachmentLocalList) {
            String fileName = attachmentLocal.getRemoteFileName();

            debug("fileName (before conversion): " + fileName);
            fileName = HtmlConverter.toHtml(fileName);
            debug("fileName (after  conversion): " + fileName);

            attachmentLocal.setRemoteFileName(fileName);
            htmlAttachmentLocalList.add(attachmentLocal);
        }

        messageLocal.setAttachmentLocal(htmlAttachmentLocalList);

        AwakeConnection awakeConnection = (AwakeConnection)connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        int newMessageId = -1;
        String newMessageIdStr = "-1";
        
        try
        {
            // First get the New Message ID to use on Main and Http File Server
            newMessageIdStr = awakeFileSession.call("net.safester.server.hosts.MessageCreatorMain.getMessageId", userNumber, keyId, connection);
            
            // Set the Message ID on MessageLocal immediately
            messageLocal.setMessageId(Integer.parseInt(newMessageIdStr));
                            
            String jsonString = GsonUtil.messageLocalToGson(messageLocal);
            debug("jsonString: " + jsonString);
            // 2) Put message on main server using SQL. Subject is stored on file system. Remote will commit if everything ok.
            newMessageIdStr = awakeFileSession.call("net.safester.server.hosts.MessageCreatorMain.putMessageWithSubject", 
                                                 jsonString,
                                                 userNumber,
                                                 keyId,
                                                 isDraft,
                                                 connection);
        }
        catch (Exception e)
        {
            throw new SQLException(e);
        }
        
        try
        {
            newMessageId = Integer.parseInt(newMessageIdStr);
        }
        catch (NumberFormatException e)
        {
            JOptionPaneNewCustom.showException(null, e);
        }
        
        return newMessageId;
    }


    /**
     * Remove the Message *reference* from the user_message table
     * <br>
     * IF the messageId does not exists anymore in the message_user table, delete it completely
     * from the Server
     */
    @Override
    public void remove() throws SQLException
    {
	throw new IllegalArgumentException("Not implemented!");
    }

    /**
     * Change isRead status of a messageUser
     * @param isRead			new is read status
     * @throws SQLException
     */
    public void setMessageIsRead(boolean isRead)
    throws SQLException
    {
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        String keyId = awakeFileSession.getUsername();
        
        String methodRemote = "net.safester.server.hosts.newapi.MessagesNewApi.setMessageIsRead";
        debug("methodRemote: " + methodRemote);

        try
        {
            awakeFileSession.call(methodRemote,
                                                userNumber,
                                                keyId,
                                                messageId,
                                                folderId,
                                                isRead,
                                                connection
                                                );
        }
        catch (Exception e)
        {
            throw new SQLException(e);
        }
        
    }

    /**
     * return the size of storage of all mailboxes for a use
     * @param connection        Connection to db
     * @param userNumber            user id of the user
     * @return 
     *
     * @throws SQLException
     */
    public static long getTotalMailboxSize(Connection connection, int userNumber)
    throws SQLException{

        long actualStore = 0;

        //Put the list on the server, because of intricated SQL statements        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
        
        try
        {
            String jsonString = awakeFileSession.call( "net.safester.server.hosts.newapi.MessagesNewApi.getTotalMailboxSize",
                                    userNumber,
                                    connection );
            
            actualStore = Long.parseLong(jsonString);
        }
        catch (Exception e)
        {
            throw new SQLException(e);
        }

        return actualStore;
    }

    /**
     * debug tool
     */
    private static void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

    
}
