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
package com.safelogic.pgp.api.toolkit.xmlrpc;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import com.safelogic.pgp.api.toolkit.xmlrpc.util.LoggingOutputStream;
import com.safelogic.pgp.api.toolkit.xmlrpc.util.StdOutErrLevel;


/**
 * @author Nicolas de Pomereu
 * 
 * The XML RPC Server
 * java com.safelogic.pgp.api.toolkit.xmlrpc.Server
 */


public class Server 
{
    private static final String SERVER_NAME  = "cGeepAPI XML-RPC Server";

    private static final String CR_LF = System.getProperty("line.separator");

    /** To be used instead of System.out */
    private PrintStream stdout;

    /** To be used instead of System.err */
    private PrintStream stderr;

    
    /**
     * Sets the logging file and redirect System.out/System.err to the logging file 
     * Then use stdout.println() and stderr.println() to continue to display on console
     * 
     * @throws IOException
     */
    private void setLogging(String file)
    throws IOException
    {
        // initialize logging to go to rolling log file
        LogManager logManager = LogManager.getLogManager();
        logManager.reset();

        // log file max size 10K, 3 rolling files, append-on-open
        Handler fileHandler = new FileHandler(file, 10000000, 3, true);
        fileHandler.setFormatter(new SimpleFormatter());
        Logger.getLogger("").addHandler(fileHandler);        

        stdout = System.out;
        stderr = System.err;

        // now rebind stdout/stderr to logger
        Logger logger;
        LoggingOutputStream los;

        logger = Logger.getLogger("stdout");
        los = new LoggingOutputStream(logger, StdOutErrLevel.STDOUT);
        System.setOut(new PrintStream(los, true));

        logger = Logger.getLogger("stderr");
        los= new LoggingOutputStream(logger, StdOutErrLevel.STDERR);
        System.setErr(new PrintStream(los, true));        
    }
    
    /**
     * 
     * @param port          the port to use. Defaults to 8081
     * @param isParanoid    if true, Server will only accept 127.0.01 requests
     * @param logFile       the name of the logfile
     * 
     * @throws XmlRpcException
     * @throws Exception
     * @throws IOException
     */
    public void startServer(int port, boolean isParanoid, String logFile)
        throws XmlRpcException, Exception, IOException
    {
        setLogging(logFile);
        
        // 1) Minimal Web Server
        WebServer webServer = new WebServer(port);
        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

        // 2) Full Servlet implementation: cleaner
        //XmlRpcServlet servlet = new XmlRpcServlet();
        //ServletWebServer webServer = new ServletWebServer(servlet, chosenPort);
        //XmlRpcStreamServer xmlRpcServer = webServer.getXmlRpcServer();

        PropertyHandlerMapping phm = new PropertyHandlerMapping();

        //phm.addHandler("Calculator",  com.safelogic.pgp.api.toolkit.xmlrpc.Calculator.class);        
        phm.addHandler("CgeepApiXmlRpc",    com.safelogic.pgp.api.toolkit.CgeepApiXmlRpcServerWrapper.class);

        xmlRpcServer.setHandlerMapping(phm);

        XmlRpcServerConfigImpl serverConfig =
            (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
        serverConfig.setEnabledForExtensions(false);
        serverConfig.setContentLengthOptional(true);

        String ipOfMachine = IpMachinesAddress.getIpAddress();

        if (isParanoid)
        {
            webServer.setParanoid (true);

            // deny all clients
            webServer.acceptClient (ipOfMachine); // allow only local access
            webServer.acceptClient ("127.0.0.1"); // allow only local access
        }

        try
        {
            webServer.start();
        }
        catch (java.net.BindException e)
        {            
            String error = "Impossible to start " + SERVER_NAME + ". Port is already in use: " 
                            + port 
                            + CR_LF
                            + "Please retry with another port value.";

            stdout.println(error);
            System.out.println(error);
            return;
        }

        String line1 = SERVER_NAME + " address: " + "http://" + ipOfMachine + ":" + port + "/xmlrpc";
        String line2 = SERVER_NAME + " started and listening on port " + webServer.getPort();
        String line3 = null;
        
        if (isParanoid)
        {
            line3 = "(Paranoid on: server accepts only connections from " + ipOfMachine + ")";
        }
        else
        {
            line3 = "(Paranoid off: server accepts all connections)";            
        }
        
        stdout.println(line1);
        stdout.println(line2);
        stdout.println(line3);

        System.out.println(line1);
        System.out.println(line2);
        System.out.println(line3);        
        
        
    }    
    /**
     * 
     * @param args the Server IP address and Port Port
     * @throws Exception
     * 
     */
    public static void main(String[] args) throws Exception 
    {                       
        // Manage the parameters
        ServerParmsManager serverParmsManager = new ServerParmsManager();

        boolean isParmOk = serverParmsManager.executeCommand(args);

        if (! isParmOk)
        {
            return;
        }

        int port = serverParmsManager.getPort();
        boolean isParanoid = true;

        String strParanoid = serverParmsManager.getParanoid();
        if (strParanoid.equals("on"))
        {
            isParanoid = true;            
        }
        else
        {
            isParanoid = false;                
        }

        String logFile = System.getProperty("user.home") + File.separator +  "CgeepApiXmlRpcServer.log";
        
        Server server = new Server();
        server.startServer(port, isParanoid, logFile);
    }
}
