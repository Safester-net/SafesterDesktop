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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.safester.noobs.clientserver.GroupMemberLocal;


/**
 * @author Nicolas de Pomereu
 *
 */
public class GroupMemberCache {

    
    private static Map<Integer, List<GroupMemberLocal>> groupMemberLocalMap = new TreeMap<Integer, List<GroupMemberLocal>>();
    
    
    /**
     * Protected constructor
     */
    protected GroupMemberCache() {
	// TODO Auto-generated constructor stub
    }
    
    
    /**
     * Says if the cache contains the List<GroupMemberLocal> for  the passed Group  d
     * 
     * @param groupId      the Group Id
     * @return  true if the cache exists for the group
     */
    public static boolean containsKey(Integer groupId)
    {

        if (! groupMemberLocalMap.containsKey(groupId))
        {
            return false;
        }	
        else {
            return true;
        }	
    }
    
    /**
     * Returns the list of group members for a group id
     * @param groupId	the group id
     * @return	the list of group members for a group id
     */
    public static List<GroupMemberLocal> get(int groupId) {
	
        if (! groupMemberLocalMap.containsKey(groupId))
        {
            throw new IllegalArgumentException("groupMemberLocalMap is empty for group Id: " + groupId);
        }
        
        return groupMemberLocalMap.get(groupId);
        
    }
    
    /**
     * Sttre in the cache the members of a grouû 
     * @param groupId		the group id of t e group
     * @param groupMemberList	the list of member of thh ggouo to cache
     */
    public static void put(int groupId, List<GroupMemberLocal> groupMemberList) {
	groupMemberLocalMap.put(groupId, groupMemberList);
	
    }
    
    /**
     * Clears the cache for the passed group id 
     * @param groupId	the group id to clear thetcache for
     */
    public static void clear(int groupId) {
	groupMemberLocalMap.remove(groupId);
    }
    
    /**
     * Clears the cache for all group ids
     */
    public static void clearAll() {
	groupMemberLocalMap = new TreeMap<Integer, List<GroupMemberLocal>>();
    }    
    
}
