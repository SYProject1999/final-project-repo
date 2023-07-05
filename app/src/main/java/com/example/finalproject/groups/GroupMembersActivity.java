package com.example.finalproject.groups;

import static com.example.finalproject.models.References.GROUPS;
import static com.example.finalproject.models.References.USERS;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.finalproject.BaseActivity;
import com.example.finalproject.R;
import com.example.finalproject.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class GroupMembersActivity extends BaseActivity implements GroupMembersAdapter.Listener {

    private Group currentGroup;
    private RecyclerView recyclerView;
    private GroupMembersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
        currentGroup = (Group) getIntent().getSerializableExtra("group");
        loadCurrentGroupData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadCurrentGroupData() {

        showProgressDialog("Loading...");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(GROUPS).child(currentGroup.getId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentGroup = snapshot.getValue(Group.class);
                init();
                closeProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                closeProgressDialog();
            }
        });
    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int mode;
        boolean isAdmin = FirebaseAuth.getInstance().getCurrentUser().getUid().equals(currentGroup.getCreatedBy());
        if (isAdmin) {
            mode = GroupMembersAdapter.REMOVE_MODE;
        } else {
            mode = GroupMembersAdapter.NONE_MODE;
        }
        adapter = new GroupMembersAdapter(new ArrayList<>(), currentGroup.getCreatedBy(), mode, this);
        recyclerView.setAdapter(adapter);
        loadData();

        Toolbar mToolbar = findViewById(R.id.app_bar_layout);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Group Members (" + currentGroup.getMembers().size() + ")");
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
                    User user = snapshot.getValue(User.class);
                    user.setId(memberId);
                    adapter.addMember(user);
                }
            });
        }
    }

    @Override
    public void onRemoveClickListener(User user) {
        // only called for the owner
        for (int i = 0; i < currentGroup.getMembers().size(); i++) {
            if (user.getId().equals(currentGroup.getMembers().get(i))) {
                currentGroup.getMembers().remove(i);
                break;
            }
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(GROUPS).child(currentGroup.getId());
        reference.setValue(currentGroup);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Group Members (" + currentGroup.getMembers().size() + ")");
    }

    @Override
    public void onAddClickListener(User user, int position) {
        // not supported
    }
}