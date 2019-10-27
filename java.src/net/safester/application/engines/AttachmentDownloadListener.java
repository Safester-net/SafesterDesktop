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


import java.awt.Cursor;
import java.text.MessageFormat;

import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import org.awakefw.commons.api.client.DefaultAwakeProgressManager;
import org.awakefw.file.http.HttpTransfer;
import org.awakefw.file.http.engine.FileDownloaderEngine;

import net.safester.application.messages.MessagesManager;
import net.safester.application.tool.AttachmentListHandler;
import net.safester.application.util.CacheFileHandler;
import net.safester.application.util.JOptionPaneNewCustom;


/**
 * An Action Listener designed to listen to the Encryption/Decryption Thread.
 * Will display an end message or any Exception which occured in the Thread
 * 
 * @author Nicolas de Pomereu
 */

public class AttachmentDownloadListener
{
    public static final String CR_LF = System.getProperty("line.separator") ;
    
    /** Message Manager for I18N */
    private MessagesManager messages = new  MessagesManager();
            
    /**
     * @param cryptoEngine          The crypto engine, aka the Thread that encrypt/decrypt
     * @param WaiterEngine          The Waiter Engine that displays the "Please Wait..." Message        
     * @param cryptoEngineMonitor   The Crypto Engine Monitor that wathes the thread progress
     * @param progressDialog        The Progress Dialog
     * @param jframe                The caller JFrame
     * @param isWithWindow          if true, called by a java Window application
     * @param isForEmail            If true, we will open send mail UI with Desktop API!
     * @param emailSubject          The email subject
     * 
     */
    
    public AttachmentDownloadListener(  FileDownloaderEngine fileDownloadEngine,
                                        DefaultAwakeProgressManager defaultAwakeProgressManager,
                                        WaiterEngine waiterEngine,
                                        Timer fileDownloaderEngineMonitor,
                                        ProgressMonitor progressDialog,
                                        AttachmentListHandler attachmentJListHandler,
                                        boolean openFile)
                                //JFrame jframe)
    {
                
        
        if (attachmentJListHandler != null)
        {
            attachmentJListHandler.getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));          
            attachmentJListHandler.getParent().setEnabled(false);
        }
                                        
        // Get back all values from crypto engine
        int current = defaultAwakeProgressManager.getProgress();
        String fileName = fileDownloadEngine.getCurrentFilename();
        
        // If current is > 0 : our cryptoEngine is working.
        // So no more,waiting dialog: stop it!
        if (current > 0)
        {
            waiterEngine.interrupt();
            String note = messages.getMessage("downloading_file");
            note = MessageFormat.format(note, fileName);
            progressDialog.setNote(note);
            progressDialog.setProgress(current);            
        }
        else
        {   
            // if current is not > 0 ==>
            // main thread has not started, so get info from waiting thread            
            String waiterNote = waiterEngine.getNote();
            int WaiterCurrent = waiterEngine.getCurrent();                    
            progressDialog.setNote(waiterNote);
            progressDialog.setProgress(WaiterCurrent);                    
        }
        
        boolean isTaskCanceled = false;
        if (progressDialog.isCanceled())
        {
            isTaskCanceled = true;
            defaultAwakeProgressManager.cancel();
        }

        // check if task is completed or canceled
        if (current >= HttpTransfer.MAXIMUM_PROGRESS_100 || isTaskCanceled)
        {
            stopAll(fileDownloaderEngineMonitor, progressDialog, fileDownloadEngine, attachmentJListHandler, openFile, isTaskCanceled);
        }
        if(isTaskCanceled)
        {
            CacheFileHandler cacheFileHandler = new CacheFileHandler();
            cacheFileHandler.deletedAllCachedFiles();
        }
    }

    public void stopAll(Timer fileDownloaderEngineMonitor, ProgressMonitor progressDialog, FileDownloaderEngine fileDownloadEngine, AttachmentListHandler attachmentJListHandler, boolean openFile, boolean cancelled) {

        Exception exception = fileDownloadEngine.getException();
        fileDownloaderEngineMonitor.stop();
        progressDialog.close();
        //defaultAwakeProgressManager.interrupt();
        fileDownloadEngine.interrupt();
        if (exception != null) {
            exception.printStackTrace();
            JOptionPaneNewCustom.showException(attachmentJListHandler.getParent(), exception);
        } else {
            if(!cancelled){
                attachmentJListHandler.decryptAttachments(fileDownloadEngine.getFiles(), openFile);
            }
        }
        if (attachmentJListHandler.getParent() != null) {
            attachmentJListHandler.getParent().getContentPane().setCursor(Cursor.getDefaultCursor());
            attachmentJListHandler.getParent().setCursor(Cursor.getDefaultCursor());
            attachmentJListHandler.getParent().setEnabled(true);
            attachmentJListHandler.getParent().requestFocus();
        }
    }


 
    
}

