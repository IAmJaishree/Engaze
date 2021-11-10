package com.india.engaze.screens.Authentication.BasicDetailsInput;


import com.india.engaze.screens.base.MvpContract;


public class BasicDetailsInputContract {

    public interface View extends MvpContract.View {
        void nameError();

        void genderError();

        void profileUpdatedSuccessfully();

        void invalidEmail();
    }


    public interface Presenter<V extends View> extends MvpContract.Presenter<V> {


//        void updateDetails(String toString, String toString1, int selectedGender, UserData user);
    }
}
