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

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Map;

import org.awakefw.commons.api.client.InvalidLoginException;
import org.awakefw.commons.api.client.RemoteException;
import org.awakefw.file.api.client.AwakeFileSession;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * As it says... Allows to access the software files on host & get version, get files, etc.
 * 
 * @author Nicolas
 *
 */
public class RemoteSoftwareInfo
{
    /** The http parameters for the http session */
    private AwakeFileSession awakeFileSession = null;
    
    /**
     * Constructor
     * 
     * @param awakeFileSession
     */
    public RemoteSoftwareInfo(AwakeFileSession awakeFileSession)
    {

        if (awakeFileSession == null) {
            throw new IllegalArgumentException("awakeFileSession cannot be null");
        }
        
        this.awakeFileSession = awakeFileSession;
    }

    /**
     * 
     * @return  the SafeShareIt Software Info
     * 
     * @throws ConnectException
     * @throws IllegalArgumentException
     * @throws UnknownHostException
     * @throws InvalidLoginException
     * @throws RemoteMethodNotCallable
     * @throws IOException
     */
    public String getVersion() 
            throws ConnectException, IllegalArgumentException, UnknownHostException, 
                   InvalidLoginException, RemoteException, IOException,
                   SecurityException
    {
        String version = awakeFileSession.call("net.safester.server.VersionManager.getVersion");
        return version;
    }
    
    /**
     * 
     * @return  the files and the hash size of the files in a Map
     * 
     * @throws ConnectException
     * @throws IllegalArgumentException
     * @throws UnknownHostException
     * @throws InvalidLoginException
     * @throws RemoteMethodNotCallable
     * @throws IOException
     */
    public Map<String, String> getFilesAndHash() 
        throws ConnectException, IllegalArgumentException, UnknownHostException, 
               InvalidLoginException, RemoteException, IOException, SecurityException
    {
        String mapFilesAndHashStr = awakeFileSession.call("net.safester.server.VersionManager.getSoftwareFilesAndHash");

        Gson gsonOut = new Gson();
        Type mapFilesAndHashType = new TypeToken<Map<String, String>>(){}.getType();

        Map<String, String> mapFilesAndHash = gsonOut.fromJson(mapFilesAndHashStr, mapFilesAndHashType);    
        return mapFilesAndHash;
    }
    
    /**
     *
     * @return  the SafeShareIt Software total size
     *
     * @throws ConnectException
     * @throws IllegalArgumentException
     * @throws UnknownHostException
     * @throws InvalidLoginException
     * @throws AwakeFailureException
     * @throws RemoteMethodNotCallable
     * @throws IOException
     */
    public long getDownloadLength()
            throws ConnectException, IllegalArgumentException, UnknownHostException,
                   InvalidLoginException, RemoteException, IOException,
                   SecurityException
    {
        String downloadLength = awakeFileSession.call("net.safester.server.VersionManager.getDownloadLength");        
        return Long.parseLong(downloadLength);
    }
    
    
    
}
