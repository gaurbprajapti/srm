package com.srm.campusnexus.dto.response;

public class AuthResponse {

    private boolean success;
    private String message;
    private String token;
    private UserInfoResponse user;

    // Constructors
    public AuthResponse() {
    }

    public AuthResponse(boolean success, String message, String token, UserInfoResponse user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfoResponse getUser() {
        return user;
    }

    public void setUser(UserInfoResponse user) {
        this.user = user;
    }

    // Static factory method
    public static AuthResponse success(String message, String token, UserInfoResponse user) {
        return new AuthResponse(true, message, token, user);
    }
}