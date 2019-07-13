/**
 * 
 */
package com.kawansoft.httpclient;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ProxyTest {

    /**
     * 
     */
    public ProxyTest() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	 String proxyHostname = "127.0.0.1";
	 int proxyPort = 8080;
	 Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHostname, proxyPort));
	 
	 

    }

}
