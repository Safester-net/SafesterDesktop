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
package net.safester.application.wait.tools;

// 18/06/01 12:15 GR - creation
// 02/10/01 17:45 GR - WIP
// 03/10/01 12:45 GR - WIP
// 16/10/01 16:15 GR - remove useless old code related to animation/thread

import java.awt.Label;

import com.swing.util.LookAndFeelHelper;


public class CmWaitPanel
	extends Label
{
	private String m_sText ;
	private CmPalette m_cpPal ;
	
	public CmWaitPanel(String sText,
					   CmPalette cpPal)
	{
		super() ;
		super.setAlignment(super.CENTER) ;
		setText(sText) ;
		m_cpPal = cpPal ;
		if(m_cpPal == null)
			m_cpPal = CmPalette.getDefaultInstance() ;
		if(LookAndFeelHelper.isDarkMode()) {
			this.setBackground(LookAndFeelHelper.getDefaultBackgroundColor());
		} else {
			this.setBackground(m_cpPal.getColor("cm.comp.bgcolor")) ;
		}
		this.setForeground(m_cpPal.getColor("cm.bar.fgcolor")) ;
	}
	
	
	public void start()
	{
		setText(m_sText) ;
	}
	
	public void setText(String sText)
	{
		m_sText = sText ;
		if(m_sText == null)
			throw new IllegalArgumentException("Wait Dialog text can't be null") ;
		super.setText(m_sText) ;
	}
	
	public void stop()
	{
		//m_atAnim.stop() ;
	}
}
