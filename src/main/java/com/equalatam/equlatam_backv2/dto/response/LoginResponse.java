package com.equalatam.equlatam_backv2.dto.response;

import java.util.Set;

public class LoginResponse {

    private String username;
    private Set<String> roles;
    private Set<String> permissions;
    private String message;

    public LoginResponse(String username, Set<String> roles, Set<String> permissions, String message) {
        this.username = username;
        this.roles = roles;
        this.permissions = permissions;
        this.message = message;
    }

    public String getUsername() { return username; }
    public Set<String> getRoles() { return roles; }
    public Set<String> getPermissions() { return permissions; }
    public String getMessage() { return message; }
}
