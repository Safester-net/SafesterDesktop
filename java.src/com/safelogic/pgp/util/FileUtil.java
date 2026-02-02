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

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.safelogic.pgp.api.util.msg.MessagesManager;
import com.safelogic.utilx.Debug;

/**
 * Misc file utilities
 * @author Nicolas de Pomereu
 *
 */
public class FileUtil
{

    /** The DEBUG flag */
    private boolean DEBUG = Debug.isSet(this) ;
    
    public static String CR_LF = System.getProperty("line.separator") ;
    
    /** List of files */
    private List<File> m_fileList = new Vector<File> ();
    
    /**
     * Constructor
     */
    public FileUtil()
    {
        super();
    }
    
    /**
     * Return true if the fils exists, is readable and is not locked by another process
     * <br>
     * New version that works because it uses RandomAccessFile and will throw an
     * exception if another file has locked the file
     */
    public boolean isUnlockedForRead(File file)
    {
        if (file.exists() && file.canRead())
        {            
            if (file.isDirectory())
            {
                return true;
            }
            
            try
            {
                RandomAccessFile raf =
                    new RandomAccessFile(file, "r");
                
                raf.close();
                
                debug(new Date() + " " + file + " OK!"); 
                
                return true;
            }
            catch (Exception e)
            {
                debug(new Date() + " " + file + " LOCKED! " + e);
            }
        }
        else
        {            
            debug(new Date() + " " + file + " LOCKED! File exists(): " 
                        + file.exists() + " File canWrite: " + file.canWrite());            
        }
        
        return false;
        
    }
    
    
    /**
     * Return true if the file exists and is readable and writable not locked by another process
     * <br>
     * New version that works because it uses RandomAccessFile and will throw an
     * exception if another file has locked the file
     */
    public boolean isUnlockedForWrite(File file)
    {
        if (file.exists() && file.canWrite())
        {            
            if (file.isDirectory())
            {
                return true;
            }
            
            try
            {
                RandomAccessFile raf =
                    new RandomAccessFile(file, "rw");
                
                raf.close();
                
                debug(new Date() + " " + file + " OK!"); 
                
                return true;
            }
            catch (Exception e)
            {
                debug(new Date() + " " + file + " LOCKED! " + e);
            }
        }
        else
        {            
            debug(new Date() + " " + file + " LOCKED! File exists(): " 
                        + file.exists() + " File canWrite: " + file.canWrite());            
        }
        
        return false;
        
    }
    
    
    /**
     * Recuse research of a files.
     * Result is stored in m_fileList
     *
     * @param fileList      the initial list
     * @param recurse       if true, search inside directories
     */

    private void searchFiles(File[] fileList, boolean recurse)
    {
        if (fileList == null)
        {
            throw new IllegalArgumentException("File list can't be null!");
        }
        
        for(int i = 0; i < fileList.length; i++)
        {
            m_fileList.add(fileList[i]);

            if(fileList[i].isDirectory())
            {
                if(recurse)
                {
                    searchFiles(fileList[i].listFiles(), recurse);
                }
            }
        }
    }

    /**
     * Extract all files from a list of files, with recurse options
     * 
     * @param fileList      the initial list
     * @param recurse       if true, search inside directories
     * 
     * @return the extracted all files
     */
    public List<File> listAllFiles(List<File> fileList, boolean recurse)
    {           
        
        if (fileList == null)
        {
            throw new IllegalArgumentException("File list can't be null!");
        }
        
        if (fileList.isEmpty())
        {
            return new Vector();
        }
                
        File [] files = new File[fileList.size()];
        
        for (int i = 0; i < fileList.size(); i++)
        {
            files[i] = fileList.get(i);
        }
        
        searchFiles(files, recurse);
        return m_fileList;
    }
    
    
    /**
     * 
     * Extract the files that are not directories from a file list
     * 
     * @param fileList      the initial list (firt level only)
     * @return  the file list
     */
    public List<File> extractFilesOnly(List<File> fileList)
    {
        if (fileList == null)
        {
            throw new IllegalArgumentException("File list can't be null!");
        }
        
        if (fileList.isEmpty())
        {
            return new Vector();
        }
        
        List <File> files = new Vector<File>();
        
        for (int i = 0; i < fileList.size(); i++)
        {
           if (! fileList.get(i).isDirectory())
           {
               files.add(fileList.get(i));
           }
        }
        
        return files;
        
    }
    
    /**
     * 
     * Extract the  directories only from a file list
     * 
     * @param fileList      the initial list (firt level only)
     * @return  the file list
     */
    public List<File> extractDirectoriesOnly(List<File> fileList)
    {
        List <File> directories = new Vector<File>();
        
        for (int i = 0; i < fileList.size(); i++)
        {
           if (fileList.get(i).isDirectory())
           {
               directories.add(fileList.get(i));
           }
        }
        
        return directories;
        
    }
    
    
    /**
     * Put the content of a file as HTML into a String
     * <br>
     * No carriage returns will be included in output String
     * 
     * @param fileIn        The HTML text file
     * @return              The content in text
     * @throws IOException
     */
    public String getHtmlContent(File fileIn)
        throws IOException
    {
        if (fileIn == null)
        {
            throw new IllegalArgumentException("File name can't be null!");
        }
                
        String text = "";
                
        BufferedReader br
        = new BufferedReader(new FileReader(fileIn));

        String line = null;
        
        while ((line = br.readLine()) != null) 
        {
            text += line;
        }
        
        return text;
    }
    
        
    /**
     * Put the content of a file as text into a String
     * 
     * @param fileIn        The text file
     * @return              The content in text
     * @throws IOException
     */
    public String getTextContent(File fileIn)
        throws IOException
    {
        if (fileIn == null)
        {
            throw new IllegalArgumentException("File name can't be null!");
        }
                
        String text = "";
                
        BufferedReader br
        = new BufferedReader(new FileReader(fileIn));

        String line = null;
        
        while ((line = br.readLine()) != null) 
        {
            //System.out.println(line);
            text += line + CR_LF;
        }
        
        return text;
    }
    
    /**
     * Put the content of a string text into a file
     * 
     * @param text          The text to put
     * @param fileOut        The output file
     * @throws IOException
     */
    public void putTextContent(String text, File fileOut)
        throws IOException
    {        
        
        if (text == null)
        {
            throw new IllegalArgumentException("String can't be null!");
        }
        
        if (fileOut == null)
        {
            throw new IllegalArgumentException("File name can't be null!");
        }
        
        PrintWriter printWriter = new PrintWriter(fileOut);
        printWriter.print(text);
        printWriter.close();        
    }    
    
    private void debug(String sMsg)
    {
        if(DEBUG)
            System.out.println("DBG> " + sMsg) ;
    }    
    
    /**
     * 
     * Create a Directory. if any failure, will be cleanly reported in a Window Message.
     * Will do nothing and return true if the directory already exists.
     * 
     * @param directory             the directory to create
     * @throws HeadlessException
     */
    public static boolean createDirectory(File directory) 
        throws HeadlessException
    {
        if (directory.isDirectory())
        {                    
            return true;
        }
        
        boolean created = false;
        
        try
        {
            created = directory.mkdirs();
            
            if (!created)
            {
                throw new IOException("Unknown reason. (Probably Authorization Failure).");
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            MessagesManager messages = new MessagesManager();                
            JOptionPaneCustom.showException(null, e, messages.getMessage("unable_to_create_dir") + " " 
                    + directory);
            return false;
        }
            
        return true;
    }

    public static void main(String[] args)
    throws Exception
    {
                
        FileUtil fileUtil = new FileUtil();
                
        File file = new File("c:\\temp\\qsdfqsdf.txt");
        
        boolean canRead = fileUtil.isUnlockedForRead(file);
        System.out.println("canRead : " + canRead);
        
        if (true) return;
        
        File file1 = new File("c:\\temp\\cGeepPro_License.txt");
        fileUtil.getTextContent(file1);
        

     
        //System.out.println(file.canRead());
        //System.out.println(file.canWrite());
        
        canRead = fileUtil.isUnlockedForRead(file);
        System.out.println("canRead : " + canRead);
        
        boolean canWrite = fileUtil.isUnlockedForWrite(file);
        System.out.println("canWrite: " + canWrite);
        
    }
    


}

