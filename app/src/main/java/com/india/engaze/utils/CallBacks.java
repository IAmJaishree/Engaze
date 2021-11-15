package com.india.engaze.utils;


import com.google.firebase.database.DataSnapshot;

public class CallBacks {


    public interface FireCallback{
        void onError(String message);
        void onSuccess(DataSnapshot ds);
    }

    public interface FireUpdate{
        void onError(String message);
        void onSuccess();
    }

    public interface ClassClickListener{
        void classClicked(String id);
    }
}
