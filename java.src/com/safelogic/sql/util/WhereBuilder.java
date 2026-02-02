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

// WhereBuilder.java	: ConfiMail - SQL Select with Search options
// Copyright (c) SafeLogic, 2000 - 2001
//
// 08/10/01 20:15 NDP - creation
// 08/10/01 22:55 NDP - ready to test
// 14/10/01 16:15 NDP - add setCaseSensitive()
// 22/10/01 17:05 NDP - add TRIM(MAILBOX_ID)
// 26/12/01 12:40 NDP - remove use of Connection & If there is no WHERE in clause, add one. 
//                      Otw, add a AND
// 08/07/02 18:05 NDP - add getCondition()


/**
 * 
 * Use this class to build Statement with WHERE conditions
 * <br>Notes: <br>
 * 1) The allowed operator are static final in this class.
 * 2) The null or "" String values are discarded if in conditions.
 * 3) The String are SQL Trimed and "SQL encoded" with ConfiMail SQLStringCoder.
 * 4) The case sensitive option can be set before each condition
 * 
 */

public class WhereBuilder
{        
	// Base Operators
	public final static String LIKE			= "LIKE";
	public final static String EQUAL		= "=";
	public final static String BETWEEN		= "BETWEEN";
	public final static String LT			= "<";
	public final static String LT_OR_EQUAL	= "<=";
	public final static String GT			= ">";
	public final static String GT_OR_EQUAL	= ">=";
	
	// Operator list
	private final static String [] OPERATOR_LIST
		= {LIKE, EQUAL, BETWEEN, LT, LT_OR_EQUAL, GT, GT_OR_EQUAL};
												      
	// The SQL Statement
	protected String m_sStatement = null;
	
	// The condition string
	protected String m_sCondition = null;
	
	// The case sensitive mode
	private boolean m_bCaseSensitive = false;
	
	// The encoder
	SQLStringCoder m_sscSQLCoder = null;
	
	// Key words for the conditions
	
    /**
     * Constructor
     * @param sStatement    the base Statement to use
     */
    public WhereBuilder(String sStatement)
    {
		this.m_sStatement	= sStatement;	
		this.m_sscSQLCoder = new SQLStringCoder();
    }		
       
	
	/**
	 * Set the condition dependinf on the state of the condition
	 */
	private void setConditionClause()
	{
		if (this.m_sCondition == null)
		{
			// If there is no WHERE in clause, add one. Otw, add a AND
			if (this.m_sStatement.toUpperCase().indexOf("WHERE") != -1)
			{
				this.m_sCondition = " AND ";
			}
			else {
				this.m_sCondition = " WHERE ";
			}
		}
		else {
			this.m_sCondition+= " AND ";
		}		
		
	}
	
	/**
	 * Test if operator is allowed
	 * @param	sOperator	the operator to test
	 */
	private void testOperatorValidity(String sOperator)
	{
		boolean bIsOperatorAllowed = false;
		
		for(int i=0 ; i < OPERATOR_LIST.length ; i++)
		{
			if (OPERATOR_LIST[i].equals(sOperator))
			{
				bIsOperatorAllowed = true;
			}
		}	
		
		if (! bIsOperatorAllowed)
		{
			throw new IllegalArgumentException("CM. Forbidden operator: " + sOperator);
		}
	}
	
	/**
	 * Set the Case Sensitive mode for the search
	 * @param	bCaseSensitive		if true, the search will be case sensitive
	 */
	public void setCaseSensitive(boolean bCaseSensitive)
	{
		this.m_bCaseSensitive = bCaseSensitive;
	}
	
	/**
	 * Set the Case Sensitive mode for the search.
	 * This method uses a string as parameters; it's easier for form values retrieval
	 * @param	sCaseSensitive		if boolean value is true, 
	 *								the search will be case sensitive
	 */
	public void setCaseSensitive(String sCaseSensitive)
	{
		if (sCaseSensitive == null || sCaseSensitive.equals(""))
		{
			sCaseSensitive = "false";
		}
		
		this.m_bCaseSensitive = new Boolean(sCaseSensitive).booleanValue();
	}	
	
	
	/**
	 * Add a condition
	 * @param	sColumn			the column name
	 * @param	sOperator		the comparaison operator to use
	 * @param	sValue			the Value to compare with
	 */
	public void addCondition(String sColumn, String sOperator, String sValue)
	{
		if( sValue == null || sValue.equals("") )
		{
			return;
		}
			
		this.setConditionClause();
		this.testOperatorValidity(sOperator);
		
		sValue = this.m_sscSQLCoder.encode(Trimmer.Trim(sValue));
		
		if (sOperator.equalsIgnoreCase(LIKE))
		{
			sValue = "%" + sValue + "%";
		}
		
		if (sOperator.equals(this.EQUAL))
		{
			sColumn = "TRIM("+ sColumn + ")";
		}		
						
		//OK, add the condition
		if (this.m_bCaseSensitive)
		{ 
			this.m_sCondition += sColumn + " " + sOperator + " " + "'" + sValue + "'" + " ";
		}
		else 
		{
			this.m_sCondition += "UPPER(" + sColumn + ") " 
							  + sOperator + " " 
							  + "'" + sValue.toUpperCase() + "'" + " ";
		}
	}
	
	/**
	 * Add a condition
	 * @param	sColumn			the column name
	 * @param	sOperator		the comparaison operator to use
	 * @param	sValue1			the first Value to compare with
	 * @param	sValue2			the second Value to compare with
	 */
	public void addCondition(String sColumn, 
							 String sOperator, 							 
							 String sValue1, 
							 String sValue2)
	{
		if( sValue1 == null || sValue1.equals("") )
		{
			return;
		}
				
		if( sValue2 == null || sValue2.equals("") )
		{
			return;
		}		
		
		this.setConditionClause();
		
		//this.testOperatorValidity(sOperator);
		
		// Only between is authorized in this version
		if (! sOperator.equalsIgnoreCase(BETWEEN))
		{
			throw new IllegalArgumentException("CM. Forbidden operator: " + sOperator);
		}
		
		sValue1 = this.m_sscSQLCoder.encode(Trimmer.Trim(sValue1));
		sValue2 = this.m_sscSQLCoder.encode(Trimmer.Trim(sValue2));
							
		if (sOperator.equals(this.EQUAL))
		{
			sColumn = "TRIM("+ sColumn + ")";
		}
		
		//OK, add the condition
		if (this.m_bCaseSensitive)
		{ 		
			this.m_sCondition+= sColumn + " " + sOperator 
								+ " " + "'" + sValue1 + "' AND '" + sValue2 + "'" + " ";
		}
		else 
		{
			this.m_sCondition+= "UPPER(" + sColumn + ") " + sOperator 
								+ " " + "'" 
								+ sValue1.toUpperCase() + "' AND '" 
								+ sValue2.toUpperCase() + "'" + " ";			
		}
	}	
	
	/**
	 * Add a condition
	 * @param	sColumn			the column name
	 * @param	sOperator		the comparaison operator to use
	 * @param	nValue			the numeric Value to compare with
	 */
	public void addCondition(String sColumn, String sOperator, int nValue)
	{
		this.setConditionClause();
		this.testOperatorValidity(sOperator);				
						
		//OK, add the condition
		this.m_sCondition+= sColumn + " " + sOperator + " " + nValue + " ";
	}
	
	/**
	 * Add a condition
	 * @param	sColumn			the column name
	 * @param	sOperator		the comparaison operator to use
	 * @param	nValue1			the first numeric Value to compare with
	 * @param	nValue2			the second numeric Value to compare with

	 */
	public void addCondition(String sColumn, 
							 String sOperator, 							 
							 int nValue1, 
							 int nValue2)
	{
		this.setConditionClause();
		
		//this.testOperatorValidity(sOperator);
		
		// Only between is authorized in this version
		if (! sOperator.equalsIgnoreCase(BETWEEN))
		{
			throw new IllegalArgumentException("CM. Forbidden operator: " + sOperator);
		}
										
		//OK, add the condition
		this.m_sCondition+= sColumn + " " + sOperator 
							+ " " + nValue1 + " AND " + nValue2 + " ";
	}		
	
	/**
	 * Get the Statement
	 * @return the Statement with all the conditions, ready to use
	 */
	public String getStatement()
	{
		String sStatement = null;
		
		if (this.m_sCondition == null)
		{			
			sStatement = this.m_sStatement;
		}
		else {
			sStatement = this.m_sStatement + this.m_sCondition;
		}
		
		return sStatement;
	}
	
	/**
	 * Get the condition
	 * @return the condition of the statement beginning at the wird "WHERE"
	 */
	public String getCondition ()
	{
		if (this.m_sCondition == null)
		{
			return  " ";
		}
		else {
			return this.m_sCondition;
		}
	}	
}

// END
