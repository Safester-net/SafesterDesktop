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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nicolas de Pomereu
 * 
 * Cache for storing MessageLocalStore holders per folder id
 */

public class MessageLocalStoreCache
{

    /** The map of maps that will store all our MessageLocalStore per folder and per offset */
    private static Map<Integer, Map<Integer, MessageLocalStore>> mapMessageLocalStore 
                        = new HashMap<Integer, Map<Integer, MessageLocalStore>>();
       
    /** The map that will store the total messages number per folder */    
    private static Map <Integer, Integer> mapTotalMessages = new HashMap<Integer, Integer>();    
    
    
    /**
     * Protected void Constructor
     */
    protected MessageLocalStoreCache()
    {
        
    }
    
    
    
    /**
     * @param folderId      the folder id
     * @return  true if the cache exists for the folder
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public static boolean containsKey(Integer folderId, Integer offset)
    {
        if (! mapMessageLocalStore.containsKey(folderId))
        {
            return false;
        }
        else
        {
            Map<Integer, MessageLocalStore> mapForFolder = mapMessageLocalStore.get(folderId);
            
            if (mapForFolder.containsKey(offset))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
    
    
    
    
    /**
     * return the MessageLocalStore cached for the passed folder id
     * 
     * @param folderId      the folder id
     * @return the MessageLocalStore cached for the passed folder id
     */
    public static MessageLocalStore get(Integer folderId, Integer offset)
    {
        if (! mapMessageLocalStore.containsKey(folderId))
        {
            throw new IllegalArgumentException("mapMessageLocalStore is empty for folder Id: " + folderId);
        }
        
        Map<Integer, MessageLocalStore> mapForFolder = mapMessageLocalStore.get(folderId);
        
        if (! mapForFolder.containsKey(offset))
        {
            throw new IllegalArgumentException("mapForFolder is empty for offset " + offset);
        }        
        
        MessageLocalStore messageLocalStore = mapForFolder.get(offset);
        return messageLocalStore;
        
    }

    /**
     * 
     * Cache a MessageLocalStore for a folder id
     * 
     * @param folderId      the folder id
     * @param messageLocalStore         the MessageLocalStore to store for this folder id
     *
     */
    public static void put(Integer folderId, Integer offset, MessageLocalStore messageLocalStore)
    {
        if (! mapMessageLocalStore.containsKey(folderId))
        {
            // Create the new map
            Map<Integer, MessageLocalStore> mapForFolder = new HashMap<Integer, MessageLocalStore>();        
            mapForFolder.put(offset, messageLocalStore);
            mapMessageLocalStore.put(folderId, mapForFolder);
        }
        else
        {
            // Map exists replace it
            Map<Integer, MessageLocalStore> mapForFolder = mapMessageLocalStore.get(folderId);
            mapForFolder.put(offset, messageLocalStore);
            mapMessageLocalStore.put(folderId, mapForFolder);
        }
        
               
    }

    
    /**
     * clear the cache.
     */
    public static void clear()
    {
        mapMessageLocalStore.clear();
        mapTotalMessages.clear();
    }

    
    /**
     * remove a cached MessageLocalStore for a folder id & also remove the total messages
     * @param folderId      the folder id)
     */
    public static void remove(Integer folderId)
    {
        mapMessageLocalStore.remove(folderId);
        mapTotalMessages.remove(folderId);
    }

    
    /**
     * Store the total messages number per folder
     * @param folderId      the folder id
     * @param totalMessages the total messages number for the folder
     */
    public static void putTotalMessages(Integer folderId, Integer totalMessages)
    {
        mapTotalMessages.put(folderId, totalMessages);
    }
    
    /**
     * return  total messages number per folder
     * @param folderId      the folder id
     * @return total messages number per folder
     */
    public static int getTotalMessages(Integer folderId)
    {
        return mapTotalMessages.get(folderId);
    }    

    /**
     * @param folderId      the folder id
     * @return  true if the total message cache exists for the folder
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public static boolean containsKeyTotalMessages(Integer folderId)
    {
        return mapTotalMessages.containsKey(folderId);
    }
    
    /**
     * @return  the size in bytes of this instance
     */
    public static int sizeStorage()
    {
        int sizeStorage = 0;
        List<Integer> messageIdList = new ArrayList<Integer>(mapMessageLocalStore.keySet());      
        for (Integer integer : messageIdList)
        {
            Map<Integer, MessageLocalStore> map = mapMessageLocalStore.get(integer);
            sizeStorage += getMapSizeStorage(map);
        }
        
        return sizeStorage;
    }    
    
    /**
     * @return  the size in bytes of the map
     */
    private static int getMapSizeStorage(Map<Integer, MessageLocalStore> map)
    {
        int sizeStorage = 0; 
        List<Integer> messageIdList = new ArrayList<Integer>(map.keySet());      
        for (Integer integer : messageIdList)
        {
            MessageLocalStore messageLocalStore = map.get(integer);
            sizeStorage += messageLocalStore.sizeStorage();            
        }        
        return sizeStorage;
    }
    
}
