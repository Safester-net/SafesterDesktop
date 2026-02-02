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
import java.io.InputStream;
import java.security.KeyException;

import org.bouncycastle.openpgp.PGPDataValidationException;
import org.bouncycastle.openpgp.PGPException;


/**
 * This Interface defines abstract PGP actions for symetric PBE (Passphrase Based Encryption)
 * <br>
 * Purpose is to create a PGP layer that is independant from any Provider specific features.
 * <br>
 * Notes:
 * <br>
 * - Implementation will use a preferred PGP Provider. (There will be no factory to switch
 * from one Provider to another. Implementation will be "static" and hard coded).
 */

public interface PgpSymActions
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
     * Encrypt a byte array using a PGP symetric encryption with a passphrase (PBE type)
     * 
     * @param byteArray     The byte array to encrypt
     * @param passphrase    The passphrase for symetric 
     * @return              The encrypted byte array
     * 
     * @throws IllegalArgumentException If a required argument is null
     * @throws KeyException             If an error occurs during encryption
     * @throws Exception                If any other error occurs
     */
    public byte[] encryptSymmetricPgp (byte [] byteArray, char [] passphrase)
        throws IllegalArgumentException, KeyException, Exception;
  
    /**
     * Decrypt a byte array using a PGP symetric encryption with a passphrase (PBE type)
     * 
     * @param byteArray                 The encrypted byte array
     * @param passphrase                The passphrase for symmetric encryption 
     * @return                          The decrypted byte array
     * 
     * @throws PGPDataValidationException   If passphrase is invalid
     * @throws PGPException                 If the passed byte array is not PGP encrypted
     * @throws Exception                    If any other error occurs
     */
    public byte[] decryptSymmetricPgp(byte[] byteArray, char [] passphrase)
        throws PGPDataValidationException, PGPException, Exception;
    
    /**
     * Encrypt a file with a symmetric algorithm using PBE.
     * 
     * @param fileIn            The file to encrypt
     * @param fileOut           The encrypted file
     * @param passphrase        The encryption passphrase
     * 
     * @throws IllegalArgumentException
     * @throws KeyException
     * @throws Exception
     */
    public void encryptFileSymmetricPgp(File fileIn, File fileOut, char[] passphrase)
        throws IllegalArgumentException, KeyException, Exception;
    
    /**
     * decrypt a file with a symmetric algorithm using PBE.
     * 
     * @param fileIn            The file to decrypt
     * @param fileOut           The decrypted out file
     * @param passphrase        The encryption passphrase
     * 
     * @throws PGPDataValidationException   If passphrase is invalid
     * @throws PGPException                 If the passed in file is not PGP encrypted
     * @throws Exception                    If any other error occurs
     */
    
    public void decryptFileSymmetricPgp(File fileIn, File fileOutr, char[] passphrase)
        throws PGPDataValidationException, PGPException, Exception;

    /**
     * decrypt a file with a symmetric algorithm using PBE and return an IntputStream 
     * 
     * @param fileIn            The file to decrypt
     * @param passphrase        The encryption passphrase
     * @return the decrrpted input stream
     * 
     * @throws PGPDataValidationException   If passphrase is invalid
     * @throws PGPException                 If the passed in file is not PGP encrypted
     * @throws Exception                    If any other error occurs
     */
        
    public InputStream  decryptFileSymmetricPgp(File fileIn, char[] passphrase)
	        throws PGPDataValidationException, PGPException, Exception;
	        
}



