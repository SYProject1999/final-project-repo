package com.example.finalproject.models;

public class User {

    private String fullName, email;
    private boolean alreadyUsedTheApp;

    public User(String fullName, String email, boolean alreadyUsedTheApp) {
        this.email = email;
        this.fullName = fullName;
        this.alreadyUsedTheApp = alreadyUsedTheApp;
    }

    public User(String fullName, String email, boolean alreadyUsedTheApp, String gender, String dateOfBirth) {
        this.email = email;
        this.fullName = fullName;
        this.alreadyUsedTheApp = alreadyUsedTheApp;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAlreadyUsedTheApp() {
        return alreadyUsedTheApp;
    }

    public void setAlreadyUsedTheApp(boolean alreadyUsedTheApp) {
        this.alreadyUsedTheApp = alreadyUsedTheApp;
    }
}
