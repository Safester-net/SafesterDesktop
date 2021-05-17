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
import java.awt.Font;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.swing.util.LookAndFeelHelper;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.parms.RendererParms;
import net.safester.application.util.AppDateFormat;
import net.safester.application.util.TableUtil;

/**
 * Pgeep Table Cell Renderer. Will be used to only highlight the owner public
 * key.
 */
public class MessageTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = -6589075810337623730L;

    public static final int COL_INDEX_MSG_ID = 0;
    public static final int COL_INDEX_FOLDER_ID = 1;
    public static final int COL_INDEX_READ = 2;
    public static final int COL_INDEX_ATTACH = 3;
    public static final int COL_INDEX_STARRED = 4;
    public static final int COL_INDEX_FROM_TO = 5;
    public static final int COL_INDEX_SUBJECT = 6;
    public static final int COL_INDEX_DATE = 7;
    public static final int COL_INDEX_SIZE = 8;

    /**
     * The Set that stores all the read messages
     */
    public static Set<Integer> readMessages = new HashSet<Integer>();

    /**
     * The Set that stores all the starred messages
     */
    public static Set<Integer> starredMessages = new HashSet<Integer>();

    private Icon iconAttach = Parms.createImageIcon(Parms.PAPERCLIP_ICON);
    private Icon iconRead = Parms.createImageIcon("images/files_2/16x16/mail_open2.png");
    private Icon iconUnRead = Parms.createImageIcon("images/files_2/16x16/mail.png");
    private Icon iconStarred = Parms.createImageIcon(Parms.STARRED_ICON);
    private Icon iconStarredOff = Parms.createImageIcon(Parms.STARRED_OFF_ICON);

    private AppDateFormat df = null;

    private boolean isSearchTable = false;

    MessagesManager messagesManager = new MessagesManager();

    /**
     * Default Constructor.
     */
    public MessageTableCellRenderer() {
        super();
        df = new AppDateFormat();
    }

    /**
     * Constructor to use if fe are in a serach table
     *
     * @param isSearchTable
     */
    public MessageTableCellRenderer(boolean isSearchTable) {
        this();
        this.isSearchTable = isSearchTable;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        JComponent jComponent = (JComponent) c;
        jComponent.setBorder(new EmptyBorder(5, 5, 5, 5));

        if (column == COL_INDEX_READ) {
            // this.setToolTipText((String)table.getValueAt(row, 0));
            setToolTipText(messagesManager.getMessage("message_id") + " " + table.getValueAt(row, 0) + "");
        }

        String read = (String) table.getValueAt(row, COL_INDEX_READ);
        String attachValue = (String) table.getValueAt(row, COL_INDEX_ATTACH);
        String starred = (String) table.getValueAt(row, COL_INDEX_STARRED);
        if (read == null || attachValue == null || starred == null) {
            return this;
        }

        int messageId = (Integer) table.getValueAt(row, 0);
        if (readMessages.contains(messageId)) {
            read = "true";
        }

        if (!starredMessages.contains(messageId)) {
            starred = "false";
        }

        if (column == COL_INDEX_ATTACH && attachValue.equals("true")) {
            // return new JLabel(iconAttach);
            // table.setValueAt(label, row, column);
            this.setIcon(iconAttach);
        } else if (column == COL_INDEX_STARRED) {
            this.setIcon(starred.equals("true") ? iconStarred : iconStarredOff);
        } else {
            // this.setText("");
            this.setIcon(null);
        }

        if (read.equals("false")) {
            if (column == COL_INDEX_READ) {
                this.setIcon(iconUnRead);
            }
            Font f = c.getFont();
            f = f.deriveFont(Font.BOLD);
            c.setFont(f);
        } else if (column == COL_INDEX_READ) {
            this.setIcon(iconRead);
        }

        if (column == COL_INDEX_READ || column == COL_INDEX_ATTACH || column == COL_INDEX_STARRED) {
            this.setText("");
        }

        // Format the timestamp
        // MessageTableCellRenderer: date message is now column 7 (instead of 6)
        if (column == COL_INDEX_DATE) {
            Timestamp ts = Timestamp.valueOf(this.getText());
            String formatedDate = df.format(ts);
            this.setText(formatedDate);
        }

        // Size display with Kb or Mb
        if (column == COL_INDEX_SIZE) {
            long size = Long.parseLong(this.getText());
            String displaySize = getDisplaySize(size);
            this.setText(displaySize);
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

        int[] selRows = table.getSelectedRows();
        if (selRows.length != 0) {
            // Is the row inside the selected rows ?
            for (int i = 0; i < selRows.length; i++) {
                if (row == selRows[i]) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(Color.white);
                }
            }
        }

        if (!isSearchTable) {
            if (TableUtil.selectedBoxLine > -1 && TableUtil.selectedBoxLine == row && !isSelected) {
                c.setBackground(TableUtil.HOVER_COLOR);
                c.setForeground(TableUtil.getMouseOverForeground());
            }
        } else {
            if (TableUtil.selectedSearchLine > -1 && TableUtil.selectedSearchLine == row && !isSelected) {
                c.setBackground(TableUtil.HOVER_COLOR);
                c.setForeground(TableUtil.getMouseOverForeground());
            }
        }

        return c;
    }

    private static String KB_DISPLAY = null;
    private static String MB_DISPLAY = null;
    private static String GB_DISPLAY = null;

    /**
     * Format the size with Kb or Mb
     *
     * @param sizeStr the input size in string
     * @return the formated size with Kb or Mb
     */
    public static String getDisplaySize(long size) {
        if (KB_DISPLAY == null) {
            MessagesManager messages = new MessagesManager();
            KB_DISPLAY = messages.getMessage("kb");
            MB_DISPLAY = messages.getMessage("mb");
            GB_DISPLAY = messages.getMessage("gb");
        }

        String unit = null;

        if (size >= Parms.GO) {
            size = size / Parms.GO;
            unit = GB_DISPLAY;
        } else if (size >= Parms.MO) {
            size = size / Parms.MO;
            unit = MB_DISPLAY;
        } else {
            size = size / Parms.KO;
            unit = KB_DISPLAY;
        }

        if (size == 0) {
            size = 1; // minimum display size is 1 Kb...
        }
        return size + " " + unit;
    }
}
