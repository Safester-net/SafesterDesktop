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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;

import com.safelogic.utilx.io.stream.LineInputStream;
import com.swing.util.SwingUtil;

import net.safester.application.addrbooknew.AddressBookImportStart;

/**
 * Mis methos to read Text files in HTML.
 *
 * @author Nicolas de Pomereu
 */
public class HtmlTextUtil {

    public static final String CR_LF = System.getProperty("line.separator");

    public static int nbBr = 0;

//    /**
//     * Format the content of a string for HTML with a <br> for each cr.lf
//     * <br>
//     *
//     * @param in            The HTML text
//     * @return              The HTML text with the <br> added
//     * @throws IOException
//     */
//    public static String removeCrLF(String in)
//    {
//        if (in == null)
//        {
//            return in;
//        }
//
//        StringBuilder text = new StringBuilder();
//        BufferedReader br = null;
//
//        try
//        {
//            br = new BufferedReader(new StringReader(in));
//
//            String line = null;
//
//            while ((line = br.readLine()) != null)
//            {
//                text.append(line);
//            }
//        }
//        catch (IOException ioe)
//        {
//            // Wrap the exception that can never be thrown because the input stream is a string
//            throw new IllegalArgumentException(ioe);
//        }
//        finally
//        {
//            IOUtils.closeQuietly(br);
//        }
//
//        return text.toString();
//    }

    /**
     * Count the numbezr of lines in a string
     * @param in        the input string
     * @return  the number of lines in the string, 0 if empty
     * @throws IOException
     */
    public static int countLines(String in) throws IOException
    {
        if (in == null)
        {
            return 0;
        }

        BufferedReader br = new BufferedReader(new StringReader(in));

        @SuppressWarnings("unused")
	String line = null;
        int lineNum = 0;

        while ((line = br.readLine()) != null)
        {
            lineNum++;
        }

        return lineNum;

    }

    /**
     * Format the content of a string for HTML with a <br> for each cr.lf
     * <br>
     *
     * @param in            The HTML text
     * @return              The HTML text with the <br> added
     * @throws IOException
     */
    public static String formatWithBr(String in)
    {
        if (in == null)
        {
            return in;
        }

        StringBuilder text = new StringBuilder();
        BufferedReader br = null;
        
        try
        {
            br = new BufferedReader(new StringReader(in));

            String line = null;

            int lineNum = 0;

            while ((line = br.readLine()) != null)
            {
                if (lineNum > 0)
                {
                    text.append("</div><div>");
                }
                lineNum++;
                if(line.length() == 0){
                    nbBr++;
                }
                text.append(line);

            }
            
            //nbBr = lineNum;
           
        }
        catch (IOException ioe)
        {
            // Wrap the exception that can never be thrown because the input stream is a string
            throw new IllegalArgumentException(ioe);
        }
        finally
        {
            IOUtils.closeQuietly(br);
        }

        return "<div>" + text.toString();
    }

    /**
     * Return  the HTML content of a  HTML resource file in the message file package
     * The default package name is "net.safester.application.messages.files"
     * 
     * @param helpContentKeyWord    The key word for help file retrieve
     * @langaue the language tag    
     * @return  the HTML content of a  HTML resource file in the message file package
     * @throws IOException
     */
    public static String getHtmlHelpContent(String helpContentKeyWord, String language) 
    {
        String resource = "net.safester.application.messages.files";
        return getHtmlHelpContent(resource, language, helpContentKeyWord);
    }
    
    /**
     * Return  the HTML content of a  HTML resource file in the message file package
     * The default package name is "net.safester.application.messages.files"
     * 
     * @param helpContentKeyWord    The key word for help file retrieve
     * @return  the HTML content of a  HTML resource file in the message file package
     * @throws IOException
     */
    public static String getHtmlHelpContent(String helpContentKeyWord) 
    {
        String resource = "net.safester.application.messages.files";
        String language = "en";
        return getHtmlHelpContent(resource, language, helpContentKeyWord);
    }
    
    
    /**
     * Return  the HTML content of a  HTML resource file in the message file package
     * 
     * @param packageName           the package name where to find the files
     * @param language		    the language code "en", "fr", "it", Etc
     * @param helpContentKeyWord    The key word for help file retrieve.
     *                              The language and html extension will be added
     *                              Ex: "myfile" ==> "myfile_en.html" 
     * @return  the HTML content of a  HTML resource file in the message file package
     * @throws IOException
     */
    private static String getHtmlHelpContent(String packageName, String language, String helpContentKeyWord) 
    {
        if (packageName == null)
        {
            throw new IllegalArgumentException("packageName can not be null!");
        }
 
        if (language == null)
        {
            throw new IllegalArgumentException("language can not be null!");
        }    
        
        if (helpContentKeyWord == null)
        {
            throw new IllegalArgumentException("helpContentKeyWord can not be null!");
        }        
              
        String htmlContent;
        try {

            if (! packageName.startsWith("/"))
            {
                packageName = "/" + packageName;
            }
            packageName = packageName.replace(".", "/");

            // KEEP THIS CODE AS MODEL
            //java.net.URL myURL
            //  = ResourceBundleTest.class.getResource("/com/safelogic/pgp/test/MyResource_fr.properties");

            String helpFile = helpContentKeyWord + "_" + language + ".html";
            String urlResource = packageName + "/" + helpFile;

            //debug(urlResource);

            java.net.URL myURL = AddressBookImportStart.class.getResource(urlResource);

            if (myURL == null) {
                return "<font face=\"Arial\" size=4><br>"
                        + "<b>Please apologize. <br>  "
                        + "Help is not yet available for this topic. </b> <br>"
                        + "<br>"
                        + "(" + helpFile + ")";
            }

            InputStream is = myURL.openStream();

            BufferedInputStream bisIn = new BufferedInputStream(is);
            LineInputStream lisIn = new LineInputStream(bisIn);

            String sLine = new String();

            htmlContent = "";

            while ((sLine = lisIn.readLine()) != null) {
                sLine = sLine.trim();
                //debug(sLine);

                htmlContent += sLine + CR_LF;

            }

            htmlContent = SwingUtil.formatHtmlContentForSyntheticaAndNimbus(htmlContent);           
            
            lisIn.close();
        } catch (IOException e) {
            e.printStackTrace();
            htmlContent = e.getMessage();
        }

        return htmlContent;
    }
}
