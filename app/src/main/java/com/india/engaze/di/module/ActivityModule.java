

package com.india.engaze.di.module;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.india.engaze.di.Qualifiers.ActivityContext;
import com.india.engaze.di.Qualifiers.LinLayoutHori;
import com.india.engaze.di.Qualifiers.LinLayoutVert;
import com.india.engaze.screens.Authentication.OtpActivity.OtpContract;
import com.india.engaze.screens.Authentication.OtpActivity.OtpPresenter;
import com.india.engaze.screens.Authentication.PhoneInput.LoginWithPhoneContract;
import com.india.engaze.screens.Authentication.PhoneInput.LoginWithPhonePresenter;
import com.india.engaze.screens.ClassActivity.ClassContract;
import com.india.engaze.screens.ClassActivity.ClassPresenter;
import com.india.engaze.screens.HomePage.HomeContract;
import com.india.engaze.screens.HomePage.HomePresenter;
import com.india.engaze.screens.Splash.SplashContract;
import com.india.engaze.screens.Splash.SplashPresenter;
import com.india.engaze.screens.slide.SlideContract;
import com.india.engaze.screens.slide.SlidePresenter;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;


@Module
public class ActivityModule {

    private AppCompatActivity mActivity;

    public ActivityModule(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    AppCompatActivity provideActivity() {
        return mActivity;
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }


    @Provides
    GridLayoutManager providesGridLayoutManager() {
        return new GridLayoutManager(mActivity, 2, RecyclerView.VERTICAL, false);
    }

    @Provides
    @LinLayoutVert
    LinearLayoutManager providesLayoutManagerVert() {
        return new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false);
    }

    @Provides
    @LinLayoutHori
    LinearLayoutManager providesLayoutManagerHori() {
        return new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);
    }


    @Provides
    SplashContract.Presenter<SplashContract.View> providesSplashPresenter(SplashPresenter<SplashContract.View> presenter) {
        return presenter;
    }


    @Provides
    LoginWithPhoneContract.Presenter<LoginWithPhoneContract.View> providesLoginWithPhonePresenter(LoginWithPhonePresenter<LoginWithPhoneContract.View> presenter) {
        return presenter;
    }

    @Provides
    OtpContract.Presenter<OtpContract.View> providesOtpPresenter(OtpPresenter<OtpContract.View> presenter) {
        return presenter;
    }


    @Provides
    HomeContract.Presenter<HomeContract.View> providesHomePresenter(HomePresenter<HomeContract.View> presenter) {
        return presenter;
    }

    @Provides
    SlideContract.Presenter<SlideContract.View> providesSlidePresenter(SlidePresenter<SlideContract.View> presenter) {
        return presenter;
    }

    @Provides
    ClassContract.Presenter<ClassContract.View> providesClassPresenter(ClassPresenter<ClassContract.View> presenter) {
        return presenter;
    }


}

