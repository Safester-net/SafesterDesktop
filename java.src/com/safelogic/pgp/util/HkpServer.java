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
package com.safelogic.pgp.util;

import java.net.MalformedURLException;
import java.net.URL;


public class HkpServer {

    //address of the server
    private String address;
    //Status of server 
    private boolean isOn;

    /**
     * Creates a HkpServer with given parms
     * @param address       address of the server
     * @param isOn          status of the server (active or not)
     * @throws java.net.MalformedURLException       if address is not an url
     */
    public HkpServer(String address, boolean isOn)
                throws MalformedURLException
    {
        java.net.URL addressAsUrl = new URL(address);
        this.address = address;
        this.isOn = isOn;
    }

    /**
     * get address of the server
     * @return A string representation of the address of the server
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set address of server
     * @param address               the new address
     * @throws MalformedURLException if given address is not valid
     */
    public void setAddress(String address) 
            throws MalformedURLException {
        java.net.URL addressAsUrl = new URL(address);
        this.address = address;
    }

    /**
     * Get server status
     * @return  true if server is active
     */
    public boolean isIsOn() {
        return isOn;
    }

    /**
     * Set status of server
     * @param isOn      true if server is active false otherwise
     */
    public void setIsOn(boolean isOn) {
        this.isOn = isOn;
    }
}
