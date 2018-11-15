package com.manveerbasra.ontime.alarmmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.manveerbasra.ontime.db.Alarm;
import com.manveerbasra.ontime.viewmodel.AlarmViewModel;

import java.time.Duration;
import java.util.Calendar;

/**
 * Class to control alarm scheduling/cancelling
 */
public class AlarmHandler {

    private final String TAG = "AlarmHandler";

    private AlarmViewModel alarmViewModel;
    private Context appContext;
    private View snackbarAnchor;

    public AlarmHandler(Context context, View snackbarAnchor) {
        this.appContext = context;
        this.snackbarAnchor = snackbarAnchor;
    }

    public void scheduleAlarm(Alarm alarm) {
        if (!alarm.isActive()) {
            return;
        }

        AlarmManager alarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);

//        long alarmTimeInMillis = alarm.getTimeInMillis() - System.currentTimeMillis();

//        Calendar calendar = Calendar.getInstance();
//        long timeNow = calendar.getTimeinMillis();
//        // add alarmTriggerTime seconds to the calendar object
//        calendar.add(Calendar.MILLISECOND, (int) alarm.getTimeInMillis());

        long alarmTimeInMillis = alarm.getTimeToRing();

        Intent intent = new Intent(appContext, AlarmReceiver.class);
        // intent.putExtra() Use to pass alarm data to receiver.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(appContext, alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i(TAG, "setting alarm " + alarm.getId() + " to AlarmManager for " + alarmTimeInMillis + " milliseconds");
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);

    }
}
