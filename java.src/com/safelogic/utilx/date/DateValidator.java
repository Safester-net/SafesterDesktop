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
package com.safelogic.utilx.date;

// 23/02/02 17:50 NDP - creation
// 23/02/02 18:15 NDP - tests
// 28/04/05 15:55 NDP - DateValidator() new constructor with int values

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Use it to check a String date format and to find the "closest" date to
 * an invalid date because of 31 for a bad month, or 30 february, etc.
 * <br>
 * Useful when date is user inputed with 3 listbox from a web mask
 */

public class DateValidator
{
	/** the calendar to use as validator */
	private GregorianCalendar m_gCal = null;
	
	/** The year a YYYY String after validation */
	private String m_sYear = null;
	
	/** The Month as MM String after validation */
	private String m_sMonth = null;	
	
	/** The Day as DD String after validation */
	private String m_sDay = null;		
	
    
    /**
     * The default constructor.
     * @param sYear     year as YYYY
     * @param sMonth    month as MM
     * @param sDay      day   as DD
     */
    
    public DateValidator(int Year, int nMonth, int nDay)
    {       
        m_gCal = new GregorianCalendar();
        m_gCal.set(Year, nMonth -1, nDay);     
        
        this.formatDate();
    }
    
	/**
	 * The constructor for String
	 * @param sYear		year as YYYY
	 * @param sMonth	month as MM
	 * @param sDay		day   as DD
	 */
	
	public DateValidator(String sYear, String sMonth, String sDay)
	{		
		m_gCal = new GregorianCalendar();
		m_gCal.set(new Integer(sYear).intValue(), 
				 new Integer(sMonth).intValue() -1,
				 new Integer(sDay).intValue());		
		
		this.formatDate();
	}
	
	
	/**
	 * The default constructor.
	 * @param sDate		Date as YYYYMMDD
	 */
	
	public DateValidator(String sDate)
	{
		String sYear	= sDate.substring(0,4);		
		String sMonth	= sDate.substring(4,6);		
		String sDay		= sDate.substring(6,8);
			
		m_gCal = new GregorianCalendar();
		m_gCal.set(new Integer(sYear).intValue(), 
				 new Integer(sMonth).intValue() -1,
				 new Integer(sDay).intValue());	
		
		this.formatDate();
	}
	
    
	/**
	 * Format the Year, Month & Day after validation
	 */
	private void formatDate()
	{
		int nYear   = this.m_gCal.get(Calendar.YEAR);
		int nMonth  = this.m_gCal.get(Calendar.MONTH) + 1;
		int nDay	= this.m_gCal.get(Calendar.DAY_OF_MONTH);
		
		this.m_sYear = "" + nYear;
			
		if (nMonth < 10)
		{
			this.m_sMonth = "0" + nMonth;
		}
		else {
			this.m_sMonth = ""  + nMonth;
		}			
					
		if (nDay < 10)
		{
			this.m_sDay = "0" + nDay;
		}
		else {
			this.m_sDay = "" + nDay;
		}		
		
	}
	

	/**
	 * Get the "right" formated Date as YYYYMMDD, after checking with a Gregorian Calendar
	 * @return  the Date as YYYYMMDD
	 */
	public String getDateFormated()
	{
		return this.m_sYear + this.m_sMonth + this.m_sDay;
	}	
	
	/**
	 * Get the "right" formated Year as YYYY, after checking with a Gregorian Calendar
	 * @return  the Year as YYYY
	 */
	public String getYearFormated()
	{
		return this.m_sYear;
	}	
	
	/**
	 * Get the "right" formated Month as MM, after checking with a Gregorian Calendar
	 * @return  the Month as MM
	 */
	public String getMonthFormated()
	{
		return this.m_sMonth;
	}		
	
	/**
	 * Get the "right" formated Day as DD, after checking with a Gregorian Calendar
	 * @return  the Day as DD
	 */
	public String getDayFormated()
	{
		return this.m_sDay;
	}		
	
}

// End
