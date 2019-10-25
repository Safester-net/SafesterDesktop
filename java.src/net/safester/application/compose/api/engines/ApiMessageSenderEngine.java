/*
 * Awake File: Easy file upload & download through HTTP with Java
 * Awake SQL: Remote JDBC access through HTTP.                                    
 * Copyright (C) 2012, Kawan Softwares S.A.S.
 * (http://www.awakeframework.org). All rights reserved.                                
 *                                                                               
 * Awake File/SQL is free software; you can redistribute it and/or                 
 * modify it under the terms of the GNU Lesser General Public                    
 * License as published by the Free Software Foundation; either                  
 * version 2.1 of the License, or (at your option) any later version.            
 *                                                                               
 * Awake File/SQL is distributed in the hope that it will be useful,               
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
 
package net.safester.application.compose.api.engines;

import java.util.Date;

import org.awakefw.commons.api.client.AwakeProgressManager;
import org.awakefw.file.http.HttpTransfer;

import net.safester.application.compose.api.ApiMessageSender;
import net.safester.application.http.ResultAnalyzer;

/**
 * An Uploader Engine allows to upload files to to the server.
 */

public class ApiMessageSenderEngine extends Thread {
    public static final int RC_ERROR = -1;
    public static final int RC_OK = 1;

    /** The debug flag */
    public boolean DEBUG = true;

    /** The return code */
    private int returnCode = RC_ERROR;

    /**
     * The error message to store if something bad happened without an Exception
     */
    private String errorMessage = null;

    /** The Exception thrown if something *realy* bad happened */
    private Exception exception = null;
    
    private ApiMessageSender apiMessageSender = null;
    
    /** The progress manager instance, to follow the transfer */
    private AwakeProgressManager awakeProgressManager;

    /**
     * Constructor
     * @param apiMessageSender
     */
    public ApiMessageSenderEngine(ApiMessageSender apiMessageSender) {

	if (apiMessageSender == null) {
	    throw new IllegalArgumentException(
		    "apiMessageSender can not be null!");
	}
	
	this.apiMessageSender = apiMessageSender;
	this.awakeProgressManager = apiMessageSender.getAwakeProgressManager();
	
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.Kawan Softwares S.A.S.utilx.http.FileTransferEngine#run()
     */
    @Override
    public void run() {
	try {
	    debug("ApiMessageSenderEngine Begin");
	    String jsonResult = apiMessageSender.sendMessage();
	    debug("jsonResult: " + jsonResult);
	    ResultAnalyzer resultAnalyzer = new ResultAnalyzer(jsonResult);
	    
	    if (resultAnalyzer.isStatusOk()) {
		returnCode = RC_OK;
	    }
	    else {
		this.exception = new Exception(resultAnalyzer.getErrorMessage());
	    }
	    
	    awakeProgressManager.setProgress(HttpTransfer.MAXIMUM_PROGRESS_100);
	    debug("ApiMessageSenderEngine End");
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    debug("ApiMessageSenderEngine Exception thrown: " + e);
	    this.exception = e;
	    awakeProgressManager.setProgress(HttpTransfer.MAXIMUM_PROGRESS_100);
	}
    }

    public int getReturnCode() {
	return returnCode;
    }

    public String getErrorMessage() {
	return errorMessage;
    }

    /**
     * Get currently uploaded file name
     * 
     * @return the file name
     */
    public String getCurrentFilename() {
	if (apiMessageSender == null) {
	    return null;
	}
	else {
	    return apiMessageSender.getCurrentFilename();
	}
	
    }

    public Exception getException() {
	return exception;
    }

    /**
     * debug tool
     */
    private void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
