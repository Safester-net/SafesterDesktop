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


// 20/03/01 11:05 GR - creation
// 20/03/01 11:05 GR - OK tested
// 20/03/01 11:15 GR - javadoc
// 20/03/01 14:45 GR - add null/0 test on input String

// for security rules
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * Encodes and decodes String to insure that no character will cause
 * SQL error or be modified by the database charset.
 */

public class SQLStringCoder
{
	/**	The encoded character begin marker */
	public static final char BEGIN	= '#' ;
	
	/**	The encoded character end marker */
	public static final char END	= '#' ;
	
	/**
	 * This array defines the "readable" (US-ASCII) cahracters that are to be
	 * encoded.
	 */
	public static final char[] ENCODE_CHARS =
	{
		'\'',	// because it is not allowed in SQL
		BEGIN,	// because it is an encoding marker
		END,	// because it is an encoding marker
	};
	
	
	/**
	 * Creates a new SQL String encoder.
	 */
	
	public SQLStringCoder()
	{
		
	}
	
	
	/**
	 * Encodes the specified String.<br>
	 * Characters which are in ENCODE_CHARS table or whose ASCII value is more than 127
	 * will be encoded. This is checked by calling isToBeEncoded() for each character.
	 * @param	sIn		the String to encode
	 * @return	the encoded String
	 */
	
	public String encode(String sIn)
	{
		if(sIn == null || sIn.length() == 0)
			return sIn ;
		
		String sOut = new String() ;
		char c ;
		for(int n = 0 ; n < sIn.length() ; n++)
		{
			c = sIn.charAt(n) ;
			
			if(isToBeEncoded(c))
			{
				sOut += encode(c) ;
				continue ;
			}
			
			sOut += c ;
		}
		
		return sOut ;
	}
	
	
	/**
	 * Encodes the specified character.
	 * @param	c		the character to encode
	 * @return	a String representing the encoded character
	 */
	
	public String encode(char c)
	{
		int n = c ;
		String sEncoded = new String() ;
		sEncoded += BEGIN ;
		sEncoded += n ;
		sEncoded += END ;
		return sEncoded ;
	}
	
	
	/**
	 * Checks if the given character is a special one that need to be encoded.<br>
	 * Characters which are in ENCODE_CHARS table or whose ASCII value is more than 127
	 * will be encoded.
	 * @param	c	the character to check
	 * @return	true if the given character is to be encoded, false otherwise
	 */
	
	public boolean isToBeEncoded(char c)
	{
		if(c > 127)
			return true ;
		
		for(int i = 0 ; i < ENCODE_CHARS.length ; i++)
		{
			if(c == ENCODE_CHARS[i])
			{
				return true ;
			}
		}
		
		return false ;
	}
	
	
	/**
	 * Decodes the specified String.
	 * @param	sIn		the String to decode
	 * @return	the decoded String
	 */
	
	public String decode(String sIn)
	{
		if(sIn == null || sIn.length() == 0)
			return sIn ;
		
		String sOut = new String() ;
		char c ;
		boolean inEncoded = false ;
		String sEncoded = new String() ;
		for(int n = 0 ; n < sIn.length() ; n++)
		{
			c = sIn.charAt(n) ;
			
			if(c == BEGIN && !inEncoded)
			{
				sEncoded = new String() ;
				inEncoded = true ;
				continue ;
			}
			
			if(c == END && inEncoded)
			{
				sOut += decodeChar(sEncoded) ;
				inEncoded = false ;
				continue ;
			}
			
			if(inEncoded)
			{
				sEncoded += c ;
				continue ;
			}
			
			sOut += c ;
		}
		
		return sOut ;
	}
	
	
	/**
	 * Decodes the specified character.
	 * @param	sEncoded	the int ASCII value as a String
	 * @return	the corresponding character
	 */
	
	public char decodeChar(String sEncoded)
	{
		int nChar = Integer.parseInt(sEncoded) ;
		//byte b[] = new byte[1] ;
		//b[0] = (byte) nChar ;
		//System.out.println(new String(b)) ;
		//return new String(b) ;
		return (char) nChar ;
		//Character C = new Character((char) nChar) ;
		//return C.toString() ;
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
