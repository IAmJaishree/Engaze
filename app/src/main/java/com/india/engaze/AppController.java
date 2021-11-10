package com.india.engaze;

import android.app.Application;
import android.content.ContentResolver;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.StrictMode;

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


    MediaPlayer player;
    boolean isSoundEnabled = false;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

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

    CountDownTimer countDownSound;

    public void setPlayer() {
        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getApplicationContext().getPackageName() + "/raw/buzzer_alarm");
        this.player = MediaPlayer.create(getApplicationContext(), alarmSound);
        this.player.setLooping(true);
        this.player.start();
        isSoundEnabled = true;
    }

    public void disableSound() {
        Timber.e("wenbt to disable sound");
        if (player != null) {
            Timber.e(this.player.toString());
            Timber.e("player disabled");
            player.setLooping(false);
            player.stop();
        }
    }


    public void logout() {
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
}
