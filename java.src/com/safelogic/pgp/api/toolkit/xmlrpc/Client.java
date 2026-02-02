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
package com.safelogic.pgp.api.toolkit.xmlrpc;

import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;



public class Client
{

    public static void main(String[] args) throws Exception 
    {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://192.168.1.100:8081/xmlrpc"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
                        
        //Object[] params = new Object[]{new Integer(33), new Integer(10)};        
        //Integer result = (Integer) client.execute("CgeepApi.testIt", params);        
        //System.out.println("CgeepApi.testIt result: " + result);
                
        Object[] strParams = new Object[]{} ;       
        String strResult = (String) client.execute("CgeepApi.getVersion", strParams);
        
        System.out.println("strResult: " + strResult);
        
    }
}

