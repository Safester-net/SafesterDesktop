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
package net.safester.clientserver.holder;

import java.sql.Connection;
import java.sql.SQLException;

import org.awakefw.sql.api.client.AwakeConnection;

import com.kawansoft.httpclient.KawanHttpClient;

import net.safester.application.http.ApiKeys;
import net.safester.application.http.KawanHttpClientBuilder;
import net.safester.clientserver.UserNumberGetterClient;

public class PrivateKeyGetter {

    public static final boolean DEBUG = false;

    private Connection connection;

    /**
     * Constructor.
     * 
     * @param connection
     * @param userNumber the user Number to get the public key for
     */
    public PrivateKeyGetter(Connection connection) {

	if (connection == null) {
	    throw new NullPointerException("connection is null!");
	}

	this.connection = connection;

    }

    /**
     * Returns the PGP private key asc block for the user number
     * 
     * @return
     * @throws SQLException
     */
    public String getPrivateKey(int userNumber) throws SQLException {
	String userEmailAddr = new UserNumberGetterClient(connection).getLoginFromUserNumber(userNumber);

	AwakeConnection awakeConnection = (AwakeConnection) connection;
	KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.buildFromAwakeConnection(connection);

	ApiKeys apiKeys = new ApiKeys(kawanHttpClient, awakeConnection.getUsername(),
		awakeConnection.getAuthenticationToken());
	String privateKeyBlock = null;

	try {
	    privateKeyBlock = apiKeys.getPrivateKey(userEmailAddr);
	} catch (Exception e) {
	    throw new SQLException(e);
	}

	return privateKeyBlock;

    }

//    /**
//     * Returns the PGP private key asc block for the email address.
//     * 
//     * @param userEmailAddr
//     * @return
//     * @throws SQLException
//     */
//    public String getPrivateKey(String userEmailAddr) throws SQLException {
//	String privateKeyBlock = null;
//
//	String url = ServerParms.getHOST();
//	if (!url.endsWith("/")) {
//	    url += "/";
//	}
//
//	AwakeConnection awakeConnection = (AwakeConnection) connection;
//	AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
//	KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.build(awakeConnection);
//
//	try {
//	    String username = awakeFileSession.getUsername();
//
//	    Map<String, String> parametersMap = new HashMap<>();
//
//	    parametersMap.put("username", username);
//	    parametersMap.put("token", awakeConnection.getAwakeFileSession().getAuthenticationToken());
//	    parametersMap.put("userEmailAddr", userEmailAddr);
//	    debug("username     :" + username);
//	    debug("token        :" + awakeConnection.getAwakeFileSession().getAuthenticationToken());
//	    debug("userEmailAddr:" + userEmailAddr);
//
//	    String urlGetPrivateKey = url + "api/getPrivateKey";
//	    debug("url get private key:" + urlGetPrivateKey);
//
//	    String privateKeyBlockJson = kawanHttpClient.callWithPost(new URL(urlGetPrivateKey), parametersMap);
//	    PrivKeyDTO pubKeyDTO = GsonWsUtil.fromJson(privateKeyBlockJson, PrivKeyDTO.class);
//	    privateKeyBlock = pubKeyDTO.getPrivateKey();
//
//	    debug("privateKeyBlock: " + privateKeyBlock);
//	    return privateKeyBlock;
//	} catch (Exception e) {
//	    throw new SQLException(e);
//	}
//    }

    public static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new java.util.Date() + " " + s);
	}
    }

}
