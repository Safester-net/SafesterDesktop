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

import java.awt.Frame;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.safelogic.pgp.api.util.msg.MessagesManager;
import com.safelogic.utilx.io.stream.LineInputStream;

/**
 * @author Nicolas de Pomereu
 *
 * Class to call PgeepOutlookVersion.exe VB 6.0 program to retrieve the Office Outlook Version
 * 
 */

public class WindowsOutlookVersion
{    
    ///** The external program name to call */
    //private static String PROGRAM_NAME      = "PgeepOutlookVersion.exe";
    
    /** The file name containing the program result */
    private static String RESULT_FILE_NAME = "outlook_version.txt";
    
    /** The Message Manager */
    private MessagesManager messagesManager = new MessagesManager();
    
    /** The Registry sub key ProxyEnable value */
    private String outlookOfficeVersion = "";

    /**
     * Constructor
     * @param frame     the parent frame
     */
    public WindowsOutlookVersion(Frame frame)
    throws IOException
    {
        if(System.getProperty("os.name").indexOf("Windows") == -1)
        {
            return;
        }

        //23/04/07 16:30 NDP - useget OcxAbout.TempDir() to access temp directory            
        String tempDir = UrlUtil.getTempDir();
        File file = new File(tempDir + "\\" + RESULT_FILE_NAME);

        outlookOfficeVersion = "";

        if (file.exists())
        {
            BufferedInputStream bisIn = new BufferedInputStream(new FileInputStream(file)) ;
            LineInputStream in = new LineInputStream(bisIn) ;

            String inputLine = null;

            while ((inputLine = in.readLine()) != null) 
            {
                outlookOfficeVersion += inputLine;
            }
        }

    }

    /**
     * @return the outlookOfficeVersion
     */
    public String getOutlookOfficeVersion()
    {
        return outlookOfficeVersion;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
        throws IOException
    {
        WindowsOutlookVersion windowsOutlookVersion = new WindowsOutlookVersion(null);
        System.out.println(windowsOutlookVersion.getOutlookOfficeVersion());
    }
}

