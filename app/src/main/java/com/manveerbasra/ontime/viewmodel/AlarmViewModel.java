package com.manveerbasra.ontime.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.manveerbasra.ontime.AlarmRepository;
import com.manveerbasra.ontime.db.AlarmEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * View Model to keep a reference to the alarm repository and
 * an up-to-date list of all alarm.
 * Completely separates UI from Repository
 */
public class AlarmViewModel extends AndroidViewModel {

    private AlarmRepository repository;
    private LiveData<List<AlarmEntity>> allAlarms;

    public AlarmViewModel(Application application) {
        super(application);
        repository = new AlarmRepository(application);
        allAlarms = repository.getAllAlarms();
    }

    /**
     * Get a List of all AlarmEntity objects in repository. List is wrapped in LiveData
     * in order to be observed and updated efficiently
     * @return LiveData wrapped List of AlarmEntity objects
     */
    public LiveData<List<AlarmEntity>> getAllAlarms() {
        return allAlarms;
    }

    /**
     * Insert new AlarmEntity in repository
     * @param alarm AlarmEntity object to insert
     */
    public void insert(AlarmEntity alarm) {
        repository.insert(alarm);
    }

    /**
     * Update AlarmEntity in repository
     * @param alarm AlarmEntity object to update
     */
    public void update(AlarmEntity alarm) {
        repository.update(alarm);
    }

    /**
     * Delete AlarmEntity in repository
     * @param alarm AlarmEntity object to delete
     */
    public void delete(AlarmEntity alarm) {
        repository.delete(alarm);
    }

    /**
     * Get AlarmEntity by id from repository
     * @param id AlarmEntity's int id
     * @return requested AlarmEntity object
     */
    public AlarmEntity getById(int id) {
        try {
            return repository.getById(id);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new AlarmEntity();
    }

}
