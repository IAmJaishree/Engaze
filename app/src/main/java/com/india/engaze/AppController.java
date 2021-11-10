package com.india.engaze;

import android.app.Application;
import android.os.StrictMode;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.india.engaze.di.component.ApplicationComponent;
import com.india.engaze.di.component.DaggerApplicationComponent;
import com.india.engaze.di.module.ApplicationModule;
import com.india.engaze.repository.Firebase.FireBaseRepository;
import com.india.engaze.repository.session.ISessionManager;
import com.india.engaze.utils.TimberLogger;
import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;
import timber.log.Timber;

public class AppController extends Application {
    private static ApplicationComponent mApplicationComponent;


    @Inject
    ISessionManager mSessionManager;


    FireBaseRepository fireBaseRepository;

    private static AppController mInstance;

    private FirebaseUser firebaseUser;



    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

        FirebaseApp.initializeApp(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        mApplicationComponent.inject(this);
        init();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    private void init() {
        if (BuildConfig.DEBUG) Timber.plant(new TimberLogger());

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }


    public ISessionManager getmSessionManager() {
        return mSessionManager;
    }


    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }


    public void logout() {
        FirebaseAuth.getInstance().signOut();
        mSessionManager.setToken(null);
        mSessionManager.removeRide(-1);
        mSessionManager.setIsLoggedIn(false);
        mSessionManager.normPathUpdated(false);
    }

    public FireBaseRepository getFireBaseRepo() {
        if (fireBaseRepository == null) {
            fireBaseRepository = new FireBaseRepository();
        }
        return fireBaseRepository;
    }

    public FirebaseUser getFirebaseUser(){

        if(firebaseUser==null){
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        return firebaseUser;
    }
}
