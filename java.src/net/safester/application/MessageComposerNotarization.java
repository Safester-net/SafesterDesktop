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

import java.sql.Connection;

import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;
import org.awakefw.file.api.client.AwakeFileSession;
import org.awakefw.sql.api.client.AwakeConnection;

import com.safelogic.utilx.syntax.EmailChecker;

import net.safester.application.messages.MessagesManager;
import net.safester.application.parms.Parms;
import net.safester.application.util.JOptionPaneNewCustom;

/**
 * Everything to do notarizarion with a BCC email address.
 *
 * @author Nicolas de Pomereu
 */
public class MessageComposerNotarization {

    /** Used to cache if we need to BCC notarize for this client email domain. Value is an email or "NONE" if no email to use. */
    private static String EMAIL_BCC_NOTARIZATION = null;
    
    /** if An Exception is thrown */
    private static Exception EXCEPTION = null;
    
    /**
     * Add if necessary a BCC recipient for Notarization in the the BCC textarea
     * @param rootPane
     * @param textArea
     * @param keyId
     * @param connection
     * @return 
     */
    public static JTextArea addEmailBccNotarization(JRootPane rootPane, JTextArea textArea, String keyId, Connection connection){

        // If not done in Main, do it now. Normally it's already done.
        if (EMAIL_BCC_NOTARIZATION == null) {
            setEmailBccNotarization(keyId, connection);
        }
        
        if (EXCEPTION != null) {
            JOptionPaneNewCustom.showException(rootPane, EXCEPTION, "Impossible to send the BCC Notarization email. Please contact Support.");
            return textArea;
        }
        
        // No BBC to add to textArea
        if ( EMAIL_BCC_NOTARIZATION.equals("NONE")) {
            return textArea;
        }

        String emailBccNotarization  = EMAIL_BCC_NOTARIZATION.toLowerCase();
        EmailChecker emailChecker = new EmailChecker(EMAIL_BCC_NOTARIZATION);
        if (!emailChecker.isSyntaxValid()) {
            String errorMessage = MessagesManager.get("invalid_notarization_email");
            errorMessage = errorMessage.replace("{0}", emailBccNotarization);
            JOptionPane.showMessageDialog(rootPane, errorMessage, Parms.APP_NAME, JOptionPane.ERROR_MESSAGE);
            return textArea;
        }

        if (textArea.getText() == null) {
            return textArea;
        }
        String recipientsBcc = textArea.getText();
        if (recipientsBcc.isEmpty()) {
            textArea.setText(emailBccNotarization);
        } else {
            recipientsBcc = recipientsBcc.trim();
            if (!recipientsBcc.endsWith(";")) {
                recipientsBcc += ";";
            }
            recipientsBcc += emailBccNotarization;
            textArea.setText(recipientsBcc);
        }

        return textArea;
    }

    /**
     * Get the BCC email address for notarization if set for the domain of user and
     * set it in memory.
     * email (keyId)
     *
     * @param keyId
     * @param connection
     */
    public static void setEmailBccNotarization(String keyId, Connection connection)  {

        try {
            if (keyId == null) {
                throw new NullPointerException("keyId is null!");
            }

            keyId = keyId.toLowerCase();
            String domain = StringUtils.substringAfterLast(keyId, "@");
            
            AwakeConnection awakeConnection = (AwakeConnection) connection;
            AwakeFileSession awakeFileSession = awakeConnection.getAwakeFileSession();

            String returnString  = awakeFileSession.call("net.safester.server.hosts.newapi.BccNotarizationNewApi.getEmailforDomain",
                    domain,
                    connection);
        
            EMAIL_BCC_NOTARIZATION = returnString;
            
            
        } catch (Exception e) {
            EXCEPTION = e;
        }

    }

    /**
     * @return the EMAIL_BCC_NOTARIZATION
     */
    public static String getEMAIL_BCC_NOTARIZATION() {
        return EMAIL_BCC_NOTARIZATION;
    }
    
    
}
