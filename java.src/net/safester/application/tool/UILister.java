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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class UILister {
    public static void main(String[] args) {
        try {

            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            
            UIDefaults defaults1 = UIManager.getDefaults();
            Enumeration keys = defaults1.keys();

            Color color = (Color) defaults1.getColor("Label.background");
            System.out.println(color.getGreen());
            System.out.println(color.getRed());
            System.out.println(color.getBlue());
            
            List<String> orderedSettings = new Vector<String>();
            
            while (keys.hasMoreElements())
            {
                Object key = keys.nextElement();
                Object value = defaults1.get(key);
                
                //if (key.toString().toLowerCase().contains("html"))                 
                //System.out.println(key + " " + value);
                
                if (value != null)
                {
                    UIManager.put(key, null);
                    Font font = UIManager.getFont(key);

                    orderedSettings.add(key + " " + font);                
                }
            }
            
            //if (true) return;
            
            Set defaults = UIManager.getLookAndFeelDefaults().entrySet();



//            TreeSet ts = new TreeSet(new Comparator() {
//                public int compare(Object a, Object b) {
//                    Map.Entry ea = (Map.Entry) a;
//                    Map.Entry eb = (Map.Entry) b;
//                    
//                    //StringBuffer s1 = (StringBuffer) (ea.getKey());
//                    //StringBuffer s2 = new StringBuffer((String)eb.getKey());
//                    
//                    //return ((String) ea.getKey()).compareTo(((String)
//                    //        eb.getKey()));
//                    
//                    //return (s1.toString().compareTo(s2.toString()));
//                    
//                    return 1;
//                }
//            });
//            
            Set ts = new HashSet();
            
            ts.addAll(defaults);
            Object[][] kvPairs = new Object[defaults.size()][2];
            Object[] columnNames = new Object[] { "Key", "Value" };
            int row = 0;
            for (Iterator i = ts.iterator(); i.hasNext();) {
                Object o = i.next();
                Map.Entry entry = (Map.Entry) o;
                kvPairs[row][0] = entry.getKey();
                kvPairs[row][1] = entry.getValue();
                row++;
            }

            JTable table = new JTable(kvPairs, columnNames);
            
            RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());

            table.setRowSorter(sorter);
            
            JScrollPane tableScroll = new JScrollPane(table);

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER,
                    6, 6));
            buttons.add(closeButton, null);

            JPanel main = new JPanel(new BorderLayout());
            main.add(tableScroll, BorderLayout.CENTER);
            main.add(buttons, BorderLayout.SOUTH);

            JFrame frame = new JFrame("UI Properties");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(main);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

