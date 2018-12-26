package com.manveerbasra.ontime.alarmmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.manveerbasra.ontime.R;
import com.manveerbasra.ontime.alarmmanager.receiver.AlarmReceiver;
import com.manveerbasra.ontime.db.Alarm;

import java.util.Calendar;


/**
 * Class to control alarm scheduling/cancelling
 */
public class AlarmHandler {

    public static final String EXTRA_ID = "extra_id";

    private final String TAG = "AlarmHandler";

    private Context appContext;
    private View snackbarAnchor;

    public AlarmHandler(Context context, View snackbarAnchor) {
        this.appContext = context;
        this.snackbarAnchor = snackbarAnchor;
    }

    /**
     * Schedule alarm notification using AlarmManager
     *
     * @param alarm Alarm to schedule
     */
    public void scheduleAlarm(Alarm alarm) {
        if (!alarm.isActive()) {
            return;
        }

        long alarmTimeInMillis = alarm.getTimeToRing();

        // Get PendingIntent to AlarmReceiver Broadcast channel
        Intent intent = new Intent(appContext, AlarmReceiver.class);
        intent.putExtra(EXTRA_ID, alarm.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i(TAG, "setting alarm " + alarm.getId() + " to AlarmManager for " + alarmTimeInMillis + " milliseconds");
        AlarmManager alarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);

        // Show snackbar to notify user
        Snackbar.make(snackbarAnchor, appContext.getString(R.string.alarm_set), Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Schedule alarm notification based on time until next alarm
     *
     * @param timeToRing time to next alarm in milliseconds
     * @param alarmID    ID of alarm to ring
     */
    public void scheduleAlarm(int timeToRing, int alarmID) {
        // Calculate time until alarm from millis since epoch
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, timeToRing);
        long alarmTimeInMillis = calendar.getTimeInMillis();

        // Get PendingIntent to AlarmReceiver Broadcast channel
        Intent intent = new Intent(appContext, AlarmReceiver.class);
        intent.putExtra(EXTRA_ID, alarmID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i(TAG, "setting alarm " + alarmID + " to AlarmManager for " + alarmTimeInMillis + " milliseconds");
        AlarmManager alarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
    }

    /**
     * Cancel alarm notification using AlarmManager
     *
     * @param alarm Alarm to cancel
     */
    public void cancelAlarm(Alarm alarm) {
        if (alarm.isActive()) {
            return;
        }

        // Get PendingIntent to AlarmReceiver Broadcast channel
        Intent intent = new Intent(appContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, alarm.getId(), intent, PendingIntent.FLAG_NO_CREATE);

        // PendingIntent may be null if the alarm hasn't been set
        if (pendingIntent != null) {
            Log.i(TAG, "cancelling alarm " + alarm.getId());
            AlarmManager alarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }

        // Show snackbar to notify user
        Snackbar.make(snackbarAnchor, appContext.getString(R.string.alarm_cancelled), Snackbar.LENGTH_SHORT).show();
    }
}
