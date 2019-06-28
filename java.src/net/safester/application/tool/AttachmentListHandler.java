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
package net.safester.application.tool;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import net.safester.application.engines.AttachmentDecryptEngine;
import net.safester.application.engines.AttachmentDecryptEngineListener;
import net.safester.application.engines.WaiterEngine;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.util.AttachmentHandler;
import net.safester.application.util.JOptionPaneNewCustom;
import net.safester.clientserver.util.FileNameConverter;

import com.safelogic.pgp.api.engines.CryptoEngine;
import com.swing.util.SwingUtil;

public class AttachmentListHandler {

    private JList jList;
    private JFrame parent;

    /** Sender number of message, needed to get the file on Http File Server */
    private int senderUserNumber = -1;
        
    private Connection connection;

    private char[] passphrase;
      // Variables for Threaded crypto engine & progress monitor
    /** The crypto engine, aka the Thread that encrypt/decrypt */
    private CryptoEngine cryptoEngine;

    /** The Waiter Engine that displays the "Please Wait..." Message */
    private WaiterEngine waiterEngine;

    /** The Crypto Engine Monitor that wathes the thread progress */
    private Timer cryptoEngineMonitor;

    /** The Progress Dialog */
    private ProgressMonitor progressDialog;

    private String privateKeyBloc;
    private MessagesManager messagesManager = new MessagesManager();
    AttachmentListHandler thisOne;
    
    Map<Integer, Long> attachmentsSize = null;
    
    /**
     * To be used by MessageComposer only.
     *
     * @param theParent
     * @param senderUserNumber
     * @param theConnection
     * @param keyBloc
     * @param thePassphrase
     */
   public AttachmentListHandler(JFrame theParent, int senderUserNumber, Connection theConnection, String keyBloc, char[] thePassphrase) {
        parent = theParent;
        
        this.senderUserNumber = senderUserNumber;
        connection = theConnection;
        passphrase = thePassphrase;
        privateKeyBloc = keyBloc;
        thisOne = this;
    }

   /**
    * To be used by Main & MessageReader only
    * 
    * @param theParent
    * @param senderUserNumber
    * @param theJList
    * @param attachmentsSize
    * @param theConnection
    * @param keyBloc
    * @param thePassphrase
    */
    public AttachmentListHandler(JFrame theParent, int senderUserNumber, JList theJList, Map<Integer, Long> attachmentsSize, Connection theConnection, String keyBloc, char[] thePassphrase) {
        this(theParent, senderUserNumber, theConnection, keyBloc, thePassphrase);

        jList = theJList;
        this.attachmentsSize = attachmentsSize;
    }


    public void openAttach() {

        File destinationDir = new File(Parms.getSafesterTempDir());
        boolean okOpen = isSpaceEnoughOnDestinationDirForOneFile(destinationDir);
        if (! okOpen)
        {
            return;
        }

        String fileName = (String) jList.getSelectedValue();

        try {
            AttachmentHandler attachmentHandler = new AttachmentHandler(connection, senderUserNumber, this.parent, fileName);
            attachmentHandler.downloadDecryptAndOpen(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPaneNewCustom.showException(this.parent, ex);
        }
    }

    public void saveAttach() {
        
        String fileName = (String) jList.getSelectedValue();
        FileNameConverter fileNameConverter = new FileNameConverter(fileName);
        String fileNameNew = fileNameConverter.fromServerName();
        JFileChooser directoryChooser = JFileChooserFactory.getInstance();
        directoryChooser.setSelectedFile(new File(fileNameNew));
        File destinationFileName = null;
        
        if (directoryChooser.showSaveDialog(this.parent) == JFileChooser.APPROVE_OPTION) {
            destinationFileName = directoryChooser.getSelectedFile();
        } else {
            return;
        }

        File destinationDir = new File(destinationFileName.getParent());

        boolean okOpen = isSpaceEnoughOnDestinationDirForOneFile(destinationDir);
        if (! okOpen)
        {
            return;
        }
        
        try {
            AttachmentHandler attachmentHandler = new AttachmentHandler(connection, senderUserNumber,
                                                                        this.parent,
                                                                        fileName);

            attachmentHandler.downloadDecryptAndOpen(destinationFileName.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPaneNewCustom.showException(this.parent, ex);
        }

    }

    public void saveAllAttachments() {

        List<String> fileNames = new Vector<String>();
        ListModel listModel = jList.getModel();

        for (int i = 0; i < listModel.getSize(); i++) {
            String filename = (String) listModel.getElementAt(i);
            fileNames.add(filename);
        }
        JFileChooser directoryChooser = JFileChooserFactory.getInstance();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File destinationDir = null;

        if (directoryChooser.showSaveDialog(this.parent) == JFileChooser.APPROVE_OPTION) {
            destinationDir = directoryChooser.getSelectedFile();
        } else {
            return;
        }

        boolean okOpen = isSpaceEnoughOnDestinationDirForAllFiles(destinationDir);
        if (! okOpen)
        {
            return;
        }
        
        try {
            AttachmentHandler attachmentHandler = new AttachmentHandler(connection, senderUserNumber, this.parent, fileNames);
            attachmentHandler.downloadDecryptAndOpenAll(destinationDir.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPaneNewCustom.showException(this.parent, ex);
        }

    }

    public void downloadAll(List<String> files, String destinationDir)
    {
        try {
            AttachmentHandler attachmentHandler = new AttachmentHandler(connection, senderUserNumber, this.parent, files);
            attachmentHandler.setAttachmentJListHandler(thisOne);
            attachmentHandler.downloadDecryptAll(destinationDir);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPaneNewCustom.showException(this.parent, ex);
        }
    }

    public void decryptAttachments(List<File> filesToDecrypt, boolean openAfterDecryption)
    {
    	this.parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        final boolean openFile = openAfterDecryption;
      
    	cryptoEngineMonitor = new Timer(50, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                   new AttachmentDecryptEngineListener( cryptoEngine,
                   		waiterEngine,
                		cryptoEngineMonitor,
                		progressDialog,
                		connection,
                		parent,
                                openFile);
            }
        });

    	progressDialog = new ProgressMonitor(this.parent, null, null, 0, 100);
        progressDialog.setMillisToPopup(0); // Hyperfast popup

        // Start Waiter engine
        waiterEngine = new WaiterEngine(this.messagesManager.getMessage("PLEASE_WAIT"));
        waiterEngine.start();
       // System.out.println("Size of list : " + filesToDecrypt.size());
        cryptoEngine = new AttachmentDecryptEngine(filesToDecrypt, waiterEngine, this.parent, privateKeyBloc, passphrase);

        cryptoEngine.start();

        // Start timer
        cryptoEngineMonitor.start();
        
    }

    public JFrame getParent()
    {
        return parent;
    }

    /**
     * Test if a file can be download & decrypted
     * @param destinationDir    the dir where to store the file to download & decrypt
     * @return  true if there is enough free space, elase display a message ansd return false
     */
    private boolean isSpaceEnoughOnDestinationDirForOneFile(File destinationDir)
    {
        int index = jList.getSelectedIndex();
        long fileSize = attachmentsSize.get(index);
        long freeSpace = destinationDir.getFreeSpace();
        
        if (freeSpace < (fileSize * 2))
        {
            String deviceDirectory = destinationDir.toString();
            String errorMsg = messagesManager.getMessage("free_space_required_for_decryption");
            String explain = SwingUtil.getTextContent("the_file_can_not_be_decrypted");

            explain = MessageFormat.format(explain, deviceDirectory, (fileSize * 2), freeSpace);

            JOptionPane.showMessageDialog(parent, explain, errorMsg, JOptionPane.ERROR_MESSAGE);
            return false;
                
        }

        return true;
    }

    /**
     * Test if all files can be downloaded & decrypted
     * @param destinationDir    the dir where to store the files to download & decrypt
     * @return  true if there is enough free space, elase display a message ansd return false
     */
    private boolean isSpaceEnoughOnDestinationDirForAllFiles(File destinationDir) {

        long totalSize = 0;
        Set<Integer> set = attachmentsSize.keySet();

        for (Integer integer : set) {
            totalSize += attachmentsSize.get(integer);
        }

        long freeSpace = destinationDir.getFreeSpace();

        if (freeSpace < (totalSize * 2))
        {
            String deviceDirectory = destinationDir.toString();
            String errorMsg = messagesManager.getMessage("free_space_required_for_decryption");
            String explain = SwingUtil.getTextContent("the_files_list_can_not_be_decrypted");

            explain = MessageFormat.format(explain, deviceDirectory, (totalSize * 2), freeSpace);

            JOptionPane.showMessageDialog(parent, explain, errorMsg, JOptionPane.ERROR_MESSAGE);
            return false;

        }

        return true;
        
        
    }
}
