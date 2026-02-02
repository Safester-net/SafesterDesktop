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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class ConnectionUtil
{

	// SQL JDBC connection
	protected Connection m_connection = null;

	/**
	 * Constructor
	 * @param connection    actual SQL/JDBC Connection in use
	 */
	public ConnectionUtil(Connection connection)
	{
		this.m_connection = connection;
	}

	/**
	 * Gets a SQL connection. Params are define in a ini file.
	 * @param		sConnectionBrokerIni	the connection broker ini file	
	 * @return		a new SQL connection
	 */

	public static Connection GetConnection(String sConnectionBrokerIni)
		throws Exception
	{
		ConnectionLoader conLoader = new ConnectionLoader(sConnectionBrokerIni);
		return conLoader.getConnection();
	}

	/**
	* List of a table primary keys into a Vector
	* @param sTable the SQL table 
	* @return a Vector of primary key names
	*/
	public Vector getPrimaryKeys(String sTable) throws SQLException
	{
		Vector vPrimaryKeys = new Vector();
		String sCatalog = null;
		String sSchema = null;

		String sStatement = "DESC " + sTable;
		Statement stmt = this.m_connection.createStatement();
		ResultSet rs = stmt.executeQuery(sStatement);

		ResultSetMetaData rsmd = rs.getMetaData();

		sCatalog = rsmd.getCatalogName(1);
		sSchema = rsmd.getSchemaName(1);

		DatabaseMetaData meta = this.m_connection.getMetaData();
		ResultSet rsMeta = meta.getPrimaryKeys(sCatalog, sSchema, sTable);

		while (rsMeta.next())
		{
			vPrimaryKeys.addElement(rsMeta.getObject(4));
		}

		return vPrimaryKeys;
	}

	/**
	 * Test if a column value is a valid String foreign key in a reference table
	 * NOTE : THE REFERENCE TABLE MUST HAVE ONLY ONE PRIMARY KEY
	 * @param sRefTable     the reference SQL table
	 * @param sRefKeyName   the reference  key name
	 * @param sForeignKey   the foreign key value
	 */
	public boolean isValidForeignKey(
		String sRefTable,
		String sRefKeyName,
		String sForeignKey)
		throws SQLException
	{
		boolean bValid = false;

		String sStatement =
			"SELECT "
				+ sRefKeyName
				+ " FROM "
				+ sRefTable
				+ " WHERE "
				+ sRefKeyName
				+ "= ?";

		PreparedStatement query =
			this.m_connection.prepareStatement(sStatement);

		query.setString(1, sForeignKey);

		ResultSet rs = query.executeQuery();

		if (rs.next())
			bValid = true;
		else
			bValid = false;

		return bValid;
	}

	/**
	 * Test if a column value is a valid integer foreign key in a reference table
	 * NOTE : THE REFERENCE TABLE MUST HAVE ONLY ONE PRIMARY KEY
	 * @param sRefTable     the reference SQL table
	 * @param sRefKeyName   the reference  key name
	 * @param sForeignKey   the foreign key value
	 */
	public boolean isValidForeignKey(
		String sRefTable,
		String sRefKeyName,
		int nForeignKey)
		throws SQLException
	{
		boolean bValid = false;

		String sStatement =
			"SELECT "
				+ sRefKeyName
				+ " FROM "
				+ sRefTable
				+ " WHERE "
				+ sRefKeyName
				+ "= ?";

		PreparedStatement query =
			this.m_connection.prepareStatement(sStatement);

		query.setInt(1, nForeignKey);

		ResultSet rs = query.executeQuery();

		if (rs.next())
			bValid = true;
		else
			bValid = false;

		return bValid;
	}

}

// End
