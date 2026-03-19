package com.urbanissue.model;

/**
 * Abstract base class for all user types.
 * Demonstrates: Abstraction (abstract class), Encapsulation (private fields + getters/setters).
 */
public abstract class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String role;

    public User() {}

    public User(int userId, String name, String email, String password, String phone, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }

    // Polymorphism: subclasses can override to provide role-specific behavior
    public abstract String getDisplayRole();

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
