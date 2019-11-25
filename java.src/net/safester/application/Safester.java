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
package net.safester.application;

import java.awt.HeadlessException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.atlanticbb.tantlinger.shef.HTMLEditorPane;

import org.apache.commons.lang3.SystemUtils;
import org.awakefw.file.api.client.AwakeFileSession;

import net.safester.application.installpolicy.PolicyInstallerV1;
import net.safester.application.mac.MacOsXFullPolicyFiles;
import net.safester.application.messages.LanguageManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.JOptionPaneHtml;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.version.Version;
import net.safester.clientserver.ServerParms;

public class Safester {

    public static boolean DEBUG = false;

    public static final String CR_LF = System.getProperty("line.separator");
    private static boolean USE_SYNTHETICA = false;

    // Change to to true to suport Franch
    public static final boolean LANGUAGE_ENABLED = true;

    /**
     * SafeShareIt main launcher
     *
     * @param args
     */
    public static void main(String[] args) {

        try {
            if (new File(SystemInit.getLOG_DIR() + File.separator + "debug.txt").exists()) {
                Main.DEBUG = true;
                MessageReader.DEBUG = true;
            }

            // Must be done at each language change
            HTMLEditorPane.setLanguage(LanguageManager.getLanguage());

            // Set User-Agent 
            System.setProperty("http.agent", SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " Safester " + Version.getVersionWithDate());

            //TODO: change when French available
            if (!LANGUAGE_ENABLED) {
                LanguageManager languageManager = new LanguageManager();
                LanguageManager.setLanguage("en");
                languageManager.storeLanguage();
            }

            // take the menu bar off the jframe
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            AwakeFileSession.setUseBase64EncodingForCall();

            System.out.println(System.getProperty("user.dir"));

            if (!System.getProperty("user.dir").startsWith("I:\\")) {
                SystemInit.redirectOutAndErr();
            }

            // SwingUtilities.invokeLater(new Runnable() {
            // @Override
            // public void run() {
            // setLookAndFeel();
            // }
            // });
            SafesterLookAndFeelManager.setLookAndFeel();

            doMain(args);
        } catch (Throwable t) {
            t.printStackTrace();

            if (DEBUG) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PrintWriter pw = new PrintWriter(bos);
                t.printStackTrace(pw);
                JOptionPane.showMessageDialog(null, bos.toString());
            }

            JOptionPane.showMessageDialog(null, "An error has occured: " + CR_LF
                    + t.getMessage() + CR_LF + CR_LF
                    + "Please go " + ServerParms.CONTACT_WEB + " to download and reinstall last version of Safester.",
                    "An error has occured... ", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

    }

    /**
     * Safester main launcher
     *
     * @param args
     */
    public static void doMain(String[] args) {

        /*
	if (Parms.FEATURE_CACHE_PASSPHRASE) {
	    // Start the socket server (if necessary if use wants to cache
	    // passphrase)
	    SocketClient socketClient = new SocketClient();
	    socketClient.startSocketServerNoWait();
	}
         */
        Login login = new Login();
        login.setVisible(true);

    }

    /**
     * Test if policy files can be copied if not display a detailed help message
     *
     * @throws HeadlessException
     */
    public static boolean testPolicyFile() throws HeadlessException {

        boolean isInstalled = false;

        try {
            if (SystemUtils.IS_OS_MAC_OSX) {
                isInstalled = testPolicyFilesMacOsX();
            } else {
                isInstalled = testPolicyFileWindowsAndLinux();
            }
        } catch (Exception e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "An error has occured. " + CR_LF
                    + "Please go to " + ServerParms.CONTACT_WEB + "  to download and reinstall last version of Safester.",
                    "An error has occured... ", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        return isInstalled;

    }

    /**
     * Test the full policy files installation on Mac OS X
     *
     * @return true if the files are correctly copied on java.home/lib/security
     * @throws HeadlessException
     */
    private static boolean testPolicyFilesMacOsX() {
        try {
            MacOsXFullPolicyFiles macOsXFullPolicyFiles = new MacOsXFullPolicyFiles();
            return macOsXFullPolicyFiles.tryToInstall();
        } catch (Exception e) {
            JOptionPaneNewCustom.showException(null, e);
            return false;
        }
    }

    /**
     * Test the full policy files installation on Windows and Linux
     *
     * @return true if the files are correctly copied on java.home/lib/security
     * @throws HeadlessException
     */
    private static boolean testPolicyFileWindowsAndLinux()
            throws HeadlessException {
        // boolean doCopyPolicyFiles =
        // UrlUtil.copyNonRestricedPolicyFilesToJavaHomeLibSecurity();

        boolean doCopyPolicyFiles = false;
        try {
            doCopyPolicyFiles = new PolicyInstallerV1().tryToInstall();
        } catch (Exception ex) {
            Logger.getLogger(Safester.class.getName()).log(Level.SEVERE, null,
                    ex);
            ex.printStackTrace();
        }

        if (!doCopyPolicyFiles) {
            String htmlFile = null;
            if (SystemUtils.IS_OS_WINDOWS) {
                htmlFile = "requires_login_as_administrator";
            } else {
                htmlFile = "requires_login_as_root";
            }

            String content = Help.getHtmlHelpContent(htmlFile);
            JFrame jframe = new JFrame();
            jframe.setIconImage(
                    Parms.createImageIcon(Parms.ICON_PATH).getImage());
            JOptionPaneHtml.showConfirmDialog(jframe, content, "Warning",
                    JOptionPane.CLOSED_OPTION);
            return false;
        }
        return true;
    }

}
