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

import net.safester.application.wakeup.WakeupCallSender;
import java.awt.HeadlessException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.atlanticbb.tantlinger.shef.HTMLEditorPane;
import net.safester.application.addrbooknew.tools.ProcessUtil;

import org.apache.commons.lang3.SystemUtils;
import org.awakefw.file.api.client.AwakeFileSession;

import net.safester.application.installpolicy.PolicyInstallerV1;
import net.safester.application.mac.MacOsXFullPolicyFiles;
import net.safester.application.messages.LanguageManager;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.JOptionPaneHtml;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.application.util.SunUiScalingUtil;
import net.safester.application.util.UserPrefManager;
import net.safester.application.version.Version;
import net.safester.clientserver.ServerParms;

import net.safester.application.wakeup.*;

public class Safester {

    public static boolean DEBUG = false;

    public static final String CR_LF = System.getProperty("line.separator");
    private static boolean USE_SYNTHETICA = false;

    // Change to to true to suport Franch
    public static final boolean LANGUAGE_ENABLED = true;

    // Wakeup stuff
    public static final String PORT_FILE = Parms.getSafesterUserHomeDir() + File.separator +  "app_port.txt";
    public static ServerSocket serverSocket = null;
    
    /**
     * Safester main launcher
     *
     * @param args
     */
   
    
   public static void main(String[] args) {
        if (TestAnotherInstance.isAnotherInstanceRunning()) {
            System.out.println("Safester Start - Another instance running ==> endWakeUpCall()");
            WakeupCallSender.sendWakeUpCall();
            System.exit(0); // Exit the second instance
        } else {
            System.out.println("Safester Start - First start!");
            ServerSockerSetup.setupServerSocket();
            startWakeupListener();
            runApplication(); // This is where your app's main functionality starts
        }
    }
    
   private static void startWakeupListener() {
        new Thread(new WakeupListener()).start();
    }

    public static void runApplication() throws HeadlessException {
        try {
            String scaling = UserPrefManager.getPreference(UserPrefManager.SUN_SCALING, SunUiScalingUtil.SCALING_100);
            System.setProperty("sun.java2d.uiScale",  scaling); 
            
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            
            // take the menu bar off the jframe
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            //System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Safester");
            
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
            
            if (! isJavaVersion11mini()) {
                MessagesManager messagesManager = new MessagesManager();
                String message = messagesManager.getMessage("safester_requires_java_11_minimum");
                JOptionPane.showMessageDialog(null, message, "Safester", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
                        
            if (SystemUtils.IS_OS_WINDOWS && ProcessUtil.countWindowsInstanceRunning("Safester.exe") > 1) {
                MessagesManager messagesManager = new MessagesManager();
                String message = messagesManager.getMessage("safester_already_running_use_task_bar");
                JOptionPane.showMessageDialog(null, message, "Safester", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
            
            //doMain(args);
            Login login = new Login();
            login.setVisible(true);
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
     * Says if Java current major version is > 11
     * @return true if current major version is > 11
     */
    public static boolean isJavaVersion11mini() {
        String JAVA_11 = "11";
        String currentVersion =  SystemUtils.JAVA_VERSION;
        int compared = currentVersion.compareTo(JAVA_11);
        
        return compared >= 0;
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
