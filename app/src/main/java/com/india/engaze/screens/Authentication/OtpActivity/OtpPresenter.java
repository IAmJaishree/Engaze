

package com.india.engaze.screens.Authentication.OtpActivity;

import android.annotation.SuppressLint;


import com.india.engaze.repository.session.ISessionManager;
import com.india.engaze.screens.base.BasePresenter;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;


public class OtpPresenter<V extends OtpContract.View> extends BasePresenter<V>
        implements OtpContract.Presenter<V> {





    @Inject
    public OtpPresenter(CompositeDisposable compositeDisposable, ISessionManager sessionManager) {
        super( compositeDisposable, sessionManager);
    }



    @SuppressLint("CheckResult")
    @Override
    public void doOTPConfirmation(Long mobileNum, String otp) {

    }

    @SuppressLint("CheckResult")
    @Override
    public void resendOtp(Long number) {
            Long phone = Long.valueOf(number);

    }
}
