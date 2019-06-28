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
package net.safester.application.photo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicolas de Pomereu
 *
 * A cahce of address book users ( designed to be used by Vault)
 */
public class UsersAddressBookCache {

    /** The list of existing users within the Address book */
    private static List<PhotoAddressBookLocal> users = null;
    
    /**
     * Constructor
     */
    protected UsersAddressBookCache() {

    }

    /** Stores in the cache the existing users list 
     * @param users the list of users within the Address book 
     **/
    public static void put(List<PhotoAddressBookLocal> photoAddressBookLocalList) {
        users = new ArrayList<PhotoAddressBookLocal>();
        for (PhotoAddressBookLocal photoAddressBookLocal : photoAddressBookLocalList) {
            users.add(photoAddressBookLocal);
        } 
    }

    /**
     * 
     * @return the users
     */
    public static List<PhotoAddressBookLocal> get() {
        
        if (users == null) {
            return null;
        }
        
        List<PhotoAddressBookLocal> newUsers = new ArrayList<PhotoAddressBookLocal>();
        
        for (PhotoAddressBookLocal photoAddressBookLocal : users) {
            newUsers.add(photoAddressBookLocal);
        }
        
        return newUsers;
    }
    
    /**
     * Clears the  cache 
     */
    public static void clear() {
	UsersAddressBookCache.users = null;
    }

}
