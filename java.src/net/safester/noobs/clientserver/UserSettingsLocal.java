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

import net.safester.noobs.clientserver.specs.Local;


/**
 * @author Nicolas de Pomereu
 * Defines local instance of a UserSettings
 */

public class UserSettingsLocal implements Local
{		
    
    /** Table columns */    
    private int user_number;
    private String user_name = null;
    private String notification_email = null;
    private boolean receive_infos = false;
    private boolean stealth_mode = false;
    private String signature = null;
    private boolean notification_on = false;  
    private boolean send_anonymous_notification_on = false;
    private boolean use_otp = false;

    /**
     * Constructor (Void)
     */
    public UserSettingsLocal()
    {
        // Void Constructor
    }
          
    /** Return field value */
    public int getUserNumber()
    {
        return this.user_number;
    }
    /** Return field value */
    public String getUserName()
    {
        return this.user_name;
    }
    /** Return field value */
    public String getNotificationEmail()
    {
        return this.notification_email;
    }
    /** Return field value */
    public boolean getReceiveInfos()
    {
        return this.receive_infos;
    }
    /** Return field value */
    public boolean getStealthMode()
    {
        return this.stealth_mode;
    }
        
    /**
     * @return the notification_on
     */
    public boolean isNotificationOn()
    {
        return notification_on;
    }

    /**
     * @return the use_otp
     */
    public boolean isUseOtp(){
    	return use_otp;
    }
    
    public void setUseOtp(boolean useOtp){
    	use_otp = useOtp;
    }
    /**
     * @param notificationOn the notification_on to set
     */
    public void setNotificationOn(boolean notificationOn)
    {
        notification_on = notificationOn;
    }

    /** Set field value */
    public void setUserNumber(int user_number)
    {
        this.user_number = user_number;
    } 
    /** Set field value */
    public void setUserName(String user_name)
    {
        this.user_name = user_name;
    } 
    /** Set field value */
    public void setNotificationEmail(String notification_email)
    {
        this.notification_email = notification_email;
    } 
    /** Set field value */
    public void setReceiveInfos(boolean receive_infos)
    {
        this.receive_infos = receive_infos;
    } 
    /** Set field value */
    public void setStealthMode(boolean stealth_mode)
    {
        this.stealth_mode = stealth_mode;
    } 
    
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean getSend_anonymous_notification_on() {
        return send_anonymous_notification_on;
    }

    public void setSend_anonymous_notification_on(boolean send_anonymous_notification_on) {
        this.send_anonymous_notification_on = send_anonymous_notification_on;
    }

    

} // EOF
 
