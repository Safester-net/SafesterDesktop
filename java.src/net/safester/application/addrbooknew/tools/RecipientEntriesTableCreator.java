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
package net.safester.application.addrbooknew.tools;


import java.awt.Component;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.safester.application.addrbooknew.RecipientEntry;
import net.safester.application.messages.MessagesManager;
import net.safester.application.tool.TableModelNonEditable;
import net.safester.application.util.JTableUtil;
import net.safester.application.util.TableUtil;


/**
 * Create a JTable for Files : name, size, last modified.
 * 
 * @author Nicolas de Pomereu
 */ 
public class RecipientEntriesTableCreator
{

    /** List of all recipientEntries*/
    private List<RecipientEntry> recipientEntries = null;
    private boolean editable = false;
    private boolean sortable = false;
    
    private boolean emailIsUrl = false;
    
    private JTable jTable1 = null; 
            
    /**
     * 
     * Constructor
     * 
     * @param recipientEntries        The PDF recipients email, name, mobile & notify email
     */
    public RecipientEntriesTableCreator(List<RecipientEntry> recipientEntries)
    {
        this.recipientEntries = recipientEntries;
    }
    
    /**
     * Says if the table is editable
     * @param editable if true, table is editable
     */
    public void setTableEditable(boolean editable) {
        this.editable = editable;
    }
    
    /**
     * Says if table is sortable (sometimes we don't want a table to be sorted)
     * @param sortable if true, table is sortable on email
     */
    public void setTableSortable(boolean sortable) {
        this.sortable = sortable;
    }
        
     /**
     * @param emailIsUrl if true the email is clickable and will be colored to URL color
     */
    public void setEmailIsUrl(boolean emailIsUrl) {
        this.emailIsUrl = emailIsUrl;
    }
    
    
    /**
     * Create a JTable with all Word recipientEntries
     *                      
     * @return a JTable with all all Word recipientEntries
     */
    public JTable create()
    {        
        int columnsNumber = 4;
        Object[] colName = new Object[columnsNumber];
        
        int col = 0;
        colName[col++] = MessagesManager.get("email"); //"Email";
        colName[col++] = MessagesManager.get("name"); //"Nom";
        colName[col++] = MessagesManager.get("company"); //"Société";
        colName[col++] = MessagesManager.get("mobile_number"); //N° de Mobile";
        //colName[col++] = "Email Secondaire";
                       
        Object [][] data = new Object[recipientEntries.size()][columnsNumber];

        for(int i = 0; i< recipientEntries.size(); i++)
        {   
            int j = 0;                                

            data[i][j++] = recipientEntries.get(i).getEmailAddress();
            data[i][j++] = recipientEntries.get(i).getName();
            data[i][j++] = recipientEntries.get(i).getCompany();
            data[i][j++] = recipientEntries.get(i).getMobile();
            //data[i][j++] = recipientEntries.get(i).getEmailNotify();
        }
        
        // We will create out own getCellRenderer() in the JTable, so that it can call
        // BasicFilesTableCellRenderer

        try
        {
            TableModel tableModel = null;
            
            if (editable) {
                tableModel = new DefaultTableModel(data , colName);
            }
            else {
                tableModel = new TableModelNonEditable(data , colName);
            }
            
            jTable1  = new JTable(tableModel) {
                // Vital code for easy drag & drop on all JTable area
                public boolean getScrollableTracksViewportHeight() {
                    Component parent = getParent();

                    if (parent instanceof JViewport) {
                        return parent.getHeight() > getPreferredSize().height;
                    }
                    return false;
                }
            };                        
            
            jTable1.getTableHeader().setReorderingAllowed(false);
                    
            if (sortable) {
                /*
                TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(jTable1.getModel());
                jTable1.setRowSorter(sorter);

                List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
                sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
                sorter.setSortKeys(sortKeys);
                */
                
                TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(jTable1.getModel());
                jTable1.setRowSorter(sorter);
                
                TableUtil.setSortOrderFromPrefs(this.getClass(), jTable1);
                TableUtil.rememberSortColumn(this.getClass(), jTable1);
                
            }
            else {
                jTable1.setAutoCreateRowSorter(true);
            }
                    
            TableColumnModel columnModel = jTable1.getColumnModel();
               
            for (int i = 0; i < columnsNumber; i++)
            {
                columnModel.getColumn(i).setCellRenderer(new RecipientsImportTableCellRenderer(emailIsUrl));                  
            }            
            
            // Set the Table Header Display
            //Font fontHeader = new Font(m_font.getName(), Font.PLAIN, m_font.getSize());        
            //JTableHeader jTableHeader = jTable1.getTableHeader();
            //jTableHeader.setFont(fontHeader);  
            //jTable1.setTableHeader(jTableHeader);
            //jTable1.setFont(m_font);
            
            jTable1.setColumnSelectionAllowed(false);
            jTable1.setRowSelectionAllowed(true);
            jTable1.setAutoscrolls(true);

            //jTable1.setColumnModel(new MyTableColumnModel());
            //jTable1.setAutoCreateColumnsFromModel(true);

            jTable1.setShowHorizontalLines(false);
            jTable1.setShowVerticalLines(false);

            // Resize last column (if necessary)
            jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            //jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            // use an Expansion factor of 1.3x for change between 12 and Arial,17        
            JTableUtil.calcColumnWidths(jTable1, 1.00);       

            //LookAndFeelMgr.setTableHeadersForSynthetica(jTable1);    
            
            return jTable1;
        }
        catch(Exception e)
        {
            //Should never happens
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Exception occurred in JTable: " + e.toString());
        }
        return null;
    }


}

/**
 * 
 */
