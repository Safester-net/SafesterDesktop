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

import net.safester.noobs.clientserver.specs.Local;


public class PendingMessageUserLocal implements Local {

    /**
     * This class represent a message recipient which not registred yet
     */
    private int pending_user_id;
    private int message_id;
    private int type_recipient;
    private int is_draft;

    /** Add the email */
    private String email;
    

    public PendingMessageUserLocal(){
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public int getPending_user_id() {
        return pending_user_id;
    }

    public void setPending_user_id(int pending_user_id) {
        this.pending_user_id = pending_user_id;
    }

    public int getType_recipient() {
        return type_recipient;
    }

    public void setType_recipient(int type_recipient) {
        this.type_recipient = type_recipient;
    }


    public int getIs_draft() {
        return is_draft;
    }

    public void setIs_draft(int is_draft) {
        this.is_draft = is_draft;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    
}
