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
package com.safelogic.pgp.api;

import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;
import org.bouncycastle.bcpg.attr.ImageAttribute;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPUserAttributeSubpacketVector;

import com.safelogic.pgp.apispecs.PubkeyDescriptor;
import com.safelogic.utilx.Hex;


/**
 * Public Key Descriptor for for a Bouncy Castle formated PGP Key.
 * @author Nicolas de Pomereu
 *
 */

public class PubkeyDescriptorOne implements PubkeyDescriptor
{
    
    /** Crytpix PGP Public Key */
    private PGPPublicKey m_pgpKey = null;

    /**
     * Constructor
     * @param pubKey        an Cryptix OpenPGP Public Key
     * @throws IllegalArgumentException it pubKey is not a Cryptix OpenPGP Public Key
     */
    public PubkeyDescriptorOne(PublicKey pubKey)
    {
        /*
        if (! (pubKey instanceof PGPPublicKey))
        {
            throw new IllegalArgumentException("pubKey is not a Cryptix PGP Public Key: " 
                        + pubKey);
        }
        */
        PgeepPublicKey geepPublicKey = (PgeepPublicKey)pubKey;
        m_pgpKey = geepPublicKey.getKey();                      
    }
    
    
    private static String getAlgorithmName(
            int    algId)
        {
            switch (algId)
            {
            case PublicKeyAlgorithmTags.RSA_GENERAL:
                return "RSA";
            case PublicKeyAlgorithmTags.RSA_ENCRYPT:
                return "RSA/Crypt";
            case PublicKeyAlgorithmTags.RSA_SIGN:
                return "RSA/Sign";
            case PublicKeyAlgorithmTags.ELGAMAL_ENCRYPT:
                return "ElGamal/Crypt";
            case PublicKeyAlgorithmTags.DSA:
                return "DSA";
            case PublicKeyAlgorithmTags.EC:
                return "EC";
            case PublicKeyAlgorithmTags.ECDSA:
                return "ECDSA";
            case PublicKeyAlgorithmTags.ELGAMAL_GENERAL:
                return "ElGamal";
            case PublicKeyAlgorithmTags.DIFFIE_HELLMAN:
                return "Diffie Hellman";
            }

            return "unknown";
        }    

    /**
     * @return the PGP Key ID (*not* the pgeep keyId!)
     */
    public String getPgpId()
    {
        String fingerprint = getFingerprint();
        return "0x" + fingerprint.substring(32);
    }
    
    /**
     * @return the PGP Fingerprint
     */

    public String getFingerprint()
    {       
        byte [] hash = this.m_pgpKey.getFingerprint();
        String sHash = Hex.toString(hash);        
        return sHash;
    }
    
    /**
     * @return the PGP Key Type
     */
    public String getType()
    {
        return  getAlgorithmName(this.m_pgpKey.getAlgorithm());
    }

    /**
     * @return the PGP Key Length
     */
    public int getLength()
    {
        return m_pgpKey.getBitStrength();
    }

    /**
     * @return the PGP Key Creation Date
     */
    public Date getCreationDate()
    {
        return m_pgpKey.getCreationTime();
    }

    /**
     * @return the PGP Key Expiration Date (null if never expires)
     */
    public Date getExpirationDate()
    {       
        
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(getCreationDate().getTime());
        
        //int iday      = gc.get(Calendar.DATE);
        //int iMonth    = gc.get(Calendar.MONTH) + 1 ;
        //int iYear     = gc.get(Calendar.YEAR) ;
        
        int validDays = m_pgpKey.getValidDays();
        if (validDays <= 0) 
        {
            // Never expires
            return null;
        }
        
        gc.add(Calendar.DATE, validDays);
        Date expires = new Date(gc.getTimeInMillis());
        return expires;     
    }

    /**
     * return the symmetric algorithm of the corresponding private key (deprecated)
     */
    public String getSymetricAlgorithm()
    {        
        String symmetricAlgorithmName = "N/A";                       
        return symmetricAlgorithmName;       
    }

            
    /**
     * @return the Image of the Public PGP key - null if no image
     */
    public byte [] getImage()
    {
        Iterator it =  m_pgpKey.getUserAttributes();
        
        while (it.hasNext())
        {
            PGPUserAttributeSubpacketVector attributes = (PGPUserAttributeSubpacketVector)it.next();

            ImageAttribute imageAttribute = attributes.getImageAttribute();
            
            if (imageAttribute != null)
            {
                //System.out.println("ImageAttribute != null");
                byte [] imageInBytes = imageAttribute.getImageData();                
                return imageInBytes; 
            }
        }
        
        return null;
    }

    /**
     * @return true if the public key is expired
     */
    public boolean isExpired()
    {        
        Date dateExpiration = getExpirationDate();
        
        if (dateExpiration == null)
        {
            return false;
        }
        
        Date today = new Date();
        
        if (today.after(dateExpiration))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * @return true if the public key is revoked
     */
    public boolean isRevoked()
    {        
        return this.m_pgpKey.isRevoked();
    }
    
   

}

// End
