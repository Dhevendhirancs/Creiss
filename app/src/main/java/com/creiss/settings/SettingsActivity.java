package com.creiss.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.creiss.R;

/**
 * Created by Upendra.Patil on 7/11/2016.
 */
public class SettingsActivity extends AppCompatActivity implements NavigationListener {
    public static final String TAG = SettingsActivity.class.getSimpleName();

    public static Intent getIntent(Context mContext) {
        return new Intent(mContext, SettingsActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        navigate();
    }

    @Override
    public void navigate() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl, SettingsFragment.newInstance()).commit();
    }
}

