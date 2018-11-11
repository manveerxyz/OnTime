package com.manveerbasra.ontime;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.manveerbasra.ontime.db.AlarmDao;
import com.manveerbasra.ontime.db.AlarmEntity;
import com.manveerbasra.ontime.db.AlarmDatabase;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Abstracted Repository to handle interactions between ViewModels and Database
 * TODO Implement Singleton Pattern
 */
public class AlarmRepository {

    private AlarmDao alarmModel;
    private LiveData<List<AlarmEntity>> allAlarms;

    /**
     * Application is used instead of Context in order to prevent memory leaks
     * between Activity switches
     * @param application Application object of global app state
     */
    public AlarmRepository(Application application) {
        AlarmDatabase db = AlarmDatabase.getInstance(application);
        alarmModel = db.alarmModel();
        allAlarms = alarmModel.getAllAlarms();
    }

    /**
     * Observed LiveData will notify the observer when data has changed
     * @return List of AlarmEntitys wrapped in a LiveData object
     */
    public LiveData<List<AlarmEntity>> getAllAlarms() {
        return allAlarms;
    }

    /**
     * Asynchronously insert AlarmEntity into db to prevent UI stalls.
     * @param alarm AlarmEntity to insert.
     */
    public void insert(AlarmEntity alarm) {
        new insertAsyncTask(alarmModel).execute(alarm);
    }

    /**
     * Asynchronously update AlarmEntity in db to prevent UI stalls.
     * @param alarm AlarmEntity to update.
     */
    public void update(AlarmEntity alarm) {
        new updateAsyncTask(alarmModel).execute(alarm);
    }

    /**
     * Asynchronously get AlarmEntity by id to prevent UI stalls.
     * @param id AlarmEntity int id
     * @return requested AlarmEntity object
     * @throws ExecutionException error thrown from AsyncTask
     * @throws InterruptedException error thrown from AsyncTask
     */
    public AlarmEntity getById(int id) throws ExecutionException, InterruptedException {
        return new getByIdAsyncTask(alarmModel).execute(id).get();
    }

    /**
     * Insert by AlarmEntity
     */
    private static class insertAsyncTask extends AsyncTask<AlarmEntity, Void, Void> {

        private AlarmDao alarmModel;

        insertAsyncTask(AlarmDao alarmModel) {
            this.alarmModel = alarmModel;
        }

        @Override
        protected Void doInBackground(final AlarmEntity... params) {
            alarmModel.insert(params[0]);
            return null;
        }
    }

    /**
     * Update by AlarmEntity
     */
    private static class updateAsyncTask extends AsyncTask<AlarmEntity, Void, Void> {

        private AlarmDao alarmModel;

        updateAsyncTask(AlarmDao alarmModel) {
            this.alarmModel = alarmModel;
        }

        @Override
        protected Void doInBackground(final AlarmEntity... params) {
            alarmModel.update(params[0]);
            return null;
        }
    }

    /**
     * Get AlarmEntity by id
     */
    private static class getByIdAsyncTask extends android.os.AsyncTask<Integer, Void, AlarmEntity> {

        private AlarmDao alarmModel;

        getByIdAsyncTask(AlarmDao alarmModel) {
            this.alarmModel = alarmModel;
        }

        @Override
        protected AlarmEntity doInBackground(final Integer... params) {
            return alarmModel.getById(params[0]);
        }
    }
}
