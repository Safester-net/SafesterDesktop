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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.generators.ElGamalKeyPairGenerator;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.ElGamalKeyGenerationParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPKeyPair;

import com.safelogic.pgp.api.util.crypto.PgpUserId;
import com.safelogic.pgp.api.util.crypto.elgamal.Group;
import com.safelogic.pgp.api.util.crypto.elgamal.Precomputed;
import com.safelogic.pgp.api.util.msg.MessagesManager;
import com.safelogic.pgp.api.util.parms.CmPgpCodes;
import com.safelogic.pgp.api.util.parms.Parms;
import com.safelogic.pgp.api.util.parms.PgpCryptoParms;
import com.safelogic.pgp.api.util.parms.PgpTags;
import com.safelogic.pgp.apispecs.KeyHandler;
import com.safelogic.pgp.apispecs.PubkeyDescriptor;
import com.safelogic.pgp.util.CsvEmailExtractor;
import com.safelogic.pgp.util.FileUtil;
import com.safelogic.pgp.util.UserPreferencesManager;
import com.safelogic.utilx.Debug;
import com.safelogic.utilx.Hex;
import com.safelogic.utilx.io.stream.StreamCopier;
import com.safelogic.utilx.syntax.EmailChecker;

/**
 * @author Alexandre Becquereau
 *
 */
public class KeyHandlerOne implements KeyHandler {
    /** The debug flag */
    protected boolean DEBUG = Debug.isSet(this);

    public static boolean isSet = false;

    private static final BigInteger RSA_PUBLIC_EXPONENT = BigInteger.valueOf(0x10001L);

    private static final int PRIME_CERTAINTY = 12;

    private static PGPKeyPair toPgpKeyPair(int algorithm, AsymmetricCipherKeyPair keyPair) throws PGPException {
	return new BcPGPKeyPair(algorithm, keyPair, new Date());
    }

    private static PGPDigestCalculator buildSha1DigestCalculator() throws PGPException {
	return new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA1);
    }

    private static PBESecretKeyDecryptor buildPBESecretKeyDecryptor(char[] passphrase) throws PGPException {
	return new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(passphrase);
    }

    private static PBESecretKeyEncryptor buildPBESecretKeyEncryptor(int algorithm, char[] passphrase)
	    throws PGPException {
	return new BcPBESecretKeyEncryptorBuilder(algorithm, buildSha1DigestCalculator())
		.setSecureRandom(new SecureRandom())
		.build(passphrase);
    }

    private static AsymmetricCipherKeyPair generateRsaKeyPair(int keyLength, SecureRandom random) {
	RSAKeyPairGenerator generator = new RSAKeyPairGenerator();
	generator.init(new RSAKeyGenerationParameters(RSA_PUBLIC_EXPONENT, random, keyLength, PRIME_CERTAINTY));
	return generator.generateKeyPair();
    }

    private static AsymmetricCipherKeyPair generateDsaKeyPair(SecureRandom random) {
	DSAParametersGenerator parametersGenerator = new DSAParametersGenerator();
	parametersGenerator.init(1024, PRIME_CERTAINTY, random);
	DSAParameters parameters = parametersGenerator.generateParameters();
	DSAKeyPairGenerator generator = new DSAKeyPairGenerator();
	generator.init(new DSAKeyGenerationParameters(random, parameters));
	return generator.generateKeyPair();
    }

    private static AsymmetricCipherKeyPair generateElGamalKeyPair(int keyLength, SecureRandom random) {
	Group elgamalGroup = Precomputed.getElGamalGroup(keyLength);

	if (elgamalGroup == null) {
	    throw new IllegalArgumentException("Group elgamalGroup can not be null!");
	}

	ElGamalParameters parameters = new ElGamalParameters(elgamalGroup.getP(), elgamalGroup.getG());
	ElGamalKeyPairGenerator generator = new ElGamalKeyPairGenerator();
	generator.init(new ElGamalKeyGenerationParameters(random, parameters));
	return generator.generateKeyPair();
    }

    /**
     * Constructor. Loads theBouncy Castle OpenPGP Provider
     */
    public KeyHandlerOne() {
	if (!isSet) {
	    Security.addProvider(new BouncyCastleProvider());
	    isSet = true;
	}
    }

    public void generateAndStoreKeyPair(String userId, char[] passphrase, String algoAsym, int keyLengthAsym,
	    String algoSym, int keyLengthSym, byte[] seed, Date expirationDate)
	    throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
	    IllegalArgumentException, IOException, KeyException {

	if (userId == null) {
	    throw new IllegalArgumentException("Parameter user can not be null");
	}

	if (algoAsym == null) {
	    throw new IllegalArgumentException("Parameter algoAsym can not be null");
	}

	// Checks if parameters are valid
	if ((keyLengthAsym == 0) || (keyLengthAsym % 128 != 0)) {
	    throw new IllegalArgumentException("Asymetric Key length must be a multiple of 128: " + keyLengthAsym);
	}

	if (keyLengthSym != 128 && keyLengthSym != 192 && keyLengthSym != 256 && keyLengthSym != 168) {
	    throw new IllegalArgumentException("Symetric Key length must be: 128 or 192 or 256: " + keyLengthSym);
	}

	if (seed == null) {
	    throw new IllegalArgumentException("Seed cannot be null.");
	}

	if (passphrase == null) {
	    throw new IllegalArgumentException("Passphrase cannot be null.");
	}

	// PgpUserId pgpUserId = new PgpUserId(userId);

	try {
	    File secretKeyRing = new File(PgpUserId.getPrivKeyRingFilename());
	    FileOutputStream osSecretKeyRing = new FileOutputStream(secretKeyRing);

	    File publicKeyRing = new File(PgpUserId.getPubKeyRingFilename());
	    FileOutputStream osPublicKeyRing = new FileOutputStream(publicKeyRing);

	    generateKeyPair(userId, passphrase, algoAsym, keyLengthAsym, algoSym, keyLengthSym, seed, expirationDate,
		    osSecretKeyRing, osPublicKeyRing);

	    osSecretKeyRing.close();
	    osPublicKeyRing.close();

	} catch (PGPException e) {
	    throw new KeyException(e);
	}

    }

    /**
     * Generic key pair generation (with output stream)
     * 
     * @param userId
     * @param passphrase
     * @param algoAsym
     * @param keyLengthAsym
     * @param algoSym
     * @param keyLengthSym
     * @param seed
     * @param expirationDate
     * @param osSecretKeyRing
     * @param osPublicKeyRing
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchProviderException
     * @throws PGPException
     * @throws IOException
     */
    public void generateKeyPair(String userId, char[] passphrase, String algoAsym, int keyLengthAsym, String algoSym,
	    int keyLengthSym, byte[] seed, Date expirationDate, OutputStream osSecretKeyRing,
	    OutputStream osPublicKeyRing) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException,
	    NoSuchProviderException, PGPException, IOException {
	PGPKeyRingGenerator keyRingGen = getKeyringGenerator(userId, passphrase, algoAsym, keyLengthAsym, algoSym,
		keyLengthSym, seed, expirationDate);

	keyRingGen.generateSecretKeyRing().encode(osSecretKeyRing);

	keyRingGen.generatePublicKeyRing().encode(osPublicKeyRing);
    }

    public void generateAndAddKeyPair(String userId, char[] passphrase, String algoAsym, int keyLengthAsym,
	    String algoSym, int keyLengthSym, byte[] seed, Date expirationDate)
	    throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
	    IllegalArgumentException, IOException, KeyException {

	if (userId == null) {
	    throw new IllegalArgumentException("Parameter user can not be null");
	}

	if (algoAsym == null) {
	    throw new IllegalArgumentException("Parameter algoAsym can not be null");
	}

	// Checks if parameters are valid
	if ((keyLengthAsym == 0) || (keyLengthAsym % 128 != 0)) {
	    throw new IllegalArgumentException("Asymetric Key length must be a multiple of 128: " + keyLengthAsym);
	}

	if (keyLengthSym != 128 && keyLengthSym != 192 && keyLengthSym != 256 && keyLengthSym != 168) {
	    throw new IllegalArgumentException("Symetric Key length must be: 128 or 192 or 256: " + keyLengthSym);
	}

	if (seed == null) {
	    throw new IllegalArgumentException("Seed cannot be null.");
	}

	if (passphrase == null) {
	    throw new IllegalArgumentException("Passphrase cannot be null.");
	}
	try {
	    PGPKeyRingGenerator keyRingGen = getKeyringGenerator(userId, passphrase, algoAsym, keyLengthAsym, algoSym,
		    keyLengthSym, seed, expirationDate);

	    File secKeyring = new File(PgpUserId.getPrivKeyRingFilename());
	    InputStream in = new FileInputStream(secKeyring);
	    in = PGPUtil.getDecoderStream(in);
	    PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(in);
	    pgpSec = PGPSecretKeyRingCollection.addSecretKeyRing(pgpSec, keyRingGen.generateSecretKeyRing());

	    File pubKeyring = new File(PgpUserId.getPubKeyRingFilename());

	    in.close();
	    in = new FileInputStream(pubKeyring);
	    in = PGPUtil.getDecoderStream(in);

	    PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in);
	    pgpPub = PGPPublicKeyRingCollection.addPublicKeyRing(pgpPub, keyRingGen.generatePublicKeyRing());
	    in.close();

	    OutputStream out = new FileOutputStream(secKeyring);
	    pgpSec.encode(out);
	    out.flush();
	    out.close();

	    out = new FileOutputStream(pubKeyring);
	    pgpPub.encode(out);
	    out.flush();
	    out.close();
	} catch (PGPException e) {
	    throw new KeyException(e);
	}

    }

    /**
     * Get a PGPKeyRingGenerator to generate key pair with given parameters
     * 
     * @param userId
     * @param passphrase
     * @param algoAsym
     * @param keyLengthAsym
     * @param algoSym
     * @param keyLengthSym
     * @param seed
     * @param expirationDate
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws PGPException
     */
    private PGPKeyRingGenerator getKeyringGenerator(String userId, char[] passphrase, String algoAsym,
	    int keyLengthAsym, String algoSym, int keyLengthSym, byte[] seed, Date expirationDate)
	    throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException, PGPException {
	// PgpUserId pgpUserId = new PgpUserId(userId);

	// Symetric algorithm depends on symetric key length
	int pgpSymAlgorihtm = 0;

	if (algoSym.equals(PgpCryptoParms.AES)) {
	    if (keyLengthSym == 128) {
		pgpSymAlgorihtm = PGPEncryptedData.AES_128;
	    } else if (keyLengthSym == 192) {
		pgpSymAlgorihtm = PGPEncryptedData.AES_192;
	    } else if (keyLengthSym == 256) {
		pgpSymAlgorihtm = PGPEncryptedData.AES_256;
	    }
	} else if (algoSym.equals(PgpCryptoParms.BLOWFISH)) {
	    pgpSymAlgorihtm = PGPEncryptedData.BLOWFISH;
	} else if (algoSym.contains(PgpCryptoParms.CAST)) {
	    pgpSymAlgorihtm = PGPEncryptedData.CAST5;
	} else if (algoSym.contains(PgpCryptoParms.TRIPLE_DES)) {
	    pgpSymAlgorihtm = PGPEncryptedData.TRIPLE_DES;
	} else {
	    throw new IllegalArgumentException("Invalid Symmetric Algorithm: " + algoSym);
	}

	AsymmetricCipherKeyPair keyPairSign = null;
	AsymmetricCipherKeyPair keyPairCrypt = null;

	PGPKeyPair pgpKeyPairSign = null;
	PGPKeyPair pgpKeyPairCrypt = null;

	if (algoAsym.equals(PgpCryptoParms.RSA)) {
	    SecureRandom random = new SecureRandom(seed);
	    keyPairSign = generateRsaKeyPair(keyLengthAsym, random);
	    keyPairCrypt = generateRsaKeyPair(keyLengthAsym, random);

	    //pgpKeyPairSign = new PGPKeyPair(PGPPublicKey.RSA_SIGN, keyPairSign, new Date());
	    //pgpKeyPairCrypt = new PGPKeyPair(PGPPublicKey.RSA_ENCRYPT, keyPairCrypt, new Date());
	    pgpKeyPairSign = toPgpKeyPair(PGPPublicKey.RSA_SIGN, keyPairSign);
	    pgpKeyPairCrypt = toPgpKeyPair(PGPPublicKey.RSA_ENCRYPT, keyPairCrypt);
	    
	    
	} else if (algoAsym.equals(PgpCryptoParms.DSA_ELGAMAL)) {
	    SecureRandom random = new SecureRandom(seed);
	    keyPairSign = generateDsaKeyPair(random);
	    keyPairCrypt = generateElGamalKeyPair(keyLengthAsym, random);

	    //pgpKeyPairSign = new PGPKeyPair(PGPPublicKey.DSA, keyPairSign, new Date());
	    //pgpKeyPairCrypt = new PGPKeyPair(PGPPublicKey.ELGAMAL_ENCRYPT, keyPairCrypt, new Date());
	    
	    pgpKeyPairSign = toPgpKeyPair(PGPPublicKey.DSA, keyPairSign);
	    pgpKeyPairCrypt = toPgpKeyPair(PGPPublicKey.ELGAMAL_ENCRYPT, keyPairCrypt);

	} else {
	    throw new IllegalArgumentException("Invalid Asymmetric Algorithm: " + algoAsym);
	}

	PGPSignatureSubpacketGenerator subpacketGenerator = new PGPSignatureSubpacketGenerator();

	if (expirationDate != null) {
	    Date today = new Date(System.currentTimeMillis());
	    long expiration = (expirationDate.getTime() - today.getTime()) / 1000;
	    subpacketGenerator.setKeyExpirationTime(false, expiration);
	} else {
	    // Forever key
	    subpacketGenerator.setKeyExpirationTime(false, 0);
	}

	subpacketGenerator.setPreferredSymmetricAlgorithms(false, new int[] { pgpSymAlgorihtm });

	subpacketGenerator.setPreferredCompressionAlgorithms(false,
		new int[] { CompressionAlgorithmTags.ZIP, CompressionAlgorithmTags.ZLIB });
	subpacketGenerator.setPreferredHashAlgorithms(false,
		new int[] { HashAlgorithmTags.SHA1, HashAlgorithmTags.MD5 });
	subpacketGenerator.setExportable(true, true);
	PGPSignatureSubpacketVector subpacketVector = subpacketGenerator.generate();

	PGPKeyRingGenerator keyRingGen = null;

	PGPDigestCalculator sha1Calc = buildSha1DigestCalculator();
	PBESecretKeyEncryptor keyEncryptor = buildPBESecretKeyEncryptor(pgpSymAlgorihtm, passphrase);
	PGPContentSignerBuilder signerBuilder = new BcPGPContentSignerBuilder(
		pgpKeyPairSign.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1);

	keyRingGen = new PGPKeyRingGenerator(
		PGPSignature.POSITIVE_CERTIFICATION,
		pgpKeyPairSign,
		userId,
		sha1Calc,
		subpacketVector,
		null,
		signerBuilder,
		keyEncryptor);

	keyRingGen.addSubKey(pgpKeyPairCrypt);
	return keyRingGen;
    }

    /**
     * Extract from private key ring the owner as user Id.
     * 
     * @return the Private Key Ring owner as user Id
     * 
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws KeyException
     * @throws NoSuchAlgorithmException
     */

    public String getPrivKeyRingOwner()
	    throws IOException, IllegalArgumentException, KeyException, NoSuchAlgorithmException {
	return this.getDefaultKey();
    }

    public boolean existsInPrivKeyRing(String userId)
	    throws IOException, IllegalArgumentException, KeyException, NoSuchAlgorithmException {

	// PgpUserId pgpUserId = new PgpUserId(userId);
	String privKeyRingFile = PgpUserId.getPrivKeyRingFilename();

	// PrivateKey privKey = null;

	PGPUtil.setDefaultProvider("BC");

	try {
	    //
	    // Read the private key rings
	    //
	    PGPSecretKeyRingCollection secRings = new PGPSecretKeyRingCollection(
		    PGPUtil.getDecoderStream(new FileInputStream(privKeyRingFile)));

	    Iterator<?> rIt = secRings.getKeyRings();

	    while (rIt.hasNext()) {
		PGPSecretKeyRing pgpSec = (PGPSecretKeyRing) rIt.next();

//				long        pgpKeyID = 0;
//				PublicKey   pKey = null;

		Iterator<?> it = pgpSec.getSecretKeys();
		// boolean first = true;
		while (it.hasNext()) {
		    PGPSecretKey pgpKey = (PGPSecretKey) it.next();

		    Iterator<?> ituser = pgpKey.getUserIDs();

		    while (ituser.hasNext()) {
			String userIdInKeyRing = (String) ituser.next();

			if (userIdInKeyRing.contains(userId)) {
			    return true;
			}
		    }
		}
	    }
	} catch (PGPException e) {
	    throw new KeyException(e);
	}

	return false;

    }

    public boolean existsInPubKeyRing(String userId)
	    throws IOException, IllegalArgumentException, KeyException, NoSuchAlgorithmException {

//		PgpUserId pgpUserId = new PgpUserId(userId); 
	String pubKeyRingFile = PgpUserId.getPubKeyRingFilename();

	// PrivateKey privKey = null;

	PGPUtil.setDefaultProvider("BC");

	try {
	    userId = CsvEmailExtractor.getEmailString(userId);
	} catch (Exception e1) {
	    // Key is not an email.
	    e1.printStackTrace();
	}

	try {
	    //
	    // Read the private key rings
	    //
	    PGPPublicKeyRingCollection pubRings = new PGPPublicKeyRingCollection(
		    PGPUtil.getDecoderStream(new FileInputStream(pubKeyRingFile)));

	    Iterator<?> rIt = pubRings.getKeyRings();

	    while (rIt.hasNext()) {
		PGPPublicKeyRing pgpPub = (PGPPublicKeyRing) rIt.next();

//				long        pgpKeyID = 0;
//				PublicKey   pKey = null;

		Iterator<?> it = pgpPub.getPublicKeys();

		while (it.hasNext()) {
		    PGPPublicKey pgpKey = (PGPPublicKey) it.next();

		    Iterator<?> ituser = pgpKey.getUserIDs();

		    while (ituser.hasNext()) {
			String userIdInKeyRing = (String) ituser.next();

			if (userIdInKeyRing.contains(userId)) {
			    return true;
			}
		    }
		}
	    }
	} catch (PGPException e) {
	    throw new KeyException(e);
	}

	return false;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.safelogic.pgp.apispecs.KeyHandler#getPgpPrivateKey(java.lang.String,
     * char[])
     */
    public PrivateKey getPgpPrivateKey(String userId, PublicKey pubKey, char[] passphrase) throws IOException,
	    FileNotFoundException, IllegalArgumentException, KeyException, NoSuchAlgorithmException {
	if (userId == null) {
	    throw new IllegalArgumentException("User Id cannot be null");
	}

	PgpUserId pgpUserId = new PgpUserId(userId); // Checks the userId format
	File privKeyRingFile = new File(PgpUserId.getPrivKeyRingFilename());

	PrivateKey privKey = getPgpPrivateKey(userId, privKeyRingFile, pubKey, passphrase);
	return privKey;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.safelogic.pgp.apispecs.KeyHandler#getPgpPrivateKey(java.lang.String,
     * char[])
     */
    public PrivateKey getPgpPrivateKey(String userId, File privKeyring, PublicKey pubKey, char[] passphrase)
	    throws IOException, FileNotFoundException, IllegalArgumentException, KeyException,
	    NoSuchAlgorithmException {
	if (userId == null) {
	    throw new IllegalArgumentException("User Id cannot be null");
	}

	if (privKeyring == null) {
	    throw new IllegalArgumentException("private keyring cannot be null");
	}

	if (!privKeyring.exists()) {
	    throw new FileNotFoundException("Private Keyring file not found:" + privKeyring);
	}

	InputStream in = new FileInputStream(privKeyring);

	try {
	    PGPUtil.setDefaultProvider("BC");

	    return getPgpPrivateKey(in, userId, passphrase);
	} catch (PGPException e) {
	    // e.printStackTrace();
	    throw new KeyException(e);
	}
    }

    public PrivateKey getPgpPrivateKey(InputStream in, String userId, char[] passphrase)
	    throws IOException, PGPException {
	in = PGPUtil.getDecoderStream(in);

	PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(in);

	//
	// we just loop through the collection till we find a key suitable for
	// encryption, in the real
	// world you would probably want to be a bit smarter about this.
	//
	PGPSecretKey key = null;

	//
	// iterate through the key rings.
	//
	Iterator<?> rIt = pgpSec.getKeyRings();
	boolean isGoodUserid = false;

	while (key == null && rIt.hasNext()) {
	    PGPSecretKeyRing kRing = (PGPSecretKeyRing) rIt.next();
	    Iterator<?> kIt = kRing.getSecretKeys();

	    while (key == null && kIt.hasNext()) {
		PGPSecretKey k = (PGPSecretKey) kIt.next();

		Iterator<?> ituser = k.getUserIDs();
		while (ituser.hasNext()) {
		    String userIdInKeyRing = (String) ituser.next();
		    // debug("User ID as param : " + userId);
		    // debug("User ID in keyring: " + userIdInKeyRing);
		    if (userIdInKeyRing.contains(userId)) {
			// debug("id Good User ID!");
			isGoodUserid = true;
		    }
		}

		if (k.isMasterKey() && isGoodUserid) {
		    key = k;
		}
	    }
	}

	if (key == null) {
	    throw new IllegalArgumentException("Can't find signing key in key ring.");
	}

	debug("OK. Returning signing key for: " + userId);
	return new PgeepPrivateKey(key, passphrase);
    }

    public PublicKey getPgpPublicKey(String userId) throws IOException, FileNotFoundException, IllegalArgumentException,
	    KeyException, NoSuchAlgorithmException {
	return getPgpPublicKey(userId, false);
    }

    public PublicKey getPgpPublicKeyForEncryption(String userId) throws IOException, FileNotFoundException,
	    IllegalArgumentException, KeyException, NoSuchAlgorithmException {
	return getPgpPublicKey(userId, true);
    }

    /**
     * 
     * @param userId           The User ID of the key.
     * @param getEncryptionKey if true, get the encryption key. To be used for the
     *                         public key of the owner.
     * @return the Public Key corresponding to the User iD
     * @throws IOException
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     * @throws KeyException
     * @throws NoSuchAlgorithmException
     */
    private PublicKey getPgpPublicKey(String userId, boolean getEncryptionKey) throws IOException,
	    FileNotFoundException, IllegalArgumentException, KeyException, NoSuchAlgorithmException {

	String pubKeyRingFile = PgpUserId.getPubKeyRingFilename();
	InputStream in = new FileInputStream(pubKeyRingFile);

	return getPgpPublicKeyFromStream(userId, getEncryptionKey, in);

    }

    /**
     * 
     * @param userId           The User ID of the key.
     * @param getEncryptionKey if true, get the encryption key. To be used for the
     *                         public key of the owner.
     * @param in               The input stream containing public keyring
     * 
     * @return the Public Key corresponding to the User iD
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws KeyException
     * @throws NoSuchAlgorithmException
     */
    public PublicKey getPgpPublicKeyFromStream(String userId, boolean getEncryptionKey, InputStream in)
	    throws IOException, KeyException {
	PGPPublicKey key = null;

	if (userId == null) {
	    throw new IllegalArgumentException("User Id cannot be null");
	}

	try {
	    // Key may not be an email
	    EmailChecker emailChecker = new EmailChecker(userId);

	    if (emailChecker.isSyntaxValid()) {
		userId = CsvEmailExtractor.getEmailString(userId);
	    } else {
		// Userid must not be extracted from the <> and kept as original
	    }

	} catch (Exception e1) {
	    // Key is not an email.
	    e1.printStackTrace();
	}

	// NO: DOT NOT VERIFY USER ID ==> WILL FAIL IF NO EMAIL IN USERID
	// PgpUserId pgpUserId = new PgpUserId(userId); // Checks the userId format
	// String pubKeyRingFile = PgpUserId.getPubKeyRingFilename();

	// PublicKey publicKey = null;

	try {
	    PGPUtil.setDefaultProvider("BC");

	    // InputStream in = new FileInputStream(pubKeyRingFile);
	    in = PGPUtil.getDecoderStream(in);

	    PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in);

	    //
	    // we just loop through the collection till we find a key suitable for
	    // encryption, in the real
	    // world you would probably want to be a bit smarter about this.
	    //

	    //
	    // iterate through the key rings.
	    //
	    Iterator<?> rIt = pgpPub.getKeyRings();

	    boolean isGoodUserid = false;

	    while (key == null && rIt.hasNext()) {
		PGPPublicKeyRing kRing = (PGPPublicKeyRing) rIt.next();
		Iterator<?> kIt = kRing.getPublicKeys();
		// boolean encryptionKeyFound = false;

		while (key == null && kIt.hasNext()) {
		    PGPPublicKey k = (PGPPublicKey) kIt.next();

		    Iterator<?> ituser = k.getUserIDs();

		    while (ituser.hasNext()) {
			String userIdInKeyRing = (String) ituser.next();
			// DEBUG = true;
			debug("User ID as param  : " + userId);
			debug("User ID in keyring: " + userIdInKeyRing);

			// 19/04/06 10:10 NDP - getPgpPublicKey() - Test0 User Id in existence without
			// caps
			userId = userId.toLowerCase();
			userIdInKeyRing = userIdInKeyRing.toLowerCase();

			if (userIdInKeyRing.contains(userId) || (userId.contains(userIdInKeyRing))) {
			    if (DEBUG) {
				System.out.println("");
				System.out.println("UserId: " + userIdInKeyRing);
				System.out.println("KeyId : " + k.getKeyID());
			    }

			    // debug("id Good User ID!");
			    isGoodUserid = true;
			}
		    }

		    // Get the first key found, or only the encryption key
		    // depending on how getEncryptionKey boolean is set

		    if (getEncryptionKey) {
			if (isGoodUserid && k.isEncryptionKey()) {
			    debug("k.isEncryptionKey()");
			    key = k;
			}
		    } else {
			if (isGoodUserid) {
			    key = k;

			}
		    }

		}
	    }

	    in.close();

	    if (key == null) {
		throw new IllegalArgumentException("Can't find encryption key in key ring for User ID: " + userId);
	    }

	    debug("OK. Returning encryption key for: " + userId);
	    return new PgeepPublicKey(key);
	} catch (PGPException e) {
	    throw new KeyException(e);
	}
    }

    public PublicKey getPgpPublicKeyForEncryptionFromAsc(InputStream in) throws IOException, FileNotFoundException,
	    IllegalArgumentException, KeyException, NoSuchAlgorithmException {
	return getPgpPublicKeyFromAsc(in, true);
    }

    public PublicKey getPgpPublicKeyFromAsc(InputStream in) throws IOException, FileNotFoundException,
	    IllegalArgumentException, KeyException, NoSuchAlgorithmException {
	return getPgpPublicKeyFromAsc(in, false);
    }

    private PublicKey getPgpPublicKeyFromAsc(InputStream in, boolean getEncryptionKey) throws IOException,
	    FileNotFoundException, IllegalArgumentException, KeyException, NoSuchAlgorithmException {

	// PublicKey publicKey = null;

	try {
	    PGPUtil.setDefaultProvider("BC");

	    // InputStream in = new FileInputStream(pubKeyRingFile);
	    in = PGPUtil.getDecoderStream(in);

	    PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in);

	    //
	    // we just loop through the collection till we find a key suitable for
	    // encryption, in the real
	    // world you would probably want to be a bit smarter about this.
	    //
	    PGPPublicKey key = null;

	    //
	    // iterate through the key rings.
	    //
	    Iterator<?> rIt = pgpPub.getKeyRings();

	    while (key == null && rIt.hasNext()) {
		PGPPublicKeyRing kRing = (PGPPublicKeyRing) rIt.next();
		Iterator<?> kIt = kRing.getPublicKeys();
		// boolean encryptionKeyFound = false;

		while (key == null && kIt.hasNext()) {
		    PGPPublicKey k = (PGPPublicKey) kIt.next();

		    Iterator<?> ituser = k.getUserIDs();

		    // Get the first key found, or only the encryption key
		    // depending on how getEncryptionKey boolean is set

		    if (getEncryptionKey) {
			if (k.isEncryptionKey()) {
			    debug("k.isEncryptionKey()");
			    key = k;
			}
		    } else {
			key = k;
		    }

		}
	    }

	    in.close();

	    if (key == null) {
		throw new IllegalArgumentException("Can't find encryption key in InputStream: ");
	    }

	    return new PgeepPublicKey(key);
	} catch (PGPException e) {
	    throw new KeyException(e);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.safelogic.pgp.apispecs.KeyHandler#getPublicKeyRingUserIds()
     */

    public List<String> getPublicKeyRingUserIds() throws IOException, FileNotFoundException, IllegalArgumentException,
	    KeyException, NoSuchAlgorithmException {
	return getPublicKeyRingUserIds(false);
    }

    public List<String> getPublicKeyRingUserIdsStillValid() throws IOException, FileNotFoundException,
	    IllegalArgumentException, KeyException, NoSuchAlgorithmException {
	return getPublicKeyRingUserIds(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.safelogic.pgp.apispecs.KeyHandler#getPublicKeyRingUserIds()
     */

    private List<String> getPublicKeyRingUserIds(boolean onlyVadidKeys) throws IOException, FileNotFoundException,
	    IllegalArgumentException, KeyException, NoSuchAlgorithmException {
	List<String> userIds = new Vector<String>();

	String pubKeyRingFile = PgpUserId.getPubKeyRingFilename();

//		PublicKey publicKey = null;

	PGPUtil.setDefaultProvider("BC");

	try {
	    //
	    // Read the private key rings
	    //
	    PGPPublicKeyRingCollection pubRings = new PGPPublicKeyRingCollection(
		    PGPUtil.getDecoderStream(new FileInputStream(pubKeyRingFile)));

	    Iterator<?> rIt = pubRings.getKeyRings();

	    while (rIt.hasNext()) {
		PGPPublicKeyRing pgpPub = (PGPPublicKeyRing) rIt.next();

//				long        pgpKeyID = 0;
//				PublicKey   pKey = null;

		Iterator<?> it = pgpPub.getPublicKeys();

		while (it.hasNext()) {
		    PGPPublicKey pgpKey = (PGPPublicKey) it.next();

		    PgeepPublicKey pgeepPublicKey = new PgeepPublicKey(pgpKey);
		    PubkeyDescriptor pubkeyDescriptor = new PubkeyDescriptorOne(pgeepPublicKey);

		    if (onlyVadidKeys) {
			if (pubkeyDescriptor.isExpired() || pubkeyDescriptor.isRevoked()) {
			    continue;
			}
		    }

		    Iterator<?> ituser = pgpKey.getUserIDs();

		    while (ituser.hasNext()) {
			String userIdInKeyRing = (String) ituser.next();
			userIds.add(userIdInKeyRing);

			// displayUsingAllCharsets(userIdInKeyRing);
		    }
		}
	    }
	} catch (PGPException e) {
	    throw new KeyException(e);
	}

	Collections.sort(userIds);

	return userIds;
    }

    /**
     * Extract a KeyBundle from the specified key ring and for the specified userId
     * <br>
     * WARNING: The match will be done only on the e-mail part of the userId! <br>
     * 
     * @param userId  the User Id to extract the key bundle for.
     * @param keyRing the keyRing to extract
     * @return a KeyBundle that contains the PGP private/public key for the user Id.
     *         return null if there is no key bundle for this userId in the key ring
     */
    /*
     * public KeyBundle getKeyBundleFromKeyRing(String userId, ExtendedKeyStore
     * keyRing) throws IOException, FileNotFoundException, IllegalArgumentException,
     * KeyException, NoSuchAlgorithmException { throw new
     * NotImplementedException("Do not use it with Bouncy Castle. Method will be deleted."
     * ); }
     */

    /*
     * (non-Javadoc)
     * 
     * @see com.safelogic.pgp.apispecs.KeyHandler#exportAscPgpPrivateKey(java.lang.
     * String, char[])
     */
    public String exportAscPgpPrivateKey(String userId) throws IOException, FileNotFoundException,
	    IllegalArgumentException, KeyException, NoSuchAlgorithmException {

	if (userId == null) {
	    throw new IllegalArgumentException("User Id cannot be null");
	}

	PgpUserId pgpUserId = new PgpUserId(userId);
	String privKeyring = PgpUserId.getPrivKeyRingFilename();

	InputStream in = new FileInputStream(privKeyring);

	return getAscPgpPrivKey(userId, in);

    }

    public String getAscPgpPrivKey(String userId, InputStream in) throws KeyException {
	try {
	    PGPUtil.setDefaultProvider("BC");

	    in = PGPUtil.getDecoderStream(in);

	    PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(in);

	    //
	    // iterate through the key rings.
	    //
	    Iterator<?> rIt = pgpSec.getKeyRings();
	    boolean isGoodUserid = false;
	    String ascPgpPrivKey = null;
	    while (rIt.hasNext()) {
		PGPSecretKeyRing kRing = (PGPSecretKeyRing) rIt.next();
		Iterator<?> kIt = kRing.getSecretKeys();

		while (kIt.hasNext()) {
		    PGPSecretKey k = (PGPSecretKey) kIt.next();
		    Iterator<?> ituser = k.getUserIDs();
		    while (ituser.hasNext()) {
			String userIdInKeyRing = (String) ituser.next();
			if (userIdInKeyRing.contains(userId)) {
			    isGoodUserid = true;
			}
		    }
		    if (isGoodUserid) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			OutputStream os = new ArmoredOutputStream(bos);

			kRing.encode(os);
			os.close();
			ascPgpPrivKey = bos.toString();
			// System.out.println(ascPgpPrivKey);
			return ascPgpPrivKey;
		    }
		}
	    }

	    return ascPgpPrivKey;
	} catch (Exception e) {
	    // e.printStackTrace();
	    throw new KeyException(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.safelogic.pgp.apispecs.KeyHandler#exportAscPgpPublicKey(java.lang.String)
     */
    public String exportAscPgpPublicKey(String userId) throws IOException, FileNotFoundException,
	    IllegalArgumentException, KeyException, NoSuchAlgorithmException {
	if (userId == null) {
	    throw new IllegalArgumentException("Key Id cannot be null");
	}

	PgpUserId pgpUserId = new PgpUserId(userId); // Checks the userId format

	String pubKeyRingFile = PgpUserId.getPubKeyRingFilename();

	try {
	    InputStream in = new FileInputStream(pubKeyRingFile);

	    return getAscPgpPubKey(userId, in);

	} catch (PGPException e) {
	    throw new KeyException(e);
	}

    }

    public String getAscPgpPubKey(String userId, InputStream in) throws IOException, PGPException, KeyException {
	PGPUtil.setDefaultProvider("BC");

	in = PGPUtil.getDecoderStream(in);

	PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in);

	//
	// iterate through the key rings.
	//
	Iterator<?> rIt = pgpPub.getKeyRings();

	// BEWARE
	// This will contain AT FIRST ITERATION ONLY on PGPPublicKey.getUserIDs()
	// The userid name. Aka, the second iteration, there will be no value
	String userIdInKeyRing = null;

	// The armored key to export
	String armoredKey = null;

	while (rIt.hasNext()) {
	    PGPPublicKeyRing kRing = (PGPPublicKeyRing) rIt.next();
	    Iterator<?> kIt = kRing.getPublicKeys();

	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    OutputStream os = new ArmoredOutputStream(bos);

	    while (kIt.hasNext()) {
		PGPPublicKey pgpPubKey = (PGPPublicKey) kIt.next();
		Iterator<?> ituser = pgpPubKey.getUserIDs();

		while (ituser.hasNext()) {
		    userIdInKeyRing = (String) ituser.next();

		    debug("");
		    debug("User ID as param  : " + userId);
		    debug("User ID in keyring: " + userIdInKeyRing);

		}

		if (userIdInKeyRing.contains(userId)) {
		    // Ok, add the key to export armored string
		    // For 1) Master key AND 2) Encryption Key
		    debug("id Good User ID!");
		    pgpPubKey.encode(os);

		    // Finish all if we have done both master key & encryption key
		    if (pgpPubKey.isEncryptionKey()) {
			debug("is Encryption Key!");
			os.close();
			armoredKey = bos.toString();
			return armoredKey;
		    }
		}
	    }
	}

	if (armoredKey == null) {
	    // throw new IllegalArgumentException("Can't find encryption key in key ring."
	    // + " for User ID: " + userId);
	    throw new KeyException(Parms.NO_ENCRYPTION_KEY_FOR_ID);
	}

	// Will never be reached
	return armoredKey;
    }

    /**
     * Import and stores a PGP private key on disk from the armored private key
     * block contained in a file <br>
     * NOTE: the user Id is "contained" in the public key block. See implementation
     * for details
     * 
     * @param ascKey the the armored private key block
     * 
     * @throws NoSuchAlgorithmException
     * @throws KeyException
     * @throws IOException
     */
    public void importPgpPrivateKeyFromAsc(String ascKey) throws NoSuchAlgorithmException, KeyException, IOException {
	if (ascKey == null) {
	    throw new IllegalArgumentException("ascKey cannot be null");
	}

	if (!ascKey.startsWith(PgpTags.BEGIN_PGP_PRIVATE_KEY_BLOCK) && !ascKey.startsWith(PgpTags.BEGIN_PGP_MESSAGE)) {
	    throw new IllegalArgumentException("sAscKey is not a PGP Private Key:" + ascKey);
	}

	ascKey = addCrLfAtBeginOfBase646Lines(ascKey);

	String privKeyRingFile = PgpUserId.getPrivKeyRingFilename();

	PGPUtil.setDefaultProvider("BC");

	//
	// Read the private key rings
	//
	try {

	    InputStream in = new FileInputStream(privKeyRingFile);
	    in = PGPUtil.getDecoderStream(in);

	    PGPSecretKeyRingCollection secRings = new PGPSecretKeyRingCollection(in);

	    InputStream inKeyFile = new ByteArrayInputStream(ascKey.getBytes());

	    PGPSecretKeyRing pgpPrivKeyRing = readPrivateKeyRing(inKeyFile);
	    inKeyFile.close();

	    secRings = PGPSecretKeyRingCollection.addSecretKeyRing(secRings, pgpPrivKeyRing);

	    in.close(); // Do This before re-creating the file!
	    // inDecoderStream.close();

	    // Store back the stream
	    OutputStream fos = new FileOutputStream(privKeyRingFile);
	    secRings.encode(fos);
	    fos.close();

	} catch (PGPException e) {
	    // e.printStackTrace();
	    throw new KeyException(e);
	}
    }

    public PGPPrivateKey getPgpSecretKeyFromAsc(String keyBloc, String encryptedBody, char[] passphrase)
	    throws IOException, KeyException, PGPException {
	PGPEncryptedDataList enc = getPgpEncryptedDataList(encryptedBody);

	Iterator<?> it0 = enc.getEncryptedDataObjects();

	PGPPublicKeyEncryptedData pbe = null;
	List<Long> keyIds = new Vector<Long>();
	while (it0.hasNext()) {
	    pbe = (PGPPublicKeyEncryptedData) it0.next();

	    long keyId = pbe.getKeyID();

	    keyIds.add(keyId);
	}

	InputStream in = new ByteArrayInputStream(keyBloc.getBytes());
	PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(in));
	Iterator<?> it = pgpSec.getKeyRings();
	PGPPrivateKey pGPPrivateKey = null;
	// PGPSecretKey pgpSecKey = null;
	while (it.hasNext()) {
	    PGPSecretKeyRing pgpSecretKeyRing = (PGPSecretKeyRing) it.next();
	    Iterator<?> it2 = pgpSecretKeyRing.getSecretKeys();
	    while (it2.hasNext()) {
		PGPSecretKey pgpSecretKey = (PGPSecretKey) it2.next();

		if (keyIds.contains(pgpSecretKey.getKeyID())) {

		    // Add a try/catch block anyway
		    try {

			PBESecretKeyDecryptor decryptor = buildPBESecretKeyDecryptor(passphrase);
			pGPPrivateKey = pgpSecretKey.extractPrivateKey(decryptor);
			break;
		    } catch (Exception e) {

			e.printStackTrace();
			System.out.println("Break 2");
			break;
		    }
		}
	    }
	}
	return pGPPrivateKey;
    }

    /**
     * A simple routine that opens a private key key ring fileIn and loads it
     * 
     * @param in th input stream to read
     * @return
     * @throws IOException
     * @throws PGPException
     */
    private static PGPSecretKeyRing readPrivateKeyRing(InputStream in) throws IOException, PGPException {

	in = PGPUtil.getDecoderStream(in);

	PGPSecretKeyRingCollection pgpPriv = new PGPSecretKeyRingCollection(in);

	//
	// iterate through the key rings.
	//
	Iterator<?> rIt = pgpPriv.getKeyRings();
	// int i = 0;
	while (rIt.hasNext()) {
	    PGPSecretKeyRing kRing = (PGPSecretKeyRing) rIt.next();

	    return kRing;
	}

	return null;

    }

    /**
     * Import and stores a PGP public key on disk from the armored public key block
     * stored in a fileIn <br>
     * NOTE: the user Id is "contained" in the public key block. See implementation
     * for details
     * 
     * @param filePubKeyAsc the fileIn of the armored public key blok
     * @throws NoSuchAlgorithmException
     * @throws KeyException
     * @throws IOException
     */
    public boolean importPgpPublicAndPrivateKeyFromAsc(File filePubKeyAsc)
	    throws NoSuchAlgorithmException, KeyException, IOException {

	if (filePubKeyAsc == null) {
	    throw new IllegalArgumentException("sAscKey cannot be null");
	}

	if (!filePubKeyAsc.exists()) {
	    throw new FileNotFoundException("Asc Key file does not exists: " + filePubKeyAsc);
	}

	String ascKey = new FileUtil().getTextContent(filePubKeyAsc);

	boolean containsPublicKey = StringUtils.contains(ascKey, PgpTags.BEGIN_PGP_PUBLIC_KEY_BLOCK);
	boolean containsPrivateKey = StringUtils.contains(ascKey, PgpTags.BEGIN_PGP_PRIVATE_KEY_BLOCK);

	if (!containsPublicKey) {
	    return false;
	}

	// 1) Import the public key
	String ascPublic = StringUtils.substringBetween(ascKey, PgpTags.BEGIN_PGP_PUBLIC_KEY_BLOCK,
		PgpTags.END_PGP_PUBLIC_KEY_BLOCK);

	// System.out.println(":" + ascPublic + ":");

	if (ascPublic == null) {
	    return false;
	}

	ascPublic = PgpTags.BEGIN_PGP_PUBLIC_KEY_BLOCK + ascPublic + PgpTags.END_PGP_PUBLIC_KEY_BLOCK;

	importPgpPublicKeyFromAsc(ascPublic);

	// 2) If it exists: import the private key
	if (containsPrivateKey) {
	    String ascPrivate = StringUtils.substringBetween(ascKey, PgpTags.BEGIN_PGP_PRIVATE_KEY_BLOCK,
		    PgpTags.END_PGP_PRIVATE_KEY_BLOCK);

	    if (ascPrivate != null) {
		ascPrivate = PgpTags.BEGIN_PGP_PRIVATE_KEY_BLOCK + ascPrivate + PgpTags.END_PGP_PRIVATE_KEY_BLOCK;

		// System.out.println(ascPrivate);

		importPgpPrivateKeyFromAsc(ascPrivate);
	    }

	}

	return true;
    }

    /**
     * Stores a PGP public key on disk from the armored public key block contained
     * in a String
     * 
     * @param sAscKey the armored public key blok
     * @throws NoSuchAlgorithmException
     * @throws KeyException
     * @throws IOException
     */

    // 10/06/09 16:25 NDP - Clean close of streams in finally in
    // importPgpPublicKeyFromAsc()

    public void importPgpPublicKeyFromAsc(String sAscKey) throws NoSuchAlgorithmException, KeyException, IOException {

	if (sAscKey == null) {
	    throw new IllegalArgumentException("sAscKey cannot be null");
	}

	if (!sAscKey.startsWith(PgpTags.BEGIN_PGP_PUBLIC_KEY_BLOCK) && !sAscKey.startsWith(PgpTags.BEGIN_PGP_MESSAGE)) {
	    throw new IllegalArgumentException("sAscKey is not a PGP Public Key:" + sAscKey);
	}

	sAscKey = addCrLfAtBeginOfBase646Lines(sAscKey);

	String pubKeyRingFile = PgpUserId.getPubKeyRingFilename();

	PGPUtil.setDefaultProvider("BC");

	InputStream in = null;
	FileOutputStream fos = null;

	//
	// Read the private key rings
	//
	try {
	    in = new FileInputStream(pubKeyRingFile);
	    InputStream inDecoderStream = PGPUtil.getDecoderStream(in);

	    PGPPublicKeyRingCollection pubRings = new PGPPublicKeyRingCollection(inDecoderStream);

	    System.out.println(sAscKey);

	    InputStream is = new ByteArrayInputStream(sAscKey.getBytes());
	    PGPPublicKeyRing pgpPubKeyRing = readPublicKeyRing(is);
	    is.close();

	    if (pubRings == null)
		System.out.println("pubRings is null!");
	    if (pgpPubKeyRing == null)
		System.out.println("pgpPubKeyRing is null!");

	    pubRings = PGPPublicKeyRingCollection.addPublicKeyRing(pubRings, pgpPubKeyRing);

	    fos = new FileOutputStream(pubKeyRingFile);
	    pubRings.encode(fos);

	    inDecoderStream.close();

	    // in.close();
	    // fos.close();

	} catch (PGPException e) {
	    // e.printStackTrace();
	    throw new KeyException(e);
	} finally {
	    IOUtils.closeQuietly(in);
	    IOUtils.closeQuietly(fos);
	}
    }

    /**
     * Make sure that the FIRST BASE 64 has a blank line before
     * 
     * @param ascKey the ask key to format
     * @return the formated asc key
     * @throws IOException
     */
    private String addCrLfAtBeginOfBase646Lines(String ascKey) throws IOException {
	BufferedReader br = new BufferedReader(new StringReader(ascKey));

	String ascKeyNoComments = "";
	String line;

	boolean isBase64flow = false;

	while ((line = br.readLine()) != null) {
	    // if (! line.isEmpty())
	    if (line.length() != 0) {
		if (isBase64flow) {
		    ascKeyNoComments += line + FileUtil.CR_LF;
		} else {
		    try {
			// 02/04/10 18:10 NDP - Could not import key because missing line in
			// addCrLfAtBeginOfBase646Lines()
			com.safelogic.utilx.Base64.base64ToByteArray(line);

			ascKeyNoComments += FileUtil.CR_LF + line + FileUtil.CR_LF;
			isBase64flow = true;
		    } catch (Exception e) {
			ascKeyNoComments += line + FileUtil.CR_LF;
		    }
		}
	    }
	}

	// System.out.println(ascKeyNoComments);

	return ascKeyNoComments;

    }

    /**
     * A simple routine that opens a key ring fileIn and loads it
     * 
     * @param in
     * @return
     * @throws IOException
     * @throws PGPException
     */
    public static PGPPublicKeyRing readPublicKeyRing(InputStream in) throws IOException, PGPException {

	in = PGPUtil.getDecoderStream(in);

	PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in);
	//
	// iterate through the key rings.
	//

	// System.out.println("pgpPub.size(): " + pgpPub.size());

	Iterator<?> rIt = pgpPub.getKeyRings();

	while (rIt.hasNext()) {
	    PGPPublicKeyRing kRing = (PGPPublicKeyRing) rIt.next();
	    return kRing;
	}

	return null;
    }

    /**
     * Displays the specified message if the DEBUG flag is set.
     * 
     * @param sMsg the debug message to display
     */

    protected void debug(String sMsg) {
	if (DEBUG)
	    System.out.println("DBG> " + sMsg);
    }

    // 13/10/09 16:35 NDP - Add IllegalAccessException() in isPassphraseValid for
    // strong crypto diagnosis

    public boolean isPassphraseValid(String userId, char[] passphrase) throws IOException, FileNotFoundException,
	    IllegalArgumentException, KeyException, NoSuchAlgorithmException, IllegalAccessException {
	// Check parameter validity
	if (userId == null) {
	    throw new IllegalArgumentException("Key Id cannot be null");
	}
	if (passphrase == null) {
	    throw new IllegalArgumentException("Passphrase cannot be null");
	}
	debug("" + new Date());

	try {
	    // Try to get the private key of the given userid
	    PgeepPrivateKey pgeepPrivKey = (PgeepPrivateKey) this.getPgpPrivateKey(userId, null, passphrase);
	    PGPSecretKey pgpSecretKey = pgeepPrivKey.getPGPSecretKey();

	    PBESecretKeyDecryptor decryptor = buildPBESecretKeyDecryptor(passphrase);
	    PGPPrivateKey pgpPrivKey = pgpSecretKey.extractPrivateKey(decryptor);
//	} catch (NoSuchProviderException e) {
//	    throw new KeyException(e);
//	} 
	}
	catch (PGPException e) {
	    debug("" + new Date());
	    String stackTrace = Debug.GetStackTrace(e);

	    // Check if exception throw because of a problem on passphrase
	    if (stackTrace.indexOf("Exception constructing key") != -1) {
		// Passphrase invalid return false
		return false;
	    } else if (stackTrace.indexOf("Exception decrypting key") != -1) {

		// 01/10/07 17:45 NDP - Trap exception in isPassphraseValid() because of missing
		// Policy Files
		// Check if exception throw because of a problem of policy files
		// aka ==> forbidden to use long keys
		// Display a message saying it!
		MessagesManager messages = new MessagesManager();
		String errorMessage = messages.getMessage("STRONG_ENCRYPTION_ADMIN_ONLY");

		throw new IllegalAccessException(errorMessage);
	    } else if (stackTrace.indexOf("checksum mismatch") != -1) {
		return false;
	    } else {
		// an other error occurs : throw a key exception
		throw new KeyException("Unable to check passphrase : " + e);
	    }
	}

	debug("" + new Date());
	// Everything worked : passphrase was correct.
	return true;
    }

    public int deletePubKeyFromKeyRing(String pubKeyId)
	    throws NoSuchAlgorithmException, KeyException, IOException, KeyStoreException {
	if (pubKeyId == null) {
	    throw new IllegalArgumentException("User Id cannot be null");
	}

	PgpUserId pgpUserId = new PgpUserId(pubKeyId); // Checks the userId format

	String pubKeyRingFile = PgpUserId.getPubKeyRingFilename();

	try {
	    // Key may not be an email
	    EmailChecker emailChecker = new EmailChecker(pubKeyId);

	    if (emailChecker.isSyntaxValid()) {
		pubKeyId = CsvEmailExtractor.getEmailString(pubKeyId);
	    } else {
		// pubKeyId must not be extracted from the <> and kept as original
	    }
	} catch (Exception e1) {
	    e1.printStackTrace();
	}

	// PublicKey publicKey = null;

	PGPUtil.setDefaultProvider("BC");

	FileInputStream in = null;

	try {
	    //
	    // Read the private key rings
	    //

	    // PGPPublicKeyRingCollection pubRings
	    // = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(
	    // new FileInputStream(pubKeyRingFile)));

	    in = new FileInputStream(pubKeyRingFile);
	    InputStream inDecoderStream = PGPUtil.getDecoderStream(in);

	    PGPPublicKeyRingCollection pubRings = new PGPPublicKeyRingCollection(inDecoderStream);

	    Iterator<?> rIt = pubRings.getKeyRings();

	    while (rIt.hasNext()) {
		PGPPublicKeyRing pgpPubKeyRing = (PGPPublicKeyRing) rIt.next();

//				long        pgpKeyID = 0;
//				PublicKey   pKey = null;

		Iterator<?> it = pgpPubKeyRing.getPublicKeys();

		while (it.hasNext()) {
		    PGPPublicKey pgpKey = (PGPPublicKey) it.next();

		    Iterator<?> ituser = pgpKey.getUserIDs();

		    while (ituser.hasNext()) {
			String userIdInKeyRing = (String) ituser.next();

			// System.out.println(userIdInKeyRing);

			if (userIdInKeyRing.contains(pubKeyId)) {
			    pubRings = PGPPublicKeyRingCollection.removePublicKeyRing(pubRings, pgpPubKeyRing);

			    FileOutputStream fos = new FileOutputStream(pubKeyRingFile);
			    pubRings.encode(fos);

			    inDecoderStream.close();
			    in.close();
			    fos.close();

			    return CmPgpCodes.KEY_REMOVED;
			}
		    }
		}
	    }
	} catch (PGPException e) {
	    throw new KeyException(e);
	}

	return CmPgpCodes.KEY_NOT_FOUND;

    }

    /**
     * Retrieves from the PGP Public Key Keyring a key User ID using the PGP Key ID
     * as the search parameter
     * 
     * @param keyId the pGP Key ID in long format
     * @return the corresponding PGP User ID
     * 
     * @throws IOException
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     * @throws KeyException
     * @throws NoSuchAlgorithmException
     */
    public String getSubkeyUserId(long keyId) throws IOException, FileNotFoundException, IllegalArgumentException,
	    KeyException, NoSuchAlgorithmException {
	String keyring = PgpUserId.getPubKeyRingFilename();

	PGPPublicKey pubKey = null;
//		PrivateKey privKey = null;

	PGPUtil.setDefaultProvider("BC");

	try {
	    //
	    // Read the public key rings
	    //
	    PGPPublicKeyRingCollection pubRings = new PGPPublicKeyRingCollection(
		    PGPUtil.getDecoderStream(new FileInputStream(keyring)));

	    Iterator<?> rIt = pubRings.getKeyRings();

	    while (rIt.hasNext()) {
		PGPPublicKeyRing pgpPub = (PGPPublicKeyRing) rIt.next();

		try {
		    pubKey = pgpPub.getPublicKey();
		} catch (Exception e) {
		    e.printStackTrace();
		    continue;
		}

//				long        pgpKeyID = 0;
//				PublicKey   pKey = null;

		Iterator<?> it = pgpPub.getPublicKeys();
		boolean first = true;

		// System.out.println("");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		OutputStream os = new ArmoredOutputStream(bos);

		String userId = null;

		while (it.hasNext()) {
		    PGPPublicKey pgpKey = (PGPPublicKey) it.next();

		    Iterator<?> ituser = pgpKey.getUserIDs();

		    while (ituser.hasNext()) {
			userId = (String) ituser.next();
		    }

		    if (first) {
			first = false;
		    } else {
			//
			// This is the key id we wan't!
			//
			debug("Passed Key Id: " + keyId);
			debug("Key Id       : " + pgpKey.getKeyID() + " (subkey)");
			debug("userId       : " + userId + " (subkey)");

			if (pgpKey.getKeyID() == keyId) {
			    return userId;
			}
		    }

		}

		os.close();

	    }
	} catch (PGPException e) {
	    throw new KeyException(e);
	}

	return null;
    }

    public void changePassphrase(String userId, char[] old_passphrase, char[] new_passphrase)
	    throws FileNotFoundException, IllegalArgumentException, KeyException, NoSuchAlgorithmException, IOException,
	    NoSuchProviderException, PGPException {
	try {
	    FileInputStream in = new FileInputStream(PgpUserId.getPrivKeyRingFilename());
	    FileOutputStream os = new FileOutputStream(PgpUserId.getPrivKeyRingFilename() + ".tmp");

	    changePassphrase(in, os, old_passphrase, new_passphrase);

	    in.close();
	    in = new FileInputStream(PgpUserId.getPrivKeyRingFilename() + ".tmp");
	    os = new FileOutputStream(PgpUserId.getPrivKeyRingFilename());
	    StreamCopier sc = new StreamCopier();
	    sc.copyAndClose(in, os);
	    File f = new File(PgpUserId.getPrivKeyRingFilename() + ".tmp");
	    FileUtils.forceDelete(f);

	} catch (PGPException e) {
	    e.printStackTrace();
	    throw new KeyException(e);
	}
    }

    public void changePassphrase(InputStream in, OutputStream os, char[] old_passphrase, char[] new_passphrase)
	    throws IOException, PGPException, NoSuchProviderException {
	PGPSecretKeyRingCollection secRings = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(in));

	Iterator<?> rIt = secRings.getKeyRings();
	// int rItIndex = 0;
	while (rIt.hasNext()) {
	    PGPSecretKeyRing pgpSec = (PGPSecretKeyRing) rIt.next();

	    Iterator<?> it = pgpSec.getSecretKeys();

	    while (it.hasNext()) {
		PGPSecretKey pgpKey = (PGPSecretKey) it.next();
		try {
		    PBESecretKeyDecryptor decryptor = buildPBESecretKeyDecryptor(old_passphrase);
		    PBESecretKeyEncryptor encryptor = buildPBESecretKeyEncryptor(
			    pgpKey.getKeyEncryptionAlgorithm(), new_passphrase);
		    pgpKey = PGPSecretKey.copyWithNewPassword(pgpKey, decryptor, encryptor);
		} catch (Exception e) {
		    // Wrong key ==> do not change the key and try the next one!
		}
		pgpKey.encode(os);
	    }
	}
	os.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.safelogic.pgp.apispecs.KeyHandler#getSecretKeyIdsFromFile(File)
     */

    public Map<String, String> getSecretKeyIdsFromFile(File keyFile)
	    throws FileNotFoundException, IOException, PGPException {
	PGPUtil.setDefaultProvider("BC");
	Map<String, String> userIds = new HashMap<String, String>();

	InputStream in = new FileInputStream(keyFile);
	in = PGPUtil.getDecoderStream(in);
	PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(in);

	PGPSecretKey key = null;

	//
	// iterate through the key rings.
	//
	Iterator<?> rIt = pgpSec.getKeyRings();
	// boolean isGoodUserid = false;

	while (rIt.hasNext()) {
	    PGPSecretKeyRing kRing = (PGPSecretKeyRing) rIt.next();
	    Iterator<?> kIt = kRing.getSecretKeys();

	    while (kIt.hasNext()) {
		// Get current secret key
		PGPSecretKey k = (PGPSecretKey) kIt.next();

		Iterator<?> ituser = k.getUserIDs();

		// Get public key
		PGPPublicKey pubKey = k.getPublicKey();
		byte[] hash = pubKey.getFingerprint();

		String fingerprint = Hex.toString(hash);
		fingerprint = "0x" + fingerprint.substring(32);
		;

		// Get key id
		while (ituser.hasNext()) {
		    String userIdInKeyRing = (String) ituser.next();
		    // debug("User ID as param : " + userId);
		    // debug("User ID in keyring: " + userIdInKeyRing);
		    userIds.put(userIdInKeyRing, fingerprint);
		}
		if (k.isSigningKey()) {
		    key = k;
		}
	    }
	}

	if (key == null) {
	    throw new IllegalArgumentException("Can't find signing key in key ring.");
	}
	return userIds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.safelogic.pgp.apispecs.KeyHandler#storeKeyRing(File, File, String)
     */

    public void storeKeyRing(File privKeyFile, File pubKeyFile, String userId, boolean add)
	    throws FileNotFoundException, IOException, PGPException {

	InputStream inSec = new FileInputStream(privKeyFile);
	InputStream inPub = new FileInputStream(pubKeyFile);

	InputStream inDecoderStreamSec = PGPUtil.getDecoderStream(inSec);
	InputStream inDecoderStreamPub = PGPUtil.getDecoderStream(inPub);

	// Secret key ring part
	PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(inDecoderStreamSec);
	PgpUserId pgpUserId = new PgpUserId(userId);
	Iterator<?> rIt = pgpSec.getKeyRings();

	PGPSecretKey key = null;

	// Look for userId in keyRing
	while (key == null && rIt.hasNext()) {
	    PGPSecretKeyRing kRing = (PGPSecretKeyRing) rIt.next();
	    Iterator<?> kIt = kRing.getSecretKeys();
	    boolean isGoodUserId = false;
	    while (key == null && kIt.hasNext()) {
		// Get current secret key
		PGPSecretKey k = (PGPSecretKey) kIt.next();

		Iterator<?> ituser = k.getUserIDs();

		// Get key id
		while (ituser.hasNext()) {

		    String userIdInKeyRing = (String) ituser.next();
		    if (userIdInKeyRing.equals(userId)) {
			isGoodUserId = true;
			break;
		    }
		}
	    }
	    if (!isGoodUserId) {
		// Remove userIds not wanted
		pgpSec = PGPSecretKeyRingCollection.removeSecretKeyRing(pgpSec, kRing);
	    } else {
		isGoodUserId = false;
	    }
	}
	// Public key part
	PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(inDecoderStreamPub);

	if (!add) {
	    File secretKeyRing = new File(PgpUserId.getPrivKeyRingFilename());
	    FileOutputStream osSecretKeyRing = new FileOutputStream(secretKeyRing);
	    pgpSec.encode(osSecretKeyRing);

	    inSec.close();
	    osSecretKeyRing.close();

	} else {
	    File secKeyring = new File(PgpUserId.getPrivKeyRingFilename());
	    InputStream in = new FileInputStream(secKeyring);
	    in = PGPUtil.getDecoderStream(in);
	    PGPSecretKeyRingCollection existingPgpSec = new PGPSecretKeyRingCollection(in);
	    Iterator<?> secKeys = pgpSec.getKeyRings();

	    while (secKeys.hasNext()) {
		try {
		    existingPgpSec = PGPSecretKeyRingCollection.addSecretKeyRing(existingPgpSec,
			    (PGPSecretKeyRing) secKeys.next());
		} catch (IllegalArgumentException e) {
		    if (e.getMessage().indexOf("Collection already contains") != -1)
			return;
		}
	    }

	    in.close();

	    OutputStream out = new FileOutputStream(secKeyring);
	    existingPgpSec.encode(out);
	}

	// Look for public key of userId
	Iterator<?> rIt2 = pgpPub.getKeyRings();
	PGPPublicKey pkey = null;
	while (pkey == null && rIt2.hasNext()) {
	    PGPPublicKeyRing kRing = (PGPPublicKeyRing) rIt2.next();
	    Iterator<?> kIt = kRing.getPublicKeys();
	    boolean isGoodUserId = false;
	    while (pkey == null && kIt.hasNext()) {
		// Get current public key
		PGPPublicKey k = (PGPPublicKey) kIt.next();

		Iterator<?> ituser = k.getUserIDs();
		while (ituser.hasNext()) {
		    String userIdInKeyRing = (String) ituser.next();
		    if (userIdInKeyRing.equals(userId)) {
			isGoodUserId = true;
			break;
		    }
		}
	    }
	    if (!isGoodUserId) {
		// Remove other keys
		pgpPub = PGPPublicKeyRingCollection.removePublicKeyRing(pgpPub, kRing);
	    } else {
		isGoodUserId = false;
	    }

	}

	if (!add) {
	    File publicKeyRing = new File(PgpUserId.getPubKeyRingFilename());
	    FileOutputStream osPublicKeyRing = new FileOutputStream(publicKeyRing);

	    pgpPub.encode(osPublicKeyRing);

	    osPublicKeyRing.close();
	    inPub.close();
	} else {
	    File pubKeyring = new File(PgpUserId.getPubKeyRingFilename());
	    InputStream in = new FileInputStream(pubKeyring);
	    in = PGPUtil.getDecoderStream(in);
	    PGPPublicKeyRingCollection existingPgpPub = new PGPPublicKeyRingCollection(in);
	    Iterator<?> pubKeys = pgpPub.getKeyRings();
	    while (pubKeys.hasNext()) {
		existingPgpPub = PGPPublicKeyRingCollection.addPublicKeyRing(existingPgpPub,
			(PGPPublicKeyRing) pubKeys.next());
	    }
	    in.close();
	    OutputStream out = new FileOutputStream(new File(PgpUserId.getPubKeyRingFilename()));
	    existingPgpPub.encode(out);
	    out.flush();
	    out.close();
	}
    }

    /**
     * See KeyHandler for description
     */
    public List<String> listPrivKeyIds() throws FileNotFoundException, IOException, KeyException {
	List<String> ids = new Vector<String>();
	// Get priv keyring file
	File secKeyring = new File(PgpUserId.getPrivKeyRingFilename());
	InputStream in = new FileInputStream(secKeyring);

	in = PGPUtil.getDecoderStream(in);

	try {
	    PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(in);
	    // List all key ids
	    Iterator<?> rIt = pgpSec.getKeyRings();
	    while (rIt.hasNext()) {
		PGPSecretKeyRing kRing = (PGPSecretKeyRing) rIt.next();
		Iterator<?> kIt = kRing.getSecretKeys();

		while (kIt.hasNext()) {
		    PGPSecretKey k = (PGPSecretKey) kIt.next();

		    Iterator<?> ituser = k.getUserIDs();
		    while (ituser.hasNext()) {
			String userIdInKeyRing = (String) ituser.next();
			ids.add(userIdInKeyRing);
		    }
		}

	    }
	} catch (PGPException e) {
	    throw new KeyException(e);
	}
	return ids;
    }

    /**
     * Returns the default priv key id
     * 
     * @return A string containing the default priv key id
     * @throws FileNotFoundException
     * @throws IOException
     * @throws KeyException
     */
    public String getDefaultKey() throws FileNotFoundException, IOException, KeyException {
	String defaultKeyId = null;
	// list priv keys of keyring
	List<String> keys = this.listPrivKeyIds();

	if (keys.size() == 1) {
	    // only one priv key in keyring => this is the default key
	    // used to handle compatibilty with previous versions
	    defaultKeyId = keys.get(0);
	} else {
	    // more than one key in keyring look for default key
	    UserPreferencesManager prefs = new UserPreferencesManager();
	    defaultKeyId = UserPreferencesManager.getDefaultKey();

	    if (defaultKeyId == null) {
		defaultKeyId = keys.get(0);
		return defaultKeyId;
	    }

	    try {
		if (!existsInPrivKeyRing(defaultKeyId)) {
		    defaultKeyId = keys.get(0);
		    return defaultKeyId;
		}
	    } catch (IllegalArgumentException e) {
		throw new KeyException(e);
	    } catch (NoSuchAlgorithmException e) {
		throw new KeyException(e);
	    }
	}

	return defaultKeyId;
    }

    /**
     * Tell if key is the default private key for user
     * 
     * @param id Key id
     * @return true if passed key id is the default key false otherwise
     */
    /*
     * private boolean isDefaultKey(String id) { boolean isDefault = false;
     * UserPreferencesManager prefs = new UserPreferencesManager();
     * if(id.equals(UserPreferencesManager.getDefaultKey())) { isDefault = true; }
     * return isDefault; }
     */

    public int deleteKeyPair(String keyId)
	    throws IOException, KeyException, NoSuchAlgorithmException, KeyStoreException {

	int result = removePrivKey(keyId);
	if (result != CmPgpCodes.KEY_NOT_REMOVED)
	    result = this.deletePubKeyFromKeyRing(keyId);

	return result;

    }

    private int removePrivKey(String keyId) throws FileNotFoundException, IOException, KeyException {
	File secKeyring = new File(PgpUserId.getPrivKeyRingFilename());
	InputStream in = new FileInputStream(secKeyring);

	in = PGPUtil.getDecoderStream(in);
	try {
	    PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(in);
	    in.close();
	    Iterator<?> rIt = pgpSec.getKeyRings();
	    while (rIt.hasNext()) {
		PGPSecretKeyRing kRing = (PGPSecretKeyRing) rIt.next();
		Iterator<?> kIt = kRing.getSecretKeys();

		while (kIt.hasNext()) {
		    PGPSecretKey k = (PGPSecretKey) kIt.next();

		    Iterator<?> ituser = k.getUserIDs();
		    while (ituser.hasNext()) {
			String userIdInKeyRing = (String) ituser.next();
			if (userIdInKeyRing.equals(keyId)) {
			    pgpSec = PGPSecretKeyRingCollection.removeSecretKeyRing(pgpSec, kRing);
			    OutputStream out = new FileOutputStream(secKeyring);
			    pgpSec.encode(out);
			    out.close();
			    return CmPgpCodes.KEY_REMOVED;
			}
		    }
		}
	    }
	    return CmPgpCodes.KEY_NOT_FOUND;
	} catch (PGPException e) {
	    throw new KeyException(e);
	}
    }

    /**
     * 
     * Test if a key is revoked.
     * 
     * @param userId the PGP Public key user id to check
     * @return true if the public key is revoked
     * 
     * @throws FileNotFoundException
     * @throws KeyException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public boolean isPublicKeyRevoked(String userId)
	    throws FileNotFoundException, KeyException, IOException, NoSuchAlgorithmException {
	PgeepPublicKey pgeepPublicKey = (PgeepPublicKey) this.getPgpPublicKey(userId);
	PubkeyDescriptor pubkeyDescriptor = new PubkeyDescriptorOne(pgeepPublicKey);
	return pubkeyDescriptor.isRevoked();
    }

    /**
     * 
     * Test if a key is expired.
     * 
     * @param userId the PGP Public key user id to check
     * @return true if the public key is expired
     * 
     * @throws FileNotFoundException
     * @throws KeyException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public boolean isPublicKeyExpired(String userId)
	    throws FileNotFoundException, KeyException, IOException, NoSuchAlgorithmException {
	PgeepPublicKey pgeepPublicKey = (PgeepPublicKey) this.getPgpPublicKey(userId);
	PubkeyDescriptor pubkeyDescriptor = new PubkeyDescriptorOne(pgeepPublicKey);
	return pubkeyDescriptor.isExpired();
    }

    /**
     * Return true if the key is valid (non revoked, non expired)
     * 
     * @param keyAsc the key in asc format
     * @return true if the key is valid (non revoked, non expired) else false
     */
    public boolean isPublicKeyAsAscValid(String keyAsc) throws IOException, FileNotFoundException,
	    IllegalArgumentException, KeyException, NoSuchAlgorithmException {
	// Test if the key is still valid (non expired & non revoked)
	ByteArrayInputStream in = new ByteArrayInputStream(keyAsc.getBytes());
	PublicKey pubKey = this.getPgpPublicKeyFromAsc(in);

	PubkeyDescriptor pubkeyDescriptor = new PubkeyDescriptorOne(pubKey);

	if (pubkeyDescriptor.isExpired() || pubkeyDescriptor.isRevoked()) {
	    return false;
	} else {
	    return true;
	}
    }

    private static PGPEncryptedDataList getPgpEncryptedDataList(String strEncrypted)
	    throws IOException, UnsupportedEncodingException {
	InputStream fIn = new ByteArrayInputStream(strEncrypted.getBytes());

	ArmoredInputStream aIn = new ArmoredInputStream(fIn);

	InputStream fInDecoder = PGPUtil.getDecoderStream(aIn);

	PGPObjectFactory pgpF = new PGPObjectFactory(fInDecoder);
	PGPEncryptedDataList enc;

	Object o = null;

	try {
	    o = pgpF.nextObject();
	} catch (IOException e) {
	    throw new UnsupportedEncodingException(e.toString());
	}

	//
	// the first object might be a PGP marker packet.
	//
	if (o instanceof PGPEncryptedDataList) {
	    enc = (PGPEncryptedDataList) o;
	} else {
	    enc = (PGPEncryptedDataList) pgpF.nextObject();
	}
	return enc;
    }


}

//End
