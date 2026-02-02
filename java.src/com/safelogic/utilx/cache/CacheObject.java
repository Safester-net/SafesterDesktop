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
import java.util.Hashtable;

/**
 * Use *only* heritance of this class to cache objects *in synchronized mode* 
 * in the current JVM.
 * <br>
 * it implements thr Cacheable interface (this is cleaner design).
 * <br>
 * An  object to cache is identified by its name in form of a String/identifier and it's value.
 * The pair (Object Name, Object) is stored into the static Hashtable of the 
 * sister/implementation class
 * <br>
 * This version uses a Hashtable because the cache mecanism is to be synchronized.
 * <br>
 * The calls are non-static ==> better for future usage, even if all methods calls
 * refer to a static Hashtable. 
 * <br>
 * Future enhancements :
 * <br> - set a limit to the cache size
 * <br> - add a minutes to live in constructor + isExpired() test method.
 * <br> - thread to destroy "old" cached values
 * <br>
 * The class is declared abstract because it allows to use multiple Hastable "containers"
 * instead of a big one.
 * <br>
 * A sister class for templates would use it's own static Hashtable,
 * Another sister class for Result Sets would use it's own static Hashtable, etc.
 * <br>
 * Please see the CmCacheTemplate implementation as for example.
 */

public abstract class CacheObject implements Cacheable
{		
	
	/** The debug flag */ 
	protected boolean CM_DEBUG = false;
	
	/** 
	 * Hastable to contain cached objects.
	 * It is *NOT* static, because the "real" Hashtable is contained in the sister
	 * concrete class
	 */
	
	private Hashtable m_hCacheContainer	= null;	
		
	
	/**
	 * Constructor. 
     * Get the handle on the cache from the sister/concrete class
     * @param hCacheContainer	the handle on the Hashtable of the sister/concrete class
	 */
	public CacheObject(Hashtable hCacheContainer)		
	{			
		m_hCacheContainer = hCacheContainer;
	}	
	
	
	/**
	 * put an object into the cache
	 * @param oName		the object name/identifier
	 * @param oValue	the object value
	 */
	public void putCache(Object oName, Object oValue)
	{
		
		// Help debug ==> do not allow throw of null pointer exception
		if (oName == null)
		{
			throw new IllegalArgumentException("CM : Object Name is null!");
		}
		
		// Help debug ==> do not allow throw of null pointer exception
		if (oValue == null)
		{
			throw new IllegalArgumentException("CM : Object Value is null!");
		}		
		
		// Remember if the Hashtable previously contains a mapping for the key,
		//the old value will be replaced.  This is valid functioning.
		this.m_hCacheContainer.put(oName, oValue);		
	}
	
	/**
	 * get an object from the cache, using is name/id
	 * @param oName		the object name/identifier
	 * 
	 * @return Value	the object value
	 */
	public Object getCache(Object oName)
	{
		// Help debug ==> do not allow to get a null object
		if (oName == null)
		{
			throw new IllegalArgumentException("CM : Object Name is null!");
		}		
		
		Object oValue = this.m_hCacheContainer.get(oName);
		
		if (oValue == null) // This seems a bit stupid ? I don't know.
		{
			return null;
		}
		
		return oValue;
	}
	
	
	/**
	 * Clear the cache container
	 */
	
	public void clear()
	{
		this.m_hCacheContainer.clear();
	}
	
	
	/**
	 * Tests if the specified object is (a key) in the cache 	
	 * @param oName		the object name/identifier
	 * @return true if and only if the specified object is (a key) in the cache
	 */
	public boolean containsKey(Object oName)
	{
		// Help debug ==> do not allow to get a null object
		if (oName == null)
		{
			throw new IllegalArgumentException("CM : Object Name is null!");
		}	
		
		return this.m_hCacheContainer.containsKey(oName);
	}

	
	/**
	 * remove an object from the cache, using is name/id
	 * @param oName		the object name/identifier
	 */
	public void remove(Object oName)
	{		
		// Help debug ==> do not allow to remove a null object
		if (oName == null)
		{
			throw new IllegalArgumentException("CM : Object Name is null!");
		}		
		
		this.m_hCacheContainer.remove(oName);
	}	
	
	/**
	 * Returns the number of keys in this cache. 
	 * @return the number of keys in this cache.
	 */

	public int size()
	{
		return this.m_hCacheContainer.size();
	}
	
	/**
	 * Returns an enumeration of the keys in this cache. 
	 * @return  an enumeration of the keys in this cache. 
	 */
	
	public Enumeration keys()
	{
		return this.m_hCacheContainer.keys();
	}

	
	/**
	 * Tests if this cache maps no keys to values. 
	 * @return  true if this cache maps no keys to values; false otherwise. 
	 */
	
	public boolean isEmpty()
	{
		return this.m_hCacheContainer.isEmpty();
	}	
	

	/**
	 * debug print line
	 */
	private void debugPrintln(String sMsg)
	{
		if(CM_DEBUG)
			System.out.println(sMsg) ;
	}		
	
}

// End
