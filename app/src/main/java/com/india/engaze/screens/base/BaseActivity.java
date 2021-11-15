

package com.india.engaze.screens.base;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.india.engaze.AppController;
import com.india.engaze.R;
import com.india.engaze.di.component.ActivityComponent;
import com.india.engaze.di.component.DaggerActivityComponent;
import com.india.engaze.di.module.ActivityModule;
import com.india.engaze.di.module.AdapterModule;


public abstract class BaseActivity extends AppCompatActivity
        implements MvpContract.View {


    ActivityComponent mActivityComponent;
    boolean isLoading = false;

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

        setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
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
        if (isLoading) hideLoading();
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
    protected void onResume() {
        super.onResume();
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }
}
