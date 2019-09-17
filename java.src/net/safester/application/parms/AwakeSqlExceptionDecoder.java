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
package net.safester.application.parms;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import org.awakefw.commons.api.client.InvalidLoginException;
import org.awakefw.commons.api.client.RemoteException;

/**
 *
 * @author Nicolas de Pomereu
 *
 * AwakeSqlException Decoder (because of new Awake SQL throw mechanism)
 */
public class AwakeSqlExceptionDecoder {

    /**
     * Decodes the AwakeSqlException thrown when creating an
     * AwakeConnection instance.
     * <br>
     * The exception are classified in order from "most local"
     * to "most remote"
     *
     * @param e     the Exception thrown by AwakeConnection
     *              when creating an instance
     * @throws java.net.MalformedURLException
     * @throws java.net.UnknownHostException
     * @throws java.net.ConnectException
     * @throws org.awakefw.commons.api.client.InvalidLoginException
     */
    public static void decodeAndRethrow(Exception e)
            throws MalformedURLException,
            UnknownHostException,
            ConnectException,
            InvalidLoginException,
            SecurityException,
            IOException
    {

        /*
        if (! (e instanceof SQLException))
        {
            // Not trapped ==> IOException
            throw new IOException(e);
        }

        SQLException sQLException = (SQLException) e;
        Throwable exceptionWrapped = sQLException.getCause();
        */
        
        Throwable exceptionWrapped = e;
        
        //
        // 1) Exceptions thrown before accessing the Internet:
        //
        if (exceptionWrapped instanceof MalformedURLException)
        {
            MalformedURLException e1 = (MalformedURLException) exceptionWrapped;
            throw e1;
        }

        if (exceptionWrapped instanceof UnknownHostException)
        {
            UnknownHostException e1 = (UnknownHostException) exceptionWrapped;
            throw e1;
        }

        //
        // 2) Exceptions thrown when accessing Internet but
        //    before accessing the Awake remote server:
        //

        if (exceptionWrapped instanceof ConnectException)
        {
            ConnectException e1 = (ConnectException) exceptionWrapped;
            throw e1;
        }

        //
        // 3) AwakeFileManager Servlet is reached, but the Servlet
        //    refuses to grant access to database:
        //

        if (exceptionWrapped instanceof SecurityException)
        {
            SecurityException e1 = (SecurityException) exceptionWrapped;
            throw e1;
        }

        if (exceptionWrapped instanceof InvalidLoginException)
        {
            InvalidLoginException e1 = (InvalidLoginException) exceptionWrapped;
            throw e1;
        }
        
	if (exceptionWrapped instanceof RemoteException) {

	    RemoteException remoteException = (RemoteException) exceptionWrapped;
	    System.out
		    .println("The Awake SQL Manager throwed an Exception on the server.");
	    System.out.println("The wrapped Exception message is: "
		    + remoteException.getMessage());
	    System.out.println("The wrapped Exception is a      : "
		    + remoteException.getCause());
	    System.out.println("The remote stack trace is       : "
		    + remoteException.getRemoteStackTrace());
            throw remoteException;

	}
        
        //
        // 4) Unexpected IO / System Error (should never occur.)
        //

        if (exceptionWrapped instanceof IOException)
        {
            IOException e1 = (IOException) exceptionWrapped;
            throw e1;
        }

        // Not trapped ==> IOException
        throw new IOException(e);
    }
    
}
