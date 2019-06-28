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

/*
 * TableTransferHandler.java is used by the 1.4
 * ExtendedDnDDemo.java example.
 */
 
 //Last updates
 // 30/09/09 10:15 ABE: TeamCenter table can now contains doctors, when adding a center remove all doctors of center previously present
 //						cannot add a doctor of a center previously present
 
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TableTransferHandler extends StringTransferHandler {
    private int[] rows = null;
    private int addIndex = -1; //Location where items were added
    private int addCount = 0;  //Number of items added.

    private boolean isDropable = false;
    
    /** The Set of all first columns values */
    private static List firstColumns = new Vector();
    
    public TableTransferHandler()
    {
        isDropable = false;
    }
        
    /**
     * @param isDropable if true, Table with be dropable
     */
    public TableTransferHandler(boolean isDropable)
    {
        this.isDropable = isDropable;
    }
    @Override
    protected String exportString(JComponent c) {
        JTable table = (JTable)c;
        rows = table.getSelectedRows();

        int colCount = table.getColumnCount();
        
        // Work on the TableModel please, not the table itself!       
        //int colCount = table.getModel().getColumnCount();

        //JOptionPane.showMessageDialog(null, "colCount: "  + colCount);
        
        StringBuffer buff = new StringBuffer();
        
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < colCount; j++) {
                
                Object val = table.getValueAt(rows[i], j);

                // Work on the TableModel please, not the table itself!
                //Object val = table.getModel().getValueAt(rows[i], j);
                
                buff.append(val == null ? "" : val.toString());
                
                if (j != colCount - 1) 
                {
                    buff.append(",");
                }
            }
          
            if (i != rows.length - 1) {
                buff.append("\n");
            }
        }
        
        //JOptionPane.showMessageDialog(null, buff);
        
        return buff.toString();
    }
    @Override
    protected void importString(JComponent c, String str) {
        JTable target = (JTable)c;
        DefaultTableModel model = (DefaultTableModel)target.getModel();
        int index = target.getSelectedRow();

        if (! isDropable)
        {
           // JOptionPane.showMessageDialog(null, "TableTransferHandler.isDropable = " + isDropable);
            return;
        }
        
        //Prevent the user from dropping data back on itself.
        //For example, if the user is moving rows #4,#5,#6 and #7 and
        //attempts to insert the rows after row #5, this would
        //be problematic when removing the original rows.
        //So this is not allowed.
        if (rows != null && index >= rows[0] - 1 &&
              index <= rows[rows.length - 1]) {
            rows = null;
            return;
        }

        int max = model.getRowCount();
        if (index < 0) {
            index = max;
        } else {
            index++;
            if (index > max) {
                index = max;
            }
        }
        addIndex = index;
        
        String[] values = str.split("\n");
        //JOptionPane.showMessageDialog(null, values);
        
        addCount = values.length;

        // Compute first columns  values for each row
        firstColumns = new Vector();
        List<Object> secondColumns = new Vector<Object>();
        List<Object> thirdColumns = new Vector<Object>();
        List<Object> doctorIds = new Vector<Object>();
        
        for (int i = 0; i < model.getRowCount() ; i++) 
        {
            Object firstColumn = model.getValueAt(i, 0);                
            firstColumns.add(firstColumn);
//
//            Object secondColumn = model.getValueAt(i, 1);
//            secondColumns.add(secondColumn);

            Object thirdColumn = model.getValueAt(i, 2);
            thirdColumns.add(thirdColumn);

            Object doctorId = model.getValueAt(i, 5);
            doctorIds.add(doctorId);
        }

        //for (int i = 0; i < values.length && i < colCount; i++) 
        for (int i = 0; i < values.length; i++) 
        {
            String value = values[i];

            value = formatCenterDoctor(value);
            System.out.println(value);

            String[] columns = value.split(",");
            
            if(firstColumns.contains(columns[0]))
            {
                //Search if all center of doctor is already added
                boolean founded = false;
                for(int k = 0; k < firstColumns.size(); k++)
                {
                    System.out.println("==> firstColumns.get(k): " + firstColumns.get(k));
                    System.out.println("==> columns[5]         : " + columns[5]);

                    if(firstColumns.get(k).equals(columns[0]))
                    {
                        if(doctorIds.get(k).equals(columns[5]) || doctorIds.get(k).equals("-1"))
                        {
                            System.out.println("founded!");
                            founded = true;                            
                        }
                    }
                }
                
                if(founded)
                {
                    System.out.println("CONTINUE2!");
                    continue;
                }
            }            
            
            if(columns[5].toString().equals("-1"))
            {
                while(firstColumns.contains(columns[0]))
                {
                   model.removeRow(firstColumns.indexOf(columns[0]));
                   firstColumns.remove(firstColumns.indexOf(columns[0]));
                   index--;
                }
            }
            //model.insertRow(index++, values[i].split(","));
            model.insertRow(index++, value.split(","));
            
            firstColumns.add(columns[0]);
            doctorIds.add(columns[5]);
        }
        
        /*
         * KEEP THIS CODE
         * 
        for (int i = 0; i < model.getRowCount() ; i++) 
        {
            for (int j = 0; j < model.getRowCount() ; j++) 
            {
                String centerId = (String) model.getValueAt(j, 0);
                if (centerId == null || centerId.length() == 0 || centerId.equals(" "))
                {
                    model.removeRow(j);
                }
            }
        }
               
        // Add 4 blank rows at end of Jtable
        int last = model.getRowCount();
        int nbLinesToInsert = 4 - last;
                
        Object[] rowData = new Object[4];
        
        for (int i = 0; i < nbLinesToInsert ; i++) 
        {
            model.insertRow(last++, rowData);
        }
        */
        
        target.updateUI();
               
    }
    @Override
    protected void cleanup(JComponent c, boolean remove) 
    {
        //remove = false; // SafeLogic HACK
        
        JTable source = (JTable)c;
                
        source.updateUI();
                
        if (remove && rows != null) {
            DefaultTableModel model =
                 (DefaultTableModel)source.getModel();

            //If we are moving items around in the same table, we
            //need to adjust the rows accordingly, since those
            //after the insertion point have moved.
            if (addCount > 0) {
                for (int i = 0; i < rows.length; i++) {
                    if (rows[i] > addIndex) {
                        rows[i] += addCount;
                    }
                }
            }
            
            for (int i = rows.length - 1; i >= 0; i--) {
                model.removeRow(rows[i]);
            }
        }
        rows = null;
        addCount = 0;
        addIndex = -1;
    }
//
//        colName.add("Id");
//        colName.add("Nom Centre");
//        colName.add("Nom Docteur");
//        colName.add("Type Centre");
//        colName.add("Ville");
//        colName.add("Id Doctor");
    
    private String formatCenterDoctor(String str)
    {
        String values[] = str.split(",");

        String formattedString = "";

        formattedString += values[0]  + "," + values[1] + "," + values[2] + "," + values[3] + "," + values[4] + "," + values[5]  ;

        return formattedString;
    }
}
