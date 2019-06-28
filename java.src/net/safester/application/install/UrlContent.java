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
package net.safester.application.install;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JFrame;

import org.apache.commons.io.IOUtils;

/**
 * Allows the download the content of an URL.
 *
 * @author Nicolas de Pomereu
 *
 */
public class UrlContent {

    private URL url = null;
    private Proxy proxy = null;
    private PasswordAuthentication passwordAuthentication;

    /**
     * Constructor.
     * @param url
     * @param proxy
     * @param passwordAuthentication 
     */
    public UrlContent(URL url, Proxy proxy, PasswordAuthentication passwordAuthentication) {
        super();
        this.url = url;
        this.proxy = proxy;
        this.passwordAuthentication = passwordAuthentication;
    }

    /**
     * Return into a string the content of an URL.
     * @return the content of an URL.
     * @throws IOException 
     */
    public String download() throws IOException {

        URLConnection urlConnection = null;

	if (proxy != null) {

	    Authenticator authenticator = new Authenticator() {
                @Override
		public PasswordAuthentication getPasswordAuthentication() {
		    return passwordAuthentication;
		}
	    };
	    Authenticator.setDefault(authenticator);

	    urlConnection = url.openConnection(proxy);
	} else {
	    urlConnection = url.openConnection();
	}

        urlConnection.setDoOutput(true);

        InputStream in = null;
        ByteArrayOutputStream out = null;

        in = urlConnection.getInputStream();
        out = new ByteArrayOutputStream();

        try {
            byte[] buf = new byte[20 * 10014];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            IOUtils.closeQuietly(out);
        }
        
        String text = out.toString();
        if (text != null) {
            text = text.trim();
        }
        
        return text;

    }

}
