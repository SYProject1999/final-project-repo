package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.finalproject.databinding.ActivityBottomNavigationBarBinding;
import com.example.finalproject.databinding.ActivityMainBinding;

public class BottomNavigationBarActivity extends AppCompatActivity {

    ActivityBottomNavigationBarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBottomNavigationBarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new TodolistFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.todo:
                    replaceFragment(new TodolistFragment());
                    break;
                case R.id.habits:
                    replaceFragment(new HabitsFragment());
                    break;
                case R.id.diary:
                    replaceFragment(new DiaryFragment());
                    break;
                case R.id.timer:
                    replaceFragment(new TimerFragment());
                    break;
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }

}