package com.example.finalproject.groups;

import static com.example.finalproject.models.References.GROUPS;
import static com.example.finalproject.models.References.USERS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.example.finalproject.R;
import com.example.finalproject.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AddGroupMembersActivity extends AppCompatActivity implements GroupMembersAdapter.Listener{

    private Group currentGroup;

    private RecyclerView recyclerView;
    private GroupMembersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_members);
        currentGroup = (Group) getIntent().getSerializableExtra("group");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int mode;
        boolean isAdmin = FirebaseAuth.getInstance().getCurrentUser().getUid().equals(currentGroup.getCreatedBy());
        if (isAdmin) {
            mode = GroupMembersAdapter.ADD_MODE;
        } else {
            mode = GroupMembersAdapter.NONE_MODE;
        }
        adapter = new GroupMembersAdapter(new ArrayList<>(), currentGroup.getCreatedBy(), mode, this);
        recyclerView.setAdapter(adapter);
        loadData();

        Toolbar mToolbar = findViewById(R.id.app_bar_layout);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Group Members");
    }

    private void loadData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(USERS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setId(dataSnapshot.getKey());
                    if (!currentGroup.getMembers().contains(user.getId())) {
                        adapter.addMember(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onRemoveClickListener(User user) {
        // not supported
    }

    @Override
    public void onAddClickListener(User user, int position) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Adding...");
        progressDialog.show();
        currentGroup.getMembers().add(user.getId());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(GROUPS).child(currentGroup.getId());
        reference.setValue(currentGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    adapter.removeMember(position);
                } else {
                    Toast.makeText(AddGroupMembersActivity.this, "Failed to add user to group", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}