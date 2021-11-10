package com.india.engaze.screens.base;



public class MvpContract {


    public interface View {
        void showLoading();

        void hideLoading();

        void onError(String message);

        void showMessage(String message);

        boolean isNetworkConnected();

        void onThrowableError(String message);
    }

    public interface Presenter<V extends View> {

        void onAttach(V mvpView);


    }

}
