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
package net.safester.clientserver;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 *
 * @author Alexandre Becquereau
 */
public class ServerParms {

    /**
     * Servlet call parameters
     */
    private static final String DEFAULT_HOST_URL = "https://www.runsafester.net";
    private static final String AWAKE_SQL_SERVER = "AwakeSqlServer";
    private static final String MASTER_KEY_ID = "contact@safelogic.com";

    /**
     * Contact parameters
     */
    public static final String CONTACT_WEB = "www.safester.net";
    public static final String CONTACT_EMAIL = "contact@safester.net";


    /**
     * @return the HOST
     */
    public static String getHOST() {

        String host = forceHostFromFile();

        if (!host.equals(DEFAULT_HOST_URL)) {
            System.out.println("WARNING! Using Test Host: " + host);
        }

        return host;
    }

    /**
     * Force a new host as content of the file user.home/safester_host.txt if it
     * exists
     */
    private static String forceHostFromFile() {

        String home = System.getProperty("user.home");
        if (!home.endsWith(File.separator)) {
            home += File.separator;
        }

        String host = DEFAULT_HOST_URL;
        try {
            File safesterNoSslTxt = new File(home + "safester_host.txt");
            if (safesterNoSslTxt.exists()) {
                host = FileUtils.readFileToString(safesterNoSslTxt);
            }
        } catch (IOException iOException) {
            throw new IllegalArgumentException(iOException);
        }

        if (host != null) {
            host = host.trim();
        }
        return host;
    }

    /**
     * @return the full AwakeSqlController path
     */
    public static String getAwakeSqlServerUrl() {

        String url = getHOST();
        if (!url.endsWith("/")) {
            url += "/";
        }

        url += AWAKE_SQL_SERVER;

        return url;
    }


    public static String getMasterKeyId() {
        return MASTER_KEY_ID;

    }

    public static int getMasterKeyUserNumber() {
        return 1;
    }

}
