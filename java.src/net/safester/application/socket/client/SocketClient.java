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
package net.safester.application.socket.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import net.safester.application.updater.InstallParameters;
import net.safester.application.util.UserPrefManager;

import org.apache.commons.lang3.StringUtils;
import org.awakefw.file.util.convert.Pbe;


/**
 * @author RunningLiberty
 *
 */
public class SocketClient
{
    
    /** debug infos */
    public static boolean DEBUG = true;

    /** Buffered reader to read content from Socket*/
    private BufferedReader socket_in = null;

    /** PrintWriter into which we send out content */
    private PrintWriter socket_out = null;

    /** The client socket to use */
    private Socket socket = null;

    /** The installation dir of the socket server jar */
    private String installationDir = null;

    private String secret = "ThisIs*$ASecretsTriNg345";
    
    
    /**
     * @param args
     */
    public static void main(String[] args)   throws Exception
    {        
        // Ok, new install of modified jars
        //String installationDir = InstallParms.getInstallationDir();

        String installationDir = "I:\\SafeShareIt\\dist";
        if(!installationDir.endsWith(File.separator)){
            installationDir += File.separator;
        }

        int action = 1;

        if (args.length == 1)
        {
            action = Integer.parseInt(args[0]);
        }
       
        SocketClient socketClient = new SocketClient();

        if (action == 1)
        {
            socketClient.startSocketServerNoWait();
        }
        else  if (action == 2)
        {
            socketClient.setRemotePassphrase("the passphrase in Socket Server *$".toCharArray());
        }
        else if (action == 3)
        {
            char [] passphrase = socketClient.getRemotePassphrase();

            if (passphrase != null)
            {
                System.out.println(new String(passphrase));
            }
            else
            {
                System.out.println("passphrase is null!");
            }
        }
        else if (action == 4)
        {
            socketClient.closeServerSilent();
            System.out.println("MultiServert Shut Down.");
        }

        System.out.println("Done!");
    }

    /**
     * Constructor
     * @throws FileNotFoundException        it he installation dir doe
     */
    public SocketClient()
    {
        
    }
    
    /**
     * Constructor
     * @param String installationDir    the SafeShare Installation directory
     * 
     * @throws IOException 
     * @throws UnknownHostException 
     * 
     */
    public SocketClient(String installationDir) throws FileNotFoundException
    {
        if (installationDir == null)
        {
            throw new IllegalArgumentException("installationDir can not be null!");
        }

        if (! new File(installationDir).exists())
        {
            throw new FileNotFoundException("installationDir does not exists: " + installationDir);
        }
        
        this.installationDir = installationDir;        
    }

    /**
     *
     * @return  true id the user has chosen to cache passphrase
     */
    private boolean doCachePassphrase() {

        boolean doCachePaspshrase = false;
        doCachePaspshrase = UserPrefManager.getBooleanPreference(UserPrefManager.DO_CACHE_PASSPHRASE);

        return doCachePaspshrase;
    }

    /**
     * Start he remote socket server without waiting
     */
    public void startSocketServerNoWait()
    {
        if (! doCachePassphrase() )
        {
            return;
        }
        
        // Start a thread to update perpetually the running date + port number
        Thread t = new Thread() 
        {
            @Override
            public void run() 
            {                
                try
                {
                    boolean socketServerRunning = isSocketServerRunning();
                    
                    // Start the serve programm if it's not running
                    if (! socketServerRunning)
                    {
                        launchSocketServer();
                    }
                }
                catch (IOException e)
                {
                    // log the error
                    System.out.println("MultiServer: impossible to start from SocketClient. Reason: " 
                            + e.toString());
                    e.printStackTrace();
                }               
            }
        };
        t.start();        
    }
    
    /**
     * 
     * get the passphrase stored in/by socket server. null if Socket Server is not started.
     * 
     * @return the passphrase stored in/by socket server
     * @throws IOException
     */
    public char [] getRemotePassphrase()
    throws IOException
    {
        if (! doCachePassphrase() )
        {
            return null;
        }
        
        boolean socketServerRunning = isSocketServerRunning();
        
        // Start the serve programm if it's not running
        if (! socketServerRunning)
        {
            // ok start the server in background
                        
            // Start a thread to update perpetually the running date + port number
            Thread t = new Thread() 
            {
                @Override
                public void run() 
                {                
                    try
                    {
                        launchSocketServer();
                    }
                    catch (IOException e)
                    {
                        // log the error
                        System.out.println("MultiServer: impossible to launch from SocketClient. Reason: " 
                                + e.toString());
                        e.printStackTrace();
                    }               
                }
            };
            t.start();
            
            // Immediate release  so that user does not wait
            return null;
        }
        
        // Ok Socket server is running. Create a client socket:
        int port = getSocketServerPort();
        connectToServer(port);
                
        socket_out.println(SoTag.PLEASE_SEND_PASSPHRASE);
        socket_out.flush();

        // get the Received string 
        String buffer = socket_in.readLine();
        buffer = buffer.trim();
        debug("buffer       : " + buffer);

        try
        {
            if (buffer.contains(SoTag.SORRY_NO_PASSPHRASE_IN_MEMORY))
            {
                return null;
            }
            else if (buffer.contains(SoTag.PASSPHRASE_IS))
            {
                String passphraseStr = buffer.substring(SoTag.PASSPHRASE_IS.length());
                try{
                    Pbe pbe = new Pbe();
                    passphraseStr = pbe.decryptFromHexa(passphraseStr, secret.toCharArray());
                    return passphraseStr.toCharArray();
                }catch(Exception e){
                   e.printStackTrace(System.err);
                    return null;
                }
                
            }
            else
            {
                throw new IllegalArgumentException("Socket Server protocol error. Invalid buffer: "
                        +  buffer);
            }
        }
        finally
        {
            //socket.close();
        }

    }
    
    /**
     * Sets a passphrase in memory. Immediate release (done in  thread)
     * @param passphrase        the passphrase to set
     */
    public void setRemotePassphrase(char[] passphrase)
    {
        if (! doCachePassphrase() )
        {
            return;
        }

        setRemotePassphraseInThread(passphrase);
    }    
    
    /**
     * Says it the Socket Server is already started
     * @return  true if the Socket Server is already started
     */
    private boolean isSocketServerRunning()
    {
        // Delete the SOCKET_SERVER_PORT preference
        UserPrefManager.removePreference(UserPrefManager.SOCKET_SERVER_PORT);
        
        int port = 0;        
        int cptLoop = 0;
        
        // If Socket Server is running, preference will be updated by the Socket Server
        // Try 3 times to get a value
        while (cptLoop++ < 3)
        {
            port = UserPrefManager.getIntegerPreference(UserPrefManager.SOCKET_SERVER_PORT);
            
            if (port != 0)
            {
                break;
            }
            try {Thread.sleep(200);} catch (InterruptedException ex) {}
        }
        
        if (port == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    /**
     * @return  the port to use for socket connection
     */
    private int getSocketServerPort()
    {
        int port = 0;        
        int cptLoop = 0;
        
        // If Socket Server is running, preference will be updated by the Socket Server
        // Try 3 times to get a value
        while (cptLoop++ < 3)
        {
            port = UserPrefManager.getIntegerPreference(UserPrefManager.SOCKET_SERVER_PORT);
            
            if (port != 0)
            {
                break;
            }
            try {Thread.sleep(33);} catch (InterruptedException ex) {} 
        }
        
        return port;
    }
    
        
    /**
     * Start the socket server in the specified directory
     * @throws IOException
     */
    private void launchSocketServer()
    throws IOException 
    {
        String programName = InstallParameters.getJavaExecutablePath();
        String cpParam = "-cp";

        if (installationDir == null)
        {
            installationDir = InstallParameters.getInstallationDir();

            if (! new File(installationDir).exists())
            {
                throw new FileNotFoundException("installationDir does not exists: " + installationDir);
            }
        }
       
        String cpValue = installationDir +  InstallParameters.SAFESTER_JAR;

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            cpValue = "\"" + cpValue + "\"";
        }

        String clazz = "net.safester.application.socket.server.MultiServer";

        debug("programName: " + programName);
        debug("cpParam    : " + cpParam);
        debug("cpValue    : " + cpValue);
        debug("class      : " + clazz);

        //java -cp "I:\SafeShareIt\dist\SafeShareIt.jar" net.safester.application.socket.server.MultiServer

        @SuppressWarnings("unused")
        Process p = Runtime.getRuntime().exec(new String[] {programName, cpParam, cpValue, clazz});
    }
    
    
    
    /**
     * @throws UnknownHostException
     * @throws IOException
     */
    private boolean connectToServer(int port) throws UnknownHostException, IOException
    {
        // Connect to the given machine 
        InetAddress addr = InetAddress.getByName("127.0.0.1");
        
        socket = new Socket(addr, port);

        socket_in = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));
        socket_out = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream())), true);
        
        return true;
    }


    /**
     * @param passphrase
     * @throws IllegalArgumentException
     * @throws IOException
     */
    private void setRemotePassphraseInThread(char[] passphrase)
    {
        final char [] passphraseFinal;
        try{
            Pbe pbe = new Pbe();
            passphraseFinal = pbe.encryptToHexa(new String(passphrase), secret.toCharArray()).toCharArray();
        }catch(Exception e){
            e.printStackTrace(System.err);
            return;
        }
        // Start a thread to update perpetually the running date + port number
        Thread t = new Thread() 
        {
            @Override
            public void run() 
            {                
                try
                {
                    boolean socketServerRunning = isSocketServerRunning();
                    
                    // Start the serve programm if it's not running
                    if (! socketServerRunning)
                    {
                        launchSocketServer();
                    }

                    int port = 0;
                    int cpt = 0;
                    while (port == 0)
                    {
                        // Exit loop only when server is started
                        port = getSocketServerPort();
                        try {Thread.sleep(100);} catch (InterruptedException ex) {}

                        cpt++;
                        if (cpt > 20000) // 20 seconds
                        {
                            break;
                        }
                    }

                    if (port == 0)
                    {
                        throw new IOException("MultiServer: Impossible to start Socket Server.");
                    }

                    // Ok Socket server is running. Create a client socket:
                    debug("port: " + port);
                    connectToServer(port);
                    
                    socket_out.println(SoTag.PASSPHRASE_IS + new String(passphraseFinal));
                    socket_out.flush();

                    debug("passphrase sent!");

                    String buffer = socket_in.readLine();                    
                    debug(buffer); // Should be "OK".. Will test later
                }
                catch (IOException e)
                {
                    // log the error
                    System.out.println("MultiServer: impossible to set remote passphrase Client. Reason: " 
                            + e.toString());
                    e.printStackTrace();
                }               
            }
        };
                
        t.start();  

    }

    /**
     * Closes the Socket Server
     * @throws IOException
     */
    private void closeServer() throws IOException
    {
        // Delete the SOCKET_SERVER_PORT preference
        UserPrefManager.removePreference(UserPrefManager.SOCKET_SERVER_PORT);

        int port = getSocketServerPort();

        // Nothing to do if server is stopped
        if (port == 0)
        {
            return;
        }

        connectToServer(port);

        socket_out.println(SoTag.BYE);
        socket_out.flush();

        String buffer = socket_in.readLine();
        debug(buffer); // Should be "OK".. Will test later

        // Delete the SOCKET_SERVER_PORT preference
        UserPrefManager.removePreference(UserPrefManager.SOCKET_SERVER_PORT);

        socket.close();
    }

    
    /**
     * Close the socket server and the socket
     *
     */
    public void closeServerSilent()
    {        
        try {
            closeServer();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } 
    
    /**
     * debug tool
     */
    private void debug(String s)
    {
        String myClass = StringUtils.substringAfterLast(this.getClass().getName() , "."); 
        if (DEBUG)
            System.out.println(
                    myClass
                    + " " 
                    + new java.util.Date() 
                    + " "
                    + s);
    }
    

}

/**
 * 
 */
