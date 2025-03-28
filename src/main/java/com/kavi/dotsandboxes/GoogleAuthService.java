package com.kavi.dotsandboxes;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;
import java.awt.Desktop;

public class GoogleAuthService {
    public static String exchangeCodeForToken(String authCode) {
        try {
            String clientId = "300597737355-eplaej8tclce6hf34kvje4dcejgn7upc.apps.googleusercontent.com";
            String clientSecret = "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";  // Replace with actual secret
            String redirectUri = "http://localhost:8080";
            String tokenUrl = "https://oauth2.googleapis.com/token";

            String requestBody = "code=" + authCode +
                                 "&client_id=" + clientId +
                                 "&client_secret=" + clientSecret +
                                 "&redirect_uri=" + redirectUri +
                                 "&grant_type=authorization_code";

            URL url = new URL(tokenUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            System.out.println("✅ Access Token Response: " + response.toString());
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
   public static void signInWithGoogle() {
        try {
            // Step 1: Start the local server to listen for OAuth response
            System.out.println("⚡ Attempting to start server...");
            LocalHttpServer.startServer();
            System.out.println("✅ Server started successfully.");

            // Step 2: Open Google login page
            String clientId = "300597737355-eplaej8tclce6hf34kvje4dcejgn7upc.apps.googleusercontent.com";
            String redirectUri = "http://localhost:8080";
            String scope = "email%20profile%20openid";
            String authUrl = "https://accounts.google.com/o/oauth2/auth" +
                             "?client_id=" + clientId +
                             "&redirect_uri=" + redirectUri +
                             "&response_type=code" +
                             "&scope=" + scope;
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(authUrl));
            } else {
                System.err.println("Desktop browsing is not supported on this system.");
            }
                            
            Desktop.getDesktop().browse(new URI(authUrl));

            // Step 3: Wait until we receive the authorization code
            while (LocalHttpServer.getAuthorizationCode() == null) {
                Thread.sleep(1000);
            }
            String authCode = LocalHttpServer.getAuthorizationCode();

            // Step 4: Exchange authorization code for access token
            GoogleAuthService.exchangeCodeForToken(authCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
