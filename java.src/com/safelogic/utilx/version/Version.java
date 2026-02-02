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
package com.safelogic.utilx.version;

// for security rules


/**
 * Displays the SafeJdbc product Version
 */

public class Version 
{
        
    
	public static final String getVersion()
	{
		return "" + new PRODUCT() ;
	}
	
	public static final String getFullVersion()
	{
		String CR_LF = System.getProperty("line.separator") ;
		
		return PRODUCT.DESCRIPTION + CR_LF
			   + getVersion() + CR_LF
			   + "by : " + new VENDOR() ;
	}
	
	public String toString()
	{
		return getVersion() ;
	}
	
	
	public static final class PRODUCT
	{
		public static final String VERSION			= VersionValues.VERSION_MAJOR;
		public static final String VERSION_MINOR	= VersionValues.VERSION_MINOR;
		public static final String NAME				= "utilx" ;
		public static final String DESCRIPTION		= "utilx : SafeLogic tools" ;
		public static final String DATE				= VersionValues.DATE;
		
		public String toString()
		{
			return NAME + " " + VERSION + " " + VERSION_MINOR + " " + DATE ;
		}
	}
	
	
	public static final class VENDOR
	{
		public static final String NAME		= "SafeLogic" ;
		public static final String WEB		= "http://www.safelogic.com" ;
		public static final String COPYRIGHT	= "Copyright &copy; 2001, 2002";
		public static final String EMAIL		= "info@safelogic.com" ;
		public static final String PHONE		= "(33) (0)1 45 72 25 15" ;
		public static final String FAX		= "(33) (0)1 45 72 14 06" ;
		
		public String toString()
		{
			return VENDOR.NAME + " - " + VENDOR.WEB ;
		}
	}
	
	/*
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
	*/
	/**
	 * MAIN
	 */
	
	public static void main(String[] args) 
	{
		System.out.println(getFullVersion());
	}
}
