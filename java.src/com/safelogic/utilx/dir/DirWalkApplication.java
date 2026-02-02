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
package com.safelogic.utilx.dir;

// 22/07/02 18:35 GR - creation
// 25/07/02 10:45 GR - javadoc

import java.io.IOException;


/**
 * This interface defines the methods that are called back by
 * DirWalker when walkThough() is called, i.e. when the root
 * directory is browsed recursively.<br>
 * Classes that use the DirWalker must be an implementation
 * of this interface or provide a specific implementation.<br>
 * See DirDeleteTool for a simple exemple.
 */

public interface DirWalkApplication
{
	/**
	 * Handles the given file.
	 * @param	sFilePath	the path of the reported file
	 * @exception	IOException		if an i/o error occured
	 */
	
	public void handleFile(String sFilePath) throws IOException ;
	
	
	/**
	 * Handles the given directory.
	 * @param	sDirPath	the path of the reported directory
	 * @exception	IOException		if an i/o error occured
	 */
	
	public void handleDir(String sDirPath) throws IOException ;
	
	
	/**
	 * Handles unknown elements (type unknown or not existing element).
	 * @param	sPath	the path of the unknown element
	 * @exception	IOException		if an i/o error occured
	 */
	
	public void handleUnknown(String sPath) throws IOException ;
}
