package com.kavi.dotsandboxes;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseInitializer {
    public static void initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/google-services.json");

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(com.google.auth.oauth2.GoogleCredentials.fromStream(serviceAccount))
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialized successfully!");
            }
        } catch (IOException e) {
            System.err.println("❌ Firebase initialization failed: " + e.getMessage());
        }
    }
}
