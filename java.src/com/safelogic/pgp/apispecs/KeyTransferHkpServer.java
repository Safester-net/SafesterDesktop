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

import java.util.List;



/**
 * Interface for PGP HKP key transfer between a local host and a remote host.
 * <br>
 * Methods never throw Exception, because implementation class may be used as a JNI
 * interface from C/C++ or VB (OCX).
 * <br>
 * After each method call:
 * <br> - completion status must be checked with isOperationOk()
 * <br> - error may be trapped as a short string with getErrorCode().
 * <br> - error may be displayed to user with getErrorLabel().
 * <br> - complete stack trace may be displayed with getStackTrace().
 * 
 */

public interface KeyTransferHkpServer
{
    /**
     * @return Returns tuee if the opeation is Ok.
     */
    public abstract boolean isOperationOk();

    /**
     * @return the Error Code as a generic string
     */
    public abstract String getErrorCode();

    /**
     * @return the Error Exception
     */
    public abstract Exception getException();

    /**
     * Get a clean error label that may be displayed to the final user
     * @return the error label
     */
    public abstract String getErrorLabel();

    /**
     * @return the last Exception stack trace as a String.
     */
    public abstract String getStackTrace();


    /**
     * Search all the keys that match the passed patern
     * @param searchText                The pattern to search

     * @return a List of keys with in the format pgpId + userId + public key block :
     *      -<br> 0xDF76253A,   contact <contact@safelogic.com>    , public key block
     *      -<br> 0xC2540EE4,   nico2 <ndepomereu@safelogic.com>   , public key block
     */

    public abstract List<StringTriplet> getRemoteSearchedKey(String searchText, 
                                                             boolean includePublicKeyBlock);
    
    /**
     * Return true if the key associated to the userId exists on cgeep.com server
     * 
     * @param pgpId PGP Key Id - In format "0x3BDF6C25"
     * 
     * @return true if the key associated to the userId exists on cgeep.com server
     */    
    public abstract boolean existsRemotePubKey(String pgpId);

        
    /**
     * Download the PGP public key from the remote host keyring in armor format as String and
     * return it
     * 
     * @param pgpId PGP Key Id - In format "0x3BDF6C25"
     */       
    public abstract String getRemoteAscPgpPublicKeyAsAsc(String pgpId);    
    

    /**
     * Upload the PGP public key in asc format to the server
     * 
     * @return  the PGP public key  corresponding to the PGP key id in armor format
     * 
     */            
    public abstract void putRemoteAscPgpPublicKeyAsAsc(String publicKeyBlock);

}
