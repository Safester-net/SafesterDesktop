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

import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JLabel;

import com.safelogic.utilx.StringMgr;

/**
 * @author Nicolas de Pomereu
 * 
 * An JeditorPane that detects hyperlink in texts and expand/code them
 * in HTML
 *
 */
public class JEditorPaneLinkDetector extends JEditorPane
{

    public static String CR_LF = System.getProperty("line.separator") ;
    
    
    /** 
     * if true, we will prefix the content with a <pre> tag 
     * Necessary for email decryption panel, in order to have a clean paste/copy 
     */
    private boolean usePreTag = false;
    
    /**
     * Constructor: will not add the <pre> tag
     */
    public JEditorPaneLinkDetector()
    {
        super();       
    }
    
    /**
     * @param usePre  if true, we will prefix the content with a <pre> tag 
     *                Necessary for email decryption panel, in order to have
     *                a clean paste/copy 
     */
    public JEditorPaneLinkDetector(boolean usePreTag)
    {
        this.usePreTag = usePreTag;
    }
           
    /* (non-Javadoc)
     * @see javax.swing.JEditorPane#setText(java.lang.String)
     */
    @Override
    public void setText(String text)
    {
        if (text == null)
        {
            super.setText(null);
            return;
        }
        
        String htmlText = null;
        
        // Detect the links
        try
        {
            htmlText = detectLinkAndCodeHtml(text);
        }
        catch (IOException e)
        {
            htmlText = text;
            e.printStackTrace();
        }

//        System.out.println();
//        System.out.println(text);
//        System.out.println(htmlText);
//        System.out.println();
        
        JLabel label = new JLabel();
        Font f = label.getFont();
        
        String textToSet = "<font face=\"" + f.getName() + "\" size=3>" + htmlText;
        
        if (usePreTag)
        {
            textToSet = "<pre>" + textToSet;
        }
        
        super.setText(textToSet);
    }

    /**
     * 
     * Takes an input text and add <br> per lines + URL anchors
     * 
     * @param text      the input formated text
     * @return          and output HTML text with <br> for CR_LF and URL link code
     * 
     * @throws Exception
     */
    public static String detectLinkAndCodeHtml(String text)
        throws IOException
    {
        BufferedReader br = new BufferedReader(new StringReader(text));

        String line = null;

        StringBuffer textOutBuffer = new StringBuffer();
        
        while ((line = br.readLine()) != null) 
        {            
            // Do not take care of lines with "<a href=" or closing </a>
            if (line.toLowerCase().contains("<a href=") ||
                    line.toLowerCase().contains("</a>"))
            {
                textOutBuffer.append(line);
                textOutBuffer.append(CR_LF);
               // textOutBuffer.append("<br>");
                continue;
            }
            
            // Replace the lines with only "www.dns.com" by "http://www.dns.com"
            line = replaceRawWwwByWwwHttp(line);
            
            //Special replacement for use with HTMLEditorPane
            line = line.replaceAll("&#160;", " ");
            line = line.replaceAll("<br>", " <br> ");
            // Separate input by spaces ( URLs don't have spaces )
            String [] parts = line.split("\\s");

            // Attempt to convert each item into an URL.   
            for( String item : parts ) try {
                URL url = new URL(item);
                // If possible then replace with anchor...
                //System.out.print("<a href=\"" + url + "\">"+ url + "</a> " );

                textOutBuffer.append("<a href=\"" + url + "\">"+ url + "</a> "); 
            } catch (MalformedURLException e) {
                // If there was an URL that was not it!...
                //System.out.print( item + " " );
                
                textOutBuffer.append(item + " ");
            }            

            //System.out.println(line);
            textOutBuffer.append(CR_LF);
           // textOutBuffer.append("<br>");
        }

        return textOutBuffer.toString();
    }    

    /**
     * Replace all "www." with no http//: prefix by "http://www." 
     * 
     * @param string    the input String
     * @return          the replaced String with no more raw "www." 
     */
    public static String replaceRawWwwByWwwHttp(String string)
    {
        string = StringMgr.ReplaceAll(string, "http://www.", "**safelogic_http_tag**");
        string = StringMgr.ReplaceAll(string, "https://www.", "**safelogic_https_tag**");
        
        string = StringMgr.ReplaceAll(string, "www.", "http://www.");
        string = StringMgr.ReplaceAll(string, "**safelogic_http_tag**", "http://www.");
        string = StringMgr.ReplaceAll(string, "**safelogic_https_tag**", "https://www.");    
        return string;       
    }


}

