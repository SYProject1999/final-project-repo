package com.example.finalproject.groups;

import static com.example.finalproject.models.FirebaseReference.USERS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.finalproject.R;
import com.example.finalproject.models.FirebaseReference;
import com.example.finalproject.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class GroupMembersActivity extends AppCompatActivity {

    private Group currentGroup;
    private RecyclerView recyclerView;
    private GroupMembersAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
        currentGroup = (Group) getIntent().getSerializableExtra("group");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupMembersAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        loadData();

        Toolbar mToolbar = findViewById(R.id.app_bar_layout);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Group Members (" + currentGroup.getMembers().size()+")");
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
        Intent intent = new Intent(this, AddGroupMembersActivity.class);
        intent.putExtra("group", currentGroup);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(USERS);
        for (String memberId : currentGroup.getMembers()) {
            reference.child(memberId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot snapshot) {
                    adapter.addMember(snapshot.getValue(User.class));
                }
            });
        }
    }

}