package com.india.engaze.di.module;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.india.engaze.utils.Constants;

import dagger.Module;
import dagger.Provides;



@Module
public class AdapterModule {

    private AppCompatActivity mActivity;

    public AdapterModule(AppCompatActivity activity) {
        this.mActivity = activity;
    }


    @Provides
    PlacesClient getPlacesClient() {
        if (!Places.isInitialized()) {
            Places.initialize(
                    mActivity.getApplicationContext(), Constants.google_api_key);
        }
        PlacesClient client = Places.createClient(mActivity);
        return client;
    }

}
