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
package net.safester.application.addrbooknew.outlook;

import com.moyosoft.connector.ms.outlook.Outlook;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * Utility methods for Outlook Office to be used with Moyosoft tool
 * @author Nicolas de Pomereu
 */
public class OutlookUtilMoyosoft {
    
    
    /**
     * Secure dispose
     * @param outlook   the Outlook instance to securely dispose 
     */
    public static void outlookDispose(Outlook outlook) {
        try {
            if (outlook != null) {
                outlook.dispose();
                outlook = null;
            }
        } catch (Exception e) {
            System.err.println("OUTLOOK DISPOSE EXCEPTION:");
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @return true if OS is Windows & Outlook is installed, else
     */
    public static boolean isOutlookInstalled() {
        
        if (! SystemUtils.IS_OS_WINDOWS) {
            return false;
        }
        
        String version = getOutlookVersion();
        
        if (version == null) {
            return false;
        }
        else {
            return true;
        }
    }
    
    /**
     * Returns Outlook Version, aka a String with a number.
     * Returns null if Outlook is now installed or OS not Windows
     * @return Outlook Major Version. Exemples : 13, 14, 15 or null if no Outlook Installed
     */
     public static String getOutlookVersion() {
        
        if (! SystemUtils.IS_OS_WINDOWS) {
            return null;
        }
                 
        String outlookVersion = null;
        try {
            Outlook outlook = new Outlook();
            outlookVersion = outlook.getOutlookVersion();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } 
        
        if (outlookVersion.contains(".")) {
            outlookVersion = StringUtils.substringBefore(outlookVersion, ".");
        }
        
        // Must be numerci before first dot!
         if (! StringUtils.isNumeric(outlookVersion)) {
             return null;
         }
         else {
             return outlookVersion;
         }
        
    }
    
    
    
}
