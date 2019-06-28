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


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import net.safester.application.messages.LanguageManager;
import net.safester.application.messages.MessagesManager;

import org.apache.commons.lang3.SystemUtils;

import com.safelogic.utilx.io.stream.LineInputStream;
import java.awt.Color;
import java.awt.Window;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
import net.safester.application.tool.UI_Util;



/**
 * @author Nicolas de Pomereu
 */

public class SwingUtil
{
    public static final String CR_LF = System.getProperty("line.separator") ;
    private static final boolean SET_BACKGROUND_NOT_ACTIVE = true;

    /**
     * Format the HTML content for Synthetica ==> increase font size per +1
     * @param content   the html content to format 
     * @return the formated html content
     */
    public static String formatHtmlContentForSyntheticaAndNimbus(String content) {
        if (UI_Util.isSynthetica() || UI_Util.isNimbus() ) {
            content = content.replaceAll("size=4", "size=5");
            content = content.replaceAll("size=6", "size=7");
        }
        return content;
    }
    
    /**
     * Format the JPanel for Synthetica ==> remove border
     * @param jpanel the panel to format
     * 
     * @return the JPanel without border
     */
    public static void formatJpanelBorderForSynthetica(JPanel jpanel) {
        if (UI_Util.isSynthetica()) {
         jpanel.setBorder(null);
        }
    }
    
    /**
     * Format the JPanel containing a JXTextField for Synthetica
     * ==> add a line border thickness 1 and rounded
     * @param jPanel
     * @param jpanel the panel containing the JXTextField to  format
     */
    public static void formatJXTextFieldForSynthetica(JPanel jPanel) {
        if (UI_Util.isSynthetica()) {
         jPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        }
    }    
    
     /**
     * Allows to set the background color for the window
     * @param window
     * @param color the color 
     */
    public static void setBackgroundColor(Window window, Color color) {
        
        if (SET_BACKGROUND_NOT_ACTIVE) {
            return;
        }
        
        setBackgroundColor(window, color, true);
    } 
    
     /**
     * Allows to set the background color for the window
     * @param color the color 
     */
    public static void setBackgroundColor(Window window, Color color, boolean includeJTextField) {
        java.util.List<Component> components = SwingUtil.getAllComponants(window);

        for (int i = 0; i < components.size(); i++) {
            Component comp = components.get(i);

            if (comp instanceof JPanel || comp instanceof JCheckBox || comp instanceof JRadioButton) {
                comp.setBackground(color);
            }
            
            if (includeJTextField) {
             if (comp instanceof JTextField) {
                 JTextField jTextField = (JTextField)comp;
                 if (! jTextField.isEditable()) {
                     jTextField.setBackground(color);
                     jTextField.setBorder(null);
                     jTextField = addColonBeforeDisplay(jTextField);
                 }
             }                 
            }                       
        }        
    }     
    
    
    public static JTextField addColonBeforeDisplay(JTextField jTextField) {
        String text = jTextField.getText();

        if (!text.startsWith(": ")) {
            text = ": " + text;
            jTextField.setText(text);
        }

        return jTextField;
    }
    
    public static JLabel addColonBeforeDisplay(JLabel jLabel) {
        String text = jLabel.getText();

        if (!text.startsWith(": ")) {
            text = ": " + text;
            jLabel.setText(text);
        }

        return jLabel;
    }    
    
    
        
    /**
     * Resize jComponents for Nimbus look and feel
     * @param container
     */
    public static void resizeJComponentsForNimbusAndMacOsX(Container container){
        
         // To be done for all Nimbus + Mac OS X
         if (! UI_Util.isNimbus() && ! SystemUtils.IS_OS_MAC_OSX)
         {
             return;
         }

        List<Component> components = SwingUtil.getAllComponants(container);
        for (Component component : components) {
            int maxWidth = (int)component.getMaximumSize().getWidth();
            int minWidth = (int)component.getMinimumSize().getWidth();
            int prefWidth = (int)component.getPreferredSize().getWidth();

            int newHeight = 26;
            
            if(component instanceof JTextField || component instanceof JPasswordField){
                component.setMaximumSize(new Dimension(maxWidth, newHeight));
                component.setMinimumSize(new Dimension(minWidth, newHeight));
                component.setPreferredSize(new Dimension(prefWidth, newHeight));
            }else if(component instanceof JLabel){
                
                int maxHeigth = (int)component.getMaximumSize().getHeight();
                int minHeigth = (int)component.getMinimumSize().getHeight();
                int prefHeigth = (int)component.getPreferredSize().getHeight();
                //int heigth = (int)component.getSize().getHeight();

                component.setMaximumSize(new Dimension(maxWidth, maxHeigth + 2));
                component.setMinimumSize(new Dimension(minWidth, minHeigth + 2));
                component.setPreferredSize(new Dimension(prefWidth, prefHeigth + 2));
                //component.setSize(new Dimension(prefWidth, heigth + 2));
            }
        }
    }
    
    /**
     * Return all Components contained in a Container
     * 
     * @param container The Container        
     * @return          The complete list of inside Components                     
     */
    public static List<Component> getAllComponants(Container container)
    {
        List<Component> componentList  = new Vector();
        
        getAllComponents(container, componentList);
        
        return componentList;
    }

    /**
     * Disable or enablez a Tool bar
     * @param enable        true or false
     * @param jToolBar      the tool bar to enable disable
     */
    public static void enableToolbar( JToolBar jToolBar, boolean enable)
    {
        for(Component comp : jToolBar.getComponents())
        {
           comp.setEnabled(enable);
        }
    }
    
    /**
     *     
     * Get all the components inside a component and put it in a collection.
     * Recursiv method
     * 
     * @param c              The Component
     * @param collection     The collection to store in the result 
     */
    private static void getAllComponents(Component c, Collection collection) {
        collection.add(c);
        if (c instanceof Container) {
          Component[] kids = ((Container)c).getComponents();
          for(int i=0; i<kids.length; i++)
            getAllComponents(kids[i], collection);
        }
      }

       /**
     * Return  the  content of a  Text resource file in the message file package
     * @param fileReference    The key word for text file retrieve
     * @return  the text content of a  Text resource file in the message file package
     *
     * @throws IOException
     */
    public static String  getTextContent(String fileReference)
    {
        String content;
        try
        {

            if (fileReference == null)
            {
                throw new IllegalArgumentException("fileReference can not be null!");
            }

            fileReference = fileReference.toLowerCase();

            String language = LanguageManager.getLanguage();

            String resource = "/" + MessagesManager.MESSAGE_FILES_PACKAGE;
            resource = resource.replace(".", "/");

            // KEEP THIS CODE AS MODEL
            //java.net.URL myURL
            //  = ResourceBundleTest.class.getResource("/com/safelogic/pgp/test/MyResource_fr.properties");



            String helpFile =  fileReference + "_" + language + ".txt";
            String urlResource = resource + "/" + helpFile;

            //debug(urlResource);

            java.net.URL myURL = SwingUtil.class.getResource(urlResource);

            if (myURL == null)
            {
                return "<font face=\"Arial\" size=4><br>"
                      + "<b>Please apologize. <br>  "
                      + "Help is not yet available for this topic. </b> <br>"
                      + "<br>"
                      + "(" +  helpFile + ")";
            }

            InputStream is = myURL.openStream();

            BufferedInputStream bisIn = new BufferedInputStream(is) ;
            LineInputStream lisIn = new LineInputStream(bisIn) ;

            String sLine = new String() ;

            content = "";

            //int cpt = 0;

            while( (sLine = lisIn.readLine()) != null)
            {
                sLine = sLine.trim() ;
                //debug(sLine);

                if (content.equals(""))
                {
                    content = sLine;
                }
                else
                {
                    content += CR_LF + sLine;
                }

//                if (cpt == 0)
//                {
//                    content =  sLine;
//                }
//                else
//                {
//                    content += CR_LF + sLine;
//                }
//
//                cpt++;

            }

            lisIn.close() ;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            content = e.getMessage();
        }

        return content;
    }

}

/**
 * 
 */
