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

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * @author Nicolas de Pomereu
 */

public class GsonUtilAddressBookLocal {
    
    public static String listToGson(List<AddressBookNewLocal> addressBookLocals) {
        Gson gsonOut = new Gson();
        Type listOfAddressBookLocal = new TypeToken<List<AddressBookNewLocal>>() {
        }.getType();
        String jsonString = gsonOut.toJson(addressBookLocals, listOfAddressBookLocal);
        return jsonString;
    }

    public static List<AddressBookNewLocal> gsonToList(String jsonString) throws JsonParseException {
        Gson gsonOut = new Gson();
        Type listOfAddressBookLocal = new TypeToken<List<AddressBookNewLocal>>() {
        }.getType();
        List<AddressBookNewLocal> addressBookLocalNewList = gsonOut.fromJson(jsonString, listOfAddressBookLocal);
        return addressBookLocalNewList;
    }
  
}
