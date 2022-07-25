package com.example.finalproject;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class TimerFragment extends Fragment {

    private Chronometer chronometer;
    long time;
    private boolean isPlaying = false, fabStartClicked = false;
    FloatingActionButton fabStart, fabStop;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        chronometer = view.findViewById(R.id.chronometer);
        fabStart = view.findViewById(R.id.fab_start);
        fabStop = view.findViewById(R.id.fab_stop);
        chronometer.setCountDown(true);

        getParentFragmentManager().setFragmentResultListener("timerSettings", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                time = result.getLong("focusTime");
                time *= 60000;
                chronometer.setBase(SystemClock.elapsedRealtime() + time);
                fabStart.setOnClickListener(v -> {
                    if (!fabStartClicked) {
                        fabStartClicked = true;
                        chronometer.setBase(SystemClock.elapsedRealtime() + time);
                        chronometer.start();
                        isPlaying = true;
                        fabStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24, getContext().getTheme()));
                        fabStop.setVisibility(View.INVISIBLE);
                        fabStop.setClickable(false);
                    } else {
                        fabStartClicked = false;
                        chronometer.stop();
                        time = chronometer.getBase() - SystemClock.elapsedRealtime();
                        isPlaying = false;
                        fabStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, getContext().getTheme()));
                        fabStop.setVisibility(View.VISIBLE);
                        fabStop.setClickable(true);
                    }
                });

            }
        });

        fabStop.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        return view;
    }
}