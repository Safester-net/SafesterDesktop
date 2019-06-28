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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.awakefw.commons.api.client.HttpProtocolParameters;
import org.awakefw.commons.api.client.HttpProxy;
import org.awakefw.commons.api.client.InvalidLoginException;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.safester.application.register.Register;
import net.safester.application.util.crypto.PassphraseUtil;
import net.safester.clientserver.ServerParms;
import net.safester.clientserver.SubscriptionLocal;
import net.safester.clientserver.UserNumberGetterClient;

/**
 * This class provides Connection to db
 * @author Alexandre Becquereau
 */

public class ConnectionParms {

    public static boolean DEBUG = true;
    private static boolean isExpired = false;

    public static boolean isExpired() {
        return isExpired;
    }
    
    //User number associated to login
    private int userNumber = -1;

    //Login for connection
    private String login;
    
    //Password for connection
    private char[] password;
    
    /** The http parameters for the http session */
    private HttpProxy httpProxy = null;

    /** User active subscription */
    private static short userSubscription = StoreParms.PRODUCT_FREE;

    /** The subscription as it existed before expiration */
    private static short userExpiredSubscription = StoreParms.PRODUCT_FREE;
    
    /** Store the timestamp of end of eval */
    private static Timestamp enddate = null;    
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
        
        debug("connectionPassword: " + connectionPassword);
        
        // Try to establish a connection
        HttpProtocolParameters httpProtocolParameters = Register.getHttpProtocolParameters();
        
        Connection connection = null;

        //JOptionPane.showMessageDialog(null, "password: " + new String(httpProtocolParameters.getEncryptionPasswordForHttpParameters()));
        
        try
        {
            AwakeFileSession awakeFileSession = new AwakeFileSession(url, login, connectionPassword.toCharArray(), httpProxy, httpProtocolParameters, double2faCode);
            connection = new AwakeConnection(awakeFileSession); // Now a simple Wrapper.
        }
        catch (Exception e)
        {
            AwakeSqlExceptionDecoder.decodeAndRethrow(e);
        }
       
        //Retrieve subscription
        getUserSubscription(connection);
        return connection;
    }
    
    /**
     * Retreive current user subscription from server
     * @param connection    Connection to db
     *
     * @throws SQLException
     */
    private void getUserSubscription(Connection connection) throws SQLException, InvalidLoginException
    {       
	/*
//        String sql = "select user_login.user_number, voucher.type_subscription, subscription.enddate, voucher.label "
//        		+ " from user_login, subscription, voucher "
//                        + " where subscription.user_number in (select user_login.user_number from user_login where login = ?)" 
//                        + " and subscription.user_number = user_login.user_number " 
//                        + " and active = ? "
//                        + " and subscription.voucher_code = voucher.voucher_code";
        
        PreparedStatementRunner preparedStatementRunner = new PreparedStatementRunner(
                connection, sql, this.login, true);
        ResultSet rs = preparedStatementRunner.executeQuery();
        
        if (rs.next())
        {
            int i = 1;
            this.userNumber = rs.getInt(i++);
            int typeSubscription = rs.getInt(i++);
            enddate = rs.getTimestamp(i++);
            
            Timestamp now = new Timestamp(System.currentTimeMillis());

            debug("userNumber       : " + userNumber);
            debug("typeSubscription : " + typeSubscription);
            debug("endOfSubscription: " + enddate);
            debug("now              : " + now);

            if (enddate.before(now) 
                    || isForceExpire()) //  boolean ini filefor tests
            {
                // Subscription is expired ==> Roll back userSubscription to
                // StoreParms.PRODUCT_FREE
                ConnectionParms.userExpiredSubscription = (short)typeSubscription;
                ConnectionParms.userSubscription = StoreParms.PRODUCT_FREE;
                ConnectionParms.isExpired = true; 
                debug("expired!");
                debug("userSubscription              : " + userSubscription);
            }      
            else
            {
                ConnectionParms.userSubscription = (short)typeSubscription;
                ConnectionParms.isExpired = false; 
            }
        }
        else {
            // Should not happen, just to be cautious to get a user number if no subscription
            UserNumberGetterClient userNumberGetter = new UserNumberGetterClient(connection);
            userNumber = userNumberGetter.getUserNumberFromLogin(login);

            if (userNumber == -1)
            {
                throw new InvalidLoginException();
            }
            
            System.err.println("WARNING: No Subscription/voucher for user");
            //throw new SQLException("No subscription found for login: " + this.login);
        }
        */
	

	AwakeConnection awakeConnection = (AwakeConnection) connection;
	AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

	String methodRemote = "net.safester.server.hosts.newapi.UserSubscriptionInfo.getUserSubscription";
	debug("methodRemote: " + methodRemote);

	String jsonString = null;
	try {
	    jsonString = awakeFileSession.call(methodRemote, this.login, connection);

	    Gson gsonOut = new Gson();
	    Type type = new TypeToken<SubscriptionLocal>() {
	    }.getType();

	    SubscriptionLocal subscriptionLocal = gsonOut.fromJson(jsonString, type);

	    if (subscriptionLocal.getUserNumber() >= 0) {
		this.userNumber = subscriptionLocal.getUserNumber();
		int typeSubscription = subscriptionLocal.getTypeSubscription();
		enddate = subscriptionLocal.getEnddate();

		Timestamp now = new Timestamp(System.currentTimeMillis());

		debug("userNumber       : " + userNumber);
		debug("typeSubscription : " + typeSubscription);
		debug("endOfSubscription: " + enddate);
		debug("now              : " + now);

		if (enddate.before(now) || isForceExpire()) // boolean ini filefor tests
		{
		    // Subscription is expired ==> Roll back userSubscription to
		    // StoreParms.PRODUCT_FREE
		    ConnectionParms.userExpiredSubscription = (short) typeSubscription;
		    ConnectionParms.userSubscription = StoreParms.PRODUCT_FREE;
		    ConnectionParms.isExpired = true;
		    debug("expired!");
		    debug("userSubscription              : " + userSubscription);
		} else {
		    ConnectionParms.userSubscription = (short) typeSubscription;
		    ConnectionParms.isExpired = false;
		}
	    } else {
		// Should not happen, just to be cautious to get a user number if no
		// subscription
		UserNumberGetterClient userNumberGetter = new UserNumberGetterClient(connection);
		userNumber = userNumberGetter.getUserNumberFromLogin(login);

		if (userNumber == -1) {
		    throw new InvalidLoginException();
		}

		System.err.println("WARNING: No Subscription/voucher for user");
		// throw new SQLException("No subscription found for login: " + this.login);
	    }
            
        }
        catch (Exception e)
        {
            throw new SQLException(e);
        }
        
        
    }

    /**
     * Allow to force expire by creating a user.home/safester_force_expire.txt file.
     * @return true if we want to force expire
     */
    private boolean isForceExpire() {

        String userHome = System.getProperty("user.home");
        File file = new File(userHome + File.separator + ".safester" + File.separator + "safester_force_expire.txt");

        return file.exists();
    }
    

    public int getUserNumber(){
        return userNumber;
    }

    /**
     *
     * @return the actual subscription 
     */
    public static short getSubscription(){
        return userSubscription;
    }

    /**
     *
     * @return the subscription before it expired
     */
    public static short getExpiredSubscription(){
        return userExpiredSubscription;
    }
   
    
    /**
     * @return the enddate
     */
    public static Timestamp getEnddate() {
        return enddate;
    }
    
    /**
     * Gets the number of day before eval subscription expiration 
     * @return the days remaining for the eval
     */
    public static long getDaysRemaining()
    {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        long millisecondsRemaining = enddate.getTime() - now.getTime();
        long secondsRemaining = millisecondsRemaining / 1000;
        long daysRemaining = secondsRemaining / 3600 / 24;
        return daysRemaining + 1;
    }
    
    public static void setSubscription(short subscription) {        
        userSubscription = subscription;
    }

    /**
     * debug tool
     */
    private void debug(String s)
    {
        if (DEBUG)        {
            System.out.println(s);
        }
    }


}
