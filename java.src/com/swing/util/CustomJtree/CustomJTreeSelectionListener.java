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
package com.swing.util.CustomJtree;

import java.awt.Cursor;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import net.safester.application.Main;



public class CustomJTreeSelectionListener implements TreeSelectionListener {

    Main parent;
    JTree jTree;
    
    public CustomJTreeSelectionListener(Main caller, JTree theTree) {
        this.parent = caller;
        this.jTree = theTree;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {        
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));        
        parent.resetJTable();
        parent.createTable();
    }

}
