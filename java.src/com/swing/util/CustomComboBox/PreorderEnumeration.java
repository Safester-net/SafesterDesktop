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
import java.util.Stack;
import java.util.Vector;

import javax.swing.tree.TreeModel;

/**
 *
 * @author Alexandre Becquereau
 */
public class PreorderEnumeration implements Enumeration<Object>{
    private TreeModel treeModel;
    protected Stack<Object>stack;
    private int depth = 0;


    public PreorderEnumeration(TreeModel treeModel){
        this.treeModel = treeModel;
        Vector<Object> v = new Vector<>(1);
        v.addElement(treeModel.getRoot());
        stack = new Stack<>();
        stack.push(v.elements());
    }

    @Override
    public boolean hasMoreElements(){
        return (!stack.empty() &&
                ((Enumeration<?>)stack.peek()).hasMoreElements());
    }

    @Override
    public Object nextElement(){
        Enumeration<?> enumer = (Enumeration<?>)stack.peek();
        Object node = enumer.nextElement();
        depth = enumer instanceof ChildrenEnumeration
                ? ((ChildrenEnumeration)enumer).depth
                : 0;
        if(!enumer.hasMoreElements())
            stack.pop();
        ChildrenEnumeration children = new ChildrenEnumeration(treeModel, node);
        children.depth = depth+1;
        if(children.hasMoreElements()){
            stack.push(children);
        }
        return node;
    }

    public int getDepth(){
        return depth;
    }

} 
