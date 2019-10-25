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
import java.io.FileNotFoundException;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import com.safelogic.pgp.api.engines.CryptoEngine;
import com.swing.util.SwingUtil;

import net.safester.application.compose.api.ApiMessageSender;
import net.safester.application.engines.ThreadLocker;
import net.safester.application.engines.WaiterEngine;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.util.CacheFileHandler;
import net.safester.application.util.JOptionPaneNewCustom;

public class ApiAttachmentEncryptEngineListener {

    public static boolean DEBUG = false;
    
    public ApiAttachmentEncryptEngineListener(ApiAttachmentEncryptEngine apiAttachmentEncryptEngine,
            WaiterEngine waiterEngine,
            Timer apiMessageSenderEngineMonitor,
            ProgressMonitor progressDialog,
            ApiMessageSender apiMessageSender,
            JFrame jframe) {
	
	if (apiAttachmentEncryptEngine == null) {
	    throw new NullPointerException("apiAttachmentEncryptEngine cannot be null!");
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
	if (apiMessageSender == null) {
	    throw new NullPointerException("apiMessageSender cannot be null!");
	}
	
        if (jframe != null) {
            jframe.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            jframe.setEnabled(false);
        }
        // Get back all values from crypto engine
        int current = apiAttachmentEncryptEngine.getCurrent();
        String note = apiAttachmentEncryptEngine.getNote();
        Exception exception = apiAttachmentEncryptEngine.getException();
        // If current is > 0 : our cryptoEngine is working.
        // So no more,waiting dialog: stop it!
        if (current > 0) {
            waiterEngine.interrupt();
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
            // System.out.println("CryptoEngineListener progressDialog.isCanceled()");
            isTaskCanceled = true;

            new ThreadLocker().unlock();
        }

        // check if task is completed or canceled
        if (current >= CryptoEngine.MAXIMUM_PROGRESS + 1 || isTaskCanceled) {

            apiMessageSenderEngineMonitor.stop();
            progressDialog.close();
            
            debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            debug("Interrupting cryptoEngine: ");
            debug("isTaskCanceled: " + isTaskCanceled);
            debug("current: " + current);
            
            apiAttachmentEncryptEngine.interrupt();

            debug("");
            debug("exception: " + exception);
            
            if (exception != null) {

                if (jframe != null) {
                    jframe.getContentPane().setCursor(Cursor.getDefaultCursor());
                    jframe.setCursor(Cursor.getDefaultCursor());
                    jframe.setAlwaysOnTop(true);     // Put the window in front
                    jframe.setEnabled(true);         // Enable it
                }

                if (exception instanceof FileNotFoundException) {
                    MessagesManager messages = new MessagesManager();
                    String errorMsg = messages.getMessage("file_probably_locked") + SwingUtil.CR_LF + apiAttachmentEncryptEngine.getEndMessage();
                    JOptionPane.showMessageDialog(jframe, errorMsg,  Parms.PRODUCT_NAME, JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPaneNewCustom.showException(jframe, exception);
                }

                return;
            }

            if (jframe != null && isTaskCanceled) {
                jframe.setAlwaysOnTop(true);     // Put the window in front
                jframe.setEnabled(true);         // Enable it
                jframe.setAlwaysOnTop(false);    // As soon it is enabled, no more a top window

                jframe.getContentPane().setCursor(Cursor.getDefaultCursor());
                jframe.setCursor(Cursor.getDefaultCursor());
            } else if (jframe != null && exception == null) {
                jframe.setAlwaysOnTop(true);     // Put the window in front
                jframe.setEnabled(true);         // Enable it
                jframe.setAlwaysOnTop(false);    // As soon it is enabled, no more a top window

                try {
                    debug("calling sendMessageAndFilesUsingThread");
                    sendMessageAndFilesUsingThread(apiMessageSender, jframe);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPaneNewCustom.showException(jframe, e);
                }
            }

            if (isTaskCanceled) {
                CacheFileHandler cacheFileHandler = new CacheFileHandler();
                cacheFileHandler.deletedAllCachedFiles();
            }

        }
    }

    private void sendMessageAndFilesUsingThread(ApiMessageSender apiMessageSender, JFrame jframe) {
	ApiMessageSenderUsingThread apiMessageSenderUsingThread = new ApiMessageSenderUsingThread(apiMessageSender, jframe);
	apiMessageSenderUsingThread.sendMessage();
    }

    protected static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
