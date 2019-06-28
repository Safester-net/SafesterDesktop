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
package net.safester.noobs.clientserver;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.safester.clientserver.specs.LocalStore;

/**
 * @author Nicolas de Pomereu
 * 
 * A Local Store for the Message Local.
 */
public class MessageBodyLocalStore implements LocalStore<Integer, MessageBodyLocal>
{

    /** The map that will store all our objects */
    private Map <Integer, MessageBodyLocal> map = null;
               

    /**
     * Constructor. Inits a new Sttore
     */
   public MessageBodyLocalStore()
   {
       map  = Collections.synchronizedMap(new LinkedHashMap<Integer, MessageBodyLocal>());
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
    public MessageBodyLocal get(Integer key)
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
    public MessageBodyLocal put(Integer key, MessageBodyLocal value)
    {
        return map.put(key, value);
    }


    /**
     * @param key
     * @return
     * @see java.util.Map#remove(java.lang.Object)
     */
    @Override
    public MessageBodyLocal remove(Integer key)
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
    
    
    public void resetStore(){
        this.map.clear();
    }
}
