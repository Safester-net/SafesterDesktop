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

import java.io.IOException;

// java -cp SafeShareIt.jar net.safester.application.mac.MacOsXCommandsTest 2

/**
 *
 * @author Nicolas de Pomereu
 */
public class MacOsXCommandsTest {

    /**
     * @param args
     */
    public static void main(String[] args)  throws Exception
    {
        System.out.println("Before...");

        String help = "1: Test cp / 2: Test RealName Display / 3: Test Policy Files copy";
        
        if (args.length == 0)
        {
            System.out.println(help);
        }
        
        if (args[0].equals("1"))
        {
            testCp();
        }
        else if (args[0].equals("2"))
        {
            testRealNameDisplay();
        }    
        else if (args[0].equals("3"))
        {
            testPolicyFiles();
        }  
        else
        {
            System.out.println(help);            
        }

    }
    
    
    /**
     * Test the Policy Files installation
     */
    private static void testPolicyFiles()   throws Exception
    {
        MacOsXFullPolicyFiles macOsXFullPolicyFiles = new MacOsXFullPolicyFiles();
        macOsXFullPolicyFiles.tryToInstall();
    }


    /**
     * Display the Real Name from the username
     */
    private static void testRealNameDisplay() throws IOException
    {
        MacOsXCommands macOsXCommands = new MacOsXCommands();
        String realName = macOsXCommands.getRealNameFromUserName();
        System.out.println();
        System.out.println("macOsXCommands.getRealNameFromUserName() :" + realName + ":");
    }

    /**
     * @throws IOException
     */
    private static void testCp() throws IOException
    {
        String password = "loveme$";
        String fileIn   = "/Users/nicolasdepomereu/test.doc";
        String fileOut  = "/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home/lib/security/test77.doc";

        MacOsXCommands macOsXCommands = new MacOsXCommands();
        macOsXCommands.cpWithSudo(password, fileIn, fileOut);

        System.out.println("Done!");
    }
    
}
