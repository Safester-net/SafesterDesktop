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

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import net.safester.application.util.JListUtil;
import net.safester.application.util.TableUtil;

public class FileListRenderer extends JLabel implements ListCellRenderer {

    private AbstractFileListManager listManager;

    //private JList list;
    public void setListManager(AbstractFileListManager manager) {
        this.listManager = manager;
       
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {

        //setText(value.toString());

        List<File> files = listManager.getFiles();
        File f = null;

        for (int i = 0; i < files.size(); i++) {
            f = files.get(i);
            if (f.getName().equals(value.toString())) {
                String displaySize = MessageTableCellRenderer.getDisplaySize(f.length());
                String fileName = value.toString() + " (" + displaySize + ")";
                setText(fileName);
                break;
            }
        }

        // Set spaces between items
        this.setBorder(new EmptyBorder(0, 5, 0, 5));
        
        //zthis.list = list;
        Icon icon = IconManager.getSystemIcon(f);

        if (icon != null) {
            int iconWidth = icon.getIconWidth() + 5;
            int iconHeigth = icon.getIconWidth() + 4;

            if (System.getProperty("os.name").indexOf("Windows") == -1) {
                iconWidth += 3;
                iconHeigth += 2;
            }

            setIcon(icon);
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            //setBackground(Color.WHITE);
            //setForeground(list.getForeground());
            
            if (JListUtil.selectedItem > -1 && JListUtil.selectedItem == index && !isSelected) {
                setBackground(TableUtil.HOVER_COLOR);
                setForeground(TableUtil.getMouseOverForeground());
            } else {
                setBackground(Color.WHITE);
                setForeground(list.getForeground());
            }
        }
        this.setOpaque(true);

        return this;
    }
}
