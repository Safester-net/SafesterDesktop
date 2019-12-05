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
package net.safester.noobs.clientserver;

public class LoginLogLocal2 {

    private int user_number = 0;
    private long date_time = -1;
    private String ip_address= null;
    private String hostname= null;
    private String device= null;
    
    public int getUser_number() {
        return user_number;
    }
    public void setUser_number(int user_number) {
        this.user_number = user_number;
    }
    public long getDate_time() {
        return date_time;
    }
    public void setDate_time(long time) {
        this.date_time = time;
    }
    public String getIp_address() {
        return ip_address;
    }
    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }
    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    public String getDevice() {
        return device;
    }
    public void setDevice(String device) {
        this.device = device;
    }
    @Override
    public String toString() {
	return "LoginLogLocal [user_number=" + user_number + ", date_time=" + date_time + ", ip_address=" + ip_address
		+ ", hostname=" + hostname + ", device=" + device + "]";
    }  
    
    
}
