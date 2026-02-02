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
package com.safelogic.pgp.api.toolkit;

import java.util.HashMap;
import java.util.Map;


public class CgeepCmdLineOption {

	//Short name 
	private String shortName;
	//Long name
	private String longName;
	
	//Description
	private String desc;
	
	//List of arguments
	private Map<String, String> arguments;
	//Indicates if arguments are optional or not
	private boolean optional = false;
	
	
	public CgeepCmdLineOption(String shortName, String longName, String desc)
	{
		this.shortName = shortName;
		this.longName = longName;
		this.desc = desc;
		
	}
	
	/**
	 * Add argument to option
	 * @param argName		Name of arg
	 * @param argValue		Description 
	 */
	public void addArg(String argName, String argValue)
	{
		if(arguments == null)
		{
			arguments = new HashMap<String, String>();
		}
		arguments.put(argName, argValue);
	}
	
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Map<String, String> getArguments() {
		return arguments;
	}
	
	public void setOptional(boolean optional)
	{
		this.optional = optional;
	}
	
	public boolean isOptional()
	{
		return this.optional;
	}
}
