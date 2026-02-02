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
package com.safelogic.utilx;


// 27/02/01 16:40 GR - creation from Sun code
// 01/03/01 10:25 GR - add isAllUSAscii()
// 21/07/03 12:40 NDP - comments 

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 */

public class AsciiUtility
{
	// Private constructor so that this class is not instantiated
    private AsciiUtility() { }
	
	
	/** 
     * Check if the given string contains non US-ASCII characters.
     * @param	sToCheck	the string to check for ASCII chars
     * @return	true if all characters are US-ASCII, false otherwise
     */
	
	public static boolean isAllUSAscii(String sToCheck)
	{
		int nLen = sToCheck.length();

		for (int i=0; i < nLen; i++)
		{
			if ((int)sToCheck.charAt(i) > 0177) // non-ascii
				return false;
		}
		
		return true ; // all ascii
	}
	
	
    /**
     * Convert the bytes within the specified range of the given byte 
     * array into a signed integer in the given radix . The range extends 
     * from <code>start</code> till, but not including <code>end</code>. <p>
     *
     * Based on java.lang.Integer.parseInt()
     */
    public static int parseInt(byte[] b, int start, int end, int radix)
		throws NumberFormatException {
	if (b == null)
	    throw new NumberFormatException("null");
	
	int result = 0;
	boolean negative = false;
	int i = start;
	int limit;
	int multmin;
	int digit;

	if (end > 0) {
	    if (b[i] == '-') {
		negative = true;
		limit = Integer.MIN_VALUE;
		i++;
	    } else {
		limit = -Integer.MAX_VALUE;
	    }
	    multmin = limit / radix;
	    if (i < end) {
		digit = Character.digit((char)b[i++], radix);
		if (digit < 0) {
		    throw new NumberFormatException(
			"illegal number: " + toString(b, start, end)
			);
		} else {
		    result = -digit;
		}
	    }
	    while (i < end) {
		// Accumulating negatively avoids surprises near MAX_VALUE
		digit = Character.digit((char)b[i++], radix);
		if (digit < 0) {
		    throw new NumberFormatException("illegal number");
		}
		if (result < multmin) {
		    throw new NumberFormatException("illegal number");
		}
		result *= radix;
		if (result < limit + digit) {
		    throw new NumberFormatException("illegal number");
		}
		result -= digit;
	    }
	} else {
	    throw new NumberFormatException("illegal number");
	}
	if (negative) {
	    if (i > 1) {
		return result;
	    } else {	/* Only got "-" */
		throw new NumberFormatException("illegal number");
	    }
	} else {
	    return -result;
	}
    }

    /**
     * Convert the bytes within the specified range of the given byte 
     * array into a signed integer . The range extends from 
     * <code>start</code> till, but not including <code>end</code>. <p>
     */
    public static int parseInt(byte[] b, int start, int end)
		throws NumberFormatException {
	return parseInt(b, start, end, 10);
    }

    /**
     * Convert the bytes within the specified range of the given byte 
     * array into a signed long in the given radix . The range extends 
     * from <code>start</code> till, but not including <code>end</code>. <p>
     *
     * Based on java.lang.Long.parseLong()
     */
    public static long parseLong(byte[] b, int start, int end, int radix)
		throws NumberFormatException {
	if (b == null)
	    throw new NumberFormatException("null");
	
	long result = 0;
	boolean negative = false;
	int i = start;
	long limit;
	long multmin;
	int digit;

	if (end > 0) {
	    if (b[i] == '-') {
		negative = true;
		limit = Long.MIN_VALUE;
		i++;
	    } else {
		limit = -Long.MAX_VALUE;
	    }
	    multmin = limit / radix;
	    if (i < end) {
		digit = Character.digit((char)b[i++], radix);
		if (digit < 0) {
		    throw new NumberFormatException(
			"illegal number: " + toString(b, start, end)
			);
		} else {
		    result = -digit;
		}
	    }
	    while (i < end) {
		// Accumulating negatively avoids surprises near MAX_VALUE
		digit = Character.digit((char)b[i++], radix);
		if (digit < 0) {
		    throw new NumberFormatException("illegal number");
		}
		if (result < multmin) {
		    throw new NumberFormatException("illegal number");
		}
		result *= radix;
		if (result < limit + digit) {
		    throw new NumberFormatException("illegal number");
		}
		result -= digit;
	    }
	} else {
	    throw new NumberFormatException("illegal number");
	}
	if (negative) {
	    if (i > 1) {
		return result;
	    } else {	/* Only got "-" */
		throw new NumberFormatException("illegal number");
	    }
	} else {
	    return -result;
	}
    }

    /**
     * Convert the bytes within the specified range of the given byte 
     * array into a signed long . The range extends from 
     * <code>start</code> till, but not including <code>end</code>. <p>
     */
    public static long parseLong(byte[] b, int start, int end)
		throws NumberFormatException {
	return parseLong(b, start, end, 10);
    }

    /**
     * Convert the bytes within the specified range of the given byte 
     * array into a String. The range extends from <code>start</code>
     * till, but not including <code>end</code>. <p>
     */
    public static String toString(byte[] b, int start, int end) {
	int size = end - start;
	char[] theChars = new char[size];

	for (int i = 0, j = start; i < size; )
	    theChars[i++] = (char)b[j++];
	
	return new String(theChars);
    }

    public static String toString(ByteArrayInputStream is) {
	int size = is.available();
	char[] theChars = new char[size];
	byte[] bytes    = new byte[size];

	is.read(bytes, 0, size);
	for (int i = 0; i < size;)
	    theChars[i] = (char)bytes[i++];
	
	return new String(theChars);
    }


    public static byte[] getBytes(String s) {
	char [] chars= s.toCharArray();
	int size = chars.length;
	byte[] bytes = new byte[size];
    	
	for (int i = 0; i < size;)
	    bytes[i] = (byte) chars[i++];
	return bytes;
    }

    public static byte[] getBytes(InputStream is) throws IOException {

	int len;
	int size = 1024;
	byte [] buf;


	if (is instanceof ByteArrayInputStream) {
	    size = is.available();
	    buf = new byte[size];
	    len = is.read(buf, 0, size);
	}
	else {
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    buf = new byte[size];
	    while ((len = is.read(buf, 0, size)) != -1)
		bos.write(buf, 0, len);
	    buf = bos.toByteArray();
	}
	return buf;
    }
	
	//Rule 8: Make your classes noncloneable
	public final Object clone() throws java.lang.CloneNotSupportedException {
		throw new java.lang.CloneNotSupportedException();
	}

	//Rule 9: Make your classes nonserializeable
	private final void writeObject(ObjectOutputStream out)
		throws java.io.IOException {
		throw new java.io.IOException("Object cannot be serialized");
	}

	//Rule 10: Make your classes nondeserializeable
	private final void readObject(ObjectInputStream in)
		throws java.io.IOException {
		throw new java.io.IOException("Class cannot be deserialized");
	}
}


