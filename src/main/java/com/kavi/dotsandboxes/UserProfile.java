package com.kavi.dotsandboxes;

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
        // Load profile picture
        try {
            this.profilePicture = new Image("https://graph.facebook.com/" + id + "/picture?type=large");
        } catch (Exception e) {
            // Use default profile picture if loading fails
            this.profilePicture = new Image(getClass().getResourceAsStream("/default_profile.png"));
        }
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