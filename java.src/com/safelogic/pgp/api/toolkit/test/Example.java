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
package com.safelogic.pgp.api.toolkit.test;

import javax.swing.JOptionPane;

import com.safelogic.pgp.api.toolkit.CgeepApi;

public class Example
{
    /**
     * Main function for test only
     * @param args
     */
    public static void main(String [] args) throws Exception
    {
               
        // 1) Load cGeep API Instance
        CgeepApi cgeepApi = new CgeepApi();
        
        // 2) Encrypt a file (OpenPGP Symmetric Encryption)
        cgeepApi.encryptSymmetric("c:\\temp\\myFile.doc",
                                  "c:\\temp\\myFile.doc.pgp",
                                  "myPaspshrase".toCharArray(), 
                                  0);
        
        // 3) Test the result of the operation
        if (cgeepApi.isOperationOk())
        {
            JOptionPane.showMessageDialog(null, "Success!");
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Failure!");
            JOptionPane.showMessageDialog(null, 
                    "Error Code: " + cgeepApi.getErrorCode());
        }
    }
}

