package com.example.finalproject.groups;

import static com.example.finalproject.models.References.GROUPS;

import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.BaseActivity;
import com.example.finalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupProfileActivity extends BaseActivity {

    Context context;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Group currentGroup;
    TextView tvGroupName;
    CircleImageView ivProfile;
    TextView tvGroupMembers;
    FirebaseStorage mStore;

    TextView tvGroupTasks;
    TextView tvExploreMultimedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);
        mStore = FirebaseStorage.getInstance();
        currentGroup = (Group) getIntent().getSerializableExtra("group");
        loadCurrentGroupData();

        init();
        setListeners();
        String url = currentGroup.getPicUrl();
        if (url != null) {
            Picasso.with(getApplicationContext()).load(currentGroup.getPicUrl()).into(ivProfile);

        } else {
            ivProfile.setImageResource(R.drawable.ic_baseline_person_24);
        }
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

        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            ivProfile.setImageURI(uri);

            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference(GROUPS).child(currentGroup.getId());
            StorageReference filePath = mStore.getReference().child("GroupsImages").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            HashMap m1 = new HashMap();
                            m1.put("picUrl", task.getResult().toString());
                            databaseReference1.updateChildren(m1).addOnSuccessListener(suc -> {
                                Toast.makeText(getApplicationContext(), "UpdateSuccessfully", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(er -> {
                                Toast.makeText(getApplicationContext(), er.getMessage(), Toast.LENGTH_SHORT).show();


                            });

                        }
                    });
                }
            });
        }
    }
}