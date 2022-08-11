package com.example.finalproject;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.models.ProfileImageModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private CircleImageView profileImage;
    private TextView dateOfBirthTV;
    private Button changeImageBtn, dateOfBirthBtn, saveBtn;
    private EditText fullNameET, emailET, genderET;

    private Uri imageUri;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private StorageTask storageTask;
    private long timeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileImage = findViewById(R.id.editProfileImage);
        dateOfBirthTV = findViewById(R.id.editProfileDateOfBirthTV);
        changeImageBtn = findViewById(R.id.editProfileImageBtn);
        dateOfBirthBtn = findViewById(R.id.editProfileDateOfBirthBtn);
        saveBtn = findViewById(R.id.editProfileSaveBtn);
        fullNameET = findViewById(R.id.editProfileFullName);
        emailET = findViewById(R.id.editProfileEmail);
        genderET = findViewById(R.id.editProfileGender);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Users").child(user.getUid());
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        ActivityResultLauncher<String> getContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    imageUri = result;
                    Picasso.with(EditProfileActivity.this).load(result).into(profileImage);
                }
            }
        });

        changeImageBtn.setOnClickListener(view -> getContent.launch("image/*"));

        saveBtn.setOnClickListener(view -> {
            if (storageTask != null && storageTask.isInProgress()) {
                Toast.makeText(EditProfileActivity.this, "Image change in progress", Toast.LENGTH_SHORT).show();
            } else {
                saveChanges();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void saveChanges() {
        String fullName = fullNameET.getText().toString();
        String email = emailET.getText().toString();
        String gender = genderET.getText().toString();
        String dateOfBirth = dateOfBirthTV.toString();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (imageUri != null) {
            timeMillis = System.currentTimeMillis();
            StorageReference fileReference = storageReference.child(timeMillis + "." + getFileExtension(imageUri));

            storageTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    databaseReference.child("imageUrl").setValue(timeMillis + "." + getFileExtension(imageUri));
                    Toast.makeText(EditProfileActivity.this, "Image Selected", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        emailET.setText(user.getEmail());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullNameET.setText(snapshot.child("fullName").getValue(String.class));
                if (snapshot.child("DateOfBirth").getValue() != null) {
                    dateOfBirthTV.setText(snapshot.child("DateOfBirth").getValue(String.class));
                }
                if (snapshot.child("Gender").getValue() != null) {
                    genderET.setText(snapshot.child("Gender").getValue(String.class));
                }
                if (snapshot.child("imageUrl").getValue() != null) {
                    GlideApp.with(EditProfileActivity.this).load(storageReference.child(snapshot.child("imageUrl").getValue(String.class))).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}