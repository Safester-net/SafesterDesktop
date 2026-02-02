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


// 04/04/01 18:55 GR - creation


// for security rules
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * This is a tool to trim strings.
 * Just call String.trim() after checking for null value.
 */

public class Trimmer
{
	public Trimmer()
	{
		// do nothing
	}
	
	/**
	 * This is a tool to trim strings.
	 * Just call String.trim() after checking for null value.
	 * @param	sText	the string to trim
	 * @return	the trimmed version of sText or null if sText was null
	 */
	
	public static String Trim(String sText)
	{
		if(sText == null)
			return sText ;
		return sText.trim() ;
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
