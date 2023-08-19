package com.example.finalproject.timer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.finalproject.LoginActivity;
import com.example.finalproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class TimerFragment extends Fragment {


    private static final String CHANNEL_ID = "Timer";
    private static final int BREAK_PERCENTAGE = 30;
    private static final long BREAK_TIME_FACTOR = 100 / BREAK_PERCENTAGE;
    private Spinner minuteSpinner;
    private TextView textViewWorkBreak, textViewMinuteSpinner;
    private TextView textViewCountDown;
    private Button buttonSet;
    private FloatingActionButton buttonStartPause, buttonReset;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning, isWorkMode = true;
    private long startTimeInMillis, timeLeftInMillis, totalWorkTime, endTime;
    private Uri notificationSoundUri;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        minuteSpinner = view.findViewById(R.id.minute_spinner);
        textViewMinuteSpinner = view.findViewById(R.id.minute_text_spinner);
        textViewWorkBreak = view.findViewById(R.id.work_break);

        textViewCountDown = view.findViewById(R.id.chronometer);

        buttonSet = view.findViewById(R.id.button_set);
        buttonStartPause = view.findViewById(R.id.fab_start);
        buttonReset = view.findViewById(R.id.fab_reset);

        createNotificationChannel();
        notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Button buttonDND = view.findViewById(R.id.button_dnd);
        buttonDND.setOnClickListener(view1 -> toggleDoNotDisturbMode());

        buttonSet.setOnClickListener(view1 -> setWorkTime());
        buttonReset.setOnClickListener(view2 -> resetTimer());

        buttonStartPause.setOnClickListener(view1 -> {
            if (isTimerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        setupMinuteSpinner();

        return view;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Channel Name",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel Description");

            NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void setWorkTime() {

        String selectedMinuteString = minuteSpinner.getSelectedItem().toString();
        int selectedMinute = Integer.parseInt(selectedMinuteString);

        timeLeftInMillis = (long) (selectedMinute) * 60 * 200;
        startTimeInMillis = (long) (selectedMinute) * 60 * 200;

        updateCountDownText();
    }

    private void setupMinuteSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.minutes, R.layout.my_selected_item);
        adapter.setDropDownViewResource(R.layout.my_dropdown_item);
        minuteSpinner.setAdapter(adapter);
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

                if (isWorkMode) {
                    showBreakDialog();
                } else {
                    showNotification();
                    showContinueDialog();
                }

            }
        }.start();

        isTimerRunning = true;
        updateButtons();

    }

    private void showNotification() {

        Intent intent = new Intent(getContext(), LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable. ic_launcher_foreground )
                .setContentTitle("Break is Over")
                .setContentText("Get Back To Work And Stay Focused!")
                .setContentIntent(pendingIntent)
                .setSound(notificationSoundUri)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.notify(1, builder.build());
    }

    private void showBreakDialog() {
        totalWorkTime += startTimeInMillis;
        timeLeftInMillis = totalWorkTime / BREAK_TIME_FACTOR;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Take a Break")
                .setMessage("Do you want to take a break?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    totalWorkTime = 0;
                    isWorkMode = false;
                    startTimer();
                })
                .setNegativeButton("No", (dialog, which) -> showContinueDialog())
                .setCancelable(false)
                .show();
    }

    private void showContinueDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Continue?")
                .setMessage("Do you want to start the next work session?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    isWorkMode = true;
                    timeLeftInMillis = startTimeInMillis;
                    startTimer();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    isWorkMode = true;
                    timeLeftInMillis = startTimeInMillis;
                    updateButtons();
                    updateCountDownText();
                })
                .setCancelable(false)
                .show();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void pauseTimer() {
        countDownTimer.cancel();
        isTimerRunning = false;
        updateButtons();
    }

    private void resetTimer() {
        timeLeftInMillis = startTimeInMillis;
        isWorkMode = true;

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

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void updateButtons() {
        if (isTimerRunning) {
            textViewMinuteSpinner.setVisibility(View.INVISIBLE);
            textViewWorkBreak.setText("Stay Focused");
            minuteSpinner.setVisibility(View.INVISIBLE);
            buttonSet.setVisibility(View.INVISIBLE);
            buttonReset.setVisibility(View.INVISIBLE);
            buttonStartPause.setVisibility(View.VISIBLE);
            buttonStartPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24, requireContext().getTheme()));
        } else {
            textViewMinuteSpinner.setVisibility(View.VISIBLE);
            minuteSpinner.setVisibility(View.VISIBLE);
            buttonSet.setVisibility(View.VISIBLE);
            buttonReset.setVisibility(View.VISIBLE);
            buttonStartPause.setVisibility(View.VISIBLE);
            buttonStartPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, requireContext().getTheme()));

            if (timeLeftInMillis < startTimeInMillis) {
                buttonReset.setVisibility(View.VISIBLE);
            } else {
                buttonReset.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void toggleDoNotDisturbMode() {
        NotificationManager notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            Toast.makeText(requireContext(), "Device does not support Do Not Disturb mode.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager.isNotificationPolicyAccessGranted()) {
                int currentFilter = notificationManager.getCurrentInterruptionFilter();
                if (currentFilter == NotificationManager.INTERRUPTION_FILTER_NONE) {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                    Toast.makeText(requireContext(), "Do Not Disturb mode was enabled", Toast.LENGTH_SHORT).show();
                } else {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                    Toast.makeText(requireContext(), "Do Not Disturb mode was disabled", Toast.LENGTH_SHORT).show();
                }

                int updatedFilter = notificationManager.getCurrentInterruptionFilter();
                if (updatedFilter == NotificationManager.INTERRUPTION_FILTER_NONE) {
                    Toast.makeText(requireContext(), "Do Not Disturb mode is active", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Do Not Disturb mode is not active", Toast.LENGTH_SHORT).show();
                }
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        } else {
            Toast.makeText(requireContext(), "Device does not support Do Not Disturb mode.", Toast.LENGTH_SHORT).show();
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

        startTimeInMillis = prefs.getLong("startTimeInMillis", startTimeInMillis);
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