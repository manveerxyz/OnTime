package com.manveerbasra.ontime.util;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.manveerbasra.ontime.db.Alarm;
import com.manveerbasra.ontime.db.AlarmDao;
import com.manveerbasra.ontime.db.AlarmDatabase;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Abstracted Repository to handle interactions between ViewModels and Database
 */
public class AlarmRepository {

    private AlarmDao mAlarmModel;
    private LiveData<List<Alarm>> mAllAlarms;

    public AlarmRepository(Application application) {
        // Application is used instead of Context in order to prevent memory leaks
        // between Activity switches
        AlarmDatabase db = AlarmDatabase.getInstance(application);
        mAlarmModel = db.alarmModel();
        mAllAlarms = mAlarmModel.getAllAlarms();
    }

    // Observed LiveData will notify the observer when data has changed
    public LiveData<List<Alarm>> getAllAlarms() {
        return mAllAlarms;
    }

    public void insert(Alarm alarm) {
        new insertAsyncTask(mAlarmModel).execute(alarm);
    }

    public void replace(Alarm alarm) {
        new replaceAsyncTask(mAlarmModel).execute(alarm);
    }

    public void update(Alarm alarm) {
        new updateAsyncTask(mAlarmModel).execute(alarm);
    }

    public void updateActive(Alarm alarm) {
        new updateActiveAsyncTask(mAlarmModel).execute(alarm);
    }

    public void delete(Alarm alarm) {
        new deleteAsyncTask(mAlarmModel).execute(alarm);
    }

    public Alarm getAlarmById(int id) throws ExecutionException, InterruptedException {
        return new getByIdAsyncTask(mAlarmModel).execute(id).get();
    }


    /**
     * Asynchronous Tasks
     *
     * One for each interaction with database.
     * All are static to prevent memory leaks
     */

    private static class insertAsyncTask extends AsyncTask<Alarm, Void, Void> {

        private AlarmDao alarmModel;

        insertAsyncTask(AlarmDao alarmModel) {
            this.alarmModel = alarmModel;
        }

        @Override
        protected Void doInBackground(final Alarm... params) {
            alarmModel.insert(params[0]);
            return null;
        }
    }

    private static class replaceAsyncTask extends AsyncTask<Alarm, Void, Void> {

        private AlarmDao alarmModel;

        replaceAsyncTask(AlarmDao alarmModel) {
            this.alarmModel = alarmModel;
        }

        @Override
        protected Void doInBackground(final Alarm... params) {
            alarmModel.insertOrReplaceAlarm(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Alarm, Void, Void> {

        private AlarmDao alarmModel;

        updateAsyncTask(AlarmDao alarmModel) {
            this.alarmModel = alarmModel;
        }

        @Override
        protected Void doInBackground(final Alarm... params) {
            alarmModel.update(params[0]);
            return null;
        }
    }

    private static class updateActiveAsyncTask extends AsyncTask<Alarm, Void, Void> {

        private AlarmDao alarmModel;

        updateActiveAsyncTask(AlarmDao alarmModel) {
            this.alarmModel = alarmModel;
        }

        @Override
        protected Void doInBackground(final Alarm... params) {
            alarmModel.updateActive(params[0].getId(), params[0].isActive());
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Alarm, Void, Void> {

        private AlarmDao alarmModel;

        deleteAsyncTask(AlarmDao alarmModel) {
            this.alarmModel = alarmModel;
        }

        @Override
        protected Void doInBackground(final Alarm... params) {
            alarmModel.delete(params[0]);
            return null;
        }
    }

    private static class getByIdAsyncTask extends android.os.AsyncTask<Integer, Void, Alarm> {

        private AlarmDao alarmModel;

        getByIdAsyncTask(AlarmDao alarmModel) {
            this.alarmModel = alarmModel;
        }

        @Override
        protected Alarm doInBackground(final Integer... params) {
            return alarmModel.getById(params[0]);
        }
    }
}
