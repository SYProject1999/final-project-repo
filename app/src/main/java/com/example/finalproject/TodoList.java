package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
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

import com.example.finalproject.adapters.TodoListStepAdapter;
import com.example.finalproject.models.StepsModel;
import com.example.finalproject.models.TodoTaskModel;
import com.example.finalproject.todolist.TodolistFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TodoList extends AppCompatActivity {


    String id,Title,note_txt;
    Boolean iscompleted=false,reminder=false,isimportant=false;
    Long duedatetime=0L;

    int id_int;

    Switch reminder_switch;
    String date_time="";
    LinearLayout select_task;
    TextView date_txt,save;

    TodoTaskModel todoTaskModel;
    StepsModel stepsModel;
    int step_id=0;
    String step_title;
    Boolean step_iscompleted;
    ArrayList<StepsModel> stepsModelArrayList=new ArrayList<>();
    ArrayList<StepsModel> stepsModelArrayListsender=new ArrayList<>();
    ImageView favorite;
    ImageView add_step;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");
    String userID=LoginActivity.userID;
    private int mYear, mMonth, mDay, mHour, mMinute;
    LinearLayout calendar_layout;
    TodoListStepAdapter TodoListStepAdapter;
    LinearLayout listview_steps;

    Boolean its_edit=false;

    ImageView back;
    EditText new_step_edttxt,task_title,note;
    LinearLayout next_step,add_step_layout;
    TodoTaskModel todoTaskModelsender= TodolistFragment.todoTaskModelsender;
    Boolean select_task_check=false,fav_check=false;
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
        new_step_edttxt=findViewById(R.id.new_step_edttxt);
        add_step=findViewById(R.id.add_step);
        listview_steps=findViewById(R.id.listview_steps);

        if (todoTaskModelsender!=null){
            Log.d("TAG", "TAGTAF: "+todoTaskModelsender.getTitle().toString());
            its_edit=true;
            task_title.setText(todoTaskModelsender.getTitle());
            id=todoTaskModelsender.getID();
            duedatetime=todoTaskModelsender.getDuedatetime();
            note.setText(todoTaskModelsender.getNote());
            date_txt.setText(militodate(todoTaskModelsender.getDuedatetime()));
            stepsModelArrayListsender=todoTaskModelsender.getStepsModelArrayList();

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
                                    step_iscompleted=true;
                                else
                                    step_iscompleted=false;
//                                Log.d("TAG", "getDrawableState: "+step_sel.getdrawab;e().toString());
                                step_title=text.getText().toString();
                                step_id++;
                                stepsModel=new StepsModel(String.valueOf(step_id),step_title,step_iscompleted);
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
                                    step_iscompleted=true;
                                else
                                    step_iscompleted=false;
//                                Log.d("TAG", "getDrawableState: "+step_sel.getdrawab;e().toString());
                                step_title=text.getText().toString();
                                step_id++;
                                stepsModel=new StepsModel(String.valueOf(step_id),step_title,step_iscompleted);
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



            if (todoTaskModelsender.getReminder()){
                reminder_switch.setChecked(true);
                reminder=true;
            }else{
                reminder_switch.setChecked(false);
                reminder=false;
            }

            if (todoTaskModelsender.getIsimportant()){
                favorite.setImageResource(R.drawable.star_filled);
                isimportant=true;
            }else{
                favorite.setImageResource(R.drawable.ic_baseline_star_border_24);
                isimportant=false;
            }

            if (todoTaskModelsender.getIscompleted()){
                select_task.setBackgroundResource(R.drawable.selected_circle_bg);
                iscompleted=true;
            }else{
                select_task.setBackgroundResource(R.drawable.unselected_circle_bg);
                iscompleted=false;
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
                        if (its_edit){

                        }else{
                            id=snapshot.child("task_count").getValue(String.class);
                            if (id==null){
                                id="0";
                            }
                            id_int=Integer.parseInt(id);
                            id_int++;
                            id=String.valueOf(id_int);
                            myRef.child(userID).child("task_count").setValue(id);
                        }


                        Title=task_title.getText().toString();
                        note_txt=note.getText().toString();
                        if (Title.equals("")||note_txt.equals("")||duedatetime.equals("")){
                            Toast.makeText(TodoList.this, "Fill all the details", Toast.LENGTH_SHORT).show();
                        }else{
                            todoTaskModel=new TodoTaskModel(id,Title,note_txt,iscompleted,reminder,isimportant,duedatetime,stepsModelArrayList);
                            myRef.child(userID).child("Tasks").child(id).setValue(todoTaskModel);
                            if(its_edit){
                                Toast.makeText(TodoList.this, "Task Edited Successfully!", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(TodoList.this, "Task Created Successfully!", Toast.LENGTH_SHORT).show();
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
                steps.add(new_step_edttxt.getText().toString());
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
                                    step_iscompleted=true;
                                else
                                    step_iscompleted=false;
//                                Log.d("TAG", "getDrawableState: "+step_sel.getdrawab;e().toString());
                                step_title=text.getText().toString();
                                step_id++;
                                stepsModel=new StepsModel(String.valueOf(step_id),step_title,step_iscompleted);
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
                                    step_iscompleted=true;
                                else
                                    step_iscompleted=false;
//                                Log.d("TAG", "getDrawableState: "+step_sel.getdrawab;e().toString());
                                step_title=text.getText().toString();
                                step_id++;
                                stepsModel=new StepsModel(String.valueOf(step_id),step_title,step_iscompleted);
                                stepsModelArrayList.add(stepsModel);
                            }

                        }
                    }
                });
                text.setText(new_step_edttxt.getText().toString());
                listview_steps.addView(step_layout);

                next_step.setVisibility(View.VISIBLE);
                add_step_layout.setVisibility(View.GONE);
                ;

                if (checker.getText().toString().equals("1"))
                    step_iscompleted=true;
                else
                    step_iscompleted=false;
                step_title=new_step_edttxt.getText().toString();
                step_id++;
                stepsModel=new StepsModel(String.valueOf(step_id),step_title,step_iscompleted);
                stepsModelArrayList.add(stepsModel);
                Log.d("TAG", "stepsModelArrayList: "+stepsModelArrayList.size());
                new_step_edttxt.setText("");
            }
        });
        next_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next_step.setVisibility(View.GONE);
                add_step_layout.setVisibility(View.VISIBLE);


                // show soft keyboard
                new_step_edttxt.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
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
                if (isimportant){
                    favorite.setImageResource(R.drawable.ic_baseline_star_border_24);
                    isimportant=false;
                }else{
                    favorite.setImageResource(R.drawable.star_filled);
                    isimportant=true;
                }
            }
        });
        select_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iscompleted){
                    select_task.setBackgroundResource(R.drawable.unselected_circle_bg);
                    iscompleted=false;
                }else{
                    select_task.setBackgroundResource(R.drawable.selected_circle_bg);
                    iscompleted=true;
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

                        String date_saved=date_txt.getText().toString();
                        date_txt.setText(date_saved+" - "+hourOfDay + ":" + minute);
                        date_time=date_saved+" "+hourOfDay+":"+minute+":"+"00";
                        Log.d("TAG", "milli: "+date_time);
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
        Log.d("TAG", "milli: "+formatter.format(calendar.getTime()));
        return formatter.format(calendar.getTime());
    }

    private void date_to_mili(String givenDateString){
//         givenDateString = "03/02/2023 12:25:00";
        String dateFormat = "dd/MM/yyyy hh:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            Date mDate = sdf.parse(givenDateString);
            duedatetime = mDate.getTime();
            Log.d("TAG","milli :: " + duedatetime);
//                    militodate(duedatetime);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("TAG", "data_to_mili: "+e);

        }
    }
<<<<<<< HEAD
    public void createNotification (Long mili) {
        Intent myIntent = new Intent(getApplicationContext() , NotifyService. class ) ;
        AlarmManager alarmManager = (AlarmManager) getSystemService( ALARM_SERVICE ) ;
        PendingIntent pendingIntent = PendingIntent. getService ( this, 0 , myIntent , PendingIntent.FLAG_CANCEL_CURRENT ) ;
        Calendar calendar = Calendar.getInstance () ;
        calendar.set(Calendar. SECOND , 0 ) ;
        calendar.set(Calendar. MINUTE , 0 ) ;
        calendar.set(Calendar. HOUR , 0 ) ;
        calendar.set(Calendar. AM_PM , Calendar. AM ) ;
        calendar.add(Calendar. DAY_OF_MONTH , 1 ) ;
        Log.d("TAG", "createNotification: "+calendar.getTimeInMillis());
        alarmManager.set(AlarmManager.RTC_WAKEUP , calendar.getTimeInMillis()  , pendingIntent) ;

        Log.d("TAG", "createNotification: ");

    }

    /** Adds Events and Reminders in Calendar. */
    private void addReminderInCalendar(Long mili) {
        checkPermission(Manifest.permission.WRITE_CALENDAR, CALENDAR_WRITE_PERMISSION_CODE);
        checkPermission(Manifest.permission.READ_CALENDAR, CALENDAR_READ_PERMISSION_CODE);
        Calendar cal = Calendar.getInstance();
        Uri EVENTS_URI = Uri.parse(getCalendarUriBase(true) + "events");
        ContentResolver cr = getContentResolver();
        TimeZone timeZone = TimeZone.getDefault();

        /** Inserting an event in calendar. */
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
        // Display event id
//        Toast.makeText(getApplicationContext(), "Event added :: ID :: " + event.getLastPathSegment(), Toast.LENGTH_SHORT).show();

        /** Adding reminder for event added. */
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
        else {
//            Toast.makeText(TodoList.this, "Permission already granted", Toast.LENGTH_SHORT).show();

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
            else {
//                Toast.makeText(TodoList.this, "CALENDAR Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }

    }
=======
>>>>>>> parent of 8f8bb2b (Reminder option added)
}