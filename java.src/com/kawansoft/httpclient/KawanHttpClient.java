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
package com.kawansoft.httpclient;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;

/**
 * @author Nicolas de Pomereu
 * 
 *         AceQL Rest wrapper for AceQL http/REST apis that take care of all
 *         http calls and operations.
 * 
 *         All Exceptions are trapped with a {#link AceQLException} that allows
 *         to retrieve the detail of the Exceptions
 */
public class KawanHttpClient {

    public static boolean DEBUG = false;

    private boolean TRACE_ON = false;

    /** Proxy to use with HttpUrlConnection */
    private Proxy proxy = null;
    /** For authenticated proxy */
    private PasswordAuthentication passwordAuthentication = null;

    private static int connectTimeout = 0;
    private static int readTimeout = 0;

    private boolean prettyPrinting = false;
    private boolean gzipResult = true;

    private int httpStatusCode = HttpURLConnection.HTTP_OK;
    private String httpStatusMessage;

    private AtomicBoolean cancelled;
    private AtomicInteger progress;


    /**
     * Default constructor if no proxy info required
     */
    public KawanHttpClient() {
	super();
    }

    /**
     * Constructor with proxy info.
     * @param proxy
     * @param passwordAuthentication
     */
    public KawanHttpClient(Proxy proxy, PasswordAuthentication passwordAuthentication) {
	super();
	this.proxy = proxy;
	this.passwordAuthentication = passwordAuthentication;
	
	setProxyCredentials();
    }

    private void setProxyCredentials() {

	if (proxy == null) {
	    return;
	}

	// Sets the credential for authentication
	if (passwordAuthentication != null) {
	    final String proxyAuthUsername = passwordAuthentication.getUserName();
	    final char[] proxyPassword = passwordAuthentication.getPassword();

	    Authenticator authenticator = new Authenticator() {

		@Override
		public PasswordAuthentication getPasswordAuthentication() {
		    return new PasswordAuthentication(proxyAuthUsername, proxyPassword);
		}
	    };

	    if (DEBUG) {
		System.out.println("passwordAuthentication: " + proxyAuthUsername + " " + new String(proxyPassword));
	    }

	    Authenticator.setDefault(authenticator);
	}
	else {
	    Authenticator.setDefault(null);
	}

    }
    
    /**
     * Calls an URL with GET.
     * @param url
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws ProtocolException
     * @throws UnsupportedEncodingException
     */
    public String callWithGet(String url)
	    throws MalformedURLException, IOException, ProtocolException, UnsupportedEncodingException {

	String responseBody;

	try (InputStream in = callWithGetReturnStream(url)) {
	    if (in == null)
		return null;

	    ByteArrayOutputStream out = new ByteArrayOutputStream();

	    IOUtils.copy(in, out);

	    responseBody = out.toString("UTF-8");
	    if (responseBody != null) {
		responseBody = responseBody.trim();
	    }

	    trace("----------------------------------------");
	    trace(responseBody);
	    trace("----------------------------------------");

	    return responseBody;
	}

    }
    
    /**
     * Executes a POST on server and returns a result string.
     * @param theUrl
     * @param parametersMap
     * @return
     * @throws IOException
     * @throws ProtocolException
     * @throws SocketTimeoutException
     * @throws UnsupportedEncodingException
     */
    public String callWithPost(URL theUrl, Map<String, String> parametersMap)
	    throws IOException, ProtocolException, SocketTimeoutException, UnsupportedEncodingException {

	String result = null;

	try (InputStream in = callWithPostReturnInputStream(theUrl, parametersMap);) {

	    if (in != null) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(in, out);

		result = out.toString("UTF-8");
		trace("result: " + result);
	    }
	}
	return result;
    }
    
    // FUTUR USAGE: HTTP/2 with HttpClient

    // private int httpVersion = 1;
    // OkHttpClient client = new OkHttpClient();
    //
    // private InputStream callWithGetInputStreamHttp2(String url)
    // throws MalformedURLException, IOException, ProtocolException {
    //
    // Request request = new Request.Builder().url(url).build();
    //
    // Response response = client.newCall(request).execute();
    // return response.body().byteStream();
    //
    // }

    private InputStream callWithGetReturnStream(String url)
	    throws MalformedURLException, IOException, UnsupportedEncodingException {

	/*
	 * if (httpVersion == 1) { return callWithGetInputStreamHttp11(url); } else {
	 * return callWithGetInputStreamHttp2(url); }
	 */

	return callWithGetInputStreamHttp11(url);

    }

    private InputStream callWithGetInputStreamHttp11(String url)
	    throws MalformedURLException, IOException, ProtocolException {
	URL theUrl = new URL(url);
	HttpURLConnection conn = null;

	if (this.proxy == null) {
	    conn = (HttpURLConnection) theUrl.openConnection();
	} else {
	    conn = (HttpURLConnection) theUrl.openConnection(proxy);
	}

	conn.setRequestProperty("Accept-Charset", "UTF-8");
	conn.setReadTimeout(readTimeout);
	conn.setRequestMethod("GET");
	conn.setDoOutput(true);

	trace();
	trace("Executing request " + url);

	httpStatusCode = conn.getResponseCode();
	httpStatusMessage = conn.getResponseMessage();

	InputStream in = null;
	// if (httpStatusCode == HttpURLConnection.HTTP_OK || httpStatusCode ==
	// HttpURLConnection.HTTP_MOVED_TEMP) {
	if (httpStatusCode == HttpURLConnection.HTTP_OK) {
	    in = conn.getInputStream();
	} else {
	    in = conn.getErrorStream();
	}

	return in;
    }



    /*
     * NO! Bad implementation: always call an URL private InputStream
     * callWithPost(String action, Map<String, String> parameters) throws
     * MalformedURLException, IOException, ProtocolException,
     * UnsupportedEncodingException {
     * 
     * URL theUrl = new URL(url + action); return callWithPost(theUrl, parameters);
     * }
     */

    private InputStream callWithPostReturnInputStream(URL theUrl, Map<String, String> parameters)
	    throws IOException, ProtocolException, SocketTimeoutException, UnsupportedEncodingException {
	HttpURLConnection conn = null;

	if (this.proxy == null) {
	    conn = (HttpURLConnection) theUrl.openConnection();
	} else {
	    conn = (HttpURLConnection) theUrl.openConnection(proxy);
	}

	conn.setRequestProperty("Accept-Charset", "UTF-8");
	conn.setReadTimeout(readTimeout);
	conn.setRequestMethod("POST");
	conn.setDoOutput(true);

	TimeoutConnector timeoutConnector = new TimeoutConnector(conn, connectTimeout);

	try (OutputStream connOut = timeoutConnector.getOutputStream();) {
	    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connOut, "UTF-8"));
	    writer.write(KawanHttpClient.getPostDataString(parameters));

	    // writer.flush();
	    writer.close();
	}

	trace();
	trace("Executing request: " + theUrl.toString());

	if (parameters.containsKey("sql")) {
	    trace("sql..............: " + parameters.get("sql"));
	}

	trace("parameters.......: " + parameters);

	// Analyze the error after request execution
	httpStatusCode = conn.getResponseCode();
	httpStatusMessage = conn.getResponseMessage();

	InputStream in = null;
	if (httpStatusCode == HttpURLConnection.HTTP_OK) {
	    in = conn.getInputStream();
	} else {
	    in = conn.getErrorStream();
	}

	return in;
    }


    /**
     * Formats & URL encode the the post data for POST.
     * 
     * @param params the parameter names and values
     * @return the formated and URL encoded string for the POST.
     * @throws UnsupportedEncodingException
     */
    public static String getPostDataString(Map<String, String> requestParams) throws UnsupportedEncodingException {
	StringBuilder result = new StringBuilder();
	boolean first = true;

	for (Map.Entry<String, String> entry : requestParams.entrySet()) {

	    // trace(entry.getKey() + "/" + entry.getValue());

	    if (first)
		first = false;
	    else
		result.append("&");

	    if (entry.getValue() != null) {
		result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
		result.append("=");
		result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
	    }
	}

	return result.toString();
    }
  

    /**
     * Says if trace is on
     * 
     * @return true if trace is on
     */
    public boolean isTraceOn() {
	return TRACE_ON;
    }

    /**
     * Sets the trace on/off
     * 
     * @param TRACE_ON if true, trace will be on
     */
    public void setTraceOn(boolean traceOn) {
	TRACE_ON = traceOn;
    }

    /**
     * Returns the cancelled value set by the progress indicator
     * 
     * @return the cancelled value set by the progress indicator
     */
    public AtomicBoolean getCancelled() {
	return cancelled;
    }

    /**
     * Sets the shareable canceled variable that will be used by the progress
     * indicator to notify this instance that the user has cancelled the current
     * blob/clob upload or download.
     * 
     * @param cancelled the shareable canceled variable that will be used by the
     *                  progress indicator to notify this instance that the end user
     *                  has cancelled the current blob/clob upload or download
     * 
     */
    public void setCancelled(AtomicBoolean cancelled) {
	this.cancelled = cancelled;
    }

    /**
     * Returns the sharable progress variable that will store blob/clob upload or
     * download progress between 0 and 100
     * 
     * @return the sharable progress variable that will store blob/clob upload or
     *         download progress between 0 and 100
     * 
     */
    public AtomicInteger getProgress() {
	return progress;
    }

    /**
     * Sets the sharable progress variable that will store blob/clob upload or
     * download progress between 0 and 100. Will be used by progress indicators to
     * show the progress.
     * 
     * @param progress the sharable progress variable
     */
    public void setProgress(AtomicInteger progress) {
	this.progress = progress;
    }

    /**
     * @return the prettyPrinting
     */
    public boolean isPrettyPrinting() {
	return prettyPrinting;
    }

    /**
     * Says the query result is returned compressed with the GZIP file format.
     * 
     * @return the gzipResult
     */
    public boolean isGzipResult() {
	return gzipResult;
    }

    /**
     * Says if JSON contents are to be pretty printed. Defaults to false.
     * 
     * @param prettyPrinting if true, JSON contents are to be pretty printed
     */
    public void setPrettyPrinting(boolean prettyPrinting) {
	this.prettyPrinting = prettyPrinting;
    }

    /**
     * Define if result sets are compressed before download. Defaults to true.
     * 
     * @param gzipResult if true, sets are compressed before download
     */
    public void setGzipResult(boolean gzipResult) {
	this.gzipResult = gzipResult;
    }

  
    public int getHttpStatusCode() {
	return httpStatusCode;
    }

    /**
     * @return the httpStatusMessage
     */
    public String getHttpStatusMessage() {
	return httpStatusMessage;
    }
    
    /**
     * Sets the read timeout.
     * 
     * @param readTimeout an <code>int</code> that specifies the read timeout value,
     *                    in milliseconds, to be used when an http connection is
     *                    established to the remote server. See
     *                    {@link URLConnection#setReadTimeout(int)}
     */
    public static void setReadTimeout(int readTimeout) {
	KawanHttpClient.readTimeout = readTimeout;
    }

    /**
     * Sets the connect timeout.
     * 
     * @param connectTimeout Sets a specified timeout value, in milliseconds, to be
     *                       used when opening a communications link to the remote
     *                       server. If the timeout expires before the connection
     *                       can be established, a java.net.SocketTimeoutException
     *                       is raised. A timeout of zero is interpreted as an
     *                       infinite timeout. See
     *                       {@link URLConnection#setConnectTimeout(int)}
     */
    public static void setConnectTimeout(int connectTimeout) {
	KawanHttpClient.connectTimeout = connectTimeout;
    }
    
   
    public void trace() {
	if (TRACE_ON) {
	    System.out.println();
	}
    }

    public void trace(String s) {
	if (TRACE_ON) {
	    System.out.println(s);
	}
    }

}
