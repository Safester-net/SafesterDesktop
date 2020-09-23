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

import static net.safester.application.updater.InstallParameters.debug;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultRowSorter;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.JTableHeader;

import org.apache.commons.lang3.SystemUtils;

/**
 * Table utilities
 *
 * @author Nicolas de Pomereu
 */
public class TableUtil {

    public static final Color COLOR_HOVER_OUTLOOK_OFFICE = new Color(205, 230, 247);
    /**
     * To use for background selection
     */
    public static Color LIGHT_BLUE = new Color(243, 243, 255);

    public static Color VERY_LIGHT_GRAY = new Color(223, 223, 223);
    
    public static Color HOVER_COLOR = TableUtil.getMouseOverBackground();
            
    public static void selectRow(JTable jTable, int row) {
//        int rows = jTable.getRowCount();
//        List<Integer> listRrows = new Vector<Integer>();
//        for (int i = 0; i < rows; i++) {
//            listRrows.add(jTable.convertRowIndexToModel(i));
//        }

        ListSelectionModel selectionModel = jTable.getSelectionModel();
        selectionModel.setSelectionInterval(row, row);
        Rectangle rect = jTable.getCellRect(row, 0, true);
        jTable.scrollRectToVisible(rect);
    }

    /**
     * Says if we are on VMware when mouseClicked does doe work and we must use
     * mousePressed or mouseReleased
     *
     * @return
     */
    public static boolean isVMwareStation() {

        if (SystemUtils.IS_OS_MAC_OSX && new File(SystemUtils.USER_HOME + File.separator + "vmware.txt").exists()) {
            return true;
        } else {
            return false;
        }
    }

    public static int selectedRowFiles = -1;
    public static int selectedRowAddress = -1
            ;
    public static int selectedBoxLine = -1;
    public static int selectedSearchLine = -1;
        
    /**
     *
     * @param jTable the table to select the row on
     */
    public static void selectRowWhenMouverOverSearchLine(JTable jTable) {

        final JTable theTable = jTable;

        if (jTable.getRowCount() > 0) {
            try {
                theTable.setRowSelectionInterval(0, 0);
            } catch (Exception e) {
                //
            }
        }

        theTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                selectedSearchLine = theTable.rowAtPoint(p);
                theTable.repaint();
            }
        });

        theTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                selectedSearchLine = -1;
                theTable.repaint();
            }
        });
    }
    
     /**
     *
     * @param jTable the table to select the row on
     */
    public static void selectRowWhenMouverOverBoxLine(JTable jTable) {

        final JTable theTable = jTable;

        if (jTable.getRowCount() > 0) {
            try {
                theTable.setRowSelectionInterval(0, 0);
            } catch (Exception e) {
                //
            }
        }

        theTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                selectedBoxLine = theTable.rowAtPoint(p);
                theTable.repaint();
            }
        });

        theTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                selectedBoxLine = -1;
                theTable.repaint();
            }
        });
    }
    
    /**
     *
     * @param jTable the table to select the row on
     */
    public static void selectRowWhenMouverOverFile(JTable jTable) {

        final JTable theTable = jTable;

        if (jTable.getRowCount() > 0) {
            try {
                theTable.setRowSelectionInterval(0, 0);
            } catch (Exception e) {
                //
            }
        }

        theTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                selectedRowFiles = theTable.rowAtPoint(p);
                theTable.repaint();
            }
        });

        theTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                selectedRowFiles = -1;
                theTable.repaint();
            }
        });
    }

    /**
     * Selects the row when mouse is over a table of PDF filesSelects the row
     * when mouse is over a table of Recipients or Contacts
     *
     * @param jTable the table to select the row on
     */
    public static void selectRowWhenMouverOverAddressLine(JTable jTable) {

        final JTable theTable = jTable;

        theTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                selectedRowAddress = theTable.rowAtPoint(p);
                theTable.repaint();
            }
        });

        theTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                selectedRowAddress = -1;
                theTable.repaint();
            }
        });
    }

       public static int selectedRowRecipients = -1;
    public static int selectedRowPdfFiles = -1;

    /**
     *
     * @param jTable the table to select the row on
     */
    public static void selectRowWhenMouverOverRecipients(JTable jTable) {

        final JTable theTable = jTable;

        theTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                selectedRowRecipients = theTable.rowAtPoint(p);
                theTable.repaint();
            }
        });

        theTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                selectedRowRecipients = -1;
                theTable.repaint();
            }
        });
    }

    /**
     * Selects the row when mouse is over a table of PDF filesSelects the row
     * when mouse is over a table of Recipients or Contacts
     *
     * @param jTable the table to select the row on
     */
    public static void selectRowWhenMouverOverPdfFiles(JTable jTable) {

        final JTable theTable = jTable;

        theTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                selectedRowPdfFiles = theTable.rowAtPoint(p);
                theTable.repaint();
            }
        });

        theTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                selectedRowPdfFiles = -1;
                theTable.repaint();
            }
        });
    }

    /**
     *
     * Set the cursor type on a cell depending on the mouse position
     *
     * @param table the jtable
     * @param e the mouse event
     * @param columns the comumns on which hand cursor must be displayed
     */
    public static void setCursorTypeOnCell(JTable table, MouseEvent e, int[] columns) {
        Point p = e.getPoint();

        int column = table.columnAtPoint(p);
        int row = table.rowAtPoint(p);

        if (row < 0 || column < 0) {
            return;
        }

        Object value = table.getValueAt(row, column); // get the field value

        boolean isColumnOk = false;

        for (int i = 0; i < columns.length; i++) {

            if (columns[i] == column) {
                isColumnOk = true;
            }
        }

        //System.out.println("column : " + column);
        //System.out.println("value  : " + value);
        //if (row != - 1 && isColumnOk && value != null && !value.toString().isEmpty())
        if (row != - 1 && isColumnOk && value != null && value.toString().length() > 0) {
            Component c = table.getParent();
            c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            table.repaint();
        } else {
            Component c = table.getParent();
            c.setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Set the JTable column to sort and the asc/desc
     */
    public static final String DEFAULT_COLUMN_SORT = "DEFAULT_COLUMN_SORT";
    public static final String SORT_ORDER = "SORT_ORDER";

    /**
     * Sets the sort order for a table.
     *
     * @param clazz the class, used for preference id
     * @param jTable the jTable to sort
     */
    public static void setSortOrderFromPrefs(Class<?> clazz, JTable jTable) {

        String idTable = clazz.getSimpleName() + "_" + jTable.getName();

        int columnSort = UserPrefManager.getIntegerPreference(DEFAULT_COLUMN_SORT + "_" + idTable);
        String sortOrderStr = UserPrefManager.getPreference(SORT_ORDER + "_" + idTable);

        SortOrder theSortOrder = SortOrder.ASCENDING;
        if (sortOrderStr != null && sortOrderStr.equals("DESCENDING")) {
            theSortOrder = SortOrder.DESCENDING;
        }

        DefaultRowSorter<?,?> sorter = ((DefaultRowSorter<?,?>) jTable.getRowSorter());
        ArrayList<RowSorter.SortKey> list = new ArrayList<>();
        list.add(new RowSorter.SortKey(columnSort, theSortOrder));
        sorter.setSortKeys(list);
        sorter.sort();

        /*
        List<? extends RowSorter.SortKey> listSort = jTable.getRowSorter().getSortKeys();
        debug("sortKeys: " + listSort);
        for (RowSorter.SortKey sortKey : listSort) {
            debug("sortKey.getColumn(): " + sortKey.getColumn());
            debug("sortKey.getSortOrder(): " + sortKey.getSortOrder());
        }
        */
    }

//    /**
//     * Add a mouse listenr that will remember each user choice
//     *
//     * @param clazz the class, used for preference id
//     * @param jTable the jTable to sorte
//     */
//    
//    public static void rememberSortColumn(Class clazz, JTable jTable) {
//
//        JTableHeader jTableHeader = jTable.getTableHeader();
//        jTableHeader.setReorderingAllowed(false);
//
//        final JTable jTableFinal = jTable;
//        final String idTable = clazz.getSimpleName() + "_" + jTable.getName();
//
//        // Will store the column the user choose for sort
//        jTableHeader.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent mouseEvent) {
//                /*
//                 int index = jTable.convertColumnIndexToModel(jTable.columnAtPoint(mouseEvent.getPoint()));
//                 if (index >= 0) {
//                 VaultUserPrefManager.setPreference(FileTableParms.DEFAULT_COLUMN_SORT + "_" + id_folder, index);
//                 }
//                 */
//
//                List<? extends RowSorter.SortKey> listSort = jTableFinal.getRowSorter().getSortKeys();
//
//                //debug("sortKeys: " + listSort);
//                for (RowSorter.SortKey sortKey : listSort) {
//                    debug("sortKey.getColumn(): " + sortKey.getColumn());
//                    debug("sortKey.getSortOrder(): " + sortKey.getSortOrder());
//
//                    new UserPreferencesManager().setPreference(DEFAULT_COLUMN_SORT + "_" + idTable, sortKey.getColumn());
//                    new UserPreferencesManager().setPreference(SORT_ORDER + "_" + idTable, sortKey.getSortOrder().toString());
//
//                    break;
//                }
//            }
//        });
//    }
    /**
     * @return the light table color used for mouse over
     */
    public static Color getMouseOverBackground() {

        

//        if (SystemUtils.IS_OS_WINDOWS) {
//            lightColor = COLOR_HOVER_OUTLOOK_OFFICE;
//        } else {
//            // No good color on Mac OS & Linux.
//            UIDefaults defaults = UIManager.getDefaults();
//            lightColor = defaults.getColor("Table.selectionBackground");
//        }

        Color lightColor = null;
        if (SystemUtils.IS_OS_MAC){
            lightColor = COLOR_HOVER_OUTLOOK_OFFICE;   
        }
        else {
            lightColor = VERY_LIGHT_GRAY;
        }
        
        return lightColor;

    }
  /**
     * Add a mouse listenr that will remember each user choice
     *
     * @param clazz the class, used for preference id
     * @param jTable the jTable to sorte
     */
    public static void rememberSortColumn(Class<?> clazz, JTable jTable) {

        JTableHeader jTableHeader = jTable.getTableHeader();
        jTableHeader.setReorderingAllowed(false);

        final JTable jTableFinal = jTable;
        final String idTable = clazz.getSimpleName() + "_" + jTable.getName();

        // Will store the column the user choose for sort
        jTableHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                /*
                 int index = jTable.convertColumnIndexToModel(jTable.columnAtPoint(mouseEvent.getPoint()));
                 if (index >= 0) {
                 VaultUserPrefManager.setPreference(FileTableParms.DEFAULT_COLUMN_SORT + "_" + id_folder, index);
                 }
                 */

                List<? extends RowSorter.SortKey> listSort = jTableFinal.getRowSorter().getSortKeys();

                //debug("sortKeys: " + listSort);
                for (RowSorter.SortKey sortKey : listSort) {
                    debug("sortKey.getColumn(): " + sortKey.getColumn());
                    debug("sortKey.getSortOrder(): " + sortKey.getSortOrder());

                    UserPrefManager.setPreference(DEFAULT_COLUMN_SORT + "_" + idTable, sortKey.getColumn());
                    UserPrefManager.setPreference(SORT_ORDER + "_" + idTable, sortKey.getSortOrder().toString());

                    break;
                }
            }
        });
    }
    public static Color getMouseOverForeground() {
            return Color.black;
    }
}
