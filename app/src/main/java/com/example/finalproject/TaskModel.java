package com.example.finalproject;

public class TaskModel {

    private String taskTitle, TaskDescription, TaskId, TaskDate;

    public TaskModel() {
    }

    public TaskModel(String taskTitle, String taskDescription, String taskId, String taskDate) {
        this.taskTitle = taskTitle;
        TaskDescription = taskDescription;
        TaskId = taskId;
        TaskDate = taskDate;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return TaskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        TaskDescription = taskDescription;
    }

    public String getTaskId() {
        return TaskId;
    }

    public void setTaskId(String taskId) {
        TaskId = taskId;
    }

    public String getTaskDate() {
        return TaskDate;
    }

    public void setTaskDate(String taskDate) {
        TaskDate = taskDate;
    }
}
