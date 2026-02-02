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
package com.safelogic.pgp.util;

import javax.swing.JOptionPane;



/**
 * @author Nicolas de Pomereu
 * com.safelogic.pgp.util.RegistryJni
 * 
 * Wraps the registryJni2.dll 
 */
public class RegistryJni
{

    /**
     * Extract from the Windows Registry HKEY_CLASSES_ROOT a String data value 
     * 
     * @param subKey    The registry subkey name
     * @param value     The registry subkey value
     * @return          The data value as String
     * 
     * @throws Exception
     */
    native String getClassesRootKeyValue(String subKey, String value);
    static
    {
        try
        {
            System.loadLibrary("RegistryJni2");
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, e.toString());
            //throw e;
        }
    }
    
    /**
     * 
     * Method to make RegOpenKeyEx() and a RegQueryValueEx() call with a DWORD
     * 
     * @param subKey        the sub key to query
     * @param value         the value of the sub key to query
     * 
     * @return the DWORD value converted to Java long
     */
    native long getCurrentUserKeyValueDword(String subKey, String value);
    static 
    {
        System.loadLibrary("RegistryJni2");
    }
    
    /**
     * 
     * Method to make RegOpenKeyEx() and a RegQueryValueEx() call with a REG_BINARY
     * 
     * @param subKey        the sub key to query
     * @param value         the value of the sub key to query
     * 
     * @return  the REG_BINARY converted to a Hex String
     */
    native String getCurrentUserKeyValueBinary(String subKey, String value);
    static
    {
        System.loadLibrary("RegistryJni2");
    }
    
    /**
     * 
     * @param handle    HKEY_CURRENT_USER, HKEY_LOCAL_MACHINE...
     * @param subKey    address of name of subkey to open
     * 
     * @return      true it the key exists
     */
    native boolean existsKey(int handle, String subKey)    ;
    static
    {
        System.loadLibrary("RegistryJni2");
    }    
    
}
