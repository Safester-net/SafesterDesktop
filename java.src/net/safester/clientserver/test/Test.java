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
package net.safester.clientserver.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.Locale;

import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.translate.NumericEntityEscaper;
import org.awakefw.file.api.util.HtmlConverter;

import net.safester.application.util.crypto.PassphraseUtil;


public class Test
{
    
    Connection connection = null;
    
    /**
     * @param connection
     */
    public Test(Connection connection)
    {
        this.connection = connection;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {             
	
	
	System.out.println(Locale.FRENCH.toLanguageTag());
        
        System.out.println(HtmlConverter.fromHtml("Charles Andr&eacute;"));
        System.out.println(HtmlConverter.fromHtml("Charles Andr&#233;"));
               
	System.out.println(System.currentTimeMillis());
	
	String result = PassphraseUtil.computeHashAndSaltedPassphrase("brunopaul88@outlook.com","brunopaul88".toCharArray() );
	
        System.out.println(result);
        if (true) return;
        
        String text = JOptionPane.showInputDialog("Enter the text");
        
        String textHtml = null;
        
	textHtml = org.apache.commons.lang3.StringEscapeUtils.ESCAPE_HTML4.with(NumericEntityEscaper.between(0x7f, Integer.MAX_VALUE) ).translate(text);        
        System.out.println("textHtml lang3: " + textHtml);
        
//        textHtml = org.apache.commons.lang3.StringEscapeUtils.escapeHtml(text);
//        System.out.println("textHtml lang3: " + textHtml);
        
        
        text = StringEscapeUtils.unescapeHtml4(textHtml);
        JOptionPane.showInputDialog("Enter the text", text);
        
        if (true) return;
        
        System.out.println(new Date());
        
        File file = new File("c:\\temp\\crypto-145.zip");
        
        String s = "00000000000000000000000000000000000000000000000000";            
        wipeFile(file, s);
        
        s = "11111111111111111111111111111111111111111111111111";
        wipeFile(file, s);
        
        System.out.println(new Date());
    }

    /**
     * Wipe the file using a pattern
     * 
     * @param file      the file to wipe
     * @param pattern    the pattern to use
     * @throws IOException
     */
    public static void wipeFile(File file, String pattern) throws IOException
    {               
        if (file == null)
        {
            throw new IllegalArgumentException("file can not be null!");
        }
        
        if (pattern == null)
        {
            throw new IllegalArgumentException("pattern can not be null!");
        }
        
        BufferedOutputStream bos = null;
        
        try
        {
            Long length = file.length();
            bos = new BufferedOutputStream(new FileOutputStream(file));            
            
            int cpt =0;            
            int len = pattern.length();
            
            while (cpt <= length)
            {
                cpt += len;
                bos.write(pattern.getBytes());
            }
        }
        finally
        {
            IOUtils.closeQuietly(bos);
        }        
    }
    
}

