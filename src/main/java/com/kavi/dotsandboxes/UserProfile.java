package com.kavi.dotsandboxes;

import java.io.*;
import java.net.URL;

import org.checkerframework.checker.units.qual.g;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.api.client.json.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.HttpURLConnection;

import javafx.scene.image.Image;

public class UserProfile {
    private String id;
    private String name;
    private String email;
    private Image profilePicture;
    private String accessToken;

    public UserProfile(String id, String name, String email, String accessToken) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.accessToken = accessToken;

        this.profilePicture = new Image(getProfilePictureUrl(id, accessToken), true);

        // Load profile picture
        // try {
            // Fetch friends using Facebook Graph API
            // String friendsUrl = "https://graph.facebook.com/me/friends?fields=id,name,picture&access_token=" + accessToken;
            // HttpURLConnection connection = (HttpURLConnection) new URL(friendsUrl).openConnection();
            // connection.setRequestMethod("GET");
            
            // int responseCode = connection.getResponseCode();
            // if (responseCode == HttpURLConnection.HTTP_OK) {
            //     BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //     StringBuilder response = new StringBuilder();
            //     String line;
            //     while ((line = in.readLine()) != null) {
            //         response.append(line);
            //     }
            //     in.close();

            //     // Parse the JSON response
            //     JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            //     JsonArray data = jsonObject.getAsJsonArray("data");
            //     for (JsonElement element : data) {
            //         JsonObject friend = element.getAsJsonObject();
            //         String friendId = friend.get("id").getAsString();
            //         String friendName = friend.get("name").getAsString();
            //         String friendPictureUrl = friend.get("picture").getAsJsonObject().get("data").getAsJsonObject().get("url").getAsString();

            //         // Print the friend's details
            //         System.out.println("Friend ID: " + friendId);
            //         System.out.println("Friend Name: " + friendName);
            //         System.out.println("Friend Picture URL: " + friendPictureUrl);
            //     }
            // }
                
        //     this.profilePicture = new Image("https://graph.facebook.com/" + id + "/picture", true);
        //     this.profilePicture.errorProperty().addListener((observable, oldValue, newValue) -> {
        //         if (newValue) {
        //             System.out.println("Error loading Facebook profile picture, using default");
        //             this.profilePicture = new Image("https://cdn-icons-png.flaticon.com/512/149/149071.png", true);
        //         }
        //     });
        // } catch (Exception e) {
        //     System.out.println("Error loading profile picture: " + e.getMessage());
        //     this.profilePicture = new Image("https://cdn-icons-png.flaticon.com/512/149/149071.png", true);
        // }
    }

    private String getProfilePictureUrl(String userId, String accessToken) {
        try {
            String graphApiUrl = String.format("https://graph.facebook.com/%s/picture?type=large&redirect=false&access_token=%s", userId, accessToken);
            URL url = new URL(graphApiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    JsonElement jsonElement = JsonParser.parseString(response.toString()); //jsonParser.parse(response.toString());
                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonResponse = jsonElement.getAsJsonObject();
                        if (jsonResponse.has("data")) {
                            JsonObject data = jsonResponse.getAsJsonObject("data");
                            if (data.has("url")) {
                                return data.get("url").getAsString();
                            }
                        }
                    }
                }
            } else {
                System.err.println("Failed to retrieve profile picture. Response Code: " + responseCode);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    System.err.println("Error Response: " + errorResponse.toString());
                }
            }
            return null;
        } catch (MalformedURLException e) {
            System.err.println("Malformed URL: " + e.getMessage());
        } catch (ProtocolException e) {
            System.err.println("Protocol Exception: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            System.err.println("JSON Syntax Exception: " + e.getMessage());
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Image getProfilePicture() {
        return profilePicture;
    }

    public String getAccessToken() {
        return accessToken;
    }
} 