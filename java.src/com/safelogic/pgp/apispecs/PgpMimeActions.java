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
package com.safelogic.pgp.apispecs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyException;
import java.util.List;




/**
 * PgpActions 16 juin 2005 11:18:12
 * <br>
 * This Interface defines abstract PGP encrypt actions on Mime contents.
 * <br>Purpose is to create a PGP layer that is independant from any Provider specific
 * features.
 * <br>
 * <br>The encrypt action will:
 * <br> - Take a mime part with all it's boundary.
 * <br> - Replace it with the equivalent PGP encryption.
 * <br>Then, the e-mail will be PGP encrypted for it's recipients(s).
 * <br>
 * We don't need the Mime decrypt counter part, because it's ok to use stream in PgpActions
 * <br>
 * <br>
 * Example of tranformation:
 * <br>
 * This is the source mime part going out of Outlook:
 * <tt>
 *    ------=_NextPart_000_0006_01C5728F.F9957AA0
 *    Content-Type: text/plain; name=small_file.txt
 *    Content-Transfer-Encoding: 7bit
 *    Content-Disposition: attachment; filename="small_file.txt"
 *    A small fileIn with two lines
 *    **end line of small fileIn**
 *    ------=_NextPart_000_0006_01C5728F.F9957AA0
 * </tt>  
 * 
 * <br>
 * This is the source mime part going out of PC after PGP Encryption.
 * Is' now pGP encrypted and fileIn name/conter transfer encoding may be changed.
 * 
 * <tt>
 *   ------=_NextPart_000_0012_01C57291.F0A0EDB0
 *   Content-Type: application/octet-stream; name=small_file.txt.asc
 *   Content-Transfer-Encoding: 7bit
 *   Content-Disposition: attachment; filename="small_file.txt.asc"
 *   
 *   -----BEGIN PGP MESSAGE-----
 *   Version: PGP 8.0.3
 *   
 *   qANQR1DBw04Db/qua/hucZIQD/4iNr/gFNAO+/X2vS05LC6uckZyIW9EVdbwuDMp
 *   Wp2RjCcojyuW8/3iidbV39icoy1BkuAheK4OKFd62wQVSj8hm/+OwP09acOhSWJA
 *   m+3L81HfThTcYL/SX/gOfowg33id0xN7cOipXYaGs5Q7FUzt/nbVn2hgxA==
 *   =Sb3Q
 *   -----END PGP MESSAGE-----
 *
 *   ------=_NextPart_000_0012_01C57291.F0A0EDB0
 * </tt>
 * Notes:
 * <br> - Implementation will use a preferred PGP Provider. (There will be no factory to switch
 * from one Provier to another. Implementation will be "static" and hard coded).
 * <br> - Implementation *must* use PgpActions for part encryption/decryption
 */

public interface PgpMimeActions
{

    /**
     * Encrypt with PGP a stream of a Mime attachment for a list of public publicKeys.
     * <br>
     * Purpose is to take this kind of input
     * 
     * @param in              the input stream as mime attachment to PGP encrypt
     * @param publicKeysId    the list of PGP public publicKeys Id   
     * 
     * @return  the PGP encrypted mime stream
     * 
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if a public key is not found
     * @throws IllegalArgumentException if the key id list is invalid
     * @throws KeyException             if a key error occurs 
     * @throws Exception                if any other exception occurs, 
     *                                  must be defined because there are too many possible cases.
     */
    public OutputStream encryptMimeAttachPgp(InputStream in, List publicKeysId)
        throws IOException, FileNotFoundException, IllegalArgumentException, 
               KeyException, Exception;
        
    
    /**
     * Sign with a PGP private key a Mime Attachmentstream. The signature is included in 
     * the output stream Mime Attachment.
     * 
     * @param in                the input stream to PGP sign
     * @param privKeyId         the private key id 
     * @param passphrase        the private key passprhase
     * 
     * @return  the PGP signed stream
     * 
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if the private key is not found
     * @throws IllegalArgumentException if the private key id is invalid
     * @throws KeyException             if a key error occurs 
     * @throws Exception                if any other exception occurs, 
     *                                  must be defined because there are too many possible cases.
     */    
    
    public OutputStream signMimeAttachPgp(InputStream in, String privKeyId, char []passphrase)
            throws IOException, FileNotFoundException, IllegalArgumentException, 
                    KeyException, Exception;
    
    
  
    /**
     * Encrypt and sign with PGP a stream for a list of public publicKeys
     * <br>Note: implementation *must* use KeyHandler.
     * 
     * @param in                the input stream to PGP encrypt
     * @param publicKeysId      the list of PGP public publicKeys Id   
     * @param privKeyId         the private key id 
     * @param passphrase        the private key passprhase
     * 
     * @return  the PGP signed stream (containing original fileIn + signature)
     * 
     * @throws IOException              if an I/O Exception occurs
     * @throws FileNotFoundException    if a public key is not found and/or the private key is not
     *                                  found
     * @throws IllegalArgumentException if a key (alone or in id list) is invalid
     * @throws KeyException             if a key error occurs 
     * @throws Exception                if any other exception occurs, 
     *                                  must be defined because there are too many possible cases.
     */
    public OutputStream encryptAndSignMimeAttachPgp(InputStream in, List publicKeysId, 
                                          String privKeyId, char []passphrase)
        throws IOException, FileNotFoundException, IllegalArgumentException, 
               KeyException, Exception;    
    
 
}
