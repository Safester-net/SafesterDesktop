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
package com.safelogic.pgp.api.engines;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownServiceException;

import com.safelogic.pgp.api.ErrorManager;
import com.safelogic.pgp.api.HttpTransferOne;
import com.safelogic.pgp.api.util.msg.MessagesManager;
import com.safelogic.pgp.api.util.parms.CmPgpCodes;
import com.safelogic.pgp.api.util.parms.Parms;
import com.safelogic.pgp.apispecs.HttpTransfer;

/**
 * A Downloader Engine allows to download a file from the server.
 */

public class DownloaderEngine extends Thread 
{
    /** The size of the installer in bytes */
    private static int CGEEP_INSTALLER_SIZE = 15 * 1024 * 1024;

    /** The maximum value of progress indicator */
    private static int MAXIMUM_PROGRESS = 100;
    
    /** The debug flag */
    protected boolean DEBUG = true; //Debug.isSet(this);

    /* National language messages */
    private MessagesManager messages = new  MessagesManager();
        
    /** Current value of progression out of MAXIMUM_PROGRESS */
    private int m_current;

    /** Note to pass to the progression bar */
    private String m_note = null;

    /** The return code */
    private int m_returnCode = CmPgpCodes.RC_ERROR;
    
    /** The error message to store if something bad happened without an Exception*/
    private String  errorMessage = null;    
    
    /** The Exception thrown if something *realy* bad happened */
    private Exception m_exception = null;

    /** The file to install */
    private File m_file = null;
    
    /** The URL of the file to download */
    private String m_url = null;
    
    /** The HttpTransfer instance */
    private HttpTransfer httpTransfer = null;

    /** The error Manager */
    private ErrorManager errorMan;
    
    /**
     * Constructor
     * 
     * @param  file     the file to create from a download
     * @param  url      the url of the file on the host 
     */
    public DownloaderEngine(File file, String url)
    {
        m_current = 0;
        
        m_file = file;
        m_url = url;
    }


    /**
     * Run the Crypto Engine when this thread is started
     */
    public void run()    
    {        
        try
        {                        
            debug("InstallerEngine Begin");                
            httpTransfer = new HttpTransferOne(this);
            
            httpTransfer.downloadFileFromUrl(m_file, CGEEP_INSTALLER_SIZE, m_url);

            m_returnCode = CmPgpCodes.RC_OK;            
            setCurrent(MAXIMUM_PROGRESS); // Says current = maximum; so task is over
            debug("InstallerEngine End");
            
        }
        catch (ConnectException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_CONNECT_EXCEPTION, e1);
            errorMessage = errorMan.getErrorLabel();
            m_exception =  e1;            
            setCurrent(MAXIMUM_PROGRESS);
        }
        catch (SocketException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e1);
            errorMessage = errorMan.getErrorLabel();
            m_exception =  e1;            
            setCurrent(MAXIMUM_PROGRESS);          
        }
        catch (UnknownServiceException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e1);
            errorMessage = errorMan.getErrorLabel();
            m_exception =  e1;            
            setCurrent(MAXIMUM_PROGRESS);         
        }
//        catch (ProtocolException e1)
//        {
//            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e1); 
//            errorMessage = errorMan.getErrorLabel();
//            setCurrent(MAXIMUM_PROGRESS);            
//        }
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            errorMessage = errorMan.getErrorLabel();
            return;             
        }          
        catch (InterruptedException e)
        {            
            e.printStackTrace();
            
            // This is a normal/regular interruption asked by user.
            debug("EncryptEngine Normal InterruptedException thrown by user Cancel!");
            setCurrent(MAXIMUM_PROGRESS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            debug("EncryptEngine Exception thrown: " + e); 

            m_exception =  e;
            
            setCurrent(MAXIMUM_PROGRESS);
        }

    }


    
    /**
     * @return the file
     */
    public File getFile()
    {
        return m_file;
    }


    /* (non-Javadoc)
     * @see com.pgeep.application.CryptoEngine#getCurrent()
     */
    public int getCurrent()
    {
        return m_current;
    }

    /* (non-Javadoc)
     * @see com.pgeep.application.CryptoEngine#setCurrent(int)
     */
    public void setCurrent(int current)
    {
        m_current = current;
    }

    /* (non-Javadoc)
     * @see com.pgeep.application.CryptoEngine#getNote()
     */
    public String getNote()
    {
        return m_note;
    }

    /* (non-Javadoc)
     * @see com.pgeep.application.CryptoEngine#setNote(java.lang.String)
     */
    public void setNote(String note)
    {
        m_note = note;
    }


    /* (non-Javadoc)
     * @see com.pgeep.application.CryptoEngine#getReturnCode()
     */
    public int getReturnCode()
    {
        return m_returnCode;
    }
   
    /**
     * @return the errorMessage
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }
    
    
    /* (non-Javadoc)
     * @see com.pgeep.application.CryptoEngine#getException()
     */
    public Exception getException()
    {
        return m_exception;
    }

   
    /**
     * @return the mAXIMUM_PROGRESS
     */
    public static int getMAXIMUM_PROGRESS()
    {
        return MAXIMUM_PROGRESS;
    }


    /**
     * debug tool
     */
    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
    }


}
