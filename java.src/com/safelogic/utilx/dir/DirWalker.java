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
// 24/07/02 11:55 GR - OK
// 25/07/02 11:00 GR - javadoc

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Stack;


/**
 * This class allows to walk through a directory recursively
 * and get the subdirectories and files.<br>
 * This works with a call-back mechanism: calling classes must
 * provide a DirWalkApplication implementation whose method
 * will be called back when DirWalker walkThrough() method is
 * called.<br>
 * See DirDeleteTool for a simple exemple. 
 */

public class DirWalker
{
	// fields
	
	/**	The dir walk application to call back */
	private DirWalkApplication m_dwaApp ;
	
	/**	The directories path stack */
	private Stack m_stPath ;
	
	/**	Indicates if the DirWalker is in the first round of the recursive call */
	private boolean m_isFirst ;
	
	/**	Indicates if absolute or relative path must be used */
	private boolean m_useAbsolute ;
	
	/**	Indicates whether the files in the root directory must be returned or not */
	private boolean m_returnRootFile ;
	
	// constructor
	
	/**
	 * Creates a new DirWalker which will use the given DirWalkApplication
	 * implementation when walking through directories.
	 * @param	dwaApp			the dir walk application to call back
	 * @param	useAbsolute		true to use absolute path, false to use relative path
	 * @param	returnRootFile	true if the files in root directories must be returned
	 *							false otherwise
	 */
	
	public DirWalker(DirWalkApplication dwaApp,
					 boolean useAbsolute,
					 boolean returnRootFile)
	{
		m_dwaApp = dwaApp ;
		m_useAbsolute = useAbsolute ;
		m_isFirst = true ;
		m_returnRootFile = returnRootFile ;
		m_stPath = new Stack() ;
	}
	
	// services
	
	/**
	 * Walks through the given directory.
	 * @param		sDirectory		the directory to browse
	 * @exception	IOException		if an i/o error occured
	 */
	
	public void walkThrough(String sDirectory)
		throws IOException
	{
		//m_fDir = new File(sDirectory) ;
		walkThrough(new File(sDirectory)) ;
	}
	
	
	/**
	 * Walks through the given directory.
	 * @param		fDir			the directory to browse
	 * @exception	IOException		if an i/o error occured
	 */
	
	private void walkThrough(File fDir)
		throws IOException
	{
		// null reference: throw a NullPointerException
		if(fDir == null)
			throw new NullPointerException("Provided file reference is null") ;
		
		// element doesn't exist: call handleUnknown()
		if(!fDir.exists())
		{
			m_dwaApp.handleUnknown(fDir.getAbsolutePath()) ;
			return ;
		}
		
		// element isn't a directory: consider it a file and call handleFile()
		if(!fDir.isDirectory())
		{
			return ;
		}
		
		
		String sPath = getPath(fDir) ;
		if(!sPath.endsWith(File.separator))
			sPath += File.separator ;
		
		// this is an existing directory: call handleDir()
		m_dwaApp.handleDir(sPath) ;
		
		boolean isFirst = m_isFirst ;
		if(isFirst)
			m_isFirst = false ;
		else
			m_stPath.push(fDir.getName()) ;
		
		// go recursively on directory content
		
		String sList[] = fDir.list() ;
		File fCurrent ;
		for(int n = 0 ; n < sList.length ; n++)
		{
			fCurrent = new File(fDir.getAbsolutePath() + File.separator + sList[n]) ;
			if(!fCurrent.isDirectory())
			{
				if(isFirst && !m_returnRootFile)
					continue ;
				m_dwaApp.handleFile(getPath(fCurrent, sPath)) ;
			}
			else
			{
				walkThrough(fCurrent) ;
			}
		}
		
		if(!isFirst)
			m_stPath.pop() ;
	}
	
	
	/**
	 * Gets the path for the given file, according to the addressing "policy"
	 * of this DirWalker.
	 * @param	fElement	the file or directory whose path is required
	 * @return	the proper path (absolute or relative depending on m_useAbsolute)
	 */
	
	private String getPath(File fElement)
	{
		if(m_useAbsolute)
			return fElement.getAbsolutePath() ;
		else
			return getPath(fElement, getRelativePath()) ;
	}
	
	/**
	 * Gets the path for the given file, according to the addressing "policy"
	 * of this DirWalker, using the given path for parent relative path (if
	 * m_useAbsolute is set to false).
	 * @param	fElement	the file or directory whose path is required
	 * @param	sPath		the parent relative path
	 * @return	the proper path (absolute or relative depending on m_useAbsolute)
	 */
	
	private String getPath(File fElement, String sPath)
	{
		if(m_useAbsolute)
		{
			return fElement.getAbsolutePath() ;
		}
		else
		{
			//if(fElement.equals(m_fDir))
			if(!m_isFirst)
				sPath += fElement.getName() ;
			return sPath ;
		}
	}
	
	
	/**
	 * Gets the current relative path, according to the directories stack state.
	 * @return	the current relative path
	 */
	
	private String getRelativePath()
	{
		Enumeration ePaths = m_stPath.elements() ;
		String sPath = new String() ;
		while(ePaths.hasMoreElements())
		{
			sPath += ePaths.nextElement() + File.separator ;
		}
		return sPath ;
	}
}
