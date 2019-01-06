package com.manveerbasra.ontime.alarmmanager.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.manveerbasra.ontime.R;
import com.manveerbasra.ontime.alarmmanager.AlarmHandler;
import com.manveerbasra.ontime.db.Alarm;
import com.manveerbasra.ontime.timehandlers.TimeShiftHandler;
import com.manveerbasra.ontime.util.AlarmRepository;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * BroadcastReceiver to process time shift required to alarm time
 * and set respective alarm.
 */
public class TimeShiftReceiver extends BroadcastReceiver {

    private final String TAG = "TimeShiftReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received alarm time shift intent");

        int alarmID = intent.getIntExtra(AlarmHandler.EXTRA_ID, 0);

        // Get alarm from repository
        AlarmRepository repository = new AlarmRepository((Application) context.getApplicationContext());
        Alarm alarm;
        try {
            alarm = repository.getAlarmById(alarmID);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error when retrieving alarm by id: " + alarmID);
            e.printStackTrace();
            alarm = null;
        }

        AlarmHandler alarmHandler = new AlarmHandler(context, null);

        if (alarm != null) {
            String mapsApiKey = context.getString(R.string.google_maps_ip_key);
            String weatherApiKey = context.getString(R.string.weather_map_key);
            TimeShiftHandler timeShiftHandler = new TimeShiftHandler(mapsApiKey, weatherApiKey);

            long timeToNextRing = alarm.getTimeToNextRing();
            int departureTimeInSecs = (int) (timeToNextRing / 1000) + 30 * 60;
            long timeShiftInMillis = timeShiftHandler.getTimeShiftInMillis(alarm.startPoint, alarm.endPoint, departureTimeInSecs);
            Log.i(TAG, "setting alarm's time shift to " + timeShiftInMillis);

            // Set alarm to 1 hour from now minus time shift
            long alarmTime = TimeUnit.HOURS.toMillis(1) - timeShiftInMillis;
            alarmHandler.scheduleAlarmWithTime((int) alarmTime, alarmID);

        } else { // couldn't find alarm in repository, schedule alarm for 1 hour from now
            alarmHandler.scheduleAlarmWithTime((int) TimeUnit.HOURS.toMillis(1), alarmID);
        }
    }
}
