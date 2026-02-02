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

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import com.safelogic.utilx.Debug;


/**
 * @author Nicolas de Pomereu
 * Class that detext a Proxy Address & Port.
 */
public class JavaProxyDetector implements HttpProxyDetector
{
    /** The debug flag */ 
    protected boolean DEBUG = Debug.isSet(this);
    
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
    public JavaProxyDetector()
    {
        boolean useSystemProxy = UserPreferencesManager.getUseProxy();
                
        // Detect proxy only if user has decided to:
        if (useSystemProxy)
        {
            detectProxy();
        }
        
    }
    
   

    /**
     * Detect if there is a System Proxy (defined in IE) and set the values in memory 
     *
     */
    private void detectProxy()
    {
                
        try
        {
            System.setProperty("java.net.useSystemProxies", Boolean.toString(true));
        }
        catch (RuntimeException e)
        {
            JOptionPaneCustom.showException(null, e, "Impossible to use the System Proxy: ");     
            return;
        }
                
        
        List l = null;
        int i = 0;
                
        try
        {
            l = ProxySelector.getDefault().select(new URI("http://www.cgeep.com"));
        }
        catch (URISyntaxException e)
        {
            throw new IllegalArgumentException(e);
        }
        
        i = 0;
        
        if (true) return;

        for (Iterator iter = l.iterator(); iter.hasNext(); ) 
        {            
            Proxy proxy = (Proxy) iter.next();
            
            debug(i + " proxy hostname : " + proxy.type());
            
            InetSocketAddress addr = (InetSocketAddress)
                proxy.address();
            
            if(addr == null) 
            {                
                debug(i + " No Proxy");     
                proxyAddress = null;
                proxyPort = 0;
            } 
            else 
            {                
                debug(i + " proxy hostname : " + addr.getHostName());                
                debug(i + " proxy port     : " + addr.getPort());
                
                proxyAddress = addr.getHostName();
                proxyPort = addr.getPort();
            }
            
            i++;
        }
            
        /*
        try
        {
            l = ProxySelector.getDefault().select(new URI("https://www.google.com/"));
        }
        catch (URISyntaxException e)
        {
            throw new IllegalArgumentException(e);
        }
        
        i = 0;
        
        for (Iterator iter = l.iterator(); iter.hasNext(); ) 
        {            
            Proxy proxy = (Proxy) iter.next();
            
            debug(i + " proxy hostname : " + proxy.type());
            
            InetSocketAddress addr = (InetSocketAddress)
                proxy.address();
            
            if(addr == null) 
            {                
                debug(i + " No Proxy");     
                secureProxyAddress = null;
                secureProxyPort = 0;
            } 
            else 
            {                
                debug(i + " secure proxy hostname : " + addr.getHostName());                
                debug(i + " secure proxy port     : " + addr.getPort());
                
                secureProxyAddress = addr.getHostName();
                secureProxyPort = addr.getPort();
            }
            
            i++;
        }        
        */
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
//        try {
//          ProxyInfo info[] = ProxyService.getProxyInfo(new URL("https://www.cgeep.com"));
//          if(info != null && info.length>0)
//          {
//            String proxyHost = info[0].getHost();
//            int proxyPort = info[0].getPort();
//            System.out.println("PROXY = " + proxyHost + ":" + proxyPort);
//          }
//        }catch (Exception ex) {
//            System.err.println(
//            "could not retrieve proxy configuration, attempting direct connection.");
//            
//        }
//        
//        HttpProxyDetector proxyDetector = new JavaProxyDetector();
//                
//        System.out.println("getProxyAddress(): " + proxyDetector.getProxyAddress());
//        System.out.println("getProxyPort()   : " + proxyDetector.getProxyPort());
//        System.out.println("");
//        
//        System.out.println("getSecureProxyAddress(): " + proxyDetector.getSecureProxyAddress());
//        System.out.println("getSecureProxyPort()   : " + proxyDetector.getSecureProxyPort()); 
//        
    }
    
}
