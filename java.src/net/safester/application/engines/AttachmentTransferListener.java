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
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import net.safester.application.MessageComposer;
import net.safester.application.messages.MessagesManager;
import net.safester.application.util.CacheFileHandler;
import net.safester.application.util.JOptionPaneNewCustom;

import org.awakefw.commons.api.client.DefaultAwakeProgressManager;
import org.awakefw.file.http.HttpTransfer;
import org.awakefw.file.http.engine.FileUploaderEngine;


/**
 * An Action Listener designed to listen to the Encryption/Decryption Thread.
 * Will display an end message or any Exception which occured in the Thread
 * 
 * @author Nicolas de Pomereu
 */
public class AttachmentTransferListener {

    public static final String CR_LF = System.getProperty("line.separator");
    /** Message Manager for I18N */
    private MessagesManager messages = new MessagesManager();

    /**
     * @param cryptoEngine          The crypto engine, aka the Thread that encrypt/decrypt
     * @param WaiterEngine          The Waiter Engine that displays the "Please Wait..." Message        
     * @param cryptoEngineMonitor   The Crypto Engine Monitor that wathes the thread progress
     * @param progressDialog        The Progress Dialog
     * @param jframe                The caller JFrame
     * @param isWithWindow          if true, called by a java Window application
     * @param isForEmail            If true, we will open send mail UI with Desktop API!
     * @param emailSubject          The email subject
     * @param isDraft               The message is a draft message
     */
    public AttachmentTransferListener(FileUploaderEngine fileUploaderEngine,
            DefaultAwakeProgressManager defaultAwakeProgressManager,
            WaiterEngine waiterEngine,
            Timer fileUploaderEngineMonitor,
            ProgressMonitor progressDialog,
            JFrame jframe,
            boolean isDraft) {


        if (jframe != null) {
            jframe.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // Always on top Except for PgeepMailEncryptor, because Mailer window
            // Wont' be accessible
            //if (! (jframe instanceof PgeepMailEncryptor))
            //{
            //    jframe.setAlwaysOnTop(true);
            //}

            //jframe.setAlwaysOnTop(true);            
            jframe.setEnabled(false);
        }

        // Get back all values from crypto engine
        int current = defaultAwakeProgressManager.getProgress();//.getCurrent();
        String currentFile = fileUploaderEngine.getCurrentFilename();
        //int returnCode = fileUploaderEngine.getReturnCode();

        Exception exception = fileUploaderEngine.getException();

        // If current is > 0 : our cryptoEngine is working.
        // So no more,waiting dialog: stop it!
        if (current > 0) {
            waiterEngine.interrupt();
            String note = messages.getMessage("uploading_file");
            note = MessageFormat.format(note, currentFile);
            progressDialog.setNote(note);
            progressDialog.setProgress(current);
        } else {
            // if current is not > 0 ==>
            // main thread has not started, so get info from waiting thread            
            String waiterNote = waiterEngine.getNote();
            int WaiterCurrent = waiterEngine.getCurrent();
            progressDialog.setNote(waiterNote);
            progressDialog.setProgress(WaiterCurrent);
        }

        boolean isTaskCanceled = false;

        if (progressDialog.isCanceled()) {
            isTaskCanceled = true;
            defaultAwakeProgressManager.cancel();

            // 20/05/10 16:40 NDP:  AttachmentTransferListener:
            //                      unlock default ThreadLocker when task is canceled
            new ThreadLocker().unlock();
        }

        // check if task is completed or canceled
        if (current >= HttpTransfer.MAXIMUM_PROGRESS_100 || isTaskCanceled) {

            fileUploaderEngineMonitor.stop();
            progressDialog.close();

            jframe.setAlwaysOnTop(true);     // Put the window in front
            jframe.setEnabled(true);         // Enable it
            jframe.setAlwaysOnTop(false);    // As soon it is enabled, no more a top window
            //fileUploaderEngine.interrupt();

            if (exception != null) {
                JOptionPaneNewCustom.showException(jframe, exception);
            } else {
                if (jframe instanceof MessageComposer && !isTaskCanceled) {

                    MessageComposer mailComposer = (MessageComposer) jframe;

                    try {
                        if (isDraft) {
                            mailComposer.uploadDraft();
                        } else {
                            mailComposer.putMessage();
                        }
                    } catch (SQLException sqle) {
                        JOptionPaneNewCustom.showException(jframe, sqle);
                    }                    
                }
            }
            
            if(isTaskCanceled){
                //Just delete all cached files immediatly
                CacheFileHandler cacheFileHandler = new CacheFileHandler();
                cacheFileHandler.deletedAllCachedFiles();
            }else
            {
                //When the message is really sent sometimes temp files ressources are not released immediatly
                // so launch a thread that wait a little time before deleting files
                Thread t = new Thread() {

                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AttachmentTransferListener.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        CacheFileHandler cacheFileHandler = new CacheFileHandler();
                        cacheFileHandler.deletedAllCachedFiles();
                    }
                };
                t.start();
            }
            if (jframe != null) {
                jframe.getContentPane().setCursor(Cursor.getDefaultCursor());
                jframe.setCursor(Cursor.getDefaultCursor());
                jframe.setEnabled(true);
            }

        }
        //System.out.println("Deleting cache");
        //System.out.println("cache deleted");
    }
}
