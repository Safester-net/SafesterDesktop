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
import java.util.Arrays;
import java.util.List;
import javax.swing.UIManager;

import net.safester.application.util.UserPrefManager;

/**
 * Says if is Dark Mode, will contains getter for Colors, etc.
 * @author ndepo
 */
public class LookAndFeelHelper {

	//private static final Color SAFESTER_DARK_BG_COLOR = new Color(69, 73, 74);
	private static final Color SAFESTER_DARK_FG_COLOR = new Color(187, 187, 187);
    /**
     * Analyses if the Theme is in Dark mode.
     * @return true if Dark Mode.
     */
    public static boolean isDarkMode() {
        String lookAndFeel = UIManager.getLookAndFeel().getClass().getName();
        List<String> themesArray = Arrays.asList(Themes.LIGHT_THEMES);
        return !themesArray.contains(lookAndFeel);
    }
    
    /**
     * Returns the current Theme in use
     * @return the current Theme in use
     */
    public static String getCurrentTheme() {
        return UserPrefManager.getPreference(UserPrefManager.LOOK_AND_FEEL_THEME, Themes.DEFAULT_THEME);
    }
    
    /**
     * @return Default background color for Dark / Ligth mode
     */
    public static Color getDefaultBackgroundColor() {
    	return isDarkMode() ? UIManager.getColor("Panel.background") : Color.WHITE;
    }
    
    public static Color getDefaultForegroundColor() {
    	//return isDarkMode() ? UIManager.getColor(SAFESTER_DARK_FG_COLOR) : Color.BLACK;
        return isDarkMode() ? UIManager.getColor("TextField.foreground") : Color.BLACK;
    }
}
