package com.nammametro.metro.model;

/**
 * User Role Enumeration
 * SOLID: Open/Closed Principle - easy to extend with new roles
 */
public enum UserRole {
    ADMIN("Admin - Full system access"),
    REGULAR_USER("Regular User - Can book tickets"),
    STATION_USER("Station User - Manages specific station");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
