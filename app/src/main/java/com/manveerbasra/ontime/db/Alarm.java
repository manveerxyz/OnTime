package com.manveerbasra.ontime.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Alarm {
    @PrimaryKey
    @NonNull
    public String id;

    public int hour;

    public int minute;

    public boolean active;

    public boolean repeat;

    public String meridian;

    public String activeDays;
}
