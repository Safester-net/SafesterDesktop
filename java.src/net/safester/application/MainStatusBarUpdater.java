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
package net.safester.application;

import java.awt.Color;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.parms.StoreParms;
import net.safester.application.parms.SubscriptionLocalStore;
import net.safester.clientserver.MessageTransfer;

/**
 *  @author Nicolas de Pomereu
 *  
 *  Class to use to update the two fields in bottom status bar:
 *  <ul>
 *  <li>Remaining storage info.
 *  <li>Last login info.
 *  </ul>
 *   
 */

public class MainStatusBarUpdater {

    public static boolean DEBUG = false;

    public static final String CR_LF = System.getProperty("line.separator") ;

    /** If true, operations are done for the first time */
    private static boolean FIRST_ACCESS = true;

    /** The text containign the last login info. Static, as we want one set during session */
    private static String textLastLoginAgo = null;

    private MessagesManager messages = new MessagesManager();
    
    /** The Color to use for storage info */
    private Color storageInfoColor = Color.black;
    
    /** The awake Connection */
    private Connection connection = null;

    /** The username */
    private String keyId = null;

    /** The user number */
    private int userNumber = 0;
    
    /** The Awake Authentication Token */
    private String authenticationToken =null;

    /** The host */
    private String host = null;

    
    /**
     * Constructor.
     *  
     * @param theConnection the Awake JDBC Connection
     * @param keyId         the Safester login 
     * @param userNumber    the user number
     */
    public MainStatusBarUpdater(Connection theConnection, String keyId, int userNumber)
    {
        if (theConnection == null) {
            throw new IllegalArgumentException("theConnection can\'t be null");           
        }
        
        if (keyId == null) {
            throw new IllegalArgumentException("username can\'t be null");           
        }
        
        // Use a dedicated Connection to avoid overlap of result files
        this.connection = ((AwakeConnection)theConnection).clone();
        
        this.keyId = keyId;
        this.userNumber = userNumber;
        
        AwakeConnection awakeConnection = (AwakeConnection)this.connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
        authenticationToken = awakeFileSession.getAuthenticationToken();
                
        host = awakeFileSession.getUrl();
        
        host = StringUtils.substringBeforeLast(host, "/"); 
        if (host.contains("safester_vault")) {
            host = StringUtils.substringBeforeLast(host, "/");             
        }
         
        debug("MainStatusBarUpdater.host: " + host);
    }

    /**
     * @return the plan in formatted string
     */
    public static String getAccount()
    {
        MessagesManager messages = new MessagesManager();
        short subscription = SubscriptionLocalStore.getSubscription();
        String account = StoreParms.getProductNameForSubscription(subscription) + " " + messages.getMessage("account");                
        return account;
    }
    
           
    
    /**
     * Gets the formatted storage info for the user
     * @return the formatted storage info for the user
     * @throws SQL
     */
    public String getStorageInfo() throws SQLException
    {
        long actualStore = MessageTransfer.getTotalMailboxSize(connection, userNumber);
        long maxStore = StoreParms.getStorageForSubscription(SubscriptionLocalStore.getSubscription());

        actualStore = actualStore / Parms.MO;
        maxStore = maxStore / Parms.MO;
        
        if( maxStore == 0)
        {
            return "";
        }
        else
        {
            Long percent = actualStore * 100 / maxStore;

            if (percent > 90)
            {
                storageInfoColor = Color.red;
            }
            else if (percent > 60)
            {
                storageInfoColor = Color.orange;
            }
            else
            {
                storageInfoColor = Color.black;
            }
            String message = messages.getMessage("using_storage");
            message = MessageFormat.format(message, actualStore, maxStore, percent);

            return message;
        }

    }
    
    /**
     * @return the storageInfoColor
     */
    public Color getStorageInfoColor()
    {
        return this.storageInfoColor;
    }


    /**
     * 
     * @return the formatted last login info in Last login: {0} {1} ago format
     * 
     * @throws SQLException
     */
    public String getLastLoginAgo() throws SQLException
    {
        
        if (textLastLoginAgo != null)
        {
            return textLastLoginAgo;
        }
        
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();


        String jsonString = null;
        try {
            jsonString = awakeFileSession.call("net.safester.server.hosts.newapi.LoginLogNewApi.getLastLoginTimestamp",
                    userNumber,
                    connection);
        } catch (Exception e) {
            throw new SQLException(e);
        }
                
        long time = Long.parseLong(jsonString);
        Timestamp dateTime = new Timestamp(time);

        textLastLoginAgo = getLastLoginText(now, dateTime, messages);

        debug("text: " + textLastLoginAgo);
        return textLastLoginAgo;
    }
    

    /**
     * Get the last login textLastLoginAgo using the passed timestamp
     * @param dateTime      the timpestamp of the last login textLastLoginAgo
     * @param messages      the messages
     * 
     * @return  the last login textLastLoginAgo in form:
     *           Last login: {0} {1} ago
     */
    public static String getLastLoginText(Timestamp now, Timestamp dateTime, MessagesManager messages)
    {        
        debug("");
        debug("dateTime: " + dateTime);        
        debug("now     : " + now);    
        debug("dateTime: " + dateTime.getTime());        
        debug("now     : " + now.getTime());
        
        // Compute the delay in seconds
        long lastLoginDelay =  now.getTime() - dateTime.getTime();

        if (lastLoginDelay == 0)
        {
            return ""; // No display message at init
        }

        lastLoginDelay = lastLoginDelay / 1000;

        debug("");
        debug("lastLoginDelay: " + lastLoginDelay);
        
        long delay = 0;
        String unit = null;

        debug("lastLoginDelay: " + lastLoginDelay);

        if (lastLoginDelay < 60)
        {
            unit = messages.getMessage("seconds");
            delay = lastLoginDelay;
        }
        else if(lastLoginDelay < 3600)
        {
            unit = messages.getMessage("minutes");
            delay = lastLoginDelay / 60;
        }
        else if (lastLoginDelay < 3600 * 24)
        {
            unit = messages.getMessage("hours");
            delay = lastLoginDelay / (60 * 60);
        }
        else
        {
            unit = messages.getMessage("days");
            delay = lastLoginDelay / (60 * 60 * 24);
        }

        String unitText = messages.getMessage(unit);

        //last_login=Last login: {0} {1} ago
        String message = messages.getMessage("last_login_ago");
        message = MessageFormat.format(message, delay, unitText);
        return message;
    }

    /**
     * Call the LoginLogUpdate servlet to log this login event
     *
     * @return  the httpClient return code
     *
     * @throws SQLException
     * @throws IOException
     */
    public int logThisLogin() throws SQLException, IOException
    {
        if (! FIRST_ACCESS )
        {
            return 0;
        }
        
        FIRST_ACCESS = false;
        
        DefaultHttpClient httpClient = null;

        try
        {
            httpClient = new DefaultHttpClient();
            String ServletAddress = null;
            String servletName = "LoginLogUpdate";
            
            ServletAddress = host + "/" + servletName;
            //debug(ServletAddress);

            HttpPost httpPost = new HttpPost(ServletAddress);
            httpPost.setHeader("User-Agent", "Safester " + net.safester.application.version.Version.getVersionWithDate() + " " + SystemUtils.OS_NAME + " " +  SystemUtils.OS_VERSION );
            
            List<BasicNameValuePair> requestParams = new Vector<BasicNameValuePair>();
            requestParams.add(new BasicNameValuePair("username", keyId));
            requestParams.add(new BasicNameValuePair("authentication_token", authenticationToken));

            httpPost.setEntity(new UrlEncodedFormEntity(requestParams, HTTP.UTF_8));

            HttpResponse response  = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            return statusCode;
        }
        finally
        {
            if (httpClient != null)
            {
                httpClient.getConnectionManager().shutdown();
            }
        }
    }
    
    public static void main(String[] args)  throws Exception
    {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp dateTime = new Timestamp(new Date(111, 6, 19).getTime());
        MessagesManager messages = new MessagesManager();
        String theLastLogin = getLastLoginText(dateTime, now, messages);
        
        System.out.println(theLastLogin);
    }
    
    /**
     * debug tool
     */
    private static void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
    }


    
}
