package com.manveerbasra.ontime.db.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.manveerbasra.ontime.AlarmDataManager;
import com.manveerbasra.ontime.db.Alarm;
import com.manveerbasra.ontime.db.AlarmDatabase;

import java.util.Calendar;
import java.util.Date;

public class DatabaseInitializer {

    // Simulate a blocking operation delaying each Loan insertion with a delay:
    private static final int DELAY_MILLIS = 500;

    public static void populateAsync(final AlarmDatabase db) {

        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    public static void populateSync(@NonNull final AlarmDatabase db) {
        populateWithTestData(db);
    }

    private static Alarm addAlarm(final AlarmDatabase db, @NonNull final String id, final AlarmDataManager alarmDM) {
        Alarm alarm = new Alarm();
        alarm.id = id;
        alarm.hour = alarmDM.getHour();
        alarm.minute = alarmDM.getMinute();
        alarm.active = alarmDM.isActive();
        alarm.repeat = alarmDM.isRepeat();
        alarm.meridian = alarmDM.getMeridian();
        alarm.activeDays = alarmDM.getStringOfActiveDays();
        db.alarmModel().insertAlarm(alarm);
        return alarm;
    }

    private static void populateWithTestData(AlarmDatabase db) {
        db.alarmModel().deleteAll();

        AlarmDataManager alarm1 = new AlarmDataManager(8, 48, "AM");
        AlarmDataManager alarm2 = new AlarmDataManager(10, 45, "PM", false,
                new String[] {"Saturday", "Sunday"});
        addAlarm(db, "1", alarm1);
        addAlarm(db, "2", alarm2);

//        try {
//            // Loans are added with a delay, to have time for the UI to react to changes.
//
//            Date today = getTodayPlusDays(0);
//            Date yesterday = getTodayPlusDays(-1);
//            Date twoDaysAgo = getTodayPlusDays(-2);
//            Date lastWeek = getTodayPlusDays(-7);
//            Date twoWeeksAgo = getTodayPlusDays(-14);
//
//            addLoan(db, "1", alarm1, book1, twoWeeksAgo, lastWeek);
//            Thread.sleep(DELAY_MILLIS);
//            addLoan(db, "2", alarm2, book1, lastWeek, yesterday);
//            Thread.sleep(DELAY_MILLIS);
//            addLoan(db, "3", alarm2, book2, lastWeek, today);
//            Thread.sleep(DELAY_MILLIS);
//            addLoan(db, "4", alarm2, book3, lastWeek, twoDaysAgo);
//            Thread.sleep(DELAY_MILLIS);
//            addLoan(db, "5", alarm2, book4, lastWeek, today);
//            Log.d("DB", "Added loans");
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
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
