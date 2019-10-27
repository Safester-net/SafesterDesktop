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
package net.safester.application.tool;

    // KEEP THIS COMMENT - Taille des fenetres : Slide 72 : 1:1 / 4:5 / 4:3 / 16:9
    // KEEP THIS COMMENT - 1  / 0,80 / 1,33 / 1,78

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import net.safester.application.util.UserPrefManager;

public class WindowSettingManager {
    

    /** Not visible constructor */
    protected WindowSettingManager() {
    }

    public static void save(Window window) {
        int width = window.getWidth();
        int height = window.getHeight();

        if (WindowSettingManagerParms.DEBUG) {
            System.out.println();
            System.out.println("window size : (" + width + ", " + height + ")");
            System.out.println("ratio w/h   : " + (float) width / height);
            System.out.println("ratio h/w   : " + (float) height / width);
        }

        String jframeName = window.getClass().getName();
        WindowSettingManager windowSettingMgr = new WindowSettingManager();

        Preferences prefs = Preferences.userNodeForPackage(windowSettingMgr.getClass());
        prefs.put("x_" + jframeName, "" + window.getX());
        prefs.put("y_" + jframeName, "" + window.getY());
        prefs.put("w_" + jframeName, "" + window.getWidth());
        prefs.put("h_" + jframeName, "" + window.getHeight());
    }

    /**
     * Load the JFrame settting at stored position.
     * Defaults to center of Window.
     */
    public static void load(Window window) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        Point middlePoint = new Point((dim.width / 2) - (window.getWidth() / 2),
                (dim.height / 2) - (window.getHeight() / 2));

        load(window, middlePoint);
    }

    /**
     * Load the JFrame settting at stored location. Defaults to Passed Point
     *
     * @param defaultPoint the default position if the location is not stored
     */
    public static void load(Window window, Point defaultPoint) {

        window.setLocation(defaultPoint);

        String jframeName = window.getClass().getName();

        WindowSettingManager windowSettingMgr = new WindowSettingManager();

        Preferences prefs = Preferences.userNodeForPackage(windowSettingMgr.getClass());
        String frame_x = prefs.get("x_" + jframeName, "0");
        String frame_y = prefs.get("y_" + jframeName, "0");
        String frame_w = prefs.get("w_" + jframeName, "0");
        String frame_h = prefs.get("h_" + jframeName, "0");

        int x = 0;
        int y = 0;
        int w = 0;
        int h = 0;

        try {
            x = Integer.parseInt(frame_x);
            y = Integer.parseInt(frame_y);
            w = Integer.parseInt(frame_w);
            h = Integer.parseInt(frame_h);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }

        if (x == 0 && y == 0 && w == 0 && h == 0) {
            if (WindowSettingManagerParms.DEBUG) {
                System.out.println();
                System.out.println("window width : " + window.getWidth());
                System.out.println("window height: " + window.getHeight());
                System.out.println("window ratio w/h: " + (float) ((float) window.getWidth() / (float) window.getHeight()));
            }

            return;
        }

        window.setLocation(x, y);
        window.setSize(new Dimension(w, h));

        if (WindowSettingManagerParms.DEBUG)
        {
            System.out.println();
            System.out.println("window width : " + window.getWidth());
            System.out.println("window height: " + window.getHeight());
            System.out.println("window ratio w/h: " + (float) ((float) window.getWidth() / (float) window.getHeight()));
        }

    }

    /**
     * Displays the windo settings
     * @param window    the window to display
     */
    public static void displayWindowSettings(Window window)
    {
        System.out.println();
        System.out.println("window width : " + window.getWidth());
        System.out.println("window height: " + window.getHeight());
        System.out.println("window ratio w/h: " + (float) ((float) window.getWidth() / (float) window.getHeight()));
    }

    /**
     * Realod the JFrame settting
     */
    public static void reload(Window window) {
        save(window);
        load(window);
    }

    /**
     * Reset all Windows
     * @throws BackingStoreException
     */
    public static void resetAll()
            throws BackingStoreException {
        WindowSettingManager windowSettingMgr = new WindowSettingManager();
        Preferences prefs = Preferences.userNodeForPackage(windowSettingMgr.getClass());
        prefs.clear();
        
        // Add Split Panes info
        UserPrefManager.removePreference(UserPrefManager.READING_PANE_POSITION);
        UserPrefManager.removePreference(UserPrefManager.FOLDER_SECTION_IS_INACTIVE);
        
        UserPrefManager.removePreference(UserPrefManager.SPLIT_PANE_FOLDERS_LOC);
        UserPrefManager.removePreference(UserPrefManager.SPLIT_PANE_MESSAGE_LOC_VERTICAL_SPLIT);
        UserPrefManager.removePreference(UserPrefManager.SPLIT_PANE_MESSAGE_LOC_HORIZONTAL_SPLIT);

    }

    /**
     * Displays the specified message if the DEBUG flag is set.
     * @param   sMsg    the debug message to display
     */
    protected void debug(String sMsg) {
        if (WindowSettingManagerParms.DEBUG) {
            System.out.println("DBG> " + sMsg);
        }
    }
}
