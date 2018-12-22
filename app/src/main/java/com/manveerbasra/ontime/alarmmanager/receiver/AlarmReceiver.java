package com.manveerbasra.ontime.alarmmanager.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.manveerbasra.ontime.R;
import com.manveerbasra.ontime.alarmmanager.AlarmHandler;
import com.manveerbasra.ontime.alarmmanager.AlarmSoundControl;

public class AlarmReceiver extends BroadcastReceiver {

    private final String TAG = "AlarmReceiver";
    private final String CHANNEL_ID = "AlarmReceiverChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received alarm intent");

        int alarmID = intent.getIntExtra(AlarmHandler.EXTRA_ID, 0);

        // Create Stop Receiver intent to stop alarm ringing
        Intent stopAlarmIntent = new Intent(context, AlarmStopReceiver.class);
        stopAlarmIntent.putExtra(AlarmHandler.EXTRA_ID, alarmID);
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
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("Alarm going off!")
                .addAction(R.drawable.ic_launcher_background, "Stop",
                        stopAlarmPendingIntent)
                .build();

        // Play alarm ringing sound
        AlarmSoundControl alarmSoundControl = AlarmSoundControl.getInstance();
        alarmSoundControl.playAlarmSound(context.getApplicationContext());

        Log.i(TAG, "displaying notification for alarm " + alarmID);
        notificationManager.notify(alarmID, notification);
    }
}
