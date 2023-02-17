package com.example.finalproject.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalproject.R;
import com.example.finalproject.models.NoteModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class EditNoteActivity extends AppCompatActivity {

    Intent data;
    EditText mEditTitle, mEditContent;
    FloatingActionButton saveEditNote;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        mEditTitle = findViewById(R.id.editTitleOfNote);
        mEditContent = findViewById(R.id.editContentOfNote);
        saveEditNote = findViewById(R.id.saveEditNote);
        data = getIntent();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        assert firebaseUser != null;
        String userID = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Notes");

        Toolbar toolbar = findViewById(R.id.toolbarOfEditNote);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        saveEditNote.setOnClickListener(view -> {

            String newTitle = mEditTitle.getText().toString();
            String newContent = mEditContent.getText().toString();
            if (newTitle.isEmpty() || newContent.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Both field are Required", Toast.LENGTH_LONG).show();
                return;
            } else {
                NoteModel noteModel = new NoteModel(newTitle, newContent, data.getStringExtra("noteId"));
                databaseReference.child(data.getStringExtra("noteId")).setValue(noteModel).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Note Has Been Updated Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed: " + Objects.requireNonNull(task.getException()), Toast.LENGTH_LONG).show();
                    }
                });
            }



        });

        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");
        mEditTitle.setText(noteTitle);
        mEditContent.setText(noteContent);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}