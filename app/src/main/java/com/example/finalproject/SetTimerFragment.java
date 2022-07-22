package com.example.finalproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalproject.databinding.FragmentSetTimerBinding;

public class SetTimerFragment extends Fragment {

    private FragmentSetTimerBinding setTimerBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_timer, container, false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setTimerBinding = null;
    }
}