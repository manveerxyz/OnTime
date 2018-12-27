package com.manveerbasra.ontime.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import android.util.Log;

import com.manveerbasra.ontime.db.converter.BooleanArrayConverter;
import com.manveerbasra.ontime.db.converter.DateConverter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Entity(tableName = "alarms")
@TypeConverters({DateConverter.class, BooleanArrayConverter.class})
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "alarm_id")
    public int id;

    @ColumnInfo(name = "alarm_time")
    public Date time;

    @ColumnInfo(name = "alarm_active")
    public boolean active;

    /**
     * Must have length 7
     * i'th item being true means alarm is active on i'th day
     */
    @ColumnInfo(name = "alarm_active_days")
    public boolean[] activeDays;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean[] getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(boolean[] activeDays) {
        this.activeDays = activeDays;
    }

    public Alarm() {
    }

    // Ignored Members

    @Ignore
    public static final String[] daysOfWeek =
            {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @Ignore
    public Alarm(Date time, boolean active, boolean[] activeDays) {
        this.time = time;
        this.active = active;
        this.activeDays = activeDays;
    }

    @Ignore
    public boolean isRepeat() {
        return (activeDays != null && activeDays.length > 0);
    }

    /**
     * Get alarm ring time in 12 hour format
     *
     * @return String of alarm ring time
     */
    @Ignore
    public String getStringTime() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm aa");
        return dateFormatter.format(this.time);
    }

    /**
     * Get a simple user-readable representation of activeDays
     *
     * @return a String of alarm's active days
     */
    @Ignore
    public String getStringOfActiveDays() {
        // Build string based on which indices are true in activeDays
        StringBuilder builder = new StringBuilder();
        int activeCount = 0;
        for (int i = 0; i < 7; i++) {
            if (activeDays[i]) {
                String formattedDay = daysOfWeek[i].substring(0, 3) + ", ";
                builder.append(formattedDay);
                activeCount++;
            }
        }

        if (activeCount == 7) {
            return "everyday";
        } else if (activeCount == 0) {
            return "never";
        }

        boolean satInArray = activeDays[6]; // "Saturday" in activeDays
        boolean sunInArray = activeDays[0]; // "Sunday" in activeDays

        if (satInArray && sunInArray && activeCount == 2) {
            return "weekends";
        } else if (!satInArray && !sunInArray && activeCount == 5) {
            return "weekdays";
        }

        if (builder.length() > 1) {
            builder.setLength(builder.length() - 2);
        }

        return builder.toString();
    }

    /**
     * Get time until next alarm ring
     *
     * @return time to ring in milliseconds since epoch
     */
    @Ignore
    public long getTimeToNextRing() {
        Calendar calendar = getAlarmTimeAsCalendar();

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) { // alarm time has passed for today
            calendar.add(Calendar.DAY_OF_MONTH, 1); // set alarm to ring tomorrow
        }

        Log.i("Alarm.java", "Alarm ring time is " + calendar.getTime().toString());
        return calendar.getTimeInMillis();
    }

    /**
     * Get Alarm time as a calendar object set to current date
     *
     * @return Alarm time parsed into a Calendar object
     */
    @NonNull
    private Calendar getAlarmTimeAsCalendar() {
        String[] timeString = new SimpleDateFormat("HH:mm").format(this.time).split(":");
        int hour = Integer.parseInt(timeString[0]);
        int minute = Integer.parseInt(timeString[1]);

        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }

    /**
     * Get the time to ring in milliseconds since epoch for each day alarm is active
     *
     * @return a List of milliseconds to the next alarm ring for each active day
     */
    @Ignore
    public List<Long> getTimeToWeeklyRings() {
        Calendar calendar = getAlarmTimeAsCalendar();
        long currAlarmTime = calendar.getTimeInMillis();

        List<Long> weekRingTimes = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            if (activeDays[i]) { // if alarm is active on that day
                weekRingTimes.add(getCorrectRingDay(currAlarmTime, i));
            }
        }

        return weekRingTimes;
    }

    /**
     * Correctly get the next alarm ring day based on the day it's active and current day of week
     *
     * @param alarmTime long of milliseconds since epoch of alarm time **today**
     * @param activeDay int of day alarm is active on
     * @return long of milliseconds since epoch of next alarm ring time on active day
     */
    @Ignore
    private long getCorrectRingDay(long alarmTime, int activeDay) { // TODO handle no repeat days
        Calendar currentCalendar = Calendar.getInstance();
        int currDay = currentCalendar.get(Calendar.DAY_OF_WEEK);
        currDay--; // index using 0 = Sunday

        if (alarmTime < System.currentTimeMillis() // alarm time has passed for today
                || !activeDays[currDay]) { // current day is not an active day

            if (activeDay > currDay) { // Alarm is active a later day of the week
                alarmTime += TimeUnit.MILLISECONDS.convert(activeDay - currDay, TimeUnit.DAYS);
            } else { // Have to move the alarm time to next week's active day
                alarmTime += TimeUnit.MILLISECONDS.convert(7 - currDay, TimeUnit.DAYS);
                alarmTime += TimeUnit.MILLISECONDS.convert(activeDay, TimeUnit.DAYS);
            }
        }
        return alarmTime;
    }
}
