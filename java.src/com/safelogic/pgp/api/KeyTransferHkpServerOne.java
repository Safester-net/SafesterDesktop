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
import java.net.SocketException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.UnknownServiceException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import com.safelogic.pgp.api.util.crypto.PgpUserId;
import com.safelogic.pgp.api.util.parms.Parms;
import com.safelogic.pgp.api.util.parms.PgpTags;
import com.safelogic.pgp.apispecs.KeyHandler;
import com.safelogic.pgp.apispecs.KeyTransferHkpServer;
import com.safelogic.pgp.apispecs.StringTriplet;
import com.safelogic.pgp.util.JOptionPaneCustom;
import com.safelogic.utilx.Debug;

/**
 * @author Nicolas de Pomereu
 * <br>
 * KeyTransferHkpServer concrete implementation
 */
public class KeyTransferHkpServerOne implements KeyTransferHkpServer
{
    /** The limit of full public keys that may be imported in one request */
    private static int LIMIT_FOR_FULL_KEYS_NUMBER = 200;

    /** The debug flag */
    protected boolean DEBUG = Debug.isSet(this);

    /** The error Manager */
    private ErrorManager errorMan;

    /** The HKP Server url, with port included */
    private String serverUrl = null;

    /** Java 11 HTTP client */
    private final HttpClient httpClient;

    /**
     * Constructor
     * @param serverUrl The HKP Server url, with port included
     */
    public KeyTransferHkpServerOne(String serverUrl)
    {
        this.serverUrl = Objects.requireNonNull(serverUrl, "serverUrl cannot be null!");

        this.errorMan = new ErrorManager();

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public boolean isOperationOk()
    {
        return this.errorMan.isOperationOk();
    }

    public String getErrorCode()
    {
        return this.errorMan.getErrorCode();
    }

    public Exception getException()
    {
        return this.errorMan.getException();
    }

    public String getErrorLabel()
    {
        return this.errorMan.getErrorLabel();
    }

    public String getStackTrace()
    {
        return this.errorMan.getStackTrace();
    }

    public List<StringTriplet> getRemoteSearchedKey(String searchText, boolean includePublicKeyBlock)
    {
        Objects.requireNonNull(searchText, "searchText cannot be null!");

        if (searchText.equals(""))
        {
            throw new IllegalArgumentException("Search String can't be null");
        }

        String recv;

        try
        {
            // http://pgp.mit.edu:11371/pks/lookup?search=npomereu&op=index&options=mr

            Map<String, String> params = new LinkedHashMap<String, String>();
            params.put("search", searchText);
            params.put("op", "index");
            params.put("options", "mr");

            URI uri = buildUri(serverUrl, "/pks/lookup", params);

            debug("urlSearch: " + uri);

            errorMan.setOperationOk();
            HttpResponse<String> response = sendGet(uri);

            // Align with previous behavior: some servers can return odd statuses.
            if (response == null)
            {
                return null;
            }

            int status = response.statusCode();
            if (status >= 400)
            {
                return new Vector<StringTriplet>();
            }

            recv = response.body();
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
            // Can happen on some key servers
            return new Vector<StringTriplet>();
        }
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            return null;
        }
        catch (InterruptedException e1)
        {
            Thread.currentThread().interrupt();
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            return null;
        }

        debug("recv: " + recv);

        HkpServerResultExtractor hkpServerResultExtractornew = null;

        List<StringTriplet> foundKeys = new Vector<StringTriplet>();

        try
        {
            hkpServerResultExtractornew = new HkpServerResultExtractor(serverUrl, recv);
            foundKeys = hkpServerResultExtractornew.getPgpIdsAndUserIdsCommaSeparated();
        }
        catch (IOException e)
        {
            // Should never happen, because IO is on a String
            JOptionPaneCustom.showException(null, e);
        }

        if (includePublicKeyBlock)
        {
            updateListWithPublicKeyBlock(foundKeys);
        }

        return foundKeys;
    }

    private void updateListWithPublicKeyBlock(List<StringTriplet> foundKeys)
    {
        for (int i = 0; i < foundKeys.size(); i++)
        {
            if (i > LIMIT_FOR_FULL_KEYS_NUMBER)
            {
                break;
            }

            StringTriplet stringTriplet = foundKeys.get(i);
            String pgpId = stringTriplet.getElement1();
            String publicKeyBlock = this.getRemoteAscPgpPublicKeyAsAsc(pgpId);
            stringTriplet.setElement3(publicKeyBlock);
            foundKeys.set(i, stringTriplet);
        }
    }

    public boolean existsRemotePubKey(String pgpId)
    {
        PgpUserId pgpUserId = null;

        try
        {
            errorMan.setOperationOk();
            pgpUserId = new PgpUserId(pgpId);
        }
        catch (IllegalArgumentException e)
        {
            errorMan.setErrorCode(Parms.ERR_INVALID_USER_ID, e);
            return false;
        }

        String email = pgpUserId.getKeyId();

        List<StringTriplet> keys = getRemoteSearchedKey(email, false);

        if (keys == null || keys.isEmpty())
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public String getRemoteAscPgpPublicKeyAsAsc(String pgpId)
    {
        Objects.requireNonNull(pgpId, "pgpId cannot be null!");

        if (pgpId.equals(""))
        {
            throw new IllegalArgumentException("pgpId String can't be null");
        }

        if (!pgpId.startsWith("0x") || pgpId.length() != 10)
        {
            throw new IllegalArgumentException("pgpId String must be 10 hex chars long and must start with \"0x\"");
        }

        String recv;

        try
        {
            Map<String, String> params = new LinkedHashMap<String, String>();
            params.put("search", pgpId);
            params.put("op", "get");
            params.put("options", "mr");

            URI uri = buildUri(serverUrl, "/pks/lookup", params);

            debug("urlSearch: " + uri);

            errorMan.setOperationOk();
            HttpResponse<String> response = sendGet(uri);

            if (response == null)
            {
                return null;
            }

            int status = response.statusCode();
            if (status >= 400)
            {
                return null;
            }

            recv = response.body();
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
            return null;
        }
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            return null;
        }
        catch (InterruptedException e1)
        {
            Thread.currentThread().interrupt();
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            return null;
        }

        if (recv == null)
        {
            return null;
        }

        if (!recv.contains(PgpTags.BEGIN_PGP_PUBLIC_KEY_BLOCK) ||
            !recv.contains(PgpTags.END_PGP_PUBLIC_KEY_BLOCK))
        {
            return null;
        }

        recv = StringUtils.substringBetween(recv,
                PgpTags.BEGIN_PGP_PUBLIC_KEY_BLOCK,
                PgpTags.END_PGP_PUBLIC_KEY_BLOCK);

        recv = PgpTags.BEGIN_PGP_PUBLIC_KEY_BLOCK
                + recv
                + PgpTags.END_PGP_PUBLIC_KEY_BLOCK;

        return recv;
    }

    public void putRemoteAscPgpPublicKeyAsAsc(String publicKeyBlock)
    {
        Objects.requireNonNull(publicKeyBlock, "publicKeyBlock cannot be null!");

        if (publicKeyBlock.equals(""))
        {
            throw new IllegalArgumentException("publicKeyBlock String can't be null");
        }

        publicKeyBlock = publicKeyBlock.trim();

        if (!publicKeyBlock.startsWith(PgpTags.BEGIN_PGP_PUBLIC_KEY_BLOCK))
        {
            throw new IllegalArgumentException("publicKeyBlock String must start with: "
                    + PgpTags.BEGIN_PGP_PUBLIC_KEY_BLOCK);
        }

        if (!publicKeyBlock.contains(PgpTags.END_PGP_PUBLIC_KEY_BLOCK))
        {
            throw new IllegalArgumentException("publicKeyBlock String must contains at end: "
                    + PgpTags.END_PGP_PUBLIC_KEY_BLOCK);
        }

        try
        {
            URI uri = buildUri(serverUrl, "/pks/add", null);

            debug("urlPost: " + uri);

            String form = "keytext=" + urlEncode(publicKeyBlock);

            errorMan.setOperationOk();
            HttpResponse<String> response = sendPostForm(uri, form);

            if (response == null)
            {
                return;
            }

            int status = response.statusCode();
            if (status >= 400)
            {
                errorMan.setErrorCode(Parms.ERR_HTTP_UNKNOWN_SERVICE_EXCEPTION,
                        new IOException("HTTP status: " + status));
                return;
            }
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
        catch (IOException e1)
        {
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            return;
        }
        catch (InterruptedException e1)
        {
            Thread.currentThread().interrupt();
            errorMan.setErrorCode(Parms.ERR_HTTP_IO_EXCEPTION, e1);
            return;
        }
    }

    private HttpResponse<String> sendGet(URI uri)
    throws IOException, InterruptedException
    {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "*/*")
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private HttpResponse<String> sendPostForm(URI uri, String formBody)
    throws IOException, InterruptedException
    {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Accept", "*/*")
                .POST(HttpRequest.BodyPublishers.ofString(formBody, StandardCharsets.UTF_8))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private URI buildUri(String baseUrl, String path, Map<String, String> params)
    {
        StringBuilder sb = new StringBuilder();

        if (baseUrl.endsWith("/"))
        {
            sb.append(baseUrl.substring(0, baseUrl.length() - 1));
        }
        else
        {
            sb.append(baseUrl);
        }

        if (path != null && !path.equals(""))
        {
            if (!path.startsWith("/"))
            {
                sb.append("/");
            }
            sb.append(path.startsWith("/") ? path : ("/" + path));
        }

        if (params != null && !params.isEmpty())
        {
            sb.append("?");
            boolean first = true;

            for (Map.Entry<String, String> e : params.entrySet())
            {
                if (!first)
                {
                    sb.append("&");
                }
                first = false;

                sb.append(urlEncode(e.getKey()));
                sb.append("=");
                sb.append(urlEncode(e.getValue()));
            }
        }

        return URI.create(sb.toString());
    }

    private String urlEncode(String s)
    {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private void debug(String s)
    {
        if (DEBUG)
        {
            System.out.println(s);
        }
    }

    public static void main(String[] args)
    throws Exception
    {
        String server = null;

        boolean mit = true;

        if (mit)
        {
            server = "http://pgp.mit.edu:11371";
        }
        else
        {
            server = "http://keyserver.ubuntu.com:11371";
        }

        System.out.println(new Date() + " HKP Server is: " + server);
        System.out.println();

        KeyTransferHkpServer keyTransferHkpServer = new KeyTransferHkpServerOne(server);

        String keyToExport = "testpom@testpom.com";

        KeyHandler keyHandler = new KeyHandlerOne();
        String keyAsc = keyHandler.exportAscPgpPublicKey(keyToExport);

        keyTransferHkpServer.putRemoteAscPgpPublicKeyAsAsc(keyAsc);

        if (!keyTransferHkpServer.isOperationOk())
        {
            System.out.println(keyTransferHkpServer.getErrorCode());
            System.out.println(keyTransferHkpServer.getErrorLabel());
            System.out.println(keyTransferHkpServer.getException().toString());
            return;
        }

        if (true) return;

        boolean includePublicKeyBlock = true;

        List<StringTriplet> keys = keyTransferHkpServer.getRemoteSearchedKey("testpom", includePublicKeyBlock);

        if (!keyTransferHkpServer.isOperationOk())
        {
            System.out.println(keyTransferHkpServer.getErrorCode());
            System.out.println(keyTransferHkpServer.getErrorLabel());
            System.out.println(keyTransferHkpServer.getException().toString());
            return;
        }

        if (keys.isEmpty())
        {
            System.out.println("No keys found!");
        }

        System.out.println();

        for (int i = 0; i < keys.size(); i++)
        {
            System.out.println(keys.get(i));
        }

        for (int i = 0; i < keys.size(); i++)
        {
            String key = keys.get(i).getElement1();

            System.out.println();
            String publicKeyBlock = "";

            if (includePublicKeyBlock)
                publicKeyBlock = keyTransferHkpServer.getRemoteAscPgpPublicKeyAsAsc(key);

            System.out.println(publicKeyBlock);
        }

        System.out.println();
        System.out.println(new Date());
    }
}
