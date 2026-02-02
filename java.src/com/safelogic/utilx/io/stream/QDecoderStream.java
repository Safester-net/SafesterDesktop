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

// 27/02/01 18:00 GR - creation from Sun code

import java.io.IOException;
import java.io.InputStream;

import com.safelogic.utilx.AsciiUtility;


/**
 * This class implements a Q Decoder as defined in RFC 2047
 * for decoding MIME headers. It subclasses the QPDecoderStream class.
 * 
 */

public class QDecoderStream extends QPDecoderStream {

    /**
     * Create a Q-decoder that decodes the specified input stream.
     * @param in        the input stream
     */
    public QDecoderStream(InputStream in) {
	super(in);
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
    public int read() throws IOException {
	int c = in.read();

	if (c == '_') // Return '_' as ' '
	    return ' ';
	else if (c == '=') {
	    // QP Encoded atom. Get the next two bytes ..
	    ba[0] = (byte)in.read();
	    ba[1] = (byte)in.read();
	    // .. and decode them
	    try {
		return AsciiUtility.parseInt(ba, 0, 2, 16);
	    } catch (NumberFormatException nex) {
		throw new IOException("Error in QP stream " + nex.getMessage());
	    }
	} else
	    return c;
    }
}

