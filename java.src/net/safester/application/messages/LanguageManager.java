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

import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;

import net.safester.application.parms.Parms;


/**
 * Allows to define as static a language to use for the complete JVM life.
 * <br>
 * If the static stored language is null, i.e. not set, the language returned will
 * be the System Property "user.language"
 * 
 */
public class LanguageManager
{
    /** The Preferences Key for the Language in use with Safester*/
    private static String LANGUAGE_KEY = "LANGUAGE_KEY";
    
    /** Language defined by the setLanguage() method */
    private static String LANGUAGE = null;
    
    /**
     * constructor.
     */
    public LanguageManager()
    {
        // Nothing        
    }
    
    /**
     * Return the language in use.
     * <br>
     * If language has  been never set 
     * ==> value will be DEFAULT_LANGUAGE (the System Property "user.language")
     * 
     * @return Returns the LANGUAGE.
     */
    public static String getLanguage()
    {
        if (LANGUAGE == null)
        {           
            // if no Language in memory ==> Load from Preferences
            LanguageManager languageManager = new LanguageManager();
            languageManager.loadLanguage();          
        }
        
        //System.out.println("Language:" + LANGUAGE);
        
        return LANGUAGE;
    }
    
    /**
     * @param language The LANGUAGE to set.
     */
    public static void setLanguage(String language)
    {
        LANGUAGE = language;
    }
    
    /**
     * Store the language in use as a Preference
     */
    public void storeLanguage()
    {
        //
        // Registry key will be: HKEY_CURRENT_USER\Software\JavaSoft\Prefs\com\safelogic\pgp\msg
        // WARNING: SAME KEY will be used in C++. Be carefull id package is renamed///
        //
        Preferences prefs = Preferences.userNodeForPackage( this.getClass());
        prefs.put(LANGUAGE_KEY, LANGUAGE);       
    }
    
    /**
     * Load the language in use as a Preference
     * <br>
     * If no prefered language is found, use user.language.
     * if user.language is not available use en (English)
     */
    public void loadLanguage()
    {
        Preferences prefs = Preferences.userNodeForPackage( this.getClass()); 
        
        String defaultLanguage = System.getProperty("user.language");
        String language = prefs.get(LANGUAGE_KEY, defaultLanguage); 
        
        List<String> availableLanguages = new Vector<String>();
        for (int i = 0; i < Parms.AVAILABLE_LANGUAGES.length; i++)
        {
            availableLanguages.add(Parms.AVAILABLE_LANGUAGES[i]);
        }
        
        // if the found language is not available in cGeep ==> Use English!
        if (! availableLanguages.contains(language))
        {
            language = Parms.AVAILABLE_LANGUAGES[0];
        }
        
        // Set the language in memory
        setLanguage(language);
    }
    
    
}
