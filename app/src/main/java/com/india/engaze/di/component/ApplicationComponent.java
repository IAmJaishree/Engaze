package com.india.engaze.di.component;


import com.india.engaze.AppController;
import com.india.engaze.di.Qualifiers.ApplicationScope;
import com.india.engaze.di.module.ApplicationModule;
import com.india.engaze.repository.session.ISessionManager;

import dagger.Component;


@ApplicationScope
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {


    ISessionManager getSessionManager();


    void inject(AppController appController);
}