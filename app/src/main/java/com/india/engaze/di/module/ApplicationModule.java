

package com.india.engaze.di.module;

import android.app.Application;
import android.content.Context;


import com.india.engaze.di.Qualifiers.ApplicationContext;
import com.india.engaze.di.Qualifiers.ApplicationScope;
import com.india.engaze.repository.session.ISessionManager;
import com.india.engaze.repository.session.SessionManager;

import dagger.Module;
import dagger.Provides;


@Module(includes = NetworkModule.class)
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    @ApplicationContext
    @ApplicationScope
    Context provideContext() {
        return mApplication;
    }


    @Provides
    @ApplicationScope
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationScope
    ISessionManager provideSessionManager(@ApplicationContext Context c) {
        return new SessionManager(c);
    }

}
