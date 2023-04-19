package com.example.finalproject.groups;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.finalproject.BaseActivity;
import com.example.finalproject.R;
import com.example.finalproject.models.FirebaseReference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class GroupTaskDetailActivity extends BaseActivity {

    private GroupTask groupTask;
    private Group currentGroup;
    private TextView tvTaskTitle, tvTaskDueDate, tvAssignedTo;
    private EditText etTaskDescription;
    private Button btnDelete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_task_detail);
        groupTask = (GroupTask) getIntent().getSerializableExtra("groupTask");
        currentGroup = (Group) getIntent().getSerializableExtra("group");
        init();
        setListeners();
        Toolbar mToolbar = findViewById(R.id.app_bar_layout);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Task Detail");
    }

    private void init() {
        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        tvTaskDueDate = findViewById(R.id.tvTaskDueDate);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        tvAssignedTo = findViewById(R.id.tvAssignedTo);
        btnDelete = findViewById(R.id.btnDelete);
        if (currentGroup.getCreatedBy().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            btnDelete.setVisibility(View.VISIBLE);
        }

        tvTaskTitle.setText(groupTask.getTaskTitle());
        tvAssignedTo.setText(groupTask.getAssignedToName());
        tvTaskDueDate.setText(groupTask.getTaskDueDate());
        etTaskDescription.setText(groupTask.getTaskDescription());
    }

    private void setListeners() {
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGroupTask();
            }
        });
    }


    private void deleteGroupTask() {
        showProgressDialog("Deleting...");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(FirebaseReference.GROUP_TASKS).child(currentGroup.getId());
        reference.child(groupTask.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                closeProgressDialog();
                if (task.isSuccessful()) {
                    showToast("Task deleted successfully");
                    finish();
                } else {
                    showToast("Failed to delete the task");
                }
            }
        });
    }
}