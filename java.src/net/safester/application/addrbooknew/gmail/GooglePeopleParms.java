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
package net.safester.application.addrbooknew.gmail;


/**
 * Gmail People API constants for authentication
 * 
 * @author Nicolas de Pomereu
 *
 */
public class GooglePeopleParms {

    protected GooglePeopleParms() {
    }
         
    //https://console.cloud.google.com/
    //https://console.developers.google.com/apis/api/people.googleapis.com/overview?project=safester-198714&duration=PT1H
    
    public static final String APPLICATION_NAME = "Safester"; 
    public static final String RESPONSE_TYPE_CODE = "code"; 
    public static final String SCOPE = "https://www.googleapis.com/auth/contacts.readonly";
    public static final String REDIRECT_URL = "urn:ietf:wg:oauth:2.0:oob";
    public static final String CLIENT_ID = "867619729893-r2itvu10me6ni6hps7f7qps73vr5k13h.apps.googleusercontent.com";
       
    // The encryption key for Client Secret fetched from server
    static final String CLIENT_SECRET_PASSWORD = "loveme$890;3";

}
