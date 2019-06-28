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

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * Thus class overides the DefaultTableModel so that *no* Cell is editable
 * and to handle clean Date sorting & Size sorting.
 * This is done by overcharging isCellEditable() which returns always false
 * and overcharging
 * 
 * @author Nicolas de Pomereu
 */
public class TableModelMessages extends DefaultTableModel
{

    /**
     * Consrructor
     */
    public TableModelMessages()
    {
        super();
    }

    /**
     * @param rowCount
     * @param columnCount
     */
    public TableModelMessages(int rowCount, int columnCount)
    {
        super(rowCount, columnCount);

    }

    /**
     * @param columnNames
     * @param rowCount
     */
    public TableModelMessages(Vector columnNames, int rowCount)
    {
        super(columnNames, rowCount);

    }

    /**
     * @param columnNames
     * @param rowCount
     */
    public TableModelMessages(Object[] columnNames, int rowCount)
    {
        super(columnNames, rowCount);

    }

    /**
     * @param data
     * @param columnNames
     */
    public TableModelMessages(Vector data, Vector columnNames)
    {
        super(data, columnNames);

    }

    /**
     * @param data
     * @param columnNames
     */
    public TableModelMessages(Object[][] data, Object[] columnNames)
    {
        super(data, columnNames);
    }
    
    /**
     * Returns true regardless of parameter values.
     *
     * @param   row             the row whose value is to be queried
     * @param   column          the column whose value is to be queried
     * @return                  true
     * @see #setValueAt
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /*
    messages.getMessage("id"),
    messages.getMessage("folder"),
    messages.getMessage(" "),
    messages.getMessage(" "),
    messages.getMessage("to_col"),
    
    messages.getMessage("sent"),
    messages.getMessage("subject"),
    messages.getMessage("size")
    */
    
    Class[] types = new Class [] {
        java.lang.Object.class,
        java.lang.Object.class,
        java.lang.Object.class,
        java.lang.Object.class,
        java.lang.Object.class,
        java.sql.Timestamp.class,
        java.lang.Object.class,
        java.lang.Long.class
    };

    @Override
    public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
    }
       
}
