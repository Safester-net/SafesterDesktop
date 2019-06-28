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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.safelogic.utilx.Debug;

import net.safester.application.addrbooknew.RecipientEntry;
import net.safester.application.parms.Parms;

/**
 *
 * @author Nicolas de Pomereu
 */
public class AddressBookUtil {

    public static boolean DEBUG = Debug.isSet(AddressBookUtil.class);
        
    public static final String CSV_SEPARATOR = ";";
    public static final String COMMA = ",";
    public static final String SEMI_COLUMN = ";";
    
    /**
     * Save a List of recipients into a CSV formated file.
     *
     * @param file the CSV formated file
     * @param pdfRecipients the list of PDF Recipients,
     * @throws IOException
     */
    /*
    public static synchronized void save(File file, List<PdfRecipient> pdfRecipients) throws IOException {
        Writer writer = null;

        String charsetName = Parms.DEFAULT_CHARSET;
        try {
            OutputStream out = new FileOutputStream(CryptAppUtil.getAddressBookFile());
            OutputStreamWriter outWriter = new OutputStreamWriter(out, charsetName);
            writer = new BufferedWriter(outWriter);

            for (RecipientEntry recipient : pdfRecipients) {
                writer.write(recipient.toAdressBookRecord() + CR_LF);
            }

        } finally {
            IOUtils.closeQuietly(writer);
        }
    }
*/
        /**
     * Loads the address book that was saved with save() If address book
     * file does not exists, throws a FileNotFoundException
     *
     * @param file the CSV formated file
     * @return the list of PDF Recipients, empty list if file does not exists.
     * @throws IOException
     */
    public static List<RecipientEntry> load(File file) throws IOException {
        List<RecipientEntry> pdfRecipients = new ArrayList<RecipientEntry>();
        if (!file.exists()) {
            throw new FileNotFoundException("Le fichier n'existe pas: " + file);
        }
        
        String delemiterStr = getSeparator(file);
        if (delemiterStr == null) {
            return pdfRecipients;
        }
        
        BufferedReader reader = null;
        try {
            InputStream in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in, Parms.DEFAULT_CHARSET));
            String line = null;
            while ((line = reader.readLine()) != null) {
                
                // Catch Exception is something wrong for an item
                try {
                    String[] elements = line.split(delemiterStr);
                    
                    if (elements.length < 2) {
                        continue;
                    }
                    String emailAddress = elements[0];
                    String name = elements[1];
                    
                    String company = null;
                    if (elements.length > 2) {
                        company = elements[2];
                    } else {
                        company = "";
                    }
                    
                    String mobile = null;
                    if (elements.length > 3) {
                        mobile = elements[3];
                    }
                    else {
                        mobile = "";
                    }
                    
                    // Format mobile by removing special chars
                    mobile = MobileUtil.removeSpecialCharacters(mobile);
                    
                    /*
                    String emailNotify = null;
                    if (elements.length != 4) {
                        emailNotify = "";
                    } else {
                        emailNotify = elements[3];
                    }
                    */
                    
                    RecipientEntry pdfRecipient = new RecipientEntry(emailAddress, name, company, mobile, null);
                    pdfRecipients.add(pdfRecipient);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return pdfRecipients;
    }
    

    /**
     * Createa a list PDF Recipients from a CSV formated file. If address book
     * file does not exists, throws a FileNotFoundException
     *
     * @param file the CSV formated file
     * @return the list of PDF Recipients, empty list if file dos not exists.
     * @throws IOException
     */
    public static synchronized List<RecipientEntry> importFromCsv(File file) throws IOException {
        List<RecipientEntry> pdfRecipients = new ArrayList<RecipientEntry>();
        if (!file.exists()) {
            throw new FileNotFoundException("Le fichier n'eixste pas: " + file);
        }

        String delemiterStr = getSeparator(file);
        if (delemiterStr == null) {
            return pdfRecipients;
        }
        
        char delimiter = delemiterStr.charAt(0); 
                
        BufferedReader reader = null;
        try {
            InputStream in = new FileInputStream(file);
            
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withQuote(null).withDelimiter(delimiter);
   
            reader = new BufferedReader(new InputStreamReader(in, Parms.DEFAULT_CHARSET));
            
            CSVParser csvFileParser = new CSVParser(reader, csvFileFormat);
                
            List<CSVRecord> csvRecords = csvFileParser.getRecords();
           
            for (CSVRecord record : csvRecords) {
                     
                String emailAddress = record.get(0);

                debug("emailAddress: " + emailAddress);
                
                String name = record.get(1);
                String company = record.get(2);
                String mobile = record.get(3);
                //String emailNotify = record.get(3);
                
                // Format mobile by removing special chars
                mobile = MobileUtil.removeSpecialCharacters(mobile);

//                if (emailNotify == null) {
//                    emailNotify = "";
//                }

                RecipientEntry pdfRecipient = new RecipientEntry(emailAddress, name, company, mobile, null);
                pdfRecipients.add(pdfRecipient);

            }
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return pdfRecipients;
    }

    
    /**
     * Returns the separator "," or ";" in use in a file. Returns null if none.
     * @param file
     * @return the separator "," or ";" in use in a file. Returns null if none.
     * @throws IOException 
     */
    public static String getSeparator(File file) throws IOException {
        BufferedReader reader = null;
        String separator = null;
        
        try {
            InputStream in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in, Parms.DEFAULT_CHARSET));
            String line = reader.readLine();
            if (line == null) {
               return null;
            }
            
            if (line.contains(SEMI_COLUMN) && StringUtils.countMatches(line, SEMI_COLUMN) >= 1 ) {
                separator = SEMI_COLUMN;
            }
            else if (line.contains(COMMA) && StringUtils.countMatches(line, COMMA) >= 1 ) {
                separator = COMMA;               
            }
            
            return separator;
        } finally {
            IOUtils.closeQuietly(reader);
        }
        
    }

    
    /**
     * debug tool
     */
    private static void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
            // System.out.println(this.getClass().getName() + " " + new Date() +
            // " " + s);
        }
    }
}
