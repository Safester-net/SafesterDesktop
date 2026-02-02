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

// 03/11/01 11:35 NDP - creation
// 03/11/01 18:55 NDP - tests
// 05/11/01 21:45 NDP - use the time_zone in default ini file 
// 04/02/02 18:10 GR  - use IniProperties instead of SaProperties
// 09/11/01 16:10 NDP - debug march summer hour instead of winter
// 18/10/02 18:45 NDP - Refactor. Suppress references form CmParms

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.safelogic.utilx.Debug;
import com.safelogic.utilx.conf.IniProperties;

/**
 * Use to get the correct local time from the UT (Universal Time)
 * <br>
 * Works for all UE, but tested only for Paris now.
 */

public class LocalTime
{
	/**	The debug flag */
	protected boolean DEBUG = Debug.isSet(this) ;
	
	
    /** the default context ini file */
	public static final String DEFAULT_CONTEXT_INI	= "context_confimail_ie.ini";	
	
	
	/** Times zones */
	//London, England - Edinburgh, Scotland - Dublin, Ireland - Lisbon, Portugal
	public static final String TIME_ZONE_WESTERN = "western";	
	//Amsterdam, Holland - Berlin, Germany - Paris, France - Madrid, Spain
	public static final String TIME_ZONE_CENTRAL = "central";
	//Helsinki, Finland - Athens, Greece - Ankara, Turkey Jerusalem, Israel
	public static final String TIME_ZONE_EASTERN = "eastern";
	
	/** Constants of winter/summer hours */
	public static final int SUMMER_TIME		  = 1;
	public static final int WINTER_TIME		  = 2;
	
	public static final int DATE_SUMMER_MONTH = 03;	
	public static final int DATE_WINTER_MONTH = 10;
	
	/** stores the Universal Time */
	private GregorianCalendar m_gcUT = null;
	
	/** the timz zone of this machine */
	private String m_sTimeZone		 = null;
	
	/**
	 * Constructor
	 * @param	gcUT	date/time in Universal Time
	 */
	public LocalTime(GregorianCalendar gcUT)
		throws IOException
	{
		this.m_gcUT		= (GregorianCalendar) gcUT.clone();
		this.m_sTimeZone	= getTimeZone();
		
		if (this.m_sTimeZone == null)
		{
			this.m_sTimeZone = TIME_ZONE_CENTRAL;
		}
	}

	
	/**
	 * Get the time zone from the default ini file
	 */
	private String getTimeZone()
		throws IOException
	{
		String sTimeZone = null;
		IniProperties props = new IniProperties() ;
		props.load(DEFAULT_CONTEXT_INI) ;
		
		sTimeZone	= props.getString("TIME_ZONE") ;
		return sTimeZone;
	}
	

	/**
	 * Set the Gregorian Calendar to local time form Universal time
	 * @param	gc		date/time in Local(Paris) Time
	 */
	public GregorianCalendar getLocalTimeFromUniversal()
	{
		GregorianCalendar gc = (GregorianCalendar) this.m_gcUT.clone();
		
		int nWinterShift = 0;
		int nSummerShift = 0;
		
		if (this.m_sTimeZone.equalsIgnoreCase(TIME_ZONE_WESTERN))
		{
			nWinterShift = 0;
			nSummerShift = 1;
		}
		else if (this.m_sTimeZone.equalsIgnoreCase(TIME_ZONE_CENTRAL))
		{
			nWinterShift = 1;
			nSummerShift = 2;			
		}
		else if (this.m_sTimeZone.equalsIgnoreCase(TIME_ZONE_EASTERN))
		{
			nWinterShift = 2;
			nSummerShift = 3;			
		}		
		else {
			//in all other cases use Paris time...
			nWinterShift = 1;
			nSummerShift = 2;	
		}
		
		if (getSeasonTime() == WINTER_TIME)
		{
			gc.add(Calendar.HOUR, +nWinterShift); // Winter time
		}
		else if (getSeasonTime() == SUMMER_TIME)
		{
			gc.add(Calendar.HOUR, +nSummerShift); // Summer time
		}
		else {
			throw new IllegalArgumentException("CM : unknown getSeasonTime()");
		}
		
		return gc;			
	}
	
	
	/**
	 * Check if we are in summer time or in winter time
	 * <br>
	 * Rules should apply to all Europe. 
	 * <br>(*but* tested only for Paris)
	 * @return 
	 * <br> WINTER_TIME if we are in winter time
	 * <br> SUMMER_TIME if we are in summer time
	 */
	private int getSeasonTime()
	{
		GregorianCalendar gc = (GregorianCalendar) this.m_gcUT.clone();
		
		int nDate	= gc.get(Calendar.DAY_OF_MONTH);
		int nMonth  = gc.get(Calendar.MONTH) + 1;
		int nHour	= gc.get(Calendar.HOUR);
		
		debugPrintln("nDate  :" + nDate);
		debugPrintln("nMonth :" + nMonth);
		debugPrintln("nHour  :" + nHour);
		
		debugPrintln("DATE_WINTER_MONTH " + DATE_WINTER_MONTH );
		debugPrintln("DATE_SUMMER_MONTH " + DATE_SUMMER_MONTH );
		
		// Easy computing... Winter period
		if ( nMonth > DATE_WINTER_MONTH || nMonth < DATE_SUMMER_MONTH)
		{
			debugPrintln("WINTER_TIME");
			return WINTER_TIME;
		}
		
		// Easy computing... Summer period
		if ( nMonth < DATE_WINTER_MONTH && nMonth > DATE_SUMMER_MONTH)
		{
			debugPrintln("SUMMER_TIME");
			return SUMMER_TIME;
		}		
		
		// Changing period. Ok, let's check what changing month we are...
		if (nMonth == DATE_SUMMER_MONTH)
		{
			debugPrintln("nMonth == DATE_SUMMER_MONTH");
			
			// Summer hour if we are after last sunday of march, else winter
			int nDateLastSundayOfMarch = getDayLastSundayOfMonth(DATE_SUMMER_MONTH);
			
			if (nDate < nDateLastSundayOfMarch)
			{
				return WINTER_TIME;
			}			
			else if (nDate > nDateLastSundayOfMarch)
			{
				return SUMMER_TIME;		
			}
			
			// We are on changing date... Change time is always 01h00 UT 
			if (nHour < 1)
			{
				return WINTER_TIME;			
			}
			else {
				return SUMMER_TIME;					
			}	
		}
		else if (nMonth == DATE_WINTER_MONTH)
		{			
			// Winter hour if we are after last sunday of october, else summer
			int nDateLastSundayOfOctober = getDayLastSundayOfMonth(DATE_WINTER_MONTH);
			
			if (nDate < nDateLastSundayOfOctober)
			{
				return SUMMER_TIME;				
			}
			
			if (nDate > nDateLastSundayOfOctober)
			{
				return WINTER_TIME;				
			}
			
			// We are on changing date... Change time is always 01h00 UT 
			if (nHour < 1)
			{
				return SUMMER_TIME;			
			}
			else {
				return WINTER_TIME;						
			}			
		}		
		
		return 0; // This should not happen
	}
	
	
	/**
	 * Get the day corresponding to first sunday of the month
	 * @param	nMonth	the month from 1 to 12
	 * @return	nDate	the day corresponding to the first sunday
	 */
	private int getDayLastSundayOfMonth(int nMonth)
	{
		int nlastSundayOfMonth = 0;
		
		GregorianCalendar gcWork = (GregorianCalendar)this.m_gcUT.clone();
		
		// Start at 01/03/YYYY
		gcWork.set(gcWork.get(Calendar.YEAR), nMonth - 1, 01);
		
		while (true)
		{
			gcWork.add(Calendar.DATE, +1);
			
			//debugPrintln("gcWork.getTime() :" + gcWork.getTime());
				
			// Ok, exit if we switch from month
			if (gcWork.get(Calendar.MONTH) != nMonth - 1)
			{
				break;
			}
						
			if ( gcWork.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			{
				nlastSundayOfMonth = gcWork.get(Calendar.DATE);
			}
		}
		
		//debugPrintln("nlastSundayOfMonth : " + nlastSundayOfMonth);
			
		return nlastSundayOfMonth;
	}
	
		
	/**
	 * Displays the specified message if DEBUG is set.
	 * @param	sMsg		The message to display
	 */
	protected void debugPrintln(String sMsg)
	{
		if(DEBUG)
			System.out.println("LT.DBG> " + sMsg) ;
	}		
	
	/** main */
	public static void main(String[] args)
		throws Exception
	{
		GregorianCalendar gc = new GregorianCalendar();
		LocalTime lt = new LocalTime(gc);
		
		// Test getDayLastSundayOfMonth()
		System.out.println("getDayLastSundayOfMonth(DATE_WINTER_MONTH) " 
						   + lt.getDayLastSundayOfMonth(DATE_WINTER_MONTH));
				System.out.println("getDayLastSundayOfMonth(DATE_SUMMER_MONTH) " 
						   + lt.getDayLastSundayOfMonth(DATE_SUMMER_MONTH));
						   
	}

}

// End
