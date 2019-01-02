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

    private AlarmDao alarmModel;
    private LiveData<List<Alarm>> allAlarms;

    /**
     * Application is used instead of Context in order to prevent memory leaks
     * between Activity switches
     *
     * @param application Application object of global app state
     */
    public AlarmRepository(Application application) {
        AlarmDatabase db = AlarmDatabase.getInstance(application);
        alarmModel = db.alarmModel();
        allAlarms = alarmModel.getAllAlarms();
    }

    /**
     * Observed LiveData will notify the observer when data has changed
     *
     * @return List of AlarmEntitys wrapped in a LiveData object
     */
    public LiveData<List<Alarm>> getAllAlarms() {
        return allAlarms;
    }

    /**
     * Asynchronously insert Alarm into db to prevent UI stalls.
     *
     * @param alarm Alarm to insert.
     */
    public void insert(Alarm alarm) {
        new insertAsyncTask(alarmModel).execute(alarm);
    }

    /**
     * Asynchronously update Alarm in db to prevent UI stalls.
     *
     * @param alarm Alarm to update.
     */
    public void update(Alarm alarm) {
        new updateAsyncTask(alarmModel).execute(alarm);
    }

    /**
     * Asynchronously update Alarm's activity in db to prevent UI stalls.
     *
     * @param alarm Alarm to update.
     */
    public void updateActive(Alarm alarm) {
        new updateActiveAsyncTask(alarmModel).execute(alarm);
    }

    /**
     * Asynchronously delete Alarm in db to prevent UI stalls.
     *
     * @param alarm Alarm to delete.
     */
    public void delete(Alarm alarm) {
        new deleteAsyncTask(alarmModel).execute(alarm);
    }

    /**
     * Asynchronously get Alarm by id to prevent UI stalls.
     *
     * @param id Alarm int id
     * @return requested Alarm object
     * @throws ExecutionException   error thrown from AsyncTask
     * @throws InterruptedException error thrown from AsyncTask
     */
    public Alarm getById(int id) throws ExecutionException, InterruptedException {
        return new getByIdAsyncTask(alarmModel).execute(id).get();
    }

    /**
     * Insert by Alarm
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

    /**
     * Update Active by Id
     */
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

    /**
     * Update Active by Alarm Id
     */
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

    /**
     * Delete by Alarm
     */
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

    /**
     * Get Alarm by id
     */
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
