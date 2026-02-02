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

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Timestamp;
import java.util.List;

import com.safelogic.pgp.api.ServerSettingsContainer;


/**
 * Interface for PGP/pgeep key transfer between a local host and a remote host.
 * <br>
 * Methods never throw Exception, because implementation class may be used as a JNI
 * interface from C/C++ or VB (OCX).
 * <br>
 * After each method call:
 * <br> - completion status must be checked with isOperationOk()
 * <br> - error may be trapped as a short string with getErrorCode().
 * <br> - error may be displayed to user with getErrorLabel().
 * <br> - complete statck trace may be displayed with getStackTrace().
 * 
 */

public interface KeyTransfer
{    
        
    /**
     * @return Returns tuee if the opeation is Ok.
     */
    public boolean isOperationOk();

    /**
     * @return the Error Code as a generic string
     */
    public String getErrorCode();

    /**
     * Get a clean error label that may be displayed to the final user
     * @return the error label
     */
    public String getErrorLabel();

    /**
     * @return the Error Exception
     */
    public Exception getException();
    
    /**
     * @return the last Exception stack trace as a String.
     */
    public String getStackTrace();
    
   
//  /**
//  * Return true if the private key associated to the userId exists on cgeep.com server
//  * 
//  * @param userId PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
//  * 
//  * @return true if the private key associated to the userId exists on cgeep.com server
//  */
// public boolean existsRemotePrivKey(String userId);    
 
// /**
//  * Download the PGP private key from the remote host keyring in armor format as String.
//  * <br>
//  * The passphrase is needed for security reasons but the armor key will be of course
//  * encrypted.
//  * 
//  * @param userId           PGP UserId - User name and email : "Name Firstname <email@domaine.com>" 
//  */
// 
//  public void getRemoteAscPgpPrivateKey(String userId, char [] passphrase);  
 
 
//
///**
//* Upload the PGP private key from the local host keyring to the remote 
//* host keyring in armor format as String.
//* <br>
//* The passphrase is needed for security reasons but the armor key will be of course
//* encrypted.
//* @param userId           PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
//*/
//
//public void putRemoteAscPgpPrivateKey(String userId, char [] passphrase);    
//
    
    /**
     * Search all the keys that match the passed patern
     * @param searchText                The pattern to search
     * @param includePublicKeyBlock     if true, the List will include the Public Keys Blocks in ASC format
     * 
     * @return a List of keys with in the format pgpId + userId + public key block :
     *      -<br> 0xDF76253A,contact <contact@safelogic.com>,<public key block>
     *      -<br> 0xC2540EE4,nico2 <ndepomereu@safelogic.com>,<public key block>
     */
        
    public List<StringTriplet> getRemoteSearchedKey(String searchText, boolean includePublicKeyBlock);
    
    /**
     * Return true if the key associated to the userId exists on cgeep.com server
     * 
     * @param userId PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * 
     * @return true if the key associated to the userId exists on cgeep.com server
     */
    public boolean existsRemotePubKey(String userId);
    
       
    /**
     * Download the Fingerprint of the PGP public key from the remote host keyring.
     *  
     * @param userId           PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * @return
     */
    public String getRemotePgpFingerprint(String userId);
    
    /**
     * Download the PGP public key from the remote host keyring in armor format as String
     * and store it in the keyring.
     * 
     * @param userId           PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     */
    
    public void getRemoteAscPgpPublicKey(String userId);
     
    /**
     * Download the PGP public key from the remote host keyring in armor format as String and
     * return it
     * 
     * @param userId           PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     */
    
    public String getRemoteAscPgpPublicKeyAsAsc(String userId);    

    /**
     * Upload the PGP public key from the local keyring to the remote host 
     * keyring in armor format as String.
     * 
     * @param userId           PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * @param stealthMode      if true, user key will not be searchable on Central Directory
     * @param receiveInfos     if true, user will receive product infos
     * 
     * @return  the PGP public key  corresponding to the PGP key id in armor format
     */
    
    public void putRemoteAscPgpPublicKey(String userId, 
                                         boolean stealthMode, 
                                         boolean receiveInfos);
    
    /**
     * Upload the PGP public key from the local keyring to the remote host 
     * keyring in armor format as String.
     * 
     * The sha-1(email address) is signed to verify the true identity of the request 
     * 
     * @param userId                    PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * @param hashEmailDetachedSigned   The detached signature signature of the hashEmail
     */
    
    public void putRemoteAscSignedPgpPublicKey( String userId, 
                                                String hashEmailDetachedSigned); 
    
    /**
     * Sarch all the keys id created/changed on the specified date
     * @param date        The pattern to search
     * @return a List of keys id 
     */
    
    public List<String> getRemoteKeyByDate(String date);
    
    /**
     * Delete a public key on the server
     * @param keyId						id of the key to delete
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public void deleteKey(String keyId) 
	    throws NoSuchAlgorithmException, NoSuchProviderException;
    
    /**
     * Get the evalutation status for given keyId
     * @param keyId				The key id to look for
     * @param today				Current date (with all hh:mm:ss:mmmmm set to 0
     * @return					com.safelogic.pgp.parms.Parms.ASK_FOR_EVAL | EVAL_IN_PROGRESS | EVAL_COMPLETED
     */
    public String getEvalStatus(String keyId, Timestamp today);
    
    /**
     * Get the evalutation licence file in a String
     * @param keyId		The key id associated to the licence
     * @return			An String containing licence file content
     */
    public String getEvalLicence(String keyId);
    
    /**
     * Get the remote key Server Settings into a ServerSettingsContainer Object
     * 
     * @param userId        the User Id
     * @return a Server Settings Container with the boolean info : keep on central dir, stealth mode, receive info
     * 
     */
    public ServerSettingsContainer getRemotePrivacySettings(String userId);
    
    /**
     * Set the remote key Server Settings using a ServerSettingsContainer Object
     * 
     * @param userId                    PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * @param hashEmailDetachedSigned   The detached signature signature of the hashEmail
     * @param serverSettingsContainer   Server Settings Container with the boolean info : keep on central dir, stealth mode, receive info
     * 
     */
    public void setRemotePrivacySettings(String userId, 
                                         String hashEmailDetachedSigned, 
                                         ServerSettingsContainer serverSettingsContainer);   
    
    /**
     * Delete the remote Public Key. Sign it before to identify the requestor.
     * 
     * @param userId                    PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * @param hashEmailDetachedSigned   The detached signature signature of the hashEmail
     */
    
    public void deleteRemoteAscSignedPgpPublicKey(String userId, 
                                                  String hashEmailDetachedSigned); 

     
}

// End
