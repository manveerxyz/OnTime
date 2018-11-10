package com.manveerbasra.ontime.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

@Entity
@TypeConverters(StringArrayConverter.class)
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int hour;

    public int minute;

    public boolean active;

    public boolean repeat;

    public String meridian;

    public String[] activeDays;
}
