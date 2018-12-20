package com.manveerbasra.ontime.alarmmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.manveerbasra.ontime.R;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

public class AlarmReceiver extends BroadcastReceiver {

    private final String TAG = "AlarmReceiver";
    private final String CHANNEL_ID = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received alarm intent");

        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmNotificationService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));

// Configure the notification channel.
//        mChannel.setDescription(description);

//        mChannel.enableLights(true);
//// Sets the notification light color for notifications posted to this
//// channel, if the device supports this feature.
//        mChannel.setLightColor(Color.RED);
//
//        mChannel.enableVibration(true);
//        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create and add notification channel
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, TAG , NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(mChannel);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Alarm")
                .setContentText("Upcoming Alarm.")
                .build();

        Log.i(TAG, "displaying notification");
        notificationManager.notify(1, notification);

        // TODO: Set Activity that opens when Alarm goes off
        //
        // Notification notification = new NotificationCompat.Builder(context, "M_CH_ID")
        //        .setFullScreenIntent(PendingIntent intent, boolean highPriority=true)
        //        .build();

    }
}
