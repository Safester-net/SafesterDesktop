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

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.util.HtmlConverter;
import org.awakefw.sql.api.client.AwakeConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.safester.clientserver.specs.ListTransfer;
import net.safester.noobs.clientserver.FolderLocal;
import net.safester.noobs.clientserver.GsonUtil;


/**
 * @author Nicolas de Pomereu
 *
 * Class that allow to
 * <br> - get the list of FolderLocals from the Server  
 * <br> - put a list of FolderLocals on the Server.
 */

public class FolderListTransfer implements ListTransfer<FolderLocal>
{
    /** The debug flag */
    public static boolean DEBUG = false;
    
    /** The Jdbc connection */
    private Connection connection = null;

    /** The user number */
    private int userNumber = 0;
                    
    /**
     * Constructor
     * @param connection        the JDBC connection
     * @param userNumber        the user number
     */
    public FolderListTransfer(Connection connection, int userNumber)
    {
        if (connection == null)
        {
            throw new IllegalArgumentException("Connection can\'t be null");
        }
        
        this.connection = connection;
        this.userNumber = userNumber;
    }

   
    /* (non-Javadoc)
     * @see net.safester.clientserver.IServer#get()
     */
    @Override
    public List<FolderLocal> getList()
        throws SQLException
    {

        //Get the list from the server, because of intricated SQL statements
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        String methodRemote = "net.safester.server.FolderLocalList.get";
        debug("methodRemote: " + methodRemote);

        String jsonString;
        try
        {
            jsonString = awakeFileSession.call(methodRemote,
                                                userNumber,
                                                connection
                                                );
        }
        catch (Exception e)
        {
            throw new SQLException(e);
        }

        debug("jsonString: " + jsonString);
                
        List<FolderLocal> folderLocalList = GsonUtil.gsonToList(jsonString);

        // Escape HTML in names
        for (FolderLocal folderLocal : folderLocalList) {
            String name = folderLocal.getName();
            name = HtmlConverter.fromHtml(name);
            folderLocal.setName(name);
        }
        
        if (DEBUG)
        {
            for (FolderLocal folderLocal : folderLocalList) {
                System.out.println("folderLocal: " + folderLocal.toDisplayString());
           }
        }

        debug("before returning folderLocalList");
        
        return folderLocalList;
    }


    /**
     * @return the MAX(folder_id) for the user
     */
    public int getMaxFolderId() throws SQLException
    {
        int maxFolderId = 0;

        //Put the list on the server, because of intricated SQL statements        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
        
        try
        {
            String jsonString = awakeFileSession.call( "net.safester.server.hosts.newapi.FoldersNewApi.getMaxFolderId",
                                    userNumber,
                                    connection );
            
            maxFolderId = Integer.parseInt(jsonString);
            return maxFolderId;
        }
        catch (Exception e)
        {
            throw new SQLException(e);
        }
    }

    
    /* (non-Javadoc)
     * @see net.safester.clientserver.IServer#put(java.util.List)
     */
    @Override
    public void putList(List<FolderLocal> folderLocals) throws SQLException
    {
        // Set HTML in names (we don't want any accents on host SQL UTF-8)
        toHtml(folderLocals);
        
        String jsonString = GsonUtil.listToGson(folderLocals);
        
        //Put the list on the server, because of intricated SQL statements        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
        
        try
        {
            awakeFileSession.call( "net.safester.server.FolderLocalList.put",
                                    jsonString,
                                    userNumber,
                                    connection );
        }
        catch (Exception e)
        {
            throw new SQLException(e);
        }
    }


    /**
     * @param folderLocals
     */
    private void toHtml(List<FolderLocal> folderLocals)
    {
        // Set HTML in names (we don't want any accents on host SQL UTF-8)
        for (FolderLocal folderLocal : folderLocals) {
            String name = folderLocal.getName();
            name = HtmlConverter.toHtml(name);
            folderLocal.setName(name);
        }
    }

    /**
     * Same as putList, but for a Delete of a folder: folder id is needed, because server will delete
     * all user
     * @param folderLocals  the new folder list
     * @param the key id of the user (for security control)
     * @throws SQLException
     */
    public void putListAndDelete(List<FolderLocal> folderLocals, String keyId) throws SQLException
    {
        // Set HTML in names (we don't want any accents on host SQL UTF-8)
        toHtml(folderLocals);
        
        String jsonString = GsonUtil.listToGson(folderLocals);
        
        //Put the list on the server, because of intricated SQL statements        
        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();
        
        try
        {
            awakeFileSession.call( "net.safester.server.FolderLocalList.putAndDelete",
                                    jsonString,
                                    keyId,
                                    userNumber,
                                    connection );
        }
        catch (Exception e)
        {
            throw new SQLException(e);
        }
    }

   
    /**
     * Get the list of folder ids of a folder
     * @param rootFolderId      root folder
     * @param recurse           recurse children
     * @return                  all folder id that are children of root folder
     * @throws SQLException
     */
    public List<Integer> getAllChildren(int rootFolderId, boolean recurse)
    throws SQLException {
         
        debug("rootFolderId: " + rootFolderId);

        AwakeConnection awakeConnection = (AwakeConnection) connection;
        AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

        try {
            String returnString = awakeFileSession.call("net.safester.server.hosts.newapi.FoldersNewApi.getAllChildren",
                    userNumber,
                    rootFolderId,
                    recurse,
                    connection);
            
	Gson gsonOut = new Gson();
	Type type = new TypeToken<List<Integer>>() {
	}.getType();
	List<Integer> children= gsonOut.fromJson(returnString, type);
	return children;

        } catch (Exception ex) {
            throw new SQLException(ex);
        }
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

}
