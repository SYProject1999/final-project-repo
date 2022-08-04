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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.models.TaskModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    private String key = "", task = "", description = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_todolist, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String userID = mUser.getUid();
        Button logoutBtn = view.findViewById(R.id.logoutBtn);
        TextView username = view.findViewById(R.id.username);
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Tasks");
//        username.setText(reference.child("fullName").toString());


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerView.getAdapter());
        recyclerView.setLayoutManager(linearLayoutManager);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab);

        floatingActionButton.setOnClickListener((v) -> addTask());

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            moveToLoginActivity();
        });
        return view;
    }

    private void addTask() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View dialogView = inflater.inflate(R.layout.todolist_input_layout, null);
        myDialog.setView(dialogView);

        AlertDialog alertDialog = myDialog.create();
        alertDialog.setCancelable(false);

        final EditText taskEditText = dialogView.findViewById(R.id.task);
        final EditText descriptionEditText = dialogView.findViewById(R.id.description);
        Button saveTask = dialogView.findViewById(R.id.saveBtn);
        Button cancelTask = dialogView.findViewById(R.id.cancelBtn);

        cancelTask.setOnClickListener((v) ->  alertDialog.dismiss());

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

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<TaskModel> options = new FirebaseRecyclerOptions.Builder<TaskModel>()
                .setQuery(reference, TaskModel.class).build();

        FirebaseRecyclerAdapter<TaskModel, MyViewHolder> adapter = new FirebaseRecyclerAdapter<TaskModel, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull TaskModel model) {
                holder.setDate(model.getTaskDate());
                holder.setTask(model.getTaskTitle());
                holder.setDescription(model.getTaskDescription());

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        key = getRef(holder.getAbsoluteAdapterPosition()).getKey();
                        task = model.getTaskTitle();
                        description = model.getTaskDescription();
                        updateTask();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layout, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setTask(String task) {
            TextView taskTextView = view.findViewById(R.id.taskLayoutTV);
            taskTextView.setText(task);
        }

        public void setDescription(String description) {
            TextView descriptionTextView = view.findViewById(R.id.descriptionLayoutTV);
            descriptionTextView.setText(description);
        }

        public void setDate(String date) {
            TextView dateTextView = view.findViewById(R.id.dateLayoutTV);
            dateTextView.setText(date);
        }
    }

    private void updateTask() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.todolist_update_layout, null);
        myDialog.setView(view);

        AlertDialog alertDialog = myDialog.create();

        EditText taskTitleEditText = view.findViewById(R.id.updateTaskTitleET);
        EditText taskDescriptionEditText = view.findViewById(R.id.updateTaskDescriptionET);

        taskTitleEditText.setText(task);
        taskTitleEditText.setSelection(task.length());

        taskDescriptionEditText.setText(description);
        taskDescriptionEditText.setSelection(description.length());

        Button deleteBtn = view.findViewById(R.id.deleteBtn);
        Button updateBtn = view.findViewById(R.id.updateBtn);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task = taskTitleEditText.getText().toString().trim();
                description = taskDescriptionEditText.getText().toString().trim();
                String date = DateFormat.getDateInstance().format(new Date());
                TaskModel taskModel = new TaskModel(task, description, key, date);

                reference.child(key).setValue(taskModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Task Has Been Updated Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(getContext(), "Update Failed " + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
                alertDialog.dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Task Has Been Deleted Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(getContext(), "Delete Failed " + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void moveToLoginActivity () {

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        ((Activity) getActivity()).overridePendingTransition(0, 0);

    }
}