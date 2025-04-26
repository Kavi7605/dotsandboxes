package com.kavi.dotsandboxes;

import fi.iki.elonen.NanoHTTPD;
import java.util.Map;
import java.awt.Desktop;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.*;
import java.io.*;
import java.util.List;

public class FacebookLoginHandler extends NanoHTTPD {
    private String appId = "1165954588586368"; // Replace with your App ID
    private String client_secret = "ed6264724f9ab7b91335290d6e3b759b"; // Replace with your App Secret
    //private String redirectUri = "http://localhost:8080/oauth/callback"; // Your local server callback URL
    private String encodedRedirectUri = "http%3A%2F%2Flocalhost%3A8080%2Foauth%2Fcallback"; // URL-encoded redirect URI
    private String scope = "email,public_profile,user_friends";
    private String facebookToken = null;
    private LoginCallback callback;
    private static final String FIREBASE_API_KEY = "AIzaSyAEiS8OlZVCvMpXiv1NuLExr4Bx7IwM-FE";
    private static final String FIREBASE_AUTH_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=" + FIREBASE_API_KEY;
    private UserProfile userProfile;

    public FacebookLoginHandler(LoginCallback callback) {
        super(8080);
        this.callback = callback;
    }
    
    public UserProfile getUserProfile() {
        return userProfile;
    }
    
    public void handleLogin() {
        try {
            if(!this.isAlive()) {
                start(NanoHTTPD.SOCKET_READ_TIMEOUT, false); // Start the NanoHTTPD server
            }
            String authUrl = getFacebookAuthorizationUrl();
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(authUrl)); // Open the auth URL in the default browser
            } else {
                System.err.println("Desktop is not supported. Please open the following URL manually: " + authUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchUserProfile(String accessToken) {
        try {
            String profileUrl = "https://graph.facebook.com/me?fields=id,name,email&access_token=" + accessToken;
            HttpURLConnection connection = (HttpURLConnection) new URL(profileUrl).openConnection();
            connection.setRequestMethod("GET");
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                String id = jsonObject.get("id").getAsString();
                String name = jsonObject.get("name").getAsString();
                String email = jsonObject.has("email") ? jsonObject.get("email").getAsString() : "";

                userProfile = new UserProfile(id, name, email, accessToken);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void authenticateFacebookWithFirebase(String facebookToken) {
        HttpURLConnection connection = null;
        try {
            URI uri = new URI(FIREBASE_AUTH_URL);
            connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JsonObject jsonObject = JsonParser.parseString(facebookToken).getAsJsonObject();
            String accessToken = jsonObject.get("access_token").getAsString();
            
            String jsonInputString = String.format(
                "{\"postBody\":\"access_token=%s&providerId=facebook.com\",\"requestUri\":\"http://localhost\",\"returnSecureToken\":true,\"returnIdpCredential\":true}",
                accessToken
            );
            System.out.println("\nConnection Full Post request: " + connection.getURL() + "\n" + jsonInputString);
            //String jsonInputString = "{\"postBody\":\"id_token=EAAOAD0WWYbcBO0DIZBlCOmDguRXOr6rpe0UYqksRWuV92OEuQ00gMptxOY88SZB7EJzNjgFQx6xrXIR2yU7wbKzaQKmCO174rVpSCNBL5Wcsnck5xcJjIEpTCOgxothAunLbVWwqpOGbck87oZAU7oMDwnmrK3wNuLIjkAjPKqZBjZATfFOj4IWOmhvHWZBxF9ZBZB20NfNrHP1SQKYVKPIVnGldnWSGunaWJlHZA9Yp6zvnHH2pqDvf6G9x388jX%26facebook.com\",\"requestUri\":\"http://localhost\",\"returnSecureToken\":true,\"returnIdpCredential\":true}";
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                System.out.println("\n------------Firebase ID Token Response: " + response.toString());
                fetchUserProfile(accessToken);
                callback.onLoginSuccessful(); // Notify the callback on success
                // Handle the response to extract the ID token
            } else {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    callback.onLoginFailed("\nDETAILED ERROR:\n"+errorResponse.toString()+"\nDETAILED ERROR END\n"); // Notify the callback on failure with detailed error
                } catch (IOException ioException) {
                    callback.onLoginFailed("Error Code: " + responseCode + ", but failed to read error response: " + ioException.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        if ("/oauth/callback".equals(session.getUri())) {
            Method method = session.getMethod();
            if (Method.GET.equals(method)) {
                Map<String, List<String>> params = session.getParameters();
                String code = params.containsKey("code") ? params.get("code").get(0) : null;
                if (code != null) {
                    facebookToken = exchangeCodeForToken(code);
                    if (facebookToken != null) {
                        authenticateFacebookWithFirebase(facebookToken);
                    } else {
                        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Failed to exchange code for access token.");
                    }                 
                    return newFixedLengthResponse(Response.Status.OK, "text/plain", "OAuth callback received. Code: " + code);
                } else {
                    return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Missing 'code' parameter.");
                }
            }
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found");
    }

    public String exchangeCodeForToken(String code) {
        String tokenEndpoint = "https://graph.facebook.com/v12.0/oauth/access_token";
        String tokenRequestUrl = tokenEndpoint + 
            "?client_id=" + appId + 
            "&redirect_uri=" + encodedRedirectUri + 
            "&client_secret=" + client_secret + 
            "&code=" + code;

        HttpURLConnection connection = null;
        try {
            URI tokenUri = new URI(tokenRequestUrl);
            connection = (HttpURLConnection) tokenUri.toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (InputStream is = connection.getInputStream();
                     java.util.Scanner scanner = new java.util.Scanner(is, "UTF-8")) {
                    scanner.useDelimiter("\\A");
                    return scanner.hasNext() ? scanner.next() : "";
                }
            } else {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    System.err.println("Error during Exchange Code for Token: " + responseCode + "\n" + errorResponse.toString());
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String getFacebookAuthorizationUrl() {
        String baseUri = "https://www.facebook.com/v12.0/dialog/oauth";
        return baseUri + "?client_id=" + appId + "&redirect_uri=" + encodedRedirectUri + "&response_type=code&scope=" + scope;
    }
}