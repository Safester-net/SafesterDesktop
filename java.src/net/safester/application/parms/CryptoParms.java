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
package net.safester.application.parms;

/**
 * 
 * @author Nicolas de Pomereu
 *
 * The PGP cryptographic parameters
 * 
 */
public class CryptoParms
{
    
    /** Symmetric algorithms list */
    public static String AES            = "AES";
    
    /** Asymmetric algorithms list */
    public static String DSA_ELGAMAL    = "DSA/Elgamal";    
    public static String RSA            = "RSA";
    
    /** PGP Asymmetric algorithms */
    public static final String[] KEY_ALGOS_ASYM   = {DSA_ELGAMAL, RSA};
    
    /** PGP Asymmetric algorithms key lengths */
    public static final String[] KEY_LENGTHS_ASYM = {"2048", "3072", "4096"};
    
    /** Symmetric Key Algorithms  and included key lengths */  
    public static final String[] KEY_ALGOS_SYM =
    {
        "AES - 256 bits", 
    };

    // Default values
    public static String KEY_ALGO_ASYM_DSA_ELGAMAL = KEY_ALGOS_ASYM[0];
    public static String KEY_LENGTHS_ASYM_2048 = KEY_LENGTHS_ASYM[0];
    public static String KEY_ALGOS_SYM_AES_256 = KEY_ALGOS_SYM[0];

    // Says if we encrypt subjects and adress books
    public static final boolean DO_ENCRYPT_ADDRESS_BOOK = false;
    public static final boolean DO_ENCRYPT_SUBJECT = false;
        
}

