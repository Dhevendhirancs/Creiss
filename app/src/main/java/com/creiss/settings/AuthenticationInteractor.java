package com.creiss.settings;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;

import com.creiss.network.ApiListener;
import com.creiss.network.RestAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Upendra.Patil on 7/14/2016.
 */
public class AuthenticationInteractor {
    public final boolean DEBUG = true;
    private boolean isAlive;

    public void onCreate() {
        isAlive = true;
    }

    public void onDestroy() {
        isAlive = false;
    }

    public void authenticate(@NonNull String key, @NonNull final ApiListener callback) {
        if (DEBUG) {
            if (key.equals("creiss@123"))
                callback.success();
            else callback.failure(new Throwable());
        } else {
            if (null != callback) callback.progressDialog(true);
            Call<Boolean> authenticate = RestAdapter.api().authenticate(key);
            authenticate.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (isAlive) {
                        if (null != callback) {
                            if (response.body())
                                callback.success();
                            else callback.failure(new Throwable("Please check key."));
                            callback.progressDialog(false);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    if (isAlive) {
                        if (null != callback) {
                            callback.failure(t);
                            callback.progressDialog(false);
                        }
                    }
                }
            });
        }
    }
}
