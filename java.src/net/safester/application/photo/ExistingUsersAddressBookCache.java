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
 * A cahce of existing address book users ( designed to be used by Vault)
 */
public class ExistingUsersAddressBookCache {

    /** The list of existing users (that have an account) within the Address book */
    private static List<PhotoAddressBookLocal> existingUsers = null;
    
    /**
     * Constructor
     */
    protected ExistingUsersAddressBookCache() {

    }

    /** Stores in the cache the existing users list 
     * @param existingUsers the list of existing users (that have an account) within the Address book 
     **/
    public static void put(List<PhotoAddressBookLocal> photoAddressBookLocalList) {
	
        existingUsers = new ArrayList<PhotoAddressBookLocal>();
        for (PhotoAddressBookLocal photoAddressBookLocal : photoAddressBookLocalList) {
            existingUsers.add(photoAddressBookLocal);
        }        
    }

    /**
     * 
     * @return the existingUsers
     */
    public static List<PhotoAddressBookLocal> get() {
        
        if (existingUsers == null) {
            return null;
        }
        
        List<PhotoAddressBookLocal> newExistingUsers = new ArrayList<PhotoAddressBookLocal>();
        
        for (PhotoAddressBookLocal photoAddressBookLocal : existingUsers) {
            newExistingUsers.add(photoAddressBookLocal);
        }
        
        return newExistingUsers;
    }
    
    /**
     * Clears the  cache 
     */
    public static void clear() {
	ExistingUsersAddressBookCache.existingUsers = null;
    }

}
