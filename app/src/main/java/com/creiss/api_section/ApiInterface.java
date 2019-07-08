package com.creiss.api_section;

import com.creiss.model.TripScanResponse;

import java.sql.Array;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET ("api/BoxDelivery")
    Call<List<TripScanResponse>> tripScan (@Query("tourNumber") String number);
}
