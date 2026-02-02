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

public class ArrayMgr
{
	/**
     * Return a byte array from char array
     * 
     * @param  charArray    a character array
     * @return a byte array
     */
    
    public static byte [] getBytesFromChars(char [] charArray)
    {
        int size = charArray.length;

        byte[] byteArray = new byte[size];
        
        for (int i = 0; i < size;)
        {
            byteArray[i] = (byte) charArray[i++];        
        }    
    
        return byteArray;
    }
    
    /**
     * Return a byte array from char array
     * 
     * @param  charArray    a character array
     * @return a byte array
     */
    
    public static char [] getCharsFromBytes(byte [] byteArray)
    {
        int size = byteArray.length;

        char [] charArray  = new char[size];
        
        for (int i = 0; i < size;)
        {
            charArray[i] = (char) byteArray[i++];        
        }    
    
        return charArray;
    }    
    
    
    
	/**
	 * compare two bytes arrays
	 * 
	 * @param	b1	1st array to compare
	 * @param	b2	2nd array to compare
	 * 
	 * @return	true if arrays are equal, false otherwise
	 */
	public static boolean CompareByte(byte b1[], byte b2[])
	{
		if(b1.length != b2.length)
		{
			return false ;
		}
		
		 String s1 = new String(b1) ;
		 String s2 = new String(b2) ;
		 
		 if(s1.hashCode() == s2.hashCode())
			 return true ;
		 else
			 return false ;
	}
	
	/**
	 * XOR two 8 bytes long bytes.
	 * 
	 * @param	b1	input byte b1
	 * @param	b2	input byte b2
	 * 
	 * @return  b1  with b1 = b1 XOR b2 
	 */
	
	public static byte[] XOR8bytes(byte[] b1, byte[] b2)
	{
		for (int i =0; i<8; i++)
		{
		    int n1 = new Byte(b1[i]).intValue();
			int n2 = new Byte(b2[i]).intValue();
			
			n1 = n1 ^ n2;
			b1[i] = new Integer(n1).byteValue();
		}
		
		return b1;
	}
	
	/**
	 * Concatenate two byte arrays together.
	 * 
	 * @b1	first byte array
	 * @b2  second byte array
	 * 
	 * @return new byte array of b1+B2
	 */
	
	public static byte[] AddByte(byte b1[], byte b2[])
	{
		byte [] bTotal = new byte[b1.length + b2.length];
		int j = 0;
		
		for (int i = 0; i < b1.length; i++)
		{
			bTotal[i]= b1[i];
			j++;
		}
	
		for (int i = 0; i < b2.length; i++)
		{
			bTotal[i+j]= b2[i];
		}
				
		return bTotal;
	}
	
	/**
	 * Extract nLength 1st bytes from bBuffer
	 * 
	 * @param	bBuffer		buffer with bytes to extract
	 * @param	nLength		number of bytes to extract
	 */
	public static byte[] ExtractByte(byte bBuffer[], int nLength)
	{
		return ExtractByte(bBuffer, 0, nLength) ;
	}
	
	
	/**
	 * Extract bytes from buffer between nStart (inclusive) and nEnd (exclusive)
	 * 
	 * @param	bBuffer		buffer with bytes to extract
	 * @param	nStart		starting position (inclusive)
	 * @param	nEnd		ending position (exclusive)
	 */
	public static byte[] ExtractByte(byte bBuffer[], int nStart, int nEnd)
	{
		byte[] bNew = new byte[nEnd - nStart] ;
		
		for(int i = nStart ; i< nEnd ; i++)
		{
			bNew[i-nStart] = bBuffer[i] ;
		}
		
		return bNew ;
	}	
	

} // End CSaArray.java
