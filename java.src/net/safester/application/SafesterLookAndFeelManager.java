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

import com.formdev.flatlaf.FlatLightLaf;
import com.swing.util.Themes;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.swing.JComponent;
import javax.swing.Painter;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.safester.application.parms.Parms;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import net.safester.application.tool.UI_Util;
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

        // Allows to define a specific look & feel for testss
        File fileLookAndFeel = new File(getLafPath() + File.separator + "safester_look_and_feel.txt");

        if (fileLookAndFeel.exists()) {
            String className = FileUtils.readFileToString(fileLookAndFeel, Charset.defaultCharset());
            className = className.trim();
                       
            UIManager.setLookAndFeel(className);
            cleanNimbusBackground();
            return;
        }

        System.setProperty("sun.java2d.uiScale", "1.0");
        String lookAndFeel =  UserPrefManager.getPreference(UserPrefManager.LOOK_AND_FEEL_THEME, Themes.DEFAULT_THEME);
        UIManager.setLookAndFeel(lookAndFeel);
        
        /*
        if (SystemUtils.IS_OS_MAC) {
            setSystemLookAndfeel();
        } else if (SystemUtils.IS_OS_WINDOWS) {
            //setJTatoolLookAndFeel();
            //FlatDarculaLaf.install();
        } else if (SystemUtils.IS_OS_LINUX) {
            setNimbusLookAndFeel();
        } else {
            setSystemLookAndfeel();
        }
        */
        
        //cleanNimbusBackground();
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
    
    public static void setSystemLookAndfeel() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
    }

    public static void setJTatoolLookAndFeel()
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, UnsupportedLookAndFeelException {
    
        
        UIManager.setLookAndFeel(
                "com.jtattoo.plaf.acryl.AcrylLookAndFeel");
       
    }
        
    public static void setNimbusLookAndFeel() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            return;
        } catch (Exception e ) {
            e.printStackTrace();

        } 
        
        // Try another one 
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    
        public static void cleanNimbusBackground() {
        if (UI_Util.isNimbus()) {
            UIManager.put("EditorPane[Disabled].backgroundPainter",
                    new Painter<JComponent>() {
                @Override
                public void paint(Graphics2D g, JComponent comp,
                        int width, int height) {
                    g.setColor(comp.getBackground());
                    g.fillRect(0, 0, width, height);
                }
            });
        }
    }


}
