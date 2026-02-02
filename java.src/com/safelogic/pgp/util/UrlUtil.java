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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.safelogic.pgp.api.util.crypto.PgpUserId;
import com.safelogic.pgp.api.util.msg.LanguageManager;
import com.safelogic.pgp.api.util.parms.Parms;

/**
 * Utilities related to URL for cGeep:
 * <br> - Get cGeep web site address
 * <br> - Launch a Browser from Java
 * <br> - Wrap actions with utility method
 *  
 * @author Nicolas de Pomereu
 *
 */
public class UrlUtil
{
    private static final String US_EXPORT_POLICY_JAR = "US_export_policy.jar";

    private static final String LOCAL_POLICY_JAR = "local_policy.jar";

    /** PGP Key subdirectory */
    public static final String PGP_KEY_SUBDIR   = "cGeep";

    /** The secret key ring name */
    public static final String SECRING_SKR      = "secring.skr";

    /** The public key ring name */
    public static final String PUBRING_PKR      = "pubring.pkr";

    /** The Package that contains all properties messages & html files */
    public static String SYSTEM_FILES_PACKAGE = "com.safelogic.pgp.system.files";
    public static String SYSTEM_FILES_PACKAGE_JAVA7 = "com.safelogic.pgp.system.files.java7";

    /** Status of the Web Start System Property. if "true", we are in Web start */
    public static final String CGEEP_WEBSTART_ON = "cgeep_webstart_on";

    private static final String HELP_IS_AVAILABLE_ON_FRENCH 
    = "Please apologize. Help is available only in french for this version.";

    // Wrap Actions 
    public static final String ACTION_CHECK_NEW_VERSION  = "ACTION_CHECK_NEW_VERSION";
    public static final String ACTION_HELP               = "ACTION_HELP";
    public static final String ACTION_INVITE_FRIEND      = "ACTION_INVITE_FRIEND";
    public static final String ACTION_KEY_HELP           = "ACTION_KEY_HELP";

    //public static final String ACTION_KEY_SEARCH         = "ACTION_KEY_SEARCH";
    //public static final String ACTION_REGISTER_BEGIN     = "ACTION_REGISTER_BEGIN";
    //public static final String ACTION_DELETE_SERVER_KEY  = "ACTION_DELETE_SERVER_KEY";

    public static final String ACTION_OPEN_PGEEP_COM     = "ACTION_OPEN_PGEEP_COM";
    public static final String ACTION_OPEN_PGEEP_COM_PRODUCTS   = "ACTION_OPEN_PGEEP_COM_PRODUCTS";
    public static final String ACTION_BUY_PRO               = "ACTION_BUY_PRO";
    public static final String ACTION_WHATS_NEW             = "ACTION_WHATS_NEW";

    // File Separator
    public static final String SEP = System.getProperty("file.separator");
    
    // Key directory used by CgeepApi
    private static String keyDirectory = null;
    /**
     * @return true if we are in Web Start mode
     */

    public static boolean isWebStartOn()
    {
        //if (true) return true;

        String webStartOn = System.getProperty(CGEEP_WEBSTART_ON);

        if (webStartOn != null && webStartOn.toLowerCase().equals("true"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 
     * @return the <java-home>/lib/security directory
     */
    public static String getJavaHomeLibSecurityDir()
    {
        String javaHome = System.getProperty("java.home") + File.separator;   

        //<java-home>\lib\security\
        String libSecurity = javaHome + "lib" + File.separator + "security" + File.separator; 

        return libSecurity;
    }

    /**
     * Copy the local_policy.jar & US_export_policy.jar contained as resources files in cgeep_app.jar to 
     * <java-home>/lib/security.
     * 
     * @return  true if the 2 files have been correctly copied.
     */

    public static boolean copyNonRestricedPolicyFilesToJavaHomeLibSecurity()
    {
	
      String libSecurity = getJavaHomeLibSecurityDir();
      
      //JOptionPane.showMessageDialog(null, "libSecurity : " + libSecurity);
      
      if (! copyResourceFileToDir(LOCAL_POLICY_JAR, libSecurity))
      {
      	return false;
      }

      if (! copyResourceFileToDir(US_EXPORT_POLICY_JAR, libSecurity))
      {
          return false;
      }
	
      return true;
      
//        String tempDir = System.getProperty("java.io.tmpdir") + File.separator;
//        //<java-home>\lib\security\
//        String libSecurity = getJavaHomeLibSecurityDir();
//        
//        if (! copyResourceFileToDir(LOCAL_POLICY_JAR, tempDir))
//        {
//        	return false;
//        }
//
//        if (! copyResourceFileToDir(US_EXPORT_POLICY_JAR, tempDir))
//        {
//            return false;
//        }
//        
//        Sha1 sha1 = new Sha1();
//        String hashTempLocalPolicy = null;
//        String hashTempUsExportPolicy = null;
//
//        String hashJavaHomeLibLocalPolicy = null;
//        String hashJavaHomeLibUsExportPolicy = null;        
//        
//        String originalLocalPolicy = libSecurity + LOCAL_POLICY_JAR;
//        File localPolicy = new File(originalLocalPolicy);
//
//        if(!localPolicy.exists()){
//        	if(System.getProperty("java.vm.name").contains("OpenJDK")){
//        		return true;
//        	}
//        }
//
//        
//        
//        try
//        {
//            hashTempLocalPolicy     = sha1.getHexFileHash(tempDir + LOCAL_POLICY_JAR);
//            hashTempUsExportPolicy  = sha1.getHexFileHash(tempDir + US_EXPORT_POLICY_JAR);
//            
//            hashJavaHomeLibLocalPolicy = sha1.getHexFileHash(libSecurity + LOCAL_POLICY_JAR);
//            hashJavaHomeLibUsExportPolicy = sha1.getHexFileHash(libSecurity + US_EXPORT_POLICY_JAR);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            return false;
//        }
//        
//        // re-copy the file ontly if it's different
//        if (! hashTempLocalPolicy.equals(hashJavaHomeLibLocalPolicy))
//        {
//            if (! copyResourceFileToDir(LOCAL_POLICY_JAR, libSecurity))
//            {
//                return false;
//            }
//        }
//        // re-copy the file ontly if it's different
//        if (! hashTempUsExportPolicy.equals(hashJavaHomeLibUsExportPolicy))
//        {
//            if (! copyResourceFileToDir(US_EXPORT_POLICY_JAR, libSecurity))
//            {
//                return false;
//            }
//            
//        }
//
//
//        return true;
    }

    /**
     * This method copy the content of a file stored as a resource in the cgeep_app.jar in 
     * the com.safelogic.pgp.msg.files
     * 
     * @param fileInResource    the raw file name as container in the the com.safelogic.pgp.msg.files
     *                          package.
     *                          Example : cgeep_local_policy.jar
     *                          
     * @param destinationDir    the destination directory terminated with a file separator
     *                          Example : c:\tmp\
     * 
     * @return  true if the 2 files have been correclty copied.
     */
    public static boolean copyResourceFileToDir(String fileInResource, String destinationDir)
    {

        //String resource = "/" + SYSTEM_FILES_PACKAGE;     
	
	String javaVersion = System.getProperty("java.version");

	String resource = null;
	
	if (javaVersion.compareTo("1.7") > 0) {
	    resource = "/" + SYSTEM_FILES_PACKAGE_JAVA7;   
	}
	else 
	{
	    resource = "/" + SYSTEM_FILES_PACKAGE;  
	}	
                
        resource = resource.replace(".", "/");

        String urlResource = resource + "/" + fileInResource;                     
        java.net.URL myURL = UrlUtil.class.getResource(urlResource);

        InputStream in = null;
        FileOutputStream out = null;

        try
        {
            in = myURL.openStream();    

            File outFile = new File(destinationDir + fileInResource);
            out  = new FileOutputStream(outFile);

            IOUtils.copy(in, out);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }


    /**
     * Return the base URL for cGeep.
     * <br>
     * Value depends on the existence of the fileIn user.home/appdata/pgeep/pgeep_localhost.txt
     * @return the Pgeep base URL
     */
    public static String getCgeepUrl()
    {       
        String keyDir = UrlUtil.getKeyDirectory();
        String sep    = System.getProperty("file.separator");

        File pgeepLocalhost_Txt = new File(keyDir + sep + "cgeep_localhost.txt");

        if (pgeepLocalhost_Txt.exists())
        {
            System.out.println("WARNING: fileIn " + pgeepLocalhost_Txt + " exists! ");
            System.out.println("Web server is: " + Parms.URL_LOCALHOST);
            System.out.println("Delete this fileIn for www.cgeep.com use: " + pgeepLocalhost_Txt);
            return Parms.URL_LOCALHOST;
        }
        else
        {
            return Parms.URL_CGEEP_COM;
        }       
    }

    /**
     * Return the base UNSECURE http not S URL for cGeep.
     * <br>
     * Value depends on the existence of the fileIn user.home/appdata/pgeep/pgeep_localhost.txt
     * @return the Pgeep base URL
     */
    public static String getCgeepUrlUnsecure()
    {
        String cgeepUrlUnsecure = getCgeepUrl();
        cgeepUrlUnsecure = cgeepUrlUnsecure.replace("https://", "http://");
        return cgeepUrlUnsecure;
    }



    /**
     * @return the shop URL
     */
    public static String getUrlForShop()
    {
        // Shop  Url
        // get the language to send it to web server
        String language_param= "?language=" + LanguageManager.getLanguage(); 

        // base url
        String url = getCgeepUrlUnsecure() + "/do/ProShopRedirect" + language_param;
        return url;
    }

    /**
     * @return the License Dwonload URL
     */
    public static String getUrlForEvaluationLicense()
    {
        // get the language to send it to web server
        String language_param= "?language=" + LanguageManager.getLanguage(); 

        // base url
        String url = getCgeepUrlUnsecure() + "/do/ProRegisterBegin" + language_param;
        return url;
    }

    /**
     * 
     * @return   the file corresponding to the cGeep Pro License File
     */
    public static File getProLicenseFile()
    {              
        String sep          = System.getProperty("file.separator");   

        String cGeepDir = null;
        cGeepDir = UrlUtil.getSystemUserHome() 
        + sep + UrlUtil.PGP_KEY_SUBDIR  + sep;

        File fCGeepDir = new File(cGeepDir);

        if (! FileUtil.createDirectory(fCGeepDir))
        {
            return null;
        }   

        String license = cGeepDir +  Parms.LICENSE_FILE;

        File licenseFile = new File(license);
        return licenseFile;

    }

    /**
     * Return the cGeep install directory
     * @return the cGeep install directory
     * @throws Exception
     */
    public static String getCgeepApiInstallDirectory()
    throws Exception
    {
        Registry registry = new Registry();
        String subKey = "SOFTWARE\\cGeepApi";
        String value  = "installDir";
        String installDirectory = registry.getLocalMachineKeyValue(subKey, value);

        if (installDirectory == null)
        {
            throw new IllegalArgumentException("installDirectory is null!");
        }
  
        return installDirectory;
    }
    
    /**
     * Return the cGeep install directory
     * @return the cGeep install directory
     * @throws Exception
     */
    public static String getInstallDirectory()
    throws Exception
    {
        Registry registry = new Registry();
        String subKey = "SOFTWARE\\" + Parms.PRODUCT_NAME;
        String value  = "RuntimeLib";
        String pgeppDll = registry.getLocalMachineKeyValue(subKey, value);

        if (pgeppDll == null)
        {
            throw new IllegalArgumentException("cgeppDll is null!");
        }

        String installDirectory = new File(pgeppDll).getParent();       

        return installDirectory;
    }

    /**
     * Wrapper class to get system User Home.
     * <br>
     * This seems *stupid*, because user.home exists on all OS, but is mandatory
     * because of cGeep historic reasons when we thought we would be only Windows
     * and used APPDATA as user Home.
     * 
     * @return  the User Home: APPDATA on Windows or user.home of others
     */
    public static String getSystemUserHome()
    {
        if(System.getProperty("os.name").indexOf("Windows") != -1)
        {
            // Application Data. Historic reason to be maintened
            return System.getenv("APPDATA");
        }
        else
        {
            // Linux and MacIntosh
            return System.getProperty("user.home");
        }
    }

    /**
     * @return the cGeep Temp Directory as APPDATA\<product name>\temp
     */
    public static String getTempDir()
    {
        // 03/10/07 18:50 NDP - Better & cleaner getTempDir(): no more Windows references
        String sep          = System.getProperty("file.separator");   
        
        String tempDir = null;
        tempDir = UrlUtil.getSystemUserHome() 
                 + sep + UrlUtil.PGP_KEY_SUBDIR  + sep + "Temp" + sep;
        
        File fTempDir = new File(tempDir);
                
        if (! FileUtil.createDirectory(fTempDir))
        {
            return null;
        }        
        
        return tempDir;
    }    
    
    /**
     * Returns the key directory.
     * Defaults to APPDATA\cGeep on Windows and user.home/cGeep on Unix/mac 
     * 
     * @return Returns the User Key Directory. 
     */
    public static String getKeyDirectory()
    {       
        //Api mode keyDirectory is set
        if(keyDirectory != null)
        {
            return keyDirectory;
        }
        
        String sep          = System.getProperty("file.separator");      

        String userHome     = getSystemUserHome();

        String keyDirectory = null;

        // Check if there is a key directory
        keyDirectory = UserPreferencesManager.getKeyDirectory();

        // If null, use the default one: APPDATA\cGeep
        if (keyDirectory == null)
        {
            keyDirectory = userHome + sep + UrlUtil.PGP_KEY_SUBDIR;
        }

        // Patch for cGeep Directory if the user has old pGeep Version
        if (! new File(keyDirectory).exists())
        {
            keyDirectory = userHome + sep + "pGeep";
        }                

        // Try to find the KeyDirectory if it's still 
        if (! new File(keyDirectory).exists())
        {
            keyDirectory = userHome + sep + UrlUtil.PGP_KEY_SUBDIR; // Back to new name

            // If the key is on a amovible device, find the real unit
            if (UserPreferencesManager.getUseAmovibleDevice())
            {
                keyDirectory = UrlUtil.getKeyDirectoryWithCorrectRoot(keyDirectory);
            }
        }
        //Always store keyDirectory in registry
        UserPreferencesManager.setKeyDirectory(keyDirectory);

        return keyDirectory;
    }

    public static void setKeyDirectory(String directory)
    {
        keyDirectory = directory;
    }
    
    /**
     * Get the key directory with the correct unit.
     * <br>
     * <br> - List all system roots/units.
     * <br> - Test the existence of the key directory with all units
     * <br> - Retrun the first correct sirectory found, aka with existing secring.skr
     * @param keyDirectory
     * @return
     */
    public static String getKeyDirectoryWithCorrectRoot(String keyDirectory)
    {     
        String sep = System.getProperty("file.separator");
        String keyDirectoryWithCorrectRoot = keyDirectory;

        File [] files = File.listRoots();

        for (int i = 0; i < files.length; i++)
        {

            String dirNoRoot = keyDirectory.substring(3);

            keyDirectoryWithCorrectRoot = files[i] + dirNoRoot;

            System.out.println("files[i]                   : " + files[i]); 
            System.out.println("keyDirectoryWithCorrectRoot: " + keyDirectoryWithCorrectRoot); 

            File secring = new File(keyDirectoryWithCorrectRoot + sep + UrlUtil.SECRING_SKR);

            if (secring.exists())
            {
                return keyDirectoryWithCorrectRoot;
            }
        }

        return keyDirectoryWithCorrectRoot;
    }

    /**
     * Checks keyring existance
     * 
     * @param userId keyring owner
     * @return true if keyring ok false otherwise
     */
    public static boolean keyringExist()
    {                                 
        String fileName = PgpUserId.getPrivKeyRingFilename();            
        File fKeyring = new File(fileName);           
        boolean keyringOk = fKeyring.exists();
        
        return keyringOk;
    }      
}
