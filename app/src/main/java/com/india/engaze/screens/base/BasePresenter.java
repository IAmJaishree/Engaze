package com.india.engaze.screens.base;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.india.engaze.repository.Firebase.FireBaseRepository;
import com.india.engaze.repository.session.ISessionManager;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * onAttach() and onDetach(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 */

public class BasePresenter<V extends MvpContract.View> implements MvpContract.Presenter<V> {

    private V mMvpView;

    private final CompositeDisposable mCompositeDisposable;
    private final ISessionManager sessionManager;
    private final FireBaseRepository fireBaseRepository;


    @Inject
    public BasePresenter(
                         CompositeDisposable compositeDisposable, ISessionManager sessionManager) {
        this.mCompositeDisposable = compositeDisposable;
        this.sessionManager = sessionManager;
        this.fireBaseRepository = new FireBaseRepository();
    }

    @Override
    public void onAttach(V mvpView) {
        mMvpView = mvpView;
    }



    public V getMvpView() {
        return mMvpView;
    }

    public ISessionManager getSessionManager() {
        return sessionManager;
    }

    public FireBaseRepository getFireBaseRepo() {
        return fireBaseRepository;
    }


}
