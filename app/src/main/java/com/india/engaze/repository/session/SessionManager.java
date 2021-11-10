package com.india.engaze.repository.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.directions.route.Route;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.india.engaze.utils.Constants;

import java.util.UUID;

import javax.inject.Inject;

public class SessionManager implements ISessionManager {


    private static final String PAYMENT_METHOD = "payment_method";
    private static final String FIREBASE_TOKEN = "firebase_token";
    private static final String DRIVER_ONLINE = "driver_online";
    private static final String NORM_PATH = "norm_path";
    private static final String NORM_PATH_UPDATED = "norm_path_updated";

    private static final String PREF_UNIQUE_ID = "pref_unique_id";
    private static final String USER = "user";


    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private static final String PREF_NAME = "chazeSession";

    private static final String TOKEN = "token";


    private static final String IS_LOGGED_IN = "is_logged_in";


    private static final String NOTIFICATION_SOUND = "notification_sound";


    private static final String IS_TO_UPDATE_CAMERA = "is_to_update_camera";

    @Inject
    public SessionManager(Context c) {
        int PRIVATE_MODE = 0;
        pref = c.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    @Override
    public void setToken(String token) {
        editor.putString(TOKEN, token);
        editor.commit();
    }

    @Override
    public String getToken() {
        return pref.getString(TOKEN, null);
    }


    @Override
    public void setIsLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);


        editor.commit();
    }

    @Override
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    @Override
    public void normPathUpdated(boolean b) {
        editor.putBoolean(NORM_PATH_UPDATED, b);
        editor.commit();
    }

    @Override
    public void removeRide(int rideId) {

    }

    @Override
    public void setFirebaseUser(FirebaseUser user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        
        editor.putString(USER, json);
        editor.commit();
    }


    @Override
    public FirebaseUser getFirebaseUser() {
        String json = pref.getString(USER, null);
        Gson gson = new Gson();
        return gson.fromJson(json, FirebaseUser.class);
    }


}
