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
package net.safester.clientserver;

import java.sql.Connection;
import java.sql.SQLException;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.awakefw.sql.api.client.AwakeConnection;

import net.safester.clientserver.specs.UniqueExtractor;
import net.safester.noobs.clientserver.GsonUtil;
import net.safester.noobs.clientserver.UserSettingsLocal;

/**
 * @author Nicolas de Pomereu
 *
 */
public class UserSettingsExtractor implements UniqueExtractor<UserSettingsLocal> {

    /** The Jdbc connection */
    private Connection connection = null;
    /** The user number */
    private int userNumber = 0;

    /**
     * Constructor
     * 
     * @param userNumber the user number to get the message from
     * @param connection the JDBC connection
     */
    public UserSettingsExtractor(Connection connection, int userNumber) {
	if (connection == null) {
	    throw new IllegalArgumentException("Connection can\'t be null");
	}

	this.connection = connection;
	this.userNumber = userNumber;
    }

    /**
     * @return a unique User Settingds Local from the SQL Server
     * @throws SQLException if any SQL Exception is raised
     */
    @Override
    public UserSettingsLocal get() throws SQLException {

//        UserSettings userSettings = new UserSettings();
//        userSettings.setUserNumber(userNumber);
//        if (!userSettings.read(connection)) {
//            return null;
//        }
//
//        UserSettingsLocal userSettingsLocal = new UserSettingsLocal();
//        userSettingsLocal.setNotificationEmail(userSettings.getNotificationEmail());
//        userSettingsLocal.setReceiveInfos(userSettings.getReceiveInfos());
//        userSettingsLocal.setStealthMode(userSettings.getStealthMode());
//
//        String userName = userSettings.getUserName();
//        userName = HtmlConverter.fromHtml(userName);
//        userSettingsLocal.setUserName(userName);
//        
//        userSettingsLocal.setNotificationOn(userSettings.getNotificationOn());
//        userSettingsLocal.setSend_anonymous_notification_on(userSettings.getSendAnonymousNotificationOn());
//        
//        String signature = userSettings.getSignature();
//        if(signature != null && !signature.equalsIgnoreCase("null"))
//        {        
//            signature = HtmlConverter.fromHtml(signature);
//            userSettingsLocal.setSignature(signature);
//        }
//        
//        userSettingsLocal.setUseOtp(userSettings.getUseOtp());
//        return userSettingsLocal;

	AwakeConnection awakeConnection = (AwakeConnection) connection;
	AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

	String methodRemote = "net.safester.server.hosts.newapi.UserSettingsNewApi.get";
	// debug("methodRemote: " + methodRemote);

	String jsonString = null;
	try {
	    String emailAddress = awakeFileSession.getUsername();
	    jsonString = awakeFileSession.call(methodRemote, userNumber, emailAddress, connection);
	    UserSettingsLocal userSettingsLocal = GsonUtil.userSettingsLocalfromGson(jsonString);
	    String userName = userSettingsLocal.getUserName();
	    userName = HtmlConverter.fromHtml(userName);
	    userSettingsLocal.setUserName(userName);

	    String signature = userSettingsLocal.getSignature();
	    if (signature != null && !signature.equalsIgnoreCase("null")) {
		signature = HtmlConverter.fromHtml(signature);
		userSettingsLocal.setSignature(signature);
	    }

	    return userSettingsLocal;

	} catch (Exception e) {
	    throw new SQLException(e);
	}

    }

    /**
     * Updates user_settings on the server
     * @param userSettingsLocal
     * @throws SQLException
     */
    public void update(UserSettingsLocal userSettingsLocal) throws SQLException {
	
	/*
	UserSettings userSettings = new UserSettings();

	userSettings.setUserNumber(userSettingsLocal.getUserNumber());

	String userName = userSettingsLocal.getUserName();
	userName = HtmlConverter.toHtml(userName);
	userSettings.setUserName(userName);

	userSettings.setNotificationEmail(userSettingsLocal.getNotificationEmail());
	userSettings.setReceiveInfos(userSettingsLocal.getReceiveInfos());
	userSettings.setStealthMode(userSettingsLocal.getStealthMode());

	// userSettings.setSignature(userSettingsLocal.getSignature());
	String signature = userSettingsLocal.getSignature();
	signature = HtmlConverter.toHtml(signature);
	userSettings.setSignature(signature);

	userSettings.setNotificationOn(userSettingsLocal.isNotificationOn());
	userSettings.setSendAnonymousNotificationOn(userSettingsLocal.getSend_anonymous_notification_on());
	userSettings.setUseOtp(userSettingsLocal.isUseOtp());

	userSettings.update(connection);
	*/

	String userName = userSettingsLocal.getUserName();
	userName = HtmlConverter.toHtml(userName);
	userSettingsLocal.setUserName(userName);
	
	String signature = userSettingsLocal.getSignature();
	signature = HtmlConverter.toHtml(signature);
	userSettingsLocal.setSignature(signature);
	
	AwakeConnection awakeConnection = (AwakeConnection) connection;
	AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

	String methodRemote = "net.safester.server.hosts.newapi.UserSettingsNewApi.put";
	// debug("methodRemote: " + methodRemote);

	try {
	    String emailAddress = awakeFileSession.getUsername();
	    String jsonString = GsonUtil.toGson(userSettingsLocal);
	    awakeFileSession.call(methodRemote, userNumber, emailAddress, jsonString, connection);
	} catch (Exception e) {
	    throw new SQLException(e);
	}
	
    }
}
