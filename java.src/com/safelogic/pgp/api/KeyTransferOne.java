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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.UnknownServiceException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import com.safelogic.pgp.api.util.apifactory.KeyHandlerFactory;
import com.safelogic.pgp.api.util.crypto.PgpUserId;
import com.safelogic.pgp.api.util.crypto.Sha1;
import com.safelogic.pgp.api.util.msg.LanguageManager;
import com.safelogic.pgp.api.util.parms.Parms;
import com.safelogic.pgp.apispecs.HttpTransfer;
import com.safelogic.pgp.apispecs.KeyHandler;
import com.safelogic.pgp.apispecs.KeyTransfer;
import com.safelogic.pgp.apispecs.PubkeyDescriptor;
import com.safelogic.pgp.apispecs.StringTriplet;
import com.safelogic.pgp.util.Util;
import com.safelogic.pgp.util.UtilDate;
import com.safelogic.utilx.Debug;
import com.safelogic.utilx.Hex;

/**
 * @author Nicolas de Pomereu
 * <br>
 * KeyTransfer concrete implementation
 */
public class KeyTransferOne implements KeyTransfer
{
    /** The debug flag */
    protected boolean DEBUG = Debug.isSet(this);

    /** The error Manager */
    private ErrorManager errorMan;

    private static String SECRET_FOR_TOKEN = "this is a very secret 327qMy3 for token";

    /**
     * Constructor
     */
    public KeyTransferOne()
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
     * @return the Error Code as a generic string
     */
    public String getErrorCode()
    {
        return this.errorMan.getErrorCode();
    }

    /**
     * @return the Error Exception
     */
    public Exception getException()
    {
        return this.errorMan.getException();
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

    /* (non-Javadoc)
     * @see com.safelogic.pgp.apispecs.KeyTransfer#existsRemotePubKey(java.lang.String)
     */
    public boolean existsRemotePubKey(String userId)
    {
        return this.existsRemoteKey(userId, Parms.ACTION_EXISTS_PUBKEY);
    }

    /* (non-Javadoc)
     * @see com.safelogic.pgp.apispecs.KeyTransfer#existsRemotePrivKey(java.lang.String)
     */
    public boolean existsRemotePrivKey(String userId)
    {
        return this.existsRemoteKey(userId, Parms.ACTION_EXISTS_PRIVKEY);
    }

    /**
     * Search all the keys that match the passed patern
     * @param searchText                The pattern to search
     * @param includePublicKeyBlock     if true, the List will include the Public Keys Blocks in ASC format
     *
     * @return a List of keys with in the format pgpId + userId + public key block :
     *      -<br> 0xDF76253A, contact <contact@safelogic.com>, <public key block>
     *      -<br> 0xC2540EE4, nico2 <ndepomereu@safelogic.com>, <public key block>
     */
    public List<StringTriplet> getRemoteSearchedKey(String searchText, boolean includePublicKeyBlock)
    {
        if (searchText == null || searchText.equals(""))
        {
            throw new IllegalArgumentException("Search String can't be null");
        }

        String action = null;

        if (includePublicKeyBlock)
        {
            action = Parms.ACTION_SEARCH_INCLUDE_KEY_BLOK;
        }
        else
        {
            action = Parms.ACTION_SEARCH;
        }

        Map<String, String> mapRecv = new HashMap<String, String>();

        mapRecv.put(Parms.ACTION, action);
        mapRecv.put(Parms.USER_NAME, searchText);

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
            return null;
        }
        catch (SocketException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e1);
            return null;
        }
        catch (UnknownServiceException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e1);
            return null;
        }
        catch (ProtocolException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e1);
            return null;
        }
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            return null;
        }

        // Ok, test the action
        if (!httpTransfer.isSendOk())
        {
            // Build an Exception with the content of the recv stream
            Exception e = new Exception(httpTransfer.recv());

            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e);
            return null;
        }

        // Ok, get the keys list from the stream
        String recv = httpTransfer.recv();

        List<StringTriplet> foundedKeys = new Vector<StringTriplet>();

        if (!recv.startsWith("OK"))
        {
            // List is empty to say no keys have been found
            return foundedKeys;
        }

        // Ok, return all string rows
        String line = null;
        BufferedReader bufferedReader =
                new BufferedReader(new StringReader(recv));

        try
        {
            while ((line = bufferedReader.readLine()) != null)
            {
                //0xDF76253A contact <contact@safelogic.com>

                StringTriplet stringTriplet = buildTriplet(line);
                foundedKeys.add(stringTriplet);
            }
        }
        catch (IOException e)
        {
            // Should never happend
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e);
            return null;
        }

        return foundedKeys;
    }

    /**
     * Build a Triplet (Pgp Id, User Id, Public Key block) from the line
     * @param line      the line to build the triplet from
     * @return  a triplet string
     */
    private StringTriplet buildTriplet(String line)
    {
        StringTokenizer stringTokenizer = new StringTokenizer(line, ",");

        int cntToken = 0;

        String pgpId = null;
        String userId = null;
        String publicKeyBlock = null;

        while (stringTokenizer.hasMoreTokens())
        {
            String token = (String) stringTokenizer.nextToken();

            if (cntToken == 0)
            {
                pgpId = new String(token);
            }
            else if (cntToken == 1)
            {
                userId = new String(token);
            }
            else if (cntToken == 2)
            {
                try
                {
                    publicKeyBlock = new String(Util.fromBase64(token));
                }
                catch (Exception e)
                {
                    // Do nothing!
                    // Just prevent problems if a user has a key id with "name, firstname"
                    e.printStackTrace();
                }
            }

            cntToken++;
        }

        StringTriplet stringTriplet = new StringTriplet(pgpId, userId, publicKeyBlock);
        return stringTriplet;
    }

    /**
     * Test0 is a remote Public Key or Private Key exists.
     * @param userId        the User Id
     * @param action        Parms.ACTION_EXISTS_PUBKEY or Parms.ACTION_EXISTS_PRIVKEY
     */
    private boolean existsRemoteKey(String userId, String action)
    {

        if (action == null)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_PARM, null);
            return false;
        }

        if (!action.equals(Parms.ACTION_EXISTS_PUBKEY) &&
                !action.equals(Parms.ACTION_EXISTS_PRIVKEY))
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_PARM, null);
            return false;
        }

        PgpUserId pgpUserId = null;

        try
        {
            errorMan.setOperationOk();
            pgpUserId = new PgpUserId(userId);
        }
        catch (IllegalArgumentException e)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_USER_ID, e);
            return false;
        }

        String keyId = pgpUserId.getKeyId();

        Map<String, String> mapRecv = new HashMap<String, String>();

        // The action is eitheir Parms.ACTION_EXISTS_PUBKEY or Parms.ACTION_EXISTS_PRIVKEY
        mapRecv.put(Parms.ACTION, action);
        mapRecv.put(Parms.USER_EMAIL, keyId);

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
            return false;
        }
        catch (SocketException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e1);
            return false;
        }
        catch (UnknownServiceException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e1);
            return false;
        }
        catch (ProtocolException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e1);
            return false;
        }
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            return false;
        }

        // Ok, test the action
        if (!httpTransfer.isSendOk())
        {
            // Build an Exception with the content of the recv stream
            Exception e = new Exception(httpTransfer.recv());

            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e);
            return false;
        }

        // Ok, get the key from the stream
        String recv = httpTransfer.recv();

        Map<String, String> map = Util.toMap(recv);

        String keyExists = map.get(Parms.KEY_EXISTS);

        debug("KeyExists :" + keyExists + ":");

        return Boolean.parseBoolean(keyExists);
    }

    /* (non-Javadoc)
     * @see com.safelogic.pgp.apispecs.KeyTransfer#getRemoteAscPgpPrivateKey(java.lang.String, char[])
     */
    public void getRemoteAscPgpPrivateKey(String keyId, char[] passphrase)
    {
        throw new NoSuchMethodError("Method not yet implemented!");
    }

    public String getRemotePgpFingerprint(String userId)
    {

        PgpUserId pgpUserId = null;

        try
        {
            errorMan.setOperationOk();
            pgpUserId = new PgpUserId(userId);
        }
        catch (IllegalArgumentException e)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_USER_ID, e);
            return null;
        }

        String keyId = pgpUserId.getKeyId();

        Map<String, String> mapRecv = new HashMap<String, String>();

        mapRecv.put(Parms.ACTION, Parms.ACTION_DOWNLOAD_FINGERPRINT);
        mapRecv.put(Parms.USER_EMAIL, keyId);

        // Put the Map into a String
        String string = mapRecv.toString();

        System.out.println("string: " + string);

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
            return null;
        }
        catch (SocketException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e1);
            return null;
        }
        catch (UnknownServiceException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e1);
            return null;
        }
        catch (ProtocolException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e1);
            return null;
        }
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            return null;
        }

        // Ok, test the action
        if (!httpTransfer.isSendOk())
        {
            // Build an Exception with the content of the recv stream
            Exception e = new Exception(httpTransfer.recv());

            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e);
            return null;
        }

        // Ok, get the key from the stream
        String recv = httpTransfer.recv();

        Map<String, String> map = Util.toMap(recv);

        String fingerprint = map.get(Parms.FINGERPRINT);

        System.out.println("fingerprint: " + fingerprint);

        return fingerprint;

    }

    /**
     * Download the PGP public key from the remote host keyring in armor format as String and
     * return it
     *
     * @param userId           PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     */
    public String getRemoteAscPgpPublicKeyAsAsc(String userId)
    {

        PgpUserId pgpUserId = null;

        try
        {
            errorMan.setOperationOk();
            pgpUserId = new PgpUserId(userId);
        }
        catch (IllegalArgumentException e)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_USER_ID, e);
            return null;
        }

        String keyId = pgpUserId.getKeyId();

        Map<String, String> mapRecv = new HashMap<String, String>();

        mapRecv.put(Parms.ACTION, Parms.ACTION_DOWNLOAD_PUBKEY);
        mapRecv.put(Parms.USER_EMAIL, keyId);

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
            return null;
        }
        catch (SocketException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e1);
            return null;
        }
        catch (UnknownServiceException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e1);
            return null;
        }
        catch (ProtocolException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e1);
            return null;
        }
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            return null;
        }

        // Ok, test the action
        if (!httpTransfer.isSendOk())
        {
            // Build an Exception with the content of the recv stream
            Exception e = new Exception(httpTransfer.recv());

            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e);
            return null;
        }

        // Ok, get the key from the stream
        String recv = httpTransfer.recv();

        Map<String, String> map = Util.toMap(recv);

        String publicKeyBlock = map.get(Parms.PUBLIC_KEY_BLOCK);

        DEBUG = true;

        // It's in Base64, so decode!
        try
        {
            debug("publicKeyBlock before base64 decode:" + publicKeyBlock);

            publicKeyBlock = Util.fromBase64(publicKeyBlock);

            debug("publicKeyBlock after  base64 decode:" + publicKeyBlock);
        }
        catch (Exception e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return null;
        }

        return publicKeyBlock;
    }

    /* (non-Javadoc)
     * @see com.safelogic.pgp.apispecs.KeyTransfer#getRemoteAscPgpPublicKey(java.lang.String)
     */
    public void getRemoteAscPgpPublicKey(String userId)
    {

        PgpUserId pgpUserId = null;

        try
        {
            errorMan.setOperationOk();
            pgpUserId = new PgpUserId(userId);
        }
        catch (IllegalArgumentException e)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_USER_ID, e);
            return;
        }

        String keyId = pgpUserId.getKeyId();

        Map<String, String> mapRecv = new HashMap<String, String>();

        mapRecv.put(Parms.ACTION, Parms.ACTION_DOWNLOAD_PUBKEY);
        mapRecv.put(Parms.USER_EMAIL, keyId);

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
            return;
        }
        catch (SocketException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e1);
            return;
        }
        catch (UnknownServiceException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e1);
            return;
        }
        catch (ProtocolException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e1);
            return;
        }
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            return;
        }

        // Ok, test the action
        if (!httpTransfer.isSendOk())
        {
            // Build an Exception with the content of the recv stream
            Exception e = new Exception(httpTransfer.recv());

            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e);
            return;
        }

        // Ok, get the key from the stream
        String recv = httpTransfer.recv();

        Map<String, String> map = Util.toMap(recv);

        String publicKeyBlock = map.get(Parms.PUBLIC_KEY_BLOCK);

        // It's in Base64, so decode!
        try
        {
            publicKeyBlock = Util.fromBase64(publicKeyBlock);

            debug("publicKeyBlock:" + publicKeyBlock);
        }
        catch (Exception e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }

        KeyHandler keyHandler = KeyHandlerFactory.getInstance();

        try
        {
            keyHandler.importPgpPublicKeyFromAsc(publicKeyBlock);
        }
        catch (NoSuchAlgorithmException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }
        catch (KeyException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }
        catch (IOException e)
        {
            errorMan.setErrorCode(Parms.ERR_IO_EXCEPTION, e);
            return;
        }

    }

    /* (non-Javadoc)
     * @see com.safelogic.pgp.apispecs.KeyTransfer#putRemoteAscPgpPrivateKey(java.lang.String, char[])
     */
    public void putRemoteAscPgpPrivateKey(String keyId, char[] passphrase)
    {
        throw new NoSuchMethodError("Method not yet implemented!");
    }

    /* (non-Javadoc)
     * @see com.safelogic.pgp.apispecs.KeyTransfer#putRemoteAscPgpPublicKey(java.lang.String)
     */
    public void putRemoteAscPgpPublicKey(String userId, boolean stealthMode, boolean receiveInfos)
    {

        KeyHandler kh = KeyHandlerFactory.getInstance();
        PublicKey pubKey = null;

        try
        {
            // Get the PGP Public Key as a Java Public Key
            pubKey = kh.getPgpPublicKey(userId);
        }
        catch (FileNotFoundException e)
        {
            errorMan.setErrorCode(Parms.ERR_PUBLIC_KEY_FILE_NOT_FOUND, e);
            return;
        }
        catch (IllegalArgumentException e)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_USER_ID, e);
            return;
        }
        catch (KeyException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }
        catch (NoSuchAlgorithmException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }
        catch (IOException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }

        PubkeyDescriptor pubkeyDescriptor = new PubkeyDescriptorOne(pubKey);

        String fingerprint = pubkeyDescriptor.getFingerprint();
        String keyType = pubkeyDescriptor.getType();
        int keyLength = pubkeyDescriptor.getLength();
        String symAlgo = pubkeyDescriptor.getSymetricAlgorithm();
        Date dateCreate = pubkeyDescriptor.getCreationDate();
        Date dateExpire = pubkeyDescriptor.getExpirationDate();

        String sDateCreate = UtilDate.formatDate(dateCreate);
        String sDateExpire = UtilDate.formatDate(dateExpire);

        // Get the Key Id:
        PgpUserId pgpUserId = new PgpUserId(userId);
        String UserName = pgpUserId.getUserName();
        String keyId = pgpUserId.getKeyId();

        debug("UserName   : " + UserName);
        debug("fingerprint: " + fingerprint);
        debug("keyLength  : " + keyLength);
        debug("SymAlgo    : " + symAlgo);
        debug("dateCreate : " + dateCreate);
        debug("dateExpire : " + dateExpire);
        debug("sDateCreate: " + sDateCreate);
        debug("sDateExpire: " + sDateExpire);

        // Create a store for the public key infos
        Map<String, String> map = new HashMap<String, String>();

        String userNameHex = null;
        try
        {
            userNameHex = Hex.toString(UserName.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            userNameHex = Hex.toString(UserName.getBytes());
        }

        map.put("user_name", Util.toBase64(userNameHex)); // There may be accents in name

        map.put(Parms.KEY_ID, keyId);
        map.put(Parms.FINGERPRINT, fingerprint);
        map.put(Parms.KEY_TYPE, keyType);
        map.put(Parms.KEY_LENGTH, new Integer(keyLength).toString());
        map.put(Parms.SYM_ALGORITHM, symAlgo);
        map.put(Parms.DT_CREATE, sDateCreate);
        map.put(Parms.DT_EXPIRE, sDateExpire);

        // Central Directory Infos
        map.put(Parms.STEALTH_MODE, Boolean.toString(receiveInfos));
        map.put(Parms.RECEIVE_INFOS, Boolean.toString(stealthMode));
        map.put(Parms.LANGUAGE, LanguageManager.getLanguage());

        // Ok, now export the public key block
        this.putRemoteAscPgpPublicKey(Parms.ACTION_UPLOAD_PUBKEY_NEW, userId, map);
    }

    /**
     * Upload the PGP public key from the local keyring to the remote host
     * keyring in armor format as String.
     *
     * The sha-1(email address) is signed to verify the true identity of the request
     *
     * @param userId                    PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * @param hashEmailDetachedSigned   The detached signature signature of the hashEmail
     */
    public void putRemoteAscSignedPgpPublicKey(String userId, String hashEmailDetachedSigned)
    {
        KeyHandler kh = KeyHandlerFactory.getInstance();
        PublicKey pubKey = null;

        try
        {
            // Get the PGP Public Key as a Java Public Key
            pubKey = kh.getPgpPublicKey(userId);
        }
        catch (FileNotFoundException e)
        {
            errorMan.setErrorCode(Parms.ERR_PUBLIC_KEY_FILE_NOT_FOUND, e);
            return;
        }
        catch (IllegalArgumentException e)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_USER_ID, e);
            return;
        }
        catch (KeyException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }
        catch (NoSuchAlgorithmException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }
        catch (IOException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }

        // Get the Key Id:
        PgpUserId pgpUserId = new PgpUserId(userId);
        String keyId = pgpUserId.getKeyId();

        // Create a store for the public key infos
        Map<String, String> map = new HashMap<String, String>();

        // Put in the map the email address & signed(sha-1(email address)
        map.put("key_id", keyId);
        map.put("hash_email_detached_signed", Util.toBase64(hashEmailDetachedSigned));

        // Ok, now export the public key block
        this.putRemoteAscPgpPublicKey(Parms.ACTION_UPLOAD_SIGNED_PUBKEY, userId, map);
    }

    /**
     *
     * Put a Public Key on host with options, depending on the action
     * <br> - Parms.ACTION_UPLOAD_PUBKEY_NEW        : Upload a new Public Key
     * <br> - Parms.ACTION_UPLOAD_SIGNED_PUBKEY     : Modify a self Signed Public Key (Photo, ...)
     * <br> - Parms.ACTION_UPLOAD_SERVER_SETTINGS   : Modify the server settings
     * <br> - Parms.ACTION_DELETE_SIGNED_PUBKEY     : Delete Public Key from the server
     *
     * @param action       the action of the upload
     * @param userId       the PGP User Id
     * @param keyParms     the Map of key params
     */
    private void putRemoteAscPgpPublicKey(String action, String userId, Map<String, String> keyParms)
    {
        if (action == null)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_PARM, null);
            return;
        }

        if (!action.equals(Parms.ACTION_UPLOAD_PUBKEY_NEW)
                && !action.equals(Parms.ACTION_UPLOAD_SIGNED_PUBKEY)
                && !action.equals(Parms.ACTION_DELETE_SIGNED_PUBKEY)
                && !action.equals(Parms.ACTION_UPLOAD_SERVER_SETTINGS))
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_PARM, null);
            return;
        }

        if (userId == null)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_USER_ID, null);
            return;
        }

        if (keyParms == null)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_PARM, null);
            return;
        }

        // Get a Key handler
        KeyHandler keyHandler = KeyHandlerFactory.getInstance();

        // Retrieve from Key Handler the ASC public key for this key Id
        String PublicKeyBlock = null;

        try
        {
            errorMan.setOperationOk();
            PublicKeyBlock = keyHandler.exportAscPgpPublicKey(userId);
        }
        catch (FileNotFoundException e)
        {
            errorMan.setErrorCode(Parms.ERR_PUBLIC_KEY_FILE_NOT_FOUND, e);
            return;
        }
        catch (IllegalArgumentException e)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_USER_ID, e);
            return;
        }
        catch (KeyException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }
        catch (NoSuchAlgorithmException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }
        catch (IOException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }

        // Add the base action & Add the Public Key Block
        keyParms.put(Parms.ACTION, action);
        keyParms.put(Parms.PUBLIC_KEY_BLOCK, Util.toBase64(PublicKeyBlock));

        // Put the Map into a String
        String string = keyParms.toString();

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
            return;
        }
        catch (SocketException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e1);
            return;
        }
        catch (UnknownServiceException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e1);
            return;
        }
        catch (ProtocolException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e1);
            return;
        }
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            return;
        }

        // Ok, test the action
        if (!httpTransfer.isSendOk())
        {
            // Build an Exception with the content of the recv stream
            Exception e = new Exception(httpTransfer.recv());

            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e);
            return;
        }

        return; // Victory! Key is now on server!
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

    public List<String> getRemoteKeyByDate(String date)
    {

        if (date == null || date.equals(""))
        {
            throw new IllegalArgumentException("Date can't be null");
        }

        Map<String, String> mapRecv = new HashMap<String, String>();

        mapRecv.put(Parms.ACTION, Parms.ACTION_SEARCH_DATE);
        mapRecv.put(Parms.DT_CREATE, date);

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
            return null;
        }
        catch (SocketException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e1);
            return null;
        }
        catch (UnknownServiceException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e1);
            return null;
        }
        catch (ProtocolException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e1);
            return null;
        }
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            return null;
        }

        // Ok, test the action
        if (!httpTransfer.isSendOk())
        {
            // Build an Exception with the content of the recv stream
            Exception e = new Exception(httpTransfer.recv());

            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e);
            return null;
        }

        // Ok, get the keys list from the stream
        String recv = httpTransfer.recv();

        List<String> foundedKeyIds = new Vector<String>();

        if (!recv.startsWith("OK"))
        {
            // List is empty to say no keys have been found
            return foundedKeyIds;
        }

        String line = null;
        BufferedReader bufferedReader =
                new BufferedReader(new StringReader(recv));

        try
        {
            while ((line = bufferedReader.readLine()) != null)
            {
                foundedKeyIds.add(line);
            }
        }
        catch (IOException e)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e);
            return null;
        }

        return foundedKeyIds;
    }

    public void deleteKey(String keyId)
            throws NoSuchAlgorithmException, NoSuchProviderException
    {

        Map<String, String> mapRecv = new HashMap<String, String>();

        mapRecv.put(Parms.ACTION, Parms.ACTION_DELETE_PUBKEY);
        mapRecv.put(Parms.USER_NAME, keyId);

        //Build a token
        Sha1 hashcode = new Sha1();
        String baseToken = keyId + SECRET_FOR_TOKEN;
        String token = hashcode.getHexHash(baseToken.getBytes());
        mapRecv.put(Parms.TOKEN, token);

        String string = mapRecv.toString();

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
        }
        catch (SocketException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e1);
        }
        catch (UnknownServiceException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e1);
        }
        catch (ProtocolException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e1);
        }
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
        }

        // Ok, test the action
        if (!httpTransfer.isSendOk())
        {
            // Build an Exception with the content of the recv stream
            Exception e = new Exception(httpTransfer.recv());
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e);
        }
    }

    public String getEvalStatus(String keyId, Timestamp today)
    {
        String status = null;
        Map<String, String> mapRecv = new HashMap<String, String>();

        String sToday = UtilDate.formatDate(today);
        mapRecv.put(Parms.ACTION, Parms.EVAL_STATUS);
        mapRecv.put(Parms.KEY_ID, keyId);
        mapRecv.put(Parms.DATE, sToday);

        String string = mapRecv.toString();
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
        }
        catch (SocketException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e1);
        }
        catch (UnknownServiceException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e1);
        }
        catch (ProtocolException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e1);
        }
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
        }

        // Ok, test the action
        if (!httpTransfer.isSendOk())
        {
            Exception e = new Exception(httpTransfer.recv());
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e);
            return null;
        }

        // Ok, get the status from the stream
        String recv = httpTransfer.recv();

        Map<String, String> map = Util.toMap(recv);
        if (map == null)
        {
            System.out.println("MAP IS NULL");
        }
        Iterator it = map.keySet().iterator();
        while (it.hasNext())
        {
            System.out.println("MAP KEY: " + it.next());
        }
        status = map.get(Parms.EVAL_STATUS);

        return status;
    }

    public String getEvalLicence(String keyId)
    {
        String licenceFile = null;
        Map<String, String> mapRecv = new HashMap<String, String>();

        mapRecv.put(Parms.ACTION, Parms.GET_EVAL_LICENCE);
        mapRecv.put(Parms.KEY_ID, keyId);

        String string = mapRecv.toString();
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
        }
        catch (SocketException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e1);
        }
        catch (UnknownServiceException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e1);
        }
        catch (ProtocolException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e1);
        }
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
        }

        // Ok, test the action
        if (!httpTransfer.isSendOk())
        {
            return null;
        }
        String recv = httpTransfer.recv();

        Map<String, String> map = Util.toMap(recv);

        licenceFile = map.get(Parms.LICENSE);
        return licenceFile;
    }

    /**
     * Get the remote key Server Settings into a ServerSettingsContainer Object
     *
     * @param userId        the User Id
     * @return a Server Settings Container with the boolean info : keep on central dir, stealth moden receive infos
     *
     */
    public ServerSettingsContainer getRemotePrivacySettings(String userId)
    {

        PgpUserId pgpUserId = null;

        try
        {
            errorMan.setOperationOk();
            pgpUserId = new PgpUserId(userId);
        }
        catch (IllegalArgumentException e)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_USER_ID, e);
            return null;
        }

        String keyId = pgpUserId.getKeyId();

        Map<String, String> mapRecv = new HashMap<String, String>();

        mapRecv.put(Parms.ACTION, Parms.ACTION_GET_SERVER_SETTINGS);
        mapRecv.put(Parms.KEY_ID, keyId);

        String string = mapRecv.toString();

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
            return null;
        }
        catch (SocketException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_SOCKET_EXCEPTION, e1);
            return null;
        }
        catch (UnknownServiceException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION, e1);
            return null;
        }
        catch (ProtocolException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_PROTOCOL_EXCEPTION, e1);
            return null;
        }
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            return null;
        }

        // Ok, test the action
        if (!httpTransfer.isSendOk())
        {
            Exception e = new Exception(httpTransfer.recv());

            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e);
            return null;
        }

        String recv = httpTransfer.recv();

        Map<String, String> map = Util.toMap(recv);

        String keepKeyOnCentralDir = map.get(Parms.KEEP_KEY_ON_CENTRAL_DIR);

        ServerSettingsContainer serverSettingsContainer = new ServerSettingsContainer();

        if (keepKeyOnCentralDir.equals("true"))
        {
            serverSettingsContainer.setKeepKeyOnCentralDir(true);

            String stealthMode = map.get(Parms.STEALTH_MODE);
            String receiveInfos = map.get(Parms.RECEIVE_INFOS);

            serverSettingsContainer.setStealthMode(false);
            serverSettingsContainer.setReceiveInfos(false);

            if (stealthMode.equals("true"))
            {
                serverSettingsContainer.setStealthMode(true);
            }

            if (receiveInfos.equals("true"))
            {
                serverSettingsContainer.setReceiveInfos(true);
            }

        }
        else
        {
            serverSettingsContainer.setKeepKeyOnCentralDir(false);
            serverSettingsContainer.setStealthMode(false);
            serverSettingsContainer.setReceiveInfos(false);
        }

        return serverSettingsContainer;
    }

    /**
     * Set the remote key Server Settings using a ServerSettingsContainer Object
     *
     * @param userId                    PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * @param hashEmailDetachedSigned   The detached signature signature of the hashEmail
     * @param serverSettingsContainer   Server Settings Container with the boolean info : keep on central dir, stealth mode, receive info
     *
     */
    public void setRemotePrivacySettings(String userId, String hashEmailDetachedSigned, ServerSettingsContainer serverSettingsContainer)
    {

        KeyHandler kh = KeyHandlerFactory.getInstance();
        PublicKey pubKey = null;

        try
        {
            // Get the PGP Public Key as a Java Public Key
            pubKey = kh.getPgpPublicKey(userId);
        }
        catch (FileNotFoundException e)
        {
            errorMan.setErrorCode(Parms.ERR_PUBLIC_KEY_FILE_NOT_FOUND, e);
            return;
        }
        catch (IllegalArgumentException e)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_USER_ID, e);
            return;
        }
        catch (KeyException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }
        catch (NoSuchAlgorithmException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }
        catch (IOException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }

        PgpUserId pgpUserId = new PgpUserId(userId);
        String keyId = pgpUserId.getKeyId();

        Map<String, String> map = new HashMap<String, String>();

        map.put("key_id", keyId);
        map.put("hash_email_detached_signed", Util.toBase64(hashEmailDetachedSigned));

        Boolean keepKeyOnCentralDir = serverSettingsContainer.isKeepKeyOnCentralDir();
        Boolean stealthMode = serverSettingsContainer.isStealthMode();
        Boolean receiveInfos = serverSettingsContainer.isReceiveInfos();

        map.put(Parms.KEEP_KEY_ON_CENTRAL_DIR, keepKeyOnCentralDir.toString());
        map.put(Parms.STEALTH_MODE, stealthMode.toString());
        map.put(Parms.RECEIVE_INFOS, receiveInfos.toString());

        this.putRemoteAscPgpPublicKey(Parms.ACTION_UPLOAD_SERVER_SETTINGS, userId, map);

    }

    /**
     * Delete the remote Public Key. Sign it before to identify the requestor.
     *
     * @param userId                    PGP UserId - User name and email : "Name Firstname <email@domaine.com>"
     * @param hashEmailDetachedSigned   The detached signature signature of the hashEmail
     */
    public void deleteRemoteAscSignedPgpPublicKey(String userId, String hashEmailDetachedSigned)
    {
        KeyHandler kh = KeyHandlerFactory.getInstance();
        PublicKey pubKey = null;

        try
        {
            pubKey = kh.getPgpPublicKey(userId);
        }
        catch (FileNotFoundException e)
        {
            errorMan.setErrorCode(Parms.ERR_PUBLIC_KEY_FILE_NOT_FOUND, e);
            return;
        }
        catch (IllegalArgumentException e)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_USER_ID, e);
            return;
        }
        catch (KeyException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }
        catch (NoSuchAlgorithmException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }
        catch (IOException e)
        {
            errorMan.setErrorCode(Parms.ERR_SYSTEM_EXCEPTION, e);
            return;
        }

        PgpUserId pgpUserId = new PgpUserId(userId);
        String keyId = pgpUserId.getKeyId();

        Map<String, String> map = new HashMap<String, String>();

        map.put("key_id", keyId);
        map.put("hash_email_detached_signed", Util.toBase64(hashEmailDetachedSigned));

        this.putRemoteAscPgpPublicKey(Parms.ACTION_DELETE_SIGNED_PUBKEY, userId, map);
    }

}
