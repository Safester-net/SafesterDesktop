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

/**
 * This class provides two static services to convert:
 * <br>- an int to a 4-byte long byte array;
 * <br>- a 4-byte long byte array to an int.
 * <br>
 * This class has been declared final and has a private constructor
 * so it is only available as a static utility.
 */

public final class BytesIntConverter
{
	/**
	 * Private constructor to forbid class instanciation.
	 */
	private BytesIntConverter()
	{
		
	}
	
	
	/**
	 * Command line entry point. Parses argument and calls Test().
	 */
	
	public static void main(String args[])
	{
		if(args.length != 1)
		{
			System.out.println("usage: [int_value|'all']") ;
			System.exit(0) ;
		}
		
		int nInc = (int)Math.pow(2, 16) ;
		char[] c32zeros = new String("00000000000000000000000000000000").toCharArray() ;
		
		if(args[0].equalsIgnoreCase("all"))
		{
			for(int n = Integer.MIN_VALUE ; n <= Integer.MAX_VALUE ; n++)
			{
				if(n % nInc == 0)
				{
					StringBuffer sbBuffer = new StringBuffer();
					sbBuffer.append(Integer.toBinaryString(n)) ;
					sbBuffer.insert(0, c32zeros, 0, 32 - sbBuffer.length()) ;
					sbBuffer.append(" (") ;
					sbBuffer.append(n) ;
					sbBuffer.append(")") ;
					
					System.out.println(sbBuffer.toString()) ;
				}
				
				if(!Test(n, false))
				{
					
					System.out.println("failed at: " + n) ;
					break ;
				}
				
				if(n == Integer.MAX_VALUE)
				{
					System.out.println("Test successfull. The End") ;
					break ;
				}
			}
			return ;
		}
		
		
		int nIn = 0 ;
		try{
			nIn = Integer.parseInt(args[0]) ;
		}
		catch(Exception eAll)
		{
			System.out.println("Invalid value for <int> argument") ;
			System.exit(0) ;
		}
		System.out.println("Test: " +  (Test(nIn, true)?"OK":"Failed")) ;
	}
	
	
	/**
	 * Tests the conversion methods using the given int.
	 * @param	nIn		the int to use for tests
	 * @param	disp	true to display result, false to mute it
	 * @return	true if the test is successfull
	 *			(i.e. BytesToInt(IntToBytes(nIn)) == nIn),
	 *			false otherwise
	 */
	
	public static boolean Test(int nIn, boolean disp)
	{
		byte bIn[] = intToBytes(nIn) ;
		int nOut = bytesToInt(bIn) ;
		if(disp)
		{
			System.out.println("Input int                   = " + nIn) ;
			System.out.println("Converted in byte[4] (hexa) = " + Hex.toString(bIn)) ;
			System.out.println("Converted in int            = " + nOut) ;
		}
		return nIn == nOut ;
	}
	
	
	/**
	 * Converts an int into a 4-byte long byte array.<br>
	 * Don't rely on it to do math conversions. It's only a "way"
	 * of seing the 32 bits of an int as 4 bytes.
	 * I didn't check if it is Java way.
	 * <br><br>
	 * How I "see" the bits in the int :<br>
	 * - sign codes for the first bit of the first byte (- = 1, + = 0);<br>
	 * - left bits are heavier than right ones.<br>
	 * 
	 * @param	nValue	the int value to convert
	 * @return	the byte array (4-byte long)
	 */
	
	public static byte[] intToBytes(int nValue)
	{
		// keep track of the sign (it won't be available later)
		boolean negative = (nValue < 0) ;
		
		if(negative)
			nValue++ ;
		
		// make nValue positive
		nValue = Math.abs(nValue) ;
		
		// create the byte array
		byte bReturn[] = new byte[4] ;
		
		// fill all cells of the byte array, starting "at right hand"
		for(int i = 3 ; i >= 0 ; i--)
		{
			// create a new byte
			byte b = 0 ;
		//	System.out.println("i=" + i) ;
			
			// iterate through the 8 bits that makes this byte
			for(int j = 0 ; j < 8 ; j++)
			{
				
			//	System.out.println(Integer.toBinaryString(nValue)) ;
			//	System.out.println(CSaHex.toString(bReturn)) ;
				
				// shift "output" buffer to the right
				b >>= 1 ;
				// erase the left bit (the new one from shift)
				b &= 0x7F ;			// mask value for beotians: 0111 1111
				
				// skip the last bit ("at left hand"): it is sign
				if(i == 0 && j == 7)
					break ;
				
				// set the left bit to 1 if it is its value
				if(nValue % 2 == 1)
				{
			//		System.out.println("insert 1") ;
					b += 0x80 ;		// mask value for beotians: 1000 0000
				}
				// shift nValue to the right (i.e. divide it by 2)
				nValue /= 2 ;
			}
			// set the byte in the array
			bReturn[i] = b ;
		}
		
		// set the last left bit if the input value is negative
		if(negative)
			bReturn[0] += 0x80 ;	// mask value for beotians: 1000 0000
		
		// return the byte array
		return bReturn ;
	}
	
	
	/**
	 * Converts a 4-byte long byte array into an int.<br>
	 * Don't rely on it to do math conversions. It's only a "way"
	 * of seing 32 bits as an int. I didn't check if it is Java way.
	 * <br><br>
	 * How I "see" the bits :<br>
	 * - 1st bit is sign bit (- = 1, + = 0);<br>
	 * - left bits are heavier than right ones;<br>
	 * - I use a Horner-like algorithm.<br>
	 * 
	 * @param	bValue	the byte array (must be 4-byte long)
	 * @return	the int value of the specified bits
	 */
	
	public static int bytesToInt(byte bValue[])
		throws ArrayIndexOutOfBoundsException
	{
		// return value		
		int nReturn = 0 ;
		
		// test if there is 32 bits (4 bytes)
		if(bValue.length != 4)
			throw new ArrayIndexOutOfBoundsException() ;
		
		
		// "Horner algorithm" with radix=2
		
		// scan the 4 bytes
		for(int nCnt = 0 ; nCnt < 4 ; nCnt++)
		{
			// get current byte to scan
			byte bTmp = bValue[nCnt] ;
			
			// scan byte bit after bit
			for(int nIdx=7 ; nIdx >= 0 ; nIdx--)
			{
				// skip sign bit (bValue[0][0])
				if(nCnt==0 && nIdx==7)
				{
					bTmp <<= 1 ;
					continue ;
				}
				
				// check current bit value
				if((bTmp & 0x80 ) != 0)
				{
					// if it's 1, then sum it with return value
					
					int nExp = nIdx + ((bValue.length-1) - nCnt ) * 8 ;
					
					nReturn += java.lang.Math.pow(2,nExp) ;
					
				}
					
				// move bits to the left side
				bTmp <<= 1 ; 
			}
		}
		
		// add sign if necessary
		if( (bValue[0] & 0x80) != 0 )
		{
			nReturn *= -1 ;
			nReturn-- ;
		}
		
		// return value
		return nReturn ;
	}
}
