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
package net.safester.application.util.proxy;

import java.awt.Cursor;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.util.Date;

import javax.swing.JFrame;

import org.awakefw.commons.api.client.HttpProtocolParameters;
import org.awakefw.commons.api.client.HttpProxy;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.client.AwakeUrl;

import com.kawansoft.httpclient.KawanHttpClient;

import net.safester.application.DialogProxyAuth;
import net.safester.application.SystemInit;
import net.safester.application.messages.MessagesManager;
import net.safester.application.updater.InstallParameters;
import net.safester.application.updater.RemoteSoftwareInfo;
import net.safester.application.updater.UpdateDownloader;
import net.safester.application.util.UserPrefManager;
import net.safester.application.wait.tools.CmWaitDialog;
import net.safester.clientserver.ServerParms;

/**
 * @author Nicolas de Pomereu
 * 
 *         Allow to test a Http Session through the found proxy. <br>
 *         If the proxy requires authentication, a pop-up window will display to
 *         ask for username & password. <br>
 *         <br>
 *         check() will return true if an URL maybe download from the server.
 *         <br>
 *         <br>
 *         if check() returns false: you may get the Exception with getException
 * 
 */
public class ProxySessionCheckerNew {
    public static boolean DEBUG = false;

    /** The parent Window */
    private JFrame parent = null;

    /** The http parameters for the http session */
    private HttpProxy httpProxy = null;

    /** The awake file session instance */
    // AwakeFileSession awakeSession = null;
    AwakeUrl awakeUrl = null;

    /** The wait dialog */
    CmWaitDialog cmWaitDialog = null;

    private String email = null;
    private char[] passphrase = null;

    /** To get the translated messages */
    MessagesManager messages = new MessagesManager();

    /**
     * Constructor
     * 
     * @param parant    the parent JFrame
     * @param httpProxy the http parameters for the http session
     */
    public ProxySessionCheckerNew(JFrame parent, CmWaitDialog cmWaitDialog) {
	this.parent = parent;
	this.httpProxy = ProxyUtil.setProxyFromUserPreference();
	this.cmWaitDialog = cmWaitDialog;
    }

    /**
     * Constructor
     * 
     * @param parant     the parent JFrame
     * @param httpProxy  the http parameters for the http session
     * @param email      the email to reset on the login screen
     * @param passphrase the passphrase to reset on the login screen
     */
    public ProxySessionCheckerNew(JFrame parent, CmWaitDialog cmWaitDialog, String email, char[] passphrase) {
	this.parent = parent;
	this.httpProxy = ProxyUtil.setProxyFromUserPreference();
	this.cmWaitDialog = cmWaitDialog;

	this.email = email;
	this.passphrase = passphrase;
    }

    /**
     * @return the httpProxy
     */
    public HttpProxy getHttpProxy() {
	return httpProxy;
    }

    /**
     * Check if a http session may be established (by downloading an URL). <br>
     * if proxy requires authentication, username/password will be asked in loop.
     * 
     * @return true if the http session - with possible proxy config - allows to
     *         download file
     */
    public boolean check() throws Exception {
	int statusCode = -1;

	// Infinite loop because there is maybe a proxy authentication
	while (true) {

	    Proxy proxy = null;
	    PasswordAuthentication passwordAuthentication = null;

	    debug("httpProxy: " + httpProxy);
	    if (httpProxy != null) {
		String proxyHostname = httpProxy.getAddress();
		int proxyPort = httpProxy.getPort();
		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHostname, proxyPort));

		if (httpProxy.getUsername() != null) {
		    passwordAuthentication = new PasswordAuthentication(httpProxy.getUsername(),
			    httpProxy.getPassword().toCharArray());

		    final String proxyAuthUsername = passwordAuthentication.getUserName();
		    final char[] proxyPassword = passwordAuthentication.getPassword();

		    Authenticator authenticator = new Authenticator() {

			@Override
			public PasswordAuthentication getPasswordAuthentication() {
			    return new PasswordAuthentication(proxyAuthUsername, proxyPassword);
			}
		    };

		    if (DEBUG) {
			System.out.println(
				"passwordAuthentication: " + proxyAuthUsername + " " + new String(proxyPassword));
		    }

		    Authenticator.setDefault(authenticator);
		}
	    }

	    KawanHttpClient kawanHttpClient = new KawanHttpClient(proxy, passwordAuthentication);
	    @SuppressWarnings("unused")
	    String result = kawanHttpClient.callWithGet("http://www.runsafester.net");
	    statusCode = kawanHttpClient.getHttpStatusCode();
	    debug("statusCode returned: " + statusCode);
		
	    if (statusCode == 200) {
		return true;
	    }

	    if (statusCode == 407) {
		cmWaitDialog.stopWaiting();
		parent.setCursor(Cursor.getDefaultCursor());

		debug("status: 407!");
		// Proxy requires authentification ask for it
		DialogProxyAuth dialogProxyAuth = new DialogProxyAuth(parent);
		// try {Thread.sleep(200);} catch (Exception e) {}
		dialogProxyAuth.setVisible(true);

		parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		cmWaitDialog.startWaiting();

		if (dialogProxyAuth.isCancelled()) {
		    // User cancel operation => stop
		    return false;
		}

		debug("Proxy Window Auth closed...");

		// Ok! We are now authenticated
		// Add authentication username & password to
		// HttpNetworkParameters

		String address = httpProxy.getAddress();
		int port = httpProxy.getPort();

		boolean ntlmProxy = UserPrefManager.getBooleanPreference(UserPrefManager.PROXY_AUTH_NTLM);

		if (ntlmProxy) {
		    String workstation = UserPrefManager.getPreference(UserPrefManager.NTLM_WORKSTATION);
		    String domain = UserPrefManager.getPreference(UserPrefManager.NTLM_DOMAIN);

		    if (workstation == null) {
			workstation = "";
		    }
		    if (domain == null) {
			domain = "";
		    }

		    httpProxy = new HttpProxy(address, port, dialogProxyAuth.getProxyUsername(),
			    dialogProxyAuth.getProxyPassword(), workstation, domain);
		} else {
		    httpProxy = new HttpProxy(address, port, dialogProxyAuth.getProxyUsername(),
			    dialogProxyAuth.getProxyPassword());
		}

		// We do not return ==> We loop
	    }

	}
    }

    /**
     * Test whether a new version is to be installed
     * 
     * @param httpProxy the http network parameters
     * @return true if a new version is to be installed
     */

    public boolean doDownloadNewVersion() throws Exception {

	if (SystemInit.isJavaEditor()) {
	    return false;
	}

	HttpProtocolParameters httpProtocolParameters = new HttpProtocolParameters();
	// httpProtocolParameters.setAcceptAllSslCertificates(true);

	// Ok, install if necessary new version
	debug("Before new AwakeFileSession...");
	AwakeFileSession awakeFileSession = new AwakeFileSession(ServerParms.getAwakeSqlServerUrl(), 
		null, 
		(char[])null, // Constructor with password
		httpProxy, httpProtocolParameters);
	debug("After new AwakeFileSession...");
	RemoteSoftwareInfo remoteSoftwareInfo = new RemoteSoftwareInfo(awakeFileSession);

	debug("Before new  remoteSoftwareInfo.getVersion()...");
	String remoteVersion = remoteSoftwareInfo.getVersion();
	debug("After new  remoteSoftwareInfo.getVersion()...");
	String version = new net.safester.application.version.Version().toString();

	if (version.compareTo(remoteVersion) >= 0) {
	    return false; // No new install
	}

	// Ok, new install of modified jars
	String installationDir = InstallParameters.getInstallationDir();

	if (!installationDir.endsWith(File.separator)) {
	    installationDir += File.separator;
	}

	// Ok do the download & install
	parent.setVisible(false);
	new UpdateDownloader(awakeFileSession, installationDir, email, passphrase);

	return true;
    }

    /**
     * Check internet connection through a http session. (If connection fails it
     * will throw an execption)
     * 
     * @param urlAddress the url address to download
     * @throws Exception the Exception thrown by Awake & HttpClient
     */
    @SuppressWarnings("unused")
    private void checkUrlDownload(String urlAddress) throws Exception {
	// org.awakefm.file.http.HttpTransferOne.DEBUG = false;
	// System.out.println("httpProxy: " + httpProxy);

	debug("");
	debug(new Date() + " checkUrlDownload(): new AwakeFileSession()...");

	debug("proxy: " + httpProxy);

	awakeUrl = new AwakeUrl(httpProxy);

	debug(new Date() + " checkUrlDownload(): awakeSession.downloadUrl()...");

	@SuppressWarnings("unused")
	String content = awakeUrl.download(new URL(urlAddress));

	debug(new Date() + " checkUrlDownload(): Done!");
    }

    /**
     * debug tool
     */
    private void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + ProxySessionCheckerNew.class.getSimpleName() + " " + s);
	}
    }

}
