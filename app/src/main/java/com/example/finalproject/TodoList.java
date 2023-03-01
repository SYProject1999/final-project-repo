package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.finalproject.adapters.TodoListStepAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TodoList extends AppCompatActivity {

    LinearLayout select_task;
    TextView date_txt;
    ImageView favorite;
    ImageView add_step;
    private int mYear, mMonth, mDay, mHour, mMinute;
    LinearLayout calendar_layout;
    TodoListStepAdapter TodoListStepAdapter;
    LinearLayout listview_steps;
    ImageView back;
    EditText new_step_edttxt;
    LinearLayout next_step,add_step_layout;
    Boolean select_task_check=false,fav_check=false;
    ArrayList<String> steps=new ArrayList();
    final Calendar myCalendar= Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        select_task=findViewById(R.id.select_task);
        calendar_layout=findViewById(R.id.calendar_layout);
        LayoutInflater layoutInflater=LayoutInflater.from(getBaseContext());
        date_txt=findViewById(R.id.date_txt);

        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        favorite=findViewById(R.id.favorite);
        next_step=findViewById(R.id.next_step);
        add_step_layout=findViewById(R.id.add_step_layout);
        new_step_edttxt=findViewById(R.id.new_step_edttxt);
        add_step=findViewById(R.id.add_step);

        listview_steps=findViewById(R.id.listview_steps);



        add_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Boolean[] step_selection_check = {false};
                View step_layout=layoutInflater.inflate(R.layout.new_step_listview_layout,null,false);
                steps.add(new_step_edttxt.getText().toString());
                TextView text=step_layout.findViewById(R.id.step_name);
                 LinearLayout step_selection = step_layout.findViewById(R.id.step_selection);
                step_selection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (step_selection_check[0]){
                            step_selection.setBackgroundResource(R.drawable.unselected_circle_bg);
                            step_selection_check[0] =false;
                        }else{
                            step_selection.setBackgroundResource(R.drawable.selected_circle_bg);
                            step_selection_check[0] =true;
                        }
                    }
                });
                text.setText(new_step_edttxt.getText().toString());
                listview_steps.addView(step_layout);

                next_step.setVisibility(View.VISIBLE);
                add_step_layout.setVisibility(View.GONE);
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
                if (fav_check){
                    favorite.setImageResource(R.drawable.ic_baseline_star_border_24);
                    fav_check=false;
                }else{
                    favorite.setImageResource(R.drawable.star_filled);
                    fav_check=true;
                }
            }
        });
        select_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select_task_check){
                    select_task.setBackgroundResource(R.drawable.unselected_circle_bg);
                    select_task_check=false;
                }else{
                    select_task.setBackgroundResource(R.drawable.selected_circle_bg);
                    select_task_check=true;
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
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
}