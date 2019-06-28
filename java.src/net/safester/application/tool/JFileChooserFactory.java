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
package net.safester.application.tool;

import java.io.File;

/**
 * Wrapper class to JFileChooser
 * <br>
 * Done because there is a bug in Java 1.5.0_06 that prevents file selection if there is
 * a recursiv shortcut in the directory to choose in.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class JFileChooserFactory
{
    /**
     * Constructor
     */
    protected JFileChooserFactory()
    {

    }
    
    /**
     * Get the JFileChooser instance
     * <br>
     * Will load a normal JFileChooser, except if the file use_shell_folder.txt
     * exisre in the cGeep keys Directory
     * @return
     */
    public static JFileChooserMemory getInstance()
    {
        String userHome = System.getProperty("user.home");
        String sep      = System.getProperty("file.separator");
                
        File use_shell_folder_txt = new File(userHome + sep + "use_shell_folder.txt");
        
        if (use_shell_folder_txt.exists())
        {
            // Special Light File Chooser 
            JFileChooserMemory jFileChooserMemory = new JFileChooserMemory() {
                public void updateUI() {
                    putClientProperty("FileChooser.useShellFolder", CustomFileChooser.USE_SHELL_FOLDER);
                    super.updateUI();
                }
            };
            
            return jFileChooserMemory;
        }
        else
        {
            // Normal File Chooser 
            JFileChooserMemory jFileChooserMemory = new JFileChooserMemory();        
            return jFileChooserMemory;
        }

    }
}

