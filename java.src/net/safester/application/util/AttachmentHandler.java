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
package net.safester.application.util;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import org.awakefw.commons.api.client.DefaultAwakeProgressManager;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.http.engine.FileDownloaderEngine;
import org.awakefw.sql.api.client.AwakeConnection;

import net.safester.application.Main;
import net.safester.application.MessageReader;
import net.safester.application.engines.AttachmentDownloadListener;
import net.safester.application.engines.WaiterEngine;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.tool.AttachmentListHandler;
import net.safester.clientserver.util.FileNameConverter;

public class AttachmentHandler {

    public static boolean EMULATION_ON = false;

    /**
     * The Jdbc connection
     */
    private Connection connection = null;

    /**
     * Sender number of message, needed to get the file on Http File Server
     */
    private int senderUserNumber = -1;

    /**
     * The ServerCallerNew instance
     */
    private AwakeFileSession awakeFileSession = null;

    private String remoteFileName;
    //private List<String>
    private FileDownloaderEngine fileDownloaderEngine;
    private WaiterEngine waiterEngine;
    private Timer fileDownloaderEngineMonitor;
    private DefaultAwakeProgressManager defaultAwakeProgressManager;

    private ProgressMonitor progressDialog;
    private MessagesManager messages = new MessagesManager();
    private JFrame parent;
    private List<String> attachmentNames;
    // private List<String> destinationNames;
    private AttachmentListHandler attachmentJListHandler = null;

    public AttachmentHandler(Connection theConnection, int senderUserNumber, JFrame caller, String attachmentFilename) {
        if (theConnection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        if (attachmentFilename == null) {
            throw new IllegalArgumentException("attachmentFilename cannot be null");
        }

        this.connection = theConnection;
        this.senderUserNumber = senderUserNumber;
        this.parent = caller;
        this.remoteFileName = attachmentFilename;
    }

    public AttachmentHandler(Connection theConnection, int senderUserNumber, JFrame caller, List<String> files) {
        if (theConnection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        if (files == null) {
            throw new IllegalArgumentException("files cannot be null");
        }

        this.connection = theConnection;
        this.senderUserNumber = senderUserNumber;
        this.parent = caller;
        this.attachmentNames = files;
    }

    public void downloadDecryptAndOpen(String localFileName) throws HeadlessException, IOException {

        //   String localFileName;
        String destinationFile = localFileName;
        if (destinationFile == null) {

            //destinationFile = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator");
            destinationFile = Parms.getSafesterTempDir();

            FileNameConverter fileNameConverter = new FileNameConverter(remoteFileName);
            destinationFile += fileNameConverter.fromServerName();
        }

        CacheFileHandler cacheFileHandler = new CacheFileHandler();
        cacheFileHandler.addCachedFile(destinationFile);
        List<String> filesToDownload = new Vector<String>();
        filesToDownload.add(remoteFileName);

        File localFile = new File(destinationFile);
        List<File> localFiles = new Vector<File>();
        localFiles.add(localFile);
        if (localFileName == null) {
            cacheFileHandler.addCachedFile(localFileName);
        }

        boolean doOpen = false;
        if (localFileName == null) {
            doOpen = true;
        }

        startAll(filesToDownload, localFiles, doOpen);
    }

    public void downloadDecryptAndOpenAll(String destinationDir) throws HeadlessException, IOException {
        List<File> localFiles = getLocalFileList(destinationDir, false);
        startAll(attachmentNames, localFiles, false);
    }

    public void downloadDecryptAll(String destinationDir) throws HeadlessException, IOException {
        List<File> localFiles = getLocalFileList(destinationDir, false);
        startAll(attachmentNames, localFiles, false);
    }

    private List<File> getLocalFileList(String destinationDir, boolean isTemp) {
        List<File> localFiles = new Vector<File>();
        CacheFileHandler cacheFileHandler = new CacheFileHandler();
        for (String filename : this.attachmentNames) {
            String localFileName;
            if (destinationDir == null) {
                //localFileName = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator");
                localFileName = Parms.getSafesterTempDir();
            } else {
                localFileName = destinationDir;
                if (!localFileName.endsWith(File.separator)) {
                    localFileName += System.getProperty("file.separator");
                }
            }

            FileNameConverter fileNameConverter = new FileNameConverter(filename);
            localFileName += fileNameConverter.fromServerName();
            File localFile = new File(localFileName);
            if (isTemp) {
                cacheFileHandler.addCachedFile(localFileName);
            }
            localFiles.add(localFile);
        }
        return localFiles;
    }

    public void setAttachmentJListHandler(AttachmentListHandler theAttachmentListHandler) {
        attachmentJListHandler = theAttachmentListHandler;
    }

    private void startAll(List<String> filesToDownload, List<File> localFiles, boolean openFile) throws HeadlessException, IOException {

        if (senderUserNumber <= 0) {
            throw new IllegalArgumentException("senderUserNumber is not set: " + senderUserNumber);
        }

        // Use a dedicated Connection to avoid overlap of result files
        Connection theConnection = ((AwakeConnection) connection).clone();

        if (theConnection instanceof AwakeConnection) {
            awakeFileSession = ((AwakeConnection)connection).getAwakeFileSession();;
        } else {
            // Nothing done in emulation mode
        }

        if (!EMULATION_ON && awakeFileSession == null) {
            throw new NullPointerException("httpFileServerSession is null!");
        }

        //System.out.println("Files to download : \n" + filesToDownload);
        final boolean openFileAfterDownload = openFile;
        progressDialog = new ProgressMonitor(parent, null, null, 0, 100);
        progressDialog.setMillisToPopup(100); // Hyperfast popup
        // Start the engine & the listener
        if (awakeFileSession == null) {
            JOptionPane.showMessageDialog(this.parent, "serverCallerNew is null");
            return;
        }
        //fileDownloaderEngine = new FileDownloaderEngine(awakeFileSession, filesToDownload, localFiles);
        defaultAwakeProgressManager = new DefaultAwakeProgressManager();
        fileDownloaderEngine = new FileDownloaderEngine(awakeFileSession, defaultAwakeProgressManager,
                filesToDownload,
                localFiles);
        waiterEngine = new WaiterEngine(this.messages.getMessage("PLEASE_WAIT"));
        waiterEngine.start();

        if (parent instanceof Main) {
            attachmentJListHandler = ((Main) parent).getAttachmentJListHandler();
        } else if (parent instanceof MessageReader) {
            attachmentJListHandler = ((MessageReader) parent).getAttachmentJListHandler();
        }
        if (attachmentJListHandler != null) {
            fileDownloaderEngineMonitor = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    new AttachmentDownloadListener(fileDownloaderEngine,
                            defaultAwakeProgressManager,
                            waiterEngine,
                            fileDownloaderEngineMonitor,
                            progressDialog,
                            attachmentJListHandler,
                            openFileAfterDownload);
                }
            });
        } else {
            throw new IllegalStateException("attachmentJListHandler is not set");
        }
        fileDownloaderEngine.start();
        fileDownloaderEngineMonitor.start();
    }
}
