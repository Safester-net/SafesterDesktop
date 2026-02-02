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

public class DomainChecker   
{
    /** The domain to check */
    private String m_sDomain;
	
	/**	The IP Address checker */
	private IPAddressChecker m_iacChecker ;
	
	/**	The Host Name checker */
	private HostNameChecker m_chncChecker ;
    
    /**
     * Creates a domain checker by initializing the underlaying checkers:
     * IP Address checker and Hostname checker.
     * @param   the domain to check (DNS or IP address)
     */
    
    public DomainChecker(String sDomain)
		throws IOException
    {
        this.m_sDomain = sDomain;
		m_iacChecker = new IPAddressChecker() ;
		m_chncChecker = new HostNameChecker() ;
    }
    
	
	/**
	 * Checks the domain validity.<br>
	 * This calls the method isValid() of one argument with allowIP set at true.<br>
	 * @return	true if the domain is of valid DNS form or of valid IP address form,
	 *			false otherwise
	 */
                                   
	public boolean isValid()
	{
        return isValid(true) ;
    }
	
	
	/**
	 * Checks the domain validity.<br>
	 * If allowIP is set, IP addresses are allowed, otheriwse only valid DNS form are
	 * considered valid.<br>
	 * @param	allowIP	true to allow IP address as valid domain, false to validate DNS only
	 * @return	true if the domain is of valid DNS form
	 *			(or of valid IP address form, if allowIP is set),
	 *			false otherwise
	 */
	
	public boolean isValid(boolean allowIP)
	{
		// if domain is a valid hostname, return true now
		if(m_chncChecker.isValid(m_sDomain))
			return true ;
		
		// if IP isn't allowed, then return false because previous domain check failed
		if(!allowIP)
			return false ;
		
		// IP is allowed, so check for it and return result
		return m_iacChecker.isValid(m_sDomain) ;
	}
}

// End
