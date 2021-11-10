package com.india.engaze.screens.Authentication.OtpActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.transition.Slide;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.goodiebag.pinview.Pinview;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.india.engaze.AppController;
import com.india.engaze.R;
import com.india.engaze.screens.HomePage.MainActivity;
import com.india.engaze.screens.base.BaseActivity;
import com.msg91.sendotpandroid.library.Verification;
import com.msg91.sendotpandroid.library.VerificationListener;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.view.Gravity.RIGHT;


public class OtpActivity extends BaseActivity implements OtpContract.View, FABProgressListener, VerificationListener {


    @BindView(R.id.ivback)
    ImageView ivBack;


    @BindView(R.id.rootFrame)
    FrameLayout rootFrame;

    @BindView(R.id.greetTextOtp)
    TextView greetText;

    @BindView(R.id.pinview)
    Pinview pinView;

    @BindView(R.id.resend_otp_btn)
    Button resendOtpBtn;


    @BindView(R.id.fab)
    FloatingActionButton fab;


    @BindView(R.id.fabProgress)
    FABProgressCircle fabProgressCircle;


    @Inject
    OtpContract.Presenter<OtpContract.View> presenter;

    CountDownTimer cTimer = null;

    Verification mVerification;
    boolean isVoice;

    private String phone;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken token;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        presenter.onAttach(this);

        phone =getIntent().getExtras().getString("phone");
        verificationId =getIntent().getExtras().getString("verificationId");
        token = (PhoneAuthProvider.ForceResendingToken) getIntent().getExtras().get("token");

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                onError(e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String v,
                                   @NonNull PhoneAuthProvider.ForceResendingToken t) {
                Timber.e("code sent again");
                token = t;
                verificationId = v;
            }
        };

        resendOtpBtn.setOnClickListener(v -> {
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber("+91" + phone)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .setForceResendingToken(token)     // ForceResendingToken from callbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        });
        setup();
        pinView.requestFocus();
    }


    private void setup() {
        String text = getResources().getString(R.string.welcome_back);
        String text2 = "\n" + phone;
        Spannable spannable = new SpannableString(text + text2);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), text.length(), (text + text2).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        greetText.setText(spannable, TextView.BufferType.SPANNABLE);
        setupAnimation();
        startTimer();

        fab.setOnClickListener(v -> {
            if (!getIsLoading())
                verifyOtp();
        });

        fabProgressCircle.attachListener(this);
        pinView.setPinViewEventListener((pinview, fromUser) -> verifyOtp());
        ivBack.setOnClickListener(v -> onBackPressed());
    }


    @Override
    public void otpVerified() {
        super.hideLoading();
        rootFrame.setAlpha(1f);
        onFABProgressAnimationEnd();
    }


    @Override
    public void onFABProgressAnimationEnd() {
        Intent intent = new Intent(OtpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void showLoading() {
        super.showLoading();
        rootFrame.setAlpha(0.8f);
        fabProgressCircle.show();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        rootFrame.setAlpha(1f);
        fabProgressCircle.hide();
    }


    private void verifyOtp() {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, getOtp());
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        AppController.getInstance().getmSessionManager().setFirebaseUser(user);
                        otpVerified();
                        // Update UI
                    } else {
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            onVerificationFailed(task.getException());
                        }
                    }
                });
    }


    public String getOtp() {
        return pinView.getValue();
    }


    @Override
    public void otpSent() {
        if (isVoice) {
            showMessage("Calling " + "+91-" + phone);
        }
        startTimer();
    }

    void startTimer() {
        cTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                resendOtpBtn.setEnabled(false);
                resendOtpBtn.setTextColor(getResources().getColor(R.color.grayForIconsAndSmallText));
                resendOtpBtn.setBackground(getDrawable(R.drawable.white_rectangle_border_gray));
                resendOtpBtn.setText("Resend OTP(" + (millisUntilFinished) / 1000 + ")");
            }

            public void onFinish() {
                activateResend();
            }
        };
        cTimer.start();
    }

    private void activateResend() {
        resendOtpBtn.setEnabled(true);
        resendOtpBtn.setText("Resend OTP");
        resendOtpBtn.setTextColor(getResources().getColor(R.color.white));
        resendOtpBtn.setBackground(getDrawable(R.drawable.green_rounded_rectangle));
    }


    private void setupAnimation() {
        Slide enterSlide = new Slide(RIGHT);
        enterSlide.setDuration(700);
        enterSlide.addTarget(R.id.llphone);
        enterSlide.setInterpolator(new DecelerateInterpolator(2));
        getWindow().setEnterTransition(enterSlide);
        Slide returnSlide = new Slide(RIGHT);
        returnSlide.setDuration(700);
        returnSlide.addTarget(R.id.llphone);
        returnSlide.setInterpolator(new DecelerateInterpolator());
        getWindow().setReturnTransition(returnSlide);
    }

    @Override
    public void onInitiated(String response) {
        otpSent();
    }

    @Override
    public void onInitiationFailed(Exception paramException) {
        onError(paramException.getMessage());
        cTimer.onFinish();
    }

    @Override
    public void onVerified(String response) {
        hideLoading();
        Intent intent = new Intent();
        intent.putExtra("phone", phone);
        intent.putExtra("is_verified", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onVerificationFailed(Exception paramException) {
        onError(paramException.getMessage());
    }

}
