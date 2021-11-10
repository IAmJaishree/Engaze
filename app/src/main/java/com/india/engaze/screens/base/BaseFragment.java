package com.india.engaze.screens.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.india.engaze.di.component.ActivityComponent;

import butterknife.Unbinder;
import timber.log.Timber;



public abstract class BaseFragment extends Fragment implements MvpContract.View {


    private BaseActivity mActivity;
    private Unbinder mUnBinder;
    private boolean toShowInternetNotAvailable = true;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.mActivity = activity;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(String message) {

        if (message == null) {
            Toast.makeText(mActivity, "Weak Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        //   Timber.e(message);

        if (!isNetworkConnected()) {


            if (toShowInternetNotAvailable) {

                toShowInternetNotAvailable = false;

                //    Timber.e("Toasted");

                (new Handler()).postDelayed(() -> {
//                    Timber.e("Toasted updated");
                    toShowInternetNotAvailable = true;
                }, 8000);


                Toast.makeText(mActivity, "Internet not available", Toast.LENGTH_SHORT).show();
            }
        } else if (message.toLowerCase().contains("unable to resolve")) {
            Toast.makeText(mActivity, "Weak Internet connection", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onThrowableError(String message) {
        Timber.e(message);
        Toast.makeText(mActivity, "Some error occured", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public boolean isNetworkConnected() {
        return false;
    }

    public void setUnBinder(Unbinder unBinder) {
        mUnBinder = unBinder;
    }

    public ActivityComponent getActivityComponent() {
        if (mActivity != null) {
            return mActivity.getActivityComponent();
        }
        return null;
    }

}
