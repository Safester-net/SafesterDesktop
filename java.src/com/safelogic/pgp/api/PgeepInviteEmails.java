/**
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
package com.safelogic.pgp.api;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.UnknownServiceException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import com.safelogic.pgp.api.util.crypto.PgpUserId;
import com.safelogic.pgp.api.util.msg.LanguageManager;
import com.safelogic.pgp.api.util.parms.Parms;
import com.safelogic.pgp.apispecs.HttpTransfer;
import com.safelogic.pgp.apispecs.KeyHandler;
import com.safelogic.pgp.util.Util;

/**
 * API neeeded for invitation phasis:
 * <br> - Send email to server for invitation sending.
 *
 * @author Nicolas de Pomereu
 */
public class PgeepInviteEmails
{
    /** The debug flag */
    protected boolean DEBUG = true; //Debug.isSet(this);

    /** The error Manager */
    private ErrorManager errorMan;

    /**
     * Constructor
     */
    public PgeepInviteEmails()
    {
        errorMan = new ErrorManager();
    }

    /**
     * @return Returns tuee if the opeation is Ok.
     */
    public boolean isOperationOk()
    {
        return this.errorMan.isOperationOk();
    }

    /**
     * @return the Exception
     */
    public Exception getException()
    {
        return this.errorMan.getException();
    }

    /**
     * @return the Error Code as a generic string
     */
    public String getErrorCode()
    {
        return this.errorMan.getErrorCode();
    }

    /**
     * Get a clean error label that may be displayed to the final user
     * @return the error label
     */
    public String getErrorLabel()
    {
        return this.errorMan.getErrorLabel();
    }

    /**
     * @return the last Exception stack trace as a String.
     */
    public String getStackTrace()
    {
        return this.errorMan.getStackTrace();
    }

    /**
     * Sends the emails to server for globalinvitation send
     *
     * @param invitedEmails        the emails of the persons to invite
     * @param actionType           the type of action to be done: can be Parms.ACTION_INVITE_EMAILS or Parms.ACTION_STORE_INVITATION
     *
     * @return the Register Status send back by the server:
     * <br> - Parms.INVITE_ERROR_EMAIL_NOT_SEND : The invitation email could not be send
     * <br> - Parms.INVITE_OK_EMAIL_SEND        : Invitation is OK and email has been send
     */
    public int sendInvitationToEmails(List<String> invitedEmails, String actionType)
    {
        if (invitedEmails == null)
        {
            throw new IllegalArgumentException("invitedEmails can't be null");
        }

        KeyHandler keyHandler = new KeyHandlerOne();

        String userEmail = null;
        String userName = null;

        try
        {
            PgpUserId pgpUserId = null;
            errorMan.setOperationOk();
            String sender = keyHandler.getPrivKeyRingOwner();
            pgpUserId = new PgpUserId(sender);
            userEmail = pgpUserId.getKeyId();
            userName = pgpUserId.getUserName();
        }
        catch (Exception e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return Parms.INVITE_ERROR_EMAIL_NOT_SEND;
        }

        Map<String, String> mapRecv = new HashMap<String, String>();

        String invitedEmailsAsString = invitedEmails.toString();
        invitedEmailsAsString = invitedEmailsAsString.replace(',', ';');

        mapRecv.put(Parms.ACTION, actionType);
        mapRecv.put(Parms.USER_NAME, userName);
        mapRecv.put(Parms.USER_EMAIL, userEmail);
        mapRecv.put(Parms.INVITED_EMAILS, invitedEmailsAsString);
        mapRecv.put(Parms.USER_LANGUAGE, LanguageManager.getLanguage());

        debug("userName    : " + userName);
        debug("userEmail   : " + userEmail);
        debug("invitedEmail: " + invitedEmails);

        // Put the Map into a String
        String string = mapRecv.toString();

        // Prepare the HTTP output
        HttpTransfer httpTransfer = new HttpTransferOne();

        try
        {
            // Send!
            errorMan.setOperationOk();
            httpTransfer.send(string);
        }
        catch (ConnectException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_CONNECT_EXCEPTION, e1);
            return Parms.INVITE_ERROR_EMAIL_NOT_SEND;
        }
        catch (SocketException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e1);
            return Parms.INVITE_ERROR_EMAIL_NOT_SEND;
        }
        catch (UnknownServiceException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e1);
            return Parms.INVITE_ERROR_EMAIL_NOT_SEND;
        }
        catch (ProtocolException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e1);
            return Parms.INVITE_ERROR_EMAIL_NOT_SEND;
        }
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            return Parms.INVITE_ERROR_EMAIL_NOT_SEND;
        }

        // Ok, get the key from the stream
        String recv = httpTransfer.recv();
        debug("recv: " + recv);

        Map<String, String> map = Util.toMap(recv);

        String inviteStatus = map.get(Parms.INVITE_STATUS);

        debug("inviteStatus: " + inviteStatus + ":");

        return Integer.parseInt(inviteStatus);

    }

    /**
     * Build a List<String> using the values of a List in the format List.toString()
     * <br>
     * Values may be separated by "," or ";"
     *
     * @param string     a string in format of List.toString()
     * @return           a List<String> rebuilded from the passed String
     */
    public static List<String> toListOfStrings(String string)
    {
        if (string == null)
        {
            throw new IllegalArgumentException("Input string can't be null!");
        }

        string = string.trim();

        if (string.startsWith("["))
        {
            string = string.substring(1);
        }

        if (string.endsWith("]"))
        {
            string = string.substring(0, string.length() - 1);
        }

        List<String> list = new Vector<String>();

        // replace all "," by ';'
        string = string.replace(',', ';');

        // Rebuild a List from a string
        StringTokenizer st = new StringTokenizer(string, ";", false);

        if (st.countTokens() == 0)
        {
            return list; // return an empty Map
        }

        while (st.hasMoreTokens())
        {
            String stoken = st.nextToken();
            stoken = stoken.trim();
            list.add(stoken);
        }

        return list;
    }

    /**
     * debug tool
     */
    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
    }

}
