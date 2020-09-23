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

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 *
 * @author RunningLiberty
 */
public class UI_Utils {

    public static void reduceFontForUI(String UIobject, int reduceAmount) {

        Font oldFont = UIManager.getFont(UIobject);

        float size = oldFont.getSize();

        size -= reduceAmount;

        UIManager.put(UIobject, oldFont.deriveFont(size));
        Font newFont = UIManager.getFont(UIobject);

        System.out.println("oldFont for " + UIobject + ": " + oldFont);
        System.out.println("newFont for " + UIobject + ": " + newFont);
    }

 public static void setUIScale(double scale)
    {
        UIDefaults defaults = UIManager.getDefaults();
        Enumeration<?> keys = defaults.keys();

        while (keys.hasMoreElements())
        {
            Object key = keys.nextElement();
            Object value = defaults.get(key);

            if (value != null && value instanceof Font)
            {
                UIManager.put(key, null);
                Font font = UIManager.getFont(key);

                if (font != null)
                {
                    float size = font.getSize2D();
                    UIManager.put(key, new FontUIResource(font.deriveFont(size
                            * (float)scale)));
                }
            }
        }
    }
 
}
