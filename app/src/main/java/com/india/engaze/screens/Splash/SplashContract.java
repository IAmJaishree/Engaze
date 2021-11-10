package com.india.engaze.screens.Splash;

import com.india.engaze.screens.base.MvpContract;




public class SplashContract {

    public interface View extends MvpContract.View {

        void openLoginActivity();

    }


    public interface Presenter<V extends View> extends MvpContract.Presenter<V> {
    }
}
