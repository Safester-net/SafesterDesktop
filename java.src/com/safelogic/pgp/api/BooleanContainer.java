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
package com.safelogic.pgp.api;

/**
 * Class that store a boolean value.
 * 
 * @author Nicolas de Pomereu
 */
public class BooleanContainer
{

    /** The boolean value to store */
    private boolean booleanValue = false;
    
    /**
     * Constructor
     */
    public BooleanContainer()
    {
        // Nothing
    }

    /**
     * @return Returns true is the signature is verified
     */
    public boolean getBooleanValue()
    {
        return booleanValue;
    }

    /**
     * @param booleanValue The booleanContained to set
     */
    public void setBooleanValue(boolean booleanValue)
    {
        this.booleanValue = booleanValue;
    }

    public String toString()
    {
        return new Boolean(booleanValue).toString();
    }
    
}

/**
 * 
 */
