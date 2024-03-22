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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPUtil;

import com.safelogic.pgp.api.BooleanContainer;
import com.safelogic.pgp.api.KeyHandlerOne;
import com.safelogic.pgp.api.PgeepPrivateKey;
import com.safelogic.pgp.api.PgeepPublicKey;
import com.safelogic.pgp.api.PgpActionsOne;
import com.safelogic.pgp.api.PgpFileStatusOne;
import com.safelogic.pgp.api.engines.CryptoEngine;
import com.safelogic.pgp.api.util.crypto.CgeepTagArmoredOutputStream;
import com.safelogic.pgp.api.util.crypto.PgpUserId;
import com.safelogic.pgp.api.util.msg.MessagesManager;
import com.safelogic.pgp.api.util.parms.CmPgpCodes;
import com.safelogic.pgp.api.util.parms.PgpTags;
import com.safelogic.pgp.apispecs.KeyHandler;
import com.safelogic.pgp.apispecs.PgpActions;
import com.safelogic.pgp.apispecs.PgpFileStatus;
import com.safelogic.pgp.util.CharsetToolkit;
import com.safelogic.pgp.util.UserPreferencesManager;
import com.safelogic.pgp.util.Util;
import com.safelogic.utilx.Debug;

import net.safester.application.parms.Parms;

/**
 * @author Nicolas
 * Special decryption class for PDF documents: pass the decrypted stream in order to recencrypt the
 * PDF doc with thre user passphrase.
 *
 */
public class PgpActionsOnePdf implements PgpActions {

    private static final int BUFFER_SIZE_4096 = 4096;
    private static final int BUFFER_SIZE_8192 = 8192;
    private static final int BUFFER_SIZE_16384 = 16384;
    private static final int BUFFER_SIZE_32768 = 32768;
    private static final int BUFFER_SIZE_BIG = 1 << 16;

    /**
     * The buffer default size
     */
    private static final int BUFFER_SIZE = BUFFER_SIZE_BIG;

    /**
     * Number of bytes before the progress bas is refreshed
     */
    private static final int BYTES_REFRESH_PROGRESS_BAR = 1000000;

    /**
     * If true, all files with be encrypted and/or signed with armored format
     * true is the best solution for email sending/receptions
     */
    private boolean armorMode = false;

    private static String CR_LF = System.getProperty("line.separator");

    /**
     * The debug flag
     */
    protected boolean DEBUG = Debug.isSet(this);

    /**
     * The calling/owner thread
     */
    private CryptoEngine m_owner = null;

    /**
     * The last PGP Public Key that signed a verified message
     */
    private PgeepPublicKey lastSignPublicKey = null;

    /**
     * The files size - is used by the Progress indicator
     */
    private long filesLength = 0;

    /**
     * Messages for I18N
     */
    private MessagesManager messages = new MessagesManager();

    /**
     * The encoding charset to use for text encryption
     */
    private String encodingCharset = null;

    /**
     * To be set to say if the signature is OK for a decrypted String
     */
    public boolean stringSignatureVerified = false;

    private boolean integrityCheck = false;

    /**
     * Defaut constructor.
     */
    public PgpActionsOnePdf() {
        // NO! LICENSE GENERATION FAIL ON WE BSERVER        
        // LookAndFeelMgr.setDefault(null);

        Security.addProvider(new BouncyCastleProvider());
        encodingCharset = UserPreferencesManager.getUserEncoding();
    }

    /**
     * Constructor to be called when a Progress Monitor
     *
     * @param owner
     */
    public PgpActionsOnePdf(CryptoEngine owner) {
        this();
        this.m_owner = owner;
    }

    /**
     * @param filesLength the total files Size
     */
    public void setFilesLength(long filesLength) {
        this.filesLength = filesLength;
    }

    /**
     * @return the Last cGeep Public Key that signed a verified message
     */
    public PgeepPublicKey getLastSignPublicKey() {
        return lastSignPublicKey;
    }

    /**
     * Set th owner current value for progression bar
     *
     * @param current
     */
    private void setOwnerCurrent(int current) {
        if (m_owner != null) {
            m_owner.setCurrent(current);
        }
    }

    /**
     * Set th owner current value for progression bar
     *
     * @param current
     */
    private void addOneOwnerCurrent() {
        if (m_owner != null) {
            int current = m_owner.getCurrent();
            current++;

            // Do no force interruption
            if (current < CryptoEngine.MAXIMUM_PROGRESS) {
                m_owner.setCurrent(current);
            }

            debug("current: " + current);
        }
    }

    /**
     * Set the owner current note for progression bar
     *
     * @param current
     */
    private void setOwnerNote(String note) {
        if (m_owner != null) {
            m_owner.setNote(note);
        }
    }

    /**
     * @return true is there is an owner AND it's interrupted
     */
    private boolean isOwnerInterrupted() {
        if (m_owner == null) {
            return false;
        }

        // 08/10/08 17:10 NDP - use non static method in isOwnerInterrupted() for APIs
        //return ((Thread)m_owner).interrupted();
        return ((Thread) m_owner).isInterrupted();
    }

    /**
     * @param armorMode if true, the encrypted file will be PGP armored
     */
    public void setArmorMode(boolean armorMode) {
        this.armorMode = armorMode;
    }

    /**
     * Signal that the file operation is terminated (to be used in cGeepApi)
     */
    public void setMaximumProgress() {
        m_owner.setCurrent(CryptoEngine.MAXIMUM_PROGRESS);
    }

    /* (non-Javadoc)
	 * @see com.safelogic.pgp.apispecs.PgpActions#encryptPgp(java.io.InputStream, java.util.List)
     */
    public void encryptPgp(File inFile, File outFile, List<String> publicKeysId)
            throws IOException, FileNotFoundException,
            IllegalArgumentException, KeyException, Exception {

        //Verify argument validity
        if (inFile == null) {
            throw new IllegalArgumentException("Input fileIn cannot be null");
        }

        if (outFile == null) {
            throw new IllegalArgumentException("Output fileIn cannot be null");
        }

        if (publicKeysId == null || publicKeysId.size() < 1) {
            throw new IllegalArgumentException("public key list cannot be null");
        }

        List<PGPPublicKey> pgpPublicKeys = new Vector<PGPPublicKey>();

        for (int i = 0; i < publicKeysId.size(); i++) {
            String publicKeyid = (String) publicKeysId.get(i);
            KeyHandler kh = new KeyHandlerOne();

            PgeepPublicKey pubKey = (PgeepPublicKey) kh.getPgpPublicKeyForEncryption(publicKeyid);
            PGPPublicKey pgpPubkey = pubKey.getKey();

            debug("pgpPubkey.getKeyID(): " + pgpPubkey.getKeyID() + " " + publicKeyid);

            //            if (DEBUG)
            //            {
            //                JOptionPane.showMessageDialog(null, publicKeyid);
            //            }
            pgpPublicKeys.add(pgpPubkey);

        }

        encryptFilePgp(inFile, outFile, pgpPublicKeys);

    }

    public void encryptFilePgp(File inFile, File outFile, List<PGPPublicKey> pgpPublicKeys)
            throws NoSuchProviderException,
            PGPException, FileNotFoundException, IOException,
            InterruptedException, IllegalArgumentException {

        //Verify argument validity
        if (inFile == null) {
            throw new IllegalArgumentException("Input fileIn cannot be null");
        }

        if (outFile == null) {
            throw new IllegalArgumentException("Output fileIn cannot be null");
        }

        if (pgpPublicKeys == null || pgpPublicKeys.size() < 1) {
            throw new IllegalArgumentException("public key list cannot be null");
        }

        // No integrity check for now
        boolean withIntegrityCheck = this.integrityCheck;

        PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(PGPEncryptedData.CAST5,
                withIntegrityCheck,
                new SecureRandom(),
                "BC");
        for (PGPPublicKey pgpPublicKey : pgpPublicKeys) {
            cPk.addMethod(pgpPublicKey);
        }
        OutputStream fOut = new FileOutputStream(outFile);

        OutputStream armOut = null;

        if (armorMode) {
            armOut = new CgeepTagArmoredOutputStream(fOut);
        } else {
            armOut = fOut;
        }

        OutputStream cOut = cPk.open(armOut, new byte[1 << 16]);

        BufferedOutputStream out = new BufferedOutputStream(armOut);

        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(
                PGPCompressedData.ZIP);

        // The file name to use and store in the PGP stream is the raw input file name
        String fileNameToUse = inFile.getName();

        try {
            writeFileToLiteralData(comData.open(cOut),
                    PGPLiteralData.BINARY,
                    inFile,
                    fileNameToUse,
                    //new byte[1 << 16]);
                    new byte[BUFFER_SIZE_4096]);
        } catch (InterruptedException e) {
            comData.close();
            cPk.close();
            cOut.close();
            out.flush();
            out.close();
            throw e;
        }

        comData.close();
        cPk.close();
        cOut.close();
        out.flush();
        out.close();

        /*
        if (false)
        {
            InputStream in2 = new FileInputStream(outFile);
            OutputStream out2 = new FileOutputStream(outFile + ".asc");

            ArmoredOutputStream armOs = new ArmoredOutputStream(out2);

            StreamCopier sc = new StreamCopier();
            sc.copyAndClose(in2, armOs);
        }
         */
    }

    /**
     * write out the passed in fileIn as a literal data packet in partial packet
     * format.
     *
     * @param out
     * @param fileIn the in file name
     * @param fileType the LiteralData type for the fileIn.
     * @param fileNameToUse The file name to use and embed inside the PGP output
     * strem
     * @param buffer buffer to be used to chunk the fileIn into partial packets.
     *
     * @throws IOException
     */
    private void writeFileToLiteralData(
            OutputStream out,
            char fileType,
            File file,
            String fileNameToUse,
            byte[] buffer)
            throws IOException, InterruptedException {
        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();

        //OutputStream          pOut = lData.open(out, fileType, fileIn.getName(), new Date(fileIn.lastModified()), buffer);        
        //FileInputStream       in = new FileInputStream(fileIn);
        //OutputStream            pOut = lData.open(out, fileType, file.getName(), new Date(file.lastModified()), buffer);        
        OutputStream pOut = lData.open(out, fileType, fileNameToUse, new Date(file.lastModified()), buffer);

        BufferedOutputStream pBufOut = new BufferedOutputStream(pOut);

        BufferedInputStream fIn = new BufferedInputStream(new FileInputStream(file));

        byte[] buf = new byte[buffer.length];
        int len;

        setOwnerNote(messages.getMessage("progress_crypt_file") + " " + file.getName()); // + " ==> " + fileIn.getName() + ".pgeep");
        //     addOneOwnerCurrent();

        // For ProgressMonitor 
        int tempLen = 0;

        System.out.println("filesLength: " + filesLength);

        long treated = 0;
        while ((len = fIn.read(buf)) > 0) {
            treated += len;
            tempLen += len;
            if (tempLen > filesLength / CryptoEngine.MAXIMUM_PROGRESS) {
                tempLen = 0;
                Thread.sleep(10); // TODO: adjust sleep time depending on fileIn length                    
                addOneOwnerCurrent();  // For ProgressMonitor progress bar 
            }

            if (isOwnerInterrupted()) {
                pBufOut.flush();
                pBufOut.close();

                pOut.close();
                lData.close();
                fIn.close();
                throw new InterruptedException();
            }

            pBufOut.write(buf, 0, len);
        }

        //System.out.println("treated: " + treated);
        //System.out.println("owner current: " + m_owner.getCurrent());
        pBufOut.flush();
        pBufOut.close();

        pOut.close();
        lData.close();
        fIn.close();

    }

    public int decryptPgp(File fileIn, File fileOut, String privKeyId, char[] passphrase)
            throws IOException, FileNotFoundException, IllegalArgumentException,
            KeyException, Exception {
        if (privKeyId == null) {
            throw new IllegalArgumentException("Private key cannot be null");
        }

        // Pass null for privKeyBlock because we will use the local keyring file for key extracttion
        int rc = decryptPgpWithPrivateKeyStream(fileIn, fileOut, null, passphrase);
        return rc;
    }

    /**
     * Private method that allow to decrypt a file using a private key stream
     *
     * @param fileIn the PGP file to decrypt
     * @param fileOut the resulting decrypted file
     * @param privKeyBlock the private key block. IF NULL, LOCAL KEY RING WILL
     * BE USED!
     * @param passphrase the passphrase
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     * @throws KeyException
     * @throws Exception
     */
    private int decryptPgpWithPrivateKeyStream(File fileIn, File fileOut, String privKeyBlock, char[] passphrase)
            throws IOException, FileNotFoundException, IllegalArgumentException,
            KeyException, Exception {
        //Verify argument validity
        if (fileIn == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }

        if (fileOut == null) {
            throw new IllegalArgumentException("Output stream cannot be null");
        }

        // Check the fileIn PGP status
        PgpFileStatus pgpFileStatus = new PgpFileStatusOne();
        int rc = pgpFileStatus.getPgpStatus(fileIn);
        if (rc != PgpFileStatus.STATUS_CRYPTED_ASYM) {
            return CmPgpCodes.ERR_UNKNOWN;
        }

        InputStream fIn = new BufferedInputStream(new FileInputStream(fileIn));

        InputStream fInDecoder = PGPUtil.getDecoderStream(fIn);

        PGPObjectFactory pgpF = new PGPObjectFactory(fInDecoder);
        PGPEncryptedDataList enc;

        Object o = null;

        try {
            o = pgpF.nextObject();
        } catch (IOException e) {
            String stack = Debug.GetStackTrace(e);
            throw new UnsupportedEncodingException(stack);
        }

        //
        // the first object might be a PGP marker packet.
        //
        if (o instanceof PGPEncryptedDataList) {
            enc = (PGPEncryptedDataList) o;
        } else {
            try {
                enc = (PGPEncryptedDataList) pgpF.nextObject();
            } catch (IOException e) {
                String stack = Debug.GetStackTrace(e);
                throw new UnsupportedEncodingException(stack);
            }
        }

        //
        // find the secret key
        //
        Iterator it = enc.getEncryptedDataObjects();
        PGPPrivateKey pgpPrivKey = null;
        PGPPublicKeyEncryptedData pbe = null;

        while (pgpPrivKey == null && it.hasNext()) {
            pbe = (PGPPublicKeyEncryptedData) it.next();

            InputStream in = null;
            // long keyId = pbe.getKeyID();
            if (privKeyBlock == null) {
                in = new FileInputStream(PgpUserId.getPrivKeyRingFilename());
            } else {
                ByteArrayInputStream bis = new ByteArrayInputStream(privKeyBlock.getBytes());
                in = new ArmoredInputStream(bis);
            }

            pgpPrivKey = findSecretKeyForEncryption(pbe.getKeyID(), passphrase, in);
            IOUtils.closeQuietly(in);
        }

        if (pgpPrivKey == null) {
            //throw new IllegalArgumentException("secret key for message not found.");
            return CmPgpCodes.KEY_NOT_FOUND;
        }

        PGPPublicKey key = null;

        //      PGPObjectFactory    plainFact = new PGPObjectFactory(clear);
        //      PGPCompressedData   cData = (PGPCompressedData)plainFact.nextObject();
        //      InputStream         compressedStream = new BufferedInputStream(cData.getDataStream());
        //      PGPObjectFactory    pgpFact = new PGPObjectFactory(compressedStream);
        //      Object              message = pgpFact.nextObject();
        InputStream clear = null;
        PGPObjectFactory plainFact = null;
        Object message = null;

        try {
            clear = pbe.getDataStream(pgpPrivKey, "BC");
            plainFact = new PGPObjectFactory(clear);
            message = plainFact.nextObject();
        } catch (Exception e) {
            String stack = Debug.GetStackTrace(e);
            throw new UnsupportedEncodingException(stack);
        }

        // 13/05/08 21:30 NDP - FIX: decryptPgp(File fileIn, File fileOut, String privKeyId, char []passphrase)
        //                            would not test if file was compressed
        if (message instanceof PGPCompressedData) {
            PGPCompressedData cData = (PGPCompressedData) message;
            InputStream compressedStream = new BufferedInputStream(cData.getDataStream());
            plainFact = new PGPObjectFactory(compressedStream);

            try {
                message = plainFact.nextObject();
            } catch (IOException e) {
                String stack = Debug.GetStackTrace(e);
                throw new UnsupportedEncodingException(stack);
            }
        }

        // If the File is signed, we must verify it.
        PGPOnePassSignature ops = null;

        if (message instanceof PGPOnePassSignatureList) {
            PGPOnePassSignatureList p1 = (PGPOnePassSignatureList) message;
            ops = p1.get(0);

            message = plainFact.nextObject();

            InputStream inKeyRing = new FileInputStream(PgpUserId.getPubKeyRingFilename());
            inKeyRing = PGPUtil.getDecoderStream(inKeyRing);

            PGPPublicKeyRingCollection pgpRing = new PGPPublicKeyRingCollection(inKeyRing);

            key = pgpRing.getPublicKey(ops.getKeyID());

            if (ops != null && key != null) {
                ops.initVerify(key, "BC");
            } else {
                // SAY NOTHING FOR NOW!
                // TODO
                // FUTUR USAGE: Say we can not verify the signature because of the signing key.
            }
        }

        if (message instanceof PGPLiteralData) {
            PGPLiteralData ld = (PGPLiteralData) message;

            // NDP - We don't use anymore the native name
            //FileOutputStream    fOut = new FileOutputStream(ld.getFileName());
            //FileOutputStream    fOut = new FileOutputStream(fileOut);
            //InputStream    unc = ld.getInputStream();
            
            //OutputStream out = new FileOutputStream(fileOut);
           
            BufferedOutputStream bos  = null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();;
            
            boolean isFilePdfToEncrypt = isFilePdfToEncrypt(fileIn);
           
            if (isFilePdfToEncrypt) {
                bos = new BufferedOutputStream(byteArrayOutputStream);
            }
            else {
                OutputStream out = new FileOutputStream(fileOut);
                bos = new BufferedOutputStream(out);
            }

            InputStream in = ld.getInputStream();
            BufferedInputStream unc = new BufferedInputStream(in);
            
            byte[] buf = new byte[BUFFER_SIZE];
            int len;

            int tempLen = 0;

            // For ProgressMonitor 
            setOwnerNote(messages.getMessage("progress_decrypt_file") + " " + fileIn.getName()); // + " ==> " + fileIn.getName() + ".pgeep"); 

            //int totalLen = 0;
            while ((len = unc.read(buf)) > 0) {
                tempLen += len;
                //totalLen += len;

                if (tempLen > filesLength / CryptoEngine.MAXIMUM_PROGRESS) {
                    tempLen = 0;
                    Thread.sleep(10); // TODO: adjust sleep time depending on fileIn length                                        
                    addOneOwnerCurrent(); // For ProgressMonitor progress bar 
                }

                if (isOwnerInterrupted()) {
                    bos.flush();
                    bos.close();
                    unc.close();
                    fIn.close();
                    throw new InterruptedException();
                }

                bos.write(buf, 0, len);

                if (ops != null && key != null) {
                    ops.update(buf, 0, len);
                }
            }

            bos.flush();
            bos.close();
            unc.close();
            fIn.close();

            // Take the ByteArrayOutputStream and convert it to ByteArrayOutputStream
            // If the File is a PDF and we want to encrypt it!
            if (isFilePdfToEncrypt) {
                InputStream inputPdfStream = convertOutputStreamToInputStream(byteArrayOutputStream);
                String password = new String(passphrase);
                PdfEncryptor.encryptPdf(inputPdfStream, fileOut.toString(), password, password);
            }
    
            if (ops != null && key != null) {
                PGPSignatureList p3 = (PGPSignatureList) plainFact.nextObject();

                if (ops.verify(p3.get(0))) {
                    //System.out.println("signature verified.");
                    this.lastSignPublicKey = new PgeepPublicKey(key);
                    rc = CmPgpCodes.SIGN_OK;
                } else {
                    //System.out.println("signature verification failed.");
                    this.lastSignPublicKey = null;
                    rc = CmPgpCodes.SIGN_BAD;
                }

                return rc;
            }

        } else if (message instanceof PGPOnePassSignatureList) {
            fIn.close();

            throw new PGPException("encrypted message contains a signed message - not literal data.");

        } else {
            fIn.close();
            throw new PGPException("message is not a simple encrypted fileIn - type unknown.");
        }

        // Futur Usage
        if (pbe.isIntegrityProtected()) {
            if (!pbe.verify()) {
                this.integrityCheck = false;
                //System.out.println("message failed integrity check");
            } else {
                this.integrityCheck = true;
                //System.out.println("message integrity check passed");
            }
        } else {
            this.integrityCheck = true;
            //System.out.println("no message integrity check");
        }

        return CmPgpCodes.ENCRYPT_ASYM;
    }

    private static boolean isFilePdfToEncrypt(File fileIn) {
        String pathIn = fileIn.toString();
        pathIn = pathIn.toLowerCase();
        if (Parms.encryptPdfWithPassphrase() && pathIn.endsWith("pdf.pgp")) {
            return true;
        }
        else {
            return false;
        }
    }
        
     public static InputStream convertOutputStreamToInputStream(ByteArrayOutputStream baos) {
        // Convert ByteArrayOutputStream to byte array
        byte[] byteArray = baos.toByteArray();

        // Create a ByteArrayInputStream from the byte array
        return new ByteArrayInputStream(byteArray);
    }
        

    /*
    private static PGPPrivateKey findSecretKeyForEncryption(

            long           keyID,
            char[]         passphrase)
    throws IOException, PGPException, NoSuchProviderException
    {    

        InputStream in = new FileInputStream(PgpUserId.getPrivKeyRingFilename());
        return findSecretKeyForEncryption(keyID, passphrase, in);

    }
     */
    /**
     * Load a secret key ring collection from keyIn and find the secret key
     * corresponding to keyID if it exists using an IntputStream
     *
     * @param keyID keyID we want.
     * @param passphrase passphrase to decrypt secret key with.
     * @
     * @return
     * @throws IOException
     * @throws PGPException
     * @throws NoSuchProviderException
     */
    private static PGPPrivateKey findSecretKeyForEncryption(long keyID,
            char[] passphrase, InputStream in) throws IOException, PGPException {
        PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
                PGPUtil.getDecoderStream(in));
        PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);

        if (pgpSecKey == null) {
            return null;
        }

        PGPPrivateKey pGPPrivateKey = null;

        // Add a try/catch block anyway
        try {
            pGPPrivateKey = pgpSecKey.extractPrivateKey(passphrase, "BC");
        } catch (Exception e) {
            System.out.println("NORMAL: " + e.toString());
            return null;
        }

        return pGPPrivateKey;
    }

    /* (non-Javadoc)
	 * @see com.safelogic.pgp.apispecs.PgpActions#signDetachedPgp(java.io.InputStream, java.lang.String, char[])
     */
    public void signPgp(File fileIn, File fileOut, String privKeyId, char[] passphrase)
            throws IOException, FileNotFoundException,
            IllegalArgumentException, KeyException, Exception {

        //Verify argument validity
        if (fileIn == null) {
            throw new IllegalArgumentException("Input fileIn cannot be null");
        }

        if (fileOut == null) {
            throw new IllegalArgumentException("Output fileIn cannot be null");
        }

        if (privKeyId == null) {
            throw new IllegalArgumentException("You must provide a private key");
        }

        if (passphrase == null) {
            throw new IllegalArgumentException("Passphrase cannot be null");
        }

        //PGPSecretKey                pgpSec = readSecretKey(keyIn);  
        KeyHandler kh = new KeyHandlerOne();

        PgeepPrivateKey pgeepPrivKey
                = (PgeepPrivateKey) kh.getPgpPrivateKey(privKeyId, null, passphrase);
        PGPSecretKey pgpSec = pgeepPrivKey.getPGPSecretKey();
        PGPPrivateKey pgpPrivKey = pgpSec.extractPrivateKey(passphrase, "BC");

        PGPSignatureGenerator sGen
                = new PGPSignatureGenerator(pgpSec.getPublicKey().getAlgorithm(),
                        PGPUtil.SHA1, "BC");

        sGen.initSign(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);

        Iterator it = pgpSec.getPublicKey().getUserIDs();
        if (it.hasNext()) {
            PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();

            spGen.setSignerUserID(false, (String) it.next());
            sGen.setHashedSubpackets(spGen.generate());
        }

        PGPCompressedDataGenerator cGen = new PGPCompressedDataGenerator(
                //PGPCompressedData.UNCOMPRESSED); 
                //PGPCompressedData.BZIP2);
                //PGPCompressedData.ZLIB); 
                PGPCompressedData.ZIP);

        OutputStream out = new BufferedOutputStream(new FileOutputStream(fileOut));

        OutputStream armOut = null;

        if (armorMode) {
            armOut = new CgeepTagArmoredOutputStream(out);
        } else {
            armOut = out;
        }

        BCPGOutputStream bOut = new BCPGOutputStream(cGen.open(armOut));

        //BCPGOutputStream            bOut = new BCPGOutputStream(armOut);
        sGen.generateOnePassVersion(false).encode(bOut);

        PGPLiteralDataGenerator lGen = new PGPLiteralDataGenerator();
        OutputStream lOut = lGen.open(bOut, PGPLiteralData.BINARY, fileIn);
        InputStream fIn = new BufferedInputStream(new FileInputStream(fileIn));

        byte[] buf = new byte[BUFFER_SIZE];
        int len;
        int totalLen = 0;

        // For ProgressMonitor 
        int tempLen = 0;
        //int current = 0;        

        setOwnerNote("OK! sign " + fileIn.getName()); // + " ==> " + fileIn.getName() + ".pgeep"); 

        addOneOwnerCurrent();

        while ((len = fIn.read(buf)) > 0) {
            tempLen += len;
            totalLen += len;

            if (tempLen > filesLength / CryptoEngine.MAXIMUM_PROGRESS) {
                tempLen = 0;
                Thread.sleep(10); // TODO: adjust sleep time depending on fileIn length                

                addOneOwnerCurrent(); // For ProgressMonitor progress bar 
            }

            if (isOwnerInterrupted()) {
                lGen.close();

                //bOut.close();    
                cGen.close();

                fIn.close();
                out.flush();
                out.close();
                throw new InterruptedException();
            }

            lOut.write(buf, 0, len);

            //sGen.update(buf);
            sGen.update(buf, 0, len); // HACK NDP 07/12/05
        }

        sGen.generate().encode(bOut);

        lGen.close();

        //bOut.close(); 
        cGen.close();

        fIn.close();
        out.flush();
        out.close();

    }

    public int verifyPgp(File fileIn, File fileOut)
            throws IllegalArgumentException, KeyException, Exception {
        //Verify argument validity
        if (fileIn == null) {
            throw new IllegalArgumentException("Input fileIn cannot be null");
        }

        if (fileOut == null) {
            throw new IllegalArgumentException("Output fileIn cannot be null");
        }

        // Check the fileIn PGP status
        int rc = 0;
        PgpFileStatus pgpFileStatus = new PgpFileStatusOne();
        rc = pgpFileStatus.getPgpStatus(fileIn);
        if (rc != PgpFileStatus.STATUS_SIGNED_ATTACHED) {
            return CmPgpCodes.ERR_UNKNOWN;
        }

        InputStream in = new BufferedInputStream(new FileInputStream(fileIn));
        in = PGPUtil.getDecoderStream(in);

        PGPObjectFactory pgpFact = new PGPObjectFactory(in);

        PGPCompressedData c1 = null;

        try {
            c1 = (PGPCompressedData) pgpFact.nextObject();
        } catch (IOException e) {
            throw new UnsupportedEncodingException(e.getMessage());
        }

        pgpFact = new PGPObjectFactory(c1.getDataStream());

        //PGPObjectFactory    plainFact = new PGPObjectFactory(clear);        
        //Object              message = plainFact.nextObject();
        //if (message instanceof PGPOnePassSignatureList)
        //{
        //    System.out.println("pgpFact instanceof PGPOnePassSignatureList");
        //}
        // BEGIN OLD CODE
        //PGPOnePassSignatureList     p1 = (PGPOnePassSignatureList)pgpFact.nextObject();        
        //PGPOnePassSignature         ops = p1.get(0);
        // END OLD CODE
        // BEGIN NEW CODE
        Object object = pgpFact.nextObject();
        PGPOnePassSignatureList p1 = null;
        PGPOnePassSignature ops = null;

        if (object instanceof PGPOnePassSignatureList) {
            p1 = (PGPOnePassSignatureList) object;
            ops = p1.get(0);
        } else if (object instanceof PGPOnePassSignature) {
            ops = (PGPOnePassSignature) object;
        } else {
            // SAY NOTHING FOR NOW!
            // TODO
            // FUTUR USAGE: Say we can not verify the signature because of the signing key.                
            this.lastSignPublicKey = null;
            rc = CmPgpCodes.KEY_NOT_FOUND;

            in.close();
            return rc;
        }
        // END NEW CODE        

        PGPLiteralData p2 = null;

        try {
            p2 = (PGPLiteralData) pgpFact.nextObject();
        } catch (IOException e) {
            throw new UnsupportedEncodingException(e.getMessage());
        }

        InputStream dIn = p2.getInputStream();

        //PGPPublicKeyRingCollection  pgpRing 
        //        = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(keyIn));
        InputStream inKeyRing = new FileInputStream(PgpUserId.getPubKeyRingFilename());
        inKeyRing = PGPUtil.getDecoderStream(inKeyRing);

        PGPPublicKeyRingCollection pgpRing = new PGPPublicKeyRingCollection(inKeyRing);

        PGPPublicKey key = pgpRing.getPublicKey(ops.getKeyID());

        if (key == null) {
            return CmPgpCodes.KEY_NOT_FOUND;
        }

        //OutputStream  out = new BufferedOutputStream( new FileOutputStream(p2.getFileName()));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(fileOut));

        ops.initVerify(key, "BC");

        byte[] buf = new byte[BUFFER_SIZE];
        int len;

        int totalLen = 0;

        // For ProgressMonitor 
        int tempLen = 0;

        setOwnerNote(messages.getMessage("progress_verifying_file") + " " + fileIn.getName()); // + " ==> " + fileIn.getName() + ".pgeep"); 
        addOneOwnerCurrent();

        while ((len = dIn.read(buf)) > 0) {
            tempLen += len;
            totalLen += len;

            if (tempLen > filesLength / CryptoEngine.MAXIMUM_PROGRESS) {
                tempLen = 0;
                Thread.sleep(10); // TODO: adjust sleep time depending on fileIn length                
                addOneOwnerCurrent(); // For ProgressMonitor progress bar
            }

            if (isOwnerInterrupted()) {
                in.close();
                out.close();
                throw new InterruptedException();
            }

            //ops.update(buf);
            //out.write(buf);
            ops.update(buf, 0, len);
            out.write(buf, 0, len);
        }

        out.close();

        PGPSignatureList p3 = (PGPSignatureList) pgpFact.nextObject();

        if (ops.verify(p3.get(0))) {
            //System.out.println("signature verified.");
            this.lastSignPublicKey = new PgeepPublicKey(key);
            rc = CmPgpCodes.SIGN_OK;
        } else {
            //System.out.println("signature verification failed.");
            this.lastSignPublicKey = null;
            rc = CmPgpCodes.SIGN_BAD;
        }

        in.close();
        return rc;
    }

    /**
     * Sign and then encrypt with PGP a fileIn for a list of public publicKeys
     * <br>Note: implementation *must* use KeyHandler.
     *
     * @param fileIn the input fileIn to PGP encrypt/sign.
     * @param fileOut the encrypted/signed output fileIn.
     * @param publicKeysId the list of PGP public publicKeys Id
     * @param privKeyId the private key id
     * @param passphrase the private key passprhase
     * @throws IOException if an I/O Exception occurs
     * @throws FileNotFoundException if a public key is not found and/or the
     * private key is not found
     * @throws IllegalArgumentException if a key (alone or in id list) is
     * invalid
     * @throws KeyException if a key error occurs
     * @throws Exception if any other exception occurs, must be defined because
     * there are too many possible cases.
     */
    public void signAndEncryptPgp(File fileIn,
            File fileOut,
            List<String> publicKeysId,
            String privKeyId,
            char[] passphrase)
            throws IOException, FileNotFoundException,
            IllegalArgumentException, KeyException, Exception {

        //***********************************
        //*  SIGNING AND ENCRYPTING CODE    *
        //***********************************
        //Verify argument validity
        if (fileIn == null) {
            throw new IllegalArgumentException("Input fileIn cannot be null");
        }
        if (fileOut == null) {
            throw new IllegalArgumentException("Output fileIn cannot be null");
        }
        if (publicKeysId.size() < 1) {
            throw new IllegalArgumentException("Public key(s) list cannot be empty");
        }
        if (privKeyId == null) {
            throw new IllegalArgumentException("Private key cannot be null");
        }

        //if (passphrase == null)
        //{
        //    throw new IllegalArgumentException("Passphrase cannot be null");
        //}
        int DEFAULT_BUFFER_SIZE = 16 * 1024;

        KeyHandler kh = new KeyHandlerOne();
        PgeepPrivateKey pgeepPrivKey
                = (PgeepPrivateKey) kh.getPgpPrivateKey(privKeyId, null, passphrase);

        PGPSecretKey signingKey = pgeepPrivKey.getPGPSecretKey();

        PGPPrivateKey signingPrivateKey = signingKey.extractPrivateKey(passphrase, "BC");

        int symmetricKeyAlgorithm = PGPEncryptedData.CAST5;
        int signingKeyAlgorithm = signingKey.getPublicKey().getAlgorithm();
        String userid = (String) signingKey.getPublicKey().getUserIDs().next();
        Iterator it = signingKey.getPublicKey().getUserIDs();

        // Init encrypted data generator
        PGPEncryptedDataGenerator encryptedDataGenerator
                = new PGPEncryptedDataGenerator(
                        symmetricKeyAlgorithm, true, new SecureRandom(), "BC");

        for (int i = 0; i < publicKeysId.size(); i++) {
            String publicKeyid = (String) publicKeysId.get(i);

            PgeepPublicKey pubKey = (PgeepPublicKey) kh.getPgpPublicKeyForEncryption(publicKeyid);
            PGPPublicKey pgpPubkey = pubKey.getKey();

            /*
            debug("pgpPubkey.getKeyID(): " + pgpPubkey.getKeyID() + " " + publicKeyid);

            if (DEBUG)
            {
                JOptionPaneCustom.showMessageDialog(null, publicKeyid);
            }
             */
            encryptedDataGenerator.addMethod(pgpPubkey);
        }

        OutputStream finalOut = new BufferedOutputStream(new FileOutputStream(fileOut),
                DEFAULT_BUFFER_SIZE);
        OutputStream encOut = encryptedDataGenerator.open(finalOut,
                new byte[DEFAULT_BUFFER_SIZE]);

        // Init compression
        int compressionAlgorithm = PGPCompressedData.ZIP;
        PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(compressionAlgorithm);
        OutputStream compressedOut = new BufferedOutputStream(compressedDataGenerator.open(encOut));

        // Init signature
        PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(
                signingKeyAlgorithm, HashAlgorithmTags.SHA1, "BC");
        signatureGenerator.initSign(PGPSignature.BINARY_DOCUMENT, signingPrivateKey);
        PGPSignatureSubpacketGenerator subpacketGenerator = new PGPSignatureSubpacketGenerator();
        subpacketGenerator.setSignerUserID(false, userid);
        signatureGenerator.setHashedSubpackets(subpacketGenerator.generate());
        PGPOnePassSignature onePassSignature
                = signatureGenerator.generateOnePassVersion(false);
        onePassSignature.encode(compressedOut);

        // Create the Literal Data generator Output stream which writes to the
        //compression stream
        PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator(true);
        OutputStream literalOut = literalDataGenerator.open(compressedOut,
                //PGPLiteralData.BINARY, outputFile.getName(),
                PGPLiteralData.BINARY, fileIn.getName(),
                new Date(), new byte[1 << 16]);

        InputStream input = new BufferedInputStream(new FileInputStream(fileIn));

        setOwnerNote(messages.getMessage("progress_crypt_file") + " " + fileIn.getName()); // + " ==> " + fileIn.getName() + ".pgeep");
        addOneOwnerCurrent();

        // update sign and encrypt
        byte[] buffer = new byte[1 << 16];

        int len = 0;

        // For ProgressMonitor 
        int tempLen = 0;

        while ((len = input.read(buffer)) != -1) {
            tempLen += len;
            if (tempLen > filesLength / CryptoEngine.MAXIMUM_PROGRESS) {
                tempLen = 0;
                Thread.sleep(10); // TODO: adjust sleep time depending on fileIn length                    
                addOneOwnerCurrent();  // For ProgressMonitor progress bar 
            }

            if (isOwnerInterrupted()) {
                // Close Literal data stream and add signature
                literalOut.close();
                literalDataGenerator.close();
                signatureGenerator.generate().encode(compressedOut);

                // Close all other streams
                compressedOut.close();
                compressedDataGenerator.close();
                encOut.close();
                encryptedDataGenerator.close();
                finalOut.close();
                input.close();
                throw new InterruptedException();
            }

            literalOut.write(buffer, 0, len);
            signatureGenerator.update(buffer, 0, len);
            literalOut.flush();
        }

        // Close Literal data stream and add signature
        literalOut.close();
        literalDataGenerator.close();
        signatureGenerator.generate().encode(compressedOut);

        // Close all other streams
        compressedOut.close();
        compressedDataGenerator.close();
        encOut.close();
        encryptedDataGenerator.close();
        finalOut.close();
        input.close();

    }

    public void signDetachedPgp(File fileIn, File fileOut,
            String privKeyId, char[] passphrase)
            throws IOException, FileNotFoundException, IllegalArgumentException,
            KeyException, Exception {
        //Verify argument validity
        if (fileIn == null) {
            throw new IllegalArgumentException("Input fileIn cannot be null");
        }

        if (fileOut == null) {
            throw new IllegalArgumentException("Output fileIn cannot be null");
        }

        if (privKeyId == null) {
            throw new IllegalArgumentException("You must provide a Private Key Id");
        }

        if (passphrase == null) {
            throw new IllegalArgumentException("Passphrase cannot be null");
        }

        OutputStream out = new BufferedOutputStream(new FileOutputStream(fileOut));

        if (armorMode) {
            out = new CgeepTagArmoredOutputStream(out);
        }

        KeyHandler kh = new KeyHandlerOne();

        PgeepPrivateKey pgeepPrivKey
                = (PgeepPrivateKey) kh.getPgpPrivateKey(privKeyId, null, passphrase);
        PGPSecretKey pgpSec = pgeepPrivKey.getPGPSecretKey();

        PGPPrivateKey pgpPrivKey = pgpSec.extractPrivateKey(passphrase, "BC");
        PGPSignatureGenerator sGen
                = new PGPSignatureGenerator(pgpSec.getPublicKey().getAlgorithm(),
                        PGPUtil.SHA1, "BC");

        sGen.initSign(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);

        BCPGOutputStream bOut = new BCPGOutputStream(out);

        FileInputStream fIn = new FileInputStream(fileIn);

        byte[] buf = new byte[BUFFER_SIZE];
        int len;

        // For ProgressMonitor 
        int tempLen = 0;
        //int current = 0;

        setOwnerNote(messages.getMessage("progress_sign_file") + " " + fileIn.getName()); // + " ==> " + fileIn.getName() + ".pgeep"); 
        addOneOwnerCurrent();

        int totalLen = 0;

        while ((len = fIn.read(buf)) > 0) {
            tempLen += len;
            totalLen += len;

            if (tempLen > filesLength / CryptoEngine.MAXIMUM_PROGRESS) {
                tempLen = 0;
                Thread.sleep(10); // TODO: adjust sleep time depending on fileIn length                     
                addOneOwnerCurrent();  // For ProgressMonitor progress bar  
            }

            if (isOwnerInterrupted()) {
                out.flush();
                out.close();
                fIn.close();
                throw new InterruptedException();
            }

            //sGen.update(buf);
            sGen.update(buf, 0, len);
        }

        sGen.generate().encode(bOut);

        out.flush();
        out.close();
        fIn.close();

    }

    public int verifyDetachedPgp(File fileIn, File fileDetachedSign)
            throws IOException, FileNotFoundException, IllegalArgumentException,
            KeyException, Exception {
        //Verify argument validity
        if (fileIn == null) {
            throw new IllegalArgumentException("Input fileIn cannot be null");
        }

        if (fileDetachedSign == null) {
            throw new IllegalArgumentException("Detached Signed fileIn cannot be null");
        }

        int rcCode;

        // Check the fileIn PGP status
        int rc = 0;
        PgpFileStatus pgpFileStatus = new PgpFileStatusOne();
        rc = pgpFileStatus.getPgpStatus(fileDetachedSign);

        if (rc != PgpFileStatus.STATUS_SIGNED_DETACHED) {
            return CmPgpCodes.ERR_UNKNOWN;
        }

        InputStream in = new BufferedInputStream(new FileInputStream(fileDetachedSign));
        in = PGPUtil.getDecoderStream(in);

        PGPObjectFactory pgpFact = new PGPObjectFactory(in);
        PGPSignatureList p3 = null;

        Object o = null;

        try {
            o = pgpFact.nextObject();
        } catch (IOException e) {
            throw new UnsupportedEncodingException(e.getMessage());
        }

        if (o instanceof PGPCompressedData) {
            PGPCompressedData c1 = (PGPCompressedData) o;

            pgpFact = new PGPObjectFactory(c1.getDataStream());

            try {
                p3 = (PGPSignatureList) pgpFact.nextObject();
            } catch (IOException e) {
                throw new UnsupportedEncodingException(e.getMessage());
            }

        } else {
            p3 = (PGPSignatureList) o;
        }

        //PGPPublicKeyRingCollection  pgpPubRingCollection 
        //        = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(keyIn));
        InputStream inKeyRing = new FileInputStream(PgpUserId.getPubKeyRingFilename());
        inKeyRing = PGPUtil.getDecoderStream(inKeyRing);

        PGPPublicKeyRingCollection pgpPubRingCollection
                = new PGPPublicKeyRingCollection(inKeyRing);

        InputStream dIn = new FileInputStream(fileIn);

        PGPSignature sig = p3.get(0);
        PGPPublicKey key = pgpPubRingCollection.getPublicKey(sig.getKeyID());

        if (key == null) {
            return CmPgpCodes.KEY_NOT_FOUND;
        }

        sig.initVerify(key, "BC");

        byte[] buf = new byte[BUFFER_SIZE];
        int len;

        // For ProgressMonitor        
        int tempLen = 0;

        setOwnerNote(messages.getMessage("progress_verifying_file") + " " + fileIn.getName()); // + " ==> " + fileIn.getName() + ".pgeep"); 
        addOneOwnerCurrent();

        int totalLen = 0;

        while ((len = dIn.read(buf)) > 0) {
            tempLen += len;
            totalLen += len;

            if (tempLen > filesLength / CryptoEngine.MAXIMUM_PROGRESS) {
                tempLen = 0;
                Thread.sleep(10); // TODO: adjust sleep time depending on fileIn length                
                addOneOwnerCurrent(); // For ProgressMonitor progress bar
            }

            if (isOwnerInterrupted()) {
                in.close();
                dIn.close();
                throw new InterruptedException();
            }

            //sig.update(buf);
            sig.update(buf, 0, len);
        }

        in.close();
        dIn.close();

        if (sig.verify()) {
            rcCode = CmPgpCodes.SIGN_OK;
            this.lastSignPublicKey = new PgeepPublicKey(key);
        } else {
            rcCode = CmPgpCodes.SIGN_BAD;
            this.lastSignPublicKey = null;
        }

        return rcCode;
    }

    public String signDetachedPgp(String strDetachedSign, String privKeyId, char[] passphrase)
            throws IOException, FileNotFoundException,
            IllegalArgumentException, KeyException, Exception {
        //Verify argument validity
        if (strDetachedSign == null) {
            throw new IllegalArgumentException("Input String cannot be null");
        }

        if (privKeyId == null) {
            throw new IllegalArgumentException("You must provide a private key");
        }

        PgpUserId pgpUserId = new PgpUserId(privKeyId); // Checks the userId format

        File privKeyRingFile = new File(PgpUserId.getPrivKeyRingFilename());

        String signedSrtring = signDetachedPgp(strDetachedSign, privKeyRingFile, privKeyId, passphrase);
        return signedSrtring;
    }

    public String signDetachedPgp(String strDetachedSign, File privKeyring, String privKeyId, char[] passphrase)
            throws IOException, FileNotFoundException,
            IllegalArgumentException, KeyException, Exception {

        //Verify argument validity
        if (strDetachedSign == null) {
            throw new IllegalArgumentException("Input String cannot be null");
        }

        if (privKeyring == null) {
            throw new IllegalArgumentException("private keyring cannot be null");
        }

        if (!privKeyring.exists()) {
            throw new FileNotFoundException("Private Keyring file not found:" + privKeyring);
        }

        if (privKeyId == null) {
            throw new IllegalArgumentException("You must provide a private key");
        }

        OutputStream outBytes = new ByteArrayOutputStream();

        //Always armor!
        ArmoredOutputStream out = new ArmoredOutputStream(outBytes);

        //PGPSecretKey             pgpSec = readSecretKey(keyIn);
        KeyHandler kh = new KeyHandlerOne();

        PgeepPrivateKey pgeepPrivKey
                = (PgeepPrivateKey) kh.getPgpPrivateKey(privKeyId, privKeyring, null, passphrase);
        PGPSecretKey pgpSec = pgeepPrivKey.getPGPSecretKey();

        PGPPrivateKey pgpPrivKey = pgpSec.extractPrivateKey(passphrase, "BC");
        PGPSignatureGenerator sGen
                = new PGPSignatureGenerator(pgpSec.getPublicKey().getAlgorithm(),
                        PGPUtil.SHA1, "BC");

        sGen.initSign(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);

        BCPGOutputStream bOut = new BCPGOutputStream(out);

        //FileInputStream    fIn = new FileInputStream(fileIn);
        InputStream fIn = new ByteArrayInputStream(strDetachedSign.getBytes());

        int ch = 0;

        while ((ch = fIn.read()) >= 0) {
            sGen.update((byte) ch);
        }

        sGen.generate().encode(bOut);
        bOut.flush();
        bOut.close();

        return outBytes.toString();

    }

    public int verifyDetachedPgp(String strIn, String strDetachedSign)
            throws IOException, FileNotFoundException, IllegalArgumentException,
            KeyException, Exception {
        //Verify argument validity
        if (strIn == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        }

        if (strDetachedSign == null) {
            throw new IllegalArgumentException("Detached signed string cannot be null");
        }

        File pubKeyring = new File(PgpUserId.getPubKeyRingFilename());
        int rcCode = verifyDetachedPgp(strIn, strDetachedSign, pubKeyring);

        return rcCode;

    }

    public int verifyDetachedPgp(String strIn, String strDetachedSign, File pubKeyring)
            throws IOException, FileNotFoundException, IllegalArgumentException,
            KeyException, Exception {
        if (pubKeyring == null) {
            throw new IllegalArgumentException("pubKeyring File can not be null");
        }

        int rc = -1;
        InputStream inKeyRing = new FileInputStream(pubKeyring);

        rc = this.verifyDetachedPgp(strIn, strDetachedSign, inKeyRing);
        return rc;
    }

    public int verifyDetachedPgp(String strIn, String strDetachedSign, String ascPubKey)
            throws IOException, FileNotFoundException, IllegalArgumentException,
            KeyException, Exception {
        if (ascPubKey == null) {
            throw new IllegalArgumentException("ascPubKey File can not be null");
        }

        int rc = -1;
        InputStream inKeyRing = new ByteArrayInputStream(ascPubKey.getBytes());

        rc = this.verifyDetachedPgp(strIn, strDetachedSign, inKeyRing);
        return rc;
    }

    private int verifyDetachedPgp(String strIn, String strDetachedSign, InputStream inKeyRing)
            throws IOException, FileNotFoundException, IllegalArgumentException,
            KeyException, Exception {
        //Verify argument validity
        if (strIn == null) {
            throw new IllegalArgumentException("Input string can not be null");
        }

        if (strDetachedSign == null) {
            throw new IllegalArgumentException("Detached signed string can not be null");
        }

        if (inKeyRing == null) {
            throw new IllegalArgumentException("inKeyRing InputStream can not be null");
        }

        int rcCode;

        InputStream in = new ByteArrayInputStream(strDetachedSign.getBytes());
        in = PGPUtil.getDecoderStream(in);

        PGPObjectFactory pgpFact = new PGPObjectFactory(in);

        PGPSignatureList p3 = null;

        Object o = null;

        try {
            o = pgpFact.nextObject();
        } catch (IOException e) {
            throw new UnsupportedEncodingException(e.getMessage());
        }

        if (o instanceof PGPCompressedData) {
            PGPCompressedData c1 = (PGPCompressedData) o;

            pgpFact = new PGPObjectFactory(c1.getDataStream());

            try {
                p3 = (PGPSignatureList) pgpFact.nextObject();
            } catch (IOException e) {
                throw new UnsupportedEncodingException(e.getMessage());
            }
        } else {
            p3 = (PGPSignatureList) o;
        }

        //PGPPublicKeyRingCollection  pgpPubRingCollection 
        //        = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(keyIn));
        //InputStream inKeyRing = new FileInputStream(pubKeyring);
        inKeyRing = PGPUtil.getDecoderStream(inKeyRing);

        PGPPublicKeyRingCollection pgpPubRingCollection
                = new PGPPublicKeyRingCollection(inKeyRing);

        InputStream dIn = new ByteArrayInputStream(strIn.getBytes());
        int ch;

        if (p3 == null) {
            // SAY NOTHING FOR NOW!
            // TODO
            // FUTUR USAGE: Say we can not verify the signature because of the signing key.            
            return CmPgpCodes.KEY_NOT_FOUND;
        }

        PGPSignature sig = p3.get(0);

        if (sig == null) {
            // SAY NOTHING FOR NOW!
            // TODO
            // FUTUR USAGE: Say we can not verify the signature because of the signing key.            
            return CmPgpCodes.KEY_NOT_FOUND;
        }

        PGPPublicKey key = pgpPubRingCollection.getPublicKey(sig.getKeyID());
        if (key == null) {
            return CmPgpCodes.KEY_NOT_FOUND;
        }

        sig.initVerify(key, "BC");

        while ((ch = dIn.read()) >= 0) {
            sig.update((byte) ch);
        }

        if (sig.verify()) {
            //System.out.println("signature verified.");
            this.lastSignPublicKey = new PgeepPublicKey(key);
            rcCode = CmPgpCodes.SIGN_OK;
        } else {
            //System.out.println("signature verification failed.");
            this.lastSignPublicKey = null;
            rcCode = CmPgpCodes.SIGN_BAD;
        }

        return rcCode;
    }

    /* (non-Javadoc)
	 * @see com.safelogic.pgp.apispecs.PgpActions#wipe(java.io.File, int)
     */
    public void wipe(File file, int securityLevel) throws IOException,
            FileNotFoundException, NoSuchMethodException {
        // This method will be implemented in a later version
        throw new NoSuchMethodException("Method not yet implemented!");

    }

    public String encryptPgp(String inString, List<String> publicKeysId)
            throws IllegalArgumentException, KeyException, Exception {

        //        if (DEBUG)
        //        {
        //            JOptionPane.showMessageDialog(null, "encryptPgp() BEGIN 1");
        //        }
        //  String outString = null;
        if (publicKeysId == null || publicKeysId.size() < 1) {
            throw new IllegalArgumentException("public key list cannot be null");
        }

        //If trying to encrypt a null string return null!
        if (inString == null) {
            return null;
        }

        List<PGPPublicKey> publicKeys = new Vector<PGPPublicKey>();

        for (int i = 0; i < publicKeysId.size(); i++) {
            String publicKeyid = (String) publicKeysId.get(i);
            KeyHandler kh = new KeyHandlerOne();

            PgeepPublicKey pubKey = (PgeepPublicKey) kh.getPgpPublicKeyForEncryption(publicKeyid);
            PGPPublicKey pgpPubkey = pubKey.getKey();

            //            if (DEBUG)
            //            {
            //                JOptionPane.showMessageDialog(null, publicKeyid);
            //            }
            publicKeys.add(pgpPubkey);

        }
        String encrypted = encryptStringPgp(inString, publicKeys);

        // USE ONLY PGP TAGS
        //encrypted = encrypted.replaceAll("PGP MESSAGE", "PGEEP MESSAGE"); //Use PGEEP Tags
        return encrypted;
    }

    public String encryptStringPgp(String inString,
            List<PGPPublicKey> publicKeys) throws NoSuchProviderException,
            PGPException, IOException, UnsupportedEncodingException, IllegalArgumentException {
        // No integrity check for now
        boolean withIntegrityCheck = this.integrityCheck;

        PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(PGPEncryptedData.CAST5,
                withIntegrityCheck,
                new SecureRandom(),
                "BC");
        for (PGPPublicKey pgpPubkey : publicKeys) {
            cPk.addMethod(pgpPubkey);
        }

        OutputStream fOut = new ByteArrayOutputStream();

        // Always armor!
        OutputStream armOut = new ArmoredOutputStream(fOut);

        OutputStream cOut = cPk.open(armOut, new byte[1 << 16]);

        BufferedOutputStream out = new BufferedOutputStream(armOut);

        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(
                PGPCompressedData.ZIP);

        //        writeFileToLiteralData(comData.open(cOut), 
        //                               PGPLiteralData.BINARY, 
        //                               inFile, 
        //                               new byte[1 << 16]);
        //        if (DEBUG)
        //        {
        //            JOptionPane.showMessageDialog(null, "encryptPgp() BEGIN 1");
        //        }
        byte[] bytesInCharset = inString.getBytes(encodingCharset);

        //        if (DEBUG)
        //        {
        //            JOptionPane.showMessageDialog(null, "encryptPgp() BEGIN 2");
        //        }
        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        OutputStream pOut = lData.open(comData.open(cOut),
                PGPLiteralData.BINARY,
                "string.txt",
                bytesInCharset.length,
                new Date());

        InputStream in = new ByteArrayInputStream(bytesInCharset);

        //        if (DEBUG)
        //        {
        //            JOptionPane.showMessageDialog(null, "encryptPgp() BEGIN 3");
        //        }
        //ArmoredInputStream    in = new ArmoredInputStream(inBytes);
        byte[] buf = new byte[4096];
        int len;

        while ((len = in.read(buf)) > 0) {
            pOut.write(buf, 0, len);
        }

        lData.close();
        in.close();

        comData.close();

        cPk.close();

        cOut.close();

        out.close();

        String encrypted = fOut.toString();
        return encrypted;
    }

    /**
     * @return true if the String Signature is verified
     */
    //@Override
    public boolean isStringSignatureVerified() {
        return stringSignatureVerified;
    }

    /**
     * @param stringSignatureVerified the stringSignatureVerified to set
     */
    private void setStringSignatureVerified(boolean stringSignatureVerified) {
        this.stringSignatureVerified = stringSignatureVerified;
    }

    public String decryptPgp(String strEncrypted, String privKeyId, char[] passphrase)
            throws IllegalArgumentException, KeyException, Exception {
        //  String strDecrypted = null;

        //Check parameter validity
        if (privKeyId == null) {
            throw new IllegalArgumentException("Private key id cannot be null!");
        }

        if (strEncrypted == null) {
            // if there is a null string to decrypt return null!
            return null;
        }

        // USE ONLY PGP TAGS
        strEncrypted = strEncrypted.replaceAll("PGEEP MESSAGE", "PGP MESSAGE");

        //InputStream fIn = new BufferedInputStream(new FileInputStream(fileIn));
        //OutputStream fOut = new ByteArrayOutputStream();
        // Always armor!
        //OutputStream armOut = new ArmoredOutputStream(fOut);
        PGPEncryptedDataList enc = getPgpEncryptedDataList(strEncrypted);

        //
        // find the secret key
        //
        Iterator it = enc.getEncryptedDataObjects();
        PGPPrivateKey pgpPrivKey = null;
        PGPPublicKeyEncryptedData pbe = null;

        while (pgpPrivKey == null && it.hasNext()) {
            pbe = (PGPPublicKeyEncryptedData) it.next();

            //        long keyId = pbe.getKeyID();
            InputStream in = new FileInputStream(PgpUserId.getPrivKeyRingFilename());
            pgpPrivKey = findSecretKeyForEncryption(pbe.getKeyID(), passphrase, in);

        }

        if (pgpPrivKey == null) {
            //throw new IllegalArgumentException("secret key for message not found.");
            return "KEY_NOT_FOUND";
        }

        return decryptStringPgp(strEncrypted, pgpPrivKey, null);

    }

    private PGPEncryptedDataList getPgpEncryptedDataList(String strEncrypted)
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

    public String decryptStringPgp(String strEncrypted,
            PGPPrivateKey pgpPrivKey, PGPPublicKey pgpPubKey)
            throws UnsupportedEncodingException, PGPException, IOException,
            NoSuchProviderException, SignatureException, Exception {
        InputStream clear = null;
        PGPObjectFactory plainFact = null;
        Object message = null;

        InputStream fIn = new ByteArrayInputStream(strEncrypted.getBytes());

        PGPEncryptedDataList enc = getPgpEncryptedDataList(strEncrypted);

        //
        // find the secret key
        //
        Iterator it = enc.getEncryptedDataObjects();
        PGPPublicKeyEncryptedData pbe = null;

        while (it.hasNext()) {
            pbe = (PGPPublicKeyEncryptedData) it.next();

            if (pbe.getKeyID() == pgpPrivKey.getKeyID()) {
                break;
            }

        }

        try {
            clear = pbe.getDataStream(pgpPrivKey, "BC");

            plainFact = new PGPObjectFactory(clear);

            message = plainFact.nextObject();
        } catch (Exception e) {
            throw new UnsupportedEncodingException(e.toString());
        }

        PGPObjectFactory pgpFact = null;

        if (message instanceof PGPCompressedData) {
            PGPCompressedData cData = (PGPCompressedData) message;
            pgpFact = new PGPObjectFactory(cData.getDataStream());

            message = pgpFact.nextObject();
        }

        ByteArrayOutputStream fOut = new ByteArrayOutputStream();
        PGPOnePassSignature calculatedSignature = null;

        if (message instanceof PGPOnePassSignatureList) {
            // Signature is present
            PGPOnePassSignatureList sigList
                    = (PGPOnePassSignatureList) message;

            calculatedSignature = sigList.get(0);
            long keyId = calculatedSignature.getKeyID();

            if (pgpPubKey == null) {
                File pubKeyring = new File(PgpUserId.getPubKeyRingFilename());
                InputStream inKeyRing = new FileInputStream(pubKeyring);
                inKeyRing = PGPUtil.getDecoderStream(inKeyRing);
                PGPPublicKeyRingCollection pgpPubRingCollection
                        = new PGPPublicKeyRingCollection(inKeyRing);

                pgpPubKey = pgpPubRingCollection.getPublicKey(keyId);
            }

            if (pgpPubKey == null) {
                // SAY NOTHING FOR NOW!
                // TODO
                // FUTUR USAGE: Say we can not verify the signature because of the signing key.                
                calculatedSignature = null;
            } else {
                calculatedSignature.initVerify(pgpPubKey, "BC");
            }

            message = pgpFact.nextObject();
        }

        if (message instanceof PGPLiteralData) {
            PGPLiteralData ld = (PGPLiteralData) message;

            // NDP - We don't use anymore the native name
            //FileOutputStream    fOut = new FileOutputStream(ld.getFileName());
            //FileOutputStream    fOut = new FileOutputStream(fileOut);
            //InputStream    unc = ld.getInputStream();
            //BufferedOutputStream fOut = new BufferedOutputStream(new FileOutputStream(fileOut));            
            BufferedInputStream unc = new BufferedInputStream(ld.getInputStream());

            int ch;

            while ((ch = unc.read()) >= 0) {
                fOut.write(ch);

                if (calculatedSignature != null) {
                    calculatedSignature.update((byte) ch);
                }
            }

            fOut.flush();
            fOut.close();
            unc.close();
            fIn.close();
        } else if (message instanceof PGPOnePassSignatureList) {
            fIn.close();
            throw new PGPException("encrypted message contains a signed message - not literal data.");
        } else {
            fIn.close();
            throw new PGPException("message is not a simple encrypted fileIn - type unknown.");
        }

        try {
            message = pgpFact.nextObject();

            if (message instanceof PGPSignatureList) {
                // verify the signature
                if (calculatedSignature != null) {
                    PGPSignatureList sigList = (PGPSignatureList) message;
                    PGPSignature sig = sigList.get(0);

                    if (calculatedSignature.verify(sig)) {
                        //JOptionPane.showMessageDialog(null, this.messages.getMessage("SIGNATURE_OK"));
                        setStringSignatureVerified(true);
                    } else {
                        //JOptionPane.showMessageDialog(null, this.messages.getMessage("SIGNATURE_BAD"));
                        setStringSignatureVerified(false);
                    }
                }
            }
        } catch (java.awt.HeadlessException e) {
            //Should never happen but happens on linux    
            System.out.println("WARNING: Impossible to use Swing JOptionPane on Linux!");
            e.printStackTrace();
        } catch (Exception e) {
            //Should never happen but happens on linux    
            throw e;
        }

        // Futur Usage
        if (pbe.isIntegrityProtected()) {
            if (!pbe.verify()) {
                this.integrityCheck = false;
                //System.out.println("message failed integrity check");
            } else {
                this.integrityCheck = true;
                //System.out.println("message integrity check passed");
            }
        } else {
            this.integrityCheck = true;
            //System.out.println("no message integrity check");
        }

        // 10/06/09 14:50 NDP - Autodetect encoding of incoming emails
        boolean autodetectEnconding
                = UserPreferencesManager.getBooleanPreference(UserPreferencesManager.AUTODETECT_ENCODING);

        String strCharset = null;

        if (autodetectEnconding) {
            byte[] byteArray = fOut.toByteArray();
            CharsetToolkit charsetToolkit = new CharsetToolkit(byteArray);
            strCharset = charsetToolkit.guessEncoding().displayName();
        } else {
            // Use default encoding     
            strCharset = encodingCharset;
        }

        // System.out.println("strCharset: " + strCharset);            
        return fOut.toString(strCharset);
    }

    public String signPgp(String str, String privKeyId, char[] passphrase)
            throws IllegalArgumentException, KeyException, Exception {
        //  String signed = null;

        //Check parameters validity
        if (str == null) {
            //Input string is null so return null
            return null;
        }

        if (privKeyId == null) {
            throw new IllegalArgumentException("Private key id cannot be null");
        }

        PgpUserId pgpUserId = new PgpUserId(privKeyId); // Checks the userId format
        File privKeyring = new File(PgpUserId.getPrivKeyRingFilename());

        String signedString = signPgp(str, privKeyring, privKeyId, passphrase);

        return signedString;
    }

    public String signPgp(String str, File privKeyring, String privKeyId, char[] passphrase)
            throws IllegalArgumentException, KeyException, Exception {
        //      String signed = null;

        //Check parameters validity
        if (str == null) {
            //Input string is null so return null
            return null;
        }

        if (privKeyring == null) {
            throw new IllegalArgumentException("private keyring cannot be null");
        }

        if (!privKeyring.exists()) {
            throw new FileNotFoundException("Private Keyring file not found:" + privKeyring);
        }

        if (privKeyId == null) {
            throw new IllegalArgumentException("Private key id cannot be null");
        }

        String strDetached = this.signDetachedPgp(str, privKeyring, privKeyId, passphrase);

        strDetached = Util.changeKeyVersion(strDetached);

        // USE ONLY PGP TAGS
        //str = PgpTags.BEGIN_PGEEP_SIGNED_MESSAGE + CR_LF + str;
        //strDetached = strDetached.replaceAll("PGP SIGNATURE", "PGEEP SIGNATURE");
        str = PgpTags.BEGIN_PGP_SIGNED_MESSAGE + CR_LF + str;

        // Ok, attach the detached signature to the string
        str = str + CR_LF + strDetached;

        return str;
    }

    public String verifyPgp(String strSigned, BooleanContainer booleanContainer)
            throws IllegalArgumentException, KeyException, Exception {

        String str = null;

        File pubKeyring = new File(PgpUserId.getPubKeyRingFilename());
        str = verifyPgp(strSigned, pubKeyring, booleanContainer);

        return str;

    }

    public String verifyPgp(String strSigned, File pubKeyring, BooleanContainer booleanContainer)
            throws IllegalArgumentException, KeyException, Exception {
        //Check parameters validity
        if (strSigned == null) {
            // input stream is null so return null 
            return null;
        }

        if (pubKeyring == null) {
            throw new IllegalArgumentException("File can not be null");
        }

        if (strSigned.indexOf(PgpTags.BEGIN_PGP_SIGNED_MESSAGE) == -1
                && strSigned.indexOf(PgpTags.BEGIN_PGEEP_SIGNED_MESSAGE) == -1) {
            return strSigned;
        }

        // Verify is done on both PGP and PGEEP tags
        String entete = null;
        String str = null;
        String strDetached = null;

        if (strSigned.startsWith(PgpTags.BEGIN_PGP_SIGNED_MESSAGE)) {
            entete = PgpTags.BEGIN_PGP_SIGNED_MESSAGE;
            str = strSigned.substring(entete.length() + 2,
                    strSigned.indexOf(PgpTags.BEGIN_PGP_SIGNATURE)).trim();
            strDetached = strSigned.substring(strSigned.indexOf(PgpTags.BEGIN_PGP_SIGNATURE));
        } else if (strSigned.startsWith(PgpTags.BEGIN_PGEEP_SIGNED_MESSAGE)) {
            entete = PgpTags.BEGIN_PGEEP_SIGNED_MESSAGE;
            str = strSigned.substring(entete.length() + 2,
                    strSigned.indexOf(PgpTags.BEGIN_PGEEP_SIGNATURE)).trim();
            strDetached = strSigned.substring(strSigned.indexOf(PgpTags.BEGIN_PGEEP_SIGNATURE));

            // Put back PGP tags
            strDetached = strDetached.replaceAll("PGEEP SIGNATURE", "PGP SIGNATURE");
        }

        //debug("!" + strDetached + "!");
        int rc = verifyDetachedPgp(str, strDetached, pubKeyring);

        if (rc == CmPgpCodes.SIGN_OK) {
            //System.out.println("Verified!");
            booleanContainer.setBooleanValue(true);
        } else {
            //System.out.println("NOT Verified!");
            booleanContainer.setBooleanValue(false);
        }

        return str;
    }

    public String encryptPgp(String inString, InputStream keyStream)
            throws IllegalArgumentException, KeyException, Exception {
        //  String outString = null;

        if (keyStream == null) {
            throw new IllegalArgumentException("public key stream cannot be null");
        }

        //If trying to encrypt a null string return null!
        if (inString == null) {
            return null;
        }

        // No integrity check for now
        boolean withIntegrityCheck = this.integrityCheck;

        PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(PGPEncryptedData.CAST5,
                withIntegrityCheck,
                new SecureRandom(),
                "BC");

        PGPPublicKeyRing pubKeyRing = KeyHandlerOne.readPublicKeyRing(keyStream);
        Iterator it = pubKeyRing.getPublicKeys();
        int i = 0;
        while (it.hasNext()) {
            PGPPublicKey pubKey = (PGPPublicKey) it.next();
            if (pubKey.isEncryptionKey()) {
                cPk.addMethod(pubKey);
                break;
            }

        }
        OutputStream fOut = new ByteArrayOutputStream();

        // Always armor!
        OutputStream armOut = new ArmoredOutputStream(fOut);

        OutputStream cOut = cPk.open(armOut, new byte[1 << 16]);

        BufferedOutputStream out = new BufferedOutputStream(armOut);

        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(
                PGPCompressedData.ZIP);

        //        writeFileToLiteralData(comData.open(cOut), 
        //                               PGPLiteralData.BINARY, 
        //                               inFile, 
        //                               new byte[1 << 16]);
        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        OutputStream pOut = lData.open(comData.open(cOut),
                PGPLiteralData.TEXT,
                "string",
                inString.length(),
                new Date());

        InputStream in = new ByteArrayInputStream(inString.getBytes(encodingCharset));

        //ArmoredInputStream    in = new ArmoredInputStream(inBytes);
        byte[] buf = new byte[4096];
        int len;

        while ((len = in.read(buf)) > 0) {
            pOut.write(buf, 0, len);
        }

        lData.close();
        in.close();

        comData.close();

        cPk.close();

        cOut.close();

        out.close();

        String encrypted = fOut.toString();

        // USE ONLY PGP TAGS
        //encrypted = encrypted.replaceAll("PGP MESSAGE", "PGEEP MESSAGE"); //Use PGEEP Tags
        return encrypted;

    }

    public int decryptPgpFromAscKey(File fileIn, File fileOut, String keyBloc, char[] passphrase)
            throws IOException, FileNotFoundException, IllegalArgumentException,
            KeyException, Exception {

        int rc = decryptPgpWithPrivateKeyStream(fileIn, fileOut, keyBloc, passphrase);
        return rc;
    }

    //Rule 8: Make your classes noncloneable
    public final Object clone() throws java.lang.CloneNotSupportedException {
        throw new java.lang.CloneNotSupportedException();
    }

    //Rule 9: Make your classes nonserializeable
    private final void writeObject(ObjectOutputStream out)
            throws java.io.IOException {
        throw new java.io.IOException("Object cannot be serialized");
    }

    //Rule 10: Make your classes nondeserializeable
    private final void readObject(ObjectInputStream in)
            throws java.io.IOException {
        throw new java.io.IOException("Class cannot be deserialized");
    }

    /**
     * debug tool
     */
    private void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
            //System.out.println(this.getClass().getName() + " " + new Date() + " " + s);
        }
    }

    public static PgeepPublicKey getPgeepPublicKey(String publicKeyPgpBlock)
            throws Exception {
        KeyHandler kh = new KeyHandlerOne();
        ByteArrayInputStream bis = new ByteArrayInputStream(publicKeyPgpBlock.getBytes());

        PgeepPublicKey pubKey = (PgeepPublicKey) kh.getPgpPublicKeyForEncryptionFromAsc(bis);

        PGPPublicKey pGPPublicKey = pubKey.getKey();

        Iterator it = pGPPublicKey.getUserIDs();

        while (it.hasNext()) {
            String userId = (String) it.next();
            System.out.println("userId: " + userId);
        }

        return pubKey;
    }

    public boolean isIntegrityCheck() {
        return integrityCheck;
    }

    public void setIntegrityCheck(boolean integrityCheck) {
        this.integrityCheck = integrityCheck;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Begin " + new Date());

        String pubKeyBlock_NICO = FileUtils.readFileToString(
                new File("C:\\temp\\safeshareit\\nico@safelogic.com_PUBLIC.asc"));

        String pubKeyBlock_ALEX = FileUtils.readFileToString(
                new File("C:\\temp\\safeshareit\\abecquereau@safelogic.com_PUBLIC.asc"));

        String privKeyBlock_NICO = FileUtils.readFileToString(
                new File("C:\\temp\\safeshareit\\nico.asc"));

        String privKeyBlock_ALEX = FileUtils.readFileToString(
                new File("C:\\temp\\safeshareit\\abecquereau@safelogic.com_PRIVATE.asc"));

        PgpActionsOne pActionsOne = new PgpActionsOne();

        PgeepPublicKey pgeepPublicKey_NICO = getPgeepPublicKey(pubKeyBlock_NICO);
        PgeepPublicKey pgeepPublicKey_ALEX = getPgeepPublicKey(pubKeyBlock_ALEX);

        List<PGPPublicKey> keys = new Vector<PGPPublicKey>();
        keys.add(pgeepPublicKey_NICO.getKey());
        keys.add(pgeepPublicKey_ALEX.getKey());

        File fileIn = new File("C:\\temp\\safeshareit\\Tulips.jpg");
        File fileOut = new File("C:\\temp\\safeshareit\\Tulips.jpg.pgp");
        pActionsOne.encryptFilePgp(fileIn, fileOut, keys);

        System.out.println("encryption done... " + new Date());
        //if (true) return; 

        File fileOut2 = new File("C:\\temp\\safeshareit\\Tulips_NEW_NICO.jpg");

        pActionsOne.decryptPgpFromAscKey(
                fileOut,
                fileOut2,
                privKeyBlock_NICO,
                "arnold loves $ and ca".toCharArray());
        System.out.println("decryption done for NICO passphrase... " + new Date());

        File fileOut3 = new File("C:\\temp\\safeshareit\\Tulips_NEW_ALEX.jpg");
        pActionsOne.decryptPgpFromAscKey(
                fileOut,
                fileOut3,
                privKeyBlock_ALEX,
                "passphrase".toCharArray());
        System.out.println("decryption done for ALEX passphrase... " + new Date());

        File fileOut4 = new File("C:\\temp\\safeshareit\\me.jpg.pgp");
        File fileOut5 = new File("C:\\temp\\safeshareit\\me.jpg");

        pActionsOne.decryptPgpFromAscKey(
                fileOut4,
                fileOut5,
                privKeyBlock_ALEX,
                "passphrase".toCharArray());
        System.out.println("decryption done for ALEX passphrase... " + new Date());

        System.out.println("ALL DONE!");
    }



}

// End
