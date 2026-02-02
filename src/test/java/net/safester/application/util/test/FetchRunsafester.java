package net.safester.application.util.test;

import java.net.URI;
import java.net.http.*;
import java.util.Date;

public class FetchRunsafester {
    public static void main(String[] args) {
	Object[] signers = org.bouncycastle.jce.provider.BouncyCastleProvider.class.getSigners();
	System.out.println("BC signers: " + (signers == null ? "null" : java.util.Arrays.toString(signers)));
	System.out.println("BC jar: " + org.bouncycastle.jce.provider.BouncyCastleProvider.class
	        .getProtectionDomain().getCodeSource().getLocation());
    }

    /**
     * 
     */
    public static void cryptoTest() {
	System.out.println("java.version=" + System.getProperty("java.version"));

	System.out.println("BC loaded from: " +
	    org.bouncycastle.jce.provider.BouncyCastleProvider.class
	        .getProtectionDomain().getCodeSource().getLocation());

	System.out.println("KeyHandlerOne loaded from: " +
	    com.safelogic.pgp.api.KeyHandlerOne.class
	        .getProtectionDomain().getCodeSource().getLocation());
	
	try {
	    java.net.URL u = org.bouncycastle.jce.provider.BouncyCastleProvider.class
	            .getProtectionDomain().getCodeSource().getLocation();
	    System.out.println("BC loaded from: " + u);

	    java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
	    try (java.io.InputStream in = u.openStream()) {
	        byte[] buf = new byte[8192];
	        for (int r; (r = in.read(buf)) > 0; ) md.update(buf, 0, r);
	    }
	    byte[] d = md.digest();
	    StringBuilder sb = new StringBuilder();
	    for (byte b : d) sb.append(String.format("%02x", b));
	    System.out.println("BC jar sha256: " + sb);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * 
     */
    public static void testHttp() {
	try {
            System.out.println(new Date() + " Starting...");

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.runsafester.net/"))
                    .header("User-Agent", "Java11HttpClient")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("HTTP " + response.statusCode());
            System.out.println(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
