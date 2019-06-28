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
package net.safester.application.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import net.safester.application.messages.MessagesManager;


/**
 * @author Nicolas de Pomereu
 *
 */
public class AppDateFormat {

    /** debug infos */
    public static boolean DEBUG = false;
        
    /** The value of one hour in milliseconds */
    private static int ONE_HOUR_MILLISECONDS = 60 * 60 * 1000;
    
    /**
     * Default constructor
     */
    public AppDateFormat()
    {
        
    }
    
    
    /**
     * Return the date in shodrt format in dd/mm/yyyy hh:mm in French, etc.
     * @param date      the date
     * @return  the formated date
     */
    public String format(Timestamp date) {
        DateFormat df;
        try {            
            df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            //setFormatToTimeZone(df, date);
        } catch (Exception e) {
            e.printStackTrace();
            MessagesManager messages = new MessagesManager();
            df = new SimpleDateFormat(messages.getMessage("date_format"));
            //setFormatToTimeZone(df, date);         
        }
        
        String dayOfWeek = null;
        try {
            DateFormat dfDay = new SimpleDateFormat("EEE");
            dayOfWeek = dfDay.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        date = getTimestampInTimeZone(date);
        
        String dateStr = df.format(date);
        
        if (dayOfWeek != null) {
            dateStr = dayOfWeek.trim() + " " + dateStr;
        }
        
        return dateStr;
    }

        public static boolean  isSummerTime(long timeMillis) {
	boolean summerTime = false;
	Calendar c = Calendar.getInstance(); // omit timezone for default tz
	//c.setTime(new Date()); // your date; omit this line for current date
	c.setTimeInMillis(timeMillis);
	int offset = c.get(Calendar.DST_OFFSET);
	
	if (offset == 0) {
	    summerTime = false;
	}
	else {
	    summerTime = true;
	}
	
	return summerTime;
    }
        
    /**
     * The passed timestamp is in Paris GMT + 1. This method return the Timestamp
     * in the time zone of the user
     * 
     * @param ts        the timestamp in Paris GMT + 1
     * @return the ts in locale time zone
     */
    public static Timestamp getTimestampInTimeZone(Timestamp ts) {
        
        //HACK XXX
        boolean doNotUse = true;
        if (doNotUse) {
            return ts;
        }
        
        // Convert the Date to the locale
        // 1) ts is stored on server as Paris time GMT + 1: ==> Remove 1 hour        
        long theTime = ts.getTime();
        
        if (isSummerTime(theTime)) {
            theTime -= ONE_HOUR_MILLISECONDS * 2; // 2 hours in summer
        }
        else {
            theTime -= ONE_HOUR_MILLISECONDS;
        }
        
        // 2) Get the locale offset and add the hours to the time
        Calendar c = Calendar.getInstance();
        TimeZone z = c.getTimeZone();
        int offsetMilliseconds = z.getRawOffset(); // in hours
        debug("offsetMilliseconds: " + offsetMilliseconds);
        

        theTime =  theTime + offsetMilliseconds;
        
        ts = new Timestamp(theTime);
        return ts;
    }
            
    /**
     * debug tool
     */
    private static void debug(String s)
    {
        if (DEBUG)
            System.out.println(net.safester.application.util.AppDateFormat.class.getName()
                    + " " 
                    + new java.util.Date() 
                    + " "
                    + s);
    }
    
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        AppDateFormat appDateFormat = new AppDateFormat();
        System.out.println(appDateFormat.format(new Timestamp(System.currentTimeMillis())));
        
        DateFormat df = new SimpleDateFormat();

        Timestamp date = new Timestamp(System.currentTimeMillis());
        date = getTimestampInTimeZone(date);
        
        System.out.println(df.format(date));
  
    }
    
}
