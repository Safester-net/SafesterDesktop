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
package com.safelogic.utilx.net;

// 17/07/02 11:00 NC - creation
// 22/07/02 14:00 NC - logs
// 24/07/02 15:20 NC - getLocalHost(), getHostName(), getHostAddress() added
// 30/07/02 14:55 NC - member-variable comments changed
// 31/07/02 18:55 NC - logs updated
// 05/05/04 17:25 NDP- remove references to Internet Explorer in IPAddressManager

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class was designed to provide info about an IP address.
 */

public class IPAddressManager
{
	/** the IP-address object	 */
	private InetAddress m_iaInetAddress ;


	/**
	 * Constructs a default IPAdressManager.
	 * @exception	UnknownHostException 
	 *		thrown to indicate that the IP address of a host could not be determined
	 */

	public IPAddressManager()
		throws UnknownHostException,
			   Exception
	{

		//try{
		//	if (Class.forName("com.ms.security.PolicyEngine") != null)
		//	{
		//		PolicyEngine.assertPermission(PermissionID.NETIO) ;
		//	}
		//}
		//catch (Throwable cnfe)
		//{
		//	throw new Exception(cnfe.toString()) ;
		//}

		m_iaInetAddress = InetAddress.getLocalHost() ;
	}


	/**
	 * Returns the local host.
	 * @return		the local host
	 * @exception	UnknownHostException 
	 *		thrown to indicate that the IP address of a host could not be determined
	 */

	public InetAddress getLocalHost()
		throws UnknownHostException 
	{
		return m_iaInetAddress ;
	}


	/**
	 * Returns the host name for this IP address.
	 * @return	the host name for this IP address
	 */

	public String getHostName()
	{
		return m_iaInetAddress.getHostName() ;
	}


	/**
	 * Returns the IP address string "%d.%d.%d.%d".
	 * @return	the IP address string "%d.%d.%d.%d"
	 */

	public String getHostAddress()
	{
		return m_iaInetAddress.getHostAddress() ;
	}
}
