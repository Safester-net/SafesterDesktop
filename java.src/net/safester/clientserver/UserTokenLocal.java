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

import net.safester.noobs.clientserver.specs.Local;

/**
 * 
 * @author Nicolas de Pomereu
 * Defines local instance of a UserToken holder
 */
public class UserTokenLocal implements Local
{
    /** The fields/columns of the local instance */
    private String hash_id      = null;
    private String user_email   = null;
    private String key_id       = null;
    private String user_name    = null;
    private String hash_passphrase = null;
    

    /**
     * Constructor
     */
    public UserTokenLocal()
    {
        
    }

    
    
    /**
     * @return the hash_id
     */
    public String getHashId()
    {
        return hash_id;
    }



    /**
     * @param hashId the hash_id to set
     */
    public void setHashId(String hashId)
    {
        hash_id = hashId;
    }



    /**
     * @return the user_email
     */
    public String getUserEmail()
    {
        return user_email;
    }



    /**
     * @param userEmail the user_email to set
     */
    public void setUserEmail(String userEmail)
    {
        user_email = userEmail;
    }



    /**
     * @return the key_id
     */
    public String getKeyId()
    {
        return key_id;
    }



    /**
     * @param keyId the key_id to set
     */
    public void setKeyId(String keyId)
    {
        key_id = keyId;
    }



    /**
     * @return the user_name
     */
    public String getUserName()
    {
        return user_name;
    }



    /**
     * @param userName the user_name to set
     */
    public void setUserName(String userName)
    {
        user_name = userName;
    }



    /**
     * @return the hash_passphrase
     */
    public String getHashPassphrase()
    {
        return hash_passphrase;
    }



    /**
     * @param hashPassphrase the hash_passphrase to set
     */
    public void setHashPassphrase(String hashPassphrase)
    {
        hash_passphrase = hashPassphrase;
    }



    @Override
    public String toString()
    {
        return "hashId= " + hash_id 
                + ", userEmail= " + user_email   
                + ", keyId= " + key_id      
                + ", userName= " + user_name   
                + ", hashPassphrasse= " + hash_passphrase;
    }
}
