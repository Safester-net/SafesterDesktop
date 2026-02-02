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
package com.safelogic.pgp.api;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.UnknownServiceException;
import java.util.HashMap;
import java.util.Map;

import com.safelogic.pgp.api.util.crypto.PgpUserId;
import com.safelogic.pgp.api.util.msg.LanguageManager;
import com.safelogic.pgp.api.util.parms.Parms;
import com.safelogic.pgp.apispecs.HttpTransfer;
import com.safelogic.pgp.util.Util;

/**
 * API needed for registration phase:
 * - Send userId to server for token building.
 * - Send token + userId to server for validation.
 * @author Nicolas de Pomereu
 */
public class PgeepRegister
{
    protected boolean DEBUG = true;

    private final ErrorManager errorMan;

    public PgeepRegister()
    {
        this.errorMan = new ErrorManager();
    }

    public boolean isOperationOk()
    {
        return this.errorMan.isOperationOk();
    }

    public String getErrorCode()
    {
        return this.errorMan.getErrorCode();
    }

    public String getErrorLabel()
    {
        return this.errorMan.getErrorLabel();
    }

    public String getStackTrace()
    {
        return this.errorMan.getStackTrace();
    }

    public int sendUserIdForRegister(String userId, boolean receiveInfos)
    {
        PgpUserId pgpUserId;

        try
        {
            errorMan.setOperationOk();
            pgpUserId = new PgpUserId(userId);
        }
        catch (IllegalArgumentException e)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_USER_ID, e);
            return 0;
        }

        Map<String, String> mapRecv = new HashMap<>();
        mapRecv.put(Parms.ACTION, Parms.ACTION_UPLOAD_USERID);
        mapRecv.put(Parms.USER_NAME, pgpUserId.getUserName());
        mapRecv.put(Parms.USER_EMAIL, pgpUserId.getKeyId());
        mapRecv.put(Parms.RECEIVE_INFOS, Boolean.toString(receiveInfos));
        mapRecv.put(Parms.USER_LANGUAGE, LanguageManager.getLanguage());

        String payload = mapRecv.toString();

        HttpTransfer httpTransfer = new HttpTransferOne();

        try
        {
            errorMan.setOperationOk();
            httpTransfer.send(payload);
        }
        catch (ConnectException e)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_CONNECT_EXCEPTION, e);
            return 0;
        }
        catch (SocketException e)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e);
            return 0;
        }
        catch (UnknownServiceException e)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e);
            return 0;
        }
        catch (ProtocolException e)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e);
            return 0;
        }
        catch (IOException e)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e);
            return 0;
        }

        String recv = httpTransfer.recv();
        if (recv == null || recv.trim().isEmpty())
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, new IOException("Empty HTTP response"));
            return 0;
        }

        Map<String, String> map = Util.toMap(recv);
        String registerStatus = map.get(Parms.REGISTER_STATUS);

        debug("registerStatus: " + registerStatus + ":");

        if (registerStatus == null)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION,
                    new ProtocolException("Missing response field: " + Parms.REGISTER_STATUS));
            return 0;
        }

        try
        {
            return Integer.parseInt(registerStatus.trim());
        }
        catch (NumberFormatException e)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e);
            return 0;
        }
    }

    public boolean sendHashIdToCheck(String hashId)
    {
        Map<String, String> mapRecv = new HashMap<>();
        mapRecv.put(Parms.ACTION, Parms.ACTION_UPLOAD_HASHID);
        mapRecv.put(Parms.HASH_ID, hashId);

        String payload = mapRecv.toString();

        HttpTransfer httpTransfer = new HttpTransferOne();

        try
        {
            errorMan.setOperationOk();
            httpTransfer.send(payload);
        }
        catch (ConnectException e)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_CONNECT_EXCEPTION, e);
            return false;
        }
        catch (SocketException e)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e);
            return false;
        }
        catch (UnknownServiceException e)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e);
            return false;
        }
        catch (ProtocolException e)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e);
            return false;
        }
        catch (IOException e)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e);
            return false;
        }

        String recv = httpTransfer.recv();
        if (recv == null || recv.trim().isEmpty())
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, new IOException("Empty HTTP response"));
            return false;
        }

        Map<String, String> map = Util.toMap(recv);
        String validity = map.get(Parms.HASHID_TOKEN_VALIDITY);

        debug("hashIdTokenValidity: " + validity + ":");

        if (validity == null)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION,
                    new ProtocolException("Missing response field: " + Parms.HASHID_TOKEN_VALIDITY));
            return false;
        }

        return Boolean.parseBoolean(validity.trim());
    }

    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
    }
}
