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
package net.safester.application.updater;


//
// WARNING : THIS CLASS MUT NOT DO IMPORT OF ANY LIBRARIES THAT ARE NOT IN RAW JRE
//

//
// WARNING : THIS CLASS MUT BE COMMON AND DUPLICATED (COPIED) INTO SAFESHAREITLAUNCHER PROJECT 
//

import java.io.File;

import javax.swing.JOptionPane;

/**
 * Defines installation parameters and java parameters.
 * 
 * This class source code is into twto projects safeShareIt & safeShareItLa
 * 
 * Do NOT use any external library (because used in SafeShareItLauncher.jar that must be standalone)
 *  
 */
public class InstallParameters {

    public static boolean DEBUG = false;
    

    /** The Launcher Jar */
    public static final String SAFESTER_LAUNCHER_JAR = "SafesterLauncher.jar";

    /** The main Jar to launch */
    public static final String SAFESTER_JAR = "Safester.jar";

    public static final String JAVA_1_6 = "1.6";

    /** The minimal Java version */
    public static final String JAVA_1_5 = "1.5";

   
    /**
     *
     * @return the installation dir (where the SafeShareIt.jar) is installed
     */
    public static String getInstallationDir()
    {
        String installationDir = System.getProperty("java.class.path");
        
        // Do NOT use any external library (because used in SafeShareItLauncher.jar that must be standalone)
        //installationDir = StringUtils.substringBeforeLast(installationDir, File.separator);
    
        // Remove /SafeShareIt.jar or \SafeShareIt.jar
        installationDir = installationDir.substring(0, installationDir.lastIndexOf(File.separator)+ 1);
                
        if(!installationDir.endsWith(File.separator)){
            installationDir += File.separator;
        }
    
        return installationDir;
    }

    public static boolean isWindowsOs() {
        String os = System.getProperty("os.name");
        return os.toLowerCase().contains("windows");
    }

    public static boolean isMacOs() {
        String os = System.getProperty("os.name");
        return os.toLowerCase().contains("mac");
    }
    
    /**
     * @return the jave executable path
     */
    public static String getJavaExecutablePath() {
        String rawJavaExe = "java";

        if (isWindowsOs())
        {
            rawJavaExe = "java.exe";
        } else {
            rawJavaExe = "java";
        }

        String javaExe = System.getProperty("java.home");

        if (DEBUG) JOptionPane.showMessageDialog(null, javaExe);
        
        // For Mac, we take always the 1.6 version
        if (isMacOs())
        {
            javaExe = getJava6HomeFromCurrentOnMac();
        }

        if (!javaExe.endsWith(File.separator)) {
            javaExe += File.separator;
        }

        javaExe += "bin" + File.separator + rawJavaExe;

        //InstallParameters: fix bug: must surround java with quotes on Windows, not Mac!
        if (isWindowsOs())
        {
            javaExe = "\"" + javaExe + "\"";
        }

        return javaExe;

    }

    /**
     * @return the home of Java 1.6 from the current java.home
     */
    public static String getJava6HomeFromCurrentOnMac() {
        String javaHome_1_6 = null;

        String version = System.getProperty("java.version");
        String javaHomeCurent = System.getProperty("java.home");

        if (version.compareTo(InstallParameters.JAVA_1_6) >= 0) {
            debug("javaHomeCurent: " + javaHomeCurent);
            return javaHomeCurent;
        } else if (javaHomeCurent.contains("1.5.0")) {
            javaHome_1_6 = javaHomeCurent;
            javaHome_1_6 = javaHome_1_6.replaceAll("/1.5.0/", "/1.6.0/");

            File javaHomeFile = new File(javaHome_1_6);

            debug("javaHomeFile: " + javaHomeFile);

            if (javaHomeFile.exists() && javaHomeFile.isDirectory()) {
                return javaHome_1_6;
            }

            // Java 16 is not installed
            return null;
        } else {
            debug("null!");
            // All other cases
            return null;
        }
    }


     public static void debug(String str) {
        if (DEBUG)
            System.out.println("dbg> " + str);
    }
}
