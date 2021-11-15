

package com.india.engaze.screens.HomePage;

import com.google.firebase.database.DataSnapshot;
import com.india.engaze.AppController;
import com.india.engaze.repository.session.ISessionManager;
import com.india.engaze.screens.base.BasePresenter;
import com.india.engaze.utils.CallBacks;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class HomePresenter<V extends HomeContract.View> extends BasePresenter<V>
        implements HomeContract.Presenter<V> {


    @Inject
    public HomePresenter(CompositeDisposable compositeDisposable, ISessionManager sessionManager) {
        super(compositeDisposable, sessionManager);
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);
    }

    @Override
    public void AttachFirebaseListeners() {

        AppController.getInstance().getFireBaseRepo().attachClassListener( new CallBacks.FireCallback() {
            @Override
            public void onError(String message) {
                getMvpView().onError(message);
            }

            @Override
            public void onSuccess(DataSnapshot ds) {

                ArrayList<ArrayList<String>> classList;
                classList = new ArrayList<>();
                for (DataSnapshot group:  ds.getChildren()) {
                    if (group.getValue() == null)
                        continue;
                    ArrayList<String> list = new ArrayList<>();
                    list.add(group.getKey());
                    list.add(group.child("as").getValue().toString());

                    classList.add(list);
                }
                getMvpView().showClasses(classList);
            }
        });

    }
}
