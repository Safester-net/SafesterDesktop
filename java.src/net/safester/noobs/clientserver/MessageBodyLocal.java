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

import net.safester.noobs.clientserver.specs.Local;

/**
 * WARNING:Content is tranfered to HTTP File Server in Json format.
 * So we make the field names short for less traffic.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class MessageBodyLocal implements Local, Serializable {

    private static final long serialVersionUID = -2223486731249269793L;
    
    private int id = -1;
    private int usr = -1;
    private String body = null;
    private long dt = 0;
    
    /**
     * Consructor 
     */
    public MessageBodyLocal() {
	
    }

    /**
     * @return the message_id
     */
    public int getMessageId() {
        return id;
    }

    /**
     * @param message_id the message_id to set
     */
    public void setMessageId(int message_id) {
        this.id = message_id;
    }

    /**
     * @return the sender_user_number
     */
    public int getSenderUserNumber() {
        return usr;
    }

    /**
     * @param sender_user_number the sender_user_number to set
     */
    public void setSenderUserNumber(int sender_user_number) {
        this.usr = sender_user_number;
    }


    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /** Return field value */
    public Timestamp getDateMessage()
    {
        return new Timestamp(dt);
    }

    /** Set field value */
    public void setDateMessage(Timestamp date_message)
    {
        //this.date_message = date_message;
        this.dt = date_message.getTime();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "MessageBodyLocal [id=" + id + ", usr=" + usr + ", body=" + body + ", dt=" + dt + "]";
    }
    
    
}
