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
package net.safester.application.util.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import net.safester.application.updater.InstallParameters;

import org.apache.commons.lang3.StringUtils;


/**
 * @author RunningLiberty
 *
 */
public class Client
{
    
    /** debug infos */
    public static boolean DEBUG = false;
    
    /** The default port to use */
    public static int DEFAULT_PORT = 50028; 
    
    /** The default port to use */
    private int port = 0; 

    /** Buffered reader to read content from Socket*/
    private BufferedReader socket_in = null;

    /** PrintWriter into which we send out content */
    private PrintWriter socket_out = null;

    /** The client socket to use */
    private Socket socket = null;

    /** The new exception when connecting to socket server */
    private Exception exception;
    
    
    /**
     * @param args
     */
    public static void main(String[] args)   throws Exception
    {
        
        // Ok, new install of modified jars
        //String installationDir = InstallParms.getInstallationDir();

        String installationDir = "I:\\SafeShareIt\\dist";
        if(!installationDir.endsWith(File.separator)){
            installationDir += File.separator;
        }
                
        Client clientTest = new Client(installationDir, Client.DEFAULT_PORT);        
        clientTest.setRemotePassphrase("the passphrase in Socket Server *$".toCharArray());
        
        char [] passphrase = clientTest.getRemotePassphrase();        
        clientTest.close();
        
        if (passphrase != null)
        {
            System.out.println(new String(passphrase));
        }
        else
        {
            System.out.println("passphrase is null!");
        }

        System.out.println("Done!");
    }

    
    /**
     * @param String installationDir    the SafeShare Installation directory
     * 
     * @throws IOException 
     * @throws UnknownHostException 
     * 
     */
    public Client(String installationDir, int port) throws UnknownHostException, IOException
    {
        if (installationDir == null)
        {
            throw new IllegalArgumentException("installationDir can not be null!");
        }

        if (! new File(installationDir).exists())
        {
            throw new FileNotFoundException("installationDir does not exists: " + installationDir);
        }

        this.port = port;
        
        boolean connected =  false ;

        try
        {
            connected = connectToServer();
        }
        catch (ConnectException e)
        {
            // Start the server
            startSocketServer(installationDir);              
            //e.printStackTrace();
        }
            
        int maxTry = 0;
        while (! connected)
        {
            try { Thread.sleep(500); } catch (Exception e) {}
                        
            // try 3 times 
            if (maxTry > 3)
            {
                throw new IllegalArgumentException(exception);
            }
            
            try
            {
                connected = connectToServer();
            }
            catch (Exception e)
            {
                exception = e;
            }            
            
            maxTry++;
        }
                               
        debug("Ok Connected to port: " + port);
    }

    /**
     * Start the socket server in the specified directory
     * @param installationDir
     * @throws IOException
     */
    public void startSocketServer(String installationDir)
    throws IOException{
        String programName = InstallParameters.getJavaExecutablePath();
        String jarParam = "-jar";
        
        //String jar = buildJarParameter(installationDir);

        String jar = installationDir + InstallParameters.SAFESTER_JAR;

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            jar = "\"" + jar + "\"";
        }

        String host   = "null";   // Auto detection of host in SAFESTER_JAR
        String server = "server"; // Says we want to launch server
                
        debug("programName: " + programName);
        debug("jarParam   : " + jarParam);
        debug("jar        : " + jar);        
        debug("host       : " + host);     
        debug("server     : " + server);             
        
        @SuppressWarnings("unused")
        Process p = Runtime.getRuntime().exec(new String[]{programName, jarParam, jar, host, server});

    }
    
    /**
     * @throws UnknownHostException
     * @throws IOException
     */
    private boolean connectToServer() throws UnknownHostException, IOException
    {
        // Connect to the given machine 
        InetAddress addr = InetAddress.getByName("127.0.0.1");
        
        socket = new Socket(addr, port);

        socket_in = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));
        socket_out = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream())), true);
        
        return true;
    }


    /**
     * get the passphrase stored in/by socket server
     * @return the passphrase stored in/by socket server
     * @throws IOException
     */
    public char [] getRemotePassphrase()
    throws IOException
    {
        if ( socket_in == null ||  socket_out == null)
        {
            throw new IOException("Please start Client Socket.");
        }
        
        socket_out.println(SoTag.PLEASE_SEND_PASSPHRASE);
        socket_out.flush();

        // get the Received string 
        String buffer = socket_in.readLine();

        buffer = buffer.trim();

        if (buffer.contains(SoTag.SORRY_NO_PASSPHRASE_IN_MEMORY))
        {
            return null;
        }
        else if (buffer.contains(SoTag.PASSPHRASE_IS))
        {
            String passphraseStr = buffer.substring(SoTag.PASSPHRASE_IS.length());            
            return passphraseStr.toCharArray();
        }      
        else
        {
            throw new IllegalArgumentException("Socket Server protocol error. Invalid buffer: " 
                    +  buffer);
        }
    }
    
    /**
     * Sets a passphrase in memory
     * @param passphrase        the passphrase to set
     */
    public void setRemotePassphrase(char[] passphrase)
        throws IOException
    {        
        if ( socket_in == null ||  socket_out == null)
        {
            throw new IllegalArgumentException("Please start Client Socket.");
        }
        
        String s = null;

        if (passphrase == null)
        {
            s = SoTag.BYE; // Shut the serve for security
        }
        else
        {
            s = SoTag.PASSPHRASE_IS + new String(passphrase);
        }

        socket_out.println(s);
        socket_out.flush();

        String buffer = socket_in.readLine();
        
        debug(buffer); // Should be "OK".. Will test later
    }

    
    /**
     * Close the socket server and the socket
     *
     */
    public void close() throws IOException
    {
        setRemotePassphrase(null);
        socket.close();
    } 
    
    /**
     * debug tool
     */
    private void debug(String s)
    {
        String myClass = StringUtils.substringAfterLast(this.getClass().getName() , "."); 
        if (DEBUG)
            System.out.println(
                    myClass
                    + " " 
                    + new java.util.Date() 
                    + " "
                    + s);
    }
    

}

/**
 * 
 */
