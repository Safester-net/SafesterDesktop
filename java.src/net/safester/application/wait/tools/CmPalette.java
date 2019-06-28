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


// 09/10/00 13:05 GR - creation
// 09/10/00 13:25 GR - add getDefaultInstance()
// 09/10/00 13:50 GR - OK
// 12/10/00 15:10 GR - extends SaProperties
// 12/10/00 15:15 GR - new package definition
// 18/01/01 18:15 GR - javadoc
// 13/03/01 15:45 GR - use ConfiMail color set now

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;


/**
 * CmPalette represents graphical properties (color,...) of ConfiMail
 * graphical applets.
 */

public class CmPalette
{

        private Map<String, Color> colorsMap = new HashMap<String, Color>();
        
	/**
	 * Creates a new Palette.
	 */

	public CmPalette()
	{
		super() ;
	}


	/**
	 * Gets the default instance (using the default values).
	 * @return
	 */

	public static CmPalette getDefaultInstance()
	{
		CmPalette cp = new CmPalette() ;
		
		cp.setColor("cm.comp.bgcolor", new Color(0xff, 0xff, 0xff)) ;
		cp.setColor("cm.bar.bgcolor", new Color(0xca, 0xca, 0xca)) ;
		cp.setColor("cm.bar.fgcolor", new Color(0xba, 0x00, 0x00)) ;
		cp.setColor("cm.bar.bordercolor", new Color(0x00, 0x00, 0x00)) ;
		
		/*
		cp.setColor("cm.comp.bgcolor", Color.lightGray) ;
		cp.setColor("cm.bar.bgcolor", Color.gray) ;
		cp.setColor("cm.bar.fgcolor", Color.green) ;
		cp.setColor("cm.bar.bordercolor", Color.black) ;
		*/
		
		return cp ;
	}



	/**
	 * Sets a Inew color property.
	 * @param	sName		the name
	 * @param	clrValue	the color to use
	 */

	public void setColor(String sName, Color clrValue)
	{
            colorsMap.put(sName, clrValue) ;
	}


	/**
	 * Gets the color of this property.
	 * @param	sName		the color property name
	 * @return
	 */

	public Color getColor(String sName)
	{
		return colorsMap.get(sName);
	}

}
