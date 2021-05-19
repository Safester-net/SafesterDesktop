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
package net.safester.application.photo;

import java.awt.Color;
import java.awt.Component;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.lang3.StringUtils;

import com.kawansoft.crypt.util.sms.PhoneCountryLookup;
import com.swing.util.LookAndFeelHelper;

import net.safester.application.messages.LanguageManager;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.parms.RendererParms;
import net.safester.application.util.TableUtil;

/**
 * Addres Book Cell Renderer.
 *
 */
public class PhotoAddresBookTableCellRendererNew extends DefaultTableCellRenderer {

    public static String GENERIC_PHOTO_ICON = "images/files_2/24x24/businessman2_unknown.png";
        
    MessagesManager messagesManager = new MessagesManager();
    
    /**
     * Constructor
     */
    public PhotoAddresBookTableCellRendererNew() {
        super();
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (column == 0) {
            
            setToolTipText(messagesManager.getMessage("double_click_to_enlarge"));
            String photoBase64 = (String) table.getValueAt(row, 0);

            if (photoBase64 != null && photoBase64.length() > 10) {
                            
                ImageIcon imageIcon = ImageResizer.createIconFromBase64(photoBase64);
                
                this.setIcon(imageIcon);
                this.setText(null);
            }
            else {
               this.setIcon(Parms.createImageIcon(GENERIC_PHOTO_ICON));
               this.setText(null);
            }
        }
       
        if (column == 4) {
            String mobile = (String) value;
            if (mobile != null && ! mobile.isEmpty() && (StringUtils.isNumeric(mobile) || StringUtils.isNumeric(mobile.substring(1)))) {
        	
        	String countryCode = null;
        	try {
		    countryCode = PhoneCountryLookup.getIsoCountryCode(mobile);
		} catch (Exception e) {
		    // No trace in Cell Rendered! Will freeze all!
		}

                mobile = PhoneCountryLookup.getInternationalFormat(mobile, countryCode);
                setText(mobile);

                if (countryCode != null) {
                    Locale locale = new Locale(LanguageManager.getLanguage(), countryCode);
                    setToolTipText(locale.getDisplayCountry(locale));
                }

                Icon flagIcon = getFlagIcon(countryCode);
                setIcon(flagIcon);
            } else {
                setIcon(null);
            }
        }
        
        // This is done to set alternate colors on table background
        // Always set if no rows selected
        if (RendererParms.ALTERNATE_LINES) {
            if (row % 2 == 0) {
                c.setBackground(Parms.LIGHT_BLUE);
            } else {
                c.setBackground(Color.white);
            }
        }
        else {
            c.setBackground(LookAndFeelHelper.getDefaultBackgroundColor());
        }
        
        c.setForeground(LookAndFeelHelper.getDefaultForegroundColor());
        
        int [] selRows = table.getSelectedRows();
        if (selRows.length != 0)
        {
          // Is the row inside the selected rows ?
          for (int i = 0; i < selRows.length; i++)
          {
              if (row == selRows[i] ) 
              {
                   c.setBackground(table.getSelectionBackground());      
                   c.setForeground(Color.white);     
              }               
          }            
        }
        
        if (TableUtil.selectedRowAddress > -1 && TableUtil.selectedRowAddress == row && ! isSelected) {
            c.setBackground(TableUtil.getMouseOverBackground());
            c.setForeground(TableUtil.getMouseOverForeground());
        }
        
        return c;
    }
    
    public static Icon getFlagIcon(String countryCode)  {
        
        if (countryCode == null) {
            return null;
        }
        
        countryCode = countryCode.toLowerCase();
        
        Icon flagIcon = null;
        flagIcon = Parms.createImageIcon("images/flags/" + countryCode + ".png");
        
        return flagIcon;
    }
    
}
