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
package com.safelogic.pgp.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;


// CUtilDate.java : Simplify - This class contains all utlity method for
//							the application
// 15/03/05 17:56 - AB : Creation
// 15/03/05 18:07 - AB : add formatDate
// 15/03/05 18:25 - AB : add formatHour
// 21/04/05 16:55 - NDP : getEndDate
// 12/06/06 18:45 - NDP : back to previous getDate()
// 06/02/08 11:03 - ABE : Fix bug in getDate(String) Exception was never thrown if the string was malformed!

public class UtilDate 
{
    /**
     * This method formats a String in dd/mm/yy or dd/mm or mm/yy
     * from a Date
     * <br>
     * Note: Return null if imput is null.
     * 
     * @param ts        the Timestamp to use
     * @return the formatted string
     */
    
    public static String formatDate(Date date)
    {
    	if (date == null) 
		{
    		return null;
		}
    	
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(date.getTime());
        
        int iday  = gc.get(Calendar.DATE);
        int iMonth = gc.get(Calendar.MONTH) + 1 ;
        int iYear = gc.get(Calendar.YEAR) ;
        
        String sDate = formatDate(iday, iMonth, iYear);
        return sDate;
    }
    
    /**
     * This method formats a String in dd/mm/yy or dd/mm or mm/yy
     * from a Timestamp.
     * <br>
     * Note: Return null if imput is null.
     * 
     * @param ts        the Timestamp to use
     * @return the formatted string
     */
    
    public static String formatDate(Timestamp ts)
    {
    	if (ts == null) 
		{
    		return null;
		}
    	
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(ts.getTime());
        
        int iday  = gc.get(Calendar.DATE);
        int iMonth = gc.get(Calendar.MONTH) + 1 ;
        int iYear = gc.get(Calendar.YEAR) ;
        
        String sDate = formatDate(iday, iMonth, iYear);
        return sDate;
    }
    
    /**
     * This method formats a String in hh/mm
     * <br>
     * Note: Return null if imput is null.
     * 
     * @param ts        the Timestamp to use
     * @return the formatted String
     */
     
    public static String formatHour(Timestamp ts)
    {
    	if (ts == null) 
		{
    		return null;
		}
    	
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(ts.getTime());
        
        int nHours      = gc.get(Calendar.HOUR_OF_DAY);
        int nMinutes    = gc.get(Calendar.MINUTE);
        
        return formatHour(nHours, nMinutes);
    }
    
    /**
     * This method formats a String in hh/mm
     * <br>
     * Note: Return null if imput is null.
     * 
     * @param ts        the Timestamp to use
     * @return the formatted String
     */
     
    public static String formatTime(Timestamp ts)
    {
        if (ts == null) 
        {
            return null;
        }
        
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(ts.getTime());
        
        int nHours      = gc.get(Calendar.HOUR_OF_DAY);
        int nMinutes    = gc.get(Calendar.MINUTE);
        int nSec        = gc.get(Calendar.SECOND);
        int nMilli      = gc.get(Calendar.MILLISECOND);
        
        return formatTime(nHours, nMinutes, nSec, nMilli);
    }    
    
    
    
	/**
	 * This method formats a String in dd/mm/yy or dd/mm or mm/yy
	 * 
	 * @param dayOfMonth day of month (0 if none)
	 * @param month month (0 if none)
	 * @param year year (-1 if none)
	 * @return the formatted string
	 */
	public static String formatDate(int dayOfMonth, int month, int year)
	{
		String sDateFormated = "";
		
		if(dayOfMonth != 0)
		{
			if(dayOfMonth<10)
				sDateFormated+="0" + dayOfMonth;
			else
				sDateFormated += dayOfMonth;
		}
		if(month !=0)
		{
			
			if(sDateFormated.length()>0)
			{
				sDateFormated += "/";
			}
			if(month<10)
			{
				sDateFormated += "0" + month;
			}
			else
			{
				sDateFormated += month;
			}
		}
		if(year!=-1)
		{
			if(sDateFormated.length()>0)
			{
				sDateFormated += "/";
			}
			if(year<10)
			{
				sDateFormated += "0" + year;
			}
			else
			{
				sDateFormated += year;
			}
		}
		return sDateFormated;
	}
	
	/**
	 * This method formats a String in hh/mm
	 * @param hour
	 * @param min
	 * @return the formatted String
	 */
	public static String formatHour(int hour, int min)
	{
		String sHour = "";
		if(hour<10)
		{
			sHour += "0" + hour;
		}
		else
		{
			sHour += hour;
		}
		sHour +=":";
		if(min<10)
		{
			sHour += "0" + min;
		}
		else
		{
			sHour += min;
		}
		
		return sHour;
	}

    /**
     * This method formats a String in hh/mm
     * @param hour
     * @param min
     * @return the formatted String
     */
    public static String formatTime(int hour, int min, int sec, int milli)
    {
        String sHour = "";
        if(hour<10)
        {
            sHour += "0" + hour;
        }
        else
        {
            sHour += hour;
        }
        sHour +=":";
        if(min<10)
        {
            sHour += "0" + min;
        }
        else
        {
            sHour += min;
        }
        
        sHour +=":";
        if(sec<10)
        {
            sHour += "0" + sec;
        }
        else
        {
            sHour += sec;
        }
        
        sHour +=":";
        if(milli<10)
        {
            sHour += "0" + milli;
        }
        else
        {
            sHour += milli;
        }
        
        
        return sHour;
    }    
    
    
    /**
     * Get The End Date
     * @param tsStartDate
     * @param daterange
     * @return
     */
    public static Timestamp getEndTs(Timestamp tsStartDate, String daterange)
    {
        // There is a startdate criteria
        // Calculate end date of selection (date range is one month)
        Timestamp tsEndDate = new Timestamp(tsStartDate.getTime());
        //int iMonth = tsEndDate.getMonth();
        //int iYear = tsEndDate.getYear();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(tsEndDate.getTime());
        
        int iMonth = gc.get(Calendar.MONTH);
        int iYear = gc.get(Calendar.YEAR);
        
        if(daterange.equals("month"))
        {
            if(iMonth<11)
            {
                iMonth++;
            }
            else
            {
                iMonth=0;
                iYear++;
                
            }
        }
        else
        {
            iYear++;
        }
        
        gc.set(Calendar.MONTH, iMonth);
        gc.set(Calendar.YEAR, iYear);
        tsEndDate.setTime(gc.getTimeInMillis());
        return tsEndDate;
    }

    /**
     * Build a Date from a String
     */
    public static java.sql.Date getDate(String string)
    {
        if (string == null || string.equals("") || string.equals("null"))
        {
            return null;
        }
        
        // Rebuild a Hasmap from a string
        StringTokenizer st = new StringTokenizer(string, "/", false);
        String sToken;

        if (st.countTokens() != 3)
        {	
            throw new IllegalArgumentException("This string is not formatted as DD/MM/YYYY: " 
                                               + string);
        }
               
        GregorianCalendar gc = new GregorianCalendar();
        int i = 0;
        int day = 0;
        int month = 0;
        int year = 0;
        
        while (st.hasMoreTokens())
        {
            sToken = st.nextToken();
            
            if (i == 0)
            {
                day = Integer.parseInt(sToken);
            }
            else if (i == 1)
            {
                month = Integer.parseInt(sToken);
            }
            else if (i == 2)
            {
                year = Integer.parseInt(sToken);
            }          
            i++;
        }        
        
         gc.set(Calendar.DATE, day);
         gc.set(Calendar.MONTH, month -1);
         gc.set(Calendar.YEAR, year);
         
         java.sql.Date date = new java.sql.Date(gc.getTimeInMillis());
         return date;
    }
    
	/**
	 * Build a Date from a String
	 */
	public static Timestamp getDateBeginOfDay(String string)
	{
	    GregorianCalendar gc = getDateAsGc(string);
         
         gc.set(Calendar.HOUR_OF_DAY, 0);
         gc.set(Calendar.MINUTE, 0);
         gc.set(Calendar.SECOND, 0);
         gc.set(Calendar.MILLISECOND, 0);
         
         Timestamp date = new Timestamp(gc.getTimeInMillis());
	     return date;
	}

    /**
     * Build a Date from a String
     */
    public static Timestamp getDateEndOfDay(String string)
    {
        GregorianCalendar gc = getDateAsGc(string);
         
         gc.set(Calendar.HOUR_OF_DAY, 23);
         gc.set(Calendar.MINUTE, 59);
         gc.set(Calendar.SECOND, 59);
         gc.set(Calendar.MILLISECOND, 999);
         
         Timestamp date = new Timestamp(gc.getTimeInMillis());
         return date;
    }
    
    /**
     * @param string
     * @return
     * @throws IllegalArgumentException
     * @throws NumberFormatException
     */
    private static GregorianCalendar getDateAsGc(String string) throws IllegalArgumentException, NumberFormatException
    {
        if (string == null || string.equals("") || string.equals("null"))
	    {
	    	return null;
	    }
	    
	    // Rebuild a Hasmap from a string
	    StringTokenizer st = new StringTokenizer(string, "/", false);
	    String sToken;
	    
	    if (st.countTokens() == 0)
	    {
	        throw new IllegalArgumentException("This string is not formatted as DD/MM/YYYY: " 
	                                           + string);
	    }
	           
	    GregorianCalendar gc = new GregorianCalendar();
	    int i = 0;
	    int day = 0;
	    int month = 0;
	    int year = 0;
	    
	    while (st.hasMoreTokens())
	    {
	        sToken = st.nextToken();
	        
	        if (i == 0)
	        {
	            day = Integer.parseInt(sToken);
	        }
	        else if (i == 1)
	        {
	            month = Integer.parseInt(sToken);
	        }
	        else if (i == 2)
	        {
	            year = Integer.parseInt(sToken);
	        }          
	        i++;
	    }        
	    
	     gc.set(Calendar.DATE, day);
	     gc.set(Calendar.MONTH, month -1);
	     gc.set(Calendar.YEAR, year);
        return gc;
    }

}
