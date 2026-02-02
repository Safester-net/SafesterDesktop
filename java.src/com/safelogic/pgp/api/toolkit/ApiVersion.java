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

/**
 * @author Nicolas de Pomereu
 *
 */
public class ApiVersion
{   
    private static String NAME    = "cGeep API";
    private static String VERSION = "v1.00f BETA";
    private static String SLOGAN  = "Easy Encryption For all";
    
    private static String DATE    = "25/11/2009";
    
    public static String getVersion()
    {
        return VERSION;
    }

    public static String getVersionWithDate()
    {
        return VERSION + " - " + DATE;
    }
    
    /**
     * @return the Name
     */
    public static String getName()
    {
        return NAME;
    }

    /**
     * @return the Slogan
     */
    public static String getSlogan()
    {
        return SLOGAN;
    }
    
       
}

