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

package net.safester.application.register;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.Vector;

public class MyOwnFocusTraversalPolicy extends FocusTraversalPolicy
{
    Vector<Component> order;

    public MyOwnFocusTraversalPolicy(Vector<Component> order)
    {
        this.order = new Vector<Component>(order.size());
        this.order.addAll(order);
    }

    public Component getComponentAfter(Container focusCycleRoot,
                                       Component aComponent)
    {
//      int idx = (order.indexOf(aComponent) + 1) % order.size();
//      return order.get(idx);

        int idx = order.indexOf(aComponent);

        for (int i = 0; i < order.size(); i++)
        {
            idx = (idx + 1) % order.size();
            Component next = order.get(idx);

            if (canBeFocusOwner(next)) return next;
        }

        return null;
    }

    public Component getComponentBefore(Container focusCycleRoot,
                                        Component aComponent)
    {
/*
        int idx = order.indexOf(aComponent) - 1;
        if (idx < 0) {
            idx = order.size() - 1;
        }
        return order.get(idx);
*/
        int idx = order.indexOf(aComponent);

        for (int i = 0; i < order.size(); i++)
        {
            idx = (idx - 1);

            if (idx < 0)
            {
                idx = order.size() - 1;
            }

            Component previous = order.get(idx);

            if (canBeFocusOwner(previous)) return previous;
        }

        return null;
    }

    public Component getDefaultComponent(Container focusCycleRoot) {
//      return order.get(0);
        return getFirstComponent( focusCycleRoot );
    }

    public Component getLastComponent(Container focusCycleRoot) {
//      return order.lastElement();

        Component c = order.lastElement();

        if (canBeFocusOwner(c))
            return c;
        else
            return getComponentBefore(focusCycleRoot, c);
    }

    public Component getFirstComponent(Container focusCycleRoot)
    {
//      return order.get(0);

        Component c = order.get(0);

        if (canBeFocusOwner(c))
            return c;
        else
            return getComponentAfter(focusCycleRoot, c);
    }

    private boolean canBeFocusOwner(Component c)
    {
        if (c.isEnabled() && c.isDisplayable() && c.isVisible() && c.isFocusable())
        {
            return true;
        }

        return false;
    }

}

