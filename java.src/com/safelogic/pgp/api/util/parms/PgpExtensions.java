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
package com.safelogic.pgp.api.util.parms;

import java.io.File;

import com.safelogic.pgp.util.UserPreferencesManager;

/**
 * @author Nicolas de Pomereu
 * 
 * Class to test the pgp extensions type, because of the mess :
 * <br> - OpenPGP Crypto extensions : .pgp .gpg, .pgeep, .cgeep.
 * <br> - cGeep signature extension : .pgeep_signature, .cgeep_signature
 */
public class PgpExtensions
{
    // Extension for cGeep encryption
    public static final String DEFAULT_CGEEP_ENCRYPTION_EXT     = ".pgp";
        
    // Extension for cGeep signatures
    public static final String CGEEP_SIGNATURE_EXT              = ".cgeep_signature";
    
    /** The array of Secret Keyring extension (common known usage) */
    public static final String[] SECRET_KEYRING_EXTENTIONS      = {".asc", ".gpg", ".skr" };
    
    /** The array of Public Keyring extension  (common known usage) */
    public static final String[] PUBLIC_KEYRING_EXTENTIONS      = {".asc", ".gpg", ".pkr", ".pubkr" };    
    
    /** The array of OpenPGP extensions (common known usage) */
    public static final String[] PGP_CRYPTO_EXTENSIONS = 
        {   
            // cGeep extensions
            ".cgeep", 
            ".cgeep_signature",
            ".pgeep",
            ".pgeep_signature",
            
            // Other OpenPGP softwares extensions : PGP, GnuPG, Hushmail, etc.
            ".pgp", // encrypted and/or signed file attached PGP
            ".gpg", // encrypted and/or signed file GnuPG           
            ".asc", // Encryption & Signature with Armor
            ".sig", // Signature detached PGP.           
        };
    
    public static final String[] PGP_ENCRYPTED_EXTENSIONS = 
    {
        ".cgeep",
        ".gpg",
        ".pgp"        
    };
    /**
     * @return the encryption extension to use for files
     */
    public static String getEncryptionExtension()
    {
        return UserPreferencesManager.getDefaultEncryptionExtension();
    }
    
    /**
     * @param file  the OpenPGP encrypted file
     * @return  true if the extension is one of a OpenPGP extension for encryption
     *          or signature
     */
    public static boolean isPgpEncryptedOrSignedExtension(String file)
    {
        return isPgpEncryptedOrSignedExtension(new File(file));
    }
    
    /**
     * @param file  the OpenPGP encrypted file
     * @return  true if the extension is one of a OpenPGP product: PGP, GpG, cGeep, cGeep,...
     */    
    public static boolean isPgpEncryptedOrSignedExtension(File file)
    {
        if (file == null)
        {
            throw new IllegalArgumentException("File is null!");
        }
        
        String fileName = file.getName();
        fileName = fileName.toLowerCase();
        
        for (int i = 0; i < PGP_CRYPTO_EXTENSIONS.length; i++)
        {
            if (fileName.endsWith(PGP_CRYPTO_EXTENSIONS[i]))
            {
                return true;
            }
        }
        
        return false;
    }
        
    public static boolean isPgpEncrypted(File file)
    {
        if(file == null)
        {
            throw new IllegalArgumentException("File is null!");
        }
         String fileName = file.getName();
        fileName = fileName.toLowerCase();
        
        for (int i = 0; i < PGP_ENCRYPTED_EXTENSIONS.length; i++)
        {
            if(fileName.endsWith(PGP_ENCRYPTED_EXTENSIONS[i]))
            {
                return true;
            }
        }
        return false;
    }
    
}
