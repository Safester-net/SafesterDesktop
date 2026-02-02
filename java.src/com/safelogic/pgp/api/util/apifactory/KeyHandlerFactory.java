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
package com.safelogic.pgp.api.util.apifactory;

import com.safelogic.pgp.api.KeyHandlerOne;
import com.safelogic.pgp.apispecs.KeyHandler;

public class KeyHandlerFactory
{
	/** Says which class to use in instance creation **/
	public static String className = "KeyHandlerOne";
	
	/**
	 * proctected constructor
	 */
	protected KeyHandlerFactory()
	{
		// Nothing
	}
	
	/**
	 * @return a concrete instance of KeyHandler.
	 */
	public static KeyHandler getInstance()
	{
		if (className.equals("KeyHandlerOne"))
		{
			return new KeyHandlerOne();
		}
		else if (className.equals("KeyHandlerSimul"))
		{
			throw new NoSuchMethodError("Not implemented!");
			//return new KeyHandlerSimul();
		}
		else 
		{
			throw new IllegalArgumentException("unknown concrete class name for KeyHandler");
		}
			
	}

}

