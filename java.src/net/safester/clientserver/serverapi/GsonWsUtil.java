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
package net.safester.clientserver.serverapi;

import java.io.BufferedReader;
import java.io.StringReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * GSON utility class
 * 
 * @author abecquereau
 *
 */
public final class GsonWsUtil {

    /**
     * Create json string representing object
     * 
     * @param obj
     * @return
     */
    public static String getJSonString(final Object obj) {
	final GsonBuilder builder = new GsonBuilder();
	final Gson gson = builder.setPrettyPrinting().create();
	return gson.toJson(obj, obj.getClass());
    }

    /**
     * Create Object from jsonString
     * 
     * @param jsonString
     * @param type
     * @return
     */
    public static <T extends Object> T fromJson(final String jsonString, final Class<T> type) {
	final GsonBuilder builder = new GsonBuilder();
	final Gson gson = builder.create();
	final BufferedReader bufferedReader = new BufferedReader(new StringReader(jsonString));
	final T dTO = gson.fromJson(bufferedReader, type);
	return dTO;
    }
}
