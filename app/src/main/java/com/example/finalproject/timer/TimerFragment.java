package com.example.finalproject.timer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class TimerFragment extends Fragment {

    private EditText editTextInput;
    private TextView textViewWorkBreak;
    private TextView textViewCountDown;
    private Button buttonSet;
    private FloatingActionButton buttonStartPause, buttonReset;

    private CountDownTimer countDownTimer;

    private boolean isTimerRunning;

    private long startTimeInMillis, timeLeftInMillis;
    private long endTime;

    private static int workCount = 0;
    private static int shortBreakCount = 0;
    private static int longBreakCount = 0;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        editTextInput = view.findViewById(R.id.edit_text_input);
        textViewWorkBreak = view.findViewById(R.id.work_break);

        textViewCountDown = view.findViewById(R.id.chronometer);

        buttonSet = view.findViewById(R.id.button_set);
        buttonStartPause = view.findViewById(R.id.fab_start);
        buttonReset = view.findViewById(R.id.fab_reset);

        buttonStartPause.setOnClickListener(view1 -> {
            if (isTimerRunning) {
                pauseTimer();
            } else {
                sessionStructure();
            }

        });

        buttonSet.setOnClickListener(view12 -> {

            String input = editTextInput.getText().toString();
            if (input.length() == 0) {
                Toast.makeText(requireContext(), "Field can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            long millisInput = Long.parseLong(input) * 60000;
            if (millisInput == 0) {
                Toast.makeText(requireContext(), "Please enter a positive number", Toast.LENGTH_SHORT).show();
                return;
            }

            setTime(millisInput);
            editTextInput.setText("");

        });

        buttonReset.setOnClickListener(view2 -> resetTimer());

        return view;
    }

    private void sessionStructure() {

        int workTotal, shortBreakTotal, longBreakTotal;

        if (startTimeInMillis <= 1800000) {
            workTotal = 1;
            shortBreakTotal = 0;
            longBreakTotal = 0;
        } else if (startTimeInMillis <= 3600000) {
            workTotal = 2;
            shortBreakTotal = 1;
            longBreakTotal = 0;
        } else if (startTimeInMillis <= 10800000) {
            workTotal = 4;
            shortBreakTotal = 4;
            longBreakTotal = 1;
        } else if (startTimeInMillis <= 18000000) {
            workTotal = 6;
            shortBreakTotal = 6;
            longBreakTotal = 2;
        } else {
            workTotal = 8;
            shortBreakTotal = 8;
            longBreakTotal = 3;
        }

        AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).create();
        alertDialog.setTitle("Session Structure");
        if (shortBreakTotal == 0) {
            alertDialog.setMessage(workTotal + " Work Session of " + (startTimeInMillis/60000) / workTotal + " Minutes \n" + shortBreakTotal + " Short Breaks " + "\n" + longBreakTotal + " long Break");
        } else if (longBreakTotal == 0){
            alertDialog.setMessage(workTotal + " Work Session of " + (startTimeInMillis/60000) / workTotal + " Minutes \n" + shortBreakTotal + " Short Breaks of " + (startTimeInMillis/60000)/ workTotal / (shortBreakTotal + 1) + " Minutes\n" + longBreakTotal + " long Break");
        } else {
            alertDialog.setMessage(workTotal + " Work Session of " + (startTimeInMillis/60000) / workTotal + " Minutes \n" + shortBreakTotal + " Short Breaks of " + (startTimeInMillis/60000)/ workTotal / shortBreakTotal + " Minutes\n" + longBreakTotal + " long Break of " + (startTimeInMillis/60000) / workTotal / (longBreakTotal + 1) + " Minutes\n");
        }
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Start Session", (dialogInterface, i) -> {
            startTimer();
            isTimerRunning = true;
            updateButtons();
            textViewWorkBreak.setVisibility(View.VISIBLE);
            textViewWorkBreak.setText("Work Time Stay Focused");
        });
        alertDialog.show();

    }

    private void setTime(long milliseconds) {
        startTimeInMillis = milliseconds;
        resetTimer();
        closeKeyboard();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void startTimer() {
        endTime = System.currentTimeMillis() + timeLeftInMillis;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;

                AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).create();
                alertDialog.setMessage("Start Short Break?");
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Yes", (dialogInterface, i) -> {
                    startShortBreak();
                    isTimerRunning = true;
                    updateButtons();
                    textViewWorkBreak.setVisibility(View.VISIBLE);
                    textViewWorkBreak.setText("Work Time");
                });
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "No", (dialogInterface, i) -> {
                    isTimerRunning = true;
                });
                alertDialog.show();
            }
        }.start();

        isTimerRunning = true;
        updateButtons();

    }

    private void startShortBreak() {
        updateButtons();
        endTime = System.currentTimeMillis() + timeLeftInMillis;

        countDownTimer = new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                updateButtons();

            }
        }.start();

        isTimerRunning = true;
        updateButtons();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void pauseTimer() {
        countDownTimer.cancel();
        isTimerRunning = false;
        updateButtons();
    }

    private void resetTimer() {
        timeLeftInMillis = startTimeInMillis;
        updateCountDownText();
        updateButtons();
    }
    private void updateCountDownText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }

        textViewCountDown.setText(timeLeftFormatted);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateButtons() {
        if (isTimerRunning) {
            editTextInput.setVisibility(View.INVISIBLE);
            buttonSet.setVisibility(View.INVISIBLE);
            buttonReset.setVisibility(View.INVISIBLE);
            buttonStartPause.setVisibility(View.VISIBLE);
            buttonStartPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24, requireContext().getTheme()));
        } else {
            editTextInput.setVisibility(View.VISIBLE);
            buttonSet.setVisibility(View.VISIBLE);
            buttonStartPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, requireContext().getTheme()));

            if (timeLeftInMillis < 1000) {
                buttonStartPause.setVisibility(View.INVISIBLE);
            } else {
                buttonStartPause.setVisibility(View.VISIBLE);
            }

            if (timeLeftInMillis < startTimeInMillis) {
                buttonReset.setVisibility(View.VISIBLE);
            } else {
                buttonReset.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void closeKeyboard() {
        Context view = this.getContext();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("startTimeInMillis", startTimeInMillis);
        editor.putLong("millisLeft", timeLeftInMillis);
        editor.putBoolean("timerRunning", isTimerRunning);
        editor.putLong("endTime", endTime);

        editor.apply();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        //startTimeInMillis = prefs.getLong("startTimeInMillis", 1500000);
        startTimeInMillis = prefs.getLong("startTimeInMillis", 1500000);
        timeLeftInMillis = prefs.getLong("millisLeft", startTimeInMillis);
        isTimerRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();
        updateButtons();

        if (isTimerRunning) {
            endTime = prefs.getLong("endTime", 0);
            timeLeftInMillis = endTime - System.currentTimeMillis();

            if (timeLeftInMillis < 0) {
                timeLeftInMillis = 0;
                isTimerRunning = false;
                updateCountDownText();
                updateButtons();
            } else {
                startTimer();
            }
        }

    }
}