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

import java.util.Date;


/**
 * Describes a PGP Public Key:
 * <br>- Key Id.
 * <br>- key Fingerprint.
 * <br>- Type.
 * <br>- Length.
 * <br>- Creation Date.
 * <br>- Expiration Date.
 * <br>- Image (if any)
 * <br>- is Revoked or not
 * 
 * @author Nicolas de Pomereu
 */

public interface PubkeyDescriptor
{
    
    /**
     * @return the PGP Key ID (*not* the pgeep keyId!)
     */
    public String getPgpId();
    
    /**
     * @return the PGP Fingerprint
     */
    public String getFingerprint();
    
    /**
     * @return the Key Type: RSA, etc.
     */
    public String getType();
    
    /**
     * @return the Key Length: 1024, 2048, etc.
     */
    public int getLength();
    
    /**
     * @return the Creation Date
     */
    public Date getCreationDate();
    
    /**
     * @return the Expiration Date (null if the public key never expires)
     */
    public Date getExpirationDate();
    
    /**
     * @return the Symetric Algorithm: AES-128, CAST5, etc.
     */
    public String getSymetricAlgorithm();
    
    /**
     * @return the Image of the Public PGP key - null if no image
     */
    public byte [] getImage();
    
    /**
     * @return true if the public key is expired
     */
    public boolean isExpired();
    
    /**
     * @return true if the public key is revoked
     */
    public boolean isRevoked();
    
    
        
}

