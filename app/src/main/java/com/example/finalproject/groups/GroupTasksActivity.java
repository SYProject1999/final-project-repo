package com.example.finalproject.groups;

import static com.example.finalproject.models.FirebaseReference.GROUPS;
import static com.example.finalproject.models.FirebaseReference.GROUP_TASKS;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.finalproject.BaseActivity;
import com.example.finalproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupTasksActivity extends BaseActivity {

    private Group currentGroup;
    RecyclerView recyclerView;
    List<GroupTask> groupTaskList;

    Context context;
    private GroupTasksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_tasks);

        currentGroup = (Group) getIntent().getSerializableExtra("group");
        Toolbar mToolbar = findViewById(R.id.app_bar_layout);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Group Tasks");
        loadCurrentGroupData();
    }

    private void init() {
        context = this;
        groupTaskList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new GroupTasksAdapter(currentGroup, groupTaskList);
        recyclerView.setAdapter(adapter);
    }

    private void loadTasksData() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(GROUP_TASKS).child(currentGroup.getId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupTaskList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    groupTaskList.add(dataSnapshot.getValue(GroupTask.class));
                }
                adapter.notifyDataSetChanged();
                closeProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                closeProgressDialog();
                showToast("Failed to load data");
            }
        });

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

    private void loadCurrentGroupData() {

        showProgressDialog("Loading...");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(GROUPS).child(currentGroup.getId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentGroup = snapshot.getValue(Group.class);
                init();
                loadTasksData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                closeProgressDialog();
            }
        });
    }

}