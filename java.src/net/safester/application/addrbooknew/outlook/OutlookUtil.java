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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * Uses fast raw C# user.dir/OutlookChecker.exe call  to test if outlook is installed.
 * Call is only done on Windows, of course.
 *
 * @author Nicolas de Pomereu
 */
public class OutlookUtil {

    /**
     * Says if Outlook Office is installed
     * @return true if Outlook Office is installed, else false
     * @throws IOException 
     */
    public static boolean isOutlookInstalled() throws IOException {

        if (!SystemUtils.IS_OS_WINDOWS) {
            return false;
        }

        String programFileStr = getOutlookCheckerProgram();
        
        String line;

        Process p = Runtime.getRuntime().exec(programFileStr);

        BufferedReader input = null;

        try {
            input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                if (line.trim().contains("true") || line.trim().contains("false")) {
                    break;
                }
            }
        } finally {
            IOUtils.closeQuietly(input);
        }

        if (line == null) {
            throw new IllegalArgumentException("line does not contain \"true\" or \"false\".");
        }

        if (line.trim().contains("true")) {
            return true;
        } else {
            return false;
        }

    }

     /**
     * Gets the Outlook Offiversion if installed.
     * @return the Outlook Offiversion if installed, else null
     * @throws IOException 
     */
    public static String getOutlookVersion() throws IOException {

        if (!SystemUtils.IS_OS_WINDOWS) {
            return null;
        }
        
        if (! isOutlookInstalled()) {
            return null;
        }

        String programFileStr = getOutlookCheckerProgram();
        
        String line = "";

        Process p = Runtime.getRuntime().exec(programFileStr + " -v");

        BufferedReader input = null;

        try {
            input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                line+= line.trim();
            }
        } finally {
            IOUtils.closeQuietly(input);
        }

        return line;

    }
    
    
    
    /**
     * Returns the path to OutlookChecker.exe
     * @return the path to OutlookChecker.exe
     * @throws FileNotFoundException 
     */
    private static String getOutlookCheckerProgram() throws FileNotFoundException {
        String userDir = SystemUtils.USER_DIR;
        if (!userDir.endsWith(File.separator)) {
            userDir += File.separator;
        }
        String programFileStr = userDir + "OutlookChecker.exe";
        if (! new File(programFileStr).exists()) {
            throw new FileNotFoundException("OutlookChecker.exe not found! Please reinstall client software.");
        }
        return programFileStr;
    }
}
