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
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;


/**
 * KeyHandler 14 juin 2005 13:27:54
 * 
 * Interface for PGP key management abstration layer:
 * <br> - Key Pair generation
 * <br> - Key storage
 * <br> - Key export 
 * <br>
 * Implementation on local host and remote server will use these same APIs.
 * 
 */
public interface KeyHandler
{
	
	      
    /**
     * Generate a PGP Key Pair and store it 
     * 
    * @param userId            PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
    * @param passphrase        private Key passphrase
    * @param algoAsym          public key algorithm
    * @param keyLengthAsym     Asymmetric key length
    * @param keyLengthSym      Symmetric key length
    * @param seed              seed for initialization
    * @param expirationDate    expiration date of key (null if none)
    * 
    * @throws IOException              if an I/O Exception occurs
    * @throws IllegalArgumentException if the key is invalid (not PGP type)
    * @throws KeyException             if any other error occurs     
    * @throws NoSuchProviderException  if the provider is not set correctly
    * @throws NoSuchAlgorithmException if the algorithm is not set correctly
    * @throws InvalidAlgorithmParameterException        if the Elgamal parameter specs ar invalid
    */
   
   public void generateAndStoreKeyPair(String userId, 
                                       char[] passphrase, 
                                       String algoAsym, 
                                       int keyLengthAsym, 
                                       String algoSym, 
                                       int keyLengthSym, 
                                       byte[] seed,
                                       Date expirationDate)
   throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
   IllegalArgumentException, IOException, KeyException; 
   
     
   /**
    * Generate and add a PGP key Pair to an existing keyring
    * 
    * @param userId            PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
    * @param passphrase        private Key passphrase
    * @param algoAsym          public key algorithm
    * @param keyLengthAsym     Asymmetric key length
    * @param keyLengthSym      Symmetric key length
    * @param seed              seed for initialization
    * @param expirationDate    expiration date of key (null if none)
    * 
    * @throws IOException                               if an I/O Exception occurs
    * @throws IllegalArgumentException                  if the key is invalid (not PGP type)
    * @throws KeyException                              if any other error occurs     
    * @throws NoSuchProviderException                   if the provider is not set correctly
    * @throws NoSuchAlgorithmException                  if the algorithm is not set correctly
    * @throws InvalidAlgorithmParameterException        if the Elgamal parameter specs ar invalid
    * @throws PGPException 
    */
   public void generateAndAddKeyPair(  String userId, 
                                       char[] passphrase, 
                                       String algoAsym, 
                                       int keyLengthAsym, 
                                       String algoSym, 
                                       int keyLengthSym, 
                                       byte[] seed,
                                       Date expirationDate)
   throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
   IllegalArgumentException, IOException, KeyException, PGPException; 
   
    /**
     * Extract from the private key ring the owner as user Id.
     * 
     * @return the Private Key Ring owner as user Id
     * 
     * @throws IOException             		if a fileIn error occurs
     * @throws NoSuchAlgorithmException		if the provider is not set correctly
     * @throws KeyException					if any other error occurs
     */
    
    public String getPrivKeyRingOwner()
		throws IOException,
			   IllegalArgumentException, KeyException, NoSuchAlgorithmException;
    
    /**
     * 
     * @param userId           	PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * 
     * @return true if the corresponding key exists in the private key ring
     * 
     * @throws IOException              if an I/O Exception occurs
     * @throws IllegalArgumentException if the key id is invalid and/or the passphrase invalid
     * @throws KeyException             if any other error occurs 
     * @throws NoSuchAlgorithmException if the provider is not set correctly
     */
    public boolean existsInPrivKeyRing(String userId)
		throws IOException,
			   IllegalArgumentException, KeyException, NoSuchAlgorithmException;
    
    /**
     * 
     * @param userId           	PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * 
     * @return true if the corresponding key exists in the public key ring
     * 
     * @throws IOException              if an I/O Exception occurs
     * @throws IllegalArgumentException if the key id is invalid
     * @throws KeyException             if any other error occurs 
     * @throws NoSuchAlgorithmException if the provider is not set correctly
     */    
    public boolean existsInPubKeyRing(String userId)
		throws IOException,
			   IllegalArgumentException, KeyException, NoSuchAlgorithmException;    
	
    /**
     * Retrieves the PGP private key from a specified private keyring
     * 
     * @param userId            PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * @param privKeyring       The private keyring file
     * @param pubKey            the pgp public key associated with the given userid
     * @param passphrase        he passphrase that protect the private key
     * @return  the PGP private key  corresponding to the PGP key id
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the private key is not found
     * @throws IllegalArgumentException if the key id is invalid and/or the passphrase invalid
     * @throws KeyException             if any other error occurs 
     * @throws NoSuchAlgorithmException if the provider is not set correctly
     */
    
    public PrivateKey getPgpPrivateKey(String userId, File privKeyring, PublicKey pubKey, char [] passphrase)
        throws IOException, FileNotFoundException, IllegalArgumentException, KeyException, NoSuchAlgorithmException;  
    
    /**
     * Retrieves the PGP private key from the local default keyring
     * 
     * @param userId           	PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * @param pubKey          	the pgp public key associated with the given userid
     * @param passphrase      	he passphrase that protect the private key
     * @return  the PGP private key  corresponding to the PGP key id
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the private key is not found
     * @throws IllegalArgumentException if the key id is invalid and/or the passphrase invalid
     * @throws KeyException             if any other error occurs 
     * @throws NoSuchAlgorithmException if the provider is not set correctly
     */
    
    public PrivateKey getPgpPrivateKey(String userId, PublicKey pubKey, char [] passphrase)
        throws IOException, FileNotFoundException, IllegalArgumentException, KeyException, NoSuchAlgorithmException;  
    
    /**
     * Retrieves the PGP public key from a passed keyring in InputStream
     * 
     * @param in      The InputStream of the Keyring Collection
     * 
     * @return  the PGP public key  corresponding to the PGP key id
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the public key is not found
     * @throws IllegalArgumentException if the key id is invalid
     * @throws KeyException             if any other error occurs 
     * @throws NoSuchAlgorithmException if the provider is not set correctly
     */
    
    public PublicKey getPgpPublicKeyFromAsc(InputStream in)
    throws IOException, FileNotFoundException, 
          IllegalArgumentException, KeyException, NoSuchAlgorithmException ;
    
    /**
     * Retrieves the PGP public key for encryption from a passed keyring in InputStream
     * 
     * @param in        the public key block input stream
     * @return          the PGP Public Key
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the public key is not found
     * @throws IllegalArgumentException if the key id is invalid
     * @throws KeyException             if any other error occurs 
     * @throws NoSuchAlgorithmException if the provider is not set correctly
     */
    public PublicKey getPgpPublicKeyForEncryptionFromAsc(InputStream in)
    throws  IOException, FileNotFoundException, 
            IllegalArgumentException, KeyException, NoSuchAlgorithmException;
    
    
//    /**
//     * 
//     * @param in                    The InputStream of the Keyring Collection
//     * @param userId                The User ID of the key.
//     * 
//     * @return the Public Key corresponding to the User iD
//     * @throws IOException
//     * @throws FileNotFoundException
//     * @throws IllegalArgumentException
//     * @throws KeyException
//     * @throws NoSuchAlgorithmException
//     */
//    public PublicKey getPgpPublicKeyForEncrytpion(InputStream in, String userId)
//            throws IOException, FileNotFoundException, 
//                   IllegalArgumentException, KeyException, NoSuchAlgorithmException;
//    
    /**
     * Retrieves the PGP public key from the local keyring
     * 
     * @param userId  PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * @return  the PGP public key  corresponding to the PGP key id
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the public key is not found
     * @throws IllegalArgumentException if the key id is invalid
     * @throws KeyException             if any other error occurs 
     * @throws NoSuchAlgorithmException if the provider is not set correctly
     */
    
    public PublicKey getPgpPublicKey(String userId)
        throws IOException, FileNotFoundException, IllegalArgumentException, KeyException, NoSuchAlgorithmException;
    
    
    
    /**
     * Retrieves the PGP public key for encryption from the local keyring
     * <br>
     * @param userId  PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * 
     * @return  the PGP public key  corresponding to the PGP key id
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the public key is not found
     * @throws IllegalArgumentException if the key id is invalid
     * @throws KeyException             if any other error occurs 
     * @throws NoSuchAlgorithmException if the provider is not set correctly
     */
    
    public PublicKey getPgpPublicKeyForEncryption(String userId)
        throws IOException, FileNotFoundException, IllegalArgumentException, KeyException, NoSuchAlgorithmException;
     
    
    /**
     * 
     *  Create a List of Userids of the public key ring on this machine
     *  
     * @return the List of Userids of the public key ring on this machine
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the public key is not found
     * @throws IllegalArgumentException if the key id is invalid
     * @throws KeyException             if any other error occurs 
     * @throws NoSuchAlgorithmException if the provider is not set correctly
     */
    public List<String> getPublicKeyRingUserIds()
    throws 	IOException, FileNotFoundException, IllegalArgumentException, 
	   		KeyException, NoSuchAlgorithmException;
    
    /**
     * 
     *  Create a List of Userids of the public key ring on this machine
     *  Include only the keys that are still valid:
     *  <br> - Non Revoked.
     *  <br> - Non Expired.
     *  
     * @return the List of Userids of the public key ring on this machine
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the public key is not found
     * @throws IllegalArgumentException if the key id is invalid
     * @throws KeyException             if any other error occurs 
     * @throws NoSuchAlgorithmException if the provider is not set correctly
     */
    public List<String> getPublicKeyRingUserIdsStillValid()
    throws  IOException, FileNotFoundException, IllegalArgumentException, 
            KeyException, NoSuchAlgorithmException;    
    
    /**
     * Check if the passphrase match the private key associated with the user id
     * <br>
     * @param userId		User id of the private key
     * @param passphrase	the passphrase to check
     * @return				true if the passphrase is correct
     * 						fasle if the passphrase is incorrect
     * @throws IOException				if an I/O Exception occurs
     * @throws FileNotFoundException	if the private key is not found
     * @throws IllegalArgumentException	if userId or passphrase is null
     * @throws KeyException				if an other error occurs
     * @throws NoSuchAlgorithmException	if the provider is not set correctly
     * @throws IllegalAccessException   if the user has not the right to use Strong 2049/256+ cryptography
     */
    public boolean isPassphraseValid(String userId, char [] passphrase)
    	throws IOException, FileNotFoundException, IllegalArgumentException, 
    	    KeyException, NoSuchAlgorithmException, IllegalAccessException;
    
    
    /**
     * Retrieves the PGP private key from the local keyring in armor format as String.
     * <br>
     * @param userId           	PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * @return  the PGP private key  corresponding to the PGP key id in armor format
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the private key is not found
     * @throws IllegalArgumentException if the key id is invalid and/or the passphrase invalid
     * @throws KeyException             if any other error occurs 
     * @throws NoSuchAlgorithmException if the provider is not set correctly
     */
    
    public String exportAscPgpPrivateKey(String userId)
        throws IOException, FileNotFoundException, IllegalArgumentException, KeyException, NoSuchAlgorithmException;  
    
    /**
     * Retrieves the PGP public key from the local keyring in armor format as String
     * @param userId           	PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * @return  the PGP public key  corresponding to the PGP key id in armor format
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the public key is not found
     * @throws IllegalArgumentException if the key id is invalid
     * @throws KeyException             if any other error occurs 
     * @throws NoSuchAlgorithmException if the provider is not set correctly
     */
    
    public String exportAscPgpPublicKey(String userId)
        throws IOException, FileNotFoundException, IllegalArgumentException, KeyException, 
               NoSuchAlgorithmException;
     
    
    /**
     * Import and stores a PGP public key on disk from the armored public key block 
     * stored in a fileIn
     * <br>
     * if the asc file contains a private key, it will be imported too.
     * <br>
     * NOTE: the user Id is  "contained" in the public key block. See implementation
     * for details
     * @param filePubKeyAsc       the fileIn of the armored public key block 
     * @return TODO
     * @throws NoSuchAlgorithmException
     * @throws KeyException
     * @throws IOException
     */
    public boolean importPgpPublicAndPrivateKeyFromAsc(File filePubKeyAsc)
        throws NoSuchAlgorithmException, KeyException, IOException;
    
    /**
     * Import and stores a PGP public key on disk from the armored public key block
     * contained in a String
     * <br>
     * NOTE: the user Id is  "contained" in the public key block. See implementation
     * for details
     * @param sAscKey		the the armored public key blok
     * @throws NoSuchAlgorithmException
     * @throws KeyException
     * @throws IOException
     */
    public void importPgpPublicKeyFromAsc(String sAscKey)
    	throws NoSuchAlgorithmException, KeyException, IOException;
    	
    /**
     * Import and stores a PGP private key from the armored private key block
     * contained in as String
     * <br>
     * NOTE: the user Id is  "contained" in the private key block. See implementation
     * for details
     * @param ascKey		the the armored private key block
     * 
     * @throws NoSuchAlgorithmException
     * @throws KeyException
     * @throws IOException
     */
    public void importPgpPrivateKeyFromAsc(String ascKey)
        throws NoSuchAlgorithmException, KeyException, IOException;
    
    /**
     * Delete a public key from local keyRing
     * 
     * @param pubKeyId
     * @throws NoSuchAlgorithmException
     * @throws KeyException
     * @throws IOException
     */
    public int deletePubKeyFromKeyRing(String pubKeyId)
        throws NoSuchAlgorithmException, KeyException,KeyStoreException, IOException;
    
    /**
     * Retrieves from the PGP Public Key Ring a key User ID using
     * the PGP Key ID as the search parameter
     * 
     * @param keyId     the pGP Key ID in long format
     * @return the corresponding PGP User ID
     * 
     * @throws IOException
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     * @throws KeyException
     * @throws NoSuchAlgorithmException
     */
    public String getSubkeyUserId(long keyId)
    throws IOException, FileNotFoundException, 
        IllegalArgumentException, KeyException, NoSuchAlgorithmException;        
    
    /**
     * Change passphrase of private key of given user
     * @param userId                    Owner of the private key
     * @param old_passphrase            current passphrase
     * @param new_passphrase            new passphrase
     * 
     * @throws FileNotFoundException    
     * @throws IllegalArgumentException
     * @throws KeyException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws NoSuchProviderException
     * @throws PGPException
     */
    
    public void changePassphrase(String userId, char[] old_passphrase, char[] new_passphrase) 
    throws FileNotFoundException, IllegalArgumentException, 
           KeyException, NoSuchAlgorithmException, IOException, 
          NoSuchProviderException, PGPException;
          

    /**
     * List key ids & fingerprints of private keys of a file
     * @param keyFile   The file containing the keys.
     * @return A map containing 2 Strings 1) the id 2) the fingerprint
     */

    public Map<String, String> getSecretKeyIdsFromFile(File keyFile)
    throws FileNotFoundException, IOException, PGPException;
    

    /**
     * Store in cGeep keyring the keypair passed in parameters
     * @param privKeyFile				File containing private key to store
     * @param pubKeyFile				File containing public key to store
     * @param userId					Id of the key to store
     * @param add						boolean to tell if passed keyring is to add to existing keyring
     * 									or must replace any exiting keyring
     * @throws FileNotFoundException
     * @throws IOException
     * @throws PGPException 
     */
    
    public void storeKeyRing(File privKeyFile, File pubKeyFile, String userId, boolean add) 
    throws FileNotFoundException, IOException, PGPException;
    

        
    /**
     * List private key ids stored in keyring
     * @return A list containing all private key ids
     * @throws FileNotFoundException
     * @throws IOException
     * @throws KeyException
     */
    public List<String> listPrivKeyIds() throws FileNotFoundException,IOException, KeyException;
    
    /**
     * GEt the default priv key id of keyring
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws KeyException
     */
    public String getDefaultKey() throws FileNotFoundException , IOException, KeyException;
    
    
    /**
     * Delete a key pair of the keyring
     * @param keyId			Id of keypair to delete
     * @return				CmPgpCodes.KEY_REMOVED if keypair deleted
     * 						CmPgpCode.KEY_NOT_FOUND if keypair not found in keyring
     */
    public int deleteKeyPair(String keyId)
    throws IOException,  KeyException,NoSuchAlgorithmException, KeyStoreException;
    
    /**
     * 
     * Test if a key is revoked.
     * 
     * @param userId            the PGP Public key user id to check
     * @return  true if the public key is revoked
     * 
     * @throws FileNotFoundException
     * @throws KeyException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public boolean isPublicKeyRevoked(String userId)
        throws FileNotFoundException, KeyException, IOException, NoSuchAlgorithmException;     
    
    /**
     * 
     * Test if a key is expired.
     * 
     * @param userId            the PGP Public key user id to check
     * @return  true if the public key is expired
     * 
     * @throws FileNotFoundException
     * @throws KeyException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public boolean isPublicKeyExpired(String userId)
        throws FileNotFoundException, KeyException, IOException, NoSuchAlgorithmException;    
    
    /**
     * Return true if the key is valid (non revoked, non expired)
     * 
     * @param keyAsc       the key in asc format
     * @return true if the key is valid (non revoked, non expired) else false
     */
    public boolean isPublicKeyAsAscValid(String keyAsc)    
            throws IOException, FileNotFoundException, 
            IllegalArgumentException, KeyException, NoSuchAlgorithmException;    
    

    /**
     * Get PGPPrivate key from Stream
     * @param keyBloc		Asc key bloc
     * @return
     * @throws IOException
     * @throws KeyException
     */
    public PGPPrivateKey getPgpSecretKeyFromAsc(String keyBloc, String encryptedBody, char[] passphrase)
    	throws IOException, KeyException, PGPException;
    
    /**
     * Change passphrase of private key of given user
     * @param in                    	Stream containing private keyRing
     * @param out						Stream where to save updated keyring
     * @param old_passphrase            current passphrase
     * @param new_passphrase            new passphrase
     * 
     * @throws IOException
     * @throws PGPException
     * @throws NoSuchProviderException
     */
	public void changePassphrase(InputStream in, OutputStream os, 
			char[] old_passphrase, char[] new_passphrase) throws IOException,
			PGPException, NoSuchProviderException;
	
	public PrivateKey getPgpPrivateKey(InputStream in, String userId,
			char[] passphrase) throws IOException, PGPException;
    
}

// End
