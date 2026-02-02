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
package com.safelogic.utilx.io.file;

// 23/07/01 15:10 GR - creation
// 23/07/01 15:15 GR - OK
// 23/07/01 15:20 GR - add nice notice display
// 23/07/01 17:15 GR - use sNoticeLine in losOut.writeln(). Not sNotice!

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.safelogic.utilx.io.stream.Base64DecoderStream;
import com.safelogic.utilx.io.stream.Base64EncoderStream;
import com.safelogic.utilx.io.stream.LineInputStream;
import com.safelogic.utilx.io.stream.LineOutputStream;

public class ObjectFileSaver
{
	private String m_sStoreDir ;
	private boolean m_showStatus ;
	
	public static final String NOTICE_MARK = "# " ;
	
	public ObjectFileSaver(String sStoreDir, boolean showStatus)
	{
		if(sStoreDir == null)
			throw new IllegalArgumentException("Storing directory is null") ;
		m_sStoreDir = sStoreDir ;
		m_showStatus = showStatus ;
	}
	
	public void save(Serializable serObj, String sFileName, String sNotice)
		throws IOException
	{
		if(serObj == null)
			throw new IllegalArgumentException("Object is null") ;
		
		String sNoticeLine = NOTICE_MARK ;
		
		// done by getFilePath()
		//if(sFileName == null)
		//	throw new IllegalArgumentException("File name is null") ;
		
		if(sNotice == null)
			throw new IllegalArgumentException("Notice is null") ;
		
		if(sNotice.indexOf("\r") != -1 || sNotice.indexOf("\n") != -1)
			throw new IllegalArgumentException("Notice can't include line feed") ;
		
		sNoticeLine += sNotice ;
		
		String sFilePath = getFilePath(sFileName) ;
		
		displayStatus("Saving to file: " + sFilePath) ;
		
		// create file output
		FileOutputStream fosOut = new FileOutputStream(sFilePath) ;
		// wrap it in a line output to write notice
		LineOutputStream losOut = new LineOutputStream(fosOut) ;
		losOut.writeln(sNoticeLine) ;
		
		// wrap it in a Base64 output to serialize object
		Base64EncoderStream besOut = new Base64EncoderStream(losOut) ;
		
		ObjectOutputStream oosOut = new ObjectOutputStream(besOut) ;
		oosOut.writeObject(serObj) ;
		
		// close stream
		oosOut.close() ;
		
		displayStatus("Saved") ;
	}
	
	private String getFilePath(String sFileName)
	{
		if(sFileName == null)
			throw new IllegalArgumentException("File name is null") ;
		
		return m_sStoreDir + File.separator + sFileName ;
	}
	
	public Object load(String sFileName)
		throws IOException,
			   ClassNotFoundException
	{
		String sFilePath = getFilePath(sFileName) ;
		
		displayStatus("Loading from file: " + sFilePath) ;
		
		// create file input 
		FileInputStream fisIn = new FileInputStream(sFilePath) ;
		
		// wrap it in a line input to read notice
		LineInputStream lisIn = new LineInputStream(fisIn) ;
		String sNotice = lisIn.readLine() ;
		
		// display notice
		if(sNotice != null)
		{
			int nPos = sNotice.indexOf(NOTICE_MARK) ;
			if(nPos == 0)
				sNotice = sNotice.substring(NOTICE_MARK.length()) ;
			displayStatus("Notice: " + sNotice) ;
		}
		
		// wrap it in a Base64 input to read Object
		Base64DecoderStream bdsIn = new Base64DecoderStream(lisIn) ;
		ObjectInputStream oisIn = new ObjectInputStream(bdsIn) ;
		Object oStored = oisIn.readObject() ;
		
		// close input
		oisIn.close() ;
		
		displayStatus("Loaded") ;
		// return loaded Object
		return oStored ;
	}
	
	private void displayStatus(String sMsg)
	{
		if(m_showStatus)
			System.out.println(sMsg) ;
	}
}
