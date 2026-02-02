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
package com.safelogic.pgp.apispecs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.bouncycastle.openpgp.PGPException;

/**
 * ExternalPubRingHandler
 * 
 * Interface used to manage an external public key ring
 *  - Open it
 *  - List keys
 *  - Get a key
 * 
 * @author Alexandre BECQUEREAU
 *
 */

public interface ExternalPubRingHandler 
{
    
    /**
     * Return true if the file exists and is a public key ring file
     * 
     * @param file          the public key ring file
     * @return              true if the file exists and is a public key ring file
     */
    boolean isFilePublicKeyRing(File file);
    
    /**
     * Return true if the file exists and is a private key ring file
     * 
     * @param file          the private key ring file
     * @return              true if the file exists and is a private key ring file
     */
    boolean isFilePrivateKeyRing(File file);    
    
    /**
     * Opens the public keyring contained in the file
     * @param file                      file containing the public key ring
     * @throws IOException              if an I/O error occurs
     * @throws FileNotFoundException    if file is not found
     * @throws IllegalArgumentException 
     * @throws PGPException
     */
    public void openKeyRing(File file)
    throws IOException, FileNotFoundException, 
            IllegalArgumentException, KeyException;
        
    public List<String> getKeyUserIds(File file)
    throws  IOException, FileNotFoundException, IllegalArgumentException, 
            KeyException, NoSuchAlgorithmException;
    
    public String getKeyId(String userId)
    throws  IOException, FileNotFoundException, IllegalArgumentException, 
            KeyException, NoSuchAlgorithmException;
        
    public String getKeyAsc(String keyId)
    throws  IOException, FileNotFoundException, IllegalArgumentException, 
        KeyException, NoSuchAlgorithmException;
    
    
}
