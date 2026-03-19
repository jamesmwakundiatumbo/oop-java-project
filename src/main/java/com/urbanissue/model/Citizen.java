package com.urbanissue.model;

/**
 * Represents a citizen user. Inheritance: extends User.
 */
public class Citizen extends User {

    public Citizen() {
        super();
        setRole("CITIZEN");
    }

    public Citizen(int userId, String name, String email, String password, String phone) {
        super(userId, name, email, password, phone, "CITIZEN");
    }

    @Override
    public String getDisplayRole() {
        return "Citizen";
    }
}
