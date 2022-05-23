package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back;
    EditText fullnameRegEt, emailRegEt, passwordRegEt, repasswordRegEt;
    Button signUpBtn;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        fullnameRegEt = findViewById(R.id.fullname);
        emailRegEt = findViewById(R.id.emailRegEt);
        passwordRegEt = findViewById(R.id.passwordRegEt);
        repasswordRegEt = findViewById(R.id.repassword);
        back = findViewById(R.id.back);
        signUpBtn = findViewById(R.id.signupbtn);
        back.setOnClickListener(view -> finish());

        signUpBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view == signUpBtn) {
            mAuth.createUserWithEmailAndPassword(emailRegEt.getText().toString(), passwordRegEt.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Good", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to Register" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        System.out.println(task.getException().getMessage());
                    }
                }
            });
        }

    }

    private void registerUser() {

        String fullname = fullnameRegEt.getText().toString().trim();
        String email = emailRegEt.getText().toString().trim();
        String password = passwordRegEt.getText().toString().trim();
        String rePassword = repasswordRegEt.getText().toString().trim();

        if (fullname.isEmpty()) {
            fullnameRegEt.setError("Full Name is Required");
            fullnameRegEt.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailRegEt.setError("Email is Required");
            emailRegEt.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailRegEt.setError("Please Provide Valid Email");
            emailRegEt.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordRegEt.setError("Password is Required");
            passwordRegEt.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordRegEt.setError("Min Password Length Should Be 6 Characters");
            passwordRegEt.requestFocus();
            return;
        }

        if (rePassword.isEmpty()) {
            repasswordRegEt.setError("rePassword is Required");
            repasswordRegEt.requestFocus();
            return;
        }

        if (!rePassword.equals(password)) {
            repasswordRegEt.setError("rePassword Doesn't Match Your Password");
            repasswordRegEt.requestFocus();
            return;
        }

    }
}