package com.example.finalproject.models;

public class StepsModel {
    String ID,Title;
    Boolean iscompleted;

    public StepsModel() {
    }

    public StepsModel(String ID, String title, Boolean iscompleted) {
        this.ID = ID;
        this.Title = title;
        this.iscompleted = iscompleted;

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public Boolean getIscompleted() {
        return iscompleted;
    }

    public void setIscompleted(Boolean iscompleted) {
        this.iscompleted = iscompleted;
    }


}
