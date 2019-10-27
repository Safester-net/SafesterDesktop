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
package com.moyosoft.samples.outlook.folderchooser;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.moyosoft.connector.ms.outlook.folder.FolderType;
import com.moyosoft.samples.outlook.gui.contact.EtchedLine;

public class NewFolderDialog extends JDialog
{
    private FolderType[] mFolderTypeList = new FolderType[] {
            FolderType.CALENDAR, FolderType.CONTACTS, FolderType.DRAFTS,
            FolderType.INBOX, FolderType.JOURNAL, FolderType.NOTES,
            FolderType.TASKS };

    private String[] mFolderTypeLabelList = new String[] { "Calendar",
            "Contacts", "Drafts", "Inbox", "Journal", "Notes", "Tasks" };

    // labels
    private JLabel mLabelFolderName = new JLabel("Folder name");
    private JLabel mLabelFolderType = new JLabel("Folder type");

    // fields
    private JTextField mFieldFolderName = new JTextField();
    private JComboBox mFieldFolderType = new JComboBox();

    // buttons
    private JButton mButtonOk = new JButton("OK");
    private JButton mButtonCancel = new JButton("Cancel");

    private boolean mOkPressed = false;
    private String mFolderName = null;
    private FolderType mFolderType = null;

    public NewFolderDialog(Dialog pParent, String pTitle)
    {
        super(pParent, pTitle);
        init();
    }

    public NewFolderDialog(Frame pParent, String pTitle)
    {
        super(pParent, pTitle);
        init();
    }

    protected void init()
    {
        mFieldFolderType
                .setModel(new DefaultComboBoxModel(mFolderTypeLabelList));

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(mButtonOk);
        buttonPanel.add(mButtonCancel);

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(BorderLayout.CENTER, createFieldsPanel());
        mainPanel.add(BorderLayout.SOUTH, buttonPanel);

        mainPanel.setBorder(BorderFactory.createEmptyBorder(7, 4, 4, 4));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(BorderLayout.CENTER, mainPanel);

        pack();
        centerOnScreen(this);

        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                dispose();
            }
        });

        mButtonOk.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                okPressed();
            }
        });

        mButtonCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                cancelPressed();
            }
        });
    }
    
    private static void centerOnScreen(Window window)
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = window.getSize();
        windowSize.height = Math.min(windowSize.height + 20, screenSize.height);
        windowSize.width = Math.min(windowSize.width, screenSize.width);
        window.setLocation((screenSize.width - windowSize.width) / 2,
                (screenSize.height - windowSize.height) / 2);
    }

    private JPanel createFieldsPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // insets between components
        Insets insets = new Insets(2, 5, 2, 5);

        // separator
        EtchedLine separator = new EtchedLine();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;

        // 0,0
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.insets = insets;
        panel.add(mLabelFolderName, c);

        // 1,0
        c.gridx = 1;
        c.gridy = 0;
        c.insets = insets;
        panel.add(mFieldFolderName, c);

        // 0,1
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.insets = insets;
        panel.add(mLabelFolderType, c);

        // 1,1
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.insets = insets;
        panel.add(mFieldFolderType, c);

        // separator line: 0,2 and 1,2
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        c.gridwidth = 2;
        c.insets = insets;
        panel.add(separator, c);
        c.gridwidth = 1;

        int height = (insets.top + insets.bottom) * 3
                + mFieldFolderName.getPreferredSize().height
                + mFieldFolderType.getPreferredSize().height
                + separator.getPreferredSize().height;

        panel.setPreferredSize(new Dimension(300, height));

        return panel;
    }

    protected void okPressed()
    {
        mFolderName = mFieldFolderName.getText();
        int selectedIndex = mFieldFolderType.getSelectedIndex();
        if(selectedIndex >= 0)
        {
            mFolderType = mFolderTypeList[selectedIndex];
        }
        mOkPressed = true;
        dispose();
    }

    protected void cancelPressed()
    {
        mOkPressed = false;
        dispose();
    }

    public boolean isOkPressed()
    {
        return mOkPressed;
    }

    public String getFolderName()
    {
        return mFolderName;
    }

    public FolderType getFolderType()
    {
        return mFolderType;
    }
}
