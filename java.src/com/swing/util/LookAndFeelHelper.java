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

import java.util.Arrays;
import java.util.List;
import javax.swing.UIManager;
import net.safester.application.util.UserPrefManager;

/**
 * Says if is Dark Mode, will contains getter for Colors, etc.
 * @author ndepo
 */
public class LookAndFeelHelper {

    /**
     * Analyses if the Theme is in Dark mode.
     * @return true if Dark Mode.
     */
    public static boolean isDarkMode() {
        String lookAndFeel = UIManager.getLookAndFeel().toString();
        List<String> themesArray = Arrays.asList(Themes.DARK_THEMES);
        return themesArray.contains(lookAndFeel);
    }
    
    /**
     * Returns the current Theme in use
     * @return the current Theme in use
     */
    public static String getCurrentTheme() {
        return UserPrefManager.getPreference(UserPrefManager.LOOK_AND_FEEL_THEME, Themes.DEFAULT_THEME);
    }
    
}
