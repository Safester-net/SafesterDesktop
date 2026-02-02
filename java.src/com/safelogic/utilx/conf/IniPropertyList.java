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
package com.safelogic.utilx.conf;

// 04/02/02 18:15 GR - creation

import java.io.File;
import java.io.IOException;

import com.safelogic.utilx.collection.PropertyList;
import com.safelogic.utilx.io.file.IniFileManager;


/**
 * Provides the same features than PropertyList and adds the
 * load(String) method (which isn't in PropertyList anymore).
 */

public class IniPropertyList
	extends PropertyList
{
	
	/**
	 * Loads the list contained in the file whose name (not path!) has been given.<br>
	 * @param		sFile			the file name containing the list
	 * @exception	IOException		if an I/O error occured
	 */

	public void load(String sFile)
		throws IOException
	{
        //String sFilePath = CSaIni.GetIniFilename(sFile);
		IniFileManager IniFileManager = new IniFileManager();
		String sFilePath = IniFileManager.getIniFilename(sFile);
		load(new File(sFilePath)) ;
	}

}
