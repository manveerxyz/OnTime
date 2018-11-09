package com.manveerbasra.ontime;

import java.util.HashMap;

/**
 * Represent an Alarm Object
 */
public class Alarm {

    private int hour;
    private int minute;
    private boolean active;
    private boolean repeat;
    private String meridian;
    private String[] activeDays;

    /**
     * Empty Constructor for Alarm Object
     */
    public Alarm() {
        this.active = true;
    }

    /**
     * Initialize Alarm object with time hour, minute, and meridian
     *
     * @param hour     hour of alarm
     * @param minute   minute of alarm
     * @param meridian meridian of time either am or pm
     */
    public Alarm(int hour, int minute, String meridian) {
        this.hour = hour;
        this.minute = minute;
        this.meridian = meridian.toUpperCase();
        this.active = true;

        this.repeat = false;
        this.activeDays = new String[0];
    }

    /**
     * Initialize Alarm object with time hour, minute, and meridian, and repeat + activeDays
     *
     * @param hour     hour of alarm
     * @param minute   minute of alarm
     * @param meridian meridian of time either am or pm
     */
    public Alarm(int hour, int minute, String meridian, boolean active, String[] activeDays) {
        this.hour = hour;
        this.minute = minute;
        this.meridian = meridian.toUpperCase();
        this.active = active;

        this.repeat = true;
        this.activeDays = activeDays;
    }

    /**
     * Return a string representation of time
     *
     * @return a string concatenation of hour, time and meridian.
     */
    public String getStringTime() {
        String hour = Integer.toString(this.hour);

        String minute;
        if (this.minute < 10) {
            minute = "0" + Integer.toString(this.minute);
        } else {
            minute = Integer.toString(this.minute);
        }

        return hour + ":" + minute + " " + meridian;
    }

    /**
     * Return a user-readable representation of activeDays
     *
     * @return readable String of activeDays array
     */
    public String getStringOfActiveDays() {
        if (activeDays.length == 7) {
            return "everyday";
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

    public void setTime(int hour, int minute, String meridian) {
        this.hour = hour;
        this.minute = minute;
        this.meridian = meridian;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getMeridian() {
        return meridian;
    }

    public void setMeridian(String meridian) {
        this.meridian = meridian;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }


    public void setActiveDays(String[] activeDays) {
        this.activeDays = activeDays;
    }

    public String[] getActiveDays() {
        return this.activeDays;
    }
}
