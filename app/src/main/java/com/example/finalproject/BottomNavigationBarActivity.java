package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.finalproject.databinding.ActivityBottomNavigationBarBinding;
import com.example.finalproject.groups.GroupsFragment;
import com.example.finalproject.notes.DiaryFragment;
import com.example.finalproject.timer.SetTimerFragment;
import com.example.finalproject.todolist.TodolistFragment;

public class BottomNavigationBarActivity extends AppCompatActivity {

    ActivityBottomNavigationBarBinding binding;

    @SuppressLint("NonConstantResourceId")
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
                    replaceFragment(new GroupsFragment());
                    break;
                case R.id.diary:
                    replaceFragment(new DiaryFragment());
                    break;
                case R.id.timer:
                    replaceFragment(new SetTimerFragment());
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