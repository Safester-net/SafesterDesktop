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
package net.safester.application.addrbooknew.gmail;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 *
 * @author Nicolas de Pomereu
 */
public class GmailClientSecret {

    // DO NOT SUPPRESS BEGIN
    
    // CLIENT_SECRET = "uIUDqLa81lxuYzLRUNMniEN1";
    // private static String ENCRYPTED_CLIENT_SECRET = "QxQFSqbCchf24J13fDcSB43qfd1PfJcHn81XQfe0uPShrvwfYt3fkg==";
    
    /*
    private static void encrypt() {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPasswordCharArray(GooglePeopleParms.CLIENT_SECRET_PASSWORD.toCharArray());
        
        String encryptedClientSecret = textEncryptor.encrypt("uIUDqLa81lxuYzLRUNMniEN1");
        System.out.println("private static String ENCRYPTED_CLIENT_SECRET = \"" + encryptedClientSecret + "\";");
    }
    
    public static void main(String[] args) throws Exception {
        //encrypt();
        
        //String decryptedClientSecret = decrypt(ENCRYPTED_CLIENT_SECRET);
        //System.out.println("decryptedClientSecret: " + decryptedClientSecret + ":s");
    }
   */
    
    // DO NOT SUPPRESS END
    
    public static String decrypt(String encrypted) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPasswordCharArray(GooglePeopleParms.CLIENT_SECRET_PASSWORD.toCharArray());
        String decrypted = textEncryptor.decrypt(encrypted);
        return decrypted;
    }

}
