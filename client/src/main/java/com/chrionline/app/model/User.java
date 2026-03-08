package com.chrionline.app.model;

/**
 * Utilisateur (client ou admin).
 */
public class User {
    private int id;
    private String name;
    private String email;
    private UserRole role;

    public User(int id, String name, String email, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public boolean isAdmin() { return role == UserRole.ADMIN; }
}
