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
package com.safelogic.pgp.api.util.crypto;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import com.safelogic.utilx.Hex;

/**
 * Hash funtions with only SUN provider and SHA-1
 * @author Nicolas de Pomereu
 *
 */
public class Sha1
{
    
    // default read buffer size 
    public static final int READ_BUFFER_SIZE  = 4096;
    
    // Default Constructor
    public Sha1()
    {
        
    }
    
    /**
    *
    * Compute the SHA-1 Hash code of a byte array as an hexa String
    * <br>
    * @param  bIn the bytes to hash
    * <br>
    * @return the hash value in Hexa String
    * @exception            NoSuchAlgorithmException if the algorithm is not
    *                      available from the provider.
    * @exception            NoSuchProviderException if the provider is not
    *                      available in the environment.
    */
    
    public byte[] getHash(byte bIn[])
        throws NoSuchAlgorithmException,
           NoSuchProviderException
    {              
       MessageDigest mdInstance = 
           MessageDigest.getInstance("SHA-1", "SUN");  
        
        // Bytes containing the MD5 or SHA-1 Hash Code
        byte [] bHash = new byte[160];
        
        mdInstance.update(bIn);
    
        bHash = mdInstance.digest();
                
        return bHash;
    }

    /**
    *
    * Compute the Hash code of a byte array as an hexa String
    * <br>
    * @param  bIn the bytes to hash
    * <br>
    * @return the hash value in Hexa String
    * @exception            NoSuchAlgorithmException if the algorithm is not
    *                      available from the provider.
    * @exception            NoSuchProviderException if the provider is not
    *                      available in the environment.
    */

    public String getHexHash(byte bIn[])
        throws NoSuchAlgorithmException,
               NoSuchProviderException
    {
        return Hex.toString(getHash(bIn));  
    }
    
    
    /**
     * get the fileIn hash
     * @param   sFilePath   the fileIn path
     * @return              the fileIn hash as bytes
     * 
     * @exception   IOException                 an I/O error occured
     * @exception   NoSuchAlgorithmException    hash algorithm not found
     * @exception   NoSuchProviderException     hash provider not found

     */
    
    public byte [] getFileHash(String sFilePath)
        throws IOException,
               NoSuchAlgorithmException,
               NoSuchProviderException
    {
        File fTmp = new File(sFilePath) ;
        
        FileInputStream     fisIn = new FileInputStream(fTmp);
        BufferedInputStream bisIn = new BufferedInputStream(fisIn);
        DataInputStream     disIn = new DataInputStream(bisIn);     
        
        // Bytes containing the MD5 or SHA-1 Hash Code
        byte [] bHash = new byte[160];
        
        MessageDigest mdInstance = MessageDigest.getInstance("SHA-1", "SUN"); 
        
        int nBytesRead;
        int nTotal = disIn.available();
        
        if (nTotal == 0)
        {
            return null;
        }
        
        if(nTotal > READ_BUFFER_SIZE)
        {
            nTotal = READ_BUFFER_SIZE ;
        }
        
        byte bRead[] = new byte[nTotal];
        
        while( (nBytesRead = disIn.read(bRead)) > 0 )
        {           
            mdInstance.update(bRead, 0, nBytesRead);            
        }
        
        disIn.close();
        
        bHash = mdInstance.digest();
        
        return bHash;       
    }       
    
    /**
     * get the fileIn hash as an hexa string value
     * @param   sFilePath   the fileIn path
     * @return              the fileIn hash as an hexa string
     * 
     * @exception   IOException                 an I/O error occured
     * @exception   NoSuchAlgorithmException    hash algorithm not found
     * @exception   NoSuchProviderException     hash provider not found
     */
    
    public String getHexFileHash(String sFilePath)
        throws IOException,
               NoSuchAlgorithmException,
               NoSuchProviderException
    {
        String sHashResult = new String();
        
        byte [] bHash = this.getFileHash(sFilePath);
        
        if (bHash == null)
        {   
            return "NULL"; // Fow Lastwall compliance
        }
        
        sHashResult = Hex.toString(bHash);
        return sHashResult;     
    }       
    
}

