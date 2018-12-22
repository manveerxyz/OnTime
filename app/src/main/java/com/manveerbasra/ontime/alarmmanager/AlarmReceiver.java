package com.manveerbasra.ontime.alarmmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.manveerbasra.ontime.R;

import java.io.IOException;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class AlarmReceiver extends BroadcastReceiver {

    private final String TAG = "AlarmReceiver";
    private final String CHANNEL_ID = "AlarmReceiverChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received alarm intent");


        Intent stopAlarmIntent = new Intent(context, AlarmStopReceiver.class);
        stopAlarmIntent.setAction("Stop Alarm");
        PendingIntent stopAlarmPendingIntent =
                PendingIntent.getBroadcast(context, 0, stopAlarmIntent, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create and add notification channel
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, TAG , NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(mChannel);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("OnTime")
                .setContentText("Alarm going off!")
                .addAction(R.drawable.ic_launcher_background, "Stop",
                        stopAlarmPendingIntent)
                .build();

        Log.i(TAG, "displaying notification");
        AlarmSoundControl alarmSoundControl = AlarmSoundControl.getInstance();
        alarmSoundControl.playAlarmSound(context);
        notificationManager.notify(1, notification);

        // TODO: Set Activity that opens when Alarm goes off
        //
        // Notification notification = new NotificationCompat.Builder(context, "M_CH_ID")
        //        .setFullScreenIntent(PendingIntent intent, boolean highPriority=true)
        //        .build();

    }
}
