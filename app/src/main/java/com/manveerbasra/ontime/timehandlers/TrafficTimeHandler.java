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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Class to get and handle time shift for alarm's origin -> destination commute
 */
public class TrafficTimeHandler {

    private final String TAG = "TrafficTimeHandler";

    // keys for HashMap of parsed JSON data
    public static final String DURATION = "duration";
    public static final String DURATION_TRAFFIC = "duration_in_traffic";

    private String mApiKey;

    TrafficTimeHandler(String apiKey) {
        this.mApiKey = apiKey;
    }

    /**
     * Get the difference between the usual and today's commute duration
     *
     * @param start               starting LatLng point
     * @param end                 destination LatLng point
     * @param departureTimeInSecs time in seconds since epoch of expected departure time
     * @return long time difference between usual and today's commute in milliseconds
     */
    long getTimeShiftInMillis(LatLng start, LatLng end, int departureTimeInSecs) {

        String jsonData;
        HashMap<String, Integer> data;

        // Download data
        DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask();
        downloadAsyncTask.execute(getHTTPSRequestUrl(start, end, departureTimeInSecs));
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
            long duration = data.get(DURATION);
            long durationTraffic = data.get(DURATION_TRAFFIC);
            Log.i(TAG, "Normal trip length: " + duration + " secs and length in traffic: " + durationTraffic + " secs");
            long shiftInSecs = durationTraffic - duration;
            return TimeUnit.SECONDS.toMillis(shiftInSecs);
        }
    }

    /**
     * Build and return the HTTPS request url to get directions from the Google Maps API
     *
     * @param start               starting LatLng point
     * @param end                 destination LatLng point
     * @param departureTimeInSecs time in seconds since epoch of expected departure time
     * @return String url of HTTPS request
     */
    private String getHTTPSRequestUrl(LatLng start, LatLng end, int departureTimeInSecs) {

        // Build parameters
        String origin = "origin=" + start.latitude + "," + start.longitude;
        String dest = "destination=" + end.latitude + "," + end.longitude;
        String departureTime = "departure_time=" + departureTimeInSecs;
        String key = "key=" + mApiKey;

        String parameters = origin + "&" + dest + "&" + departureTime + "&" + key;

        return "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
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
    private static class ParserAsyncTask extends AsyncTask<String, Integer, HashMap<String, Integer>> {

        @Override
        protected HashMap<String, Integer> doInBackground(String... jsonData) {

            JSONObject jObject;
            HashMap<String, Integer> parsedData = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                JSONParser parser = new JSONParser();

                parsedData = parser.parseFromMaps(jObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return parsedData;
        }
    }
}
