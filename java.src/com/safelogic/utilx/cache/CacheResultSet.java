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
package com.safelogic.utilx.cache;

import java.util.Hashtable;

import com.safelogic.utilx.Debug;

/**
 * This is an implementation of caching reserved *only* for the SQL results sets 
 * <br>
 * m_hCacheContainerRs is isolated will not collide with any other cache container.
 */

public class CacheResultSet extends CacheObject 
{		
	
	/** The debug flag */ 
	private boolean CM_DEBUG = Debug.isSet(this);
	
	/** 
	 * Hastable to contain all ConfiMail cached Statement results
	 * The Statement results are cached by pair : (SqlString, ResultSet)
	 * where:
	 * <br> - SqlString is the String containing the syntax of the Statement
	 * <br> - ResultSet	is the Result Set containing the result after execution
	 * 
	 */
	private static Hashtable m_hCacheContainerRs = new Hashtable();	
		
	/**
	 * Constructor. 
	 * Pass the handle on m_hCacheContainer to abstract class, so that methods can
	 * work on it. See parent abstract class CacheObject
	 */
	public CacheResultSet()		
	{			
		super(m_hCacheContainerRs);
		super.CM_DEBUG = CM_DEBUG;
	}	

}

// end
