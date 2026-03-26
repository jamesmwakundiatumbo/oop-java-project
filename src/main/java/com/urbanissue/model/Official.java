package com.urbanissue.model;

/**
 * Represents an official user. May be associated with a Department (composition).
 */
public class Official extends User {
    private Integer departmentId;

    public Official() {
        super();
        setRole("OFFICIAL");
    }

    public Official(int userId, String name, String email, String password, String phone, Integer departmentId) {
        super(userId, name, email, password, phone, "OFFICIAL");
        this.departmentId = departmentId;
    }

    @Override
    public String getDisplayRole() {
        return "Official";
    }

    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }

    @Override
    public String toString() {
        return getName() + " (ID: " + getUserId() + ")";
    }
}
