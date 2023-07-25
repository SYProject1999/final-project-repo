package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.finalproject.models.StepsModel;
import com.example.finalproject.models.TodoTaskModel;
import com.example.finalproject.todolist.MyAlarmManager;
import com.example.finalproject.todolist.TodolistFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TodoList extends AppCompatActivity {


    String id,Title,note_txt;
    Boolean isCompleted =false,reminder=false, isImportant =false;
    Long dueDateTime =0L;

    int id_int;
    private static final int CALENDAR_WRITE_PERMISSION_CODE = 100;
    private static final int CALENDAR_READ_PERMISSION_CODE = 101;

    Switch reminder_switch;
    String date_time="";
    LinearLayout select_task;
    TextView date_txt,save;

    TodoTaskModel todoTaskModel;
    StepsModel stepsModel;
    int step_id=0;
    String step_title;
    Boolean step_isCompleted;
    ArrayList<StepsModel> stepsModelArrayList=new ArrayList<>();
    ArrayList<StepsModel> stepsModelArrayListsender=new ArrayList<>();
    ImageView favorite;
    ImageView add_step;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    String userID=LoginActivity.userID;
    private int mHour, mMinute;
    LinearLayout calendar_layout;
    LinearLayout listview_steps;

    Boolean its_edit=false;

    ImageView back;
    EditText new_step_edittext,task_title,note;
    LinearLayout next_step,add_step_layout;
    TodoTaskModel todoTaskModelSender = TodolistFragment.todoTaskModelSender;
    ArrayList<String> steps=new ArrayList();
    final Calendar myCalendar= Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        checkPermission(Manifest.permission.WRITE_CALENDAR, CALENDAR_WRITE_PERMISSION_CODE);
        note=findViewById(R.id.note);
        reminder_switch=findViewById(R.id.reminder_switch);
        task_title=findViewById(R.id.task_title);
        save=findViewById(R.id.save);

        select_task=findViewById(R.id.select_task);
        calendar_layout=findViewById(R.id.calendar_layout);
        LayoutInflater layoutInflater=LayoutInflater.from(getBaseContext());
        date_txt=findViewById(R.id.date_txt);
        back=findViewById(R.id.back);
        favorite=findViewById(R.id.favorite);
        next_step=findViewById(R.id.next_step);
        add_step_layout=findViewById(R.id.add_step_layout);
        new_step_edittext =findViewById(R.id.new_step_edttxt);
        add_step=findViewById(R.id.add_step);
        listview_steps=findViewById(R.id.listview_steps);

        if (todoTaskModelSender !=null){
            its_edit=true;
            task_title.setText(todoTaskModelSender.getTitle());
            id= todoTaskModelSender.getID();
            dueDateTime = todoTaskModelSender.getDuedatetime();
            note.setText(todoTaskModelSender.getNote());
            date_txt.setText(militodate(todoTaskModelSender.getDuedatetime()));
            stepsModelArrayListsender= todoTaskModelSender.getStepsModelArrayList();

            for (int k=0;k<stepsModelArrayListsender.size();k++){
                View step_layout=layoutInflater.inflate(R.layout.new_step_listview_layout,null,false);
                TextView checker=step_layout.findViewById(R.id.checker);
                TextView text=step_layout.findViewById(R.id.step_name);
                LinearLayout step_selection = step_layout.findViewById(R.id.step_selection);
                step_selection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checker.getText().toString().equals("1")){
                            step_selection.setBackgroundResource(R.drawable.unselected_circle_bg);
                            checker.setText("0");
                            stepsModelArrayList=new ArrayList<>();
                            for (int i=0;i<listview_steps.getChildCount();i++){
                                step_id=0;
                                View layout=listview_steps.getChildAt(i);
                                TextView text=layout.findViewById(R.id.step_name);
                                TextView check=layout.findViewById(R.id.checker);
                                if (check.getText().toString().equals("1"))
                                    step_isCompleted =true;
                                else
                                    step_isCompleted =false;
                                step_title=text.getText().toString();
                                step_id++;
                                stepsModel=new StepsModel(String.valueOf(step_id),step_title, step_isCompleted);
                                stepsModelArrayList.add(stepsModel);
                            }
                        }else{
                            step_selection.setBackgroundResource(R.drawable.selected_circle_bg);
                            checker.setText("1");
                            stepsModelArrayList=new ArrayList<>();
                            for (int i=0;i<listview_steps.getChildCount();i++){

                                step_id=0;
                                View layout=listview_steps.getChildAt(i);
                                TextView text=layout.findViewById(R.id.step_name);
                                TextView check=layout.findViewById(R.id.checker);
                                if (check.getText().toString().equals("1"))
                                    step_isCompleted =true;
                                else
                                    step_isCompleted =false;
                                step_title=text.getText().toString();
                                step_id++;
                                stepsModel=new StepsModel(String.valueOf(step_id),step_title, step_isCompleted);
                                stepsModelArrayList.add(stepsModel);
                            }

                        }
                    }
                });
                if (stepsModelArrayListsender.get(k).getIscompleted()){
                    step_selection.setBackgroundResource(R.drawable.selected_circle_bg);
                    checker.setText("1");
                }else{
                    step_selection.setBackgroundResource(R.drawable.unselected_circle_bg);
                    checker.setText("0");

                }
                text.setText(stepsModelArrayListsender.get(k).getTitle());
                listview_steps.addView(step_layout);
                step_id=k;






            }



            if (todoTaskModelSender.getReminder()){
                reminder_switch.setChecked(true);
                reminder=true;
            }else{
                reminder_switch.setChecked(false);
                reminder=false;
            }

            if (todoTaskModelSender.getIsimportant()){
                favorite.setImageResource(R.drawable.star_filled);
                isImportant =true;
            }else{
                favorite.setImageResource(R.drawable.ic_baseline_star_border_24);
                isImportant =false;
            }

            if (todoTaskModelSender.getIscompleted()){
                select_task.setBackgroundResource(R.drawable.selected_circle_bg);
                isCompleted =true;
            }else{
                select_task.setBackgroundResource(R.drawable.unselected_circle_bg);
                isCompleted =false;
            }

        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        reminder_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    reminder=true;
                    checkPermission(Manifest.permission.WRITE_CALENDAR, CALENDAR_WRITE_PERMISSION_CODE);
                }else{
                    reminder=false;
                }
            }
        });



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                myRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       /* if (its_edit){

                        }else{
                           // id = new Date().getTime()+"";
                            //id=snapshot.child("task_count").getValue(String.class);
                            if (id==null){
                                id="0";
                            }
                            id_int=Integer.parseInt(id);
                            id_int++;
                            id=String.valueOf(id_int);
                           // myRef.child(userID).child("task_count").setValue(id);
                        }*/


                        Title=task_title.getText().toString();
                        note_txt=note.getText().toString();
                        if (Title.equals("")||date_txt.getText().toString().trim().equals("")||note_txt.equals("")|| dueDateTime.equals("")){
                            Toast.makeText(TodoList.this, "Fill all the details", Toast.LENGTH_SHORT).show();
                        }else{

                            todoTaskModel=new TodoTaskModel(id,Title,note_txt, isCompleted,reminder, isImportant, dueDateTime,stepsModelArrayList);
                            myRef.child(userID).child("Tasks").child(id).setValue(todoTaskModel);
                            if(its_edit){
                                Toast.makeText(TodoList.this, "Task Edited Successfully!", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(TodoList.this, "Task Created Successfully!", Toast.LENGTH_SHORT).show();
                            }
                            if(reminder){
                                MyAlarmManager.setAlarm(TodoList.this, Integer.parseInt(id) , task_title.getText().toString(), myCalendar);
                            }
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });



        add_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View step_layout=layoutInflater.inflate(R.layout.new_step_listview_layout,null,false);
                TextView checker=step_layout.findViewById(R.id.checker);
                steps.add(new_step_edittext.getText().toString());
                TextView text=step_layout.findViewById(R.id.step_name);
                LinearLayout step_selection = step_layout.findViewById(R.id.step_selection);
                step_selection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checker.getText().toString().equals("1")){
                            step_selection.setBackgroundResource(R.drawable.unselected_circle_bg);
                            checker.setText("0");
                            stepsModelArrayList=new ArrayList<>();
                            for (int i=0;i<listview_steps.getChildCount();i++){
                                step_id=0;
                                View layout=listview_steps.getChildAt(i);
                                TextView text=layout.findViewById(R.id.step_name);
                                TextView check=layout.findViewById(R.id.checker);
                                if (check.getText().toString().equals("1"))
                                    step_isCompleted =true;
                                else
                                    step_isCompleted =false;
                                step_title=text.getText().toString();
                                step_id++;
                                stepsModel=new StepsModel(String.valueOf(step_id),step_title, step_isCompleted);
                                stepsModelArrayList.add(stepsModel);
                            }
                        }else{
                            step_selection.setBackgroundResource(R.drawable.selected_circle_bg);
                            checker.setText("1");
                            stepsModelArrayList=new ArrayList<>();
                            for (int i=0;i<listview_steps.getChildCount();i++){

                                step_id=0;
                                View layout=listview_steps.getChildAt(i);
                                TextView text=layout.findViewById(R.id.step_name);
                                TextView check=layout.findViewById(R.id.checker);
                                if (check.getText().toString().equals("1"))
                                    step_isCompleted =true;
                                else
                                    step_isCompleted =false;
                                step_title=text.getText().toString();
                                step_id++;
                                stepsModel=new StepsModel(String.valueOf(step_id),step_title, step_isCompleted);
                                stepsModelArrayList.add(stepsModel);
                            }

                        }
                    }
                });
                text.setText(new_step_edittext.getText().toString());
                listview_steps.addView(step_layout);

                next_step.setVisibility(View.VISIBLE);
                add_step_layout.setVisibility(View.GONE);

                if (checker.getText().toString().equals("1"))
                    step_isCompleted =true;
                else
                    step_isCompleted =false;
                step_title= new_step_edittext.getText().toString();
                step_id++;
                stepsModel=new StepsModel(String.valueOf(step_id),step_title, step_isCompleted);
                stepsModelArrayList.add(stepsModel);
                Log.d("TAG", "stepsModelArrayList: "+stepsModelArrayList.size());
                new_step_edittext.setText("");
            }
        });
        next_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next_step.setVisibility(View.GONE);
                add_step_layout.setVisibility(View.VISIBLE);


                // show soft keyboard
                new_step_edittext.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });
        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);

                updateLabel();
            }
        };
        calendar_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(TodoList.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isImportant){
                    favorite.setImageResource(R.drawable.ic_baseline_star_border_24);
                    isImportant =false;
                }else{
                    favorite.setImageResource(R.drawable.star_filled);
                    isImportant =true;
                }
            }
        });
        select_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCompleted){
                    select_task.setBackgroundResource(R.drawable.unselected_circle_bg);
                    isCompleted =false;
                }else{
                    select_task.setBackgroundResource(R.drawable.selected_circle_bg);
                    isCompleted =true;
                }
            }
        });
    }
    private void updateLabel(){
        String myFormat="MM/dd/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        date_txt.setText(dateFormat.format(myCalendar.getTime()));
        time_picker();


    }

    private void time_picker(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);


        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        myCalendar.set(Calendar.MINUTE, minute);
                        String date_saved=date_txt.getText().toString();
                        date_txt.setText(date_saved+" - "+hourOfDay + ":" + minute);
                        date_time=date_saved+" "+hourOfDay+":"+minute+":"+"00";
                        date_to_mili(date_time);

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
    private String militodate(long milliSeconds){
        String dateFormat="dd/MM/yyyy hh:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void date_to_mili(String givenDateString){
        String dateFormat = "MM/dd/yyyy hh:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            Date mDate = sdf.parse(givenDateString);
            dueDateTime = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /** Adds Events and Reminders in Calendar. */
    private void addReminderInCalendar(Long mili) {
        checkPermission(Manifest.permission.WRITE_CALENDAR, CALENDAR_WRITE_PERMISSION_CODE);
        checkPermission(Manifest.permission.READ_CALENDAR, CALENDAR_READ_PERMISSION_CODE);
        Calendar cal = Calendar.getInstance();
        Uri EVENTS_URI = Uri.parse(getCalendarUriBase(true) + "events");
        ContentResolver cr = getContentResolver();
        TimeZone timeZone = TimeZone.getDefault();

        /* Inserting an event in calendar. */
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.TITLE, Title);
        values.put(CalendarContract.Events.DESCRIPTION, note_txt);
        values.put(CalendarContract.Events.ALL_DAY, 0);
        // event starts at 11 minutes from now
        values.put(CalendarContract.Events.DTSTART, mili);
        // ends 60 minutes from now
        values.put(CalendarContract.Events.DTEND, cal.getTimeInMillis() + 60 * 60 * 1000);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.HAS_ALARM, 1);
        Uri event = cr.insert(EVENTS_URI, values);

        Log.d("TAG", "addReminderInCalendar: ");

        /* Adding reminder for event added. */
        Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(true) + "reminders");
        values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, Long.parseLong(event.getLastPathSegment()));
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        values.put(CalendarContract.Reminders.MINUTES, 10);
        cr.insert(REMINDERS_URI, values);
    }

    /** Returns Calendar Base URI, supports both new and old OS. */
    private String getCalendarUriBase(boolean eventUri) {
        Uri calendarURI = null;
        try {
            if (android.os.Build.VERSION.SDK_INT <= 7) {
                calendarURI = (eventUri) ? Uri.parse("content://calendar/") : Uri.parse("content://calendar/calendars");
            } else {
                calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/") : Uri
                        .parse("content://com.android.calendar/calendars");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarURI.toString();
    }
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(TodoList.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(TodoList.this, new String[] { permission }, requestCode);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == CALENDAR_WRITE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(TodoList.this, "CALENDAR Permission Granted", Toast.LENGTH_SHORT) .show();
            }
        }

    }
}