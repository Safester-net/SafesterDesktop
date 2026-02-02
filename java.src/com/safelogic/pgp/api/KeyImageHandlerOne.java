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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.SignatureException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.attr.ImageAttribute;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPUserAttributeSubpacketVector;
import org.bouncycastle.openpgp.PGPUserAttributeSubpacketVectorGenerator;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;

import com.safelogic.pgp.api.util.crypto.PgpUserId;
import com.safelogic.pgp.apispecs.KeyHandler;
import com.safelogic.pgp.apispecs.KeyImageHandler;
import com.safelogic.utilx.Debug;

/**
 * @author Nicolas de Pomereu
 */
public class KeyImageHandlerOne implements KeyImageHandler
{
    /** The debug flag */
    protected boolean DEBUG = Debug.isSet(this);

    private static Provider getBcProvider()
    {
        Provider provider = Security.getProvider("BC");
        if (provider == null)
        {
            provider = new BouncyCastleProvider();
            Security.addProvider(provider);
        }
        return provider;
    }

    /**
     * Constructor. Loads the Bouncy Castle OpenPGP Provider
     */
    public KeyImageHandlerOne()
    {
        getBcProvider();
    }

    /**
     * Add an Image (Photo) to a PGP Key
     *
     * @param userId the user Id of the PGP private key
     * @param passphrase the passphrase of the PGP private key
     * @param image the JPEG image as byte array
     *
     * @throws IllegalArgumentException if passphrase is invalid
     */
    public void addImageToPgpKey(String userId, char[] passphrase, byte[] image)
        throws NoSuchAlgorithmException, NoSuchProviderException,
               IllegalArgumentException, IOException, KeyException,
               SignatureException, KeyStoreException
    {
        try
        {
            PGPPublicKeyRing pgpPublicKeyRing = getPGPPublicKeyRing(userId);
            PGPSecretKeyRing pgpSeccretKeyRing = getPGPSecretKeyRing(userId);

            Iterator it0 = pgpPublicKeyRing.getPublicKeys();

            PGPPublicKey pubKeyMaster = null;

            int i = 0;

            List<PGPPublicKey> pubKeyList = new Vector<PGPPublicKey>();

            while (it0.hasNext())
            {
                PGPPublicKey pubKey = (PGPPublicKey) it0.next();

                debug("i= " + i);
                debug("pubKey.isMasterKey    (): " + pubKey.isMasterKey());
                debug("pubKey.isEncryptionKey(): " + pubKey.isEncryptionKey());

                if (pubKey.isMasterKey())
                {
                    pubKeyMaster = pubKey;
                }

                pubKeyList.add(pubKey);

                i++;
            }

            if (pubKeyMaster == null)
            {
                throw new IllegalArgumentException("No master key found in public key ring for: " + userId);
            }

            PGPUserAttributeSubpacketVectorGenerator vGen = new PGPUserAttributeSubpacketVectorGenerator();
            vGen.setImageAttribute(ImageAttribute.JPEG, image);
            PGPUserAttributeSubpacketVector uVec = vGen.generate();

            // Build decryptor (BC operator API - 1.51)
            PBESecretKeyDecryptor decryptor =
                    new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider())
                            .build(passphrase);

            PGPSecretKey secKey = pgpSeccretKeyRing.getSecretKey();
            PGPPrivateKey privKey;

            try
            {
                privKey = secKey.extractPrivateKey(decryptor);
            }
            catch (PGPException e)
            {
                throw new IllegalArgumentException("Invalid passphrase for image signature.");
            }

            int pubAlg = secKey.getPublicKey().getAlgorithm();

            // Signature generator (BC operator API - 1.51)
            PGPSignatureGenerator sGen =
                    new PGPSignatureGenerator(new BcPGPContentSignerBuilder(pubAlg, HashAlgorithmTags.SHA1));

            // Note: in operator API, it's init(...) (not initSign(...))
            sGen.init(PGPSignature.POSITIVE_CERTIFICATION, privKey);

            PGPSignature sig = sGen.generateCertification(uVec, pubKeyMaster);

            PGPPublicKey nKey = PGPPublicKey.addCertification(pubKeyMaster, uVec, sig);

            Iterator it = nKey.getUserAttributes();
            int count = 0;

            while (it.hasNext())
            {
                PGPUserAttributeSubpacketVector attributes = (PGPUserAttributeSubpacketVector) it.next();

                Iterator sigs = nKey.getSignaturesForUserAttribute(attributes);
                int sigCount = 0;

                while (sigs.hasNext())
                {
                    PGPSignature s = (PGPSignature) sigs.next();

                    // In operator API, it's init(verifierProvider, pubKey)
                    s.init(new BcPGPContentVerifierBuilderProvider(), nKey);

                    if (!s.verifyCertification(attributes, nKey))
                    {
                        throw new IllegalArgumentException("cGeep - Image Add Failure: Added signature failed verification");
                    }

                    sigCount++;
                }

                if (sigCount != 1)
                {
                    throw new IllegalArgumentException("cGeep - Image Add Failure: Failed added user attributes signature check");
                }

                count++;
            }

            if (count != 1)
            {
                throw new IllegalArgumentException("cGeep - Image Add Failure: Did not find added user attributes");
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            OutputStream os = new ArmoredOutputStream(bos);

            for (int j = 0; j < pubKeyList.size(); j++)
            {
                PGPPublicKey pgpPublicKey = pubKeyList.get(j);

                if (pgpPublicKey.isMasterKey())
                {
                    nKey.encode(os);  // Encode with the replaced master key
                }
                else
                {
                    pgpPublicKey.encode(os);
                }
            }

            os.close();
            String armored = bos.toString();

            KeyHandler keyHandler = new KeyHandlerOne();
            keyHandler.deletePubKeyFromKeyRing(userId);
            keyHandler.importPgpPublicKeyFromAsc(armored);
        }
        catch (PGPException e)
        {
            throw new KeyException(e);
        }
    }

    /**
     * Remove an Image from a PgpPublicKey
     *
     * @param userId the user Id of the PGP private key
     */
    public void removeImageFromPgpKey(String userId)
        throws NoSuchAlgorithmException, NoSuchProviderException,
               IllegalArgumentException, IOException, KeyException,
               SignatureException, KeyStoreException
    {
        PGPPublicKeyRing pgpPublicKeyRing = getPGPPublicKeyRing(userId);

        Iterator it0 = pgpPublicKeyRing.getPublicKeys();

        List<PGPPublicKey> pubKeyList = new Vector<PGPPublicKey>();

        while (it0.hasNext())
        {
            PGPPublicKey pubKey = (PGPPublicKey) it0.next();

            Iterator it = pubKey.getUserAttributes();

            while (it.hasNext())
            {
                PGPUserAttributeSubpacketVector attributes = (PGPUserAttributeSubpacketVector) it.next();

                ImageAttribute imageAttribute = attributes.getImageAttribute();

                if (imageAttribute != null)
                {
                    pubKey = pubKey.removeCertification(pubKey, attributes);
                }
            }

            pubKeyList.add(pubKey);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStream os = new ArmoredOutputStream(bos);

        for (int j = 0; j < pubKeyList.size(); j++)
        {
            PGPPublicKey pgpPublicKey = pubKeyList.get(j);
            pgpPublicKey.encode(os);
        }

        os.close();
        String armored = bos.toString();

        KeyHandler keyHandler = new KeyHandlerOne();
        keyHandler.deletePubKeyFromKeyRing(userId);
        keyHandler.importPgpPublicKeyFromAsc(armored);
    }

    /**
     * Return the image as a byte array from any PGP Public key
     *
     * @param userId the User Id of the PGP public key
     * @return the image as a byte array from any PGP Public key, null if the key has no image
     */
    public byte[] getImageFromPgpPublicKey(String userId)
        throws IOException, FileNotFoundException,
               IllegalArgumentException, KeyException, NoSuchAlgorithmException
    {
        KeyHandler keyHandler = new KeyHandlerOne();
        PgeepPublicKey key = (PgeepPublicKey) keyHandler.getPgpPublicKey(userId);

        if (key == null)
        {
            return null;
        }

        PubkeyDescriptorOne descKey = new PubkeyDescriptorOne(key);
        return descKey.getImage();
    }

    /**
     * Return the image as a byte array from any PGP Public key contained in an InputStream.
     *
     * @param in The InputStream of the Keyring Collection
     * @return the image as a byte array from any PGP Public key, null if the key has no image
     */
    public byte[] getImageFromPgpPublicKey(InputStream in)
        throws IOException, FileNotFoundException,
               IllegalArgumentException, KeyException, NoSuchAlgorithmException
    {
        KeyHandler keyHandler = new KeyHandlerOne();
        PgeepPublicKey key = null;

        try
        {
            key = (PgeepPublicKey) keyHandler.getPgpPublicKeyFromAsc(in);
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
        }

        if (key == null)
        {
            return null;
        }

        PubkeyDescriptorOne descKey = new PubkeyDescriptorOne(key);
        return descKey.getImage();
    }

    /**
     * @param keyId the userId to extract the PGPSecretKeyRing for
     * @return the BC PGPSecretKeyRing
     */
    public static PGPSecretKeyRing getPGPSecretKeyRing(String keyId)
        throws FileNotFoundException, IOException, KeyException
    {
        File secKeyring = new File(PgpUserId.getPrivKeyRingFilename());
        InputStream in = new FileInputStream(secKeyring);

        in = PGPUtil.getDecoderStream(in);
        try
        {
            PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(in);
            in.close();
            Iterator rIt = pgpSec.getKeyRings();
            while (rIt.hasNext())
            {
                PGPSecretKeyRing kRing = (PGPSecretKeyRing) rIt.next();
                Iterator kIt = kRing.getSecretKeys();

                while (kIt.hasNext())
                {
                    PGPSecretKey k = (PGPSecretKey) kIt.next();

                    Iterator ituser = k.getUserIDs();
                    while (ituser.hasNext())
                    {
                        String userIdInKeyRing = (String) ituser.next();
                        if (userIdInKeyRing.contains(keyId) || keyId.contains(userIdInKeyRing))
                        {
                            return kRing;
                        }
                    }
                }
            }

            return null;
        }
        catch (PGPException e)
        {
            throw new KeyException(e);
        }
    }

    /**
     * @param keyId the userId to extract the PGPPublicKeyRing for
     * @return the BC PGPPublicKeyRing
     */
    public static PGPPublicKeyRing getPGPPublicKeyRing(String keyId)
        throws IOException, FileNotFoundException, IllegalArgumentException, KeyException,
               NoSuchAlgorithmException
    {
        if (keyId == null)
        {
            throw new IllegalArgumentException("Key Id cannot be null");
        }

        PgpUserId pgpUserId = new PgpUserId(keyId); // Checks the userId format
        String pubKeyRingFile = pgpUserId.getPubKeyRingFilename();

        try
        {
            PGPUtil.setDefaultProvider("BC");

            InputStream in = new FileInputStream(pubKeyRingFile);
            in = PGPUtil.getDecoderStream(in);

            PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in);

            Iterator rIt = pgpPub.getKeyRings();

            String userIdInKeyRing = null;
            PGPPublicKeyRing pgpPublicKeyRing = null;

            while (rIt.hasNext())
            {
                pgpPublicKeyRing = (PGPPublicKeyRing) rIt.next();
                Iterator kIt = pgpPublicKeyRing.getPublicKeys();

                while (kIt.hasNext())
                {
                    PGPPublicKey pgpPubKey = (PGPPublicKey) kIt.next();
                    Iterator ituser = pgpPubKey.getUserIDs();

                    while (ituser.hasNext())
                    {
                        userIdInKeyRing = (String) ituser.next();

                        if (userIdInKeyRing.contains(keyId))
                        {
                            return pgpPublicKeyRing;
                        }
                    }
                }
            }

            return pgpPublicKeyRing;
        }
        catch (PGPException e)
        {
            throw new KeyException(e);
        }
    }

    /**
     * @param keyId the userId to extract the PGPSecretKeyRing for
     * @return the BC PGPSecretKeyRing
     */
    public static PGPSecretKeyRing getPGPSecretKeyRingFromAsc(String keyId, String privateKeyBloc)
        throws FileNotFoundException, IOException, KeyException
    {
        InputStream in = new ByteArrayInputStream(privateKeyBloc.getBytes());

        in = PGPUtil.getDecoderStream(in);
        try
        {
            PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(in);
            in.close();
            Iterator rIt = pgpSec.getKeyRings();
            while (rIt.hasNext())
            {
                PGPSecretKeyRing kRing = (PGPSecretKeyRing) rIt.next();
                Iterator kIt = kRing.getSecretKeys();

                while (kIt.hasNext())
                {
                    PGPSecretKey k = (PGPSecretKey) kIt.next();

                    Iterator ituser = k.getUserIDs();
                    while (ituser.hasNext())
                    {
                        String userIdInKeyRing = (String) ituser.next();
                        if (userIdInKeyRing.contains(keyId) || keyId.contains(userIdInKeyRing))
                        {
                            return kRing;
                        }
                    }
                }
            }

            return null;
        }
        catch (PGPException e)
        {
            throw new KeyException(e);
        }
    }

    /**
     * Displays the specified message if the DEBUG flag is set.
     */
    protected void debug(String sMsg)
    {
        if (DEBUG)
        {
            System.out.println("DBG> " + sMsg);
        }
    }
}

//End
