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
package net.safester.application.photo;

import java.awt.Window;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import net.safester.application.messages.MessagesManager;
import net.safester.application.tool.DesktopWrapper;

public class PhotoAddressBookTableCreatorNew {

    private List<PhotoAddressBookLocal> addresses = null;
    private MessagesManager messages = new MessagesManager();
    private boolean isEditable = false;
    private Window parent;

    public PhotoAddressBookTableCreatorNew(List<PhotoAddressBookLocal> addresses, Window theParent, boolean editable) {
        this.addresses = addresses;
        this.isEditable = editable;
        this.parent = theParent;
    }

    public JTable create() {
        Object[] colName = null;
        int columnsNumber = 5;

        colName = new Object[columnsNumber];

        colName[0] = messages.getMessage("");
        colName[1] = messages.getMessage("name");
        colName[2] = messages.getMessage("email");
        colName[3] = messages.getMessage("company");
        colName[4] = messages.getMessage("cell_phone");
        
        Object[][] data;
        if (addresses != null) {
            data = new Object[addresses.size()][columnsNumber];
            int i = 0;
            for (PhotoAddressBookLocal record : addresses) {
                data[i][0] = record.getThumbnail();                
                data[i][1] = record.getName();
                data[i][2] = record.getEmail();
                data[i][3] = record.getCompany();
                data[i][4] = record.getCell_phone();
                
                i++;
            }
        } else {
            data = new Object[1][columnsNumber];
            data[0][0] = "";
            data[0][1] = "";
            data[0][2] = "";
            data[0][3] = "";
            data[0][4] = "";
        }

        JTable table = new JTable(new DefaultTableModel(data, colName)) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
               
                if (colIndex == 0) {
                    return false;
                }
                else {
                    return isEditable;                    
                }
            }
            
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                Object oldValue = getValueAt(row, column);
                super.setValueAt(aValue, row, column);
                if (oldValue == null || !oldValue.equals(aValue)) {
                    if (parent != null && parent instanceof PhotoAddressBookUpdaterNew) {
                        PhotoAddressBookUpdaterNew caller = (PhotoAddressBookUpdaterNew) parent;
                        //caller.updateListFromTable();
                        caller.setUpdated(true);
                    }

                }
            }
        };

        JTableHeader jTableHeader = table.getTableHeader();
        jTableHeader.setReorderingAllowed(false);
        
        DesktopWrapper.setAutoCreateRowSorterTrue(table);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.setColumnSelectionAllowed(false);
                
        columnModel.getColumn(0).setMinWidth(ImageResizer.getStickerHeight() + 4);
        columnModel.getColumn(0).setMaxWidth(ImageResizer.getStickerHeight() + 4);
        columnModel.getColumn(0).setPreferredWidth(ImageResizer.getStickerHeight() + 4); 
        
        // For photo display
        columnModel.getColumn(0).setCellRenderer(new PhotoAddresBookTableCellRendererNew());
        columnModel.getColumn(1).setCellRenderer(new PhotoAddresBookTableCellRendererNew());
        columnModel.getColumn(2).setCellRenderer(new PhotoAddresBookTableCellRendererNew());
        columnModel.getColumn(3).setCellRenderer(new PhotoAddresBookTableCellRendererNew());
        columnModel.getColumn(4).setCellRenderer(new PhotoAddresBookTableCellRendererNew());
                
        table.setRowHeight(ImageResizer.getStickerHeight() + 2);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        
        return table;
    }
}
