package com.manveerbasra.ontime.db.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.manveerbasra.ontime.AlarmDataManager;
import com.manveerbasra.ontime.db.Alarm;
import com.manveerbasra.ontime.db.AlarmDatabase;

public class DatabaseInitializer {


    public static void populateAsync(final AlarmDatabase db) {

        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    public static void populateSync(@NonNull final AlarmDatabase db) {
        populateWithTestData(db);
    }

    private static Alarm addAlarm(final AlarmDatabase db, final AlarmDataManager alarmDM) {
        Alarm alarm = new Alarm();
        alarm.hour = alarmDM.getHour();
        alarm.minute = alarmDM.getMinute();
        alarm.active = alarmDM.isActive();
        alarm.repeat = alarmDM.isRepeat();
        alarm.meridian = alarmDM.getMeridian();
        alarm.activeDays = alarmDM.getActiveDays();
        db.alarmModel().insertAlarm(alarm);
        return alarm;
    }

    private static void populateWithTestData(AlarmDatabase db) {
        db.alarmModel().deleteAll();
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AlarmDatabase mDb;

        PopulateDbAsync(AlarmDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(mDb);
            return null;
        }

    }
}
