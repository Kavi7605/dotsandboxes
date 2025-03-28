package com.kavi.dotsandboxes;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseInitializer {
    public static void main(String[] args) {
        try {
            FileInputStream serviceAccount = new FileInputStream("path/to/your/serviceAccountKey.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            FirebaseApp.initializeApp(options);
            System.out.println("Firebase Initialized Successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
