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

import java.util.Enumeration;

/**
 * Defines the basic methods for manipulmating cached object.
 * <br>
 * Syntax is much inspired from Hashtable which are the best candidates because :
 * <br> - They can store any kind of objects.
 * <br> - easy to use and well known syntax from Java 1.00
 * <br> - For Servlet concurrent use: the Hashtable is synchronized (not the collections)
 * <br>
 * However, the interface allow of course any cache container type for 
 * whatever other implementations!
 */

public interface Cacheable
{			
	
	/**
	 * put an object into the cache
	 * @param oName		the object name/identifier
	 * @param oValue	the object value
	 */
	public void putCache(Object oName, Object oValue);
	
	
	/**
	 * get an object from the cache, using is name/id
	 * @param oName		the object name/identifier
	 * 
	 * @return Value	the object value
	 */
	public Object getCache(Object oName);

	
	/**
	 * Tests if the specified object is (a key) in the cache 	
	 * @param oName		the object name/identifier
	 * @return true if and only if the specified object is (a key) in the cache
	 */
	public boolean containsKey(Object oName);

	
	/**
	 * remove an object from the cache, using is name/id
	 * @param oName		the object name/identifier
	 */
	public void remove(Object oName);
	
	/**
	 * Returns the number of keys in this cache. 
	 * @return the number of keys in this cache.
	 */

	public int size();

	
	/**
	 * Returns an enumeration of the keys in this cache. 
	 * @return  an enumeration of the keys in this cache. 
	 */
	
	public Enumeration keys();
	
	/**
	 * Tests if this cache maps no keys to values. 
	 * @return  true if this cache maps no keys to values; false otherwise. 
	 */
	
	public boolean isEmpty();

}

// end
