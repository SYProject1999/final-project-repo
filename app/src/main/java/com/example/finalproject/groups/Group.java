package com.example.finalproject.groups;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {
    private String id;
    private String name;
    private String createdBy;
    private List<String> members;
    private String picUrl;

    public Group() {
    }

    public Group(String id, String name, String createdBy, List<String> members) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
