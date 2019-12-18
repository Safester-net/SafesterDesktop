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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ProcessUtil {

    public static int countWindowsInstanceRunning(String programName) throws IOException {
        if (!SystemUtils.IS_OS_WINDOWS) {
            return 0;
        }

        if (programName == null) {
            throw new NullPointerException("programName can not be null!");
        }

        String line;
        List<String> pidInfoSet = new ArrayList<>();

        Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");

        BufferedReader input = null;

        try {
            input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {

                if (line.toLowerCase().contains(programName.toLowerCase())) {
                    pidInfoSet.add(line);
                }
            }
        } finally {
            IOUtils.closeQuietly(input);
        }
        
        return pidInfoSet.size();

    }

    /**
     *
     */
    protected ProcessUtil() {
    }

    /**
     * Says if the program name is running. Check is done independent of caps.
     *
     * @param programName	the program name to check
     * @return	true if the program name String is contained in task lists
     * @throws IOException
     */
    public static boolean isWindowsProgramRunning(String programName) throws IOException {

        if (!SystemUtils.IS_OS_WINDOWS) {
            return false;
        }

        if (programName == null) {
            throw new NullPointerException("programName can not be null!");
        }

        String line;
        Set<String> pidInfoSet = new TreeSet<>();

        Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");

        BufferedReader input = null;

        try {
            input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                pidInfoSet.add(line);
            }
        } finally {
            IOUtils.closeQuietly(input);
        }

        for (String pidInfo : pidInfoSet) {
            System.out.println("==> " + pidInfo);
        }

        for (String pidInfo : pidInfoSet) {
            if (pidInfo.toLowerCase().contains(programName.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

}
