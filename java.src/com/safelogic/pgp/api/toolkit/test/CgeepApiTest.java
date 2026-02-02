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



import com.safelogic.pgp.api.toolkit.CgeepApi;
import com.safelogic.pgp.api.toolkit.TaskContainer;

/**
 * @author Nicolas de Pomereu
 * Main class to test all APis
 */

//SET CLASSPATH=c:\Program Files\cGeepApi\system\cgeep_api.jar;c:\Program Files\cGeepApi\system\cgeep_provider.jar;c:\Program Files\cGeepApi\system\cgeep_crypto.jar;
// java com.safelogic.pgp.api.toolkit.test.CgeepApiTest

public class CgeepApiTest
{

        /**
    	 * Main function for test only
    	 * @param args
    	 */
    	public static void main(String [] args)
    	{
    		try
    		{
    					    		    
    			CgeepApi cgeepApi = new CgeepApi();
    			
    			String keyDirectory = "C:\\temp\\key_directory";
    			String fileIn  = keyDirectory + "\\VisualStudio6Full.exe";
    			String fileOut = keyDirectory + "\\VisualStudio6Full.exe.pgp";
    			cgeepApi.encryptSymmetric(fileIn, fileOut, "passphrase".toCharArray(), 1);    		    			
    			    			
    			while (cgeepApi.getPercentProcessedForTask(1) < 100)
    			{			    
    			    //System.out.println("ASYNC current: " + cgeepApi.getPercentProcessedForTask(1));
    			    
    			    //System.out.println("cgeepApi.getPercentProcessedForTask(1): " 
    			    //        + cgeepApi.getPercentProcessedForTask(1));
    			    
    			    TaskContainer taskContainer = new TaskContainer(1);
    			    System.out.println("taskContainer.getPercentProgress(): " 
    			                + taskContainer.getPercentProgress());
    		         boolean isOk = taskContainer.isOperationOk();
    		         Thread.sleep(1000);
    			}
    				    			
                if(!cgeepApi.isOperationOkForTask(1))
                {
                    System.out.println("Error      : " + cgeepApi.getErrorCodeForTask(1));
                    System.out.println("Exception  : " + cgeepApi.getExceptionForTask(1));
                    System.out.println("Stack Trace: " + cgeepApi.getStackTraceForTask(1));
                    return;
                }
                
                System.out.println( "encryptSymmetric() DONE!");
                if (true) return;
                
    			cgeepApi.setKeyRingDirectory("c:\\temp");
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			
    			/*
    			boolean keyringExists = cgeepApi.keyringExist();
    			System.out.println("Keyring exists:" + keyringExists);
    			System.out.println();
    			if(!keyringExists)
    			{
    				cgeepApi.generateKeyPair("alexTestAPI@safelogic.com", "passphrase".toCharArray(), "RSA", 2048, "AES-256", "NEVER");
    				if(!cgeepApi.isOperationOk())
    				{
    					System.out.println("Error: " + cgeepApi.getErrorCode());
    					System.out.println("Exception: " + cgeepApi.getException());
    					return;
    				}
    			}
    			*/
    			cgeepApi.addRecipientKey("alexTestAPI@safelogic.com");
    			String encrypted = cgeepApi.encryptString("This is a test with é");
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			System.out.println("encrypted\n" + encrypted);
    			
    			System.out.println();
    			String ids = cgeepApi.getUserIdForStringDecryption(encrypted);
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			System.out.println("encrypted for:\n" + ids);
    			System.out.println();			
    			
    			String clearString = cgeepApi.decryptString(encrypted, "alexTestAPI@safelogic.com", "passphrase".toCharArray());
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			
    			System.out.println("clear\n" + clearString);
    			
    //			System.out.println("Importing key abecquereau@safelogic.com");
    //			cgeepApi.importKey("c:\\temp\\abecquereau@safelogic.com.asc");
    //			if(!cgeepApi.isOperationOk())
    //			{
    //				System.out.println("Error: " + cgeepApi.getErrorCode());
    //				System.out.println("Exception: " + cgeepApi.getException());
    //				return;
    //			}
    //			System.out.println("Key imported");
    //			System.out.println();
    
    //			System.out.println("Deleting key abecquereau@safelogic.com");
    //			System.out.println();
    //			cgeepApi.deleteKey("abecquereau@safelogic.com");
    //			if(!cgeepApi.isOperationOk())
    //			{
    //				System.out.println("Error: " + cgeepApi.getErrorCode());
    //				System.out.println("Exception: " + cgeepApi.getException());
    //				return;
    //			}
    //			System.out.println("Key deleted");
    //			System.out.println();
    	
    			
    			System.out.println("List keys");
    			String list = cgeepApi.listKeys("");
    
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			
    			System.out.println("Search result:");
    			System.out.println(list);
    			System.out.println();
    			
    			System.out.println("Encrypting file CgeepApi.java to CgeepApi.java.pgp");
    			
    			cgeepApi.addRecipientKey("abecquereau@safelogic.com");
    			
    			cgeepApi.encrypt("c:\\temp\\CgeepApi.java", "c:\\temp\\CgeepApi.java.pgp", 0);
    			
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			
    			System.out.println("File encrypted");
    			System.out.println();
    			
    			System.out.println("Decrypting file CgeepApi.java.pgp to CgeepApi.txt");
    			cgeepApi.decrypt("c:\\temp\\CgeepApi.java.pgp", "c:\\temp\\CgeepApi.txt", "alexTestAPI@safelogic.com", "passphrase".toCharArray(), 0);
    			
    			String result = null;
    			
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			
    			System.out.println("File decrypted: " + result);
    			
    			System.out.println("Encrypt and Sign file");
    			
    			cgeepApi.signAndEncrypt("c:\\temp\\CgeepApi.java", "c:\\temp\\CgeepApi.java.pgp", "alexTestAPI@safelogic.com", "passphrase".toCharArray(), 0);
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			System.out.println("File encrypted and signed");
    			System.out.println();
    			
    			System.out.println("");
    			System.out.println("Decrypt / verify");
    			cgeepApi.decrypt("c:\\temp\\CgeepApi.java.pgp", "c:\\temp\\CgeepApi_verified.txt", "alexTestAPI@safelogic.com", "passphrase".toCharArray(), 0);
    			
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			System.out.println("Decrypted with result: " + result);
    			System.out.println();
    			
    			System.out.println("Detach Sign");
    			cgeepApi.signDetached("c:\\temp\\CgeepApi.java.pgp", "c:\\temp\\CgeepApi.sig", "alexTestAPI@safelogic.com", "passphrase".toCharArray(), 0);
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			System.out.println("File signed");
    			System.out.println();
    			
    			System.out.println("Verify signature");
    			cgeepApi.verifyDetached("c:\\temp\\CgeepApi.java.pgp", "c:\\temp\\CgeepApi.sig", 0);
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			result = cgeepApi.getSignatureStatus();
    			System.out.println("Verify result: " + result);
    			System.out.println();
    			
    			//SYMMETRIC TESTS
    			String toEncrypt = "Chaîne à crypter";
    			System.out.println("Symmetric encryption:");
    			System.out.println(toEncrypt);
    			String cryptedString = cgeepApi.encryptSymmetricString(toEncrypt, "password".toCharArray());
    			
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			System.out.println("Crypted String:");
    			System.out.println(cryptedString);
    			System.out.println();
    			
    			System.out.println("Decrypting");
    			clearString = cgeepApi.decryptSymmetricString(cryptedString, "password".toCharArray());
    			
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			System.out.println("Decrypted String: ");
    			System.out.println(clearString);
    
    			System.out.println("Symm encryption of file");
    			
    			cgeepApi.encryptSymmetric("c:\\temp\\CgeepApi.java", "c:\\temp\\CgeepApi_sym.java.pgp", "password".toCharArray(), 0);
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			System.out.println("File encrypted");
    			System.out.println();
    			
    			
    			cgeepApi.decryptSymmetric("c:\\temp\\CgeepApi_sym.java.pgp", "c:\\temp\\CgeepApi_sym.java",  "password".toCharArray(), 0);
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			System.out.println("file decrypted");
    			System.out.println();
    			
    			System.out.println("Changing passphrase");
    			cgeepApi.changePassphrase("alexTestAPI@safelogic.com", "passphrase".toCharArray(), "passphraseNew".toCharArray());
    			if(!cgeepApi.isOperationOk())
    			{
    				System.out.println("Error: " + cgeepApi.getErrorCode());
    				System.out.println("Exception: " + cgeepApi.getException());
    				return;
    			}
    			System.out.println("Passphrase changed");
    			System.out.println();
    			
    			System.out.println("Exporting key pair");
    			System.out.println();
    			
    			cgeepApi.exportPublicAndPrivateKey("alexTestAPI@safelogic.com", "alexTestAPI@safelogic.com.asc");
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}

}
