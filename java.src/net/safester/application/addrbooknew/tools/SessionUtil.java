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
package net.safester.application.addrbooknew.tools;


import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import net.safester.application.messages.MessagesManager;


/**
 * Tools for session management for GUI package
 *
 * @author Nicolas de Pomereu
 */
public class SessionUtil {



    /**
     * Protected
     */
    protected SessionUtil() {

    }
    
    /**
     * Decode with a clear user message a session Exception and throws an Exception
     *
     * @param exception the Exception triggered by the client software
     * @return a clean error message
     */
    public static String getCleanErrorMessage(Exception exception) {
	
	System.err.println(new Date());
	exception.printStackTrace();
	
	String message = getErrorMessage(exception);
	return message;
    }
    
    /**
     * Decode with a clear user message a session Exception
     *
     * @param exception the Exception triggered by the client software
     * @return a clean error message
     */
    private static String getErrorMessage(Exception exception) {
        
        /*
        RemoteSession creation:
        - Carte activée mais pas connecté : java.net.UnknownHostException: www.kawandoc.com
        - Wifi désactivé : java.net.UnknownHostException: www.kawandoc.com
        - Carte désactivée : java.net.UnknownHostException: www.kawandoc.com

        - webapp fermée : java.net.ConnectException: https://www.kawandoc.com/ServerFileManager: Servlet failed: Not Found status: 404

        RemoteSession call():
        - carte activée mais pas connecté : java.net.NoRouteToHostException: No route to host: connect
        - Wifi désactivé : java.net.NoRouteToHostException: No route to host: connect
        - Carte désactivée : java.net.SocketException: Network is unreachable: connect

        - webapp fermée : java.net.ConnectException: https://www.kawandoc.com/ServerFileManager: Servlet failed: Not Found status: 404

        Aussi arrivé quand wifi allumé et pas de réseau sélectionné :
        java.net.ConnectionException : timeout
         */

        String message = null;

        if (exception instanceof NoRouteToHostException) {
            message = MessagesManager.get("not_connected_to_internet_or_firewall_blocked");
            message = message.replace("{0}", exception.getClass().getSimpleName());
            return message;
        }

        if (exception instanceof UnknownHostException) {
            message = MessagesManager.get("not_connected_to_internet_or_ip_server_not_found");
            message = message.replace("{0}", exception.getClass().getSimpleName());
            return message;
        }

        if (exception instanceof ConnectException) {
            if (exception.getMessage().toLowerCase().contains("timed out")) {
                message = MessagesManager.get("not_connected_to_internet_timeout");
                message = message.replace("{0}", exception.getClass().getSimpleName());
                return message;
            }

            if (exception.getMessage().toLowerCase().contains("404")) {
                message = MessagesManager.get("no_response_from_server");
                message = message.replace("{0}", exception.getClass().getSimpleName());
                return message;
            }
        }

        if (exception instanceof SocketException) {
            message = MessagesManager.get("not_connected_to_internet");
            message = message.replace("{0}", exception.getClass().getSimpleName());
            return message;
        }

        // All other messages (crypto, email, I/O are trapped here)
        message = exception.getMessage() + ". (" + exception.getClass().getSimpleName() + ")";
        return message; 
    }
    

}
