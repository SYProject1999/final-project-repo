package com.example.finalproject.todolist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalproject.LoginActivity;
import com.example.finalproject.TodoList;
import com.example.finalproject.adapters.Todocompletedshowadapter;
import com.example.finalproject.models.TodoTaskModel;
import com.example.finalproject.profile.ProfileActivity;
import com.example.finalproject.R;
import com.example.finalproject.models.TaskModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class TodolistFragment extends Fragment {


    LinearLayout today, allTasks;

    ImageView userProfile;
    TextView todayCompleted,todayTotal;
    Boolean todaySelected=true;
    public static TodoTaskModel todoTaskModelSender =null;
    ArrayList<TodoTaskModel> todoTaskModelArrayList=new ArrayList<>();
    ArrayList<TodoTaskModel> completedTodoTaskModelArrayList =new ArrayList<>();
    ArrayList<TodoTaskModel> todayTodoTaskModelArrayList=new ArrayList<>();
    TodoTaskModel todoTaskModel;

    LayoutInflater layoutInflater;


    int countTodayTotal=0,countTodayCompleted=0;

    LinearLayout todoListLayout;
    Todocompletedshowadapter todocompletedshowadapter;
    private TextView username;
    ProgressDialog progressdialog;
    String currentDateAndTime;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    String userID= LoginActivity.userID;
    private DatabaseReference reference, fullNameReference;

    private String key = "", task = "", description = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_todolist, container, false);
        progressdialog = new ProgressDialog(getContext());
        progressdialog.setMessage("Loading, Please Wait....");
        progressdialog.show();
        today=view.findViewById(R.id.today);
        allTasks =view.findViewById(R.id.alltasks);
        layoutInflater=LayoutInflater.from(getActivity());
        todayCompleted=view.findViewById(R.id.todayCompleted);
        todayTotal=view.findViewById(R.id.todayTotal);

        userProfile=view.findViewById(R.id.userProfile);
        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todoListLayout.removeAllViews();
                viewChanger(today);
                todaySelected=true;

                Log.d("TAG", "TODO: size of today arraylist"+todayTodoTaskModelArrayList.size());
                for (int i=0;i<todayTodoTaskModelArrayList.size();i++){

                    todoTaskModel=todayTodoTaskModelArrayList.get(i);
                    View todoListView= LayoutInflater.from(getContext()).inflate(R.layout.main_todo_show_layout,null,false);
                    TextView title=todoListView.findViewById(R.id.task_title);
                    ImageView favourite=todoListView.findViewById(R.id.favorite);
                    TextView taskDate=todoListView.findViewById(R.id.task_date);
                    title.setText(todoTaskModel.getTitle());
                    if(todoTaskModel.getIsimportant())
                    {
                        favourite.setImageResource(R.drawable.star_filled);
                    }
                    taskDate.setText(militodate(todoTaskModel.getDuedatetime()));

                    todoListLayout.addView(todoListView);
                    LinearLayout select_task=todoListView.findViewById(R.id.select_task);

                    if (todoTaskModel.getIscompleted()){
                        select_task.setBackgroundResource(R.drawable.selected_circle_bg);
                        title.setBackgroundResource(R.drawable.strike_through_text);

                    }
                    else{
                        select_task.setBackgroundResource(R.drawable.unselected_circle_bg);
                    }

                    todoListView.setTag(todoTaskModel);
                    todoListView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            todoTaskModel= (TodoTaskModel) todoListView.getTag();
//                            Toast.makeText(getContext(),todoTaskModel.getTitle() , Toast.LENGTH_SHORT).show();
                            todoTaskModelSender =todoTaskModel;
                            Intent intent=new Intent(getActivity(), TodoList.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
        allTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewChanger(allTasks);
                todaySelected=false;
                dataBaseFetch();
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        currentDateAndTime = sdf.format(new Date());
        ImageView profile_iv = view.findViewById(R.id.userProfile);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        todoListLayout=view.findViewById(R.id.todoListLayout);
        String userID = mUser.getUid();
        todoListLayout.removeAllViews();
        myRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TAG", "onDataChange1: "+userID);
                String url=snapshot.child("imageUrl").getValue(String.class);
                String usernameString=snapshot.child("fullName").getValue(String.class);
                username.setText(usernameString);
                userImage(url);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

//                Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }
        });

        dataBaseFetch();

        username = view.findViewById(R.id.username);
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Tasks");
        fullNameReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("fullName");



        profile_iv.setOnClickListener(v -> startActivity(new Intent(getActivity(), ProfileActivity.class)));

        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener((v) -> todoListActivity());

        return view;
    }

    private void todoListActivity(){
        Intent intent=new Intent(getActivity(), TodoList.class);
        startActivity(intent);
    }
    @Override
    public void onStart() {
        super.onStart();

        fullNameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<TaskModel> options = new FirebaseRecyclerOptions.Builder<TaskModel>()
                .setQuery(reference, TaskModel.class).build();

        FirebaseRecyclerAdapter<TaskModel, MyViewHolder> adapter = new FirebaseRecyclerAdapter<TaskModel, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull TaskModel model) {
//                holder.setDate(model.getTaskDate());


                holder.setTask(model.getTaskTitle());
//                holder.setDescription(model.getTaskDescription());

                holder.holderView.setOnClickListener(view -> {
                    key = getRef(holder.getAbsoluteAdapterPosition()).getKey();
                    task = model.getTaskTitle();
                    description = model.getTaskDescription();
                    updateTask();
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layout, parent, false);
                return new MyViewHolder(view);
            }
        };

//        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View holderView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            holderView = itemView;
        }

        public void setTask(String task) {
            TextView taskTextView = holderView.findViewById(R.id.task_title);
            taskTextView.setText(task);
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

        updateBtn.setOnClickListener(view1 -> {
            task = taskTitleEditText.getText().toString().trim();
            description = taskDescriptionEditText.getText().toString().trim();
            String date = DateFormat.getDateInstance().format(new Date());

            TaskModel taskModel = new TaskModel(task, description, key, date);

            reference.child(key).setValue(taskModel).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Task Has Been Updated Successfully", Toast.LENGTH_LONG).show();
                } else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(getContext(), "Update Failed " + error, Toast.LENGTH_LONG).show();
                }
            });
            alertDialog.dismiss();
        });

        deleteBtn.setOnClickListener(view2 -> {
            reference.child(key).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Task Has Been Deleted Successfully", Toast.LENGTH_LONG).show();
                } else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(getContext(), "Delete Failed " + error, Toast.LENGTH_LONG).show();
                }
            });
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText(getContext(), "RESUME", Toast.LENGTH_SHORT).show();

        todoTaskModelSender =null    ;
        completedTodoTaskModelArrayList =new ArrayList<>();
        todoTaskModelArrayList=new ArrayList<>();
        viewChanger(allTasks);
        todaySelected=false;
        dataBaseFetch();
    }
    private String militodate(long milliSeconds){
        String dateFormat="dd/MM/yyyy hh:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        Log.d("TAG", "milli: "+formatter.format(calendar.getTime()));
        return formatter.format(calendar.getTime());
    }

    public void viewChanger(View view){
        today.setBackgroundResource(R.drawable.greybtn24dp);
        allTasks.setBackgroundResource(R.drawable.greybtn24dp);
        view.setBackgroundResource(R.drawable.lightbluebtn24dp);
    }


    public void dataBaseFetch(){
        countTodayCompleted=0;
        countTodayTotal=0;

        myRef.child(userID).child("Tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                completedTodoTaskModelArrayList =new ArrayList<>();
                todoTaskModelArrayList=new ArrayList<>();
                todayTodoTaskModelArrayList=new ArrayList<>();
                todoListLayout.removeAllViews();
                countTodayCompleted=0;
                countTodayTotal=0;
                todayTotal.setText("/"+String.valueOf(countTodayTotal));
                todayCompleted.setText(String.valueOf(countTodayCompleted));


                for(DataSnapshot dataSnapshot: snapshot.getChildren()){

                    todoTaskModel =dataSnapshot.getValue(TodoTaskModel.class);
                    // TO GET DATE OF TASK

                    if (todoTaskModel.getIscompleted()){
                        completedTodoTaskModelArrayList.add(todoTaskModel);

                        todocompletedshowadapter=new Todocompletedshowadapter(getActivity(), android.R.layout.simple_list_item_1, completedTodoTaskModelArrayList);


                    }else{
                        todoTaskModelArrayList.add(todoTaskModel);

                    }
                }
                for (int i=0;i<todoTaskModelArrayList.size();i++){

                    Comparator<TodoTaskModel> numberComparator = new Comparator<TodoTaskModel>() {
                        @Override
                        public int compare(TodoTaskModel todoTaskModel, TodoTaskModel t1) {
                            return Boolean.compare(todoTaskModel.getIsimportant(), t1.getIsimportant());
                        }
                    };


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Collections.sort(todoTaskModelArrayList,numberComparator.reversed());
                    }


                    todoTaskModel=todoTaskModelArrayList.get(i);
                    String taskDateS=militodate(todoTaskModel.getDuedatetime());
                    taskDateS=taskDateS.substring(0,10);
//                    Toast.makeText(getContext(), taskDateS, Toast.LENGTH_SHORT).show();

                    //compare task date to today's date
                    if (taskDateS.equals(currentDateAndTime)){
                        todayTodoTaskModelArrayList.add(todoTaskModel);
                        countTodayTotal++;
                        todayTotal.setText("/"+String.valueOf(countTodayTotal));
                        todayCompleted.setText(String.valueOf(countTodayCompleted));
                        Log.d("TAG", "TODO: TASK ADDED");
                    }
                    View todoListView= layoutInflater.inflate(R.layout.main_todo_show_layout,null,false);
                    TextView title=todoListView.findViewById(R.id.task_title);
                    ImageView favourite=todoListView.findViewById(R.id.favorite);
                    TextView taskDate=todoListView.findViewById(R.id.task_date);
                    title.setText(todoTaskModel.getTitle());


                    if(todoTaskModel.getIsimportant()==true)
                    {
                        favourite.setImageResource(R.drawable.star_filled);
                    }

                    taskDate.setText(militodate(todoTaskModel.getDuedatetime()));
                    todoListLayout.addView(todoListView);
                    todoListView.setTag(todoTaskModel);
                    todoListView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            todoTaskModel= (TodoTaskModel) todoListView.getTag();
                            todoTaskModelSender =todoTaskModel;
                            Intent intent=new Intent(getActivity(), TodoList.class);
                            startActivity(intent);
                        }
                    });
                }
                for (int i = 0; i< completedTodoTaskModelArrayList.size(); i++){

                    Comparator<TodoTaskModel> numberComparator = new Comparator<TodoTaskModel>() {
                        @Override
                        public int compare(TodoTaskModel todoTaskModel, TodoTaskModel t1) {
                            return Boolean.compare(todoTaskModel.getIsimportant(), t1.getIsimportant());
                        }
                    };


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Collections.sort(completedTodoTaskModelArrayList,numberComparator.reversed());
                    }
                    todoTaskModel= completedTodoTaskModelArrayList.get(i);
                    String taskDateS=militodate(todoTaskModel.getDuedatetime());
                    taskDateS=taskDateS.substring(0,10);
//                    Toast.makeText(getContext(), taskDateS, Toast.LENGTH_SHORT).show();

                    //compare task date to today's date
                    if (taskDateS.equals(currentDateAndTime)){
                        Log.d("TAG", "TODO: TASK ADDED");
                        todayTodoTaskModelArrayList.add(todoTaskModel);
                        countTodayTotal++;
                        countTodayCompleted++;
                        todayTotal.setText("/"+String.valueOf(countTodayTotal));
                        todayCompleted.setText(String.valueOf(countTodayCompleted));

                    }
                    View todoListView= layoutInflater.inflate(R.layout.main_todo_show_layout,null,false);
                    TextView title=todoListView.findViewById(R.id.task_title);
                    TextView taskDate=todoListView.findViewById(R.id.task_date);
                    title.setText(todoTaskModel.getTitle());
                    ImageView favourite=todoListView.findViewById(R.id.favorite);

                    if(todoTaskModel.getIsimportant()==true)
                    {
                        favourite.setImageResource(R.drawable.star_filled);
                    }
                    LinearLayout select_task=todoListView.findViewById(R.id.select_task);
                    if (todoTaskModel.getIscompleted()){
                        select_task.setBackgroundResource(R.drawable.selected_circle_bg);
                    }
                    else{
                        select_task.setBackgroundResource(R.drawable.unselected_circle_bg);
                    }
                    select_task.setBackgroundResource(R.drawable.selected_circle_bg);
                    title.setBackgroundResource(R.drawable.strike_through_text);
                    taskDate.setText(militodate(todoTaskModel.getDuedatetime()));
                    todoListLayout.addView(todoListView);
                    todoListView.setTag(todoTaskModel);
                    todoListView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            todoTaskModel= (TodoTaskModel) todoListView.getTag();
//                            Toast.makeText(getContext(),todoTaskModel.getTitle() , Toast.LENGTH_SHORT).show();
                            todoTaskModelSender =todoTaskModel;
                            Intent intent=new Intent(getActivity(), TodoList.class);
                            startActivity(intent);
                        }
                    });
                }
                progressdialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                progressdialog.dismiss();
                Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void userImage(String url){
//        Toast.makeText(getContext(), url, Toast.LENGTH_SHORT).show();
        if (getActivity() == null) {
            return;
        }
        Glide.with(getActivity()).load(url).into(userProfile);


    }

}