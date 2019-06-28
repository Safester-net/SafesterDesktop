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

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.util.UserPrefManager;
import net.safester.clientserver.ServerParms;

/**
 *
 * @author Nicolas de Pomereu
 */
public class NewVersionInstaller {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {

        String version = net.safester.application.version.Version.VERSION;
        JFrame jframe = null;
        NewVersionInstaller.checkIfNewVersion(jframe, version, false);

    }

    /**
     * Check if new version is to download, if yes redirect to download page
     *
     * @param jframe
     * @param currentVersion
     * @param silent the value of silent
     * @throws MalformedURLException
     * @throws IOException
     */
    public static void checkIfNewVersion(JFrame jframe, String currentVersion, boolean silent) throws MalformedURLException, IOException {

        Proxy proxy = getProxy();
        PasswordAuthentication passwordAuthentication = getPasswordAuthentication();
        
        System.out.println("proxy                 : " + proxy + ":");
        System.out.println("passwordAuthentication: " + passwordAuthentication + ":");
                 
        UrlContent urlContent = new UrlContent(new URL(ServerParms.getHOST() + "/download/version.txt"), proxy, passwordAuthentication);
        String serverVersion = urlContent.download();

        System.out.println("currentVersion: " + currentVersion + ":");
        System.out.println("serverVersion : " + serverVersion + ":");
        
        if (serverVersion.compareTo(currentVersion) > 0) {
            AskForDownloadJframe askForDownloadJframe = new AskForDownloadJframe(jframe, serverVersion);
            int result = askForDownloadJframe.getResult();

            if (result == JOptionPane.YES_OPTION) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    String installPage = ServerParms.getHOST() + "/install.html";
                    desktop.browse(new URI(installPage));
                    
                    //System.exit(0);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(jframe, e.toString());
                }
            }

        } else {
            
            if (silent) {
                return;
            }
            
            String message = MessagesManager.get("you_have_latest_version");
            message = message.replace("{0}", Parms.PRODUCT_NAME);
            message = message.replace("{1}", currentVersion);
            
            Icon icon = Parms.createImageIcon("images/files_2/48x48/recycle.png");
            JOptionPane.showMessageDialog(jframe, message, Parms.PRODUCT_NAME, JOptionPane.INFORMATION_MESSAGE, icon);
        }

    }

    public static Proxy getProxy() {
        
        String proxyHostname = UserPrefManager.getPreference(UserPrefManager.PROXY_ADDRESS);
        int proxyPort = UserPrefManager.getIntegerPreference(UserPrefManager.PROXY_PORT); 
        
	Proxy proxy = null;
	if (proxyHostname != null) {
	    proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
		    proxyHostname, proxyPort));
	}
	return proxy;
    }

    public static PasswordAuthentication getPasswordAuthentication() {
        String proxyAuthUsername = UserPrefManager.getPreference(UserPrefManager.PROXY_AUTH_USERNAME);
        String proxyAuthPassword = UserPrefManager.getPreference(UserPrefManager.PROXY_AUTH_PASSWORD);
        
        PasswordAuthentication passwordAuthentication = null;
        
        if (proxyAuthUsername != null) {
            passwordAuthentication = new PasswordAuthentication(proxyAuthUsername, proxyAuthPassword.toCharArray());
        }
        
        return passwordAuthentication;
    }

}
