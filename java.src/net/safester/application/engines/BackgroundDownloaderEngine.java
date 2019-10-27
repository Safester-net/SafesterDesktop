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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.awakefw.sql.api.client.AwakeConnection;

import com.kawansoft.httpclient.KawanHttpClient;

import net.safester.application.MessageDecryptor;
import net.safester.application.http.ApiMessages;
import net.safester.application.http.KawanHttpClientBuilder;
import net.safester.application.http.dto.AttachmentInfoDTO;
import net.safester.application.http.dto.MessageDTO;
import net.safester.clientserver.MessageLocalStore;
import net.safester.clientserver.holder.TheUserSettingsHolder;
import net.safester.clientserver.holder.UserCompletionHolder;
import net.safester.noobs.clientserver.AttachmentLocal;
import net.safester.noobs.clientserver.MessageLocal;

/**
 * @author Nicolas de Pomereu
 * Loads all the Bodies and Attachments of messages.
 * <br>
 * Also, loads *once* the User Settings datas and the Completion Data.
 *
 */

public class BackgroundDownloaderEngine extends Thread
{

    /** if true, the thread is still alive */
    private static boolean isAlive = false;

    /** If true, a thread interruption has been asked */
    private static boolean isRequestInterrupt = false;
      
    /** The throw Exception if any for BodyDownloader for Bodies download*/
    private Exception exception = null;

    /** The throw Exception if any for User Settings*/
    private Exception exceptionUserSettings = null;
    
    // Constructor objects
    
    /** the user number */
    private int userNumber;
    
    /** The passphrase to use for decryption */
    private char [] passphrase;
    
    /** The Local Message store */
    private MessageLocalStore messageLocalStore = null;
    
    /** The SQL Jdbc Connection */
    private Connection connection;    
 
    /**
     * @param userNumber            the userNumber to get the messages for
     * @param passphrase            the decryption passphrase
     * @param messageLocalStore     the store with all the messages
     * @param theConnection         the jdbc theConnection
     */
    public BackgroundDownloaderEngine(int userNumber, char[] passphrase,
            MessageLocalStore messageLocalStore, Connection theConnection)
    {
        this.userNumber = userNumber;
        this.passphrase = passphrase;
        this.messageLocalStore = messageLocalStore;
        
        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();

    }

    /**
     * @param aIsRequestInterrupt the isRequestInterrupt to set
     */
    public static void setIsRequestInterrupt(boolean aIsRequestInterrupt) {
        isRequestInterrupt = aIsRequestInterrupt;
    }

    
    /**
     * Download all the Body string and decrypt them
     */
    @Override
    public void run()
    {        
        try
        {
            //Wait for previous thread to end
            while(isAlive){
                sleep(100);
            }

            isRequestInterrupt = false;
            isAlive = true;
                     
            Set<Integer> messageIdSet = messageLocalStore.keySet();
            
            // must be done after
            updateBody(messageIdSet);

            if (isRequestInterrupt)
            {
                return;
            }
            
            // Update the User Settings in a final separated thread
            userSettingsThread();

        }
        catch (Exception e)
        {
            this.exception = e;
            e.printStackTrace();
        }
        finally
        {
            isRequestInterrupt = false;
            isAlive = false;
        }
                
    }

    private void userSettingsThread()
    {
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    // Update the User Settings
                    TheUserSettingsHolder theUserSettingsHolder = new TheUserSettingsHolder(connection, userNumber);
                    theUserSettingsHolder.load();

                    UserCompletionHolder userCompletionHolder = new UserCompletionHolder(connection, userNumber);
                    userCompletionHolder.load();
                } catch (Exception e) {
                    exceptionUserSettings = e;
                }
            }
        };
        t.start();
            
    }

    /**
     * Update the Bodies
     * 
     * @param messageIdSet  the set of Messages Ids
     * @throws Exception    if a SQL Exception or a decryption exception occurs
     */
    private void updateBody(Set<Integer> messageIdSet) throws Exception
    {
        if (messageIdSet == null || messageIdSet.isEmpty())
        {
            return;
        }
        
        AwakeConnection awakeConnection = (AwakeConnection)connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
        
        KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.buildFromAwakeConnection(awakeConnection);
        ApiMessages apiMessages = new ApiMessages(kawanHttpClient, awakeFileSession.getUsername(),
                awakeFileSession.getAuthenticationToken());
        
        for (Integer messageId : messageIdSet) {
                        
            MessageDTO messageDTO = apiMessages.getMessage(messageId);
                        
            String body = messageDTO.getBody();
            MessageDecryptor messageDecryptor = new MessageDecryptor(userNumber, passphrase, connection);
            boolean integrityCheck = true;
            try
            {
                body = messageDecryptor.decrypt(body);
                integrityCheck = messageDecryptor.isIntegrityCheckValid();
            }
            catch (Exception e)
            {
                System.err.println(body + ": bodys id. FAIL TO DECRYPT BODY: " + e);
            }
            
            List<AttachmentInfoDTO> attachmentInfoDTOList = messageDTO.getAttachments();
            List<AttachmentLocal> attachmentLocalList = getAttachmentLocalList(attachmentInfoDTOList);
            
            MessageLocal messageLocal = messageLocalStore.get(messageId);
            messageLocal.setBody(body);
            messageLocal.setAttachmentLocal(attachmentLocalList);
            messageLocal.setIntegrityCheck(integrityCheck);
            messageLocal.setUpdateComplete(true);
            messageLocalStore.put(messageId, messageLocal);
            
        }
    }
    
    private List<AttachmentLocal> getAttachmentLocalList(List<AttachmentInfoDTO> attachmentInfoDTOList) {
        
        List<AttachmentLocal> attachmentLocalList = new ArrayList<>();
        
        if (attachmentInfoDTOList == null || attachmentInfoDTOList.isEmpty()) {
            return attachmentLocalList;
        }
        
        for (AttachmentInfoDTO attachmentInfoDTO : attachmentInfoDTOList) {
            AttachmentLocal attachmentLocal = new AttachmentLocal();
            attachmentLocal.setAttachPosition(attachmentInfoDTO.getAttachPosition());

            attachmentLocal.setFileSize(attachmentInfoDTO.getSize());
            
            // Remote file name not set: not used anymore in futur usage
            //attachmentLocal.setFileName(attachmentInfoDTO.getFilename());
            attachmentLocal.setFileName(HtmlConverter.fromHtml(attachmentInfoDTO.getRemoteFilename()));
            
            attachmentLocalList.add(attachmentLocal);
        }
        
        return attachmentLocalList;
    }
        

    /**
     * @return the exception
     */
    public Exception getException()
    {
        return exception;
    }



}
