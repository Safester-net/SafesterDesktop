package com.safelogic.pgp.apispecs;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.UnknownServiceException;

/**
 * HttpTransfer
 *
 * Simple send() and recv() commands on PC side.
 *
 * Purpose of this interface is to wrap the underlying HTTP tool in use in implementation.
 * Default implementation uses Java 11+ java.net.http.HttpClient.
 *
 * Note:
 * - This interface is not usable on HTTP Server side.
 * - Design of server uses a ServletController. The ServletController retrieves the string
 *   with GET or POST.
 * - The ServletController sends back a string using a Servlet that writes on output stream.
 *
 * Usage:
 * - Each send() must be followed by:
 *   - isSendOk() to test if all operations are ok
 *   - recv() to get the result as a String
 *
 * Class includes downloadFileFromUrl() as it says.
 */
public interface HttpTransfer {

    /**
     * Send a request to the HTTP server.
     *
     * @param url       the url of the site. Example: http://www.cgeep.com/do/Recv
     * @param method    GET or POST only
     * @param payload   the POST body (ignored for GET). Typically "application/x-www-form-urlencoded"
     *
     * @throws SocketException           if there is no HTTP access
     * @throws ConnectException          if the host is not reachable
     * @throws UnknownServiceException   if the servlet is not found or HTTP status is not OK
     * @throws ProtocolException         if the response is not valid for the expected protocol
     * @throws IOException               for all other IO / Network / System errors
     */
    void send(String url, String method, String payload)
            throws SocketException,
                   ConnectException,
                   UnknownServiceException,
                   ProtocolException,
                   IOException;

    /**
     * Send a String to the cGeep HTTP server using the default endpoint (implementation-defined).
     *
     * @param s string to send
     *
     * @throws SocketException           if there is no HTTP access
     * @throws ConnectException          if the host is not reachable
     * @throws UnknownServiceException   if the servlet is not found
     * @throws ProtocolException         if the response is not valid for the expected protocol
     * @throws IOException               for all other IO / Network / System errors
     */
    void send(String s)
            throws SocketException,
                   ConnectException,
                   UnknownServiceException,
                   ProtocolException,
                   IOException;

    /**
     * @return true if the last send() command successfully executed
     */
    boolean isSendOk();

    /**
     * Receive a String from the HTTP Server.
     *
     * @return the received string from the HTTP Server
     */
    String recv();

    /**
     * Download and create a File from an URL.
     *
     * @param file       the file to create from the download
     * @param fileLength expected length (for progress indicator)
     * @param url        the URL to download
     *
     * @throws SocketException           if there is no HTTP access
     * @throws ConnectException          if the host is not reachable
     * @throws UnknownServiceException   if the resource is not found or HTTP status is not OK
     * @throws ProtocolException         if the response is not valid for the expected protocol
     * @throws IOException               for all other IO / Network / System errors
     * @throws InterruptedException      if user interrupts thread
     */
    void downloadFileFromUrl(File file, long fileLength, String url)
            throws SocketException,
                   ConnectException,
                   UnknownServiceException,
                   ProtocolException,
                   IOException,
                   InterruptedException;

    /**
     * Test if an http address is reachable.
     *
     * @param httpAddress the Http Address to test. Ex: http://www.google.com
     * @return true if reachable
     */
    boolean isHttpReachable(String httpAddress);

    /**
     * Test if there is a System Proxy defined in Windows.
     *
     * @return  0 if there is no System Proxy in use
     *          1 if there is a System Proxy in use
     *         -1 if it is impossible to know (no Internet connection)
     */
    int diagnoseProxySetting();
}
