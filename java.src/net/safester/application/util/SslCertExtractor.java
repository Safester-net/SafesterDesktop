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
package net.safester.application.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.awakefw.commons.api.client.HttpProxy;
import org.awakefw.commons.api.server.util.Sha1;

import net.safester.application.util.proxy.SimpleAuthenticator;

public class SslCertExtractor
{
    /** The ssl host */
    private String host = null;
    
    /** The http proxy to use for access to host */
    private HttpProxy httpProxy = null;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception{
        HttpProxy httpProxy = new HttpProxy("127.0.0.1", 8081, "npomereu", "******");
        SslCertExtractor sslCertExtractor = new SslCertExtractor("https://www.cgeep.com", httpProxy);
        Map<String, String> map = sslCertExtractor.getCertInfo();

        System.out.println(map);
    }
    /**
     * 
     * Constructor
     * 
     * @param host          The ssl host 
     * @param httpProxy     The http proxy to use for access to host
     */
    public SslCertExtractor(String host, HttpProxy httpProxy)
    {
        if (host == null)
        {
            throw new IllegalArgumentException("host can not be null!");
        }
        
        this.host = host;
        this.httpProxy = httpProxy;
    }

    /**
     * @return  the list of X509 certificates for this host
     * @throws IOException
     */
    public List<X509Certificate> getX509Certificates() throws IOException
    {
        InputStream in = null;
        
        try
        {
                                   
            URL url = new URL(host);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();

            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setUseCaches(false);

            if (httpProxy != null && httpProxy.getUsername() != null)
            {                
                //String authentication = httpProxy.getUsername()  + ":" + httpProxy.getPassword();
                //String encodedPassword = "Basic " + Base64.byteArrayToBase64(authentication.getBytes());
                //httpsURLConnection.setRequestProperty("Proxy-Authorization", encodedPassword);
                
                 Authenticator.setDefault(new SimpleAuthenticator(
                        httpProxy.getUsername(), httpProxy.getPassword()));                
            }
            
            in = httpsURLConnection.getInputStream();
                    
            Certificate [] certArray = httpsURLConnection.getServerCertificates();
            
            List<X509Certificate> x509Certificates = new Vector<X509Certificate>();
            for (Certificate certificate : certArray)
            {
                if (certificate instanceof X509Certificate)
                {
                    x509Certificates.add((X509Certificate) certificate);
                }
            }
            
            return x509Certificates;
        }
        finally
        {
           IOUtils.closeQuietly(in);
        }
    }
    
    /**
     * Extract in a clean HashMap all the important info of a X509 Certificate.
     * <br>
     * To be used in a JTable, for example
e
     * @return a clean HashMap with all the important info of a X509 Certificate.
     * @throws IOException 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws CertificateEncodingException 
     */
    public Map<String, String> getCertInfo() 
            throws IOException, CertificateEncodingException, NoSuchAlgorithmException, NoSuchProviderException
    {
        // get the first cert info
        List<X509Certificate> x509Certificates = this.getX509Certificates();
        X509Certificate x509Certificate = x509Certificates.get(0);

        Map<String, String> map = new LinkedHashMap<String, String>();
        
        StringReader stringReader = new StringReader(x509Certificate.getPublicKey().toString());
        BufferedReader br = new BufferedReader(stringReader);
        String keyLength = br.readLine();
        keyLength = StringUtils.substringAfter(keyLength, ", ");
        
        Sha1 sha1 = new Sha1();
        
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
              
        map.put("Version",              "" + x509Certificate.getVersion());
        map.put("Serial Number",        formatHexaString(x509Certificate.getSerialNumber().toString(16)));        
        map.put("Signature Algorithm ", new String(x509Certificate.getSigAlgName()));
        map.put("Issuer",               "" + x509Certificate.getIssuerX500Principal());
        map.put("Validity From",        df.format(x509Certificate.getNotBefore()));
        map.put("Validity To",          df.format(x509Certificate.getNotAfter()));        
        map.put("Subject",              "" + x509Certificate.getSubjectX500Principal());                             
        map.put("Public Key",           (x509Certificate.getPublicKey().getAlgorithm() + " (" + keyLength + ")"));
        map.put("Fingerprint Algorithm", "Sha1");
        map.put("Fingerprint",          formatHexaString(sha1.getHexHash(x509Certificate.getEncoded())));
        
        return map;
    }
        
    /**
     * Format with spaces each two chars an hexa string
     * @param hexa  the hexa string to format
     * @return  the formated hexa string
     */
    private String formatHexaString (String hexa)
    {
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < hexa.length(); i++)
        {
            sb.append(hexa.charAt(i));            
            if ((i + 1) % 2 == 0) sb.append(" ");
        }
        
        return sb.toString();
    }
        
}

