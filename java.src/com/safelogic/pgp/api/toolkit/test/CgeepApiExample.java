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
package com.safelogic.pgp.api.toolkit.test;

import java.io.File;

import com.safelogic.pgp.api.toolkit.CgeepApi;

//CgeepApiExample.java
//Copyright (c) SafeLogic, 2000 - 2009
//
//Last Updates: 
// 19 oct. 2009 20:42:14 Nicolas de Pomereu

/**
 * @author Nicolas de Pomereu
 * 
 *         Example of cGeep API calls in Java
 */
public class CgeepApiExample
{
    /** We store the name of our temporary directory */
    private static final String TEMP_DIR = "c:\\temp";

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        // Init: create the c:\temp dir & clean previous instances of keyring
        File fileTempDir = new File(TEMP_DIR);
        fileTempDir.mkdir();
        new File(TEMP_DIR + "\\" + "secring.skr").delete();
        new File(TEMP_DIR + "\\" + "pubring.pkr").delete();        
        
        // Create cGeep API instance
        CgeepApi cgeepApi = new CgeepApi();

        // 1) Set the keyring in c:\temp
        cgeepApi.setKeyRingDirectory(TEMP_DIR);

        // 2) Create two key pairs
        // (it will generate a private key ring & a public key ring in c:\\temp)
        
        // Create a key with test1@test.com as PGP UserId
        cgeepApi.generateKeyPair("test1@test.com", "passphrase".toCharArray(),
                "RSA", 1024, "AES-256", "NEVER");

        if (!cgeepApi.isOperationOk())
        {
            System.out.println("Error    : " + cgeepApi.getErrorCode());
            System.out.println("Exception: " + cgeepApi.getException());
            return;
        }
        
        // We display the key list
        String list = cgeepApi.listKeys("");

        if(!cgeepApi.isOperationOk())
        {
            System.out.println("Error: " + cgeepApi.getErrorCode());
            System.out.println("Exception: " + cgeepApi.getException());
            return;
        }        
        
        System.out.println(list);
     

        //
        // 4) Asymmetric encryption & decryption
        //        
        
        // We reset the recipients list (Cautious to be done before each encryption)
        cgeepApi.resetRecipientsKeys();
        
        // We add test1@test.com as first recipient
        cgeepApi.addRecipientKey("test1@test.com");
                
        String fileIn   = TEMP_DIR + "\\Acro_70_Std_Fre_Upg.exe";
        String fileOut  = TEMP_DIR + "\\Acro_70_Std_Fre_Upg.exe.pgp"; // The ecnrypted file    
        
        // Now we encrypt a file for these two keys / recipients
        cgeepApi.encrypt(fileIn, fileOut, 0);       
        
        if (!cgeepApi.isOperationOk())
        {
            System.out.println("Error    : " + cgeepApi.getErrorCode());
            System.out.println("Exception: " + cgeepApi.getException());
            return;
        }     
        
        String fileDecrypted   = TEMP_DIR + "\\Acro_70_Std_Fre_Upg_DECRYPYTED.exe";
        String fileEncrypted  = fileOut; 
        
        // Now, we try to decrypt the encrypted file.
        // We can decrypt with test1@test.com, our first private key:
        
        cgeepApi.decrypt(fileEncrypted, fileDecrypted, "test1@test.com", "passphrase".toCharArray(), 0); 
        
        if (!cgeepApi.isOperationOk())
        {
            System.out.println("Error    : " + cgeepApi.getErrorCode());
            System.out.println("Exception: " + cgeepApi.getException());
            return;
        }     
        
        // We are done:
        System.out.println("Done!");        
    }

}
