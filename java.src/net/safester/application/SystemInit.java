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
package net.safester.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * @author Nicolas de Pomereu
 *
 * Allows to redirect System.out & System.err to a formated file
 *
 */
public class SystemInit {

private static final String LOG_DIR = System.getProperty("user.home") + File.separator + "safester_log";

    /** the Out redirection path */
    private static String SAFESTER_OUT_LOG
                = LOG_DIR + File.separator + "Safester.out.log";

    /** the Err redirection path */
    private static String SAFESTER_ERR_LOG
                = LOG_DIR + File.separator + "Safester.err.log";

    /**
     * Redirect out and err to user.home/SafeShareIt.out.log &
     * user.home/SafeShareIt.err.log
     */
    public static void redirectOutAndErr()
    {
        // Do not do it in Netbeans
        if (isJavaEditor()) return;

        try {

            new File(LOG_DIR).mkdirs();

            File fileOut = new File(SAFESTER_OUT_LOG);

            //File fileOut2 = new File(SAFESTER_OUT_LOG + new Date().getTime() + ".txt");
            //if (fileOut.exists()) FileUtils.copyFile(fileOut, fileOut2);

            PrintStream printStream = new PrintStream(new FileOutputStream(fileOut));
            System.setOut(printStream);
            System.out.println(new Date());
            printSystemProperties();
            System.out.println();

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
            File fileOut = new File(SAFESTER_ERR_LOG);
            PrintStream printStream = new PrintStream(new FileOutputStream(fileOut));
            System.setErr(printStream);
            System.err.println(new Date());
            System.err.println();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @return true if we are in Netbeans or Eclipse
     */
    public static boolean isJavaEditor()
    {
        String classpath = System.getProperty("java.class.path");
        if (classpath == null || (! classpath.contains("NetBeans") && ! classpath.contains("eclipse")))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Print the System Properties on the file
     */
    private static void printSystemProperties()
    {
        Properties p = System.getProperties();
        Enumeration<Object> keys = p.keys() ;
        List<String> listKeys = new Vector<String>();

        while(keys.hasMoreElements())
        {
            String key      = (String)keys.nextElement() ;
            listKeys.add(key);
        }

        Collections.sort(listKeys);

        for(int i = 0; i< listKeys.size(); i++)
        {
            String key      = listKeys.get(i);
            String value    =  p.getProperty(key);

            System.out.println(key + ": " + value);
        }
    }

    /**
     * @return the SAFESTER_OUT_LOG
     */
    public static String getSAFESTER_OUT_LOG() {
        return SAFESTER_OUT_LOG;
    }

    /**
     * @return the SAFESTER_ERR_LOG
     */
    public static String getSAFESTER_ERR_LOG() {
        return SAFESTER_ERR_LOG;
    }

    public static String getLOG_DIR() {
        return LOG_DIR;
    }

    
}
