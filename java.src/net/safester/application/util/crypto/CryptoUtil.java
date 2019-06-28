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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;

import com.safelogic.pgp.api.KeyHandlerOne;
import com.safelogic.pgp.api.PgeepPublicKey;
import com.safelogic.pgp.api.PgpActionsOne;
import com.safelogic.pgp.apispecs.KeyHandler;

/**
 *
 * @author Alexandre Becquereau
 */
public class CryptoUtil {

    /**
     * Extract first user id from a public key bloc
     * @param publicKeyBloc
     * @return
     * @throws Exception
     */
    public static String extractUserIdFromKeyBloc(String publicKeyBloc)
            throws Exception {
        PGPPublicKeyRingCollection pubRings = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(
                new ByteArrayInputStream(publicKeyBloc.getBytes())));

        Iterator<?> rIt = pubRings.getKeyRings();

        while (rIt.hasNext()) {
            PGPPublicKeyRing pgpPub = (PGPPublicKeyRing) rIt.next();

            Iterator<?> it = pgpPub.getPublicKeys();

            while (it.hasNext()) {
                PGPPublicKey pgpKey = (PGPPublicKey) it.next();
                Iterator<?> ituser = pgpKey.getUserIDs();

                while (ituser.hasNext()) {
                    String userIdInKeyRing = (String) ituser.next();
                    return userIdInKeyRing;
                }
            }
        }
        return null;
    }

    public static String decrypt(String encrypted, String privateKeyBloc, char[] passphrase)
    throws Exception{
        if (encrypted == null){
            return null;
        }

        KeyHandler keyHandler = new KeyHandlerOne();
        PGPPrivateKey privateKey = keyHandler.getPgpSecretKeyFromAsc(privateKeyBloc, encrypted, passphrase);

        if (privateKey == null)
        {
            throw new IllegalArgumentException("privateKey is null.");
        }

        PgpActionsOne pgpActions = new PgpActionsOne();

        String decrypted = pgpActions.decryptStringPgp(encrypted, privateKey, null);
        if(!pgpActions.isIntegrityCheck()){
            decrypted = "**WARNING Integrity Check Failed**" + decrypted;
        }
        return decrypted;
    }

    public static String encrypt(String clear, List<String> pubKeys)
    throws Exception{
        if(clear == null){
            return null;
        }
        List<PGPPublicKey> publicKeys = new ArrayList<PGPPublicKey>();
        KeyHandler kh = new KeyHandlerOne();
        for(String keyBloc : pubKeys){
            ByteArrayInputStream bis = new ByteArrayInputStream(keyBloc.getBytes());
            PgeepPublicKey pubKey = (PgeepPublicKey) kh.getPgpPublicKeyForEncryptionFromAsc(bis);
            publicKeys.add(pubKey.getKey());
        }

        PgpActionsOne pgpActions = new PgpActionsOne();
        pgpActions.setIntegrityCheck(true);
        String encrypted = pgpActions.encryptStringPgp(clear, publicKeys);
        return encrypted;
    }
}
