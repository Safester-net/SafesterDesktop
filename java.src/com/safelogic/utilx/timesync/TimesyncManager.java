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
package com.safelogic.utilx.timesync;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.safelogic.utilx.Debug;

/**
 * Defines methods to set the ConfiMail Server exact date & time
 * <br>Class is abstract because implementations depends on differents
 * "Time Servers".
 * <br>default implementation uses us navy official time server with
 * TimesyncNavyMil.
 */

public abstract class TimesyncManager
{
	/**	The debug flag */
	protected boolean DEBUG = Debug.isSet(this) ;
		
	/**
	 * Constants to define the allowed date time server
	 */	
	public static final int NAVY_MIL_TIME_SERVER = 0; // US Navy 
	public static final int TIME_GOV_TIME_SERVER = 1; // US Governement official time
	public static final int BIPM_FR_TIME_SERVER	 = 2; // Bureau International des Poids 
													 // et Mesures.
	
	/**
	 * Defined as protected to give no visible access.
	 */
	protected TimesyncManager()
	{
	}
	
	/**
	 * Gets an initialized instance of the TimesyncManager to the
	 * default Time Server US_NAVY_TIME_SERVER
	 * @return a Time Manager Instance 
	 */
	
	public static final TimesyncManager getInstance()
	{	
		return getInstance(NAVY_MIL_TIME_SERVER);
	}	
	
	/**
	 * Gets an initialized instance of the TimesyncManager to a 
	 * specified Server 
	 * @param nDatetimeServer	the date/time server to log in :
	 * <br>US_NAVY_TIME_SERVER
	 * <br>US_GOV_TIME_SERVER
	 * <br>BIPM_TIME_SERVER
	 * @return a Time Manager Instance 
	 */
	
	public static final TimesyncManager getInstance(int nDatetimeServer)
	{
		// Only Navy implementation for now...
		if (nDatetimeServer != NAVY_MIL_TIME_SERVER)
		{
			throw new IllegalArgumentException("CM: Not implemented yet");
		}
		
		TimesyncManager tmInstance =  new TimesyncNavyMil();
		return tmInstance ;
	}
	
	/**
	 * Set the URL to access
	 * <br>overseed the real Navy Url.
	 * Main purpose is debug
	 * @param	sUrl	the URL to use
	 */
	public abstract void setServerUrl(String sUrl);
	
	/**
	 * Return a Gregorian Calendar set with the Date/Time of the Time Server
	 * @return the Date/Time defined by the time servers as a GregorianCalendar
	 */
	public abstract GregorianCalendar getServerDatetimeValue()
		throws IOException;	
	
	/**
	 * Set a date time value on a platform using a GregorianCalendar 
	 * @param gc	The Gregorian Calendar updated with date/time
	 */
	// FUTUR USAGE
	//public abstract void setDatetime(GregorianCalendar gc)
	//	throws Exception ;

	/**
	 * Set the Gregorian Calendar to local time form Universal time
	 * @param	gcUT	date/time in Universal Time
	 * @param	gc		date/time in Local(Paris) Time
	 */
	public GregorianCalendar getLocalTimeFromUniversal(GregorianCalendar gcUT)
		throws IOException
	{
		LocalTime lt = new LocalTime(gcUT);
		return lt.getLocalTimeFromUniversal();
	}
	
	
	/**
	 * Set a date time value on a Linux platform
	 * <br>The format of the command passed to Linux is :
	 * "date  MMDDHHMMYYYY.SS"
	 * <br>See date man on Linux for more infos
	 * @param gc	The Gregorian Calendar at Universal Time updated with date/time
	 */
	
	public void  setLinuxDate(GregorianCalendar gc)
		throws IOException, IllegalAccessException, InterruptedException
	{					
		String sDateCommand = "/bin/date ";
		String sDateParam	= null;
	
		// Build the "MMDDHHMMYYYY.SS" String
		sDateParam = addLeadingZero(gc.get(Calendar.MONTH) + 1);
		sDateParam+= addLeadingZero(gc.get(Calendar.DAY_OF_MONTH));
		sDateParam+= addLeadingZero(gc.get(Calendar.HOUR_OF_DAY));	
		sDateParam+= addLeadingZero(gc.get(Calendar.MINUTE));	
		sDateParam+= addLeadingZero(gc.get(Calendar.YEAR));
		sDateParam+= ".";
		sDateParam+= addLeadingZero(gc.get(Calendar.SECOND));	
		
		sDateCommand+=sDateParam;
		
		Runtime.getRuntime().exec(sDateCommand) ;
					
	}
	
	/**
	 * Set a date time value on a Windows platform
	 * <br>The format of the command passed to Linux is :
	 * "date  dd-mm-yy"
	 * "time  hh:mm:ss"
	 * <br>See "date" and "time" on Windows for more infos
	 * @param gc	The Gregorian Calendar at Universal Time updated with date/time
	 */
	
	public void  setWindowsDate(GregorianCalendar gc)
		throws IOException, IllegalAccessException, InterruptedException
	{			
		//CmShell shell = new CmShell();		
		//String sResult		= null;
		
		String sCommand = "date ";
	
		// Build the "date DD-MM-YYYY" date command
		sCommand+= addLeadingZero(gc.get(Calendar.DAY_OF_MONTH));
		sCommand+= "-" + addLeadingZero(gc.get(Calendar.MONTH) + 1);
		sCommand+= "-" + addLeadingZero(gc.get(Calendar.YEAR));		
		
		Runtime.getRuntime().exec(sCommand) ;
		
		debugPrintln(sCommand);
		
		// Build the "time HH:MM:SS" date command		
		sCommand = "time ";
		sCommand+= addLeadingZero(gc.get(Calendar.HOUR_OF_DAY));	
		sCommand+= ":" + addLeadingZero(gc.get(Calendar.MINUTE));	
		sCommand+= ":" + addLeadingZero(gc.get(Calendar.SECOND));		
		
		Runtime.getRuntime().exec(sCommand) ;
		
		debugPrintln(sCommand);
		
		//sResult = shell.getResult(sCommand);		
		//
		//if (sResult == null || sResult.length() == 0)
		//	throw new IllegalAccessException("CM: impossible to set the Linux date");		
		//
		//return sResult;		
	}
	

	/**
	 * add a leading zero to numeric componant and return as a String
	 * @param	the numeric value
	 * @return  the corresponding String value, with a lading 0 if < 10
	 */
	private String addLeadingZero(int nNumber)
	{
		String sNumber = null;
		
		if (nNumber < 10)
		{
			sNumber = "0" + nNumber;
		}
		else {
			sNumber = "" + nNumber;
		}
		
		return sNumber;
	}
	
	/**
	 * Displays the specified message if DEBUG is set.
	 * @param	sMsg		The message to display
	 */
	protected void debugPrintln(String sMsg)
	{
		if(DEBUG)
			System.out.println("TS.DBG> " + sMsg) ;
	}		

}

// End
