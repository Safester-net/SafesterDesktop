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
package net.safester.clientserver.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Nicolas de Pomereu
 * Allows to convert the file name on the PC to remote host & vice-versa.
 */
public class FileNameConverter
{

    /** The separator char character */
    public static final String PREFIX_SEPARATOR = "_";
    
    /** The file name to convert */
    private String fileName = null;

    private static final String DATE_TAG_BEGIN = "-d";
    private static final String DATE_TAG_END    = "d-";

    
    /**
     * Constructor
     * @param fileName the file name to convert
     */
    public FileNameConverter(String fileName)
    {
        if (fileName == null)
        {
            throw new IllegalArgumentException("fileName can\'t be null.");
        }
        
        this.fileName = fileName;
    }

    /**
     * Get the local file name from the remote file name:
     * <br> - Remove the name part before the first "_" included.
     * 
     * @param remoteFilePath    the remote file name
     * @return  the local file name without
     */
    public String fromServerName()
    {
        if (! fileName.contains(PREFIX_SEPARATOR))
         {
            throw new IllegalArgumentException
                ("The fileName on server does not contain the "
                        + PREFIX_SEPARATOR + " separator: " + fileName);
        }

        // Return the filename part after the first "_":
        return StringUtils.substringAfter(fileName, PREFIX_SEPARATOR);
    }
    
    /**
     * Return the serverName with the added prefix
     * @param prefix        the prefix to use to prefix the file name on server name
     * @return
     */
    public String toServerName(int senderUserNumber)
    {
        Date date = new Date();
        SimpleDateFormat  simpleFormat = new SimpleDateFormat("yyMMdd");

        // prefix = 001-d101013d-123456789
        String prefix = senderUserNumber + DATE_TAG_BEGIN + simpleFormat.format(date)+ DATE_TAG_END + getTimePrefix();

        File f = new File(fileName);

        // serverName = 001-d101013d-123456789_filename.txt
        String serverName = prefix + PREFIX_SEPARATOR + f.getName();
        
        return serverName;
    }

    
    private static synchronized String getTimePrefix() {
	String prefix = Long.toString(new Date().getTime());

	try {
	    Thread.sleep(2);
	} catch (InterruptedException ex) {
	}
	return prefix;
    }


    /**
     * Get the local file name from the remote file name:
     * <br> - Remove the name part before the first "_" included.
     *
     * @param remoteFilePath    the remote file name
     * @return  the local file name without
     */
    public String fromServerNameoLD()
    {
        int nbSep = StringUtils.countMatches(fileName, "" + PREFIX_SEPARATOR);
        if (nbSep < 1)
        {
            throw new IllegalArgumentException
                ("The fileName on server does not contain the "
                        + PREFIX_SEPARATOR + " separator: " + fileName);
        }

        int sepIndex = fileName.indexOf(PREFIX_SEPARATOR);
        String localFileName = fileName.substring(sepIndex + 1);

        return localFileName;
    }

}

