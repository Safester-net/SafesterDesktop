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
package com.safelogic.pgp.util;

import java.util.StringTokenizer;



/**
 * @author Nicolas de Pomereu
 * 
 * Class that detext a Proxy Address & Port.
 * <br> 
 * <br>The the settings are:
 * <br> - System.getProperties().put( "socksProxyPort", "1080");
 * <br> - System.getProperties().put( "socksProxyHost" ,"proxy.host.address");
 * 
 */
public class WindowsSocksProxyDetector implements SocksProxyDetector
{
    // Static final registry names
    private static final String PROXY_ENABLE = "ProxyEnable";
    private static final String PROXY_SERVER = "ProxyServer";
    private static final String SOFTWARE_MICROSOFT_WINDOWS_CURRENT_VERSION_INTERNET_SETTINGS 
                        = "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
    
    /** The debug flag */ 
    protected boolean DEBUG = true; //Debug.isSet(this);
    
    /** The Proxy Address */
    private String proxyAddress = null;

    /** The Proxy Port */
    private int proxyPort = 0;
        
    /**
     * Constructor
     */
    public WindowsSocksProxyDetector()
    {
        if(System.getProperty("os.name").indexOf("Windows") == -1)
        {
            return;
        }
        
        this.detectProxy();
    }
      
    /**
     * Detect if there is a Socks System Proxy (defined in IE) and set the values in memory.
     * 
     *  We will query directly the 
     *  HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Internet Settings 
     *  subkeys:
     *  <br>
     *  <br> - ProxyEnable: if 1 ==> use proxy defined in IE & Windows
     *  <br> - ProxyServer: will contain either 
     *        1) Proxy Address & Port in "192.168.1.20:3128" format for socks
     *        2) Different settings for socks  in format:
     *           "ftp=192.168.1.20:3128;http=192.168.1.20:3128;https=192.168.1.20:3128;socks=192.168.1.24:3128" 
     *           
     */
    private void detectProxy()
    {
                                
        try
        {
            
            String subkey = SOFTWARE_MICROSOFT_WINDOWS_CURRENT_VERSION_INTERNET_SETTINGS; 
            
            // 1) Test if registry value ProxyEnable == 1
            
/*            
            WindowsIERegistrySettings ieRegistrySettings = new WindowsIERegistrySettings(null);
            
            int proxyEnable = ieRegistrySettings.getProxyEnable();
            
            if (proxyEnable == 0)
            {
                // Nothing to do ==> No active proxy settings
                return;
            }        
*/
            Registry registry = new Registry();            
            long proxyEnable = registry.getCurrentUserKeyValueDword(subkey, PROXY_ENABLE);
            
            if (proxyEnable == 0)
            {
                // Nothing to do ==> No active proxy settings
                return;
            }     
            
            // 2) Get The HTTP & HTTPS Proxy Address & Port            
            String proxyServer = registry.getCurrentUserKeyValue(subkey, PROXY_SERVER);
            
            //debug("proxyServer: " + proxyServer + ":");
            
            // Test if there is a multi protocol string 
            // as "ftp=192.168.1.20:3128;http=192.168.1.20:3128;https=192.168.1.20:3128;socks=192.168.1.24:3128"
                       
            if (proxyServer.indexOf("=") != -1)
            {
                // We have a multi protocole chain     
                String proxyServerHttp  = extractInetSocketAddress(proxyServer);
                proxyAddress   = proxyServerHttp.substring(0, proxyServerHttp.indexOf(":"));
                String strProxyPort = proxyServerHttp.substring(proxyServerHttp.indexOf(":") + 1);
                proxyPort = Integer.parseInt(strProxyPort);
                           
            }
            else
            {
                proxyAddress   = proxyServer.substring(0, proxyServer.indexOf(":"));
                String strProxyPort = proxyServer.substring(proxyServer.indexOf(":") + 1);
                proxyPort = Integer.parseInt(strProxyPort);               
            }
        }
        catch (Exception e)
        {
           e.printStackTrace(); 
           throw new IllegalArgumentException("Internet Settings registry access failed: " 
                   + e);
        }
    }
    
    /**
     * Extract a socks proxy addr from a formated string as :
     * "ftp=192.168.1.20:3128;http=192.168.1.20:3128;https=192.168.1.20:3128;socks=192.168.1.24:3128"
     * 
     * @param chain             the format chain
     * 
     * @return the socks proxy addr in 192.168.1.20:3128 format if found, else null
     */
    private String extractInetSocketAddress(String chain)
    {
        if (chain == null)
        {
            throw new IllegalArgumentException("chain can't be null!");
        }
        
        String protocol = "socks";
                
        StringTokenizer st = new StringTokenizer(chain, ";", false);
        String token;
        
        while (st.hasMoreTokens())
        {
            token = st.nextToken();
            
            if (token.startsWith(protocol + "="))
            {
                token = token.substring(protocol.length() + 1);
                System.out.println("token :" + token + ":");
                return token;
            }
        }
        
        return null;
    }

    /**
     * @return the proxyAddress
     */
    public String getProxyAddress()
    {
        return proxyAddress;
    }


    /**
     * @return the proxyPort
     */
    public int getProxyPort()
    {
        return proxyPort;
    }

    

    /**
     * debug tool
     */
    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
    }
    
    /** Main */
    public static void main(String[] args)
    {
        SocksProxyDetector socksProxyDetector = new WindowsSocksProxyDetector();
            
        System.out.println("");
        System.out.println("getProxyAddress()       :" + socksProxyDetector.getProxyAddress() + ":");
        System.out.println("getProxyPort()          :" + socksProxyDetector.getProxyPort() + ":");
        
    }
    
}
