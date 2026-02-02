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

// 27/02/01 16:25 GR - creation from sun code
// 27/02/01 16:40 GR - use CmASCIIUtility

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import com.safelogic.utilx.AsciiUtility;

/**
 * This class implements a QP Decoder. It is implemented as
 * a FilterInputStream, so one can just wrap this class around
 * any input stream and read bytes from this filter. The decoding
 * is done as the bytes are read out.
 * 
 */

public class QPDecoderStream
	extends FilterInputStream
{
    protected byte[] ba = new byte[2];
    protected int spaces = 0;

	
    /**
     * Create a Quoted Printable decoder that decodes the specified 
     * input stream.
     * @param in        the input stream
     */
	
    public QPDecoderStream(InputStream in)
	{
		super(new PushbackInputStream(in, 2)); // pushback of size=2
    }

	
    /**
     * Read the next decoded byte from this input stream. The byte
     * is returned as an <code>int</code> in the range <code>0</code>
     * to <code>255</code>. If no byte is available because the end of
     * the stream has been reached, the value <code>-1</code> is returned.
     * This method blocks until input data is available, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     */
	
	public int read()
		throws IOException
	{
		if (spaces > 0)
		{
			// We have cached space characters, return one
			spaces--;
			return ' ';
		}
		
		int c = in.read();

		if (c == ' ')
		{ 
			// Got space, keep reading till we get a non-space char
			while ((c = in.read()) == ' ')
				spaces++;

			if (c == '\r' || c == '\n' || c == -1)
			{
				// If the non-space char is CR/LF/EOF, the spaces we got
				// so far is junk introduced during transport. Junk 'em.
				spaces = 0;
			}
			else
			{
				// The non-space char is NOT CR/LF, the spaces are valid.
				((PushbackInputStream)in).unread(c);
				c = ' ';
			}
			return c; // return either <SPACE> or <CR/LF>
		}
		else if (c == '=')
		{
			// QP Encoded atom. Decode the next two bytes
			int a = in.read();

			if (a == '\n')
			{
				/* Hmm ... not really confirming QP encoding, but lets
				* allow this as a LF terminated encoded line .. and
				* consider this a soft linebreak and recurse to fetch 
				* the next char.
				*/
				return read();
			}
			else if (a == '\r')
			{
				// Expecting LF. This forms a soft linebreak to be ignored.
				int b = in.read();
				if (b != '\n') 
					/* Not really confirming QP encoding, but
					* lets allow this as well.
					*/
					((PushbackInputStream)in).unread(b);
				return read();
			}
			else if (a == -1)
			{
				// Not valid QP encoding, but we be nice and tolerant here !
				return -1;
			}
			else
			{
				ba[0] = (byte)a;
				ba[1] = (byte)in.read();
				try
				{
					return AsciiUtility.parseInt(ba, 0, 2, 16);
				}
				catch (NumberFormatException nex)
				{
					/* 27/02/01 GR - remove this strange (non-java?!) code
					System.err.println(
					"Illegal characters in QP encoded stream: " + 
					ASCIIUtility.toString(ba, 0, 2) );
					((PushbackInputStream)in).unread(ba);
					return c;
					*/
					throw new IllegalStateException(
						"Illegal characters in QP encoded stream: " + 
						AsciiUtility.toString(ba, 0, 2)) ;
					

					
				}
			}
		}
		return c;
	}
	

    /**
     * Reads up to <code>len</code> decoded bytes of data from this input stream
     * into an array of bytes. This method blocks until some input is
     * available.
     * <p>
     *
     * @param      buf   the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     */
	
	public int read(byte[] buf, int off, int len)
		throws IOException
	{
		int i, c;
		try 
		{
			for (i = 0; i < len; i++)
			{
				if ((c = read()) == -1)
				{
					if (i == 0) // At end of stream, so we should
						i = -1; // return -1 , NOT 0.
					break;
				}
				buf[off+i] = (byte)c;
			}
		}
		catch (IOException e) { i = -1;}
		return i;
	}

	
    /**
     * Tests if this input stream supports marks. Currently this class
	 * does not support marks
	 */
	
	public boolean markSupported()
	{
		return false;
	}

	
    /**
     * Returns the number of bytes that can be read from this input
     * stream without blocking. The QP algorithm does not permit
     * a priori knowledge of the number of bytes after decoding, so
     * this method just invokes the <code>available</code> method
     * of the original input stream.
     */
	
    public int available()
		throws IOException
	{
		// This is bogus ! We don't really know how much
		// bytes are available *after* decoding
		return in.available();
	}
}
