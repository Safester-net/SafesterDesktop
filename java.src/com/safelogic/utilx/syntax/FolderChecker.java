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
package com.safelogic.utilx.syntax;

public class FolderChecker   
{
    
	// Maximum folder size
	public static final int FOLDER_MAX_LENGTH = 60;
	
    // Login 
    private String m_sFolder;
    

    /**
     * Constructor()
     * @param sFolder    Folder to check
     */
    public FolderChecker (String sFolder)
    {
        this.m_sFolder = sFolder;
    }
    
    
	/**
	*
	* Verifies that the Folder has a valid form
	* <br>
	* @return	true if the Login is valid
	*
	*/
                                    
	public boolean isValid()
	{   
        boolean bVerify = false;
        
        StringChecker cmSchecker = new StringChecker();
        bVerify = cmSchecker.isAllowed(this.m_sFolder, StringChecker.PATTERN.FOLDER);
        
		if (this.m_sFolder.length() > FOLDER_MAX_LENGTH)
		{
			bVerify = false;
		}
		
		return bVerify;
		//if (! bVerify)
        //    return false;
       
        // First char *must* not be alphanumeric
		//return	cmSchecker.isFirstAllowed(this.m_sFolder, 
		//									  StringChecker.PATTERN.ALPHA_NUM);
	}
  
}

// END
