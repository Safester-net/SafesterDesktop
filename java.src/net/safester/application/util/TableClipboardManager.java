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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.UndoManager;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;



/**
 * Clipboard Manager allows to add right click pop up menu to JTable with
 * the classical cut/copy/paste/select menu.
 * <br><br>
 * This is done with two lines of code per JFrame:
 * <br> -1) public TableClipboardManager clipboard = null; 
 * <br> -2) clipboard = new TableClipboardManager(JTable);
 * <br>
 * <br> -2) Must be done <i>after</i> jFrame creation.
 *  
 * @author Nicolas de Pomereu
 *
 */
public class TableClipboardManager
{
    public static final String CR_LF = System.getProperty("line.separator") ;
    
    private MessagesManager messagesManager = new  MessagesManager();
        
    // The Pop Up menu for Paste action
    private JPopupMenu popupMenu;
    
    // Clipboard lines
    private JMenuItem menuItemCancel = null;
    private JMenuItem menuItemCut = null;
    private JMenuItem menuItemCopy = null;
    private JMenuItem menuItemPaste = null;
    private JMenuItem menuItemDelete= null;
    private JMenuItem menuItemSelectAll = null;
    
    // Futur usage
    protected UndoManager undo = new UndoManager();

    // Clipboard actions
    private String cancel       = messagesManager.getMessage("system_cancel");
    private String cut          = messagesManager.getMessage("system_cut");
    private String copy         = messagesManager.getMessage("system_copy");
    private String paste        = messagesManager.getMessage("system_paste");
    private String select_all   = messagesManager.getMessage("system_select_all");
    private String delete       = messagesManager.getMessage("system_delete");
    
    /** The JTable to add a contextual pop menu */
    private JTable table = null;
    
    /**
     * Constructor
     * @param table The JTable to add a contextual pop menu
     */
    
    public TableClipboardManager(JTable table)
    { 
        this.table = table;
        
        popupMenu = new JPopupMenu();        
        popupMenu.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                
        menuItemCancel = new JMenuItem(cancel);
        menuItemCancel.setEnabled(false);
        popupMenu.add(menuItemCancel);   
                
        popupMenu.addSeparator();
        
        menuItemCut = new JMenuItem(new DefaultEditorKit.CutAction());
        menuItemCut.setText(cut);
        menuItemCut.setIcon(Parms.createImageIcon(Parms.CUT_ICON));
        
        menuItemCut.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                jTable_clipboard_actionPerformed(e);
            }})); 
        //menuItemCut.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        popupMenu.add(menuItemCut);   
        
        menuItemCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
        menuItemCopy.setText(copy);
        menuItemCopy.setIcon(Parms.createImageIcon(Parms.COPY_ICON));
        
        menuItemCopy.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                jTable_clipboard_actionPerformed(e);
            }}));         
        //menuItemCopy.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        popupMenu.add(menuItemCopy);   
        
        menuItemPaste = new JMenuItem(new DefaultEditorKit.PasteAction());
        menuItemPaste.setText(paste);
        menuItemPaste.setIcon(Parms.createImageIcon(Parms.PASTE_ICON));

        menuItemPaste.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                jTable_clipboard_actionPerformed(e);
            }}));               
        //menuItemPaste.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        popupMenu.add(menuItemPaste);          
        
        menuItemDelete = new JMenuItem(delete);
        menuItemDelete.setIcon(Parms.createImageIcon(Parms.DELETE_ICON));

        menuItemDelete.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                jTable_clipboard_actionPerformed(e);
            }}));         
        popupMenu.add(menuItemDelete);    
                
        popupMenu.addSeparator();
        
        menuItemSelectAll = new JMenuItem(select_all);
        menuItemSelectAll.setText(select_all);
        menuItemSelectAll.addActionListener((new ActionListener() {
          public void actionPerformed(ActionEvent e)
          {
              jTable_clipboard_actionPerformed(e);
          }})); 
        popupMenu.add(menuItemSelectAll);        
        
        table.addMouseListener(new MouseAdapter() { 
            
            public void mousePressed(MouseEvent e) {
                jTable_mouseReleased(e);
            }

            public void mouseReleased(MouseEvent e) {
                jTable_mouseReleased(e);
            }
                        
        });
    }
    
    /** 
     * When the Mouse is released, clipboard action is done
     * @param e     The Mouse Eevent
     */
    public void jTable_mouseReleased(MouseEvent e) 
    { 
          
        // These are disabled because the Table is not editable
        menuItemPaste.setEnabled(false);
        menuItemCut.setEnabled(false);
        menuItemDelete.setEnabled(false);
        
        int [] selRows = table.getSelectedRows();
        
        if (selRows.length == 0) 
        {
            menuItemCopy.setEnabled(false);
        }     
        else
        {
            menuItemCopy.setEnabled(true);
        }
        
        if (e.isPopupTrigger()) {
            popupMenu.show(e.getComponent(),
                       e.getX(), e.getY());
        }
    } 
    
    
    // Paste action
    public void jTable_clipboard_actionPerformed(ActionEvent e)
    {        
        //System.out.println("e.getActionCommand(): " + e.getActionCommand());
             
        if ( e.getActionCommand().equals(select_all))
        {           
           table.selectAll();
        }           
        else if ( e.getActionCommand().equals(copy))
        {
            // Put the content of the table in a JTextField
            int [] selRows = table.getSelectedRows();
            
            if (selRows.length > 0) 
            {
                String value = "";
                
                for (int i= 0; i < selRows.length ; i++) 
                {
                    // get Table data
                    TableModel tm = table.getModel();
                                                        
                    // Force the cast of the getValueAt into a String
                    Object oValue0 = tm.getValueAt(selRows[i], 0);      
                    Object oValue1 = tm.getValueAt(selRows[i], 1);  
                    
                    value +=  (String) oValue0.toString();
                    value += "\t";
                    value += (String) oValue1.toString();
                    value += CR_LF;                    
                }   
                
              JEditorPane jEditorPane = new JEditorPane(); 
              jEditorPane.setText(value);
              jEditorPane.selectAll();
              jEditorPane.copy();
                                
            }            
            
        }

       
    }    

}

/**
 * 
 */
