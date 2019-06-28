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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import net.safester.application.parms.Parms;
import net.safester.application.util.CacheFileHandler;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.AttachmentListTransfer;
import net.safester.noobs.clientserver.AttachmentLocal;
import net.safester.noobs.clientserver.MessageLocal;

import com.safelogic.pgp.api.engines.CryptoEngine;
import com.swing.util.SwingUtil;
import java.io.FileNotFoundException;
import javax.swing.JOptionPane;
import net.safester.application.messages.MessagesManager;

public class AttachmentEncryptEngineListener {

    public AttachmentEncryptEngineListener(CryptoEngine cryptoEngine,
            WaiterEngine waiterEngine,
            Timer cryptoEngineMonitor,
            ProgressMonitor progressDialog,
            MessageLocal messageLocal,
            Connection connection,
            JFrame jframe,
            boolean isDraft) {
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
            // System.out.println("CryptoEngineListener progressDialog.isCanceled()");
            isTaskCanceled = true;

            new ThreadLocker().unlock();
        }

        // check if task is completed or canceled
        if (current >= CryptoEngine.MAXIMUM_PROGRESS + 1 || isTaskCanceled) {

            cryptoEngineMonitor.stop();
            progressDialog.close();
//            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            System.out.println("Interrupting cryptoEngine: ");
//            System.out.println("isTaskCanceled: " + isTaskCanceled);
//            System.out.println("current: " + current);
            cryptoEngine.interrupt();

            if (exception != null) {

                if (jframe != null) {
                    jframe.getContentPane().setCursor(Cursor.getDefaultCursor());
                    jframe.setCursor(Cursor.getDefaultCursor());
                    jframe.setAlwaysOnTop(true);     // Put the window in front
                    jframe.setEnabled(true);         // Enable it
                }

                if (exception instanceof FileNotFoundException) {
                    MessagesManager messages = new MessagesManager();
                    String errorMsg = messages.getMessage("file_probably_locked") + SwingUtil.CR_LF + cryptoEngine.getEndMessage();
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
                    uploadFiles(messageLocal, connection, jframe, isDraft);
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

    private void uploadFiles(MessageLocal messageLocal, Connection connection, JFrame jframe, boolean isDraft) throws SQLException {
        //Launch upload of files
        List<AttachmentLocal> attachements = messageLocal.getAttachmentLocal();
        List<AttachmentLocal> newAttachments = new Vector<AttachmentLocal>();
        for (AttachmentLocal attachment : attachements) {
            String attachmentName = attachment.getFileName();

            //no! File will not be found on PC!
            //attachmentName = StringMgr.RemoveAccent(attachmentName);
            //attachmentName = StringUtils.replaceChars(attachmentName, "&", "_");
            attachment.setFileName(attachmentName + Parms.ENCRYPTED_FILE_EXT);
            newAttachments.add(attachment);
        }
        messageLocal.setAttachmentLocal(newAttachments);
        AttachmentListTransfer attachmentListTransfer = new AttachmentListTransfer(messageLocal, connection, jframe, isDraft);
        attachmentListTransfer.putList(messageLocal.getAttachmentLocal());
    }

}
