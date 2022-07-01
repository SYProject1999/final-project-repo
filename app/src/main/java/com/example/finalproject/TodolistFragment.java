package com.example.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class TodolistFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference reference;
    private String userID;

    public TodolistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Tasks").child(userID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_todolist, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerView.getAdapter());
        recyclerView.setLayoutManager(linearLayoutManager);

        floatingActionButton = view.findViewById(R.id.fab);

        floatingActionButton.setOnClickListener((v) -> { addTask(); });

//        logoutBtn.setOnClickListener(v -> {
//            mAuth.signOut();
//            moveToLoginActivity();
//        });
        return view;
    }

    private void addTask() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View view = inflater.inflate(R.layout.todolist_input_layout, null);
        myDialog.setView(view);

        AlertDialog alertDialog = myDialog.create();
        alertDialog.setCancelable(false);

        final EditText taskEditText = view.findViewById(R.id.task);
        final EditText descriptionEditText = view.findViewById(R.id.description);
        Button saveTask = view.findViewById(R.id.saveBtn);
        Button cancelTask = view.findViewById(R.id.cancelBtn);

        cancelTask.setOnClickListener((v) -> { alertDialog.dismiss(); });

        saveTask.setOnClickListener((v) -> {

            String taskTitle = taskEditText.getText().toString().trim();
            String taskDescription = descriptionEditText.getText().toString().trim();
            String id = reference.push().getKey();
            String date = DateFormat.getDateInstance().format(new Date());

            if (TextUtils.isEmpty(taskTitle)) {
                taskEditText.setError("Task Title is Required");
                taskEditText.requestFocus();
                return;
            } else if (TextUtils.isEmpty(taskDescription)) {
                descriptionEditText.setError("Task Description is Required");
                descriptionEditText.requestFocus();
                return;
            } else {

                TaskModel taskModel = new TaskModel(taskTitle, taskDescription, id, date);
                reference.child(id).setValue(taskModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Task Has Been Inserted Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Failed: " + task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    private void moveToLoginActivity () {

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        ((Activity) getActivity()).overridePendingTransition(0, 0);

    }
}