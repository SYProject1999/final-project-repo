package com.example.finalproject.notes;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.finalproject.R;


public class DiaryFragment extends Fragment  {

    Button addImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diary, container, false);


        addImage = view.findViewById(R.id.addImage);


            addImage.setOnClickListener(view1 -> {

                Intent intent = new Intent(getActivity(), addDiary.class);
                startActivity(intent);
            });

        return view;
    }
}