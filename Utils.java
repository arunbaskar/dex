package Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by arun.bhaskar on 1/11/2017.
 */
public class Utils {
    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    public static final String METRIC_URL = "&units=metric";
    public static final String IMPERIAL_URL = "&units=imperial";
    public static final String APP_ID = "&appid=af564b1d805ad174cf5a989e0ff7cc0e";

    public static JSONObject getObject(String tagName, JSONObject jsonObject) throws JSONException {
        JSONObject jObj = jsonObject.getJSONObject(tagName);
        return jObj;
    }

    public static String getString(String tagName, JSONObject jsonObject) throws JSONException {
        return jsonObject.getString(tagName);
    }

    public static float getFloat(String tagName, JSONObject jsonObject) throws JSONException {
        return (float) jsonObject.getDouble(tagName);
    }

    public static double getDouble(String tagName, JSONObject jsonObject) throws JSONException {
        return (float) jsonObject.getDouble(tagName);
    }

    public static int getInt(String tagName, JSONObject jsonObject) throws JSONException {
        return jsonObject.getInt(tagName);
    }
}
