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
package com.moyosoft.samples.outlook.gui;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MessageDialog extends JDialog
{
    public static final int BUTTON_OK = 1;
    public static final int BUTTON_CANCEL = 2;
    public static final int BUTTON_YES = 4;
    public static final int BUTTON_NO = 8;

    protected JButton mButtonCancel = new JButton("Cancel");
    protected JButton mButtonYes = new JButton("Yes");
    protected JButton mButtonNo = new JButton("No");
    protected JButton mButtonOk = new JButton("Ok");
    protected JLabel mMessageLabel = new JLabel("");
    protected int mButtons = 0;
    protected int mButtonPressed = 0;

    public MessageDialog(Frame pParent, String pTitle, String pMessage,
            int pButtons)
    {
        super(pParent, pTitle, true);
        mButtons = pButtons;
        mMessageLabel.setText(pMessage);
        init();
    }

    public MessageDialog(Dialog pParent, String pTitle, String pMessage,
            int pButtons)
    {
        super(pParent, pTitle, true);
        mButtons = pButtons;
        mMessageLabel.setText(pMessage);
        init();
    }

    public MessageDialog(String pTitle, String pMessage, int pButtons)
    {
        super();
        this.setTitle(pTitle);
        this.setModal(true);
        mButtons = pButtons;
        mMessageLabel.setText(pMessage);
        init();
    }

    private JButton[] getButtons()
    {
        ArrayList list = new ArrayList();
        if((mButtons & BUTTON_OK) != 0)
        {
            list.add(mButtonOk);
        }
        if((mButtons & BUTTON_YES) != 0)
        {
            list.add(mButtonYes);
        }
        if((mButtons & BUTTON_NO) != 0)
        {
            list.add(mButtonNo);
        }
        if((mButtons & BUTTON_CANCEL) != 0)
        {
            list.add(mButtonCancel);
        }

        JButton[] buttons = new JButton[list.size()];
        buttons = (JButton[]) list.toArray(buttons);

        return buttons;
    }

    private void init()
    {
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.CENTER);
        JPanel buttonsPanel = new JPanel(flowLayout);
        JButton[] buttons = getButtons();
        if(buttons != null && buttons.length > 0)
        {
            for(int i = 0; i < buttons.length; i++)
            {
                buttonsPanel.add(buttons[i]);
            }
        }

        mMessageLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(BorderLayout.CENTER, mMessageLabel);
        mainPanel.add(BorderLayout.SOUTH, buttonsPanel);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(BorderLayout.CENTER, mainPanel);

        resizeDialog();
        centerScreen();

        mButtonCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                buttonPressed(BUTTON_CANCEL);
            }
        });
        mButtonNo.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                buttonPressed(BUTTON_NO);
            }
        });
        mButtonOk.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                buttonPressed(BUTTON_OK);
            }
        });
        mButtonYes.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                buttonPressed(BUTTON_YES);
            }
        });
    }

    protected void centerScreen()
    {
        Dimension dim = getToolkit().getScreenSize();
        Rectangle abounds = getBounds();
        setLocation((dim.width - abounds.width) / 2,
                (dim.height - abounds.height) / 2);
        requestFocus();
    }

    protected void resizeDialog()
    {
        int width = Math.max(mMessageLabel.getPreferredSize().width, 340);

        setSize(width, 100);
    }

    protected void closeDialog()
    {
        dispose();
    }

    protected void buttonPressed(int pButton)
    {
        mButtonPressed = pButton;
        closeDialog();
    }

    public int getButtonPressed()
    {
        return mButtonPressed;
    }

    public static void openOk(Frame pParent, String pMessage)
    {
        MessageDialog dlg = new MessageDialog(pParent, pParent.getTitle(),
                pMessage, BUTTON_OK);
        dlg.setVisible(true);
    }

    public static void openOk(Dialog pParent, String pMessage)
    {
        MessageDialog dlg = new MessageDialog(pParent, pParent.getTitle(),
                pMessage, BUTTON_OK);
        dlg.setVisible(true);
    }

    public static void openOk(String pMessage)
    {
        MessageDialog dlg = new MessageDialog("", pMessage, BUTTON_OK);
        dlg.setVisible(true);
    }

    public static int openYesNoCancel(Frame pParent, String pMessage)
    {
        MessageDialog dlg = new MessageDialog(pParent, pParent.getTitle(),
                pMessage, BUTTON_YES | BUTTON_NO | BUTTON_CANCEL);
        dlg.setVisible(true);
        return dlg.getButtonPressed();
    }

    public static int openYesNoCancel(Dialog pParent, String pMessage)
    {
        MessageDialog dlg = new MessageDialog(pParent, pParent.getTitle(),
                pMessage, BUTTON_YES | BUTTON_NO | BUTTON_CANCEL);
        dlg.setVisible(true);
        return dlg.getButtonPressed();
    }
}
