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
package net.safester.application.socket.server;

// net.safester.application.socket.server.MultiServer

/**
 * The Socket Server to be launched by client side when accessing passphrase:
   
   MultiServer multiServer = new MultiServer();
   multiServer.startServer();           
 * 
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;

import javax.swing.JOptionPane;

import net.safester.application.util.UserPrefManager;


public class MultiServer {
        
    /** debug infos */
    public static boolean DEBUG = false;
    
    /** The Server Socket */
    private ServerSocket serverSocket;

    /** The passphrase to store */
    public static String passphrase = null;
    
     /**
     * SafeShareItSocketServer main launcher
     *
     * @param args
     */
    public static void main(String[] args) {

        try
        {
            System.out.println(new Date() + " Starting MultiServer Socket Server..."); 
            
            MultiServer multiServer = new MultiServer();
            multiServer.startServer();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            if (DEBUG)
            {
                JOptionPane.showMessageDialog(null, "MultiServer: Impossible to start Socket Server. Reason: " 
                        + e.toString());
            }
            
            System.exit(-1);
        }
    }

    /**
     * 
     * Default constructor
     */
    public MultiServer()
    {

    }


    /**
     * Start the Multi Socket Server 
     * @throws IOException
     */
    public void startServer() throws IOException
    {
        boolean listening = true;
        serverSocket = new ServerSocket(0); // Try first free port

        System.out.println(new Date() + " MultiServer Socket Server created on port " + serverSocket.getLocalPort() + ".");

        // Start a thread to update perpetually the running date + port number
        Thread t = new Thread() 
        {
            public void run() 
            {                
                while (true)
                {
                    // Store start date & port in preferences
                    String timeNow = "" + new Date().getTime();
                    UserPrefManager.setPreference(UserPrefManager.SOCKET_SERVER_START_TIME, timeNow);
                    UserPrefManager.setPreference(UserPrefManager.SOCKET_SERVER_PORT, serverSocket.getLocalPort());
                    
                    try {Thread.sleep(100);} catch (InterruptedException ex) {}
                }            
            }
        };
        t.start();
        
        while (listening)
        {            
            new MultiServerThread(serverSocket.accept()).start();
        }

        serverSocket.close();
    }
    
    /**
     * debug tool
     */
    
    @SuppressWarnings("unused")
    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
    }
    
}


