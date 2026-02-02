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
package com.safelogic.sql.util;

/**
 * Store the number of Connections in use & increment/decrement *safely* this number
 * with the synchronzed usage.
 * 
 * @author Nicolas de Pomereu
 */

public class ConnectionCountStore
{
    // Modifiers values
    private static final int MODIFY_INCREMENT = +1;
    private static final int MODIFY_DECREMENT = -1;
    
    /** Stores the number of Connection in use in the current JVM */
    private static int m_nConnectionCount = 0;
        
    /**
     * Private Constructor
     */
    private ConnectionCountStore( )
    {      
        // Constructor declared to protect class from instanciation
    }
    
    /**
     * Return an instance of ConnectionCountStore
     */
    public static ConnectionCountStore getInstance()
    {
        return new ConnectionCountStore();
    }
    
    /**
     * 
     * @return the Connection Counter value
     */
    public synchronized int getConnectionNumber()
    {
        return m_nConnectionCount;
    }
    
    /**
     * increment the Connection Count
     */
    public void increment()
    {
        this.setConnectionCount(MODIFY_INCREMENT);
    }
    
    /**
     * increment the Connection Count
     */
    public void decrement()
    {
        this.setConnectionCount(MODIFY_DECREMENT);
    }
    
    /**
     * Safe access Wrapper for m_nConnectionCount access with increment/decrement.
     * <br><br>
     * NOTE: This method *must* be declared as synchronized.
     * <br><br>
     * @param nTypeModify   The modification type:
     * <br> - MODIFY_INCREMENT
     * <br> - MODIFY_DECREMENT
     * @throw IllegalArgumentException if nTypeModify is invalid
     */
    private synchronized void setConnectionCount(int nTypeModify)
    {      
        if (nTypeModify == MODIFY_DECREMENT )
        {
            m_nConnectionCount--;
            
            if (m_nConnectionCount < 0)
            {
                m_nConnectionCount = 0;
            }            
        }        
        else if (nTypeModify == MODIFY_INCREMENT)
        {
            m_nConnectionCount++;
        }
        else
        {
            throw new IllegalArgumentException("Type must be: " 
                    + MODIFY_DECREMENT + " or " + MODIFY_INCREMENT);
        }
    }
}

// End
