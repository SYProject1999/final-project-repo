package com.example.finalproject.groups;

import static com.example.finalproject.models.FirebaseReference.GROUPS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.finalproject.BaseActivity;
import com.example.finalproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupProfileActivity extends BaseActivity {

    Context context;
    private Group currentGroup;
    TextView tvGroupName;
    CircleImageView ivProfile;
    TextView tvGroupMembers;
    TextView tvGroupTasks;
    TextView tvExploreMultimedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);
        currentGroup = (Group) getIntent().getSerializableExtra("group");
        loadCurrentGroupData();
        init();
        setListeners();
    }

    private void init() {
        context = this;
        tvGroupName = findViewById(R.id.tvProfileName);
        tvGroupMembers = findViewById(R.id.tvGroupMembers);
        tvGroupTasks = findViewById(R.id.tvGroupTasks);
        tvExploreMultimedia = findViewById(R.id.tvExploreMedia);
        ivProfile = findViewById(R.id.profileImage);
        tvGroupName.setText(currentGroup.getName());
    }

    private void setListeners() {
        tvGroupMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getNextIntent(GroupMembersActivity.class));
            }
        });

        tvGroupTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getNextIntent(GroupTasksActivity.class));
            }
        });

        tvExploreMultimedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getNextIntent(ExploreMultimediaActivity.class));

            }
        });

    }

    private Intent getNextIntent(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("group", currentGroup);
        return intent;
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

}