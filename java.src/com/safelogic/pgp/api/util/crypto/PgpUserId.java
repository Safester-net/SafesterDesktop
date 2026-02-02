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
package com.safelogic.pgp.api.util.crypto;

import java.io.File;

import com.safelogic.pgp.api.KeyHandlerOne;
import com.safelogic.pgp.apispecs.KeyHandler;
import com.safelogic.pgp.util.FileUtil;
import com.safelogic.pgp.util.UrlUtil;
import com.safelogic.utilx.syntax.EmailChecker;

/**
 * Wrapper class for a PGP User Id - 
 * <br>- Checks a User Id syntax.
 * <br>- Extract the Key Id.
 * <br>- Generates the full path of the Public Key filename & Private Key Filename.
 * <br><br>
 * As defined by PGP Corp, a PGP User Id is:
 * <br>"A text phrase that identifies a key pair. 
 * For example, one common format for a user ID is the owner's name and email address. 
 * The user ID helps users (both the owner and colleagues) identify the owner of the key pair".
 * <br>
 * As defined by PGP Corp, a PGP Key Id is:
 * <br>"A legible code that uniquely identifies a key pair. Two key pairs may have the same user ID, 
 * but they will have different Key IDs."
 * <br><br>
 * In pgeep:
 * <br> - a User Id is defined by a String as: "Alexandre BECQUEREAU <abecquereau@safelogic.com>".
 * <br> - a Key Id is defined by the e-mail part *alone*: "abecquereau@safelogic.com"
 * <br> - public  publicKeys are stored in user.home/pgeep/pubring.pkr
 * <br> - private publicKeys are stored in user.home/pgeep/secring.skr
 * This class checks insures:
 * <br> - That a pgeep User Id is in the form "Name <email>".
 * <br> - That the derived key id is an email.
 * <br> - That a Pgeep Private KeyRing exists.
 * <br> - That a Pgeep Public  KeyRing exists.
 * 
 * @author Nicolas de Pomereu
 *
 */

public class PgpUserId
{
    /** the User Id */
    private String m_userId = null;
    
    /** User Name */
    private String m_userName = null;
    
    /** Key Id */
    private String m_keyId = null;

    
    /**
     * Constructor
     * Checks that an User Id is the form Name <email>
     * @throws IllegalArgumentException if the User id is not in correct format.
     */
    public PgpUserId(String userId)
    {
        if (userId == null)
        {
            throw new IllegalArgumentException("userId is null!");
        }
        
        // if there are no <> ==> It's a not formated String
        if (userId.indexOf("<") == -1 || 
                userId.indexOf(">") == -1 || 
                userId.indexOf("<") >= userId.indexOf(">") -1 ||
                userId.indexOf("@") == -1)
        {
            m_keyId = userId;
            m_userId = userId;
            m_userName = userId;
            m_userName = m_userName.trim();
        }
        else
        {            
            m_keyId = userId.substring(userId.indexOf("<")+1, userId.indexOf(">"));
            
            if (m_keyId == null)
            {
                throw new IllegalArgumentException("Invalid User Id format. \"<email>\" not found: "
                        + userId);
            }       
            
            if( m_keyId.indexOf("@")!=-1 ) // Always true
            {
                EmailChecker emailChecker = new EmailChecker(m_keyId);
            
                if (! emailChecker.isSyntaxValid())
                {
                    throw new IllegalArgumentException("Invalid email string in User Id: " + m_keyId);
                }
            }
            
            // Good!
            m_userId = userId;
            m_userName = userId.substring(0, userId.indexOf("<"));
            m_userName = m_userName.trim();
        }
               
                
        String keyDirectory = UrlUtil.getKeyDirectory();
        
        // Create the base directory if it not 
        File fBaseDir = new File(keyDirectory);
        
        FileUtil.createDirectory(fBaseDir);
        
    }

    public PgpUserId(String userId, boolean isFromServer)
    {

        if(userId == null)
            throw new IllegalArgumentException("userId is null!");
        if(userId.indexOf("<") == -1 || userId.indexOf(">") == -1 || userId.indexOf("<") >= userId.indexOf(">") - 1 || userId.indexOf("@") == -1)
        {
            m_keyId = userId;
            m_userId = userId;
            m_userName = userId;
            m_userName = m_userName.trim();
        } else
        {
            m_keyId = userId.substring(userId.indexOf("<") + 1, userId.indexOf(">"));
            if(m_keyId == null)
                throw new IllegalArgumentException((new StringBuilder("Invalid User Id format. \"<email>\" not found: ")).append(userId).toString());
            if(m_keyId.indexOf("@") != -1)
            {
                EmailChecker emailChecker = new EmailChecker(m_keyId);
                if(!emailChecker.isSyntaxValid())
                    throw new IllegalArgumentException((new StringBuilder("Invalid email string in User Id: ")).append(m_keyId).toString());
            }
            m_userId = userId;
            m_userName = userId.substring(0, userId.indexOf("<"));
            m_userName = m_userName.trim();
        }
        if(!isFromServer)
        {
            String keyDirectory = UrlUtil.getKeyDirectory();
            File fBaseDir = new File(keyDirectory);
            FileUtil.createDirectory(fBaseDir);
        }
    }
    /**
     * @return Returns the keyId.
     */
    public String getKeyId()
    {
        return m_keyId;
    }

    /**
     * @return Returns the userName.
     */
    public String getUserName()
    {
        return m_userName;
    }

    /**
     * @return Returns the privKeyRingFilename.
     */
    public static String getPrivKeyRingFilename()
    {
        String sep = System.getProperty("file.separator");
        return UrlUtil.getKeyDirectory() + sep + UrlUtil.SECRING_SKR;
    }

    /**
     * @return Returns the pubKeyRingFilename.
     */
    public static String getPubKeyRingFilename()
    {       
        String sep = System.getProperty("file.separator");
        return UrlUtil.getKeyDirectory() + sep + UrlUtil.PUBRING_PKR;
    }
    
    /**
     * @return true if the Pgeep Public Key exists in the public keyring
     */
    public boolean existsPublicKey()
        throws Exception
    {
        KeyHandler kh = new KeyHandlerOne();
        
        boolean exists = false;
        
        exists =  kh.existsInPubKeyRing(m_userId);
        
        return exists;
    }
    
    /**
     * @return true if the Pgeep Private key exists in the private keyring
     */
    public boolean existsPrivateKey()
        throws Exception
    {
        KeyHandler kh = new KeyHandlerOne();
        
        boolean exists = false;
        
        exists =  kh.existsInPrivKeyRing(m_userId);
        
        return exists;
    }
    

}
