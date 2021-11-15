

package com.india.engaze.di.component;

import com.india.engaze.screens.ClassActivity.ClassActivity;
import com.india.engaze.screens.slide.SlideActivity;
import com.india.engaze.di.Qualifiers.PerActivity;
import com.india.engaze.di.module.ActivityModule;
import com.india.engaze.di.module.AdapterModule;
import com.india.engaze.screens.Authentication.OtpActivity.OtpActivity;
import com.india.engaze.screens.Authentication.PhoneInput.LoginWithPhone;
import com.india.engaze.screens.HomePage.MainActivity;
import com.india.engaze.screens.Splash.SplashActivity;

import dagger.Component;



@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, AdapterModule.class})
public interface ActivityComponent {

    void inject(SplashActivity splashActivity);

    void inject(LoginWithPhone loginWithPhone);

    void inject(OtpActivity otpActivity);

    void inject(MainActivity mainActivity);

    void inject(SlideActivity slideActivity);

    void inject(ClassActivity classActivity);
}
