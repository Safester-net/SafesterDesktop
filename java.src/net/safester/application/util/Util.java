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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

import com.safelogic.utilx.Base64;
import com.safelogic.utilx.Debug;


/**
 * Misc utilites for pgeep
 * @author Nicolas de Pomereu
 *
 */
public class Util 
{

    /** The debug flag */ 
    protected static boolean DEBUG = true;
    
    /** Universal and clean line separator */
    public static String CR_LF = System.getProperty("line.separator") ;
    

    public static String fillWithHtmlBlanks(String s, int numTab)
    {
        String sNoHtml = s;

        while (sNoHtml.length() < numTab)
        {
            s += "&nbsp; ";
            sNoHtml += " ";
        }
        return s;
    }

    public static String fillWithBlanks(String s, int numTab)
    {
        while (s.length() < numTab)
        {
            s += " ";
        }
        return s;
    }
    
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
      //  String strDebugFile = UrlUtil.getKeyDirectory() + sep + "debug.txt";
          String strDebugFile = System.getProperty("user.home") + sep + "debug.txt";
        
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
     * debug tool
     */
    public static void debug(String s)
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
     * Remove the last useless ";" from a email recipients list
     * @param recipients    the list of recipients in as string separtaed by ";"
     * @return  the list of recipients without the last useless ";"
     */
    public static String removeTrailingSemiColumns(String recipients)
    {
        if (recipients == null)
        {
            return recipients;
        }

        recipients = recipients.trim();
        if (recipients.endsWith(";"))
        {
            recipients = StringUtils.substringBeforeLast(recipients, ";");
        }

        return recipients;

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
    
//    public static String wordWrap(String str)
//    {
//        if (str == null)
//        {
//            throw new IllegalArgumentException("Input String can not be null!");
//        }
//
//        // Security Check: if any Exception is thrown ==> return original text
//
//        try
//        {
//            int autoReturnLine = UserPreferencesManager.getAutoReturnLine();
//            WordWrapper wordWrapper = new WordWrapper(str, autoReturnLine);
//
//            String wrappedText = wordWrapper.getWrappedTextAsString();
//            return wrappedText;
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            return str;
//        }
//    }

       
    
}
