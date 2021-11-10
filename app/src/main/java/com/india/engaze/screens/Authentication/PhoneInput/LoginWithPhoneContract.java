package com.india.engaze.screens.Authentication.PhoneInput;


import android.app.Activity;

import com.google.firebase.auth.PhoneAuthProvider;
import com.india.engaze.screens.base.MvpContract;


public class LoginWithPhoneContract {

    public interface View extends MvpContract.View {
        void otpSent(String number, PhoneAuthProvider.ForceResendingToken token, String phone);

        void verificationCompleted();
    }


    public interface Presenter<V extends View> extends MvpContract.Presenter<V> {

        void sendOtp(String s, Activity activity);
    }
}
