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
package com.moyosoft.samples.outlook.gui.contact;

import javax.swing.*;
import java.awt.*;

public class EtchedLine extends JPanel
{
   private Color mHighlightColor = Color.white;
   private Color mShadowColor = Color.gray;
   private Dimension mPrefferedSize = new Dimension(100, 2);

   public EtchedLine()
   {
      super();
   }

   public EtchedLine(Color highlight, Color shadow)
   {
      super();
      mHighlightColor = highlight;
      mShadowColor = shadow;
   }

   public void paint(Graphics g)
   {
      int w = this.getWidth();

      g.setColor(mHighlightColor);
      g.drawLine(0, 0, w, 0);
      g.setColor(mShadowColor);
      g.drawLine(0, 1, w, 1);
   }

   public Dimension getPreferredSize()
   {
      return mPrefferedSize;
   }

   public Dimension preferredSize()
   {
      return mPrefferedSize;
   }
}
