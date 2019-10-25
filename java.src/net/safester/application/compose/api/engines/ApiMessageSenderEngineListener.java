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
package net.safester.application.compose.api.engines;

import java.awt.Cursor;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import org.awakefw.commons.api.client.AwakeProgressManager;
import org.awakefw.file.http.HttpTransfer;

import net.safester.application.MessageComposer;
import net.safester.application.engines.ThreadLocker;
import net.safester.application.engines.WaiterEngine;
import net.safester.application.messages.MessagesManager;
import net.safester.application.util.CacheFileHandler;
import net.safester.application.util.JOptionPaneNewCustom;


/**
 * An Action Listener designed to listen to the Encryption/Decryption Thread.
 * Will display an end message or any Exception which occured in the Thread
 * 
 * @author Nicolas de Pomereu
 */
public class ApiMessageSenderEngineListener {

    public static final boolean DEBUG = false;
    
    public static final String CR_LF = System.getProperty("line.separator");


    /** Message Manager for I18N */
    private MessagesManager messages = new MessagesManager();


    public ApiMessageSenderEngineListener(ApiMessageSenderEngine apiMessageSenderEngine,
            AwakeProgressManager awakeProgressManager,
            WaiterEngine waiterEngine,
            Timer apiMessageSenderEngineMonitor,
            ProgressMonitor progressDialog,
            JFrame jframe) {

	if (apiMessageSenderEngine == null) {
	    throw new NullPointerException("apiMessageSenderEngine cannot be null!");
	}
	if (awakeProgressManager == null) {
	    throw new NullPointerException("awakeProgressManager cannot be null!");
	}
	if (waiterEngine == null) {
	    throw new NullPointerException("waiterEngine cannot be null!");
	}
	if (apiMessageSenderEngineMonitor == null) {
	    throw new NullPointerException("apiMessageSenderEngineMonitor cannot be null!");
	}
	if (progressDialog == null) {
	    throw new NullPointerException("progressDialog cannot be null!");
	}	

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
        int current = awakeProgressManager.getProgress();//.getCurrent();
        String currentFile = apiMessageSenderEngine.getCurrentFilename();
        //int returnCode = fileUploaderEngine.getReturnCode();

        Exception exception = apiMessageSenderEngine.getException();

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
            awakeProgressManager.cancel();

            // 20/05/10 16:40 NDP:  ApiMessageSenderEngineListener:
            //                      unlock default ThreadLocker when task is canceled
            new ThreadLocker().unlock();
        }

        // check if task is completed or canceled
        if (current >= HttpTransfer.MAXIMUM_PROGRESS_100 || isTaskCanceled) {

            apiMessageSenderEngineMonitor.stop();
            progressDialog.close();

            jframe.setAlwaysOnTop(true);     // Put the window in front
            jframe.setEnabled(true);         // Enable it
            jframe.setAlwaysOnTop(false);    // As soon it is enabled, no more a top window
            //fileUploaderEngine.interrupt();

            if (exception != null) {
                JOptionPaneNewCustom.showException(jframe, exception);
            } else {
                if (jframe != null && jframe instanceof MessageComposer && !isTaskCanceled) {
                    MessageComposer mailComposer = (MessageComposer) jframe;
                    mailComposer.putMessage();                                   
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
                            Logger.getLogger(ApiMessageSenderEngineListener.class.getName()).log(Level.SEVERE, null, ex);
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
    
    protected static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }
}
