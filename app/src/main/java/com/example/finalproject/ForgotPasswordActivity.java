package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailResetEt;
    Button resetPasswordBtn;
    ImageView back;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailResetEt = findViewById(R.id.emailResetEt);
        resetPasswordBtn = findViewById(R.id.resetPasswordBtn);
        back = findViewById(R.id.resetPasswordBack);
        mAuth = FirebaseAuth.getInstance();

        back.setOnClickListener(view -> finish());

        resetPasswordBtn.setOnClickListener(view -> {
            resetPassword();
        });
    }

    private void resetPassword() {

        String email = emailResetEt.getText().toString().trim();

        if (email.isEmpty()) {
            emailResetEt.setError("Email is Required");
            emailResetEt.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailResetEt.setError("Please Provide Valid Email");
            emailResetEt.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this, "Check Your Email", Toast.LENGTH_LONG).show();
                    finish();
                } else
                    Toast.makeText(ForgotPasswordActivity.this, "Try Again! Something Wrong Happened", Toast.LENGTH_LONG).show();
            }
        });

    }
}