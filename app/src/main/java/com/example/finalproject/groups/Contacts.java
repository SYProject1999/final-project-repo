package com.example.finalproject.groups;

public class Contacts {

    public String fullName, imageUrl;

    public Contacts() {}

    public Contacts(String fullName, String imageUrl) {
        this.fullName = fullName;
        this.imageUrl = imageUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
