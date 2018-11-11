package com.manveerbasra.ontime.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.manveerbasra.ontime.db.converter.DateConverter;
import com.manveerbasra.ontime.db.converter.StringArrayConverter;
import com.manveerbasra.ontime.model.Alarm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "alarms")
@TypeConverters({DateConverter.class, StringArrayConverter.class})
public class AlarmEntity implements Alarm {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public Date time;
    public boolean active;
    public String[] activeDays;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String[] getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(String[] activeDays) {
        this.activeDays = activeDays;
    }

    public AlarmEntity() {
    }

    public AlarmEntity(Alarm alarm) {
        this.id = alarm.getId();
        this.time = alarm.getTime();
        this.active = alarm.isActive();
        this.activeDays = alarm.getActiveDays();
    }

    // Ignored Methods

    @Ignore
    public AlarmEntity(Date time, boolean active, String[] activeDays) {
        this.time = time;
        this.active = active;
        this.activeDays = activeDays;
    }

    @Ignore
    public boolean isRepeat() {
        return (activeDays == null || activeDays.length == 0);
    }

    @Ignore
    public String getStringTime() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm aa");
        return dateFormatter.format(this.time);
    }

    @Ignore
    public String getStringOfActiveDays() {
        if (activeDays.length == 7) {
            return "everyday";
        } else if (activeDays.length == 0) {
            return "never";
        }

        boolean satInArray = false; // "Saturday" in activeDays
        boolean sunInArray = false; // "Sunday" in activeDays

        StringBuilder builder = new StringBuilder();
        for (String day : activeDays) {
            if (day.equals("Saturday")) {
                satInArray = true;
            } else if (day.equals("Sunday")) {
                sunInArray = true;
            }
            String formattedDay = day.substring(0, 3) + ", ";
            builder.append(formattedDay);
        }

        if (satInArray && sunInArray && activeDays.length == 2) {
            return "weekends";
        } else if (!satInArray && !sunInArray && activeDays.length == 5) {
            return "weekdays";
        }

        if (builder.length() > 1) {
            builder.setLength(builder.length() - 2);
        }

        return builder.toString();
    }

}
