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
package net.safester.application.engines;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.awakefw.commons.api.client.AwakeProgressManager;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.api.client.AwakeUrl;
import org.awakefw.file.http.HttpTransfer;
import org.awakefw.file.util.AwakeClientLogger;

import net.safester.application.updater.InstallParameters;




/**
 * A Downloader Engine allows to download a file from the server.
 */

public class MultipleUrlDownloaderEngine extends Thread
{
    public static final String CR_LF = System.getProperty("line.separator") ;

    public static final String ERR_HTTP_SOCKET_EXCEPTION            = "err_http_socket_exception";
    public static final String ERR_HTTP_CONNECT_EXCEPTION           = "err_http_connect_exception";
    public static final String ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION   = "err_http_unknown_service_exception";
    public static final String ERR_HTTP_PROTOCOL_EXCEPTION          = "err_http_protocol_exception";
    public static final String ERR_HTTP_IO_EXCEPTION                = "err_http_io_exception";

    /** The debug flag */
    public static boolean DEBUG  = false;

    public static final int RC_ERROR = -1;
    public static final int RC_OK = 1;
    public static final int RC_RUNNING = -2;

    /** The return code */
    private int returnCode = RC_RUNNING;

    /** The Exception thrown if something *realy* bad happened */
    private Exception exception = null;

    /** The file to install */
    //private File file = null;

    /** The URL of the file to download */
    private List<URL> urls = null;

    /** The destination install dir*/
    private String installationDir = null;
    
    /** The ServerCallerNew instance (already logged user)  */
    private AwakeFileSession awakeFileSession;

    /** The progress manager instance, to follow the transfer */
    private AwakeProgressManager awakeProgressManager;

    private int totalfileSize;

    /**
     * Constructor
     * @param url               the url of the file on the host
     * @param file              the file to create from a download
     * @param filesLength     The size of the file in bytes
     */
    public MultipleUrlDownloaderEngine( AwakeFileSession awakeFileSession,
                                        String installationDir,
                                        AwakeProgressManager awakeProgressManager,
                                        List<URL> urls,
                                        int filesLength)
        throws MalformedURLException
    {

        this.awakeFileSession = awakeFileSession;
        this.installationDir = installationDir;
        this.awakeProgressManager = awakeProgressManager;
        this.urls = urls;
        this.totalfileSize = filesLength;

    }


    /* (non-Javadoc)
     * @see com.safelogic.utilx.http.FileTransferEngine#run()
     */
    public void run()
    {
        try
        {
            debug("InstallerEngine Begin");

            awakeProgressManager.setLengthToTransfer(totalfileSize);            
            //awakeFileSession.setAwakeProgressManager(awakeProgressManager);
            
            AwakeUrl awakeUrl = new AwakeUrl(awakeFileSession.getHttpProxy(),
                    awakeFileSession.getHttpProtocolParameters());
            awakeUrl.setAwakeProgressManager(awakeProgressManager);
                    
            String outDebug = "";

            // Do the download
            for (URL url : urls) {
                String urlString = url.toString();

                String destinationFileName = installationDir;
                if(urlString.contains("lib")){
                    destinationFileName+= File.separator + "lib" + File.separator;
                }

                if (urlString.endsWith(InstallParameters.SAFESTER_LAUNCHER_JAR))
                {
                    // No add of ".tmp" to the launcher jar ==> Immediate download into jar
                    destinationFileName += urlString.substring(urlString.lastIndexOf("/")+ 1);
                }
                else
                {
                    destinationFileName += urlString.substring(urlString.lastIndexOf("/")+ 1) + ".tmp";
                }

                debug("");
                debug("url : " + url);
                debug("file: " + new File(destinationFileName));

                //awakeFileSession.downloadUrl(url, new File(destinationFileName));
                awakeUrl.download(url, new File(destinationFileName));
            }

            returnCode = RC_OK;
            System.out.println(new Date() + " DOWNLOAD FINISHED!");
            awakeProgressManager.setProgress(HttpTransfer.MAXIMUM_PROGRESS_100);
            debug("InstallerEngine End");

        }
        catch (InterruptedException e)
        {
            e.printStackTrace();

            // This is a normal/regular interruption asked by user.
            debug("FileDownloaderEngine Normal InterruptedException thrown by user Cancel!");
            awakeProgressManager.setProgress(HttpTransfer.MAXIMUM_PROGRESS_100);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            debug("FileDownloaderEngine Exception thrown: " + e);

            exception =  e;

            awakeProgressManager.setProgress(HttpTransfer.MAXIMUM_PROGRESS_100);
            returnCode = RC_ERROR;
        }

    }

    public int getReturnCode()
    {
        return returnCode;
    }


    /* (non-Javadoc)
     * @see com.safelogic.utilx.http.FileTransferEngine#getException()
     */
    public Exception getException()
    {
        return exception;
    }


    /**
     * debug tool
     */
    private void debug(String s)
    {
        if (DEBUG)
        {
            AwakeClientLogger.log(s);
        }
    }


}
