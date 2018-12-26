package com.manveerbasra.ontime.db.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to handle conversions between boolean[] and String
 *
 * Used in Alarm.java as a @TypeConverter
 */
public class BooleanArrayConverter {
    @TypeConverter
    public boolean[] fromString(String value) {
        boolean[] arr = new boolean[7];
        if (value.equals("")) {
            return arr;
        }

        List<String> list = new ArrayList<>(Arrays.asList(value.split(",")));

        int i = 0;
        for (String item: list) {
            arr[i] = Boolean.parseBoolean(item);
            i++;
        }
        return arr;
    }

    @TypeConverter
    public String arrayToString(boolean[] arr) {
        StringBuilder builder = new StringBuilder();
        if (arr == null) {
            return "";
        }
        for (boolean item: arr) {
            builder.append(item + ",");
        }
        if (builder.length() > 0) { // cut off trailing comma
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }
}
