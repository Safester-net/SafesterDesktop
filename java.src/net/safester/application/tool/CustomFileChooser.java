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


/**
 * Wrapper for JFileChooser because of Sun's bug:
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6317789.
 * <br>
 * Will create a JFileChooser with FileConfirmer.useShellFolder set to false
 * until the bug is corrected in a future version
 * 
 * @author Nicolas de Pomereu
 */
public class CustomFileChooser
{
    /** the FileConfirmer.useShellFolder setting */
    public static final Boolean USE_SHELL_FOLDER = Boolean.FALSE;
    
    /**
     * Hidden constructor
     *
     */
    protected CustomFileChooser()
    {
        
    }
    
    /**
     * @return UseShellFolder setting
     */
    public static Boolean getUseShellFolder()
    {
        return USE_SHELL_FOLDER;
    }
        
    
    
}

/**
 * 
 */
