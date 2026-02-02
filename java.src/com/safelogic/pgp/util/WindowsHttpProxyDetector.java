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
 * Class that detext a Http/Https Proxy Address & Port.
 */
public class WindowsHttpProxyDetector implements HttpProxyDetector
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
    
    /** The Secure Proxy Address */
    private String secureProxyAddress = null;

    /** The Secure Proxy Port */
    private int secureProxyPort = 0;    
    
    /**
     * Constructor
     */
    public WindowsHttpProxyDetector()
    {        
        if(System.getProperty("os.name").indexOf("Windows") == -1)
        {
            return;
        }
        
        detectProxy();
    }
    
  
    /**
     * Detect if there is a Http System Proxy (defined in IE) and set the values in memory.
     * 
     *  We will query directly the 
     *  HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Internet Settings 
     *  subkeys:
     *  <br>
     *  <br> - ProxyEnable: if 1 ==> use proxy defined in IE & Windows
     *  <br> - ProxyServer: will contain either 
     *        1) Proxy Address & Port in "192.168.1.20:3128" format for both HTTP & HTTPS.
     *        2) Different settings for HTTP & HTTPS in:
     *           "ftp=192.168.1.20:3128;http=192.168.1.20:3128;https=192.168.1.20:3128" format
     */
    private void detectProxy()
    {
                                
        try
        {
                                    
            // 1) Test if registry value ProxyEnable == 1
            String subkey = SOFTWARE_MICROSOFT_WINDOWS_CURRENT_VERSION_INTERNET_SETTINGS;
                                    
             Registry registry = new Registry();    
           
//           long proxyEnable = registry.getCurrentUserKeyValueDword(subkey, PROXY_ENABLE);
//            
//           if (proxyEnable == 0)
//           {
//               // Nothing to do ==> No active proxy settings
//               return;
//           }              
            
            // 2) Get The HTTP & HTTPS Proxy Address & Port
            
            String proxyServer = registry.getCurrentUserKeyValue(subkey, PROXY_SERVER);
                        
            if (proxyServer == null)
            {
                return;
            }
            
            //debug("proxyServer: " + proxyServer + ":");
            
            // Test if there is a multi protocol string 
            // as "ftp=192.168.1.20:3128;http=192.168.1.20:3128;https=192.168.1.20:3128"
                       
            if (proxyServer.indexOf("=") != -1)
            {
                // We have a multi protocole chain     
                String proxyServerHttp  = extractInetSocketAddress(proxyServer, "http");
                proxyAddress   = proxyServerHttp.substring(0, proxyServerHttp.indexOf(":"));
                String strProxyPort = proxyServerHttp.substring(proxyServerHttp.indexOf(":") + 1);
                proxyPort = Integer.parseInt(strProxyPort);
                
                proxyServerHttp  = extractInetSocketAddress(proxyServer, "https");
                secureProxyAddress   = proxyServerHttp.substring(0, proxyServerHttp.indexOf(":"));
                strProxyPort = proxyServerHttp.substring(proxyServerHttp.indexOf(":") + 1);
                secureProxyPort = Integer.parseInt(strProxyPort);                
            }
            else
            {
                // There may be no proxy
                if (proxyServer.indexOf(":") != -1)
                {
                    proxyAddress   = proxyServer.substring(0, proxyServer.indexOf(":"));
                    String strProxyPort = proxyServer.substring(proxyServer.indexOf(":") + 1);
                    proxyPort = Integer.parseInt(strProxyPort);
                    secureProxyAddress = proxyAddress;
                    secureProxyPort = proxyPort;                       
                }             
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
     * Extract a proxy addr from a formated string as :
     *  "ftp=192.168.1.20:3128;http=192.168.1.20:3128;https=192.168.1.20:3128"
     * 
     * @param chain             the format chain
     * @param protocol          the protocol. "http" or "https" only.
     * 
     * @return the proxy addr in 192.168.1.20:3128 format if found, else null
     */
    private String extractInetSocketAddress(String chain, String protocol)
    {
        if (chain == null)
        {
            throw new IllegalArgumentException("chain can't be null!");
        }
        
        if (protocol == null)
        {
            throw new IllegalArgumentException("protocol can't be null!");
        }
        
        if (! protocol.equals("http") && ! protocol.equals("https"))
        {
            throw new IllegalArgumentException("Invalid protocol! Must be \"http\" or  \"https\" only. Value is: " + protocol);
        }
                
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
     * @return the secureProxyAddress
     */
    public String getSecureProxyAddress()
    {
        return secureProxyAddress;
    }


    /**
     * @return the secureProxyPort
     */
    public int getSecureProxyPort()
    {
        return secureProxyPort;
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
        HttpProxyDetector proxyDetector = new WindowsHttpProxyDetector();
            
        System.out.println("");
        System.out.println("getProxyAddress()       :" + proxyDetector.getProxyAddress() + ":");
        System.out.println("getProxyPort()          :" + proxyDetector.getProxyPort() + ":");
        
        System.out.println("getSecureProxyAddress() :" + proxyDetector.getSecureProxyAddress() + ":");
        System.out.println("getSecureProxyPort()    :" + proxyDetector.getSecureProxyPort() + ":"); 
        
    }
    
}
