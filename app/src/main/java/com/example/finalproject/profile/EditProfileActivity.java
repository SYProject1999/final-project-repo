package com.example.finalproject.profile;

import androidx.activity.result.ActivityResultCallback;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.GlideApp;
import com.example.finalproject.R;
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

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private int day = 0, month = 0, year = 0;

    private DatePickerDialog datePickerDialog;
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

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void saveChanges() {
        String fullName = fullNameET.getText().toString().trim();
        String email = emailET.getText().toString().trim();
        String gender = genderET.getText().toString().trim();
        String dateOfBirth = (String) dateOfBirthTV.getText();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!fullName.equals(snapshot.child("fullName").getValue()) && !fullName.isEmpty()) {
                    databaseReference.child("fullName").setValue(fullName);
                }
                if (!email.equals(user.getEmail()) && !email.isEmpty()) {
                    user.updateEmail(email);
                }
                if (!gender.equals(snapshot.child("Gender").getValue()) && !gender.isEmpty()) {
                    databaseReference.child("Gender").setValue(gender);
                }
                if (!dateOfBirth.equals(snapshot.child("DateOfBirth").getValue())) {
                    databaseReference.child("DateOfBirth").setValue(dateOfBirth);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
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
                if (snapshot.child("imageUrl").getValue() != null) {
                    GlideApp.with(EditProfileActivity.this).load(storageReference.child(snapshot.child("imageUrl").getValue(String.class))).into(profileImage);
                }
                fullNameET.setText(snapshot.child("fullName").getValue(String.class));
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

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateOfBirthTV.setText(date);
            }
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