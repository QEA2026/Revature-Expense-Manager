package com.revature.models;

public class User {
    private int  id;
    private String username;
    private String password;
    private String role;

    // when you want to create an object first and set fields later.
    // so... User user = new User();
    public User() {}

    // Used when reading from the database
    // so... User user = new User(1, "john", "hashedpw", "employee");
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    //Used when you want to create a new user (id is auto-generated)
    // so... User user = new User("john", "hashedpw", "employee");
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}

