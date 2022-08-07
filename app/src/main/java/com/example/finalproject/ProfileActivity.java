package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private DatabaseReference userReference;

    private TextView changePasswordTV, editProfileTV, usernameTV, userEmailTV, userDateOfBirthTV, userGenderTV;

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
        userEmailTV = findViewById(R.id.email_profile_tv);
        userDateOfBirthTV = findViewById(R.id.dateOfBirth_profile_tv);
        userGenderTV = findViewById(R.id.gender_profile_tv);
        editProfileTV = findViewById(R.id.editProfile);
        changePasswordTV = findViewById(R.id.changePassword_profile_tv);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        changePasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, "Password Has Been Changed Successfully", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Failed to Change Password: " + task.getException().toString(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Old Password is Wrong: " + task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                });

                alertDialog.show();
            }
        });

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            moveToLoginActivity();
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        userEmailTV.setText(user.getEmail());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usernameTV.setText(snapshot.child("fullName").getValue(String.class));
                if (snapshot.child("DateOfBirth").getValue() != null) {
                    userDateOfBirthTV.setText(snapshot.child("DateOfBirth").getValue(String.class));
                }
                if (snapshot.child("Gender").getValue() != null) {
                    userGenderTV.setText(snapshot.child("Gender").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void moveToLoginActivity () {
        Intent intent = new Intent(getApplication(), MainActivity.class);
        startActivity(intent);
        ((Activity) ProfileActivity.this).overridePendingTransition(0, 0);
    }
}