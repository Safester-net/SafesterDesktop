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

import java.io.File;
import java.io.IOException;
import java.security.KeyException;


public interface PgpFileStatus
{

    /** Status if the fileIn is not encrypted and not signed with PGP */
    public static final int STATUS_NOT_PGP          = 0;

    /** Status if the fileIn is encrypted with PGP with asymmetric  encryption */
    public static final int STATUS_CRYPTED_ASYM     = 1;

    /** Status if the fileIn is encrypted with PGP with symmetric encryption*/
    public static final int STATUS_CRYPTED_SYMM     = 2;
    
    /** Status if the fileIn is signed attached with PGP */
    public static final int STATUS_SIGNED_ATTACHED  = 3;
    
    /** Status if the fileIn is signed detached with PGP */
    public static final int STATUS_SIGNED_DETACHED  = 4;    

    /**
     * Return the PGP Status of the file 
     * @param fileIn        the file to check the PGP status
     * 
     * @return 
     * <br>- STATUS_NOT_PGP         if the file is not encrypted and not signed.
     * <br>- STATUS_CRYPTED_SYMM    if the file is symmetrically encrypted with PGP.
     * <br>- STATUS_CRYPTED_ASYM    if the file is asymmetrically encrypted with PGP.
     * <br>- STATUS_SIGNED_ATTACHED if the fileIn is signed attached with PGP.
     * <br>- STATUS_SIGNED_DETACHED if the fileIn is detached signature with PGP.
     * @throws IOException    if a I/O Exception occurs
     *         KeyException   if a PGP Exception occurs
     */
    public abstract int getPgpStatus(File fileIn) throws IOException,
            KeyException;

    /**
     * Return the PGP Status of the string 
     * @param stringIn        the String to check the PGP status
     * 
     * @return 
     * <br>- STATUS_NOT_PGP         if the file is not encrypted and not signed.
     * <br>- STATUS_CRYPTED_SYMM    if the file is symmetrically encrypted with PGP.
     * <br>- STATUS_CRYPTED_ASYM    if the file is asymmetrically encrypted with PGP.
     * <br>- STATUS_SIGNED_ATTACHED if the fileIn is signed attached with PGP.
     * <br>- STATUS_SIGNED_DETACHED if the fileIn is detached signature with PGP.
     * 
     * @throws IOException    if a I/O Exception occurs
     *         KeyException   if a PGP Exception occurs
     */
    public abstract int getPgpStatus(String stringIn) throws IOException,
            KeyException;
    
    /**
     * Return true if the passed file is asymmetrically encrypted with PGP
     * @param fileIn    the file to test 
     * @return true if the passed fileIn is PKI encrypted with PGP
     * @throws IOException    if a I/O Exception occurs
     *         KeyException   if a PGP Exception occurs
     */
    public abstract boolean isCryptedAsymmetric(File fileIn) throws IOException,
            KeyException;

    /**
     * Return true if the passed file is symmetrically encrypted with PGP
     * @param fileIn    the file to test 
     * @return true if the passed fileIn is symmetrically encrypted with PGP
     * @throws IOException    if a I/O Exception occurs
     *         KeyException   if a PGP Exception occurs
     */
    public abstract boolean isCryptedSymmetric(File fileIn) throws IOException,
            KeyException;    
    
    /**
     * Return true if the passed file is a attached signature file signed with with PGP
     * @param fileIn    the file to test 
     * @return true if the passed file is a attached signature signed with with PGP
     * @throws IOException    if a I/O Exception occurs
     *         KeyException   if a PGP Exception occurs
     */
    public abstract boolean isSignedAttached(File fileIn) throws IOException,
            KeyException;    
    /**
     * Return true if the passed file is a detached signature with PGP
     * @param fileIn    the file to test 
     * @return true if the passed file is a detached signature with PGP
     * @throws IOException    if a I/O Exception occurs
     *         KeyException   if a PGP Exception occurs
     */
    public abstract boolean isSignedDetached(File fileIn) throws IOException,
            KeyException;

}
