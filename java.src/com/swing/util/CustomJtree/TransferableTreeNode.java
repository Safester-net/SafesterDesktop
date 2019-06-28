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
package com.swing.util.CustomJtree;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.tree.TreePath;

//TransferableTreeNode.java
//A Transferable TreePath to be used with Drag & Drop applications.
//

class TransferableTreeNode implements Transferable {

  public static DataFlavor TREE_PATH_FLAVOR = new DataFlavor(TreePath.class,
      "Tree Path");

  DataFlavor flavors[] = { TREE_PATH_FLAVOR };

  TreePath path;

  public TransferableTreeNode(TreePath tp) {
    path = tp;
  }

    @Override
  public synchronized DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }

    @Override
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return (flavor.getRepresentationClass() == TreePath.class);
  }

    @Override
  public synchronized Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException, IOException {
    if (isDataFlavorSupported(flavor)) {
      return (Object) path;
    } else {
      throw new UnsupportedFlavorException(flavor);
    }
  }
}
