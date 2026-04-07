package com.urbanissue.model;

/**
 * Represents an administrator user.
 */
public class Admin extends User {

    public Admin() {
        super();
        setRole("ADMIN");
    }

    public Admin(int userId, String name, String email, String password, String phone) {
        super(userId, name, email, password, phone, "ADMIN");
    }

    @Override
    public String getDisplayRole() {
        return "Administrator";
    }
}
