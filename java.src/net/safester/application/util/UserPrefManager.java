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

import java.util.prefs.Preferences;

public class UserPrefManager {

    //Spell check language
    public static String SPELL_CHECK_LANGUAGE = "SPELL_CHECK_LANGUAGE";

    // Proxy settings
    public static String PROXY_TYPE          = "PROXY_TYPE";
    public static int PROXY_TYPE_BROWSER_DEF = 0; // default value
    public static int PROXY_TYPE_USER_DEF    = 1;
    public static int PROXY_TYPE_DIRECT      = 2;

    public static String PROXY_NTLM_AUTHENTICATION = "PROXY_NTLM_AUTHENTICATION";
    public static String NTLM_WORKSTATION          = "NTLM_WORKSTATION";
    public static String NTLM_DOMAIN               = "NTLM_DOMAIN";

    // For PROXY_TYPE == PROXY_TYPE_USER_DEF
    public static String PROXY_ADDRESS         = "PROXY_ADDRESS";
    public static String PROXY_PORT            = "PROXY_PORT";

    // Authenticated proxy:
    public static String PROXY_AUTH_REMEMBER_INFO = "PROXY_AUTH_REMEMBER_INFO";
    public static String PROXY_AUTH_USERNAME      = "PROXY_AUTH_USERNAME";
    public static String PROXY_AUTH_PASSWORD      = "PROXY_AUTH_PASSWORD";
    public static String PROXY_AUTH_NTLM          = "PROXY_AUTH_NTLM";


    public static String HIDE_DECRYPTING_DIALOG             = "DISPLAY_DECRYPTING_DIALOG";
    public static String HIDE_ENCRYPTION_DISCARDABLE_WINDOW = "HIDE_ENCRYPTION_DISCARDABLE_WINDOW";

    public static String USER_LOGIN            = "USER_LOGIN";
    public static String NB_MESSAGES_PER_PAGE  = "NB_MESSAGES_PER_PAGE";
    public static String FONT_SIZE_BODY        = "FONT_SIZE_BODY";


    // Preference of Simple Text or Rich Text formating
    public static String IS_SIMPLE_TEXT          = "IS_SIMPLE_TEXT";

    public static String DEFAULT_DIRECTORY     = "DEFAULT_DIRECTORY";

    public static String IGNORE_CAPITALIZED_WORDS   = "IGNORE_CAPITALIZED_WORDS";
    public static String IGNORE_WORDS_WITH_DIGITS   = "IGNORE_WORDS_WITH_DIGITS";
    public static String SEPARATE_HYPHEN_WORDS      = "SEPARATE_HYPHEN_WORDS";

    public static String DO_CACHE_PASSPHRASE        = "DO_CACHE_PASSPHRASE";
    public static String SOCKET_SERVER_START_TIME   = "SOCKET_SERVER_START_TIME";
    public static String SOCKET_SERVER_PORT         = "SOCKET_SERVER_PORT";

    public static String EXPIRED_SUBSCRIPTION_DIALOG_DISCARD  = "EXPIRED_SUBSCRIPTION_DIALOG_DISCARD";
    public static String EXPIRED_TRIAL_DIALOG_DISCARD  = "EXPIRED_TRIAL_DIALOG_DISCARD";
    public static String MESSAGE_SERVICE_DIALOG_DISCARD  = "MESSAGE_SERVICE_DIALOG_DISCARD";
    public static String INSERT_SIGNATURE = "INSERT_SIGNATURE";
    
    public static String NOTIFY_NO_POPUP_ON_TASKBAR = "NOTIFY_NO_POPUP_ON_TASKBAR";
    public static String NOTIFY_NO_PLAY_SOUND = "NOTIFY_NO_PLAY_SOUND";
    
    // The sound file to play if sound notification
    public static String NOTIFY_SOUND_RESOURCE = "NOTIFY_SOUND_RESOURCE"; 
    public static String NOTIFY_SOUND_USER_FILE = "NOTIFY_SOUND_FILE"; 
    
        // Split panes location
    public static String SPLIT_PANE_FOLDERS_LOC  = "SPLIT_PANE_FOLDERS_LOC";
    
    public static String READING_PANE_POSITION = "READING_PANE_POSITION";
    
    public static int READING_PANE_BOTTOM = 0;
    public static int READING_PANE_RIGHT = 1;
    public static int READING_PANE_INACTIVE = 2;
    
    public static String SPLIT_PANE_MESSAGE_LOC_VERTICAL_SPLIT  = "SPLIT_PANE_MESSAGE_LOC_VERTICAL_SPLIT";
    public static String SPLIT_PANE_MESSAGE_LOC_HORIZONTAL_SPLIT = "SPLIT_PANE_MESSAGE_LOC_HORIZONTAL_SPLIT";
    
    public static String FOLDER_SECTION_IS_INACTIVE = "FOLDER_SECTION_IS_INACTIVE";

    /**
      * Get a  preference
      * @param prefName  the preference name
      * @return the preference value
      */
    
     /**
      * Set preference
      */
     public static void setPreference(String prefName, String prefValue)
     {
         UserPrefManager userPreferencesManager = new UserPrefManager();
         Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());

         prefs.put(prefName,  prefValue);
     }


     public static String getPreference(String prefName)
     {
         UserPrefManager userPreferencesManager = new UserPrefManager();
         Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());

         return prefs.get(prefName, null);
     }

     /**
      * Remove preference
      */
     public static void removePreference(String prefName)
     {
         UserPrefManager userPreferencesManager = new UserPrefManager();
         Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());

         prefs.remove(prefName);
     }

     /**
      * Get a  boolean preference
      * @param prefName  the preference name
      * @return the preference value
      */
     public static boolean getBooleanPreference(String prefName)
     {
        String preferenceStr = getPreference(prefName);

        boolean preference = false;

        try
        {
            preference = Boolean.parseBoolean(preferenceStr);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        return preference;
    }

     /**
      * Get an Integer preference
      * @param prefName  the preference name
      * @return the preference value
      */
     public static int getIntegerPreference(String prefName)
     {
        String preferenceStr = getPreference(prefName);

        int preference = 0;

        try
        {
            preference = Integer.parseInt(preferenceStr);
        }
        catch (NumberFormatException e)
        {
            //e.printStackTrace();
        }

        return preference;
    }

    /**
     * Set preference
     */
    public static void setPreference(String prefName, int prefValue)
    {
        UserPrefManager userPreferencesManager = new UserPrefManager();
        Preferences prefs = Preferences.userNodeForPackage(userPreferencesManager.getClass());

        prefs.put(prefName,  Integer.toString(prefValue));
    }


     /**
      * @param prefName     the preference name
      * @param prefValue    the preference value
      */
     public static void setPreference(String prefName, boolean prefValue)
     {
         setPreference(prefName, Boolean.toString(prefValue));
     }


}
