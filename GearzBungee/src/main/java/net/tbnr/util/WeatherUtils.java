/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by jake on 12/30/13.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class WeatherUtils {
    public static String getWeatherConditons(String place) {
        InputStream is;
        BufferedReader rd;

        String autoComplete = "http://autocomplete.wunderground.com/aq?query=" + place;
        try {
            is = new URL(autoComplete).openStream();
            rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);

            JSONObject jsonObject = new JSONObject(jsonText);

            JSONArray results = (JSONArray) jsonObject.get("RESULTS");
            if (results == null) {
                return "No match found for: " + place;
            }

            JSONObject matchOne;
            try {
                matchOne = (JSONObject) results.get(0);
            } catch (IndexOutOfBoundsException e) {

                return "No match found for: " + place;
            }

            String zmw = (String) matchOne.get("zmw");

            return getForecast(zmw);

        } catch (IOException | JSONException e) {
            return "Error getting weather data!";
        }

    }

    private static String getForecast(String zmw) {
        // api key hard coded
        String dataUrl = "http://api.wunderground.com/api/" + "778ef32e457e9a7d" + "/forecast/q/zmw:" + zmw + ".json";

        try {
            InputStream is = new URL(dataUrl).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject jsonObject = new JSONObject(jsonText);
            JSONObject forecast = (JSONObject) jsonObject.get("forecast");
            JSONObject simpleForecast = (JSONObject) forecast.get("simpleforecast");
            JSONArray dayForecasts = (JSONArray) simpleForecast.get("forecastday");
            JSONObject forecastDay = (JSONObject) dayForecasts.get(0);

            JSONObject low = (JSONObject) forecastDay.get("low");
            String low_far = (String) low.get("fahrenheit");
            String low_cel = (String) low.get("celsius");

            JSONObject high = (JSONObject) forecastDay.get("high");
            String high_far = (String) high.get("fahrenheit");
            String high_cel = (String) high.get("celsius");

            String conditions = (String) forecastDay.get("conditions");

            return "Conditions: " + conditions + " " + "Temp:" + " High: " + high_far + "F/" + high_cel + "C Low: " + low_far + "F/" + low_cel + "C ";

        } catch (NullPointerException | JSONException | IOException e) {
            return "Error parsing weather data...";
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
