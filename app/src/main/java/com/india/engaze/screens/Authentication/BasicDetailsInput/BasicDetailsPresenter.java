package com.india.engaze.screens.Authentication.BasicDetailsInput;

import com.india.engaze.repository.session.ISessionManager;
import com.india.engaze.screens.base.BasePresenter;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class BasicDetailsPresenter<V extends BasicDetailsInputContract.View> extends BasePresenter<V>
        implements BasicDetailsInputContract.Presenter<V> {


    @Inject
    public BasicDetailsPresenter(CompositeDisposable compositeDisposable, ISessionManager sessionManager) {
        super(compositeDisposable, sessionManager);
    }

//
//    @SuppressLint("CheckResult")
//    @Override
//    public void updateDetails(String name, String email, int selectedGender, Object user) {
//
//
//        if (name.isEmpty()) {
//            getMvpView().nameError();
//        } else if (selectedGender == -1) getMvpView().genderError();
//        else if (!email.isEmpty() && !Functions.isValidEmail(email))
//            getMvpView().invalidEmail();
//        else {
//            getMvpView().showLoading();
//            getCommonAPIManager().getHogyiApiService().updateUserDetails(getSessionManager().getToken(), selectedGender, email.isEmpty() ? null : email, name)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(profileUpdateResponse -> {
//                        if (profileUpdateResponse.isSuccess()) {
//                            getSessionManager().setUser(profileUpdateResponse.getResults().getUserData());
//                            getSessionManager().setIsLoggedIn(true);
//                            getMvpView().profileUpdatedSuccessfully();
//                        } else {
//                            getMvpView().hideLoading();
//                            getMvpView().onError(profileUpdateResponse.getError());
//                        }
//                    }, throwable -> {
//                        getMvpView().hideLoading();
//                        getMvpView().onError(throwable.getMessage());
//                    });
//        }
//    }
}
