package kr.ac.gachon.sw.gbro.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

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
}
