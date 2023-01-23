package com.example.finalproject.models;

public class NoteModel {

    private String noteTitle, noteContent, noteId;

    public NoteModel() {}

    public NoteModel(String noteTitle, String noteContent, String noteId) {
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.noteId = noteId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }
}
