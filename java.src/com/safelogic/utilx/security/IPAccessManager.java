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
package com.safelogic.utilx.security;

// 07/03/02 17:05 NDP - creation

import java.io.IOException;
import java.util.Enumeration;

import com.safelogic.utilx.conf.IniPropertyList;

/**
 * This class provides a static service to check whether
 * an IP address is allowed
 */

public class IPAccessManager
{
	/**
	 * Set to false to open to any address.<br>
	 * Set to true to restrain access to addresses in the "allow" list.
	 */
	public static final boolean PRIVATE_ACCESS_MODE = true ;
	
	/**
	 * Set to false to keep the list as loaded (only 1 file access).<br>
	 * Set to true to reload list at each query (1 file access per method call).
	 */
	public static final boolean DYNAMIC_LIST_RELOAD = true ;
	
	/**	The static reference for the properties file */
	private static IniPropertyList m_propAllowList ;
	
	/** The file name containing the IP Addresses*/
	private String m_sIpFile = null;
	
	/**
	 * Constructor
	 * @param	sIpFile		Filename containing the IP adresses
	 */
	public IPAccessManager(String sIpFile)
	{
		this.m_sIpFile = sIpFile;
	}
	
	/**
	 * Checks if the given IP address is allowed to connect to
	 * the ConfiMail online Shop.<br>
	 * The returned value depends on the current configuration
	 * - PRIVATE_ACCESS_MODE flag set - and if this flag is set
	 * it also depends on the allow list file and its reload
	 * status (DYNAMIC_LIST_RELOAD flag).
	 * @param	sIPAddr		the user IP address
	 * @return	true if the user is allowed, false otherwise
	 */
	
	public boolean isAllowed(String sIPAddr)
	{
		// allow everybody if private access mode isn't engaged
		if(!PRIVATE_ACCESS_MODE)
			return true ;
		
		if(DYNAMIC_LIST_RELOAD || m_propAllowList == null)
		{
			try{
				m_propAllowList = new IniPropertyList() ;
				m_propAllowList.load(m_sIpFile) ;
			}
			catch(IOException ioe)
			{
				System.out.println("Can't load " + m_sIpFile) ;
				return false ;
			}
		}
		
		Enumeration eList = m_propAllowList.getEnumeration() ;
		
		// Ok, check if IP adress is allowed !
		while(eList.hasMoreElements())
		{
			if(((String)eList.nextElement()).equalsIgnoreCase(sIPAddr))
				return true ;
		}
		
		return false ;
	}
}

// End
