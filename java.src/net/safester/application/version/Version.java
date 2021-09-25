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

package net.safester.application.version;

public class Version
{
    /** Version value to increment */    
    public static String VERSION    = "v6.3";

    /** Version date to increment */
    public static String DATE       = "25-Sep-21";

    public static String NAME       = "Safester" ;
    public static String COPYRIGHT  = "Copyright © 2021 Safester";


    @Override
    public String toString()
    {
        return VERSION;
    }

    /**
     *
     * @return the Version in "SafeShareIt vX.YY Copyright (c)" format
     */
    public static String getVersionWithCopyright()
    {
        return NAME + " " + new Version().toString() + " - " + DATE + " " + COPYRIGHT;
    }

    /**
     *
     * @return the Version in "SafeShareIt vX.YY  DD/MM/YY" format
     */
    public static String getVersionWithDate()
    {
        return NAME + " " + new Version().toString() + " " + DATE;
    }

}
