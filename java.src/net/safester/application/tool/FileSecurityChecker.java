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

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JOptionPane;

import com.safelogic.pgp.api.util.msg.MessagesManager;
import com.safelogic.pgp.api.util.parms.PgpExtensions;

/**
 * @author Nicolas de Pomereu
 *
 * Allow to display a security message (warning) if a in/out file is locked by the
 * system for access. The lock will produce a FileNotFoundException on read/wwrite operation
 */
public class FileSecurityChecker
{

    public static String CR_LF = System.getProperty("line.separator") ;
    
    /** The parent component */
    private Component component;

    /** Messages Manager */
    private MessagesManager messages = new  MessagesManager();
    
    /**
     * Constructor
     */    
    public FileSecurityChecker(Component component)
    {
        this.component = component;
    }
        
    /**
     * Display a  warning message that says the crypto operation is impossible
     * <br>
     * If the user chooses to cancel the operation ==> stop the operation by throwing a 
     * new InterruptedException();
     * 
     * @param fileIn                the in file
     * @param fileOut               the out file
     * @param FileNotFoundException The FileNotFoundException
     */
    public void displayWarningAndAskForContinue(File fileIn, File fileOut, FileNotFoundException e)
        throws InterruptedException
    {
        
        // Ok test that the file is not locked in read mode
//      FileUtil fileUtil = new FileUtil();
//      if (! fileUtil.isUnlockedForRead(fileIn))
//      {
//          JOptionPane.showMessageDialog(this.m_jframe, messages.getMessage("FILE_IS_LOCKED_READ") 
//                  + CR_LF + fileIn);
//          continue;
//      }   
        
        String errorMessage = e.getMessage();
        
        int result = JOptionPane.YES_OPTION;
        
        Object[] options = { this.messages.getMessage("YES_BUTTON"), 
                             this.messages.getMessage("NO_BUTTON")};
        
        //L'opération de cryptage du fichier "
        //est impossible pour la raison :
        
        String messageHeader = null;
        
        if (PgpExtensions.isPgpEncrypted(fileOut))
        {
            messageHeader = this.messages.getMessage("encrypt_operation_of_file") ;
        }
        else
        {
            messageHeader = this.messages.getMessage("decrypt_operation_of_file") ;
        }
       
        result = JOptionPane.showOptionDialog(component, 
                     messageHeader
                    + CR_LF
                    + fileIn.getName()
                    + " ==> "
                    + fileOut.getName()
                    + CR_LF
                    + this.messages.getMessage("is_impossible_because") + " " + errorMessage 
                    + CR_LF
                    + CR_LF
                    + this.messages.getMessage("do_you_want_to_continue"), 
                
                this.messages.getMessage("WARNING_TITLE"), 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.WARNING_MESSAGE,
                null, 
                options, 
                options[0]);
        
        if(result == JOptionPane.NO_OPTION )        
        {
            throw new InterruptedException();
        }
        
    }    

}
