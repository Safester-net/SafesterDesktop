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
package net.safester.application.parms;

import java.awt.Color;
import java.io.File;
import java.util.Locale;

import javax.swing.ImageIcon;

import net.safester.application.Main;


public class Parms {

    /** Program features activation flags */
    public static boolean FEATURE_CACHE_PASSPHRASE = false;

    /** Base product name */
    public static final String PRODUCT_NAME = "Safester";
    public static final String APP_NAME = PRODUCT_NAME;
    
    /** Contains all implemented languages */
    public static String[] AVAILABLE_LANGUAGES = {
        Locale.ENGLISH.getLanguage(),
        Locale.FRENCH.getLanguage()
    };
    
    public static final int NOTIFY_PERIOD = 20000;
    
    /** To use for background selection */
    public static Color LIGHT_BLUE = new Color(243, 243, 255);
    public static Color URL_COLOR = new Color(51, 0, 255);
    
    public static String ICON_PATH  = "images/files/safester_icon.png";
    public static String ICON_PATH_BIG  = "images/files/safester-icon-80.png";
        
    public static String ABOUT_ICON = "images/files_2/24x24/speech_balloon_answer.png";
    public static String PRINT_ICON = "images/files_2/16x16/printer.png";
    
    public static String ICON_IMPORT_CSV_PATH  = "images/files/icons8-csv-32.png";
    public static String ICON_IMPORT_GMAIL_PATH  = "images/files/icons8-gmail-32.png";
    public static String ICON_IMPORT_OUTLOOK_PATH  = "images/files/icons8-ms-outlook-32.png";
    
    // Not any more in SHEF Jar
    public static String CUT_ICON       = "images/files_2/16x16/cut.png";
    public static String PASTE_ICON     = "images/files_2/16x16/clipboard_paste_no_format.png";
    public static String COPY_ICON      = "images/files_2/16x16/copy.png";
    public static String DELETE_ICON    = "images/files_2/16x16/delete.png";

    public static String PAPERCLIP_ICON = "images/files_2/16x16/paperclip2.png";
    
    public static String STARRED_ICON = "images/files/star.png";
    public static String STARRED_OFF_ICON = "images/files/star_off.png";
    
    public static final int INBOX_ID = 1;
    public static final int OUTBOX_ID = 2;
    public static final int DRAFT_ID = 3;
    public static final int STARRED_ID = 0;

    public static final int RECIPIENT_TO = 1;
    public static final int RECIPIENT_CC = 2;
    public static final int RECIPIENT_BCC = 3;

    public static final String ENCRYPTED_FILE_EXT = ".pgp";
    
    public static final int ACTION_REPLY = 1;
    public static final int ACTION_REPLY_ALL = 2;
    public static final int ACTION_FOWARD = 3;
    public static final int ACTION_EDIT = 4;

    public static final int KO = 1024;
    public static final int MO = 1024 * KO;
    public static final long GO = 1024 * MO;

    public static final int MAX_MESSAGE_CACHE_SIZE = 10 * MO;

    public static final Color COLOR_URL          = new Color(0, 0, 255);
    public static final Color COLOR_SEPARATOR    = new Color(102, 102, 255);
    public static final Color COLOR_CLEAN_ORANGE = new Color(255,102,0);
    
    public static final int DEFAULT_NB_MESSAGES_PER_PAGE  = 25;

    //public static final String DICTIONARY_ENGLISH = "messages/dictionary/combined-english-v2-whole.dict";
    //public static final String DICTIONARY_FRENCH = "messages/dictionary/fr-french-v2.dict";

    public static final String DICTIONARY_ENGLISH = "combined-english-v2-whole.dict";
    public static final String DICTIONARY_FRENCH  = "fr-french-v2.dict";

    public static String salt = "ThiS*IsSAlt4loGin$";
    public static int PASSPHRASE_HASH_ITERATIONS = 3;
        
    // default charset to use to load & write text files
    public static final String DEFAULT_CHARSET = "ISO-8859-1";

    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Main.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Return the temp directory
     */
    public static String getSafesterTempDir()
    {
        String userHome =  System.getProperty("user.home");
        if (! userHome.endsWith(File.separator))
        {
            userHome += File.separator;
        }

        String safesterTemp = userHome += "safester_temp" + File.separator;

        File safesterTempDir = new File(safesterTemp);
        if (!safesterTempDir.exists()) {
            safesterTempDir.mkdirs();
        }

        return safesterTemp;
            
    }

    /**
     * Tell if folder can be removed by user or not (Inbox or Outbox or Draft are NOT removable)
     * @param idFolder      The is of the folder
     * @return              true if user can remove folder false otherwise
     */
    public static boolean folderRemovable(int idFolder) {
        boolean removable = true;
        if (idFolder == STARRED_ID || idFolder == INBOX_ID || idFolder == OUTBOX_ID || idFolder == DRAFT_ID) {
            removable = false;
        }
        return removable;
    }

    /** String displayed if a sender is unknown */
    public static String UNKNOWN_SENDER = "????";
    
    /** String displayed if a recipient is unknown */
    public static String UNKNOWN_RECIPIENT = "????";
        
}
