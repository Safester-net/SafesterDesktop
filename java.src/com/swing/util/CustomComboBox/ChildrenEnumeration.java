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

import javax.swing.tree.TreeModel;

/**
 *
 * @author Alexandre Becquereau
 */
public class ChildrenEnumeration implements Enumeration<Object>{
    TreeModel treeModel;
    Object node;
    int index = -1;
    int depth;

    public ChildrenEnumeration(TreeModel treeModel, Object node){
        this.treeModel = treeModel;
        this.node = node;
    }

    @Override
    public boolean hasMoreElements(){
        return index<treeModel.getChildCount(node)-1;
    }

    @Override
    public Object nextElement(){
        return treeModel.getChild(node, ++index);
    }
} 
