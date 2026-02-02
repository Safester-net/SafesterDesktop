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

import java.awt.Frame;
import java.util.List;
import java.util.Vector;

import com.safelogic.pgp.api.util.msg.MessagesManager;
import com.safelogic.utilx.Hex;

/**
 * @author Nicolas de Pomereu
 *
 * Program to retrieve the Office Outlook Emails account.
 * 
 */

public class WindowsOutlookEmails
{        
    /** The Message Manager */
    private MessagesManager messagesManager = new MessagesManager();
    
    /** The list of Outlook Office Emails return by C++ program*/
    private List<String> outlookEmails = new Vector<String>();
   
    /** Debug tool */
    private boolean DEBUG = true; //Debug.isSet(this) ;
    
    /**
     * Constructor
     * @param frame     the parent frame
     */
    public WindowsOutlookEmails(Frame frame)
    {
        if(System.getProperty("os.name").indexOf("Windows") == -1)
        {
            return;
        }
                
        try
        {
            String keyNameSuffix = "Software\\Microsoft\\Windows NT\\CurrentVersion\\Windows Messaging Subsystem\\Profiles\\Outlook\\9375CFF0413111d3B88A00104B2A6676";

            Registry registry = new Registry();
            Integer handle = registry.RegOpenKeyEx(Registry.HKEY_CURRENT_USER, 
                                                   keyNameSuffix, 
                                                   Registry.KEY_ALL_ACCESS); 
            
            int i = 0;        
            
            while(true)
            {
                String enumValue = registry.RegEnumKey(handle, i++, 260);
                debug("enumValue: " + enumValue);
                
                if (enumValue == null)
                {
                    break;
                }
                
                String key = keyNameSuffix;
                key += "\\";
                key += enumValue;
                
                String emailInHexaParsed = registry.getCurrentUserKeyValueBinary(key, "Email");
                
                if (emailInHexaParsed == null || emailInHexaParsed.length() < 1)
                {
                    continue;
                }
                
                byte emailBinary[] = Hex.fromString(emailInHexaParsed);        
                String email = new String(emailBinary);
                            
                email = email.trim();
                
                if (email.equals("") || email.length() < 1 )
                {
                    continue;
                }
                
                outlookEmails.add(email);  
                
            }
        }
        catch (RuntimeException e)
        {
            JOptionPaneCustom.showException(frame, e);
            e.printStackTrace();
        }
        catch (Exception e)
        {
            JOptionPaneCustom.showException(frame, e);
            e.printStackTrace();
        }
        
    }

    /**
     * @return the outlookEmails
     */
    public List<String> getOutlookEmails()
    {
        return outlookEmails;
    }

    /** Debug tool */
    private void debug(String s)
    {
        if(DEBUG)
            System.out.println("DBG> " + s) ;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        WindowsOutlookEmails windowsOutlookEmails = new WindowsOutlookEmails(null);
        List<String> emails = windowsOutlookEmails.getOutlookEmails();
        
        for (int i = 0; i < emails.size(); i++)
        {
            System.out.println(i + " :" + emails.get(i) + ":");
        }
    }
}

