package com.example.finalproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class HabitsFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private View view;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference reference;
    private String userID;

    private int drawableSelected = 0, day = 0, month = 0, year = 0, hour = 0, minute = 0;
    private String key = "", habitsTitle = "", habitsDescription = "", timeStamp = "", cleanDate = "", cleanTime = "";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_habits, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Habits");

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_habits);
        recyclerView.setHasFixedSize(true);

        floatingActionButton = view.findViewById(R.id.fab_add_habit);

        floatingActionButton.setOnClickListener(v -> addHabit());
        pickDateAndTime();
        selectedDrawable();


        return view;
    }

    private void addHabit() {
    }

    private void pickDateAndTime() {
    }

    private void selectedDrawable() {
        ImageView iv_fastFoodSelected = (ImageView) view.findViewById(R.id.iv_fastFoodSelected);
        ImageView iv_teaSelected = (ImageView) view.findViewById(R.id.iv_teaSelected);
        ImageView iv_smokingSelected = (ImageView) view.findViewById(R.id.iv_smokingSelected);

        iv_fastFoodSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_fastFoodSelected.setSelected(!iv_fastFoodSelected.isSelected());
                drawableSelected = R.drawable.ic_baseline_fastfood_24;
                iv_smokingSelected.setSelected(false);
                iv_teaSelected.setSelected(false);
            }
        });

        iv_smokingSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_smokingSelected.setSelected(!iv_smokingSelected.isSelected());
                drawableSelected = R.drawable.ic_baseline_smoking_rooms_24;
                iv_fastFoodSelected.setSelected(false);
                iv_teaSelected.setSelected(false);
            }
        });

        iv_teaSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_teaSelected.setSelected(!iv_teaSelected.isSelected());
                drawableSelected = R.drawable.ic_baseline_emoji_food_beverage_24;
                iv_fastFoodSelected.setSelected(false);
                iv_smokingSelected.setSelected(false);
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

    }

    private void getTimeCalender() {
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    private void getDateCalender() {
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
    }
}