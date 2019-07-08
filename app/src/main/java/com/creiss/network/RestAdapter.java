package com.creiss.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Upendra.Patil on 7/14/2016.
 */
public class RestAdapter {
    private static NetworkApi apis;

    public static void create() {
        if (null == apis) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            Retrofit retrofit = new Retrofit.Builder().baseUrl("http://172.52.50.137/CreissWeb/api/UserVerification/").addConverterFactory(GsonConverterFactory.create()).client(client).build();
            apis = retrofit.create(NetworkApi.class);
        }
    }

    public static NetworkApi api() {
        return apis;
    }
}
