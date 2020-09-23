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
package net.safester.application.tool;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;

/**
 * A Sorted Model.
 * 
 * @author Nicolas de Pomereu
 */
public class SortedDefaultListModel extends DefaultListModel
{
	private static final long serialVersionUID = -416943099758365933L;
	
	/** Store the ascending/descending setting */
    private boolean sortAscending = true;
    
    /**
     * Constructor
     */
    public SortedDefaultListModel()
    {
        super();
    }

    /**
     * sort the values
     */
    public void sort()
    {
        if (!this.isEmpty())
        {
            Object[] keys = new Object[this.getSize()];
            this.copyInto(keys);
            this.removeAllElements();
            List list = new Vector();
                        
            for (int i = 0; i < keys.length; i++)
            {
                list.add(keys[i]);                               
            }
            
            if (sortAscending)
            {
                Collections.sort(list);
            }
            else
            {
                Collections.sort(list, Collections.reverseOrder());
            }
            
            for (int i = 0; i < list.size(); i++)
            {
                this.addElement(list.get(i));
            }            
        }
    }
        
    public void sortAscending()
    {
        sortAscending = true;
        sort();
    }
    
    public void sortDescending()
    {
        sortAscending = false;
        sort();        
    }
    
    /**
     * sort the values
     */
    public void sortChangerOrder()
    {
        if (sortAscending)
        {
            sortDescending();
        }
        else
        {
            sortAscending();
        }
    }
    
    
}

/**
 * 
 */
