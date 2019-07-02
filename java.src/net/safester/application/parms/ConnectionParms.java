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
package net.safester.application.parms;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Connection;
import java.sql.SQLException;

import org.awakefw.commons.api.client.HttpProtocolParameters;
import org.awakefw.commons.api.client.HttpProxy;
import org.awakefw.commons.api.client.InvalidLoginException;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import net.safester.application.register.Register;
import net.safester.application.util.crypto.PassphraseUtil;
import net.safester.clientserver.ServerParms;

/**
 * This class provides Connection to db
 * @author Alexandre Becquereau
 */

public class ConnectionParms {

    public static boolean DEBUG = true;

    //Login for connection
    private String login;
    
    //Password for connection
    private char[] password;
    
    /** The http parameters for the http session */
    private HttpProxy httpProxy = null;
     
    private String double2faCode = null;
    
    /**
     * Constructor
     * @param theLogin    login to use
     * @param thePassword password to use
     * @param httpProxy the http Proxyfor the http session
     */
    public ConnectionParms(String theLogin, char[] thePassword, HttpProxy httpProxy, String double2faCode) {
                        
        this.login = theLogin;
        this.password = thePassword;
        this.httpProxy = httpProxy; 
        
        //Awake Encryption engines requires all values to be set
        if (double2faCode == null) {
            double2faCode = "000000";
        }
        
        this.double2faCode = double2faCode;
    }
        
    /**
     * Get an Awake connection
     *
     * @return the Awake Connection
     *
     * @throws MalformedURLException    if the url is null or malformed        
     * @throws IllegalArgumentException login or password is null
     * @throws InvalidLoginException    the login is refused by the remote host   
     * @throws ConnectException         The Host is correct but the Servlet (http://www.acme.org/Servlet) 
     *      
     * @throws UnknownHostException     Host url (http://www.acme.org) does not exists or no Internet Connection.  
     * @throws IOException              For all other IO / Network / System Error
     * 
     * @throws NoSuchProviderException  hash exception
     * @throws NoSuchAlgorithmException hash exception 
     * @throws SQLException             remote SQL Exceptipn
     */
    public Connection getConnection()
        throws NoSuchAlgorithmException, 
               NoSuchProviderException,
               ConnectException,
               IllegalArgumentException, 
               UnknownHostException,
               InvalidLoginException,
               SecurityException,
               IOException,
               SQLException
    {

        String url = ServerParms.getAwakeSqlServerUrl();
        String connectionPassword =  PassphraseUtil.computeHashAndSaltedPassphrase(login, password);
        //debug("connectionPassword: " + connectionPassword);
        
        // Try to establish a connection
        HttpProtocolParameters httpProtocolParameters = Register.getHttpProtocolParameters();
        
        Connection connection = null;
        
        try
        {
            AwakeFileSession awakeFileSession = new AwakeFileSession(url, login, connectionPassword.toCharArray(), httpProxy, httpProtocolParameters, double2faCode);
            connection = new AwakeConnection(awakeFileSession); // Now a simple Wrapper.
        }
        catch (Exception e)
        {
            AwakeSqlExceptionDecoder.decodeAndRethrow(e);
        }
               
        return connection;
    }
        
}
