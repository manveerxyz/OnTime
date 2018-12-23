package com.manveerbasra.ontime.alarmmanager.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.manveerbasra.ontime.alarmmanager.AlarmHandler;
import com.manveerbasra.ontime.alarmmanager.AlarmSoundControl;

/**
 * Broadcast Receiver to snooze alarm
 */
public class AlarmSnoozeReceiver extends BroadcastReceiver {

    private final String TAG = "AlarmSnoozeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmID = intent.getIntExtra(AlarmHandler.EXTRA_ID, 0);

        Log.i(TAG, "Stopping ringing and cancelling alarm " + alarmID);
        AlarmSoundControl alarmSoundControl = AlarmSoundControl.getInstance();
        alarmSoundControl.stopAlarmSound();

        // Dismiss notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        // Schedule next ring
        AlarmHandler alarmHandler = new AlarmHandler(context, null);
        alarmHandler.scheduleAlarm(30 * 1000, alarmID);
    }
}
