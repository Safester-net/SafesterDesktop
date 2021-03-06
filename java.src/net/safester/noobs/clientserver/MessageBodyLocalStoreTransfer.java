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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 *
 * @author Nicolas de Pomereu
 */
public class MessageBodyLocalStoreTransfer {

    public static MessageBodyLocalStore fromJson(String jsonString) {
        Gson gsonOut = new Gson();
        Type typeMessageBodyLocalStore = new TypeToken<MessageBodyLocalStore>() {
        }.getType();

        MessageBodyLocalStore messageBodyLocalStore = gsonOut.fromJson(jsonString,
        	typeMessageBodyLocalStore);
        return messageBodyLocalStore;
    }

    public static String toGson(MessageBodyLocalStore messageBodyLocalStore) {
        Gson gsonOut = new Gson();
        Type typeOfMessageBodyLocalStore = new TypeToken<MessageBodyLocalStore>() {
        }.getType();
        String jsonString = gsonOut.toJson(messageBodyLocalStore, typeOfMessageBodyLocalStore);
        return jsonString;
    }

}
