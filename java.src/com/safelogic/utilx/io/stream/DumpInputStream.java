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
package com.safelogic.utilx.io.stream;

// 23/01/02 15:05 GR - creation
// 23/01/02 15:05 GR - add class javadoc
// 24/01/02 11:50 GR - new version: dump only last operation on Exception

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/* NEED TO BE UPDATED !!!!!
 * This class extends BufferedInputStream to behave as a regular
 * BufferedInputStream and adds a dumping feature to copy what
 * has been read (by call to any of the read() methods) to the
 * standard output.<br>
 * In the produced output, read methods are differenciated by the
 * count of their arguments which is concatenated to the method name,
 * i.e.: read-1 for read(byte[]).<br>
 * Data dump is preceded by information about the method and its
 * arguments values. Data are dumped on a new line, except for the
 * simple read() method. In any case, the data is immediately followed
 * by a ";".
 */

public class DumpInputStream
	extends BufferedInputStream
{
	private String[] m_sLastOp ;
	private static final int HOW_MANY_OP = 5 ;
	private static final String CR_LF = System.getProperty("line.separator") ;
	private int m_nTotalRead ;
	private int m_nTotalOperation ;
	
	public DumpInputStream(InputStream isIn)
	{
		super(isIn) ;
		init() ;
	}
	
	public DumpInputStream(InputStream isIn, int nSize)
	{
		super(isIn, nSize) ;
		init() ;
	}
	
	private void init()
	{
		m_sLastOp = new String[HOW_MANY_OP] ;
		for(int n = 0 ; n < m_sLastOp.length ; n++)
			m_sLastOp[n] = null ;
		m_nTotalRead = 0 ;
		m_nTotalOperation = 0 ;
	}
	
	public int read()
		throws IOException
	{
		int nB ;
		try{
			nB = super.read() ;
		}
		catch(IOException ioe)
		{
			dumpState() ;
			throw ioe ;
		}
		m_nTotalRead += 1 ;
		String sLastOp = "read-0()[1]" + (char)nB ;
		putLastOp(sLastOp) ;
		return nB ;
	}
	
	public int read(byte b[])
		throws IOException
	{
		int nRead ;
		try{
			nRead = super.read(b) ;
		}
		catch(IOException ioe)
		{
			dumpState() ;
			throw ioe ;
		}
		m_nTotalRead += nRead ;
		String sLastOp = "read-1()[" + nRead + "]" + CR_LF ;
		sLastOp += new String(b) ;
		putLastOp(sLastOp) ;
		return nRead ;
	}
	
	public int read(byte b[], int off, int len)
		throws IOException
	{
		int nRead ;
		try{
			nRead = super.read(b, off, len) ;
		}
		catch(IOException ioe)
		{
			dumpState() ;
			throw ioe ;
		}
		m_nTotalRead += nRead ;
		String sLastOp = "read-3(off=" + off + ", len=" + len + ")[" + nRead + "]" + CR_LF ;
		sLastOp += new String(b, off, len) ;
		putLastOp(sLastOp) ;
		return nRead ;
	}
	
	private void putLastOp(String sLastOp)
	{
		for(int n = m_sLastOp.length - 1 ; n > 0 ; n--)
		{
			m_sLastOp[n] = m_sLastOp[n-1] ;
		}
		m_sLastOp[0] = sLastOp ;
		m_nTotalOperation++ ;
	}
	
	public void dumpState()
	{
		System.out.println("Total read bytes: " + m_nTotalRead) ;
		System.out.println("Total operations: " + m_nTotalOperation) ;
		
		System.out.println("Last operations (more recent first)") ;
		for(int n = 0 ; n < m_sLastOp.length ; n++)
		{
			if(m_sLastOp[n] != null)
			{
				System.out.println("---------- #" + n + " ----------") ;
				System.out.println(m_sLastOp[n]) ;
			}
		}
		System.out.println("------------------------") ;
	}
}
