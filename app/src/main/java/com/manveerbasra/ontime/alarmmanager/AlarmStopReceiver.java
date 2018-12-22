package com.manveerbasra.ontime.alarmmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * BroadcastReceiver to stop alarm ringing
 */
public class AlarmStopReceiver extends BroadcastReceiver {

    private final String TAG = "AlarmStopReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "Stop alarm sound intent received");
        AlarmSoundControl alarmSoundControl = AlarmSoundControl.getInstance();
        alarmSoundControl.stopAlarmSound();
    }
}
