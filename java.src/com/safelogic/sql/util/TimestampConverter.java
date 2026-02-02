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
package com.safelogic.sql.util;

// 22/09/00 23:05 NDP - creation

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This is a simple Conversion utility from Timestamp to String and vice versa
 */

public class TimestampConverter
{

	/**
	 * The default constructor.
	 */
	
	public TimestampConverter()
	{
	}
	
	/**
	 * Convert a String date in YYYYMMDD and a hour in HHMMSS 
	 * format to a Timestamp
	 * @param sDate		a date in YYYYMMDD format
	 * @param sHour		a hour in HHMMSS format
	 * @return the corresponding SQL Timestamp
	 */
	
	public Timestamp ToTimestamp(String sDate, String sHour)
	{
		String sYear	= sDate.substring(0,4);		
		String sMonth	= sDate.substring(4,6);		
		String sDay		= sDate.substring(6,8);
			
		String sHH	= sHour.substring(0,2);		
		String sMM	= sHour.substring(2,4);		
		String sSS	= sHour.substring(4,6);
		
		GregorianCalendar gCal = new GregorianCalendar();
		gCal.set(new Integer(sYear).intValue(), 
				 new Integer(sMonth).intValue() -1,
				 new Integer(sDay).intValue());
		
		gCal.set(gCal.HOUR_OF_DAY,	new Integer(sHH).intValue());
		gCal.set(gCal.MINUTE,		new Integer(sMM).intValue());
		gCal.set(gCal.SECOND,		new Integer(sSS).intValue());
		
		java.util.Date dt = gCal.getTime();					
		long lTime = dt.getTime();		
		Timestamp ts=new java.sql.Timestamp(lTime);			
				
		return ts;
	}	

	/**
	 * Convert a String date in YYYYMMDD format to a Timestamp
	 * @param sDate		a date in YYYYMMDD format
	 * @return the corresponding SQL Timestamp
	 */
	
	public Timestamp ToTimestamp(String sDate)
	{
		return this.ToTimestamp(sDate, "000000");
	}	
	
	/**
	 * Convert a Timestamp to a Date in YYYYMMDD format
	 */
	public String toStringDate(Timestamp ts)
	{

		GregorianCalendar gCal = new GregorianCalendar();
		Date dt = new Date();
		dt = new java.util.Date(ts.getTime());
		gCal.setTime(dt);	
	
		return this.GetDateReverse(gCal);
	}
	
	/**
	 * Get the formated date ad YYYYMMDD from a GregorianCalendar
	 * @param   gc		the GregorianCalendar in use
	 * @return  the date as "yyyymmdd"
	 */
	public String GetDateReverse(GregorianCalendar gc)
	{
		String sDate = new String();

		int nDay	= gc.get(Calendar.DAY_OF_MONTH);
		int nMonth  = gc.get(Calendar.MONTH) + 1;

		sDate =  "" + gc.get(Calendar.YEAR);
		
		if (nMonth < 10)
		{
			sDate += "0" + nMonth;
		}
		else {
			sDate += ""  + nMonth;
		}
		
		if (nDay < 10)
		{
			sDate += "0" + nDay;
		}
		else {
			sDate += "" + nDay;
		}
		
		return sDate;
	}	
	
	
	
}
