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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Vector;

public class Directory
{
	//Rule 8: Make your classes noncloneable
	public final Object clone() throws java.lang.CloneNotSupportedException {
		throw new java.lang.CloneNotSupportedException();
	}

	//Rule 9: Make your classes nonserializeable
	private final void writeObject(ObjectOutputStream out)
		throws java.io.IOException {
		throw new java.io.IOException("Object cannot be serialized");
	}

	//Rule 10: Make your classes nondeserializeable
	private final void readObject(ObjectInputStream in)
		throws java.io.IOException {
		throw new java.io.IOException("Class cannot be deserialized");
	}

	// Debug Option
	private static boolean CLW_DEBUG	= false; //CSaTools.isDebug("csa_directory.txt");
	
	public static String CR_LF = System.getProperty("line.separator");

	//
	// Class variables
	//
	
	// Directory name
	protected  String	m_sDir		= null;
	
	// String Filter 
	protected  String	m_sFilter	= null;

	// boolean to say if to retrieve full names
	private boolean bFullName		= false;
	
	/**
	 *
	 * Default Constructor.
	 *
	 * @param sDir		The *local* Directory to use
	 *
	 */
	
	public Directory (
		String sDir		
		)
	{
		this.m_sDir  = sDir;	
		
		// Mandatory : filter cant' be null
		this.m_sFilter = " ";
		
		this.addSep();
	}

	
	/**
	 *
	 * Constructor.
	 *
	 * @param sDir		 The *local* Directory to use
     * @param sFilter	 A Substring which must appear in the File Name
     * 
     */
	

	public Directory (
		String	sDir,
		String	sFilter
		)
	{
		this.m_sDir		= sDir;
		this.m_sFilter	= sFilter;
		
		this.addSep();
	}	
	
	
	/**
	 * Add to the Drectory name with the last file.separator, if mising
	 */
	
	public  void addSep()
	{
		String sFsep = new String(System.getProperty("file.separator"));
		
		if (this.m_sDir.lastIndexOf(sFsep) != this.m_sDir.length() - 1) {
			this.m_sDir += sFsep;
		}
	}
	
	/**
	 * Add to the Directory name with the last file.separator, if mising
	 */
	
	public static String AddSep(String sDir)
	{
        if (sDir == null)
        {
            return null;
        }
        
		String sFsep = new String(System.getProperty("file.separator"));
		
		if (sDir.lastIndexOf(sFsep) != sDir.length() - 1) {
			sDir += sFsep;
		}
		
		return new String(sDir);
	}
	
	
	/*
	* returns the directory name to String, with an added sep !
	*/
	
	public String toString()
	{
		return(this.m_sDir);
	}	
	
	/**
	 *
	 * Retrieves all files in the directory into a vector (and sort them)
	 * <br> NOTE : names are in short format *without* full path
	 * 
	 * @return	the directory files list into a Vector
	 * 
	 */
	

	public Vector getFiles ()
		throws IOException
	{

		int i = 0;

		File fDir = new File(this.m_sDir);
		
		if ( ! fDir.isDirectory() )
		{
			throw new FileNotFoundException(this.m_sDir);
		}
    
		String [] sFileDir = fDir.list();

		Vector vFiles = new Vector();

		for (i= 0; i < sFileDir.length; i++)
		{
			// 05/01/00 17:40 NDP - add a file.separator for Java 1.2.2 Win32
			//File fTemp1 = new File(fDir + sFileDir[i]);
			File fTemp1 = new File(this.m_sDir + sFileDir[i]);
				
			if ( fTemp1.isFile() &&
				 (sFileDir[i].indexOf(this.m_sFilter) != -1 
				  || this.m_sFilter.length() <= 1) )
			{
				//System.out.println("sFileDir[i]="+ sFileDir[i]);
				if (bFullName)
				{
					vFiles.addElement(this.m_sDir + sFileDir[i]);
				}				
				else {
					vFiles.addElement(sFileDir[i]);
				}
			}

		}				
				
		// Sort File in Ascending Order
		//CSaTools.Sort (vFiles, 0, vFiles.size() - 1);
		
		Collections.sort(vFiles);
		
		return vFiles;
	}
	
	
	/**
	 *
	 * Retrieves all subDirectories in the directory into a vector (and sort them).
	 * <br> NOTE : names are in short format *without* full path
	 * 
	 * @return	the directory files list into a Vector
	 * 
	 */
	
	public Vector getSubDirectories ()
		throws IOException
	{

		int i = 0;

		File fDir = new File(this.m_sDir);
		
		if (CLW_DEBUG)
		{
			System.out.println("fDir...............:" + fDir.toString());
			System.out.println("this.m_sDir........:" + this.m_sDir);
			System.out.println("fDir.isDirectory() :" + fDir.isDirectory());
		}		
		
		if ( ! fDir.isDirectory() )
		{
			throw new FileNotFoundException(this.m_sDir);
		}
    
		String [] sFileDir = fDir.list();
		
		Vector vFiles = new Vector();

		for (i= 0; i < sFileDir.length; i++)
		{
			// 05/01/00 17:40 NDP - add a file.separator for Java 1.2.2 Win32
			//File fTemp1 = new File(fDir + sFileDir[i]);
			File fTemp1 = new File(this.m_sDir + sFileDir[i]);
			
			//if (CLW_DEBUG)
			//{
			//	System.out.println("sFileDir[i]..........:" + sFileDir[i]);
			//	System.out.println("fTemp1...............:" + fTemp1.toString());
			//	System.out.println("fTemp1.isDirectory() :" + fTemp1.isDirectory());
			//}			
			
			if ( fTemp1.isDirectory() )
			{	
				//System.out.println("sFileDir[i]="+ sFileDir[i]);
				if (bFullName)
				{
					vFiles.addElement(this.m_sDir + sFileDir[i]);
				}
				else {
					vFiles.addElement(sFileDir[i]);	
				}
			}

		}				
				
		// Sort File in Ascending Order
		//CSaTools.Sort (vFiles, 0, vFiles.size() - 1);
		
		Collections.sort(vFiles);
		
		return vFiles;
	}	
	
	
	/**
	 *
	 * Retrieves all files in the directory into a vector (and sort them)
	 * <br> NOTE : names are in long format *with* full path
	 * 
	 * @return	the directory files list into a Vector
	 * 
	 */
	

	public Vector getFullNameFiles ()
		throws IOException
	{
		this.bFullName = true;
		Vector vFiles = this.getFiles();
		this.bFullName = false;	
		return vFiles;
	}
	
	/**
	 *
	 * Retrieves all subDirectories in the directory into a vector (and sort them).
	 * <br> NOTE : names are in long format *with* full path
	 * 
	 * @return	the directory files list into a Vector
	 * 
	 */
	
	public Vector getFullNameSubDirectories ()
		throws IOException
	{
		this.bFullName = true;
		Vector vFiles = this.getSubDirectories();
		this.bFullName = false;
		return vFiles;
	}
	
	
	
} // End Directory.java
