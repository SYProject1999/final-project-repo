package com.example.finalproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SetTimerFragment extends Fragment {

    AutoCompleteTextView focusTimeAutoCompleteTextView, shortBreakAutoCompleteTextView,
                            longBreakAutoCompleteTextView, sectionsAutoCompleteTextView;
    Button startTimerBtn;
    String focusTime, shortBreak, longBreak, sections;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_set_timer, container, false);

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        focusTimeAutoCompleteTextView = view.findViewById(R.id.focusTimeAutoCompleteTextView);
        shortBreakAutoCompleteTextView = view.findViewById(R.id.shortBreakAutoCompleteTextView);
        longBreakAutoCompleteTextView = view.findViewById(R.id.longBreakAutoCompleteTextView);
        sectionsAutoCompleteTextView = view.findViewById(R.id.sectionsAutoCompleteTextView);
        startTimerBtn = view.findViewById(R.id.startTimerBtn);

        // Focus time dropdown menu
        String[] focusTimeArray = getResources().getStringArray(R.array.focusTime);
        ArrayAdapter<String> focusTimeAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, focusTimeArray);
        focusTimeAutoCompleteTextView.setAdapter(focusTimeAdapter);

        // Short break dropdown menu
        String[] shortBreakArray = getResources().getStringArray(R.array.shortBreak);
        ArrayAdapter<String> shortBreakAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, shortBreakArray);
        shortBreakAutoCompleteTextView.setAdapter(shortBreakAdapter);

        // Long break dropdown menu
        String[] longBreakArray = getResources().getStringArray(R.array.longBreak);
        ArrayAdapter<String> longBreakAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, longBreakArray);
        longBreakAutoCompleteTextView.setAdapter(longBreakAdapter);

        // Sections dropdown menu
        String[] sectionsArray = getResources().getStringArray(R.array.Sections);
        ArrayAdapter<String> sectionsAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, sectionsArray);
        sectionsAutoCompleteTextView.setAdapter(sectionsAdapter);

        startTimerBtn.setOnClickListener(v -> {
            focusTime = focusTimeAutoCompleteTextView.getText().toString();
            if (focusTime.length() == 10) {
                focusTime = focusTime.substring(0, 2);
            } else {
                focusTime = focusTime.substring(0, 3);
            }

            shortBreak = shortBreakAutoCompleteTextView.getText().toString();
            if (shortBreak.length() == 9) {
                shortBreak = shortBreak.substring(0, 1);
            } else {
                shortBreak = shortBreak.substring(0, 2);
            }

            longBreak = longBreakAutoCompleteTextView.getText().toString().substring(0, 2);
            sections = sectionsAutoCompleteTextView.getText().toString().substring(0, 1);

            Bundle timer = new Bundle();
            timer.putLong("focusTime", Long.parseLong(focusTime));
            timer.putLong("shortBreak", Long.parseLong(shortBreak));
            timer.putLong("longBreak", Long.parseLong(longBreak));
            timer.putInt("sections", Integer.parseInt(sections));

            bottomNavigationView.setVisibility(View.GONE);

            getParentFragmentManager().setFragmentResult("timerSettings", timer);
            replaceFragment(new TimerFragment());
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment).addToBackStack(null);
        fragmentTransaction.commit();

    }
}