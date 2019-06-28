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
package net.safester.application.addrbooknew;

import net.safester.application.addrbooknew.tools.CryptAppUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.safelogic.utilx.Debug;
import net.safester.application.parms.Parms;

/**
 * @author RunningLiberty
 *
 */
public class CsvAddressBook {

    protected boolean DEBUG = Debug.isSet(this);

    public static final int TYPE_LASTNAME = 1;
    public static final int TYPE_FIRSTNAME = 2;
    public static final int TYPE_EMAIL = 3;
    public static final int TYPE_MOBILE = 4;
    public static final int TYPE_COMPANY = 5;
    public static final int TYPE_EMAIL_SECONDARY = 6;

    /**
     * The maximum file size allowed. 800 ko
     */
    public static final double MAX_FILE_LENGTH = 1024 * 1024 * 4;

    /**
     * The file to parse
     */
    private File file = null;

    /**
     * The list of contents. Each content is a list of parsed tokens.
     */
    private List<List<String>> listsOfContents = new Vector<List<String>>();

    /**
     * if true, parsed has been done
     */
    private boolean parseDone = false;

    /**
     * The end column (chosen by user)
     */
    private int endColumn = 0;

    /**
     * Number of columns for padding
     */
    private int padNum = 20;

    /**
     * Number of "first" lines to display
     */
    private int firstLinesMax = 25;
    private String separator = null;

    /**
     * @param file the file to parse
     * @param separator the value of separator
     *
     * @throws FileNotFoundException if the file is not found
     * @throw
     */
    public CsvAddressBook(File file, String separator)
            throws FileNotFoundException, IllegalArgumentException {
        if (!file.exists()) {
            throw new FileNotFoundException("Input File does not exists: " + file);
        }

        if (file.length() > MAX_FILE_LENGTH) {
            throw new IllegalArgumentException(
                    "File size is too big. Must be less than " + MAX_FILE_LENGTH + " bytes: "
                    + file.length());
        }

        this.file = file;
        this.separator = separator;
    }

    /**
     * get the first two lines, in order to display for the user
     *
     * @return the first two lines into a list
     *
     * @throws IOException if any read error occurs
     */
    public List<String> getFirstLinesFormated()
            throws IOException {
        BufferedReader reader = null;

        try {
            InputStream in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in, Parms.DEFAULT_CHARSET));

            String line = null;

            List<String> lines = new Vector<String>();

            int i = 0;
            while ((line = reader.readLine()) != null) {
                try {
                    line = formatLine(line, i);
                    if (line != null && !line.isEmpty()) {
                        lines.add(line);
                    }

                    if (i++ >= firstLinesMax) {
                        break;
                    }
                } catch (Exception e) {
                    //System.out.println("line skipped for formating: " + i);
                    //e.printStackTrace();
                }
            }

            return lines;
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * get the first two lines, in order to display for the user
     *
     * @return the first two lines into a list
     *
     * @throws IOException if any read error occurs
     */
    public List<String> getFirstLines()
            throws IOException {
        BufferedReader reader = null;

        try {
            InputStream in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in, Parms.DEFAULT_CHARSET));

            String line = null;

            List<String> lines = new Vector<String>();

            int i = 0;
            while ((line = reader.readLine()) != null) {
                try {
                    if (line != null && !line.isEmpty()) {
                        lines.add(line);
                    }

                    if (i++ >= firstLinesMax) {
                        break;
                    }
                } catch (Exception e) {
                    //System.out.println("line skipped for formating: " + i);
                    //e.printStackTrace();
                }
            }

            return lines;
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Format a string with pad, for a clean presentation to the user
     *
     * @param line the line to format
     * @param lineNumber the line number
     * @return the formated line
     */
    public String formatLine(String line, int lineNumber) {
        String formatedLine = "";

        CSVTokenizer csvt = new CSVTokenizer(line, separator);

        int tokenCnt = 0;
        while (csvt.hasMoreTokens()) {
            tokenCnt++;
            String token = csvt.nextToken();

            if (token != null) {
                token = token.trim();
            }

            if (lineNumber == 0) {
                token = "[" + tokenCnt + "] " + token;
            }

            token = pad(token);
            formatedLine += " | " + token;
        }

        return formatedLine;
    }

    public String pad(String s) {
        if (s == null) {
            return s;
        }

        if (s.length() < padNum) {
            s = StringUtils.rightPad(s, padNum);
        } else {
            s = s.substring(0, padNum);
        }

        return s;
    }

    /**
     * Count the number of columns (tokens separated by commas) in this CSV file
     *
     * @return the number of columns in the CSV file (0 if none)
     *
     * @throws IOException if any read error occurs
     */
    public int countColumns()
            throws IOException {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;

            while ((line = reader.readLine()) != null) {
                CSVTokenizer csvt = new CSVTokenizer(line, separator);
                return csvt.countTokens();
            }

            return 0;
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Parse the file. mandatory before extracting columns.
     */
    public void parseFile(int endColumn)
            throws IOException {
        this.parseDone = true;
        this.endColumn = endColumn;

        int theCountColumns = countColumns();

        if (theCountColumns < endColumn) {
            throw new IllegalArgumentException("Invalid column number! Max column number is "
                    + theCountColumns + " < " + endColumn);
        }

        BufferedReader reader = null;

        try {
            InputStream in = new FileInputStream(file);
            String charsetName = Parms.DEFAULT_CHARSET;
            reader = new BufferedReader(new InputStreamReader(in, charsetName));

            String line = null;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                CSVTokenizer csvt = new CSVTokenizer(line, separator);

                try {
                    List<String> content = new Vector<String>();

                    boolean isNotEmpty = false;

                    for (int i = 0; i < endColumn; i++) {
                        String token = "";

                        if (csvt.hasMoreTokens()) {
                            token = csvt.nextToken();
                            isNotEmpty = true;
                        }
                        content.add(token);
                    }

                    if (isNotEmpty) {
                        listsOfContents.add(content);
                    }

                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                    System.out.println("Ignoring line: " + line);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    System.out.println("Ignoring line: " + line);
                }
            }

            //return content;
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Get the column number corresponding to a type
     *
     * @param columnType
     *
     * @return the column number for the type
     */
    public int getProbableColumnForType(int columnType)
            throws IOException {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;

            int lineNum = 0;

            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (line.isEmpty()) {
                    continue;
                }

                CSVTokenizer csvt = new CSVTokenizer(line, separator);
                int colCpt = 0;

                try {
                    while (csvt.hasMoreTokens()) {
                        String token = csvt.nextToken();

                        if (columnType == TYPE_EMAIL) {
                            if (CryptAppUtil.isValidEmailAddress(token)) {
                                debug("Line " + lineNum + ": email detected in col[" + colCpt + "]:" + token);
                                return colCpt;
                            }
                        }

                        if (columnType == TYPE_LASTNAME) {
                            if (lineNum > 1) {
                                return -1; // Not found on file header line
                            }
                            token = token.toLowerCase();
                            if (token.equals("name")) {
                                return colCpt;
                            }
                            if (token.contains("last") && token.contains("name")) {
                                return colCpt;
                            }
                            if (token.contains("nom") || token.contains("de famille")) {
                                return colCpt; // French
                            }
                        }

                        if (columnType == TYPE_FIRSTNAME) {
                            if (lineNum > 1) {
                                return -1; // Not found on file header line
                            }
                            debug("in TYPE_FIRSTNAME: " + token);

                            token = token.toLowerCase();
                            if (token.contains("first") && token.contains("name")) {
                                return colCpt;
                            }
                            if (token.contains("prénom") || token.contains("prenom") || (token.contains("pr") && token.contains("nom"))) {
                                return colCpt;  // French
                            }
                        }

                        if (columnType == TYPE_COMPANY) {
                            if (lineNum > 1) {
                                return -1; // Not found on file header line
                            }
                            token = token.toLowerCase();
                            if (token.contains("société") || token.contains("soci") || token.contains("company") || token.contains("organization") || token.contains("organisation")) {
                                return colCpt;
                            }
                        }

                        if (columnType == TYPE_MOBILE) {
                            if (lineNum > 1) {
                                return -1; // Not found on file header line
                            }
                            token = token.toLowerCase();
                            if (token.contains("mobile") || token.contains("portable")) {
                                return colCpt;
                            }
                        }

                        if (columnType == TYPE_EMAIL_SECONDARY) {
                            if (CryptAppUtil.isValidEmailAddress(token) && colCpt > 1) {
                                System.out.println("Line " + lineNum + ": email detected in col[" + colCpt + "]:" + token);
                                return colCpt;
                            }
                        }

                        colCpt++;
                    }

                } catch (NoSuchElementException e) {
                    //e.printStackTrace();
                    System.out.println("Ignoring line " + lineNum + ": " + line);
                } catch (IllegalArgumentException e) {
                    //e.printStackTrace();
                    System.out.println("Ignoring line " + lineNum + ": " + line);
                }
            }

            return -1;
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * extract the content of a column and put into a list of strings.
     *
     * @param col the column index to get the column content starts at 0
     * @return the column content as a list
     *
     * @throws IOException
     */
    public List<String> getColumnContent(int col)
            throws IOException {
        if (!parseDone) {
            throw new IllegalStateException("parseFile() must be called before accessing to a column content.");
        }

        if (col < 0 || col > endColumn) {
            throw new IllegalArgumentException("Invalid column index: " + col + ". Column index must be in [0, " + (endColumn) + "] range.");
        }

        List<String> finalList = new Vector<String>();

        for (List<String> theList : listsOfContents) {
            finalList.add(theList.get(col).trim());
        }

        return finalList;

    }

    /**
     * debug tool
     */
    private void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
            // System.out.println(this.getClass().getName() + " " + new Date() +
            // " " + s);
        }
    }

}
