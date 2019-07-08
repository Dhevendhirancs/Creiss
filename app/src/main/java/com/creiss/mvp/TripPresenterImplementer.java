package com.creiss.mvp;

import com.creiss.R;
import com.creiss.api_section.ApiClient;
import com.creiss.api_section.ApiInterface;
import com.creiss.base_class_section.BasePresenter;
import com.creiss.model.TripScanResponse;

import java.util.List;

public class TripPresenterImplementer extends BasePresenter implements TripMVP.TripPresenter, TripMVP.TripModel.OnTripListener {

    private TripMVP.TripView tripView;
    private TripModelImplementer tripModel = new TripModelImplementer();
    private ApiInterface mApiInterface = new ApiClient().getClient().create(ApiInterface.class);

    public TripPresenterImplementer(TripMVP.TripView tripView) {
        this.tripView = tripView;
    }

    @Override
    public void attachView(TripMVP.TripView tripView) {
        this.tripView = tripView;
    }

    @Override
    public void destroyView() {
        this.tripView = null;
    }

    @Override
    public void onTripScanClicked(String scanNumber) {
        tripModel.processTripScan(scanNumber, mApiInterface, this);
    }

    @Override
    public void onTripScanFinished(List<TripScanResponse> tripScanResponse) {
        if (tripView != null)
            tripView.tripScanSuccessful(tripScanResponse);
    }

    @Override
    public void onTripScanFailed(String error) {
        if (tripView != null)
            tripView.tripScanFailed(error);
    }
}
