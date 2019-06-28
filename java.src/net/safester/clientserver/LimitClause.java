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

/**
 * @author Nicolas de Pomereu
 * a clean builder for a "LIMIT nn OFFSET xx" clause.
 */
public class LimitClause
{    
    private int limit  = 0;
    private int offset = 0;

    /**
     * Constructor
     * @param limit     the LIMIT value
     * @param offset    the OFFSET value
     */
    public LimitClause(int limit, int offset)
    {
        this.limit = limit;
        this.offset = offset;
    }
    
    /**
     * Display the next records
     */
    public void next()
    {
        offset += limit;
    }
    
    /**
     * Display the previsous records
     */
    public void previous()
    {
        offset -= limit;
        if (offset < 0) 
        {
            offset = 0;
        }
    }    
    
    
    /**
     * @return the limit
     */
    public int getLimit()
    {
        return limit;
    }

    /**
     * @return the offset
     */
    public int getOffset()
    {
        return offset;
    }

    /**
     * Return the "LIMIT xx OFFSET yy" clause if xx !=0 
     * (otherwise returns an empty "" String)
     */
    @Override
    public String toString()
    {
        if (limit == 0)
        {
            return new String();
        }
        else
        {
            return "LIMIT " + limit + " OFFSET " + offset;
        }
    }
}
