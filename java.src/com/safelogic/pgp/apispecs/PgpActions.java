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
import java.io.UnsupportedEncodingException;
import java.security.KeyException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.List;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;

import com.safelogic.pgp.api.BooleanContainer;
import com.safelogic.pgp.api.PgeepPublicKey;


/**
 * This Interface defines abstract PGP actions.
 * <br>Purpose is to create a PGP layer that is independant from any Provider specific
 * features.
 * <br>
 * Notes:
 * <br> - Implementation will use a preferred PGP Provider. (There will be no factory to switch
 * from one Provier to another. Implementation will be "static" and hard coded).
 * <br> - Implementation *must* use KeyHandler for key management.
 */

public interface PgpActions
{    
    
    /**
     * Signal that the file operation is terminated 
     * (to be used in cGeepApi)
     */
    public void setMaximumProgress();
    
    /**
     * @param armorMode if true, the encrypted file will be PGP armored
     */
    public void setArmorMode(boolean armorMode);
    
    /**
     * @param filesSize the total files Size
     */
    public void setFilesLength(long filesLength);
    
    /**
     * @return the Last PGP Public Key that signed a verified message
     */
    public PgeepPublicKey getLastSignPublicKey();
    

    /**
     * Encrypt with PGP a fileIn for a list of public publicKeys
     * <br>Note: implementation *must* use KeyHandler.
     * 
     * @param in              the File to PGP encrypt
     * @param out             the encrypted File
     * @param publicKeysId    the list of PGP public publicKeys Id   
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if a public key is not found
     * @throws IllegalArgumentException if the key id list is invalid
     * @throws KeyException             if a key error occurs 
     * @throws Exception                if any other exception occurs, 
     *                                  must be defined because there are too many possible cases.
     */
    public void encryptPgp(File in, File out, List<String> publicKeysId)
        throws IOException, FileNotFoundException, IllegalArgumentException, 
               KeyException, Exception;
        
    /**
     * Decrypt with a PGP private key a fileIn encrypted
     * <br>Note: implementation *must* use KeyHandler. 
     * 
     * @param fileIn            the input fileIn to PGP decrypt
     * @param fileOut           the decrypted fileIn name
     * @param privKeyId         the private key id to decrypt the input stream
     * @param passphrase        the private key passprhase
     * 
     * @return  the PGP output decrypted stream
     * 
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the private/public key is not found
     * @throws IllegalArgumentException if a key id is invalid
     * @throws KeyException             if a key error occurs 
     * @throws Exception                if any other exception occurs, 
     *                                  must be defined because there are too many possible cases.
     */
    public int decryptPgp(File fileIn, File fileOut, String privKeyId, 
                                        char []passphrase)
        throws IOException, FileNotFoundException, IllegalArgumentException, 
               KeyException, Exception;         
    
    /**
     * Decrypt with a PGP private key a fileIn encrypted
     * <br>Note: implementation *must* use KeyHandler. 
     * 
     * @param fileIn            the input fileIn to PGP decrypt
     * @param fileOut           the decrypted fileIn name
     * @param keyBloc           the private key block in asc format
     * @param passphrase        the private key passprhase
     * 
     * @return  the PGP output decrypted stream
     * 
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the private/public key is not found
     * @throws IllegalArgumentException if a key id is invalid
     * @throws KeyException             if a key error occurs 
     * @throws Exception                if any other exception occurs, 
     *                                  must be defined because there are too many possible cases.
     */
    public int decryptPgpFromAscKey(File fileIn, File fileOut, String keyBloc, char []passphrase)
    throws IOException, FileNotFoundException, IllegalArgumentException, 
        KeyException, Exception;    
    
    /**
     * Sign with a PGP private key a fileIn. 
     * <br>Note: implementation *must* use KeyHandler.
     * 
     * @param fileIn                the input fileIn to PGP sign
     * @param fileOut               the output signed fileIn
     * @param privKeyId             the private key id 
     * @param passphrase            the private key passprhase
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the private key is not found
     * @throws IllegalArgumentException if the private key id is invalid
     * @throws KeyException             if a key error occurs 
     * @throws Exception                if any other exception occurs, 
     *                                  must be defined because there are too many possible cases.
     */    
    
    public void signPgp(File fileIn, File fileOut, String privKeyId, char []passphrase)
            throws IOException, FileNotFoundException, IllegalArgumentException, 
                    KeyException, Exception;
    
    
    /**
     * Verify a signed fileIn
     * The original name fileIn will be automatically retrieved
     * <br>Note: implementation *must* use KeyHandler.
     * 
     * @param in        the signed fileIn
     * @return  CmPgpCodes.SIGN_OK if signature successfully verified
     *          CmPgpCodes.SIGN_BAD otherwise
     * @throws IllegalArgumentException if input files are null
     * @throws KeyException             if an error occurs with key operation
     * @throws Exception                if any other error occurs
     */
    public int verifyPgp(File fileIn,  File fileOut)
        throws IllegalArgumentException, KeyException, Exception;
    
    
    /**
     * Sign and then encrypt with PGP a fileIn for a list of public publicKeys
     * <br>Note: implementation *must* use KeyHandler.
     * 
     * @param fileIn            the input fileIn to PGP encrypt/sign.
     * @param fileOut           the encrypted/signed output fileIn.
     * @param publicKeysId      the list of PGP public publicKeys Id   
     * @param privKeyId         the private key id 
     * @param passphrase        the private key passprhase
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if a public key is not found and/or the private key is not
     *                                  found
     * @throws IllegalArgumentException if a key (alone or in id list) is invalid
     * @throws KeyException             if a key error occurs 
     * @throws Exception                if any other exception occurs, 
     *                                  must be defined because there are too many possible cases.
     */
    public void signAndEncryptPgp(File fileIn, File fileOut, 
                                  List<String> publicKeysId, String privKeyId, char []passphrase)
        throws IOException, FileNotFoundException, IllegalArgumentException, 
               KeyException, Exception;    
            
    
    /**
     * Detach sign a stream
     * @param fileIn                    the input fileIn to PGP sign
     * @param fileOut                   the detached signature fileIn
     * @param privKeyId                 Signing key
     * @param passphrase                passphrase of signing key
     * @throws IOException              If an I/O Exception occurs
     * @throws FileNotFoundException    If private key not found
     * @throws IllegalArgumentException If a privKeyId is not valid
     * @throws KeyException             If a key error occurs
     * @throws Exception                must be defined because there are too many possible cases.
     */
    public void signDetachedPgp(File fileIn, File fileOut, 
                                String privKeyId, char[] passphrase)
        throws IOException, FileNotFoundException, IllegalArgumentException, KeyException, 
        Exception;    
    
    /**
     * Verify a detached signature when pubKey Id is unknown.
     * <br>Note: implementation *must* use KeyHandler.
     * 
     * @param in                the File to PGP verify.
     * @param inDetachedSign    the File of the detached signature
    * @return  SIGN_OK if the signature is right, else SIGN_BAD
     * 
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the public key is not found
     * @throws IllegalArgumentException if the key id is invalid
     * @throws KeyException             if a key error occurs 
     * @throws Exception                if any other exception occurs, 
     *                                  must be defined because there are too many possible cases.
   */
    public int verifyDetachedPgp(File in, File inDetachedSign)
    throws IOException, FileNotFoundException, IllegalArgumentException, 
           KeyException, Exception;
    
    /**
     * Verify a detached signature when pubKey Id is unknown.
     * <br>Note: implementation *must* use KeyHandler.
     * 
     * @param in                the String to PGP verify.
     * @param inDetachedSign    the String of the detached signature
    * @return  SIGN_OK if the signature is right, else SIGN_BAD
     * 
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the public key is not found
     * @throws IllegalArgumentException if the key id is invalid
     * @throws KeyException             if a key error occurs 
     * @throws Exception                if any other exception occurs, 
     *                                  must be defined because there are too many possible cases.
   */
    public int verifyDetachedPgp(String in, String inDetachedSign)
    throws IOException, FileNotFoundException, IllegalArgumentException, 
           KeyException, Exception;    
    
    /**
     * Verify a signed string when pubKey Id is unknown.
     * <br>Note: implementation *must* use KeyHandler.
     * 
     * @param str                       The signed string
     * @param booleanContainer          A boolean container that will contains the result 
     *                                  of verification :
     *                                  true if signature is correct false otherwise
     * @return                          The decoded string
     * @throws IllegalArgumentException If a requiered argument is null
     * @throws KeyException             If an error occurs during verification
     * @throws Exception                If any other error occurs
     */
    public String verifyPgp(String str, BooleanContainer booleanContainer)
        throws IllegalArgumentException, KeyException, Exception;
    
    /**
     * Verify a detached signature using a public keyring file
     * 
     * @param in                the String to PGP verify.
     * @param inDetachedSign    the String of the detached signature
     * @param pubKeyring        the public keyring to use for checking the key
     * @return  SIGN_OK if the signature is right, else SIGN_BAD
     * 
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the public key is not found
     * @throws IllegalArgumentException if the key id is invalid
     * @throws KeyException             if a key error occurs 
     * @throws Exception                if any other exception occurs, 
     *                                  must be defined because there are too many possible cases.
   */
    public int verifyDetachedPgp(String in, String inDetachedSign, File pubKeyring)
        throws IOException, FileNotFoundException, IllegalArgumentException, 
                KeyException, Exception;    
        
    /**
     * Verify a detached signature using a public keyring in asc format
     * 
     * @param in                the String to PGP verify.
     * @param inDetachedSign    the String of the detached signature
     * @param ascPubKey        the public keyring to use for checking the key in asc format
     * 
     * @return  SIGN_OK if the signature is right, else SIGN_BAD
     * 
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the public key is not found
     * @throws IllegalArgumentException if the key id is invalid
     * @throws KeyException             if a key error occurs 
     * @throws Exception                if any other exception occurs, 
     *                                  must be defined because there are too many possible cases.
   */
    
    public int verifyDetachedPgp(String strIn, String strDetachedSign, String ascPubKey) 
        throws IOException, FileNotFoundException, IllegalArgumentException,
               KeyException, Exception; 
    
    /**
     * Verify a signed string using a public keyring
     * 
     * @param str                       The signed string
     * @param pubKeyring        the public keyring to use for checking the key
     * @param booleanContainer          A boolean container that will contains the result 
     *                                  of verification :
     *                                  true if signature is correct false otherwise
     * @return                          The decode string
     * @throws IllegalArgumentException If a requiered argument is null
     * @throws KeyException             If an error occurs during verification
     * @throws Exception                If any other error occurs
     */
     public String verifyPgp(String str, File pubKeyring, BooleanContainer booleanContainer)
         throws IllegalArgumentException, KeyException, Exception;
    
     
     /**
      * @param str                      The input string to sign
      * @param privKeyring               The private keyring file
      * @param privKeyId                The private key id for signing
      * @param passphrase               The passphrase
      * @return                         A string representing the signature
      * @throws IOException              if an I/O Exception occurs
      * @throws FileNotFoundException    if the public key is not found
      * @throws IllegalArgumentException if the key id is invalid
      * @throws KeyException             if a key error occurs 
      * @throws Exception                if any other exception occurs, 
      *                                  must be defined because there are too many possible cases.
      */     
     public String signDetachedPgp(String str, File privKeyring, String privKeyId, char[] passphrase)
        throws IOException, FileNotFoundException, IllegalArgumentException, KeyException, 
        Exception;
     
    /**
     * @param str						The input string to sign
     * @param privKeyId					The private key id for signing
     * @param passphrase				The passphrase
     * @return							A string representing the signature
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the public key is not found
     * @throws IllegalArgumentException if the key id is invalid
     * @throws KeyException             if a key error occurs 
     * @throws Exception                if any other exception occurs, 
     *                                  must be defined because there are too many possible cases.
     */     
    public String signDetachedPgp(String str, String privKeyId, char[] passphrase)
    	throws IOException, FileNotFoundException, IllegalArgumentException, KeyException, 
    	Exception;

    
    /**
     * Encrypt a String
     * 
     * @param str		The string to encrypt
     * @param pubKeyIds The list of public publicKeys id to encrypt for
     * @return			The encrypted String 
     * @throws IllegalArgumentException	If a requiered argument is null
     * @throws KeyException				If an error occurs during encryption
     * @throws Exception				If any other error occurs
     */
    public String encryptPgp (String str, List<String> pubKeyIds)
    	throws IllegalArgumentException, KeyException, Exception;
  
    /**
     * Decrypt a String 
     * 
     * @param str						The encrypted string 
     * @param privKeyId					The private key to use
     * @param passphrase				The passphrase of the private key
     * @return							The decrypted String
     * @throws IllegalArgumentException	If a requiered argument is null	
     * @throws KeyException				If an error occurs during decryption
     * @throws Exception				If any other error occurs
     */
    public String decryptPgp(String str, String privKeyId, char [] passphrase)
    	throws IllegalArgumentException, KeyException, Exception;
    
    /**
     * Sign a string using a private keyring file
     * 
     * @param str                       The string to sign
     * @param privKeyring               The private keyring file
     * @param privKeyId                 The private key to use to sign
     * @param passphrase                The passphrase of the private key
     * @return                          The signed string (signature is enclosed to the string)
     * @throws IllegalArgumentException If a requiered argument is null
     * @throws KeyException             If an error occurs during signing
     * @throws Exception                If any other error occurs
     */
    public String signPgp(String str,  File privKeyring, String privKeyId, char[] passphrase)
        throws IllegalArgumentException, KeyException, Exception;
    
    /**
     * Sign a string using the default cGeep Keyring file
     * 
     * @param str						The string to sign
     * @param privKeyId					The private key to use to sign
     * @param passphrase				The passphrase of the private key
     * @return							The signed string (signature is enclosed to the string)
     * @throws IllegalArgumentException	If a requiered argument is null
     * @throws KeyException				If an error occurs during signing
     * @throws Exception				If any other error occurs
     */
    public String signPgp(String str, String privKeyId, char[] passphrase)
    	throws IllegalArgumentException, KeyException, Exception;
    
           
    
    /**
     * Wipe a fileIn.
     * 
     * @param fileIn              the fileIn to wipe.
     * @param securityLevel     the security level.
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the fileIn is not found
     * @throws NoSuchMethodException    throw if method is not implemented
     */
    
    public void wipe(File file, int securityLevel)
        throws IOException, FileNotFoundException, NoSuchMethodException;
    
    /**
     * Encrypt a string
     * @param str			The string to encrypt
     * @param keyStream		An inputstream containing the key to encrypt for
     */
    public String encryptPgp(String str, InputStream keyStream)
    	throws IllegalArgumentException, KeyException, Exception;

    /**
     * @return true if the String Signature is verified after a decryptString() operation
     */
    boolean isStringSignatureVerified();

    /**
     * Encrypt with PGP a fileIn for a list of public publicKeys
     * 
     * @param in              the File to PGP encrypt
     * @param out             the encrypted File
     * @param publicKeysId    the list of PGP public publicKeys
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if a public key is not found
     * @throws IllegalArgumentException if the key id list is invalid
     * @throws KeyException             if a key error occurs 
     * @throws Exception                if any other exception occurs, 
     *                                  must be defined because there are too many possible cases.
     */
    
    public void encryptFilePgp(File inFile, File outFile, List<PGPPublicKey> pgpPublicKeys) 
	throws NoSuchProviderException,	PGPException, FileNotFoundException, IOException, InterruptedException, IllegalArgumentException;
    
    /**
     * Encrypt a String
     * 
     * @param str		The string to encrypt
     * @param pubKeyIds The list of public publicKeys to encrypt for
     * @return			The encrypted String 
     * @throws IllegalArgumentException	If a requiered argument is null
     * @throws KeyException				If an error occurs during encryption
     * @throws Exception				If any other error occurs
     */
	public String encryptStringPgp(String inString,
			List<PGPPublicKey> publicKeys) throws NoSuchProviderException,
			PGPException, IOException, UnsupportedEncodingException, IllegalArgumentException;
	
	/**
	 * Decrypt a string
	 * @param strEncrypted   encrypted string
	 * @param pgpPrivKey	 the private key to use for decryption
	 * @param pgpPubKey		 signing public key (can be null)
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws PGPException
	 * @throws IOException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 * @throws Exception
	 */
	public String decryptStringPgp(String strEncrypted,
			PGPPrivateKey pgpPrivKey, PGPPublicKey  pgpPubKey)
			throws UnsupportedEncodingException, PGPException, IOException,
			NoSuchProviderException, SignatureException, Exception;
}

