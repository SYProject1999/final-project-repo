package com.example.finalproject.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.util.Calendar;

public class MyAlarmManager {

    public static void setAlarm(Context context, int id, String content, Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Put Reminder todoTask in Intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);

        intent.putExtra("content", content);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);



        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.SECOND,0);
        int minute = calendar.get(Calendar.MINUTE) - 1;
        calendar.set(Calendar.MINUTE, minute);


        long currentTime = currentCalendar.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;

        // Start alarm using notification time
        alarmManager.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                mPendingIntent);

    }
}
