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
package net.safester.application.photo;

import java.awt.Cursor;
import java.awt.Window;
import java.sql.Connection;

import javax.swing.JOptionPane;

import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import net.safester.application.messages.MessagesManager;
import net.safester.application.util.JOptionPaneNewCustom;

/**
 * Vault Explorer utility methods
 *
 * @author Nicolas de Pomereu
 */
public class PhotoUtil {

    /**
     * Delete the user photo
     * 
     * @param window
     * @param connection
     * @param keyId
     */
    public static void photoDelete(Window window, Connection connection, String keyId) {
	MessagesManager messagesManager = new MessagesManager();
	String message = messagesManager.getMessage("are_you_sure_delete_photo");
	String deletePhoto = messagesManager.getMessage("delete_photo");

	int response = JOptionPane.showConfirmDialog(window, message, deletePhoto, JOptionPane.YES_NO_OPTION);
	if (response != JOptionPane.YES_OPTION) {
	    return;
	}

	try {

	    window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	    // UserPhoto userPhoto = new UserPhoto();
	    // userPhoto.setUserEmail(keyId);
	    // userPhoto.delete(connection);

	    AwakeConnection awakeConnection = (AwakeConnection) connection;
	    AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

	    String methodRemote = "net.safester.server.hosts.newapi.UserPhotoNewApi.delete";
	    awakeFileSession.call(methodRemote, keyId, connection);
	    window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

	    message = messagesManager.getMessage("your_photo_has_been_deleted");
	    JOptionPane.showMessageDialog(window, message);

	} catch (Exception e) {
	    window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    JOptionPaneNewCustom.showException(window, e, "Impossible to delete photo.");
	}
    }
}
