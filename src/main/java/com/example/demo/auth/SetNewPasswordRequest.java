package com.example.demo.auth;

public class SetNewPasswordRequest {
    private String password;
    private String confirmPassword;
    private String token;

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getToken() {
        return token;
    }
}
