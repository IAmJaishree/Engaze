package com.india.engaze.screens.Authentication.OtpActivity;


import com.india.engaze.screens.base.MvpContract;

public class OtpContract {

    public interface View extends MvpContract.View {

        void otpVerified();

        void otpSent();
    }


    public interface Presenter<V extends View> extends MvpContract.Presenter<V> {
        void doOTPConfirmation(Long mobileNum, String otp);

        void resendOtp(Long phone);
    }
}
