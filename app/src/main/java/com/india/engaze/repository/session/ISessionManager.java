package com.india.engaze.repository.session;

import com.google.firebase.auth.FirebaseUser;

public interface ISessionManager {


    void setToken(String token);

    String getToken();


    void setIsLoggedIn(boolean isLoggedIn);

    boolean isLoggedIn();


    void normPathUpdated(boolean b);

    void removeRide(int rideId);


    void setFirebaseUser(FirebaseUser user);

    FirebaseUser getFirebaseUser();
}
