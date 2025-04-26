package com.kavi.dotsandboxes;

public interface LoginCallback {
    void onLoginSuccessful();
    void onLoginFailed(String errorMessage);
}