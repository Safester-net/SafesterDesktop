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
package net.safester.clientserver.util;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;

import com.safelogic.pgp.api.KeyHandlerOne;
import com.safelogic.pgp.api.PubkeyDescriptorOne;
import com.safelogic.pgp.apispecs.KeyHandler;
import com.safelogic.pgp.apispecs.PubkeyDescriptor;

/**
 * @author Nicolas de Pomereu
 * 
 * Displays the detyail of a key info
 */
public class PgpKeyInfo
{
    /** The public key in Base64 string */
    private String publicKeyPgp = null;   
    
    /**
     * Displays the detail oa key info
     */
    public PgpKeyInfo(String publicKeyPgp)
    {
        this.publicKeyPgp = publicKeyPgp;
    }

    /**
     * Extract the key infos from the Pgp Public Key
     * @throws Exception
     */
    public PubkeyDescriptor getPubkeyDescriptor()
        throws Exception
    {
        KeyHandler kh = new KeyHandlerOne();
        ByteArrayInputStream in = new ByteArrayInputStream(publicKeyPgp.getBytes());
        
        //PublicKey pubKey = kh.getPgpPublicKeyFromAsc(in);
        PublicKey pubKey = kh.getPgpPublicKeyForEncryptionFromAsc(in);

        PubkeyDescriptorOne pubkeyDescriptor  = new PubkeyDescriptorOne(pubKey);
        return pubkeyDescriptor;      
    }
    
}
