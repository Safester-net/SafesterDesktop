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
package net.safester.application.util;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import com.safelogic.utilx.Debug;

import net.safester.application.SystemInit;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.version.Version;
import net.safester.clientserver.ServerParms;


/**
 * @author Nicolas de Pomereu
 *
 */
public class JOptionPaneNewCustom
{
    /** The stack trace */
    private static String m_stackTrace;

    public static final String CR_LF = System.getProperty("line.separator") ;

    /**
     * Forbid instanciation
     */
    private JOptionPaneNewCustom()
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
        
        //TODO: special case :  java.net.ConnectException
        try
        {
            m_stackTrace = Debug.GetStackTrace(e);

            // Awlays log the exception in a user.home
            logExceptionInUserHome(m_stackTrace);
            
            //java.net.UnknownServiceException: Servlet Method failed: HTTP/1.1 407 Proxy Authentication Required status: 407
            if(displaySpecialMessageForJavaNetException(e, component))
            {
                return;
            }
            
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
            //m_stackTrace = Util.cut(m_stackTrace, 500);
            
            JOptionPane.showMessageDialog(component, e.toString() + Util.CR_LF + m_stackTrace);
        }        
    }

    /**
     * Display a clean formatted message when an Internet Exception is trapped (java.net.*)
     *
     * @param e             the Exception thrown
     * @param component     the parent component
     * @return              true if the Exception is Internet related
     * @throws HeadlessException
     */
    public static boolean displaySpecialMessageForJavaNetException(Exception e,
                                                                   Component component) throws HeadlessException {
        m_stackTrace = Debug.GetStackTrace(e);
        MessagesManager messages = new MessagesManager();

        boolean isInternetMesage = false;

        if (m_stackTrace.contains("java.net.ConnectException:") || m_stackTrace.contains("java.net.SocketException:")) {

            JOptionPane.showMessageDialog(component, messages.getMessage("check_internet_connection"));
            isInternetMesage = true;
        }

        if (m_stackTrace.contains("java.net.UnknownHostException")) {
            JOptionPane.showMessageDialog(component, messages.getMessage("check_internet_connection"));
            isInternetMesage = true;
        }

        if (m_stackTrace.contains("java.net.UnknownServiceException:") && m_stackTrace.contains("status: 407")) {
            JOptionPane.showMessageDialog(component, messages.getMessage("your_proxy_requires_authentication"));
            isInternetMesage = true;
        }

        return isInternetMesage;
    }

    /**
     * Log the Exception in user.home/safester_exception_YYMMDD_hmm.txt
     */
    private static void logExceptionInUserHome(String stackTrace)
    {
       try
       {
          Date date = new Date();
          SimpleDateFormat  simpleFormat = new SimpleDateFormat("yyMMdd_hhmm");
          String fileName = "safester_exception_" + simpleFormat.format(date) + ".txt";

          //String fileName = new Date();
         
          File exceptionFile = new File(SystemInit.getLOG_DIR() + File.separator + fileName);
          FileUtils.writeStringToFile(exceptionFile, stackTrace);
       }
       catch (Exception e)
       {
           JOptionPane.showMessageDialog(null, e.toString());
       }
       
    }

    /**
     * Display the Exception and the Header Error Message in a special JXErrorPane  
     *                                                     
     * @param component             the parent component
     * @param eexception                     the Exception trapped
     * @param headerErrorMessage    the Error Message to display as header of the Exception Panel
     *
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static void showException(Component component, Exception message) 
    {
        showException(component, message, null);
    }     
    
    /**
     * Display the Exception in a special JXErrorPane  
     *                                                     
     * @param component             the parent component
     * @param e                     the Exception trapped
     * @param applicationmessage    the Message to pass as header of the Exception Panel
     */    
    private static void buildExceptionPanel(Component component, Exception e, String applicationMessage)
    {
        
        String title    = "Error";
        String basicErrorMessage = null;           
        String detailedErrorMessage = null;
        String category = null;
        Level errorLevel = null;
        Map<String, String> state= null; 
        
        if (applicationMessage == null)
        {
            applicationMessage =  e.toString();      
        }

        applicationMessage = Util.cut(applicationMessage, 160);
        
        String weAreSorryAnErrorOccured
            = "<font face=Arial size=\"3\">" +
              "We are sorry - an error has occurred." + CR_LF +
              "Please send the Details below to " + ServerParms.CONTACT_EMAIL;
        
        basicErrorMessage  = weAreSorryAnErrorOccured + CR_LF + "<br><font color=red>" + applicationMessage + "</html>";

        String info = Version.getVersionWithDate() + " (" + System.getProperty("user.dir") + ")";
        String systemInfo = getSystemInfo();
        
        detailedErrorMessage =
              info + " - " + new Date() + CR_LF
            + "System : " + systemInfo + CR_LF
            + "Message: " + applicationMessage + CR_LF
            + CR_LF 
            + m_stackTrace.trim() + CR_LF;
                                
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
            jframe.setIconImage(Parms.createImageIcon(Parms.ICON_PATH).getImage());
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
                      
        String systemInfo 
            = osName + " " + osVersion   + " (" + osPack + ")"
            + "Java " + javaVersion + " (" + javaHome + ")";
        
        return systemInfo;
    }
      
}
