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
package net.safester.application.socket.client;

public class SoTag
{
    // Constants for client <==> server talk
    public static final String OK                              = "OK";
    public static final String BYE                             = "BYE";
    public static final String SORRY_NO_PASSPHRASE_IN_MEMORY   = "SORRY_NO_PASSPHRASE_IN_MEMORY";
    public static final String PLEASE_SEND_PASSPHRASE          = "PLEASE_SEND_PASSPHRASE";
    public static final String SOCKET_SERVER_PORT              = "SOCKET_SERVER_PORT";
    public static final String PASSPHRASE_IS                   = "PASSPHRASE_IS:";
    public static final String UNKNOWN_ORDER                   = "UNKNOWN_ORDER";   
    
}

