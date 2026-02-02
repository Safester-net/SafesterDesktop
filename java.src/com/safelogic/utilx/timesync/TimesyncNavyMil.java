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
import java.io.InputStream;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import com.safelogic.utilx.Debug;
import com.safelogic.utilx.io.stream.LineInputStream;

/**
 * Implements a concrete TimesyncManager.
 * <br>
 * uses the http://tycho.usno.navy.mil/cgi-bin/timer.pl request CGI
 * which has an output containing the HTML reference line in following format:
 * OLD FORMAT "Nov. 1, 2001, 11:12:47 Universal Time"
 * NEW FORMAT "Jan. 10, 17:55:07 UTC" 
 */

public class TimesyncNavyMil extends TimesyncManager
{
	
	/**	The debug flag */
	protected boolean DEBUG = Debug.isSet(this) ;	

	/** The URL which serves the date/time value*/
	public final static String URL_NAVY_MIL 
		= "http://tycho.usno.navy.mil/cgi-bin/timer.pl";
	
	/** The URL which serves the date/time value*/
	public final static String URL_NAVY_MIL_EMUL 
		= "http://localhost/cm/navy.mil.html";	
	
	/** The HTML result page line date/time line tag */
	//private static final String UNIVERSAL_TAG	= "Universal";
	//private static final String TIME_TAG		= "Time";
	private static final String UTC_TAG	= "UTC";
	
	// instances var
	private String m_sUrl = null;
	
	/**
	 * Constructor
	 */
	public TimesyncNavyMil()
	{
		this.m_sUrl = URL_NAVY_MIL;
	}
	
	/**
	 * Set the URL to access
	 * <br>overseed the real Navy Url.
	 * Main purpose is debug
	 * @param	sUrl	the URL to use
	 */
	public void setServerUrl(String sUrl)
	{
		this.m_sUrl = sUrl;
	}
	
	
	/**
	 * Return a Gregorian Calendar set with the Date/Time of the Time Server
	 * @return the Date/Time defined by the time servers as a GregorianCalendar
	 */
	public GregorianCalendar getServerDatetimeValue()
		throws IOException
	{
		// 1) Connect to Navy.mil date/page time and get the date/time line
		String sDatetimeLine = this.getUrlDatetimeLine();
		
		// 2) Parse the date/time line to extract the date/time values
		sDatetimeLine = sDatetimeLine.substring(4);
		sDatetimeLine = sDatetimeLine.substring(0, sDatetimeLine.indexOf(UTC_TAG));
		
		debugPrintln("sDatetimeLine :" + sDatetimeLine + ":");
				
		StringTokenizer stDateToken = new StringTokenizer(sDatetimeLine, " .,:") ;
		int nTokens = stDateToken.countTokens();
		
		String[] sDate= new String[nTokens];
		
		int i=0;
		while(stDateToken.hasMoreTokens())
		{
			sDate[i] = stDateToken.nextToken().trim() ;
			debugPrintln( i + " :" + sDate[i] + ":");
			i++;
		}		
		
		// OLD FORMAT
		//sDatetimeLine :Nov. 1, 2001,   18:30:15   :
		//sDate[0] :Nov:
		//sDate[1] :1:
		//sDate[2] :2001:
		//sDate[3] :18:
		//sDate[4) :30:
		//sDate[5) :15:		
		
		// NEW FORMAT
		//sDatetimeLine "Jan. 10, 17:55:07 UTC" 
		//sDate[0] :Jan:
		//sDate[1] :10:
		//sDate[2] :17:
		//sDate[3] :55:
		//sDate[4) :07:	
		
		GregorianCalendar gc = new GregorianCalendar();
		
		// Transform each array componant to an Gregorian Calendar entry parm
		int nMonth	= getMonthNumber(sDate[0]);
		int nDate	= Integer.valueOf(sDate[1]).intValue();
		int nYear	= gc.get(GregorianCalendar.YEAR);
		int nHour	= Integer.valueOf(sDate[2]).intValue();
		int nMinute	= Integer.valueOf(sDate[3]).intValue();
		int nSecond	= Integer.valueOf(sDate[4]).intValue();
				
		gc.set(nYear, nMonth, nDate, nHour, nMinute, nSecond);
		
		debugPrintln("gc.getTime() :" + gc.getTime() + ":");
		
		return gc;
	}
	
	/**
	 * Connect to the navy Server and get the result line
	 * @return the String containing the  Date/Time, that is the 
	 * String wich contains the "Universal Time" substring
	 */
	private String getUrlDatetimeLine()
		throws IOException
	{
		String sDatetimeLine = null;
		URL urlNavyMil = new URL(this.m_sUrl) ;
		
		InputStream Is = urlNavyMil.openStream();
		
		LineInputStream lineInputStream 
			= new LineInputStream(Is);
		
		String sLine = null;
		while((sLine = lineInputStream.readLine()) != null)
		{
			if (sLine.indexOf(UTC_TAG) > 0)
			{
				sDatetimeLine = sLine;
				break;
			}
		}		
		
		return sDatetimeLine;		
	}
	
	/**
	 * return the month in number from a 3 chars string format
	 * @param	sMonththe month in form "jan.", "feb.", etc.
	 * @return	the month in value between 0 & 11
	 */
	private int getMonthNumber(String sMonth)
	{
		sMonth = sMonth.substring(0, 3);
		if (sMonth.equalsIgnoreCase("jan"))
			return 0;
		else if (sMonth.equalsIgnoreCase("feb"))
			return 1;
		else if (sMonth.equalsIgnoreCase("mar"))
			return 2;
		else if (sMonth.equalsIgnoreCase("apr"))
			return 3;		
		else if (sMonth.equalsIgnoreCase("may"))
			return 4;		
		else if (sMonth.equalsIgnoreCase("jun"))
			return 5;	
		else if (sMonth.equalsIgnoreCase("jul"))
			return 6;		
		else if (sMonth.equalsIgnoreCase("aug"))
			return 7;		
		else if (sMonth.equalsIgnoreCase("sep"))
			return 8;		
		else if (sMonth.equalsIgnoreCase("oct"))
			return 9;		
		else if (sMonth.equalsIgnoreCase("nov"))
			return 10;				
		else if (sMonth.equalsIgnoreCase("dec"))
			return 11;		
		else 
			return 99; // This is an error value!
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
