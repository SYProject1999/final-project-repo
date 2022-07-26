package com.example.finalproject.models;

public class HabitsModel {

    private String habitId, habitTitle, habitDescription, habitStartTime, imageId;

    public HabitsModel() { }

    public HabitsModel(String habitId, String habitTitle, String habitDescription, String habitStartTime, String imageId) {
        this.habitId = habitId;
        this.habitTitle = habitTitle;
        this.habitDescription = habitDescription;
        this.habitStartTime = habitStartTime;
        this.imageId = imageId;
    }

    public String getHabitId() {
        return habitId;
    }

    public void setHabitId(String habitId) {
        this.habitId = habitId;
    }

    public String getHabitTitle() {
        return habitTitle;
    }

    public void setHabitTitle(String habitTitle) {
        this.habitTitle = habitTitle;
    }

    public String getHabitDescription() {
        return habitDescription;
    }

    public void setHabitDescription(String habitDescription) {
        this.habitDescription = habitDescription;
    }

    public String getHabitStartTime() {
        return habitStartTime;
    }

    public void setHabitStartTime(String habitStartTime) {
        this.habitStartTime = habitStartTime;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
