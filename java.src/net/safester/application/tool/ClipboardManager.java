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

import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;

import com.swing.util.SwingUtil;



/**
 * Clipboard Manager allows to add right click pop up menu to all text fields with
 * the classical cut/copy/paste/select menu.
 * <br><br>
 * This is done with two lines of code per JFrame:
 * <br> -1) public ClipboardManager clipboard = null; 
 * <br> -2) clipboard = new ClipboardManager(jFrame);
 * <br>
 * <br> -2) Must be done <i>after</i> jFrame creation.
 *  
 * @author Nicolas de Pomereu
 *
 */
public class ClipboardManager
{
    private MessagesManager messagesManager = new  MessagesManager();
        
    // The Pop Up menu for Paste action
    private JPopupMenu popupMenu;
    
    // Clipboard lines
    private JMenuItem menuItemCut = null;
    private JMenuItem menuItemCopy = null;
    private JMenuItem menuItemPaste = null;
    private JMenuItem menuItemDelete= null;
    private JMenuItem menuItemSelectAll = null;
    
    // Futur usage
    protected UndoManager undo = new UndoManager();

    // Clipboard actions
    private String cut  = messagesManager.getMessage("system_cut");
    private String copy = messagesManager.getMessage("system_copy");
    private String paste= messagesManager.getMessage("system_paste");
    private String select_all= messagesManager.getMessage("system_select_all");
    private String delete = messagesManager.getMessage("system_delete");
    
    private JTextComponent textComponent = null;
    
    /** The container */
    Container container = null;
    
    /**
     * 
     * @param container
     */
    public ClipboardManager(Container container)
    {
        this.container = container;    
        
        popupMenu = new JPopupMenu();        
        popupMenu.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                                 
        menuItemCut = new JMenuItem(new DefaultEditorKit.CutAction());
        menuItemCut.setText(cut);
        menuItemCut.setIcon(Parms.createImageIcon(Parms.CUT_ICON));

        //menuItemCut.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        popupMenu.add(menuItemCut);   
        
        menuItemCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
        menuItemCopy.setText(copy);
        menuItemCopy.setIcon(Parms.createImageIcon(Parms.COPY_ICON));

        //menuItemCopy.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        popupMenu.add(menuItemCopy);   
        
        menuItemPaste = new JMenuItem(new DefaultEditorKit.PasteAction());
        menuItemPaste.setText(paste);
        menuItemPaste.setIcon(Parms.createImageIcon(Parms.PASTE_ICON));

        //menuItemPaste.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        popupMenu.add(menuItemPaste);          
        
        menuItemDelete = new JMenuItem(delete);
        menuItemDelete.setIcon(Parms.createImageIcon(Parms.DELETE_ICON));

        menuItemDelete.addActionListener((new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                jTextComponent_clipboard_actionPerformed(e);
            }}));         
        popupMenu.add(menuItemDelete);    
                
        popupMenu.addSeparator();
        
        menuItemSelectAll = new JMenuItem(select_all);
        menuItemSelectAll.setText(select_all);
          menuItemSelectAll.addActionListener((new ActionListener() {
          public void actionPerformed(ActionEvent e)
          {
              jTextComponent_clipboard_actionPerformed(e);
          }})); 
        popupMenu.add(menuItemSelectAll);        
                
        // Done! Now add the listeners
        addMouseListenerToTextComponents();
    }
    
   
    /**
     * Add a Mouse Listener to all Text Components that is dedicated to clipboard
     * actions
     */
    private void addMouseListenerToTextComponents()
    {
        List<Component> c = SwingUtil.getAllComponants(container);
        
        for (int i = 0; i < c.size(); i++)
        {
            Component component = (Component) c.get(i);
            
            //System.out.println("component: " + component);
            
            if (component instanceof JTextComponent && ! (component instanceof JPasswordField))
            {                
                JTextComponent textComponent = (JTextComponent)component;
                
                textComponent.addMouseListener(new MouseAdapter() { 
                    public void mousePressed(MouseEvent e) 
                    { 
                        jTextComponent_mousePressedOrReleased(e); 
                    } 
                    
                    public void mouseReleased(MouseEvent e) 
                    { 
                        jTextComponent_mousePressedOrReleased(e); 
                    }                     
                    
                });
                
                // Do not allow undo/redo on non editable components!
                if (! textComponent.isEditable())
                {
                    continue;
                }
                
                // Undo Manager
                final UndoManager undo = new UndoManager();
                Document doc = textComponent.getDocument();
                
                // Listen for undo and redo events
                doc.addUndoableEditListener(new UndoableEditListener() {
                    public void undoableEditHappened(UndoableEditEvent evt) {
                        undo.addEdit(evt.getEdit());
                    }
                });
                // Create an undo action and add it to the text component
                textComponent.getActionMap().put("Undo",
                    new AbstractAction("Undo") {
                        public void actionPerformed(ActionEvent evt) {
                            try {
                                if (undo.canUndo()) {
                                    undo.undo();
                                }
                            } catch (CannotUndoException e) {
                            }
                        }
                   });
                
                // Bind the undo action to ctl-Z
                textComponent.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

                // Create a redo action and add it to the text component
                textComponent.getActionMap().put("Redo",
                    new AbstractAction("Redo") {
                        public void actionPerformed(ActionEvent evt) {
                            try {
                                if (undo.canRedo()) {
                                    undo.redo();
                                }
                            } catch (CannotRedoException e) {
                            }
                        }
                    });
                                
                // Bind the redo action to ctl-Y
                textComponent.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
                        
            }
        }       
        
    }
    
    /** 
     * When the Mouse is released, clipboard action is done
     * @param e     The Mouse Eevent
     */
    public void jTextComponent_mousePressedOrReleased(MouseEvent e) 
    { 
       // System.out.println(Util.CR_LF + "jTextFieldCode_mouseReleased(MouseEvent e) called.");
          
        if (e.getComponent() instanceof JTextComponent)
        {
            textComponent = (JTextComponent) e.getComponent();
            
            textComponent.requestFocusInWindow();
            
            //System.out.println("Text Component: " +  textComponent);
            //System.out.println("Text Content  : " +  textComponent.getText());
            
            if (textComponent.isEditable() && 
                textComponent.isEnabled() && 
                isTextDataAvailableForPaste())
            {
                menuItemPaste.setEnabled(true);
            }
            else
            {
                menuItemPaste.setEnabled(false);
            }
            
            if (textComponent.getText() == null || 
                textComponent.getText().equals(""))               
            {
                menuItemSelectAll.setEnabled(false);
            }
            else
            {           
                
                if (textComponent.isEnabled())
                {
                    menuItemSelectAll.setEnabled(true);
                }
                else
                {
                    menuItemSelectAll.setEnabled(false);
                }
            }
            
            if (textComponent.getSelectedText() == null)
            {
               menuItemCopy.setEnabled(false);
               menuItemCut.setEnabled(false);
               menuItemDelete.setEnabled(false);
            }
            else
            {
                menuItemCopy.setEnabled(true);
                
                if (textComponent.isEditable())
                {
                    menuItemCut.setEnabled(true);                
                    menuItemDelete.setEnabled(true);
                }
                else
                {
                    menuItemCut.setEnabled(false);                
                    menuItemDelete.setEnabled(false);
                }
            }                      
        }
                
        if (e.isPopupTrigger()) {
            popupMenu.show(e.getComponent(),
                       e.getX(), e.getY());
        }
    } 
    
    
    // Paste action
    public void jTextComponent_clipboard_actionPerformed(ActionEvent e)
    {        
        //System.out.println("e.getActionCommand(): " + e.getActionCommand());
             
        if ( e.getActionCommand().equals(select_all))
        {           
           textComponent.selectAll();
        }   
        else if ( e.getActionCommand().equals(delete))
        {
            textComponent.setText(null);
        }         
        
    }    
    
    /**
     * return true if the data is available for paste from the clipboard
     */
    public boolean isTextDataAvailableForPaste()
    {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        
        //odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
                    
        boolean hasTransferableFiles = (contents != null) &&
               contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        
        return hasTransferableFiles;
    }
}
