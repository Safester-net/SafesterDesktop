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
package com.swing.util.CustomComboBox;

import java.util.Enumeration;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.tree.TreeModel;

/**
 *
 * @author Alexandre Becquereau
 */
public class TreeListModel extends AbstractListModel implements ComboBoxModel{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2876955012273694027L;
	private TreeModel treeModel;
    private Object selectedObject;

    public TreeListModel(TreeModel treeModel){
        this.treeModel = treeModel;
    }

    @Override
    public int getSize(){
        int count = 0;
        Enumeration<?> enumer = new PreorderEnumeration(treeModel);
        while(enumer.hasMoreElements()){
            enumer.nextElement();
            count++;
        }
        return count;
    }

    @Override
    public Object getElementAt(int index){
        Enumeration<?> enumer = new PreorderEnumeration(treeModel);
        for(int i=0; i<index; i++)
            enumer.nextElement();
        return enumer.nextElement();
    } 

    @Override
    public void setSelectedItem(Object anObject){
        if((selectedObject!=null && !selectedObject.equals(anObject)) ||
                selectedObject==null && anObject!=null){
            selectedObject = anObject;
            fireContentsChanged(this, -1, -1);
        }
    }

    @Override
    public Object getSelectedItem() {
        return selectedObject;
    }
} 
