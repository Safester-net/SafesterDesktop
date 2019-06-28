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
import java.util.List;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.search.MessageSearch;
import net.safester.noobs.clientserver.MessageLocal;

/**
 * Listener for the message download engine
 *
 * @author Alexandre Becquereau
 */
public class MessageDownloadEngineListener {

    public MessageDownloadEngineListener(MessageDownloadEngine engine,
            WaiterEngine waiterEngine,
            Timer engineMonitor,
            ProgressMonitor progressDialog,
            MessageSearch messageSearch,
            JFrame jFrame) {

        if (jFrame != null) {
            jFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            jFrame.setEnabled(false);
        }
        // Get back all values from engine
        int current = engine.getCurrent();
        String note = engine.getNote();
        Exception exception = engine.getException();

        // If current is > 0 : our engine is working.
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
            isTaskCanceled = true;
        }

        // check if task is completed or canceled
        if (current >= MessageDownloadEngine.MAXIMUM_PROGRESS || isTaskCanceled) {

            List<MessageLocal> messages = engine.getDecryptedMessages();
            engineMonitor.stop();
            progressDialog.close();

            engine.interrupt();

            jFrame.setCursor(Cursor.getDefaultCursor());
            jFrame.setEnabled(true);
            jFrame.setAlwaysOnTop(true);
            jFrame.setAlwaysOnTop(false);

            if (exception != null) {
                JOptionPaneNewCustom.showException(jFrame, exception);
                return;
            }

            if (isTaskCanceled) {
                return;
            }

            //Now that all messages are decrypted continue treatment (search or just print of result)
            //messageSearch.decryptMessageInThread(messages);
            //Now that all messages are decrypted continue treatment (search or just print of result)
            messageSearch.filterMessages(messages);
        }
    }
}
