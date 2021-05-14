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

import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.Map;
import net.safester.application.util.UserPrefManager;

/**
 * Themes management for Safester Look & Feel
 * @author ndepo
 */
public class Themes {
    
    // Ligt
    public static final String FLAT_INTELLIJ_LAF = com.formdev.flatlaf.FlatIntelliJLaf.class.getName();
    public static final String FLAT_ARCORANGEIJ_THEME = com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme.class.getName();
    
    // Dark
    public static final String FLAT_DARCULA_LAF = com.formdev.flatlaf.FlatDarculaLaf.class.getName();
    public static final String FLAT_DARK_PURPLEIJ_THEME = com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme.class.getName();
    
    public static final String DEFAULT_THEME = FLAT_INTELLIJ_LAF;
    
    public static  String [] LIGT_THEMES = {FLAT_INTELLIJ_LAF, FLAT_ARCORANGEIJ_THEME};
    public static  String [] DARK_THEMES = {FLAT_DARCULA_LAF, FLAT_DARK_PURPLEIJ_THEME};
    
    public static  String [] THEME_NAMES = {"Flat IntelJ", "Arc Orange", "Flat Darkula", "Dark Purple" };
    public static  String [] THEME_VAUES = {FLAT_INTELLIJ_LAF, FLAT_ARCORANGEIJ_THEME, FLAT_DARCULA_LAF, FLAT_DARK_PURPLEIJ_THEME};
    
    /** Map of (Theme name, Theme class) */
    private static Map<String, String> map = new HashMap<>();
       
    /**
     * Builds the map of (Theme name, Theme class)
     */
    private static void buildDefaultMap()
    {
        int length = THEME_NAMES.length;
        
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < length; i++) {
            map.put(THEME_NAMES[i], THEME_VAUES[i]);
        }
    }
    
    /**
     * Builds the map of (Theme name, Theme class).
     * @return the map of (Theme name, Theme class).
     */
    public static Map<String, String> getThemes()
    {
        if (map.isEmpty()) {
            buildDefaultMap();
        }
        
        return map;
    }
    
    public static String getCurrentTheme() {
        return UserPrefManager.getPreference(UserPrefManager.LOOK_AND_FEEL_THEME, DEFAULT_THEME);
    }
}
    
