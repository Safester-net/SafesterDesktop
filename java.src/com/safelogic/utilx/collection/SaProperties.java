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

// 12/10/00 15:10 GR - creation
// 18/01/01 18:15 GR - javadoc
// 24/01/01 18:25 GR - add load() method
// 15/02/01 16:35 GR - add boolean methods
// 15/02/01 18:00 GR - javadoc correction
// 15/03/01 12:10 GR - add load(InputStream) & load(URL)
// 22/03/01 17:45 GR - add load(File)
// 15/01/02 15:55 GR - add getKeys()
// 15/01/02 16:05 GR - constants are protected only: access by subclasses
// 03/02/02 16:45 NDP- load file from ini dir directory instead of classpath
// 04/02/02 17:50 GR - only accecpt load() with File!
// 25/06/03 12:25 NDP- add keySet() 

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
import java.util.Hashtable;
import java.util.Set;

import com.safelogic.utilx.io.stream.LineInputStream;

/**
 * Defines a property set.
 */

public class SaProperties
{

	// CONSTANTS
	
	/** The character used for commented lines in ressources files */ 
	protected static final String COMMENT = "#" ;

	/** The equal character for variables and values in ressources files */ 
	protected static final String EQUAL = "=" ;    

	/** The properties in a Hashtable */ 
	private Hashtable m_hProperties ;


	/**
	 * Creates a new property set.
	 */

	public SaProperties()
	{
		m_hProperties = new Hashtable() ;
	}


	/**
	 * Sets a String property.
	 * @param	sName		the property name
	 * @param	sValue		the property value
	 */

	public void setString(String sName, String sValue)
	{
		setProperty(sName, sValue) ;
	}


	/**
	 * Gets a String property.
	 * @param	sName	the property name
	 * @return			the property value
	 */

	public String getString(String sName)
	{
		Object oValue = getProperty(sName) ;
		
		if(oValue == null)
			return null ;
		
		if(oValue instanceof String)
		{
			return (String) oValue ;
		}
		else
		{
			return oValue.toString() ;
		}
	}
	
	
	/**
	 * Sets a boolean property.
	 * @param	sName		the property name
	 * @param	oValue		the property value
	 */

	public void setBoolean(String sName, boolean bValue)
	{
		setProperty(sName, new Boolean(bValue)) ;
	}


	/**
	 * Gets a boolean property.
	 * @param	sName	the property name
	 * @return			the property value
	 */

	public boolean getBoolean(String sName)
	{
		try{
			return ((String)getProperty(sName)).equalsIgnoreCase(Boolean.TRUE.toString()) ;
		}
		catch(Exception eAll)
		{
			// do nothing
		}
		
		// if a Boolean was inserted
		try{
			return ((Boolean)getProperty(sName)).booleanValue() ;
		}
		catch(Exception eAll)
		{
			// do nothing
		}
		
		return false ;
	}
	

	/**
	 * Sets an Object property.
	 * @param	sName		the property name
	 * @param	oValue		the property value
	 */

	public void setProperty(String sName, Object oValue)
	{
		m_hProperties.put(sName, oValue) ;
	}


	/**
	 * Gets an Object property.
	 * @param	sName	the property name
	 * @return			the property value
	 */

	public Object getProperty(String sName)
	{
		return m_hProperties.get(sName) ;
	}
	
	
	/**
	 * Gets all the keys (properties names) of this properties object.
	 * @return	all the keys of this properties object
	 */

	public Enumeration getKeys()
	{
		return m_hProperties.keys() ;
	}
	
    /**
     * Returns a Set view of the keys contained in this Hashtable.  The Set
     * is backed by the Hashtable, so changes to the Hashtable are reflected
     * in the Set, and vice-versa.  The Set supports element removal
     * (which removes the corresponding entry from the Hashtable), but not
     * element addition.
     *
     * @return a set view of the keys contained in this map.
     * @since 1.2
     */
    public Set keySet() 
    {
        return this.m_hProperties.keySet();
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
	 * Loads the values contained in the specified file.<br>
	 * Only use the file name. The path is searched with CSaIni.<br>
	 * If some values are missing, no default value is assumed.
	 * @param		sFile		the file containing the properties
	 * @exception	IOException	if an I/O error occured
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
	 * Loads the values contained in the specified file.<br>
	 * If some values are missing, no default value is assumed.
	 * @param		fFile		the file containing the properties
	 * @exception	IOException	if an I/O error occured
	 */
	
	public void load(File fFile)
		throws IOException
	{
		load(new FileInputStream(fFile)) ;
	}
	
	
	/**
	 * Loads the values contained in the file at the given URL.<br>
	 * If some values are missing, no default value is assumed.
	 * @param		urlFile		the URL of the file containing the properties
	 * @exception	IOException	if an I/O error occured
	 */
	
	public void load(URL urlFile)
		throws IOException,
			   MalformedURLException
	{
		load(urlFile.openStream()) ;
	}
	
	
	/**
	 * Loads the values contained in the specified stream.<br>
	 * If some values are missing, no default value is assumed.
	 * @param		isIn		the stream containing the properties
	 * @exception	IOException	if an I/O error occured
	 */
	
	public void load(InputStream isIn)
		throws IOException
	{
		BufferedInputStream bisIn = new BufferedInputStream(isIn) ;
		LineInputStream lisIn = new LineInputStream(bisIn) ;
		
		String sLine = new String() ;
		int nPos = 0 ;
		
		m_hProperties = new Hashtable() ;
		
		while( (sLine = lisIn.readLine()) != null)
		{
			sLine = sLine.trim() ;
			
			if(sLine.length() == 0
			   || sLine.startsWith(COMMENT)
			   || (nPos = sLine.indexOf(EQUAL)) <= 0)
			{
				continue ;
			}
			
			m_hProperties.put(sLine.substring(0, nPos).trim(),
						   sLine.substring(nPos + EQUAL.length()).trim()) ;
			/*
			System.out.println("loaded: " + sLine.substring(0, nPos).trim() + "="
							   + sLine.substring(nPos + EQUAL.length()).trim()) ;
			*/
		}
		
		lisIn.close() ;
	}    
    
}

// End
