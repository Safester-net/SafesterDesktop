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

/**
 * The Socket Server mtulti-clients thread launched by client side when accessing passphrase
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import net.safester.application.socket.client.SoTag;


class MultiServerThread extends Thread {
        
    private Socket socket = null;

    public MultiServerThread(Socket socket) {
        super("MultiServerThread");
        
        if (socket == null)
        {
            throw new IllegalArgumentException("socket can not be null!");
        }
        this.socket = socket;
    }

        
    public void run() {

        System.out.println("MultiServer started and waiting on port " + socket.getLocalPort() + "...");
        
        BufferedReader in = null;
        PrintWriter out = null;
        
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                        new InputStreamReader(
                        socket.getInputStream()));
    
            String inputLine = null; 
            String outputLine = null;
                
            while ((inputLine = in.readLine()) != null)
            {
                if (inputLine.startsWith(SoTag.PLEASE_SEND_PASSPHRASE))
                {
                    if (MultiServer.passphrase == null)
                    {
                        outputLine = SoTag.SORRY_NO_PASSPHRASE_IN_MEMORY;
                    }
                    else
                    {
                        outputLine = SoTag.PASSPHRASE_IS + new String(MultiServer.passphrase );
                    }
                }
                else if (inputLine.startsWith(SoTag.PASSPHRASE_IS))
                {     
                    MultiServer.passphrase = StringUtils.substringAfter(inputLine, SoTag.PASSPHRASE_IS);
                    outputLine = SoTag.OK + " passprase received.";
                }
                else if (inputLine.startsWith(SoTag.BYE))
                {
                    outputLine = SoTag.OK; 
                }
                else
                {
                    outputLine = SoTag.UNKNOWN_ORDER ;
                }
                
                // Push back responses
                out.println(outputLine);


                if (inputLine.startsWith(SoTag.BYE))
                {
                    System.exit(1); // Brutal exit
                }

                socket.close();
                break;


            } // loop in while
            

    
        } catch (IOException e) {
            e.printStackTrace();
            
            if (MultiServer.DEBUG)
            {
                JOptionPane.showMessageDialog(null,  "MultiServer: Exception occured on Socket Stream: " + e.toString());
            }

             System.exit(-1);
        }
        finally
        {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
}

