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
package com.safelogic.pgp.api;

import java.io.PrintWriter;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.safelogic.pgp.api.util.msg.MessagesManager;

/**
 * 
 * Stores statically the last error that occured in a API program
 * <br>
 * @author Nicolas de Pomereu
 */

public class ErrorManager
{
	/** if true, the last operation is ok. Is static to be valid during JVM life */
	private static boolean m_operationOk = false;
	
	/** The last associated error code. Is static to be valid during JVM life **/
	private static String m_errorCode = null;
	
	/** The last Exception that occurred. Is static to be valid during JVM life */
	private static Exception m_exception = null;
	
    /** Says if a thread is terminated. Status to be used with asynchrounous process/thread */
    private boolean threadTerminated = false;
    
	/** 
     * Constructor 
     **/
	public ErrorManager()
	{
        //Util.loadDebugFile();
	}

	/**
     * Return true it the last operation is OK with no errors
     */
	public boolean isOperationOk()
	{
		return m_operationOk;
	}

	/**
	 * sets back the operation to OK.
     * Reset also the thread status
	 */
	public void setOperationOk()
	{
		m_operationOk = true;
        threadTerminated= false;
	}

	/**
     * @return the threadTerminated status :
     */
    public boolean isThreadTerminated()
    {
        return threadTerminated;
    }

    /**
     * @param threadTerminated the threadTerminated to set
     */
    public void setThreadTerminated(boolean threadTerminated)
    {
        this.threadTerminated = threadTerminated;
    }

    /**
	 * set the Error code (and associated Exception)
	 * <br>
	 * The next isOperationOk() will return false
	 */
	public void setErrorCode(String errorCode, Exception e)
	{
		// This is an error
		m_operationOk = false;
		
		m_errorCode = errorCode;
		m_exception = e;
		
		//m_exception.printStackTrace();
	}

	/** 
     * return the error code as a string
     */
	public String getErrorCode()
	{
		return m_errorCode;
	}
    
    /**
     * return the end user error label for the designated language
     */
    public String getErrorLabel()
    {
    	if (this.m_errorCode == null)
    	{
    		return null;
    	}
    	
        MessagesManager messagesManager = new MessagesManager();        
        return messagesManager.getMessage(this.m_errorCode.toUpperCase());
    }
    
	/**
	 * @return the Exception
	 */
	public Exception getException()
	{
		return m_exception;
	}
	
	/**
     * Return the complete stack trace a a string
     */
	public String getStackTrace()
	{
		if (m_exception == null)
		{
			return null;
		}
		
		ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
		
		PrintWriter pw = new PrintWriter(byteArrayOutputStream);
		m_exception.printStackTrace(pw);
		pw.close();
		
		String sException = byteArrayOutputStream.toString();
				
		return sException;
	}
    
    public String toString()
    {
        return "[error code: " + m_errorCode + "] [exception message: " + m_exception + "]"; 
    }
	
}

