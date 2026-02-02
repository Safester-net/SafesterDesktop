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

// 18/10/01 15:25 GR  - OK
// 18/10/01 15:30 GR  - javadoc

import java.util.StringTokenizer;

/**
 * This class is a verification tool for IP addresses format.
 */

public class IPAddressChecker
{
	/**	The IP addresse octet separator: simple dot */
	public static final String DOT = "." ;
	
	
	/**
	 * Creates a new IP address checker.
	 */
	
	public IPAddressChecker()
	{
		// nothin particular
	}
	
	
	/**
	 * Checks if the given String is a formatted as a valid IP address.<br>
	 * If it's something else than x.x.x.x where x is a 1-to-3 digit token,
	 * then it will be considered an invalid IP address.
	 * @param	sIPAddr		the String to check
	 * @param	true if the given String is formatted as an IP Address, false otherwise
	 */
	
	public boolean isValid(String sIPAddr)
	{
		if(sIPAddr == null || sIPAddr.length() == 0)
			return false ;
		
		StringTokenizer stTokenizer = new StringTokenizer(sIPAddr, DOT, true) ;
		
		// in the following, "octet" means IP addr byte.
		// an IP address is four octets separated by dots.
		String sOctet ;
		int nOctetCount = 0 ;
		int nDotCount = 0 ;
		while(stTokenizer.hasMoreTokens())
		{
			sOctet = stTokenizer.nextToken() ;
			
			if(sOctet.equals(DOT))
			{
				if(nDotCount++ > 2)
					return false ;
				continue ;
			}
			
			if(!isValidOctet(sOctet))
				return false ;
			
			// don't parse further!
			if(nOctetCount++ > 3)
				return false ;
		}
		
		return (nOctetCount == 4 && nDotCount == 3) ;
	}
	
	
	/**
	 * Checks if the provided String is a valid IP address octet,
	 * i.e. a String of 1 to 3 digits.
	 * @param	sOctet		the token to check
	 * @return	true if it is a valid IP address octet, false otherwise
	 */
	
	private boolean isValidOctet(String sOctet)
	{
		// validate length
		if(sOctet == null || sOctet.length() == 0 || sOctet.length() > 3)
			return false ;
		
		// validate chars
		for(int n = 0 ; n < sOctet.length() ; n++)
		{
			if(!Character.isDigit(sOctet.charAt(n)))
				return false ;
		}
		
		return true ;
	}
	
	
	/**
	 * Test interface.
	 */
	
	public static void main(String args[])
	{
		if(args.length == 0)
		{
			System.out.println("usage: <IP_1> [<IP_2> ... <IP_N>]") ;
			System.exit(0) ;
		}
		
		try{
			IPAddressChecker iacIPCheck = new IPAddressChecker() ;
			for(int n = 0 ; n < args.length ; n++)
			{
				System.out.println(args[n] + ": "
								   + (iacIPCheck.isValid(args[n])?"valid":"invalid")) ;
			}
		}
		catch(Exception eAll)
		{
			eAll.printStackTrace() ;
		}
	}
}
