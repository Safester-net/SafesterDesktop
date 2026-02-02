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


// 28/12/00 11:25 GR - creation
// 28/12/00 11:50 GR - OK, tested
// 16/01/01 15:50 GR - javadoc
// 16/10/01 17:05 GR - removed test code is commented using "//"

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * This class is a tool to format a java Date as a String representation
 * in the E-mail format (RFC 822).
 */

public class LightMailDateFormat
{
	/** The format to use for the SimpleDateFormat */ 
	private String m_sFormat ;

	/** The locale to use for the SimpleDateFormat */ 
	private Locale m_loc ;


	/** The SimpleDateFormat to format the Date */ 
	private SimpleDateFormat m_sdf ;


	/**
	 * Creates a new formater with the default e-mail format
	 * and locale.<br>
	 * The default format is: "Mon, 15 Jan 2001 10:49:21 +0100"
	 */

	public LightMailDateFormat()
	{
		this("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);
	}


	/**
	 * Creates a new formater with the specified e-mail format
	 * and locale.<br>
	 * 
	 * @param	sFormat		The format to use for formating
	 * @param	loc			The locale to use for formating
	 */

	private LightMailDateFormat(String sFormat, Locale loc)
	{
		m_sFormat = sFormat ;
		m_loc = loc ;
		m_sdf = new SimpleDateFormat(m_sFormat, m_loc) ;
	}


	/**
	 * Formats the given date as a valid email date.
	 * 
	 * @param	dDate	the date to format
	 * @return			the formated String representation of the Date
	 */

	public String format(Date dDate)
	{
		String sFormatedDate = m_sdf.format(dDate) ;
		sFormatedDate += " " ;
		Calendar calendar = m_sdf.getCalendar() ;
		
		// set the timezone to +HHMM or -HHMM
		calendar.clear();
		calendar.setTime(dDate);
		
		int nDayOfWeek	= calendar.get(Calendar.DAY_OF_WEEK);
		int nDay		= calendar.get(Calendar.DAY_OF_MONTH);
		int nMonth		= calendar.get(Calendar.MONTH);
		int nYear		= calendar.get(Calendar.YEAR);
		
		TimeZone tz = calendar.getTimeZone();
		int nOffset = tz.getOffset(calendar.get(Calendar.ERA), 
								   nYear, nMonth, nDay, nDayOfWeek, 
								   calendar.get(Calendar.MILLISECOND));
		// take care of the sign
		if (nOffset < 0)
		{
			sFormatedDate += "-" ;
			nOffset = (-nOffset);
		}
		else
		{
			sFormatedDate += "+" ;
		}
		
		int nRawOffsetInMins = nOffset / 60 / 1000; // offset from GMT in mins
		int nOffsetInHrs = nRawOffsetInMins / 60;
		int nOffsetInMins = nRawOffsetInMins % 60;

		sFormatedDate += Character.forDigit((nOffsetInHrs/10), 10) ;
		sFormatedDate += Character.forDigit((nOffsetInHrs%10), 10) ;
		sFormatedDate += Character.forDigit((nOffsetInMins/10), 10) ;
		sFormatedDate += Character.forDigit((nOffsetInMins%10), 10) ;
	
		return sFormatedDate;
	}
	
	
	//public static void main(String args[])
	//{
	//	System.out.println("<test>") ;
	//	Date dDate = new Date() ;
	//	LightMailDateFormat lmdf = new LightMailDateFormat() ;
	//	System.out.println("Date: " + dDate.toString()) ;
	//	System.out.println("Date: " + lmdf.format(dDate)) ;
	//	System.out.println("</test>") ;
	//}
	
	
	/*
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
	*/
}
