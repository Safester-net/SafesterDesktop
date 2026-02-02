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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.safelogic.utilx.conf.IniPropertyList;


/**
* this is a tool to check hostname syntax validity.
* 
* New ConfiMail Spring 2004: domains are now checked from static values
*/
public class HostNameChecker
{
	/**	the dot separator (".") */
	public static final char SEP_DOT = '.' ;
	
	/**	the dash separator ("-") */
	public static final char SEP_DASH = '-' ;
	
	/*
	private static final int CHAR_OK = 1 ;
	private static final int CHAR_SEP = CHAR_OK + 1 ;
	private static final int CHAR_SEP_OK = CHAR_SEP + 1 ;
	private static final int CHAR_NOT_OK = CHAR_SEP_OK + 1 ;
	private static final int CHAR_SEP_NOT_OK = CHAR_NOT_OK + 1 ;
	
	private char m_cLastSep = 0 ;
	private int m_nStatus = 0 ;
	*/
	
	/**	the last char value */
	private char m_cLastChar = 0 ;
	
	/**	the domain stack */
	private Stack m_stDomains ;
	
	/**	the allowed top levels DNS */
	private Set  m_hTopLevels ;
	
	
	/**
	 * Creates a new checker using the ressource file whose name is specified.<br>
	 * The file is searched in classpath.
	 * 
	 * @param	sRessourceName	the name of DNS ressource file to use
	 * 
	 * @exception	IOException		if IO error occurs when accessing the ressource file
	 */
	
	/**
	 * Creates a new checker using the default ressource file.
	 * <br>
	 * The domains list is in class TopDomainsList in same package
	 * 
	 * @exception	IOException		if IO error occurs when accessing the ressource file
	 */
	
	public HostNameChecker()
	{
	    List lstTopDomains = Arrays.asList(TopDomainsList.getDomains());
	    m_hTopLevels = new HashSet(lstTopDomains);
	}
	
	
	/*
	public HostNameChecker(String sRessourceName)
		throws IOException
	{
		loadTopLevels(sRessourceName) ;
	}
	*/
	
	/*
	public static void main(String args[])
		throws IOException
	{
	    HostNameChecker hnc = new HostNameChecker();
	    System.out.println(hnc.isValid("com"));
	    System.out.println(hnc.isValid("yep"));
	    System.out.println(hnc.isValid("fr"));
	}
	*/
	


	
	/**
	 * Loads the specified ressource file.
	 * 
	 * @param	sRessourceName	the *name* of DNS ressource file to use
	 * 
	 * @exception	IOException		if IO error occurs when accessing the ressource file
	 */
	
	private void loadTopLevels(String sRessourceName)
		throws IOException
	{
		m_hTopLevels = new HashSet() ;
		IniPropertyList plTopLevels = new IniPropertyList() ;
		plTopLevels.load(sRessourceName) ;
		
		Enumeration eTopLevels = plTopLevels.getEnumeration() ;
		Object oValue ;
		while(eTopLevels.hasMoreElements())
		{
			oValue = eTopLevels.nextElement() ;
			m_hTopLevels.add(oValue) ;
		}
	}
	
	
	/**
	 * Checks the validity of the specified domain name.
	 * 
	 * @param	sHostName	the host name value to check
	 * 
	 * @return	true if the specified host name is valid, false otherwise
	 */
	
	public boolean isValid(String sHostName)
	{
		m_stDomains = new Stack() ;
		String sCurrentPart = new String() ;
		
		for(int nCnt = 0 ; nCnt < sHostName.length() ; nCnt++)
		{
			m_cLastChar = sHostName.charAt(nCnt) ;
			
			if(isAllowed(m_cLastChar)
			   || m_cLastChar == SEP_DASH)
			{
				sCurrentPart = sCurrentPart + m_cLastChar ;
			}
			else if(m_cLastChar == SEP_DOT)
			{
				m_stDomains.push(sCurrentPart) ;
				sCurrentPart = new String() ;
			}
			else
			{
				return false ;
			}
		}
		
		m_stDomains.push(sCurrentPart) ;
		
		return checkStack() ;
	}
	
	
	/**
	 * Checks the validity of the current state of the domain name stack.
	 * 
	 * @return	true if the stack content is valid, false otherwise
	 */
	
	private boolean checkStack()
	{
		String sDomain = null ;
		int nCnt = 0 ;
		while(!m_stDomains.empty())
		{
			sDomain = (String)m_stDomains.pop() ;
			
			//System.out.println(nCnt + " : " + sDomain) ;
			
			if(sDomain == null || sDomain.length() == 0)
			{
				return false ;
			}
			
			//if(nCnt == 0 && sDomain.indexOf(SEP_DASH) != -1)
			if(nCnt == 0 && !isTopLevelDomain(sDomain))
			{
				return false ;
			}
			else
			{
				if(sDomain.indexOf(SEP_DASH) == 0
				   || sDomain.lastIndexOf(SEP_DASH) == sDomain.length() -1)
				{
					return false ;
				}
			}
			
			nCnt++ ;
		}
		
		return true ;
	}
	
	
	/**
	 * Checks the validity of the specified character.
	 * 
	 * @param	c	the character to verify
	 * 
	 * @return	true if the character is valid, false otherwise
	 */
	
	private boolean isAllowed(char c)
	{
		return (c >= 'a' && c <= 'z')
			   || (c >= 'A' && c <= 'Z')
			   || (c >= '0' && c <= '9') ;
		
	}
	
	
	/**
	 * Checks if the specified domain is a valid top level domain,
	 * according to the ressource file used by this instance.
	 * 
	 * @param	sDomain		the domain extension to verify
	 * 
	 * @return	true if this is a valid top level domain name, false otherwise
	 */
	
	private boolean isTopLevelDomain(String sDomain)
	{
	    // 19/04/06 10:55 NDP- isTopLevelDomain() checks domain using lowercase
		return m_hTopLevels.contains(sDomain.toLowerCase()) ;
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
