package com.example.finalproject.groups;

public class Contacts {

    private String fullName, Status, imageUrl;

    public Contacts() {}

    public Contacts(String fullName, String Status, String imageUrl) {
        this.fullName = fullName;
        this.Status = Status;
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
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
