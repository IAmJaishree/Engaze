package com.india.engaze.screens.Authentication.PhoneInput;

import android.content.Intent;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.PhoneAuthProvider;
import com.india.engaze.R;
import com.india.engaze.screens.Authentication.BasicDetailsInput.BasicDetailsInputActivity;
import com.india.engaze.screens.Authentication.OtpActivity.OtpActivity;
import com.india.engaze.screens.base.BaseActivity;
import com.india.engaze.service.AppSignatureHelper;
import com.india.engaze.utils.Functions;

import javax.inject.Inject;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityOptionsCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.view.Gravity.LEFT;

public class LoginWithPhone extends BaseActivity implements LoginWithPhoneContract.View, FABProgressListener {

    @BindView(R.id.ivback)
    ImageView ivBack;

    @BindView(R.id.mobile)
    EditText etPhoneNo;

    @BindView(R.id.tvMoving)
    TextView tvMoving;

    @BindView(R.id.ivFlag)
    ImageView ivFlag;

    @BindView(R.id.tvCode)
    TextView tvCode;

    @BindView(R.id.rootFrame)
    CoordinatorLayout rootFrame;

    @BindView(R.id.llphone)
    LinearLayout llPhone;

    @BindView(R.id.fabProgress)
    FABProgressCircle fabProgressCircle;


    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.referral)
    TextInputEditText referraInput;

    @Inject
    LoginWithPhoneContract.Presenter<LoginWithPhoneContract.View> presenter;



    String referral;
    private String phone;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_phone);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        presenter.onAttach(this);
        setup();
    }

    private void setup() {
        etPhoneNo.requestFocus();
        setupWindowAnimations();
        ivBack.setOnClickListener(v -> onBackPressed());

        try {
            referral = getIntent().getExtras().getString("referral");
        } catch (Exception ignored) {
        }

        if (referral != null) referraInput.setText(referral);

        fab.setOnClickListener(v -> {
            if (etPhoneNo.getText().toString().equals("9162829505")) {
                AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
                for (String s :
                        appSignatureHelper.getAppSignatures()) {
                    onError(s);
                    Timber.e(s);
                }
            }
            if (!getIsLoading())
//                presenter.sendOtp(etPhoneNo.getText().toString(), Functions.getHash(this), referraInput.getText().toString());
                  presenter.sendOtp(etPhoneNo.getText().toString(), this);
            });
        fabProgressCircle.attachListener(LoginWithPhone.this);
    }


    private void setupWindowAnimations() {

        ChangeBounds enterTransition = new ChangeBounds();
        enterTransition.setDuration(1000);
        enterTransition.setInterpolator(new DecelerateInterpolator(4));
        getWindow().setSharedElementEnterTransition(enterTransition);

        ChangeBounds returnTransition = new ChangeBounds();
        returnTransition.setDuration(1000);
        getWindow().setSharedElementReturnTransition(returnTransition);

        Slide exitSlide = new Slide(LEFT);
        exitSlide.setDuration(1000);
        exitSlide.addTarget(R.id.llphone);
        exitSlide.setInterpolator(new DecelerateInterpolator());
        getWindow().setExitTransition(exitSlide);

        Slide reenterSlide = new Slide(LEFT);
        reenterSlide.setDuration(1000);
        reenterSlide.setInterpolator(new DecelerateInterpolator(2));
        reenterSlide.addTarget(R.id.llphone);
        getWindow().setReenterTransition(reenterSlide);
    }

    @Override
    public void onError(String message) {
        super.onError(message);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        etPhoneNo.setCursorVisible(false);
        rootFrame.setAlpha(0.8f);
        fabProgressCircle.show();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        etPhoneNo.setCursorVisible(true);
        rootFrame.setAlpha(1f);
        fabProgressCircle.hide();
    }

    @Override
    public void otpSent(String number, PhoneAuthProvider.ForceResendingToken token, String verificationId) {
        fabProgressCircle.beginFinalAnimation();
        hideLoading();
        this.phone = number;
        this.verificationId = verificationId;
        this.token = token;
        onFABProgressAnimationEnd();
    }

    @Override
    public void verificationCompleted() {
        Intent intent = new Intent(LoginWithPhone.this, BasicDetailsInputActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onFABProgressAnimationEnd() {
        Intent intent = new Intent(LoginWithPhone.this, OtpActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("token", token);
        intent.putExtra("verificationId", verificationId);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(LoginWithPhone.this);
        startActivity(intent, options.toBundle());
    }
}
