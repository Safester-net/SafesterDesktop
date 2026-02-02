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
package com.safelogic.pgp.api.util.parms;

/**
 * 
 * @author Nicolas de Pomereu
 *
 * The PGP cryptographic parameters
 * 
 */
public class PgpCryptoParms
{
    
    /** Symmetric algorithms list */
    public static String AES            = "AES";
    public static String BLOWFISH       = "Blowfish";
    public static String CAST           = "CAST";
    public static String TRIPLE_DES     = "3DES";
    
    /** Asymmetric algorithms list */
    public static String DSA_ELGAMAL    = "DSA/Elgamal";    
    public static String RSA            = "RSA";
    
    /** PGP Asymmetric algorithms */
    public static String[] KEY_ALGOS_ASYM   = {DSA_ELGAMAL, RSA};
    
    /** PGP Asymmetric algorithms key lengths */
    public static String[] KEY_LENGTHS_ASYM = {"1024", "2048", "3072", "4096"};  
    
    /** Symmetric Key Algorithms  and included key lengths */  
    public static String[] KEY_ALGOS_SYM =
    { 
        "AES - 128 bits", 
        "AES - 192 bits", 
        "AES - 256 bits",
        "Blowfish - 128 bits",
        "CAST - 128 bits",
        "3DES - 168 bits"
    };

}

