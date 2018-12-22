package com.manveerbasra.ontime.alarmmanager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

/**
 * Singleton class to control Alarm Ringing Sound
 */
class AlarmSoundControl {

    private final String TAG = "AlarmSoundControl";

    private static AlarmSoundControl INSTANCE;
    private MediaPlayer mediaPlayer;
    private AlarmSoundControl() {
    }

    public static AlarmSoundControl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AlarmSoundControl();
        }
        return INSTANCE;
    }

    /**
     * Play Alarm Sound
     */
    public void playAlarmSound(Context context) {
        Log.i(TAG, "Playing alarm ringing sound");
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(context, getAlarmUri());
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    /**
     * Stop Alarm Sound currently playing
     */
    public void stopAlarmSound() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * Get alarm sound, try to get default, then notification, then ringtone
     * @return URI for alarm sound
     */
    private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }
}