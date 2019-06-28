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

import java.sql.SQLException;
import java.util.List;

import net.safester.noobs.clientserver.specs.Local;

/**
 * An interface for getting/putting Local Objects from/to the Server
 * @author Nicolas de Pomereu
 *
 * @param <E extends Local> the type of Local to to use
 */
public interface ListTransfer<E extends Local>
{
    /**
     * @return the List of Local(s) to be strored locally and fetched from the Sql Server
     */
    public abstract List<E> getList() throws SQLException;

    /**
     * Save on the Sql Server the List of Local(s) from local store
     * @param list the list of Stores to set
     */
    public abstract void putList(List<E> list)
            throws SQLException;

}
