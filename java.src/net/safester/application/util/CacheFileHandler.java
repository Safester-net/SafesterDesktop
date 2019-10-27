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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.IOUtils;

import net.safester.application.parms.Parms;

/**
 * Handles cache files everytime a file is cached somwhere (generaly in java.io.tmpdir)
 * We add it to the static list.
 * Before exiting the application we must call deletedAllCachedFiles
 * @author Alexandre Becquereau
 */

public class CacheFileHandler {

    /** If true, the file is delete (for wipe tests) */
    private static boolean DO_DELETE_FILE = true;
    
    
    /** The list of all cached filenames*/
    private static List<String> cacheFileNames = new Vector<String>();

    /**
     * Empty constructor
     */
    public CacheFileHandler()
    {

    }

    /**
     * Add a file name to the list
     * @param fileName  the filename to add
     */
    public void addCachedFile(String fileName)
    {
        CacheFileHandler.cacheFileNames.add(fileName);
    }

    /**
     * Delete all cached files
     */
    public void deletedAllCachedFiles() {
        
        for (String filename : CacheFileHandler.cacheFileNames) {
            if (filename != null) {
                File f = new File(filename);
                
                if (f.exists() && ! f.isDirectory()) {
                    wipeFile(f);
                }
            }
        }

        // Delete all files in safester_temp
        File safesterTemp = new File(Parms.getSafesterTempDir());
        File [] files = safesterTemp.listFiles();
        wipeFilesRecurse(files);
        
    }

    private void wipeFilesRecurse(File [] files ) {
        
        if (files != null)
        {
            for (File file : files) {
                
                if (! file.isDirectory()) {
                    wipeFile(file);
                }
                else {
                    File [] filesInDir = file.listFiles();  
                    wipeFilesRecurse(filesInDir);
                }
            }
        }
    }
    
    /**
     * Wipe the file & then delete it
     * 
     * @param file      the file to wipee
     * @throws IOException
     */
    
    public static void wipeFile(File file) 
    {
        try
        {
            String s = null;
            s = "00000000000000000000000000000000000000000000000000"; 
            wipeFile(file, s);
            
            s = "11111111111111111111111111111111111111111111111111"; 
            wipeFile(file, s);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }    
        
        if (DO_DELETE_FILE)
        {
            file.delete();
        }
        
    }
    
    /**
     * Wipe the file using a pattern
     * 
     * @param file      the file to wipe
     * @param pattern    the pattern to use
     * @throws IOException
     */
    private static void wipeFile(File file, String pattern) throws IOException
    {               
        if (file == null)
        {
            throw new IllegalArgumentException("file can not be null!");
        }
        
        if (! file.exists())
        {
            throw new FileNotFoundException("file does not exists: " + file); 
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
    
//    /**
//     * Delete a filename (and remove it from the list
//     * @param filename      The filename to delete
//     */
//    public void deleteCachedFile(String filename)
//    {
//        //If file not in list do nothing
//        if(!CacheFileHandler.cacheFileNames.contains(filename))
//        {
//            return;
//        }
//
//        File f = new File(filename);
//        //If file already deleted just remove it from list
//        
//        if(!f.exists())
//        {
//             CacheFileHandler.cacheFileNames.remove(filename);
//        }
//        
//        if(f.delete())
//        {
//            //Remove file from list only if delete succeed!
//            CacheFileHandler.cacheFileNames.remove(filename);
//        }
//    }


}
