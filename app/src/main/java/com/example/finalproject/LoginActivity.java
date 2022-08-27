package com.example.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Onboarding.OnBoardingScreensActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    EditText emailLogEt, passwordLogEt;
    Button loginBtn;
    TextView registerBtn, forgotPassword;
    ImageView googleIV;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        googleIV = findViewById(R.id.google_IV);
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

//        if (view == googleIV) {
//            googleLogin();
//        }

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

//    private void googleLogin() {
//        Intent signInWithGoogle = googleSignInClient.getSignInIntent();
//        startActivityForResult(signInWithGoogle, 1000);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1000) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                task.getResult(ApiException.class);
//                finish();
//                Toast.makeText(LoginActivity.this, "User logged in successfully", Toast.LENGTH_LONG).show();
//                startActivity(new Intent(LoginActivity.this, OnBoardingScreensActivity.class));
//            } catch (ApiException e) {
//                Toast.makeText(LoginActivity.this, "Login Error " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }
//    }

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

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                assert user != null;
                if (user.isEmailVerified()) {
                    finish();
                    Toast.makeText(LoginActivity.this, "User logged in successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, OnBoardingScreensActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Verify your email", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Login Error " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}