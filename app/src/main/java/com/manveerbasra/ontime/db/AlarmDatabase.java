package com.manveerbasra.ontime.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.android.gms.maps.model.LatLng;
import com.manveerbasra.ontime.db.converter.BooleanArrayConverter;
import com.manveerbasra.ontime.db.converter.DateConverter;
import com.manveerbasra.ontime.db.converter.LatLngConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Backend Database
 */
@Database(entities = {Alarm.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class, BooleanArrayConverter.class, LatLngConverter.class})
public abstract class AlarmDatabase extends RoomDatabase {

    private static AlarmDatabase INSTANCE;

    @VisibleForTesting
    public static final String DATABASE_NAME = "alarm-db";

    public abstract AlarmDao alarmModel();

    public static AlarmDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AlarmDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AlarmDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration() // TODO Add Proper Migration
                            .addCallback(roomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Override the onCreate method to populate the database.
     */
    private static RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsync(INSTANCE).execute();
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };

    /**
     * Populate the database in the background when app is first created.
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AlarmDao alarmModel;

        PopulateDbAsync(AlarmDatabase db) {
            alarmModel = db.alarmModel();
        }

        @Override
        protected Void doInBackground(final Void... params) {
//            String str = "08:00 am";
//            DateFormat formatter = new SimpleDateFormat("hh:mm aa");
//            Date time = null;
//            try {
//                time = formatter.parse(str);
//            } catch (java.text.ParseException e) {
//                e.printStackTrace();
//            }
//            boolean[] activeDays = {true, true, false, false, false, false, false};
//
//            Alarm alarm = new Alarm(time, false, activeDays);
//            alarmModel.insert(alarm);
            return null;
        }
    }
}
