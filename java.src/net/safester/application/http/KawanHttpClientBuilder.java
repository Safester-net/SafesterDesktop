/**
 * 
 */
package net.safester.application.http;

import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

import org.awakefw.commons.api.client.HttpProxy;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.kawansoft.httpclient.KawanHttpClient;

/**
 * Allows to build an easy to use HttpClient.
 * @author Nicolas de Pomereu
 *
 */
public class KawanHttpClientBuilder {

    protected KawanHttpClientBuilder() {

    }

    /**
     * Builds KawanHttpClient using proxy info. If proxyHostname is null, no proxy will be used.
     * @param proxyHostname	(can be null)
     * @param proxyPort
     * @param proxyUserName
     * @param proxyPassword
     * @return
     */
    public static KawanHttpClient build(String proxyHostname, int proxyPort, String proxyUserName,
	    String proxyPassword) {
	KawanHttpClient kawanHttpClient = null;
	
	Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHostname, proxyPort));

	if (proxyUserName == null) {
	    kawanHttpClient = new KawanHttpClient(proxy, null);
	} else {
	    kawanHttpClient = new KawanHttpClient(proxy,
		    new PasswordAuthentication(proxyUserName, proxyPassword.toCharArray()));
	}
	return kawanHttpClient;
    }
    
    /**
     * Builds KawanHttpClient from an Awake HttpProxy.
     * @param httpProxy		(can be null)
     * @return
     */
    public static KawanHttpClient build(HttpProxy httpProxy) {
	KawanHttpClient kawanHttpClient = null;

	if (httpProxy == null) {
	    kawanHttpClient = new KawanHttpClient();
	} else {
	    String proxyHostname = httpProxy.getAddress();
	    int proxyPort = httpProxy.getPort();
	    String proxyUserName = httpProxy.getUsername();
	    String proxyPassword = httpProxy.getPassword();

	    kawanHttpClient = build(proxyHostname, proxyPort, proxyUserName, proxyPassword);
	}

	return kawanHttpClient;
    }
    
    /**
     * Builds KawanHttpClient using an Awake AwakeConnection proxy info.
     * 
     * @param awakeConnection
     * @return
     */
    public static KawanHttpClient buildFromAwakeConnection(AwakeConnection awakeConnection) {
	
	if (awakeConnection == null) {
	    throw new NullPointerException("awakeConnection is null!");
	}
	
	KawanHttpClient kawanHttpClient = null;

	AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
	HttpProxy httpProxy = awakeFileSession.getHttpProxy();

	kawanHttpClient = build(httpProxy);

	return kawanHttpClient;
    }





}
