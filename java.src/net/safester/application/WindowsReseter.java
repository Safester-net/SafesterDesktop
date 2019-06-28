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
package net.safester.application;

import java.awt.Window;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.JOptionPane;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.WindowSettingManager;

/**
 *
 * @author Nicolas de Pomereu
 */
public class WindowsReseter {

    public static void actionResetWindows(Window window) {
        MessagesManager messages = new MessagesManager();
        int response = JOptionPane.showConfirmDialog(window, messages.getMessage("the_windows_will_be_reset"), Parms.PRODUCT_NAME, JOptionPane.OK_CANCEL_OPTION);
        if (response != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            WindowSettingManager.resetAll();
        } catch (BackingStoreException ex) {
            Logger.getLogger(UserSettingsUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog(window, messages.getMessage("the_windows_have_been_reset"), Parms.PRODUCT_NAME, JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
    
}
