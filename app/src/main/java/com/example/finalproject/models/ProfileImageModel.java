package com.example.finalproject.models;

public class ProfileImageModel {

    private String imageUrl;

    public ProfileImageModel() { }

    public ProfileImageModel(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
