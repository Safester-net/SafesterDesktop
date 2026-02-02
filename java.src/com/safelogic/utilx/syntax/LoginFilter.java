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
package com.safelogic.utilx.syntax;

import java.io.IOException;
import java.util.Vector;

import com.safelogic.utilx.conf.IniPropertyList;

/**
 * Use to check if a login (e-mail prefix) is allowed, following to some rules:
 * <br> - 1) Minimum login length.
 * <br> - 2) Login stricly *not* in login_dictionary.ini dictionnary.
 * <br> - 3) Login does *not* contain a subchain in login_suchain_reserved.ini dictionnary.
 * <br>
 * Other rules may be added in the futur
 */

public class LoginFilter 
{
	/** the file of reserved login */
	public static final String LOGIN_DICTIONARY_INI 		= "login_dictionary.ini";	

	/** the file of reserved login subchains */
	public static final String LOGIN_SUBCHAIN_DICTIONARY_INI 
		= "login_subchain_dictionary.ini";		
	
	// Error codes
	public static final int OK_LOGIN_IS_VALID				= 0;
	public static final int	ERROR_LOGIN_TOO_SHORT			= 1;
	public static final int	ERROR_LOGIN_TOO_LONG			= 2;	
	public static final int	ERROR_LOGIN_IN_DICTIONNARY		= 3;
	public static final int	ERROR_LOGIN_FORBIDEN_SUBCHAIN	= 4;
	
	//
	// Defines the login rules
	//
	
	/** minimal length allowed for a Login*/
	public static final int RULE_MINIMAL_LENGTH = 4;
	
	/** maximal length allowed for a Login*/
	public static final int RULE_MAXIMAL_LENGTH = 25;	 
	
	// Login
	private String m_sLogin = null;
	
	// error codes (when refusal)
	private int mErrorCode = 0;
	
	
	/**
	 * Constructor()
	 * @param   Login or e-mail prefix as login@confimail.com
	 */
	
	public LoginFilter(String sLogin)
	{
		this.m_sLogin		= sLogin;
		this.mErrorCode = OK_LOGIN_IS_VALID; // For init only
	}
	
	
	/**
	 * Returns true if the login is in the login_dictionary
	 * @return	true if the Login is in the dictionnary
	 */
	private boolean isInLoginDictionnary()
		throws IOException
	{	
		IniPropertyList propDic = new IniPropertyList() ;
		propDic.load(LOGIN_DICTIONARY_INI) ;
		Vector vList = propDic.getList();
		
		if (vList.contains(m_sLogin))
		{
			return true;
		}
		
		// Ok, sLogin not in Dictionnary
		return false;
	}
	

	/**
	 * Returns true if the login is a subchain of one of the 
	 * elements of the subchain Dictionnary
	 * @return	true if the login is a subchain of one of the 
	 * elements of the subchain Dictionnary
	 */
	private boolean containsForbidenSubchain()
		throws IOException
	{	
		String sLogin = null;
		
		IniPropertyList propDic = new IniPropertyList() ;
		propDic.load(LOGIN_SUBCHAIN_DICTIONARY_INI) ;
		
		Vector vList = propDic.getList();		
		
		for (int i=0; i < vList.size(); i++)
		{           
			sLogin  = (String)vList.elementAt(i);
			
			if (m_sLogin.indexOf(sLogin) != -1)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 *
	 * Verifies that the Login has a valid form
	 * <br>
	 * @return	true if the Login is valid
	 *
	 */
	
	public boolean isValid()
		throws IOException
	{
		// 1) Test minimal length
		if (this.m_sLogin.length() < RULE_MINIMAL_LENGTH)
		{
			this.mErrorCode = ERROR_LOGIN_TOO_SHORT;
			return false;
		}
		
		// 2) Test maximal length
		if (this.m_sLogin.length() > this.RULE_MAXIMAL_LENGTH)
		{
			this.mErrorCode = ERROR_LOGIN_TOO_LONG;
			return false;
		}		
		
		// 3) Test if login is in login dictionary
		if (this.isInLoginDictionnary())
		{
			this.mErrorCode = ERROR_LOGIN_IN_DICTIONNARY;
			return false;
		}
		
		// 4) Test if login is a subchain of forbidden subchains
		if (this.containsForbidenSubchain())
		{
			this.mErrorCode = ERROR_LOGIN_FORBIDEN_SUBCHAIN;
			return false;
		}		
		
		this.mErrorCode = OK_LOGIN_IS_VALID;
		return true;        
	}    
	
	/**
	 * Returns OK_LOGIN_IS_VALID if the login is accepted or one of the 
	 * follwing error codes if refused :
	 * <br>ERROR_LOGIN_TOO_SHORT
	 * <br>ERROR_LOGIN_TOO_LONG
	 * <br>ERROR_LOGIN_IN_DICTIONNARY
	 * <br>ERROR_LOGIN_FORBIDEN_SUBCHAIN
	 */
	public int getErrorCode()
	{
		return this.mErrorCode;
	}
	
	
	/** main */
	public static void main(String[] args)
		throws Exception
	{
		String sLogin = null;
		
		if (args.length == 1)
		{
			sLogin = args[0];	
		}
		else {
			System.out.println("LoginFilter [login]");
			return;
		}
		
		LoginFilter lf = new LoginFilter(sLogin);
		
		if (lf.isValid())
		{
			System.out.println(sLogin + " is valid!");
		}
		else {
			System.out.println(sLogin + " is *not* valid!");
			System.out.println("Error Code: " + lf.getErrorCode());
		}
		
	}
}

// END
