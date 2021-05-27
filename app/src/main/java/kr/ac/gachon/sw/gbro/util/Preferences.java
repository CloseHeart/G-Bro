package kr.ac.gachon.sw.gbro.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Preferences {
    private static String LOG_TAG = "Preferences";
    private Context context;
    private SharedPreferences prefs;

    public Preferences(Context context) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setString(String key, String value) {
        Log.d(LOG_TAG, "Set " + key + " / Value : " + value);
        prefs.edit().putString(key, value).apply();
    }

    public String getString(String key, String defValue)  {
        Log.d(LOG_TAG, "Return " + key + " / Value : " +  prefs.getString(key, defValue));
        return prefs.getString(key, defValue);
    }

    public void setBoolean(String key, boolean value) {
        Log.d(LOG_TAG, "Set " + key + " / Value : " + value);
        prefs.edit().putBoolean(key, value).apply();
    }

    public Boolean getBoolean(String key, boolean defValue)  {
        Log.d(LOG_TAG, "Return " + key + " / Value : " +  prefs.getBoolean(key, defValue));
        return prefs.getBoolean(key, defValue);
    }

    public void setStringArrayList(String key, ArrayList<String> value) {
        Gson gson = new Gson();
        prefs.edit().putString(key, gson.toJson(value)).apply();
    }

    public ArrayList<String> getStringArrayList(String key, String defValue) {
        Gson gson = new Gson();
        return gson.fromJson(prefs.getString(key, defValue), new TypeToken<ArrayList<String>>(){}.getType());
    }
}
