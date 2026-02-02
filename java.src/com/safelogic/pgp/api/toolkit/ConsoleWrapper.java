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
package com.safelogic.pgp.api.toolkit;

import java.io.IOException;

import com.safelogic.pgp.api.util.java6Emul.Java5Console;


/**
 * This class prompts the user for a password.
 * 
 * This is ans emulator for Java 1.6 java.io.Console
 */

public class ConsoleWrapper 
{

    public ConsoleWrapper()
    {        
        
    }
    
    /**
     *  Return a line from the console
     * @return
     */
    public String readLine()
    {
        //java.io.Console console = System.console();
        //return console.readLine();
        
        Java5Console console = new Java5Console();
        return console.readLine();
        
    }    
        
    /**
     * 
     * @return  a password read on the console
     * 
     * @throws IOException
     */
    public char[] readPassword() 
    {
        //java.io.Console console = System.console();
        //return console.readPassword();    
        
        Java5Console console = new Java5Console();
        
        try
        {
            return console.readPassword();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    
}
 


