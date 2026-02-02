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
package com.safelogic.utilx.syntax;

//for security rules
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
* This class is a tools to test the DNS validity check.
* @see		CSaHostNameChecker
*/

public class NameCheckTest
{


	/**
	 * The main method to launch the test in a shell.
	 * @param	args[]		
	 */

	public static void main(String args[])
	{
		//System.out.println("Test program for CSaHostNameChecker.java") ;
		
		int nFirst = 0 ;
		
		if(args.length == 0)
		{
			System.out.println("Test validity of all specified DNS with CSaHostNameChecker") ;
			System.out.println("Usage : <dns1> [<dns2> ... <dnsN>]") ;
			System.out.println("With : ") ;
				System.out.println(" - dns 1 -> N : the names to test") ;
			System.exit(0) ;
		}
		
		HostNameChecker chncChecker = null ;
		
		try
		{
				chncChecker = new HostNameChecker() ;
		}
		catch(Exception e)
		{
			e.printStackTrace() ;
		}
		
		boolean isValid ;
		
		for(int nCnt = nFirst ; nCnt < args.length ; nCnt++)
		{
			isValid = chncChecker.isValid(args[nCnt]) ;
			
			System.out.println(args[nCnt] 
							   + " " 
							   + (isValid?"is valid":"is not valid")) ;
		}
	}
	//Rule 8: Make your classes noncloneable


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
