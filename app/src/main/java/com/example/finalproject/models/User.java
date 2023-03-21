package com.example.finalproject.models;

public class User {

    // test change
    private String fullName, email, gender, dateOfBirth;
    private boolean alreadyUsedTheApp;

    public User() { }

    public User(String fullName, String email, boolean alreadyUsedTheApp) {
        this.email = email;
        this.fullName = fullName;
        this.alreadyUsedTheApp = alreadyUsedTheApp;
    }

    public User(String fullName, String email, boolean alreadyUsedTheApp, String gender, String dateOfBirth) {
        this.email = email;
        this.fullName = fullName;
        this.alreadyUsedTheApp = alreadyUsedTheApp;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }
}
