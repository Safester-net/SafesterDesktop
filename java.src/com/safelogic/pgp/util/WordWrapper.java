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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Vector;




/**
 * This class format a text to a max line length
 * Usage :
 * 
 *  Build a WordWrapper using constructor with original text
 *  and max line length
 *  
 *  call getWrappedText()
 */

public class WordWrapper {
	
	private String text = null;

	private List<String> wrappedText = null;
	
	private int max_line_length = 0;
		
	/**
	 * Constructor
	 * @param text			The text to format
	 * @param length		The max line length
	 */
	
	public WordWrapper(String text, int length)
	{
		this.text = text;
		this.max_line_length = length;
	}
	
	/**
	 * @return the text to wrap into a List of lines
	 */
	private List<String> getTextAsLines()
	{
	    List<String> lines = new Vector<String>();
	    	    
        StringReader stringReader = new StringReader(this.text);
        BufferedReader reader = new BufferedReader(stringReader);
        StringWriter writer = new StringWriter();
    
        String line = null;
            
        try
        {
            while(( line = reader.readLine()) != null)
            {    
                lines.add(line);  
            }
        }
        catch (IOException ioe)
        {
            throw new IllegalArgumentException(ioe);
        }
        
        return lines;        	   
	}
	
	/**
	 * Split each long line (line > this.max_line_length) into two lines
	 * 
	 * @param lines    the List of lines to split
	 * @return true if there a no more lines to split
	 */
	private boolean splitLongLines(List<String> lines)
	{
	    boolean moreSplitsToDo = false;
	    
	    List<String> newLines = new Vector<String>();
	    
	    for (int i = 0; i < lines.size(); i++)
        {
            String line = lines.get(i);
            
            if (line.length() > max_line_length)
            {
                String line1 = null;
                String line2 = null;
                                
                if(line.substring(0, max_line_length).contains(" "))
                {
                    int index = line.substring(0, max_line_length).lastIndexOf(" ");
                                        
                    //30/07/08 20:05 - NDP: Test if index != -1 before spliting line on index
                    if (index != -1)
                    {
                        line1 = line.substring(0, index);
                        line2 = line.substring(index + 1);
                        line2 = line2.trim();
                    }
                    else
                    {
                        line1 = line.substring(0, max_line_length);
                        line2 = line.substring(max_line_length);         
                        line2 = line2.trim();                        
                    }
                }
                else
                {
                    line1 = line.substring(0, max_line_length);
                    line2 = line.substring(max_line_length);         
                    line2 = line2.trim();
                }
                
                newLines.add(line1);
                newLines.add(line2);
                
                moreSplitsToDo = true;
            }
            else
            {
                newLines.add(line);                
            }
        }
	    
	    // Put back the new lines into the passed List as parameter
	    lines.clear();
	    lines.addAll(newLines);
	    
	    return moreSplitsToDo;
	    
	}
	
    /**
     * Get the formatted text
     * @return  the formatted text as a  String
     */
    
    public String getWrappedTextAsString()
    {               
        List<String> lines = getTextAsLines();
        
        while ( splitLongLines(lines));
        
        StringBuffer wrappedText = new StringBuffer();
        
        for(int i = 0; i< lines.size(); i++)
        {
            //wrappedText += lines.get(i) + Util.CR_LF;;
            wrappedText.append(lines.get(i));
            wrappedText.append(Util.CR_LF);
        }
        
        return wrappedText.toString();
    }   	
	
	public static void main(String[] args)
	{
		WordWrapper wordWrapper = null;
		try{
			int length = 79;

			File f = new File("c:\\temp\\test.txt");
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			
			String readText = "";
			String sLine = br.readLine();
			
			while(sLine !=null)
			{
				readText +=sLine + System.getProperty("line.separator");
				sLine = br.readLine();
				System.out.println(sLine);
			}
			
			wordWrapper = new WordWrapper(readText, length);

			System.out.println();
			System.out.println(wordWrapper.getWrappedTextAsString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
