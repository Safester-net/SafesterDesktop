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
package com.safelogic.pgp.api.toolkit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;

import com.safelogic.pgp.api.KeyHandlerOne;

public class CgeepApiTools {

	
    /**
     * Retreive a list of user id from a key bloc
     * @param keyBloc           public key bloc
     * @return                  A list of all user ids found in key bloc
     * @throws IOException
     * @throws PGPException
     */
    public static List<String> getUserIdsFromBloc(String keyBloc) 
    throws IOException, PGPException {
        List<String> userIds = new Vector<String>();
        
        InputStream inKey = new ByteArrayInputStream(keyBloc.getBytes());

        PGPPublicKeyRing  pgpPubKeyRing = KeyHandlerOne.readPublicKeyRing(inKey); 
        inKey.close();
        String userIdInKeyRing;
        Iterator it = pgpPubKeyRing.getPublicKeys();
        while(it.hasNext())
        {
            PGPPublicKey    pgpPubKey = (PGPPublicKey)it.next();                       
            Iterator    ituser = pgpPubKey.getUserIDs();

            while (ituser.hasNext())
            {                                               
                userIdInKeyRing = (String) ituser.next(); 
                userIds.add(userIdInKeyRing);
            }
        }
        
        return userIds;
    }
    
    /**
     * Fill the file with the given byte[8] pattern
     *  
     * @param rafToWipe         File to wipe
     * @param bPattern          the Pattern to sue for wipe
     * @param isRandom          if true, use random pattern
     * @throws IOException
     */

    protected static void wipeRound(RandomAccessFile rafToWipe, byte[] bPattern, boolean isRandom)
        throws IOException, NoSuchAlgorithmException, InterruptedException
    {
        long lFileLength ;

        byte[] bWritePattern = bPattern ;
        
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
    
        rafToWipe.seek(0) ;
        int tempLen = 0;
        for(lFileLength = rafToWipe.length() ; lFileLength > 0 ; lFileLength -= bWritePattern.length)
        {                        
            tempLen += bWritePattern.length;
            
            if (tempLen > lFileLength / 100)
            {                  
                tempLen = 0;
                Thread.sleep(5);                
            }         
            
            if (isRandom)
            {
                random.nextBytes(bWritePattern);
            }
                        
            rafToWipe.write(bWritePattern) ;
        }
        
    }
    
    /**
     * Return the new name full of "a" for the file name
     * 
     * @param parent        The parent directory
     * @param name          The file actual name
     */
    protected static File getNewName(String parent, String name)
    {
        
        String newName = "";

        for (int i = 0; i < name.length(); i++)
        {
            newName += "a";
        }
        
        int cpt = 0;
        
        while (true)
        {            
            File newFile = new File(parent + File.separatorChar + newName + ".aaa");

            cpt++;
            
            if ( !newFile.exists())
            {
                return newFile;
            }
            else
            {
                // If there a more than 60 a, use time stamp                 
                if (cpt > 60)
                {
                    newName = "a" + new Date().getTime();  
                    cpt = 0;
                }
                else
                {
                    newName += "a";
                }
                
            }
        }
    }
    
    /**
     * Fill an arry of bytes with integer byte value
     * 
     * @param nSize         size of bytes
     * @param nValue        value to fill with
     * @return
     */
    protected static byte[] FillArray(int nSize, int nValue)
    {
        byte[] bReturn = new byte[nSize] ;

        for(int nCnt = 0 ; nCnt < nSize ; nCnt++)
        {
            bReturn[nCnt] = new Integer(nValue).byteValue() ;
        }

        return bReturn ;
    }
	
	/**
	 * Generate a random seed in a file
	 * @param seedFile		The file that will contain generated data
	 * @throws Exception
	 */
    public static void generateSeed(File seedFile) 
	throws Exception
	{
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte seed[] = new byte[1024];
		random.nextBytes(seed);
		OutputStream out = new FileOutputStream(seedFile);
		out.write(seed);
		out.flush();
		out.close();
	}
}
