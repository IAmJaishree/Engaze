

package com.india.engaze.di.component;

import com.btp.me.classroom.slide.SlideActivity;
import com.india.engaze.di.Qualifiers.PerActivity;
import com.india.engaze.di.module.ActivityModule;
import com.india.engaze.di.module.AdapterModule;
import com.india.engaze.screens.Authentication.BasicDetailsInput.BasicDetailsInputActivity;
import com.india.engaze.screens.Authentication.OtpActivity.OtpActivity;
import com.india.engaze.screens.Authentication.PhoneInput.LoginWithPhone;
import com.india.engaze.screens.HomePage.MainActivity;
import com.india.engaze.screens.Splash.SplashActivity;

import org.jetbrains.annotations.NotNull;

import dagger.Component;



@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, AdapterModule.class})
public interface ActivityComponent {


    void inject(SplashActivity splashActivity);

    void inject(LoginWithPhone loginWithPhone);

    void inject(OtpActivity otpActivity);

    void inject(BasicDetailsInputActivity basicDetailsInputActivity);

    void inject(MainActivity mainActivity);

    void inject(SlideActivity slideActivity);
}
