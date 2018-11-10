package com.manveerbasra.ontime.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;


@Dao
public interface AlarmDao {
    @Query("select * from alarm")
    List<Alarm> loadAllAlarms();

    @Query("select * from alarm where id = :id")
    Alarm loadAlarmById(int id);

    @Insert(onConflict = IGNORE)
    void insertAlarm(Alarm alarm);

    @Update
    void update(Alarm alarm);

    @Delete
    void deleteAlarm(Alarm alarm);

    @Insert(onConflict = IGNORE)
    void insertOrReplaceAlarms(Alarm... alarms);

    @Delete
    void deleteAlarms(Alarm alarm1, Alarm alarm2);

    @Query("DELETE FROM Alarm")
    void deleteAll();
}
