package com.india.engaze.screens.slide;

import com.india.engaze.screens.base.MvpContract;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;


public class SlideContract {

    public interface View extends MvpContract.View {

        void showSlideUploadButton(boolean teacher);

        void showSlides(ArrayList<HashMap<String, String>> slideList);
    }


    public interface Presenter<V extends View> extends MvpContract.Presenter<V> {
        void attachListeners( String classId);
    }
}
