package com.example.finalproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.finalproject.databinding.FragmentSetTimerBinding;

public class SetTimerFragment extends Fragment {

    AutoCompleteTextView autoCompleteTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_timer, container, false);

        String[] focusTimeArray = getResources().getStringArray(R.array.focusTime);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdown_item, focusTimeArray);
        autoCompleteTextView = view.findViewById(R.id.focusTimeAutoCompleteTextView);
        autoCompleteTextView.setAdapter(arrayAdapter);

        return view;
    }
}