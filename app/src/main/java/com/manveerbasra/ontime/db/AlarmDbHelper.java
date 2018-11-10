package com.manveerbasra.ontime.db;

import android.content.Context;

import com.manveerbasra.ontime.AlarmDataManager;

import java.util.List;

/**
 * Class to handle interaction between activities and AlarmDatabase
 */
public class AlarmDbHelper {

    private AlarmDatabase db;

    public AlarmDbHelper(Context context) {
        db = AlarmDatabase.getInMemoryDatabase(context);
    }

    /**
     * Add AlarmDataManager object to database
     * @param alarmDM AlarmDataManager object
     */
    public void addAlarm(final AlarmDataManager alarmDM) {
        Alarm alarm = new Alarm();
        alarm.hour = alarmDM.getHour();
        alarm.minute = alarmDM.getMinute();
        alarm.meridian = alarmDM.getMeridian();
        alarm.active = alarmDM.isActive();
        alarm.repeat = alarmDM.isRepeat();
        alarm.activeDays = alarmDM.getActiveDays();
        db.alarmModel().insertAlarm(alarm);
    }

    public void updateAlarm(final AlarmDataManager alarmDM) {
        Alarm alarm = new Alarm();
        alarm.id = alarmDM.getId();
        alarm.hour = alarmDM.getHour();
        alarm.minute = alarmDM.getMinute();
        alarm.meridian = alarmDM.getMeridian();
        alarm.active = alarmDM.isActive();
        alarm.repeat = alarmDM.isRepeat();
        alarm.activeDays = alarmDM.getActiveDays();
        db.alarmModel().update(alarm);
    }

    /**
     * Return an Array of Alarm objects
     * @return Array of Alarms
     */
    public AlarmDataManager[] getAllAlarms() {
        List<Alarm> alarmsList = db.alarmModel().loadAllAlarms();
        AlarmDataManager[] alarms = new AlarmDataManager[alarmsList.size()];

        int i = 0;
        for (Alarm alarm: alarmsList) {
            alarms[i++] = getAlarmDataManager(alarm);
        }

        return alarms;
    }

    /**
     * Convert Alarm to AlarmDataManager and return
     * @param alarm Alarm object
     * @return AlarmDataManager representation of alarm
     */
    private AlarmDataManager getAlarmDataManager(Alarm alarm) {
        return new AlarmDataManager(
                alarm.id,
                alarm.hour,
                alarm.minute,
                alarm.meridian,
                alarm.active,
                alarm.repeat,
                alarm.activeDays
        );
    }
}
