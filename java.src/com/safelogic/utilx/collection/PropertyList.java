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
package com.safelogic.utilx.collection;

// 15/02/01 16:05 GR - creation
// 15/02/01 16:15 GR - OK
// 22/03/01 17:40 GR - add load(File fFile)
// 25/04/01 10:35 GR - add load(InputStream) + load(URL)
// 03/02/02 16:45 NDP- load file from ini dir directory instead of classpath
// 04/02/02 17:50 GR - only accecpt load() with File!

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import com.safelogic.utilx.io.stream.LineInputStream;

/**
 * Defines a property set.
 */

public class PropertyList
{

	// CONSTANTS
	
	/** The character used for commented lines in ressources files */ 
	private static final String COMMENT = "#" ;

	/** The list in a Vector */ 
	private Vector m_vList ;


	/**
	 * Creates a new property set.
	 */

	public PropertyList()
	{
		m_vList = new Vector() ;
	}


	/**
	 * Adds a String to the list.
	 * @param	sValue		the property value
	 */

	public void addString(String sValue)
	{
		add(sValue) ;
	}


	/**
	 * Gets a copy of the current state of the list.
	 * @return			a copy of the current list
	 */

	public Vector getList()
	{
		return (Vector)m_vList.clone() ;
	}
	
	/**
	 * Gets the list as an Enumeration .
	 * @return			a copy of the current list as an Enumeration
	 */

	public Enumeration getEnumeration()
	{
		return m_vList.elements() ;
	}
	
	
	/**
	 * Adds an Object to the list.
	 * @param	oValue		the value to add
	 */

	public void add(Object oValue)
	{
		m_vList.addElement(oValue) ;
	}

	
	/**
	 * Rule 8: Make your classes noncloneable
	 * @return	not reached
	 * @exception	java.lang.CloneNotSupportedException		
	 */
	
	//Rule 8: Make your classes noncloneable
	public final Object clone()
		throws java.lang.CloneNotSupportedException
	{
		throw new java.lang.CloneNotSupportedException();
	}

	
	/**
	 * Rule 9: Make your classes nonserializeable
	 * @param	out		the stream for serialization
	 * @exception	java.io.IOException		
	 */
	
	//Rule 9: Make your classes nonserializeable
	private final void writeObject(ObjectOutputStream out)
		throws java.io.IOException
	{
		throw new java.io.IOException("Object cannot be serialized");
	}

	
	/**
	 * Rule 10: Make your classes nondeserializeable
	 * @param	in	the stream for deserialization	
	 * @exception	java.io.IOException		
	 */
	
	//Rule 10: Make your classes nondeserializeable
	private final void readObject(ObjectInputStream in)
		throws java.io.IOException
	{
		throw new java.io.IOException("Class cannot be deserialized");
	}
    
	// SERVICES

// 04/02/02 17:50 GR - only accecpt load() with File!
	/*
	 * Loads the list contained in the file whose name (not path!) has been given.<br>
	 * @param		sFile			the file name containing the list
	 * @exception	IOException		if an I/O error occured
	 */
/*
	public void load(String sFile)
		throws IOException
	{
        //String sFilePath = CSaIni.GetIniFilename(sFile);
		IniFileManager IniFileManager = new IniFileManager();
		String sFilePath = IniFileManager.getIniFilename(sFile);
		load(new File(sFilePath)) ;
	}
*/	
	/**
	 * Loads the list contained in the specified file.<br>
	 * @param		fFile			the file containing the list
	 * @exception	IOException		if an I/O error occured
	 */
	
	public void load(File fFile)
		throws IOException
	{
        load(new FileInputStream(fFile)) ;
	}    
    
	
	/**
	 * Loads the values contained in the file at the given URL.<br>
	 * @param		urlFile		the URL of the file containing the list
	 * @exception	IOException	if an I/O error occured
	 * @exception	MalformedURLException	Bad URL
	 */
	
	public void load(URL urlFile)
		throws IOException,
			   MalformedURLException
	{
		load(urlFile.openStream()) ;
	}
	
	
	/**
	 * Loads the list contained in the specified stream.<br>
	 * @param		isIn			the stream containing the list
	 * @exception	IOException		if an I/O error occured
	 */
	
	public void load(InputStream isIn)
		throws IOException
	{
		BufferedInputStream bisIn = new BufferedInputStream(isIn) ;
		LineInputStream lisIn = new LineInputStream(bisIn) ;
		
		String sLine = new String() ;
		
		m_vList = new Vector() ;
		
		while( (sLine = lisIn.readLine()) != null)
		{
			sLine = sLine.trim() ;
			
			if(sLine.length() == 0
			   || sLine.startsWith(COMMENT))
			{
				continue ;
			}
			
			addString(sLine) ;
		}
		
		lisIn.close() ;
	}
	
}

// End
