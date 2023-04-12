package com.example.finalproject.models;

public class User {

    // test change
    private String fullName, email, gender, dateOfBirth, imageUrl, Status;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isAlreadyUsedTheApp() {
        return alreadyUsedTheApp;
    }

    public void setAlreadyUsedTheApp(boolean alreadyUsedTheApp) {
        this.alreadyUsedTheApp = alreadyUsedTheApp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}

