package com.india.engaze.screens.Splash;

import androidx.constraintlayout.widget.Group;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.india.engaze.AppController;
import com.india.engaze.R;
import com.india.engaze.screens.Authentication.PhoneInput.LoginActivity;
import com.india.engaze.screens.MainActivity;
import com.india.engaze.screens.base.BaseActivity;

import org.androidannotations.annotations.App;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SplashActivity extends BaseActivity implements SplashContract.View {

    private final int SPLASH_DISPLAY_LENGTH = 1;

    @Inject
    SplashContract.Presenter<SplashContract.View> presenter;


    @BindView(R.id.imagePermission)
    ImageView imagePermission;

    @BindView(R.id.headerPermission)
    TextView permissionHeader;

    @BindView(R.id.textPermission)
    TextView permissionText;

    @BindView(R.id.buttonPermission)
    Button permissionButton;

    @BindView(R.id.splash)
    Group splashView;

    @BindView(R.id.permission)
    Group permission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        presenter.onAttach(this);
        permission.setVisibility(View.GONE);
        splashView.setVisibility(View.VISIBLE);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)openHomeActivity();
        else openLoginActivity();
    }

    @Override
    public void openLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void openHomeActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
