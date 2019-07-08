package com.creiss.mvp;

import com.creiss.api_section.ApiInterface;
import com.creiss.base_class_section.MvpView;
import com.creiss.model.TripScanResponse;

import java.nio.charset.CoderMalfunctionError;
import java.util.List;

public class TripMVP {
    public interface TripView {
        void tripScanSuccessful (List<TripScanResponse> tripScanResponse);
        void tripScanFailed (String error);
    }
    interface TripPresenter {
        void attachView (TripView tripView);
        void destroyView ();
        void onTripScanClicked (String scanNumber);
    }
    interface TripModel {
        void processTripScan (String scanNumber, ApiInterface apiInterface, OnTripListener onTripListener);
        interface OnTripListener {
            void onTripScanFinished (List<TripScanResponse> tripScanResponse);
            void onTripScanFailed (String error);
        }
    }
}
