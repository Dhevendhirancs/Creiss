package com.creiss.mvp;

import com.creiss.api_section.ApiInterface;
import com.creiss.model.TripScanResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripModelImplementer implements TripMVP.TripModel {

    @Override
    public void processTripScan(String scanNumber, ApiInterface apiInterface, final OnTripListener onTripListener) {
        apiInterface.tripScan(scanNumber).enqueue(new Callback<List<TripScanResponse>>() {
            @Override
            public void onResponse(Call<List<TripScanResponse>> call, Response<List<TripScanResponse>> response) {
                try {
                    if (response.isSuccessful()) {
                        onTripListener.onTripScanFinished (response.body());
                    } else {
                        onTripListener.onTripScanFailed(response.body().toString());
                    }
                } catch (Exception e) {
                    onTripListener.onTripScanFailed(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<TripScanResponse>> call, Throwable t) {

            }
        });
    }
}
