package com.manveerbasra.ontime.util;

import android.util.Log;

import com.manveerbasra.ontime.timehandlers.TrafficTimeHandler;
import com.manveerbasra.ontime.timehandlers.WeatherTimeHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Class used to parse JSONObject received from a HTTPS request
 */
public class JSONParser {

    private final String TAG = "JSONParser";

    /**
     * Receives a JSONObject from a GoogleMaps API Request
     * and returns a HashMap of keys to values containing only the route durations
     * See <a href="https://developers.google.com/maps/documentation/directions/intro#Routes">docs</a>
     *
     * @param jObject JSONObject received from API request
     * @return a HashMap of keys (duration) to their respective int durations in seconds
     */
    public HashMap<String, Integer> parseFromMaps(JSONObject jObject){

        HashMap<String, Integer> routes = new HashMap<>();

        try {
            String statusCode = jObject.getString("status");
            if (!statusCode.equals("OK")) {
                Log.e(TAG, "Status code from GoogleMaps API: " + statusCode);
                return null;
            }
            JSONArray jRoutes = jObject.getJSONArray("routes");
            JSONArray jLegs = (jRoutes.getJSONObject(0)).getJSONArray("legs");

            JSONObject jTime = (jLegs.getJSONObject(0)).getJSONObject("duration");
            JSONObject jTimeInTraffic = (jLegs.getJSONObject(0)).getJSONObject("duration_in_traffic");

            routes.put(TrafficTimeHandler.DURATION, jTime.getInt("value"));
            routes.put(TrafficTimeHandler.DURATION_TRAFFIC, jTimeInTraffic.getInt("value"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return routes;
    }

    /**
     * Receives a JSONObject from a OpenWeatherMap API Request
     * and returns a HashMap of keys to values containing only the route durations
     * See <a href="https://openweathermap.org/current#current_JSON">docs</a>
     *
     * @param jObject JSONObject received from API request
     * @return a HashMap of keys (duration) to their respective int durations in seconds
     */
    public HashMap<String, Object> parseFromWeather(JSONObject jObject) {

        HashMap<String, Object> conditions = new HashMap<>();

        try {
            JSONArray jWeather = jObject.getJSONArray("weather");
            JSONObject jMain = jObject.getJSONObject("main");

            conditions.put(WeatherTimeHandler.CONDITIONS, jWeather.getJSONObject(0).getString("main"));
            conditions.put(WeatherTimeHandler.CONDITIONS_DESC, jWeather.getJSONObject(0).getString("description"));
            conditions.put(WeatherTimeHandler.TEMPERATURE, jMain.get("temp"));

            if (jObject.has("wind"))
                conditions.put(WeatherTimeHandler.WIND, jObject.getJSONObject("wind").get("speed"));

            int rainAmount = 0;
            if (jObject.has("rain")) {
                JSONObject jRain = jObject.getJSONObject("rain");
                if (jRain.has("1h")) {
                    rainAmount =  jRain.getInt("1h");
                } else if (jRain.has("3h")) {
                    rainAmount =  jRain.getInt("3h");
                }
            }

            int snowAmount = 0;
            if (jObject.has("snow")) {
                JSONObject jSnow = jObject.getJSONObject("snow");
                if (jSnow.has("1h")) {
                    snowAmount = jSnow.getInt("1h");
                } else if (jSnow.has("3h")) {
                    snowAmount = jSnow.getInt("3h");
                }
            }

            conditions.put(WeatherTimeHandler.RAIN, rainAmount);
            conditions.put(WeatherTimeHandler.SNOW, snowAmount);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return conditions;
    }
}
