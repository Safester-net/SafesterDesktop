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
package com.safelogic.pgp.api.toolkit.test;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Nicolas de Pomereu
 *
 */
public class MapTester
{

    public static Map<Integer, Boolean> tasksOperationOk        = new HashMap<Integer, Boolean>();
    
    // The TaskNumber
    private int taskNumber = 0;
    
    /**
     * 
     */
    public MapTester()
    {
        System.out.println(tasksOperationOk.get(taskNumber));
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        new MapTester();
    }

}

/**
 * 
 */
