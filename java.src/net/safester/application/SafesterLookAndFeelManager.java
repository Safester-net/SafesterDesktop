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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import com.swing.util.Themes;
import javax.swing.JFrame;
import net.safester.application.parms.Parms;
import net.safester.application.util.UserPrefManager;

/**
 * Allows to easely test and define lool & feels.
 *
 * @author Nicolas de Pomereu
 */
public class SafesterLookAndFeelManager {

    public static void setLookAndFeel()
            throws IOException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, UnsupportedLookAndFeelException {

        JFrame.setDefaultLookAndFeelDecorated( true );
        
        // Allows to define a specific look & feel for testss
        File fileLookAndFeel = new File(getLafPath() + File.separator + "safester_look_and_feel.txt");

        if (fileLookAndFeel.exists()) {
            String className = FileUtils.readFileToString(fileLookAndFeel, Charset.defaultCharset());
            className = className.trim();
                       
            UIManager.setLookAndFeel(className);
            return;
        }

        //-Dsun.java2d.uiScale=1.0
        //System.setProperty("flatlaf.uiScale", "1.0");
        
        String scaling = UserPrefManager.getPreference(UserPrefManager.SCALING, "1.0");
        System.setProperty("flatlaf.uiScale", scaling);
        
        String lookAndFeel =  UserPrefManager.getPreference(UserPrefManager.LOOK_AND_FEEL_THEME, Themes.DEFAULT_THEME);
        UIManager.setLookAndFeel(lookAndFeel);
        
    }


    /**
     * Returns the path to dir where english and french dictionnaires are to be
     * stored
     *
     * @return
     */
    public static String getLafPath() {

        String filepath = SystemUtils.getUserHome() + File.separator + ".kawansoft" + File.separator + Parms.PRODUCT_NAME + File.separator + "laf";
        File dictFiles = new File(filepath);
        if (!dictFiles.exists()) {
            dictFiles.mkdirs();
        }

        return filepath;
    }
    
}
