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
import java.security.KeyException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPBEEncryptedData;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;

import com.safelogic.pgp.apispecs.PgpFileStatus;

/**
 * This class allows to test the PGP status of any file passed.
 * <br>
 * The class allows to analyse if :
 * <br> - A  File is encrypted with PGP symmetrically.
 * <br> - A  File is encrypted with PGP asymmetrically.
 * <br> - A  File is signed with PGP attached.
 * <br> - A  File is signed with PGP detached.
 * <br>
 * Note: There is no status for a both Crypted & Signed File ==> Same as encrypted.
 * 
 * @author Nicolas de Pomereu
 *
 */

public class PgpFileStatusOne implements PgpFileStatus
{
    /** The debug flag */ 
    protected boolean DEBUG = false; //Debug.isSet(this);
    
    /**
     * Constructor
     *
     */
    public PgpFileStatusOne()
    {
        Security.addProvider(new BouncyCastleProvider());   
    }
    
    /* (non-Javadoc)
     * @see com.safelogic.pgp.api.PgpFileStatus#getPgpStatus(java.io.File)
     */
    public int getPgpStatus(File fileIn)
        throws IOException, KeyException
    {
        try
        {
            InputStream in = new FileInputStream(fileIn);
            return getPgpStatus(in);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return STATUS_NOT_PGP;
        }
    }
    
    /* (non-Javadoc)
     * @see com.safelogic.pgp.api.PgpFileStatus#getPgpStatus(java.lang.String)
     */
    public int getPgpStatus(String s)
        throws IOException, KeyException
    {
        InputStream in = new ByteArrayInputStream(s.getBytes());
        return getPgpStatus(in);
    }    
           
    /**
     * Retrieves the PGP  Status of a Input Stream
     * @param in    The Input Stream to retrieve the PGP statusd off
     * return the PGP status
     */
    private int getPgpStatus(InputStream in)
        throws IOException, KeyException
    {
        try
        {                                
            InputStream fInDecoder = PGPUtil.getDecoderStream(in);
            
            PGPObjectFactory pgpF = new PGPObjectFactory(fInDecoder);
            PGPEncryptedDataList    enc;

            Object                  o = pgpF.nextObject();
            
            //
            // the first object might be a PGP marker packet.
            //
            
            if (o instanceof PGPEncryptedDataList)
            {
                in.close();          
                
                enc = (PGPEncryptedDataList)o;
                
                Object objEnc = enc.get(0);
                
                if (objEnc instanceof PGPPBEEncryptedData)
                {
                    debug("4 PGP encrypted symmetric!");
                    in.close();
                    return STATUS_CRYPTED_SYMM;                            
                }
                
                debug("1 PGP encrypted asymmetric!");
                return STATUS_CRYPTED_ASYM;
            }
            else
            {              
                if (o instanceof PGPCompressedData)
                {
                    PGPCompressedData c1 = (PGPCompressedData)o;
                    PGPObjectFactory pgpFact = new PGPObjectFactory(c1.getDataStream());
                    
                    o  = pgpFact.nextObject();   
                    
                    in.close();
                    debug("2 PGP attached signed!");                    
                    return STATUS_SIGNED_ATTACHED;                
                }
                else if (o instanceof PGPSignatureList)
                {
                    in.close();
                    debug("2Bis PGP detached signed!");                    
                    return STATUS_SIGNED_DETACHED; 
                }
                else
                {
                    Object o2 = null;
                    
                    try
                    {
                        o2 = pgpF.nextObject();
                        in.close();
                    }
                    catch (IOException e)
                    {
                        //e.printStackTrace();
                        debug("3 PGP unknown!");
                        in.close();
                        return STATUS_NOT_PGP;
                    }
                                        
                    if (o2 instanceof PGPEncryptedDataList)
                    {                    
                        enc = (PGPEncryptedDataList)o2;
                        
                        Object objEnc = enc.get(0);
                        
                        if (objEnc instanceof PGPPBEEncryptedData)
                        {
                            debug("4 PGP encrypted symmetric!");
                            in.close();
                            return STATUS_CRYPTED_SYMM;                            
                        }
                                                
                        debug("4 PGP encrypted!");
                        in.close();
                        return STATUS_CRYPTED_ASYM;
                    }
                    
                    if (o2 instanceof PGPSignatureList)
                    {
                        debug("5 PGP detached signed!");
                        in.close();
                        return STATUS_SIGNED_ATTACHED;
                    }                    
                }
                            
            }
            
            in.close();
            debug("5 PGP unknown!");   
            return STATUS_NOT_PGP;
        }
        catch (Exception e)
        {            
            //throw new KeyException(e);
            e.printStackTrace();
            
            debug("6 Exception ==> PGP unknown! ");   
            return STATUS_NOT_PGP;
        }
    }
    
    /* (non-Javadoc)
     * @see com.safelogic.pgp.api.PgpFileStatus#isCryptedAsym(java.io.File)
     */
    
    public boolean isCryptedAsymmetric(File fileIn)
        throws IOException, KeyException
    {
        boolean isCrypted = false;
        
        int status = this.getPgpStatus(fileIn);
        
        if (status == STATUS_CRYPTED_ASYM)
        {
            isCrypted = true;
        }
        
        return isCrypted;
    }
    
    /* (non-Javadoc)
     * @see com.safelogic.pgp.api.PgpFileStatus#isCryptedSymmetric(java.io.File)
     */
    
    public boolean isCryptedSymmetric(File fileIn)
        throws IOException, KeyException
    {
        boolean isCrypted = false;
        
        int status = this.getPgpStatus(fileIn);
        
        if (status == STATUS_CRYPTED_ASYM)
        {
            isCrypted = true;
        }
        
        return isCrypted;
    }    
    
    /* (non-Javadoc)
     * @see com.safelogic.pgp.api.PgpFileStatus#isSigned(java.io.File)
     */
    public boolean isSignedAttached(File fileIn)
        throws IOException, KeyException
    {
        boolean isSigned = false;
        
        int status = this.getPgpStatus(fileIn);
        
        if (status == STATUS_SIGNED_ATTACHED)
        {
            isSigned = true;
        }
        
        return isSigned;
    }    
    
    public boolean isSignedDetached(File fileIn)
        throws IOException, KeyException
    {
        boolean isSigned = false;

        int status = this.getPgpStatus(fileIn);

        if (status == STATUS_SIGNED_DETACHED)
        {
            isSigned = true;
        }

        return isSigned;
    }      
    
    /**
     * debug tool
     */
    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
            //System.out.println(this.getClass().getName() + " " + new Date() + " " + s);
        }
    } 

}
