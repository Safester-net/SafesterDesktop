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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;

import com.safelogic.pgp.apispecs.ExternalPubRingHandler;
import com.safelogic.utilx.Hex;

public class ExternalPubRingHandlerOne  implements ExternalPubRingHandler{

    private PGPPublicKeyRingCollection    pubRings;

    public ExternalPubRingHandlerOne()
    {
        Security.addProvider(new BouncyCastleProvider());
    }

    
    public boolean isFilePublicKeyRing(File file)
    {
        if (file == null || ! file.exists())
        {
            return false;
        }
        
        InputStream is = null;
        PGPUtil.setDefaultProvider("BC");
        
        try {
            is = new FileInputStream(file);
            PGPPublicKeyRingCollection pubRings = new PGPPublicKeyRingCollection(
                    PGPUtil.getDecoderStream(is));
            
            if (pubRings.size() == 0)
            {
                return false;
            }
            
            return true;
        } 
        catch (FileNotFoundException fnfe) 
        {
            return false;
        }
        catch (IOException ioe)
        {
            return false;
        }
        catch (PGPException pgpe)
        {
            return false;
        }        
        catch (Exception e) // Should never happen
        {
            return false;
        }           
        finally
        {
            IOUtils.closeQuietly(is);
        }
        
    }
    
    public boolean isFilePrivateKeyRing(File file)
    {
        if (file == null || ! file.exists())
        {
            return false;
        }
        
        InputStream is = null;
        PGPUtil.setDefaultProvider("BC");
        
        try {            
            is = new FileInputStream(file);
            PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(is));    
            
            if (pgpSec.size() == 0)
            {
                return false;
            }            
            
            return true;
        } 
        catch (FileNotFoundException fnfe) 
        {
            return false;
        }
        catch (IOException ioe)
        {
            return false;
        }
        catch (PGPException pgpe)
        {
            return false;
        }        
        catch (Exception e) // Should never happen
        {
            return false;
        }           
        finally
        {
            IOUtils.closeQuietly(is);
        }
        
    }    
    
    public void openKeyRing(File file) 
    throws IOException, FileNotFoundException, 
    IllegalArgumentException, KeyException
    {
        InputStream is = null;

        PGPUtil.setDefaultProvider("BC");
        try {
            is = new FileInputStream(file);
            this.pubRings = new PGPPublicKeyRingCollection(
                    PGPUtil.getDecoderStream(is));
        } 
        catch (PGPException e) {
            // TODO Auto-generated catch block
            throw new KeyException(e);
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }

    }

    public List<String> getKeyUserIds(File file) 
    throws IOException, FileNotFoundException, 
    IllegalArgumentException, KeyException, 
    NoSuchAlgorithmException 
    {
        
        List<String> keyIds = new Vector<String>();
        if(this.pubRings == null)
        {
            if(file==null)
            {
                throw new IllegalArgumentException("You must specify a file or call openKeyRing first");
            }
            else
            {
                openKeyRing(file);
            }
        }

        Iterator    rIt = pubRings.getKeyRings();

        while (rIt.hasNext())
        {
            PGPPublicKeyRing    pgpPub = (PGPPublicKeyRing)rIt.next();

            long        pgpKeyID = 0;
            PublicKey   pKey = null;

            Iterator    it = pgpPub.getPublicKeys();

            while (it.hasNext())
            {
                PGPPublicKey    pgpKey = (PGPPublicKey)it.next();

                Iterator    ituser = pgpKey.getUserIDs();
                

                while (ituser.hasNext())
                {
                    String userIdInKeyRing = (String) ituser.next();
                    keyIds.add(userIdInKeyRing);                                      
                }
            }
        }


        Collections.sort(keyIds);


        return keyIds;
    }

    public String getKeyAsc(String keyId) 
    throws IOException, FileNotFoundException,
    IllegalArgumentException, KeyException, 
    NoSuchAlgorithmException 
    {
        PGPPublicKeyRingCollection        pgpPub = this.pubRings;

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
            
            while (kIt.hasNext())
            {
                PGPPublicKey    pgpPubKey = (PGPPublicKey)kIt.next();

                Iterator    ituser = pgpPubKey.getUserIDs();
                int n = 0;
                while (ituser.hasNext())
                {                                               
                    userIdInKeyRing = (String) ituser.next();  
                    if(userIdInKeyRing.contains(keyId))
                    {
                    	//We founded what we were looking for => exit loop or we may lost it!
                    	break;
                    }
                }

                if (userIdInKeyRing.contains(keyId))
                {
                    // Ok, add the key to export armored string
                    // For 1) Master key AND 2) Encryption Key
                	System.out.println("founded: " + keyId + " / userIdInKeyring: " + userIdInKeyRing);
                    pgpPubKey.encode(os);

                    // Finish all if we have done both master key & encryption key
                    if (pgpPubKey.isEncryptionKey())
                    {
                    	System.out.println("is encryption key");
                        os.close();
                        armoredKey = bos.toString();
                        return armoredKey;
                    }
                    else
                    {
                    	System.out.println("is NOT encryption key");
                        armoredKey = bos.toString();
                    }
                    
                }                    

            }
        }

        if (armoredKey == null)
        {
            throw new IllegalArgumentException("Can't find encryption key in key ring."
                    + " for User ID: " + keyId);
        }
        
        // Will never be reached
        return armoredKey;


    }

    public String getKeyId(String userId) 
        throws IOException, FileNotFoundException, 
        IllegalArgumentException, KeyException, NoSuchAlgorithmException 
    {
        PGPPublicKeyRingCollection        pgpPub = this.pubRings;

        //
        // iterate through the key rings.
        //
        Iterator rIt = pgpPub.getKeyRings();

        // BEWARE
        // This will contain AT FIRST ITERATION ONLY on PGPPublicKey.getUserIDs()
        // The userid name. Aka, the second iteration, there will be no value
        String userIdInKeyRing = null;

        // The armored key to export

        while (rIt.hasNext())
        {
            PGPPublicKeyRing    kRing = (PGPPublicKeyRing)rIt.next();    
            Iterator            kIt = kRing.getPublicKeys();

            ByteArrayOutputStream bos = new  ByteArrayOutputStream();                
            OutputStream os = new ArmoredOutputStream(bos);

            while (kIt.hasNext())
            {
                PGPPublicKey    pgpPubKey = (PGPPublicKey)kIt.next();

                Iterator    ituser = pgpPubKey.getUserIDs();
                

                while (ituser.hasNext())
                {                                               
                    userIdInKeyRing = (String) ituser.next();                                                           
                }

                if (userIdInKeyRing.contains(userId))
                {
                    String fingerprint = pgpPubKey.getFingerprint().toString();
                    String sHash = Hex.toString( pgpPubKey.getFingerprint()); 
                    return "0x" + sHash.substring(32);
                }                    
            }
        }
        return null;
    }
    
    /*
    public PGPPublicKey getKey(String keyId) 
    throws IOException, FileNotFoundException,
    IllegalArgumentException, KeyException, 
    NoSuchAlgorithmException 
    {
        PGPPublicKeyRingCollection        pgpPub = this.pubRings;

        //
        // iterate through the key rings.
        //
        Iterator rIt = pgpPub.getKeyRings();

        // BEWARE
        // This will contain AT FIRST ITERATION ONLY on PGPPublicKey.getUserIDs()
        // The userid name. Aka, the second iteration, there will be no value
        String userIdInKeyRing = null;
        PGPPublicKey    pgpPubKey = null;
        while (rIt.hasNext())
        {
            PGPPublicKeyRing    kRing = (PGPPublicKeyRing)rIt.next();    
            Iterator            kIt = kRing.getPublicKeys();

            while (kIt.hasNext())
            {
                pgpPubKey = (PGPPublicKey)kIt.next();

                Iterator    ituser = pgpPubKey.getUserIDs();

                while (ituser.hasNext())
                {                                               
                    userIdInKeyRing = (String) ituser.next();                                                           
                }

                if (userIdInKeyRing.contains(keyId))
                {
                    // Ok, add the key to export armored string
                    // For 1) Master key AND 2) Encryption Key


                    // Finish all if we have done both master key & encryption key
                    if (pgpPubKey.isEncryptionKey())
                    {
                        //JOptionPaneCustom.showMessageDialog(null, "Key founded");
                        return pgpPubKey;
                    }
                }                    

            }
        }

        if (pgpPubKey == null)
        {
            throw new IllegalArgumentException("Can't find encryption key in key ring."
                    + " for User ID: " + keyId);
        }

        // Will never be reached
        return pgpPubKey;


    }
    */
}
