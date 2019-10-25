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
package net.safester.noobs.clientserver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Set;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import net.safester.application.MessageDecryptor;
import net.safester.clientserver.MessageLocalStore;
import net.safester.clientserver.MessageStoreExtractor;

/**
 * Decrypts subjects on client sides and puts them back on the server
 * 
 * @author Nicolas de Pomereu
 *
 */
public class SubjectDecryptionClient {

    private static boolean DEBUG = false;

    private int userNumber = -1;
    /** The passphrase to use */
    private char[] passphrase;
    private Connection connection = null;

    /**
     * Constructor.
     * 
     * @param userNumber
     * @param connection
     */
    public SubjectDecryptionClient(int userNumber, char[] passphrase, Connection connection) {

	if (connection == null) {
	    throw new NullPointerException("connection is null!");
	}

	this.userNumber = userNumber;
	this.passphrase = passphrase;
	this.connection = connection;
    }

    public void updateSubjectsInThread() {
	Thread t = new Thread() {

	    @Override
	    public void run() {
		try {
		    updateSubjects();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};
	t.start();
    }

    /**
     * Gets from server and decrypts all encrypted subjects and puts them back in
     * clear on server
     */
    private void updateSubjects() throws Exception {

	if (! isBeforeMay2019()) {
	    return;
	}

	MessageDecryptor messageDecryptor = new MessageDecryptor(userNumber, passphrase, connection);

	AwakeFileSession awakeFileSession = ((AwakeConnection) connection).getAwakeFileSession();
	String jsonString = awakeFileSession.call("net.safester.server.SubjectDecryptionServer.getEncryptedSubjects",
		userNumber, connection);

        debug("net.safester.server.SubjectDecryptionServer.getEncryptedSubjects jsonString: " + jsonString);
        
	MessageLocalStore messageLocalStore = MessageLocalStoreTransfer.fromJson(jsonString);

        if (messageLocalStore == null) {
            return;
        }
        
	Set<Integer> messageIdSet = messageLocalStore.keySet();

	// Decrypts all subjects & put them back to store
	for (Integer messageId : messageIdSet) {

	    MessageLocal messageLocal = messageLocalStore.get(messageId);
	    String subject = messageLocal.getSubject();
	    debug("subject encrypted: " + messageLocal.getMessageId() + " " + subject);

            //subject = messageDecryptor.decrypt(subject);
            //messageLocal.setSubject(subject);
            
             String decrypedSubject = null;
            
             if (MessageStoreExtractor.isSubjectEncrypted(subject)) {
                try {
                    //decrypedSubject = messageDecryptor.decrypt(messageLocal.getSubject());
                    decrypedSubject = messageDecryptor.decrypt(subject);
                } catch (Exception exception) {
                    throw new SQLException(exception);
                }

                messageLocal.setSubject(decrypedSubject);
            }
            else {
                messageLocal.setSubject(subject);
            }
            
            
	    messageLocalStore.put(messageId, messageLocal);
	}

	if (DEBUG) {
	    debug("");
	    for (Integer messageId : messageIdSet) {
		MessageLocal messageLocal = messageLocalStore.get(messageId);
		debug("subject decrypted: " + messageLocal.getMessageId() + " " + messageLocal.getSubject());
	    }
	}

        jsonString = MessageLocalStoreTransfer.toGson(messageLocalStore);
        awakeFileSession.call("net.safester.server.SubjectDecryptionServer.setDecryptedSubjects",
                jsonString, connection);


    }

    private static boolean isBeforeMay2019() {
	Calendar calendar = Calendar.getInstance(); // this would default to now
	if (calendar.get(Calendar.MONTH) <= 4 && calendar.get(Calendar.YEAR) <= 2019) {
	    return true;
	} else {
	    return false;
	}
    }

    private void debug(String s) {
	if (DEBUG) {
	    System.out.println(s);
	}
    }

}
