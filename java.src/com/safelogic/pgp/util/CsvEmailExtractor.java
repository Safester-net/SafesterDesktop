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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import com.safelogic.utilx.syntax.EmailChecker;

/**
 *
 * @author  Alexandre Becquereau
 */
public class CsvEmailExtractor {
	
	boolean debug = false;
    
	//The file to parse
	File mFile;
	//Separator in file (, ; etc.)
	String separator;
	//The list containing emails
	List<String> emails;
	//Total number of line founded in file
	int nbLines = 0;
    
	/** 
	 * Creates new form CsvEmailExtractor 
	 */
	
    public CsvEmailExtractor(File csvFile, String sep) {
    
    	this.mFile = csvFile;
    	this.separator = sep;
    }
    
    /**
     * Extract emails from a csv file
     * @throws FileNotFoundException if File does not exists
     * @throws IOException	         if an IO error occurs
     * @throws SecurityException	 if read access to file is not permitted
     */
    public void extractEmail()
    	throws FileNotFoundException, IOException, SecurityException
    {
    	if(!mFile.exists())
    	{
    		throw new FileNotFoundException("File " + this.mFile.getName() + " does not exists."); 
    	}
    	if(!mFile.canRead())
    	{
    		throw new SecurityException ("Can't read " + this.mFile.getName() + ". Check file access permission.");
    	}
    	
    	//Read file
    	FileReader fr = new FileReader(this.mFile);
    	BufferedReader br = new BufferedReader(fr);
  
    	emails = new Vector<String>();
    	String sLine = br.readLine();
    	this.nbLines = 0;
    	while (sLine !=null)
    	{
    		//parse each line of file
    		debug(sLine);
    		nbLines++;
    		StringTokenizer st = new StringTokenizer(sLine, this.separator);
  
    		while(st.hasMoreElements())
    		{
    			String token = st.nextToken();
    			if(token.indexOf('@') != -1)
    			{
    				//Probably found one email address
    				String email = getEmailString(token).trim();
    				//Check if email is valid
    				EmailChecker emailChecker = new EmailChecker(email);
    				if(emailChecker.isValid())
    				{
    					//Everything is ok add email to list
    					emails.add(email);
    				}
    				//exit loop
    				break;
    			}
    		}
    		//Next line
    		sLine = br.readLine();
    	}
    	
    	br.close();
    }
    
    /**
     * Return the address email without following char : '<' '>' '(' and ')' and '"';
     * @param str
     * @return
     */
    public static String getEmailString(String str)
    {
    	//Key can be without emails => special treat
    	if(str.indexOf("@") == -1)
    	{
    		if(str.indexOf('<')!=-1 && str.indexOf('>')!=-1)
    		{
    			return StringUtils.substringBetween(str, "<", ">");
    		}
    		if(StringUtils.countMatches(str, "\"") == 2)
    		{
    			return StringUtils.substringBetween(str, "\"");
    		}
    		if(str.indexOf('(')!=-1 && str.indexOf(')')!=-1)
    		{
    			return StringUtils.substringBetween(str, "(", ")");
    		}
    		return str;
    	}
    	
    	//debug(str);
    	//Remove potential <
    	if(str.indexOf("<") != -1)
    	{
    		str = removeChar( str, "<");
    		return getEmailString(str);
    	}

    	
    	//Remove potential >
    	if(str.indexOf(">") !=-1)
    	{
    		str = removeChar(str,">");
    		return getEmailString(str);
    	}
    	//Remove potential (
    	if(str.indexOf("(") !=-1)
    	{
    		str = removeChar(str,"(");
    		return getEmailString(str);
    	}
    	 
    	//Remove potential )
    	if(str.indexOf(")") !=-1)
    	{
    		str = removeChar(str,")");
    		return getEmailString(str);
    	}    	
    	//Remove potential "
    	if(str.indexOf("\"") !=-1)
    	{
    		str = removeChar(str,"\"");
    		return getEmailString(str);
    	}
    	
        //Remove potential '
        if(str.indexOf("\'") !=-1)
        {
            str = removeChar(str,"\'");
            return getEmailString(str);
        }
        
    	
    	return str;
    }
    
    /**
     * Remove first occurence of specified char and return the part of
     * the input string containing @
     * @param str				The string to analyse
     * @param charToRemove		The char to remove
     * @return					The part of the string containing @
     */
    private static String removeChar(String str, String charToRemove)
    {
    	if(str.indexOf(charToRemove) != -1)
    	{
    		StringTokenizer tokenizer = new StringTokenizer(str, charToRemove);
    		while(tokenizer.hasMoreTokens())
    		{
    			String sTemp = tokenizer.nextToken();
    			if(sTemp.indexOf("@")!=-1)
    			{
    				return sTemp;
    			}
    		}
    	}
    	return str;
    }
    
    /**
     * Get the list of emails founded
     * @return	a List<String>
     */

	public List<String> getEmails() {
		return emails;
	}

	/**
	 * 
	 * @return the number of lines read in file
	 */
	public int getNbLines() {
		return nbLines;
	}
    
	/**
	 * 
	 * @return the number of emails founded in file
	 */
	public int getNbEmails() {
		if(this.emails != null)
		{
			return emails.size();
		}
		return 0;
	}
	
	/**
	 * Main function for test
	 */
	public static void main(String []args)
	{
		
		try{
			File f = new File("c:\\contact.csv");
			CsvEmailExtractor csvEmailExtractor = new CsvEmailExtractor(f, ",");
			csvEmailExtractor.extractEmail();
			System.out.println("Nb lines in file : " + csvEmailExtractor.getNbLines());
			System.out.println("Nb emails in file : " + csvEmailExtractor.getNbEmails());
			
			if(csvEmailExtractor.getNbEmails() != 0)
			{
				List<String> emails = csvEmailExtractor.getEmails();
				for(int i = 0; i<emails.size(); i++)
				{
					System.out.println(emails.get(i));
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void debug(String str)
	{
		if(debug)
			System.out.println("DBG>" + str);
	}
    
   
}
