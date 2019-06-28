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
package net.safester.clientserver.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSignature;

import com.safelogic.pgp.api.KeyHandlerOne;
import com.safelogic.pgp.api.PgeepPublicKey;
import com.safelogic.pgp.apispecs.KeyHandler;

import net.safester.application.util.crypto.CryptoUtil;

public class PublicKeySignatureTest {

    public PublicKeySignatureTest() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    { 
	String dir = "C:\\Users\\Nicolas de Pomereu\\safester_samples\\";
	String publicKeyBlocToVerify = FileUtils.readFileToString(new File(dir + File.separator + "pub_key.txt"));
	String signerPublicKeyBloc = FileUtils.readFileToString(new File(dir + File.separator + "master_pub_key.txt"));
	
	String signerUserId = "contact@safelogic.com";
        String userIdToVerify = CryptoUtil.extractUserIdFromKeyBloc(publicKeyBlocToVerify);
        
        PublicKeySignatureTest publicKeySignatureTest = new PublicKeySignatureTest();
        boolean verify = publicKeySignatureTest.verifyKeySignature(signerUserId, userIdToVerify, signerPublicKeyBloc, publicKeyBlocToVerify);
        System.out.println(verify);
    }
    
    
    public boolean verifyKeySignature(String signerUserId, String userIdToVerify, String signerPublicKeyBloc, String publicKeyBlocToVerify)
    throws Exception {

    	debug("signerUserId:  " + signerUserId);
    	debug("userIdToVerify:  " + userIdToVerify);
    	    	
    	KeyHandler kh = new KeyHandlerOne();
    	PublicKey masterPublicKey = kh.getPgpPublicKeyFromAsc(new ByteArrayInputStream(signerPublicKeyBloc.getBytes()));
    	//PublicKey publicKey = kh.getPgpPublicKeyFromAsc(new ByteArrayInputStream(publicKeyBlocToVerify.getBytes()));
    	PublicKey publicKey = kh.getPgpPublicKeyForEncryptionFromAsc(new ByteArrayInputStream(publicKeyBlocToVerify.getBytes()));
    	
    	PgeepPublicKey geepPublicKeyMaster = (PgeepPublicKey)masterPublicKey;
        PGPPublicKey pubKeyMaster = geepPublicKeyMaster.getKey();
        long masterKeyId = pubKeyMaster.getKeyID();
        PgeepPublicKey geepPublicKey = (PgeepPublicKey)publicKey;
        PGPPublicKey pubKey = geepPublicKey.getKey();

        List<PGPSignature> signatures = new ArrayList<PGPSignature>();
        
        Iterator iter = pubKey.getSignatures();

        while(iter.hasNext())
        {
            PGPSignature    sig = (PGPSignature)iter.next();
            signatures.add(sig);                                    
        }   
        for (int i = 0; i < signatures.size(); i++)
        {
            debug("i: " + i);
            
            PGPSignature s = signatures.get(i);
            
            debug("s.getKeyID: " + s.getKeyID());
            debug("masterKeyId: " + masterKeyId);
            
            if(s.getKeyID() != masterKeyId){
            	continue;
            }
            
            s.initVerify(pubKeyMaster, "BC");

              
                if ( s.getSignatureType() == PGPSignature.POSITIVE_CERTIFICATION)
                {
                    if (s.verifyCertification(userIdToVerify, pubKeyMaster))
                    {
                	debug("POSITIVE_CERTIFICATION true!");
                        return true;
                    } 
                    
                }
                
                if ( s.getSignatureType() == PGPSignature.SUBKEY_BINDING)
                {
                    if (s.verifyCertification(pubKeyMaster, pubKey))
                    {
                    	System.out.println(" PGPSignature.SUBKEY_BINDING true!");
                        return true;
                    }                    
                }                

         
        }
        debug("false!");
    	return false;
    }

    private void debug(String string) {
	System.out.println(string);
	
    }
    
    
}
