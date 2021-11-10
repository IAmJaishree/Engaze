

package com.india.engaze.screens.Splash;

import com.india.engaze.repository.session.ISessionManager;
import com.india.engaze.screens.base.BasePresenter;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class SplashPresenter<V extends SplashContract.View> extends BasePresenter<V>
        implements SplashContract.Presenter<V> {


    @Inject
    public SplashPresenter(CompositeDisposable compositeDisposable, ISessionManager sessionManager) {
        super(compositeDisposable, sessionManager);
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);
    }

}
