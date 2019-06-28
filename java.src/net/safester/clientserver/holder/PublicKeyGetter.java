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

import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.awakefw.commons.api.client.HttpProxy;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.kawansoft.httpclient.KawanHttpClient;

import net.safester.clientserver.ServerParms;
import net.safester.clientserver.UserNumberGetterClient;
import net.safester.clientserver.serverapi.GsonWsUtil;
import net.safester.clientserver.serverapi.PubKeyDTO;

public class PublicKeyGetter {

    public static final boolean DEBUG = true;
    
    private Connection connection;
    
    /**
     * Constructor. 
     * @param connection
     * @param userNumber	the user Number to get the public key for
     */
    public PublicKeyGetter(Connection connection) {
	
	if (connection == null) {
	    throw new NullPointerException("connection is null!");
	}
	
	this.connection = connection;
    }

    /**
     * Returns the PGP public key asc block for the passed number.
     * @return
     * @throws SQLException
     */
    public String getPublicKey(int userNumber) throws SQLException  {

	String userEmailAddr = new UserNumberGetterClient(connection).getLoginFromUserNumber(userNumber);
	return getPublicKey(userEmailAddr);
    }

    /**
     * Returns the PGP public key asc block for the email address.
     * @param userEmailAddr
     * @return
     * @throws SQLException
     */
    public String getPublicKey(String userEmailAddr) throws SQLException {
	String publicKeyBlock = null;

	String url = ServerParms.getHOST();
	if (!url.endsWith("/")) {
	    url += "/";
	}

	AwakeConnection awakeConnection = (AwakeConnection) connection;
	AwakeFileSession awakeFileSession =  awakeConnection.getAwakeFileSession();
	KawanHttpClient kawanHttpClient = buildKawanHttpClient(awakeConnection);

	try {
            String username = awakeFileSession.getUsername();
	    Map<String, String> parametersMap = new HashMap<>();

	    parametersMap.put("username", username);
	    parametersMap.put("token", awakeConnection.getAwakeFileSession().getAuthenticationToken());
	    parametersMap.put("userEmailAddr", userEmailAddr);
	    
	    debug("username     :" + username);
	    debug("token        :" + awakeConnection.getAwakeFileSession().getAuthenticationToken());
	    debug("userEmailAddr:" + userEmailAddr);
	    
	    String urlGetPublicKey = url + "api/getPublicKey";
	    debug("url get public key:" + urlGetPublicKey);

	    String publicKeyBlockJson = kawanHttpClient.callWithPost(new URL(urlGetPublicKey), parametersMap);
	    PubKeyDTO pubKeyDTO = GsonWsUtil.fromJson(publicKeyBlockJson, PubKeyDTO.class);
	    publicKeyBlock = pubKeyDTO.getPublicKey();
	    
	    debug("publicKeyBlock: " + publicKeyBlock);
	    return publicKeyBlock;
	} catch (Exception e) {
	    throw new SQLException(e);
	}
    }
    
    public static KawanHttpClient buildKawanHttpClient(AwakeConnection awakeConnection) {
	KawanHttpClient kawanHttpClient;
	AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
	HttpProxy httpProxy = awakeFileSession.getHttpProxy();
	if (httpProxy == null) {
	    kawanHttpClient = new KawanHttpClient();
	} else {
	    String proxyHostname = httpProxy.getAddress();
	    int proxyPort = httpProxy.getPort();
	    String proxyUserName = httpProxy.getUsername();
	    String proxyPassword = httpProxy.getPassword();

	    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHostname, proxyPort));

	    if (proxyUserName == null) {
		kawanHttpClient = new KawanHttpClient(proxy, null);
	    }
	    else {
		kawanHttpClient = new KawanHttpClient(proxy, new PasswordAuthentication(proxyUserName, proxyPassword.toCharArray() ));
	    }
	}
	return kawanHttpClient;
    }
    
    private static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new java.util.Date() + " " + s);
	}
    }
    
}
