package com.kavi.dotsandboxes;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicReference;

public class LocalHttpServer {
    private static AtomicReference<String> authCode = new AtomicReference<>(null);

    public static void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(8080)) {
                System.out.println("🚀 Local server started on port 8080...");
                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("🔗 Connection received...");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String requestLine;
                    while ((requestLine = reader.readLine()) != null) {
                        System.out.println("📩 Received Request: " + requestLine);
                        if (requestLine.contains("code=")) {
                            String code = requestLine.split("code=")[1].split(" ")[0];
                            System.out.println("✅ Extracted Auth Code: " + code);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("❌ Server Error: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public static String getAuthorizationCode() {
        return authCode.get();
    }
}
