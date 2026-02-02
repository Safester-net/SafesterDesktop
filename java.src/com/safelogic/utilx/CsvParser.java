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
package com.safelogic.utilx;

// 17/06/02 11:00 NC - creation
// 21/07/03 12:40 NDP - comments 

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * This class implements a ".csv" (Comma Separated Values) parser in order to find
 * attributes such as lastname, firstname, e-mail, nickname in an adress book.
 */

public class CsvParser
{
	BufferedReader m_brReader ;	// the buffer which stores the data from the file
	String m_sLastName ;		// retrieved lastname
	String m_sFirstName ;		// retrieved firstname
	String m_sEmail ;			// retrieved e-mail
	String m_sNickName ;		// retrieved nickname
	boolean m_bIsNickNameUsed ;	// a boolean used to know if there is a nickname

	/**
	 * Constructs a default ".csv"-file parser.
	 * 
	 * @param	isInput		the inputstream to use
	 * 
	 * @exception Exception
	 */

	public CsvParser(InputStream isInput)
		throws Exception
	{
		InputStreamReader isrInput = new InputStreamReader(isInput) ;
		m_brReader	= new BufferedReader(isrInput) ;
	}


	/**
	 * Returns the currently read line within the ".csv" file.
	 * 
	 * @return	the currently read line within the ".csv" file
	 */

	public String nextLine()
	{
		String sLine = null ;	// the line which is currently read from the file

		try	{
			sLine = m_brReader.readLine() ;
		
			if(sLine == null)
			{
				m_brReader.close() ;
			}
		}
		catch(Exception e)
		{
			System.out.println("\nError : failed to parse the file.\n") ;
			e.printStackTrace() ;
		}

		return sLine ;
	}


	/**
	 * Parses a given line in order to find last/firstnames, email, and nick.
	 * 
	 * @param	sLine	the currently read line within the ".csv" file
	 * 
	 * @exception	Exception
	 */

	public void parseLine(String sLine)
		throws Exception
	{
		char[] array	= new char[50] ; // the array used to parse the line
		int iFirstIndex = 0 ;		// position of the first ";" in the line
		int iMiddleIndex= 0 ;		// poistion of the middle ";" in the line
		int iLastIndex	= 0 ;		// position of the last ";" in the line
		int iLineLength = 0 ;		// length of the total line
		int iLastNameLength = 0 ;	// length of the retrieved lastname
		int iFirstNameLength= 0 ;	// length of the retrieved firstname
		int iEmailLength	= 0 ;	// length of the retrieved e-mail

		m_sFirstName	= null ;
		m_sLastName		= null ;
		m_sEmail		= null ;

		iLineLength = sLine.length() ;
		
		//  check for any syntax error :
		//  Lastname;Firstname;E-mail;Nickname  and...
		// "Lastname;Firstname;E-mail;Nickname" are authorized syntaxes,
		//  but...
		// "Lastname;Firstname;E-mail;Nickname  and...
		//  Lastname;Firstname;E-mail;Nickname" are not authorized syntaxes.
		if(sLine.charAt(0) == '"')
		{
			if (sLine.charAt(iLineLength-1) == '"')
			// correct syntax (now remove the two '"')
			{
				// remove the starting '"' and the ending '"' from the initial line
				String sSubLine = sLine.substring(1, iLineLength-1) ;
				sLine = sSubLine ;
				iLineLength = sLine.length() ; // update
			}
			else
			{
				// System.out.println("Syntax error in [" + sLine + "]");
			}
		}

		iFirstIndex = sLine.indexOf(';') ;
		iMiddleIndex= sLine.indexOf(';', iFirstIndex+1) ;
		iLastIndex	= sLine.lastIndexOf(';') ; // used if a nickname is present

		m_bIsNickNameUsed = false ;

		// there are only 2 ";" so there is no nickname
		if(iMiddleIndex == iLastIndex)
		{
			iLastIndex = iLineLength ; // needed to be fixed for "sEmail"
		}
		// there are 3 ";" but no nick after the 3rd ";"
		else if(iLastIndex+1 == iLineLength)
		{
			iLastIndex = iLineLength-1 ; // needed to be fixed for "sEmail"
		}
		// ok, there are 3 ";" (and not only 2) and a nickname
		else
		{
			m_bIsNickNameUsed = true ;
		}		
		
		// gets the lastname
		sLine.getChars(0, iFirstIndex, array, 0) ;
		m_sLastName = new String(array, 0, iFirstIndex) ;
		iLastNameLength = m_sLastName.length() ;

		// gets the firstname
		sLine.getChars(iFirstIndex+1, iMiddleIndex, array, 0) ;
		m_sFirstName = new String(array, 0, iMiddleIndex-iLastNameLength-1) ;
		iFirstNameLength = m_sFirstName.length() ;

		// gets the e-mail
		sLine.getChars(iMiddleIndex+1, iLastIndex, array, 0) ;
		m_sEmail = new String(array, 0, iLastIndex
											- iLastNameLength - 1
											- iFirstNameLength- 1) ;
		iEmailLength = m_sEmail.length() ;


		// is there a nickname for this contact ?
		if(m_bIsNickNameUsed == true)
		{
			sLine.getChars(iLastIndex+1, iLineLength, array, 0) ;
			m_sNickName = new String(array, 0, iLineLength
												- iLastNameLength - 1
												- iFirstNameLength - 1
												- iEmailLength -1) ;
		}
		else
		{
			m_sNickName = "" ;
		}
	}

	/**
	 * Returns the value of m_sLastName.
	 * 
	 * @return	the value of m_sLastName
	 */

	public String getLastName()
	{
		return m_sLastName ;
	}


	/**
	 * Returns the value of m_sFirstName.
	 * 
	 * @return	the value of m_sFirstName
	 */

	public String getFirstName()
	{
		return m_sFirstName ;
	}


	/**
	 * Returns the value of m_sEmail.
	 * 
	 * @return	the value of m_sEmail
	 */

	public String getEmail()
	{
		return m_sEmail ;
	}


	/**
	 * Returns the value of m_sNickName.
	 * 
	 * @return	the value of m_sNickName
	 */

	public String getNickName()
	{
		return m_sNickName ;
	}


	/**
	 * Returns the value of m_bIsNickNameUsed.
	 * 
	 * @return	the value of m_bIsNickNameUsed
	 */

	public boolean getIsNickNameUsed()
	{
		return m_bIsNickNameUsed ;
	}
}
