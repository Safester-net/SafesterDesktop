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
package com.safelogic.pgp.api;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;
import org.bouncycastle.bcpg.SignatureSubpacket;
import org.bouncycastle.bcpg.SignatureSubpacketTags;
import org.bouncycastle.bcpg.sig.Exportable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.bouncycastle.openpgp.PGPUtil;

import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;

import com.safelogic.pgp.api.util.crypto.PgpUserId;
import com.safelogic.pgp.api.util.parms.Parms;
import com.safelogic.pgp.apispecs.KeyHandler;
import com.safelogic.pgp.apispecs.KeySignatureHandler;

public class KeySignatureHandlerOne implements KeySignatureHandler
{
    /** The debug flag */
    protected boolean DEBUG = false; //Debug.isSet(this);
    

    /** Operation for key revocation */
    private static int SIGNATURE_KEY_REVOKE = -1;
    
    /** Operation for Signature removal */
    private static int SIGNATURE_DELETE = 0;
    
    /** Operation to add signature to a key */
    private static int SIGNATURE_ADD = 1;    

    
    public KeySignatureHandlerOne()
    {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static PGPPrivateKey extractPrivateKey(PGPSecretKey secretKey, char[] passphrase) throws PGPException
    {
        PBESecretKeyDecryptor decryptor =
                new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider())
                        .build(passphrase);

        return secretKey.extractPrivateKey(decryptor);
    }

    public static void main(
            String[] args)
        throws Exception
    {
        KeySignatureHandler keySignatureHandler = new KeySignatureHandlerOne();
        
        String test = "test@test.com";
        char [] testPassphrase = "passphrase".toCharArray();
        
        keySignatureHandler.revokeKey(test, testPassphrase);
        System.out.println("revoked! : " + test);
        
        if (true) return;
        
        String nico = "ndepomereu@safelogic.com";
        char [] myPassphrase = "arnold loves $ and ca".toCharArray();
        
        String alex = "abecquereau@safelogic.com";
        List<String> keyIds = null;
        List<String> descriptions = null;
        
        keyIds          = keySignatureHandler.getUserIdsOfSigningKeys(alex);      
        descriptions    = keySignatureHandler.getDescriptionOfSigningKeys(alex);
        
        System.out.println();
        System.out.println("Before: " + keyIds);
        System.out.println("Before: " + descriptions);
                
        boolean isSigned = keySignatureHandler.signPgpPublicKey(
                        nico,
                        myPassphrase,
                        alex,
                        "cGeep",
                        "cGeep", 
                        true);
        
        System.out.println("isSigned: " + isSigned);
               
        keyIds          = keySignatureHandler.getUserIdsOfSigningKeys(alex);      
        descriptions    = keySignatureHandler.getDescriptionOfSigningKeys(alex);
        
        System.out.println();
        System.out.println("After Add Signature: " + keyIds);
        System.out.println("After Add Signature: " + descriptions);
        
        if (true) return;
        
        keySignatureHandler.removeSignatureFromPgpPublicKey(nico, 
                                                               myPassphrase, 
                                                               alex);
        
        keyIds =   keySignatureHandler.getUserIdsOfSigningKeys(alex);      
        
        System.out.println();
        System.out.println("After delete Signature:" + keyIds);
                
    }


    public boolean signPgpPublicKey(String userId, 
                                 char[] passphrase, 
                                 String userIdToSign, 
                                 String notationName,
                                 String notationValue, 
                                 boolean isExportable) 
            throws NoSuchAlgorithmException,
            NoSuchProviderException, IllegalArgumentException, IOException,
            KeyException, SignatureException, KeyStoreException
    {
        
        List<String> signerUserids = getUserIdsOfSigningKeys(userIdToSign);
        
        for (String string : signerUserids)
        {
            if (string.contains(userId))
            {
                return false;
            }
        }
        
        String  ascSignedPublicKey = signAndExportAscPgpPublicKey(userId, 
                                                                  passphrase, 
                                                                  userIdToSign,
                                                                  notationName, 
                                                                  notationValue,
                                                                  isExportable,
                                                                  SIGNATURE_ADD);
        
        KeyHandler keyHandler = new KeyHandlerOne();
        keyHandler.deletePubKeyFromKeyRing(userIdToSign);
        keyHandler.importPgpPublicKeyFromAsc(ascSignedPublicKey);
        
        return true;
                
    }

    public void removeSignatureFromPgpPublicKey(String userId, 
                                                char[] passphrase, 
                                                String userIdToSign)
        throws  NoSuchAlgorithmException, NoSuchProviderException,
                IllegalArgumentException, IOException, KeyException,
                SignatureException, KeyStoreException
    {

        String  ascSignedPublicKey = signAndExportAscPgpPublicKey(  userId, 
                                                                    passphrase, 
                                                                    userIdToSign,
                                                                    null, 
                                                                    null,
                                                                    true,
                                                                    SIGNATURE_DELETE);

        KeyHandler keyHandler = new KeyHandlerOne();
        keyHandler.deletePubKeyFromKeyRing(userIdToSign);
        keyHandler.importPgpPublicKeyFromAsc(ascSignedPublicKey);
    }

    
    public void revokeKey(String userId, char[] passphrase)
        throws  NoSuchAlgorithmException, NoSuchProviderException,
                IllegalArgumentException, IOException, KeyException,
                SignatureException, KeyStoreException
    {

        String  ascSignedPublicKey = signAndExportAscPgpPublicKey(  userId, 
                passphrase, 
                userId,
                null, 
                null,
                true,
                SIGNATURE_KEY_REVOKE);

        KeyHandler keyHandler = new KeyHandlerOne();
        keyHandler.deletePubKeyFromKeyRing(userId);
        keyHandler.importPgpPublicKeyFromAsc(ascSignedPublicKey);
    }    

    
    /**
     * 
     * @param userId    the user if of the Signed Key
     * @return          the List of keys that have signed this key.
     *                  if a key is unknown, the value in the list
     *                  is it's PGP Id in Long
     *                  
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws KeyException
     * @throws SignatureException
     * @throws KeyStoreException
     * 
     */
    public List<String> getUserIdsOfSigningKeys(String userId)
        throws NoSuchAlgorithmException, NoSuchProviderException,
               IllegalArgumentException, IOException, KeyException, 
               SignatureException, KeyStoreException
       {
            List<String> signingUserIds = new Vector<String>();
        
            List<Long> signingPgpIds = getPgpIdsOfSigningKeys(userId);
            
            for (Long long1 : signingPgpIds)
            {
                String userid = getUserIdforPgpId(long1);
                
                if (userid == null)
                {
                    userid = Long.toHexString(long1);
                    userid = "0x" + userid.substring(8);
                }
 
                signingUserIds.add(userid);
            }
            
            return signingUserIds;            
       }
    
    
    /**
     * 
     * @param userIdToSign    the User id of the key to sign
     * @param signerUserId    the User id of the signer key
     *        
     * @return true if the key is already signed
     * 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws KeyException
     * @throws SignatureException
     * @throws KeyStoreException
     * 
     */
    public boolean isKeyAlreadySignedBySigner(String userIdToSign, String signerUserId)
        throws NoSuchAlgorithmException, NoSuchProviderException,
        IllegalArgumentException, IOException, KeyException, 
        SignatureException, KeyStoreException      
    {
        List<String> signerUserids = getUserIdsOfSigningKeys(userIdToSign);
        
        for (String string : signerUserids)
        {
            if (string.contains(signerUserId))
            {
                return true;
            }
        }        
        
        return false;
    }
    
    
    private Map<Long, String> keyMap = null;
    
    /**
     * Return the full user Id (email@domain.com) for a Long PGP Id
     * 
     * @param pgpId     the PG Id as Long
     * @return the full user Id (email@domain.com) 
     * 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws KeyException
     * @throws SignatureException
     * @throws KeyStoreException
     */
    private String getUserIdforPgpId(long pgpId)
            throws NoSuchAlgorithmException, NoSuchProviderException,
            IllegalArgumentException, IOException, KeyException, 
            SignatureException, KeyStoreException    
    {
        
        if (keyMap == null)
        {
            keyMap = new HashMap<Long, String>(); 
            
            KeyHandler keyHandler = new KeyHandlerOne();

            List<String> publicKeysUserids = keyHandler.getPublicKeyRingUserIds();

            for (int i = 0; i < publicKeysUserids.size(); i++)
            {
                String userId = publicKeysUserids.get(i);

                // Not the one for encryption 
                PgeepPublicKey pubKey  = (PgeepPublicKey) keyHandler.getPgpPublicKey(userId);             
                PGPPublicKey pgpPubkey = pubKey.getKey();
                long pgpPubKeyId = pgpPubkey.getKeyID();
                
                keyMap.put(pgpPubKeyId, userId);
            }    
            
            return getUserIdforPgpId(pgpId);
        }
        else
        {
            return keyMap.get(pgpId);
        }

    }
    
    
    private  List<Long> getPgpIdsOfSigningKeys(String userId)
        throws  NoSuchAlgorithmException, NoSuchProviderException,
                IllegalArgumentException, IOException, KeyException,
                SignatureException, KeyStoreException
    {
        List<PGPSignature>  pgpSignatures = getSignatures(userId);
                
        List<Long> keyIds = new Vector<Long>();
        
        for (PGPSignature signature : pgpSignatures)
        {            
            Long keyId = signature.getKeyID();
            
            if (! keyIds.contains(keyId))
            {
                keyIds.add(keyId);
            }
        }
        
        return keyIds;
    }
    

    public List<String> getDescriptionOfSigningKeys(String userId)
        throws NoSuchAlgorithmException, NoSuchProviderException,
               IllegalArgumentException, IOException, KeyException, 
               SignatureException, KeyStoreException
   {

        List<PGPSignature>  pgpSignatures = getSignatures(userId);
        
        List<PGPSignature> pgpSignaturesUnique = new Vector<PGPSignature>();
        
        List<Long> keyIds = new Vector<Long>();
        
        // Avoid duplicate signature infos: take one signaure base on the PGP Key ID
        for (PGPSignature signature : pgpSignatures)
        {                        
            if (! keyIds.contains(signature.getKeyID()))
            {
                pgpSignaturesUnique.add(signature);
                keyIds.add(signature.getKeyID());
            }
        }        
        
        List<String> descriptions = new Vector<String>();
                
        KeyHandler keyHandler = new KeyHandlerOne();
        
        for (PGPSignature signature : pgpSignaturesUnique)
        {                        
            String description = getAlgorithm(signature.getKeyAlgorithm());
            
            description += getDescriptions(signature);
            description += "signature ";
                        
            if (keyHandler.isPublicKeyRevoked(userId))
            {
                description += "REVOKED";
            }
            
            descriptions.add(description);
        }         

        
        return descriptions;
   }

    /**
     * Extract all know descriptions from signature:
     * <br>
     * <br>- If it is Exportable or Local
     * <br>- If it is expired
     * 
     * @param signature the PGP sdignature to analyse
     * @return
     */
    public static String getDescriptions(PGPSignature signature)
    {
        String description = " "; // One blank always
        
        //Add if signature if the exportable tag is set
        PGPSignatureSubpacketVector pGPSignatureSubpacketVector = signature.getHashedSubPackets();  
            
        if (pGPSignatureSubpacketVector == null)
        {
            return description;
        }
        
        if (pGPSignatureSubpacketVector.hasSubpacket(SignatureSubpacketTags.EXPORTABLE))
        {                
            SignatureSubpacket    p = pGPSignatureSubpacketVector.getSubpacket(SignatureSubpacketTags.EXPORTABLE);
            
            if (p!= null && p instanceof Exportable)
            {                    
                Exportable exportable = (Exportable)p;
                if (exportable.isExportable())
                {
                    description += "exportable ";
                }
                else
                {
                    description += "local ";
                }
            }           
        }
                

        Long expirationTime = pGPSignatureSubpacketVector.getKeyExpirationTime();
        if (expirationTime != 0)
        {
            Date today = new Date();
            if (today.getTime() > expirationTime)
            {
                description += "expired ";
            }
        }
        
        return description;
    }
    
    private static String getAlgorithm(
            int    algId)
        {
            switch (algId)
            {
            case PublicKeyAlgorithmTags.RSA_GENERAL:
                return "RSA_GENERAL";
            case PublicKeyAlgorithmTags.RSA_ENCRYPT:
                return "RSA_ENCRYPT";
            case PublicKeyAlgorithmTags.RSA_SIGN:
                return "RSA_SIGN";
            case PublicKeyAlgorithmTags.ELGAMAL_ENCRYPT:
                return "ELGAMAL_ENCRYPT";
            case PublicKeyAlgorithmTags.DSA:
                return "DSA";
            case PublicKeyAlgorithmTags.EC:
                return "EC";
            case PublicKeyAlgorithmTags.ECDSA:
                return "ECDSA";
            case PublicKeyAlgorithmTags.ELGAMAL_GENERAL:
                return "ELGAMAL_GENERAL";
            case PublicKeyAlgorithmTags.DIFFIE_HELLMAN:
                return "DIFFIE_HELLMAN";
            }

            return "unknown alg";
        }    

    /**
     * return all PGP signatures for a key id
     * 
     * 
     * @param userId            the key id 
     * 
     * @return a signed pgp public key armored into a string
     * @throws Exception
     */
    
    /* (non-Javadoc)
     * @see com.safelogic.pgp.apispecs.KeyHandler#exportAscPgpPublicKey(java.lang.String)
     */
    public List<PGPSignature> getSignatures(String userId) 
        throws  NoSuchAlgorithmException,
                NoSuchProviderException, IllegalArgumentException, IOException,
                KeyException, SignatureException, KeyStoreException
    {
        
        if(userId == null)
        {
            throw new IllegalArgumentException("Key Id userId cannot be null");
        }
        
        PgpUserId pgpUserId         = new PgpUserId(userId); // Checks the userId format

        String pubKeyRingFile = pgpUserId.getPubKeyRingFilename();

        List<PGPSignature> pgpSignatures = new Vector<PGPSignature>();
        
        try
        {
            PGPUtil.setDefaultProvider("BC");

            InputStream in = new FileInputStream(pubKeyRingFile);
            in = PGPUtil.getDecoderStream(in);

            PGPPublicKeyRingCollection        pgpPub = new PGPPublicKeyRingCollection(in);

            //
            // iterate through the key rings.
            //
            Iterator rIt = pgpPub.getKeyRings();

            // BEWARE
            // This will contain AT FIRST ITERATION ONLY on PGPPublicKey.getUserIDs()
            // The userid name. Aka, the second iteration, there will be no value
            String userIdInKeyRing = null;

            // The armored key to export
            String armoredKey = null;

            while (rIt.hasNext())
            {
                PGPPublicKeyRing    kRing = (PGPPublicKeyRing)rIt.next();    
                Iterator            kIt = kRing.getPublicKeys();
                
                while (kIt.hasNext())
                {
                    PGPPublicKey    pgpPubKey = (PGPPublicKey)kIt.next();      
                                                                                           
                    Iterator    ituser = pgpPubKey.getUserIDs();

                    while (ituser.hasNext())
                    {                                               
                        userIdInKeyRing = (String) ituser.next();
                        debug("User ID as param  : " + userId);  
                        debug("User ID in keyring: " + userIdInKeyRing);                                                              
                    }

                    if (userIdInKeyRing.contains(userId))
                    {
                        // Ok, add the key to export armored string
                        // For 1) Master key AND 2) Encryption Key
                        debug("id Good User ID!"); 
                                                
                        // Iterate through signatures to find the signatures 
                        Iterator iter = pgpPubKey.getSignatures();

                        while(iter.hasNext())
                        {
                            PGPSignature    sig = (PGPSignature)iter.next();
                            pgpSignatures.add(sig);                                    
                        }                           
                                                 
                    }                    

                }
            }

        }
        catch (PGPException e)
        {
            throw new KeyException(e);
        }

        return pgpSignatures;
        
    }    
      
    
    
    
    /**
     * Sign a PGP Public Key and return a signed getEncoded() byte array
     * 
     * 
     * @param userId            the PGP Secret key that will sign
     * @param passphrase        the passphrase of the PGP Secret key
     * @param userIdToSign      the PGPPublicKey to be signed
     * @param notationName      the Notation Name 
     * @param notationValue     the Notation Value
     * @param isExportable      if true, the signature will be exportable
     * @param operation         SIGNATURE_ADD or SIGNATURE_DELETE
     * 
     * @return a signed pgp public key armored into a string
     * @throws Exception
     */
    
    /* (non-Javadoc)
     * @see com.safelogic.pgp.apispecs.KeyHandler#exportAscPgpPublicKey(java.lang.String)
     */
    private String signAndExportAscPgpPublicKey(String userId, 
                                                char[] passphrase, 
                                                String userIdToSign, 
                                                String notationName,
                                                String notationValue,
                                                boolean isExportable,
                                                int operation) 
        throws  NoSuchAlgorithmException,
                NoSuchProviderException, IllegalArgumentException, IOException,
                KeyException, SignatureException, KeyStoreException
    {
        
        if(userId == null)
        {
            throw new IllegalArgumentException("Key Id userId cannot be null");
        }
        
        if(userIdToSign == null)
        {
            throw new IllegalArgumentException("Key Id userIdToSign cannot be null");
        }
        
        PgpUserId pgpUserId         = new PgpUserId(userId); // Checks the userId format
        PgpUserId pgpUserIdToSign   = new PgpUserId(userIdToSign); // Checks the userId format

        String pubKeyRingFile = pgpUserIdToSign.getPubKeyRingFilename();

        try
        {
            PGPUtil.setDefaultProvider("BC");

            InputStream in = new FileInputStream(pubKeyRingFile);
            in = PGPUtil.getDecoderStream(in);

            PGPPublicKeyRingCollection        pgpPub = new PGPPublicKeyRingCollection(in);

            //
            // iterate through the key rings.
            //
            Iterator rIt = pgpPub.getKeyRings();

            // BEWARE
            // This will contain AT FIRST ITERATION ONLY on PGPPublicKey.getUserIDs()
            // The userid name. Aka, the second iteration, there will be no value
            String userIdInKeyRing = null;

            // The armored key to export
            String armoredKey = null;

            while (rIt.hasNext())
            {
                PGPPublicKeyRing    kRing = (PGPPublicKeyRing)rIt.next();    
                Iterator            kIt = kRing.getPublicKeys();

                ByteArrayOutputStream bos = new  ByteArrayOutputStream();                
                OutputStream os = new ArmoredOutputStream(bos);

                PGPSecretKeyRing pgpSeccretKeyRing 
                        = KeyImageHandlerOne.getPGPSecretKeyRing(userId);
                
                while (kIt.hasNext())
                {
                    PGPPublicKey    pgpPubKey = (PGPPublicKey)kIt.next();      
                                                                                           
                    Iterator    ituser = pgpPubKey.getUserIDs();

                    while (ituser.hasNext())
                    {                                               
                        userIdInKeyRing = (String) ituser.next();
                        debug("User ID as param  : " + userId);  
                        debug("User ID in keyring: " + userIdInKeyRing);                                                              
                    }

                    if (userIdInKeyRing.contains(userIdToSign))
                    {
                        // Ok, add the key to export armored string
                        // For 1) Master key AND 2) Encryption Key
                        debug("id Good User ID!"); 
                                                        
                        if (operation == SIGNATURE_ADD)
                        {
                            pgpPubKey = signatureAddToPGPPublicKey(
                                                pgpSeccretKeyRing.getSecretKey(),
                                                passphrase,
                                                userIdToSign,
                                                pgpPubKey,                                                
                                                notationName,
                                                notationValue,
                                                isExportable
                            ); 
                        }
                        else if (operation == SIGNATURE_KEY_REVOKE)
                        {
                            if (pgpPubKey.isMasterKey())
                            {
                                pgpPubKey = revoke(pgpSeccretKeyRing.getSecretKey(),
                                                passphrase,
                                                pgpPubKey); 
                            }
                        }                            
                        else if (operation == SIGNATURE_DELETE)
                        {
                            // Ierate through signatures to find the signatues fonr by 
                            Iterator iter = pgpPubKey.getSignatures();

                            while(iter.hasNext())
                            {
                                PGPSignature    sig = (PGPSignature)iter.next();
                                
                                // Remove all the signatures issued by userId
                                if (sig.getKeyID() == pgpSeccretKeyRing.getSecretKey().getKeyID())
                                {
                                    pgpPubKey = PGPPublicKey.removeCertification(pgpPubKey, sig);
                                }
                            }
                        }
                        else
                        {
                            throw new IllegalArgumentException("Invalid operation: " + operation);
                        }
                       
                                                
                        pgpPubKey.encode(os);

                        // Finish all if we have done both master key & encryption key
                        if (pgpPubKey.isEncryptionKey())   
                        {
                            //debug("is Encryption Key!"); 
                            os.close();
                            armoredKey = bos.toString();
                            return armoredKey;
                        }
                    }                    

                }
            }

            if (armoredKey == null)
            {
//              throw new IllegalArgumentException("Can't find encryption key in key ring."
//              + " for User ID: " + userId);
                throw new KeyException(Parms.NO_ENCRYPTION_KEY_FOR_ID);
            }

            // Will never be reached
            return armoredKey;

        }
        catch (PGPException e)
        {
            throw new KeyException(e);
        }

    }
    
    /**
     * Sign a PGPPublicKey and return a signed getEncoded() byte array
     * 
     * 
     * @param secretKey         the PGP Secret key that will sign
     * @param secretKeyPass     the passphrase of the PGP Secret key
     * @param keyToBeSigned     the PGPPublicKey to be signed
     * @param notationName      the Notation Name 
     * @param notationValue     the Notation Value
     * @param isExportable      if true, the signature will be exportable
     * 
     * @return a signed getEncoded() byte array
     * @throws Exception
     */
    private PGPPublicKey revoke(    PGPSecretKey secretKey, 
                                    char [] secretKeyPass, 
                                    PGPPublicKey keyToBeSigned
                                )
        throws NoSuchAlgorithmException,
                NoSuchProviderException, IllegalArgumentException, IOException,
                KeyException, SignatureException, KeyStoreException    
    {
        try
        {

            PGPPrivateKey pgpPrivKey = extractPrivateKey(secretKey, secretKeyPass);

            PGPSignatureGenerator sGen = new PGPSignatureGenerator(
                    new BcPGPContentSignerBuilder(secretKey.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1));

            //sGen.init(PGPSignature.DEFAULT_CERTIFICATION, pgpPrivKey);
            sGen.init(PGPSignature.KEY_REVOCATION, pgpPrivKey);
            
            PGPSignature sig = sGen.generateCertification(keyToBeSigned);

            keyToBeSigned = PGPPublicKey.addCertification(keyToBeSigned, sig);

            return PGPPublicKey.addCertification(keyToBeSigned, sig);

        }
        catch (PGPException e)
        {
           throw new KeyException(e);
        }
    }       
    
    /**
     * Sign a PGPPublicKey and return a signed getEncoded() byte array
     * 
     * 
     * @param secretKey         the PGP Secret key that will sign
     * @param secretKeyPass     the passphrase of the PGP Secret key
     * @param userIdToSign      the User id to sign
     * @param keyToBeSigned     the PGPPublicKey to be signed
     * @param notationName      the Notation Name 
     * @param notationValue     the Notation Value
     * @param isExportable      if true, the signature will be exportable
     * 
     * @return a signed getEncoded() byte array
     * @throws Exception
     */
    private PGPPublicKey signatureAddToPGPPublicKey(    PGPSecretKey secretKey, 
                                                        char [] secretKeyPass, 
                                                        String userIdToSign,
                                                        PGPPublicKey keyToBeSigned, 
                                                        String notationName, 
                                                        String notationValue, 
                                                        boolean isExportable)
        throws NoSuchAlgorithmException,
                NoSuchProviderException, IllegalArgumentException, IOException,
                KeyException, SignatureException, KeyStoreException    
    {
        try
        {
            OutputStream out = new ByteArrayOutputStream();
            
            out = new ArmoredOutputStream(out);

            PGPPrivateKey pgpPrivKey = extractPrivateKey(secretKey, secretKeyPass);

            PGPSignatureGenerator sGen = new PGPSignatureGenerator(
                    new BcPGPContentSignerBuilder(secretKey.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1));

            //sGen.initSign(PGPSignature.DEFAULT_CERTIFICATION, pgpPrivKey);
            
            int signatureType = 0;
            
            if (keyToBeSigned.isMasterKey())
            {
                signatureType = PGPSignature.POSITIVE_CERTIFICATION;
            }
            else
            {
                signatureType = PGPSignature.SUBKEY_BINDING;
            }
            
            sGen.init(signatureType, pgpPrivKey);
            
            BCPGOutputStream            bOut = new BCPGOutputStream(out);

            sGen.generateOnePassVersion(false).encode(bOut);

            PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();

            boolean isHumanReadable = true;
            
            //spGen.setTrust(true, 3, 120);
            //spGen.setSignerUserID(isHumanReadable, notationValue); 
            //spGen.setRevocable(true, true);
            
            if (notationName != null && notationValue != null)
            {
                spGen.setNotationData(true, isHumanReadable, notationName, notationValue);
            }
            
            spGen.setExportable(true, isExportable);
                        
            PGPSignatureSubpacketVector packetVector = spGen.generate();
            sGen.setHashedSubpackets(packetVector);

            bOut.flush();
            
            if (keyToBeSigned.isMasterKey())
            {           
                PGPSignature    certification = sGen.generateCertification(userIdToSign, keyToBeSigned);
                
                return PGPPublicKey.addCertification(keyToBeSigned,
                                                     userIdToSign,
                                                     certification);                
            }
            else
            {
                KeyHandler kh = new KeyHandlerOne();
                String signerUserId = kh.getDefaultKey();
                
                PublicKey masterPublicKey = kh.getPgpPublicKey(signerUserId);
                PgeepPublicKey pgeepPublicKey = (PgeepPublicKey)masterPublicKey;
                
                PGPSignature  certification = sGen.generateCertification(pgeepPublicKey.getKey(), 
                                                                         keyToBeSigned);
                
                return PGPPublicKey.addCertification(keyToBeSigned,
                                                     certification);                                 
            }

        }
        catch (PGPException e)
        {
           throw new KeyException(e);
        }
    }      

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// SafeShare specific part
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* (non-Javadoc)
     * @see com.safelogic.pgp.apispecs.KeyHandler#exportAscPgpPublicKey(java.lang.String)
     */
    public String signAndExportAscPgpPublicKeyFromAsc(
									String pgpPrivateKeyBloc,
									String signerPublicKeyBloc,
									String userId, 
                                    char[] passphrase, 
                                    String pgpPublicKeyBloc,
                                    String userIdToSign, 
                                    String notationName,
                                    String notationValue,
                                    boolean isExportable,
                                    int operation) 
        throws  NoSuchAlgorithmException,
                NoSuchProviderException, IllegalArgumentException, IOException,
                KeyException, SignatureException, KeyStoreException
    {
        
        if(userId == null)
        {
            throw new IllegalArgumentException("Key Id userId cannot be null");
        }
        
        if(userIdToSign == null)
        {
            throw new IllegalArgumentException("Key Id userIdToSign cannot be null");
        }
        
        
        PgpUserId pgpUserIdToSign   = new PgpUserId(userIdToSign); // Checks the userId format



        try
        {
            PGPUtil.setDefaultProvider("BC");

            InputStream in = new ByteArrayInputStream(pgpPublicKeyBloc.getBytes());
            in = PGPUtil.getDecoderStream(in);

            PGPPublicKeyRingCollection        pgpPub = new PGPPublicKeyRingCollection(in);

            //
            // iterate through the key rings.
            //
            Iterator rIt = pgpPub.getKeyRings();

            // BEWARE
            // This will contain AT FIRST ITERATION ONLY on PGPPublicKey.getUserIDs()
            // The userid name. Aka, the second iteration, there will be no value
            String userIdInKeyRing = null;

            // The armored key to export
            String armoredKey = null;

            while (rIt.hasNext())
            {
                PGPPublicKeyRing    kRing = (PGPPublicKeyRing)rIt.next();    
                Iterator            kIt = kRing.getPublicKeys();

                ByteArrayOutputStream bos = new  ByteArrayOutputStream();                
                OutputStream os = new ArmoredOutputStream(bos);

                PGPSecretKeyRing pgpSeccretKeyRing 
                        = KeyImageHandlerOne.getPGPSecretKeyRingFromAsc(userId, pgpPrivateKeyBloc);
                
                while (kIt.hasNext())
                {
                    PGPPublicKey    pgpPubKey = (PGPPublicKey)kIt.next();      
                                                                                           
                    Iterator    ituser = pgpPubKey.getUserIDs();

                    while (ituser.hasNext())
                    {                                               
                        userIdInKeyRing = (String) ituser.next();                                                     
                    }

                    if (userIdInKeyRing.contains(userIdToSign))
                    {
                        // Ok, add the key to export armored string
                        // For 1) Master key AND 2) Encryption Key
                        debug("id Good User ID!"); 
                                                        
                        if (operation == SIGNATURE_ADD)
                        {
                            pgpPubKey = signatureAddToPGPPublicKeyAsc( userId,
                            					signerPublicKeyBloc,
                                                pgpSeccretKeyRing.getSecretKey(),
                                                passphrase,
                                                userIdToSign,
                                                pgpPubKey,                                                
                                                notationName,
                                                notationValue,
                                                isExportable
                            ); 
                        }
                        else if (operation == SIGNATURE_KEY_REVOKE)
                        {
                            if (pgpPubKey.isMasterKey())
                            {
                                pgpPubKey = revoke(pgpSeccretKeyRing.getSecretKey(),
                                                passphrase,
                                                pgpPubKey); 
                            }
                        }                            
                        else if (operation == SIGNATURE_DELETE)
                        {
                            // Iterate through signatures to find the signatues fonr by 
                            Iterator iter = pgpPubKey.getSignatures();

                            while(iter.hasNext())
                            {
                                PGPSignature    sig = (PGPSignature)iter.next();
                                
                                // Remove all the signatures issued by userId
                                if (sig.getKeyID() == pgpSeccretKeyRing.getSecretKey().getKeyID())
                                {
                                    pgpPubKey = PGPPublicKey.removeCertification(pgpPubKey, sig);
                                }
                            }
                        }
                        else
                        {
                            throw new IllegalArgumentException("Invalid operation: " + operation);
                        }
                       
                                                
                        pgpPubKey.encode(os);

                        // Finish all if we have done both master key & encryption key
                        if (pgpPubKey.isEncryptionKey())   
                        {
                            os.close();
                            armoredKey = bos.toString();
                            return armoredKey;
                        }
                    }                    

                }
            }

            if (armoredKey == null)
            {
                throw new KeyException(Parms.NO_ENCRYPTION_KEY_FOR_ID);
            }

            // Will never be reached
            return armoredKey;

        }
        catch (PGPException e)
        {
            throw new KeyException(e);
        }

    }
    
    
    /**
     * Sign a PGPPublicKey and return a signed getEncoded() byte array
     * 
     * 
     * @param secretKey         the PGP Secret key that will sign
     * @param secretKeyPass     the passphrase of the PGP Secret key
     * @param userIdToSign      the User id to sign
     * @param keyToBeSigned     the PGPPublicKey to be signed
     * @param notationName      the Notation Name 
     * @param notationValue     the Notation Value
     * @param isExportable      if true, the signature will be exportable
     * 
     * @return a signed getEncoded() byte array
     * @throws Exception
     */
    private PGPPublicKey signatureAddToPGPPublicKeyAsc( String signerUserId,   
    													String signerPublicKeyBloc,
    													PGPSecretKey secretKey, 
                                                        char [] secretKeyPass, 
                                                        String userIdToSign,
                                                        PGPPublicKey keyToBeSigned, 
                                                        String notationName, 
                                                        String notationValue, 
                                                        boolean isExportable)
        throws NoSuchAlgorithmException,
                NoSuchProviderException, IllegalArgumentException, IOException,
                KeyException, SignatureException, KeyStoreException    
    {
        try
        {
            OutputStream out = new ByteArrayOutputStream();
            
            out = new ArmoredOutputStream(out);

            PGPPrivateKey pgpPrivKey = extractPrivateKey(secretKey, secretKeyPass);

            PGPSignatureGenerator sGen = new PGPSignatureGenerator(
                    new BcPGPContentSignerBuilder(secretKey.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1));

            
            int signatureType = 0;
            
            if (keyToBeSigned.isMasterKey())
            {
                signatureType = PGPSignature.POSITIVE_CERTIFICATION;
            }
            else
            {
                signatureType = PGPSignature.SUBKEY_BINDING;
            }
            
            sGen.init(signatureType, pgpPrivKey);
            
            BCPGOutputStream            bOut = new BCPGOutputStream(out);

            sGen.generateOnePassVersion(false).encode(bOut);

            PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();

            boolean isHumanReadable = true;


            
            if (notationName != null && notationValue != null)
            {
                spGen.setNotationData(true, isHumanReadable, notationName, notationValue);
            }
            
            spGen.setExportable(true, isExportable);
                        
            PGPSignatureSubpacketVector packetVector = spGen.generate();
            sGen.setHashedSubpackets(packetVector);

            bOut.flush();
            
            if (keyToBeSigned.isMasterKey())
            {           
                PGPSignature    certification = sGen.generateCertification(userIdToSign, keyToBeSigned);
                
                return PGPPublicKey.addCertification(keyToBeSigned,
                                                     userIdToSign,
                                                     certification);                
            }
            else
            {
                KeyHandler kh = new KeyHandlerOne();
              
                
                //PublicKey masterPublicKey = kh.getPgpPublicKey(signerUserId);
                PublicKey masterPublicKey = kh.getPgpPublicKeyFromAsc(new ByteArrayInputStream(signerPublicKeyBloc.getBytes()));
                PgeepPublicKey pgeepPublicKey = (PgeepPublicKey)masterPublicKey;
                
                PGPSignature  certification = sGen.generateCertification(pgeepPublicKey.getKey(), 
                                                                         keyToBeSigned);
                
                return PGPPublicKey.addCertification(keyToBeSigned,
                                                     certification);                                 
            }

        }
        catch (PGPException e)
        {
           throw new KeyException(e);
        }
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
            
            s.init(new BcPGPContentVerifierBuilderProvider(), pubKeyMaster);

              
                if ( s.getSignatureType() == PGPSignature.POSITIVE_CERTIFICATION)
                {
                    if (s.verifyCertification(userIdToVerify, pubKeyMaster))
                    {
                	debug("true!");
                        return true;
                    } 
                    
                }
                
                if ( s.getSignatureType() == PGPSignature.SUBKEY_BINDING)
                {
                    if (s.verifyCertification(pubKeyMaster, pubKey))
                    {
                    	System.out.println("true!");
                        return true;
                    }                    
                }                

         
        }
        debug("false!");
    	return false;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// End SafeShare specific part
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    

    
    /**
     * debug tool
     */
    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
    }    
}

