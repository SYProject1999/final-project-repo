package com.example.finalproject.todolist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalproject.LoginActivity;
import com.example.finalproject.TodoList;
import com.example.finalproject.adapters.Todocompletedshowadapter;
import com.example.finalproject.adapters.Todoshowadapter;
import com.example.finalproject.models.StepsModel;
import com.example.finalproject.models.TodoTaskModel;
import com.example.finalproject.profile.ProfileActivity;
import com.example.finalproject.R;
import com.example.finalproject.models.TaskModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TodolistFragment extends Fragment {

    private RecyclerView recyclerView;

<<<<<<< HEAD
    ImageView userProfile;
    TextView todayCompleted,todayTotal;
    Boolean todaySelected=true;
=======
>>>>>>> parent of 8f8bb2b (Reminder option added)
    public static TodoTaskModel todoTaskModelsender=null;
    ArrayList<TodoTaskModel> todoTaskModelArrayList=new ArrayList<>();
    ArrayList<TodoTaskModel> completedtodoTaskModelArrayList=new ArrayList<>();
    TodoTaskModel todoTaskModel;
    StepsModel stepsModel;
<<<<<<< HEAD

    LayoutInflater layoutInflater;


    int countTodayTotal=0,countTodayCompleted=0;

    LinearLayout todoListLayout;
=======
>>>>>>> parent of 8f8bb2b (Reminder option added)
    Todoshowadapter todoshowadapter;
    Todocompletedshowadapter todocompletedshowadapter;
    ListView listView,completed_listview;
    private TextView username;
<<<<<<< HEAD
    ProgressDialog progressdialog;
    String currentDateandTime;
=======

>>>>>>> parent of 8f8bb2b (Reminder option added)
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    String userID= LoginActivity.userID;
    private DatabaseReference reference, fullNameReference;

    private String key = "", task = "", description = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_todolist, container, false);
<<<<<<< HEAD
        progressdialog = new ProgressDialog(getContext());
        progressdialog.setMessage("Loading, Please Wait....");
        progressdialog.show();
        today=view.findViewById(R.id.today);
        alltasks=view.findViewById(R.id.alltasks);
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
                    TextView taskDate=todoListView.findViewById(R.id.task_date);
                    title.setText(todoTaskModel.getTitle());
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
                            todoTaskModelsender=todoTaskModel;
                            Intent intent=new Intent(getActivity(), TodoList.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
        alltasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewChanger(alltasks);
                todaySelected=false;
                dataBaseFetch();
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
         currentDateandTime = sdf.format(new Date());
//        Toast.makeText(getContext(), currentDateandTime, Toast.LENGTH_SHORT).show();

//        Toast.makeText(getContext(), "Activity Started", Toast.LENGTH_SHORT).show();
//        completed_listview=view.findViewById(R.id.completed_listview);
//        listView=view.findViewById(R.id.listview);
=======

        completed_listview=view.findViewById(R.id.completed_listview);
        listView=view.findViewById(R.id.listview);
>>>>>>> parent of 8f8bb2b (Reminder option added)
        ImageView profile_iv = view.findViewById(R.id.userProfile);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        String userID = mUser.getUid();
<<<<<<< HEAD
        todoListLayout.removeAllViews();
        myRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             Log.d("TAG", "onDataChange1: "+userID);
                String url=snapshot.child("imageUrl").getValue(String.class);
                String usernamestring=snapshot.child("fullName").getValue(String.class);
                username.setText(usernamestring);
                userimage(url);
=======

        myRef.child(userID).child("Tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                completedtodoTaskModelArrayList=new ArrayList<>();
                todoTaskModelArrayList=new ArrayList<>();
                todocompletedshowadapter=new Todocompletedshowadapter(getActivity(), android.R.layout.simple_list_item_1,completedtodoTaskModelArrayList);
                completed_listview.setAdapter(todocompletedshowadapter);
                todoshowadapter=new Todoshowadapter(getActivity(), android.R.layout.simple_list_item_1,todoTaskModelArrayList);
                listView.setAdapter(todoshowadapter);


                for(DataSnapshot dataSnapshot: snapshot.getChildren()){

                    todoTaskModel =dataSnapshot.getValue(TodoTaskModel.class);
                    if (todoTaskModel.getIscompleted()){
//                        Log.d("TAG", "debuding added to completed arraylist == "+completedtodoTaskModelArrayList.size());
                        completedtodoTaskModelArrayList.add(todoTaskModel);
//                        Log.d("TAG", "ssssss complete: "+completedtodoTaskModelArrayList.size());
                        todocompletedshowadapter=new Todocompletedshowadapter(getActivity(), android.R.layout.simple_list_item_1,completedtodoTaskModelArrayList);
                        completed_listview.setAdapter(todocompletedshowadapter);
                    }else{
                        todoTaskModelArrayList.add(todoTaskModel);
                        Log.d("TAG", "ssssss onDataChange todo: "+todoTaskModelArrayList.size());
                        todoshowadapter=new Todoshowadapter(getActivity(), android.R.layout.simple_list_item_1,todoTaskModelArrayList);
                        listView.setAdapter(todoshowadapter);
                    }



                }

>>>>>>> parent of 8f8bb2b (Reminder option added)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

<<<<<<< HEAD
//                Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }
        });

        dataBaseFetch();

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                todoshowadapter.getItem(position);
//                todoTaskModelsender=todoshowadapter.getItem(position);
//                Intent intent=new Intent(getActivity(), TodoList.class);
//                startActivity(intent);
//            }
//        });
//        completed_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//
//                todoTaskModelsender=todocompletedshowadapter.getItem(position);
//                Intent intent=new Intent(getActivity(), TodoList.class);
//                startActivity(intent);
//            }
//        });
=======
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                todoshowadapter.getItem(position);
                todoTaskModelsender=todoshowadapter.getItem(position);
                Intent intent=new Intent(getActivity(), TodoList.class);
                startActivity(intent);
            }
        });
        completed_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                todoTaskModelsender=todocompletedshowadapter.getItem(position);
                Intent intent=new Intent(getActivity(), TodoList.class);
                startActivity(intent);
            }
        });
>>>>>>> parent of 8f8bb2b (Reminder option added)

        username = view.findViewById(R.id.username);
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Tasks");
        fullNameReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("fullName");



        profile_iv.setOnClickListener(v -> startActivity(new Intent(getActivity(), ProfileActivity.class)));

//        recyclerView = view.findViewById(R.id.recyclerView);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(recyclerView.getAdapter());
//        recyclerView.setLayoutManager(linearLayoutManager);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener((v) -> todoListActivity());

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
                assert id != null;
                reference.child(id).setValue(taskModel).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Task Has Been Inserted Successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Failed: " + Objects.requireNonNull(task.getException()), Toast.LENGTH_LONG).show();
                    }
                });
            }
            alertDialog.dismiss();
        });
        alertDialog.show();
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

//        public void setDescription(String description) {
//            TextView descriptionTextView = holderView.findViewById(R.id.date);
//            descriptionTextView.setText(description);
//        }

//        public void setDate(String date) {
//            TextView dateTextView = holderView.findViewById(R.id.date);
//            dateTextView.setText(date);
//        }
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
        todoTaskModelsender=null    ;
        completedtodoTaskModelArrayList=new ArrayList<>();
        todoTaskModelArrayList=new ArrayList<>();
        Log.d("TAG", "debuding: on resumer");
<<<<<<< HEAD
        viewChanger(alltasks);
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
        alltasks.setBackgroundResource(R.drawable.greybtn24dp);
        view.setBackgroundResource(R.drawable.lightbluebtn24dp);
    }


    public void dataBaseFetch(){
        countTodayCompleted=0;
        countTodayTotal=0;

        myRef.child(userID).child("Tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                completedtodoTaskModelArrayList=new ArrayList<>();
                todoTaskModelArrayList=new ArrayList<>();
                todayTodoTaskModelArrayList=new ArrayList<>();
                todoListLayout.removeAllViews();
                countTodayCompleted=0;
                countTodayTotal=0;
                todayTotal.setText("/"+String.valueOf(countTodayTotal));
                todayCompleted.setText(String.valueOf(countTodayCompleted));

//                todocompletedshowadapter=new Todocompletedshowadapter(getActivity(), android.R.layout.simple_list_item_1,completedtodoTaskModelArrayList);
//                completed_listview.setAdapter(todocompletedshowadapter);
//                todoshowadapter=new Todoshowadapter(getActivity(), android.R.layout.simple_list_item_1,todoTaskModelArrayList);
//                listView.setAdapter(todoshowadapter);


                for(DataSnapshot dataSnapshot: snapshot.getChildren()){

                    todoTaskModel =dataSnapshot.getValue(TodoTaskModel.class);
                    // TO GET DATE OF TASK


                    if (todoTaskModel.getIscompleted()){
//                        Log.d("TAG", "debuding added to completed arraylist == "+completedtodoTaskModelArrayList.size());
                        completedtodoTaskModelArrayList.add(todoTaskModel);
//                        Log.d("TAG", "ssssss complete: "+completedtodoTaskModelArrayList.size());
                        todocompletedshowadapter=new Todocompletedshowadapter(getActivity(), android.R.layout.simple_list_item_1,completedtodoTaskModelArrayList);

                    }else{
                        todoTaskModelArrayList.add(todoTaskModel);
                        Log.d("TAG", "ssssss onDataChange todo: "+todoTaskModelArrayList.size());

                    }
                }
                for (int i=0;i<todoTaskModelArrayList.size();i++){
                    todoTaskModel=todoTaskModelArrayList.get(i);
                    String taskDateS=militodate(todoTaskModel.getDuedatetime());
                    taskDateS=taskDateS.substring(0,10);
//                    Toast.makeText(getContext(), taskDateS, Toast.LENGTH_SHORT).show();

                    //compare task date to today's date
                    if (taskDateS.equals(currentDateandTime)){
                        todayTodoTaskModelArrayList.add(todoTaskModel);
                        countTodayTotal++;
                        todayTotal.setText("/"+String.valueOf(countTodayTotal));
                        todayCompleted.setText(String.valueOf(countTodayCompleted));
                        Log.d("TAG", "TODO: TASK ADDED");
                    }
                    View todoListView= layoutInflater.inflate(R.layout.main_todo_show_layout,null,false);
                    TextView title=todoListView.findViewById(R.id.task_title);
                    TextView taskDate=todoListView.findViewById(R.id.task_date);
                    title.setText(todoTaskModel.getTitle());
                    taskDate.setText(militodate(todoTaskModel.getDuedatetime()));
                    todoListLayout.addView(todoListView);
                    todoListView.setTag(todoTaskModel);
                    todoListView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            todoTaskModel= (TodoTaskModel) todoListView.getTag();
//                            Toast.makeText(getContext(),todoTaskModel.getTitle() , Toast.LENGTH_SHORT).show();
                            todoTaskModelsender=todoTaskModel;
                            Intent intent=new Intent(getActivity(), TodoList.class);
                            startActivity(intent);
                        }
                    });
                }
                for (int i=0;i<completedtodoTaskModelArrayList.size();i++){
                    todoTaskModel=completedtodoTaskModelArrayList.get(i);
                    String taskDateS=militodate(todoTaskModel.getDuedatetime());
                    taskDateS=taskDateS.substring(0,10);
//                    Toast.makeText(getContext(), taskDateS, Toast.LENGTH_SHORT).show();

                    //compare task date to today's date
                    if (taskDateS.equals(currentDateandTime)){
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
                            todoTaskModelsender=todoTaskModel;
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

    public void userimage(String url){
//        Toast.makeText(getContext(), url, Toast.LENGTH_SHORT).show();
        if (getActivity() == null) {
            return;
        }
        Glide.with(getActivity()).load(url).into(userProfile);


    }

=======

    }
>>>>>>> parent of 8f8bb2b (Reminder option added)
}