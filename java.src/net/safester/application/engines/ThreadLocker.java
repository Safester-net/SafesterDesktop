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
package net.safester.application.engines;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Nicolas de Pomereu
 *
 * Class to prevent more than one code sequence to execute simultaneously
 * on main thread.
 * <br>
 * Usage:
 * <ul>
 * <li>Session asks if thread is locked with isLocked().</li>
 * <li>if thread is unlocked(), user may lock it with lock().</li>
 * <li>user *must* release the lock with unlock().<li>
 * </ul>
 * 
 */
public class ThreadLocker
{
    /** If true session is locked */
    private static Set<Integer> locks = new HashSet<Integer>();
    
    /** The id to use */
    private int id = -1;
    
    /** Id for encryption/upload task */
    public static final int TYPE_ENCRYPT = 1;
    
    /** Id for decryption/download task */    
    public static final int TYPE_DECRYPT = 2;
    
    /**
     * Default Constructor
     */
    public ThreadLocker()
    {
        this.id = 1;
    }
    
    
    /**
     * Constructor
     * @param id    the id of the thread
     */
    public ThreadLocker(int id)
    {
        this.id = id;
    }

    
    /**
     * @return true id thread is locked, else false
     */
    public boolean isLocked()
    {
        return safeIsLocked(this.id);
    }

    
    /**
     * Lock the thread identified by id passed to constructor
     */
    public boolean TryTolock()
    {
        return safeTryToLock(this.id);
    }
    
    /**
     * Unlock the thread identified by id passed to constructor
     */
    public void unlock()
    {
        safeUnlock(this.id);
    }    

    /**
     * test if a id is locked
     * @param id the id to lock
     */    
    private static synchronized boolean safeIsLocked(int id)
    {
        return locks.contains(id);
    }
    
    /**
     * lock id.
     * @param id the id to lock
     */
    private static synchronized boolean safeTryToLock(int id)
    {
        if (locks.contains(id))
        {
            return false;
        }
        else
        {
            locks.add(id);
            return true;
        }
    }    
    
    /**
     * unlock id.
     * @param id the id to unlock
     */    
    private static synchronized void safeUnlock(int id)
    {
        locks.remove(id);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) 
    {
        ThreadLocker t = new ThreadLocker();
        
        System.out.println("isLocked: " + t.isLocked());        
        System.out.println("lock: " +  t.TryTolock());
        
        System.out.println("isLocked: " + t.isLocked());
        
        System.out.println("unlock");
        t.unlock();        
        
        System.out.println("isLocked: " + t.isLocked());        
    }
   
}
