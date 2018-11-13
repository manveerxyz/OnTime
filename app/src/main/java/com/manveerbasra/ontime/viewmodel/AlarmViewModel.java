package com.manveerbasra.ontime.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.manveerbasra.ontime.AlarmRepository;
import com.manveerbasra.ontime.db.Alarm;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * View Model to keep a reference to the alarm repository and
 * an up-to-date list of all alarm.
 * Completely separates UI from Repository
 */
public class AlarmViewModel extends AndroidViewModel {

    private AlarmRepository repository;
    private LiveData<List<Alarm>> allAlarms;

    public AlarmViewModel(Application application) {
        super(application);
        repository = new AlarmRepository(application);
        allAlarms = repository.getAllAlarms();
    }

    /**
     * Get a List of all Alarm objects in repository. List is wrapped in LiveData
     * in order to be observed and updated efficiently
     * @return LiveData wrapped List of Alarm objects
     */
    public LiveData<List<Alarm>> getAllAlarms() {
        return allAlarms;
    }

    /**
     * Insert new Alarm in repository
     * @param alarm Alarm object to insert
     */
    public void insert(Alarm alarm) {
        repository.insert(alarm);
    }

    /**
     * Update Alarm in repository
     * @param alarm Alarm object to update
     */
    public void update(Alarm alarm) {
        repository.update(alarm);
    }

    /**
     * Delete Alarm in repository
     * @param alarm Alarm object to delete
     */
    public void delete(Alarm alarm) {
        repository.delete(alarm);
    }

    /**
     * Get Alarm by id from repository
     * @param id Alarm's int id
     * @return requested Alarm object
     */
    public Alarm getById(int id) {
        try {
            return repository.getById(id);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new Alarm();
    }

}
