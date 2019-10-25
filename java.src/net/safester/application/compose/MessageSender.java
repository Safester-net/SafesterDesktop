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
package net.safester.application.compose;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;

import com.safelogic.pgp.api.KeySignatureHandlerOne;
import com.safelogic.pgp.api.PgeepPublicKey;
import com.safelogic.pgp.api.PgpActionsOne;

import net.safester.application.MessageComposer;
import net.safester.application.engines.AttachmentEncryptEngine;
import net.safester.application.engines.AttachmentEncryptEngineListener;
import net.safester.application.engines.WaiterEngine;
import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.CryptoParms;
import net.safester.application.util.crypto.CryptoUtil;
import net.safester.clientserver.PgpKeyPairLocal;
import net.safester.clientserver.PgpPublicKeyListExtractorClient;
import net.safester.clientserver.PgpPublicKeyLocal;
import net.safester.clientserver.ServerParms;
import net.safester.clientserver.holder.PgpKeyPairHolder;
import net.safester.noobs.clientserver.AttachmentLocal;
import net.safester.noobs.clientserver.MessageLocal;

public class MessageSender {

    public static boolean DEBUG = true;
    
    private Connection connection;
    private MessageLocal message;
    private JFrame caller;
    private Set<String> recipientKeyList;
    private MessagesManager messages = new MessagesManager();
    /** Crypto thread stuff */
    Timer cryptoEngineMonitor;
    WaiterEngine waiterEngine;
    ProgressMonitor progressDialog;
    AttachmentEncryptEngine cryptoEngine;
    private List<File> fileList;
    private List<PGPPublicKey> publicKeys;
    private List<String> noKeyEmails;
    
    private boolean VERIFY_PUBLIC_KEY_SGINATURE = false;
    

    public MessageSender(JFrame frame, Connection theConnection, MessageLocal theMessage, Set<String> keyList) {
        caller = frame;
        connection = theConnection;
        message = theMessage;
        recipientKeyList = keyList;
    }

    public void saveAsDraft() throws Exception {
        //this.recipientKeyList.add(this.user);
        send(true);
    }
    public void send() throws Exception {
        send(false);
    }

    private void send(boolean isDraft) throws Exception {

        debug("ENCRYPT send " + new Date());

        // Encrypt does the upload of message if there is an attachemennt
        boolean isEncrypted = encryptMessage(isDraft);

        if(!isEncrypted){
            return;
        }

        MessageComposer mailComposer = (MessageComposer) this.caller;
        connection = mailComposer.getCaller().getConnection();

        message.setIsEncrypted(true);

        if (!message.getIsWithAttachment()) {
            if(isDraft){
                //mailComposer.uploadDraft();
            }
            else{

                debug("MessageSender.putMessage() begin " + new Date());
                mailComposer.putMessage();
                debug("MessageSender.putMessage() end   " + new Date());
            }
        }

    }

    private boolean encryptMessage(boolean isDraft) throws Exception{

        // OLD CODE BEGIN
        // KEEP IT FOR DOCUMENTATION
//        PgpPublicKeyListExtractorSave pgpPublicKeyListExtractor = new PgpPublicKeyListExtractorSave(connection, this.recipientKeyList);
//        pgpPublicKeyListExtractor.initLists();
//        List<PgpPublicKeyLocal> pgpPublicKeyLocalList = pgpPublicKeyListExtractor.getList();
//
//        noKeyEmails = pgpPublicKeyListExtractor.getUnknownKeyId();
//
//        boolean externalRecipients = false;
//        if (noKeyEmails == null || (!noKeyEmails.isEmpty())) {
//            externalRecipients = true;
//        }
        // OLD CODE END
        
	PgpPublicKeyListExtractorClient pgpPublicKeyListExtractor = new PgpPublicKeyListExtractorClient(connection, this.recipientKeyList);
        List<PgpPublicKeyLocal> pgpPublicKeyLocalList = pgpPublicKeyListExtractor.getList();
        boolean externalRecipients = pgpPublicKeyListExtractor.containsUnknownKeys();
        
     //   System.out.println("externalRecipients: " + externalRecipients);
        if(!buildPublicKeyList(pgpPublicKeyLocalList, externalRecipients))
        {
            return false;
        }
        
        encryptSubjectAndBody();

        if (message.getIsWithAttachment()) {
            fileList = new Vector<File>();
            List<AttachmentLocal> attachements = message.getAttachmentLocal();

            for (AttachmentLocal attachment : attachements) {
                String attachmentName = attachment.getFileName();
                fileList.add(new File(attachmentName));
            }

            encryptAttachmentsInThread(isDraft);
        }

        return true;

    }

    private synchronized void encryptAttachmentsInThread(boolean isDraft) throws Exception{

        progressDialog = new ProgressMonitor(this.caller, null, null, 0, 100);
        progressDialog.setMillisToPopup(0); // Hyperfast popup

        // Start Waiter engine
        waiterEngine = new WaiterEngine(this.messages.getMessage("PLEASE_WAIT"));
        waiterEngine.start();
        // Start EncryptEngine
       // System.out.println("publicKeys.size()" + publicKeys.size());

        cryptoEngine = new AttachmentEncryptEngine(fileList, publicKeys, waiterEngine, caller);
        final boolean is_Draft = isDraft;
        cryptoEngineMonitor = new Timer(50, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                new AttachmentEncryptEngineListener(cryptoEngine,
                        waiterEngine,
                        cryptoEngineMonitor,
                        progressDialog,
                        message,
                        connection,
                        caller,
                        is_Draft);
            }
        });

        cryptoEngine.start();
        cryptoEngineMonitor.start();


    }

    /**
     * Encrypt both subject and body of Message
     * @throws PGPException
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws NoSuchProviderException
     */
    private void encryptSubjectAndBody()
            throws PGPException, IOException, IllegalArgumentException, NoSuchProviderException {

        PgpActionsOne pgpActions = new PgpActionsOne();
        pgpActions.setIntegrityCheck(true);

        if (CryptoParms.DO_ENCRYPT_SUBJECT) {
            String subject = message.getSubject();
            subject = pgpActions.encryptStringPgp(subject, publicKeys);
            message.setSubject(subject);
        }

        String body = message.getBody();
        body = pgpActions.encryptStringPgp(body, publicKeys);
        message.setBody(body);
    }

    /**
     * Build the list of PGPPublicKey from the list of PgpPublicKeyLocal
     * @param pgpPublicKeyLocalList
     * @throws Exception
     */
    private boolean buildPublicKeyList(List<PgpPublicKeyLocal> pgpPublicKeyLocalList, boolean addMasterKey)
            throws Exception {
    	//System.out.println("buildPublicKeyList with external set to " + addMasterKey);

        // Put in memory cache the master key
        String masterUserId = ServerParms.getMasterKeyId();
        PgpKeyPairHolder pgpKeyPairHolder = new PgpKeyPairHolder(connection, ServerParms.getMasterKeyUserNumber());
        PgpKeyPairLocal pgpMasterKeyPairLocal = pgpKeyPairHolder.get();
        String masterPublicKeyBloc = pgpMasterKeyPairLocal.getPublicKeyPgpBlock();
        
        KeySignatureHandlerOne keySignatureHandlerOne = new KeySignatureHandlerOne();

        publicKeys = new ArrayList<PGPPublicKey>();
        for (PgpPublicKeyLocal pgpPublicKeyLocal : pgpPublicKeyLocalList) {

            String pgpPublicKeyBloc = pgpPublicKeyLocal.getPublicKeyPgpBlock();

            String userId = CryptoUtil.extractUserIdFromKeyBloc(pgpPublicKeyBloc);

            if (VERIFY_PUBLIC_KEY_SGINATURE) {
                if (!keySignatureHandlerOne.verifyKeySignature(masterUserId, userId, masterPublicKeyBloc, pgpPublicKeyBloc)) {
                    String msg = messages.getMessage("signature_of_key_invalid");
                    msg = MessageFormat.format(msg, pgpMasterKeyPairLocal.getPgpKeyId());
                    JOptionPane.showMessageDialog(caller, msg);
                    return false;
                }
            }
           
            PgeepPublicKey pgeepPublicKey = pgpPublicKeyLocal.getPgeepPublicKey();
            publicKeys.add(pgeepPublicKey.getKey());
        }

        if (addMasterKey) {

            PgeepPublicKey pgeepPublicKey = pgpMasterKeyPairLocal.getPgeepPublicKey();
            publicKeys.add(pgeepPublicKey.getKey());

        }
        return true;
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
