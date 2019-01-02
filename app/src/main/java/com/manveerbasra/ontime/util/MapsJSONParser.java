package com.manveerbasra.ontime.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Class used to parse JSONObject received from GoogleMaps API Request
 */
public class MapsJSONParser {

    /**
     * Receives a JSONObject and returns a HashMap of keys to values
     *
     * @param jObject JSONObject received from HTTP(S) req
     * @return a HashMap of keys (duration) to their respective int durations in seconds
     */
    public HashMap<String, Integer> parse(JSONObject jObject) {

        HashMap<String, Integer> routes = new HashMap<>();

        try {

            JSONArray jRoutes = jObject.getJSONArray("routes");
            JSONArray jLegs = (jRoutes.getJSONObject(0)).getJSONArray("legs");

            JSONObject jTime = (jLegs.getJSONObject(0)).getJSONObject("duration");
            JSONObject jTimeInTraffic = (jLegs.getJSONObject(0)).getJSONObject("duration_in_traffic");

            routes.put("duration", jTime.getInt("value"));
            routes.put("duration_in_traffic", jTimeInTraffic.getInt("value"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return routes;
    }
}
