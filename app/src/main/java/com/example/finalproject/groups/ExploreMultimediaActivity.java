package com.example.finalproject.groups;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.finalproject.R;
import com.example.finalproject.models.References;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ExploreMultimediaActivity extends AppCompatActivity {

    Group currentGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_multimedia);
        currentGroup = (Group) getIntent().getSerializableExtra("group");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(References.GROUP_CHAT).child(currentGroup.getId());


    }
}