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
import java.util.List;


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

        // Get PendingIntent to AlarmReceiver Broadcast channel
        Intent intent = new Intent(appContext, AlarmReceiver.class);
        intent.putExtra(EXTRA_ID, alarm.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);

        long nextAlarmRing = 0; // used in Snackbar

        if (alarm.isRepeating()) {
            // get list of time to ring in milliseconds for each active day, and repeat weekly
            List<Long> timeToWeeklyRings = alarm.getTimeToWeeklyRings();
            Calendar calendar = Calendar.getInstance();
            for (long millis : timeToWeeklyRings) {
                calendar.setTimeInMillis(millis);
                Log.i(TAG, "Setting weekly repeat at " + calendar.getTime().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, millis, AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                if (millis < nextAlarmRing || nextAlarmRing == 0) nextAlarmRing = millis;
            }
        } else {
            nextAlarmRing = alarm.getTimeToNextRing(); // get time until next alarm ring

            Log.i(TAG, "setting alarm " + alarm.getId() + " to AlarmManager for " + nextAlarmRing + " milliseconds");
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextAlarmRing, pendingIntent);
        }

        String timeUntilNextRing = getTimeUntilNextRing(nextAlarmRing - System.currentTimeMillis());
        // Show snackbar to notify user
        Snackbar.make(snackbarAnchor,
                String.format(appContext.getString(R.string.alarm_set), timeUntilNextRing),
                Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Converts milliseconds to # day(s), # hour(s), # minute(s)
     *
     * @param millisToRing milliseconds to next alarm ring
     * @return a user readable String of time until next alarm ring
     */
    private String getTimeUntilNextRing(long millisToRing) {
        long seconds = millisToRing / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        minutes -= hours*60;
        hours -= days*24;

        StringBuilder timeUntilNextRing = new StringBuilder();
        if (days >= 1) {
            timeUntilNextRing.append(days);
            timeUntilNextRing.append(days > 1 ? " days, " : " day, ");
        }
        if (hours >= 1) {
            timeUntilNextRing.append(hours);
            timeUntilNextRing.append(hours > 1 ? " hours, " : " hour, ");
        }
        timeUntilNextRing.append(minutes);
        timeUntilNextRing.append(minutes > 1 ? " minutes" : " minute");

        return timeUntilNextRing.toString();
    }

    /**
     * Schedule alarm notification based on time until next alarm, used to snooze alarm
     *
     * @param timeToRing time to next alarm in milliseconds
     * @param alarmID    ID of alarm to ring
     */
    public void scheduleSnoozeAlarm(int timeToRing, int alarmID) {
        // Calculate time until alarm from millis since epoch
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, timeToRing);
        long alarmTimeInMillis = calendar.getTimeInMillis();

        // Get PendingIntent to AlarmReceiver Broadcast channel
        Intent intent = new Intent(appContext, AlarmReceiver.class);
        intent.putExtra(EXTRA_ID, alarmID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i(TAG, "setting snoozed alarm " + alarmID + " to AlarmManager for " + alarmTimeInMillis + " milliseconds");
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
