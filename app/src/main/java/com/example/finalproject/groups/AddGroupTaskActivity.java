package com.example.finalproject.groups;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.example.finalproject.R;

import java.util.Objects;

public class AddGroupTaskActivity extends AppCompatActivity {

    private Group currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_task);

        currentGroup = (Group) getIntent().getSerializableExtra("group");
        Toolbar mToolbar = findViewById(R.id.app_bar_layout);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Assign Task");
    }
}