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
package com.safelogic.utilx.cmdtools;

// 23/07/01 17:00 GR - creation (from CmRegisterPass.textPrompt())

import java.io.IOException;

/**
 * Provides a service to collect user typed input.
 */

public abstract class TextPrompt
{
	
	/**
	 * Collects user typed input (the passphrase).
	 * @param	sPrompt		the message prompted to the user
	 */
	
	public static String prompt(String sPrompt)
		throws IOException
	{
		byte [] bIn = new byte[512];
		String sIn  = new String();
		System.out.print(sPrompt);
		System.out.flush() ;
		System.in.read(bIn) ;
		sIn = new String(bIn);
		sIn = sIn.trim();
		return new String(sIn);
	}
}
