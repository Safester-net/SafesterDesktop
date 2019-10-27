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
package net.safester.application.addrbooknew.tools;


import static net.safester.application.addrbooknew.AddressBookImportCsv2.CR_LF;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import net.safester.application.parms.Parms;
import net.safester.application.tool.WindowSettingManager;

public class CryptAppUtil {

    public static final int KB = 1024;
    public static final int MB = KB * KB;
   
    public static final String EMAIL_FORBIDDEN_CHARS = " ! / | ? * &";
    
    protected CryptAppUtil() {
    }

    /**
     * Allows to display a remote html page
     *
     * @param window
     * @param stringUrl
     */
    public static void redirectToHtmlPage(Window window, String stringUrl) {
        try {
            URL url = new URL(stringUrl);
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(url.toURI());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(window, "Unable to access remote HTML page: " + CR_LF
                    + ex.toString(), Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void resetWindowsWithKeys(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.VK_F11 && e.isShiftDown() && e.isControlDown()) {
            try {
                WindowSettingManager.resetAll();
            } catch (BackingStoreException ex) {
                Logger.getLogger(CryptAppUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.exit(0);
        }
    }

    public static boolean isValidEmailAddress(String email) {

        if (email == null) {
            return false;
        }
        
        email = email.trim();
        
        if (email.isEmpty()) {
            return false;
        }
        
        if (email.contains(" ")) {
            return false;
        }

        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        boolean doMatches = m.matches();
        
        if (! doMatches) {
            return false;
        }
        
        // Temporary limitation as we use email addresses as directory names:

        for (int i = 0; i < EMAIL_FORBIDDEN_CHARS.length(); i++) {
            {
                if (email.contains(EMAIL_FORBIDDEN_CHARS.charAt(i) + "")) {
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * Build a unique string
     *
     * @return a unique string
     */
    public static synchronized String createClientReference() {
        UUID idOne = UUID.randomUUID();
        String reference = idOne.toString().substring(0, 6).toUpperCase();
        return reference;
    }

    /**
     * Returns the total size of email attachs
     *
     * @param pdfFiles the files of the email
     * @return the total size of email attachs
     */
    public static long getEmailSize(List<File> pdfFiles) {

        long emailSize = 0;

        for (File pdfFile : pdfFiles) {
            emailSize += pdfFile.length();
        }

        return emailSize;

    }

    public static String cut(String in, int maxLength) {
        if (in == null) {
            return null;
        }

        if (in.length() <= maxLength) {
            return in;
        }

        // Ok, cut it to maxLength chars!
        return in.substring(0, maxLength) + "...";

    }

    /**
     * Returns user.home/.APP_NAME/user-files/user@domain.com
     *
     * @return user.home/.APP_NAME/user-files/user@domain.com
     */
    /*
    public static String getHomeUserFiles() {
        File userFilesDir = new File(getHomeDir() + File.separator + "user-files" + File.separator + LoginFrame.getLogin());
        if (!userFilesDir.exists()) {
            userFilesDir.mkdirs();
        }
        return userFilesDir.toString();
    }

    public static File getAddressBookFile() {
        File file = new File(CryptAppUtil.getHomeUserFiles() + File.separator + "address-book.csv");
        return file;
    }
    
    public static File getAddressBookPasswordsFile() {
        File file = new File(CryptAppUtil.getHomeUserFiles() + File.separator + "address-book-passwords.csv");
        return file;
    }


    public static File getDraftFile() {
        File file = new File(CryptAppUtil.getHomeUserFiles() + File.separator + "draft.txt");
        return file;
    }

    public static File getSignatureFile() {
        File file = new File(CryptAppUtil.getHomeUserFiles() + File.separator + "signature.txt");
        return file;
    }
*/
    
    /**
     * Returns user.home/.APP_NAME/dicts
     *
     * @return user.home/.APP_NAME/dicts
     */
    /*
    public static String getHomeDictDir() {
        File userFilesDir = new File(getHomeDir() + File.separator + "dicts");
        if (!userFilesDir.exists()) {
            userFilesDir.mkdirs();
        }
        return userFilesDir.toString();
    }

    public static List<String> getLines(String text) {
        // Some lines may have CR_LF, buil a final by splitting lines on CR_LF
        List<String> lines = new ArrayList<>();

        BufferedReader br = null;

        try {
            br = new BufferedReader(new StringReader(text));

            String theLine = null;

            try {
                while ((theLine = br.readLine()) != null) {

                    if (theLine.isEmpty()) {
                        theLine = " ";
                    }

                    lines.add(theLine);
                }
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex.toString());
            }
        } finally {
            IOUtils.closeQuietly(br);
        }

        return lines;
    }
*/
    /**
     * See
     * http://stackoverflow.com/questions/9661823/text-split-after-a-specified-length-but-dont-break-words-using-grails
     * Split a text into lines and return a List. if maxlength is > 0, lines
     * will be cut at maxlength but will not cut words
     *
     * @param text the text to split
     * @param maxLength the max length of a line. 0 for no line cut. If > 0,
     * maxLength minimum value is 29 to be sure not to cut long words
     *
     * @return the splitted lines, each line is < maxlength
     * @th
     * rows IllegalArgumentException
     */
    public static List<String> getLinesNoWordCut(String text, int maxLength) throws IllegalArgumentException {

        if (maxLength == 0) {
            maxLength = 999999;
        }

        if (maxLength < 29) {
            throw new IllegalArgumentException("maxlength must be > 29.");
        }

        List<String> matchList = new ArrayList<>();
        //final String thePattern = ".{1,10}(?:\\s|$)";
        final String thePattern = ".{1," + maxLength + "}(?:\\s|$)";

        Pattern regex = Pattern.compile(thePattern, Pattern.DOTALL);
        Matcher regexMatcher = regex.matcher(text);
        while (regexMatcher.find()) {
            matchList.add(regexMatcher.group());
        }

        return matchList;

    }

    public static void createDictFileFromResource(String dictPath) throws IOException {

        if (!new File(dictPath).exists()) {

            InputStream is = null;
            OutputStream out = null;

            try {

                is = Parms.class.getResourceAsStream("resources/" + StringUtils.substringAfterLast(dictPath, File.separator));
                out = new BufferedOutputStream(new FileOutputStream(dictPath));

                IOUtils.copy(is, out);
            } finally {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(out);
            }
        }

    }



    /**
     * Sets the background same a JLabel
     *
     * @param jTextField the text field to "transform" to a label like display
     */
    public static void setTextFieldAsLabel(JTextField jTextField) {
        jTextField.setEditable(false);
        jTextField.setBorder(null);
        jTextField.setBackground(CryptAppUtil.getLabelBackground());
    }

    /**
     * Returns the label background
     *
     * @return the label background
     */
    public static Color getLabelBackground() {
        UIDefaults defaults = UIManager.getDefaults();
        Color labelBackgroundColor = defaults.getColor("Label.background");
        return labelBackgroundColor;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Reference: " + createClientReference());
    }

}
