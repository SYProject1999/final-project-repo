package com.example.finalproject.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.GlideApp;
import com.example.finalproject.LoginActivity;
import com.example.finalproject.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference userReference;

    private CircleImageView circleImageView;

    private TextView usernameTV;
    private TextView userStatusTV;
    private TextView userDateOfBirthTV;
    private TextView userGenderTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        assert user != null;
        String userID = user.getUid();

        userReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        usernameTV = findViewById(R.id.usernameProfile);
        userStatusTV = findViewById(R.id.status_profile_tv);
        userDateOfBirthTV = findViewById(R.id.dateOfBirth_profile_tv);
        userGenderTV = findViewById(R.id.gender_profile_tv);
        TextView editProfileTV = findViewById(R.id.editProfile);
        TextView changePasswordTV = findViewById(R.id.changePassword_profile_tv);
        circleImageView = findViewById(R.id.profileImage);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        changePasswordTV.setOnClickListener(view -> {
            AlertDialog.Builder changePasswordDialog = new AlertDialog.Builder(ProfileActivity.this);
            LayoutInflater inflater = LayoutInflater.from(ProfileActivity.this);
            View changePasswordDialogView = inflater.inflate(R.layout.change_password_layout, null);
            changePasswordDialog.setView(changePasswordDialogView);

            AlertDialog alertDialog = changePasswordDialog.create();
            alertDialog.setCancelable(false);

            final EditText oldPasswordET = changePasswordDialogView.findViewById(R.id.oldPasswordET);
            final EditText checkOldPasswordET = changePasswordDialogView.findViewById(R.id.checkOldPasswordET);
            final EditText newPasswordET = changePasswordDialogView.findViewById(R.id.newPasswordET);
            final Button cancelPasswordBtn = changePasswordDialogView.findViewById(R.id.cancelPasswordBtn);
            final Button changePasswordBtn = changePasswordDialogView.findViewById(R.id.changePasswordBtn);

            cancelPasswordBtn.setOnClickListener(v -> alertDialog.dismiss());

            changePasswordBtn.setOnClickListener(v -> {
                String oldPassword = oldPasswordET.getText().toString();
                String checkOldPassword = checkOldPasswordET.getText().toString();
                String newPassword = newPasswordET.getText().toString();
                final String email = user.getEmail();

                if (TextUtils.isEmpty(oldPassword)) {
                    oldPasswordET.setError("Old Password is Required");
                    oldPasswordET.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(checkOldPassword)) {
                    checkOldPasswordET.setError("Check Old Password is Required");
                    checkOldPasswordET.requestFocus();
                    return;
                }
                if (!TextUtils.equals(oldPassword, checkOldPassword)) {
                    checkOldPasswordET.setError("Check Old Password Don't Match Old Password");
                    checkOldPasswordET.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(newPassword)) {
                    newPasswordET.setError("New Password is Required");
                    newPasswordET.requestFocus();
                    return;
                }
                if (newPassword.length() < 6 ) {
                    newPasswordET.setError("Min Password Length Should Be 6 Characters");
                    newPasswordET.requestFocus();
                    return;
                }

                assert email != null;
                AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Password Has Been Changed Successfully", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed to Change Password: " + Objects.requireNonNull(task1.getException()), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(ProfileActivity.this, "Old Password is Wrong: " + Objects.requireNonNull(task.getException()), Toast.LENGTH_LONG).show();
                    }
                });
            });

            alertDialog.show();
        });

        editProfileTV.setOnClickListener(v -> {
            Intent intent = new Intent(getApplication(), EditProfileActivity.class);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            moveToLoginActivity();
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usernameTV.setText(snapshot.child("fullName").getValue(String.class));
                if (snapshot.child("Status").getValue() != null) {
                    userStatusTV.setText(snapshot.child("Status").getValue(String.class));
                }
                if (snapshot.child("DateOfBirth").getValue() != null) {
                    userDateOfBirthTV.setText(snapshot.child("DateOfBirth").getValue(String.class));
                }
                if (snapshot.child("Gender").getValue() != null) {
                    userGenderTV.setText(snapshot.child("Gender").getValue(String.class));
                }
                GlideApp.with(getApplicationContext()).load(snapshot.child("imageUrl").getValue(String.class)).placeholder(R.drawable.ic_baseline_person_24).into(circleImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error Loading Image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void moveToLoginActivity () {
        Intent intent = new Intent(getApplication(), LoginActivity.class);
        startActivity(intent);
    }
}

