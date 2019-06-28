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
package net.safester.application;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * Util methods to access translation dictionnaries
 *
 * @author Nicolas de Pomereu
 */
public class DictUtil {

    /**
     * Copy the two englis anf french .dict in resources to
     * user.home/.kawansoft/safester/dict
     *
     * @throws IOException
     */
    public static void copyDictFilesToUserHome() throws IOException {

        // KEEP THIS CODE AS MODEL
        //java.net.URL myURL
        //  = ResourceBundleTest.class.getResource("/com/safelogic/pgp/test/MyResource_fr.properties");
        String resource = "/" + MessagesManager.MESSAGE_FILES_PACKAGE;
        resource = resource.replace(".", "/");

        String urlResourceDictEnglish = resource + "/" + Parms.DICTIONARY_ENGLISH;
        String urlResourceDictFrench = resource + "/" + Parms.DICTIONARY_FRENCH;

        File dictEnglishDest = new File(getDictFilesPath() + File.separator + Parms.DICTIONARY_ENGLISH);
        File dictFrenchDest = new File(getDictFilesPath() + File.separator + Parms.DICTIONARY_FRENCH);

        if (!dictEnglishDest.exists()) {
            copyResourceToFile(urlResourceDictEnglish, dictEnglishDest);
        }

        if (!dictFrenchDest.exists()) {
            copyResourceToFile(urlResourceDictFrench, dictFrenchDest);
        }

    }

    /**
     * Returns the path to dir where english and french dictionnaires are to be
     * stored
     *
     * @return
     */
    public static String getDictFilesPath() {

        String dictFilesPath = SystemUtils.getUserHome() + File.separator + ".kawansoft" + File.separator + Parms.PRODUCT_NAME + File.separator + "dict";
        File dictFiles = new File(dictFilesPath);
        if (!dictFiles.exists()) {
            dictFiles.mkdirs();
        }

        return dictFilesPath;
    }

    private static void copyResourceToFile(String urlResource, File fileToCreate) throws IOException {

        InputStream in = null;
        OutputStream out = null;
        java.net.URL myURL = null;

        try {
            myURL = Help.class.getResource(urlResource);
            in = myURL.openStream();
            out = new BufferedOutputStream(new FileOutputStream(fileToCreate));
            IOUtils.copy(in, out);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

}
