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

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import com.safelogic.utilx.StringMgr;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.util.JOptionPaneNewCustom;

/**
 * @author Nicolas de Pomereu
 * Central Wrapper for JDIC & Desktop calls, because JDIC ans Java 1.6 Desktop
 * have not the same API
 */
public class DesktopWrapper
{
    public static final String CR_LF = System.getProperty("line.separator") ;
    
    /**
     * Constructor
     */
    protected DesktopWrapper()
    {
        
    }
    /**
     * Wrapper for Java 1.6 specific row sorter for JTable
     * @param jtable
     */
    public static void setAutoCreateRowSorterTrue(JTable jtable)
    {
        jtable.setAutoCreateRowSorter(true);
    }
    
//    /**
//     * Wrapped JDIC open
//     *
//     * @param file              the file to open
//     * @param parentComponent   the parent component
//     */
//
//    public static void mail(org.jdesktop.jdic.desktop.Message msg)
//    {
//        try
//        {
//            org.jdesktop.jdic.desktop.Desktop.mail(msg);
//        }
//        catch (Exception e)
//        {
//            MessagesManager messagesManager = new MessagesManager();
//            String message = messagesManager.getMessage("os_not_enable_to_mail");
//            JOptionPane.showMessageDialog(null, message + CR_LF + e);
//        }
//    }
    
    /**
     * Wrapped JDIC open
     * 
     * @param file              the file to open
     * @param parentComponent   the parent component
     */
    
    public static void print(File file, Component parentComponent)
    {
        try
        {
            java.awt.Desktop dekstop = java.awt.Desktop.getDesktop();
            dekstop.print(file);
        }
        catch (Exception e)
        {            
            MessagesManager messagesManager = new MessagesManager();
            String message 
                = messagesManager.getMessage("os_not_enable_to_open_file") + " " + file;            
            JOptionPane.showMessageDialog(null, message + CR_LF + e);       
        }        
    }   
    
    /**
     * 
     * @param file  the file to open
     * 
     * @return  true if the file comes from Outlook Office
     */
    private static boolean isFileComingFromOutlookOffice(File file)
    {
        String fileName = file.getName();
        
        if (fileName.lastIndexOf(".") == 0)
        {
            // There is no extension ==> Impossible to open it
            return true;
        }
        
        if (fileName.lastIndexOf(".") < fileName.lastIndexOf(" "))
        {
            // There is no extension after last space ==> Impossible to open it
            return true;
        }        
                
        /*
        int length = fileName.length();
        if (fileName.lastIndexOf(".") !=  fileName.length() - 4)
        {
            // There is no ending with ".xxx";
            return false;
        }
        */
        
        return false;
    }
    
    /**
     * Wrapped JDIC open
     * 
     * @param file              the file to open
     * @param parentComponent   the parent component
     */
    
    private static File finalFile = null;
    
    public static void open(File file, Component parentComponent)
    {        
        
        finalFile = file;
        
        if (isFileComingFromOutlookOffice(file))
        {                   
            String tempdir = Parms.getSafesterTempDir();
            
            String fileName = file.getName(); 
            fileName = StringMgr.ReplaceAll(fileName, " ", ".");
            
            if (fileName.endsWith(")") && fileName.length() > 3)
            {
                while (fileName.indexOf("(") > 0 && fileName.length() > 3)
                {
                    fileName = fileName.substring(0, fileName.length() - 1);
                }                
            }
            
            File fileOut = new File(tempdir + fileName);
                    
            try
            {
                org.apache.commons.io.FileUtils.copyFile(file, fileOut);
            }
            catch (IOException e)
            {
                JOptionPaneNewCustom.showException(parentComponent, e);
                return;
            }
            
            finalFile = fileOut;
            
        }

       
     //   final Component parentComponentFinal = parentComponent;        
                        
        Runnable doWorkRunnable = new Runnable() {
            public void run() { 

                try
                {
                    java.awt.Desktop dekstop = java.awt.Desktop.getDesktop();
                    //System.out.println(new Date() +" Opening...! " + finalFile);
                    dekstop.open(finalFile);
                    //System.out.println(new Date() +" Open Done !");
                }
                catch (Exception e)
                {
                    MessagesManager messagesManager = new MessagesManager();
                    String message 
                        = messagesManager.getMessage("os_not_enable_to_open_file") + " " + finalFile;            
                    JOptionPane.showMessageDialog(null, message + CR_LF + e);     
                }                     

            }};             

            doWorkRunnable.run();            

    }    
    
    /**
     * Wrapped JDIC browse
     * 
     * @param url   the URL to browse
     */
    public static void browse(String url)
    {
        final String finalUrl = url;
        
        Runnable doWorkRunnable = new Runnable() {
            public void run() { 
            
                try
                {
                    java.awt.Desktop dekstop = java.awt.Desktop.getDesktop();
                    dekstop.browse(new URL(finalUrl).toURI());
                }
                catch (Exception e)
                {
                    MessagesManager messagesManager = new MessagesManager();
                    String message = messagesManager.getMessage("ERR_HTTP_BROWSER_ERROR");
                    JOptionPaneNewCustom.showException(null, e, message);
                }                     

            }};             
          //  System.out.println("before run()");
            doWorkRunnable.run();
          //  System.out.println("after run()");            

    }
    
    /**
     * Wrapped JDIC browse
     * 
     * @param url   the URL to browse
     */
    public static void browse(URL url)
    {
        final URL finalUrl = url;
        
        Runnable doWorkRunnable = new Runnable() {
            public void run() { 
            
                try
                {
                    java.awt.Desktop dekstop = java.awt.Desktop.getDesktop();
                    System.out.println("finalUrl: " + finalUrl);
                    dekstop.browse(finalUrl.toURI());
                }
                catch (Exception e)
                {
                    MessagesManager messagesManager = new MessagesManager();
                    String message = messagesManager.getMessage("err_http_browser_error");
                    JOptionPaneNewCustom.showException(null, e, message);
                }                     

            }};             
            
            doWorkRunnable.run();
    }

}

/**
 * 
 */
