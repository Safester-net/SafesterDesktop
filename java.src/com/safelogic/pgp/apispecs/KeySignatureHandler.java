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

import java.io.IOException;
import java.security.KeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.List;


/**
 * @author Nicolas de Pomereu
 *
 * Allows the sign public keys with a private key
 */
public interface KeySignatureHandler
{
        
    /**
     * Add a signature to a PGP Public key in the keyring
     * 
     * @param userId         The user Id of the PGP private key
     * @param passphrase     The passphrase of the PGP private key
     * @param userIdToSign   The user Id of the public key to sign
     * @param notationName   The notation name (futur usage)
     * @param notationValue  The notation value (futur usage)
     * @param isExportable   if true, the signature is exportable
     * 
     * @return true if the key has been signed, false if it was already signed by this key
     * 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws KeyException
     * @throws SignatureException
     * @throws KeyStoreException
     */
    public boolean signPgpPublicKey(String userId, 
                                char[] passphrase, 
                                String userIdToSign,
                                String notationName,
                                String notationValue, 
                                boolean isExportable)                          
        throws NoSuchAlgorithmException, NoSuchProviderException,
               IllegalArgumentException, IOException, KeyException, 
               SignatureException, KeyStoreException;        
    
    /**
     * Remove a signature from a PGP Public key in the keyring
     * 
     * @param userId         The user Id of the PGP private key
     * @param passphrase     The passphrase of the PGP private key
     * @param userIdToSign   The user Id of the public key to sign
     * 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws KeyException
     * @throws SignatureException
     * @throws KeyStoreException
     * 
     */
    public void removeSignatureFromPgpPublicKey(String userId, char[] passphrase, String userIdToSign)
        throws NoSuchAlgorithmException, NoSuchProviderException,
               IllegalArgumentException, IOException, KeyException, 
               SignatureException, KeyStoreException;     
        
    
    /**
     * 
     * Revoke a key.
     * 
     * @param userId            The user Id of the PGP private key
     * @param passphrase        The passphrase of the PGP private key
     * 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws KeyException
     * @throws SignatureException
     * @throws KeyStoreException
     */
    public void revokeKey(String userId, char[] passphrase)
    throws  NoSuchAlgorithmException, NoSuchProviderException,
            IllegalArgumentException, IOException, KeyException,
            SignatureException, KeyStoreException;
            
    
    /**
     * 
     * @param userId    the user if of the Signed Key
     * @return          the List of keys that have signed this key
     * 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws KeyException
     * @throws SignatureException
     * @throws KeyStoreException
     * 
     */
    public List<String> getUserIdsOfSigningKeys(String userId)
        throws NoSuchAlgorithmException, NoSuchProviderException,
               IllegalArgumentException, IOException, KeyException, 
               SignatureException, KeyStoreException;       
    
    /**
     * 
     * @param userIdToSign    the User id of the key to sign
     * @param signerUserId    the User id of the signer key
     *        
     * @return true if the key is already signed
     * 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws KeyException
     * @throws SignatureException
     * @throws KeyStoreException
     * 
     */
    public boolean isKeyAlreadySignedBySigner(String userIdToSign, String signerUserId)
        throws NoSuchAlgorithmException, NoSuchProviderException,
        IllegalArgumentException, IOException, KeyException, 
        SignatureException, KeyStoreException;        
    /**
     * 
     * @param userId    the user if of the Signed Key
     * @return          the List of descriptions of  the signing keys
     * 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws KeyException
     * @throws SignatureException
     * @throws KeyStoreException
     * 
     */
    public List<String> getDescriptionOfSigningKeys(String userId)
        throws NoSuchAlgorithmException, NoSuchProviderException,
               IllegalArgumentException, IOException, KeyException, 
               SignatureException, KeyStoreException;    
    

    
}
