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
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.http.conn.HttpHostConnectException;
import org.awakefw.commons.api.client.InvalidLoginException;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;


/**
 * @author Nicolas de Pomereu
 *
 */
public class RemoteDirectoryTreeBuilder
{
    /** The ServerCallerNew instance */
    private AwakeFileSession awakeFileSession = null;
    
    private boolean simulationModeLocal = false;
    
    /**
     * @param connection    the jdbc connection should be a ConnectionHttp instance
     * @param racine        the DefaultMutableTreeNode racine
     */
    public RemoteDirectoryTreeBuilder(Connection connection,  DefaultMutableTreeNode racine)
        throws  HttpHostConnectException, 
        ConnectException,
        IllegalArgumentException,
        UnknownHostException,
        InvalidLoginException, 
        IOException,
        SecurityException
    {
        if (connection == null)
        {
            throw new IllegalArgumentException("Connection can\'t be null");
        }
        
        if (connection instanceof AwakeConnection)
        {
            awakeFileSession = ((AwakeConnection)connection).getAwakeFileSession();
        }
        else
        {
            simulationModeLocal = true; // We are now in Simulation Mode
        } 
        
        // on recupere les lecteurs
        //_roots = File.listRoots();
        List<String>_roots = listUserRemoteRoots();
         
        // pour chaque lecteur
        for (int i = 0; i < _roots.size(); i++)
        {
            // on recupere son contenu grace a getSubDirs
            DefaultMutableTreeNode root = getSubDirs(_roots.get(i));
            // et on l ajoute a notre premier noeud
            racine.add(root);
        }        
    }    
    
    /**
     * Methode recursive permettant de recuperer tous les fichiers et sous
     * dossiers d un autre
     * 
     * @param root
     *            un File qui represente le lecteur ou le repertoire de depart
     * @return DefaultMutableTreeNode
     */
    public DefaultMutableTreeNode getSubDirs(String root)
        throws  HttpHostConnectException, 
        ConnectException,
        IllegalArgumentException,
        UnknownHostException,
        InvalidLoginException, 
        IOException,
        SecurityException
    {
        // on cree un noeud
        DefaultMutableTreeNode racine = new DefaultMutableTreeNode(root, true);

        // on recupere la liste des fichiers et sous rep
        List<String> list  = getDirectoriesRemote(root);
                
        if (list != null)
        {                    
            // pour chaque sous rep on appel cette methode => recursivite
            for (int j = 0; j < list.size(); j++)
            {
                DefaultMutableTreeNode file = getSubDirs(list.get(j));
                racine.add(file);           
            }                       
        }

        return racine;
    }

    /**
     * @return true if the user is in simulaiton mode
     */
    private boolean isSimulationModeLocal()
    {
        return simulationModeLocal;
    }
        
    /**
     * Get the sub-directories of the file denoted by the passed string
     * @param root      the root directory as a a string
     * @return the sub-directories of the file denoted by the passed string
     * @throws IOException 
     * @throws InvalidLoginException 
     * @throws UnknownHostException 
     * @throws IllegalArgumentException 
     * @throws ConnectException 
     * @throws HttpHostConnectException 
     */
    public List<String> getDirectoriesRemote(String root) 
            throws  HttpHostConnectException, 
                    ConnectException,
                    IllegalArgumentException,
                    UnknownHostException,
                    InvalidLoginException, 
                    IOException,
                    SecurityException
    {               
        if (isSimulationModeLocal())
        {
            return getDirectoriesLocal(root);
        }
            
        List<String> subDires = awakeFileSession.listDirectories(root);
        return subDires;        
    }    
    
    /**
     * @return  the list of direct sub-directories on the user root
     */
    public List<String> listUserRemoteRoots()
            throws  HttpHostConnectException, 
                    ConnectException,
                    IllegalArgumentException,
                    UnknownHostException,
                    InvalidLoginException, 
                    IOException,
                    SecurityException
    {
        List<String> files = null;
        
        if (isSimulationModeLocal())
        {
            files = new Vector<String>();
            files.add("c:\\");
        }
        else
        {   
            files = new Vector<String>();
            List<String> remoteFiles = getDirectoriesRemote("c:\\");
            files.addAll(remoteFiles);
        }
               
        return files;
    }
    
    
    /**
     * Get the sub-directories of the file denoted by the passed string
     * @param root      the root directory as a a string
     * @return the sub-directories of the file denoted by the passed string
     */
    public List<String> getDirectoriesLocal(String root)
    {
        List<String> list = new Vector<String>();
                
        File[] listAsFile = new File(root).listFiles();
        
        if (listAsFile == null) return null;
        
        for (int i = 0; i < listAsFile.length; i++)
        {
            if (listAsFile[i].isDirectory())
            {
                list.add(listAsFile[i].toString());                
            }
        }
                           
        return list;
    }
 
    

    
    
}
