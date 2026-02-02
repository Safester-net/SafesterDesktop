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

import java.util.prefs.Preferences;

import com.safelogic.pgp.api.util.parms.CmPgpCodes;
import com.safelogic.pgp.api.util.parms.PgpExtensions;

/**
 * Peferences Manager for cGeep
 * <br>
 * Stores the User Preferences
 * @author Nicolas de Pomereu
 */
public class UserPreferencesManager
{
    /** The Return Line when a Email is send */
     private static int DEFAULT_AUTO_RETURN_LINE = 79;
     
     /** The Default Outlook send policy */
     
     private static int DEFAULT_OUTLOOK_POLICY = CmPgpCodes.OL_ENCRYPT_ON_SEND_WITH_CONFIRM;
     
     private static String DEFAULT_FTP_SERVER = "";
     
     private static int DEFAULT_FTP_PORT = 21;
     
     private static int USE_DEFAULT_PORT = 0;
     
     private static String DEFAULT_DEST_DIR = "/";
     
     private static int USE_ANONYMOUS = 1;
     
     private static String DEFAULT_LOGIN ="";
     
     private static String DEFAULT_PWD = "";
          
     private static int AUTO_DECRYPT =  CmPgpCodes.AUTO_DECRYPT;
     
     private static String DEFAULT_ATTACH_DIR = "";
     
     private static int DEFAULT_SAVE_ENCRYPT = CmPgpCodes.SAVE_ENCRYPT;
     
     public static int DEFAULT_ATTACHMENT_POLICY = CmPgpCodes.KEEP_ATTACH;
     
     private static boolean DEFAULT_WARN = true;
     
     private static int DEFAULT_NB_PASSES = 3;
     
     private static boolean DEFAULT_ASK_EVAL = true;
     
     private static boolean DEFAULT_DO_NOT_WARN = false;
     
     private static boolean DEFAULT_CONNECT_TO_KEY_SERVER = true;
     
     /** Displays the Key Server */   
     public static final String DISPLAY_KEY_SERVER       = "DISPLAY_KEY_SERVER";
     
     // New preferences
     public static String DEFAULT_DIRECTORY     = "DEFAULT_DIRECTORY";
     public static String NO_DISPLAY_OK_MESSAGE = "NO_DISPLAY_OK_MESSAGE";
     
     
     public static String HIDE_OUTLOOK_BUTTONS_BAR = "HIDE_OUTLOOK_BUTTONS_BAR";

     public static String KEY_SERVER_TIMEOUT   = "KEY_SERVER_TIMEOUT";

     /** Encoding */
     public static String USER_ENCODING     = "DEFAULT_ENCODING";
          
     /** Three most important ones */
     public static final String ISO_8859_1    = "ISO-8859-1";
     public static final String UTF_8         = "UTF-8";
     public static final String KOI8_R        = "KOI8-R";
     
     public static final String DEFAULT_ENCODING = ISO_8859_1;
     
     public static String AUTODETECT_ENCODING = "AUTODETECT_ENCODING";
     
     public static String SAVE_CLEAR_COPY_OF_SENT_EMAIL = "SAVE_CLEAR_COPY_OF_SENT_EMAIL";
     
     /**
      * Set preference
      */ 
     public static void setPreference(String prefName, String prefValue)
     {
         UserPreferencesManager userPreferencesManager = new UserPreferencesManager();        
         Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());  
         
         prefs.put(prefName,  prefValue);
     }
     
        
     /**
      * Get a  preference
      * @param prefName  the preference name
      * @return the preference value
      */ 
     public static String getPreference(String prefName)
     {
         UserPreferencesManager userPreferencesManager = new UserPreferencesManager();        
         Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());  
         
         return prefs.get(prefName, null);
     }    
     
     /**
      * Get a  boolean preference
      * @param prefName  the preference name
      * @return the preference value  
      */ 
     public static boolean getBooleanPreference(String prefName)
     {        
        String preferenceStr = getPreference(prefName);
        
        boolean preference = false;
        
        try
        {
            preference = Boolean.parseBoolean(preferenceStr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        
        return preference;
    }

     /**
      * Get an Integer preference
      * @param prefName  the preference name
      * @return the preference value  
      */ 
     public static int getIntegerPreference(String prefName)
     {        
        String preferenceStr = getPreference(prefName);
        
        int preference = 0;
        
        try
        {
            preference = Integer.parseInt(preferenceStr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        
        return preference;
    }     
     
     /**
      * @param prefName     the preference name
      * @param prefValue    the preference value
      */
     public static void setPreference(String prefName, boolean prefValue)
     {
         setPreference(prefName, Boolean.toString(prefValue));
     }  
     
     
     
     /**
      * @return the useAmovibleDevice from the Preferences
      */
     public static boolean getUseAmovibleDevice()
     {        
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
        String strUseAmovibleDevice = prefs.get("useAmovibleDevice",  Boolean.toString(false));
        
        boolean useAmovibleDevice = false;
        
        try
        {
            useAmovibleDevice = Boolean.parseBoolean(strUseAmovibleDevice);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        
        return useAmovibleDevice;
    }

     /**
      * @param useAmovibleDevice the useAmovibleDevice to set
      */
     public static void setUseAmovibleDevice(boolean useAmovibleDevice)
     {
         UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
         
         Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
         prefs.put("useAmovibleDevice", Boolean.toString(useAmovibleDevice));
     }  
     /**
      * @return the key directory. If null, no key directory is set
      */
     public static String getKeyDirectory()
     {
         UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
         
         Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
         String strUseSystemProxies = prefs.get("keyDirectory",  null);
         
         return strUseSystemProxies;
     }
     
     /**
      * 
      * @param keyDirectory     the key directory
      */
     public static void setKeyDirectory(String keyDirectory)
     {
         UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
         
         Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
         prefs.put("keyDirectory", keyDirectory);
     }
     
    /**
     * @return the autoReturnLine from the Preferences
     */
    public static int getAutoReturnLine()
    {        
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
        String strAutoReturnLine = prefs.get("autoReturnLine",  "" + DEFAULT_AUTO_RETURN_LINE);
        
        int autoReturnLine = DEFAULT_AUTO_RETURN_LINE;
        
        try
        {
            autoReturnLine = Integer.parseInt(strAutoReturnLine);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        
        return autoReturnLine;
    }

    /**
     * @param autoReturnLine the autoReturnLine to set
     */
    public static void setAutoReturnLine(int autoReturnLine)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("autoReturnLine",  "" + autoReturnLine);
    }

    /**
     * 
     * @param olPolicy the oultook send policy
     */
    public static void setOutlookPolicy(int olPolicy)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("olSendPolicy", "" + olPolicy);
    }

    public static int getOutlookPolicy()
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
        String strOlPolicy = prefs.get("olSendPolicy",  "" + DEFAULT_OUTLOOK_POLICY);
        
        int olPolicy = DEFAULT_OUTLOOK_POLICY;
        
//        if(!LicencingProcess.getIsPro())
//        {
//            return olPolicy;
//        }
        
        try
        {
            olPolicy = Integer.parseInt(strOlPolicy);
            
            // For old users:
            if (olPolicy == CmPgpCodes.OL_ASK_ON_SEND_OBSOLETE)
            {
                olPolicy = CmPgpCodes.OL_ENCRYPT_ON_SEND_WITH_CONFIRM;
            }            
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        
        return olPolicy;
    }
    
    public static void setFtpServer(String ftpServer)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("ftpServer", ftpServer);
    }
    
    public static String getFtpServer()
    {
        String ftpServer = DEFAULT_FTP_SERVER;
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
        ftpServer = prefs.get("ftpServer",  DEFAULT_FTP_SERVER);
        
        return ftpServer;
    }

    public static void setFtpPort(int port)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("ftpPort", "" + port);
    }
    
    public static int getFtpPort()
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
        String strOlPolicy = prefs.get("ftpPort",  "" + DEFAULT_FTP_PORT);
        
        int ftpPort = DEFAULT_FTP_PORT;
        
        try
        {
            ftpPort = Integer.parseInt(strOlPolicy);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        
        return ftpPort;
    }
    
    public static void setUseDefaultPort(int useDefaultPort)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("useDefaultPort", "" + useDefaultPort);
    }
    
    public static int getUseDefaultPort()
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
        String strUseDefaultPort = prefs.get("useDefaultPort",  "" + USE_DEFAULT_PORT);
        
        int useDefaultPort = USE_DEFAULT_PORT;
        
        try
        {
            useDefaultPort = Integer.parseInt(strUseDefaultPort);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        
        return useDefaultPort;
    }
    
    
    public static void setDefaultDir(String defaultDir)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("defaultDir", defaultDir);
    }
    
    public static String getDefaultDir()
    {
        String defaultDir = DEFAULT_DEST_DIR;
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
        defaultDir = prefs.get("defaultDir",  DEFAULT_DEST_DIR);
        
        return defaultDir;
    }
    
    public static void setUseAnonymous(int useAnonymous)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("useAnonymous", "" + useAnonymous);
    }
    
    public static int getUseAnonymous()
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
        String strUseAnonymous = prefs.get("useAnonymous",  "" + USE_ANONYMOUS);
        
        int useAnonymous = USE_ANONYMOUS;
        
        try
        {
            useAnonymous = Integer.parseInt(strUseAnonymous);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        
        return useAnonymous;
    }
    
    
    public static void setLogin(String login)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("login", login);
    }
    
    public static String getLogin()
    {
        String login = DEFAULT_LOGIN;
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
        login = prefs.get("login",  DEFAULT_LOGIN);
        
        return login;
    }
    
    public static void setPwd(String password)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("pwd", password);
        
        System.out.println("put pwd: " + password);
    }
    
    public static String getPwd()
    {
        String password = DEFAULT_PWD;
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
        password = prefs.get("pwd",  DEFAULT_PWD);
        
        System.out.println("get pwd: " + password);
        
        return password;
    }
        
    public static int getAutoDecrypt()
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        
        String strAutoDecrypt = prefs.get("autoDecrypt", "" + AUTO_DECRYPT);
        
        int autoDecrypt = AUTO_DECRYPT;
        
//        if(!LicencingProcess.getIsPro())
//        {
//            return autoDecrypt;
//        }
        
        try
        {
            autoDecrypt = Integer.parseInt(strAutoDecrypt);
        }
        catch( NumberFormatException e)
        {
            e.printStackTrace();
        }
        
        return autoDecrypt;
    }
    
    public static void setAutoDecrypt(int autoDecrypt)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("autoDecrypt", "" + autoDecrypt);
    }
        
    public static void setSaveEncrypt(int saveEncrypt)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("saveEncrypt", "" + saveEncrypt);
    }
    
    public static int getSaveEncrypt()
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());

        String strSaveEncrypt = prefs.get("saveEncrypt", "" + DEFAULT_SAVE_ENCRYPT);
        int saveEncrypt = DEFAULT_SAVE_ENCRYPT;
        
//        if(!LicencingProcess.getIsPro())
//        {
//            return saveEncrypt;
//        }
        
        try{
            saveEncrypt = Integer.parseInt(strSaveEncrypt);
        }
        catch(NumberFormatException e)
        {
            e.printStackTrace();
        }
        return saveEncrypt;
    }
    
    /**
     * @return the useSystemProxies from the Preferences
     */
    public static boolean getUseProxy()
    {        
       UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
       
       Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
       String strUseSystemProxies = prefs.get("useSystemProxies",  Boolean.toString(false));
       
       boolean useProxy = false;
       
       try
       {
           useProxy = Boolean.parseBoolean(strUseSystemProxies);
       }
       catch (NumberFormatException e)
       {
           e.printStackTrace();
       }
       
       return useProxy;
   }

    /**
     * @param useSystemProxy the useSystemProxies to set
     */
    public static void setUseProxy(boolean useSystemProxies)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("useSystemProxies", Boolean.toString(useSystemProxies));
    }   
    
    public static int getAttachmentPolicy()
    {
        int attachPolicy = DEFAULT_ATTACHMENT_POLICY;
        
//        //If non pro version return default value
//        if(!LicencingProcess.getIsPro())
//        {
//            return attachPolicy;
//        }
        
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());

        String strAttachPolicy = prefs.get("attachPolicy", "" + DEFAULT_ATTACHMENT_POLICY);
        
        try{
            attachPolicy = Integer.parseInt(strAttachPolicy);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return attachPolicy;
    }
    
    public static void setAttachmentPolicy(int policy)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("attachPolicy", Integer.toString(policy));
        
    }
    
    public static String getAttachmentStoreDir()
    {
        String attachDir = null;
//        if(!LicencingProcess.getIsPro())
//        {
//            return null;
//        }
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        
        attachDir = prefs.get("attachmentStoreDir", DEFAULT_ATTACH_DIR);
        
        return attachDir;
    }
    
    public static void setAttachmentStoreDir(String attachDir)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("attachmentStoreDir", attachDir);
    }
    
    public static boolean getWarnBeforeWipe()
    {
        boolean warnBeforeWipe = DEFAULT_WARN;
        
//        if(!LicencingProcess.getIsPro())
//        {
//            return warnBeforeWipe;
//        }
        
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        
        String strWarn = prefs.get("warnWipe", "" + DEFAULT_WARN);
        
        try{
            warnBeforeWipe = Boolean.parseBoolean(strWarn);
        }
        catch(NumberFormatException e)
        {
            e.printStackTrace();
        }
        return warnBeforeWipe;
    }
    
    public static void setWarnBeforeWipe(boolean warnBefore)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("warnWipe",  Boolean.toString(warnBefore));
    }
    
    public static int getNbPasses()
    {
        int nbPasses = DEFAULT_NB_PASSES;
//        if(!LicencingProcess.getIsPro())
//        {
//            return nbPasses;
//        }
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        
        String strNb = prefs.get("nbPasses", "" + DEFAULT_NB_PASSES);
        
        try{
            nbPasses = Integer.parseInt(strNb);
        }
        catch(NumberFormatException e)
        {
            e.printStackTrace();
        }
        return nbPasses;
    }
    
    public static void setNbPasses(int nb)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("nbPasses", Integer.toString(nb));
    
    }
    
    /**
     * @return the doNotWarnOnWordSelected boolean from the Preferences
     */
    public static boolean getDoNotWarnOnWordSelected()
    {        
       UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
       
       Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
       String strDoNotWarnOnWordSelected = prefs.get("doNotWarnOnWordSelected",  Boolean.toString(false));
       
       boolean doNotWarnOnWordSelected = false;
       
       try
       {
           doNotWarnOnWordSelected = Boolean.parseBoolean(strDoNotWarnOnWordSelected);
       }
       catch (NumberFormatException e)
       {
           e.printStackTrace();
       }
       
       return doNotWarnOnWordSelected;
   }

    /**
     * @param doNotWarnOnWordSelected the doNotWarnOnWordSelected to set
     */
    public static void setDoNotWarnOnWordSelected(boolean doNotWarnOnWordSelected)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("doNotWarnOnWordSelected", Boolean.toString(doNotWarnOnWordSelected));
    }
    
        
    /**
     * @return the doNotWarnOnInvalidLicense boolean from the Preferences
     */
    public static boolean getDoNotWarnOnInvalidLicense()
    {        
       UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
       
       Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
       String str = prefs.get("doNotWarnOnInvalidLicense",  Boolean.toString(false));
       
       boolean result = false;
       
       try
       {
           result = Boolean.parseBoolean(str);
       }
       catch (NumberFormatException e)
       {
           e.printStackTrace();
       }
       
       return result;
   }

    /**
     * @param bool the doNotWarnOnInvalidLicense to set
     */
    public static void setDoNotWarnOnInvalidLicense(boolean bool)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("doNotWarnOnInvalidLicense", Boolean.toString(bool));
    }    
    
    /**
     * @return the passiveMode from the Preferences
     */
    public static boolean getPassiveMode()
    {        
       UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
       
       Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
       String strPref = prefs.get("passiveMode",  Boolean.toString(false));
       
       boolean boolPref = false;
       
       try
       {
           boolPref = Boolean.parseBoolean(strPref);
       }
       catch (NumberFormatException e)
       {
           e.printStackTrace();
       }
       
       return boolPref;
   }

    /**
     * @param useSystemProxy the useSystemProxies to set
     */
    public static void setPassiveMode(boolean boolPref)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("passiveMode", Boolean.toString(boolPref));
    } 
    
    
    /**
     * @return true if the Socks proxy is set to be used
     */
    public static boolean getUseSocksProxy()
    {        
       UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
       
       Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
       String strPref = prefs.get("useSocksProxy",  Boolean.toString(false));
       
       boolean boolPref = false;
       
       try
       {
           boolPref = Boolean.parseBoolean(strPref);
       }
       catch (NumberFormatException e)
       {
           e.printStackTrace();
       }
       
       return boolPref;
   }

    /**
     * @param useSystemProxy the useSystemProxies to set
     */
    public static void setUseSocksProxy(boolean boolPref)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("useSocksProxy", Boolean.toString(boolPref));
    } 
        
    /**
     * @return true if cGeep must be started at Windows Startup.
     * 
     * WARNING: Default value is true. We want cGeep to be present in task bar!
     */
    public static boolean getStartOnWindowsStartup()
    {        
       UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
       
       Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
       String strPref = prefs.get("startOnWindowsStartup",  Boolean.toString(true));
       
       boolean boolPref = false;
       
       try
       {
           boolPref = Boolean.parseBoolean(strPref);
       }
       catch (NumberFormatException e)
       {
           e.printStackTrace();
       }
       
       return boolPref;
   }
    
    /**
     * @param boolPref the boolean prefrence to set for Start cGeep on Windows Startup
     */
    public static void setStartOnWindowsStartup(boolean boolPref)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("startOnWindowsStartup", Boolean.toString(boolPref));
    } 
    
    public static String getDefaultKey()
    {
    	String key = null;
    	UserPreferencesManager userPreferenceManager = new UserPreferencesManager();
    	Preferences prefs = Preferences.userNodeForPackage(userPreferenceManager.getClass());
    	key = prefs.get("defaultKey", null);
    	
    	return key;
    }
    
    public static void setDefaultKey(String keyId)
    {
    	UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("defaultKey", keyId);
    }
    
    public static void setCachePassphrase(boolean cachePassphrase)
    {
    	UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
    	
    	Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
    	prefs.put("cache", Boolean.toString(cachePassphrase));
    }
    
    public static boolean cachePassphrase()
    {
    	boolean cachePassphrase = true;
    	
//    	if(!LicencingProcess.getIsPro())
//    	{
//    		return cachePassphrase;
//    	}
//    	else
//    	{
//    		cachePassphrase = true;
//    	}
    	
    	UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
    	Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
    	String strPref = prefs.get("cache", Boolean.toString(cachePassphrase));
    	//JOptionPaneCustom.showMessageDialog(null, "cache" + strPref);
    	try{
    		cachePassphrase = Boolean.parseBoolean(strPref);
    	}
    	catch(NumberFormatException e)
    	{
    		e.printStackTrace();
    	}
    	return cachePassphrase;
   }
    
    public static String getProxyAddress()
    {
        String s = null;
        UserPreferencesManager userPreferenceManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferenceManager.getClass());
        s = prefs.get("proxyAddress", null);
        
        return s;
    }
    
    public static void setProxyAddress(String s)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        
        if (s == null)
        {
            prefs.remove("proxyAddress");
        }
        else
        {
            prefs.put("proxyAddress", s);
        }
    }
    
    public static String getProxyPort()
    {
        String s = null;
        UserPreferencesManager userPreferenceManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferenceManager.getClass());
        s = prefs.get("proxyPort", null);
        
        return s;
    }
    
    public static void setProxyPort(String s)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        
        if (s == null)
        {
            prefs.remove("proxyPort");
        }
        else
        {
            prefs.put("proxyPort", s);
        }
    }      
    
    /**
     * @return the useProxyAuth from the Preferences
     */
    public static boolean getUseProxyAuth()
    {        
       UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
       
       Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
       String strUseSystemProxies = prefs.get("useProxyAuth",  Boolean.toString(false));
       
       boolean useSystemProxies = false;
       
       try
       {
           useSystemProxies = Boolean.parseBoolean(strUseSystemProxies);
       }
       catch (NumberFormatException e)
       {
           e.printStackTrace();
       }
       
       return useSystemProxies;
   }

   
    /**
     * @param useProxyAuth the useProxyAuth to set
     */
    public static void setUseProxyAuth(boolean useProxyAuth)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("useProxyAuth", Boolean.toString(useProxyAuth));
    }  
    
    
    /**
     * @return the proxy user name for authenttication (if any)
     */
    public static String getProxyUsername()
    {        
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());    
         
        return  prefs.get("proxy_username", null);

    }
    
    /**
     * @param the proxy address to set (if any)
     */
    public static void setProxyUsername(String proxyUsername)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
                
        if (proxyUsername == null)
        {
            return;
        }
        
        prefs.put("proxy_username",  proxyUsername);
    }
    
    /**
     * @return the proxy address (if any)
     */
    public static String getProxyPassword()
    {        
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());    
         
        return  prefs.get("proxy_password", null);
    }
    
    /**
     * @param the proxy address to set (if any)
     */
    public static void setProxyPassword(String proxyPassword)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
                
        if (proxyPassword == null)
        {
            return;
        }
        
        prefs.put("proxy_password",  proxyPassword);
    }    
    
    
    /**
     * @return the doNotAskImport boolean from the Preferences
     */
    public static boolean getDoNotAskImport()
    {        
       UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
       
       Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());               
       String strDoNotWarnOnWordSelected = prefs.get("doNotAskImport",  Boolean.toString(false));
       
       boolean doNotAskImport = false;
       
       try
       {
    	   doNotAskImport = Boolean.parseBoolean(strDoNotWarnOnWordSelected);
       }
       catch (NumberFormatException e)
       {
           e.printStackTrace();
       }
       
       return doNotAskImport;
   }

    /**
     * @param doNotAskImport the doNotAskImport to set
     */
    public static void setDoNotAskImport(boolean doNotAskImport)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("doNotAskImport", Boolean.toString(doNotAskImport));
    }
    
    
    
    public static boolean getAskForEval()
    {
        boolean askForEval = DEFAULT_ASK_EVAL;
        return false;
//        
//        if(LicencingProcess.getIsPro())
//        {
//        	//Always return false if already in pro!!!!
//            return false;
//        }
//        
//        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
//        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
//        
//        String strAsk = prefs.get("askForEval", "" + DEFAULT_ASK_EVAL);
//        
//        try{
//            askForEval = Boolean.parseBoolean(strAsk);
//        }
//        catch(NumberFormatException e)
//        {
//            e.printStackTrace();
//        }
//        return askForEval;
    }
    
    public static void setAskForEval(boolean askForEval)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("askForEval",  Boolean.toString(askForEval));
    }
    
    public static boolean getAskForContinueEval()
    {
    	boolean askForEval = DEFAULT_ASK_EVAL;
    	return false;
//    	
//	    if(LicencingProcess.getIsPro())
//	    {
//	    	//Always return false if already in pro!!!!
//	        return false;
//	    }
//	    
//	    UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
//	    Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
//	    
//	    String strAsk = prefs.get("askForContinueEval", "" + DEFAULT_ASK_EVAL);
//	    
//	    try{
//	        askForEval = Boolean.parseBoolean(strAsk);
//	    }
//	    catch(NumberFormatException e)
//	    {
//	        e.printStackTrace();
//	    }
//	    return askForEval;
	}
	
	public static void setAskForContinueEval(boolean askForEval)
	{
	    UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
	    Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
	    prefs.put("askForContinueEval",  Boolean.toString(askForEval));
	}
	
	
    public static boolean getDoNotWarn5Days()
    {
//    	boolean warn = DEFAULT_DO_NOT_WARN;
//    
//	    if(LicencingProcess.isProPerpetual())
//	    {
//	    	//Always return false if already in pro!!!!
//	        return false;
//	    }
//	    
//	    UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
//	    Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
//	    
//	    String strWarn = prefs.get("doNotWarn5Days", "" + DEFAULT_DO_NOT_WARN);
//	    
//	    try{
//	        warn = Boolean.parseBoolean(strWarn);
//	    }
//	    catch(NumberFormatException e)
//	    {
//	        e.printStackTrace();
//	    }
//	    return warn;
    	return true;
	}
	
	public static void setDoNotWarn5Days(boolean warn)
	{
	    UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
	    Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
	    prefs.put("doNotWarn5Days",  Boolean.toString(warn));
	}
	
    public static boolean getDoNotWarn2Days()
    {
//    	boolean warn = DEFAULT_DO_NOT_WARN;
//    
//	    if(LicencingProcess.getIsPro())
//	    {
//	    	//Always return false if already in pro!!!!
//	        return false;
//	    }
//	    
//	    UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
//	    Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
//	    
//	    String strWarn = prefs.get("doNotWarn2Days", "" + DEFAULT_DO_NOT_WARN);
//	    
//	    try{
//	        warn = Boolean.parseBoolean(strWarn);
//	    }
//	    catch(NumberFormatException e)
//	    {
//	        e.printStackTrace();
//	    }
//	    return warn;
    	return true;
	}
	
	public static void setDoNotWarn2Days(boolean warn)
	{
	    UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
	    Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
	    prefs.put("doNotWarn2Days",  Boolean.toString(warn));
	}
	
    public static boolean getDoNotWarn1Day()
    {
//    	boolean warn = DEFAULT_DO_NOT_WARN;
//    
//	    if(LicencingProcess.getIsPro())
//	    {
//	    	//Always return false if already in pro!!!!
//	        return false;
//	    }
//	    
//	    UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
//	    Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
//	    
//	    String strWarn = prefs.get("doNotWarn1Days", "" + DEFAULT_DO_NOT_WARN);
//	    
//	    try{
//	        warn = Boolean.parseBoolean(strWarn);
//	    }
//	    catch(NumberFormatException e)
//	    {
//	        e.printStackTrace();
//	    }
//	    return warn;
    	return true;
	}
	
	public static void setDoNotWarn1Day(boolean warn)
	{
	    UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
	    Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
	    prefs.put("doNotWarn1Days",  Boolean.toString(warn));
	}
	
    /**
     * @return the default file extension for encryption(.cgeep if none)
     */
    public static String getDefaultEncryptionExtension()
    {        
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());    
         
        return  prefs.get("default_encryption_extension", PgpExtensions.DEFAULT_CGEEP_ENCRYPTION_EXT);
    }
    
    /**
     * @param the default file extension for encryption to set
     */
    public static void setDefaultEncryptionExtension(String defaultEncryptionExtension)
    {
        UserPreferencesManager userPreferencesManager = new UserPreferencesManager();        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
                
        if (defaultEncryptionExtension == null)
        {
            return;
        }
        
        prefs.put("default_encryption_extension",  defaultEncryptionExtension);
    }
	
    /**
     * 
     * @param connect true if user wants to always connect to server false otherwise
     */
    public static void setDefaultConnectToKeyServer(boolean connect)
    {
    	UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
        
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
        prefs.put("connectToKeyServer", Boolean.toString(connect));
    }
    
    public static boolean getDefauktConnectToKeyServer()
    {
    	boolean connect = DEFAULT_CONNECT_TO_KEY_SERVER;
    	UserPreferencesManager userPreferencesManager = new UserPreferencesManager();
    	Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());
    	String strConnect = prefs.get("connectToKeyServer", "" + DEFAULT_CONNECT_TO_KEY_SERVER);
    	try{
	        connect = Boolean.parseBoolean(strConnect);
	    }
	    catch(NumberFormatException e)
	    {
	        e.printStackTrace();
	    }
	    return connect;
    }


    /**
     * @return  the default encoding chosen by the user (defaults to "ISO-8859-1")       
     */
    public static String getUserEncoding()
    {
        String userEncoding = getPreference(USER_ENCODING);
    
        if(userEncoding == null || userEncoding.length() == 0)
        {
            userEncoding = DEFAULT_ENCODING;
        }
        
        return userEncoding;
    }
}
