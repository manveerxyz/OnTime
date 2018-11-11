package com.manveerbasra.ontime.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;


@Dao
public interface AlarmDao {
    @Query("select * from alarms")
    LiveData<List<AlarmEntity>> getAllAlarms();

    @Insert(onConflict = IGNORE)
    void insert(AlarmEntity alarm);

    @Update
    void update(AlarmEntity alarm);

    @Delete
    void deleteAlarm(AlarmEntity alarm);

    @Insert(onConflict = IGNORE)
    void insertOrReplaceAlarms(AlarmEntity... alarms);

    @Delete
    void deleteAlarms(AlarmEntity alarm1, AlarmEntity alarm2);

    @Query("DELETE FROM alarms")
    void deleteAll();
}
