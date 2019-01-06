package com.manveerbasra.ontime.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.manveerbasra.ontime.util.AlarmRepository;
import com.manveerbasra.ontime.db.Alarm;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * View Model to keep a reference to the alarm repository and
 * an up-to-date list of all alarm.
 * Completely separates UI from Repository
 */
public class AlarmViewModel extends AndroidViewModel {

    private final String TAG = "AlarmViewModel";

    private AlarmRepository mRepository;
    private LiveData<List<Alarm>> mAllAlarms;

    public AlarmViewModel(Application application) {
        super(application);
        mRepository = new AlarmRepository(application);
        mAllAlarms = mRepository.getAllAlarms();
    }

    /**
     * Get a List of all Alarm objects in repository. List is wrapped in LiveData
     * in order to be observed and updated efficiently
     *
     * @return LiveData wrapped List of Alarm objects
     */
    public LiveData<List<Alarm>> getAllAlarms() {
        return mAllAlarms;
    }

    /**
     * Insert new Alarm in repository
     *
     * @param alarm Alarm object to insert
     */
    public void insert(Alarm alarm) {
        mRepository.insert(alarm);
    }

    /**
     * Update Alarm in repository
     *
     * @param alarm Alarm object to update
     */
    public void update(Alarm alarm) {
        mRepository.update(alarm);
    }

    /**
     * Update Alarm's activity in repository
     *
     * @param alarm Alarm object to update
     */
    public void updateActive(Alarm alarm) {
        mRepository.updateActive(alarm);
    }

    /**
     * Delete Alarm in repository
     *
     * @param alarm Alarm object to delete
     */
    public void delete(Alarm alarm) {
        mRepository.delete(alarm);
    }

    /**
     * Get Alarm by id from repository
     *
     * @param id Alarm's int id
     * @return requested Alarm object
     */
    public Alarm getById(int id) {
        try {
            return mRepository.getById(id);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error when retrieving alarm by id: " + id);
            e.printStackTrace();
        }
        return new Alarm();
    }

}
