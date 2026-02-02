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


// 29/11/00 18:35 GR - creation
// 29/11/00 18:45 GR - OK
// 29/12/00 22:05 GR - add debug in copy()
// 16/01/01 18:00 GR - javadoc
// 03/10/01 13:05 GR - add CopyReport mechanism
// 03/10/01 13:50 GR - OK: CopyReport mechanism
// 25/04/02 12:25 GR - add copy(InputStream , OutputStream , boolean)
// 03/05/05 11:55 NDP- comments

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.safelogic.utilx.Debug;


/**
 * This class performs a stream copy.
 * Use it to copy inputStream into OutputStream.
 */

public class StreamCopier
{
	private boolean DEBUG = Debug.isSet(this) ;
	
	/**	The default buffer length to use for copy */
	public static final int DEFAULT_BUF_LEN = 2048 ;
	
	/**	The currently used buffer length */
	private int m_nBufLen ;
	
	/**	The copy report */
	private CopyReport m_crCopyReport ;
	
	/**
	 * Simple constructor. Uses the default buffer length.
	 */
	
	public StreamCopier()
	{
		this(DEFAULT_BUF_LEN) ;
	}
	
	
	/**
	 * This constructors allows to set the buffer length to use while copying.
	 * 
	 * @param	nBufLen		the buffer length to use while copying
	 */
	
	public StreamCopier(int nBufLen)
	{
		m_nBufLen = nBufLen ;
	}
	
	/**
	 * Gets the currently used copy buffer length.
	 * 
	 * @return	the buffer length used
	 */
	
	public int getBufferLength()
	{
		return m_nBufLen ;
	}
	
	
	/**
	 * Sets the copy report to use when copying.
	 * @param	crCopyReport	the copy report to use when copying
	 */
	
	public void setCopyReport(CopyReport crCopyReport)
	{
		m_crCopyReport = crCopyReport ;
	}
	
	/**
	 * Copies the content of the InputStream to the OutputStream.
	 * It returns the amount of copied bytes.
	 * <br>
	 * Note: this method flushes the OutputStream before it returns
	 * to make sure that all the bytes have been written.<br>
	 * Use copy() of three arguments if you need to set the flushOutput
	 * according to your needs.
	 * 
	 * @param	isIn	the InputStream to copy
	 * @param	osOut	the OutputStream to copy to
	 * 
	 * @return	the amount of copied bytes
	 */
	
	public int copy(InputStream isIn,
					OutputStream osOut)
		throws IOException
	{
		return copy(isIn, osOut, true) ;
	}
	
	/**
	 * Copies the content of the InputStream to the OutputStream.
	 * It returns the amount of copied bytes.
	 * <br>
	 * Note: If flushOutput is set, this method flushes the OutputStream
	 * before it returns to make sure that all the bytes have been written.
	 * 
	 * @param	isIn		the InputStream to copy
	 * @param	osOut		the OutputStream to copy to
	 * @param	flushOutput	true to flush the OutputStream at the end,
	 *						false otherwise
	 * 
	 * @return	the amount of copied bytes
	 */
	
	public int copy(InputStream isIn,
					OutputStream osOut,
					boolean flushOutput)
		throws IOException
	{
		int nCopied = 0 ;
		byte bBuffer[] = new byte[m_nBufLen] ;
		int nRead = 0 ;
		
		debugPrintln("bBuffer.length=" + bBuffer.length) ;
		
		while( (nRead = isIn.read(bBuffer)) > 0 )
		{
			osOut.write(bBuffer, 0, nRead) ;
			nCopied += nRead ;
			if(m_crCopyReport != null)
				m_crCopyReport.reportCopiedBytes(nCopied) ;
			debugPrint(".") ;
		}
		if(flushOutput)
		{
			osOut.flush() ;
		}
		debugPrintln("") ;
		debugPrintln("nCopied=" + nCopied) ;
		
		return nCopied ;
	}
    
	
	/**
	 * This proceeds as copy() but also closes the Sreams before returning.
	 * 
	 * @param	isIn	the InputStream to copy
	 * @param	osOut	the OutputStream to copy to
	 * 
	 * @return	the amount of copied bytes
	 */
	
	public int copyAndClose(InputStream isIn,
							OutputStream osOut)
		throws IOException
	{
		int nCopied = copy(isIn, osOut) ;
		isIn.close() ;
		osOut.close() ;
		return nCopied ;
	}
	
	
	/**
	 * Displays the specified message if DEBUG is set.
	 * @param	sMsg		The message to display
	 */
	
	private void debugPrintln(String sMsg)
	{
		if(DEBUG)
			System.out.println("SC.DBG> " + sMsg) ;
	}
	
	/**
	 * Displays (without line feed) the specified message if DEBUG is set.
	 * @param	sMsg		The message to display
	 */
	
	private void debugPrint(String sMsg)
	{
		if(DEBUG)
			System.out.println(sMsg) ;
	}
	
	/**
	 * Rule 8: Make your classes noncloneable
	 * @return	not reached
	 * @exception	java.lang.CloneNotSupportedException		
	 */
	
	//Rule 8: Make your classes noncloneable
	public final Object clone() throws java.lang.CloneNotSupportedException {
		throw new java.lang.CloneNotSupportedException();
	}

	
	/**
	 * Rule 9: Make your classes nonserializeable
	 * @param	out		the stream for serialization
	 * @exception	java.io.IOException		
	 */
	
	//Rule 9: Make your classes nonserializeable
	private final void writeObject(ObjectOutputStream out)
		throws java.io.IOException {
		throw new java.io.IOException("Object cannot be serialized");
	}

	
	/**
	 * Rule 10: Make your classes nondeserializeable
	 * @param	in	the stream for deserialization	
	 * @exception	java.io.IOException		
	 */
	
	//Rule 10: Make your classes nondeserializeable
	private final void readObject(ObjectInputStream in)
		throws java.io.IOException {
		throw new java.io.IOException("Class cannot be deserialized");
	}
}
