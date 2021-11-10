

package com.india.engaze.screens.slide;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.india.engaze.AppController;
import com.india.engaze.repository.Firebase.FireBaseRepository;
import com.india.engaze.repository.session.ISessionManager;
import com.india.engaze.screens.base.BasePresenter;
import com.india.engaze.utils.CallBacks;
import com.india.engaze.utils.TimeAgo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class SlidePresenter<V extends SlideContract.View> extends BasePresenter<V>
        implements SlideContract.Presenter<V> {


    @Inject
    public SlidePresenter(CompositeDisposable compositeDisposable, ISessionManager sessionManager) {
        super(compositeDisposable, sessionManager);
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);
    }

    @Override
    public void attachListeners(String classId) {


        AppController.getInstance().getFireBaseRepo().attachSlidesListener(classId, new CallBacks.FireCallback() {
            @Override
            public void onError(String message) {
                getMvpView().onError(message);
            }

            @Override
            public void onSuccess(DataSnapshot ds) {
                ArrayList<HashMap <String, String>> slideList = new ArrayList<HashMap <String, String>>();

                for (DataSnapshot slide : ds.getChildren()) {
                    if (slide == null || slide.getKey()==null) continue;


                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("date", TimeAgo.formatDateTime(Long.valueOf(slide.getKey())));
                    map.put("title", slide.child("title").getValue().toString());
                    map.put("link", slide.child("link").getValue().toString());
                    slideList.add(map);
                }

                getMvpView().showSlides(slideList);
            }
        });

        AppController.getInstance().getFireBaseRepo().attachClassAsListener(classId, new CallBacks.FireCallback() {
            @Override
            public void onError(String message) {
                getMvpView().onError(message);
            }

            @Override
            public void onSuccess(DataSnapshot ds) {

                getMvpView().showSlideUploadButton(ds.getValue().toString().equals("teacher"));

            }
        });
    }
}
