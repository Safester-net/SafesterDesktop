package com.safelogic.pgp.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.time.Duration;

import com.safelogic.pgp.api.engines.DownloaderEngine;
import com.safelogic.pgp.api.util.msg.MessagesManager;
import com.safelogic.pgp.apispecs.HttpTransfer;
import com.safelogic.pgp.util.UrlUtil;
import com.safelogic.pgp.util.UserPreferencesManager;

/**
 * HttpTransferOne  Java 11 HttpClient implementation
 */
public class HttpTransferOne implements HttpTransfer {

    private static final int MAX_LENGTH_FOR_STRING = 3_000_000;
    private static final int CONNECT_TIMEOUT_SEC = 15;
    private static final int REQUEST_TIMEOUT_SEC = 120;

    protected boolean DEBUG = true;

    public static final String SEND_FAILED = "SEND_FAILED";
    public static final String SEND_OK = "SEND_OK";

    private String responseBody;
    private boolean sendOk = false;

    private DownloaderEngine owner;
    private MessagesManager messages = new MessagesManager();

    private final HttpClient client;

    public HttpTransferOne() {
        applyProxySettings();
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SEC))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public HttpTransferOne(DownloaderEngine owner) {
        this();
        this.owner = owner;
    }

    // --------------------------------------------------------------------
    // SEND (simple API)
    // --------------------------------------------------------------------

    @Override
    public void send(String s)
            throws SocketException, ConnectException,
                   UnknownServiceException, ProtocolException, IOException {

        String url = UrlUtil.getCgeepUrl() + "/do/Recv";
        send(url, "POST", s);

        if (responseBody.startsWith(SEND_OK)) {
            sendOk = true;
            responseBody = responseBody.substring(SEND_OK.length() + 1);
        } else if (responseBody.startsWith(SEND_FAILED)) {
            sendOk = false;
            responseBody = responseBody.substring(SEND_FAILED.length() + 1);
        } else {
            throw new ProtocolException("Response does not start with SEND_OK or SEND_FAILED");
        }
    }

    // --------------------------------------------------------------------
    // SEND (generic GET / POST)
    // --------------------------------------------------------------------

    public void send(String url, String method, String payload)
            throws SocketException, ConnectException,
                   UnknownServiceException, ProtocolException, IOException {

        if (url == null || method == null) {
            throw new IllegalArgumentException("url and method cannot be null");
        }

        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SEC));

            if ("POST".equalsIgnoreCase(method)) {
                builder.POST(HttpRequest.BodyPublishers.ofString(
                        payload == null ? "" : payload,
                        Charset.forName("ISO-8859-1")));
            } else if ("GET".equalsIgnoreCase(method)) {
                builder.GET();
            } else {
                throw new IllegalArgumentException("Method must be GET or POST");
            }

            HttpResponse<String> response =
                    client.send(builder.build(),
                            HttpResponse.BodyHandlers.ofString(Charset.forName("ISO-8859-1")));

            if (response.statusCode() != 200) {
                throw new UnknownServiceException("HTTP status " + response.statusCode());
            }

            responseBody = truncateIfNeeded(response.body());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("HTTP interrupted", e);
        } catch (UnknownHostException e) {
            throw new ConnectException(e.getMessage());
        } catch( SocketException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
    }

    // --------------------------------------------------------------------
    // DOWNLOAD
    // --------------------------------------------------------------------

    public void downloadFileFromUrl(File file, long fileLength, String url)
            throws SocketException, ConnectException,
                   UnknownServiceException, ProtocolException,
                   IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SEC))
                .GET()
                .build();

        HttpResponse<InputStream> response =
                client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            throw new UnknownServiceException("HTTP status " + response.statusCode());
        }

        try (InputStream in = response.body();
             FileOutputStream out = new FileOutputStream(file)) {

            byte[] buf = new byte[4096];
            int len;
            long total = 0;
            long step = Math.max(1, fileLength / DownloaderEngine.getMAXIMUM_PROGRESS());

            setOwnerNote(messages.getMessage("DOWNLOADING_FILE") + " " + file.getName());

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                total += len;

                if (total > step) {
                    total = 0;
                    Thread.sleep(10);
                    addOneOwnerCurrent();
                }

                if (isOwnerInterrupted()) {
                    throw new InterruptedException();
                }
            }
        }
    }

    // --------------------------------------------------------------------
    // UTIL
    // --------------------------------------------------------------------

    private String truncateIfNeeded(String s) {
        if (s.length() <= MAX_LENGTH_FOR_STRING) {
            return s;
        }
        return s.substring(0, MAX_LENGTH_FOR_STRING);
    }

    private void applyProxySettings() {
        if (!UserPreferencesManager.getUseProxy()) {
            return;
        }

        String host = UserPreferencesManager.getProxyAddress();
        String port = UserPreferencesManager.getProxyPort();

        if (host != null && port != null) {
            System.setProperty("http.proxyHost", host);
            System.setProperty("http.proxyPort", port);
            System.setProperty("https.proxyHost", host);
            System.setProperty("https.proxyPort", port);
        }
    }

    private boolean isOwnerInterrupted() {
        return owner != null && ((Thread) owner).isInterrupted();
    }

    private void setOwnerNote(String note) {
        if (owner != null) {
            owner.setNote(note);
        }
    }

    
    private void addOneOwnerCurrent() {
        if (owner != null) {
            int current = owner.getCurrent() + 1;
            if (current < DownloaderEngine.getMAXIMUM_PROGRESS()) {
                owner.setCurrent(current);
            }
        }
    }

    @Override
    public String recv() {
        return responseBody;
    }

    @Override
    public boolean isSendOk() {
        return sendOk;
    }

    private void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }

    @Override
    public boolean isHttpReachable(String httpAddress) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public int diagnoseProxySetting() {
	// TODO Auto-generated method stub
	return 0;
    }
}
