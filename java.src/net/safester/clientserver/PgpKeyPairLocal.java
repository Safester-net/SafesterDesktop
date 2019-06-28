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

import java.io.ByteArrayInputStream;
import java.util.Date;

import net.safester.clientserver.util.PgpKeyInfo;
import net.safester.noobs.clientserver.specs.Local;

import com.safelogic.pgp.api.KeyHandlerOne;
import com.safelogic.pgp.api.PgeepPublicKey;
import com.safelogic.pgp.apispecs.KeyHandler;
import com.safelogic.pgp.apispecs.PubkeyDescriptor;

/**
 * 
 * @author Nicolas de Pomereu
 * defines local instance of a PGP Key Pair holder
 */
public class PgpKeyPairLocal implements Local
{
    private static final String CR_LF = System.getProperty("line.separator");
    
    /** The private key in Base64 string */
    private String privateKeyPgpBlock = null;
    
    /** The public key in Base64 string */
    private String publicKeyPgpBlock = null;    
    
    
    /** The key detail infos */
    private String fingerprint  = null;
    private String pgpKeyId     = null;
    private String keyType      = null;
    private int keyLength       = 0;
    private Date dateCreate     = null;
    private Date dateExpire     = null; 
    
    
    /**
     * A PGP key pair holder
     * 
     * @param privateKeyPgpBlock     The private key in Base64 string 
     * @param publicKeyPgpBlock      The public key in Base64  string
     */
    public PgpKeyPairLocal(String privateKeyPgpBlock, String publicKeyPgpBlock)
    {
        if (privateKeyPgpBlock == null)
        {
            throw new IllegalArgumentException("privateKeyPgpBlock can\'t be null");
        }
        
        if (publicKeyPgpBlock == null)
        {
            throw new IllegalArgumentException("publicKeyPgpBlock can\'t be null");
        }
        
        this.privateKeyPgpBlock = privateKeyPgpBlock;
        this.publicKeyPgpBlock = publicKeyPgpBlock;          
        
        try
        {

            PgpKeyInfo pgpKeyInfo = new PgpKeyInfo(this.publicKeyPgpBlock);
            PubkeyDescriptor pubkeyDescriptor = pgpKeyInfo.getPubkeyDescriptor();
            
            fingerprint  = pubkeyDescriptor.getFingerprint();
            pgpKeyId     = pubkeyDescriptor.getPgpId();
            keyType      = pubkeyDescriptor.getType();
            keyLength    = pubkeyDescriptor.getLength();
            dateCreate   = pubkeyDescriptor.getCreationDate();
            dateExpire   = pubkeyDescriptor.getExpirationDate();
            
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("The Pgp Public Key is invalid : " + e.toString());
        }
    }
    
    // PgeepPublicKey pubKey  = (PgeepPublicKey) kh.getPgpPublicKeyForEncryption(publicKeyid); 
    
    public PgeepPublicKey getPgeepPublicKey()
        throws Exception
    {
        KeyHandler kh = new KeyHandlerOne();
        ByteArrayInputStream bis = new ByteArrayInputStream(publicKeyPgpBlock.getBytes());
        
        PgeepPublicKey pubKey = (PgeepPublicKey) kh.getPgpPublicKeyForEncryptionFromAsc(bis);
        return pubKey;
    }
    
    /**
     * @return the privateKeyPgpBlock
     */
    public String getPrivateKeyPgpBlock()
    {
        return privateKeyPgpBlock;
    }

    /**
     * @return the publicKeyPgpBlock
     */
    public String getPublicKeyPgpBlock()
    {
        return publicKeyPgpBlock;
    }
       
    /**
     * @return the fingerprint
     */
    public String getFingerprint()
    {
        return fingerprint;
    }

    /**
     * @return the pgpKeyId
     */
    public String getPgpKeyId()
    {
        return pgpKeyId;
    }

    /**
     * @return the keyType
     */
    public String getKeyType()
    {
        return keyType;
    }

    /**
     * @return the keyLength
     */
    public int getKeyLength()
    {
        return keyLength;
    }

    /**
     * @return the dateCreate
     */
    public Date getDateCreate()
    {
        return dateCreate;
    }

    /**
     * @return the dateExpire
     */
    public Date getDateExpire()
    {
        return dateExpire;
    }

    @Override
    public String toString()
    {
        return  pgpKeyId 
                    + ", " + fingerprint 
                    + ", " + keyType 
                    + ", " + keyLength 
                    + ", " + dateCreate 
                    + ", " + dateExpire 
                    + CR_LF
                    + privateKeyPgpBlock
                    + CR_LF 
                    + publicKeyPgpBlock;
    }
   
}
