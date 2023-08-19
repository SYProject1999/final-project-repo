package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Onboarding.OnBoardingScreensActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = database.getReference("Users");

    EditText emailLogEt, passwordLogEt;
    Button loginBtn;
    TextView registerBtn, forgotPassword;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    ProgressBar loginProgressBar;

    public static String userID="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogEt = findViewById(R.id.emailLogEt);
        passwordLogEt = findViewById(R.id.passwordLogEt);
        registerBtn = findViewById(R.id.registerbtn);
        loginBtn = findViewById(R.id.loginbtn);
        forgotPassword = findViewById(R.id.forgotPassword);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {

                userID = user.getUid();

                databaseReference.child(userID).child("alreadyUsedTheApp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String alreadyUsedTheApp = String.valueOf(snapshot.getValue(Boolean.class));

                        if (alreadyUsedTheApp.equals("true")) {
                            finish();
                            Toast.makeText(LoginActivity.this, "User logged in successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginActivity.this, BottomNavigationBarActivity.class));
                        } else {
                            finish();
                            Toast.makeText(LoginActivity.this, "User logged in successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginActivity.this, OnBoardingScreensActivity.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        } else {
            if (user != null) {
                user.sendEmailVerification();
            }
            Toast.makeText(LoginActivity.this, "Verify your email", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {

        if (view == forgotPassword) {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        }

        if (view == registerBtn) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }

        if (view == loginBtn)
            loginUser();

    }

    private void loginUser() {

        String email = emailLogEt.getText().toString().trim();
        String password = passwordLogEt.getText().toString().trim();

        if (email.isEmpty()) {
            emailLogEt.setError("Email is Required");
            emailLogEt.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLogEt.setError("Please Provide Valid Email");
            emailLogEt.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordLogEt.setError("Password is Required");
            passwordLogEt.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordLogEt.setError("Min Password Length Should Be 6 Characters");
            passwordLogEt.requestFocus();
            return;
        }

        loginProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (mAuth.getCurrentUser().isEmailVerified()) {
                    String userID = mAuth.getCurrentUser().getUid();

                    databaseReference.child(userID).child("alreadyUsedTheApp").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String alreadyUsedTheApp = String.valueOf(snapshot.getValue(Boolean.class));

                            if (alreadyUsedTheApp.equals("true")) {
                                finish();
                                Toast.makeText(LoginActivity.this, "User logged in successfully", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LoginActivity.this, BottomNavigationBarActivity.class));
                            } else {
                                finish();
                                Toast.makeText(LoginActivity.this, "User logged in successfully", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LoginActivity.this, OnBoardingScreensActivity.class));
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    firebaseUser = mAuth.getCurrentUser();
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(LoginActivity.this, "Verify your email", Toast.LENGTH_LONG).show();
                    loginProgressBar.setVisibility(View.INVISIBLE);
                }
            } else {
                Toast.makeText(LoginActivity.this, "Login Error " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                loginProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}