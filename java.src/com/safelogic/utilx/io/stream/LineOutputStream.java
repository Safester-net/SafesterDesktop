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
package com.safelogic.utilx.io.stream;

// 03/11/00 15:00 GR - creation
// 03/11/00 16:55 GR - OK
// 16/01/01 17:50 GR - javadoc

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;



/**
 * This class allows to write byte arrays or Strings as line on an OutputStream.
 */

public class LineOutputStream
	extends BufferedOutputStream
{
	/** the CR_LF bytes */ 

	public static final byte[] CR_LF = System.getProperty("line.separator").getBytes() ;


	/**
	 * Creates a stream using the specified one.
	 * 
	 * @param	osOut	the OutputStream to write to	
	 */

	public LineOutputStream(OutputStream osOut)
	{
		super(osOut) ;
	}


	/**
	 * Writes a line (as a String).
	 * @param	sLine		the line to write
	 * @exception	IOException		if an I/O error occurs
	 */

	public void writeln(String sLine)
		throws IOException
	{
		writeln(sLine.getBytes()) ;
	}


	/**
	 * Writes a line (as a byte array).
	 * @param	bLine		the line to write
	 * @exception	IOException		if an I/O error occurs
	 */

	public void writeln(byte[] bLine)
		throws IOException
	{
		write(bLine) ;
		writeln() ;
	}


	/**
	 * writes an empty line
	 * @exception	IOException		if an I/O error occurs
	 */

	public void writeln()
		throws IOException
	{
		write(CR_LF) ;
		flush() ;
	}
	

	/**
	 * Rule 8: Make your classes noncloneable
	 * @return	not reached
	 * @exception	java.lang.CloneNotSupportedException		
	 */
	
	//Rule 8: Make your classes noncloneable
	public final Object clone() throws java.lang.CloneNotSupportedException {
		throw new java.lang.CloneNotSupportedException();
	}

	
	/**
	 * Rule 9: Make your classes nonserializeable
	 * @param	out		the stream for serialization
	 * @exception	java.io.IOException		
	 */
	
	//Rule 9: Make your classes nonserializeable
	private final void writeObject(ObjectOutputStream out)
		throws java.io.IOException {
		throw new java.io.IOException("Object cannot be serialized");
	}

	
	/**
	 * Rule 10: Make your classes nondeserializeable
	 * @param	in	the stream for deserialization	
	 * @exception	java.io.IOException		
	 */
	
	//Rule 10: Make your classes nondeserializeable
	private final void readObject(ObjectInputStream in)
		throws java.io.IOException {
		throw new java.io.IOException("Class cannot be deserialized");
	}
}
