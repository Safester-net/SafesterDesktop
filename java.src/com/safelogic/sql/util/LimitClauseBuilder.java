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
package com.safelogic.sql.util;

/**
 * Writes the correct syntax for a LIMIT clause, depending on the SQL Engine.
 * <br>
 * LIMIT Clause is *not* SQL ANSI, so each SQL has it's implementation.
 * This class detects the SQL engine with the Driver name.
 * <br>
 * The LIMIT/OFFSET clause is then written, using only the numbers in input
 * <br>
 * Exemples :
 * to retrieve the 21th to 30th rows in order :
 * <br>In MySQL : SELECT * FROM USER_LOGIN ORDER BY USER_ID LIMIT 10 OFFSET 20;
 * <br>in PSQL  : SELECT * FROM USER_LOGIN ORDER BY USER_ID LIMIT 20, 10;
 * <br>
 * The returned String woud be:
 * <br>In MySQL      : " LIMIT 10 OFFSET 20"
 * <br>in PostrgreSql: " LIMIT 20, 10 "
 * <br>
 * MySql and Postgres are the only supported SQL Engines.
 */

public class LimitClauseBuilder
{
	/** the JDBC Driver value */
	protected String m_sDriver	= null;
	
	/** the LIMIT value	 */
	protected int	m_nLimit	= 0;
	
	/** the OFFSET value*/
	protected int	m_nOffset	= 0;	
	
	/**
	 * Creates a new SQL Limit Clause
	 * @param	sDriver		the full Driver value as returned by JDBC
	 * @param	nLimit		the LIMIT value
	 * @param	nOffset		the OFFSET value
	 */
	
	public LimitClauseBuilder(String sDriver, int nLimit, int nOffset)
	{
		this.m_sDriver	= sDriver;
		this.m_nLimit	= nLimit;
		this.m_nOffset	= nOffset;
	}
	
	
	/**
	 * @return	the correct SQL LIMIT syntax for the engine
	 */
	
	public String getLimitSyntax()
	{
		if (m_sDriver.toLowerCase().indexOf("postgresql") != -1)
		{
			//SELECT * FROM USER_LOGIN ORDER BY USER_ID LIMIT 10 OFFSET 20;
			return " LIMIT " + m_nLimit + " OFFSET " +  m_nOffset;
		}
		else if (m_sDriver.toLowerCase().indexOf("mysql") != -1)
		{
			//SELECT * FROM USER_LOGIN ORDER BY USER_ID LIMIT 20, 10;
			return " LIMIT " + m_nOffset + ", " +  m_nLimit;
		}
		else {
			throw new IllegalArgumentException("JDBC/SQL Engine not supported: " + m_sDriver);
		}
	}
	
}

// End
