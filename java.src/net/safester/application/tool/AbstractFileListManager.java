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
package net.safester.application.tool;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.safelogic.utilx.Debug;

import net.safester.application.messages.MessagesManager;

/**
 * Manages all drag & drop and ctrl-c/ctrl-c operations on a list of files
 * represented by a sorted JList of files
 * <br>
 * Allowes also to Open or Print a select file
 * 
 * @author Nicolas de Pomereu
 */

public abstract class AbstractFileListManager
{
    public static final String CR_LF = System.getProperty("line.separator") ;
    
    /** The debug flag */
    public boolean DEBUG = Debug.isSet(this);
    
    /** The JFrame of the caller program */
    public JFrame jframe = null;
    
    /** The JList that holds the file list */
    public JList jListFiles = null;
    
    /** The Message Manager that manages I18N */
    public MessagesManager messages = new  MessagesManager();
    
    /** The model that contains the attach */
    public SortedDefaultListModel model_attachs = null;
    
    /** The Map of files with complete Name : (file.getName(), file) */
    public  Map<String, File> attachMap = new HashMap<String, File>();
    
    /** Pop Up menu */
    public JPopupMenu popupMenu;
    
    // Clipboard actions
    public String open      = messages.getMessage("system_open");
    public String print     = messages.getMessage("system_print");
    public String paste     = messages.getMessage("system_paste");
    public String delete    = messages.getMessage("system_delete");
    public String select_all= messages.getMessage("system_select_all");
    
    // Menu items
    public JMenuItem itemOpen;
    public JMenuItem itemPrint;
    public JMenuItem itemPaste;
    public JMenuItem itemDelete;
    public JMenuItem itemSelectAll;
    
    /**
     * Constructor
     * <br>
     * Actions done:
     * <br> - Create a contextual Pop Menu for file handling
     * <br> - Add a Mouse listener for the Pop Menu.
     * <br> - Add a Key Listener for the JList component
     *       
     * @param jframe          The caller program JFrame
     * @param jListFiles      The JList that holds the file list
     */
    public AbstractFileListManager(JFrame jframe, JList jListFiles)
    {
       
        this.jframe     = jframe;
        this.jListFiles = jListFiles;
                
        model_attachs = (SortedDefaultListModel) jListFiles.getModel();
                        
    }
    
    //
    // Public abstract methods
    //
    
    /**
     * Add files to the AbstractFileListManager; may be called by outside program
     * <br>
     * Not implemented, because add conditions depends on the file type (encrypted not encrypted)
     * @param files     Files to add
     */
    public abstract void add(File[] files);
    
   
    //
    // Public methods
    //
    
    /**
     * Add a file to the AbstractFileListManager
     * <br>
     * may be called by outside program
     * @param file     File to add
     */
    public void add(File file)
    {
        if (file == null)
        {
            return;
        } 
        
        File [] files = new File[1];
        files[0] = file;
        add(files);
    }
    
    /**
     * Add files to the AbstractFileListManager
     * <br>
     * may be called by outside program
     * @param files     Files to add
     */
    public void add(List<File> files)
    {
        File [] fileArray = new File[files.size()];
        
        for (int i = 0; i < files.size(); i++)
        {  
            fileArray[i] = files.get(i);
        }
        
        add(fileArray);
    }
    
    /**
     * @return true if there are no more file in the JList
     */
    public boolean isEmpty()
    {
        return attachMap.isEmpty();
    }
    
    /**
     * Remove all files from the componant
     */
    public void removeAll()
    {
        model_attachs.removeAllElements();
        attachMap = new HashMap<String, File>();
    }
   
    /**
     * Delete files from the list
     */
    public void remove()
    {
        Object[] keys = jListFiles.getSelectedValues();
        
        for (int i = 0; i < keys.length; i++)
        {   
            Object key = keys[i];
            model_attachs.removeElement(key);
         //   String fileName = (String) key;
            attachMap.remove(key);
        }       
    }
    
    
    /**
     * Return all the files - will full path - contained in the JList in form of a List
     * <br>
     * If the JList is empty ==> return an empty List.
     * 
     * @return all the files - will full path - contained in the JList.
     */
    public List<File> getFiles()
    {
        List<File> files = new Vector<File>();
        
        // Ok, now add the attach files to crypt
        Collection <File> c  =  attachMap.values();
        
        if (c != null && ! c.isEmpty())
        {            
            for (Iterator<?> iter = c.iterator(); iter.hasNext();)
            {
                File file =  (File)iter.next();
                files.add(file);
            }                        
        }
        
        return files;

    }
            
    /**
     * If the user has selected files using Explorer : get the files names
     * 
     * @return  the file names selected by user to copy into attach area
     */
    public static File [] getFilesFromClipboard()
    {
        //String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        
        //odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
                    
        boolean hasTransferableFiles = (contents != null) &&
               contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        
        //debug("hasTransferableFiles: " + hasTransferableFiles);
        
        List<File> listFiles = null;
        
        if ( hasTransferableFiles ) 
        {
            try 
            {
                listFiles = (List)contents.getTransferData(DataFlavor.javaFileListFlavor);
            }
            catch (UnsupportedFlavorException ex){
                //highly unlikely since we are using a standard DataFlavor
                System.out.println(ex);
                ex.printStackTrace();
            }
            catch (IOException ex) {
                System.out.println(ex);
                ex.printStackTrace();
            }
        }
        
        if (listFiles == null)
        {
            return null;
        }
        
        File [] files = new File[listFiles.size()];
        for (int i = 0; i < listFiles.size(); i++)
        {
            files[i] = listFiles.get(i);
        }
        
        return files;
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
}
