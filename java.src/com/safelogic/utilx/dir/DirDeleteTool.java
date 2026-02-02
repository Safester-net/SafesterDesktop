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

// 24/07/02 10:30 GR - creation
// 24/07/02 11:45 GR - OK
// 25/07/02 10:40 GR - javadoc

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;


/**
 * This class is a tool for directory deletion.
 * It deletes the files and subdirectories recursively and then
 * deletes the main directory.
 */

public class DirDeleteTool
{
	// static services
	
	/**
	 * Deletes the given directory.
	 * @param	sDirPath	the path of the dir to delete
	 * @exception	IOException		if an I/O error occured
	 */
	
	public static void Delete(String sDirPath)
		throws IOException
	{
		DirDeleteTool ddt = new DirDeleteTool() ;
		ddt.deleteDir(sDirPath) ;
	}
	
	// fields
	
	/**	The files vector */
	private Vector m_vFiles ;
	
	/**	The directories stack */
	private Stack m_stDirs ;
	
	// constructor
	
	/**
	 * Creates a new deletion tool.
	 */
	
	public DirDeleteTool()
	{
		reset() ;
	}
	
	// services
	
	/**
	 * Resets the deletion tool state.
	 */
	
	private void reset()
	{
		m_vFiles = new Vector() ;
		m_stDirs = new Stack() ;
	}
	
	
	/**
	 * Deletes the specified directory.
	 * @param	sDirPath	the path of the dir to delete
	 * @exception	IOException		if an I/O error occured
	 */
	
	public void deleteDir(String sDirPath)
		throws IOException
	{
		reset() ;
		//m_stDirs.push(new File(sDirPath)) ;	// not required (sent back by Walker)
		DirWalker dw = new DirWalker(new DeleteApplication(), true, true) ;
		dw.walkThrough(sDirPath) ;
		removeAll() ;
	}
	
	
	/**
	 * Removes all the files and directories currently refenreced in
	 * the fields Vector and Stack.
	 * @exception	IOException		if an I/O error occured
	 */
	
	private void removeAll()
		throws IOException
	{
		Enumeration eFiles = m_vFiles.elements() ;
		File fFile ;
		while(eFiles.hasMoreElements())
		{
			fFile = (File)eFiles.nextElement() ;
			if(!fFile.delete())
				throw new IOException("Can't delete file: " + fFile.getAbsolutePath()) ;
		}
		
		File fDir ;
		while(!m_stDirs.empty())
		{
			fDir = (File)m_stDirs.pop() ;
			if(!fDir.delete())
				throw new IOException("Can't delete dir: " + fDir.getAbsolutePath()) ;
		}
	}
	
	
	/**
	 * This class is the implementation of DirWalkApplication
	 * that allows to be called back by the DirWalker when walking
	 * through the directory recursively.
	 */
	
	private class DeleteApplication
		implements DirWalkApplication
	{
		/**
		 * Implementation of handleFile():
		 * adds the given file to the inner Vector.
		 */
		
		public void handleFile(String sFilePath)
			throws IOException
		{
			File fFile = new File(sFilePath) ;
			m_vFiles.addElement(fFile) ;
		}
		
		/**
		 * Implementation of handleDir():
		 * adds the given dir to the inner Stack.
		 */
		
		public void handleDir(String sDirPath)
			throws IOException
		{
			File fDir = new File(sDirPath) ;
			m_stDirs.push(fDir) ;
		}
		
		
		/**
		 * Implementation of handleUnknown():
		 * do nothing.
		 */
		
		public void handleUnknown(String sPath)
			throws IOException
		{
			// do nothing
		}
	}
}
