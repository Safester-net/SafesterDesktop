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
package net.safester.application.addrbooknew.tools;


import java.awt.Color;
import java.awt.Component;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.lang3.StringUtils;

import com.kawansoft.crypt.util.sms.PhoneCountryLookup;
import com.swing.util.LookAndFeelHelper;

import net.safester.application.messages.LanguageManager;
import net.safester.application.parms.Parms;
import net.safester.application.parms.RendererParms;
import net.safester.application.util.TableUtil;

/**
 * Basic Table Cell Renderer for Word Files:
 *
 * <br> - Will be used to alternate background colors.
 * <br> - The second column (aka number 1) is right justified because it's the
 * size value.
 * <br> - The third column (aka number 2) is center justified (date modified)
 */

public class RecipientsImportTableCellRenderer extends DefaultTableCellRenderer {
    
    private boolean emailIsUrl = false;

    private static Color HOVER_COLOR = null;
    
    /**
     * Constructor
     */
    RecipientsImportTableCellRenderer() {
        HOVER_COLOR = TableUtil.getMouseOverBackground();
    }
    
     /**
     * Constructor
     */
    RecipientsImportTableCellRenderer(boolean emailIsUrl) {
        this();
        this.emailIsUrl = emailIsUrl;
    }

    
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        c.setForeground(Color.black);

        // If email to be clickable
        if (column == 0 && emailIsUrl) {
            setForeground(Parms.URL_COLOR);
        }
        
        // 2 For main screen only 
        
        if (column == 2 || column == 3) {
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

        //EX ALTERNATE
        c.setBackground(LookAndFeelHelper.getDefaultBackgroundColor());
        c.setForeground(LookAndFeelHelper.getDefaultForegroundColor());

        boolean rowSelected = false;
        int[] selRows = table.getSelectedRows();
        if (selRows.length != 0) {
            // Is the row inside the rowSelected rows ?
            for (int i = 0; i < selRows.length; i++) {
                if (row == selRows[i]) {
                    rowSelected = true;
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(Color.white);
                }
            }
        }

        //int rowMouseOverInt = rowMouseOver.get();
        if (TableUtil.selectedRowRecipients > -1 && TableUtil.selectedRowRecipients == row && !rowSelected) {
            c.setBackground(HOVER_COLOR);
            c.setForeground(TableUtil.getMouseOverForeground());
        }
        
        // Important if there are splitted lines
        int height = c.getPreferredSize().height;
        if (height > table.getRowHeight(row)) {
            table.setRowHeight(row, height);
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
