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
package net.safester.application;

import com.kawansoft.httpclient.KawanHttpClient;
import java.awt.TrayIcon;
import java.sql.Connection;
import java.util.Date;
import static net.safester.application.Main.main;
import net.safester.application.http.ApiMessages;
import net.safester.application.http.KawanHttpClientBuilder;
import net.safester.application.http.dto.MessageHeaderDTO;

import org.awakefw.file.api.util.HtmlConverter;

import net.safester.application.messages.MessagesManager;
import net.safester.application.util.UserPrefManager;
import net.safester.clientserver.MessageLocalStore;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

/**
 * 
 * Notify user
 * 
 * @author Nicolas de Pomereu
 *
 */
public class MainNotifier {

    public static boolean DEBUG = false;
        
    private Main main = null;
    private CryptTray cryptTray = null;
    private int userNumber = -1;
    private Connection connection = null;

    /**
     * Constructor.
     * 
     * @param messageLocalStore
     * @param cryptTray
     * @param userNumber
     * @param connection
     */
    public MainNotifier(Main main, CryptTray cryptTray, int userNumber, Connection connection) {

        this.main = main;
	this.cryptTray = cryptTray;
	this.userNumber = userNumber;
	this.connection = connection;
        
        debug("Constructor called!");
    }

    // Notify if necessary
    public void notifyNewInbox() {

	final boolean popUpOnTaskbar = !UserPrefManager.getBooleanPreference(
		UserPrefManager.NOTIFY_NO_POPUP_ON_TASKBAR);
	final boolean playSound = !UserPrefManager
		.getBooleanPreference(UserPrefManager.NOTIFY_NO_PLAY_SOUND);

        debug("popUpOnTaskbar: " + popUpOnTaskbar);
        debug("playSound     : " + playSound);
                
	if (!popUpOnTaskbar && !playSound) {
	    return;
	}

	Thread t = new Thread() {
	    @Override
	    public void run() {
		try {

		    MainNotifierServerInfo mainNotifierServerInfo = new MainNotifierServerInfo(
			    userNumber, connection);
		    int lastMessageId = mainNotifierServerInfo
			    .getLastMessageId();
		    // Case no new message
		    if (lastMessageId <= 0) {
			return;
		    }
                    
                    AwakeConnection awakeConnection = (AwakeConnection)connection;
                    AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

                    KawanHttpClient kawanHttpClient = KawanHttpClientBuilder.buildFromAwakeConnection(awakeConnection);
                    ApiMessages apiMessages = new ApiMessages(kawanHttpClient, awakeFileSession.getUsername(),
                            awakeFileSession.getAuthenticationToken());

                    MessageHeaderDTO messageHeaderDTO = apiMessages.getMessageHeader(lastMessageId);
                    
		    if (messageHeaderDTO != null) {
			String caption = MessagesManager
				.get("safester_new_message_from") + " "
				+ messageHeaderDTO.getSenderName();
			String subject = messageHeaderDTO.getSubject();
			if (subject == null) {
			    subject = "";
			}
                        
                        main.getIncomingMessage();

			if (popUpOnTaskbar) {
			    if (CryptTray.isSupported() && cryptTray != null) {
				TrayIcon trayIcon = cryptTray.getTrayIcon();

                                subject = HtmlConverter.fromHtml(subject);
                                        
				trayIcon.displayMessage(caption, subject,
					TrayIcon.MessageType.INFO);
			    }
			}

			if (playSound) {
			    try {
				SoundChooser.playNotifySound();
			    } catch (Exception iOException) {
				iOException.printStackTrace();
			    }
			}

		    } else {
			System.err.println("No MessageLocal for message id: "
				+ lastMessageId);
		    }

		} catch (Exception ex) {
		    ex.printStackTrace();
		}
	    }

	};
	t.start();
    }

    /**
     * debug tool
     */
    private void debug(String s) {
        if (DEBUG) {
            System.out.println(new Date() + " " + MainNotifier.class.getName() + " " + s);
        }
    }
}
