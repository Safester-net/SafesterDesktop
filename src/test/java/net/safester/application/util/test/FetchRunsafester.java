package net.safester.application.util.test;

import java.net.URI;
import java.net.http.*;
import java.util.Date;

public class FetchRunsafester {
    public static void main(String[] args) {
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
