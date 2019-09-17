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
package net.safester.noobs.clientserver;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import net.safester.noobs.clientserver.specs.Local;


/**
 * 
 * @author Nicolas de Pomereu
 * Defines local instance of a Message
 */
public class MessageLocal implements Local, Serializable
{
    
    /**
     * Cleaner for call()
     */
    private static final long serialVersionUID = 4002110486464245185L;
    
    private int message_id = -1;
    private int sender_user_number;
    private int folder_id;
    private String priority;
    private boolean is_read;
    private boolean is_with_attachment;
    private boolean is_encrypted;
    private boolean is_signed;
    
    //private Timestamp date_message;
    private long date_message = 0;

    private String subject;
    private String reply_to;
    private long size_message;
    private String body;  
    private boolean printable;
    private boolean fowardable;
    
    private boolean anonymousNotification; 

    private boolean integrityCheck = true;
    
    /** Supplementary comfort info (get only) */
    private String senderUserName;
    private String senderUserEmail;
    
    /** The Recipients for the Message */
    private List<RecipientLocal> recipientLocal;
    
    /** The Attachments for this Message */
    private List<AttachmentLocal> attachmentLocal;
    
    /** The Pending Users for this message */
    private List<PendingMessageUserLocal> pendingMessageUserLocal;    


    /** VERY important boolean that says if the message
     * is now complete (body decrypted) */
    private boolean updateComplete = false;

    /**
     * Constructor
     */
    public MessageLocal()
    {

    }

    /** Return field value */
    public int getSenderUserNumber()
    {
        return this.sender_user_number;
    }
        
    /** Return field value */
    public int getMessageId()
    {
        return this.message_id;
    }
        
    /** Return field value */
    public int getFolderId()
    {
        return this.folder_id;
    }
        
    /** Return field value */
    public String getPriority()
    {
        return this.priority;
    }
        
    /** Return field value */
    public boolean getIsRead()
    {
        return this.is_read;
    }
        
    /** Return field value */
    public boolean getIsWithAttachment()
    {
        return this.is_with_attachment;
    }
        
    /** Return field value */
    public boolean getIsEncrypted()
    {
        return this.is_encrypted;
    }
        
    /** Return field value */
    public boolean getIsSigned()
    {
        return this.is_signed;
    }
        
    /** Return field value */
    public Timestamp getDateMessage()
    {
        return new Timestamp(date_message);
    }
        
    /** Return field value */
    public String getSubject()
    {
        return this.subject;
    }
        
    /** Return field value */
    public String getReplyTo()
    {
        return this.reply_to;
    }
        
    /** Return field value */
    public long getSizeMessage()
    {
        return this.size_message;
    }
                
    /** Return field value */
    public String getBody()
    {
        return this.body;
    }
        
    

    /**
     * @return the senderUserName
     */
    public String getSenderUserName()
    {
        return senderUserName;
    }

    /**
     * @return the senderUserEmail
     */
    public String getSenderUserEmail()
    {
        return senderUserEmail;
    }

    /** Set field value */
    public void setSenderUserNumber(int user_number)
    {
        this.sender_user_number = user_number;
    }
        
    /** Set field value */
    public void setMessageId(int message_id)
    {
        this.message_id = message_id;
    }
        
    /** Set field value */
    public void setFolderId(int folder_id)
    {
        this.folder_id = folder_id;
    }
        
    /** Set field value */
    public void setPriority(String priority)
    {
        this.priority = priority;
    }
        
    /** Set field value */
    public void setIsRead(boolean is_read)
    {
        this.is_read = is_read;
    }
        
    /** Set field value */
    public void setIsWithAttachment(boolean is_with_attachment)
    {
        this.is_with_attachment = is_with_attachment;
    }
        
    /** Set field value */
    public void setIsEncrypted(boolean is_encrypted)
    {
        this.is_encrypted = is_encrypted;
    }
        
    /** Set field value */
    public void setIsSigned(boolean is_signed)
    {
        this.is_signed = is_signed;
    }
        
    /** Set field value */
    public void setDateMessage(Timestamp date_message)
    {
        //this.date_message = date_message;
        this.date_message = date_message.getTime();
    }
        
    /** Set field value */
    public void setSubject(String subject)
    {
        this.subject = subject;
    }
        
    /** Set field value */
    public void setReplyTo(String reply_to)
    {
        this.reply_to = reply_to;
    }
        
    /** Set field value */
    public void setSizeMessage(long size_message)
    {
        this.size_message = size_message;
    }
        
    /** Set field value */
    public void setBody(String body)
    {
        this.body = body;
    }

    /**
     * @return the recipientLocal
     */
    public List<RecipientLocal> getRecipientLocal()
    {
        return recipientLocal;
    }

    /**
     * @param recipientLocal the recipientLocal to set
     */
    public void setRecipientLocal(List<RecipientLocal> recipientLocal)
    {
        this.recipientLocal = recipientLocal;
    }

    /**
     * @return the attachmentLocal
     */
    public List<AttachmentLocal> getAttachmentLocal()
    {
        if (attachmentLocal == null)
        {
            attachmentLocal = new Vector<AttachmentLocal>();
        }
        return attachmentLocal;
    }

    /**
     * @param attachmentLocal the attachmentLocal to set
     */
    public void setAttachmentLocal(List<AttachmentLocal> attachmentLocal)
    {
        this.attachmentLocal = attachmentLocal;
    }
    
    /**
     * @param senderUserName the senderUserName to set
     */
    public void setSenderUserName(String senderUserName)
    {
        this.senderUserName = senderUserName;
    }

    /**
     * @param senderUserEmail the senderUserEmail to set
     */
    public void setSenderUserEmail(String senderUserEmail)
    {
        this.senderUserEmail = senderUserEmail;
    }

    public boolean isPrintable() {
		return printable;
	}

	public void setPrintable(boolean printable) {
		this.printable = printable;
	}

	public boolean isFowardable() {
		return fowardable;
	}

	public void setFowardable(boolean fowardable) {
		this.fowardable = fowardable;
	}
		

    /**
     * @return the anonymousNotification
     */
    public boolean isAnonymousNotification()
    {
        return this.anonymousNotification;
    }

    /**
     * @param anonymousNotification the anonymousNotification to set
     */
    public void setAnonymousNotification(boolean anonymousNotification)
    {
        this.anonymousNotification = anonymousNotification;
    }

    public List<String>  getPendingRecipients(){
        List<String> pendingRecipients = new Vector<String>();
        
        return pendingRecipients;
    }
    
    
//    /**
//     * @return the pendingMessageUserLocal
//     */
//    public List<PendingMessageUserLocal> getPendingMessageUserLocal()
//    {
//        //Never return null
//        if(pendingMessageUserLocal == null){
//            pendingMessageUserLocal = new ArrayList<PendingMessageUserLocal>();
//        }
//        return pendingMessageUserLocal;
//    }
//
//    /**
//     * @param pendingMessageUserLocal the pendingMessageUserLocal to set
//     */
//    public void setPendingMessageUserLocal(
//            List<PendingMessageUserLocal> pendingMessageUserLocal)
//    {
//        this.pendingMessageUserLocal = pendingMessageUserLocal;
//    }

    public boolean isIntegrityCheck() {
        return integrityCheck;
    }

    public void setIntegrityCheck(boolean integrityCheck) {
        this.integrityCheck = integrityCheck;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "MessageLocal [attachmentLocal=" + attachmentLocal + ", body="
                + body + ", date_message=" + date_message + ", folder_id="
                + folder_id + ", fowardable=" + fowardable + ", is_encrypted="
                + is_encrypted + ", is_read=" + is_read + ", is_signed="
                + is_signed + ", is_with_attachment=" + is_with_attachment
                + ", message_id=" + message_id + ", pendingMessageUserLocal="
                + pendingMessageUserLocal + ", printable=" + printable
                + ", priority=" + priority + ", recipientLocal="
                + recipientLocal + ", reply_to=" + reply_to
                + ", senderUserEmail=" + senderUserEmail + ", senderUserName="
                + senderUserName + ", sender_user_number=" + sender_user_number
                + ", size_message=" + size_message + ", subject=" + subject
                + "]";
    }

    /**
     * @return  the size in bytes of this instance
     */
    public int sizeStorage()
    {
        return toString().length();
    }
    
    /**
     * @return the updateComplete
     */
    public boolean isUpdateComplete() {
        return updateComplete;
    }

    /**
     * @param updateComplete the updateComplete to set
     */
    public void setUpdateComplete(boolean updateComplete) {
        this.updateComplete = updateComplete;
    }
          
}

