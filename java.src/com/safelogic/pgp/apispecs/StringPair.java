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
package com.safelogic.pgp.apispecs;

/**
 * @author Nicolas de Pomereu
 *
 * Define a class of (String, String) pair
 */

public class StringPair
{
    /** Element 1*/
    private String element1 = null;
    
    /** Element 2*/
    private String element2 = null;   
    
    /**
     * @param element1  the first element of a pair
     * @param element2  the second element of a pair
     */
    public StringPair(String element1, String element2)
    {
        super();
        this.element1 = element1;
        this.element2 = element2;
    }

    /**
     * @return the element1
     */
    public String getElement1()
    {
        return element1;
    }

    /**
     * @return the element2
     */
    public String getElement2()
    {
        return element2;
    }
    
    public String toString()
    {
        return "[" + element1 + ", " + element2 + "]";
    }
    
}

