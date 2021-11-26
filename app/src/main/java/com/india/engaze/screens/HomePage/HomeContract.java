package com.india.engaze.screens.HomePage;

import com.india.engaze.screens.base.MvpContract;

import java.util.ArrayList;


public class HomeContract {

    public interface View extends MvpContract.View {
        void showClasses(ArrayList<ArrayList<String>> classList);


    }


    public interface Presenter<V extends View> extends MvpContract.Presenter<V> {
        void AttachFirebaseListeners();
    }
}
