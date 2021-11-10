package com.india.engaze.utils;


import com.directions.route.Route;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;

public class CallBacks {

    public interface zoomIn {
        public void zoom();
    }

    public interface onAddressItemClick {
        void searchGeoCoder(String search);

        void onClick(Object location);
    }


    public interface paymentSheetCallBack {
        void response(int method);
    }



    public interface setupPolyline {
        void setUpPolyLine(LatLng source, LatLng destination);
    }


    public interface CountDownCall {
        void finish();
    }


    public interface NetworkReq {
        void showError(String error);

        void before();
        void success();
    }

    public interface NormPath {
        void updatePath(Route route);
        void showError(String error);
        void before();
    }

    public interface FireCallback{
        void onError(String message);
        void onSuccess(DataSnapshot ds);
    }
}
