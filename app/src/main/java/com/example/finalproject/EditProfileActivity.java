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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                + "." + getFileExtension(imageUri));

            storageTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ProfileImageModel imageModel = new ProfileImageModel(taskSnapshot.getUploadSessionUri().toString());
                    databaseReference.child("imageUrl").setValue(imageModel);
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
}