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
package com.safelogic.utilx.collection;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.safelogic.utilx.Debug;
import com.safelogic.utilx.io.file.OpenAppend;

/**
 * Class to log in append mode events into a log file
 * <br>
 * It's better to close the system log at end of usage.
 * <br>
 * Methods do not and must *not* throw Exceptions to be trapped.
 * This is why all Exceptions are IllegalStateException or IllegalArgumentException
 */

public class SystemLogger 
{	
	private static String CR_LF = System.getProperty("line.separator");
		
	/** The debug flag */ 
    private boolean CM_DEBUG = Debug.isSet(this);
	
	/** The log file in append mode */
	private OpenAppend m_aLog = null;
	
    /** Boolean to say is Log is On or Off */
    private boolean m_bLogOn = false;
    
    /**
     * Constructor: 
     */
    public SystemLogger()
    {
        
    }
    
	/**
	 * Sets the Log file name. This will also activate log mode.
     * @param sLogfile  the Log file to set
	 */
	public void setLogFile(String sLogFile)
	{
        try
        {
            // Open sLogFile in *append* mode
            m_aLog = new OpenAppend(new File(sLogFile));
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            throw new IllegalArgumentException(ioe.getMessage());
        }
        
        this.setLogOn(true);

	}
	
    /**
     * @return true if Log is On
     */
    public boolean isLogOn()
    {
        return this.m_bLogOn;
    }

    /**
     * @param logOn     if tru, active the Logging
     */
    public void setLogOn(boolean logOn)
    {
        this.m_bLogOn = logOn;
    }

	/**
	 * <br> 1) log Message getServletContext()
	 * <br> 2) Print the Message on system console (DBMON)
	 * @param  sMessage     Message to log
	 */
	
	public void log(String sMessage)
	{           
        if (! this.isLogOn())
        {
            return;
        }
        
		try
		{
			//1) Format the message
			String sMessageFull = getDateReverse() + " " + getTime() + " - " + sMessage;
			
			// 2) Log it
			logWriteln(sMessageFull);
		}
		catch (Exception e)
		{
			e.printStackTrace();			
            throw new IllegalStateException(e.getMessage());
		}		
	}    
	
	
	/**
	 * <br> 1) log Message and Exception using getServletContext()
	 * <br> 2) Print the Message and the Exception Stack Trace on 
	 *         system console (DBMON)
	 * @param  sMessage     Message to log
	 * @param  e            Exception caucht in Servlet
	 */
	
	public void log(String sMessage, Exception e)	
	{        
        if (! this.isLogOn())
        {
            return;
        }
                
		log(sMessage);
		log("Exception: " + e.toString());
		log("Stack    : " + Debug.GetStackTrace(e));
	}
	
	
	/**
	 * closes the system log
	 */
	public void close()	
	{
		try
		{
			this.m_aLog.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();	
            throw new IllegalStateException(e.getMessage());		
		}		
	}
	
	/**
	 * Write the log message into a file in append mode
	 * @param		sMsg		the debug message to display
	 */

	private void logWriteln(String sMsg)
		throws IOException
	{
		this.m_aLog.append(sMsg) ;
		this.m_aLog.append(CR_LF) ;
		System.out.println(sMsg) ;
	}		
	
	/**
	 * Get the formated date of the trace file.
	 * @return  the date as "yyyy-mm-dd"
	 */
	private String getDateReverse()
	{
		String sDate = new String();

		int nDay	= new GregorianCalendar().get(Calendar.DAY_OF_MONTH);
		int nMonth  = new GregorianCalendar().get(Calendar.MONTH) + 1;

		sDate =  "" + new GregorianCalendar().get(Calendar.YEAR);
		
		if (nMonth < 10)
		{
			sDate += "-0" + nMonth;
		}
		else {
			sDate += "-"  + nMonth;
		}
		
		if (nDay < 10)
		{
			sDate += "-0" + nDay;
		}
		else {
			sDate += "-" + nDay;
		}
		
		return sDate;
	}
	
	/**
	 * Get the formated time of the trace file.
	 * @return  the date as "hhmnss"
	 */

	private String getTime()
	{
		String sTime = new String();

		int nHour   = new GregorianCalendar().get(Calendar.HOUR_OF_DAY);
		int nMin    = new GregorianCalendar().get(Calendar.MINUTE);
		int nSec    = new GregorianCalendar().get(Calendar.SECOND);

		if (nHour < 10)
		{
			sTime = "0" + nHour;
		}
		else {
			sTime = "" + nHour;
		}

		if (nMin < 10)
		{
			sTime += ":0" + nMin;
		}
		else {
			sTime += ":" + nMin;
		}

		if (nSec < 10)
		{
			sTime += ":0" + nSec;
		}
		else {
			sTime += ":" + nSec;
		}

		return sTime;
	}	   
}

// End
