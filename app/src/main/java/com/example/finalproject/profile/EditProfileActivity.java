package com.example.finalproject.profile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.GlideApp;
import com.example.finalproject.R;
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
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private DatePickerDialog datePickerDialog;
    private CircleImageView profileImage;
    private TextView dateOfBirthTV;
    private EditText fullNameET, statusET, genderET;

    private Uri imageUri;

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
        Button changeImageBtn = findViewById(R.id.editProfileImageBtn);
        Button dateOfBirthBtn = findViewById(R.id.editProfileDateOfBirthBtn);
        Button saveBtn = findViewById(R.id.editProfileSaveBtn);
        fullNameET = findViewById(R.id.editProfileFullName);
        statusET = findViewById(R.id.editProfileStatus);
        genderET = findViewById(R.id.editProfileGender);
        progressBar = findViewById(R.id.edit_profile_progress_bar);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Users");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        ActivityResultLauncher<String> getContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        imageUri = result;
                        Toast.makeText(EditProfileActivity.this, "Image Selected", Toast.LENGTH_SHORT).show();
                        Picasso.with(getApplicationContext()).load(result).placeholder(R.drawable.ic_baseline_person_24).into(profileImage);
                    }
                });

        initDatePicker();
        dateOfBirthBtn.setOnClickListener(this::openDatePicker);

        changeImageBtn.setOnClickListener(view -> getContent.launch("image/*"));

        saveBtn.setOnClickListener(view -> {
            if (storageTask != null && storageTask.isInProgress()) {
                Toast.makeText(EditProfileActivity.this, "Image change in progress", Toast.LENGTH_SHORT).show();
            } else {
                saveChanges();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GlideApp.with(getApplicationContext()).load(snapshot.child("imageUrl").getValue(String.class)).placeholder(R.drawable.ic_baseline_person_24).into(profileImage);

                fullNameET.setText(snapshot.child("fullName").getValue(String.class));
                if (snapshot.child("Status").getValue() != null) {
                    statusET.setText(snapshot.child("Status").getValue(String.class));
                }
                if (snapshot.child("DateOfBirth").getValue() != null) {
                    dateOfBirthTV.setText(snapshot.child("DateOfBirth").getValue(String.class));
                }
                if (snapshot.child("Gender").getValue() != null) {
                    genderET.setText(snapshot.child("Gender").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void saveChanges() {
        String fullName = fullNameET.getText().toString().trim();
        String status = statusET.getText().toString().trim();
        String gender = genderET.getText().toString().trim();
        String dateOfBirth = (String) dateOfBirthTV.getText();

        databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!fullName.equals(snapshot.child("fullName").getValue()) && !fullName.isEmpty()) {
                    databaseReference.child(user.getUid()).child("fullName").setValue(fullName);
                }
                if (!status.equals(snapshot.child("Status").getValue()) && !status.isEmpty()) {
                    databaseReference.child(user.getUid()).child("Status").setValue(status);
                }
                if (!gender.equals(snapshot.child("Gender").getValue()) && !gender.isEmpty()) {
                    databaseReference.child(user.getUid()).child("Gender").setValue(gender);
                }
                if (!dateOfBirth.equals(snapshot.child("DateOfBirth").getValue())) {
                    databaseReference.child(user.getUid()).child("DateOfBirth").setValue(dateOfBirth);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        if (imageUri != null) {
            long timeMillis = System.currentTimeMillis();
            StorageReference fileReference = storageReference.child(user.getUid()).child("Profile Image").child(timeMillis + "." + getFileExtension(imageUri));
            storageTask = fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = String.valueOf(uri);
                sendLink(url);
            })).addOnProgressListener(snapshot -> progressBar.setVisibility(View.VISIBLE)).addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else finish();
    }

    private void sendLink(String url) {
        databaseReference.child(user.getUid()).child("imageUrl").setValue(url).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditProfileActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            dateOfBirthTV.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }
}