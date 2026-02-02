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
package com.safelogic.utilx.io.file;

public class SeparatorManager
{        	
	/**
	 * Constructor
	 */
	
	void CmSeparatorManager()
	{
	}
	
	/**
	 * Add - if it NOT already exists - a separator to a Directory object
	 * @param sDir	the Directory
	 * @param	the Separator to add
	 * @return	the Directory with the separator
	 */
	
	public String addSeparator(String sDir, String sSep)
	{			
		sDir = sDir.trim();
		if (sDir.lastIndexOf(sSep) != sDir.length() - 1) {
			sDir += sSep;
		}
		
		return new String(sDir);
	}	
	
	/**
	 * Remove - if it already exists - a separator to a Directory object
	 * @param sDir	the Directory
	 * @param	the Separator to remove
	 * @return	the Directory without the separator
	 */
	
	public String removeSeparator(String sDir, String sSep)
	{			
		sDir = sDir.trim();
		if (sDir.lastIndexOf(sSep) == sDir.length() - 1) {
			sDir =  sDir.substring(0, sDir.length() - 1);
		}
		
		return new String(sDir);
	}	
	  	
         
}

// END
