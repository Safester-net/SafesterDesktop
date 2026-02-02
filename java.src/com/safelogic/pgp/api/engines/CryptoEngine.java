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
package com.safelogic.pgp.api.engines;

import java.io.File;
import java.util.List;



/**
 * Defines common methods for EncryptionEngine and DecryptionEngine
 * @author Nicolas de Pomereu
 *
 */
public interface CryptoEngine extends Runnable
{

    /** Maximum value of progression bar (defaults to 100)*/
    public static int MAXIMUM_PROGRESS = 100;
    
    /**
     * @return the current value of ProgressMonitor
     */
    public abstract int getCurrent();

    /**
     * @param current the current progression value to set by the task
     */
    public abstract void setCurrent(int current);

    /**
     * @return the note for the progression bar
     */
    public abstract String getNote();

    /**
     * @param note the note for the progression bar
     */
    public abstract void setNote(String note);

    /**
     * @return the out encrypted String
     */
    public String getOutString();
    
    /**
     * @return the list of files introduced in the crypto operation
     */
    public abstract List<File> getFilesIn();

    /**
     * @return the list of files produced as result of the crypto operation
     */
    public abstract List<File> getFilesOut();
    
    /**
     * @return the returnCode for the crypto operations
     */
    public abstract int getReturnCode();

    /**
     * @return the Operation Messages : one Operation Message per File
     */
    public List<String> getOperationMessages();
    
    /**
     * @return the message to display at end of operation(null if no message))
     */
    public abstract String getEndMessage();
    
    /**
     * @return the exception (null if no Exception was raised)
     */
    public abstract Exception getException();
    
    
    // Thread methods redefinition, because CryptoEngine extend Thread
    public void start();
    public void interrupt();
    

}
