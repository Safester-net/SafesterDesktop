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
package net.safester.application.util.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import net.safester.application.parms.Parms;

import org.awakefw.commons.api.server.util.Sha1;
import org.awakefw.file.api.util.HtmlConverter;

import com.safelogic.utilx.ArrayMgr;

/**
 * Utility class fo passphrase management
 * 
 * @author Nicolas de Pomereu
 *
 */
public class PassphraseUtil
{
    
    /**
     * Get password for connection using hash
     * @param login         the login
     * @param passphrase    the passphrase
     *
     * @return  the password to use for connection
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static String computeHashAndSaltedPassphrase(String login, char []password)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        Sha1 sha1 = new Sha1();

        //Use old system to maintain compatibility
        String passphraseStr = new String(password);

        // Always convert passphrase to HTML so stah the getBytes() will produce
        // the same on all platforms: Windows, Mac OS X, Linux
        passphraseStr = HtmlConverter.toHtml(passphraseStr);
                
        passphraseStr = sha1.getHexHash(passphraseStr.getBytes());
        passphraseStr = passphraseStr.substring(0,20);
        passphraseStr = passphraseStr.toLowerCase();
        
        System.out.println("passphraseStr: " + passphraseStr);
        
        //Apply salt and hash iterations
        //public static String salt = "ThiS*IsSAlt4loGin$";
        //public static int PASSPHRASE_HASH_ITERATIONS = 3;
        
        String salt = login + Parms.salt;
        byte [] bPassphraseSaltCompute = ArrayMgr.AddByte(passphraseStr.getBytes(), salt.getBytes());
        String connectionPassword = "";
        for(int i = 0; i < Parms.PASSPHRASE_HASH_ITERATIONS; i++){
            connectionPassword = sha1.getHexHash(bPassphraseSaltCompute);
            bPassphraseSaltCompute = connectionPassword.getBytes();
        }
        
        connectionPassword = connectionPassword.substring(0, 20); // half of hash
        connectionPassword = connectionPassword.toLowerCase(); // All tests in lowercase

        return connectionPassword;
    }
    
}

