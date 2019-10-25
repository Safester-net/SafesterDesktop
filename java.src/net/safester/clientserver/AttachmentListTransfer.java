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
package net.safester.clientserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import net.safester.application.engines.AttachmentTransferListener;
import net.safester.application.engines.WaiterEngine;
import net.safester.application.messages.MessagesManager;
import net.safester.application.util.CacheFileHandler;
import net.safester.clientserver.specs.ListTransfer;
import net.safester.clientserver.util.FileNameConverter;
import net.safester.noobs.clientserver.AttachmentLocal;
import net.safester.noobs.clientserver.MessageLocal;

import org.apache.commons.lang3.StringUtils;
import org.awakefw.commons.api.client.DefaultAwakeProgressManager;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.file.http.engine.FileUploaderEngine;
import org.awakefw.sql.api.client.AwakeConnection;

/**
 * @author Nicolas de Pomereu
 * 
 * Transfer a AttachmentLocal list (aka the files) between PC <==> SQL Server
 * within a thread
 */
public class AttachmentListTransfer implements ListTransfer<AttachmentLocal>
{      
    /** The debug flag */
    public static boolean DEBUG = true;
    
    private Connection connection = null;
        
    /** The ServerCallerNew instance */
    private AwakeFileSession awakeFileSession = null;

    /** Engine stuff*/

    private MessagesManager messages = new MessagesManager();

    /** Calling JFrame */
    private JFrame parent;

    /** All engine object (engine itself, monitor, listener etc.)*/
    private FileUploaderEngine fileUploaderEngine;
    private WaiterEngine waiterEngine;
    private Timer fileUploaderEngineMonitor;
    private ProgressMonitor progressDialog;
    private DefaultAwakeProgressManager defaultAwakeProgressManager;

    private boolean isDraft;
    private final MessageLocal messageLocal;

    
    /**
     * Default Constructor. To be used for uploads.
     * <br>
     * @param theConnection        the JDBC connection
     */
    public AttachmentListTransfer(MessageLocal messageLocal, Connection theConnection, JFrame parentFrame, boolean isDraft)
    {
        if (theConnection == null)
        {
            throw new IllegalArgumentException("Connection can\'t be null");
        }

        this.messageLocal = messageLocal;
        this.connection = theConnection;
        this.parent = parentFrame;
        this.isDraft = isDraft;
    }
    
//    /**
//     * Constructor to be used for downloads.
//     * <br>
//     * @param connection        the JDBC connection
//     */
//    public AttachmentListTransfer(Connection connection, int messageId, JFrame parentFrame)
//    {
//        this(connection, parentFrame);
//        this.messageId = messageId;
//    }    

   /**
    * Get an Attachment from the server using the criteria: Message Id & User Number
     * @throws java.sql.SQLException
    */
    @Override
    public List<AttachmentLocal> getList()
        throws SQLException
    {
        throw new IllegalArgumentException("Method not implemented.");
    }
    
    @Override
    public void putList(List<AttachmentLocal> attachmentLocals) 
        throws SQLException
    {               
        // Use a dedicated Connection to avoid overlap of result files
        Connection theConnection = ((AwakeConnection)connection).clone();

        if (theConnection instanceof AwakeConnection)
        {
            awakeFileSession = ((AwakeConnection)connection).getAwakeFileSession();
        }
        else
        {
            // Nothing done in emulation mode
        }
        

        List<File> files = new Vector<File>();           
        List<String> remoteFilesPath = new Vector<String>();
        CacheFileHandler cacheFileHandler = new CacheFileHandler();
        
        for (AttachmentLocal attachmentLocal : attachmentLocals)
        {

            String attachmentName = attachmentLocal.getFileName();
            File attachmentFile = new File(attachmentName);
            System.out.println("AttachmentFile : " + attachmentName);
            cacheFileHandler.addCachedFile(attachmentName);
                        
            // Build the file name on the host & put back this name in the AttachmentLocal
            String fileName =attachmentLocal.getFileName();
            FileNameConverter fileNameConverter = new FileNameConverter(fileName);

            int senderUserNumber = messageLocal.getSenderUserNumber();
            String remoteFilePath = fileNameConverter.toServerName(senderUserNumber);

            remoteFilePath = StringUtils.replaceChars(remoteFilePath, "&", "_");
            
            attachmentLocal.setRemoteFileName(remoteFilePath);
            
            files.add(attachmentFile);
            remoteFilesPath.add(remoteFilePath);
            
            debug("");
            debug("local  attachmentFile: " + attachmentFile);
            debug("remote remoteFilePath: " + remoteFilePath);
            
        }
        
        // And now, start the listener & the engine
        
        // Launch progress dialog
        progressDialog = new ProgressMonitor(parent, null, null, 0, 100);
        progressDialog.setMillisToPopup(100); // Hyperfast popup

        defaultAwakeProgressManager = new DefaultAwakeProgressManager();
        // Start the engine & the listener
        fileUploaderEngine = new FileUploaderEngine( 
                awakeFileSession,
                defaultAwakeProgressManager,
                files,
                remoteFilesPath
                );

        waiterEngine = new WaiterEngine(this.messages.getMessage("PLEASE_WAIT"));
        waiterEngine.start();
        fileUploaderEngineMonitor = new Timer(500, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                new AttachmentTransferListener(fileUploaderEngine,
                                                defaultAwakeProgressManager,
                                                waiterEngine,
                                                fileUploaderEngineMonitor,
                                                progressDialog,
                                                parent,
                                                isDraft);
            }
        });

       fileUploaderEngine.start();
       fileUploaderEngineMonitor.start();
       
    }
    
    /**
     * debug tool
     */
    private void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

    
}


