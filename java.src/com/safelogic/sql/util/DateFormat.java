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
package com.safelogic.sql.util;


// 29/09/00 13:45 GR - creation


// for security rules
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is a simple Date Conversion class, basicly aimed at JDBC String 
 * parsing and formating.
 */

public class DateFormat
{
	/**	The default JDBC Date Format */
	public static final String DFLT_JDBC_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS" ;
	
	/**	The format to use to parse date */
	private String m_sFormat = null ;
	
	
	/**
	 * The default constructor.
	 * Uses the default JDBC Date Format specified in DFLT_JDBC_FORMAT to
	 * parse and format dates in the corresponding methods.
	 */
	
	public DateFormat()
	{
		this(DFLT_JDBC_FORMAT) ;
	}
	
	
	/**
	 * Sets the format to the specified one and uses it to parse and format dates
	 * in the corresponding methods.
	 * 
	 * @param	sFormat		the date format to apply
	 */
	
	public DateFormat(String sFormat)
	{
		m_sFormat = sFormat ;
	}
	
	
	/**
	 * Formats the specified date with the JDBC format 
	 * (or the one specified in constructor).
	 * 
	 * @param	dDate	the date to format
	 * 
	 * @return			the formated date's string expression
	 */
	
	public String toJDBC(Date dDate)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(m_sFormat) ;
		return sdf.format(new java.util.Date()) ;
	}
	
	
	/**
	 * Parses the JDBC String expression to a java Date object.
	 * 
	 * @param	sDate	the date's String expression to be parsed
	 * 
	 * @return			the corresponding Date object
	 */
	
	public Date fromJDBC(String sDate)
		throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat(m_sFormat) ;
		return sdf.parse(sDate) ;
	}
	
	
	/**
	 * gets the current time formatted as a JDBC String.
	 * 
	 * @return	the current time formatted as a JDBC String
	 */
	
	public String getCurrentTime()
	{
		return toJDBC(new Date()) ;
	}
	
	
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
}
