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
package net.safester.application.messages;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import net.safester.application.util.JOptionPaneNewCustom;



/**
 * Class to dynamically get messages from fields names.
 * <br>
 * Purpose of this class is to avoid to use message values from ini fileIn with pgeep
 * on the client side.
 * <br>
 * The class uses basic reflection technique to get directly a message value from the field
 * name.
 */
public class MessagesManager
{   
    
    /** The Package that contains all properties messages & html files */
    public static String MESSAGE_FILES_PACKAGE = "net.safester.application.messages.files";
        
    /** Resource Bundle instance */
    private ResourceBundle resourceBundle;
    
    /**
     * Constructor
     * @param language 
     */
    public MessagesManager(String language)
    {
        Objects.requireNonNull(language, "language can not be null!");
        Locale locale = new  Locale(language);
        setResourcesBundle(locale);   
    }
    /**
     * Constructor
     */
    public MessagesManager()
    {
        Locale locale = new  Locale(LanguageManager.getLanguage());
        setResourcesBundle(locale);
        
    }

    public void setResourcesBundle(Locale locale) {
        // Messages are contained in:
        // com.safelogic.pgp.msg.files.Messages_fr.properties
        // com.safelogic.pgp.msg.files.Messages_en.properties
        // Etc.
        
        String baseName = MESSAGE_FILES_PACKAGE + ".Messages";
        
        //System.out.println("baseName: " + baseName);
        
        try
        {
            //
            // Note: We support only fr and en languages in Version 1.00
            //            
            resourceBundle = ResourceBundle.getBundle(baseName, locale);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JOptionPaneNewCustom.showException(null, e);
        }
    }
        
    /**
     * New method that uses ResourceBundle(s)
     * 
     * returns the full message associated with a message parameter
     * 
     * @param messageParam      the message parameter
     * 
     * @return                  the message value in the desired language
     */
    public String getMessage(String messageParam)
    {
        if (messageParam == null)
        {
            return "null";            
        }
        
        // Always ask for the Lower case parameter
        messageParam = messageParam.toLowerCase();
        messageParam = messageParam.trim();
                
        String messageValue = null;    
                
        try
        {
            messageValue = resourceBundle.getString(messageParam);            

            return messageValue;
        }
        catch (Exception e)
        {
            // We choose to quietly send back the messageParam as messageValue
            return messageParam;
        }
                
    }    
    
     /**
     * New method that uses ResourceBundle(s)
     * 
     * returns the full message associated with a message parameter
     * 
     * @param messageParam      the message parameter
     * 
     * @return                  the message value in the desired language
     */
    public static String get(String messageParam)
    {
        MessagesManager messagesManager = new MessagesManager();
        return messagesManager.getMessage(messageParam);
    }
   
}
