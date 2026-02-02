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
package com.safelogic.pgp.version;

public class Version
{
    /** Version value to increment */
    public static String VERSION = "v4.08b";

    /** Version date to increment */
    public static String DATE = "20/11/13 ";

    public static String NAME = "cGeep";
    public static String WEB = "http://www.cgeep.com";
    public static String COPYRIGHT = "Copyright &copy; 2010";

    public static String SERVLET_FOOTER = COPYRIGHT + " <A href=\"" + WEB
	    + "\" target=\"_blank\">" + NAME + "</A>";
    
    
    public String toString()
    {        
//        if(LicencingProcess.getIsPro())
//        {
//            return "cGeep Pro " + VERSION;
//        }
//        else
//        {
//            return "cGeep Free " + VERSION;
//        }
        
        return VERSION;
    }
    
    
    /**
     * 
     * @return the Version in "cGeep vX.YY Free/Pro DD/MM/YY" format
     */
    public static String getVersionWithDate()
    {
        return NAME + " " + new Version().toString() + " " + DATE;
    }
    
     
    
}
