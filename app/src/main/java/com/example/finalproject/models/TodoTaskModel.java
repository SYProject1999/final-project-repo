package com.example.finalproject.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TodoTaskModel {
    String ID,Title,Note;
    Boolean iscompleted,reminder,isimportant;
    Long duedatetime;

    ArrayList<StepsModel> stepsModelArrayList=new ArrayList<>();

    public TodoTaskModel() {
    }

    public TodoTaskModel(String ID, String title, String note, Boolean iscompleted, Boolean reminder, Boolean isimportant, Long duedatetime, ArrayList<StepsModel> stepsModelArrayList) {
        this.ID = ID;
        this.Title = title;
        this.Note = note;
        this.iscompleted = iscompleted;
        this.reminder = reminder;
        this.isimportant = isimportant;
        this.duedatetime = duedatetime;
        this.stepsModelArrayList=stepsModelArrayList;
    }

    public ArrayList<StepsModel> getStepsModelArrayList() {
        return stepsModelArrayList;
    }

    public void setStepsModelArrayList(ArrayList<StepsModel> stepsModelArrayList) {
        this.stepsModelArrayList = stepsModelArrayList;
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

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public Boolean getIscompleted() {
        return iscompleted;
    }

    public void setIscompleted(Boolean iscompleted) {
        this.iscompleted = iscompleted;
    }

    public Boolean getReminder() {
        return reminder;
    }

    public void setReminder(Boolean reminder) {
        this.reminder = reminder;
    }

    public Boolean getIsimportant() {
        return isimportant;
    }

    public void setIsimportant(Boolean isimportant) {
        this.isimportant = isimportant;
    }

    public Long getDuedatetime() {
        return duedatetime;
    }

    public void setDuedatetime(Long duedatetime) {
        this.duedatetime = duedatetime;
    }





}
