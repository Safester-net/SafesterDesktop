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
package net.safester.clientserver.specs;

import java.util.Set;

import net.safester.noobs.clientserver.specs.Local;


public interface LocalStore<T, E extends Local>
{
    /**
     * @param key       the key of the Local element
     * @return a Local  
     */
    public abstract Local get(T key);
    
    /**
     * @param key       the key of the Local element
     * @param element   the Local element
     */
    public abstract E put(T key, E element);
        
    /**
     * Removes the mapping for a key from this map if it is present
     * (optional operation).  
     */
    public abstract E remove(T key);
        
    /**
     * @return true if the Local Store is empty
     */
    public abstract boolean isEmpty();
        
    /**
     * Returns the number of key-value mappings in this Local Store.
     * @return the number of key-value mappings in this Local Store
     */
    public abstract int size();
    
    /**
     * @return the keys into a easy to scan Set
     */
    public abstract Set<T> keySet();
            
}

