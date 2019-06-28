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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.BevelBorder;

import net.iharder.dnd.FileDrop;
import net.safester.application.parms.Parms;




/**
 * Manages all drag & drop and ctrl-c/ctrl-c operations on a list of files
 * represented by a sorted JList.
 * <br>
 * Class to be used *only* with non-pGeeped files; i.e. files non terminated
 * by ".pgeep" or ".pgeep_signature", or ".cgeep" or ".cgeep_signature"
 * <br>
 * To be used for JList mapped to files to be encrypted.
 * <br>
 * Allowes also to Open or Print a selected file
 * 
 * @author Nicolas de Pomereu
 */

public class FileListManager extends AbstractFileListManager
{   
    
    /**
     * Constructor
     * <br>
     * Actions done:
     * <br> - Create a contextual Pop Menu for file handling
     * <br> - Add a Mouse listener for the Pop Menu.
     * <br> - Add a Key Listener for the JList component
     *       
     * @param jframe     The content pane of the caller program
     * @param jListFiles      The JList that holds the file list
     */
    public FileListManager(JFrame jframe, JList jListFiles)
    {       
        super(jframe, jListFiles);
                
        addPopupMenu();
                
        jListFiles.setName("jListFiles");
        jListFiles.addKeyListener(new KeyAdapter() { 
//            public void keyReleased(KeyEvent e) 
//            { 
//                this_keyReleased(e); 
//            } 
            public void keyPressed(KeyEvent e) 
            { 
                this_keyPressed(e); 
            } 
        }); 
        
        jListFiles.addMouseListener(new MouseAdapter() { 
            public void mouseClicked(MouseEvent e) 
            { 
                jListFiles_mouseClicked(e);
            } 
        });
        
        jListFiles.addMouseListener(new MouseAdapter() { 
            
            public void mousePressed(MouseEvent e) 
            { 
                jListFiles_mousePressedOrReleased(e);
            } 
            
            public void mouseReleased(MouseEvent e) 
            { 
                jListFiles_mousePressedOrReleased(e);
            } 
        }); 
        
        // 
        // This external Open Source (LGPL) componant handles all drag and drop
        // See http://iharder.sourceforge.net/filedrop/
        // Note: No learning needed!
        // 
        
        //JOptionPaneNewCustom.showMessageDialog(null, this.getClass().getName() + " 1");
        
        new FileDrop(jframe, new FileDrop.Listener()
        {
            @Override
            public void filesDropped(java.io.File[] files)
            {
               // handle file drop
               //if (ConnectionParms.getSubscription() == StoreParms.PRODUCT_BASIC) {
               //
               //     String htmlMessage = HtmlTextUtil.getHtmlHelpContent("no_attach_basic");
               //     new UnavailableFeatureDialog(null, htmlMessage, true).setVisible(true);
               //     return;
               //}

                add(files);               
                             
            } // end filesDropped
        }); // end FileDrop.Listener
        
        //JOptionPaneNewCustom.showMessageDialog(null, this.getClass().getName() + " 2");
        
    }
   
    //
    // Public methods
    //
        
    
    /**
     * Add files to the FileListManager
     * <br>
     * may be called by outside program
     * @param files     Files to add
     */
    public void add(File[] files)
    {
        boolean forbidenFileDetected = false;
        
        if (files == null)
        {
            return;
        }
        
        for (int i = 0; i < files.length; i++)
        {                    
            File file = files[i];
            
            if (file == null)
            {
                continue;
            }

            // Forbidden to add twice same filename
            if (attachMap.containsKey(file.getName()))
            {
               continue;
            }

            model_attachs.addElement(file.getName()); // File with only last name
            attachMap.put(file.getName(), file)  ;    // File with complete Path
            
            model_attachs.sort();
        }
        
        
    }
    
        
    //
    // Private methods
    //
    
    /**
     * Add the contextual Pop Menu for file management:
     * - Open File.
     * - Print File.
     * - Paste Files.
     * - Delete Files.
     * - Select All Files.
     */
    public void addPopupMenu()
    {
        // The Pop Menu for file encryption
        // Open, Print, Delete, Copy, Select All
        
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                
        itemOpen = new JMenuItem(open);
        itemOpen.setText(open);        
        itemOpen.addActionListener( (new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                list_clipboard_actionPerformed(e);
            }}));               
        popupMenu.add(itemOpen);
        
        itemPrint = new JMenuItem(print);
        itemPrint.setText(print);
        itemPrint.setIcon(Parms.createImageIcon(Parms.PRINT_ICON));
                
        itemPrint.addActionListener( (new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                list_clipboard_actionPerformed(e);
            }}));               
        popupMenu.add(itemPrint);
        
        popupMenu.addSeparator();
                
        itemPaste = new JMenuItem(paste);
        itemPaste.setText(paste);
        //itemPaste.setIcon(new javax.swing.ImageIcon(getClass().getResource(Parms.PASTE_ICON))); //IN SHEF JAR
        itemPaste.setIcon(Parms.createImageIcon(Parms.PASTE_ICON));
        
        itemPaste.addActionListener( (new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                list_clipboard_actionPerformed(e);
            }}));            
        popupMenu.add(itemPaste);
        
        itemDelete = new JMenuItem(delete);
        itemDelete.setText(delete);
        //itemDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource(Parms.DELETE_ICON))); //IN SHEF JAR
        itemDelete.setIcon(Parms.createImageIcon(Parms.DELETE_ICON));
        
        itemDelete.addActionListener( (new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                list_clipboard_actionPerformed(e);
            }}));    
        //itemDelete.setAccelerator(KeyStroke.getKeyStroke(
        //        KeyEvent.VK_DELETE, 0));           
        popupMenu.add(itemDelete);
        
        popupMenu.addSeparator();
        
        itemSelectAll = new JMenuItem(select_all);
        itemSelectAll.setText(select_all); 
        itemSelectAll.addActionListener( (new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                list_clipboard_actionPerformed(e);
            }}));    
        //itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(
        //        KeyEvent.VK_A, ActionEvent.CTRL_MASK));           
        popupMenu.add(itemSelectAll);       
         
    }
   
        
    /**
     * Open the file
     */
    private void openDocument()
    {
        String fileName  = (String) jListFiles.getSelectedValue();
        
        if (fileName == null)
        {
            return;
        }
        

        
        File file = attachMap.get(fileName);
        
        DesktopWrapper.open(file, jframe);
    }
    
    /**
     * Print the file
     */
    private void printDocument()
    {
        String fileName  = (String) jListFiles.getSelectedValue();
        
        if (fileName == null)
        {
            return;
        }

        
        File file = attachMap.get(fileName);
        
        DesktopWrapper.print(file, jframe);

    }    
    
    
    
    
    //
    // Methods linked to the componant
    //
    
    private void list_clipboard_actionPerformed(ActionEvent e)
    {
        if ( e.getActionCommand().equals(open))
        {
            openDocument();
        }      
        else if ( e.getActionCommand().equals(print))
        {
            printDocument();
        }         
        else if ( e.getActionCommand().equals(paste))
        {
            add(getFilesFromClipboard());
        } 
        else if ( e.getActionCommand().equals(select_all))
        {           
            int size = jListFiles.getModel().getSize();
            
            int[] indices = new int[size];
            
            for (int i = 0; i < indices.length; i++)
            {
                indices[i] = i;
            }
            
            for (int i = 0; i < size; i++)
            {
                jListFiles.setSelectedIndices(indices);
            }
        }   
        else if ( e.getActionCommand().equals(delete))
        {
            remove();
        } 
               
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // KEYS PART
    /////////////////////////////////////////////////////////////////////////// 
    
    /**
     * Called when the use clicks on the componant
     */
    private void this_keyPressed(KeyEvent e) 
    {        
        //System.out.println("this_keyReleased(KeyEvent e) " + e.getComponent().getName()); 
                
        int id = e.getID();
        if (id == KeyEvent.KEY_PRESSED) 
        { 
            int keyCode = e.getKeyCode();
                       
            // Trap the special ctrl-v for file copy into attach files zone            
            if (keyCode == KeyEvent.VK_V && e.getModifiers() == ActionEvent.CTRL_MASK)
            {                            
                add(getFilesFromClipboard());
            }
            
            if (keyCode == KeyEvent.VK_DELETE)
            {
                remove();                         
            }            
                                 
        }
       
    } 
    
    ///////////////////////////////////////////////////////////////////////////
    // MOUSE PART
    ///////////////////////////////////////////////////////////////////////////       
    
    /** 
     * When the Mouse is released... Please Pop Up a clean Right Click menu
     * 
     * @param e     The Mouse Eevent
     */
    private void jListFiles_mousePressedOrReleased(MouseEvent e) 
    {            
        itemOpen.setEnabled(false);
        itemPrint.setEnabled(false);
        
        itemPaste.setEnabled(false);
        itemDelete.setEnabled(false);
        itemSelectAll.setEnabled(false);    
        
        if (getFilesFromClipboard() != null)
        {
            itemPaste.setEnabled(true);
        }
        
        if (! attachMap.isEmpty())
        {
            itemSelectAll.setEnabled(true); 
        }
        
        if (jListFiles.getSelectedValue() != null)
        {
            if (jListFiles.getSelectedIndices().length == 1)
            {
                itemOpen.setEnabled(true);
                itemPrint.setEnabled(true);
            }
            
            itemDelete.setEnabled(true);
        }
        
        if (e.isPopupTrigger()) 
        {                    
            if (jListFiles.isEnabled())
            {
                popupMenu.show(e.getComponent(),
                        e.getX(), e.getY());                        
            }

        }
    }  
    
    /**
     * Open the document on a double click
     * @param e The mouse Event
     */
    private void jListFiles_mouseClicked(MouseEvent e) 
    {         
        if (e.getClickCount() >= 2)
        {               
            openDocument();
        }
    } 
    
}
