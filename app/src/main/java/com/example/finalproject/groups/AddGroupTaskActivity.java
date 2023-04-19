package com.example.finalproject.groups;

import static com.example.finalproject.models.References.GROUP_TASKS;
import static com.example.finalproject.models.References.USERS;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.finalproject.BaseActivity;
import com.example.finalproject.R;
import com.example.finalproject.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class AddGroupTaskActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    private Group currentGroup;
    Spinner spinner;
    EditText etTaskTitle,etTaskDescription, etTaskDueDate;
    Button btnSave;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_task);
        userList = new ArrayList<>();
        currentGroup = (Group) getIntent().getSerializableExtra("group");
        Toolbar mToolbar = findViewById(R.id.app_bar_layout);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Assign Task");
        init();
        setListeners();
        loadData();
    }

    private void init() {
        spinner = findViewById(R.id.spnGroupMembers);
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDueDate = findViewById(R.id.etDate);
        etTaskDueDate.getText().toString().trim();
        etTaskDescription = findViewById(R.id.etTaskDescription);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataValid()) {
                    User user = userList.get(spinner.getSelectedItemPosition()-1);
                    String taskTitle = etTaskTitle.getText().toString().trim();
                    String taskDescription = etTaskDescription.getText().toString().trim();
                    String taskDueDate = etTaskDueDate.getText().toString().trim();
                    GroupTask groupTask = new GroupTask();
                    groupTask.setTaskTitle(taskTitle);
                    groupTask.setTaskDescription(taskDescription);
                    groupTask.setTaskDueDate(taskDueDate);
                    groupTask.setAssignedToName(user.getFullName());
                    groupTask.setAssignedToId(user.getId());
                    addTask(groupTask);
                }
            }
        });
        etTaskDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                int day =  now.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddGroupTaskActivity.this, AddGroupTaskActivity.this::onDateSet,year,month,day);
                datePickerDialog.show();
            }
        });
    }

    private void loadData() {
        showProgressDialog("Loading...");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(USERS);
        for (String memberId : currentGroup.getMembers()) {
            reference.child(memberId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    user.setId(memberId);
                    userList.add(user);
                    if (userList.size() == currentGroup.getMembers().size()) {
                        setDataToSpinner();
                        closeProgressDialog();
                    }
                }
            });
        }
    }

    private void setDataToSpinner() {
        List<String> names = new ArrayList<>();
        names.add("-- Select User --");
        for(User user : userList) {
            names.add(user.getFullName());
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names.toArray(new String[0]));
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private boolean isDataValid() {
        String taskTitle = etTaskTitle.getText().toString().trim();
        String taskDescription = etTaskDescription.getText().toString().trim();
        String taskDueDate = etTaskDueDate.getText().toString();


        if (taskTitle.isEmpty()) {
            etTaskTitle.setError("Please fill task title");
            etTaskTitle.requestFocus();
            return false;
        }

        if (taskDescription.isEmpty()) {
            etTaskDescription.setError("Please fill task description");
            etTaskDescription.requestFocus();
            return false;
        }

        if (taskDueDate.isEmpty()) {
            etTaskDueDate.setError("Please fill task due date");
            etTaskDueDate.requestFocus();
            return false;
        }

        if (spinner.getSelectedItemPosition() == 0) {
            showToast("Please Select a user");
            return false;
        }

        return true;
    }

    private void addTask(GroupTask groupTask) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(GROUP_TASKS).child(currentGroup.getId());
        String taskKey = reference.push().getKey();
        groupTask.setId(taskKey);
        showProgressDialog("Saving...");
        reference.child(taskKey).setValue(groupTask).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                closeProgressDialog();
                if (task.isSuccessful()) {
                    showToast("Saved task successfully");
                    finish();
                } else {
                    showToast("Failed to add the task");
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month++;
        etTaskDueDate.setText(year + "-" + adjustValue(month) + "-" + adjustValue(dayOfMonth));
    }

    private String adjustValue(int value) {
        if (value < 10) {
            return "0" + value;
        }
        return String.valueOf(value);
    }
}