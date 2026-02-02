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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;



/**
 * KeyHandler 14 juin 2005 13:27:54
 * 
 * Interface for PGP key imagemanagement abstration layer:
 * <br> - Add Image.
 * <br> - Remove Image.
 * <br>
 * Implementation on local host and remote server will use these same APIs.
 * 
 */
public interface KeyImageHandler
{

    /**
     * Add an Image (Photo) to a PGP Key
     * 
     * @param userId         The user Id of the PGP private key
     * @param passphrase     The passphrase of the PGP private key
     * @param image          The JPEG image as byte array
     */
    public void addImageToPgpKey(String userId, char[] passphrase, byte[] image)
        throws NoSuchAlgorithmException, NoSuchProviderException,
        IllegalArgumentException, IOException, KeyException, SignatureException, KeyStoreException;        
    
    /**
     * Remove an Image from a PgpPublicKey
     * 
     * @param userId         The user Id of the PGP private key
     */
    public void removeImageFromPgpKey(String userId)
        throws NoSuchAlgorithmException, NoSuchProviderException,
        IllegalArgumentException, IOException, KeyException, SignatureException, KeyStoreException;     
    
    /**
     * Return the image as a byte array from any PGP Public key
     * inside the owner keyring collection.
     * 
     * @param userId         The user Id of the PGP public key
     * @return   the image as a byte array from any PGP Public key.
     *           null if the key has no image
     */
    public byte [] getImageFromPgpPublicKey(String userId)
        throws IOException, FileNotFoundException, 
        IllegalArgumentException, KeyException, NoSuchAlgorithmException;
    
    /**
     * Return the image as a byte array from any PGP Public key contained in
     * an InputStream.
     * 
     * @param in             The InputStream of the Keyring Collection
     * @return   the image as a byte array from any PGP Public key.
     *           null if the key has no image
     */
    public byte [] getImageFromPgpPublicKey(InputStream in)
        throws IOException, FileNotFoundException, 
               IllegalArgumentException, KeyException, NoSuchAlgorithmException; 
               
}

// End
