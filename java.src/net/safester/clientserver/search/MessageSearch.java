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
package net.safester.clientserver.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import org.apache.commons.lang3.StringUtils;
import org.awakefw.file.api.util.HtmlConverter;

import net.safester.application.Search;
import net.safester.application.engines.MessageDownloadEngine;
import net.safester.application.engines.MessageDownloadEngineListener;
import net.safester.application.engines.WaiterEngine;
import net.safester.application.messages.MessagesManager;
import net.safester.clientserver.FolderListTransfer;
import net.safester.noobs.clientserver.MessageLocal;


/**
 * This class is used to perform all searches on messages.
 *
 * @author Alexandre Becquereau
 */
public class MessageSearch {

    /**
     * The debug flag
     */
    public static boolean DEBUG = false;

    //Connection to db
    Connection connection;
    //User number
    int userNumber;
    char[] passphrase;

    //Current type of search (default is on sender or recipient)
    short type;
    //The searched string
    String searchContent;

    //Calling JFrame
    Search caller;

    Timer cryptoEngineMonitor;
    ProgressMonitor progressDialog;
    WaiterEngine waiterEngine;

    Timer downloadEngineMonitor;
    ProgressMonitor downloadProgressDialog;
    WaiterEngine downloadWaiterEngine;
    MessageDownloadEngine downloadEngine;

    private MessageSearch thisOne;
    private boolean searchOnBody = false;

    List<Integer> folderIds = null;

    /**
     * Default constructor
     *
     * @param parent
     * @param theConnection Connection to db
     * @param theUserNumber User number of user performing search
     * @param thePassphrase
     * @param searchContent
     * @param searchOnBody if true search will be on subject or body, else only
     * subject
     * @param folderId
     */
    public MessageSearch(Search parent, Connection theConnection, int theUserNumber, char[] thePassphrase, String searchContent, boolean searchOnBody, int folderId, boolean recurseSubDir) throws SQLException {
        if (theConnection == null) {
            throw new IllegalArgumentException("Connection cannot be null.");
        }
        if (theUserNumber < 0) {
            throw new IllegalArgumentException("Invalid user number: " + theUserNumber);
        }
        if (folderId < 0) {
            throw new IllegalArgumentException("Invalid folder id: " + folderId);
        }

        this.caller = parent;
        this.connection = theConnection;
        this.userNumber = theUserNumber;
        this.passphrase = thePassphrase;
        this.searchContent = searchContent;
        this.searchOnBody = searchOnBody;

        //Get folder list ONCE in class
        this.folderIds = getFolderList(folderId, recurseSubDir);

        this.thisOne = this;
    }

    /**
     * Filter list of messages depending of searched string (if search is
     * performed on sender or recipient all messages are set in result)
     *
     * @param messages The list of messages to filter
     */
    public void filterMessages(List<MessageLocal> messages) {

        /*
        if (type == TypeOfSearch.SEARCH_ON_SENDER || type == TypeOfSearch.SEARCH_ON_RECIPIENT) {
            //List already contains search result, just display it
            displayResult(messages);
            return;
        }

        List<MessageLocal> filteredList = new ArrayList<MessageLocal>();
        //Search for string in message elements
        String quotedSearchString = Pattern.quote(searchContent);
        for (MessageLocal messageLocal : messages) {
            String subject = messageLocal.getSubject();
            boolean found = Pattern.compile(quotedSearchString, Pattern.CASE_INSENSITIVE).matcher(subject).find();

            if (!found && type == TypeOfSearch.SEARCH_ON_CONTENT) {
                //Only if search also in body
                String body = messageLocal.getBody();
                found = Pattern.compile(quotedSearchString, Pattern.CASE_INSENSITIVE).matcher(body).find();
            }
            if (found) {
                //Message match query add it to search result
                filteredList.add(messageLocal);
            }
        }

        //Display result
        displayResult(filteredList);
         */
        
        if (searchContent == null || searchContent.isEmpty()) {
            //List already contains search result, just display it
            displayResult(messages);
            return;
        }

        List<MessageLocal> filteredList = new ArrayList<>();
        //Search for string in message elements
        String quotedSearchString = Pattern.quote(searchContent);
        for (MessageLocal messageLocal : messages) {
            String subject = messageLocal.getSubject();
            subject = HtmlConverter.fromHtml(subject);
            
//            //HACK 17/12
//            List<AttachmentLocal> attachments = messageLocal.getAttachmentLocal();
//            for (AttachmentLocal attachment : attachments) {
//                String name = attachment.getFileName();
//                name = HtmlConverter.fromHtml(name);
//                attachment.setFileName(name);
//            }
           
            //System.out.println("messageLocal: " + messageLocal);

            boolean found = Pattern.compile(quotedSearchString, Pattern.CASE_INSENSITIVE).matcher(subject).find();

            if (!found && searchOnBody) {
                //Only if search also in body
                String body = messageLocal.getBody();
                body = HtmlConverter.fromHtml(body);
                
                found = Pattern.compile(quotedSearchString, Pattern.CASE_INSENSITIVE).matcher(body).find();
            }
            if (found) {
                //Message match query add it to search result
                filteredList.add(messageLocal);
            }
        }

        //Display result
        displayResult(filteredList);

    }

    /**
     * Returns a list of folders depending of original folder id and if we want
     * to recurse sub folder
     *
     * @param recurseSubDir tell if sub folders must be included
     * @return
     * @throws SQLException
     */
    private List<Integer> getFolderList(int folderId, boolean recurseSubDir) throws SQLException {
        List<Integer> folderIds = new ArrayList<>();
        if (recurseSubDir) {
            //Get all childs of folder
            FolderListTransfer folderListTransfer = new FolderListTransfer(connection, userNumber);

            debug(new Date() + "Before folderListTransfer.getAllChildren()");
            folderIds = folderListTransfer.getAllChildren(folderId, recurseSubDir);
            debug(new Date() + "After folderListTransfer.getAllChildren()");

        }
        folderIds.add(folderId);

        return folderIds;
    }

    /**
     * Download and decrypt a list of messages
     *
     * @param messageIds Message ids of messages to download
     * @throws SQLException
     */
    public void downloadAndDecryptMessages(Set<Integer> messageIds) throws SQLException {

        debug("messageIds: " + messageIds.toString());
        debug("folderIds : " + folderIds.toString());

        //Use thread
        downloadEngineMonitor = new Timer(50, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                new MessageDownloadEngineListener(downloadEngine, downloadWaiterEngine,
                        downloadEngineMonitor, downloadProgressDialog, thisOne, caller);
            }
        });

        downloadProgressDialog = new ProgressMonitor(this.caller, null, null, 0, 100);
        downloadProgressDialog.setMillisToPopup(0); // Hyperfast popup
        // Start Waiter engine
        MessagesManager messageManager = new MessagesManager();
        downloadWaiterEngine = new WaiterEngine(messageManager.getMessage("PLEASE_WAIT"));
        downloadWaiterEngine.start();

        downloadEngine = new MessageDownloadEngine(connection, downloadWaiterEngine, userNumber, passphrase, messageIds, folderIds);
        downloadEngine.start();
        // Start timer
        downloadEngineMonitor.start();

    }

    /**
     * Display result of search in caller jTable
     *
     * @param filteredList
     */
    private void displayResult(List<MessageLocal> filteredList) {

        MessagesManager messagesManager = new MessagesManager();
        if (filteredList.isEmpty()) {
            JOptionPane.showMessageDialog(caller, messagesManager.getMessage("no_matching_result_found"));
            caller.createTable(null);
            return;
        }
        caller.createTable(filteredList);
    }

    /**
     * debug tool
     */
    private void debug(String s) {
        if (DEBUG) {
            System.out.println(new Date() + " " + this.getClass().getSimpleName() + " " + s);
        }
    }

    /**
     * Extracts "email@domain.com" from "name <email@domain.com>"
     * @param searchString
     * @return 
     */
    private String extractEmailAddress(String searchString) {
        if (searchString == null) {
            return null;
        }
        
        if (! searchString.contains("<") || ! searchString.contains(">")) {
            return searchString;
        }
        
        if (searchString.indexOf("<") >= searchString.indexOf(">")) {
            return searchString;
        }
        
        searchString = StringUtils.substringAfter(searchString, "<");
        searchString = StringUtils.substringBeforeLast(searchString, ">");
        return searchString;
    }

}
