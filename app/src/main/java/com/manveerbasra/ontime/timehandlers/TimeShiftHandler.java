package com.manveerbasra.ontime.timehandlers;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class to get and handle time shift required for user to be alerted OnTime
 */
public class TimeShiftHandler {

    private TrafficTimeHandler trafficTimeHandler;
    private WeatherTimeHandler weatherTimeHandler;

    public TimeShiftHandler(String mapsApiKey, String weatherApiKey) {
        trafficTimeHandler = new TrafficTimeHandler(mapsApiKey);
        weatherTimeHandler = new WeatherTimeHandler(weatherApiKey);
    }

    /**
     * Get the alarm's time shift derived from current traffic and weather conditions
     *
     * @param start               starting LatLng point
     * @param end                 destination LatLng point
     * @param departureTimeInSecs time in seconds since epoch of expected departure time
     * @return long alarm time shift in milliseconds
     */
    public long getTimeShiftInMillis(LatLng start, LatLng end, int departureTimeInSecs) {
        long trafficShift = trafficTimeHandler.getTimeShiftInMillis(start, end, departureTimeInSecs);
        long weatherShift = weatherTimeHandler.getTimeShiftInMillis(start, end);

        return (long) ((0.5) * (trafficShift + weatherShift));
    }
}
