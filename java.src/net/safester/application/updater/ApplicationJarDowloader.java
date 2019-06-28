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
package net.safester.application.updater;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import net.safester.application.engines.MultipleUrlDownloaderEngine;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.socket.client.SocketClient;
import net.safester.application.util.JOptionPaneNewCustom;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.awakefw.commons.api.client.DefaultAwakeProgressManager;
import org.awakefw.file.api.client.AwakeFileSession;

import com.safelogic.pgp.api.util.crypto.Sha1;

/**
 * Install all jars to specified installation directory
 * @author Alexandre Becquereau
 */
public class ApplicationJarDowloader {

    public static boolean DEBUG = false;
    
    private AwakeFileSession awakeFileSession = null;

    /** The destination install dir*/
    private String installationDir = null;

    private String email = null;
    private char[] passphrase = null;
    
    /**
     * Constructor
     * @param destinationDir            target installation dir
     * @param httpProxy                 the http parameters for this session
     */
    public ApplicationJarDowloader(AwakeFileSession awakeFileSession, String installationDir, String theEmail, char[] thePassphrase)
        throws MalformedURLException
    {

        if (awakeFileSession == null) {
            throw new IllegalArgumentException("awakeFileSession cannot be null");
        }
        
        if (installationDir == null) {
            throw new IllegalArgumentException("installationDir cannot be null");
        }
        
        this.awakeFileSession = awakeFileSession;
        this.installationDir = installationDir;
        this.email = theEmail;
        this.passphrase = thePassphrase;

    }

    /**
     * Download and install all jars
     * @throws Exception
     */
    public void install() throws Exception {

        debug(new Date() + " Starting install...");

        RemoteSoftwareInfo remoteSoftwareInfo = new RemoteSoftwareInfo(awakeFileSession);

                // Not necessary... Give order of magnitude
        //int downloadLength = (int) remoteSoftwareInfo.getDownloadLength();
        int downloadLength = 18 * 1024 * 1024;

        String libDir = this.installationDir;

        libDir += "lib" + File.separator;
        File libDirFile = new File(libDir);

        boolean created = libDirFile.mkdirs();
        if (!libDirFile.exists()) {
            throw new IOException("Impossible to create the sub-directory: " + libDir);
        }

        // Delete all temp file. Can throw an Exception
        deleteTmpFilesInLib(libDirFile);

        List<URL> urlsToDownload = new ArrayList<URL>();

        Map<String, String> remoteFilesAndHash = remoteSoftwareInfo.getFilesAndHash();
        Set<String> set = remoteFilesAndHash.keySet();

        // Build the list of new jars. List wil be used bu laucher to delete old unused jars (that may cause conflict with new jars)
        //createFileOfNewJars(set);

        List<String> remoteFiles = new ArrayList<String>(set);

        debug(new Date() + " Remote Files: " + remoteFiles.toString());

        String urlHost = awakeFileSession.getUrl();
        urlHost = StringUtils.substringBeforeLast(urlHost, "/");

        if (DEBUG)
        {
            JOptionPane.showMessageDialog(null, "urlHost: " + urlHost);
        }

        for (String jar : remoteFiles) {

            String localFile = installationDir + File.separator + "lib" + File.separator + jar;
            String localHash = getLocalFileHash(localFile);
            
            String remoteHash = remoteFilesAndHash.get(jar);

            // Add the Jar only if hash is different
            if ( localHash == null || ! localHash.equals(remoteHash))
            {
                debug("");
                debug("Local file : " + localFile);
                debug("Local hash : " + localHash);
                debug("Remote hash: " + remoteHash);
            
                URL urlJarLib = new URL(urlHost + "/newstart/lib/" + jar);
                urlsToDownload.add(urlJarLib);
            }
        }

        // Kill the running SocketServer! (Will not throw an Exception)
        if (Parms.FEATURE_CACHE_PASSPHRASE)
        {
            SocketClient socketClient = new SocketClient();
            socketClient.closeServerSilent();
        }

        URL url = null;

        boolean bypassLauncherJar = false;
        bypassLauncherJar = doBypassLauncherJar();
        
        // Launcher may be updated too (small file)
        if (bypassLauncherJar)
        {
            JOptionPane.showMessageDialog(null, "WARNING: download of SafesterLauncher.jar is bypassed!");
        }
        else
        {
            url = new URL(urlHost + "/newstart/" + InstallParameters.SAFESTER_LAUNCHER_JAR);
            urlsToDownload.add(url);
        }
        
        // Always Download - at last - the main jar SafeShareIt.jar if version is new
        url = new URL(urlHost + "/newstart/" + InstallParameters.SAFESTER_JAR);
        urlsToDownload.add(url);

        debug(new Date() + " Done: MultipleUrlDownloaderEngine starting...");

        DefaultAwakeProgressManager defaultAwakeProgressManager = new DefaultAwakeProgressManager();

        MultipleUrlDownloaderEngine urlDownloaderEngine = new MultipleUrlDownloaderEngine(
                awakeFileSession,
                installationDir,
                defaultAwakeProgressManager,
                urlsToDownload,
                downloadLength);

        urlDownloaderEngine.start();

       // while (defaultAwakeProgressManager.getProgress() < 100) {
         while (urlDownloaderEngine.getReturnCode() == MultipleUrlDownloaderEngine.RC_RUNNING) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(UrlFileDownloader.class.getName()).log(Level.SEVERE, null, ex);
            }
            UrlFileDownloader.setProgress(defaultAwakeProgressManager.getProgress());
            //  System.out.println("Download progress: " + progress);
        }

        Exception e = urlDownloaderEngine.getException();
        if (e != null) {
            e.printStackTrace();
        }

        UrlFileDownloader.setProgress(100);

        MessagesManager messages = new MessagesManager();
        JOptionPane.showMessageDialog(null, messages.getMessage("safester_has_been_updated"));
 
        try{
            launchJar(email, passphrase);
        }
        catch(Exception e2){
            JOptionPaneNewCustom.showException(null, e2, 
                    "Impossible to launch jar " + InstallParameters.SAFESTER_LAUNCHER_JAR);
            System.exit(0);
        }
        
        System.out.println("OK " + System.currentTimeMillis());
    }

    /**
     * @return true if the user.home/safester_bypass_launcher_jar.txt exists
     */
    private boolean doBypassLauncherJar() {
        boolean bypassLauncherJar;
        String home = System.getProperty("user.home");
        if (!home.endsWith(File.separator)) {
            home += File.separator;
        }
        File safesterBypassLauncherJarTxt = new File("home" + "safester_bypass_launcher_jar.txt");
        bypassLauncherJar = safesterBypassLauncherJarTxt.exists();
        return bypassLauncherJar;
    }

    /**
     * Compute the local file hash value
     * @param localFile     the local file 
     * @return  hash value or null if the local file dor not exists
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private String getLocalFileHash(String localFile)
        throws NoSuchProviderException, IOException, NoSuchAlgorithmException {
        
        if (! new File(localFile).exists())
        {
            return null;
        }

        Sha1 sha1 = new Sha1();
        String localHash = sha1.getHexFileHash(localFile);
        return localHash;
    }

    /**
     * Delete the temporary files
     * @param libDirFile        the file /lib subdirectory
     */
    private void deleteTmpFilesInLib(File libDirFile) throws IOException
    {
        File[] files = libDirFile.listFiles();

        if (files == null)
        {
            return;
        }

        for (File file : files) {
            if (file.getName().toLowerCase().endsWith(".tmp"))
            {
                FileUtils.forceDelete(file);
            }
        }
    }

    /**
     * Launcher start
     * @param email
     * @param thePassphrase
     * @throws IOException
     */
    public void launchJar(String email, char[] thePassphrase)
    throws IOException
    {
        String programName  = InstallParameters.getJavaExecutablePath();
        String cpParam      = "-cp";
        String cpValue      = installationDir + InstallParameters.SAFESTER_LAUNCHER_JAR;
        String mainClass    = "net.safester.application.updater.launcher.Launcher";

        if (SystemUtils.IS_OS_WINDOWS)
        {
            cpValue = "\"" + cpValue + "\"";
        }

        if (DEBUG)
        {
            String commandLine = cpParam + " " + cpValue + " " + mainClass;
            JOptionPane.showMessageDialog(null, "programName: " + programName);
            JOptionPane.showMessageDialog(null, "commandLine: " + commandLine);
            System.out.println("programName: " + programName);
            System.out.println("commandLine: " + commandLine);
        }

        if (email == null || thePassphrase == null)
        {
            Process p = Runtime.getRuntime().exec(new String[]{programName, cpParam, cpValue, mainClass});
        }
        else
        {
            Process p = Runtime.getRuntime().exec(new String[]{programName, cpParam, cpValue, mainClass, email, new String(thePassphrase)});
        }
       

        System.exit(0);
    }    
    /**
     * Launch update of jar files
     */

    /*
    private void launchDetachedUpdate(){
        try{
            InstallUpdater installUpdater = new InstallUpdater(installationDir);
            installUpdater.launchJar(email, passphrase);
        }
        catch(IOException e){
            JOptionPaneNewCustom.showException(null, e, "Impossible to launch jar");
            System.exit(0);
        }
    }
    */

     public static void debug(String str) {
        if (DEBUG)
            System.out.println("dbg> " + str);
    }


//     /**
//      * Creates the file that contains the list of the new files
//      * @param libDir           the installation directory
//      * @param newFilesSet      the remote files set
//      */
//    private void createFileOfNewJars(Set<String> newFilesSet)
//        throws IOException
//    {
//        TreeSet ts = new TreeSet(newFilesSet); // Alphabetical order
//
//        List<String> newFiles = new ArrayList<String>(ts);
//        
//        File safesterNewJarFilesTxt = new File( installationDir +  "safester_new_jar_files.txt");
//
//        if (safesterNewJarFilesTxt.exists())
//        {
//            safesterNewJarFilesTxt.delete();
//        }
//
//        PrintWriter pw = null;
//
//        try {
//            pw = new PrintWriter(safesterNewJarFilesTxt);
//
//            for (String filename : newFiles) {
//
//                // Jars only
//                if (filename.endsWith(".jar"))
//                {
//                   pw.println(filename); // no path
//                }
//
//            }
//        } finally
//        {
//            IOUtils.closeQuietly(pw);
//        }
//    }

}
