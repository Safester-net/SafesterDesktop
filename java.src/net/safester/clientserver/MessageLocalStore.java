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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.safester.clientserver.specs.LocalStore;
import net.safester.noobs.clientserver.MessageLocal;

/**
 * @author Nicolas de Pomereu
 * 
 * A Local Store for the Message Local.
 */
public class MessageLocalStore implements LocalStore<Integer, MessageLocal>
{

    /** The map that will store all our objects */
    private Map <Integer, MessageLocal> map = null;
               

    /**
     * Constructor. Inits a new Sttore
     */
   public MessageLocalStore()
   {
       map  = Collections.synchronizedMap(new LinkedHashMap<Integer, MessageLocal>());
   }

    /**
     * @return
     * @see java.util.Map#isEmpty()
     */
    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }


    /**
     * @param key
     * @return
     * @see java.util.Map#get(java.lang.Object)
     */
    @Override
    public MessageLocal get(Integer key)
    {
        return map.get(key);
    }


    /**
     * @param key
     * @param value
     * @return
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public MessageLocal put(Integer key, MessageLocal value)
    {
        return map.put(key, value);
    }


    /**
     * @param key
     * @return
     * @see java.util.Map#remove(java.lang.Object)
     */
    @Override
    public MessageLocal remove(Integer key)
    {
        return map.remove(key);
    }


    /**
     * @return
     * @see java.util.Map#size()
     */
    @Override
    public int size()
    {
        return map.size();
    }
    
    /**
     * @return the keys into a easy to scan List
     */
    @Override
    public Set<Integer> keySet()
    {
        Set<Integer> set = map.keySet();
        return set;
    }
    
    
    /**
     * @return  the size in bytes of this instance
     */
    public int sizeStorage()
    {
        int sizeStorage = 0;
        List<Integer> messageIdList = new ArrayList<Integer>(map.keySet());      
        for (Integer integer : messageIdList)
        {
            sizeStorage += map.get(integer).sizeStorage();
        }
        return sizeStorage;
    }

    public void resetStore(){
        this.map.clear();
    }
}
