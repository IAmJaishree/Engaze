

package com.india.engaze.screens.base;


import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.india.engaze.AppController;
import com.india.engaze.di.component.ActivityComponent;
import com.india.engaze.di.component.DaggerActivityComponent;
import com.india.engaze.di.module.ActivityModule;
import com.india.engaze.di.module.AdapterModule;
import com.india.engaze.utils.NetworkUtils;

import butterknife.Unbinder;
import timber.log.Timber;




public abstract class BaseActivity extends AppCompatActivity
        implements MvpContract.View {


    ActivityComponent mActivityComponent;
    private Unbinder mUnBinder;
    boolean isLoading = false;
    boolean toShowInternetNotAvailable = true;

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this)).adapterModule(new AdapterModule(this))
                .applicationComponent(((AppController) getApplication()).getComponent())
                .build();
    }

    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }


    public void setUnBinder(Unbinder unBinder) {
        mUnBinder = unBinder;
    }

    @Override
    protected void onDestroy() {
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    public void showLoading() {
        setIsLoading(true);
    }

    @Override
    public void hideLoading() {
        setIsLoading(false);
    }

    @Override
    public void onError(String message) {

        if (message == null) {
            Toast.makeText(this, "Weak Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isNetworkConnected()) {
            if (toShowInternetNotAvailable) {
                toShowInternetNotAvailable = false;
                (new Handler()).postDelayed(() -> toShowInternetNotAvailable = true, 8000);
                Toast.makeText(this, "Internet not available", Toast.LENGTH_SHORT).show();
            }
        } else if (message.toLowerCase().contains("unable to resolve")) {
            Toast.makeText(this, "Weak Internet connection", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        if (isLoading) hideLoading();
    }


    @Override
    public void onThrowableError(String message) {
        Timber.e(message);
        Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public boolean getIsLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    @Override
    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
