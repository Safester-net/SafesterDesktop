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
package net.safester.clientserver.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Nicolas de Pomereu
 *
 */
public class TestConnection {

    public static String DB2_DRIVER_CLASS_NAME = "com.ibm.db2.jcc.DB2Driver";
    public static String DB2_URL = "jdbc:db2://localhost:50000/AWAKE_EX";
    
    public static String ORACLE_DRIVER_CLASS_NAME = "oracle.jdbc.driver.OracleDriver";
    public static String ORACLE_URL = "jdbc:oracle:thin:awake_example@//localhost:1521/XE";
    
    public static String H2_DRIVER_CLASS_NAME = "org.h2.Driver";    
    public static String H2_URL = "jdbc:h2:tcp://localhost/~/awake_example"; 
    
    public static String DB_DRIVER_CLASS_NAME = H2_DRIVER_CLASS_NAME;
    public static String DB_URL = H2_URL;
    public static String USERNAME = "user1";
    public static String PASSWORD = "password1";
    
    
    /**
     * 
     */
    public TestConnection() {
	// TODO Auto-generated constructor stub
    }

    public static Connection get() throws SQLException {
        try {
            Class.forName(DB_DRIVER_CLASS_NAME).newInstance();
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        Connection connection = DriverManager.getConnection(DB_URL,
        	USERNAME, PASSWORD);

        return connection;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

	
	Connection con = get();
	
	if (con == null) {
	    System.out.println("connection is null!");
	    return;
	}

	System.out.println("connection is OK.");
	
	
	//String schema = con.getSchema();
	//System.out.println("schema: " + schema);
	
    }

}
