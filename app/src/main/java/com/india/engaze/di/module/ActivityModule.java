

package com.india.engaze.di.module;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.india.engaze.di.Qualifiers.ActivityContext;
import com.india.engaze.di.Qualifiers.LinLayoutHori;
import com.india.engaze.di.Qualifiers.LinLayoutVert;
import com.india.engaze.screens.Authentication.BasicDetailsInput.BasicDetailsInputContract;
import com.india.engaze.screens.Authentication.BasicDetailsInput.BasicDetailsPresenter;
import com.india.engaze.screens.Authentication.OtpActivity.OtpContract;
import com.india.engaze.screens.Authentication.OtpActivity.OtpPresenter;
import com.india.engaze.screens.Authentication.PhoneInput.LoginWithPhoneContract;
import com.india.engaze.screens.Authentication.PhoneInput.LoginWithPhonePresenter;
import com.india.engaze.screens.Splash.SplashContract;
import com.india.engaze.screens.Splash.SplashPresenter;

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
    BasicDetailsInputContract.Presenter<BasicDetailsInputContract.View> providesBasicDetailsPresenter(BasicDetailsPresenter<BasicDetailsInputContract.View> presenter) {
        return presenter;
    }


}

