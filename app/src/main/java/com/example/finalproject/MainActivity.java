package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            userID = user.getUid();
            checkFirstTime(new FirebaseCallBack() {
                @Override
                public void onCallback(String alreadyUsedTheApp) {
                    if (alreadyUsedTheApp.equals("false")) {
                        startActivity(new Intent(MainActivity.this, BottomNavigationBarActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, OnBoardingScreensActivity.class));
                    }
                }
            });
        }
    }

    void checkFirstTime(FirebaseCallBack firebaseCallBack) {
        databaseReference.child(userID).child("alreadyUsedTheApp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String alreadyUsedTheApp = String.valueOf(snapshot.getValue(Boolean.class));
//                firebaseCallBack.onCallback(alreadyUsedTheApp);
                if (alreadyUsedTheApp.equals("true")) {
                    startActivity(new Intent(MainActivity.this, BottomNavigationBarActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, OnBoardingScreensActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    interface FirebaseCallBack {
        void onCallback(String alreadyUsedTheApp);
    }
}