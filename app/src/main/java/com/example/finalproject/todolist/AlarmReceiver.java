package com.example.finalproject.todolist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.finalproject.R;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskTitle ="Schedule Notice";
        String taskDescription = intent.getStringExtra("content");
        Toast.makeText(context, "Alarm Raised", Toast.LENGTH_SHORT).show();
        Log.d("abc", "Alarm Raised");
        createNotificationChannel(context,"1");
        showNotification(context,taskTitle, taskDescription);
    }

    private void showNotification(Context context, String taskTitle, String taskDescription) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setTicker(taskTitle)
                .setContentText(taskDescription)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

                .setAutoCancel(true)
                .setOnlyAlertOnce(true);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.notify(1, builder.build());
    }



    private void createNotificationChannel(Context context,String CHANNEL_ID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel", importance);
            channel.setDescription("description");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
