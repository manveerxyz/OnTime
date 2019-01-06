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

    // List is wrapped in LiveData in order to be observed and updated efficiently
    public LiveData<List<Alarm>> getAllAlarms() {
        return mAllAlarms;
    }

    public void insert(Alarm alarm) {
        mRepository.insert(alarm);
    }

    public void replace(Alarm alarm) {
        mRepository.replace(alarm);
    }

    public void update(Alarm alarm) {
        mRepository.update(alarm);
    }

    public void updateActive(Alarm alarm) {
        mRepository.updateActive(alarm);
    }

    public void delete(Alarm alarm) {
        mRepository.delete(alarm);
    }

    public Alarm getAlarmById(int id) {
        try {
            return mRepository.getAlarmById(id);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error when retrieving alarm by id: " + id);
            e.printStackTrace();
        }
        return new Alarm();
    }

}
