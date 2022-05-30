package com.example.finalproject;

public class User {

    public String fullname, email;
    public boolean alreadyUsedTheApp;

    public User() {

    }

    public User(String fullname, String email, boolean alreadyUsedTheApp) {

        this.email = email;
        this.fullname = fullname;
    }
}
