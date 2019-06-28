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

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

/**
 * To be called by all JList in Safester.
 * 
 * @author Nicolas de Pomereu
 */
public class JListUtil {

    /**
     * To be called by all Safester JList : sets a border to a JList & spacing between items.
     * @param jList 
     */
    public static void formatSpacing(JList jList) {
        jList.setBorder(new EmptyBorder(3,3,3,3));
        jList.setFixedCellHeight(22);
        jList.ensureIndexIsVisible(0);
    }
    

    public static int selectedItem = -1;
        
     /**
     *
     * @param jList the JList to select the row on
     */
    public static void selectItemWhenMouverOver(JList jList) {

        final JList theList = jList;
        
        theList.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                selectedItem = theList.locationToIndex(e.getPoint());
                theList.repaint();
            }
        });

        theList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                selectedItem = -1;
                theList.repaint();
            }
        });
    }    
}
