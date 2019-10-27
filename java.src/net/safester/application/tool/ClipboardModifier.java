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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import org.apache.commons.lang3.StringUtils;

import net.safester.application.util.HtmlTextUtil;
import net.safester.application.util.JOptionPaneNewCustom;


/**
 * This class modify clipboard content
 * @author Alexandre Becquereau
 */
public class ClipboardModifier {

    private static String oldClipboardContent = "";

    
    /**
     * Test if the clipboad content has more than one line
     * @return  true if we must use our method because clipboard has more than one line
     */
    public static boolean isClipboadContentMultiLines(){
            //Get System Clipboard content

        String clipboardContent = null;

        if ( isClipboardContentIsString()) {
            //Ok clipboard is not empty and is a String
            try {
                Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                clipboardContent = (String) contents.getTransferData(DataFlavor.stringFlavor);

                int numLines = HtmlTextUtil.countLines(clipboardContent);

                if (numLines > 1)
                {
                    return true;
                }
                else
                {
                    return false;
                }

            } catch (Exception ex) {
                //should never happen (using standard String flavour)
                JOptionPaneNewCustom.showException(null, ex);
                return false;
            }
        }
        
        return false;

    }

    /**
     * Modify the String in the clipboard
     * @return  the modified String that has been placed in clipboard (null if clipboard was
     *              empty or not containing a String)
     */
    public static String modifyClipboardString(){
            //Get System Clipboard content

        String clipboardContent = null;
        
        if ( isClipboardContentIsString()) {
            //Ok clipboard is not empty and is a String
            try {
                Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                clipboardContent = (String) contents.getTransferData(DataFlavor.stringFlavor);

                //Backup clipboard content
                oldClipboardContent = clipboardContent;
				clipboardContent = StringUtils.replace(clipboardContent, "<", "&lt;");
				clipboardContent = StringUtils.replace(clipboardContent, ">", "&gt;");
                //JOptionPane.showMessageDialog(null, clipboardContent);
                //Modify clipboardContent String as needed!

                int numLines = HtmlTextUtil.countLines(clipboardContent);

                if (numLines > 1)
                {
                    clipboardContent = HtmlTextUtil.formatWithBr(clipboardContent);
                }
                
                //Put new String into clipboard
                StringSelection stringSelection = new StringSelection(clipboardContent);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
                return clipboardContent;
            } catch (Exception ex) {
                //should never happen (using standard String flavour)
                JOptionPaneNewCustom.showException(null, ex);
                return clipboardContent;
            }
        }
        return clipboardContent;
    }

    /**
     * Restore the old content of clipboard if needed
     */
    public static void restoreOrignalClipboardString(){
        if(!oldClipboardContent.equals("")){
            StringSelection stringSelection = new StringSelection(oldClipboardContent);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            //reset oldClipboardContent
            oldClipboardContent = "";
            HtmlTextUtil.nbBr = 0;
        }
    }

    /**
     * Indicates if clipboard contains a String 
     * @return
     */
    public static boolean isClipboardContentIsString(){
        Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
         if ( (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor)){
            return true;
         }
         return false;
    }

    /**
     * Get clipboard string length
     * @return the length of the string found in clipboard
     */
    public static int getOriginalContentLength(){
        int nbBr  = HtmlTextUtil.nbBr;

        if(nbBr < 0){
            nbBr = 0;
        }
        int length = oldClipboardContent.length() - nbBr;
        //Stupid but required....
        length++;
        return length+1;
    }

}
