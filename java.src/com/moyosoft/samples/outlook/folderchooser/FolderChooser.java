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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.moyosoft.connector.com.ComponentObjectModelException;
import com.moyosoft.connector.exception.LibraryNotFoundException;
import com.moyosoft.connector.ms.outlook.Outlook;
import com.moyosoft.connector.ms.outlook.folder.FoldersCollection;
import com.moyosoft.connector.ms.outlook.folder.OutlookFolder;
import com.moyosoft.samples.outlook.gui.Icons;

public class FolderChooser extends JDialog implements TreeSelectionListener,
        TreeWillExpandListener
{
    private OutlookFolder mSelectedFolder = null;
    private FoldersCollection mRootFolders = null;

    private JButton mButtonCancel = new JButton("Cancel");
    private JButton mButtonOk = new JButton("OK");
    private JTree mMainTree = new JTree();

    protected FolderChooser(FoldersCollection pFolders)
    {
        super();
        mRootFolders = pFolders;
        initialize();
    }

    protected FolderChooser(java.awt.Dialog dlg, FoldersCollection pFolders)
    {
        super(dlg);
        mRootFolders = pFolders;
        initialize();
    }

    protected FolderChooser(java.awt.Frame frame, FoldersCollection pFolders)
    {
        super(frame);
        mRootFolders = pFolders;
        initialize();
    }

    private void initialize()
    {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(320, 320);
        setModal(true);
        setTitle("Choose folder");

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new java.awt.BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.add(mButtonOk);
        buttonsPanel.add(mButtonCancel);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(Icons.FOLDER_CLOSED_ICON);
		renderer.setClosedIcon(Icons.FOLDER_CLOSED_ICON);
		renderer.setOpenIcon(Icons.FOLDER_OPEN_ICON);
        
        mMainTree.setShowsRootHandles(true);
        mMainTree.setRootVisible(false);
        mMainTree.setCellRenderer(renderer);
        mMainTree.addMouseListener(new FolderPopupMenu(this));

        JScrollPane scrollPanel = new JScrollPane();
        scrollPanel.setViewportView(mMainTree);

        contentPanel.add(scrollPanel, BorderLayout.CENTER);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);

        java.awt.Dimension dim = getToolkit().getScreenSize();
        java.awt.Rectangle abounds = getBounds();
        setLocation((dim.width - abounds.width) / 2,
                (dim.height - abounds.height) / 2);
        requestFocus();

        try
        {
            FolderTreeNode rootNode = new OutlookFolderRootNode(mRootFolders);
            rootNode.createChildrens();

            mMainTree.setModel(new DefaultTreeModel(rootNode));
            mMainTree.expandPath(new TreePath(new Object[] { rootNode }));
        }
        catch(ComponentObjectModelException e)
        {
            e.printStackTrace();
        }

        mMainTree.addTreeWillExpandListener(this);
        mMainTree.addTreeSelectionListener(this);

        mButtonOk.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pArg0)
            {
                okPressed();
            }
        });

        mButtonCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent pArg0)
            {
                cancelPressed();
            }
        });
    }

    protected JTree getTree()
    {
        return mMainTree;
    }

    public OutlookFolder getChoosedFolder()
    {
        return mSelectedFolder;
    }

    protected OutlookFolder getSelectedFolder()
    {
        return getFolderForPath(mMainTree.getSelectionPath());
    }

    protected OutlookFolder getFolderForPath(TreePath pPath)
    {
        if(pPath == null)
        {
            return null;
        }

        return ((FolderTreeNode) (pPath.getLastPathComponent())).getFolder();
    }

    protected void expandSelectedItem()
    {
        TreePath path = mMainTree.getSelectionPath();

        if(path != null)
        {
            mMainTree.expandPath(path);
        }
    }

    protected void collapseSelectedItem()
    {
        TreePath path = mMainTree.getSelectionPath();

        if(path != null)
        {
            mMainTree.collapsePath(path);
        }
    }

    protected void refreshNode(FolderTreeNode pNode)
    {
        if(pNode != null)
        {
            try
            {
                pNode.refresh();
                ((DefaultTreeModel) mMainTree.getModel()).reload(pNode);
            }
            catch(ComponentObjectModelException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void refreshSelectedItem()
    {
        TreePath path = mMainTree.getSelectionPath();

        if(path != null)
        {
            refreshNode((FolderTreeNode) path.getLastPathComponent());
        }
    }

    protected void refreshAll()
    {
        FolderTreeNode rootNode = (FolderTreeNode) mMainTree.getModel()
                .getRoot();

        if(rootNode != null)
        {
            for(int i = 0; i < rootNode.getChildCount(); i++)
            {
                refreshNode((FolderTreeNode) rootNode.getChildAt(i));
            }
        }
    }

    protected TreePath getSelectedPath()
    {
        return mMainTree.getSelectionPath();
    }

    protected void removeSelectionPath(TreePath pPath)
    {
        if(pPath != null)
        {
            FolderTreeNode node = (FolderTreeNode) pPath.getLastPathComponent();

            ((DefaultTreeModel) mMainTree.getModel())
                    .removeNodeFromParent(node);

            refreshAll();
        }
    }

    public void cancelPressed()
    {
        mSelectedFolder = null;
        dispose();
    }

    public void okPressed()
    {
        mSelectedFolder = getSelectedFolder();
        dispose();
    }

    public void treeWillCollapse(TreeExpansionEvent event)
            throws ExpandVetoException
    {
    }

    public void treeWillExpand(TreeExpansionEvent event)
            throws ExpandVetoException
    {
        if(event.getSource() == mMainTree)
        {
            Object o = event.getPath().getLastPathComponent();

            if(o instanceof FolderTreeNode)
            {
                ((FolderTreeNode) o).createChildrens();
            }
        }
    }

    public void valueChanged(TreeSelectionEvent e)
    {
    }

    public static OutlookFolder open(Frame pParentFrame,
            FoldersCollection pFolders)
    {
        final FolderChooser dlg = new FolderChooser(pParentFrame, pFolders);

        dlg.setModal(true);
        dlg.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                dlg.dispose();
            };
        });
        dlg.show();

        return dlg.getChoosedFolder();
    }

    public static OutlookFolder open(Dialog pParentDialog,
            FoldersCollection pFolders)
    {
        final FolderChooser dlg = new FolderChooser(pParentDialog, pFolders);

        dlg.setModal(true);
        dlg.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                dlg.dispose();
            };
        });
        dlg.show();

        return dlg.getChoosedFolder();
    }

    public static OutlookFolder open(FoldersCollection pFolders)
    {
        final FolderChooser dlg = new FolderChooser(pFolders);

        dlg.setModal(true);
        dlg.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                dlg.dispose();
            };
        });
        dlg.show();

        return dlg.getChoosedFolder();
    }

    public static void main(String[] args)
    {
        try
        {
            Outlook outlook = new Outlook();
            OutlookFolder folder = FolderChooser.open(outlook.getFolders());

            if(folder != null)
            {
                System.out.println("Folder: " + folder.getName());
            }

            outlook.dispose();
        }
        catch(ComponentObjectModelException ex)
        {
            ex.printStackTrace();
        }
        catch(LibraryNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
