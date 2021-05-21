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
package com.swing.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.List;
import javax.swing.JButton;
import javax.swing.UIManager;

/**
 * Methods for Color management. For Light & Dark Mode, etc.
 * @author ndepo
 */
public class SwingColorUtil {

    public static Color HYPERLINK_LIGHT = new Color(38, 117, 191);
    public static Color HYPERLINK_DARK_MODE = new Color(88, 157, 246);
   
    public static Color LIGHT_RED = new Color(255, 58,58);

    /**
     * Changes the hyperlink buttons blue foreground depending if we are in
     * Light or Dark Mode.
     *
     * @param container
     */
    public static void setHyperLinkButtonsTextColor(Container container) {
        List<Component> components = SwingUtil.getAllComponants(container);
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton jButton = (JButton) component;
                if (!isAnHyperLinkButton(jButton)) {
                    continue;
                }

                if (LookAndFeelHelper.isDarkMode()) {
                    jButton.setForeground(HYPERLINK_DARK_MODE);
                } else {
                    jButton.setForeground(UIManager.getColor("TextField.selectionBackground"));
                }
                     
            }
        }
    }

    /**
     * Analyses if the passed Jbutton is an hyperlink, aka no content aera and
     * not painted border.
     *
     * @param jButton
     * @return
     */
    private static boolean isAnHyperLinkButton(JButton jButton) {
        return (!jButton.isContentAreaFilled() && !jButton.isBorderPainted());
    }
    
    public static Color getSeparatorColor() {        
        return getThemeColor();
    }

    /**
     * The background selection color is in fact the Theme Color
     * @return The background selection color
     */
    public static Color getThemeColor() {
        Color separatorColor = UIManager.getColor("TextField.selectionBackground");
        return separatorColor;
    }
    
    
}
