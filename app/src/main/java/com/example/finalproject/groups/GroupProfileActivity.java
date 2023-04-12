package com.example.finalproject.groups;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.finalproject.R;

public class GroupProfileActivity extends AppCompatActivity {

    private Group currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);
        currentGroup = (Group) getIntent().getSerializableExtra("group");
    }
}