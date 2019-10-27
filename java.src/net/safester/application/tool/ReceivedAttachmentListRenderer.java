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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import net.safester.application.parms.Parms;
import net.safester.application.util.CacheFileHandler;
import net.safester.application.util.JListUtil;
import net.safester.application.util.TableUtil;
import net.safester.clientserver.util.FileNameConverter;
import net.safester.noobs.clientserver.AttachmentLocal;

public class ReceivedAttachmentListRenderer extends JLabel implements ListCellRenderer {

    private Map<Integer, Long> fileSizes = new HashMap<Integer, Long>();
    
    private static int MAX_WIDTH = 0;

    public ReceivedAttachmentListRenderer(List<AttachmentLocal> attachments )
    {      
        for (int i = 0; i < attachments.size(); i++) {
            AttachmentLocal attachmentLocal = attachments.get(i);
            int attachPosition = attachmentLocal.getAttachPosition();
            long size = attachmentLocal.getFileSize();

            //System.out.println("attachPosition: " + attachPosition + " size: " + size);
            fileSizes.put(attachPosition -1, size);
        }
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
                
        File f = null;

        String fileName = value.toString();
        
        try {
            FileNameConverter fileNameConverter = new FileNameConverter(fileName);
            fileName = fileNameConverter.fromServerName();

        } catch (Exception ex) {
        }
        
        fileName = fileName.substring(0, fileName.lastIndexOf("."));

        long size = 0;
        
        try {
            size = fileSizes.get(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
                        
        if (size == 0)
        {
            setText(fileName );
        }
        else
        {
           setText(fileName + " (" + MessageTableCellRenderer.getDisplaySize(size)  + ")");
        }
        
       
        //Stupid but if we don't do that file will be locked for decryption
        fileName = "ico_" + fileName;
        Icon icon = null;

        //String tempDir = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator");
        String tempDir = Parms.getSafesterTempDir();

        f = new File(tempDir + fileName);

        CacheFileHandler cacheFileHandler = new CacheFileHandler();
        cacheFileHandler.addCachedFile(tempDir + fileName);
        OutputStream os = null;
        try {
            os = new FileOutputStream(f);
            os.write("1".getBytes());
            icon = IconManager.getSystemIcon(f);
        } catch (Exception e) {
            System.out.println("Can't write temp file....");
            e.printStackTrace();
        }
        finally
        {
            IOUtils.closeQuietly(os);
        }

        // Set spaces between items
        this.setBorder(new EmptyBorder(0, 5, 0, 5));
        
        this.setToolTipText(StringUtils.substringAfter(fileName, "ico_"));

        if (icon != null) {
            setIcon(icon);
        }
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());

        } else {
            
            if (JListUtil.selectedItem > -1 && JListUtil.selectedItem == index && !isSelected) {
                setBackground(TableUtil.HOVER_COLOR);
                setForeground(TableUtil.getMouseOverForeground());
            }
            else {
                setBackground(Color.WHITE); //list.getBackground());
                setForeground(list.getForeground());               
            }

        
        
        }
        this.setOpaque(true);

        return this;
    }
}
