package com.srm.campusnexus.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    private String email;
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    // Constructors
    public LoginRequest() {
    }

    public LoginRequest(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}