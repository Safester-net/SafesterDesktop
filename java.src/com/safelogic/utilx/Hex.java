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

public class Hex
{
    private Hex() {} // static methods only

    private static final char[] hexDigits = {
        '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };

    /**
     * Returns a string of hexadecimal digits from a byte array. Each
     * byte is converted to 2 hex symbols.
     * <p>
     * If offset and length are omitted, the whole array is used.
     */
	
    public static String toString(byte[] ba, int offset, int length) {
        char[] buf = new char[length * 2];
        int j = 0;
        int k;

        for (int i = offset; i < offset + length; i++) {
            k = ba[i];
            buf[j++] = hexDigits[(k >>> 4) & 0x0F];
            buf[j++] = hexDigits[ k        & 0x0F];
        }
        return new String(buf);
    }

    public static String toString(byte[] ba) {
        return toString(ba, 0, ba.length);
    }



    /**
     * Returns a byte array from a string of hexadecimal digits.
     */
    public static byte[] fromString(String hex) {
        int len = hex.length();
        byte[] buf = new byte[((len + 1) / 2)];

        int i = 0, j = 0;
        if ((len % 2) == 1)
            buf[j++] = (byte) fromDigit(hex.charAt(i++));

        while (i < len) {
            buf[j++] = (byte) ((fromDigit(hex.charAt(i++)) << 4) |
                                fromDigit(hex.charAt(i++)));
        }
        return buf;
    }


    /**
     * Returns the hex digit corresponding to a number <i>n</i>, from 0 to 15.
     */
    public static char toDigit(int n) {
        try {
            return hexDigits[n];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(n +
                " is out of range for a hex digit");
        }
    }

    /**
     * Returns the number from 0 to 15 corresponding to the hex digit <i>ch</i>.
     */
    public static int fromDigit(char ch) {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        if (ch >= 'A' && ch <= 'F')
            return ch - 'A' + 10;
        if (ch >= 'a' && ch <= 'f')
            return ch - 'a' + 10;

        throw new IllegalArgumentException("invalid hex digit '" + ch + "'");
    }
}
