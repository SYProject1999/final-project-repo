package com.example.finalproject.notes;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.finalproject.R;


public class DiaryFragment extends Fragment  {

    CalendarView calendarView;
    TextView myDate;

    Button addImage;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diary, container, false);


        addImage = view.findViewById(R.id.addImage);

        calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        myDate = (TextView) view.findViewById(R.id.myDate);

            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                    String date = (i1 + 1) + "/"+ i2 + "/" +i;
                    myDate.setText(date);
                }
            });


            addImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), addDiary.class);
                    startActivity(intent);
                }
            });

        return view;
    }
}