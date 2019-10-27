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
package net.safester.application.addrbooknew;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.tree.TreePath;

import com.moyosoft.connector.com.ComponentObjectModelException;
import com.moyosoft.connector.ms.outlook.folder.FolderType;
import com.moyosoft.connector.ms.outlook.folder.OutlookFolder;
import com.moyosoft.samples.outlook.folderchooser.NewFolderDialog;
import com.moyosoft.samples.outlook.folderchooser.OutlookFolderTreeNode;
import com.moyosoft.samples.outlook.gui.ComErrorDialog;
import com.moyosoft.samples.outlook.gui.Icons;

import net.safester.application.messages.MessagesManager;
 
class FolderPopupMenuNew extends JPopupMenu implements MouseListener
{
    private FolderChooserNew mParent = null;
    private boolean mReadOnly = false;

    private JMenuItem mItemNewFolder = new JMenuItem(MessagesManager.get("new_folder"), Icons.ADD_FOLDER_ICON);
    private JMenuItem mItemDelete = new JMenuItem(MessagesManager.get("system_delete"), Icons.DELETE_ICON);
    private JMenuItem mItemRefresh = new JMenuItem(MessagesManager.get("refresh"), Icons.REFRESH_ICON);
    private JMenuItem mItemExpand = new JMenuItem(MessagesManager.get("develop"));
    private JMenuItem mItemCollapse = new JMenuItem(MessagesManager.get("reduce"));

    public FolderPopupMenuNew(FolderChooserNew pParent)
    {
        mParent = pParent;
        init();
    }

    protected void init()
    {
        add(mItemNewFolder);
        add(mItemDelete);
        add(new JSeparator());
        add(mItemRefresh);
        add(new JSeparator());
        add(mItemExpand);
        add(mItemCollapse);

        Dimension prefSize = getPreferredSize();
        if(prefSize != null)
        {
            setPreferredSize(new Dimension(Math.max(140, prefSize.width),
                    prefSize.height));
        }

        mItemNewFolder.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                createNewFolder();
            }
        });

        mItemDelete.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                deleteSelectedFolder();
            }
        });

        mItemExpand.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                expandSelectedFolder();
            }
        });

        mItemCollapse.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                collapseSelectedFolder();
            }
        });

        mItemRefresh.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                refreshSelectedFolder();
            }
        });
    }

    protected void createNewFolder()
    {
        if(isReadOnly())
        {
            return;
        }

        NewFolderDialog dialog = new NewFolderDialog(mParent, "New folder");
        dialog.setModal(true);
        dialog.show();

        if(dialog.isOkPressed())
        {
            String folderName = dialog.getFolderName();
            FolderType folderType = dialog.getFolderType();

            if(folderName != null && folderType != null
                    && folderName.length() > 0)
            {
                try
                {
                    TreePath path = mParent.getSelectedPath();

                    if(path != null)
                    {
                        OutlookFolder folder = mParent.getFolderForPath(path);

                        if(folder != null)
                        {
                            folder.createFolder(folderName, folderType);
                            mParent.refreshNode((OutlookFolderTreeNode) path
                                    .getLastPathComponent());
                        }
                    }
                }
                catch(ComponentObjectModelException e)
                {
                    ComErrorDialog.open(mParent, e);
                }
            }
        }
    }

    protected void deleteSelectedFolder()
    {
        if(isReadOnly())
        {
            return;
        }

        try
        {
            TreePath path = mParent.getSelectedPath();

            if(path != null)
            {
                OutlookFolder folder = mParent.getFolderForPath(path);

                if(folder != null)
                {
                    folder.delete();
                    mParent.removeSelectionPath(path);
                }
            }
        }
        catch(ComponentObjectModelException e)
        {
            ComErrorDialog.open(mParent, e);
        }
    }

    protected void expandSelectedFolder()
    {
        mParent.expandSelectedItem();
    }

    protected void collapseSelectedFolder()
    {
        mParent.collapseSelectedItem();
    }

    protected void refreshSelectedFolder()
    {
        mParent.refreshSelectedItem();
    }

    protected void setReadOnly(boolean pReadOnly)
    {
        mReadOnly = pReadOnly;
    }

    protected boolean isReadOnly()
    {
        return mReadOnly;
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    private void updateMenu()
    {
        mItemRefresh.setEnabled(true);

        if(isReadOnly())
        {
            mItemDelete.setEnabled(false);
            mItemNewFolder.setEnabled(false);
        }
        else
        {
            mItemDelete.setEnabled(true);
            mItemNewFolder.setEnabled(true);
        }

        OutlookFolder folder = mParent.getSelectedFolder();
        if(folder != null && folder.hasChildren())
        {
            mItemExpand.setEnabled(true);
            mItemCollapse.setEnabled(true);
        }
        else
        {
            mItemExpand.setEnabled(false);
            mItemCollapse.setEnabled(false);
        }
    }

    private void maybeShowPopup(MouseEvent e)
    {
        if(e.isPopupTrigger())
        {
            int row = mParent.getTree().getRowForLocation(e.getX(), e.getY());

            if(row >= 0)
            {
                mParent.getTree().setSelectionRow(row);

                updateMenu();
                show(e.getComponent(), e.getX() + 2, e.getY() - 1);
            }
        }
    }
}
