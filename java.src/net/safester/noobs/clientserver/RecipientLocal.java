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


import org.apache.commons.lang3.StringEscapeUtils;

import net.safester.noobs.clientserver.specs.Local;

/**
 * @author Nicolas de Pomereu
 * Defines local instance of a Recipient.
 * <br>
 * <br>
 * Warning:
 * <br>the RecipientLocal instance does *not* contain the Message Id, because
 * the info would be useless or dangerous (the contain class MessageLocal already contains it for get() operation,
 * and it's useless for put operation (created by the server just before insert).
 *  
 */
public class RecipientLocal implements Local
{

    /** Table columns */    
    private int recipient_position;
    private int type_recipient;
    private int user_number;
    private String name_recipient;  
    
    /** Add the email */
    private String email;

    /**
    *
    * Converts special HTML values of characters to their original values.
    * <br>Example : "&eacute;"is converted to "é"
    * <p>
    * @param  string A String to convert from HTML to original
    * <p>
    * @return A String of char converted to original values
    *
    */

    public static String fromHtml(String string)
    {
        return StringEscapeUtils.unescapeHtml4(string);
    }

    /**
    * Converts special characters to their HTML values.
    * <br>Example : "à" is converted to "&eacute;"
    * <p>
    * @param  string A String to convert from original to HTML
    * <p>
    * @return A String of char converted to HTML equivalent.
    *
    */

    public static String toHtml(String string)
    {
        return StringEscapeUtils.escapeHtml4(string);
    }
    

    /** Return field value */
    public int getRecipientPosition()
    {
        return this.recipient_position;
    }
    /** Return field value */
    public int getTypeRecipient()
    {
        return this.type_recipient;
    }
    /** Return field value */
    public int getUserNumber()
    {
        return this.user_number;
    }
    /** Return field value */
    public String getNameRecipient()
    {
        //return this.name_recipient;
        
        if (this.name_recipient == null)
        {
            return this.name_recipient;
        }
        else
        {
            return fromHtml(this.name_recipient);
        }
    }

    /** Set field value */
    public void setRecipientPosition(int recipient_position)
    {
        this.recipient_position = recipient_position;
    } 
    /** Set field value */
    public void setTypeRecipient(int type_recipient)
    {
        this.type_recipient = type_recipient;
    } 
    /** Set field value */
    public void setUserNumber(int user_number)
    {
        this.user_number = user_number;
    } 
    /** Set field value */
    public void setNameRecipient(String name_recipient)
    {        
        //this.name_recipient = name_recipient;
        if (name_recipient == null)
        {
            this.name_recipient = name_recipient;
        }
        else
        {
            this.name_recipient =  toHtml(name_recipient);
        }
        
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
    
    
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((email == null) ? 0 : email.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	RecipientLocal other = (RecipientLocal) obj;
	if (email == null) {
	    if (other.email != null)
		return false;
	} else if (!email.equals(other.email))
	    return false;
	return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "RecipientLocal [email=" + email + ", name_recipient="
                + name_recipient + ", recipient_position=" + recipient_position
                + ", type_recipient=" + type_recipient + ", user_number="
                + user_number + "]";
    }

    

    
}

