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
package com.safelogic.pgp.api.toolkit;

//CgeepApiEngine
//Copyright (c) SafeLogic, 2000 - 2009
//
//Last Updates: 
//06/10/09 12:40 NDP: Add cancelTask()

import java.io.File;
import java.util.List;
import java.util.Vector;

import com.safelogic.pgp.api.engines.CryptoEngine;
import com.safelogic.pgp.api.util.parms.CmPgpCodes;

public class CgeepApiEngine extends Thread implements CryptoEngine{

	
	private int taskNumber;
	
	
    /** Passed crypto action */
    private int m_action;

    /** The string to encrypt */
    private String m_inString = null;
    
    /** The in string of the crypto operation */
    private String m_outString = null;
  
    /** Files list in input of crypto operation*/
    private List<File> m_filesIn = new Vector();
    
    private List<File> m_filesInTemp = new Vector();
    
    /** Files list in output of crypto operation*/
    private List<File> m_filesOut = new Vector();
    
    /** List of recipients for crypto operation */
    private List<String> m_recipients = null;

    /** Current value of progression out of MAXIMUM_PROGRESS */
    private int m_current;

    /** Note to pass to the progression bar */
    private String m_note = null;

    /** The return code */
    private int m_returnCode = CmPgpCodes.RC_ERROR;

    /** The list of messages, one per file (not used in encryption) */
    private List<String> OperationMessages = new Vector();
    
    /** The display message that will be displayed at of process by the action listener */
    private String m_endMessage = null;

    /** The Exception thrown if something *realy* bad happened */
    private Exception m_exception = null;

    /** Says if the thread has been asked for termination */
    private boolean isThreadInterrupted = false;
    
    
    /**
     * Constructor 
     * 
     * @param taskNumber        the task number
     * @param inString          the string to crypt
     * @param filesIn           the list of files to crypt
     */
	public CgeepApiEngine(int taskNumber,
			String inString, 
            List<File> filesIn)
	{
		this.taskNumber = taskNumber;
		this.m_inString = inString;
		this.m_filesIn = filesIn;
						
	}
	//@Override
	public int getCurrent() {
		return m_current;
	}

	//@Override
	public String getEndMessage() {
		return this.m_endMessage;
	}

	//@Override
	public Exception getException() {
		
		return this.m_exception;
	}

	//@Override
	public List<File> getFilesIn() {
		return m_filesIn;
	}

	//@Override
	public List<File> getFilesOut() {
		return m_filesOut;
	}

	//@Override
	public String getNote() {
		return m_note;
	}

	//@Override
	public List<String> getOperationMessages() {
		return OperationMessages;
	}

	//@Override
	public String getOutString() {
		return m_outString;
}

	//@Override
	public int getReturnCode() {
		return this.m_returnCode;
	}

	//@Override
	public void setCurrent(int current) {
		
		//CgeepApi.tasks.put(taskNumber, current);
	    TaskContainer taskContainer = new TaskContainer(taskNumber);
	    taskContainer.setPercentProgress(current);
	    
		this.m_current = current;		
	}

	//@Override
	public void setNote(String note) {
		this.m_note = note;
		
	}
	
	//@Override
	public void interrupt()
	{
	    this.isThreadInterrupted = true;
	}

	//@Override
    public boolean isInterrupted() {
	    return this.isThreadInterrupted; 
    }	
	
	//@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
