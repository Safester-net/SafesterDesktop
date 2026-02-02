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
package com.safelogic.pgp.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import com.safelogic.pgp.apispecs.StringTriplet;
import com.safelogic.pgp.util.FileUtil;
import com.safelogic.utilx.Debug;
import com.safelogic.utilx.syntax.EmailChecker;

/**
 * 
 * @author Nicolas de Pomereu
 *
 */
public class HkpServerResultExtractor
{    
    /** The debug flag */
    protected boolean DEBUG = Debug.isSet(this);
    
    /** The List of public keys keys PGP Ids extracted from the text */
    private List<StringTriplet> pgpIdsAndUserIds = new Vector<StringTriplet>();

    
    private String serverUrl = null;
    private String text = null;
    
    /**
     * Constructor
     * @param text      the text to build the (Pgp Ids, user Ids) list from
     * @param serverUrl the Hkp server sfrom which the datas have been received
     */
    public HkpServerResultExtractor(String serverUrl, String text)
    throws IOException
    {
        this.serverUrl = serverUrl;
        this.text = text;
        buildList();
    }

    /**
     * @return the Pgp Ids and User Ids in List, each separated by a comma per line
     */
    public List<StringTriplet> getPgpIdsAndUserIdsCommaSeparated()
    {
        return pgpIdsAndUserIds;
    }

    /**
     * Build the triplet list of Pgp Ids & User Ids
     */
    private void buildList()
        throws IOException
    {

        // If there is no "pub:", nothing to do
        if (!StringUtils.contains(text, "pub:"))
        {
            return;
        }
        
        String [] strings = text.split("pub:");

        for (String string : strings)
        {                        
            //if (string.isEmpty())
            if (string.length() == 0)
            {
                continue;
            }

            debug("");
            debug("!" + string + "!");

            String pgpId = StringUtils.substringBefore(string, ":");
            pgpId = "0x" + pgpId;
            
            List<String> userIdsForKey = getUserIds(string);

            for (String userIdForKey : userIdsForKey)
            {
                pgpIdsAndUserIds.add( new StringTriplet(pgpId, userIdForKey, null));
            }
        }

    }

    /**
     * Extract the userIds from the text block of "uids:"
     * 
     * @param text          the text to extract the uids: from 
     * @return  the list of user Ids
     * @throws IOException
     */
    private List<String> getUserIds(String text)
    throws IOException
    {
        List<String> userIds = new Vector();

        BufferedReader br = new BufferedReader(new StringReader(text));        
        String line = null;

        while ((line = br.readLine()) != null) 
        {  
            //if (line.isEmpty())
            if (line.length() == 0)
            {
                continue;
            }

            if (! line.toLowerCase().contains("uid:"))
            {
                continue;
            }

            String userId = StringUtils.substringBetween(line, "uid:", ">");
            userId += ">";

            String email = StringUtils.substringBetween(userId, "<", ">");
            if (new EmailChecker(email).isSyntaxValid())
            {
                userIds.add(userId);
            }
        }

        return userIds;
    }

    /**
     * debug tool
     */
    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        String serverlUrl = "";
        String text = new FileUtil().getTextContent(new File("c:\\temp\\test.txt"));
        HkpServerResultExtractor hkpServerResultExtractor = new HkpServerResultExtractor(serverlUrl, text);
        
        System.out.println();
        System.out.println("--------------------------------------------------------");
        System.out.println();
        
        List<StringTriplet> pgpIdsAndUserIds  = hkpServerResultExtractor.getPgpIdsAndUserIdsCommaSeparated();
        
        for (int i = 0; i < pgpIdsAndUserIds.size(); i++)
        {
            System.out.println(pgpIdsAndUserIds.get(i));
            System.out.println();
        } 
   
    }

}
