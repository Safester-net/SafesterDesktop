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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Nicolas de Pomereu
 *
 */
public class MacOsXCommands
{
    /** debug infos */
    public static boolean DEBUG = false;
    
    /**
     * Default constructor
     */
    public MacOsXCommands()
    {
        
    }

    /**
     * Extract the Mac OX Real Name from the username.
     * @return
     */
    public String getRealNameFromUserName() throws IOException
    {        
        File tempFile = new File(getTempDir() + "RealName.txt");
        String username = System.getProperty("user.name");

        username = "\"" + username + "\"";
        
        /*
        dscl . -read "/Users/nicolasdepomereu" RealName
        RealName:
         The Real Name
        */
        
        String commandLine = "/usr/bin/dscl . -read /Users/{0} RealName | tee {1}";
        commandLine = MessageFormat.format(commandLine, username, tempFile.toString());

        // Create the temp file by running the exec
        macOsXRunExec(commandLine);

        // Extract the result from the tempfile
        String content = removeCrLf(tempFile);        
        //tempFile.delete();
        
        debug("content no CR/LF: " + content);
        
        content = StringUtils.substringAfter(content, ":");

        if (content != null)
        {
            content = content.trim();
        }
        return content;
        
    }

    /**
     * Remove the CR_LF from
     * @param in    the in string
     * @return  the string without any existing CR/LF
     * @throws IOException
     */
    private String removeCrLf(File file) throws IOException
    {
        BufferedReader br = null;
                
        try {
            StringBuilder text = new StringBuilder();
            br = new BufferedReader(new FileReader(file));

            String line = null;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }

            return text.toString();
        }
        finally
        {
            IOUtils.closeQuietly(br);
        }
    }

    /**
     * Execute a command line on Mac OS X.
     * @param commandLine
     * @throws IOException
     */
    private void macOsXRunExec(String commandLine) throws IOException
    {
        String[] theCommand = {"/bin/bash", "-c", commandLine};

        String flatCommand = "";
        for (String string : theCommand) {
            flatCommand += string + " ";
        }

        debug("");
        debug(flatCommand);
        debug("");
        
        @SuppressWarnings(value = "unused")
        Process p = Runtime.getRuntime().exec(theCommand);
        
        try {
            p.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(MacOsXCommands.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    
    public static String getTempDir()
    {
        String tempDir = System.getProperty("user.home");
        
        if (!tempDir.endsWith(File.separator)) {
            tempDir += File.separator;
        }

        tempDir += "safeshare_temp" + File.separator;

        File fileTempDir = new File(tempDir);
        fileTempDir.mkdirs();
        
        return tempDir;
    }

    /**
     * Copy a file with sudo cp
     * 
     * @param password      the sudo password
     * @param srcFile       the src file
     * @param destFile      the dest file
     * @throws IOException
     */
    public void cpWithSudo(String password, String srcFile, String destFile)
        throws IOException
    {
        
        if (srcFile == null)
        {
            throw new IllegalArgumentException("srcFile can not be null!");
        }
        
        if (destFile == null)
        {
            throw new IllegalArgumentException("destFile can not be null!");
        }
        
        if ( ! new File(srcFile).exists())
        {
            throw new FileNotFoundException("srcFile does not exists: " + srcFile);
        }        
        
//        File file = new File(destFile);        
//        if  (file.getParentFile().isDirectory())
//        {
//            throw new FileNotFoundException("desfFile parent directory does not exists: " + file);
//        }
                
        String commandLine = "echo \"{0}\" | sudo -S cp \"{1}\" \"{2}\"";        
        commandLine = MessageFormat.format(commandLine, password, srcFile, destFile);

        macOsXRunExec(commandLine);
                 
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

