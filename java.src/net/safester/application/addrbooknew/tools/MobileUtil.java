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
package net.safester.application.addrbooknew.tools;


/**
 *
 * @author Nicolas de Pomereu
 */
public class MobileUtil {
    
 
    /**
     * Rmove special characters from mobile : +, 00, and spaces
     * @param mobile the mobile to remove special characters from 
     * @return the mobile with special characters removed
     */
    public static String removeSpecialCharacters(String mobile) {
        
        if (mobile == null || mobile.isEmpty()) {
            return mobile;
        }
        
        mobile = mobile.replace("/", "");
        mobile = mobile.replace(",", "");
        mobile = mobile.replace(".", "");
        mobile = mobile.replace("-", "");
        mobile = mobile.replace("(", "");
        mobile = mobile.replace(")", "");
        mobile = mobile.replace("[", "");
        mobile = mobile.replace("]", "");
        mobile = mobile.replace(" ", "");
        
        if (mobile.startsWith("+")) {
            mobile = mobile.substring(1);
        }
        
        if (mobile.startsWith("00")) {
            mobile = mobile.substring(2);
        }
        
        if (mobile.startsWith("0")) {
            mobile = "33" + mobile.substring(1);
        }
        
        return mobile;
    }
    

}
