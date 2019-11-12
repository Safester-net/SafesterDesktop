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

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.Painter;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import net.safester.application.tool.UI_Util;

/**
 * Allows to easely test and define lool & feels.
 *
 * @author Nicolas de Pomereu
 */
public class SafesterLookAndFeelManager {


    public SafesterLookAndFeelManager() {

    }

    public static void setLookAndFeel()
            throws IOException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, UnsupportedLookAndFeelException {

        // Allows to define a specific look & feel for testss
        File fileLookAndFeel = new File(SystemUtils.USER_DIR + File.separator + "safester_look_and_feel.txt");

        if (fileLookAndFeel.exists()) {
            String className = FileUtils.readFileToString(fileLookAndFeel);
            className = className.trim();
            UIManager.setLookAndFeel(className);
            cleanNimbusBackground();
            return;
        }

        if (SystemUtils.IS_OS_MAC) {
            setSystemLookAndfeel();
        } else if (SystemUtils.IS_OS_WINDOWS) {
            setJTatoolLookAndFeel();
        } else if (SystemUtils.IS_OS_LINUX) {
            setNimbusLookAndFeel();
        } else {
            setSystemLookAndfeel();
        }

        cleanNimbusBackground();
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
        
        
        //UIManager.setLookAndFeel(
        //        "com.jtattoo.plaf.noire.NoireLookAndFeel");
        
        boolean isDarkMode = false;
        if (isDarkMode) {
            setDarkTheme();
        }

    }

    private static void setDarkTheme() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
         UIManager.put( "control", new Color( 128, 128, 128) );
         UIManager.put( "info", new Color(128,128,128) );
         UIManager.put( "nimbusBase", new Color( 18, 30, 49) );
         UIManager.put( "nimbusAlertYellow", new Color( 248, 187, 0) );
         UIManager.put( "nimbusDisabledText", new Color( 128, 128, 128) );
         UIManager.put( "nimbusFocus", new Color(115,164,209) );
         UIManager.put( "nimbusGreen", new Color(176,179,50) );
         UIManager.put( "nimbusInfoBlue", new Color( 66, 139, 221) );
         UIManager.put( "nimbusLightBackground", new Color( 18, 30, 49) );
         UIManager.put( "nimbusOrange", new Color(191,98,4) );
         UIManager.put( "nimbusRed", new Color(169,46,34) );
         UIManager.put( "nimbusSelectedText", new Color( 255, 255, 255) );
         UIManager.put( "nimbusSelectionBackground", new Color( 104, 93, 156) );
         UIManager.put( "text", new Color( 230, 230, 230) );

        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }

    }
        
    public static void setNimbusLookAndFeel() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
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
