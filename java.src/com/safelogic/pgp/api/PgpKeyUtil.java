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

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPUtil;

import com.safelogic.pgp.apispecs.KeyHandler;
import com.safelogic.pgp.apispecs.PgpFileStatus;

/**
 * Misc utilities to handle PGP messages and/or keys
 * @author Nicolas de Pomereu
 */
public class PgpKeyUtil
{

    /**
     * Constructor
     */
    public PgpKeyUtil()
    {
        Security.addProvider(new BouncyCastleProvider());
    }

    /* (non-Javadoc)
     * @see com.safelogic.pgp.apispecs.PgpUtil#extractPgpPublicKeys(java.io.File)
     */
    public static List<Long> extractPgpPublicKeys(File pgpFile)
    throws IOException, FileNotFoundException,
    IllegalArgumentException, KeyException, Exception
    {

        if (pgpFile == null)
        {
            throw new IllegalArgumentException("PGP file can not be null!");
        }

        // Check the fileIn PGP status
        PgpFileStatus pgpFileStatus= new PgpFileStatusOne();
        int rc = pgpFileStatus.getPgpStatus(pgpFile);

        if (rc != PgpFileStatus.STATUS_CRYPTED_ASYM)
        {
            throw new IllegalArgumentException("The file is not PGP encrypted: " + pgpFile);
        }

        InputStream fIn = new BufferedInputStream(new FileInputStream(pgpFile));

        return extractPgpKeysFromStream(fIn);
    }

    /* (non-Javadoc)
     * @see com.safelogic.pgp.apispecs.PgpUtil#extractPgpPublicKeys(String)
     */
    public static List<Long> extractPgpPublicKeys(String strEncrypted)
    throws IOException, FileNotFoundException,
    IllegalArgumentException, KeyException, Exception
    {

        if (strEncrypted == null)
        {
            throw new IllegalArgumentException("PGP String can not be null!");
        }

        // Replace PGEEP tags by PGP Tags
        strEncrypted = strEncrypted.replaceAll("PGP MESSAGE", "PGEEP MESSAGE");

        InputStream fIn = new ByteArrayInputStream(strEncrypted.getBytes());

        return extractPgpKeysFromStream(fIn);
    }    



    /**
     * @param fIn       the input stream to extract the PGP Public Keys from
     * @return the list of User IDs from a PGP encrypted file
     * @throws IOException
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     * @throws KeyException
     * @throws NoSuchAlgorithmException
     */
    private static List<Long> extractPgpKeysFromStream(InputStream fIn) 
    throws  IOException, 
    UnsupportedEncodingException, 
    FileNotFoundException, 
    IllegalArgumentException, 
    KeyException, 
    NoSuchAlgorithmException
    {
        InputStream fInDecoder = PGPUtil.getDecoderStream(fIn);

        PGPObjectFactory pgpF = new PGPObjectFactory(fInDecoder);
        PGPEncryptedDataList    enc;

        Object                  o = null;

        try
        {
            o = pgpF.nextObject();
        }
        catch (IOException e)
        {
            throw new UnsupportedEncodingException(e.getMessage());
        }

        //
        // the first object might be a PGP marker packet.
        //
        if (o instanceof PGPEncryptedDataList)
        {
            enc = (PGPEncryptedDataList)o;
        }
        else
        {
            try
            {
                enc = (PGPEncryptedDataList)pgpF.nextObject();
            }
            catch (IOException e)
            {
                throw new UnsupportedEncodingException(e.getMessage());
            }
        }

        //
        // find the secret key
        //
        Iterator                    it = enc.getEncryptedDataObjects();
        PGPPrivateKey               pgpPrivKey = null;
        PGPPublicKeyEncryptedData   pbe = null;

        KeyHandler kh = new KeyHandlerOne();

        List<Long> userIds = new Vector<Long>();

        //while (pgpPrivKey == null && it.hasNext())
        while (it.hasNext())
        {
            pbe = (PGPPublicKeyEncryptedData)it.next();

            long keyId = pbe.getKeyID();

            if (! userIds.contains(keyId))
            {
                userIds.add(keyId);
            }
        }

        fIn.close();

        return userIds;
    }

    /**
     * Extract the content of a JPEG file an put it into a jpeg byte[] using ImageIO
     * 
     * @param fileJpg         the file containing the JPEG image
     * @return  a jpeg byte[] using ImageIO
     * @throws IOException
     */
    public static byte[] getJpegImageAsBytesFromFile(File fileJpg)
            throws IOException
    {
        BufferedImage bufferedImage = ImageIO.read(fileJpg);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        String format = "jpg"; 
        ImageIO.write(bufferedImage, format, os);
        
        byte []image =  os.toByteArray();
        return image;
    }


}
