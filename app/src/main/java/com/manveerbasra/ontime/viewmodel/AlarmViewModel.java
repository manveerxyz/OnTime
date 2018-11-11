package com.manveerbasra.ontime.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.manveerbasra.ontime.AlarmRepository;
import com.manveerbasra.ontime.db.AlarmEntity;

import java.util.List;

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

    public LiveData<List<AlarmEntity>> getAllAlarms() {
        return allAlarms;
    }

    public void insert(AlarmEntity alarm) {
        repository.insert(alarm);
    }

    public void update(AlarmEntity alarm) {
        repository.update(alarm);
    }
}
