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
package net.safester.application.util.proxy;

import org.awakefw.commons.api.client.HttpProxy;

import net.safester.application.util.UserPrefManager;


/**
 * @author Nicolas de Pomereu
 * 
 * Allows to detecdt and set the address & port of a proxy
 * to HttpNetworkParameters instance
 */
public class ProxyUtil
{
    /**
     * Detect & addt the proxy settings to the passed instance
     * @param httpNetworkParameters the network parameters instance to wich we mus add the proxy settings
     * @return network parameters instance with the proxy address & port set, if necessary.
     */
    public static HttpProxy setProxyFromUserPreference()
    {
        String proxyAddress = null;
        int proxyPort = 0;
        
        int proxyType= UserPrefManager.getIntegerPreference(UserPrefManager.PROXY_TYPE);

        if (proxyType == UserPrefManager.PROXY_TYPE_BROWSER_DEF)
        {
            ProxyDetector proxyDetector = new ProxyDetector();
            proxyAddress = proxyDetector.getHostName();
            proxyPort = proxyDetector.getPort();
        }
        else if (proxyType == UserPrefManager.PROXY_TYPE_USER_DEF)
        {
            proxyAddress = UserPrefManager.getPreference(UserPrefManager.PROXY_ADDRESS);
            proxyPort = UserPrefManager.getIntegerPreference(UserPrefManager.PROXY_PORT);
        }
        else if (proxyType == UserPrefManager.PROXY_TYPE_DIRECT)
        {
            // Do nothing. We use direct connection!
        }
        else
        {
            throw new IllegalArgumentException("Proxy Type is invalid: " + proxyType);
        }
        
        HttpProxy httpProxy = null;
        
        // We van now set the values into HttpNetworkParameters
        if (proxyAddress != null)
        {
            httpProxy = new HttpProxy(proxyAddress, proxyPort);
        }

        return httpProxy;
    }
  
}
