package com.example.finalproject;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TimerFragment extends Fragment {

    private int progr = 100, shortBreakCount, longBreakCount, sectionsCount = 0;
    private Chronometer chronometer, breakChronometer;
    private LinearLayout breakLayout;
    private long time, breakTime, shortBreak, longBreak, sections, countDownInterval;
    private boolean isPlaying = false, fabStartClicked = false;
    private FloatingActionButton fabStart, fabStop;
    private ProgressBar timerProgressBar;
    private CountDownTimer countDownTimer;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);


        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { }
        });

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        breakLayout = view.findViewById(R.id.break_time);
        timerProgressBar = view.findViewById(R.id.timer_progress_bar);
        breakChronometer = view.findViewById(R.id.break_chronometer);
        chronometer = view.findViewById(R.id.chronometer);
        fabStart = view.findViewById(R.id.fab_start);
        fabStop = view.findViewById(R.id.fab_stop);
        chronometer.setCountDown(true);
        breakChronometer.setCountDown(true);

        getParentFragmentManager().setFragmentResultListener("timerSettings", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                shortBreak = result.getLong("shortBreak");
                longBreak = result.getLong("longBreak");
                sections = result.getInt("sections");
                time = result.getLong("focusTime");
                time *= 60000;
                shortBreak *= 60000;
                longBreak *= 60000;
                countDownInterval = time / 100;
                updateProgressBar();
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

                    if (isPlaying) {
                        countDownTimer = new CountDownTimer(time, countDownInterval) {

                            @Override
                            public void onTick(long millisecondsUntilFinished) {
                                if (millisecondsUntilFinished <= time - countDownInterval + 3000 && progr >= 1) {
                                    progr -= 1;
                                    updateProgressBar();
                                }
                            }

                            @Override
                            public void onFinish() {
                                Toast.makeText(getContext(), "Timer Finished Good Work", Toast.LENGTH_LONG).show();
                                chronometer.stop();
                            }
                        }.start();
                    } else {
                        countDownTimer.cancel();
                    }
                });
            }
        });

        fabStop.setOnClickListener(v -> {
            replaceFragment(new SetTimerFragment());
            bottomNavigationView.setVisibility(View.VISIBLE);
        });

        return view;
    }

    private void updateProgressBar() {
        timerProgressBar.setProgress(progr);
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }

}