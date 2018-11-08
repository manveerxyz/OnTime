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
    private boolean am;
    private HashMap<String, Boolean> activeDays;

    public Alarm(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        this.active = false;
    }

    public String getStringTime() {
        String hour = Integer.toString(this.hour);

        String minute;
        if (this.minute < 10) {
            minute = "0" + Integer.toString(this.minute);
        } else {
            minute = Integer.toString(this.minute);
        }

        String meridiem;
        if (am) {
            meridiem = "am";
        } else {
            meridiem = "pm";
        }

        return hour + ":" + minute + " " + meridiem;
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

    public HashMap<String, Boolean> getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(HashMap<String, Boolean> activeDays) {
        this.activeDays = activeDays;
    }
}
