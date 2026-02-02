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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPDataValidationException;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPBEEncryptedData;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PBEDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;

import com.safelogic.pgp.api.engines.CryptoEngine;
import com.safelogic.pgp.api.util.crypto.CgeepTagArmoredOutputStream;
import com.safelogic.pgp.api.util.msg.MessagesManager;
import com.safelogic.pgp.apispecs.PgpSymActions;
import com.safelogic.pgp.util.Util;
import com.safelogic.utilx.Debug;

/**
 *
 * @author Nicolas de Pomereu
 *
 * This implementation uses org.bouncycastle.openpgp.examples.ByteArrayHandler example
 * class adapatation from Bouncy Castle
 */
public class PgpSymActionsOne implements PgpSymActions
{
    private static final String PROVIDER = "BC";

    /** The debug flag */
    protected boolean DEBUG = Debug.isSet(this);

    /** The files size - is used by the Progress indicator */
    private long filesLength = 0;

    /** The calling/owner thread */
    private CryptoEngine m_owner = null;

    /** Messages for I18N */
    private MessagesManager messages = new MessagesManager();

    /**
     * If true, all files with be encrypted and/or signed with armored format
     * true is the best solution for email sending/receptions
     */
    private boolean armorMode = false;

    /**
     * Defaut constructor.
     */
    public PgpSymActionsOne()
    {
        ensureBcProvider();
    }

    /**
     * Constructor to be called when a Progress Monitor
     * @param owner
     */
    public PgpSymActionsOne(CryptoEngine owner)
    {
        ensureBcProvider();
        this.m_owner = owner;
    }

    private static void ensureBcProvider()
    {
        if (Security.getProvider(PROVIDER) == null)
        {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * Signal that the file operation is terminated
     * (to be used in cGeepApi)
     */
    public void setMaximumProgress()
    {
        m_owner.setCurrent(CryptoEngine.MAXIMUM_PROGRESS);
    }

    /**
     * @param armorMode if true, the encrypted file will be PGP armored
     */
    public void setArmorMode(boolean armorMode)
    {
        this.armorMode = armorMode;
    }

    /**
     * @param filesLength the total files Size
     */
    public void setFilesLength(long filesLength)
    {
        this.filesLength = filesLength;
    }

    /**
     * Set th owner current value for progression bar
     * @param current
     */
    private void setOwnerCurrent(int current)
    {
        if (m_owner != null)
        {
            m_owner.setCurrent(current);
        }
    }

    /**
     * Set the owner current value for progression bar
     */
    private void addOneOwnerCurrent()
    {
        if (m_owner != null)
        {
            int current = m_owner.getCurrent();
            current++;

            if (current < CryptoEngine.MAXIMUM_PROGRESS)
            {
                m_owner.setCurrent(current);
            }

            debug("current: " + current);
        }
    }

    /**
     * Set the owner current note for progression bar
     */
    private void setOwnerNote(String note)
    {
        if (m_owner != null)
        {
            m_owner.setNote(note);
        }
    }

    /**
     * @return true is there is an owner AND it's interrupted
     */
    private boolean isOwnerInterrupted()
    {
        if (m_owner == null)
        {
            return false;
        }

        return ((Thread) m_owner).isInterrupted();
    }

    private static PGPObjectFactory newPgpObjectFactory(InputStream in)
    {
        return new PGPObjectFactory(in, new JcaKeyFingerprintCalculator());
    }

    private static PBEDataDecryptorFactory buildPbeDecryptorFactory(char[] passPhrase) throws PGPException
    {
        return new JcePBEDataDecryptorFactoryBuilder(
                new JcaPGPDigestCalculatorProviderBuilder().setProvider(PROVIDER).build()
        ).setProvider(PROVIDER).build(passPhrase);
    }

    /**
     * Encrypt a byte array using a PGP symetric encryption with a passphrase (PBE type)
     */
    public byte[] encryptSymmetricPgp(byte[] clearData, char[] passPhrase)
            throws IllegalArgumentException, KeyException, Exception
    {
        Objects.requireNonNull(clearData, "clearData cannot be null!");
        Objects.requireNonNull(passPhrase, "passPhrase cannot be null!");

        int algorithm = SymmetricKeyAlgorithmTags.CAST5;
        String fileName = PGPLiteralData.CONSOLE;

        ByteArrayOutputStream encOut = new ByteArrayOutputStream();
        OutputStream out = encOut;

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);
        OutputStream cos = comData.open(bOut);

        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        OutputStream pOut = lData.open(
                cos,
                PGPLiteralData.BINARY,
                fileName,
                clearData.length,
                new Date()
        );
        pOut.write(clearData);

        IOUtils.closeQuietly(pOut);
        comData.close();

        PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(
                new JcePGPDataEncryptorBuilder(algorithm)
                        .setWithIntegrityPacket(false)
                        .setSecureRandom(new SecureRandom())
                        .setProvider(PROVIDER)
        );

        cPk.addMethod(new JcePBEKeyEncryptionMethodGenerator(passPhrase).setProvider(PROVIDER));

        byte[] bytes = bOut.toByteArray();

        OutputStream cOut = cPk.open(out, new byte[1 << 16]);
        cOut.write(bytes);
        cOut.close();

        return encOut.toByteArray();
    }

    /**
     * Decrypt a byte array using a PGP symetric encryption with a passphrase (PBE type)
     */
    public byte[] decryptSymmetricPgp(byte[] encrypted, char[] passPhrase)
            throws PGPDataValidationException, PGPException, Exception
    {
        Objects.requireNonNull(encrypted, "encrypted cannot be null!");
        Objects.requireNonNull(passPhrase, "passPhrase cannot be null!");

        InputStream in = new ByteArrayInputStream(encrypted);
        in = PGPUtil.getDecoderStream(in);

        PGPObjectFactory pgpF = newPgpObjectFactory(in);
        PGPEncryptedDataList enc;
        Object o = pgpF.nextObject();

        if (o instanceof PGPEncryptedDataList)
        {
            enc = (PGPEncryptedDataList) o;
        }
        else
        {
            enc = (PGPEncryptedDataList) pgpF.nextObject();
        }

        PGPPBEEncryptedData pbe;
        try
        {
            pbe = (PGPPBEEncryptedData) enc.get(0);
        }
        catch (Exception e)
        {
            throw new PGPException("String is not PGP encrypted!");
        }

        InputStream clear;
        try
        {
            clear = pbe.getDataStream(buildPbeDecryptorFactory(passPhrase));
        }
        catch (Exception e)
        {
            throw new PGPDataValidationException("Invalid passphrase for String symmetric decryption!");
        }

        PGPObjectFactory pgpFact = newPgpObjectFactory(clear);

        Object msg = pgpFact.nextObject();
        if (msg instanceof PGPCompressedData)
        {
            PGPCompressedData cData = (PGPCompressedData) msg;
            pgpFact = newPgpObjectFactory(cData.getDataStream());
            msg = pgpFact.nextObject();
        }

        PGPLiteralData ld = (PGPLiteralData) msg;
        InputStream unc = ld.getInputStream();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;
        while ((ch = unc.read()) >= 0)
        {
            out.write(ch);
        }

        byte[] returnBytes = out.toByteArray();
        out.close();
        return returnBytes;
    }

    public void encryptFileSymmetricPgp(File fileIn, File fileOut, char[] passphrase)
            throws IllegalArgumentException, KeyException, Exception
    {
        Objects.requireNonNull(fileIn, "fileIn cannot be null!");
        Objects.requireNonNull(fileOut, "fileOut cannot be null!");
        Objects.requireNonNull(passphrase, "passphrase cannot be null!");

        boolean withIntegrityCheck = true;

        OutputStream out = new BufferedOutputStream(new FileOutputStream(fileOut));

        if (armorMode)
        {
            out = new CgeepTagArmoredOutputStream(out);
        }

        try
        {
            PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(
                    new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5)
                            .setWithIntegrityPacket(withIntegrityCheck)
                            .setSecureRandom(new SecureRandom())
                            .setProvider(PROVIDER)
            );

            cPk.addMethod(new JcePBEKeyEncryptionMethodGenerator(passphrase).setProvider(PROVIDER));

            OutputStream cOut = cPk.open(out, new byte[1 << 16]);

            PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);

            writeFileToLiteralData(
                    comData.open(cOut),
                    PGPLiteralData.BINARY,
                    fileIn,
                    new byte[1 << 16]
            );

            comData.close();
            cOut.close();
            out.close();
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        finally
        {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * write out the passed in file as a literal data packet in partial packet format.
     */
    private void writeFileToLiteralData(
            OutputStream out,
            char fileType,
            File file,
            byte[] buffer)
            throws IOException, InterruptedException
    {
        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();

        OutputStream pOut = lData.open(out, fileType, file.getName(), new Date(file.lastModified()), buffer);
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

        byte[] buf = new byte[buffer.length];
        int len;

        if (filesLength == 0)
        {
            setFilesLength(file.length());
        }

        setOwnerNote(messages.getMessage("progress_crypt_file") + " " + file.getName());
        addOneOwnerCurrent();

        int tempLen = 0;

        while ((len = in.read(buf)) > 0)
        {
            tempLen += len;

            if (tempLen > filesLength / CryptoEngine.MAXIMUM_PROGRESS)
            {
                tempLen = 0;
                Thread.sleep(10);
                addOneOwnerCurrent();
            }

            if (isOwnerInterrupted())
            {
                lData.close();
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(pOut);
                throw new InterruptedException();
            }

            pOut.write(buf, 0, len);
        }

        lData.close();
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(pOut);
    }

    public void decryptFileSymmetricPgp(File fileIn, File fileOut, char[] passphrase)
            throws PGPDataValidationException, PGPException, Exception
    {
        Objects.requireNonNull(fileIn, "fileIn cannot be null!");
        Objects.requireNonNull(fileOut, "fileOut cannot be null!");
        Objects.requireNonNull(passphrase, "passphrase cannot be null!");

        InputStream in = new BufferedInputStream(new FileInputStream(fileIn));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(fileOut));

        try
        {
            in = PGPUtil.getDecoderStream(in);

            PGPObjectFactory pgpF = newPgpObjectFactory(in);
            PGPEncryptedDataList enc;
            Object o = pgpF.nextObject();

            if (o instanceof PGPEncryptedDataList)
            {
                enc = (PGPEncryptedDataList) o;
            }
            else
            {
                enc = (PGPEncryptedDataList) pgpF.nextObject();
            }

            PGPPBEEncryptedData pbe;
            try
            {
                pbe = (PGPPBEEncryptedData) enc.get(0);
            }
            catch (Exception e)
            {
                throw new PGPException("File is not PGP encrypted!");
            }

            InputStream clear;
            try
            {
                clear = pbe.getDataStream(buildPbeDecryptorFactory(passphrase));
            }
            catch (Exception e)
            {
                throw new PGPDataValidationException("Invalid passphrase for File symmetric decryption!");
            }

            PGPObjectFactory pgpFact = newPgpObjectFactory(clear);

            Object message = pgpFact.nextObject();
            if (message instanceof PGPCompressedData)
            {
                PGPCompressedData cData = (PGPCompressedData) message;
                InputStream compressedStream = new BufferedInputStream(cData.getDataStream());
                pgpFact = newPgpObjectFactory(compressedStream);

                try
                {
                    message = pgpFact.nextObject();
                }
                catch (IOException e)
                {
                    throw new UnsupportedEncodingException(e.getMessage());
                }
            }

            PGPLiteralData ld = (PGPLiteralData) message;

            InputStream unc = ld.getInputStream();

            byte[] buf = new byte[4086];
            int len;

            if (filesLength == 0)
            {
                setFilesLength(fileIn.length());
            }

            setOwnerNote(messages.getMessage("progress_decrypt_file") + " " + fileIn.getName());
            addOneOwnerCurrent();

            long tempLen = 0;

            while ((len = unc.read(buf)) > 0)
            {
                tempLen += len;

                if (tempLen > filesLength / CryptoEngine.MAXIMUM_PROGRESS)
                {
                    tempLen = 0;
                    Thread.sleep(10);
                    addOneOwnerCurrent();
                }

                if (isOwnerInterrupted())
                {
                    out.flush();
                    IOUtils.closeQuietly(in);
                    IOUtils.closeQuietly(out);
                    throw new InterruptedException();
                }

                out.write(buf, 0, len);
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    public InputStream decryptFileSymmetricPgp(File fileIn, char[] passphrase)
            throws PGPDataValidationException, PGPException, Exception
    {
        Objects.requireNonNull(fileIn, "fileIn cannot be null!");
        Objects.requireNonNull(passphrase, "passphrase cannot be null!");

        InputStream in = new BufferedInputStream(new FileInputStream(fileIn));

        try
        {
            in = PGPUtil.getDecoderStream(in);

            PGPObjectFactory pgpF = newPgpObjectFactory(in);
            PGPEncryptedDataList enc;
            Object o = pgpF.nextObject();

            if (o instanceof PGPEncryptedDataList)
            {
                enc = (PGPEncryptedDataList) o;
            }
            else
            {
                enc = (PGPEncryptedDataList) pgpF.nextObject();
            }

            PGPPBEEncryptedData pbe;
            try
            {
                pbe = (PGPPBEEncryptedData) enc.get(0);
            }
            catch (Exception e)
            {
                throw new PGPException("File is not PGP encrypted!");
            }

            InputStream clear;
            try
            {
                clear = pbe.getDataStream(buildPbeDecryptorFactory(passphrase));
            }
            catch (Exception e)
            {
                throw new PGPDataValidationException("Invalid passphrase for File symmetric decryption!");
            }

            PGPObjectFactory pgpFact = newPgpObjectFactory(clear);

            Object message = pgpFact.nextObject();
            if (message instanceof PGPCompressedData)
            {
                PGPCompressedData cData = (PGPCompressedData) message;
                InputStream compressedStream = new BufferedInputStream(cData.getDataStream());
                pgpFact = newPgpObjectFactory(compressedStream);

                try
                {
                    message = pgpFact.nextObject();
                }
                catch (IOException e)
                {
                    throw new UnsupportedEncodingException(e.getMessage());
                }
            }

            PGPLiteralData ld = (PGPLiteralData) message;
            return ld.getInputStream();
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
    }

    public static void main(String[] args) throws Exception
    {
        String passPhrase = "passphrase";
        char[] passArray = passPhrase.toCharArray();

        byte[] original = "Hello world".getBytes();
        System.out.println("Starting PGP test");

        PgpSymActions pgpSymActions = new PgpSymActionsOne();

        byte[] encrypted = pgpSymActions.encryptSymmetricPgp(original, passArray);
        System.out.println(Util.CR_LF + "encrypted data = '" + new String(encrypted) + "'");

        byte[] decrypted = pgpSymActions.decryptSymmetricPgp(encrypted, passArray);
        System.out.println(Util.CR_LF + "decrypted data = '" + new String(decrypted) + "'");

        File fClear = new File("c:\\temp\\cGeepPro_License.txt");
        File fEncrypted = new File("c:\\temp\\cGeepPro_License.txt.pgp");

        pgpSymActions.encryptFileSymmetricPgp(fClear, fEncrypted, passArray);
    }
}
