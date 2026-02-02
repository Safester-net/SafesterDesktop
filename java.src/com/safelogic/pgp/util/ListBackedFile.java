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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.AbstractList;


/**
 * @author Nicolas de Pomereu
 *
 */
public class ListBackedFile<E> extends AbstractList
{
   /** The file that stores the reader */
    private File inFile = null;
    
    /** The LineNumberReader file that contains the content */
    private LineNumberReader lineNumbeRreader = null;
    
    /** The size of the list backed by the text file */
    private int size =0;
       
    
    /**
     * Constructor.
     */
    public ListBackedFile(File inFile)
        throws IOException
    { 
        if (inFile == null)
        {
            throw new FileNotFoundException ("inFile can not be null!");
        }
        
        if (! inFile.exists())
        {
            throw new FileNotFoundException("File does not exists: " + inFile);
        }
        
        this.inFile = inFile;
        lineNumbeRreader = new LineNumberReader(new FileReader(inFile));      
        
        String line = null;
        
        while ((line = lineNumbeRreader.readLine()) != null) 
        {
            size++;
        }
        
    }

    /* (non-Javadoc)
     * @see java.util.AbstractList#get(int)
     */
    @Override
    public String get(int index)
    {
        if (index < 0 || index > size)
        {
            throw new IndexOutOfBoundsException("Invalid index. Must be between 0 and " 
                    + size + ". Value is: " + index);
        }
        
        String line = null;
        
        try
        {
            lineNumbeRreader = new LineNumberReader(new FileReader(inFile));      
                        
            while ((line = lineNumbeRreader.readLine()) != null) 
            {
                if (lineNumbeRreader.getLineNumber() == index)
                {
                    return line;
                }
            }            
           
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException(e);
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size()
    {
        return size;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
        throws IOException
    {
        ListBackedFile<String> list = new ListBackedFile( new File("c:\\temp\\test.txt"));
        
        System.out.println("list.size(): " + list.size);
        
        for (int i = 0; i < list.size(); i++)
        {
            String string = list.get(i);
            System.out.println(i + " " + string);
        }
        
    }

}

/**
 * 
 */
