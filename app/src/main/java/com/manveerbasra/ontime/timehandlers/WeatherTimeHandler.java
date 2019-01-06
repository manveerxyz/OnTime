package com.manveerbasra.ontime.timehandlers;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.manveerbasra.ontime.util.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Class to get and handle time shift for alarm's location's weather
 */
public class WeatherTimeHandler {

    private static final String TAG = "WeatherTimeHandler";

    // keys for HashMap of parsed JSON data
    public static final String CONDITIONS = "conditions"; // short summary of conditions
    public static final String CONDITIONS_DESC = "conditions_description";
    public static final String TEMPERATURE = "temperature";
    public static final String WIND = "wind"; // wind speed
    public static final String RAIN = "rain";
    public static final String SNOW = "snow";

    private static Map<String, Double> mConditionsToRatio = new HashMap<>();

    static { // initialize mConditionsToRatio Map
        mConditionsToRatio.put("Thunderstorm", 0.4);
        mConditionsToRatio.put("Drizzle", 0.1);
        mConditionsToRatio.put("Rain", 0.2);
        mConditionsToRatio.put("Snow", 0.2);
        mConditionsToRatio.put("Atmosphere", 0.2);
        mConditionsToRatio.put("Clear", 0.0);
        mConditionsToRatio.put("Mist", 0.0);
        mConditionsToRatio.put("Clouds", 0.0);
    }

    private String mApiKey;

    WeatherTimeHandler(String apiKey) {
        this.mApiKey = apiKey;
    }

    /**
     * Get the time shift derived from today's weather conditions
     *
     * @param start starting LatLng point
     * @param end   destination LatLng point
     * @return long time user should leave early in milliseconds
     */
    long getTimeShiftInMillis(LatLng start, LatLng end) {

        String jsonData;
        HashMap<String, Object> data;

        // Download data
        DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask();
        downloadAsyncTask.execute(getHTTPSRequestUrl(start));
        try {
            jsonData = downloadAsyncTask.get();
        } catch (ExecutionException | InterruptedException e) {
            jsonData = "";
        }

        // Parse JSON data
        ParserAsyncTask parserAsyncTask = new ParserAsyncTask();
        parserAsyncTask.execute(jsonData);
        try {
            data = parserAsyncTask.get();
        } catch (ExecutionException | InterruptedException e) {
            data = null;
        }

        if (data == null) return 0;
        else {
            return calculateTimeShiftFromData(data);
        }
    }

    /**
     * Calculate time shift for alarm time based on parsed JSON weather data
     *
     * @param data Map of parsed weather data
     * @return long time in milliseconds of alarm shift
     */
    private long calculateTimeShiftFromData(HashMap<String, Object> data) {
        String condition = (String) data.get(CONDITIONS);
        String desc = (String) data.get(CONDITIONS_DESC);

        double ratio = getShiftRatioFromWeatherConditions(condition, desc);

        if (( // Check if conditions might form ice
                condition.equals("Rain")
                        || condition.equals("Snow")
                        || (Integer) data.get(RAIN) > 2
                        || (Integer) data.get(SNOW) > 20)
                && (Double) data.get(TEMPERATURE) < 0.0) {
            ratio += 0.1;
        }

        double shiftInMinutes = 60 * ratio;
        Log.i(TAG, "Shift from weather conditions: " + shiftInMinutes + " minutes");
        return TimeUnit.MINUTES.toMillis((long) shiftInMinutes);
    }

    /**
     * Get a double from [0 - 1) based on weather conditions tables in
     * <a href="https://openweathermap.org/weather-conditions">weather-conditions-table</a>
     *
     * @param condition short one word summary of weather: one of 7 conditions from above url
     * @param desc      short description of specific weather condition
     * @return double from [0-1) representing ratio of an hour to shift alarm forward
     */
    private double getShiftRatioFromWeatherConditions(String condition, String desc) {
        double ratio = mConditionsToRatio.get(condition);
        if (desc.contains("light")) ratio -= 0.1;
        else if (desc.contains("heavy") && !condition.equals("Drizzle")) ratio += 0.1;
        else if (desc.equals("tornado")) ratio = 0.5;
        return ratio;
    }

    /**
     * Build and return the HTTPS request url to get weather data from the OpenWeatherMap API
     *
     * @param point LatLng point to get weather for
     * @return String url of HTTPS request
     */
    private String getHTTPSRequestUrl(LatLng point) {

        // Build parameters
        String lat = "lat=" + point.latitude;
        String lon = "lon=" + point.longitude;
        String key = "appid=" + mApiKey;

        String parameters = lat + "&" + lon + "&" + key;

        return "https://api.openweathermap.org/data/2.5/weather?" + parameters;
    }

    /**
     * Download data from http url connection
     *
     * @param strURL url to connect and download from
     * @return String of read data
     * @throws IOException when handling InputStreams
     */
    private static String downloadUrl(String strURL) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }

            br.close();

        } catch (IOException e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            if (inputStream != null) inputStream.close();
            if (urlConnection != null) urlConnection.disconnect();
        }
        return stringBuilder.toString();
    }

    /**
     * Asynchronously download data from URL
     */
    private static class DownloadAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }
    }

    /**
     * Asynchronously parse the Google Places in JSON format
     */
    private static class ParserAsyncTask extends AsyncTask<String, Integer, HashMap<String, Object>> {

        @Override
        protected HashMap<String, Object> doInBackground(String... jsonData) {

            JSONObject jObject;
            HashMap<String, Object> parsedData = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                JSONParser parser = new JSONParser();

                parsedData = parser.parseFromWeather(jObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return parsedData;
        }
    }
}
