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
package net.safester.application.util;

import java.awt.Font;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;

import net.safester.application.SystemInit;
import net.safester.application.messages.MessagesManager;
import net.safester.application.tool.TableModelNonEditable;




/**
 * Create a JTable with all system properties
 * @author Nicolas de Pomereu
 */
public class SystemPropsTableCreator
{
    /** The National language  messages */ 
    private MessagesManager messagesManager = new  MessagesManager();
    
    /** The font to use */
    Font m_font = null;
    
    /**
     * 
     * Constructor
     * 
     * @param font              The font ot use for the JTable lines 
     */
    public SystemPropsTableCreator(Font font)
    {
       m_font = font;
              
    }
    /**
     * Create a JTable with all system properties            
     * @return a JTable with all system properties
     */
    public JTable create()
    {
               
        Object[] colName = null;
        int columnsNumber = 0;

        columnsNumber = 2;
        colName = new Object[columnsNumber];
        
        colName[0] = this.messagesManager.getMessage("name");
        colName[1] = this.messagesManager.getMessage("value");
        
        Properties p = System.getProperties();
        
        Enumeration keys = p.keys() ;
                 
        List<String> listKeys = new Vector<String>();
        
        while(keys.hasMoreElements())
        {
            String key      = (String)keys.nextElement() ;
            listKeys.add(key);
        }
        
        Collections.sort(listKeys);

        int supplementaryLines = 2;
        
        int lineNumber = listKeys.size() + supplementaryLines;

        Object [][] data = new Object[lineNumber][columnsNumber];

        int i = 0;

        data[0][0] = "Log Out";
        data[0][1] = SystemInit.getSAFESTER_OUT_LOG();

        data[1][0] = "Log Err";
        data[1][1] = SystemInit.getSAFESTER_ERR_LOG();
        
        for(i = supplementaryLines; i< listKeys.size(); i++)
        {            
            String key      = listKeys.get(i);
            String value    =  p.getProperty(key);     
            
            data[i][0] = key;
            data[i][1] = value;           
        }
                       
        JTable jTable1  = new JTable(new TableModelNonEditable(data , colName )); 
        
        // Set the Table Header Display
        Font fontHeader = new Font(m_font.getName(), Font.PLAIN, m_font.getSize());        
        JTableHeader jTableHeader = jTable1.getTableHeader();
        jTableHeader.setFont(fontHeader);   
        jTable1.setTableHeader(jTableHeader); 
                      
        jTable1.setAutoCreateRowSorter(true);
        
        jTable1.setFont(m_font);
        jTable1.setColumnSelectionAllowed(false);
        jTable1.setRowSelectionAllowed(true);
        jTable1.setAutoscrolls(true);
        
        //jTable1.setColumnModel(new MyTableColumnModel());
        //jTable1.setAutoCreateColumnsFromModel(true);
        
        jTable1.setShowHorizontalLines(false);
        jTable1.setShowVerticalLines(true);
                
        jTable1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // Resize last column (if necessary)
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // use an Expansion factor of 1.3x for change between 12 and Arial,17        
        JTableUtil.calcColumnWidths(jTable1, 1.00);
        
        return jTable1;
    }

}

/**
 * 
 */
