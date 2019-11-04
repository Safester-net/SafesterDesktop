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


/**
 * The Socket Server to be launched by client side when accessing passphrase:
   
   MultiServer multiServer = new MultiServer();
   multiServer.startServer();           
 * 
 */

import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JOptionPane;

import net.safester.application.util.UserPrefManager;


public class MultiServer {
    
    
    /** debug infos */
    public static boolean DEBUG = false;
    
    
    /**
     * Constructor
     */
    public MultiServer()
    {
        
    }

    /**
     * Start the Multi Socket Server 
     * @throws IOException
     */
    public void startServer()
    {
        try
        {
            ServerSocket serverSocket = null;
            boolean listening = true;

            int port = UserPrefManager.getIntegerPreference(SoTag.SOCKET_SERVER_PORT);
            
            if ( port == 0)
            {
                port = Client.DEFAULT_PORT;
            }
            
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                System.err.println("MultiServer: Could not listen on port: port.");
                
                if (MultiServer.DEBUG)
                {
                    JOptionPane.showMessageDialog(null, e);
                }
                            
                System.exit(-1);
            }

            while (listening)
            {
                new MultiServerThread(serverSocket.accept()).start();
            }

            serverSocket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            if (MultiServer.DEBUG)
            {
                JOptionPane.showMessageDialog(null, e);
            }              
        }
    }
    
}


