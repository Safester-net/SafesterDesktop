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
package net.safester.application;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;

import com.safelogic.pgp.api.KeyHandlerOne;
import com.safelogic.pgp.api.PgpActionsOne;
import com.safelogic.pgp.apispecs.KeyHandler;

import net.safester.clientserver.PgpKeyPairLocal;
import net.safester.clientserver.holder.PgpKeyPairHolder;

/**
 * @author Class to decrypt a Message Body and Subject. The connection to the
 * server is done once, after that the KeyPair & private key is stored in static
 * variables.
 */
public class MessageDecryptor {

    private int userNumber;
    private char[] passphrase;
    private Connection connection;
    private boolean integrityCheckValid = false;
    /**
     * Key pair is always stored statically
     */
    private static PgpKeyPairLocal keyPair = null;

    /**
     * Constructor.
     *
     * @param userNumber the user number to use param passphrase
     * @param connection the Jdbc Connection
     */
    public MessageDecryptor(int userNumber, char[] passphrase, Connection connection)
            throws SQLException {
        
        if (passphrase == null) {
            throw new NullPointerException("passphrase is null!");
        }

        if (connection == null) {
            throw new NullPointerException("connection is null!");
        }

        this.userNumber = userNumber;
        this.passphrase = passphrase;
        this.connection = connection;

        PgpKeyPairHolder pgpKeyPairHolder = new PgpKeyPairHolder(connection, userNumber);
        keyPair = pgpKeyPairHolder.get();
    }

    /**
     * @return the keyPair
     */
    public PgpKeyPairLocal getKeyPair() {
        return keyPair;
    }

    /**
     * Relod the key pair
     *
     * @throws SQLException
     */
    public void reload() throws SQLException {
        PgpKeyPairHolder pgpKeyPairHolder = new PgpKeyPairHolder(connection, userNumber);
        pgpKeyPairHolder.reset();
    }

    /**
     * Decrypt any input string
     *
     * @param encrypted the input encrypted string
     * @return an output decrypted string
     * @throws Exception if any Exception occurs
     */
    public String decrypt(String encrypted) throws Exception {
        if (encrypted == null) {
            throw new IllegalArgumentException("encrypted parameter can not be null!");
        }

        String privateKeyPgpBlock = getKeyPair().getPrivateKeyPgpBlock();

        KeyHandler keyHandler = new KeyHandlerOne();
        PGPPrivateKey privateKey = keyHandler.getPgpSecretKeyFromAsc(privateKeyPgpBlock, encrypted, passphrase);

        if (privateKey == null) {
            String debugStr = "userNumber: " + userNumber + " keyBloc: " + privateKeyPgpBlock + " encrypted: " + encrypted;
            throw new IllegalArgumentException("privateKey is null for: " + debugStr);
        }

        PgpActionsOne pgpActions = new PgpActionsOne();

        String decrypted = pgpActions.decryptStringPgp(encrypted, privateKey, null);

        integrityCheckValid = pgpActions.isIntegrityCheck();
        return decrypted;
    }

    public boolean isIntegrityCheckValid() {
        return integrityCheckValid;
    }

    /**
     * Encypt a PGP encrypted text for *OUR* public key only
     *
     * @param text the string to encrypt
     * @return
     * @throws java.lang.Exception
     */
    public String selfEncrypt(String text) throws Exception {
        PgpActionsOne pgpActions = new PgpActionsOne();
        pgpActions.setIntegrityCheck(true);

        List<PGPPublicKey> publicKeys = new ArrayList<>();
        PGPPublicKey pgpPublicKey = keyPair.getPgeepPublicKey().getKey();
        publicKeys.add(pgpPublicKey);

        String encrypted = pgpActions.encryptStringPgp(text, publicKeys);
        return encrypted;
    }

}
