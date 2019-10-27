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
package net.safester.application.mac;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;

import com.safelogic.pgp.api.util.crypto.Sha1;
import com.safelogic.pgp.util.UrlUtil;

/**
 * Class to install full policy files for Mac OS X
 * 
 * @author Nicolas de Pomereu
 *
 */
public class MacOsXFullPolicyFiles
{
    /** debug infos */
    public static boolean DEBUG = false;
    
    public static final String US_EXPORT_POLICY_JAR = "US_export_policy.jar";
    public static final String LOCAL_POLICY_JAR = "local_policy.jar";
    
    /** The temp dir where the full policy files are going to be dropped from resources files in memory */
    private String tempDir      = null;
    
    /** The lib security dir* where the full policy files must be installed */
    private String libSecurityDir  = null;

    
    /**
     * Constructor
     */
    public MacOsXFullPolicyFiles()
    {
        tempDir         = MacOsXCommands.getTempDir();
        libSecurityDir  = getJavaHomeLibSecurityDir();        
    }


    /**
     * @return the tempDir
     */
    public String getTempDir() {
        return tempDir;
    }

    /**
     * @return the libSecurityDir
     */
    public String getLibSecurityDir() {
        return libSecurityDir;
    }
    
    
    public boolean tryToInstall()
        throws Exception
    {
        // Dump the policy file
        boolean dumpDone = dumpFullPolicyFilesToTempDir();
        
        if (DEBUG) JOptionPane.showMessageDialog(null, "dump done: " + dumpDone);
        
        if (! dumpDone)
        {
            debug("Dump not done!");
            return false;
        }

        // If policy files are already installed ==> Nothing to do!
        if (isFullPolicyFileInstalled(US_EXPORT_POLICY_JAR) && isFullPolicyFileInstalled(LOCAL_POLICY_JAR))
        {
            debug("Policy Files are correctly installed");
            return true;
        }

        // False: policy files are not installed. Try to install using password.
        String realName = null;
        
        try {
            realName = new MacOsXCommands().getRealNameFromUserName();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (realName == null || realName.isEmpty())
        {
            realName = System.getProperty("user.name");
        }        
            // 1) Get the password:
        MacOsXPasswordAsk macOsXPasswordAsk = new MacOsXPasswordAsk(null, realName);
        
        if (macOsXPasswordAsk.isCancelled())
        {
            return false;
        }
        else
        {
            // Success, install done in MacOsXPasswordAsk
            return true;
        }
    }
    
    /**
     * 
     * @return the <java-home>/lib/security directory
     */
    private String getJavaHomeLibSecurityDir()
    {
        String javaHome = System.getProperty("java.home") + File.separator;   

        //<java-home>\lib\security\
        String libSecurity = javaHome + "lib" + File.separator + "security" + File.separator; 

        return libSecurity;
    }
    
    /**
     * Dump the full policy files to java.io.tmpdir
     * @return  true if the dump have been done without Exception
     * @throws IOException 
     */
    private boolean dumpFullPolicyFilesToTempDir() throws IOException 
    {                
        if (! copyResourceFileToDir(LOCAL_POLICY_JAR, tempDir))
        {
            return false;
        }

        if (! copyResourceFileToDir(US_EXPORT_POLICY_JAR, tempDir))
        {
            return false;
        }
        
        return true;
    }
    
    
    public boolean isFullPolicyFileInstalled(String policyJar)
        throws IOException, NoSuchAlgorithmException, NoSuchProviderException
    {
        Sha1 sha1 = new Sha1();
        String hashTempPolicy = null;
        String hashJavaHomeLibPolicy = null;  

        File localFile = new File(getTempDir() + policyJar);
        if (! localFile.exists())
        {
            throw new FileNotFoundException("Temp Policy file does not exists: " + localFile);
        }
        
        File securityLibFile = new File(getLibSecurityDir() + policyJar);
        if (! securityLibFile.exists())
        {
            System.out.println("Security Lib Policy file does not exists: " + securityLibFile);
            return false;
        }
        
        hashTempPolicy       = sha1.getHexFileHash(getTempDir() + policyJar);
        hashJavaHomeLibPolicy = sha1.getHexFileHash(getLibSecurityDir() + policyJar);

        if (hashTempPolicy.equals(hashJavaHomeLibPolicy))
        {
            return true;
        }
        else
        {
            return false;
        }
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
     * @throws IOException 
     */
    private boolean copyResourceFileToDir(String fileInResource, String destinationDir) throws IOException
    {
	
        //String resource = "/" + UrlUtil.SYSTEM_FILES_PACKAGE; 
        
	String javaVersion = System.getProperty("java.version");
	String resource = null;

	if (javaVersion.compareTo("1.7") > 0) {
	    resource = "/" + UrlUtil.SYSTEM_FILES_PACKAGE_JAVA7;   
	}
	else 
	{
	    resource = "/" + UrlUtil.SYSTEM_FILES_PACKAGE;  
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
        finally
        {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
    
    /**
     * debug tool
     */
    private void debug(String s)
    {
        if (DEBUG)
            System.out.println(this.getClass().getName() 
                    + " " 
                    + new java.util.Date() 
                    + " "
                    + s);
    }    
}

