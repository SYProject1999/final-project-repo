package com.example.finalproject.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalproject.R;
import com.example.finalproject.models.NoteModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class CreateNote extends AppCompatActivity {

    EditText noteTitleET, noteContentET;
    FloatingActionButton saveNoteFAB;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        noteTitleET = findViewById(R.id.createNoteTitle);
        noteContentET = findViewById(R.id.createNoteContent);
        saveNoteFAB = findViewById(R.id.saveNote);

        Toolbar toolbar = findViewById(R.id.toolbarOfCreateNote);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        String userID = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Notes");

        saveNoteFAB.setOnClickListener(view -> {
            String noteTitle = noteTitleET.getText().toString();
            String noteContent = noteContentET.getText().toString();
            String id = databaseReference.push().getKey();
            if (noteTitle.isEmpty() || noteContent.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Both field are Required", Toast.LENGTH_LONG).show();
            } else {
                NoteModel noteModel = new NoteModel(noteTitle, noteContent, id);
                assert id != null;
                databaseReference.child(id).setValue(noteModel).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Note Has Been Inserted Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed: " + Objects.requireNonNull(task.getException()), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}