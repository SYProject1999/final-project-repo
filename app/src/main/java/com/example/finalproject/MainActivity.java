package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.finalproject.Onboarding.OnBoardingScreensActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user == null) {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            userID = user.getUid();

            databaseReference.child(userID).child("alreadyUsedTheApp").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String alreadyUsedTheApp = String.valueOf(snapshot.getValue(Boolean.class));

                    if (alreadyUsedTheApp.equals("true")) {
                        System.out.println(alreadyUsedTheApp);
                        finish();
                        startActivity(new Intent(MainActivity.this, BottomNavigationBarActivity.class));
                    } else {
                        System.out.println(alreadyUsedTheApp);
                        finish();
                        startActivity(new Intent(MainActivity.this, OnBoardingScreensActivity.class));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}