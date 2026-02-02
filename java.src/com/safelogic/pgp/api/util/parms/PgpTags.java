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
 * PGP Tags container.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class PgpTags
{
    //
    // PGP original tags
    //
    public static final String BEGIN_PGP_PRIVATE_KEY_BLOCK  = "-----BEGIN PGP PRIVATE KEY BLOCK-----";
    public static final String END_PGP_PRIVATE_KEY_BLOCK    = "-----END PGP PRIVATE KEY BLOCK-----";
    
    public static final String BEGIN_PGP_PUBLIC_KEY_BLOCK   = "-----BEGIN PGP PUBLIC KEY BLOCK-----";
    public static final String END_PGP_PUBLIC_KEY_BLOCK     = "-----END PGP PUBLIC KEY BLOCK-----";
    
    public static final String BEGIN_PGP_SIGNED_MESSAGE     = "-----BEGIN PGP SIGNED MESSAGE-----";
    public static final String BEGIN_PGP_SIGNATURE          = "-----BEGIN PGP SIGNATURE-----";
 
    public static final String BEGIN_PGP_MESSAGE            = "-----BEGIN PGP MESSAGE-----";
    public static final String END_PGP_MESSAGE              = "-----END PGP MESSAGE-----";
    
    //
    // PGEEP Tags
    //
    public static final String BEGIN_PGEEP_SIGNED_MESSAGE    = "-----BEGIN PGEEP SIGNED MESSAGE-----";    
    public static final String BEGIN_PGEEP_SIGNATURE         = "-----BEGIN PGEEP SIGNATURE-----";

    public static final String BEGIN_PGEEP_MESSAGE           = "-----BEGIN PGEEP MESSAGE-----";
    public static final String END_PGEEP_MESSAGE             = "-----END PGEEP MESSAGE-----";
    
    // Extension for detached signature
    //public static final String PGEEP_SIGNATURE_EXT           = ".pgeep_signature";
   
    // Extention for zipped file
    public static final String PGEEP_ZIP_EXT = ".zip";
    
   
}

/**
 * 
 */
