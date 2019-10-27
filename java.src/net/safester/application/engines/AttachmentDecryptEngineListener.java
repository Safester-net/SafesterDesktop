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
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import com.safelogic.pgp.api.engines.CryptoEngine;
import com.swing.util.SwingUtil;

import net.safester.application.MessageComposer;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.util.JOptionPaneNewCustom;

public class AttachmentDecryptEngineListener {

    public AttachmentDecryptEngineListener(CryptoEngine cryptoEngine,
            WaiterEngine waiterEngine,
            Timer cryptoEngineMonitor,
            ProgressMonitor progressDialog,
            Connection connection,
            JFrame jframe,
            boolean openFile) {
        if (jframe != null) {
            jframe.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            jframe.setEnabled(false);
        }
        // Get back all values from crypto engine
        int current = cryptoEngine.getCurrent();
        String note = cryptoEngine.getNote();
        Exception exception = cryptoEngine.getException();

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
            //System.out.println("CryptoEngineListener progressDialog.isCanceled()");
            isTaskCanceled = true;
        }

        // check if task is completed or canceled
        //30/05/11 18:15 ABE : AttachmentDecryptEngineListener:
        //                     if (current >= CryptoEngine.MAXIMUM_PROGRESS + 1 || isTaskCanceled)
        if (current >= CryptoEngine.MAXIMUM_PROGRESS + 1 || isTaskCanceled) {

            cryptoEngineMonitor.stop();
            progressDialog.close();

            cryptoEngine.interrupt();

            if (cryptoEngine instanceof AttachmentDecryptEngine) {
                AttachmentDecryptEngine decryptEngine = (AttachmentDecryptEngine) cryptoEngine;
                List<String> failedIntegrityCheck = decryptEngine.getFailedIntegrityFileList();
                if (failedIntegrityCheck != null && failedIntegrityCheck.size() > 0) {
                    MessagesManager messages = new MessagesManager();
                    String msg = messages.getMessage("file_integrity_failed");
                    String listFile = "";
                    for (String fileName : failedIntegrityCheck) {
                        listFile += fileName + System.getProperty("line.separator");
                    }
                    msg = MessageFormat.format(msg, listFile);
                    JOptionPane.showMessageDialog(jframe, msg);
                }
            }

            
            if (exception != null) {
                
                //JOptionPaneNewCustom.showException(jframe, exception);

                if (jframe != null) {
                    jframe.getContentPane().setCursor(Cursor.getDefaultCursor());
                    jframe.setCursor(Cursor.getDefaultCursor());                    
                    jframe.setAlwaysOnTop(true);     // Put the window in front
                    jframe.setEnabled(true);         // Enable it
                    jframe.setAlwaysOnTop(false);    // As soon it is enabled, no more a top window
                }
                
                if (exception instanceof FileNotFoundException) {
                    MessagesManager messages = new MessagesManager();
                    String errorMsg = messages.getMessage("file_probably_locked") + SwingUtil.CR_LF + cryptoEngine.getEndMessage();
                    JOptionPane.showMessageDialog(jframe, errorMsg,  Parms.PRODUCT_NAME , JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPaneNewCustom.showException(jframe, exception);
                }

                return;

            } else if (openFile) {
                List<File> files = cryptoEngine.getFilesOut();
                for (File f : files) {

                    try {
                        //System.out.println(f.toString());
                        Desktop.getDesktop().open(f);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            if (jframe != null) {
                jframe.setAlwaysOnTop(true);     // Put the window in front
                jframe.setEnabled(true);         // Enable it
                jframe.setAlwaysOnTop(false);    // As soon it is enabled, no more a top window

                if (jframe instanceof MessageComposer) {
                    ((MessageComposer) jframe).setAttachmentList(cryptoEngine.getFilesOut());
                }
                jframe.getContentPane().setCursor(Cursor.getDefaultCursor());
                jframe.setCursor(Cursor.getDefaultCursor());
            }

        }
    }
}
