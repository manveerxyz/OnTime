package com.manveerbasra.ontime.timehandlers;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class to get and handle time shift required for user to be alerted OnTime
 */
public class TimeShiftHandler {

    private final String TAG = "TimeShiftHandler";

    private TrafficTimeHandler mTrafficTimeHandler;
    private WeatherTimeHandler mWeatherTimeHandler;

    public TimeShiftHandler(String mapsApiKey, String weatherApiKey) {
        mTrafficTimeHandler = new TrafficTimeHandler(mapsApiKey);
        mWeatherTimeHandler = new WeatherTimeHandler(weatherApiKey);
    }

    /**
     * Get the alarm's time shift derived from current traffic and weather conditions
     *
     * @param start               starting LatLng point
     * @param end                 destination LatLng point
     * @param departureTimeInSecs time in seconds since epoch of expected departure time
     * @param transMode String of transportation mode
     * @return long alarm time shift in milliseconds
     */
    public long getTimeShiftInMillis(LatLng start, LatLng end, int departureTimeInSecs, String transMode) {
        long trafficShift = mTrafficTimeHandler.getTimeShiftInMillis(start, end, departureTimeInSecs, transMode);
        long weatherShift = mWeatherTimeHandler.getTimeShiftInMillis(start, end);

        long totalShift = (long) (trafficShift + weatherShift);

        Log.i(TAG, "Total Time shift: " + totalShift +
                " = (" + trafficShift + " + " + weatherShift + ")");
        return totalShift;
    }
}
