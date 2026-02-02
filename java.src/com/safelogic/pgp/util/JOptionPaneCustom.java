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
package com.safelogic.pgp.util;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jdesktop.swingx.error.ErrorInfo;

import com.safelogic.pgp.version.Version;
import com.safelogic.utilx.Debug;
import com.safelogic.utilx.StringMgr;

/**
 * @author Nicolas de Pomereu
 *
 */
public class JOptionPaneCustom
{
    /** The stack trace */
    private static String m_stackTrace;

    /**
     * Forbid instanciation
     */
    private JOptionPaneCustom()
    {
        
    }

    /**
     * Display the Exception and the Header Error Message in a special JXErrorPane  
     *                                                     
     * @param component             the parent component
     * @param e                     the Exception trapped
     * @param headerErrorMessage    the Error Message to display as header of the Exception Panel
     */
    public static void showException(Component component, Exception e, String headerErrorMessage)
    {
        // We are *very* cautious. If anything bad happens, we will have a simple clean display
        try
        {
            m_stackTrace = Debug.GetStackTrace(e);
            buildExceptionPanel(component, e, headerErrorMessage); 
        }
        catch (HeadlessException exception)
        {
            System.out.println(headerErrorMessage);
            System.out.println();
            System.out.println();
            System.out.println(e.getStackTrace());
        }         
        catch (Exception exception)
        {
            // Cut the stack trace                          
            m_stackTrace = Util.cut(m_stackTrace, 500);                
            JOptionPane.showMessageDialog(component, e.toString() + Util.CR_LF + m_stackTrace);
        }        
    }
    
    /**
     * Display the Exception and the Header Error Message in a special JXErrorPane  
     *                                                     
     * @param component             the parent component
     * @param e                     the Exception trapped
     * @param headerErrorMessage    the Error Message to display as header of the Exception Panel
     *
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static void showException(Component component, Exception message) 
    {        
        showException(component, message, null);
    }     
    
    private static String DEFAULT_FONT          = "<font face=arial>"; 
    private static String DEFAULT_FONT_SIZE     = "<font face=arial size=3>"; 
    private static String DEFAULT_FONT_RED_SIZE = "<font face=arial size=3 color=red>"; 
    
    /**
     * Display the Exception in a special JXErrorPane  
     *                                                     
     * @param component             the parent component
     * @param e                     the Exception trapped
     * @param applicationmessage    the Message to pass as header of the Exception Panel
     */    
    private static void buildExceptionPanel(Component component, Exception e, String applicationMessage)
    {
        
        String title    = null;
        String basicErrorMessage = null;           
        String detailedErrorMessage = null;
        String category = null;
        Level errorLevel = null;
        Map<String, String> state= null; 
        
        if (applicationMessage == null)
        {
            applicationMessage =  e.toString();            
        }
        
        String weAreSorryAnErrorOccured = "We are sorry - an error has occurred.<br>Please copy and send the Details below to support@cgeep.com";
        
        //MessagesManager messages = new MessagesManager();
        //if (! messages.getMessage("we_are_sorry_an_error_occured").equalsIgnoreCase("we_are_sorry_an_error_occured"))
        //{
        //    weAreSorryAnErrorOccured = messages.getMessage("we_are_sorry_an_error_occured");
        //}
        
        title    = "cGeep Error"; 
        basicErrorMessage  = 
           "<b>" + DEFAULT_FONT_RED_SIZE + applicationMessage + "</font>"
           + "<br><br>" 
           + DEFAULT_FONT_SIZE
           + weAreSorryAnErrorOccured
           + "</b>";    

        m_stackTrace = StringMgr.ReplaceAll(m_stackTrace, "\n", "\n<br>"); 
        m_stackTrace = StringMgr.ReplaceAll(m_stackTrace, "\t", "\t&nbsp;&nbsp;&nbsp;&nbsp;"); 
        
        String cGeepInfo = Version.getVersionWithDate() + " (" + System.getProperty("user.dir") + ")";                
        String systemInfo = getSystemInfo();
        
        detailedErrorMessage =
              "<h3>" + DEFAULT_FONT + title + " - " + new Date() + "</font></h3>"
            +  DEFAULT_FONT_SIZE
            + "<b>cGeep Info:</b><br>" + cGeepInfo
            + "<p>"
            + "<b>System:</b><br>" + systemInfo
            + "<p>"   
            + "<b>Message:</b><br>" + applicationMessage
            + "<p>"                          
            + "<b>Stack Trace:</b><br>" 
            + "<table width=100%>"
            + "<tr><td nowrap>"
            + DEFAULT_FONT_SIZE + m_stackTrace.trim()
            +  "</td></tr>"
            + "</table>";
                    
         ErrorInfo errorInfo = new ErrorInfo(title, 
                                            basicErrorMessage, 
                                            detailedErrorMessage, 
                                            category, 
                                            e, 
                                            errorLevel, 
                                            state);  
         
        // Equivalent SwingX version 
        //JXErrorPane.showDialog(component, errorInfo); 
         
        if (component instanceof JFrame)
        {
            new JErrorDialog((JFrame)component, errorInfo);
        }
        else
        {
            JFrame jframe = new JFrame();
            jframe.setIconImage(JOptionPaneCustom.createImageIcon("images/pgeepicon.PNG").getImage());      
            jframe.setLocationRelativeTo(component);
            new JErrorDialog(jframe, errorInfo);
        }
       
    }      
             
    /**
     * @return the formated System Info
     */
    private static String getSystemInfo()
    {
        String javaVersion  = System.getProperty("java.version");
        String osName       = System.getProperty("os.name");
        String osVersion    = System.getProperty("os.version");
        String osPack       = System.getProperty("sun.os.patch.level");
        String javaHome     = System.getProperty("java.home");
              
        WindowsOutlookVersion windowsOutlookVersion = null;
        String outlookVersion = "";
        
        try
        {
            windowsOutlookVersion = new WindowsOutlookVersion(null); 
            outlookVersion = windowsOutlookVersion.getOutlookOfficeVersion();
        }
        catch (IOException ioe)
        {
            outlookVersion = "- OL Version Not Found.";
        }        

        if (outlookVersion.equals(""))
        {
            outlookVersion = "none";
        }
        
        String systemInfo 
            = osName + " " + osVersion   + " (" + osPack + ") - OL " + outlookVersion 
            + "<br> Java " + javaVersion + " (" + javaHome + ")";
        
        return systemInfo;
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    private static ImageIcon createImageIcon(String path) 
    {
        java.net.URL imgURL = JOptionPaneCustom.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }       
  
}
