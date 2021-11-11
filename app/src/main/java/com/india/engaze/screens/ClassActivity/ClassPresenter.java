

package com.india.engaze.screens.ClassActivity;

import com.google.firebase.database.DataSnapshot;
import com.india.engaze.AppController;
import com.india.engaze.repository.session.ISessionManager;
import com.india.engaze.screens.base.BasePresenter;
import com.india.engaze.utils.CallBacks;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class ClassPresenter<V extends ClassContract.View> extends BasePresenter<V>
        implements ClassContract.Presenter<V> {


    @Inject
    public ClassPresenter(CompositeDisposable compositeDisposable, ISessionManager sessionManager) {
        super(compositeDisposable, sessionManager);
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);
    }

    @Override
    public void getClassDetails() {

        getMvpView().showLoading();
        AppController.getInstance().getFireBaseRepo().getClassDetails( new CallBacks.FireCallback() {
            @Override
            public void onError(String message) {
                getMvpView().showMessage(message);
            }

            @Override
            public void onSuccess(DataSnapshot ds) {
                getMvpView().showClassDetails(ds);
            }
        });
    }

    @Override
    public void addUpdate(String update) {
        getMvpView().showLoading();

        AppController.getInstance().getFireBaseRepo().addUpdates(update, new CallBacks.FireUpdate() {
            @Override
            public void onError(String message) {
                getMvpView().showMessage(message);
            }

            @Override
            public void onSuccess() {
                getMvpView().updated();
            }
        });
    }

    @Override
    public void removeUpdate(String key) {
        getMvpView().showLoading();

        AppController.getInstance().getFireBaseRepo().removeUpdate(key, new CallBacks.FireUpdate() {
            @Override
            public void onError(String message) {
                getMvpView().showMessage(message);
            }

            @Override
            public void onSuccess() {
                getMvpView().updated();
            }
        });
    }
}
