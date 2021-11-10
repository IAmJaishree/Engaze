package com.india.engaze.repository.Firebase;

import com.directions.route.Route;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.india.engaze.utils.CallBacks;

import androidx.annotation.NonNull;
import timber.log.Timber;

public class FireBaseRepository {

    FirebaseDatabase firebaseDatabase;

    DatabaseReference rideReference;
    DatabaseReference driverReference;


    ValueEventListener driverLocationListener;
    ValueEventListener rideListener;

    public FireBaseRepository() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void initRideReference(int rideId) {
        if (rideReference == null) {
            rideReference =
                    firebaseDatabase
                            .getReference("rides/" + rideId);
        }
    }


    public void getDriverDetails(String vehicleType, String driverId, DriverCallBack driverCallBack) {
        driverReference = firebaseDatabase.getReference("drivers/" + vehicleType + "/" + driverId + "/l");
        driverLocationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double lat = 0, lon = 0;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if (childSnapshot.getKey().equals("0")) lat = (double) childSnapshot.getValue();
                    else lon = (double) childSnapshot.getValue();
                    Timber.e("" + childSnapshot.getValue());
                }
                if (lat != 0 && lon != 0) {
                    LatLng latLng = new LatLng(lat, lon);
                    driverCallBack.updateLocation(latLng);
                }
                Timber.e("fetched location");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                driverCallBack.updateError(databaseError.getMessage());
            }
        };
        driverReference.addValueEventListener(driverLocationListener);
    }

    public void updateRideState(int rideId, int state) {
        firebaseDatabase.getReference("rides/" + rideId)
                .child("state").setValue(state);
    }

    public void removeRideListener() {
        if (rideListener != null)
            rideReference.removeEventListener(rideListener);
    }

    public void removeDriverListener() {
        if (driverLocationListener != null)
            driverReference.removeEventListener(driverLocationListener);
    }


    public void fetchNormPath(Long id, CallBacks.NormPath normPath) {
        normPath.before();
        firebaseDatabase
                .getReference("Paths")
                .child(String.valueOf(id))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String routeJson = (String) dataSnapshot.getValue();
                            Route route = new Gson()
                                    .fromJson(routeJson, Route.class);
                            normPath.updatePath(route);
                        } else {
                            normPath.showError("Route is not set yet");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        normPath.showError(databaseError.getMessage());
                    }
                });
    }



    public interface DriverCallBack {
        void updateLocation(LatLng latLng);

        void updateError(String error);
    }

    public interface SelectDriverCallBack {
        void driverSelected();

        void updateError(String error);
    }


    public interface AcceptRideByDriver {
        void success();

        void updateError(String error);
    }

}
