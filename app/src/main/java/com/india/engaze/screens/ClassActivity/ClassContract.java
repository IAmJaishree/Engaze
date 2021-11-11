package com.india.engaze.screens.ClassActivity;

import com.google.firebase.database.DataSnapshot;
import com.india.engaze.screens.base.MvpContract;


public class ClassContract {

    public interface View extends MvpContract.View {

        void showClassDetails(DataSnapshot ds);

        void updated();
    }


    public interface Presenter<V extends View> extends MvpContract.Presenter<V> {
        void getClassDetails();

        void addUpdate(String toString);

        void removeUpdate(String key);
    }
}
