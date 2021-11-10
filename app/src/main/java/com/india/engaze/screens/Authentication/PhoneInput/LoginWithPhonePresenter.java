package com.india.engaze.screens.Authentication.PhoneInput;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.india.engaze.repository.session.ISessionManager;
import com.india.engaze.screens.base.BasePresenter;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;


public class LoginWithPhonePresenter<V extends LoginWithPhoneContract.View> extends BasePresenter<V>
        implements LoginWithPhoneContract.Presenter<V> {


    @Inject
    public LoginWithPhonePresenter( CompositeDisposable compositeDisposable, ISessionManager sessionManager) {
        super( compositeDisposable, sessionManager);
    }


    @SuppressLint("CheckResult")
    @Override
    public void sendOtp(String number, Activity activity) {

        getMvpView().showLoading();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks; mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential c) {

                mAuth.signInWithCredential(c)
                        .addOnCompleteListener(activity, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                getSessionManager().setFirebaseUser(user);
                                getMvpView().verificationCompleted();
                            } else {
                                getMvpView().onError(task.getException().getMessage());
//                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
////                                    getMvpView().onError(task.getException().getMessage());
//                                }
                            }
                        });
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                getMvpView().onError(e.getMessage());
//                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                    // Invalid request
//                } else if (e instanceof FirebaseTooManyRequestsException) {
//                    // The SMS quota for the project has been exceeded
//                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                getMvpView().otpSent(number, token, verificationId);
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
