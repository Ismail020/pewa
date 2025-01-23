package com.example.demo.auth;

public class SetNewPasswordRequest {
    private final String password;
    private final String token;

    public SetNewPasswordRequest(String password, String token) {
        this.password = password;
        this.token = token;
    }

    public String getPassword() {
        return password;
    }


    public String getToken() {
        return token;
    }
}
