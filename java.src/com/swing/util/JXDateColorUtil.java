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
import javax.swing.UIManager;
import org.jdesktop.swingx.JXDatePicker;

/**
 *
 * @author ndepo
 */
public class JXDateColorUtil {

    public static void setCalendarColors(JXDatePicker jXDatePicker) {

        if (LookAndFeelHelper.getCurrentTheme().equals(Themes.FLAT_INTELLIJ_LAF)) {
            return;
        }

        Color backgroundMain = UIManager.getColor("Panel.background");
        Color foregroundMain = UIManager.getColor("TextField.foreground");
        Color selectedBackgroundMain = UIManager.getColor("TextField.selectionBackground");

        // General Sertings
        jXDatePicker.getMonthView().setBackground(backgroundMain);
        jXDatePicker.getMonthView().setForeground(foregroundMain);

        // Top : the month
        jXDatePicker.getMonthView().setMonthStringBackground(selectedBackgroundMain);
        jXDatePicker.getMonthView().setMonthStringForeground(foregroundMain);

        // The list of months
        jXDatePicker.getMonthView().setDaysOfTheWeekForeground(foregroundMain);

        // Today & selected day
        jXDatePicker.getMonthView().setTodayBackground(Color.RED);
        jXDatePicker.getMonthView().setSelectionBackground(selectedBackgroundMain);
    }
}
