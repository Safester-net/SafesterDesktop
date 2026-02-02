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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import com.safelogic.utilx.conf.IniProperties;

/**
 * This class loads JDBC Connections infos from an ini file :
 * <br> - Driver Name
 * <br> - Database Uri "Raw"  (without userid & password)
 * <br> - Database Uri "Full" (with userid & password)
 */

public class ConnectionLoader 
{
   
    /** The Driver Name */    
	private String m_sDriverName;
    
    /** The Database Uri (without UserName & Password) */
    private String m_sDbUriRaw;    
    
    /** The Database Uri (includes UserName & Password) */
	private String m_sDbUri;
    
    /**
     * Constructor
     * @param       fConnectionIni    the connection ini file abslute address 
     */
    public ConnectionLoader(File fConnectionIni)
        throws IOException
    {
        Properties props = new Properties();
        props.load(new FileInputStream(fConnectionIni));
        
        m_sDriverName = props.getProperty("DRIVER");
        
        m_sDbUriRaw = props.getProperty("DB_URI_ROOT");
        
        String sUser = props.getProperty("USER");
        String sPassword= props.getProperty("PASSWORD") ;
        
        m_sDbUri = m_sDbUriRaw + "user=" + sUser + "&password=" + sPassword;
        
        /*
        if(m_sDbUri == null)
            throw new IOException("Unable to retrieve DataBase URI.") ;
        if(m_sDriverName == null)
            throw new IOException("Unable to retrieve DataBase Driver.") ;
         */           
    }
    
    
    /**
     * Constructor
     * @param       sConnectionIni    the connection ini file rwa name - 
     *              *must* be in classpath
     */
    public ConnectionLoader(String sConnectionIni)
        throws IOException
    {
        IniProperties props = new IniProperties() ;
        props.load(sConnectionIni) ;
        
		m_sDriverName = props.getString("DRIVER");
		
        m_sDbUriRaw = props.getString("DB_URI_ROOT");
        
		String sUser = props.getString("USER");
        String sPassword= props.getString("PASSWORD") ;
        
        m_sDbUri = m_sDbUriRaw + "user=" + sUser + "&password=" + sPassword;
        
        /*
        if(m_sDbUri == null)
            throw new IOException("Unable to retrieve DataBase URI.") ;
        if(m_sDriverName == null)
            throw new IOException("Unable to retrieve DataBase Driver.") ;
         */           
    }
    		
	/**
	 * Gets a SQL connection. Params are define in a ini file.
	 * @return		a new SQL connection
	 */

	public Connection getConnection()
		throws Exception
	{		
		Class.forName(m_sDriverName).newInstance(); 
		return DriverManager.getConnection(m_sDbUri);
	}	
	
	       
	/**
	 * Returns the dbUri.
	 * @return String
	 */
	public String getDbUri()
	{
		return m_sDbUri;
	}

	/**
	 * Returns the dbUriRaw.
	 * @return String
	 */
	public String getDbUriRaw()
	{
		return m_sDbUriRaw;
	}

	/**
	 * Returns the driver name
	 * @return String
	 */
	public String getDriverName()
	{
		return m_sDriverName;
	}

}

// End
