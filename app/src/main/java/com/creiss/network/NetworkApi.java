package com.creiss.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Upendra.Patil on 7/14/2016.
 */
public interface NetworkApi {
    @POST("VerifyMobileDeviceKey")
    Call<Boolean> authenticate(@Body String authKey);
}
