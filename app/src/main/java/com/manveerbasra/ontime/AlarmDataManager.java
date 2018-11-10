package com.manveerbasra.ontime;

/**
 * Represent an AlarmDataManager Object
 */
public class AlarmDataManager {

    private int id;
    private int hour;
    private int minute;
    private boolean active;
    private boolean repeat;
    private String meridian;
    private String[] activeDays;

    /**
     * Empty Constructor for AlarmDataManager Object
     * <p>
     * Called when AddAlarmActivity is created
     */
    public AlarmDataManager() {
        this.active = true;
        this.activeDays = new String[0];
    }

    /**
     * Full Constructor for AlarmDataManager Object
     * <p>
     * Called when AlarmDbHelper getsAllAlarms()
     *
     * @param id         int alarm id (for db use)
     * @param hour       int alarm hour
     * @param minute     int alarm minute
     * @param meridian   int alarm meridian, either AM or PM
     * @param active     boolean of whether alarm is active or not
     * @param repeat     boolean of whether alarm repeats or not
     * @param activeDays String Array of days alarm is active on
     */
    public AlarmDataManager(int id, int hour, int minute, String meridian, boolean active, boolean repeat, String[] activeDays) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.meridian = meridian.toUpperCase();
        this.active = active;

        this.repeat = repeat;
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

    /**
     * Set hour, minute, and meridian from String time
     *
     * @param time String of format "hh:mm aa"
     */
    public void setTime(String time) {
        String[] parts = time.split(":");
        String[] parts2 = parts[1].split(" ");

        this.hour = Integer.parseInt(parts[0]);
        this.minute = Integer.parseInt(parts2[0]);
        this.meridian = parts2[1];
    }

    /**
     * General getters and setters
     */

    public int getId() {
        return id;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getMeridian() {
        return meridian;
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
