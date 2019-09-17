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

import org.awakefw.commons.api.client.HttpProxy;
import org.awakefw.commons.api.client.Invalid2faCodeException;
import org.awakefw.commons.api.client.InvalidLoginException;
import org.awakefw.commons.api.client.RemoteException;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.kawansoft.httpclient.KawanHttpClient;

import net.safester.application.http.ApiLogin;
import net.safester.application.http.KawanHttpClientBuilder;
import net.safester.clientserver.ServerParms;
import net.safester.clientserver.SubscriptionLocal;

/**
 * This class provides Connection to db
 *
 * @author Alexandre Becquereau
 */
public class ConnectionParms {

    public static boolean DEBUG = true;

    //Login for connection
    private String login;

    //Password for connection
    private char[] password;

    /**
     * The http parameters for the http session
     */
    private HttpProxy httpProxy = null;

    private String double2faCode = null;
    private SubscriptionLocal subscriptionLocal = null;

    /**
     * Constructor
     *
     * @param theLogin login to use
     * @param thePassword password to use
     * @param httpProxy the http Proxyfor the http session
     * @param double2faCode
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
     * @throws MalformedURLException if the url is null or malformed
     * @throws IllegalArgumentException login or password is null
     * @throws InvalidLoginException the login is refused by the remote host
     * @throws ConnectException The Host is correct but the Servlet
     * (http://www.acme.org/Servlet)
     *
     * @throws UnknownHostException Host url (http://www.acme.org) does not
     * exists or no Internet Connection.
     * @throws IOException For all other IO / Network / System Error
     *
     * @throws NoSuchProviderException hash exception
     * @throws NoSuchAlgorithmException hash exception
     * @throws SQLException remote SQL Exceptipn
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
            SQLException {

        Connection connection = null;

        try {
            KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.build(httpProxy);
            ApiLogin apiLogin = new ApiLogin(kawanHttpClient);

            boolean logged = apiLogin.login(login, password, double2faCode);

            if (logged) {
                
                subscriptionLocal = new SubscriptionLocal();
                subscriptionLocal.setUserNumber(apiLogin.getUserNumber());
                subscriptionLocal.setTypeSubscription(apiLogin.getProduct());
                subscriptionLocal.setEnddate(apiLogin.getEndDate());

                AwakeFileSession awakeFileSession = new AwakeFileSession(ServerParms.getAwakeSqlServerUrl(), login, (String) apiLogin.getToken(), httpProxy, null); // Now a simple Wrapper.
                connection = new AwakeConnection(awakeFileSession); // Now a simple Wrapper.
                return connection;
            }

            // Decode Exception from Json
            String exceptionName = apiLogin.getExceptionName();
            String errorMessage = apiLogin.getErrorMessage();

            if (exceptionName.equals(Invalid2faCodeException.class.getName())) {
                throw new Invalid2faCodeException(errorMessage);
            } else if (exceptionName.equals(InvalidLoginException.class.getName())) {
                throw new InvalidLoginException(errorMessage);
            } else {
                throw new RemoteException(errorMessage, null, exceptionName);
            }
        } catch (Exception e) {
            AwakeSqlExceptionDecoder.decodeAndRethrow(e);
        }

        return connection;
    }

    public SubscriptionLocal getSubscriptionLocal() {
        return subscriptionLocal;
    }
}
