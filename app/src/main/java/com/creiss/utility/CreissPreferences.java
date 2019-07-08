package com.creiss.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Upendra.Patil on 7/12/2016.
 */
public class CreissPreferences {
    public static final String SHPREF_NAME = CreissPreferences.class.getSimpleName();
    public static final String SHPREF_KEY_PATH = "path";
    public static final String IS_JOB_SCHEDULER_CALLED = "IsJobScheduleCalled";

    private static SharedPreferences mPrefs;
    private static CreissPreferences mInstance;

    public CreissPreferences(Context mContext) {
        mPrefs = mContext.getSharedPreferences(SHPREF_NAME, Context.MODE_PRIVATE);
    }

    public String get(String key) {
        return mPrefs.getString(key, null);
    }

    public void set(String key, String value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key) {
        return mPrefs.getBoolean(key, false);
    }

    public void setBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void init(Context applicationContext) {
        mInstance = new CreissPreferences(applicationContext);
    }

    public static CreissPreferences getInstance() {
        return mInstance;
    }
}
