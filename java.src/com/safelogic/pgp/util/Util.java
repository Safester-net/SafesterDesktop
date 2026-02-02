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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

import com.safelogic.pgp.version.Version;
import com.safelogic.utilx.Base64;
import com.safelogic.utilx.Debug;
import com.safelogic.utilx.StringMgr;

/**
 * Misc utilites for pgeep
 * @author Nicolas de Pomereu
 *
 */
public class Util 
{

    /** The debug flag */ 
    protected static boolean DEBUG = false;
    
    /** Universal and clean line separator */
    public static String CR_LF = System.getProperty("line.separator") ;
    
    
    /**
     * Wrapper class to diplay fileIn name: allow t ochange display format
     * @param f
     * @return
     */
    public static String getDisplayName(File f)
    {
        return CR_LF + f.toString();
    }
    
    /**
     * Load the debug File that contain  full class names, one class name per line
     */
    public static void loadDebugFile()
    {
        String sep = System.getProperty("file.separator");
        String strDebugFile = UrlUtil.getKeyDirectory() + sep + "debug.txt";
        
        File debugFile = new File(strDebugFile);
        
        if (debugFile.exists())
        {
            try
            {
                //JOptionPane.showMessageDialog(null, "OK. Loading debug fileIn " + debugFile);
                new Debug(debugFile);                
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    
    /**
     * Convert a String to a Base64 String
     * @param s     the String to convert
     * @return      The converted String converted in Base64
     */
    public static String toBase64(String s)
    {
        return Base64.byteArrayToBase64(s.getBytes());
    }
    
    /**
     * Convert a String to a Base64 String
     * @param s     the String to convert
     * @return      The converted String converted in Base64
     */
    public static String fromBase64(String s)
    {
        return new String(Base64.base64ToByteArray(s));
    }    
    
    /**
     * Cut the String to 64 chars max
     * @param in            the string to cut
     * @param maxLength     the string max length
     * @return      the cut string at maxLength chars
     */
    public static String cut(String in, int maxLength)
    {
        if (in == null)
        {
            return null;
        }
        
        if (in.length() <= maxLength)
        {
            return in;
        }
        
        // Ok,  cut it to maxLength chars!
        return in.substring(0, maxLength);
        
    }
    
	/**
	 * Cut the String to 64 chars max
	 * @param in	the string to cut
	 * @return		the cut string at 64chars
	 */
	public static String cut64(String in)
	{
		if (in == null)
		{
			return null;
		}
		
		if (in.length() <= 64)
		{
			return in;
		}
		
		// Ok,  cut it to 64 chars!
		return in.substring(0, 64);
        
	}
	
	/**
	 * Return true if the filename is a Window possible Filename
	 * @param filename	the filename to test
	 * @return true if the filename is a Window Filename
	 */
	public static boolean isPossibleWindowFilename(String filename)
	{
		if (filename.indexOf("\\") != -1 ||
			filename.indexOf("/")  != -1 ||
			filename.indexOf(":")  != -1 ||
			filename.indexOf("*")  != -1 ||
			filename.indexOf("?")  != -1 ||
			filename.indexOf("\"") != -1 ||
			filename.indexOf("\"") != -1 ||
			filename.indexOf("<")  != -1 ||
			filename.indexOf(">")  != -1 ||
			filename.indexOf("|")  != -1) 
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
    /**
     * Build a HashMap using the values of a string in the format HashMap.toString()
     * <br>
     * Note that the two strings s1 and s2 *are* always equal:
     * <br> 
     * <br>Map map = new HashMap();
     * <br>s1 = map.toString();
     * <br>Map map2 = toMap(s1);
     * <br>s2 = map2.toString();
     * <br>
     * @param string     a string in format of HashMap.toString()
     * @return          a Map rebuilded from the String
     */
    public static Map toMap(String string)
    {        
        Map map = new HashMap();
        
        if (string == null )
        {
            throw new IllegalArgumentException("Input string is null!");
        }
        
        // Rebuild a Hasmap from a string
        StringTokenizer st = new StringTokenizer(string, ",{}", false);
        String sToken;
        
        if (st.countTokens() == 0)
        {
            return map; // return an empty Map
        }
        
        String sProperties = "";
        while (st.hasMoreTokens())
        {
            sToken = st.nextToken();
            sProperties += sToken.trim() + CR_LF;
        }
        
//        debug(string);
//        debug("");
//        debug(sProperties);
//        debug("");
        
        //Ok, put the properties
        ByteArrayInputStream in = new ByteArrayInputStream(sProperties.getBytes());
        Properties prop = new Properties();
        try
        {
            prop.load(in);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
        
        if (prop.isEmpty())
        {
            return map;
        }
        
        Enumeration keys = prop.keys() ;
        
        String key = null;
        String value = null;
        
        while(keys.hasMoreElements())
        {
            key = (String) keys.nextElement();
            value = prop.getProperty(key);
            
            map.put(key, value);
        }        
        
        return map;
    }
    
    
    /**
     * Change the Version line with Cryptix to cGeep in a Public Key Block String 
     * @param   publicKeyBlock        the input Key Block with old Version
     * @return  the Public Key Block String with the new Version
     */
    public static String changeKeyVersion(String publicKeyBlock)
    {
        
         String versionLine = " ";
         String lineVer = null;
         
         Version ver = new Version();
         
         // Security to prevent a infinite loop
         if (publicKeyBlock.indexOf("Version") == -1)
         {
             return publicKeyBlock;
         }
         
         // Reverse string to have Last version comming first!
         publicKeyBlock = StringUtils.reverse(publicKeyBlock);
         
      // Convert the Cryptix Version String to us!
         BufferedReader buf = new BufferedReader(new StringReader(publicKeyBlock));
         
         while(true)
         {
            try
            {
                versionLine = buf.readLine();
            }
            catch (IOException e)
            {
                // Nothing should/cound never happen on a String!
                versionLine = " ";
            }
    
            //if(versionLine.startsWith("Version"))
            //Search for Version reversed!
            
            //if(versionLine.indexOf("noisreV") != -1)
            if (versionLine.endsWith("noisreV"))
             {
                 String newVersionLine = "Version : " + ver.NAME + " " + ver.toString ();
                 
                 newVersionLine += " - Easy Encryption For All";
                 
                 //Reverse newVersionLine
                 newVersionLine = StringUtils.reverse(newVersionLine);
                 publicKeyBlock = StringMgr.ReplaceFirst(publicKeyBlock, versionLine, newVersionLine);
                 
                 //Put the string in correct order
                 publicKeyBlock = StringUtils.reverse(publicKeyBlock);
                 
                 break;
             }         
         }
         
         return publicKeyBlock;
    }    
    
    /**
     * Remove the HYPERLINK tags added by Outlook for URL.
     * @param line
     */
    public static String removeHyperlinkOutlookTag(String line)
    {
        if (line == null || line.length() <= 1)
        {
            return null;
        }
        
        String lineFirstpart = null;
        String lineLastpart = null;
        String newLine =  null;
                
        final String HYPERLINK = "HYPERLINK \"";
        
        while(line.contains(HYPERLINK))
        {
            int beginQuote = line.indexOf(HYPERLINK) + HYPERLINK.length() + 1;
            
            lineFirstpart = line.substring(0, line.indexOf(HYPERLINK));
            debug(lineFirstpart);
            
            String subLine = line.substring(beginQuote);
            
            debug(subLine);
            
            int endQuote = subLine.indexOf("\"") + 1;
            lineLastpart = subLine.substring(endQuote);
            debug(lineLastpart);
            
            newLine = lineFirstpart +  lineLastpart;
            debug(newLine);
            line = newLine;
        }
        
        return line;
        
    }
    
    /**
     * 
     * @return true if Outlook Office is the default mailer
     */
    public static boolean isOutlookOfficeDefaultMailer()
    {
        // Windows Only!
        if(System.getProperty("os.name").indexOf("Windows") == -1)
        {
            return false;
        }
        
        //  06/11/07 21:00 NDP - No Outlook Office email guessing if Web Start on
        
        if (UrlUtil.isWebStartOn())
        {
            return false;
        }
        
        try
        {
            Registry registry = new Registry();
            String result = registry.getClassesRootKeyValue("mailto\\shell\\open\\command", "");
            
            if (result == null)
            {
                result = "";
            }
            
            result = result.toLowerCase();
            if (result.contains("outlook.exe"))
            {
                return true;
            }
            else
            {
                return false;
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JOptionPaneCustom.showException(null, e);
        }
        
        return false;
        
    }
     
    
    
    /**
     * @return the Outlook Office version installed on the PC for this Current User
     */
   
    public static String getOutlookOfficeVersion()
    {   
    	String outlookOfficeVersion = "0"; // same as in CgeepAddRegistry.cpp program
        
        // Windows Only!
        if(System.getProperty("os.name").indexOf("Windows") == -1)
        {
        	return outlookOfficeVersion;
        }
                
        try
        {
            // Order is important: from newest to oldest
            String [] versions = {"14.0", "12.0", "11.0", "10.0", "9.0"};
                            
            Registry registry = new Registry();
            
            for (int i = 0; i < versions.length; i++)
            {
                //HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Office\11.0\Outlook\InstallRoot
                String subkey = "Software\\Microsoft\\Office\\" 
                                    + versions[i] + "\\Outlook\\InstallRoot";
                
                String path  = registry.getLocalMachineKeyValue(subkey, "Path");
                  
                if (path != null && path.length() > 1)
                {
                    outlookOfficeVersion =  versions[i]; 
                    return outlookOfficeVersion;
                }
                
                //System.out.println("subkey: " + subkey + ":");
                //System.out.println("Path  : " + path + ":");
                
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(); 
            outlookOfficeVersion = "0"; // same as in CgeepAddRegistry.cpp program
        }
        return outlookOfficeVersion;
  }
    
    // The value in Outlook for email that are composed in HTML Format (It's a DWORD)
    private static final int OUTLOOK_HTML_OUT_FORMATED = 131072;
    
    /**
     *     * @return true if Outlook for emails are composed in HTML Format (per default)
     */
    public static boolean isOutlookHtmlOutFormated()
    {        
        try
        {
            String olVersion = getOutlookOfficeVersion();
            
            // Get the editor
            Registry registry = new Registry();
            
            //HKEY_CURRENT_USER\Software\Microsoft\Office\11.0\Outlook\Options\Mail
            String subkey = "Software\\Microsoft\\Office\\" + olVersion + "\\Outlook\\Options\\Mail";
                
            int format = (int) registry.getCurrentUserKeyValueDword(subkey, "EditorPreference");
            
            if (format == OUTLOOK_HTML_OUT_FORMATED)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception e)
        {
            JOptionPaneCustom.showException(null, e);            
            return true;
        }
        
    }
    
    
    /**
     * debug tool
     */
    private static void debug(String s)
    {
        if (new Util().DEBUG)
        {
            System.out.println(s);
            //System.out.println(this.getClass().getName() + " " + new Date() + " " + s);
        }
    }

    /**
     * Clean a String from bad Outlook binary CR/LF
     * @param   in        the input String with bad CR/LF
     * @return  the cleaned String
     * @throws IllegalArgumentException  if a I/O error occurs (act as a IOException wrapper)
     */
    
    public static String removeTrailingBlanks(String in)
    {
        StringReader stringReader = new StringReader(in);
        BufferedReader reader = new BufferedReader(stringReader);
    
        String line = null;
        StringBuffer newBody = new StringBuffer();
        
        try
        {
            while(( line = reader.readLine()) != null)
            {                                            
                newBody.append(line.trim());
                newBody.append(CR_LF);
            }
        }
        catch (IOException ioe)
        {
            throw new IllegalArgumentException(ioe);
        }
              
        String newBodyStr = newBody.toString();
        
        // if there is only one line ==> remove the CR_LF
        if (newBodyStr.lastIndexOf(CR_LF) == newBodyStr.indexOf(CR_LF))
        {
            newBodyStr = newBodyStr.replace(CR_LF, "");
        }
        
        return newBodyStr;

    }

    /**
     * Remove the CR:LF at end of long lines coming from Outlook HTML formated emails.
     * <br> Necessary because Outlook add new CR-LF for long lines (> 120 chars)
     * 
     * @param   in        the input String with bad CR/LF
     * @return  the cleaned String with no CR/LF for long lines coming from HTML Outlook
     * @throws IllegalArgumentException  if a I/O error occurs (act as a IOException wrapper)
     */
    
    private static final int LONG_OUTLOOK_LINE = 120;
    
    public static String noCrlfForLongLinesInHtml(String in)
    {
                        
        StringReader stringReader = new StringReader(in);
        BufferedReader reader = new BufferedReader(stringReader);
        StringWriter writer = new StringWriter();
    
        String line = null;
        String newBody = "";
            
//        if (DEBUG)
//        {
//            JOptionPane.showMessageDialog(null, "noCrlfForLongLinesInHtml() BEGIN 1");
//        }
        
        boolean isOutlookHtmlOutFormated = isOutlookHtmlOutFormated();
        
//        if (DEBUG)
//        {
//            JOptionPane.showMessageDialog(null, "noCrlfForLongLinesInHtml() BEGIN 2");
//        }
        
        try
        {
            while(( line = reader.readLine()) != null)
            {                  
                if (line.length() > LONG_OUTLOOK_LINE && isOutlookHtmlOutFormated)
                {
                    newBody += line ;
                }
                else
                {
                    newBody += line + CR_LF;
                }
            }
        }
        catch (IOException ioe)
        {
            throw new IllegalArgumentException(ioe);
        }
                
        return newBody;
    }    
    
    /**
     * 
     * Word Wrap a text so that each line is shorter than the boundary 
     * (ex: 76 for emails)
     * <br>
     * Boundary is defined as a user preference in UserPreferencesManager.
     * Defaults to 76 (as Outlook Office).
     * 
     * @param str   The Text String to wrap
     * @return      The word wrapped String
     * @throws IllegalArgumentException    if the passed Sgtring is null 
     */
    
    public static String wordWrap(String str) 
    {                
        if (str == null)
        {
            throw new IllegalArgumentException("Input String can not be null!");
        }
        
        // Security Check: if any Exception is thrown ==> return original text
                
        try
        {
            int autoReturnLine = UserPreferencesManager.getAutoReturnLine();            
            WordWrapper wordWrapper = new WordWrapper(str, autoReturnLine);
            
            String wrappedText = wordWrapper.getWrappedTextAsString();
            return wrappedText;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return str;
        }
    }    

       
    
}
