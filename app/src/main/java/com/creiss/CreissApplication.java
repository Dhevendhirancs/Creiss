package com.creiss;

import android.app.Application;

import com.creiss.network.RestAdapter;
import com.creiss.utility.CreissPreferences;

/**
 * Created by Upendra.Patil on 7/12/2016.
 */
public class CreissApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CreissPreferences.init(getApplicationContext());
        RestAdapter.create();
    }
}
