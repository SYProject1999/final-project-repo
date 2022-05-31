package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText emailLogEt, passwordLogEt;
    Button loginBtn;
    TextView registerBtn, forgotPassword;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogEt = findViewById(R.id.emailLogEt);
        passwordLogEt = findViewById(R.id.passwordLogEt);
        registerBtn = findViewById(R.id.registerbtn);
        loginBtn = findViewById(R.id.loginbtn);
        forgotPassword = findViewById(R.id.forgotPassword);
        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user.isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "User logged in successfully", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LoginActivity.this, OnboardingScreensActivity.class));
                            } else {
                                user.sendEmailVerification();
                                Toast.makeText(LoginActivity.this, "Verify your email", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Error " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}