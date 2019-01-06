package com.manveerbasra.ontime.alarmmanager.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.manveerbasra.ontime.alarmmanager.AlarmHandler;
import com.manveerbasra.ontime.alarmmanager.AlarmSoundControl;

/**
 * Broadcast Receiver to snooze alarm
 */
public class AlarmSnoozeReceiver extends BroadcastReceiver {

    private final String TAG = "AlarmSnoozeReceiver";
    private SharedPreferences mPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int alarmID = intent.getIntExtra(AlarmHandler.EXTRA_ID, 0);

        Log.i(TAG, "Stopping ringing and dismissing alarm " + alarmID);
        AlarmSoundControl alarmSoundControl = AlarmSoundControl.getInstance();
        alarmSoundControl.stopAlarmSound();

        // Dismiss notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        // Get snooze length from shared preferences
        int snoozeInSecs = Integer.parseInt(mPreferences.getString("alarm_snooze_length_list", "300"));

        // Schedule next ring
        Log.i(TAG, "Snoozing alarm " + alarmID + " for " + snoozeInSecs + " seconds");
        AlarmHandler alarmHandler = new AlarmHandler(context, null);
        alarmHandler.scheduleAlarmWithTime(snoozeInSecs * 1000, alarmID);
    }
}
