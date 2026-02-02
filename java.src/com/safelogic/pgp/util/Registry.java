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

import java.lang.reflect.Method;
import java.util.prefs.Preferences;



/**
 * Method to access the Windows Registry without using JNI.
 * Please see the main() code for how to use.
 * 
 * @author Nicolas de Pomereu
 */

public class Registry
{    
    // Registry constants
    public static final int HKEY_CURRENT_USER = 0x80000001;
    public static final int HKEY_LOCAL_MACHINE = 0x80000002;

    /*  Windows error codes. */
    public static final int ERROR_SUCCESS = 0;
    public static final int ERROR_FILE_NOT_FOUND = 2;
    public static final int ERROR_ACCESS_DENIED = 5;
    
    
    /* Windows security masks */
    public static final int DELETE = 0x10000;
    public static final int KEY_QUERY_VALUE = 1;
    public static final int KEY_SET_VALUE = 2;
    public static final int KEY_CREATE_SUB_KEY = 4;
    public static final int KEY_ENUMERATE_SUB_KEYS = 8;
    public static final int KEY_READ = 0x20019;
    public static final int KEY_WRITE = 0x20006;
    public static final int KEY_ALL_ACCESS = 0xf003f;
    
    // Registry functions to load using java reflection
    public static Method openKey;
    public static Method closeKey;
    public static Method winRegQueryValueEx;
    public static Method winRegEnumKey;;
    public static Method winRegEnumValue;
    public static Method winRegQueryInfo;
    public static Method WindowsRegSetValueEx;
    public static Method WindowsRegDeleteValue;
    
    public static Class clz;
    public Preferences userRoot;
    public Preferences systemRoot;
    
    /**
     * Consctructor
     */
    public Registry()
        throws NoSuchMethodException
    {
        userRoot = Preferences.userRoot();
        systemRoot = Preferences.systemRoot();
        
        clz = systemRoot.getClass();
        
        openKey = clz.getDeclaredMethod("openKey",
                byte[].class, int.class, int.class);
        openKey.setAccessible(true);
        
        closeKey = clz.getDeclaredMethod("closeKey",
                        int.class);
        closeKey.setAccessible(true);
        
       
        winRegQueryValueEx = clz.getDeclaredMethod(
                        "WindowsRegQueryValueEx", int.class, byte[].class);
        winRegQueryValueEx.setAccessible(true);
        
        
        winRegEnumKey = clz.getDeclaredMethod(
                        "WindowsRegEnumKeyEx", int.class, int.class, int.class);
        winRegEnumKey.setAccessible(true);
        
        
        winRegEnumValue = clz.getDeclaredMethod(
                        "WindowsRegEnumValue1", int.class, int.class, int.class);
        winRegEnumValue.setAccessible(true);
        
        
        winRegQueryInfo = clz.getDeclaredMethod(
                        "WindowsRegQueryInfoKey1", int.class);
        winRegQueryInfo.setAccessible(true);
        
        
        WindowsRegSetValueEx = clz.getDeclaredMethod(
                        "WindowsRegSetValueEx1", int.class, byte[].class, byte[].class);
        WindowsRegSetValueEx.setAccessible(true);
        
        WindowsRegDeleteValue = clz.getDeclaredMethod(
                        "WindowsRegDeleteValue", int.class, byte[].class);
        WindowsRegDeleteValue.setAccessible(true);
        

    }
    
    /**
     * Emulates RegOpenKey()
     * 
     * @param handle    HKEY_CURRENT_USER, HKEY_LOCAL_MACHINE...
     * @param subKey    address of name of subkey to open
     * @param sam       security access mask: KEY_READ, KEY_WRITE, KEY_ALL_ACCESS...
     * @return          address of handle to open key
     * 
     * @throws Exception
     */
    public Integer RegOpenKeyEx(int handle, String subKey, int sam)
        throws Exception
    {
        Integer hSettings = null;
        
        if (handle == HKEY_CURRENT_USER )
        {
            hSettings = (Integer) openKey.invoke(userRoot,
                    toByteArray(subKey), sam, sam);   
        }
        else if (handle == HKEY_LOCAL_MACHINE )
        {
            hSettings = (Integer) openKey.invoke(systemRoot,
                    toByteArray(subKey), sam, sam);         
        }
        else
        {
            throw new UnsupportedOperationException("Only HKEY_CURRENT_USER and HKEY_LOCAL_MACHINE are supported!");
        }

        
        return hSettings;
    }
    
    /**
     * Emulates RegQueryValueEx(HKEY_CURRENT_USER, ...)
     * 
     * @param handle        handle to key to query
     * @param value         address of name of value to query
     * 
     * @return              a REG_SZ type string
     * @throws Exception
     */
    public String RegQueryValueEx(Integer handle, String value)
        throws Exception
    {
        byte[] b = (byte[]) winRegQueryValueEx.invoke(systemRoot, handle
                .intValue(), toByteArray(value));
                
        String data = b != null ? new String(b).trim() : null;        
        return data;
    }
    
    /**
     * The RegEnumKey function enumerates subkeys of the specified open registry key. 
     * 
     * @param handle        handle to key to query
     * @param subKeyIndex   index of subkey to query
     * @param maxKeyLength  size of subkey buffer
     *
     * @return  address of buffer for subkey name
     */    
    public String RegEnumKey(Integer handle, int subKeyIndex, int maxKeyLength)
        throws Exception
    {        
        byte[] b = (byte[]) winRegEnumKey.invoke(systemRoot, handle.intValue(), subKeyIndex, maxKeyLength);
                
        String data = b != null ? new String(b).trim() : null;        
        return data;
    }
    
    
    /**
     * Extract from the Windows Registry HKEY_LOCAL_MACHINE a String data value 
     * 
     * @param subKey    The registry subkey name
     * @param value     The registry subkey value
     * @return          The data value as tring
     * 
     * @throws Exception
     */
    public String getLocalMachineKeyValue(String subKey, String value)
        throws Exception
    {
                
        Integer hSettings = (Integer) openKey.invoke(systemRoot,
                toByteArray(subKey), KEY_READ, KEY_READ);
        
        byte[] b = (byte[]) winRegQueryValueEx.invoke(systemRoot, hSettings
                .intValue(), toByteArray(value));
                
        String data = b != null ? new String(b).trim() : null;
        
        //closeKey.invoke(Preferences.userRoot(), hSettings);

        return data;
        
    }
    
    /**
     * Extract from the Windows Registry HKEY_CURRENT_USER a String data value 
     * 
     * @param subKey    The registry subkey name
     * @param value     The registry subkey value
     * @return          The data value as String
     * 
     * @throws Exception
     */
    public String getCurrentUserKeyValue(String subKey, String value)
        throws Exception
    {
   
        Integer hSettings = (Integer) openKey.invoke(userRoot,
                toByteArray(subKey), KEY_READ, KEY_READ);
        
        
        byte[] b = (byte[]) winRegQueryValueEx.invoke(userRoot, hSettings
                .intValue(), toByteArray(value));
                
        //System.out.println("hSettings: " + hSettings);        
        //System.out.println("b:" + b);        
        //System.out.println(winRegQueryValueEx.invoke(userRoot, hSettings.intValue(), toByteArray(value)));
        
        String data = b != null ? new String(b).trim() : null;
        
        //closeKey.invoke(Preferences.userRoot(), hSettings);

        return data;
        
    }   
    
    
    /**
     * Delete from the Windows Registry HKEY_CURRENT_USER a String data value 
     * 
     * @param subKey    The registry subkey name
     * @param value     The registry subkey value
     * @return          The delete status (ERROR_SUCCESS, ...)
     * 
     * @throws Exception
     */
    public int deleteCurrentUserKeyValue(String subKey, String value)
    throws Exception
    {

        Integer hSettings = (Integer) openKey.invoke(userRoot,
                toByteArray(subKey), KEY_ALL_ACCESS, KEY_ALL_ACCESS);

        Integer rc = (Integer) WindowsRegDeleteValue.invoke(userRoot, hSettings.intValue(), toByteArray(value));

        return rc.intValue();

    }       
    
    /**
     * Extract from the Windows Registry HKEY_CURRENT_USER a DWORD data value 
     * 
     * @param subKey    The registry subkey name
     * @param value     The registry subkey value
     * @return          The DWORD data value as long
     * 
     * @throws Exception
     */    
    public long getCurrentUserKeyValueDword(String subKey, String value)
        throws Exception
    {
        RegistryJni registryJni = new RegistryJni();
        
        long myLong = 0;
        
        try
        {
            myLong = registryJni.getCurrentUserKeyValueDword(subKey, value);
        }
        catch (RuntimeException e)
        {
            System.out.println("Exception");
            throw new Exception(e);
        }
        
        return myLong;
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
    public String getCurrentUserKeyValueBinary(String subKey, String value)
    throws Exception
    {
        RegistryJni registryJni = new RegistryJni();
        
        String string = null;;
        
        try
        {
            string = registryJni.getCurrentUserKeyValueBinary(subKey, value);
        }
        catch (RuntimeException e)
        {
            System.out.println("Exception");
            throw new Exception(e);
        }
        
        if (string != null)
        {
            string = string.trim();
        }
        
        return string;        
    }
    
    /**
     * Extract from the Windows Registry HKEY_CLASSES_ROOT a String data value 
     * 
     * @param subKey    The registry subkey name
     * @param value     The registry subkey value
     * @return          The data value as String
     * 
     * @throws Exception
     */
    public  String getClassesRootKeyValue(String subKey, String value)
    throws Exception
    {
        RegistryJni registryJni = new RegistryJni();
        
        String string = null;;
        
        try
        {
            string = registryJni.getClassesRootKeyValue(subKey, value);
        }
        catch (RuntimeException e)
        {
            System.out.println("Exception");
            throw new Exception(e);
        }
        
        if (string != null)
        {
            string = string.trim();
        }
        
        return string;  
    }
    
    /**
     * 
     * @param handle    HKEY_CURRENT_USER, HKEY_LOCAL_MACHINE...
     * @param subKey    address of name of subkey to open
     * 
     * @return      true it the key exists
     */
    public boolean existsKey(int handle, String subKey)
        throws Exception
    {
        Integer hSettings = null;
        
        int intHandle = 0;
        
        if (handle == HKEY_CURRENT_USER )
        {
            intHandle = 1;
        }
        else if (handle == HKEY_LOCAL_MACHINE )
        {
            intHandle = 2;
        }
        else
        {
            throw new UnsupportedOperationException("Only HKEY_CURRENT_USER and HKEY_LOCAL_MACHINE are supported!");
        }
        
        RegistryJni registryJni = new RegistryJni();
        
        boolean exists = false;
        
        try
        {
            exists = registryJni.existsKey(intHandle, subKey);
        }
        catch (RuntimeException e)
        {
            System.out.println("Exception");
            throw new Exception(e);
        }
        
        return exists;
        
    }
    
    private static byte[] toByteArray(String str) {
        byte[] result = new byte[str.length() + 1];
        for (int i = 0; i < str.length(); i++) {
            result[i] = (byte) str.charAt(i);
        }
        result[str.length()] = 0;
        return result;
    }
    
    /**
     * 
     * @param args
     * @throws Exception
     */

    public static void main(String args[]) 
        throws Exception
    {
        
        //String javaLibraryPath = System.getProperty("java.library.path");
        //System.out.println("libPath: " + javaLibraryPath);
        
        //System.setProperty("java.library.path", 
        //                    javaLibraryPath + File.pathSeparatorChar + UrlUtil.getInstallDirectory());
        // 
        //javaLibraryPath = System.getProperty("java.library.path");
        //System.out.println("libPath: " + javaLibraryPath);
        
        Registry registry = new Registry();
        
        boolean existsKey = registry.existsKey(HKEY_LOCAL_MACHINE, "SOFTWARE\\Norton");
        
        System.out.println("existsKey: " + existsKey);
        if (true) return;
        
        //HKEY_CLASSES_ROOT\mailto\shell\open\command
        String result = registry.getClassesRootKeyValue("mailto\\shell\\open\\command", "");
        System.out.println("result: " + result);
                              
        String subKey = "Software\\Microsoft\\Windows\\CurrentVersion\\Internet SettingsApi";
        String value =  "ProxyEnable";       
        long proxyEnable = registry.getCurrentUserKeyValueDword(subKey, value);
        System.out.println("proxyEnable: " + proxyEnable);
                
        //int rc = registry.deleteCurrentUserKeyValue("Software\\cGeep", "semaphore");
        //System.out.println("rc: " + rc);
                
        
    }

    
}

/**
 * 
 */
