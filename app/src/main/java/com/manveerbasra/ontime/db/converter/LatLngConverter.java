package com.manveerbasra.ontime.db.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class to handle conversions between LatLng and String
 * <p>
 * Used in Alarm.java as a @TypeConverter
 */
public class LatLngConverter {
    @TypeConverter
    public LatLng fromString(String value) {
        String[] splitValue = value.split(":");
        return new LatLng(
                Double.parseDouble(splitValue[0]),
                Double.parseDouble(splitValue[1])
        );
    }

    @TypeConverter
    public String latLngToString(LatLng latLng) {
        return latLng.latitude + ":" + latLng.longitude;
    }
}
