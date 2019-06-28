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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;


public class CmWaitDialog
	extends Dialog
{
	public static final boolean MODAL = false ;
	
	public static final int WINDOW_HEIGHT = 28 ;
	public static final int WINDOW_WIDTH = 300 ;
	
	//public static final int LOCATION_TOP = 100 ;
	//public static final int LOCATION_LEFT = 100 ;
	
	private CmWaitPanel m_wpWaitPan ;

    /**
     * Default constructor
     * 
     * @param sTitle       The title of the Waiter Panel
     * @param sText        The text to display
     * @param cpPal        The CmPalette to use
     */
    public CmWaitDialog(Window window,
                        String sTitle,
                        String sText,
                        CmPalette cpPal)
    {
        super(new Frame(), sTitle, MODAL) ;
        
        this.setUndecorated(true);
        
        // set basics
        setResizable(false) ;
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT) ;

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        this.setLocationRelativeTo(window);
        
        // prepare panel
        setupPanel(sText, cpPal) ;
    }   
    
	/**
	 * Constructor
	 * 
	 * @param sTitle       The title of the Waiter Panel
	 * @param sText        The text to display
	 * @param cpPal        The CmPalette to use
	 */
	public CmWaitDialog(String sTitle,
						String sText,
						CmPalette cpPal)
	{
	    this(null, sTitle, sText, cpPal);
	}


	public void startWaiting()
	{
		startPanel() ;
		this.setVisible(true);
	}
	
	public void stopWaiting()
	{        
            this.dispose();
	}
	
	private void setupPanel(String sText, CmPalette cpPal)
	{
		setLayout(new BorderLayout()) ;
		
		m_wpWaitPan = new CmWaitPanel(sText, cpPal) ;
		
		/*
		add(BorderLayout.EAST, new Label("-")) ;
		add(BorderLayout.NORTH, new Label("-")) ;
		add(BorderLayout.SOUTH, new Label("-")) ;
		add(BorderLayout.WEST, new Label("-")) ;
		*/
		
		add("Center", m_wpWaitPan) ;
	}
	
	private void startPanel()
	{
		if(m_wpWaitPan == null)
			throw new IllegalStateException("Null WaitPanel") ;
		m_wpWaitPan.start() ;
	}
	
	private void stopPanel()
	{
		if(m_wpWaitPan == null)
			throw new IllegalStateException("Null WaitPanel") ;
		m_wpWaitPan.stop() ;
	}
	
	public void changeText(String sText)
	{
		m_wpWaitPan.setText(sText) ;
	}
}
