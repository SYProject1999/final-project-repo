package com.example.finalproject.groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.finalproject.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class GroupTasksActivity extends AppCompatActivity {

    private Group currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_tasks);

        currentGroup = (Group) getIntent().getSerializableExtra("group");
        Toolbar mToolbar = findViewById(R.id.app_bar_layout);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Group Tasks");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (currentGroup.getCreatedBy().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            inflater.inflate(R.menu.add_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, AddGroupTaskActivity.class);
        intent.putExtra("group", currentGroup);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}